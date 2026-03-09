from pydantic import BaseModel
from datetime import datetime
from models.OrderPayload import OrderPayload

class ResponseModel(BaseModel):
    order_id: str
    payload: OrderPayload
    source: str
    timestamp: datetime
    status: str