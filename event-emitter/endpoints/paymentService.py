from models.ResponseModel import ResponseModel
import json
import uuid

def processPayment(conn, request: ResponseModel):
    try:
        with conn:
            with conn.cursor() as cursor:
                cursor.execute("""
                    UPDATE orders SET order_status = %s
                    where order_id = %s
                    """, (request.status,request.order_id,)
                )
                event_metadata = json.dumps({
                            "source": request.source
                        })
                
                event_type = "success-payment" if request.status == "PAYMENT_SUCCESSFULL" else "failed-payment"

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
                        "payments",
                        str(uuid.uuid4()),
                        event_type,
                        json.dumps(request.payload.dict()),
                        event_metadata,
                        request.timestamp
                    )
                )

                conn.commit()
                return {"status":200, "message": "Success", "payload": request.payload}
        
    except Exception as e:
        conn.rollback()
        print(f"Error Payment Endpoint: {e}")
        return {"status":400, "message": e}
