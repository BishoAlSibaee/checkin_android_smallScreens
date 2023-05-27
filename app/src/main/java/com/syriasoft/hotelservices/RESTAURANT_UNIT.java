package com.syriasoft.hotelservices;

public class RESTAURANT_UNIT extends FACILITY
{
    int id ;
    int Hotel ;
    int TypeId ;
    String TypeName ;
    String Name ;
    int Control ;
    String photo ;

    public RESTAURANT_UNIT(int id, int hotel, int typeId, String typeName, String name, int control , String photo) {
        super(id,hotel,typeId,typeName,name,control ,photo);
        this.id = id;
        this.Hotel = hotel;
        this.TypeId = typeId;
        this.TypeName = typeName;
        this.Name = name;
        this.Control = control;
        this.photo = photo ;
    }
}
