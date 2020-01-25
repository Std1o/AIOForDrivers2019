package com.beerdelivery.driver.model;

/**
 * Created by BeerDelivery on 01.12.2019.
 */
public class modelCity {

    private String cityName, cityThumbnailUrl;


    public modelCity() {
    }

    public modelCity(String name, String thumbnailUrl) {
        this.cityName = name;
        this.cityThumbnailUrl = thumbnailUrl;

    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String name) {
        this.cityName = name;
    }

    public String getCityThumbnailUrl() {
        return cityThumbnailUrl;
    }

    public void setCityThumbnailUrl(String thumbnailUrl) {
        this.cityThumbnailUrl = thumbnailUrl;
    }



}