package com.stdio.aiofordrivers2019.model;

/**
 * Created by LordRus on 24.01.2016.
 */
public class ModelTakenOrders {
    private String orderTime, orderTarif, clientPlace,clientRoute, orderInfo, orderId, orderStatus, clentPhone;


    public ModelTakenOrders() {
    }

    public ModelTakenOrders(String ordertime, String  ordertarif, String  clientplace, String clientroute,
                            String  orderinfo, String  orderid, String orderstatus, String clientphone) {
        this.orderTime = ordertime;
        this.orderTarif = ordertarif;
        this.clientPlace = clientplace;
        this.clientRoute = clientroute;
        this.orderInfo = orderinfo;
        this.orderId = orderid;

        this.orderStatus = orderstatus;
        this.clentPhone = clientphone;
    }

    public String getorderTime() {return orderTime;}
    public void setorderTime(String v) {this.orderTime = v;}

    public String getorderTarif() {return orderTarif;}
    public void setorderTarif(String v) {this.orderTarif = v;}

    public String getclientPlace() {return clientPlace;}
    public void setclientPlace(String v) {this.clientPlace = v;}

    public String getclientRoute() {return clientRoute;}
    public void setclientRoute(String v) {this.clientRoute = v;}

    public String getorderInfo() {return orderInfo;}
    public void setorderInfo(String v) {this.orderInfo = v;}

    public String getorderId() {return orderId;}
    public void setorderId(String v) {this.orderId = v;}

    public String getorderStatus() {return orderStatus;}
    public void setorderStatus(String v) {this.orderStatus = v;}

    public String getclientPhone() {return clentPhone;}
    public void setclientPhone(String v) {this.clentPhone = v;}




}
