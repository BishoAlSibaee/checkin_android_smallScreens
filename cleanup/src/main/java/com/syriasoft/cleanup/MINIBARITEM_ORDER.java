package com.syriasoft.cleanup;

public class MINIBARITEM_ORDER extends MINIBARITEM {
    public int Quantity;
    public double Total;

    public MINIBARITEM_ORDER(int id, int hotel, int facility, String name, double price, String photo, int quan, double total) {
        super(id, hotel, facility, name, price, photo);
        Quantity = quan;
        Total = total;
    }
}
