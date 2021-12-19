package com.syriasoft.hotelservices;

import android.util.Log;

import com.syriasoft.hotelservices.lock.LockObj;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.api.ITuyaGateway;
import com.tuya.smart.sdk.bean.DeviceBean;

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


    private DeviceBean POWER_B , AC_B , GATEWAY_B , DOORSENSOR_B , MOTIONSENSOR_B , CURTAIN_B , SERVICE_B , SWITCH1_B , SWITCH2_B , SWITCH3_B , SWITCH4_B ,LOCK_B , SERVICE_B2;
    private ITuyaDevice POWER , AC , GATEWAY , DOORSENSOR , MOTIONSENSOR , CURTAIN , SERVICE , SWITCH1 , SWITCH2 , SWITCH3 , SWITCH4 ,LOCK,SERVICE2 ;
    private ITuyaGateway WiredZBGateway ;
    private LockObj Lock ;




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

    public void setPOWER_B(DeviceBean POWER_B) {
        this.POWER_B = POWER_B;
    }

    public void setAC_B(DeviceBean AC_B) {
        this.AC_B = AC_B;
    }

    public void setGATEWAY_B(DeviceBean GATEWAY_B) {
        this.GATEWAY_B = GATEWAY_B;
    }

    public void setDOORSENSOR_B(DeviceBean DOORSENSOR_B) {
        this.DOORSENSOR_B = DOORSENSOR_B;
    }

    public void setMOTIONSENSOR_B(DeviceBean MOTIONSENSOR_B) {
        this.MOTIONSENSOR_B = MOTIONSENSOR_B;
    }

    public void setCURTAIN_B(DeviceBean CURTAIN_B) {
        this.CURTAIN_B = CURTAIN_B;
    }

    public void setSERVICE_B(DeviceBean SERVICE_B) {
        this.SERVICE_B = SERVICE_B;
    }

    public void setSWITCH1_B(DeviceBean SWITCH1_B) {
        this.SWITCH1_B = SWITCH1_B;
    }

    public void setSWITCH2_B(DeviceBean SWITCH2_B) {
        this.SWITCH2_B = SWITCH2_B;
    }

    public void setSWITCH3_B(DeviceBean SWITCH3_B) {
        this.SWITCH3_B = SWITCH3_B;
    }

    public void setSWITCH4_B(DeviceBean SWITCH4_B) {
        this.SWITCH4_B = SWITCH4_B;
    }

    public void setLOCK_B(DeviceBean LOCK_B) {
        this.LOCK_B = LOCK_B;
    }

    public void setPOWER(ITuyaDevice POWER) {
        this.POWER = POWER;
    }

    public void setAC(ITuyaDevice AC) {
        this.AC = AC;
    }

    public void setGATEWAY(ITuyaDevice GATEWAY) {
        this.GATEWAY = GATEWAY;
    }

    public void setDOORSENSOR(ITuyaDevice DOORSENSOR) {
        this.DOORSENSOR = DOORSENSOR;
    }

    public void setMOTIONSENSOR(ITuyaDevice MOTIONSENSOR) {
        this.MOTIONSENSOR = MOTIONSENSOR;
    }

    public void setCURTAIN(ITuyaDevice CURTAIN) {
        this.CURTAIN = CURTAIN;
    }

    public void setSERVICE(ITuyaDevice SERVICE) {
        this.SERVICE = SERVICE;
    }

    public void setSWITCH1(ITuyaDevice SWITCH1) {
        this.SWITCH1 = SWITCH1;
    }

    public void setSWITCH2(ITuyaDevice SWITCH2) {
        this.SWITCH2 = SWITCH2;
    }

    public void setSWITCH3(ITuyaDevice SWITCH3) {
        this.SWITCH3 = SWITCH3;
    }

    public void setSWITCH4(ITuyaDevice SWITCH4) {
        this.SWITCH4 = SWITCH4;
    }

    public void setLOCK(ITuyaDevice LOCK) {
        this.LOCK = LOCK;
    }

    public DeviceBean getPOWER_B() {
        return POWER_B;
    }

    public DeviceBean getAC_B() {
        return AC_B;
    }

    public DeviceBean getGATEWAY_B() {
        return GATEWAY_B;
    }

    public DeviceBean getDOORSENSOR_B() {
        return DOORSENSOR_B;
    }

    public DeviceBean getMOTIONSENSOR_B() {
        return MOTIONSENSOR_B;
    }

    public DeviceBean getCURTAIN_B() {
        return CURTAIN_B;
    }

    public DeviceBean getSERVICE_B() {
        return SERVICE_B;
    }

    public DeviceBean getSWITCH1_B() {
        return SWITCH1_B;
    }

    public DeviceBean getSWITCH2_B() {
        return SWITCH2_B;
    }

    public DeviceBean getSWITCH3_B() {
        return SWITCH3_B;
    }

    public DeviceBean getSWITCH4_B() {
        return SWITCH4_B;
    }

    public DeviceBean getLOCK_B() {
        return LOCK_B;
    }

    public ITuyaDevice getPOWER() {
        return POWER;
    }

    public ITuyaDevice getAC() {
        return AC;
    }

    public ITuyaDevice getGATEWAY() {
        return GATEWAY;
    }

    public ITuyaDevice getDOORSENSOR() {
        return DOORSENSOR;
    }

    public ITuyaDevice getMOTIONSENSOR() {
        return MOTIONSENSOR;
    }

    public ITuyaDevice getCURTAIN() {
        return CURTAIN;
    }

    public ITuyaDevice getSERVICE() {
        return SERVICE;
    }

    public ITuyaDevice getSWITCH1() {
        return SWITCH1;
    }

    public ITuyaDevice getSWITCH2() {
        return SWITCH2;
    }

    public ITuyaDevice getSWITCH3() {
        return SWITCH3;
    }

    public ITuyaDevice getSWITCH4() {
        return SWITCH4;
    }

    public ITuyaDevice getLOCK() {
        return LOCK;
    }

    public ITuyaGateway getWiredZBGateway() {
        return WiredZBGateway;
    }

    public void setWiredZBGateway(ITuyaGateway wiredZBGateway) {
        WiredZBGateway = wiredZBGateway;
    }

    public void setLock(LockObj lock) {
        Lock = lock;
    }

    public LockObj getLock() {
        return Lock;
    }

    public void setSERVICE_B2(DeviceBean SERVICE_B2) {
        this.SERVICE_B2 = SERVICE_B2;
    }

    public void setSERVICE2(ITuyaDevice SERVICE2) {
        this.SERVICE2 = SERVICE2;
    }

    public DeviceBean getSERVICE_B2() {
        return SERVICE_B2;
    }

    public ITuyaDevice getSERVICE2() {
        return SERVICE2;
    }
}
