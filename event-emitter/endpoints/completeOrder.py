from models.ResponseModel import ResponseModel
import uuid
import json

def completeOrder(conn, request: ResponseModel):
    try:
        with conn:
            with conn.cursor() as cursor:
                cursor.execute("""
                    UPDATE orders SET order_status = 'COMPLETED'
                    where order_id = %s
                    """, (request.order_id,)
                )
                event_metadata = json.dumps({
                            "source": request.source
                        })
                
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
                        str(uuid.uuid4()),
                        "orders",
                        request.order_id,
                        "completed-order",
                        json.dumps(request.payload.dict()),
                        event_metadata,
                        request.timestamp
                    )
                )

                conn.commit()
                return {"status":200, "message": "Success", "payload": request.payload}
        
    except Exception as e:
        conn.rollback()
        print(f"Error completing order: {e}")
        return {"status":400, "message": e}