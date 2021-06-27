package com.syriasoft.hotelservices;

public class RestaurantOrderItem  {

    int id ;
    int fac ;
    String type ;
    String name ;
    String desc ;
    int quantity ;
    double price ;
    double discount ;
    double total ;
    String photo ;

    public RestaurantOrderItem(int id,int fac, String type, String name, String desc, int quantity, double price, double discount, double total, String photo) {
        this.id = id;
        this.fac = fac ;
        this.type = type;
        this.name = name;
        this.desc = desc;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
        this.total = total;
        this.photo = photo;
    }
}
