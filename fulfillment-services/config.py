import os

POSTGRES_USER = os.getenv("POSTGRES_USER","inventory_user")
POSTGRES_PASSWORD = os.getenv("POSTGRES_PASSWORD","inventory_pass")
POSTGRES_DB = os.getenv("POSTGRES_DB","inventory_db")
POSTGRES_HOST_INTERNAL = os.getenv("POSTGRES_HOST_INTERNAL","postgres")
POSTGRES_PORT = os.getenv("POSTGRES_PORT","5432")
KAFKA_BOOTSTRAP_SERVERS = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "kafka:29092")
API_URL = "http://fastapi:8000"