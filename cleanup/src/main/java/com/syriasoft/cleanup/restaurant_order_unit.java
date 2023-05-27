package com.syriasoft.cleanup;

public class restaurant_order_unit {

    int id;
    int hotel;
    int facility;
    int Reservation;
    int room;
    int RorS;
    int roomId;
    long dateTime;
    double total;
    int status;

    public restaurant_order_unit(int id, int hotel, int facility, int reservation, int room, int rorS, int roomId, long dateTime, double total, int status) {
        this.id = id;
        this.hotel = hotel;
        this.facility = facility;
        Reservation = reservation;
        this.room = room;
        RorS = rorS;
        this.roomId = roomId;
        this.dateTime = dateTime;
        this.total = total;
        this.status = status;
    }
}
