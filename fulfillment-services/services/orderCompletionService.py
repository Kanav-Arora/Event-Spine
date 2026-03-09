import json
import requests
from datetime import datetime
from config import API_URL
from producer import delivery_report

def orderCompletionService(msg,producer):
    try:
        data = json.loads(msg.value().decode("utf-8"))
        timestamp = str(datetime.now())
        data["timestamp"] = timestamp
        data["source"] = "orders"
        data["status"] = "ORDER_COMPLETED"
        response = requests.post(
            API_URL + "/complete-order",
            json=data,
            timeout=5
        )
        response.raise_for_status()
        return {"status": True, "response": response, "source": data["source"], "status" : data["status"]}
    except Exception as e:
        print(
                    f"Order Completion Service; Processing failed for offset {msg.offset()}: {e}"
                )
        return {"status":False}