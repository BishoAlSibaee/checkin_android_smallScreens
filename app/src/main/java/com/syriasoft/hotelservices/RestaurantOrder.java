package com.syriasoft.hotelservices;

public class RestaurantOrder {

    int id ;
    restaurant_item[] item ;
    int quantiyi ;

    public RestaurantOrder(int id, restaurant_item[] item, int quantiyi) {
        this.id = id;
        this.item = item;
        this.quantiyi = quantiyi;
    }
}
