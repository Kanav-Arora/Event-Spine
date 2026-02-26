from confluent_kafka import Producer
from config import KAFKA_BOOTSTRAP_SERVERS

def initiate_producer():
    producer = Producer({
        "bootstrap.servers": KAFKA_BOOTSTRAP_SERVERS,
    })
    return producer

def delivery_report(err, msg):
    if err:
        print(f"Message failed: {err}")