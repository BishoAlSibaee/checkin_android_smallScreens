package com.example.hotelservicesstandalone;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hotelservicesstandalone.lock.AccountInfo;
import com.example.hotelservicesstandalone.lock.ApiService;
import com.example.hotelservicesstandalone.lock.GatewayObj;
import com.example.hotelservicesstandalone.lock.LockObj;
import com.example.hotelservicesstandalone.lock.MyApplication;
import com.example.hotelservicesstandalone.lock.RetrofitAPIManager;
import com.example.hotelservicesstandalone.lock.ServerError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.reflect.TypeToken;
import com.ttlock.bl.sdk.api.ExtendedBluetoothDevice;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.ControlLockCallback;
import com.ttlock.bl.sdk.constant.ControlAction;
import com.ttlock.bl.sdk.entity.ControlLockResult;
import com.ttlock.bl.sdk.entity.LockError;
import com.ttlock.bl.sdk.gateway.api.GatewayClient;
import com.ttlock.bl.sdk.gateway.callback.ConnectCallback;
import com.ttlock.bl.sdk.gateway.callback.InitGatewayCallback;
import com.ttlock.bl.sdk.gateway.model.ConfigureGatewayInfo;
import com.ttlock.bl.sdk.gateway.model.DeviceInfo;
import com.ttlock.bl.sdk.gateway.model.GatewayError;
import com.ttlock.bl.sdk.util.DigitUtil;
import com.ttlock.bl.sdk.util.GsonUtil;
import com.ttlock.bl.sdk.util.LogUtil;
import com.tuya.smart.android.device.api.ITuyaDeviceMultiControl;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.bean.scene.PreCondition;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.bean.scene.SceneCondition;
import com.tuya.smart.home.sdk.bean.scene.SceneTask;
import com.tuya.smart.home.sdk.bean.scene.condition.rule.BoolRule;
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.enums.TYDevicePublishModeEnum;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import retrofit2.Call;
import retrofit2.Callback;

