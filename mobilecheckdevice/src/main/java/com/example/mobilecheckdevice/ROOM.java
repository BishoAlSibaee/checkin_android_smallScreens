package com.example.mobilecheckdevice;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobilecheckdevice.lock.LockObj;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.api.ITuyaGateway;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ROOM {
    public int id ;
    public int RoomNumber ;
    public int Status ;
    public int hotel ;
    public int Building ;
    public int building_id ;
    public int Floor ;
    public int floor_id ;
    public String RoomType ;
    public int SuiteStatus ;
    public int SuiteNumber ;
    public int SuiteId ;
    public int ReservationNumber ;
    public int roomStatus ;
    public int ClientIn ;
    public String message ;
    public int selected ;
    public int loading ;
    public int Tablet ;
    public String dep ;
    public int Cleanup ;
    public int Laundry ;
    public int RoomService ;
    public String RoomServiceText ;
    public int Checkout ;
    public int Restaurant ;
    public int MiniBarCheck ;
    public int Facility ;
    public int SOS ;
    public int DND ;
    public int PowerSwitch ;
    public int DoorSensor ;
    public int MotionSensor ;
    public int Thermostat;
    public int ZBGateway ;
    public int online ;
    public int CurtainSwitch ;
    public int ServiceSwitch ;
    public int lock ;
    public int Switch1 ;
    public int Switch2 ;
    public int Switch3 ;
    public int Switch4 ;
    public String LockGateway ;
    public String LockName ;
    public int powerStatus ;
    public int curtainStatus ;
    public int doorStatus ;
    public int DoorWarning ;
    public int temp ;
    public int TempSetPoint ;
    public int SetPointInterval ;
    public int CheckInModeTime ;
    public int CheckOutModeTime ;
    public String WelcomeMessage ;
    public String Logo ;
    public String token ;
    private DatabaseReference FireRoom ;
    private DeviceBean POWER_B;
    private DeviceBean AC_B;
    private DeviceBean GATEWAY_B;
    private DeviceBean DOORSENSOR_B;
    private DeviceBean MOTIONSENSOR_B;
    private DeviceBean CURTAIN_B;
    private DeviceBean SERVICE1_B;
    private DeviceBean SERVICE2_B;
    private DeviceBean SWITCH1_B;
    private DeviceBean SWITCH2_B;
    private DeviceBean SWITCH3_B;
    private DeviceBean SWITCH4_B;
    private DeviceBean LOCK_B ;
    private ITuyaDevice POWER;
    private ITuyaDevice AC;
    private ITuyaDevice GATEWAY;
    private ITuyaDevice DOORSENSOR;
    private ITuyaDevice MOTIONSENSOR;
    private ITuyaDevice CURTAIN;
    private ITuyaDevice SERVICE1;
    private ITuyaDevice SERVICE2;
    private ITuyaDevice SWITCH1;
    private ITuyaDevice SWITCH2;
    private ITuyaDevice SWITCH3;
    private ITuyaDevice SWITCH4;
    private ITuyaDevice LOCK ;
    private ITuyaGateway WiredZBGateway ;
    private LockObj Lock ;
    ValueEventListener CleanupListener , LaundryListener , CheckoutListener , DNDListener , SetPointIntervalListener , DoorWarningListener , roomStatusListener , CheckInModeTimeListener , CheckOutModeTimeListener , ClientInListener ;


    public ROOM(int id, int roomNumber, int status, int hotel, int building, int building_id, int floor, int floor_id, String roomType, int suiteStatus, int suiteNumber, int suiteId, int reservationNumber, int roomStatus, int clientIn, String message, int selected, int loading, int tablet, String dep, int cleanup, int laundry, int roomService, String roomServiceText, int checkout, int restaurant, int miniBarCheck, int facility, int SOS, int DND, int powerSwitch, int doorSensor, int motionSensor, int thermostat, int ZBGateway, int online, int curtainSwitch, int serviceSwitch, int lock, int switch1, int switch2, int switch3, int switch4, String lockGateway, String lockName, int powerStatus, int curtainStatus, int doorStatus, int doorWarning, int temp, int tempSetPoint, int setPointInterval, int checkInModeTime, int checkOutModeTime, String welcomeMessage, String logo, String token) {
        this.id = id;
        RoomNumber = roomNumber;
        Status = status;
        this.hotel = hotel;
        Building = building;
        this.building_id = building_id;
        Floor = floor;
        this.floor_id = floor_id;
        RoomType = roomType;
        SuiteStatus = suiteStatus;
        SuiteNumber = suiteNumber;
        SuiteId = suiteId;
        ReservationNumber = reservationNumber;
        this.roomStatus = roomStatus;
        ClientIn = clientIn;
        this.message = message;
        this.selected = selected;
        this.loading = loading;
        Tablet = tablet;
        this.dep = dep;
        Cleanup = cleanup;
        Laundry = laundry;
        RoomService = roomService;
        RoomServiceText = roomServiceText;
        Checkout = checkout;
        Restaurant = restaurant;
        MiniBarCheck = miniBarCheck;
        Facility = facility;
        this.SOS = SOS;
        this.DND = DND;
        PowerSwitch = powerSwitch;
        DoorSensor = doorSensor;
        MotionSensor = motionSensor;
        Thermostat = thermostat;
        this.ZBGateway = ZBGateway;
        this.online = online;
        CurtainSwitch = curtainSwitch;
        ServiceSwitch = serviceSwitch;
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
        DoorWarning = doorWarning;
        this.temp = temp;
        TempSetPoint = tempSetPoint;
        SetPointInterval = setPointInterval;
        CheckInModeTime = checkInModeTime;
        CheckOutModeTime = checkOutModeTime;
        WelcomeMessage = welcomeMessage;
        Logo = logo;
        this.token = token;
    }

    public void setFireRoom(DatabaseReference fireRoom) {
        FireRoom = fireRoom;
    }

    public DatabaseReference getFireRoom() {
        return FireRoom;
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

    public void setSERVICE1_B(DeviceBean SERVICE_B) {
        this.SERVICE1_B = SERVICE_B;
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

    public void setSERVICE2_B(DeviceBean SERVICE2_B) {
        this.SERVICE2_B = SERVICE2_B;
    }

    public void setSERVICE2(ITuyaDevice SERVICE2) {
        this.SERVICE2 = SERVICE2;
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

    public void setSERVICE1(ITuyaDevice SERVICE) {
        this.SERVICE1 = SERVICE;
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

    public DeviceBean getSERVICE1_B() {
        return SERVICE1_B;
    }

    public DeviceBean getSERVICE2_B() {
        return SERVICE2_B;
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

    public ITuyaDevice getSERVICE1() {
        return SERVICE1;
    }

    public ITuyaDevice getSERVICE2() {
        return SERVICE2;
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

    public static void sortRoomsByNumber(List<ROOM> room) {
        for (int i = 0; i < room.size(); i++) {
            for (int j = 1; j < (room.size() - i); j++) {
                if (room.get(j - 1).RoomNumber > room.get(j).RoomNumber) {
                    Collections.swap(room, j, j - 1);
                }
            }
        }
    }

    void setDoorSensorStatus(String id,String status,Context c) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomDoorSensorInstalled";
        StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("doorSensor" , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("doorSensor" , "doorSensor updated successfully");
                    }
                    else {
                        Log.e("doorSensor" , "doorSensor update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("doorSensor" , "doorSensor update failed "+e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("doorSensor" , "doorSensor update failed "+error.toString());
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> Params = new HashMap<String,String>();
                Params.put("room_id",id);
                Params.put("room_status" , status);
                return Params;
            }
        };
        Volley.newRequestQueue(c).add(tabR);
    }

    void setServiceSwitchStatus(String id,String status,Context c) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomServiceSwitchInstalled";
        StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("serviceSwitch" , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("serviceSwitch" , "serviceSwitch updated successfully");
                    }
                    else {
                        Log.e("serviceSwitch" , "serviceSwitch update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("serviceSwitch" , "serviceSwitch update failed "+e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("serviceSwitch" , "serviceSwitch update failed "+error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> Params = new HashMap<String,String>();
                Params.put("room_id", id);
                Params.put("room_status" , status);
                return Params;
            }
        };
        Volley.newRequestQueue(c).add(tabR);
    }

    void setThermostatStatus(String id,String status,Context c) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomThermostatInstalled";
        StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("thermostat" , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("thermostat" , "thermostat updated successfully");
                    }
                    else {
                        Log.e("thermostat" , "thermostat update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("thermostat" , "thermostat update failed "+e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("thermostat" , "thermostat update failed "+error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> Params = new HashMap<String,String>();
                Params.put("room_id", id);
                Params.put("room_status" , status);
                return Params;
            }
        };
        Volley.newRequestQueue(c).add(tabR);
    }

    void setPowerSwitchStatus(String id,String status,Context c) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomPowerSwitchInstalled";
        StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("power " , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("power " , "power updated successfully");
                    }
                    else {
                        Log.e("power " , "power update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("power " , "power update failed "+e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("power " , "power update failed "+error.toString());
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> Params = new HashMap<String,String>();
                Params.put("room_id", id);
                Params.put("room_status" , status);
                return Params;
            }
        };
        Volley.newRequestQueue(c).add(tabR);
    }

    void setCurtainSwitchStatus(String id,String status,Context c) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomCurtainInstalled";
        StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("curtain" , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("curtain" , "curtain updated successfully");
                    }
                    else {
                        Log.e("curtain" , "curtain update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("curtain" , "curtain update failed "+e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("curtain" , "curtain update failed "+error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> Params = new HashMap<String,String>();
                Params.put("room_id", id);
                Params.put("room_status" , status);
                return Params;
            }
        };
        Volley.newRequestQueue(c).add(tabR);
    }

    void setMotionSensorStatus(String id,String status,Context c) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomMotionSensorInstalled";
        StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("motion" , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("motion" , "motion updated successfully");
                    }
                    else {
                        Log.e("motion" , "motion update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("motion" , "motion update failed "+e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("motion" , "motion update failed "+error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> Params = new HashMap<String,String>();
                Params.put("room_id", id);
                Params.put("room_status" , status);
                return Params;
            }
        };
        Volley.newRequestQueue(c).add(tabR);
    }

    void setSwitch1Status(String id,String status,Context c) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomSwitch1Installed";
        StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("switch1" , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("switch1" , "switch1 updated successfully");
                    }
                    else {
                        Log.e("switch1" , "switch1 update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("switch1" , "switch1 update failed "+e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("switch1" , "switch1 update failed "+error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> Params = new HashMap<String,String>();
                Params.put("room_id", id);
                Params.put("room_status" , status);
                return Params;
            }
        };
        Volley.newRequestQueue(c).add(tabR);
    }

    void setSwitch2Status(String id,String status,Context c) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomSwitch2Installed";
        StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("switch2" , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("switch2" , "switch2 updated successfully");
                    }
                    else {
                        Log.e("switch2" , "switch2 update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("switch2" , "switch2 update failed "+e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("switch2" , "switch2 update failed "+error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> Params = new HashMap<String,String>();
                Params.put("room_id", id);
                Params.put("room_status" , status);
                return Params;
            }
        };
        Volley.newRequestQueue(c).add(tabR);
    }

    void setSwitch3Status(String id,String status,Context c) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomSwitch3Installed";
        StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("switch3" , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("switch3" , "switch3 updated successfully");
                    }
                    else {
                        Log.e("switch3" , "switch3 update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("switch3" , "switch3 update failed "+e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("switch3" , "switch3 update failed "+error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> Params = new HashMap<String,String>();
                Params.put("room_id", id);
                Params.put("room_status" , status);
                return Params;
            }
        };
        Volley.newRequestQueue(c).add(tabR);
    }

    void setSwitch4Status(String id,String status,Context c) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomSwitch4Installed";
        StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("switch4" , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("switch4" , "switch4 updated successfully");
                    }
                    else {
                        Log.e("switch4" , "switch4 update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("switch4" , "switch4 update failed "+e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("switch4" , "switch4 update failed "+error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> Params = new HashMap<String,String>();
                Params.put("room_id", id);
                Params.put("room_status" , status);
                return Params;
            }
        };
        Volley.newRequestQueue(c).add(tabR);
    }

    void setZBGatewayStatus(String id,String status,Context c) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomGatewayInstalled";
        StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("gateway" , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("gateway" , "gateway updated successfully");
                    }
                    else {
                        Log.e("gateway" , "gateway update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("gateway" , "gateway update failed "+e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("gateway" , "gateway update failed "+error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> Params = new HashMap<String,String>();
                Params.put("room_id", id);
                Params.put("room_status" , status);
                return Params;
            }
        };
        Volley.newRequestQueue(c).add(tabR);
    }

    public static ROOM searchRoomInList(List<ROOM>rooms , int roomNumber) {
        if (rooms != null) {
            for (int i = 0; i<rooms.size(); i++) {
                if (rooms.get(i).RoomNumber == roomNumber) {
                    return rooms.get(i);
                }
            }
        }
        return null ;
    }

    public static void powerOnRoom(ROOM THEROOM,Context c) {
        if (THEROOM.getPOWER() != null ) {
            THEROOM.getPOWER().publishDps("{\"1\": true,\"2\": true}", new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    Log.d("powerOn",error);
                    Toast.makeText(c, THEROOM.RoomNumber+" Power on failed "+error, Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onSuccess() {
                    Log.d("powerOn","success");
                }
            });
        }
    }

    public static void powerOffRoom(ROOM THEROOM,Context c) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (THEROOM.getPOWER() != null ) {
                    if (THEROOM.roomStatus == 2) {
                        if (MyApp.ProjectVariables.PoweroffClientIn == 1) {
                            THEROOM.getPOWER().publishDps("{\"1\": false,\"2\": false}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    Log.d("powerOff",error);
                                    Toast.makeText(c, THEROOM.RoomNumber+" Power off 1 failed "+error, Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onSuccess() {
                                    Log.d("powerOff","success");
                                }
                            });
                        }
                    }
                    else {
                        THEROOM.getPOWER().publishDps("{\"1\": false,\"2\": false}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {
                                Log.d("powerOff",error);
                                Toast.makeText(c, THEROOM.RoomNumber+" Power off 1 failed "+error, Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onSuccess() {
                                Log.d("powerOff","success");
                            }
                        });
                    }
                }
            }
        });
    }

    public static void powerByCard(ROOM THEROOM,Context c) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (THEROOM.getPOWER() != null ) {
                    THEROOM.getPOWER().publishDps("{\"1\": true,\"2\": false}", new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {
                            Log.d("powerCard",error);
                            Toast.makeText(c, THEROOM.RoomNumber+" Power bycard failed " + error, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess() {
                            Log.d("powerCard","success");
                        }
                    });
                }
            }
        });
    }

}
