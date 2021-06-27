package com.syriasoft.hotelservices;

public class restaurant_item
{
    int id ;
    int Hotel ;
    int Facility ;
    int MenuId ;
    String Menu ;
    String Name ;
    String Desc ;
    double Price ;
    double Discount ;
    String photo ;

    public restaurant_item(int id, int hotel, int facility, int menuId, String menu, String name, String desc, double price, double discount, String photo)
    {
        this.id = id;
        Hotel = hotel;
        Facility = facility;
        MenuId = menuId;
        Menu = menu;
        Name = name;
        Desc = desc;
        Price = price;
        Discount = discount;
        this.photo = photo;
    }
}
