package com.syriasoft.cleanup;

public class FACILITY {
    int id;
    int Hotel;
    int TypeId;
    String TypeName;
    String Name;
    int Control;
    String photo;

    public FACILITY(int id, int hotel, int typeId, String typeName, String name, int control, String photo) {
        this.id = id;
        Hotel = hotel;
        TypeId = typeId;
        TypeName = typeName;
        Name = name;
        Control = control;
        this.photo = photo;
    }
}
