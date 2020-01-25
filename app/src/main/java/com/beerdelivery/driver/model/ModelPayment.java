package com.beerdelivery.driver.model;

/**
 * Created by BeerDelivery on 01.12.2019.
 */
public class ModelPayment {

    private String moneyCount, operationName, dateTime;


    public ModelPayment() {
    }

    public ModelPayment(String moneyCount, String operationName, String dateTime) {
        this.moneyCount = moneyCount;
        this.operationName = operationName;
        this.dateTime = dateTime;

    }

    public String getMoneyCount() {
        return moneyCount;
    }

    public void setMoneyCount(String moneyCount) {
        this.moneyCount = moneyCount;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

}