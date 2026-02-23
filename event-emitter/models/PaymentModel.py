from pydantic import BaseModel
from datetime import datetime
from typing import Dict,List

class OrderItem(BaseModel):
    inventory_id: str
    quantity: int

class OrderPayload(BaseModel):
    order_id: str
    order_items: List[OrderItem]

class PaymentModel(BaseModel):
    order_id: str
    payload: OrderPayload
    source: str
    timestamp: datetime
    status: str

