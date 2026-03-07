from pydantic import BaseModel

class OrderItem(BaseModel):
    inventory_id: str
    quantity: int