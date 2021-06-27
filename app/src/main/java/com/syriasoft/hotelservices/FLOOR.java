package com.syriasoft.hotelservices;

public class FLOOR
{
    int id ;
    int buildingId ;
    int floorNumber ;
    int rooms ;

    public FLOOR(int id, int buildingId, int floorNumber, int rooms) {
        this.id = id;
        this.buildingId = buildingId;
        this.floorNumber = floorNumber;
        this.rooms = rooms;
    }
}
