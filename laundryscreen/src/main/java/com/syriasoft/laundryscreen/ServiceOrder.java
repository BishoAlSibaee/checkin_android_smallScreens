package com.syriasoft.laundryscreen;

public class ServiceOrder
{
    String roomNumber ;
    String orderNumber ;
    String dep ;
    String roomServiceText ;
    Long date ;


    public ServiceOrder(String roomNumber , String orderNumber , String dep , String roomServiceText , Long date) {
        this.roomNumber = roomNumber;
        this.orderNumber = orderNumber;
        this.dep = dep ;
        this.roomServiceText = roomServiceText ;
        this.date = date ;
    }
}
