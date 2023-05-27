package com.syriasoft.cleanup;

public class MINIBARITEM {
    int id;
    int Hotel;
    int Facility;
    String Name;
    double price;
    String photo;

    public MINIBARITEM(int id, int hotel, int facility, String name, double price, String photo) {
        this.id = id;
        Hotel = hotel;
        Facility = facility;
        Name = name;
        this.price = price;
        this.photo = photo;
    }
}
