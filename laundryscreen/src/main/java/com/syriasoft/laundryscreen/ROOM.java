package com.syriasoft.laundryscreen;

import android.util.Log;

public class ROOM
{
    public int id ;
    public int RoomNumber ;
    public int Hotel ;
    public int Building ;
    public int BuildingId ;
    public int Floor ;
    public int FloorId ;
    public String RoomType ;
    public int SuiteStatus ;
    public int SuiteNumber ;
    public int SuiteId ;
    public int ReservationNumber ;
    public int roomStatus ;
    public int Tablet ;
    public String dep ;
    public int Cleanup ;
    public int Laundry ;
    public int RoomService ;
    public int Checkout ;
    public int Restaurant ;
    public int SOS ;
    public int DND ;
    public int PowerSwitch ;
    public int DoorSensor ;
    public int MotionSensor ;
    public int Thermostat;
    public int ZBGateway ;
    public int CurtainSwitch ;
    public int ServiceSwitch ;
    public int lock ;
    public int Switch1 ;
    public int Switch2 ;
    public int Switch3 ;
    public int Switch4 ;
    String LockGateway ;
    public String LockName ;
    public int powerStatus ;
    public int curtainStatus ;
    public int doorStatus ;
    public int temp ;
    public String token ;







    public ROOM(int id, int roomNumber, int hotel, int building, int buildingId, int floor, int floorId, String roomType, int suiteStatus, int suiteNumber, int suiteId, int reservationNumber, int roomStatus, int tablet, String dep, int cleanup, int laundry, int roomService, int checkout, int restaurant, int SOS, int DND, int powerSwitch, int doorSensor, int motionSensor, int thermostat, int ZBGateway , int curtainSwitch , int ServiceSwitch, int lock, int switch1, int switch2, int switch3, int switch4, String lockGateway, String lockName, int powerStatus, int curtainStatus, int doorStatus, int temp, String token) {
        this.id = id;
        RoomNumber = roomNumber;
        Hotel = hotel;
        Building = building;
        BuildingId = buildingId;
        Floor = floor;
        FloorId = floorId;
        RoomType = roomType;
        SuiteStatus = suiteStatus;
        SuiteNumber = suiteNumber;
        SuiteId = suiteId;
        ReservationNumber = reservationNumber;
        this.roomStatus = roomStatus;
        Tablet = tablet;
        this.dep = dep;
        Cleanup = cleanup;
        Laundry = laundry;
        RoomService = roomService;
        Checkout = checkout;
        Restaurant = restaurant;
        this.SOS = SOS;
        this.DND = DND;
        PowerSwitch = powerSwitch;
        DoorSensor = doorSensor;
        MotionSensor = motionSensor;
        Thermostat = thermostat;
        this.ZBGateway = ZBGateway ;
        CurtainSwitch = curtainSwitch;
        this.ServiceSwitch = ServiceSwitch ;
        this.lock = lock;
        Switch1 = switch1;
        Switch2 = switch2;
        Switch3 = switch3;
        Switch4 = switch4;
        LockGateway = lockGateway;
        LockName = lockName;
        this.powerStatus = powerStatus;
        this.curtainStatus = curtainStatus;
        this.doorStatus = doorStatus;
        this.temp = temp;
        this.token = token;
    }



    public void printRoomOnLog()
    {
        Log.d("theroom " , "room "+RoomNumber+" zb "+ZBGateway+" power "+PowerSwitch);
    }






}
