from fastapi import FastAPI
from db import initiateDB
from endpoints.createOrder import createOrder
from endpoints.rejectOrder import rejectOrder
from endpoints.paymentService import processPayment
from endpoints.shipmentService import processShipment
from endpoints.completeOrder import completeOrder
from models.ResponseModel import ResponseModel

app = FastAPI()

conn = initiateDB()

@app.post("/create-order")
def createOrderCaller():
    return createOrder(conn)

@app.post("/reject-order")
def rejectOrderCaller(request: ResponseModel):
    return rejectOrder(conn,request)

@app.post("/payment-service")
def paymentServiceCaller(request: ResponseModel):
    return processPayment(conn,request)

@app.post("/shipment-service")
def shipmentServiceCaller(request: ResponseModel):
    return processShipment(conn,request)

@app.post("/complete-order")
def completeOrderCaller(request: ResponseModel):
    return completeOrder(conn,request)