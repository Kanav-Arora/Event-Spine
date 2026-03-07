from confluent_kafka import Consumer, KafkaError
from config import KAFKA_BOOTSTRAP_SERVERS
from orderRejectionService import orderRejectionService
from paymentService import paymentService
from shipmentService import shipmentService
from producer import initiate_producer
import threading

def create_consumer(topic):
    consumer =  Consumer({
        "bootstrap.servers": KAFKA_BOOTSTRAP_SERVERS,
        "group.id": "fulfillment-services",
        "enable.auto.commit": False,
        "auto.offset.reset": "earliest",
        "session.timeout.ms": 30000,
        "max.poll.interval.ms": 300000,
    })
    consumer.subscribe([topic])
    return consumer

def consume(consumer, caller):
    producer = initiate_producer()
    try:
        while True:
            msg = consumer.poll(timeout = 1.0)
            if msg is None:
                continue
            if msg.error():
                if msg.error().code() == KafkaError._PARTITION_EOF:
                    continue
            response = caller(msg,producer)
            if response["status"]:
                consumer.commit(message = msg)

    finally:
        consumer.close()

def start_order_service():
    consumer = create_consumer("rejected.orders")
    consume(consumer,orderRejectionService)

def start_payment_service():
    consumer = create_consumer("request.payments")
    consume(consumer,paymentService)

def start_shipment_service():
    consumer = create_consumer("request.shipments")
    consume(consumer,shipmentService)

if __name__ == "__main__":
    threading.Thread(target=start_order_service).start()
    threading.Thread(target=start_payment_service).start()
    threading.Thread(target=start_shipment_service).start()