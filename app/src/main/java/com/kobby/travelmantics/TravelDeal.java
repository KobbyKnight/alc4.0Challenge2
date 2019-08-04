package com.kobby.travelmantics;

import java.io.Serializable;

public class TravelDeal implements Serializable {
    private String id;
    private String aTitle;
    private String aPrice;
    private String aDescription;
    private String aImageUrl;


    public TravelDeal(String title, String price, String description,String imageUrl) {
        this.id = id;
        this.aTitle = title;
        this.aPrice = price;
        this.aDescription = description;
        this.aImageUrl = imageUrl;
    }

    public TravelDeal() {

    }

    public String getImageUrl() {
        return aImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        aImageUrl = imageUrl;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return aTitle;
    }

    public void setTitle(String title) {
        aTitle = title;
    }

    public String getPrice() {
        return aPrice;
    }

    public void setPrice(String price) {
        aPrice = price;
    }

    public String getDescription() {
        return aDescription;
    }

    public void setDescription(String description) {
        aDescription = description;
    }








}
