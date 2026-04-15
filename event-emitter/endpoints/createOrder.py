import random
import json
from decimal import Decimal
from logger.logger import log_event

def createOrder(conn):
    try:
        with conn:
            with conn.cursor() as cursor:
                cursor.execute("""
                    SELECT cust_id FROM customers
                            ORDER BY RANDOM() LIMIT 1;
                            
                    """)
                cust_id = cursor.fetchone()[0]
                limit = random.randint(1,5)
                cursor.execute("""
                        SELECT a.inventory_id, a.product_id, a.warehouse_id, b.price FROM inventory a JOIN products b
                            ON a.product_id = b.product_id
                            ORDER BY RANDOM() LIMIT %s;  
                            """,(limit,),)
                products = cursor.fetchall()

                cursor.execute("""
                    INSERT INTO orders (cust_id,order_status) VALUES
                            (%s,'CREATED') RETURNING order_id;
                    """,(cust_id,),)
                
                order_id = cursor.fetchone()[0]

                order_item_payload = []
                for product in products:
                    inventory_id, product_id, warehouse_id, price = product
                    discount = random.randrange(5, 20)
                    discount_decimal = Decimal(discount) / Decimal(100)
                    discounted_price = price * (Decimal(1) - discount_decimal)
                    order_qty = random.randint(1,50)
                    cursor.execute("""
                    INSERT INTO order_items (order_id,product_id,warehouse_id,quantity,price)
                    VALUES (%s,%s,%s,%s,%s);
                    """,(order_id,product_id,warehouse_id,order_qty,discounted_price,))
                    order_item_payload.append({
                        "inventory_id":inventory_id,
                        "product_id": product_id,
                        "warehouse_id":warehouse_id,
                        "quantity": str(order_qty),
                        "price": str(discounted_price)
                    })

                event_payload = {
                    "cust_id": cust_id,
                    "order_id": order_id,
                    "order_items":order_item_payload
                }
                metadata = {
                    "source": "fastapi",
                    "service": "create-order",
                    "event_version": "1.0"
                }

                cursor.execute("""
                    INSERT INTO events (aggregate_type,aggregate_id,event_type,payload,metadata)
                            VALUES ('orders',%s,'create-order',%s,%s)
                            RETURNING event_id;
                            """,(order_id,json.dumps(event_payload),json.dumps(metadata),
                                    ),
                            )
                event_id = cursor.fetchone()[0]
                event_payload["event_id"] = event_id
                log_event(200,"create-order",event_payload["event_id"],event_payload["order_id"],event_payload)
                return {"status": 200, "message" : "Success", "payload": event_payload }
    
    except Exception as e:
        conn.rollback()
        log_event(400,"create-order",level="error",metadata={"message" : e})
        return {"status": 400, "message" : e }