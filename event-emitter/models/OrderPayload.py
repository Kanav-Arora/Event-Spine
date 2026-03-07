from pydantic import BaseModel
from typing import List
from models.OrderItem import OrderItem

class OrderPayload(BaseModel):
    order_id: str
    order_items: List[OrderItem]