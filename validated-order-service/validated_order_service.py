from confluent_kafka import Consumer, KafkaError
import json
from config import KAFKA_BOOTSTRAP_SERVERS, API_URL
import requests

def create_consumer():
    return Consumer({
        "bootstrap.servers": KAFKA_BOOTSTRAP_SERVERS,
        "group.id": "validated-orders-service",
        "enable.auto.commit": False,
        "auto.offset.reset": "earliest",
        "session.timeout.ms": 30000,
        "max.poll.interval.ms": 300000,
    })

def consume():
    consumer = create_consumer()
    consumer.subscribe(["rejected.orders.events"])
    try:
        while True:
            msg = consumer.poll(timeout=1.0)

            if msg is None:
                continue

            if msg.error():
                if msg.error().code() == KafkaError._PARTITION_EOF:
                    continue

            try:
                event = json.loads(msg.value().decode("utf-8"))
                order_id = event["order_id"]
                order_items = event["orderItems"]
                timestamp = event["timestamp"]
                rejection_source = "orders"
                payload = {}
                payload["order_id"] = order_id
                payload["order_items"] = order_items

                data = {
                    "order_id": order_id,
                    "payload": payload,
                    "rejection_source": rejection_source,
                    "timestamp": timestamp
                }
                response = requests.post(
                    API_URL + "/reject-order",
                    json=data,
                    timeout=5
                )
                response.raise_for_status()
                consumer.commit(message=msg)

            except Exception as e:
                print(
                    f"Processing failed for offset {msg.offset()}: {e}"
                )
    finally:
        consumer.close()

if __name__ == "__main__":
    consume()