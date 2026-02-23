import psycopg2
import config

def initiateDB():
    conn = psycopg2.connect(
        dbname=config.POSTGRES_DB,
    user=config.POSTGRES_USER,
    password=config.POSTGRES_PASSWORD,
    host=config.POSTGRES_HOST_INTERNAL,
    port=config.POSTGRES_PORT
    )
    return conn