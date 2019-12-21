package com.stdio.aiofordrivers2019.model;

/**
 * Created by LordRus on 17.01.2016.
 */
public class ModelOrders {
    private String status, orderTime, orderTarif, clientPlace,clientRoute, orderInfo, orderId;


    public ModelOrders() {
    }

    public ModelOrders(String status, String ordertime, String  ordertarif, String  clientplace, String clientroute, String  orderinfo, String  orderid) {
        this.orderTime = ordertime;
        this.orderTarif = ordertarif;
        this.clientPlace = clientplace;
        this.clientRoute = clientroute;
        this.orderInfo = orderinfo;
        this.orderId = orderid;
        this.status = status;

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

    public String getStat() {return status;}
    public void setStat(String v) {this.status = v;}




}
