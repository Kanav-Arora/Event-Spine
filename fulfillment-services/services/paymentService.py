import requests
from config import API_URL
import json
from datetime import datetime
from producer import delivery_report

def paymentStatus(order_id: str) -> bool:
    return order_id[-1].isnumeric()

def paymentService(msg,producer):
    try:
        event = json.loads(msg.value().decode("utf-8"))
        order_id = event["order_id"]
        order_items = []
        for i,v in event["order_items"].items():
            order_items.append({"inventory_id": i,"quantity": v})
        timestamp = str(datetime.now())
        source = "payments"
        payload = {}
        payload["order_id"] = order_id
        payload["order_items"] = order_items
        payment_success = paymentStatus(order_id)
        data = {
            "order_id": order_id,
            "payload": payload,
            "status": "PAYMENT_SUCCESSFULL" if payment_success else "PAYMENT_FAILED",
            "source": source,
            "timestamp": timestamp
        }
        response = requests.post(
            API_URL + "/payment-service",
            json=data,
            timeout=5
        )
        response.raise_for_status()
        if payment_success:
            producer.produce("request.shipments",value=json.dumps(data).encode("utf-8"), on_delivery=delivery_report)
            producer.flush()
        return {"status": True, "response": response, "source": data["source"], "status" : data["status"]}

    except Exception as e:
        print(
                    f"Payment Service; Processing failed for offset {msg.offset()}: {e}"
                )
        return {"status":False}