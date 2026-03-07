from fastapi import FastAPI
from db import initiateDB
from endpoints.createOrder import createOrder
from endpoints.rejectOrder import rejectOrder
from endpoints.paymentService import processPayment
from endpoints.shipmentService import processShipment
from models.RejectOrderModel import RejectOrderModel
from models.PaymentModel import PaymentModel
from models.ShipmentModel import ShipmentModel

app = FastAPI()

conn = initiateDB()

@app.post("/create-order")
def createOrderCaller():
    return createOrder(conn)

@app.post("/reject-order")
def rejectOrderCaller(request: RejectOrderModel):
    return rejectOrder(conn,request)

@app.post("/payment-service")
def paymentServiceCaller(request: PaymentModel):
    return processPayment(conn,request)

@app.post("/shipment-service")
def shipmentServiceCaller(request: ShipmentModel):
    return processShipment(conn,request)