from models.ResponseModel import ResponseModel
import json
import uuid
from logger.logger import log_event

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
                        "payments",
                        str(uuid.uuid4()),
                        event_type,
                        json.dumps(event_payload),
                        event_metadata,
                        request.timestamp
                    )
                )

                conn.commit()
                log_event(200,event_type,event_id,event_payload["order_id"],event_payload)
                return {"status":200, "message": "Success", "payload": request.payload}
        
    except Exception as e:
        conn.rollback()
        log_event(400,"payment_service",level="error",metadata={"message": e},order_id=request.order_id)
        return {"status":400, "message": e}
