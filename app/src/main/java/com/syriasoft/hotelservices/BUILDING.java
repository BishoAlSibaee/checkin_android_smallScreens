package com.syriasoft.hotelservices;

public class BUILDING
{
    int id ;
    int projectId ;
    int buildingNu ;
    String buildingName ;
    int floorsNumber ;

    public BUILDING(int id, int projectId, int buildingNu, String buildingName, int floorsNumber) {
        this.id = id;
        this.projectId = projectId;
        this.buildingNu = buildingNu;
        this.buildingName = buildingName;
        this.floorsNumber = floorsNumber;
    }
}