public class Rooms extends AppCompatActivity
{
    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    final static private String serverKey = "key=" + "AAAAQmygXvw:APA91bFt5CiONiZPDDj4_kz9hmKXlL1cjfTa_ZNGfobMPmt0gamhzEoN2NHiOxypCDr_r5yfpLvJy-bQSgrykXvaqKkThAniTr-0hpXPBrXm7qWThMmkiaN9o6qaUqfIUwStMMuNedTw";
    final static private String contentType = "application/json";
    private TextView hotelName ;
    private ListView devicesListView , roomsListView ;
    static List<ROOM> ROOMS;
    private final String getRoomsUrl = MyApp.THE_PROJECT.url + "roomsManagement/getRoomsForControllDevice" ;
    static Activity act ;
    static ArrayList<LockObj> Locks ;
    public AccountInfo accountInfo;
    public static AccountInfo acc;
    private List<DeviceBean> Devices ;
    private FirebaseDatabase database ;
    private boolean[] CLEANUP , LAUNDRY , DND , CHECKOUT ;
    private Runnable[] TempRunnableList, DoorRunnable ;
    private Handler[] DoorsHandlers,AcHandlers;
    private boolean[] AC_SENARIO_Status , DOOR_STATUS;
    private long[] AC_Start, AC_Period, Door_Start, Door_Period;
    private String[] Client_Temp, TempSetPoint ;
    private Rooms_Adapter_Base adapter ;
    ITuyaDeviceMultiControl iTuyaDeviceMultiControl ;
    static int checkInModeTime = 0 ;
    static int checkOutModeTime = 0 ;
    private LockDB lockDB ;
    Button toggle , resetDevices ;
    LinearLayout logoLayout , btnsLayout ,mainLogo ;
    private static List<ServiceEmps> Emps ;
    private DatabaseReference ServiceUsers ;
    static RequestQueue MessagesQueue;
    static boolean CHANGE_STATUS = false ;
    lodingDialog loading;
    RequestQueue REQ , REQ1 , CLEANUP_QUEUE , LAUNDRY_QUEUE , CHECKOUT_QUEUE ,DND_Queue ;
    EditText searchText ;
    Button searchBtn ;
    ListView gatewaysListView ;
    ExtendedBluetoothDevice TheFoundGateway ;
    private ConfigureGatewayInfo configureGatewayInfo;
    static List<SceneBean> SCENES ;
    List<String> IMAGES ;
    DatabaseReference ServerDevice , ProjectVariablesRef , DevicesControls , ProjectDevices  ;
    int addCleanupCounter=1,cancelOrderCounter=1,addLaundryCounter =1,addCheckoutCounter=1,addDNDCounter=1,cancelDNDCounter = 1 ;
    String PowerUnInstalled,PowerInstalled,GatewayUnInstalled,GatewayInstalled,MotionUnInstalled,MotionInstalled,DoorUnInstalled,DoorInstalled,ServiceUnInstalled,ServiceInstalled,S1UnInstalled,S1Installed,S2UnInstalled,S2Installed,S3UnInstalled,S3Installed,S4UnInstalled,S4Installed,ACUnInstalled,ACInstalled,CurtainUnInstalled,CurtainInstalled,LockUnInstalled,LockInstalled;
    private RequestQueue FirebaseTokenRegister ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rooms);
        setActivity();
        getProjectVariables();
        getServiceUsersFromFirebase();
        hideSystemUI();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                String token = task.getResult();
                Log.e("tokeneee" , token);
                sendRegistrationToServer(token);
            }
        });
        Timer t = new Timer() ;
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult();
                        sendRegistrationToServer(token);
                    }
                });
            }},1000*60*15,1000*60*15);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        if (CHANGE_STATUS) {
            getRooms();
            loginTTLock();
            CHANGE_STATUS = false ;
        }
    }

    private void setActivity() {
        act = this ;
        REQ = Volley.newRequestQueue(act);
        REQ1 = Volley.newRequestQueue(act);
        CLEANUP_QUEUE = Volley.newRequestQueue(act);
        LAUNDRY_QUEUE = Volley.newRequestQueue(act);
        CHECKOUT_QUEUE = Volley.newRequestQueue(act);
        DND_Queue = Volley.newRequestQueue(act);
        lockDB = new LockDB(act);
        if (!lockDB.isLoggedIn()) {
            lockDB.removeAll();
            lockDB.insertLock("off");
        }
        SCENES = new ArrayList<>();
        configureGatewayInfo = new ConfigureGatewayInfo();
        gatewaysListView = findViewById(R.id.scanLockGatewayList);
        searchText = findViewById(R.id.search_text);
        searchBtn = findViewById(R.id.button16);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchBtn.getText().toString().equals("X")) {
                    if (searchText.getText() == null ) {
                        Toast.makeText(act,"enter text",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (devicesListView.getVisibility() == View.VISIBLE ) {
                        String Text = searchText.getText().toString() ;
                        List<DeviceBean> Results = new ArrayList<DeviceBean>();
                        for (int i = 0 ; i < Devices.size() ; i++) {
                            if (Devices.get(i).getName().contains(Text)) {
                                Results.add(Devices.get(i));
                            }
                        }
                        if (Results.size() > 0 ) {
                            searchBtn.setText("X");
//                            String[] x = new String[Results.size()];
//                            for (int j=0; j<Results.size(); j++) {
//                                x[j] = Results.get(j);
//                            }
                            //ArrayAdapter<String> ad = new ArrayAdapter<String>(act,R.layout.spinners_item,x);
                            Devices_Adapter adapter = new Devices_Adapter(Results,act);
                            devicesListView.setAdapter(adapter);
                        }
                        else {
                            Toast.makeText(act,"no results",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    if (devicesListView.getVisibility() == View.VISIBLE) {
                        searchBtn.setText("Search");
//                        String[] dd = new String[Devices.size()];
//                        for (int i=0;i<Devices.size();i++)
//                        {
//                            dd[i] = Devices.get(i).name ;
//                        }
                        Devices_Adapter adapter = new Devices_Adapter(Devices,act);
                        devicesListView.setAdapter(adapter);
                    }
                }
            }
        });
        MessagesQueue = Volley.newRequestQueue(act);
        Emps = new ArrayList<>();
        toggle = findViewById(R.id.button9);
        mainLogo = findViewById(R.id.logoLyout) ;
        resetDevices = findViewById(R.id.button2);
        btnsLayout = findViewById(R.id.btnsLayout);
        hotelName = findViewById(R.id.hotelName);
        hotelName.setText(MyApp.THE_PROJECT.projectName);
        ROOMS = new ArrayList<>();
        Locks = new ArrayList<>();
        Devices = new ArrayList<>();
        roomsListView = findViewById(R.id.RoomsListView);
        devicesListView = findViewById(R.id.DevicesListView);
        database = FirebaseDatabase.getInstance("https://hotelservices-ebe66.firebaseio.com/");
        ServerDevice = database.getReference(MyApp.THE_PROJECT.projectName+"ServerDevices/"+MyApp.Device_Name);
        ServiceUsers = database.getReference(MyApp.THE_PROJECT.projectName+"ServiceUsers");
        ProjectVariablesRef = database.getReference(MyApp.THE_PROJECT.projectName+"ProjectVariables");
        DevicesControls = database.getReference(MyApp.THE_PROJECT.projectName+"DevicesControls");
        ProjectDevices = database.getReference(MyApp.THE_PROJECT.projectName+"Devices");
        iTuyaDeviceMultiControl = TuyaHomeSdk.getDeviceMultiControlInstance();
        mainLogo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Dialog  dd = new Dialog(act);
                dd.setContentView(R.layout.lock_unlock_dialog);
                Button cancel = dd.findViewById(R.id.confermationDialog_cancel);
                Button lock = dd.findViewById(R.id.messageDialog_ok);
                EditText password = dd.findViewById(R.id.editTextTextPassword);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dd.dismiss();
                    }
                });
                lock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final lodingDialog loading = new lodingDialog(act);
                        final String pass = password.getText().toString() ;
                        StringRequest re = new StringRequest(Request.Method.POST, "", new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response)
                            {
                                Log.d("LoginResult" , response );
                                loading.stop();
                                if (response.equals("1"))
                                {
                                    lockDB.modifyValue("off");
                                    roomsListView.setVisibility(View.VISIBLE);
                                    devicesListView.setVisibility(View.GONE);
                                    btnsLayout.setVisibility(View.VISIBLE);
                                    mainLogo.setVisibility(View.GONE);
                                    dd.dismiss();
                                }
                                else if (response.equals("0"))
                                {
                                    Toast.makeText(act,"UnLock Failed",Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Toast.makeText(act,"No Params",Toast.LENGTH_LONG).show();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                loading.stop();
                            }
                        })
                        {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError
                            {
                                Map<String,String> par = new HashMap<String, String>();
                                par.put( "password" , pass ) ;
                                par.put( "hotel" , "1" ) ;
                                return par;
                            }
                        };
                        Volley.newRequestQueue(act).add(re);
                    }
                });
                dd.show();
                return false;
            }
        });
        mainLogo.setVisibility(View.GONE);
        roomsListView.setVisibility(View.VISIBLE);
        devicesListView.setVisibility(View.GONE);
        hideSystemUI();
        searchBtn.setVisibility(View.GONE);
        searchText.setVisibility(View.GONE);
        if (lockDB.getLockValue().equals("off")) {
            roomsListView.setVisibility(View.VISIBLE);
            devicesListView.setVisibility(View.GONE);
            btnsLayout.setVisibility(View.VISIBLE);
            mainLogo.setVisibility(View.GONE);
        }
        else if (lockDB.getLockValue().equals("on")) {
            roomsListView.setVisibility(View.GONE);
            devicesListView.setVisibility(View.GONE);
            btnsLayout.setVisibility(View.GONE);
            mainLogo.setVisibility(View.VISIBLE);
        }
        else {
            roomsListView.setVisibility(View.GONE);
            devicesListView.setVisibility(View.GONE);
            btnsLayout.setVisibility(View.GONE);
            mainLogo.setVisibility(View.VISIBLE);
        }
        login();
    }

    private void getProjectVariables() {
        loading = new lodingDialog(act);
        String url = MyApp.THE_PROJECT.url + "roomsManagement/getProjectVariables";
        StringRequest re = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject row = new JSONObject(response);
                    JSONObject ServiceSwitchButtons = new JSONObject(row.getString("ServiceSwitchButtons"));
                    MyApp.ProjectVariables = new PROJECT_VARIABLES(row.getInt("id"),row.getString("projectName"),row.getInt("Hotel"),row.getInt("Temp"),row.getInt("Interval"),row.getInt("DoorWarning"),row.getInt("CheckinModeActive"),row.getInt("CheckInModeTime"),row.getString("CheckinActions"),row.getInt("CheckoutModeActive"),row.getInt("CheckOutModeTime"),row.getString("CheckoutActions"),row.getString("WelcomeMessage"),row.getString("Logo"),row.getInt("PoweroffClientIn"),row.getInt("PoweroffAfterHK"),row.getInt("ACSenarioActive"),row.getString("OnClientBack"),row.getInt("HKCleanupTime"));
                    MyApp.ProjectVariables.setServiceSwitchButtons(ServiceSwitchButtons);
                    MyApp.checkInActions = new CheckInActions(MyApp.ProjectVariables.CheckinActions);
                    MyApp.checkOutActions = new CheckoutActions(MyApp.ProjectVariables.CheckoutActions);
                    MyApp.clientBackActions = new ClientBackActions(MyApp.ProjectVariables.OnClientBack);
                    setProjectVariablesListener();
                    setControlDeviceListener();
                }
                catch (JSONException e) {
                    loading.stop();
                    new MessageDialog("error getting project variables "+e.toString(),"error",act);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.stop();
                new MessageDialog("error getting project variables "+error.toString(),"error",act);
            }
        });
        Volley.newRequestQueue(act).add(re);
    }

    private void getRooms() {
        StringRequest re = new StringRequest(Request.Method.POST, getRoomsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("roomsAre" , response);
                if (response.equals("0")) {
                    loading.stop();
                    new MessageDialog("no rooms detected ","No Rooms",act);
                    return;
                }
                try {
                    JSONObject ress = new JSONObject(response);
                    if (ress.getString("result").equals("success")) {
                        JSONArray arr = ress.getJSONArray("rooms");
                        ROOMS.clear();
                        for (int i=0;i<arr.length();i++) {
                            JSONObject row = arr.getJSONObject(i);
                            int id = row.getInt("id");
                            int roomNumber = row.getInt("RoomNumber");
                            int status = row.getInt("Status");
                            int hotel = row.getInt("hotel");
                            int building = row.getInt("Building");
                            int building_id = row.getInt("building_id");
                            int floor = row.getInt("Floor");
                            int floor_id = row.getInt("floor_id");
                            String roomType = row.getString("RoomType");
                            int suiteStatus = row.getInt("SuiteStatus");
                            int suiteNumber = row.getInt("SuiteNumber");
                            int suiteId = row.getInt("SuiteId");
                            int reservationNumber = row.getInt("ReservationNumber");
                            int roomStatus = row.getInt("roomStatus");
                            int clientIn = row.getInt("ClientIn");
                            String message = row.getString("message");
                            int selected = row.getInt("selected");
                            int load = row.getInt("loading");
                            int tablet = row.getInt("Tablet");
                            String dep = row.getString("dep");
                            int cleanup = row.getInt("Cleanup");
                            int laundry = row.getInt("Laundry");
                            int roomService = row.getInt("RoomService");
                            String roomServiceText = row.getString("RoomServiceText");
                            int checkout = row.getInt("Checkout");
                            int restaurant = row.getInt("Restaurant");
                            int miniBarCheck = row.getInt("MiniBarCheck");
                            int facility = row.getInt("Facility");
                            int SOS = row.getInt("SOS");
                            int DND = row.getInt("DND");
                            int powerSwitch = row.getInt("PowerSwitch");
                            int doorSensor = row.getInt("DoorSensor");
                            int motionSensor = row.getInt("MotionSensor");
                            int thermostat = row.getInt("Thermostat");
                            int ZBGateway = row.getInt("ZBGateway");
                            int online = row.getInt("online");
                            int curtainSwitch = row.getInt("CurtainSwitch");
                            int serviceSwitch = row.getInt("ServiceSwitch");
                            int lock = row.getInt("lock");
                            int switch1 = row.getInt("Switch1");
                            int switch2 = row.getInt("Switch2");
                            int switch3 = row.getInt("Switch3");
                            int switch4 = row.getInt("Switch4");
                            String lockGateway = row.getString("LockGateway");
                            String lockName = row.getString("LockName");
                            int powerStatus = row.getInt("powerStatus");
                            int curtainStatus = row.getInt("curtainStatus");
                            int doorStatus = row.getInt("doorStatus");
                            int doorWarning = row.getInt("DoorWarning");
                            int temp = row.getInt("temp");
                            int tempSetPoint = row.getInt("TempSetPoint");
                            int setPointInterval = row.getInt("SetPointInterval");
                            String welcomeMessage = row.getString("WelcomeMessage");
                            String logo = row.getString("Logo");
                            String token =row.getString("token");
                            ROOM room = new ROOM(id,roomNumber,status,hotel,building,building_id,floor,floor_id,roomType,suiteStatus,suiteNumber,suiteId,reservationNumber,roomStatus,clientIn,message,selected,load,tablet,dep,cleanup,laundry
                                    ,roomService,roomServiceText,checkout,restaurant,miniBarCheck,facility,SOS,DND,powerSwitch,doorSensor,motionSensor,thermostat,ZBGateway,online,curtainSwitch,serviceSwitch,lock,switch1,switch2,switch3,switch4,lockGateway
                                    ,lockName,powerStatus,curtainStatus,doorStatus,doorWarning,temp,tempSetPoint,setPointInterval,checkInModeTime,checkOutModeTime,welcomeMessage,logo,token);
                            room.setFireRoom(database.getReference(MyApp.THE_PROJECT.projectName+"/B"+room.Building+"/F"+room.Floor+"/R"+room.RoomNumber));
                            ROOMS.add(room);
                        }
                    }
                    else {
                        loading.stop();
                        new MessageDialog("getting rooms failed "+ress.getString("error"),"error",act);
                    }
                }
                catch (JSONException e) {
                    loading.stop();
                    new MessageDialog("getting rooms failed "+e.toString(),"error",act);
                }
                ROOM.sortRoomsByNumber(ROOMS);
                MyApp.ROOMS = ROOMS ;
                hotelName.setText(MyApp.THE_PROJECT.projectName+" "+ROOMS.size() + " room");
                defineVariables();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.stop();
                new MessageDialog("getting rooms failed "+error.toString(),"error",act);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("device_id",MyApp.Device_Id);
                return params;
            }
        };
        Volley.newRequestQueue(act).add(re);
    }

    void defineVariables() {
        AC_SENARIO_Status = new boolean[ROOMS.size()];
        DOOR_STATUS = new boolean[ROOMS.size()];
        AC_Start = new long[ROOMS.size()];
        Door_Start = new long[ROOMS.size()];
        AC_Period = new long[ROOMS.size()];
        Door_Period = new long[ROOMS.size()];
        Client_Temp = new String[ROOMS.size()];
        TempSetPoint = new String[ROOMS.size()];
        TempRunnableList = new Runnable[ROOMS.size()];
        DoorRunnable = new Runnable[ROOMS.size()];
        CLEANUP = new boolean[ROOMS.size()];
        LAUNDRY = new boolean[ROOMS.size()];
        DND = new boolean[ROOMS.size()];
        CHECKOUT = new boolean[ROOMS.size()];
        DoorsHandlers = new Handler[ROOMS.size()];
        AcHandlers = new Handler[ROOMS.size()];
        for (int t = 0; t< ROOMS.size(); t++) {
            CLEANUP[t] = false ;
            LAUNDRY[t] = false ;
            DND[t] = false ;
            CHECKOUT[t] = false ;
            AC_SENARIO_Status[t] = false ;
            DOOR_STATUS[t] = false ;
            AC_Start[t] = 0 ;
            Door_Start[t]=0;
            AC_Period[t]=0 ;
            Door_Period[t]=0;
            Client_Temp[t] = "0" ;
            if (MyApp.ProjectVariables.Temp != 0) {
                TempSetPoint[t] = MyApp.ProjectVariables.Temp+"0" ;
            }
            int finalT = t;
            int finalT1 = t;
            int finalT2 = t;
            DoorRunnable[t] = new Runnable() {
                @Override
                public void run() {
                    DoorsHandlers[finalT] = new Handler();
                    DoorsHandlers[finalT].postDelayed(this,1000) ;
                    Door_Period[finalT] = System.currentTimeMillis() - Door_Start[finalT] ;
                    if ( Door_Period[finalT] >=  MyApp.ProjectVariables.DoorWarning  && DOOR_STATUS[finalT])
                    {
                        ROOMS.get(finalT).getFireRoom().child("doorStatus").setValue(2);
                        DoorsHandlers[finalT].removeCallbacks(DoorRunnable[finalT]);
                    }
                    else if (Door_Period[finalT] >=  MyApp.ProjectVariables.DoorWarning  && !DOOR_STATUS[finalT])
                    {
                        DoorsHandlers[finalT].removeCallbacks(DoorRunnable[finalT]);
                    }

                }
            };
            TempRunnableList[t] = new Runnable() {
                @Override
                public void run() {
                    AcHandlers[finalT] = new Handler();
                    AcHandlers[finalT].postDelayed(TempRunnableList[finalT], 1000);
                    AC_Period[finalT] = System.currentTimeMillis() - AC_Start[finalT] ;
                    Log.d("acSenario" ,AC_Period[finalT]+" "+MyApp.ProjectVariables.Interval +" "+AC_SENARIO_Status[finalT]);
                    if ( AC_Period[finalT] >=  MyApp.ProjectVariables.Interval  && AC_SENARIO_Status[finalT]) {
                        if (ROOMS.get(finalT2).getAC() != null ) {
                            ROOMS.get(finalT2).getAC().publishDps("{\" 2\": "+TempSetPoint[finalT]+"}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {
                                    AC_SENARIO_Status[finalT1] = false ;
                                    AcHandlers[finalT].removeCallbacks(TempRunnableList[finalT]);
                                }
                            });
                        }

                    }
                    else if (AC_Period[finalT] >=  MyApp.ProjectVariables.Interval  && !AC_SENARIO_Status[finalT]) {
                        AcHandlers[finalT].removeCallbacks(TempRunnableList[finalT]);
                    }
                }
            };
        }
        getTuyaDevices() ;
        loginTTLock();
    }

    private void loginTTLock() {
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        String pass = MyApp.THE_PROJECT.LockPassword;
        pass = DigitUtil.getMD5(pass);
        Call<String> call = apiService.auth(ApiService.CLIENT_ID, ApiService.CLIENT_SECRET, "password", MyApp.THE_PROJECT.LockUser, pass, ApiService.REDIRECT_URI);
        String finalPass = pass;
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                String json = response.body();
                accountInfo = GsonUtil.toObject(json, AccountInfo.class);
                if (accountInfo != null) {
                    if (accountInfo.errcode == 0) {
                        Log.d("TTLOCKLogin" , "success");
                        accountInfo.setMd5Pwd(finalPass);
                        acc = accountInfo;
                        Log.d("TTLOCKLogin" , accountInfo.getAccess_token());
                        getLocks();
                    }
                    else {
                        new MessageDialog("lock login failed "+accountInfo.errcode,"lock login failed",act);
                    }
                }
                else {
                    new MessageDialog("lock login failed account null","lock login failed",act);
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                new MessageDialog("lock login failed "+t.getMessage(),"lock login failed",act);
            }
        });
    }

    private void getLocks() {
        final Dialog d = new Dialog(this);
        d.setContentView(R.layout.loading_layout);
        d.setCancelable(false);
        d.show();
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.getLockList(ApiService.CLIENT_ID,acc.getAccess_token(), 1, 100, System.currentTimeMillis());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                d.dismiss();
                String json = response.body();
                if (json.contains("list")) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray array = jsonObject.getJSONArray("list");
                        Locks = GsonUtil.toObject(array.toString(), new TypeToken<ArrayList<LockObj>>(){});
                        Log.d("locksNum" ,String.valueOf( Locks.size() ));
                    }
                    catch (JSONException e) {
                    }
                    setLocks(Locks);
                }
                else {
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    void getTuyaDevices() {
        TuyaHomeSdk.newHomeInstance(MyApp.HOME.getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean homeBean) {
                Devices.clear();
                Devices = homeBean.getDeviceList();
                if (Devices.size() == 0) {
                    Toast.makeText(act,"no devices",Toast.LENGTH_LONG).show();
                    Log.d("devicesAre " ,"no devices" );
                }
                else {
                    Toast.makeText(act,"Devices are: "+Devices.size(),Toast.LENGTH_LONG).show();
                    for (int i=0;i<ROOMS.size();i++) {
                        DeviceBean power = searchRoomDevice(Devices,ROOMS.get(i),"Power");
                        if (power == null) {
                            ROOMS.get(i).PowerSwitch = 0 ;
                        }
                        else {
                            ROOMS.get(i).setPOWER_B(power);
                            ROOMS.get(i).setPOWER(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getPOWER_B().devId));
                            ROOMS.get(i).PowerSwitch = 1 ;
                            if (power.dps.get("1") != null && power.dps.get("2") != null) {
                                if (!Boolean.parseBoolean(power.dps.get("1").toString()) && !Boolean.parseBoolean(power.dps.get("2").toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getPOWER_B().name).child("1").setValue(0);
                                }
                                else if (Boolean.parseBoolean(power.dps.get("1").toString()) && !Boolean.parseBoolean(power.dps.get("2").toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getPOWER_B().name).child("1").setValue(1);
                                }
                                else if (Boolean.parseBoolean(power.dps.get("1").toString()) && Boolean.parseBoolean(power.dps.get("2").toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getPOWER_B().name).child("1").setValue(2);
                                }
                            }
                        }
                        DeviceBean ac = searchRoomDevice(Devices,ROOMS.get(i),"AC") ;
                        if (ac == null) {
                            ROOMS.get(i).Thermostat = 0 ;
                        }
                        else {
                            ROOMS.get(i).setAC_B(ac);
                            ROOMS.get(i).setAC(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getAC_B().devId));
                            ROOMS.get(i).Thermostat = 1 ;
                            int finalI = i;
                            TuyaHomeSdk.getSceneManagerInstance().getDeviceConditionOperationList(ac.devId, new ITuyaResultCallback<List<TaskListBean>>() {
                                @Override
                                public void onSuccess(List<TaskListBean> result) {
                                    long SetId = 0 ;
                                    TaskListBean SetTask = null ;
                                    long PowerId = 0 ;
                                    TaskListBean PowerTask ;
                                    long CurrentId = 0 ;
                                    TaskListBean CurrentTask ;
                                    long FanId = 0; ;
                                    TaskListBean FanTask ;
                                    for (int i=0 ; i<result.size();i++) {
                                        if (result.get(i).getName().contains("Set temp")) {
                                            SetId = result.get(i).getDpId() ;
                                            SetTask = result.get(i) ;
                                        }
                                        if (result.get(i).getName().contains("Power")) {
                                            PowerId = result.get(i).getDpId() ;
                                            PowerTask = result.get(i) ;
                                        }
                                        if (result.get(i).getName().contains("Current temp")) {
                                            CurrentId = result.get(i).getDpId() ;
                                            CurrentTask = result.get(i) ;
                                        }
                                        if (result.get(i).getName().contains("Fan")) {
                                            FanId = result.get(i).getDpId() ;
                                            FanTask = result.get(i) ;
                                        }
                                    }
                                    if (PowerId != 0) {
                                        if (Boolean.parseBoolean(ac.dps.get(String.valueOf(PowerId)).toString())) {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(PowerId)).setValue(3);
                                        }
                                        else {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(PowerId)).setValue(0);
                                        }
                                    }
                                    if (SetId != 0) {
                                        ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(SetId)).setValue(Integer.parseInt(ac.dps.get(String.valueOf(SetId)).toString()));
                                    }
                                    if (FanId != 0) {
                                        ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(FanId)).setValue(ac.dps.get(String.valueOf(FanId)).toString());
                                    }
                                }

                                @Override
                                public void onError(String errorCode, String errorMessage) {

                                }
                            });
                        }
                        DeviceBean ZGatway = searchRoomDevice(Devices,ROOMS.get(i),"ZGatway") ;
                        if (ZGatway == null) {
                            ROOMS.get(i).ZBGateway = 0 ;
                        }
                        else {
                            ROOMS.get(i).setGATEWAY_B(ZGatway);
                            ROOMS.get(i).setGATEWAY(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getGATEWAY_B().devId));
                            ROOMS.get(i).setWiredZBGateway(TuyaHomeSdk.newGatewayInstance(ROOMS.get(i).getGATEWAY_B().devId));
                            ROOMS.get(i).ZBGateway = 1 ;
                        }
                        DeviceBean DoorSensor = searchRoomDevice(Devices,ROOMS.get(i),"DoorSensor") ;
                        if (DoorSensor == null) {
                            ROOMS.get(i).DoorSensor = 0 ;
                        }
                        else {
                            ROOMS.get(i).setDOORSENSOR_B(DoorSensor);
                            ROOMS.get(i).setDOORSENSOR(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getDOORSENSOR_B().devId));
                            ROOMS.get(i).DoorSensor = 1 ;
                        }
                        DeviceBean MotionSensor = searchRoomDevice(Devices,ROOMS.get(i),"MotionSensor") ;
                        if (MotionSensor == null) {
                            ROOMS.get(i).MotionSensor = 0 ;
                        }
                        else {
                            ROOMS.get(i).setMOTIONSENSOR_B(MotionSensor);
                            ROOMS.get(i).setMOTIONSENSOR(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getMOTIONSENSOR_B().devId));
                            ROOMS.get(i).MotionSensor = 1 ;
                        }
                        DeviceBean Curtain = searchRoomDevice(Devices,ROOMS.get(i),"Curtain") ;
                        if (Curtain == null) {
                            ROOMS.get(i).CurtainSwitch = 0 ;
                        }
                        else {
                            ROOMS.get(i).setCURTAIN_B(Curtain);
                            ROOMS.get(i).setCURTAIN(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getCURTAIN_B().devId));
                            ROOMS.get(i).CurtainSwitch = 1 ;
                        }
                        DeviceBean ServiceSwitch = searchRoomDevice(Devices,ROOMS.get(i),"ServiceSwitch") ;
                        if (ServiceSwitch == null) {
                            ROOMS.get(i).ServiceSwitch = 0 ;
                        }
                        else {
                            ROOMS.get(i).setSERVICE1_B(ServiceSwitch);
                            ROOMS.get(i).setSERVICE1(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSERVICE1_B().devId));
                            ROOMS.get(i).ServiceSwitch = 1 ;
                        }
                        DeviceBean ServiceSwitch2 = searchRoomDevice(Devices,ROOMS.get(i),"ServiceSwitch2") ;
                        if (ServiceSwitch2 != null) {
                            ROOMS.get(i).setSERVICE2_B(ServiceSwitch);
                            ROOMS.get(i).setSERVICE2(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSERVICE2_B().devId));
                        }
                        DeviceBean Switch1 = searchRoomDevice(Devices,ROOMS.get(i),"Switch1") ;
                        if (Switch1 == null) {
                            ROOMS.get(i).Switch1 = 0 ;
                        }
                        else {
                            ROOMS.get(i).setSWITCH1_B(Switch1);
                            ROOMS.get(i).setSWITCH1(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSWITCH1_B().devId));
                            ROOMS.get(i).Switch1 = 1 ;
                            if (Switch1.dps.get("1") != null) {
                                if (Boolean.parseBoolean(Switch1.dps.get("1").toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("1").setValue(3);
                                }
                                else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("1").setValue(0);
                                }
                            }
                            if (Switch1.dps.get("2") != null) {
                                if (Boolean.parseBoolean(Switch1.dps.get("2").toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("2").setValue(3);
                                }
                                else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("2").setValue(0);
                                }
                            }
                            if (Switch1.dps.get("3") != null) {
                                if (Boolean.parseBoolean(Switch1.dps.get("3").toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("3").setValue(3);
                                }
                                else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("3").setValue(0);
                                }
                            }
                            if (Switch1.dps.get("4") != null) {
                                if (Boolean.parseBoolean(Switch1.dps.get("3").toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("4").setValue(3);
                                }
                                else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("4").setValue(0);
                                }
                            }
                        }
                        DeviceBean Switch2 = searchRoomDevice(Devices,ROOMS.get(i),"Switch2") ;
                        if (Switch2 == null) {
                            ROOMS.get(i).Switch2 = 0 ;
                        }
                        else {
                            ROOMS.get(i).setSWITCH2_B(Switch2);
                            ROOMS.get(i).setSWITCH2(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSWITCH2_B().devId));
                            ROOMS.get(i).Switch2 = 1 ;
                            if (Switch2.dps.get("1") != null) {
                                if (Boolean.parseBoolean(Switch2.dps.get("1").toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch2.name).child("1").setValue(3);
                                }
                                else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch2.name).child("1").setValue(0);
                                }
                            }
                            if (Switch2.dps.get("2") != null) {
                                if (Boolean.parseBoolean(Switch2.dps.get("2").toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch2.name).child("2").setValue(3);
                                }
                                else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch2.name).child("2").setValue(0);
                                }
                            }
                            if (Switch2.dps.get("3") != null) {
                                if (Boolean.parseBoolean(Switch2.dps.get("3").toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch2.name).child("3").setValue(3);
                                }
                                else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch2.name).child("3").setValue(0);
                                }
                            }
                            if (Switch2.dps.get("4") != null) {
                                if (Boolean.parseBoolean(Switch2.dps.get("3").toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch2.name).child("4").setValue(3);
                                }
                                else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch2.name).child("4").setValue(0);
                                }
                            }
                        }
                        DeviceBean Switch3 = searchRoomDevice(Devices,ROOMS.get(i),"Switch3") ;
                        if (Switch3 == null) {
                            ROOMS.get(i).Switch3 = 0 ;
                        }
                        else {
                            ROOMS.get(i).setSWITCH3_B(Switch3);
                            ROOMS.get(i).setSWITCH3(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSWITCH3_B().devId));
                            ROOMS.get(i).Switch3 = 1 ;
                            if (Switch3.dps.get("1") != null) {
                                if (Boolean.parseBoolean(Switch3.dps.get("1").toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch3.name).child("1").setValue(3);
                                }
                                else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch3.name).child("1").setValue(0);
                                }
                            }
                            if (Switch3.dps.get("2") != null) {
                                if (Boolean.parseBoolean(Switch3.dps.get("2").toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch3.name).child("2").setValue(3);
                                }
                                else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch3.name).child("2").setValue(0);
                                }
                            }
                            if (Switch3.dps.get("3") != null) {
                                if (Boolean.parseBoolean(Switch3.dps.get("3").toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch3.name).child("3").setValue(3);
                                }
                                else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch3.name).child("3").setValue(0);
                                }
                            }
                            if (Switch3.dps.get("4") != null) {
                                if (Boolean.parseBoolean(Switch3.dps.get("3").toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch3.name).child("4").setValue(3);
                                }
                                else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch3.name).child("4").setValue(0);
                                }
                            }
                        }
                        DeviceBean Switch4 = searchRoomDevice(Devices,ROOMS.get(i),"Switch4") ;
                        if (Switch4 == null) {
                            ROOMS.get(i).Switch4 = 0 ;
                        }
                        else {
                            ROOMS.get(i).setSWITCH4_B(Switch4);
                            ROOMS.get(i).setSWITCH4(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getSWITCH4_B().devId));
                            ROOMS.get(i).Switch4 = 1 ;
                            if (Switch4.dps.get("1") != null) {
                                if (Boolean.parseBoolean(Switch4.dps.get("1").toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch4.name).child("1").setValue(3);
                                }
                                else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch4.name).child("1").setValue(0);
                                }
                            }
                            if (Switch4.dps.get("2") != null) {
                                if (Boolean.parseBoolean(Switch4.dps.get("2").toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch4.name).child("2").setValue(3);
                                }
                                else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch4.name).child("2").setValue(0);
                                }
                            }
                            if (Switch4.dps.get("3") != null) {
                                if (Boolean.parseBoolean(Switch4.dps.get("3").toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch4.name).child("3").setValue(3);
                                }
                                else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch4.name).child("3").setValue(0);
                                }
                            }
                            if (Switch4.dps.get("4") != null) {
                                if (Boolean.parseBoolean(Switch4.dps.get("3").toString())) {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch4.name).child("4").setValue(3);
                                }
                                else {
                                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(Switch4.name).child("4").setValue(0);
                                }
                            }
                        }
                        DeviceBean lock = searchRoomDevice(Devices,ROOMS.get(i),"Lock") ;
                        if (lock == null) {
                            ROOMS.get(i).lock = 0 ;
                        }
                        else {
                            ROOMS.get(i).setLOCK_B(lock);
                            ROOMS.get(i).setLOCK(TuyaHomeSdk.newDeviceInstance(ROOMS.get(i).getLOCK_B().devId));
                            ROOMS.get(i).lock = 1 ;
                            setRoomLockId(lock.devId, String.valueOf(ROOMS.get(i).id));
                            ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(lock.name).child("1").setValue(0);
                        }
                    }
                    for (int i=0;i<ROOMS.size();i++) {
                        if (ROOMS.get(i).PowerSwitch == 0) {
                            if (i == 0) {
                                PowerUnInstalled = ROOMS.get(i).id+"" ;
                            }
                            else {
                                PowerUnInstalled = PowerUnInstalled+"-"+ROOMS.get(i).id ;
                            }
                        }
                        else if (ROOMS.get(i).PowerSwitch == 1) {
                            if (i == 0) {
                                PowerInstalled = ROOMS.get(i).id+"" ;
                            }
                            else {
                                PowerInstalled = PowerInstalled+"-"+ROOMS.get(i).id ;
                            }
                        }
                        if (ROOMS.get(i).ZBGateway == 0) {
                            if (i == 0) {
                                GatewayUnInstalled = ROOMS.get(i).id+"" ;
                            }
                            else {
                                GatewayUnInstalled = GatewayUnInstalled+"-"+ROOMS.get(i).id ;
                            }
                        }
                        else if (ROOMS.get(i).ZBGateway == 1) {
                            if (i == 0) {
                                GatewayInstalled = ROOMS.get(i).id+"" ;
                            }
                            else {
                                GatewayInstalled = GatewayInstalled+"-"+ROOMS.get(i).id ;
                            }
                        }
                        if (ROOMS.get(i).MotionSensor == 0) {
                            if (i == 0) {
                                MotionUnInstalled = ROOMS.get(i).id+"" ;
                            }
                            else {
                                MotionUnInstalled = MotionUnInstalled+"-"+ROOMS.get(i).id ;
                            }
                        }
                        else if (ROOMS.get(i).MotionSensor == 1) {
                            if (i == 0) {
                                MotionInstalled = ROOMS.get(i).id+"" ;
                            }
                            else {
                                MotionInstalled = MotionInstalled+"-"+ROOMS.get(i).id ;
                            }
                        }
                        if (ROOMS.get(i).DoorSensor == 0) {
                            if (i == 0) {
                                DoorUnInstalled = ROOMS.get(i).id+"" ;
                            }
                            else {
                                DoorUnInstalled = DoorUnInstalled+"-"+ROOMS.get(i).id ;
                            }
                        }
                        else if (ROOMS.get(i).DoorSensor == 1) {
                            if (i == 0) {
                                DoorInstalled = ROOMS.get(i).id+"" ;
                            }
                            else {
                                DoorInstalled = DoorInstalled+"-"+ROOMS.get(i).id ;
                            }
                        }
                        if (ROOMS.get(i).ServiceSwitch == 0) {
                            if (i == 0) {
                                ServiceUnInstalled = ROOMS.get(i).id+"" ;
                            }
                            else {
                                ServiceUnInstalled = ServiceUnInstalled+"-"+ROOMS.get(i).id ;
                            }
                        }
                        else if (ROOMS.get(i).ServiceSwitch == 1) {
                            if (i == 0) {
                                ServiceInstalled = ROOMS.get(i).id+"" ;
                            }
                            else {
                                ServiceInstalled = ServiceInstalled+"-"+ROOMS.get(i).id ;
                            }
                        }
                        if (ROOMS.get(i).Switch1 == 0) {
                            if (i == 0) {
                                S1UnInstalled = ROOMS.get(i).id+"" ;
                            }
                            else {
                                S1UnInstalled = S1UnInstalled+"-"+ROOMS.get(i).id ;
                            }
                        }
                        else if (ROOMS.get(i).Switch1 == 1) {
                            if (i == 0) {
                                S1Installed = ROOMS.get(i).id+"" ;
                            }
                            else {
                                S1Installed = S1Installed+"-"+ROOMS.get(i).id ;
                            }
                        }
                        if (ROOMS.get(i).Switch2 == 0) {
                            if (i == 0) {
                                S2UnInstalled = ROOMS.get(i).id+"" ;
                            }
                            else {
                                S2UnInstalled = S2UnInstalled+"-"+ROOMS.get(i).id ;
                            }
                        }
                        else if (ROOMS.get(i).Switch2 == 1) {
                            if (i == 0) {
                                S2Installed = ROOMS.get(i).id+"" ;
                            }
                            else {
                                S2Installed = S2Installed+"-"+ROOMS.get(i).id ;
                            }
                        }
                        if (ROOMS.get(i).Switch3 == 0) {
                            if (i == 0) {
                                S3UnInstalled = ROOMS.get(i).id+"" ;
                            }
                            else {
                                S3UnInstalled = S3UnInstalled+"-"+ROOMS.get(i).id ;
                            }
                        }
                        else if (ROOMS.get(i).Switch3 == 1) {
                            if (i == 0) {
                                S3Installed = ROOMS.get(i).id+"" ;
                            }
                            else {
                                S3Installed = S3Installed+"-"+ROOMS.get(i).id ;
                            }
                        }
                        if (ROOMS.get(i).Switch4 == 0) {
                            if (i == 0) {
                                S4UnInstalled = ROOMS.get(i).id+"" ;
                            }
                            else {
                                S4UnInstalled = S4UnInstalled+"-"+ROOMS.get(i).id ;
                            }
                        }
                        else if (ROOMS.get(i).Switch4 == 1) {
                            if (i == 0) {
                                S4Installed = ROOMS.get(i).id+"" ;
                            }
                            else {
                                S4Installed = S4Installed+"-"+ROOMS.get(i).id ;
                            }
                        }
                        if (ROOMS.get(i).Thermostat == 0) {
                            if (i == 0) {
                                ACUnInstalled = ROOMS.get(i).id+"" ;
                            }
                            else {
                                ACUnInstalled = ACUnInstalled+"-"+ROOMS.get(i).id ;
                            }
                        }
                        else if (ROOMS.get(i).Thermostat == 1) {
                            if (i == 0) {
                                ACInstalled = ROOMS.get(i).id+"" ;
                            }
                            else {
                                ACInstalled = ACInstalled+"-"+ROOMS.get(i).id ;
                            }
                        }
                        if (ROOMS.get(i).CurtainSwitch == 0) {
                            if (i == 0) {
                                CurtainUnInstalled = ROOMS.get(i).id+"" ;
                            }
                            else {
                                CurtainUnInstalled = CurtainUnInstalled+"-"+ROOMS.get(i).id ;
                            }
                        }
                        else if (ROOMS.get(i).CurtainSwitch == 1) {
                            if (i == 0) {
                                CurtainInstalled = ROOMS.get(i).id+"" ;
                            }
                            else {
                                CurtainInstalled = CurtainInstalled+"-"+ROOMS.get(i).id ;
                            }
                        }
                        if (ROOMS.get(i).lock == 0) {
                            if (i == 0) {
                                LockUnInstalled = ROOMS.get(i).id+"" ;
                            }
                            else {
                                LockUnInstalled = LockUnInstalled+"-"+ROOMS.get(i).id ;
                            }
                        }
                        else if (ROOMS.get(i).lock == 1) {
                            if (i == 0) {
                                LockInstalled = ROOMS.get(i).id+"" ;
                            }
                            else {
                                LockInstalled = LockInstalled+"-"+ROOMS.get(i).id ;
                            }
                        }
                    }
                    adapter = new Rooms_Adapter_Base(ROOMS,act);
                    roomsListView.setAdapter(adapter);
                    setRoomsDevicesInstalledInDB();
                }
                Devices_Adapter adapter = new Devices_Adapter(Devices,act);
                devicesListView.setAdapter(adapter);
                setDevicesListeners();
                setFireRoomsListiner();
                getSceneBGs();
            }
            @Override
            public void onError(String errorCode, String errorMsg) {
                loading.stop();
                new MessageDialog("getting tuya devices failed "+errorMsg,"error",act);
            }
        });
    }

    void setLocks(ArrayList<LockObj> Locks) {
        for (int j=0;j<Locks.size();j++) {
            Log.d("locks" , Locks.get(j).getLockName());
            for (int i = 0; i< ROOMS.size(); i++) {
                if (Locks.get(j).getLockName().equals(ROOMS.get(i).RoomNumber+"Lock")) {
                    ROOMS.get(i).setLock(Locks.get(j));
                    break;
                }
            }
        }

    }

    public void resetAllDevices(View view) {
        Dialog  dd = new Dialog(act);
        dd.setContentView(R.layout.lock_unlock_dialog);
        Button cancel = (Button) dd.findViewById(R.id.confermationDialog_cancel);
        Button lock = (Button) dd.findViewById(R.id.messageDialog_ok);
        TextView title = (TextView) dd.findViewById(R.id.textView2);
        TextView message = (TextView) dd.findViewById(R.id.confermationDialog_Text);
        title.setText("Remove All Devices ?");
        message.setText("Are You Sure You want to remove all devices ??");
        EditText password = (EditText) dd.findViewById(R.id.editTextTextPassword);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dd.dismiss();
            }
        });
        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final lodingDialog loading = new lodingDialog(act);
                final String pass = password.getText().toString() ;
                StringRequest re = new StringRequest(Request.Method.POST, "", new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.d("LoginResult" , response +" "+ "" );
                        loading.stop();
                        if (response.equals("1"))
                        {
                            dd.dismiss();
                            lodingDialog loading = new lodingDialog(act);
                            //removeParameter=0;
                            for (int i=0;i<Devices.size();i++)
                            {
                                int finalI = i;
                                TuyaHomeSdk.newDeviceInstance(Devices.get(i).getDevId()).removeDevice(new IResultCallback()
                                {

                                    @Override
                                    public void onError(String code, String error)
                                    {
                                        Toast.makeText(act,error,Toast.LENGTH_LONG).show();
                                    }
                                    @Override
                                    public void onSuccess()
                                    {
                                        //Devices.remove(finalI);
                                    }
                                });

                            }

                            String url = "";//Login.SelectedHotel.URL+"removeAllDevices.php";
                            StringRequest re = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response)
                                {
                                    loading.stop();
                                    Toast.makeText(act,response,Toast.LENGTH_LONG).show();
                                    if (response.equals("1"))
                                    {
                                        Toast.makeText(act,"Devices Removed",Toast.LENGTH_LONG).show();
                                        getRooms();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error)
                                {
                                    loading.stop();
                                    Toast.makeText(act,error.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            })
                            {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError
                                {
                                    Map<String,String> Param = new HashMap<String, String>();
                                    Param.put("hotel","1");
                                    return Param;
                                }
                            };
                            Volley.newRequestQueue(act).add(re);

                        }
                        else if (response.equals("0"))
                        {
                            Toast.makeText(act,"Lock Failed",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(act,"No Params",Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        loading.stop();
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError
                    {
                        Map<String,String> par = new HashMap<String, String>();
                        par.put( "password" , pass ) ;
                        par.put( "hotel" , "1" ) ;
                        return par;
                    }
                };
                Volley.newRequestQueue(act).add(re);
            }
        });
        dd.show();
    }

    // set Listeners _______________________________________________

    public void setDevicesListeners() {
        for (int i = 0; i< ROOMS.size(); i++) {
            int finalI = i;
            if (ROOMS.get(i).getDOORSENSOR_B() != null ) {
                ROOMS.get(i).getDOORSENSOR().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        Log.d("doorAction" , dpStr.toString() +" "+ROOMS.get(finalI).getDOORSENSOR_B().dps.toString());
                        if (dpStr.get("doorcontact_state") != null ) {
                            if (dpStr.get("doorcontact_state").toString().equals("true") ) {
                                runClientBackActions(ROOMS.get(finalI));
                                ROOMS.get(finalI).getFireRoom().child("doorStatus").setValue(1);
                                AC_Start[finalI] = System.currentTimeMillis() ;
                                Door_Start[finalI] = System.currentTimeMillis() ;
                                AC_SENARIO_Status[finalI] = true ;
                                DOOR_STATUS[finalI] = true ;
                                AC_Period[finalI] = 0;
                                Door_Period[finalI]= 0;
                                if (MyApp.ProjectVariables.getAcSenarioActive()) {
                                    TempRunnableList[finalI].run();
                                }
                                DoorRunnable[finalI].run();
                            }
                            else {
                                ROOMS.get(finalI).getFireRoom().child("doorStatus").setValue(0);
                                if (DoorsHandlers[finalI] != null) {
                                    DoorsHandlers[finalI].removeCallbacks(DoorRunnable[finalI]);
                                }
                                DOOR_STATUS[finalI] = false ;
                            }
                        }
                        else {
                            if (ROOMS.get(finalI).getDOORSENSOR_B().dps.get("101") != null) {
                                if (Boolean.parseBoolean(ROOMS.get(finalI).getDOORSENSOR_B().dps.get("101").toString())) {
                                    runClientBackActions(ROOMS.get(finalI));
                                    ROOMS.get(finalI).getFireRoom().child("doorStatus").setValue(1);
                                    AC_Start[finalI] = System.currentTimeMillis() ;
                                    Door_Start[finalI] = System.currentTimeMillis() ;
                                    AC_SENARIO_Status[finalI] = true ;
                                    DOOR_STATUS[finalI] = true ;
                                    AC_Period[finalI] = 0;
                                    Door_Period[finalI]= 0;
                                    if (MyApp.ProjectVariables.getAcSenarioActive()) {
                                        TempRunnableList[finalI].run();
                                        Log.d("acSenario" ,"start");
                                    }
                                    DoorRunnable[finalI].run();
                                }
                                else {
                                    ROOMS.get(finalI).getFireRoom().child("doorStatus").setValue(0);
                                    if (DoorsHandlers[finalI] != null) {
                                        DoorsHandlers[finalI].removeCallbacks(DoorRunnable[finalI]);
                                    }
                                    DOOR_STATUS[finalI] = false ;
                                }
                            }
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        Log.d("DoorSensor" , "Removed" );
                        ROOMS.get(finalI).setDoorSensorStatus(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {
                        Log.d("DoorSensor" , "status changed " + online );

                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {
                        Log.d("DoorSensor" , "network status changed " + status );
                    }
                    @Override
                    public void onDevInfoUpdate(String devId) {
                        Log.d("DoorSensor" , "DevInfo"  );
                    }
                });
            }
            if (ROOMS.get(i).getSERVICE1_B() != null) {
                if (ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.cleanupButton)) != null) {
                    CLEANUP[i] = Boolean.getBoolean(ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.cleanupButton)).toString()) ;
                }
                if (ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.laundryButton)) != null) {
                    LAUNDRY[i] = Boolean.getBoolean( ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.laundryButton)).toString());
                }
                if (ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton)) != null) {
                    DND[i] = Boolean.getBoolean( ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton)).toString());
                }
                if (ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.checkoutButton)) != null) {
                    CHECKOUT[i] = Boolean.getBoolean(ROOMS.get(i).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.checkoutButton)).toString());
                }
                ROOMS.get(i).getSERVICE1().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        Long time = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                        if (ROOMS.get(finalI).roomStatus == 2) {
                            if (dpStr.get("switch_"+MyApp.ProjectVariables.cleanupButton) != null) {
                                if (Boolean.parseBoolean(dpStr.get("switch_"+MyApp.ProjectVariables.cleanupButton).toString()) && !CLEANUP[finalI]) {
                                    CLEANUP[finalI] = true ;
                                    addCleanupOrder(ROOMS.get(finalI));
                                    ROOMS.get(finalI).Cleanup = 1 ;
                                    ROOMS.get(finalI).dep = "Cleanup" ;
                                    ROOMS.get(finalI).getFireRoom().child("Cleanup").setValue(time);
                                }
                                else if (!Boolean.parseBoolean(dpStr.get("switch_"+MyApp.ProjectVariables.cleanupButton).toString()) && CLEANUP[finalI]) {
                                    CLEANUP[finalI] = false ;
                                    cancelServiceOrder(ROOMS.get(finalI),"Cleanup");
                                    ROOMS.get(finalI).Cleanup = 0 ;
                                    ROOMS.get(finalI).getFireRoom().child("Cleanup").setValue(0);
                                }
                            }
                            if (dpStr.get("switch_"+MyApp.ProjectVariables.laundryButton) != null) {
                                if (Boolean.parseBoolean(dpStr.get("switch_"+MyApp.ProjectVariables.laundryButton).toString()) && !LAUNDRY[finalI]) {
                                    LAUNDRY[finalI] = true ;
                                    addLaundryOrder(ROOMS.get(finalI));
                                    ROOMS.get(finalI).Laundry = 1 ;
                                    ROOMS.get(finalI).dep = "Laundry" ;
                                    ROOMS.get(finalI).getFireRoom().child("Laundry").setValue(time);
                                }
                                else if (!Boolean.parseBoolean(dpStr.get("switch_"+MyApp.ProjectVariables.laundryButton).toString()) && LAUNDRY[finalI]) {
                                    LAUNDRY[finalI] = false ;
                                    cancelServiceOrder(ROOMS.get(finalI),"Laundry");
                                    ROOMS.get(finalI).Laundry = 0 ;
                                    ROOMS.get(finalI).getFireRoom().child("Laundry").setValue(0);
                                }
                            }
                            if (dpStr.get("switch_"+MyApp.ProjectVariables.checkoutButton) != null) {
                                if (Boolean.parseBoolean(dpStr.get("switch_"+MyApp.ProjectVariables.checkoutButton).toString()) && !CHECKOUT[finalI]) {
                                    CHECKOUT[finalI] = true ;
                                    addCheckoutOrder(ROOMS.get(finalI));
                                    ROOMS.get(finalI).Checkout = 1 ;
                                    ROOMS.get(finalI).dep = "Checkout" ;
                                    ROOMS.get(finalI).getFireRoom().child("Checkout").setValue(time);
                                }
                                else if (!Boolean.parseBoolean(dpStr.get("switch_"+MyApp.ProjectVariables.checkoutButton).toString()) && CHECKOUT[finalI]) {
                                    CHECKOUT[finalI] = false ;
                                    cancelServiceOrder(ROOMS.get(finalI),"Checkout");
                                    ROOMS.get(finalI).Checkout = 0 ;
                                    ROOMS.get(finalI).getFireRoom().child("Checkout").setValue(0);
                                }
                            }
                            if (dpStr.get("switch_"+MyApp.ProjectVariables.dndButton) != null) {
                                if (Boolean.parseBoolean(dpStr.get("switch_"+MyApp.ProjectVariables.dndButton).toString()) && !DND[finalI]) {
                                    DND[finalI] = true ;
                                    addDNDOrder(ROOMS.get(finalI));
                                    ROOMS.get(finalI).DND = 1 ;
                                    ROOMS.get(finalI).dep = "DND" ;
                                    ROOMS.get(finalI).getFireRoom().child("DND").setValue(time);
                                }
                                else if (!Boolean.parseBoolean(dpStr.get("switch_"+MyApp.ProjectVariables.dndButton).toString()) && DND[finalI]) {
                                    DND[finalI] = false ;
                                    cancelDNDOrder(ROOMS.get(finalI));
                                    ROOMS.get(finalI).DND = 0 ;
                                    ROOMS.get(finalI).getFireRoom().child("DND").setValue(0);
                                }
                            }
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setServiceSwitchStatus(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {
                        if (online) {
                            setClientInOrOut(ROOMS.get(finalI),"1");
                        }
                        else {
                            setClientInOrOut(ROOMS.get(finalI),"0");
                        }
                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }

                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getAC_B() !=null) {
                ROOMS.get(i).getAC().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        Log.d("acAction" , dpStr.toString());
                        if (dpStr.get("temp_current") != null) {
                            double temp = (Integer.parseInt(dpStr.get("temp_current").toString())*0.1);
                            ROOMS.get(finalI).getFireRoom().child("temp").setValue(temp) ;
                        }
                        if (dpStr.get("temp_set") != null ) {
                            if (Double.parseDouble(dpStr.get("temp_set").toString()) !=  Double.parseDouble(TempSetPoint[finalI])) {
                                Client_Temp[finalI] = dpStr.get("temp_set").toString();
                            }
                            int newTemp = Integer.parseInt(dpStr.get("temp_set").toString());
                            //ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("2").setValue(newTemp);
                        }
                        if (dpStr.get("switch") != null) {
                            if (Boolean.parseBoolean(dpStr.get("switch").toString())) {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("1").setValue(3);
                            }
                            else {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("1").setValue(0);
                            }
                        }
                        if (dpStr.get("level") != null) {
                            String newFan = dpStr.get("level").toString();
                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child("5").setValue(newFan);
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setThermostatStatus(String.valueOf(ROOMS.get(finalI).id) , "0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }

                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getPOWER_B() != null) {
                ROOMS.get(i).getPOWER().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        Log.d("powerActions",ROOMS.get(finalI).getPOWER_B().dps.get("1").toString()+" "+ROOMS.get(finalI).getPOWER_B().dps.get("2").toString());
//                        if (dpStr.get("switch_1") != null) {
//                            if (dpStr.get("switch_1").toString().equals("false")) {
//                                ROOMS.get(finalI).getFireRoom().child("powerStatus").setValue(0);
//                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getPOWER_B().name).child("1").setValue(0);
//                            }
//                            else if (dpStr.get("switch_1").toString().equals("true")) {
//                                if (ROOMS.get(finalI).getPOWER_B().dps.get("2").toString().equals("false")) {
//                                    ROOMS.get(finalI).getFireRoom().child("powerStatus").setValue(1);
//                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getPOWER_B().name).child("1").setValue(1);
//                                }
//                                else if (ROOMS.get(finalI).getPOWER_B().dps.get("2").toString().equals("true")) {
//                                    ROOMS.get(finalI).getFireRoom().child("powerStatus").setValue(2);
//                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getPOWER_B().name).child("1").setValue(2);
//                                }
//                            }
//                        }
//                        if (dpStr.get("switch_2") != null) {
//                            if (dpStr.get("switch_2").toString().equals("true")) {
//                                if (ROOMS.get(finalI).getPOWER_B().dps.get("1").toString().equals("true")) {
//                                    ROOMS.get(finalI).getFireRoom().child("powerStatus").setValue(2);
//                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getPOWER_B().name).child("1").setValue(2);
//                                }
//                            }
//                            else if (dpStr.get("switch_2").toString().equals("false")) {
//                                if (ROOMS.get(finalI).getPOWER_B().dps.get("1").toString().equals("true")) {
//                                    ROOMS.get(finalI).getFireRoom().child("powerStatus").setValue(1);
//                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getPOWER_B().name).child("1").setValue(1);
//                                }
//                                else if (ROOMS.get(finalI).getPOWER_B().dps.get("1").toString().equals("false")) {
//                                    ROOMS.get(finalI).getFireRoom().child("powerStatus").setValue(0);
//                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getPOWER_B().name).child("1").setValue(0);
//                                }
//                            }
//                        }

                        if (Boolean.parseBoolean(ROOMS.get(finalI).getPOWER_B().dps.get("1").toString()) && Boolean.parseBoolean(ROOMS.get(finalI).getPOWER_B().dps.get("2").toString())) {
                            Log.d("powerActions","i am in");
                            ROOMS.get(finalI).getFireRoom().child("powerStatus").setValue(2);
                            //ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getPOWER_B().name).child("1").setValue(2);
                        }
                        else if (Boolean.parseBoolean(ROOMS.get(finalI).getPOWER_B().dps.get("1").toString()) && !Boolean.parseBoolean(ROOMS.get(finalI).getPOWER_B().dps.get("2").toString())) {
                            ROOMS.get(finalI).getFireRoom().child("powerStatus").setValue(1);
                            //ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getPOWER_B().name).child("1").setValue(1);
                        }
                        else if (!Boolean.parseBoolean(ROOMS.get(finalI).getPOWER_B().dps.get("1").toString()) && !Boolean.parseBoolean(ROOMS.get(finalI).getPOWER_B().dps.get("2").toString())) {
                            ROOMS.get(finalI).getFireRoom().child("powerStatus").setValue(0);
                            //ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getPOWER_B().name).child("1").setValue(0);
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setPowerSwitchStatus(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }

                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }

                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getCURTAIN_B() != null) {
                ROOMS.get(i).getCURTAIN().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {

                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setCurtainSwitchStatus(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }
                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getMOTIONSENSOR_B() != null ) {
                ROOMS.get(i).getMOTIONSENSOR().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        Log.d("motion" , dpStr.toString());
                        if (AC_SENARIO_Status[finalI]) {
                            AC_SENARIO_Status[finalI] = false ;
                            Log.d("acSenario" ,"stop");
                        }
                        else {
                            String t ="";
                            if (Client_Temp[finalI].equals("0")) {
                                t="240";
                            }
                            else {
                                t = Client_Temp[finalI] ;
                            }
                            String dp = "{\" 2\": "+t+"}";
                            if (ROOMS.get(finalI).getAC() != null ) {
                                ROOMS.get(finalI).getAC().publishDps(dp, new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }
                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setMotionSensorStatus(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }
                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getSWITCH1_B() != null) {
                ROOMS.get(i).getSWITCH1().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        if (dpStr.get("switch_1") != null) {
                            if (Boolean.parseBoolean(dpStr.get("switch_1").toString())) {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("1").setValue(3);
                            }
                            else {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("1").setValue(0);
                            }
                        }
                        if (dpStr.get("switch_2") != null) {
                            if (Boolean.parseBoolean(dpStr.get("switch_2").toString())) {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("2").setValue(3);
                            }
                            else {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("2").setValue(0);
                            }
                        }
                        if (dpStr.get("switch_3") != null) {
                            if (Boolean.parseBoolean(dpStr.get("switch_3").toString())) {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("3").setValue(3);
                            }
                            else {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("3").setValue(0);
                            }
                        }
                        if (dpStr.get("switch_4") != null) {
                            if (Boolean.parseBoolean(dpStr.get("switch_4").toString())) {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("4").setValue(3);
                            }
                            else {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("4").setValue(0);
                            }
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setSwitch1Status(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }
                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getSWITCH2_B() != null) {
                ROOMS.get(i).getSWITCH2().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        if (dpStr.get("switch_1") != null) {
                            if (Boolean.parseBoolean(dpStr.get("switch_1").toString())) {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("1").setValue(3);
                            }
                            else {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("1").setValue(0);
                            }
                        }
                        if (dpStr.get("switch_2") != null) {
                            if (Boolean.parseBoolean(dpStr.get("switch_2").toString())) {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("2").setValue(3);
                            }
                            else {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("2").setValue(0);
                            }
                        }
                        if (dpStr.get("switch_3") != null) {
                            if (Boolean.parseBoolean(dpStr.get("switch_3").toString())) {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("3").setValue(3);
                            }
                            else {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("3").setValue(0);
                            }
                        }
                        if (dpStr.get("switch_4") != null) {
                            if (Boolean.parseBoolean(dpStr.get("switch_4").toString())) {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("4").setValue(3);
                            }
                            else {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("4").setValue(0);
                            }
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setSwitch2Status(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }
                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getSWITCH3_B() != null) {
                ROOMS.get(i).getSWITCH3().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        if (dpStr.get("switch_1") != null) {
                            if (Boolean.parseBoolean(dpStr.get("switch_1").toString())) {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("1").setValue(3);
                            }
                            else {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("1").setValue(0);
                            }
                        }
                        if (dpStr.get("switch_2") != null) {
                            if (Boolean.parseBoolean(dpStr.get("switch_2").toString())) {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("2").setValue(3);
                            }
                            else {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("2").setValue(0);
                            }
                        }
                        if (dpStr.get("switch_3") != null) {
                            if (Boolean.parseBoolean(dpStr.get("switch_3").toString())) {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("3").setValue(3);
                            }
                            else {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("3").setValue(0);
                            }
                        }
                        if (dpStr.get("switch_4") != null) {
                            if (Boolean.parseBoolean(dpStr.get("switch_4").toString())) {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("4").setValue(3);
                            }
                            else {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("4").setValue(0);
                            }
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setSwitch3Status(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }
                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getSWITCH4_B() != null) {
                ROOMS.get(i).getSWITCH4().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                        if (dpStr.get("switch_1") != null) {
                            if (Boolean.parseBoolean(dpStr.get("switch_1").toString())) {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("1").setValue(3);
                            }
                            else {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("1").setValue(0);
                            }
                        }
                        if (dpStr.get("switch_2") != null) {
                            if (Boolean.parseBoolean(dpStr.get("switch_2").toString())) {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("2").setValue(3);
                            }
                            else {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("2").setValue(0);
                            }
                        }
                        if (dpStr.get("switch_3") != null) {
                            if (Boolean.parseBoolean(dpStr.get("switch_3").toString())) {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("3").setValue(3);
                            }
                            else {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("3").setValue(0);
                            }
                        }
                        if (dpStr.get("switch_4") != null) {
                            if (Boolean.parseBoolean(dpStr.get("switch_4").toString())) {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("4").setValue(3);
                            }
                            else {
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("4").setValue(0);
                            }
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setSwitch4Status(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }
                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
            if (ROOMS.get(i).getGATEWAY_B() != null) {
                ROOMS.get(i).getGATEWAY().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {

                    }
                    @Override
                    public void onRemoved(String devId) {
                        ROOMS.get(finalI).setZBGatewayStatus(String.valueOf(ROOMS.get(finalI).id),"0",act);
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online) {
                        Log.d("onlineChange" ,ROOMS.get(finalI).RoomNumber + " " +online );
                        if (online) {
                            setRoomOnlineOffline(ROOMS.get(finalI),"1");
                        }
                        else {
                            setRoomOnlineOffline(ROOMS.get(finalI),"0");
                        }
                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }
                    @Override
                    public void onDevInfoUpdate(String devId) {

                    }
                });
            }
        }
    }

    public void setFireRoomsListiner() {
        for (int i=0;i<ROOMS.size();i++) {
            int finalI = i;
            if (ROOMS.get(i).getSERVICE1_B() != null) {
                ROOMS.get(i).CleanupListener = ROOMS.get(i).getFireRoom().child("Cleanup").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            if (Long.parseLong(snapshot.getValue().toString()) == 0) {
                                if (CLEANUP[finalI]) {  //Boolean.parseBoolean(ROOMS.get(finalI).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.cleanupButton)).toString())
                                    TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalI).getSERVICE1_B().devId).publishDps("{\" "+MyApp.ProjectVariables.cleanupButton+"\" : false}", TYDevicePublishModeEnum.TYDevicePublishModeAuto, new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {
                                            CLEANUP[finalI] = false ;
                                        }
                                    });
                                }
                            }
                            else if (Long.parseLong(snapshot.getValue().toString()) > 0){
                                if (!CLEANUP[finalI]) {
                                    TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalI).getSERVICE1_B().devId).publishDps("{\" "+MyApp.ProjectVariables.cleanupButton+"\" : true}", TYDevicePublishModeEnum.TYDevicePublishModeAuto, new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {
                                            CLEANUP[finalI] = true ;
                                        }
                                    });
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                ROOMS.get(i).LaundryListener = ROOMS.get(i).getFireRoom().child("Laundry").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            if (Long.parseLong(snapshot.getValue().toString()) == 0) {
                                if (LAUNDRY[finalI]) { //Boolean.parseBoolean(ROOMS.get(finalI).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.laundryButton)).toString())
                                    TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalI).getSERVICE1_B().devId).publishDps("{\""+MyApp.ProjectVariables.laundryButton+"\" : false}", TYDevicePublishModeEnum.TYDevicePublishModeAuto, new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {
                                            LAUNDRY[finalI] = false ;
                                        }
                                    });
                                }
                            }
                            else if (Long.parseLong(snapshot.getValue().toString()) > 0) {
                                if (!LAUNDRY[finalI]) {
                                    TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalI).getSERVICE1_B().devId).publishDps("{\""+MyApp.ProjectVariables.laundryButton+"\" : true}", TYDevicePublishModeEnum.TYDevicePublishModeAuto, new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {
                                            LAUNDRY[finalI] = true ;
                                        }
                                    });
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                ROOMS.get(i).CheckoutListener = ROOMS.get(i).getFireRoom().child("Checkout").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            if (Long.parseLong(snapshot.getValue().toString()) == 0) {
                                if (ROOMS.get(finalI).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.checkoutButton)) != null) {
                                    if (CHECKOUT[finalI]) { //Boolean.parseBoolean(ROOMS.get(finalI).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.checkoutButton)).toString())
                                        TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalI).getSERVICE1_B().devId).publishDps("{\" "+MyApp.ProjectVariables.checkoutButton+"\":false}", TYDevicePublishModeEnum.TYDevicePublishModeAuto, new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {

                                            }

                                            @Override
                                            public void onSuccess() {
                                                CHECKOUT[finalI] = false ;
                                            }
                                        });
                                    }
                                }
                            }
                            else if (Long.parseLong(snapshot.getValue().toString()) > 0) {
                                if (ROOMS.get(finalI).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.checkoutButton)) != null) {
                                    if (!CHECKOUT[finalI]) {
                                        TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalI).getSERVICE1_B().devId).publishDps("{\" "+MyApp.ProjectVariables.checkoutButton+"\":true}", TYDevicePublishModeEnum.TYDevicePublishModeAuto, new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {

                                            }
                                            @Override
                                            public void onSuccess() {
                                                CHECKOUT[finalI] = true ;
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                ROOMS.get(i).DNDListener = ROOMS.get(i).getFireRoom().child("DND").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            if (Long.parseLong(snapshot.getValue().toString()) == 0) {
                                if (ROOMS.get(finalI).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton)) != null) {
                                    if (DND[finalI]) { //Boolean.parseBoolean(ROOMS.get(finalI).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton)).toString())
                                        TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalI).getSERVICE1_B().devId).publishDps("{\" "+MyApp.ProjectVariables.dndButton+"\":false}", TYDevicePublishModeEnum.TYDevicePublishModeAuto, new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {

                                            }
                                            @Override
                                            public void onSuccess() {
                                                DND[finalI] = false ;
                                            }
                                        });
                                    }
                                }
                            }
                            else if (Long.parseLong(snapshot.getValue().toString()) > 0) {
                                if (ROOMS.get(finalI).getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton)) != null) {
                                    if (!DND[finalI]) {
                                        TuyaHomeSdk.newDeviceInstance(ROOMS.get(finalI).getSERVICE1_B().devId).publishDps("{\" "+MyApp.ProjectVariables.dndButton+"\":true}", TYDevicePublishModeEnum.TYDevicePublishModeAuto, new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {

                                            }
                                            @Override
                                            public void onSuccess() {
                                                DND[finalI] = true ;
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            ROOMS.get(i).SetPointIntervalListener = ROOMS.get(i).getFireRoom().child("SetPointInterval").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                                //MyApp.ProjectVariables.Interval = 1000*60* Integer.parseInt(snapshot.getValue().toString());
                                //Log.d("intervalsetpoint" , MyApp.ProjectVariables.Interval+"" );
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            ROOMS.get(i).DoorWarningListener = ROOMS.get(i).getFireRoom().child("DoorWarning").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null ) {
                            try {
                                MyApp.ProjectVariables.DoorWarning = 1000 * 60 * Integer.parseInt(snapshot.getValue().toString());
                                Log.d("Doorinterval", MyApp.ProjectVariables.DoorWarning + "");
                            } catch (Exception e) {
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            ROOMS.get(i).roomStatusListener = ROOMS.get(i).getFireRoom().child("roomStatus").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null ) {
                            Log.d("roomChangedTo" ,snapshot.getValue().toString() );
                            if (snapshot.getValue().toString().equals("3") && ROOMS.get(finalI).roomStatus != 3) {
                                checkoutModeRoom(ROOMS.get(finalI));
                            }
                            else if (snapshot.getValue().toString().equals("2") && ROOMS.get(finalI).roomStatus != 2) {
                                checkInModeRoom(ROOMS.get(finalI));
                            }
                            ROOMS.get(finalI).roomStatus = Integer.parseInt(snapshot.getValue().toString());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            ROOMS.get(i).CheckInModeTimeListener = ROOMS.get(i).getFireRoom().child("CheckInModeTime").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.getValue() != null ) {
                            if (!snapshot.getValue().toString().equals("0")) {
                                MyApp.ProjectVariables.CheckinModeTime = Integer.parseInt( snapshot.getValue().toString());
                                Log.d("checkinModeDuration" , "check in chenged to "+checkInModeTime);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            ROOMS.get(i).CheckOutModeTimeListener = ROOMS.get(i).getFireRoom().child("CheckOutModeTime").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null ){
                            if (!snapshot.getValue().toString().equals("0")) {
                                MyApp.ProjectVariables.CheckoutModeTime = Integer.parseInt( snapshot.getValue().toString());
                                Log.d("checkoutModeDuration" , "changed to "+checkOutModeTime+"");
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            ROOMS.get(i).ClientInListener = ROOMS.get(i).getFireRoom().child("ClientIn").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null ){
                        ROOMS.get(finalI).ClientIn = Integer.parseInt( snapshot.getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if (ROOMS.get(i).getPOWER_B() != null && ROOMS.get(i).getPOWER() != null) {
                if (ROOMS.get(i).getPOWER_B().dps.get("1") != null) {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getPOWER_B().name).child("1").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (Integer.parseInt(snapshot.getValue().toString()) == 0) {
                                    if (ROOMS.get(finalI).getPOWER_B() != null) {
                                        ROOMS.get(finalI).getPOWER().publishDps("{\" 1\":false}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {
                                                }
                                                @Override
                                                public void onSuccess() {
                                                }
                                            });
                                        ROOMS.get(finalI).getPOWER().publishDps("{\" 2\":false}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {
                                                }
                                                @Override
                                                public void onSuccess() {
                                                }
                                            });
                                    }
                                }
                                else if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    if (ROOMS.get(finalI).getPOWER_B() != null) {
                                        ROOMS.get(finalI).getPOWER().publishDps("{\" 1\":true}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {
                                                }
                                                @Override
                                                public void onSuccess() {
                                                }
                                            });
                                        ROOMS.get(finalI).getPOWER().publishDps("{\" 2\":false}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {
                                                }
                                                @Override
                                                public void onSuccess() {
                                                }
                                            });
                                    }
                                }
                                else if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    if (ROOMS.get(finalI).getPOWER_B() != null) {
                                        ROOMS.get(finalI).getPOWER().publishDps("{\" 1\":true}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {
                                                }
                                                @Override
                                                public void onSuccess() {
                                                }
                                            });
                                        ROOMS.get(finalI).getPOWER().publishDps("{\" 2\":true}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {
                                                }
                                                @Override
                                                public void onSuccess() {
                                                }
                                            });
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            if (ROOMS.get(i).getSWITCH1_B() != null && ROOMS.get(i).getSWITCH1() != null) {
                if (ROOMS.get(i).getSWITCH1_B().dps.get("1") != null) {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("1").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                Log.d("S1FBvalue",snapshot.getValue().toString());
                                if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    ROOMS.get(finalI).getSWITCH1().publishDps("{\" 1\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("1").setValue(0);
                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("1").setValue(3);
                                        }
                                    });
                                }
                                if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    ROOMS.get(finalI).getSWITCH1().publishDps("{\" 1\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("1").setValue(3);
                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("1").setValue(0);
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if (ROOMS.get(i).getSWITCH1_B().dps.get("2") != null) {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("2").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                Log.d("S1FBvalue",snapshot.getValue().toString());
                                if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    ROOMS.get(finalI).getSWITCH1().publishDps("{\" 2\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("2").setValue(0);
                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("2").setValue(3);
                                        }
                                    });
                                }
                                if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    ROOMS.get(finalI).getSWITCH1().publishDps("{\" 2\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("2").setValue(3);
                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("2").setValue(0);
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if (ROOMS.get(i).getSWITCH1_B().dps.get("3") != null) {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("3").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                Log.d("S1FBvalue",snapshot.getValue().toString());
                                if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    ROOMS.get(finalI).getSWITCH1().publishDps("{\" 3\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("3").setValue(0);
                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("3").setValue(3);
                                        }
                                    });
                                }
                                if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    ROOMS.get(finalI).getSWITCH1().publishDps("{\" 3\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("3").setValue(3);
                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("3").setValue(0);
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if (ROOMS.get(i).getSWITCH1_B().dps.get("4") != null) {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH1_B().name).child("4").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                Log.d("S1FBvalue",snapshot.getValue().toString());
                                if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    ROOMS.get(finalI).getSWITCH1().publishDps("{\" 4\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("4").setValue(0);
                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("4").setValue(3);
                                        }
                                    });
                                }
                                if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    ROOMS.get(finalI).getSWITCH1().publishDps("{\" 4\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("4").setValue(3);
                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH1_B().name).child("4").setValue(0);
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            if (ROOMS.get(i).getSWITCH2_B() != null && ROOMS.get(i).getSWITCH2() != null) {
                if (ROOMS.get(i).getSWITCH2_B().dps.get("1") != null) {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH2_B().name).child("1").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    ROOMS.get(finalI).getSWITCH2().publishDps("{\" 1\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("1").setValue(3);
                                        }
                                    });
                                }
                                if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    ROOMS.get(finalI).getSWITCH2().publishDps("{\" 1\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("1").setValue(0);
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if (ROOMS.get(i).getSWITCH2_B().dps.get("2") != null) {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH2_B().name).child("2").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    ROOMS.get(finalI).getSWITCH2().publishDps("{\" 2\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("2").setValue(3);
                                        }
                                    });
                                }
                                if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    ROOMS.get(finalI).getSWITCH2().publishDps("{\" 2\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("2").setValue(0);
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if (ROOMS.get(i).getSWITCH2_B().dps.get("3") != null) {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH2_B().name).child("3").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    ROOMS.get(finalI).getSWITCH2().publishDps("{\" 3\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("3").setValue(3);
                                        }
                                    });
                                }
                                if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    ROOMS.get(finalI).getSWITCH2().publishDps("{\" 3\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("3").setValue(0);
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if (ROOMS.get(i).getSWITCH2_B().dps.get("4") != null) {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH2_B().name).child("4").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    ROOMS.get(finalI).getSWITCH2().publishDps("{\" 4\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("4").setValue(3);
                                        }
                                    });
                                }
                                if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    ROOMS.get(finalI).getSWITCH2().publishDps("{\" 4\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH2_B().name).child("4").setValue(0);
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            if (ROOMS.get(i).getSWITCH3_B() != null && ROOMS.get(i).getSWITCH3() != null) {
                if (ROOMS.get(i).getSWITCH3_B().dps.get("1") != null) {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH3_B().name).child("1").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 1\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("1").setValue(3);
                                        }
                                    });
                                }
                                if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 1\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("1").setValue(0);
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if (ROOMS.get(i).getSWITCH3_B().dps.get("2") != null) {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH3_B().name).child("2").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 2\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("2").setValue(3);
                                        }
                                    });
                                }
                                if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 2\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("2").setValue(0);
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if (ROOMS.get(i).getSWITCH3_B().dps.get("3") != null) {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH3_B().name).child("3").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 3\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("3").setValue(3);
                                        }
                                    });
                                }
                                if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 3\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("3").setValue(0);
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if (ROOMS.get(i).getSWITCH3_B().dps.get("4") != null) {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH3_B().name).child("4").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 4\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("4").setValue(3);
                                        }
                                    });
                                }
                                if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 4\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("4").setValue(0);
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            if (ROOMS.get(i).getSWITCH4_B() != null && ROOMS.get(i).getSWITCH4() != null) {
                if (ROOMS.get(i).getSWITCH4_B().dps.get("1") != null) {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH4_B().name).child("1").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 1\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("1").setValue(3);
                                        }
                                    });
                                }
                                if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 1\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("1").setValue(0);
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if (ROOMS.get(i).getSWITCH4_B().dps.get("2") != null) {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH4_B().name).child("2").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 2\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("2").setValue(3);
                                        }
                                    });
                                }
                                if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 2\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("2").setValue(0);
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if (ROOMS.get(i).getSWITCH4_B().dps.get("3") != null) {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH4_B().name).child("3").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 3\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("3").setValue(3);
                                        }
                                    });
                                }
                                if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    ROOMS.get(finalI).getSWITCH4().publishDps("{\" 3\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH4_B().name).child("3").setValue(0);
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if (ROOMS.get(i).getSWITCH4_B().dps.get("4") != null) {
                    ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getSWITCH2_B().name).child("4").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 4\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("4").setValue(3);
                                        }
                                    });
                                }
                                if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    ROOMS.get(finalI).getSWITCH3().publishDps("{\" 4\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getSWITCH3_B().name).child("4").setValue(0);
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            if (ROOMS.get(i).getAC_B() != null && ROOMS.get(i).getAC() != null) {
                TuyaHomeSdk.getSceneManagerInstance().getDeviceConditionOperationList(ROOMS.get(i).getAC_B().devId, new ITuyaResultCallback<List<TaskListBean>>() {
                    @Override
                    public void onSuccess(List<TaskListBean> result) {
                        Log.d("setDevicesList",ROOMS.get(finalI).RoomNumber+" "+result.size());
                        long SetId = 0 ;
                        long PowerId = 0 ;
                        long CurrentId = 0 ;
                        long FanId = 0; ;
                        for (int i=0 ; i<result.size();i++) {
                            Log.d("setDevicesList",result.get(i).getName());
                            if (result.get(i).getName().contains("Set temp") || result.get(i).getName().contains("temp_set") || result.get(i).getName().contains("Set Temperature")) {
                                SetId = result.get(i).getDpId() ;
                            }
                            if (result.get(i).getName().contains("Power") || result.get(i).getName().contains("switch") || result.get(i).getName().contains("Switch")) {
                                PowerId = result.get(i).getDpId() ;
                            }
                            if (result.get(i).getName().contains("Current temp") || result.get(i).getName().contains("temp_current") || result.get(i).getName().contains("Current Temperature")) {
                                CurrentId = result.get(i).getDpId() ;
                            }
                            if (result.get(i).getName().contains("Fan") || result.get(i).getName().contains("level") || result.get(i).getName().contains("Gear")) {
                                FanId = result.get(i).getDpId() ;
                            }
                        }
                        Log.d("setDevicesList", "set "+SetId+" power "+PowerId+" fan "+FanId );
                        if (PowerId != 0) {
                            if (ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(PowerId)) != null) {
                                long finalPowerId = PowerId;
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(PowerId)).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.getValue() != null) {
                                            if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                ROOMS.get(finalI).getAC().publishDps("{\" "+ finalPowerId +"\":true}", new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {
                                                        ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(finalPowerId)).setValue(0);
                                                    }
                                                    @Override
                                                    public void onSuccess() {
                                                        ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(finalPowerId)).setValue(3);
                                                    }
                                                });
                                            }
                                            if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                ROOMS.get(finalI).getAC().publishDps("{\" "+finalPowerId+"\":false}", new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {
                                                        ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(finalPowerId)).setValue(3);
                                                    }
                                                    @Override
                                                    public void onSuccess() {
                                                        ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(finalPowerId)).setValue(0);
                                                    }
                                                });
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                        if (SetId != 0) {
                            if (ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(SetId)) != null) {
                                long finalSetId = SetId;
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(SetId)).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.getValue() != null) {
                                            Log.d("tempModify" , snapshot.getValue().toString());
                                            int newTemp = Integer.parseInt(snapshot.getValue().toString());
                                            ROOMS.get(finalI).getAC().publishDps("{\" "+ finalSetId +"\":"+newTemp+"}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {
                                                    ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(finalSetId)).setValue(Integer.parseInt(ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(finalSetId)).toString()));
                                                }
                                                @Override
                                                public void onSuccess() {
                                                }
                                            });
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                        if (FanId != 0) {
                            if (ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(FanId)) != null) {
                                long finalFanId = FanId;
                                ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(FanId)).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.getValue() != null) {
                                            Log.d("fanModify" , snapshot.getValue().toString());
                                            String value = snapshot.getValue().toString();
                                            if (value.equals("high") || value.equals("med") || value.equals("low") || value.equals("auto")) {
                                                ROOMS.get(finalI).getAC().publishDps("{\" "+ finalFanId +"\":\""+value+"\"}", new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {
                                                        ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getAC_B().name).child(String.valueOf(finalFanId)).setValue(ROOMS.get(finalI).getAC_B().dps.get(String.valueOf(finalFanId)));
                                                    }
                                                    @Override
                                                    public void onSuccess() {
                                                    }
                                                });
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onError(String errorCode, String errorMessage) {

                    }
                });
            }
            if (ROOMS.get(i).getLOCK_B() != null && ROOMS.get(i).getLOCK() != null) {
                ProjectDevices.child(String.valueOf(ROOMS.get(i).RoomNumber)).child(ROOMS.get(i).getLOCK_B().name).child("1").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            if (Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                OpenTheDoor(ROOMS.get(finalI), new RequestOrder() {
                                    @Override
                                    public void onSuccess(String token) {
                                        ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getLOCK_B().name).child("1").setValue(0);
                                    }

                                    @Override
                                    public void onFailed(String error) {
                                        ProjectDevices.child(String.valueOf(ROOMS.get(finalI).RoomNumber)).child(ROOMS.get(finalI).getLOCK_B().name).child("1").setValue(0);
                                    }
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    public void setProjectVariablesListener() {
        ProjectVariablesRef.child("CheckinModeActive").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    MyApp.ProjectVariables.CheckinModeActive = Integer.parseInt(snapshot.getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("CheckoutModeActive").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    MyApp.ProjectVariables.CheckoutModeActive = Integer.parseInt(snapshot.getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("ACSenarioActive").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    MyApp.ProjectVariables.setAcSenarioActive(Integer.parseInt(snapshot.getValue().toString()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("CheckInModeTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    MyApp.ProjectVariables.CheckinModeTime = Integer.parseInt(snapshot.getValue().toString());
                    Log.d("checkinTime" ,MyApp.ProjectVariables.CheckinModeTime+" " );
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("CheckOutModeTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    MyApp.ProjectVariables.CheckoutModeTime = Integer.parseInt(snapshot.getValue().toString());
                    Log.d("checkoutTime" ,MyApp.ProjectVariables.CheckoutModeTime+" " );
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("CheckinActions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    MyApp.ProjectVariables.CheckinActions = snapshot.getValue().toString();
                    MyApp.checkInActions = new CheckInActions(MyApp.ProjectVariables.CheckinActions);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("CheckoutActions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    MyApp.ProjectVariables.CheckoutActions = snapshot.getValue().toString();
                    MyApp.checkOutActions = new CheckoutActions(MyApp.ProjectVariables.CheckoutActions);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("DoorWarning").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    MyApp.ProjectVariables.DoorWarning = Integer.parseInt(snapshot.getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("HKCleanupTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    MyApp.ProjectVariables.HKCleanTime = Integer.parseInt(snapshot.getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("Interval").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    Log.d("intervalChanged" , snapshot.getValue().toString());
                    MyApp.ProjectVariables.Interval = 1000*60* Integer.parseInt(snapshot.getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("PoweroffAfterHK").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    MyApp.ProjectVariables.PoweroffAfterHK = Integer.parseInt(snapshot.getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("OnClientBack").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    MyApp.ProjectVariables.OnClientBack = snapshot.getValue().toString();
                    MyApp.clientBackActions = new ClientBackActions(MyApp.ProjectVariables.OnClientBack);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("Temp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    MyApp.ProjectVariables.Temp = Integer.parseInt(snapshot.getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setControlDeviceListener() {
        ServerDevice.child("roomsIds").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Log.d("roomsChange", "changed "+snapshot.getValue().toString());
                for (int i=0;i<ROOMS.size();i++) {
                    if (ROOMS.get(i).CleanupListener != null) {
                        ROOMS.get(i).getFireRoom().child("Cleanup").removeEventListener(ROOMS.get(i).CleanupListener);
                    }
                    if (ROOMS.get(i).LaundryListener != null) {
                        ROOMS.get(i).getFireRoom().child("Laundry").removeEventListener(ROOMS.get(i).LaundryListener);
                    }
                    if (ROOMS.get(i).CheckoutListener != null) {
                        ROOMS.get(i).getFireRoom().child("Checkout").removeEventListener(ROOMS.get(i).CheckoutListener);
                    }
                    if (ROOMS.get(i).DNDListener != null) {
                        ROOMS.get(i).getFireRoom().child("DND").removeEventListener(ROOMS.get(i).DNDListener);
                    }
                    if (ROOMS.get(i).SetPointIntervalListener != null) {
                        ROOMS.get(i).getFireRoom().child("SetPointInterval").removeEventListener(ROOMS.get(i).SetPointIntervalListener);
                    }
                    if (ROOMS.get(i).DoorWarningListener != null) {
                        ROOMS.get(i).getFireRoom().child("DoorWarning").removeEventListener(ROOMS.get(i).DoorWarningListener);
                    }
                    if (ROOMS.get(i).roomStatusListener != null) {
                        ROOMS.get(i).getFireRoom().child("roomStatus").removeEventListener(ROOMS.get(i).roomStatusListener);
                    }
                    if (ROOMS.get(i).CheckInModeTimeListener != null) {
                        ROOMS.get(i).getFireRoom().child("CheckInModeTime").removeEventListener(ROOMS.get(i).CheckInModeTimeListener);
                    }
                    if (ROOMS.get(i).CheckOutModeTimeListener != null) {
                        ROOMS.get(i).getFireRoom().child("CheckOutModeTime").removeEventListener(ROOMS.get(i).CheckOutModeTimeListener);
                    }
                    if (ROOMS.get(i).ClientInListener != null) {
                        ROOMS.get(i).getFireRoom().child("ClientIn").removeEventListener(ROOMS.get(i).ClientInListener);
                    }
                    if (ROOMS.get(i).getPOWER() != null) {
                        ROOMS.get(i).getPOWER().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getDOORSENSOR() != null) {
                        ROOMS.get(i).getDOORSENSOR().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getSERVICE1() != null) {
                        ROOMS.get(i).getSERVICE1().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getAC() != null) {
                        ROOMS.get(i).getAC().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getMOTIONSENSOR() != null) {
                        ROOMS.get(i).getMOTIONSENSOR().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getGATEWAY() != null) {
                        ROOMS.get(i).getGATEWAY().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getSWITCH1() != null) {
                        ROOMS.get(i).getSWITCH1().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getSWITCH2() != null) {
                        ROOMS.get(i).getSWITCH2().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getSWITCH3() != null) {
                        ROOMS.get(i).getSWITCH3().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getSWITCH4() != null) {
                        ROOMS.get(i).getSWITCH4().unRegisterDevListener();
                    }
                    if (ROOMS.get(i).getCURTAIN() != null) {
                        ROOMS.get(i).getCURTAIN().unRegisterDevListener();
                    }
                }
                getRooms();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Add & Cancel Orders ____________________________________

    public void addCleanupOrder(ROOM room) {
            String url = MyApp.THE_PROJECT.url + "reservations/addCleanupOrderControlDevice"+addCleanupCounter ;
            StringRequest addOrder = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("addCleanupRsp" , response);
                    if (response != null) {
                        try {
                            JSONObject result = new JSONObject(response);
                            if (!result.getString("result").equals("success")) {
                                Toast.makeText(act,result.getString("error"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(act,e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(act,"error cleanup "+room.RoomNumber,Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("addCleanupRsp" , error.toString());
                    Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("room_id" ,String.valueOf(room.id));
                    return params;
                }
            };
            CLEANUP_QUEUE.add(addOrder);
            addCleanupCounter++;
            if (addCleanupCounter == 5) {
                addCleanupCounter = 1 ;
            }
    }

    public void addLaundryOrder(ROOM room) {
        String url = MyApp.THE_PROJECT.url + "reservations/addLaundryOrderControlDevice"+addLaundryCounter;
        StringRequest addOrder = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("addLaundryRsp" , response);
                if (response != null) {
                    try {
                        JSONObject result = new JSONObject(response);
                        if (!result.getString("result").equals("success")) {
                            Toast.makeText(act,result.getString("error"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("addLaundryRsp" , e.toString());
                        Toast.makeText(act,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(act,"error laundry "+room.RoomNumber,Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("addLaundryRsp" , error.toString());
                Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("room_id" ,String.valueOf(room.id));
                return params;
            }

        };
        LAUNDRY_QUEUE.add(addOrder);
        addLaundryCounter++;
        if (addLaundryCounter == 5) {
            addLaundryCounter = 1 ;
        }
    }

    public void addCheckoutOrder (ROOM room) {
        String url = MyApp.THE_PROJECT.url + "reservations/addCheckoutOrderControlDevice"+addCheckoutCounter;
        StringRequest addOrder = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("addCheckoutRsp" , response);
                if (response != null) {
                    try {
                        JSONObject result = new JSONObject(response);
                        if (!result.getString("result").equals("success")) {
                            Toast.makeText(act,result.getString("error"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(act,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(act,"error checkout "+room.RoomNumber,Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("addCheckoutRsp" , error.toString());
                Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("room_id" ,String.valueOf(room.id));
                return params;
            }

        };
        CHECKOUT_QUEUE.add(addOrder);
        addCheckoutCounter++;
        if (addCheckoutCounter == 5) {
            addCheckoutCounter = 1 ;
        }
    }

    public void addDNDOrder(ROOM room) {
        String url = MyApp.THE_PROJECT.url + "reservations/putRoomOnDNDModeControlDevice"+addDNDCounter;
        StringRequest request = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("addDNDRsp" , response);
                if (response != null) {
                    try {
                        JSONObject result = new JSONObject(response);
                        if (!result.getString("result").equals("success")) {
                            Toast.makeText(act,result.getString("error"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(act,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(act,"error laundry "+room.RoomNumber,Toast.LENGTH_SHORT).show();
                }
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("addDNDRsp" , error.toString());
                Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("room_id", String.valueOf(room.id));
                return params;
            }
        };
        DND_Queue.add(request);
        addDNDCounter++;
        if (addDNDCounter == 5) {
            addDNDCounter = 1 ;
        }
    }

    public void cancelServiceOrder(ROOM room , String type) {
            String url = MyApp.THE_PROJECT.url + "reservations/cancelServiceOrderControlDevice"+cancelOrderCounter;
            StringRequest removOrder = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("cancelPressed", response);
                    if (response != null) {
                        try {
                            JSONObject result = new JSONObject(response);
                            if (!result.getString("result").equals("success")) {
                                Toast.makeText(act,result.getString("error"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(act,e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(act,"error " + type +" " +room.RoomNumber,Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("cancelPressed", error.toString());
                    Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("room_id" ,String.valueOf( room.id));
                    params.put("order_type",type);
                    return params;
                }
            };
            CLEANUP_QUEUE.add(removOrder);
        cancelOrderCounter++;
        if (cancelOrderCounter == 5) {
            cancelOrderCounter = 1 ;
        }
    }

    public void cancelDNDOrder(ROOM room) {
        String url = MyApp.THE_PROJECT.url + "reservations/cancelDNDOrderControlDevice"+cancelDNDCounter;
        StringRequest rrr = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("cancelPressed", response);
                if (response != null) {
                    try {
                        JSONObject result = new JSONObject(response);
                        if (!result.getString("result").equals("success")) {
                            Toast.makeText(act,result.getString("error"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(act,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(act,"error dnd " +room.RoomNumber,Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("cancelPressed", error.toString());
                Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("room_id", String.valueOf(room.id));
                return params;
            }
        };
        DND_Queue.add(rrr);
        cancelDNDCounter++;
        if (cancelDNDCounter == 5) {
            cancelDNDCounter = 1 ;
        }
    }

    //__________________________________________________________

    public  void OpenTheDoor(LockObj lock) {
        if(lock == null)
        {
            Toast.makeText(act," you should get your key list first " , Toast.LENGTH_LONG).show();
            return;
        }
        final Dialog d = new Dialog(act);
        d.setContentView(R.layout.loading_layout);
        TextView t = (TextView) d.findViewById(R.id.textViewdfsdf);
        t.setText("Door Opening");
        d.setCancelable(false);
        d.show();
        //ensureBluetoothIsEnabled();
        //showConnectLockToast();
        TTLockClient.getDefault().controlLock(ControlAction.UNLOCK, lock.getLockData(), lock.getLockMac(),new ControlLockCallback()
        {
            @Override
            public void onControlLockSuccess(ControlLockResult controlLockResult)
            {
                //Toast.makeText(act,"lock is unlock  success!",Toast.LENGTH_LONG).show();
                d.dismiss();
                //ToastMaker.MakeToast("Door Opened",act);
            }
            @Override
            public void onFail(LockError error) {
                // Toast.makeText(UnlockActivity.this,"unLock fail!--" + error.getDescription(),Toast.LENGTH_LONG).show();
                d.dismiss();
                //ToastMaker.MakeToast("Open Fail!  "+error,act);
            }
        });

    }

    void setDoorSensorStatus(String ids, String status) {
            String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsDoorSensorInstalled";
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
                    Params.put("room_ids",ids);
                    Params.put("room_status" , status);
                    return Params;
                }
            };
            REQ.add(tabR);
    }

    void setServiceSwitchStatus(String ids, String status) {
            String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsServiceSwitchInstalled";
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
                    Params.put("room_ids", ids);
                    Params.put("room_status" , status);
                    return Params;
                }
            };
            REQ.add(tabR);
    }

    void setThermostatStatus(String ids, String status) {
            String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsThermostatInstalled";
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
                    Params.put("room_ids", ids);
                    Params.put("room_status" , status);
                    return Params;
                }
            };
            REQ.add(tabR);
    }

    void setPowerSwitchStatus(String ids, String status) {
            String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsPowerSwitchInstalled";
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
                    Params.put("room_ids", ids);
                    Params.put("room_status" , status);
                    return Params;
                }
            };
            REQ.add(tabR);
    }

    void setCurtainSwitchStatus(String ids, String status) {
            String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsCurtainInstalled";
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
                    Params.put("room_ids", ids);
                    Params.put("room_status" , status);
                    return Params;
                }
            };
            REQ.add(tabR);
    }

    void setMotionSensorStatus(String ids, String status) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsMotionSensorInstalled";
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
                Params.put("room_ids", ids);
                Params.put("room_status" , status);
                return Params;
            }
        };
        REQ.add(tabR);
    }

    void setSwitch1Status(String ids, String status) {
            String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsSwitch1Installed";
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
                    Params.put("room_ids", ids);
                    Params.put("room_status" , status);
                    return Params;
                }
            };
            REQ1.add(tabR);
    }

    void setSwitch2Status(String ids, String status) {
            String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsSwitch2Installed";
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
                    Params.put("room_ids", ids);
                    Params.put("room_status" , status);
                    return Params;
                }
            };
            REQ1.add(tabR);
    }

    void setSwitch3Status(String ids, String status) {
            String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsSwitch3Installed";
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
                    Params.put("room_ids", ids);
                    Params.put("room_status" , status);
                    return Params;
                }
            };
            REQ1.add(tabR);
    }

    void setSwitch4Status(String ids, String status) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsSwitch4Installed";
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
                    Params.put("room_ids", ids);
                    Params.put("room_status" , status);
                    return Params;
                }
            };
            REQ1.add(tabR);
    }

    void setZBGatewayStatus(String ids , String status) {
            String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsGatewayInstalled";
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
                    Params.put("room_ids", ids);
                    Params.put("room_status" , status);
                    return Params;
                }
            };
            REQ.add(tabR);
    }

    void setLockStatus(String ids , String status) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyRoomsLockInstalled";
        StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("lock" , response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("result").equals("success")) {
                        Log.e("lock" , "lock updated successfully");
                    }
                    else {
                        Log.e("lock" , "lock update failed "+res.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("lock" , "lock update failed "+e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("lock" , "lock update failed "+error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> Params = new HashMap<String,String>();
                Params.put("room_ids", ids);
                Params.put("room_status" , status);
                return Params;
            }
        };
        REQ.add(tabR);
    }

    void setRoomsDevicesInstalledInDB() {
        if (PowerUnInstalled != null) {
            setPowerSwitchStatus(PowerUnInstalled,"0");
        }
        if (GatewayUnInstalled != null) {
            setZBGatewayStatus(GatewayUnInstalled,"0");
        }
        if (ACUnInstalled != null) {
            setThermostatStatus(ACUnInstalled,"0");
        }
        if (MotionUnInstalled != null) {
            setMotionSensorStatus(MotionUnInstalled,"0");
        }
        if (DoorUnInstalled != null) {
            setDoorSensorStatus(DoorUnInstalled,"0");
        }
        if (ServiceUnInstalled != null) {
            setServiceSwitchStatus(ServiceUnInstalled,"0");
        }
        if (S1UnInstalled != null) {
            setSwitch1Status(S1UnInstalled,"0");
        }
        if (S2UnInstalled != null) {
            setSwitch2Status(S2UnInstalled,"0");
        }
        if (S3UnInstalled != null) {
            setSwitch3Status(S3UnInstalled,"0");
        }
        if (S4UnInstalled != null) {
            setSwitch4Status(S4UnInstalled,"0");
        }
        if (CurtainUnInstalled != null) {
            setCurtainSwitchStatus(CurtainUnInstalled,"0");
        }
        if (LockUnInstalled != null) {
            setLockStatus(LockUnInstalled,"0");
        }
        if (PowerInstalled != null) {
            setPowerSwitchStatus(PowerInstalled,"1");
        }
        if (GatewayInstalled != null) {
            setZBGatewayStatus(GatewayInstalled,"1");
        }
        if (ACInstalled != null) {
            setThermostatStatus(ACInstalled,"1");
        }
        if (MotionInstalled != null) {
            setMotionSensorStatus(MotionInstalled,"1");
        }
        if (DoorInstalled != null) {
            setDoorSensorStatus(DoorInstalled,"1");
        }
        if (ServiceInstalled != null) {
            setServiceSwitchStatus(ServiceInstalled,"1");
        }
        if (S1Installed != null) {
            setSwitch1Status(S1Installed,"1");
        }
        if (S2Installed != null) {
            setSwitch2Status(S2Installed,"1");
        }
        if (S3Installed != null) {
            setSwitch3Status(S3Installed,"1");
        }
        if (S4Installed != null) {
            setSwitch4Status(S4Installed,"1");
        }
        if (CurtainInstalled != null) {
            setCurtainSwitchStatus(CurtainInstalled,"1");
        }
        if (LockInstalled != null) {
            setLockStatus(LockInstalled,"1");
        }
    }

    void setRoomOnlineOffline(ROOM room, String status) {
        room.getFireRoom().child("online").setValue(status);
//        String url = MyApp.THE_PROJECT.url + "reservations/setRoomOnlineOrOffline";
//        StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.e("onlineChange" , response +" " + status);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("onlineChange" , error.toString() +" " + status);
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> Params = new HashMap<>();
//                Params.put("room_id", String.valueOf(room.id));
//                Params.put("status" , status);
//                return Params;
//            }
//        };
//        Volley.newRequestQueue(act).add(tabR);
    }

    void setClientInOrOut(ROOM room, String status) {
        room.getFireRoom().child("ClientIn").setValue(status);
//        String url = MyApp.THE_PROJECT.url + "reservations/setClientInOrOut";
//        StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.e("clientInStatus" , response +" " + status);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("clientInStatus" , error.toString() +" " + status);
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> Params = new HashMap<>();
//                Params.put("room_id", String.valueOf(room.id));
//                Params.put("status" , status);
//                return Params;
//            }
//        };
//        Volley.newRequestQueue(act).add(tabR);
    }

    void sendRegistrationToServer(String token) {
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyServerDeviceFirebaseToken" ;
        StringRequest re  = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                    Log.d("tokenRegister" , response) ;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tokenRegister" , error.toString()) ;
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> par = new HashMap<String, String>();
                par.put("token" , token);
                par.put("device_id",MyApp.Device_Id);
                return par;
            }
        };
        if (FirebaseTokenRegister == null) {
            FirebaseTokenRegister = Volley.newRequestQueue(act) ;
        }
        FirebaseTokenRegister.add(re);
    }

    void setRoomLockId(String ID,String roomId) {
        Log.d("lockIdRegister" , ID+" "+roomId) ;
        String url = MyApp.THE_PROJECT.url + "roomsManagement/setRoomLockId" ;
        StringRequest re  = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("lockIdRegister" , response) ;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("lockIdRegister" , error.toString()) ;
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> par = new HashMap<String, String>();
                par.put("room_id" ,roomId);
                par.put("lock_id",ID);
                return par;
            }
        };
        if (FirebaseTokenRegister == null) {
            FirebaseTokenRegister = Volley.newRequestQueue(act) ;
        }
        FirebaseTokenRegister.add(re);
    }

    static void checkInModeRoom(ROOM THEROOM) {
        Log.d("checkinModeTest" ,MyApp.ProjectVariables.getCheckinModeActive()+" "+MyApp.ProjectVariables.CheckinModeTime);
        if (MyApp.ProjectVariables.getCheckinModeActive()) {
            if (MyApp.checkInActions != null) {
                if (MyApp.checkInActions.power) {
                    if (THEROOM.getPOWER_B() != null && THEROOM.getPOWER() != null) {
                        THEROOM.getPOWER().publishDps("{\" 1\":true,\" 2\":true}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }
                            @Override
                            public void onSuccess() {
                                Log.d("checkinModeTest" ,"power on success");
                                if (MyApp.checkInActions.lights) {
                                    turnLightsOn(THEROOM);
//                                    if (THEROOM.getSWITCH1_B() != null && THEROOM.getSWITCH1() != null) {
//                                        if (THEROOM.getSWITCH1_B().dps.get("1") != null) {
//                                            THEROOM.getSWITCH1().publishDps("{\" 1\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH1_B().dps.get("2") != null) {
//                                            THEROOM.getSWITCH1().publishDps("{\" 2\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH1_B().dps.get("3") != null) {
//                                            THEROOM.getSWITCH1().publishDps("{\" 3\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH1_B().dps.get("4") != null) {
//                                            THEROOM.getSWITCH1().publishDps("{\" 4\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                    }
//                                    if (THEROOM.getSWITCH2_B() != null && THEROOM.getSWITCH2() != null) {
//                                        if (THEROOM.getSWITCH2_B().dps.get("1") != null) {
//                                            THEROOM.getSWITCH2().publishDps("{\" 1\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH2_B().dps.get("2") != null) {
//                                            THEROOM.getSWITCH2().publishDps("{\" 2\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH2_B().dps.get("3") != null) {
//                                            THEROOM.getSWITCH2().publishDps("{\" 3\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH2_B().dps.get("4") != null) {
//                                            THEROOM.getSWITCH2().publishDps("{\" 4\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                    }
//                                    if (THEROOM.getSWITCH3_B() != null && THEROOM.getSWITCH3() != null) {
//                                        if (THEROOM.getSWITCH3_B().dps.get("1") != null) {
//                                            THEROOM.getSWITCH1().publishDps("{\" 1\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH3_B().dps.get("2") != null) {
//                                            THEROOM.getSWITCH3().publishDps("{\" 2\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH3_B().dps.get("3") != null) {
//                                            THEROOM.getSWITCH3().publishDps("{\" 3\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH3_B().dps.get("4") != null) {
//                                            THEROOM.getSWITCH3().publishDps("{\" 4\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                    }
//                                    if (THEROOM.getSWITCH4_B() != null && THEROOM.getSWITCH4() != null) {
//                                        if (THEROOM.getSWITCH4_B().dps.get("1") != null) {
//                                            THEROOM.getSWITCH4().publishDps("{\" 1\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH4_B().dps.get("2") != null) {
//                                            THEROOM.getSWITCH4().publishDps("{\" 2\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH4_B().dps.get("3") != null) {
//                                            THEROOM.getSWITCH4().publishDps("{\" 3\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSWITCH4_B().dps.get("4") != null) {
//                                            THEROOM.getSWITCH4().publishDps("{\" 4\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                    }
                                }
                                if (MyApp.checkInActions.curtain) {
                                    if (THEROOM.getCURTAIN_B() != null && THEROOM.getCURTAIN() != null) {
                                        if (THEROOM.getCURTAIN_B().dps.get("1") != null) {
                                            THEROOM.getCURTAIN().publishDps("{\" 1\":true}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {

                                                }
                                                @Override
                                                public void onSuccess() {

                                                }
                                            });
                                        }
                                    }
                                }
                                if (MyApp.checkInActions.ac) {
                                    if (THEROOM.getAC_B() != null && THEROOM.getAC() != null) {
                                        if (THEROOM.getAC_B().dps.get("1") != null) {
                                            THEROOM.getAC().publishDps("{\" 1\":true}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {

                                                }
                                                @Override
                                                public void onSuccess() {

                                                }
                                            });
                                        }
                                    }
                                }
                                if (THEROOM.getPOWER_B().dps.get("8") != null) {
                                    int sec = MyApp.ProjectVariables.CheckinModeTime*60 ;
                                    THEROOM.getPOWER().publishDps("{\" 8\":"+sec+"}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            Log.d("checkinModeTest" ,"power status change success");
                                        }
                                    });
                                }
                                else if (THEROOM.getPOWER_B().dps.get("10") != null) {
                                    int sec = MyApp.ProjectVariables.CheckinModeTime*60 ;
                                    THEROOM.getPOWER().publishDps("{\" 10\":"+sec+"}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess() {
                                            Log.d("checkinModeTest" ,"power status change success");
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        }
//        String Duration = "" ;
//        if (checkInModeTime != 0) {
//            Duration = String.valueOf(checkInModeTime*60) ;
//        }
//        else {
//            Duration = "60" ;
//        }
//        Log.d("checkinModeDuration" , Duration +" "+checkInModeTime);
//        if (THEROOM.getPOWER() != null ) {
//            String finalDuration = Duration;
//            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                @Override
//                public void run() {
//                    Log.d("LightWithWelcome" , THEROOM.getPOWER_B().dps.toString());
//                    if (THEROOM.getPOWER() != null )
//                    {
//                        THEROOM.getPOWER().publishDps("{\"1\": true}", new IResultCallback() {
//                            @Override
//                            public void onError(String code, String error) {
//                                //Toast.makeText(act, error, Toast.LENGTH_SHORT).show();
//                                Log.e("light", error);
//                            }
//
//                            @Override
//                            public void onSuccess() {
//                                //Toast.makeText(act, "turn on the light success", Toast.LENGTH_SHORT).show();
//                                //myRefPower.setValue(1);
//                            }
//                        });
//                        THEROOM.getPOWER().publishDps("{\"2\": true}", new IResultCallback() {
//                            @Override
//                            public void onError(String code, String error) {
//                                //Toast.makeText(act, "turn on the light failure", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onSuccess() {
//                                //Toast.makeText(act, "turn on the light success", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                        THEROOM.getPOWER().publishDps("{\"8\": "+ finalDuration +"}", new IResultCallback() {
//                            @Override
//                            public void onError(String code, String error) {
//                                //Toast.makeText(act, "turn on the light failure", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onSuccess() {
//                                Log.d("LightWithWelcome" , "countdoun");
//                                final long[] tt = {0};
//                                long xx = Integer.parseInt( finalDuration ) ;
//                                Handler H = new Handler();
//                                Runnable r = new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        tt[0] = tt[0] +1000 ;
//                                        H.postDelayed(this,1000);
//                                        Log.d("LightWithWelcome" , tt[0]+" "+(xx*1000));
//                                        if (tt[0] >= (xx*1000)){
//                                            THEROOM.getPOWER().publishDps("{\"2\": false}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//                                                    Log.d("LightWithWelcome" , error);
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//                                                    Log.d("LightWithWelcome" , "Light is off ");
//                                                }
//                                            });
//                                            H.removeCallbacks(this);
//                                        }
//                                    }
//                                };
//                            }
//                        });
//                    }
//                    final long[] tt = {0};
//                    long xx = Integer.parseInt( finalDuration ) ;
//                    if ( THEROOM.getSWITCH1() != null ){
//                        Handler H = new Handler();
//                        Runnable d = new Runnable() {
//                            @Override
//                            public void run() {
//                                tt[0] = tt[0] +1000 ;
//                                H.postDelayed(this,1000);
//                                Log.d("LightWithWelcome" , tt[0]+" "+(xx*1000));
//                                if (tt[0] >= (xx*1000)){
//                                    THEROOM.getSWITCH1().publishDps("{\"1\": false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//                                            Log.d("LightWithWelcome" , error);
//                                        }
//
//                                        @Override
//                                        public void onSuccess() {
//                                            Log.d("LightWithWelcome" , "Light is off ");
//                                        }
//                                    });
//                                    H.removeCallbacks(this);
//                                }
//                            }
//                        } ;
//                        THEROOM.getSWITCH1().publishDps("{\"1\": true}", new IResultCallback() {
//                            @Override
//                            public void onError(String code, String error) {
//                                Log.d("LightWithWelcome" , error);
//                            }
//
//                            @Override
//                            public void onSuccess() {
//                                Log.d("LightWithWelcome" , "Light is on "+finalDuration);
//                                d.run();
//                            }
//                        });
//                    }
//                }
//            });
//        }
    }

    static void checkoutModeRoom(ROOM THEROOM)  {
        if (MyApp.ProjectVariables.getCheckoutModeActive()) {
            if (MyApp.checkOutActions != null) {
                if (MyApp.checkOutActions.power) {
                    if (THEROOM.getPOWER_B() != null && THEROOM.getPOWER() != null) {
                        if (MyApp.ProjectVariables.PoweroffAfterHK == 1) {
                            if (THEROOM.getPOWER_B().dps.get("8") != null) {
                                THEROOM.getPOWER().publishDps("{\" 8\": "+(MyApp.ProjectVariables.CheckoutModeTime * 60)+"}", new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }
                            else if (THEROOM.getPOWER_B().dps.get("10") != null) {
                                THEROOM.getPOWER().publishDps("{\" 10\": "+(MyApp.ProjectVariables.CheckoutModeTime * 60)+"}", new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }
                        }
                        else if (MyApp.ProjectVariables.PoweroffAfterHK == 0) {
                            if (THEROOM.getPOWER_B().dps.get("8") != null) {
                                THEROOM.getPOWER().publishDps("{\" 8\": "+(MyApp.ProjectVariables.CheckoutModeTime * 60)+" , \" 7\": "+(MyApp.ProjectVariables.CheckoutModeTime * 60)+"}", new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }
                            else if (THEROOM.getPOWER_B().dps.get("10") != null) {
                                THEROOM.getPOWER().publishDps("{\" 10\": "+(MyApp.ProjectVariables.CheckoutModeTime * 60)+" , \" 9\": "+(MyApp.ProjectVariables.CheckoutModeTime * 60)+"}", new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }
                        }
                    }
                }
                else {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep((long) MyApp.ProjectVariables.CheckoutModeTime * 60 * 1000);
                                if (MyApp.ProjectVariables.getCheckoutModeActive()) {
                                    if (MyApp.checkOutActions != null) {
                                        if (MyApp.checkOutActions.lights) {
                                            if (THEROOM.getSWITCH1_B() != null && THEROOM.getSWITCH1() != null) {
                                                if (THEROOM.getSWITCH1_B().dps.get("1") != null) {
                                                    THEROOM.getSWITCH1().publishDps("{\" 1\":false}", new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }
                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                                if (THEROOM.getSWITCH1_B().dps.get("2") != null) {
                                                    THEROOM.getSWITCH1().publishDps("{\" 2\":false}", new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }
                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                                if (THEROOM.getSWITCH1_B().dps.get("3") != null) {
                                                    THEROOM.getSWITCH1().publishDps("{\" 3\":false}", new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }
                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                                if (THEROOM.getSWITCH1_B().dps.get("4") != null) {
                                                    THEROOM.getSWITCH1().publishDps("{\" 4\":false}", new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }
                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                            }
                                            if (THEROOM.getSWITCH2_B() != null && THEROOM.getSWITCH2() != null) {
                                                if (THEROOM.getSWITCH2_B().dps.get("1") != null) {
                                                    THEROOM.getSWITCH2().publishDps("{\" 1\":false}", new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }
                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                                if (THEROOM.getSWITCH2_B().dps.get("2") != null) {
                                                    THEROOM.getSWITCH2().publishDps("{\" 2\":false}", new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }
                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                                if (THEROOM.getSWITCH2_B().dps.get("3") != null) {
                                                    THEROOM.getSWITCH2().publishDps("{\" 3\":false}", new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }
                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                                if (THEROOM.getSWITCH2_B().dps.get("4") != null) {
                                                    THEROOM.getSWITCH2().publishDps("{\" 4\":false}", new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }
                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                            }
                                            if (THEROOM.getSWITCH3_B() != null && THEROOM.getSWITCH3() != null) {
                                                if (THEROOM.getSWITCH3_B().dps.get("1") != null) {
                                                    THEROOM.getSWITCH3().publishDps("{\" 1\":false}", new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }
                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                                if (THEROOM.getSWITCH3_B().dps.get("2") != null) {
                                                    THEROOM.getSWITCH3().publishDps("{\" 2\":false}", new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }
                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                                if (THEROOM.getSWITCH3_B().dps.get("3") != null) {
                                                    THEROOM.getSWITCH3().publishDps("{\" 3\":false}", new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }
                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                                if (THEROOM.getSWITCH3_B().dps.get("4") != null) {
                                                    THEROOM.getSWITCH3().publishDps("{\" 4\":false}", new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }
                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                            }
                                            if (THEROOM.getSWITCH4_B() != null && THEROOM.getSWITCH4() != null) {
                                                if (THEROOM.getSWITCH4_B().dps.get("1") != null) {
                                                    THEROOM.getSWITCH4().publishDps("{\" 1\":false}", new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }
                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                                if (THEROOM.getSWITCH4_B().dps.get("2") != null) {
                                                    THEROOM.getSWITCH4().publishDps("{\" 2\":false}", new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }
                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                                if (THEROOM.getSWITCH4_B().dps.get("3") != null) {
                                                    THEROOM.getSWITCH4().publishDps("{\" 3\":false}", new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }
                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                                if (THEROOM.getSWITCH4_B().dps.get("4") != null) {
                                                    THEROOM.getSWITCH4().publishDps("{\" 4\":false}", new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }
                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                        if (MyApp.checkOutActions.ac) {
                                            if (THEROOM.getAC_B() != null && THEROOM.getAC() != null) {
                                                if (THEROOM.getAC_B().dps.get("1") != null) {
                                                    THEROOM.getAC().publishDps("{\" 1\":false}", new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }
                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                        if (MyApp.checkOutActions.curtain) {
                                            if (THEROOM.getCURTAIN_B() != null && THEROOM.getCURTAIN() != null) {
                                                if (THEROOM.getCURTAIN_B().dps.get("1") != null) {
                                                    THEROOM.getCURTAIN().publishDps("{\" 1\":false}", new IResultCallback() {
                                                        @Override
                                                        public void onError(String code, String error) {

                                                        }
                                                        @Override
                                                        public void onSuccess() {

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    t.start();
                }
            }
        }
//        String Duration = "" ;
//        if (checkOutModeTime != 0 ) {
//            Duration = String.valueOf(checkOutModeTime * 60);
//        }
//        else {
//            Duration = "60" ;
//        }
//        Log.d("checkoutModeDuration" , Duration+" "+checkOutModeTime );
//        if (THEROOM.getPOWER_B() != null) {
//            THEROOM.getPOWER().publishDps("{\"1\": true}", new IResultCallback() {
//                @Override
//                public void onError(String code, String error) {
//                    Toast.makeText(act, "Checkout failed room "+THEROOM.RoomNumber, Toast.LENGTH_SHORT).show();
//                }
//                @Override
//                public void onSuccess() {
//                    THEROOM.getPOWER().publishDps("{\"2\": false}", new IResultCallback() {
//                        @Override
//                        public void onError(String code, String error) {
//                            Toast.makeText(act, "Checkout failed room "+THEROOM.RoomNumber, Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onSuccess() {
//                            Toast.makeText(act, "Checkout room success "+THEROOM.RoomNumber, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            });
//        }
//        for (ServiceEmps u : Emps) {
//            if (u.department.equals("Cleanup") || u.department.equals("Service")) {
//                makemessage(u.token, "Cleanup", true, THEROOM.RoomNumber);
//            }
//        }
    }

    public void runClientBackActions(ROOM room) {
        if (MyApp.clientBackActions.lights ) {
            if (room.roomStatus == 2) {
                if (room.ClientIn == 0) {
                    if (room.getPOWER_B() != null && room.getPOWER() != null) {
                        if (room.getPOWER_B().getDps().get("1") != null && room.getPOWER_B().getDps().get("2") != null) {
                            room.getPOWER().publishDps("{\" 1\":true,\" 2\":true}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }
                                @Override
                                public void onSuccess() {
                                    int sec = 2*60 ;
                                    if (room.getPOWER_B().getDps().get("8")!= null) {
                                        room.getPOWER().publishDps("{\" 8\":"+sec+"}", new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {

                                            }

                                            @Override
                                            public void onSuccess() {

                                            }
                                        });
                                    }
                                    else if (room.getPOWER_B().getDps().get("10")!= null) {
                                        room.getPOWER().publishDps("{\" 10\":"+sec+"}", new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {

                                            }

                                            @Override
                                            public void onSuccess() {

                                            }
                                        });
                                    }
                                    turnLightsOn(room);
//                                    if (room.getSWITCH1_B() != null && room.getSWITCH1() != null) {
//                                        if (room.getSWITCH1_B().dps.get("1") != null) {
//                                            room.getSWITCH1().publishDps("{\" 1\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH1_B().dps.get("2") != null) {
//                                            room.getSWITCH1().publishDps("{\" 2\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH1_B().dps.get("3") != null) {
//                                            room.getSWITCH1().publishDps("{\" 3\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH1_B().dps.get("4") != null) {
//                                            room.getSWITCH1().publishDps("{\" 4\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                    }
//                                    if (room.getSWITCH2_B() != null && room.getSWITCH2() != null) {
//                                        if (room.getSWITCH2_B().dps.get("1") != null) {
//                                            room.getSWITCH2().publishDps("{\" 1\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH2_B().dps.get("2") != null) {
//                                            room.getSWITCH2().publishDps("{\" 2\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH2_B().dps.get("3") != null) {
//                                            room.getSWITCH2().publishDps("{\" 3\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH2_B().dps.get("4") != null) {
//                                            room.getSWITCH2().publishDps("{\" 4\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                    }
//                                    if (room.getSWITCH3_B() != null && room.getSWITCH3() != null) {
//                                        if (room.getSWITCH3_B().dps.get("1") != null) {
//                                            room.getSWITCH3().publishDps("{\" 1\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH3_B().dps.get("2") != null) {
//                                            room.getSWITCH3().publishDps("{\" 2\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH3_B().dps.get("3") != null) {
//                                            room.getSWITCH3().publishDps("{\" 3\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH3_B().dps.get("4") != null) {
//                                            room.getSWITCH3().publishDps("{\" 4\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                    }
//                                    if (room.getSWITCH4_B() != null && room.getSWITCH4() != null) {
//                                        if (room.getSWITCH4_B().dps.get("1") != null) {
//                                            room.getSWITCH4().publishDps("{\" 1\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH4_B().dps.get("2") != null) {
//                                            room.getSWITCH4().publishDps("{\" 2\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH4_B().dps.get("3") != null) {
//                                            room.getSWITCH4().publishDps("{\" 3\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                        if (room.getSWITCH4_B().dps.get("4") != null) {
//                                            room.getSWITCH4().publishDps("{\" 4\":true}", new IResultCallback() {
//                                                @Override
//                                                public void onError(String code, String error) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess() {
//
//                                                }
//                                            });
//                                        }
//                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
        if (MyApp.clientBackActions.curtain) {
            if (room.roomStatus == 2) {
                if (room.ClientIn == 0) {
                    if (room.getPOWER_B() != null && room.getPOWER() != null) {
                        if (room.getPOWER_B().getDps().get("1") != null && room.getPOWER_B().getDps().get("2") != null) {
                            room.getPOWER().publishDps("{\" 1\":true,\" 2\":true}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }
                                @Override
                                public void onSuccess() {
                                    int sec = 2*60 ;
                                    room.getPOWER().publishDps("{\" 8\":"+sec+"}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                    if (room.getCURTAIN_B() != null && room.getCURTAIN() != null) {
                                        if (room.getCURTAIN_B().dps.get("1") != null) {
                                            room.getCURTAIN().publishDps("{\" 1\":true}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {

                                                }

                                                @Override
                                                public void onSuccess() {

                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
        if (MyApp.clientBackActions.ac) {
            if (room.roomStatus == 2) {
                if (room.ClientIn == 0) {
                    if (room.getPOWER_B() != null && room.getPOWER() != null) {
                        room.getPOWER().publishDps("{\" 1\":true,\" 2\":true}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }
                            @Override
                            public void onSuccess() {
                                int sec = 2*60 ;
                                if (room.getPOWER_B().getDps().get("8")!= null) {
                                    room.getPOWER().publishDps("{\" 8\":"+sec+"}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }
                                else if (room.getPOWER_B().getDps().get("10")!= null) {
                                    room.getPOWER().publishDps("{\" 10\":"+sec+"}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }
                                if (room.getAC_B() != null && room.getAC() != null) {
                                    room.getAC().publishDps("{\" 1\": true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    static void OpenTheDoor(ROOM THEROOM,RequestOrder callBack) {
        if (THEROOM.getLOCK_B() != null) {
            String url = MyApp.THE_PROJECT.url + "roomsManagement/addClientDoorOpen";
            StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("doorOpenResp" , response);
                    try {
                        JSONObject result = new JSONObject(response);
                        if (result.getString("result") != null) {
                            if (result.getString("result").equals("success")) {
                                ZigbeeLock.getTokenFromApi(MyApp.cloudClientId, MyApp.cloudSecret, act, new RequestOrder() {
                                    @Override
                                    public void onSuccess(String token) {
                                        Log.d("doorOpenResp" , "token "+token);
                                        ZigbeeLock.getTicketId(token, MyApp.cloudClientId, MyApp.cloudSecret, THEROOM.getLOCK_B().devId, act, new RequestOrder() {
                                            @Override
                                            public void onSuccess(String ticket) {
                                                Log.d("doorOpenResp" , "ticket "+ticket);
                                                ZigbeeLock.unlockWithoutPassword(token, ticket, MyApp.cloudClientId, MyApp.cloudSecret, THEROOM.getLOCK_B().devId, act, new RequestOrder() {
                                                    @Override
                                                    public void onSuccess(String res) {
                                                        Log.d("doorOpenResp" , "res "+res);
                                                        callBack.onSuccess(res);
                                                    }

                                                    @Override
                                                    public void onFailed(String error) {
                                                        Log.d("openDoorResp" , "res "+error);
                                                        callBack.onFailed(error);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailed(String error) {
                                                Log.d("doorOpenResp" , "ticket "+error);
                                                callBack.onFailed(error);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailed(String error) {
                                        Log.d("doorOpenResp" , "token "+error);
                                        callBack.onFailed(error);
                                    }
                                });
                            }
                            else {
                                callBack.onFailed(result.getString("error"));
                            }
                        }

                    } catch (JSONException e) {
                        Log.d("doorOpenResp" , e.getMessage());
                        callBack.onFailed(e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("doorOpenResp" , error.toString());
                    callBack.onFailed(error.toString());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("room_id", String.valueOf(THEROOM.id));
                    return params;
                }
            };
            Volley.newRequestQueue(act).add(req);
        }
    }

    public void toggleRoomsDevices(View view) {
        hideSystemUI();
        if (roomsListView.getVisibility() == View.VISIBLE) {
            roomsListView.setVisibility(View.GONE);
            devicesListView.setVisibility(View.VISIBLE);
            searchText.setVisibility(View.VISIBLE);
            searchBtn.setVisibility(View.VISIBLE);
            Toast.makeText(act,"devices are "+Devices.size(),Toast.LENGTH_LONG).show();
        }
        else if (roomsListView.getVisibility() == View.GONE) {
            roomsListView.setVisibility(View.VISIBLE);
            devicesListView.setVisibility(View.GONE);
            searchBtn.setVisibility(View.GONE);
            searchText.setVisibility(View.GONE);
        }
    }

    public void lockAndUnlock(View view) {
        hideSystemUI();
        if(lockDB.getLockValue().equals("off")) {
            Dialog  dd = new Dialog(act);
            dd.setContentView(R.layout.lock_unlock_dialog);
            Button cancel = (Button) dd.findViewById(R.id.confermationDialog_cancel);
            Button lock = (Button) dd.findViewById(R.id.messageDialog_ok);
            EditText password = (EditText) dd.findViewById(R.id.editTextTextPassword);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dd.dismiss();
                }
            });
            lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final lodingDialog loading = new lodingDialog(act);
                    final String pass = password.getText().toString() ;
                    StringRequest re = new StringRequest(Request.Method.POST, "", new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            Log.d("LoginResult" , response +" "+ "" );
                            loading.stop();
                            if (response.equals("1"))
                            {
                                lockDB.modifyValue("on");
                                roomsListView.setVisibility(View.GONE);
                                devicesListView.setVisibility(View.GONE);
                                btnsLayout.setVisibility(View.GONE);
                                mainLogo.setVisibility(View.VISIBLE);
                                dd.dismiss();
                            }
                            else if (response.equals("0"))
                            {
                                Toast.makeText(act,"Lock Failed",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(act,"No Params",Toast.LENGTH_LONG).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            loading.stop();
                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError
                        {
                            Map<String,String> par = new HashMap<String, String>();
                            par.put( "password" , pass ) ;
                            par.put( "hotel" , "1" ) ;
                            return par;
                        }
                    };
                    Volley.newRequestQueue(act).add(re);
                }
            });
            dd.show();
        }
        else if (lockDB.getLockValue().equals("on")) {
            lockDB.modifyValue("off");
            roomsListView.setVisibility(View.GONE);
            devicesListView.setVisibility(View.GONE);
            btnsLayout.setVisibility(View.GONE);
            mainLogo.setVisibility(View.VISIBLE);
        }
        else {
            lockDB.modifyValue("off");
            roomsListView.setVisibility(View.GONE);
            devicesListView.setVisibility(View.GONE);
            btnsLayout.setVisibility(View.GONE);
            mainLogo.setVisibility(View.VISIBLE);
        }
    }

    public static void ensureBluetoothIsEnabled() {
//        if(!TTLockClient.getDefault().isBLEEnabled(act)){
//            TTLockClient.getDefault().requestBleEnable(act);
//        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    static void sendNotification(final JSONObject notification ) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_MESSAGE_URL, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MessagesQueue.add(jsonObjectRequest);

    }

    void getServiceUsersFromFirebase() {
        ServiceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null ) {
                    Emps.clear();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        int id = 0;
                        if (child.child("id").getValue() != null ) {
                            id = Integer.parseInt( child.child("id").getValue().toString());
                        }
                        String name = "";
                        if (child.child("name").getValue() != null ) {
                            name = child.child("name").getValue().toString();
                        }
                        int jobnum = 0 ;
                        if (child.child("jobNumber").getValue() != null ) {
                            jobnum = Integer.parseInt(child.child("jobNumber").getValue().toString());
                        }
                        String department = "";
                        if (child.child("department").getValue() != null ) {
                            department = child.child("department").getValue().toString();
                        }
                        String mobile = "" ;
                        if (child.child("Mobile").getValue() != null ) {
                            mobile = child.child("Mobile").getValue().toString();
                        }
                        String token = "";
                        if (child.child("token").getValue() != null ) {
                            token = child.child("token").getValue().toString() ;
                        }
                       Emps.add(new ServiceEmps(id,1,name,jobnum,department,mobile,token));
                    }
                    Log.d("EmpsAre ",Emps.size()+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void scanLockGateway(View view) {
        int REQUEST_PERMISSION_REQ_CODE = 18 ;
        if (act.checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_REQ_CODE);
            return;
        }
        getScanGatewayCallback();
    }

    private void getScanGatewayCallback(){
//        GatewayClient.getDefault().startScanGateway(new ScanGatewayCallback() {
//            @Override
//            public void onScanGatewaySuccess(ExtendedBluetoothDevice device) {
////                LogUtil.d("device:" + device);
//                TheFoundGateway = device ;
//                gatewaysList.add(device);
//                String[] xx = new String[gatewaysList.size()];
//                for (int i=0; i<gatewaysList.size();i++) {
//                    xx[i] = gatewaysList.get(i).getName();
//                }
//                ArrayAdapter<String> ad = new ArrayAdapter<String>(act,R.layout.gateway_list_item,xx);
//                gatewaysListView.setAdapter(ad);
//                GatewayClient.getDefault().stopScanGateway();
////                if (mListApapter != null)
////                    mListApapter.updateData(device);
//            }
//
//            @Override
//            public void onScanFailed(int errorCode) {
//
//            }
//        });
    }

    public void initLockGateway(View view) {

        if (TheFoundGateway == null ) {

        }
        else {
                GatewayClient.getDefault().connectGateway(TheFoundGateway, new ConnectCallback() {
                    @Override
                    public void onConnectSuccess(ExtendedBluetoothDevice device) {
                        //InitGatewayActivity.launch(act, TheFoundGateway);
                        //LogUtil.d("connect success");
                        Toast.makeText(act,"gateway connected",Toast.LENGTH_LONG);
                        EditText wifiName = (EditText) findViewById(R.id.wifiName);
                        EditText wifiPassword = (EditText) findViewById(R.id.wifiPassword);
                        configureGatewayInfo.uid = acc.getUid();
                        configureGatewayInfo.userPwd = acc.getMd5Pwd();
                        configureGatewayInfo.ssid = wifiName.getText().toString().trim();
                        configureGatewayInfo.wifiPwd = wifiPassword.getText().toString().trim();
                        configureGatewayInfo.plugName = device.getAddress();
                        GatewayClient.getDefault().initGateway(configureGatewayInfo, new InitGatewayCallback() {
                            @Override
                            public void onInitGatewaySuccess(DeviceInfo deviceInfo) {
                                LogUtil.d("gateway init success");
                                Toast.makeText(act,"gateway inited",Toast.LENGTH_LONG);
                                isInitSuccess(deviceInfo);
                            }

                            @Override
                            public void onFail(GatewayError error) {
                                Toast.makeText(act,error.getDescription(),Toast.LENGTH_LONG);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onDisconnected() {
                        Toast.makeText(act, "gateway_out_of_time", Toast.LENGTH_LONG).show();
                    }

                });


        }
    }

    private void isInitSuccess(DeviceInfo deviceInfo) {
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.gatewayIsInitSuccess(ApiService.CLIENT_ID, MyApplication.getmInstance().getAccountInfo().getAccess_token(), TheFoundGateway.getAddress(), System.currentTimeMillis());
        LogUtil.d("call server isSuccess api");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                String json = response.body();
                if (!TextUtils.isEmpty(json)) {
                    GatewayObj gatewayObj = GsonUtil.toObject(json, GatewayObj.class);
                    if (gatewayObj.errcode == 0) {
                        Toast.makeText(act, "init success", Toast.LENGTH_LONG);
                        uploadGatewayDetail(deviceInfo, gatewayObj.getGatewayId());
                    }
                    else Toast.makeText(act,gatewayObj.errmsg,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(act,t.getMessage(),Toast.LENGTH_LONG).show();
                LogUtil.d("t.getMessage():" + t.getMessage());
            }
        });
    }

    private void uploadGatewayDetail(DeviceInfo deviceInfo, int gatewayId) {
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        EditText wifiName = (EditText) findViewById(R.id.wifiName);
        Call<String> call = apiService.uploadGatewayDetail(ApiService.CLIENT_ID, MyApplication.getmInstance().getAccountInfo().getAccess_token(), gatewayId, deviceInfo.getModelNum(), deviceInfo.hardwareRevision, deviceInfo.getFirmwareRevision(), wifiName.getText().toString(), System.currentTimeMillis());
        LogUtil.d("call server isSuccess api");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                String json = response.body();
                if (!TextUtils.isEmpty(json)) {
                    ServerError error = GsonUtil.toObject(json, ServerError.class);
                    if (error.errcode == 0)
                        Toast.makeText(act,"Done",Toast.LENGTH_LONG).show();
                    else Toast.makeText(act,error.errmsg,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(act,t.getMessage(),Toast.LENGTH_LONG).show();
                LogUtil.d("t.getMessage():" + t.getMessage());
            }
        });
    }

    void getScenes() {
        TuyaHomeSdk.getSceneManagerInstance().getSceneList(Login.THEHOME.getHomeId(), new ITuyaResultCallback<List<SceneBean>>() {
            @Override
            public void onSuccess(List<SceneBean> result) {
                loading.stop();
                SCENES = result ;
                Log.d("scenesAre",SCENES.size()+"");
                for (SceneBean s : SCENES) {
                    Log.d("scenesAre",s.getName());
//                    if (s.getName().contains("ServiceSwitchCheckoutScene")) {
//                        TuyaHomeSdk.newSceneInstance(s.getId()).deleteScene(new
//                          IResultCallback() {
//                              @Override
//                              public void onSuccess() {
//                                  //Log.d(TAG, "Delete Scene Success");
//                              }
//                              @Override
//                              public void onError(String errorCode, String errorMessage) {
//                              }
//                          });
//                    }
                }
                setSCENES(SCENES);
            }
            @Override
            public void onError(String errorCode, String errorMessage) {
                Log.d("scenesAre",errorCode+" "+errorMessage);
                new MessageDialog("getting tuya sceins failed "+errorMessage,"error",act);
            }
        });
    }

    void setSCENES(List<SceneBean> SCENES) {
        for (int i = 0; i< ROOMS.size(); i++) {
            if (!searchScene(SCENES, ROOMS.get(i).RoomNumber + "ServiceSwitchDNDScene2")) {
                PreCondition pr = new PreCondition();
                List<PreCondition> lpr = new ArrayList<>();
                List<SceneCondition> conds = new ArrayList<>();
                List<SceneTask> tasks = new ArrayList<>();
                lpr.add(pr);
                if (ROOMS.get(i).getSERVICE1_B() != null) {
                    BoolRule rule = BoolRule.newInstance("dp1", true);
                    SceneCondition cond = SceneCondition.createDevCondition(ROOMS.get(i).getSERVICE1_B(), "1", rule);
                    conds.add(cond);
                    HashMap<String, Object> taskMap = new HashMap<>();
                    taskMap.put("2", false); // Starts a device.
                    SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(ROOMS.get(i).getSERVICE1_B().devId, taskMap);
                    tasks.add(task);
                    TuyaHomeSdk.getSceneManagerInstance().createScene(
                            Login.THEHOME.getHomeId(),
                            ROOMS.get(i).RoomNumber + "ServiceSwitchDNDScene2", // The name of the scene.
                            false,
                            IMAGES.get(0),  // Indicates whether the scene is displayed on the homepage.
                            conds, // The effective period. This parameter is optional.
                            tasks, // The conditions.
                            null,     // The tasks.
                            SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                            new ITuyaResultCallback<SceneBean>() {
                                @Override
                                public void onSuccess(SceneBean sceneBean) {
                                    Log.d("SCENE_DND1", "createScene Success");
                                    TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new
                                            IResultCallback() {
                                                @Override
                                                public void onSuccess() {
                                                    Log.d("SCENE_DND1", "enable Scene Success");
                                                }

                                                @Override
                                                public void onError(String errorCode, String errorMessage) {
                                                    Log.d("SCENE_DND1", errorMessage + " " + errorCode);
                                                }
                                            });
                                }

                                @Override
                                public void onError(String errorCode, String errorMessage) {
                                    Log.d("SCENE_DND1", errorMessage + " " + errorCode);
                                }
                            });
                }
            }
            if (!searchScene(SCENES, ROOMS.get(i).RoomNumber + "ServiceSwitchDNDScene3")) {
                List<SceneCondition> conds = new ArrayList<>();
                List<SceneTask> tasks = new ArrayList<>();
                if (ROOMS.get(i).getSERVICE1_B() != null) {
                    BoolRule rule = BoolRule.newInstance("dp1", true);
                    SceneCondition cond = SceneCondition.createDevCondition(ROOMS.get(i).getSERVICE1_B(), "1", rule);
                    conds.add(cond);
                    HashMap<String, Object> taskMap = new HashMap<>();
                    taskMap.put("3", false); // Starts a device.
                    SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(ROOMS.get(i).getSERVICE1_B().devId, taskMap);
                    tasks.add(task);
                    TuyaHomeSdk.getSceneManagerInstance().createScene(
                            Login.THEHOME.getHomeId(),
                            ROOMS.get(i).RoomNumber + "ServiceSwitchDNDScene3", // The name of the scene.
                            false,
                            IMAGES.get(0),  // Indicates whether the scene is displayed on the homepage.
                            conds, // The effective period. This parameter is optional.
                            tasks, // The conditions.
                            null,     // The tasks.
                            SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                            new ITuyaResultCallback<SceneBean>() {
                                @Override
                                public void onSuccess(SceneBean sceneBean) {
                                    Log.d("SCENE_DND2", "createScene Success");
                                    TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new
                                            IResultCallback() {
                                                @Override
                                                public void onSuccess() {
                                                    Log.d("SCENE_DND2", "enable Scene Success");
                                                }

                                                @Override
                                                public void onError(String errorCode, String errorMessage) {
                                                    Log.d("SCENE_DND2", errorMessage);
                                                }
                                            });
                                }

                                @Override
                                public void onError(String errorCode, String errorMessage) {
                                    Log.d("SCENE_DND2", errorMessage);
                                }
                            });
                }
            }
            if (!searchScene(SCENES, ROOMS.get(i).RoomNumber + "ServiceSwitchDNDScene4")) {
                List<SceneCondition> conds = new ArrayList<>();
                List<SceneTask> tasks = new ArrayList<>();
                if (ROOMS.get(i).getSERVICE1_B() != null) {
                    BoolRule rule = BoolRule.newInstance("dp1", true);
                    SceneCondition cond = SceneCondition.createDevCondition(ROOMS.get(i).getSERVICE1_B(), "1", rule);
                    conds.add(cond);
                    HashMap<String, Object> taskMap = new HashMap<>();
                    taskMap.put("4", false); // Starts a device.
                    SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(ROOMS.get(i).getSERVICE1_B().devId, taskMap);
                    tasks.add(task);
                    TuyaHomeSdk.getSceneManagerInstance().createScene(
                            Login.THEHOME.getHomeId(),
                            ROOMS.get(i).RoomNumber + "ServiceSwitchDNDScene4", // The name of the scene.
                            false,
                            IMAGES.get(0),  // Indicates whether the scene is displayed on the homepage.
                            conds, // The effective period. This parameter is optional.
                            tasks, // The conditions.
                            null,     // The tasks.
                            SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                            new ITuyaResultCallback<SceneBean>() {
                                @Override
                                public void onSuccess(SceneBean sceneBean) {
                                    Log.d("SCENE_DND2", "createScene Success");
                                    TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new
                                            IResultCallback() {
                                                @Override
                                                public void onSuccess() {
                                                    Log.d("SCENE_DND2", "enable Scene Success");
                                                }

                                                @Override
                                                public void onError(String errorCode, String errorMessage) {
                                                    Log.d("SCENE_DND2", errorMessage);
                                                }
                                            });
                                }

                                @Override
                                public void onError(String errorCode, String errorMessage) {
                                    Log.d("SCENE_DND2", errorMessage);
                                }
                            });
                }
            }
            if (!searchScene(SCENES, ROOMS.get(i).RoomNumber + "ServiceSwitchCleanupScene")) {
                List<SceneCondition> conds = new ArrayList<>();
                List<SceneTask> tasks = new ArrayList<>();
                if (ROOMS.get(i).getSERVICE1_B() != null) {
                    BoolRule rule = BoolRule.newInstance("dp2", true);
                    SceneCondition cond = SceneCondition.createDevCondition(ROOMS.get(i).getSERVICE1_B(), "2", rule);
                    conds.add(cond);
                    HashMap<String, Object> taskMap = new HashMap<>();
                    taskMap.put("1", false); // Starts a device.
                    SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(ROOMS.get(i).getSERVICE1_B().devId, taskMap);
                    tasks.add(task);
                    TuyaHomeSdk.getSceneManagerInstance().createScene(
                            Login.THEHOME.getHomeId(),
                            ROOMS.get(i).RoomNumber + "ServiceSwitchCleanupScene", // The name of the scene.
                            false,
                            IMAGES.get(0),  // Indicates whether the scene is displayed on the homepage.
                            conds, // The effective period. This parameter is optional.
                            tasks, // The conditions.
                            null,     // The tasks.
                            SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                            new ITuyaResultCallback<SceneBean>() {
                                @Override
                                public void onSuccess(SceneBean sceneBean) {
                                    Log.d("SCENE_Cleanup", "createScene Success");
                                    TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new
                                            IResultCallback() {
                                                @Override
                                                public void onSuccess() {
                                                    Log.d("SCENE_Cleanup", "enable Scene Success");
                                                }

                                                @Override
                                                public void onError(String errorCode, String errorMessage) {
                                                    Log.d("SCENE_Cleanup", errorMessage);
                                                }
                                            });
                                }

                                @Override
                                public void onError(String errorCode, String errorMessage) {
                                    Log.d("SCENE_Cleanup", errorMessage);
                                }
                            });
                }
            }
            if (!searchScene(SCENES, ROOMS.get(i).RoomNumber + "ServiceSwitchLaundryScene")) {
                List<SceneCondition> conds = new ArrayList<>();
                List<SceneTask> tasks = new ArrayList<>();
                if (ROOMS.get(i).getSERVICE1_B() != null) {
                    BoolRule rule = BoolRule.newInstance("dp3", true);
                    SceneCondition cond = SceneCondition.createDevCondition(ROOMS.get(i).getSERVICE1_B(), "3", rule);
                    conds.add(cond);
                    HashMap<String, Object> taskMap = new HashMap<>();
                    taskMap.put("1", false); // Starts a device.
                    SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(ROOMS.get(i).getSERVICE1_B().devId, taskMap);
                    tasks.add(task);
                    TuyaHomeSdk.getSceneManagerInstance().createScene(
                            Login.THEHOME.getHomeId(),
                            ROOMS.get(i).RoomNumber + "ServiceSwitchLaundryScene", // The name of the scene.
                            false,
                            IMAGES.get(0),  // Indicates whether the scene is displayed on the homepage.
                            conds, // The effective period. This parameter is optional.
                            tasks, // The conditions.
                            null,     // The tasks.
                            SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                            new ITuyaResultCallback<SceneBean>() {
                                @Override
                                public void onSuccess(SceneBean sceneBean) {
                                    Log.d("SCENE_Laundry", "createScene Success");
                                    TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new
                                            IResultCallback() {
                                                @Override
                                                public void onSuccess() {
                                                    Log.d("SCENE_Laundry", "enable Scene Success");
                                                }

                                                @Override
                                                public void onError(String errorCode, String errorMessage) {
                                                    Log.d("SCENE_Laundry", errorMessage);
                                                }
                                            });
                                }

                                @Override
                                public void onError(String errorCode, String errorMessage) {
                                    Log.d("SCENE_Laundry", errorMessage);
                                }
                            });
                }
            }
            if (!searchScene(SCENES, ROOMS.get(i).RoomNumber + "ServiceSwitchCheckoutScene")) {
                List<SceneCondition> conds = new ArrayList<>();
                List<SceneTask> tasks = new ArrayList<>();
                if (ROOMS.get(i).getSERVICE1_B() != null) {
                    BoolRule rule = BoolRule.newInstance("dp4", true);
                    SceneCondition cond = SceneCondition.createDevCondition(ROOMS.get(i).getSERVICE1_B(), "4", rule);
                    conds.add(cond);
                    HashMap<String, Object> taskMap = new HashMap<>();
                    taskMap.put("1", false); // Starts a device.
                    SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(ROOMS.get(i).getSERVICE1_B().devId, taskMap);
                    tasks.add(task);
                    TuyaHomeSdk.getSceneManagerInstance().createScene(
                            Login.THEHOME.getHomeId(),
                            ROOMS.get(i).RoomNumber + "ServiceSwitchCheckoutScene", // The name of the scene.
                            false,
                            IMAGES.get(0),  // Indicates whether the scene is displayed on the homepage.
                            conds, // The effective period. This parameter is optional.
                            tasks, // The conditions.
                            null,     // The tasks.
                            SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                            new ITuyaResultCallback<SceneBean>() {
                                @Override
                                public void onSuccess(SceneBean sceneBean) {
                                    Log.d("SCENE_Laundry", "createScene Success");
                                    TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new
                                            IResultCallback() {
                                                @Override
                                                public void onSuccess() {
                                                    Log.d("SCENE_Laundry", "enable Scene Success");
                                                }

                                                @Override
                                                public void onError(String errorCode, String errorMessage) {
                                                    Log.d("SCENE_Laundry", errorMessage);
                                                }
                                            });
                                }

                                @Override
                                public void onError(String errorCode, String errorMessage) {
                                    Log.d("SCENE_Laundry", errorMessage);
                                }
                            });
                }
            }
            }
    }

    void getSceneBGs() {
        TuyaHomeSdk.getSceneManagerInstance().getSceneBgs(new ITuyaResultCallback<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> strings) {
                Log.d("scenesAre","images get done");
                IMAGES = strings ;
                getScenes();
            }

            @Override
            public void onError(String s, String s1) {
                Log.d("scenesAre",s+" "+s1);
                new MessageDialog("getting tuya sceins failed "+s1,"error",act);
            }
        });
    }

    public static boolean searchScene (List<SceneBean> list , String name) {
        boolean res = false ;
        for (int i=0 ; i<list.size();i++) {
            if (list.get(i).getName().equals(name)) {
                res = true ;
                break;
            }
        }
        return res ;
    }

    public void goToLocks(View view) {
        Intent i = new Intent(act,Locks.class);
        startActivity(i);
    }

    DeviceBean searchRoomDevice(List<DeviceBean> devices,ROOM room,String deviceType) {
        DeviceBean d = null ;
        for (int i=0; i<devices.size();i++) {
            if (devices.get(i).name.equals(room.RoomNumber+deviceType)) {
                d = devices.get(i);
            }
        }
        return d ;
    }

    public void logOut(View view) {
        AlertDialog.Builder b = new AlertDialog.Builder(act);
        b
                .setTitle("Are you sure .?")
                .setMessage("Are you sure to log out ")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loading = new lodingDialog(act);
                String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyServerDeviceFirebaseStatus";
                StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("changeDeviceStatus" , response);
                        dialogInterface.dismiss();
                        loading.stop();
                        SharedPreferences.Editor editor = getSharedPreferences("MyProject", MODE_PRIVATE).edit();
                        editor.putString("projectName" , null);
                        editor.putString("tuyaUser" , null);
                        editor.putString("tuyaPassword" , null);
                        editor.putString("lockUser" , null);
                        editor.putString("lockPassword" , null);
                        editor.apply();
                        Intent i = new Intent(act,Login.class);
                        act.startActivity(i);
                        act.finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("changeDeviceStatus" , error.toString());
                        loading.stop();
                        new MessageDialog(error.toString(),"error",act);
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("device_id",MyApp.Device_Id);
                        params.put("status","0");
                        return params;
                    }
                };
                Volley.newRequestQueue(act).add(req);
            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        })
                .setNeutralButton("Delete Device", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                loading = new lodingDialog(act);
                                String url = MyApp.THE_PROJECT.url + "roomsManagement/deleteControlDevice";
                                StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("deleteDeviceStatus" , response);
                                        try {
                                            JSONObject res = new JSONObject(response);
                                            if (res.getString("result").equals("success")) {
                                                dialogInterface.dismiss();
                                                loading.stop();
                                                SharedPreferences.Editor editor = getSharedPreferences("MyProject", MODE_PRIVATE).edit();
                                                editor.putString("projectName" , null);
                                                editor.putString("tuyaUser" , null);
                                                editor.putString("tuyaPassword" , null);
                                                editor.putString("lockUser" , null);
                                                editor.putString("lockPassword" , null);
                                                editor.putString("Device_Id" , null);
                                                editor.putString("Device_Name" , null);
                                                editor.apply();
                                                Intent i = new Intent(act,Login.class);
                                                act.startActivity(i);
                                                act.finish();
                                            }
                                            else {
                                                new MessageDialog("delete device failed" , "failed",act);
                                                loading.stop();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            new MessageDialog(e.toString() , "failed",act);
                                            loading.stop();
                                        }


                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("changeDeviceStatus" , error.toString());
                                        loading.stop();
                                        new MessageDialog(error.toString(),"error",act);
                                    }
                                }){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("device_id",MyApp.Device_Id);
                                        return params;
                                    }
                                };
                                Volley.newRequestQueue(act).add(req);
                            }
                        })
                .create().show();
    }

    public void login(){
        String url = MyApp.THE_PROJECT.url + "roomsManagement/modifyServerDeviceFirebaseStatus";
        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("changeDeviceStatus" , response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("changeDeviceStatus" , error.toString());
                loading.stop();
                new MessageDialog(error.toString(),"error",act);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("device_id",MyApp.Device_Id);
                params.put("status","1");
                return params;
            }
        };
        Volley.newRequestQueue(act).add(req);
    }

    static List<SceneBean> getRoomScenes(ROOM r,List<SceneBean> list) {
        List<SceneBean> res = new ArrayList<>();
        for (int i=0;i<list.size();i++) {
            if (list.get(i).getName().contains(String.valueOf(r.RoomNumber))) {
                res.add(list.get(i));
            }
        }
        return res ;
    }

    static SceneBean getMood(List<SceneBean> list,String mood) {
        SceneBean s = null ;
        for (int i=0;i<list.size();i++) {
            if (list.get(i).getName().contains(mood)) {
                s = list.get(i) ;
            }
        }
        return s ;
    }

    static DeviceBean getMoodConditionDevice(SceneBean s , ROOM r) {
        Log.d("checkinModeTest" , "here");
        DeviceBean d = null ;
        if (s.getConditions() != null) {
            Log.d("checkinModeTest" , "cond not null "+s.getConditions().get(0).getEntityId());
            if (s.getConditions().get(0) != null) {
                Log.d("checkinModeTest" ,"cond 0 not null");
                if (r.getSWITCH1_B() != null) {
                    if (s.getConditions().get(0).getEntityId().equals(r.getSWITCH1_B().devId)) {
                        d = r.getSWITCH1_B() ;
                    }
                }
                if (r.getSWITCH2_B() != null) {
                    if (s.getConditions().get(0).getEntityId().equals(r.getSWITCH2_B().devId)) {
                        d = r.getSWITCH2_B() ;
                    }
                }
                if (r.getSWITCH3_B() != null) {
                    if (s.getConditions().get(0).getEntityId().equals(r.getSWITCH3_B().devId)) {
                        d = r.getSWITCH3_B() ;
                    }
                }
                if (r.getSWITCH4_B() != null) {
                    if (s.getConditions().get(0).getEntityId().equals(r.getSWITCH4_B().devId)) {
                        d = r.getSWITCH4_B() ;
                    }
                }
            }
            else {
                Log.d("checkinModeTest" , "cond 0 null");
            }
        }
        else {
            Log.d("checkinModeTest" , "cond null");
        }
        return d ;
    }

    static String getMoodConditionDeviceButton(SceneBean s) {
        String res = null ;
        if (s.getConditions() != null) {
            if (s.getConditions().get(0) != null) {
                res = s.getConditions().get(0).getEntitySubIds();
            }
        }
        return res;
    }

    static void turnLightsOn(ROOM THEROOM) {
        List<SceneBean> ss = getRoomScenes(THEROOM,SCENES);
        SceneBean S = getMood(ss,"Living") ;
        if (S != null) {
            Log.d("checkinModeTest" ,"scene found");
            DeviceBean D = getMoodConditionDevice(S,THEROOM) ;
            if (D != null) {
                Log.d("checkinModeTest" ,"device found");
                String button = getMoodConditionDeviceButton(S);
                if (button != null) {
                    Log.d("checkinModeTest" ,"button found");

                    TuyaHomeSdk.newDeviceInstance(D.devId).publishDps("{\" "+button+"\":true}", new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {
                            Log.d("checkinModeTest" ,error);
                        }

                        @Override
                        public void onSuccess() {
                            Log.d("checkinModeTest" ,"living started");
                        }
                    });
                }
            }
            else {
                Log.d("checkinModeTest" ,"device null");
                TuyaHomeSdk.newSceneInstance(S.getId()).executeScene(new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {

                    }

                    @Override
                    public void onSuccess() {

                    }
                });
            }
        }
    }


    private void KeepScreenFull() {
        final Calendar x = Calendar.getInstance(Locale.getDefault());
        final Handler hander = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hander.post(new Runnable() {
                    @Override
                    public void run() {
                        hideSystemUI();
                        KeepScreenFull();
                    }
                });
            }
        }).start();
    }
    void setPowerOnOff(ROOM room,String status) {
        String url = MyApp.THE_PROJECT.url + "reservations/setPowerOnOrOff";
        StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("powerstatusChange" , response +" " + status);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("powerstatusChange" , error.toString() +" " + status);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> Params = new HashMap<>();
                Params.put("room_id",String.valueOf(room.id) );
                Params.put("power_status" , status);
                return Params;
            }
        };
        REQ.add(tabR);
    }
    void setDoorOpenOrClosed(ROOM room, String status) {
        String url = MyApp.THE_PROJECT.url + "reservations/setDoorOpenOrClosed";
        StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("dooStatusChange" , response +" " + status);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("dooStatusChange" , error.toString() +" " + status);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> Params = new HashMap<>();
                Params.put("room_id", String.valueOf(room.id));
                Params.put("door_status" , status);
                return Params;
            }
        };
        Volley.newRequestQueue(act).add(tabR);
    }
    public static void makemessage(String t ,String Order , boolean addOrRemove , int RoomNumber) {

        String NOTIFICATION_TITLE = Order ;
        String NOTIFICATION_MESSAGE = "" ;
        if (Order.equals("DND")) {
            if (addOrRemove) {
                NOTIFICATION_MESSAGE = RoomNumber + " is on DND mode";
            }
            else {
                NOTIFICATION_MESSAGE = "DND mode for "+RoomNumber+ " is off";
            }
        }
        else {
            if (addOrRemove) {
                NOTIFICATION_MESSAGE = "New " + Order + " Order From Room "+RoomNumber;
            }
            else {
                NOTIFICATION_MESSAGE = "Cancelled " + Order + " Order From Room "+RoomNumber;
            }
        }



        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);
            notifcationBody.put("RoomNumber", RoomNumber);
            notification.put("to", t);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {

        }
        sendNotification(notification);
    }
    void getServiceEmps() {
        StringRequest request = new StringRequest(Request.Method.POST, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.equals("0")) {
                    try {
                        JSONArray arr = new JSONArray(response);
                        for (int i=0;i<arr.length();i++) {
                            JSONObject row = arr.getJSONObject(i);
                            ServiceEmps emp = new ServiceEmps(row.getInt("id"),row.getInt("projectId"),row.getString("name"),row.getInt("jobNumber"),row.getString("department"),row.getString("mobile"),row.getString("token"));
                            Emps.add(emp);
                        }
                        Log.d("EmpsCount" , Emps.size()+"");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(act,"No service emps",Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {

        };
        Volley.newRequestQueue(act).add(request);
    }

}