package com.syriasoft.cleanup;

public class OrderItem {
    int id;
    int orderNumber;
    int room;
    int itemNo;
    String name;
    String descr;
    int quantity;
    double price;
    double total;
    String notes;

    public OrderItem(int id, int orderNumber, int room, int itemNo, String name, String descr, int quantity, double price, double total, String notes) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.room = room;
        this.itemNo = itemNo;
        this.name = name;
        this.descr = descr;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.notes = notes;
    }
}
