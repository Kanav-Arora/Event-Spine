import json
import requests
from datetime import datetime
from config import API_URL

def shipmentStatus(order_id: str) -> bool:
    return order_id[-1].isnumeric()

def shipmentService(msg,producer):
    try:
        data = json.loads(msg.value().decode("utf-8"))
        shipment_success = shipmentStatus(data["order_id"])
        timestamp = str(datetime.now())
        data["timestamp"] = timestamp
        data["status"] = "SHIPMENT_SUCCESSFULL" if shipment_success else "SHIPMENT_FAILED"
        response = requests.post(
            API_URL + "/shipment-service",
            json=data,
            timeout=5
        )
        response.raise_for_status()
        return {"status": True, "response": response, "shipment_status" : shipment_success}
    except Exception as e:
        print(
                    f"Shipment Service; Processing failed for offset {msg.offset()}: {e}"
                )
        return {"status":False}