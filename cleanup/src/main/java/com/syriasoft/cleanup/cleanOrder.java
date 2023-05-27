package com.syriasoft.cleanup;

public class cleanOrder {
    String roomNumber;
    String orderNumber;
    String dep;
    String roomServiceText;
    Long date;

    public cleanOrder(String roomNumber, String orderNumber, String dep, String roomServiceText, Long date) {
        this.roomNumber = roomNumber;
        this.orderNumber = orderNumber;
        this.dep = dep;
        this.roomServiceText = roomServiceText;
        this.date = date;
    }
}
