package com.beerdelivery.driver.model;

/**
 * Created by BeerDelivery on 01.12.2019.
 */
public class ModelOrders {
    private String status, orderTime, orderTarif, clientPlace,clientRoute, orderInfo, orderId, coords_store, coords_client, orderPrice;


    public ModelOrders() {
    }

    public ModelOrders(String status, String ordertime, String  ordertarif, String  clientplace, String clientroute, String  orderinfo, String  orderid, String coords_store, String coords_client, String orderPrice) {
        this.orderTime = ordertime;
        this.orderTarif = ordertarif;
        this.clientPlace = clientplace;
        this.clientRoute = clientroute;
        this.orderInfo = orderinfo;
        this.orderId = orderid;
        this.status = status;
        this.coords_store = coords_store;
        this.coords_client = coords_client;
        this.orderPrice = orderPrice;
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

    public String getCoords_store() {return coords_store;}
    public void setCoords_store(String v) {this.coords_store = v;}

    public String getCoords_client() {return coords_client;}
    public void setCoords_client(String v) {this.coords_client = v;}

    public String getOrderPrice() {return orderPrice;}
    public void setOrderPrice(String v) {this.orderPrice = v;}
}
