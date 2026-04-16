from models.ResponseModel import ResponseModel
import uuid
import json
from logger.logger import log_event

def completeOrder(conn, request: ResponseModel):
    try:
        with conn:
            with conn.cursor() as cursor:
                cursor.execute("""
                    UPDATE orders SET order_status = 'COMPLETED'
                    where order_id = %s
                    """, (request.order_id,)
                )

                if cursor.rowcount == 0:
                    log_event(200,"duplicate-completed-order",event_id,event_payload["order_id"],event_payload)
                    return {"status":200, "message": "Success", "payload": request.payload}
                
                for item in request.payload.order_items:
                    cursor.execute("""
                        UPDATE inventory 
                        SET quantity = quantity - %s
                        WHERE inventory_id = %s
                        AND quantity >= %s
                    """, (item.quantity, item.inventory_id, item.quantity))

                event_metadata = json.dumps({
                            "source": request.source
                        })
                event_id = str(uuid.uuid4())
                event_payload = request.payload.dict()
                cursor.execute(
                    """
                    INSERT INTO events (
                        event_id,
                        aggregate_type,
                        aggregate_id,
                        event_type,
                        payload,
                        metadata,
                        created_at
                    )
                    VALUES (%s, %s, %s, %s, %s, %s,%s)
                    """,
                    (
                        event_id,
                        "orders",
                        request.order_id,
                        "completed-order",
                        json.dumps(event_payload),
                        event_metadata,
                        request.timestamp
                    )
                )

                conn.commit()
                log_event(200,"completed-order",event_id,event_payload["order_id"],event_payload)
                return {"status":200, "message": "Success", "payload": request.payload}
        
    except Exception as e:
        conn.rollback()
        log_event(400,"completed-order",level="error",metadata={"message": e},order_id=request.order_id)
        return {"status":400, "message": e}