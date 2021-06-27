package com.syriasoft.hotelservices;

public class RestaurantOldOrder
{
    int id ;
    String clientF ;
    String clientL ;
    double total ;

    public RestaurantOldOrder(int id, String clientF, String clientL, double total) {
        this.id = id;
        this.clientF = clientF;
        this.clientL = clientL;
        this.total = total;
    }
}
