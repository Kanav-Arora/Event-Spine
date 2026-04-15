from typing import Optional, Dict, Any
from logger.wrapper import loggerWrapper

SERVICE_NAME = "fast-api"

def log_event(
    status: int,
    event: str,
    event_id: Optional[int] = None,
    order_id: Optional[int] = None,
    metadata: Optional[Dict[str, Any]] = None,
    level: str = "info"
):
    log_data = {
        "status": status,
        "event": event,
        "service": SERVICE_NAME,
        "event_id": event_id,
        "order_id": order_id,
        "metadata": str(metadata) or {}
    }
    print("logger working")
    if level == "info":
        loggerWrapper.info(log_data)

    elif level == "warning":
        loggerWrapper.warning(log_data)

    elif level == "error":
        loggerWrapper.error(log_data)

    else:
        loggerWrapper.debug(log_data)