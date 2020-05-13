package com.beerdelivery.driver.model;

/**
 * Created by BeerDelivery on 01.12.2019.
 */
public class ModelOrders {
    private String status;
    private String orderTime;
    private String orderTarif;
    private String clientPlace;
    private String clientRoute;
    private String orderInfo;
    private String orderId;
    private String coords_store;
    private String coords_client;
    private String orderPrice;
    private String textTariff;
    private String clientPhone;

    public ModelOrders() {
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

    public String getTextTariff() {return textTariff;}
    public void setTextTariff(String v) {this.textTariff = v;}

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }
}
