package com.syriasoft.hotelservices;

public class RESERVATION
{
    int id ;
    int RoomNumber ;
    int ClientId ;
    int Status ;
    int RoomOrSuite ;
    int MultiRooms ;
    String AddRoomNumber ;
    String AddRoomId ;
    String StartDate ;
    int Nights ;
    String EndDate ;
    int Hotel ;
    int BuildingNo ;
    int Floor ;
    String ClientFirstName ;
    String ClientLastName ;
    String IdType ;
    int IdNumber ;
    int MobileNumber ;
    String Email ;
    int Rating ;

    public RESERVATION(int id, int roomNumber, int clientId, int status, int roomOrSuite, int multiRooms, String addRoomNumber, String addRoomId, String startDate, int nights, String endDate, int hotel, int buildingNo, int floor, String clientFirstName, String clientLastName, String idType, int idNumber, int mobileNumber, String email, int rating) {
        this.id = id;
        RoomNumber = roomNumber;
        ClientId = clientId;
        Status = status;
        RoomOrSuite = roomOrSuite;
        MultiRooms = multiRooms;
        AddRoomNumber = addRoomNumber;
        AddRoomId = addRoomId;
        StartDate = startDate;
        Nights = nights;
        EndDate = endDate;
        Hotel = hotel;
        BuildingNo = buildingNo;
        Floor = floor;
        ClientFirstName = clientFirstName;
        ClientLastName = clientLastName;
        IdType = idType;
        IdNumber = idNumber;
        MobileNumber = mobileNumber;
        Email = email;
        Rating = rating;
    }
}
