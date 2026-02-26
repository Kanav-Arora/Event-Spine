import requests
from config import API_URL
import json

def orderRejectionService(msg,producer):
    try:
        event = json.loads(msg.value().decode("utf-8"))
        order_id = event["order_id"]
        order_items = []
        for i,v in event["order_items"].items():
            order_items.append({"inventory_id": i,"quantity": v})
        timestamp = event["timestamp"]
        rejection_source = "orders"
        payload = {}
        payload["order_id"] = order_id
        payload["order_items"] = order_items
        data = {
            "order_id": order_id,
            "payload": payload,
            "rejection_source": rejection_source,
            "timestamp": timestamp
        }
        print(data)
        response = requests.post(
            API_URL + "/reject-order",
            json=data,
            timeout=5
        )
        response.raise_for_status()
        return {"status": True, "response": response, "rejection_source" : rejection_source}

    except Exception as e:
        print(
                    f"Processing failed for offset {msg.offset()}: {e}"
                )
        return {"status": False}
