package com.syriasoft.hotelservices;

import android.graphics.Bitmap;

public class Menu {

    int id ;
    String photo ;
    String name ;
    String arabic ;
    int Hotel ;
    int Facility ;

    public Menu(int id , String image, String menue ,String arabic,int Hotel , int Facility) {
        this.id = id ;
        this.photo = image;
        this.name = menue;
        this.arabic = arabic ;
        this.Facility=Facility ;
        this.Hotel = Hotel ;
    }
}
