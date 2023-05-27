package com.syriasoft.hotelservices;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.syriasoft.hotelservices.lock.LockObj;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.ControlLockCallback;
import com.ttlock.bl.sdk.constant.ControlAction;
import com.ttlock.bl.sdk.entity.ControlLockResult;
import com.ttlock.bl.sdk.entity.LockError;
import com.tuya.smart.android.device.api.ITuyaDeviceMultiControl;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.wang.avi.AVLoadingIndicatorView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    static Activity act  ;
    final String TAG = "NOTIFICATION TAG";
    Button  GymBtn,curtainBtn,ShowAc,ShowMiniBar;
    static public DatabaseReference  RoomDevicesRef,ServiceUsers,myRefLogo,myRefCheckOutDuration,myRefCheckInDuration,myRefToken,myRefDoorWarning,myRefSetpointInterval,myRefSetpoint,myRefFacility,myRefRoomServiceText,myRefServiceSwitch,myRefPowerSwitch, myRefId, myRefRorS,myRefTemp,myRefdep,myRefStatus,myRefReservation ,myRefPower ,myRefCurtain , myRefDoor ,myRefRoomStatus , Room , myRefDND, myRefTabStatus, myRefLaundry , myRefCleanup , myRefRoomService , myRefSos , myRefRestaurant , myRefGym , myRefCheckout ,myRefDoorSensor,myRefMotionSensor,myRefCurtainSwitch,myRefSwitch1,myRefSwitch2,myRefSwitch3,myRefSwitch4,myRefThermostat,myRefLock;
    static boolean DNDStatus=false,LaundryStatus=false,CleanupStatus=false,RoomServiceStatus,SosStatus,RestaurantStatus,CheckoutStatus = false;
    static String roomServiceOrder ="";
    TextView time , date;
    static LockObj myTestLockEKey ;
    static int  RoomOrSuite =1 , ID ,CURRENT_ROOM_STATUS=0 ,RESERVATION =0 ;
    private final String TempSetPoint = "250" ;
    static  boolean  Switch1Status=false,Switch2Status=false ,Switch3Status=false , Switch4Status=false  ;
    static RESERVATION THERESERVATION ;
    static OrderDB order ;
    RecyclerView LAUNDRYMENU , MINIBARMENU ;
    static DisplayMetrics displayMetrics ;
    private List<FACILITY> Facilities,Gyms ;
    private List<LAUNDRY> Laundries ;
    private List<MINIBAR> Minibar ;
    public static List<RESTAURANT_UNIT> Restaurants ;
    ITuyaDeviceMultiControl iTuyaDeviceMultiControl ;
    static ROOM THEROOM  ;
    LinearLayout homeBtn,ServicesBtn,RestaurantBtn,ShowLighting,showAc,LaundryBtn,CheckOutBtn,CleanUpBtn,RoomServiceBtn,SOSBtn,mainLayout,laundryPriceList,minibarPriceList,lightsLayout,serviceLayout,DNDBtn,OpenDoor ;
    static List<Activity> RestaurantActivities ;
    static ImageView laundryImage,laundryIcon,dndImage,dndIcon,sosImage,sosIcon,cleanupImage,cleanupIcon,checkoutimage,checkouticon,roomserviceimage,roomserviceicon,restaurantIcon;
    static TextView laundryText ,dndText , sosText,cleanupText,text,roomservicetext;
    static Resources RESOURCES ;
    private Runnable backHomeThread ;
    static long x = 0 ;
    private Handler H ;
    static String LOGO ;
    public static List<ServiceEmps> Emps ;
    public static LightingDB lightsDB ;
    public static int sosCounter = 1 , roomserviceCounter = 1 ;
    SharedPreferences pref ;
    SharedPreferences.Editor editor ;
    WindowInsetsControllerCompat windowInsetsController;
    List<SceneBean> SCENES,MY_SCENES,LivingMood,SleepMood,WorkMood,RomanceMood,ReadMood,MasterOffMood ;
    private static RequestQueue FirebaseTokenRegister ;
    DeviceBean Living,Sleep,Work,Romance,Read,MasterOff;
    boolean currentFocus,isPaused;
    Handler collapseNotificationHandler;
    public static List<String> IMAGES ;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        TTLockClient.getDefault().prepareBTService(getApplicationContext());
        setActivity();
        order = new OrderDB(act);
        order.removeOrder();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }
                String token = task.getResult();
                sendRegistrationToServer(token);
                myRefToken.setValue(token);
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
                    myRefToken.setValue(token);
                }
            });
            }},1000*60*15,1000*60*15);
        Timer refreshTimer = new Timer() ;
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getMyDevices();
            }
        },0,1000*60*60*2);
        Log.d("deviceInfo",Build.DEVICE+" "+Build.BRAND+" "+Build.MANUFACTURER+" "+Build.MODEL+" "+Build.PRODUCT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        isPaused = false;
        prepareLights();
        prepareMoodButtons();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        currentFocus = hasFocus;

        if (!hasFocus) {

            // Method that handles loss of window focus
            collapseNow();
        }
    }

    @Override
    public void onBackPressed() {
        //act.finish();
    }

    void setActivity() {
        act = this ;
        THEROOM = MyApp.Room ;
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        pref = getSharedPreferences("MyProject", MODE_PRIVATE);
        editor = getSharedPreferences("MyProject", MODE_PRIVATE).edit();
        Facilities = new ArrayList<>();
        RestaurantActivities = new ArrayList<>();
        Emps = new ArrayList<>();
        Gyms = new ArrayList<>();
        Restaurants = new ArrayList<>();
        Minibar = new ArrayList<>();
        RESOURCES = getResources();
        Laundries = new ArrayList<>();
        IMAGES = new ArrayList<>();
        SCENES = new ArrayList<>();
        MY_SCENES = new ArrayList<>();
        LivingMood = new ArrayList<>();
        SleepMood = new ArrayList<>();
        WorkMood = new ArrayList<>();
        RomanceMood = new ArrayList<>();
        ReadMood = new ArrayList<>();
        MasterOffMood = new ArrayList<>();
        RestaurantBtn = findViewById(R.id.Restaurant);
        LaundryBtn = findViewById(R.id.laundry_btn);
        CleanUpBtn = findViewById(R.id.cleanup_btn);
        CheckOutBtn = findViewById(R.id.checkout_btn);
        DNDBtn = findViewById(R.id.dndBtn);
        SOSBtn = findViewById(R.id.sosBtn);
        OpenDoor = findViewById(R.id.Door_Button);
        ServicesBtn = findViewById(R.id.ServicesBtn_cardview);
        showAc = findViewById(R.id.ACBtn_cardview);
        RoomServiceBtn = findViewById(R.id.roomservice_btn);
        date = findViewById(R.id.mainDate);
        time = findViewById(R.id.mainTime);
        homeBtn = findViewById(R.id.home_Btn);
        homeBtn.setVisibility(View.GONE);
        laundryImage  = findViewById(R.id.imageView16);
        laundryIcon = findViewById(R.id.imageView10);
        laundryText= findViewById(R.id.textView44);
        dndImage = findViewById(R.id.DND_Image);
        dndIcon = findViewById(R.id.DND_Icon);
        dndText = findViewById(R.id.DND_Text);
        sosImage = findViewById(R.id.SOS_Image);
        sosText = findViewById(R.id.SOS_Text);
        sosIcon = findViewById(R.id.SOS_Icon);
        roomserviceimage = findViewById(R.id.imageView8);
        roomserviceicon = findViewById(R.id.imageView7);
        roomservicetext = findViewById(R.id.textView38);
        checkoutimage = findViewById(R.id.imageView11);
        text = findViewById(R.id.textView42);
        checkouticon = findViewById(R.id.imageView20);
        cleanupImage = findViewById(R.id.imageView19);
        cleanupText = findViewById(R.id.textView45);
        cleanupIcon = findViewById(R.id.imageView9);
        restaurantIcon = findViewById(R.id.imageView2);
        serviceLayout = findViewById(R.id.Service_Btns);
        lightsLayout = findViewById(R.id.lightingLayout);
        mainLayout = findViewById(R.id.main_layout);
        LAUNDRYMENU = findViewById(R.id.laundryMenu_recycler);
        MINIBARMENU = findViewById(R.id.minibar_recycler);
        LinearLayoutManager laundrymanager = new LinearLayoutManager(act, RecyclerView.HORIZONTAL, false);
        final GridLayoutManager manager1 = new GridLayoutManager(this,4);
        manager1.setOrientation(LinearLayoutManager.VERTICAL);
        LAUNDRYMENU.setLayoutManager(laundrymanager);
        MINIBARMENU.setLayoutManager(manager1);
        ShowLighting = findViewById(R.id.LightsBtn_cardview);
        ShowAc = findViewById(R.id.hideShowAcLayout);
        minibarPriceList = findViewById(R.id.minibar_priceList);
        laundryPriceList = findViewById(R.id.laundry_pricelist);
        ShowMiniBar = findViewById(R.id.hideShowMinibarLayout);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://hotelservices-ebe66.firebaseio.com/");
        ServiceUsers = database.getReference(MyApp.ProjectName+"ServiceUsers");
        Room = database.getReference(MyApp.ProjectName+"/B"+MyApp.Room.Building+"/F"+MyApp.Room.Floor+"/R"+MyApp.Room.RoomNumber);
        myRefLaundry = Room.child("Laundry");
        myRefCleanup = Room.child("Cleanup");
        myRefRoomService = Room.child("RoomService");
        myRefRoomServiceText = Room.child("RoomServiceText");
        myRefSos = Room.child("SOS");
        myRefRestaurant = Room.child("Restaurant");
        myRefCheckout = Room.child("Checkout");
        myRefRoomStatus = Room.child("roomStatus");
        myRefStatus = Room.child("Status");
        myRefDND = Room.child("DND");
        myRefDoorSensor = Room.child("DoorSensor");
        myRefMotionSensor = Room.child("MotionSensor");
        myRefThermostat = Room.child("Thermostat");
        myRefCurtainSwitch = Room.child("CurtainSwitch");
        myRefLock = Room.child("Lock");
        myRefSwitch1 = Room.child("Switch1");
        myRefSwitch2 = Room.child("Switch2");
        myRefSwitch3 = Room.child("Switch3");
        myRefSwitch4 = Room.child("Switch4");
        myRefDoor = Room.child("doorStatus");
        myRefCurtain = Room.child("curtainStatus");
        myRefPower = Room.child("powerStatus");
        myRefTabStatus = Room.child("Tablet");
        myRefReservation = Room.child("ReservationNumber");
        myRefdep = Room.child("dep");
        myRefTemp = Room.child("temp");
        myRefRorS=Room.child("SuiteStatus");
        myRefId = Room.child("id");
        myRefPowerSwitch = Room.child("PowerSwitch");
        myRefServiceSwitch = Room.child("ServiceSwitch");
        myRefFacility = Room.child("Facility");
        myRefSetpoint = Room.child("TempSetPoint");
        myRefSetpointInterval = Room.child("SetPointInterval");
        myRefDoorWarning = Room.child("DoorWarning");
        myRefCheckInDuration = Room.child("CheckInModeTime");
        myRefCheckOutDuration = Room.child("CheckOutModeTime");
        myRefLogo = Room.child("Logo");
        myRefToken = Room.child("token");
        RoomDevicesRef = database.getReference(MyApp.ProjectName+"Devices").child(String.valueOf(THEROOM.RoomNumber));
        TextView RoomNumber = (TextView) findViewById(R.id.RoomNumber_MainScreen);
        RoomNumber.setText(String.valueOf(MyApp.Room.RoomNumber));
        iTuyaDeviceMultiControl = TuyaHomeSdk.getDeviceMultiControlInstance();
        lightsDB = new LightingDB(act) ;
        blink();
        backHomeThread = new Runnable() {
            @Override
            public void run() {
                H = new Handler();
                x = x+1000 ;
                Log.d("backThread" , x+"");
                H.postDelayed(this,1000);
                if (x >= 20000){
                    LinearLayout v = (LinearLayout) findViewById(R.id.home_Btn);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            backToMain(v);
                            H.removeCallbacks(backHomeThread);
                            x=0;
                        }
                    });
                }
            }
        };
        getServiceUsersFromFirebase();
        getFacilities();
        setActivityActions();
        setFireRoomListeners();
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE);
        KeepScreenFull();
        getSceneBGs();
        setTheAcLayout();
//        if (THEROOM.getWiredZBGateway() == null ) {
//            ZBRouterStatus = false ;
//            ShowLighting.setVisibility(View.GONE);
//            curtainBtn.setVisibility(View.GONE);
//            THEROOM.setWiredZBGateway(null);
//        }
//        else {
//            final boolean[] xxx = {false};
//            final boolean[] D = {false};
//            final Handler[] timerHandler = {new Handler()};
//            final Handler[] timerDoorHandler = {new Handler()};
//            TempRonnable = new Runnable() {
//
//                @Override
//                public void run()
//                {
//                    long millis = System.currentTimeMillis() - thermostatStartTime ;
//                    int seconds = (int) (millis / 1000);
//                    int minutes = seconds / 60;
//                    seconds = seconds % 60;
//                    timerHandler[0].postDelayed(this, 1000);
//                    theThermoPeriod = System.currentTimeMillis() - thermostatStartTime ;
//                    Log.d("theSTATUS" , String.valueOf(xxx[0])+" "+ClientTemp+" " +minutes+":"+seconds);
//                    if ( theThermoPeriod >=  theTime  && xxx[0])
//                    {
//                        if (Tuya_Devices.AC != null)
//                        {
//                            Tuya_Devices.AC.publishDps("{\" 2\": "+TempSetPoint+"}", new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//
//                                }
//
//                                @Override
//                                public void onSuccess()
//                                {
//                                    //ToastMaker.MakeToast("Temp Set to Setpoint " , act);
//                                    xxx[0] = false ;
//                                }
//                            });
//                        }
//
//                        timerHandler[0].removeCallbacks(TempRonnable);
//                    }
//                    else if (theThermoPeriod >=  theTime  && !xxx[0])
//                    {
//                        timerHandler[0].removeCallbacks(TempRonnable);
//                    }
//
//
//                }
//            };
//            DoorRunnable = new Runnable() {
//                @Override
//                public void run()
//                {
//                    long millis = System.currentTimeMillis() - theDoorStartTime ;
//                    int seconds = (int) (millis / 1000);
//                    int minutes = seconds / 60;
//                    seconds = seconds % 60;
//                    timerDoorHandler[0].postDelayed(this,1000) ;
//                    theDoorPeriod = System.currentTimeMillis() - theDoorStartTime ;
//                    Log.d("theSTATUSDOOR" , String.valueOf(D[0])+" " +minutes+":"+seconds);
//                    if ( theDoorPeriod >=  theDoorTime  && D[0])
//                    {
//                        myRefDoor.setValue(2);
//                        timerDoorHandler[0].removeCallbacks(DoorRunnable);
//                    }
//                    else if (theDoorPeriod >=  theDoorTime  && !D[0])
//                    {
//                        timerDoorHandler[0].removeCallbacks(DoorRunnable);
//                    }
//                }
//            };
//            ZBRouterStatus = true ;
//            THEROOM.getWiredZBGateway().getSubDevList(new ITuyaDataCallback<List<DeviceBean>>() {
//                @Override
//                public void onSuccess(List<DeviceBean> result)
//                {
//                    zigbeeDevices = result ;
//                    try
//                    {
//                        if (zigbeeDevices != null && zigbeeDevices.size()>0)
//                        {
//                            for (int i=0;i<zigbeeDevices.size();i++) {
//                                if (zigbeeDevices.get(i).getName().equals(MyApp.Room.RoomNumber+"DoorSensor")) {
//                                    DoorSensorStatus = true ;
//                                    DoorSensorBean = new DeviceBean();
//                                    DoorSensorBean = zigbeeDevices.get(i) ;
//                                    THEROOM.setDOORSENSOR_B(zigbeeDevices.get(i));
//                                    DoorSensor = TuyaHomeSdk.newDeviceInstance(DoorSensorBean.getDevId());
//                                    THEROOM.setDOORSENSOR(TuyaHomeSdk.newDeviceInstance(DoorSensorBean.getDevId()));
//                                    DoorSensor.registerDeviceListener(new IDeviceListener() {
//                                        @Override
//                                        public void onDpUpdate(String devId, Map<String, Object> dpStr)
//                                        {
//                                            //Log.d("DoorS" , dpStr.toString() + " "+ THEROOM.getPOWER_B().dps.get("2").toString()+" "+CURRENT_ROOM_STATUS);
//                                            if (dpStr.get("doorcontact_state") != null )
//                                            {
//
//                                                if (dpStr.get("doorcontact_state").toString().equals("true") )
//                                                {
//                                                    ToastMaker.MakeToast("Door is Open" , act);
//                                                    setDoorOpenClosed("1");
//                                                    myRefDoor.setValue("1");
//                                                    thermostatStartTime = System.currentTimeMillis() ;
//                                                    theDoorStartTime = System.currentTimeMillis() ;
//                                                    xxx[0] = true ;
//                                                    D[0] = true ;
//                                                    theThermoPeriod = 0 ;
//                                                    theDoorPeriod = 0 ;
//                                                    TempRonnable.run();
//                                                    DoorRunnable.run();
//                                                    if (THEROOM.getPOWER_B() != null )
//                                                    {
//                                                        Log.d("DpUpdates",THEROOM.getPOWER_B().dps.get("1").toString()+" "+ THEROOM.getPOWER_B().dps.get("2").toString()+" "+CURRENT_ROOM_STATUS+ " "+AutoLightOn);
//                                                        if (THEROOM.getPOWER_B().dps.get("1").toString().equals("true") && THEROOM.getPOWER_B().dps.get("2").toString().equals("false") && CURRENT_ROOM_STATUS == 2 && !AutoLightOn ) //&& WelcomeLight == 0
//                                                        {
//                                                            String g = "60";
//                                                            WelcomeLight = 1 ;
//                                                            THEROOM.getPOWER().publishDps("{\"1\": true}", new IResultCallback() {
//                                                                @Override
//                                                                public void onError(String code, String error) {
//                                                                    //Toast.makeText(act, error, Toast.LENGTH_SHORT).show();
//                                                                    Log.e("light", error);
//                                                                }
//
//                                                                @Override
//                                                                public void onSuccess() {
//                                                                    //Toast.makeText(act, "turn on the light success", Toast.LENGTH_SHORT).show();
//                                                                    //myRefPower.setValue(1);
//                                                                }
//                                                            });
//                                                            THEROOM.getPOWER().publishDps("{\"2\": true}", new IResultCallback() {
//                                                                @Override
//                                                                public void onError(String code, String error) {
//                                                                    //Toast.makeText(act, "turn on the light failure", Toast.LENGTH_SHORT).show();
//                                                                }
//
//                                                                @Override
//                                                                public void onSuccess() {
//                                                                    //Toast.makeText(act, "turn on the light success", Toast.LENGTH_SHORT).show();
//                                                                }
//                                                            });
//                                                            THEROOM.getPOWER().publishDps("{\"8\": "+g+"}", new IResultCallback() {
//                                                                @Override
//                                                                public void onError(String code, String error) {
//                                                                    //Toast.makeText(act, "turn on the light failure", Toast.LENGTH_SHORT).show();
//                                                                }
//
//                                                                @Override
//                                                                public void onSuccess() {
//                                                                    //Toast.makeText(act, "turn on the light success", Toast.LENGTH_SHORT).show();
//                                                                    Log.d("LightWithWelcome" , "countdoun from door sent");
//                                                                }
//                                                            });
//                                                            Log.d("LightWithWelcome" , THEROOM.getPOWER_B().dps.toString());
//                                                            if (Switch1 != null ){
//                                                                Thread t = new Thread(new Runnable() {
//                                                                    @Override
//                                                                    public void run() {
//                                                                        try {
//                                                                            Thread.sleep(60*1000);
//                                                                            if (THEROOM.getSWITCH3_B() != null ) {
//                                                                                List<Object> keys = new ArrayList<Object>(THEROOM.getSWITCH3_B().getDps().keySet());
//                                                                                for (int i = 0 ; i < keys.size() ; i++) {
//                                                                                    String x = keys.get(i).toString();
//                                                                                    TuyaHomeSdk.newDeviceInstance(THEROOM.getSWITCH3_B().devId).publishDps("{\""+x+"\": false}", new IResultCallback() {
//                                                                                        @Override
//                                                                                        public void onError(String code, String error) {
//
//                                                                                        }
//
//                                                                                        @Override
//                                                                                        public void onSuccess() {
//
//                                                                                        }
//                                                                                    });
//                                                                                }
//                                                                            }
//                                                                            else {
//                                                                                List<Object> keys = new ArrayList<Object>(THEROOM.getSWITCH2_B().getDps().keySet());
//                                                                                for (int i = 0 ; i < keys.size() ; i++) {
//                                                                                    String x = keys.get(i).toString();
//                                                                                    TuyaHomeSdk.newDeviceInstance(THEROOM.getSWITCH2_B().devId).publishDps("{\""+x+"\": false}", new IResultCallback() {
//                                                                                        @Override
//                                                                                        public void onError(String code, String error) {
//
//                                                                                        }
//
//                                                                                        @Override
//                                                                                        public void onSuccess() {
//
//                                                                                        }
//                                                                                    });
//                                                                                }
//                                                                            }
////                                                                            Switch1.publishDps("{\"1\": false}", new IResultCallback() {
////                                                                                @Override
////                                                                                public void onError(String code, String error) {
////                                                                                    Log.d("LightWithWelcome" , error);
////                                                                                }
////
////                                                                                @Override
////                                                                                public void onSuccess() {
////                                                                                    Log.d("LightWithWelcome" , "Light is on ");
////                                                                                }
////                                                                            });
//                                                                        } catch (InterruptedException e) {
//                                                                            e.printStackTrace();
//                                                                        }
//
//                                                                    }
//                                                                });
////                                                                Switch1.publishDps("{\"1\": true}", new IResultCallback() {
////                                                                    @Override
////                                                                    public void onError(String code, String error) {
////                                                                        Log.d("LightWithWelcome" , error);
////                                                                    }
////
////                                                                    @Override
////                                                                    public void onSuccess() {
////                                                                        Log.d("LightWithWelcome" , "Light is on ");
////                                                                        t.start();
////                                                                    }
////                                                                });
//                                                                if (THEROOM.getSWITCH3_B() != null ) {
//                                                                    List<Object> keys = new ArrayList<Object>(THEROOM.getSWITCH3_B().getDps().keySet());
//                                                                    for (int i = 0 ; i < keys.size() ; i++) {
//                                                                        String x = keys.get(i).toString();
//                                                                        TuyaHomeSdk.newDeviceInstance(THEROOM.getSWITCH3_B().devId).publishDps("{\""+x+"\": true}", new IResultCallback() {
//                                                                            @Override
//                                                                            public void onError(String code, String error) {
//
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onSuccess() {
//
//                                                                            }
//                                                                        });
//                                                                    }
//                                                                    t.start();
//                                                                }
//                                                                else {
//                                                                    List<Object> keys = new ArrayList<Object>(THEROOM.getSWITCH2_B().getDps().keySet());
//                                                                    for (int i = 0 ; i < keys.size() ; i++) {
//                                                                        String x = keys.get(i).toString();
//                                                                        TuyaHomeSdk.newDeviceInstance(THEROOM.getSWITCH2_B().devId).publishDps("{\""+x+"\": true}", new IResultCallback() {
//                                                                            @Override
//                                                                            public void onError(String code, String error) {
//
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onSuccess() {
//
//                                                                            }
//                                                                        });
//                                                                    }
//                                                                    t.start();
//                                                                }
//
//
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                                else
//                                                {
//                                                    setDoorOpenClosed("0");
//                                                    myRefDoor.setValue("0");
//                                                    D[0] = false ;
//                                                    ToastMaker.MakeToast("Door Closed" , act);
//                                                }
//                                            }
//
//                                        }
//                                        @Override
//                                        public void onRemoved(String devId) {
//
//                                        }
//
//                                        @Override
//                                        public void onStatusChanged(String devId, boolean online)
//                                        {
//                                            Log.d("DoorS" , String.valueOf( online));
//                                        }
//                                        @Override
//                                        public void onNetworkStatusChanged(String devId, boolean status)
//                                        {
//
//                                        }
//                                        @Override
//                                        public void onDevInfoUpdate(String devId)
//                                        {
//                                            Log.d("DoorS" , devId );
//                                        }
//                                    });
//                                }
//                                else if (zigbeeDevices.get(i).getName().equals(MyApp.Room.RoomNumber+"MotionSensor")) {
//                                    MotionSensorStatus = true ;
//                                    MotionSensorBean = new DeviceBean();
//                                    MotionSensorBean = zigbeeDevices.get(i) ;
//                                    THEROOM.setMOTIONSENSOR_B(zigbeeDevices.get(i));
//                                    MotionSensor = TuyaHomeSdk.newDeviceInstance(MotionSensorBean.getDevId());
//                                    THEROOM.setMOTIONSENSOR(TuyaHomeSdk.newDeviceInstance(MotionSensorBean.getDevId()));
//                                    MotionSensor.publishDps("{\" 10\": \"30s\"}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//
//                                        @Override
//                                        public void onSuccess()
//                                        {
//                                            //ToastMaker.MakeToast("MotionSensor Time Updated" , act);
//                                            Log.d("MotionData",MotionSensorBean.dps.toString()+" "+MotionSensorBean.getDpCodes().toString());
//                                        }
//                                    });
//                                    MotionSensor.registerDeviceListener(new IDeviceListener() {
//                                        @Override
//                                        public void onDpUpdate(String devId, Map<String, Object> dpStr)
//                                        {
//                                            Log.d("Motion" , dpStr.toString() ) ;
//
//                                            if (xxx[0])
//                                            {
//                                                xxx[0] = false ;
//                                            }
//                                            else
//                                            {
//                                                String t ="";
//                                                if (ClientTemp.equals("0"))
//                                                {
//                                                    t="240";
//                                                }
//                                                else
//                                                {
//                                                    t = ClientTemp ;
//                                                }
//                                                String dp = "{\" 2\": "+t+"}";
//
//                                                Tuya_Devices.AC.publishDps(dp, new IResultCallback() {
//                                                    @Override
//                                                    public void onError(String code, String error) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onSuccess()
//                                                    {
//                                                        //ToastMaker.MakeToast("Temp Set to Client Temp " , act);
//
//                                                    }
//                                                });
//                                            }
//
//
//                                        }
//                                        @Override
//                                        public void onRemoved(String devId)
//                                        {
//
//                                        }
//                                        @Override
//                                        public void onStatusChanged(String devId, boolean online)
//                                        {
//                                            Log.d("Motion" ,String.valueOf( online) ) ;
//                                        }
//                                        @Override
//                                        public void onNetworkStatusChanged(String devId, boolean status)
//                                        {
//                                            Log.d("Motion" , String.valueOf( status) ) ;
//                                        }
//                                        @Override
//                                        public void onDevInfoUpdate(String devId) {
//
//                                        }
//                                    });
//                                }
//                                else if (zigbeeDevices.get(i).getName().equals(MyApp.Room.RoomNumber+"Curtain")) {
//                                    CurtainControllerStatus = true ;
//                                    CurtainBean = new DeviceBean();
//                                    CurtainBean = zigbeeDevices.get(i) ;
//                                    THEROOM.setCURTAIN_B(zigbeeDevices.get(i));
//                                    Curtain = TuyaHomeSdk.newDeviceInstance(CurtainBean.getDevId());
//                                    THEROOM.setCURTAIN(TuyaHomeSdk.newDeviceInstance(CurtainBean.getDevId()));
//                                }
//                                else if (zigbeeDevices.get(i).getName().equals(MyApp.Room.RoomNumber+"ServiceSwitch")) {
//                                    ServiceSwitchStatus = true ;
//                                    ServiceSwitch = new DeviceBean();
//                                    ServiceSwitch = zigbeeDevices.get(i) ;
//                                    THEROOM.setSERVICE1_B(zigbeeDevices.get(i));
//                                    ServiceS = TuyaHomeSdk.newDeviceInstance(ServiceSwitch.getDevId());
//                                    THEROOM.setSERVICE1(TuyaHomeSdk.newDeviceInstance(ServiceSwitch.getDevId()));
//                                    Log.d("serviceSwitch" , THEROOM.getSERVICE1_B().getDps().keySet().toString());
//                                    List keys = new ArrayList(THEROOM.getSERVICE1_B().getDps().keySet());
//                                    if (keys.contains("1") && keys.contains("2") && keys.contains("3") && keys.contains("4")) {
//                                        Log.d("serviceSwitch" , "4 Switch" );
//                                        THEROOM.getSERVICE1().registerDeviceListener(new IDeviceListener() {
//                                            @Override
//                                            public void onDpUpdate(String devId, Map<String, Object> dpStr) {
//                                                Log.d("serviceSwitch" , dpStr.toString() );
//                                                //DND
//                                                if (dpStr.get("switch_1") != null) {
//                                                    if (!DNDStatus && dpStr.get("switch_1").toString().equals("true")) {
//                                                        if (CURRENT_ROOM_STATUS == 2) {
//                                                        String dep = "DND";
//                                                        Calendar x = Calendar.getInstance(Locale.getDefault());
//                                                        long timee = x.getTimeInMillis();
//                                                        myRefDND.setValue(timee);
//                                                        myRefdep.setValue("DND");
//                                                        dndOn();
//                                                        DNDStatus = true;
//                                                        //turning dnd on
//                                                        if (THEROOM.getSERVICE1_B() != null) {
//                                                            if (THEROOM.getSERVICE1_B().dps.get("1") != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("1").toString().equals("false")) {
//                                                                    THEROOM.getSERVICE1().publishDps("{\"1\": true}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//
//                                                        }
//                                                        //turning cleanup off
//                                                        if (THEROOM.getSERVICE1_B() != null) {
//                                                            if (THEROOM.getSERVICE1_B().dps.get("2") != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("2").toString().equals("true")) {
//                                                                    THEROOM.getSERVICE1().publishDps("{\"2\": false}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//
//                                                        }
//                                                        //turning laundry off
//                                                        if (THEROOM.getSERVICE1_B() != null) {
//                                                            if (THEROOM.getSERVICE1_B().dps.get("3") != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("3").toString().equals("true")) {
//                                                                    THEROOM.getSERVICE1().publishDps("{\"3\": false}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//
//                                                        }
//                                                        //turning checkout off
//                                                        if (THEROOM.getSERVICE1_B() != null) {
//                                                            if (THEROOM.getSERVICE1_B().dps.get("4") != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("4").toString().equals("true")) {
//                                                                    THEROOM.getSERVICE1().publishDps("{\"4\": false}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//
//                                                        }
//                                                        if (RoomServiceStatus) {
//                                                            removeRoomServiceOrderInDataBase();
//                                                        }
//                                                        StringRequest request = new StringRequest(Request.Method.POST, insertServiceOrderUrl, new Response.Listener<String>() {
//                                                            @Override
//                                                            public void onResponse(String response) {
//                                                                try {
//                                                                    Log.e("DND", response);
//                                                                    if (Integer.parseInt(response) > 0) {
//                                                                        //loading.stop();
//                                                                        dndId = Integer.parseInt(response);
//                                                                    }
//                                                                } catch (Exception e) {
//                                                                    Log.e("DND", e.getMessage());
//                                                                }
//
//                                                            }
//                                                        }
//                                                                , new Response.ErrorListener() {
//                                                            @Override
//                                                            public void onErrorResponse(VolleyError error) {
//                                                                //Log.e("DNDerror", error.getMessage());
//                                                            }
//                                                        }) {
//                                                            @Override
//                                                            protected Map<String, String> getParams() throws AuthFailureError {
//                                                                Map<String, String> params = new HashMap<String, String>();
//                                                                params.put("roomNumber", String.valueOf(MyApp.Room.RoomNumber));
//                                                                params.put("time", String.valueOf(timee));
//                                                                params.put("dep", dep);
//                                                                params.put("Hotel", "1");
//                                                                params.put("RorS", String.valueOf(RoomOrSuite));
//                                                                params.put("Reservation", String.valueOf(RESERVATION));
//                                                                return params;
//                                                            }
//                                                        };
//                                                        Volley.newRequestQueue(act).add(request);
//                                                    }
//                                                    }
//                                                    else if (DNDStatus && dpStr.get("switch_1").toString().equals("false")) {
//                                                        if (CURRENT_ROOM_STATUS == 2) {
//                                                            String dep = "DND";
//                                                            myRefDND.setValue(0);
//                                                            dndOff();
//                                                            //LoadingDialog loading = new LoadingDialog(act);
//                                                            DNDStatus = false;
//                                                            if (THEROOM.getSERVICE1_B() != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("1") != null) {
//                                                                    if (THEROOM.getSERVICE1_B().dps.get("1").toString().equals("true")) {
//                                                                        THEROOM.getSERVICE1().publishDps("{\"1\":false}", new IResultCallback() {
//                                                                            @Override
//                                                                            public void onError(String code, String error) {
//
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onSuccess() {
//
//                                                                            }
//                                                                        });
//                                                                    }
//                                                                }
//
//                                                            }
//                                                            StringRequest rrr = new StringRequest(Request.Method.POST, removeServiceOrderUrl, new Response.Listener<String>() {
//                                                                @Override
//                                                                public void onResponse(String response) {
//                                                                    if (response.equals("1")) {
//                                                                        //loading.stop();
//                                                                    }
//
//                                                                }
//                                                            }, new Response.ErrorListener() {
//                                                                @Override
//                                                                public void onErrorResponse(VolleyError error) {
//
//                                                                }
//                                                            }) {
//                                                                @Override
//                                                                protected Map<String, String> getParams() throws AuthFailureError {
//                                                                    Map<String, String> params = new HashMap<String, String>();
//                                                                    params.put("id", String.valueOf(dndId));
//                                                                    params.put("room", String.valueOf(MyApp.Room.RoomNumber));
//                                                                    params.put("dep", dep);
//                                                                    params.put("Hotel", "1");
//                                                                    return params;
//                                                                }
//                                                            };
//                                                            Volley.newRequestQueue(act).add(rrr);
//                                                        }
//                                                    }
//                                                }
//                                                //Cleanup
//                                                if (dpStr.get("switch_2") != null) {
//                                                    if (!CleanupStatus && dpStr.get("switch_2").toString().equals("true")) {
//                                                        if (CURRENT_ROOM_STATUS == 2) {
//                                                            CleanupStatus = true ;
//                                                            String dep = "Cleanup";
//                                                            Calendar x = Calendar.getInstance(Locale.getDefault());
//                                                            long timee =  x.getTimeInMillis();
//                                                            addCleanupOrderInDataBase();
//                                                            myRefCleanup.setValue(timee);
//                                                            myRefdep.setValue(dep);
//                                                            myRefDND.setValue(0);
//                                                            for(ServiceEmps emp : Emps) {
//                                                                if (emp.department.equals("Service") || emp.department.equals("Cleanup")) {
//                                                                    emp.makemessage(emp.token,"Cleanup",true,act);
//                                                                }
//                                                            }
//                                                            //turning cleanup on
//                                                            if (THEROOM.getSERVICE1_B() != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("2") != null) {
//                                                                    if (THEROOM.getSERVICE1_B().dps.get("2").toString().equals("false")) {
//                                                                        THEROOM.getSERVICE1().publishDps("{\"2\": true}", new IResultCallback() {
//                                                                            @Override
//                                                                            public void onError(String code, String error) {
//
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onSuccess() {
//
//                                                                            }
//                                                                        });
//                                                                    }
//                                                                }
//
//                                                            }
//                                                            //turning dnd off
//                                                            if (THEROOM.getSERVICE1_B() != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("1") != null) {
//                                                                    if (THEROOM.getSERVICE1_B().dps.get("1").toString().equals("true")) {
//                                                                        THEROOM.getSERVICE1().publishDps("{\"1\": false}", new IResultCallback() {
//                                                                            @Override
//                                                                            public void onError(String code, String error) {
//
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onSuccess() {
//
//                                                                            }
//                                                                        });
//                                                                    }
//                                                                }
//
//                                                            }
//                                                        }
//                                                    }
//                                                    else if (CleanupStatus && dpStr.get("switch_2").toString().equals("false")) {
//                                                        if (CURRENT_ROOM_STATUS == 2) {
//                                                            CleanupStatus = false ;
//                                                            myRefCleanup.setValue(0);
//                                                            cleanupOff();
//                                                            removeCleanupOrderInDataBase();
//                                                            for(ServiceEmps emp : Emps) {
//                                                                if (emp.department.equals("Service") || emp.department.equals("Cleanup")) {
//                                                                    emp.makemessage(emp.token,"Cleanup",false,act);
//                                                                }
//                                                            }
//                                                            if (THEROOM.getSERVICE1_B() != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("2") != null) {
//                                                                    if (THEROOM.getSERVICE1_B().dps.get("2").toString().equals("true")) {
//                                                                        THEROOM.getSERVICE1().publishDps("{\"2\": false}", new IResultCallback() {
//                                                                            @Override
//                                                                            public void onError(String code, String error) {
//
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onSuccess() {
//
//                                                                            }
//                                                                        });
//                                                                    }
//                                                                }
//
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                                //Laundry
//                                                if (dpStr.get("switch_3") != null) {
//                                                    if (!LaundryStatus && dpStr.get("switch_3").toString().equals("true")) {
//                                                        if (CURRENT_ROOM_STATUS == 2) {
//                                                            final String dep = "Laundry";
//                                                            Calendar x = Calendar.getInstance(Locale.getDefault());
//                                                            long timee = x.getTimeInMillis();
//                                                            LaundryStatus = true;
//                                                            myRefLaundry.setValue(timee);
//                                                            myRefdep.setValue(dep);
//                                                            myRefDND.setValue(0);
//                                                            laundryOn();
//                                                            for(ServiceEmps emp : Emps) {
//                                                                if (emp.department.equals("Service") || emp.department.equals("Cleanup")) {
//                                                                    emp.makemessage(emp.token,"Laundry",true,act);
//                                                                }
//                                                            }
//                                                            //turning laundry on
//                                                            if (THEROOM.getSERVICE1_B() != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("3") != null) {
//                                                                    if (THEROOM.getSERVICE1_B().dps.get("3").toString().equals("false")) {
//                                                                        THEROOM.getSERVICE1().publishDps("{\"3\": true}", new IResultCallback() {
//                                                                            @Override
//                                                                            public void onError(String code, String error) {
//
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onSuccess() {
//
//                                                                            }
//                                                                        });
//                                                                    }
//                                                                }
//
//                                                            }
//                                                            //turning dnd off
//                                                            if (THEROOM.getSERVICE1_B() != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("1") != null) {
//                                                                    if (THEROOM.getSERVICE1_B().dps.get("1").toString().equals("true")) {
//                                                                        THEROOM.getSERVICE1().publishDps("{\"1\": false}", new IResultCallback() {
//                                                                            @Override
//                                                                            public void onError(String code, String error) {
//
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onSuccess() {
//
//                                                                            }
//                                                                        });
//                                                                    }
//                                                                }
//
//                                                            }
//                                                            //LoadingDialog loading = new LoadingDialog(act);
//                                                            StringRequest addOrder = new StringRequest(Request.Method.POST, insertServiceOrderUrl, new Response.Listener<String>() {
//                                                                @Override
//                                                                public void onResponse(String response) {
//                                                                    //loading.stop();
//                                                                    if (Integer.parseInt(response) > 0) {
//                                                                        ToastMaker.MakeToast(dep + " Order Sent Successfully", act);
//                                                                        laundryOrderId = Integer.parseInt(response);
//                                                                    } else {
//                                                                        Toast.makeText(act, response, Toast.LENGTH_LONG).show();
//                                                                    }
//
//                                                                }
//                                                            }, new Response.ErrorListener() {
//                                                                @Override
//                                                                public void onErrorResponse(VolleyError error) {
//                                                                    //loading.stop();
//                                                                }
//                                                            }) {
//                                                                @Override
//                                                                protected Map<String, String> getParams() throws AuthFailureError {
//                                                                    Map<String, String> params = new HashMap<String, String>();
//                                                                    params.put("roomNumber", String.valueOf(MyApp.Room.RoomNumber));
//                                                                    params.put("time", String.valueOf(timee));
//                                                                    params.put("dep", dep);
//                                                                    params.put("Hotel", "1");
//                                                                    params.put("RorS", String.valueOf(RoomOrSuite));
//                                                                    params.put("Reservation", String.valueOf(RESERVATION));
//                                                                    return params;
//                                                                }
//
//                                                            };
//                                                            Volley.newRequestQueue(act).add(addOrder);
//                                                        }
//                                                    }
//                                                    else if (LaundryStatus && dpStr.get("switch_3").toString().equals("false")) {
//                                                        if (CURRENT_ROOM_STATUS == 2) {
//                                                            myRefLaundry.setValue(0);
//                                                            laundryOff();
//                                                            myRefLaundry.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                                @Override
//                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                                    if (Long.parseLong(snapshot.getValue().toString()) > 0) {
//                                                                        laundryOrderId = Long.parseLong(snapshot.getValue().toString());
//                                                                    }
//                                                                }
//
//                                                                @Override
//                                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                                }
//                                                            });
//                                                            final String dep = "Laundry";
//                                                            LaundryStatus = false;
//                                                            if (THEROOM.getSERVICE1_B() != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("3") != null) {
//                                                                    if (THEROOM.getSERVICE1_B().dps.get("3").toString().equals("true")) {
//                                                                        THEROOM.getSERVICE1().publishDps("{\"3\": false}", new IResultCallback() {
//                                                                            @Override
//                                                                            public void onError(String code, String error) {
//
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onSuccess() {
//
//                                                                            }
//                                                                        });
//                                                                    }
//                                                                }
//
//                                                            }
//                                                            for(ServiceEmps emp : Emps) {
//                                                                if (emp.department.equals("Service") || emp.department.equals("Cleanup")) {
//                                                                    emp.makemessage(emp.token,"Laundry",false,act);
//                                                                }
//                                                            }
//                                                            //LoadingDialog loading = new LoadingDialog(act);
//                                                            StringRequest removOrder = new StringRequest(Request.Method.POST, removeServiceOrderUrl, new Response.Listener<String>() {
//                                                                @Override
//                                                                public void onResponse(String response) {
//                                                                    //loading.stop();
//                                                                    if (response.equals("1")) {
//
//                                                                        ToastMaker.MakeToast(dep + " Order Cancelled", act);
//                                                                    } else {
//                                                                        //Toast.makeText(act , response,Toast.LENGTH_LONG).show();
//                                                                    }
//
//                                                                }
//                                                            }, new Response.ErrorListener() {
//                                                                @Override
//                                                                public void onErrorResponse(VolleyError error) {
//                                                                    //loading.stop();
//                                                                    // Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
//                                                                }
//                                                            }) {
//                                                                @Override
//                                                                protected Map<String, String> getParams() throws AuthFailureError {
//                                                                    Map<String, String> params = new HashMap<String, String>();
//                                                                    params.put("id", String.valueOf(laundryOrderId));
//                                                                    params.put("room", String.valueOf(MyApp.Room.RoomNumber));
//                                                                    params.put("dep", dep);
//                                                                    params.put("Hotel", "1");
//                                                                    return params;
//                                                                }
//                                                            };
//                                                            Volley.newRequestQueue(act).add(removOrder);
//                                                        }
//                                                    }
//                                                }
//                                                //Checkout
//                                                if (dpStr.get("switch_4") != null) {
//                                                    if (!CheckoutStatus && dpStr.get("switch_4").toString().equals("true")) {
//                                                        if (CURRENT_ROOM_STATUS == 2) {
//                                                            final String dep = "Checkout";
//                                                            Calendar x = Calendar.getInstance(Locale.getDefault());
//                                                            long timee = x.getTimeInMillis();
//                                                            CheckoutStatus = true;
//                                                            myRefCheckout.setValue(timee);
//                                                            myRefdep.setValue(dep);
//                                                            myRefDND.setValue(0);
//                                                            checkoutOn();
//                                                            //turning checkout on
//                                                            if (THEROOM.getSERVICE1_B() != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("4") != null) {
//                                                                    if (THEROOM.getSERVICE1_B().dps.get("4").toString().equals("false")) {
//                                                                        THEROOM.getSERVICE1().publishDps("{\"4\": true}", new IResultCallback() {
//                                                                            @Override
//                                                                            public void onError(String code, String error) {
//
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onSuccess() {
//
//                                                                            }
//                                                                        });
//                                                                    }
//                                                                }
//
//                                                            }
//                                                            //turning dnd off
//                                                            if (THEROOM.getSERVICE1_B() != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("1") != null) {
//                                                                    if (THEROOM.getSERVICE1_B().dps.get("1").toString().equals("true")) {
//                                                                        THEROOM.getSERVICE1().publishDps("{\"1\": false}", new IResultCallback() {
//                                                                            @Override
//                                                                            public void onError(String code, String error) {
//
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onSuccess() {
//
//                                                                            }
//                                                                        });
//                                                                    }
//                                                                }
//
//                                                            }
//                                                            //LoadingDialog loading = new LoadingDialog(act);
//                                                            StringRequest request = new StringRequest(Request.Method.POST, insertServiceOrderUrl, new Response.Listener<String>() {
//                                                                @Override
//                                                                public void onResponse(String response) {
//                                                                    //loading.stop();
//                                                                    if (response.equals("0")) {
//
//                                                                    } else {
//                                                                        ToastMaker.MakeToast(dep + " Order Sent Successfully", act);
//                                                                        checkOutId = Integer.parseInt(response);
//                                                                        Dialog RatingDialog = new Dialog(act);
//                                                                        RatingDialog.setContentView(R.layout.rating_dialog);
//                                                                        RatingDialog.setCancelable(false);
//                                                                        Button sendRating = (Button) RatingDialog.findViewById(R.id.sendRatingButton);
//                                                                        RatingBar RatingD = (RatingBar) RatingDialog.findViewById(R.id.ratingBar);
//                                                                        final String[] RATING = {""};
//                                                                        RatingD.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//                                                                            @Override
//                                                                            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                                                                                //ToastMaker.MakeToast(String.valueOf(rating),act);
//                                                                                RATING[0] = String.valueOf(rating);
//                                                                            }
//                                                                        });
//                                                                        sendRating.setOnClickListener(new View.OnClickListener() {
//                                                                            @Override
//                                                                            public void onClick(View v) {
//                                                                                RatingDialog.dismiss();
//                                                                                LoadingDialog loading = new LoadingDialog(act);
//                                                                                //ToastMaker.MakeToast(RATING[0],act);
//                                                                                String url = LogIn.URL + "insertRating.php";
//                                                                                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//                                                                                    @Override
//                                                                                    public void onResponse(String response) {
//                                                                                        loading.stop();
//
//                                                                                    }
//                                                                                }, new Response.ErrorListener() {
//                                                                                    @Override
//                                                                                    public void onErrorResponse(VolleyError error) {
//                                                                                        loading.stop();
//                                                                                    }
//                                                                                }) {
//                                                                                    @Override
//                                                                                    protected Map<String, String> getParams() throws AuthFailureError {
//                                                                                        Map<String, String> Params = new HashMap<String, String>();
//                                                                                        Params.put("Reservation", String.valueOf(RESERVATION));
//                                                                                        Params.put("Rating", RATING[0]);
//                                                                                        return Params;
//                                                                                    }
//                                                                                };
//                                                                                Volley.newRequestQueue(act).add(request);
//                                                                            }
//                                                                        });
//                                                                        RatingDialog.show();
//                                                                    }
//
//                                                                }
//                                                            }, new Response.ErrorListener() {
//                                                                @Override
//                                                                public void onErrorResponse(VolleyError error) {
//                                                                    //loading.stop();
//                                                                }
//                                                            }) {
//                                                                @Override
//                                                                protected Map<String, String> getParams() throws AuthFailureError {
//                                                                    Map<String, String> params = new HashMap<String, String>();
//                                                                    params.put("roomNumber", String.valueOf(MyApp.Room.RoomNumber));
//                                                                    params.put("time", String.valueOf(timee));
//                                                                    params.put("dep", dep);
//                                                                    params.put("Hotel", "1");
//                                                                    params.put("RorS", String.valueOf(RoomOrSuite));
//                                                                    params.put("Reservation", String.valueOf(RESERVATION));
//                                                                    return params;
//                                                                }
//                                                            };
//                                                            Volley.newRequestQueue(act).add(request);
//                                                    }
//                                                    }
//                                                    else if (CheckoutStatus && dpStr.get("switch_4").toString().equals("false")) {
//                                                        if (CURRENT_ROOM_STATUS == 2) {
//                                                            Log.d("checkoutProblem", " from here " + CheckoutStatus + dpStr.get("switch_4").toString());
//                                                            final String dep = "Checkout";
//                                                            checkoutOff();
//                                                            myRefCheckout.setValue(0);
//                                                            CheckoutStatus = false;
//                                                            //LoadingDialog loading = new LoadingDialog(act);
//                                                            StringRequest re = new StringRequest(Request.Method.POST, removeServiceOrderUrl, new Response.Listener<String>() {
//                                                                @Override
//                                                                public void onResponse(String response) {
//                                                                    //loading.stop();
//                                                                    ToastMaker.MakeToast(dep + " Order Cancelled", act);
//                                                                }
//                                                            }, new Response.ErrorListener() {
//                                                                @Override
//                                                                public void onErrorResponse(VolleyError error) {
//                                                                    //d.dismiss();
//                                                                    //loading.stop();
//                                                                }
//                                                            }) {
//                                                                @Override
//                                                                protected Map<String, String> getParams() throws AuthFailureError {
//                                                                    Map<String, String> params = new HashMap<String, String>();
//                                                                    params.put("id", String.valueOf(checkOutId));
//                                                                    params.put("room", String.valueOf(MyApp.Room.RoomNumber));
//                                                                    params.put("dep", dep);
//                                                                    params.put("Hotel","1");
//                                                                    return params;
//                                                                }
//                                                            };
//                                                            Volley.newRequestQueue(act).add(re);
//                                                        }
//                                                    }
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onRemoved(String devId) {
//
//                                            }
//
//                                            @Override
//                                            public void onStatusChanged(String devId, boolean online) {
//
//                                            }
//
//                                            @Override
//                                            public void onNetworkStatusChanged(String devId, boolean status) {
//
//                                            }
//
//                                            @Override
//                                            public void onDevInfoUpdate(String devId) {
//
//                                            }
//                                        });
//                                    }
//                                    else if (keys.contains("1") && keys.contains("2") && keys.contains("3")) {
//                                        Log.d("serviceSwitch" , "3 Switch" );
//                                        THEROOM.getSERVICE1().registerDeviceListener(new IDeviceListener() {
//                                            @Override
//                                            public void onDpUpdate(String devId, Map<String, Object> dpStr) {
//                                                Log.d("serviceSwitch" , dpStr.toString() );
//                                                //Cleanup
//                                                if (dpStr.get("switch_1") != null) {
//                                                    if (!CleanupStatus && dpStr.get("switch_1").toString().equals("true")) {
//                                                        if (CURRENT_ROOM_STATUS == 2) {
//                                                        addCleanupOrderInDataBaseFor3Switch();
//                                                        CleanupStatus = true;
//                                                        //turning cleanup on
//                                                        if (THEROOM.getSERVICE1_B() != null) {
//                                                            if (THEROOM.getSERVICE1_B().dps.get("1") != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("1").toString().equals("false")) {
//                                                                    THEROOM.getSERVICE1().publishDps("{\"1\": true}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//
//                                                        }
//                                                        //turning dnd off
//                                                        if (THEROOM.getSERVICE1_B() != null) {
//                                                            if (THEROOM.getSERVICE1_B().dps.get("3") != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("3").toString().equals("true")) {
//                                                                    THEROOM.getSERVICE1().publishDps("{\"3\": false}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//
//                                                        }
//                                                    }
//                                                    }
//                                                    else if (CleanupStatus && dpStr.get("switch_1").toString().equals("false")) {
//                                                        if (CURRENT_ROOM_STATUS == 2) {
//                                                        removeCleanupOrderInDataBaseFor3Switch();
//                                                        CleanupStatus = false;
//                                                        if (THEROOM.getSERVICE1_B() != null) {
//                                                            if (THEROOM.getSERVICE1_B().dps.get("1") != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("1").toString().equals("true")) {
//                                                                    THEROOM.getSERVICE1().publishDps("{\"1\": false}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//
//                                                        }
//                                                    }
//                                                    }
//                                                }
//                                                //Laundry
//                                                if (dpStr.get("switch_2") != null) {
//                                                    Log.d("serviceSwitch" , String.valueOf(LaundryStatus) + " "+ dpStr.get("switch_2").toString());
//                                                    if (!LaundryStatus && dpStr.get("switch_2").toString().equals("true")) {
//                                                        if (CURRENT_ROOM_STATUS == 2) {
//                                                        addLaundryOrderInDataBaseFor3Switch();
//                                                        LaundryStatus = true;
//                                                        //turning laundry on
//                                                        if (THEROOM.getSERVICE1_B() != null) {
//                                                            if (THEROOM.getSERVICE1_B().dps.get("2") != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("2").toString().equals("false")) {
//                                                                    THEROOM.getSERVICE1().publishDps("{\"2\": true}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//
//                                                        }
//                                                        //turning dnd off
//                                                        if (THEROOM.getSERVICE1_B() != null) {
//                                                            if (THEROOM.getSERVICE1_B().dps.get("3") != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("3").toString().equals("true")) {
//                                                                    THEROOM.getSERVICE1().publishDps("{\"3\": false}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//
//                                                        }
//                                                    }
//                                                    }
//                                                    else if (LaundryStatus && dpStr.get("switch_2").toString().equals("false")) {
//                                                        if (CURRENT_ROOM_STATUS == 2) {
//                                                        removeLaundryOrderInDataBaseFor3Switch();
//                                                        LaundryStatus = false;
//                                                        if (THEROOM.getSERVICE1_B() != null) {
//                                                            if (THEROOM.getSERVICE1_B().dps.get("2") != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("2").toString().equals("true")) {
//                                                                    THEROOM.getSERVICE1().publishDps("{\"2\": false}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//
//                                                        }
//                                                    }
//                                                    }
//                                                }
//                                                //DND
//                                                if (dpStr.get("switch_3") != null) {
//                                                    if (!DNDStatus && dpStr.get("switch_3").toString().equals("true")) {
//                                                        if (CURRENT_ROOM_STATUS == 2) {
//                                                        String dep = "DND";
//                                                        Calendar x = Calendar.getInstance(Locale.getDefault());
//                                                        long timee = x.getTimeInMillis();
//                                                        //LoadingDialog loading = new LoadingDialog(act);
//                                                        DNDStatus = true;
//                                                        if (THEROOM.getSERVICE1_B() != null) {
//                                                            if (THEROOM.getSERVICE1_B().dps.get("3") != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("3").toString().equals("false")) {
//                                                                    THEROOM.getSERVICE1().publishDps("{\"3\": true}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//
//                                                        }
//                                                        //turning cleanup off
//                                                        if (THEROOM.getSERVICE1_B() != null) {
//                                                            if (THEROOM.getSERVICE1_B().dps.get("1") != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("1").toString().equals("true")) {
//                                                                    THEROOM.getSERVICE1().publishDps("{\"1\": false}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//
//                                                        }
//                                                        //turning laundry off
//                                                        if (THEROOM.getSERVICE1_B() != null) {
//                                                            if (THEROOM.getSERVICE1_B().dps.get("2") != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("2").toString().equals("true")) {
//                                                                    THEROOM.getSERVICE1().publishDps("{\"2\": false}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//
//                                                        }
//                                                        myRefDND.setValue(timee);
//                                                        myRefdep.setValue("DND");
//                                                        dndOn();
//                                                        if (CleanupStatus) {
//                                                            removeCleanupOrderInDataBaseFor3Switch();
//                                                        }
//                                                        if (LaundryStatus) {
//                                                            removeLaundryOrderInDataBaseFor3Switch();
//                                                        }
//                                                        if (RoomServiceStatus) {
//                                                            removeRoomServiceOrderInDataBase();
//                                                        }
//                                                    }
////                                                        StringRequest request = new StringRequest(Request.Method.POST, insertServiceOrderUrl, new Response.Listener<String>() {
////                                                            @Override
////                                                            public void onResponse(String response) {
////                                                                loading.stop();
////
////                                                                try {
////                                                                    Log.e("DND", response);
////                                                                    if (Integer.parseInt(response) > 0) {
////                                                                        dndId = Integer.parseInt(response);
////                                                                    }
////                                                                } catch (Exception e) {
////                                                                    Log.e("DND", e.getMessage());
////                                                                }
////
////                                                            }
////                                                        }
////                                                                , new Response.ErrorListener() {
////                                                            @Override
////                                                            public void onErrorResponse(VolleyError error) {
////                                                                Log.e("DNDerror", error.getMessage());
////                                                            }
////                                                        }) {
////                                                            @Override
////                                                            protected Map<String, String> getParams() throws AuthFailureError {
////                                                                Map<String, String> params = new HashMap<String, String>();
////                                                                params.put("roomNumber", String.valueOf(LogIn.room.getRoomNumber()));
////                                                                params.put("time", String.valueOf(timee));
////                                                                params.put("dep", dep);
////                                                                params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
////                                                                params.put("RorS", String.valueOf(RoomOrSuite));
////                                                                params.put("Reservation", String.valueOf(RESERVATION));
////                                                                return params;
////                                                            }
////                                                        };
////                                                        Volley.newRequestQueue(act).add(request);
//                                                    }
//                                                    else if (DNDStatus && dpStr.get("switch_3").toString().equals("false")) {
//                                                        if (CURRENT_ROOM_STATUS == 2) {
//                                                        String dep = "DND";
//                                                        //LoadingDialog loading = new LoadingDialog(act);
//                                                        DNDStatus = false;
//                                                        if (THEROOM.getSERVICE1_B() != null) {
//                                                            if (THEROOM.getSERVICE1_B().dps.get("3") != null) {
//                                                                if (THEROOM.getSERVICE1_B().dps.get("3").toString().equals("true")) {
//                                                                    THEROOM.getSERVICE1().publishDps("{\"3\":false}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//
//                                                        }
//                                                        myRefDND.setValue(0);
//                                                        dndOff();
//                                                    }
////                                                        StringRequest rrr = new StringRequest(Request.Method.POST, removeServiceOrderUrl, new Response.Listener<String>() {
////                                                            @Override
////                                                            public void onResponse(String response) {
////                                                                if (response.equals("1")) {
////                                                                    loading.stop();
////
////                                                                }
////
////                                                            }
////                                                        }, new Response.ErrorListener() {
////                                                            @Override
////                                                            public void onErrorResponse(VolleyError error) {
////
////                                                            }
////                                                        }) {
////                                                            @Override
////                                                            protected Map<String, String> getParams() throws AuthFailureError {
////                                                                Map<String, String> params = new HashMap<String, String>();
////                                                                params.put("id", String.valueOf(dndId));
////                                                                params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
////                                                                params.put("dep", dep);
////                                                                params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
////                                                                return params;
////                                                            }
////                                                        };
////                                                        Volley.newRequestQueue(act).add(rrr);
//                                                    }
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onRemoved(String devId) {
//
//                                            }
//
//                                            @Override
//                                            public void onStatusChanged(String devId, boolean online) {
//
//                                            }
//
//                                            @Override
//                                            public void onNetworkStatusChanged(String devId, boolean status) {
//
//                                            }
//
//                                            @Override
//                                            public void onDevInfoUpdate(String devId) {
//
//                                            }
//                                        });
//                                    }
////                                    if (THEROOM.getSERVICE_B().getDps().keySet().toString()) {}
////                                    THEROOM.getSERVICE().registerDeviceListener(new IDeviceListener() {
////                                        @Override
////                                        public void onDpUpdate(String devId, Map<String, Object> dpStr) {
////                                            Log.d("serviceSwitch" , dpStr.toString());
////                                            //For Switch With 4 Buttons
////                                            if (dpStr.toString().contains("switch_1") && dpStr.toString().contains("switch_2") && dpStr.toString().contains("switch_3") && dpStr.toString().contains("switch_4")) {
////                                                //DND
////                                                if (dpStr.get("switch_1") != null) {
////                                                    if (!DNDStatus && dpStr.get("switch_1").toString().equals("true")) {
////                                                        String dep = "DND";
////                                                        Calendar xxx = Calendar.getInstance(Locale.getDefault());
////                                                        long timee = xxx.getTimeInMillis();
////                                                        LoadingDialog loading = new LoadingDialog(act);
////                                                        DNDStatus = true;
////                                                        StringRequest request = new StringRequest(Request.Method.POST, insertServiceOrderUrl, new Response.Listener<String>() {
////                                                            @Override
////                                                            public void onResponse(String response) {
////                                                                if (THEROOM.getSERVICE_B() != null) {
////                                                                    Log.d("serviceSwitch", "not null");
////                                                                    Log.d("serviceSwitch", THEROOM.getSERVICE_B().dps.toString());
////
////                                                                    if (THEROOM.getSERVICE_B().dps.get("1").toString().equals("false")) {
////
////                                                                        THEROOM.getSERVICE().publishDps("{\"1\":true}", new IResultCallback() {
////                                                                            @Override
////                                                                            public void onError(String code, String error) {
////                                                                                Log.d("serviceSwitch", error);
////                                                                            }
////
////                                                                            @Override
////                                                                            public void onSuccess() {
////                                                                                Log.d("serviceSwitch", "success");
////                                                                            }
////                                                                        });
////                                                                    } else {
////                                                                        Log.d("serviceSwitch", "is null");
////                                                                    }
////
////                                                                }
////                                                                if (CleanupStatus) {
////                                                                    removeCleanupOrderInDataBase();
////                                                                }
////                                                                if (LaundryStatus) {
////                                                                    removeLaundryOrderInDataBase();
////                                                                }
////                                                                try {
////                                                                    Log.e("DND", response);
////                                                                    if (Integer.parseInt(response) > 0) {
////                                                                        loading.stop();
////                                                                        dndId = Integer.parseInt(response);
////                                                                        myRefDND.setValue(dndId);
////                                                                        myRefdep.setValue("DND");
////                                                                        dndOn();
////                                                                    }
////                                                                } catch (Exception e) {
////                                                                    Log.e("DND", e.getMessage());
////                                                                }
////
////                                                            }
////                                                        }
////                                                                , new Response.ErrorListener() {
////                                                            @Override
////                                                            public void onErrorResponse(VolleyError error) {
////                                                                Log.e("DNDerror", error.getMessage());
////                                                            }
////                                                        }) {
////                                                            @Override
////                                                            protected Map<String, String> getParams() throws AuthFailureError {
////                                                                Map<String, String> params = new HashMap<String, String>();
////                                                                params.put("roomNumber", String.valueOf(LogIn.room.getRoomNumber()));
////                                                                params.put("time", String.valueOf(timee));
////                                                                params.put("dep", dep);
////                                                                params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
////                                                                params.put("RorS", String.valueOf(RoomOrSuite));
////                                                                params.put("Reservation", String.valueOf(RESERVATION));
////                                                                return params;
////                                                            }
////                                                        };
////                                                        Volley.newRequestQueue(act).add(request);
////                                                    }
////                                                    else if (DNDStatus && dpStr.get("switch_1").toString().equals("false")) {
////                                                        String dep = "DND";
////                                                        LoadingDialog loading = new LoadingDialog(act);
////                                                        DNDStatus = false;
////                                                        StringRequest rrr = new StringRequest(Request.Method.POST, removeServiceOrderUrl, new Response.Listener<String>() {
////                                                            @Override
////                                                            public void onResponse(String response) {
////                                                                if (THEROOM.getSERVICE_B() != null) {
////                                                                    if (THEROOM.getSERVICE_B().dps.get("1").toString().equals("true")) {
////                                                                        THEROOM.getSERVICE().publishDps("{\"1\":false}", new IResultCallback() {
////                                                                            @Override
////                                                                            public void onError(String code, String error) {
////
////                                                                            }
////
////                                                                            @Override
////                                                                            public void onSuccess() {
////
////                                                                            }
////                                                                        });
////                                                                    } else {
////                                                                        Log.d("serviceSwitch", "is null");
////                                                                    }
////                                                                }
////                                                                if (response.equals("1")) {
////                                                                    loading.stop();
////                                                                    myRefDND.setValue(0);
////                                                                    dndOff();
////                                                                }
////
////                                                            }
////                                                        }, new Response.ErrorListener() {
////                                                            @Override
////                                                            public void onErrorResponse(VolleyError error) {
////
////                                                            }
////                                                        }) {
////                                                            @Override
////                                                            protected Map<String, String> getParams() throws AuthFailureError {
////                                                                Map<String, String> params = new HashMap<String, String>();
////                                                                params.put("id", String.valueOf(dndId));
////                                                                params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
////                                                                params.put("dep", dep);
////                                                                params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
////                                                                return params;
////                                                            }
////                                                        };
////                                                        Volley.newRequestQueue(act).add(rrr);
////                                                    }
////                                                }
////                                                //Cleanup
////                                                if (dpStr.get("switch_2") != null) {
////                                                    if (!CleanupStatus && dpStr.get("switch_2").toString().equals("true")) {
////                                                        addCleanupOrderInDataBase();
////                                                    } else if (CleanupStatus && dpStr.get("switch_2").toString().equals("false")) {
////                                                        removeCleanupOrderInDataBase();
////                                                    }
////                                                }
////                                                //Laundry
////                                                if (dpStr.get("switch_3") != null) {
////                                                    if (!LaundryStatus && dpStr.get("switch_3").toString().equals("true")) {
////                                                        LoadingDialog loading = new LoadingDialog(act);
////                                                        final String dep = "Laundry";
////                                                        Calendar xxx = Calendar.getInstance(Locale.getDefault());
////                                                        long timee = xxx.getTimeInMillis();
////                                                        LaundryStatus = true;
////                                                        StringRequest addOrder = new StringRequest(Request.Method.POST, insertServiceOrderUrl, new Response.Listener<String>() {
////                                                            @Override
////                                                            public void onResponse(String response) {
////                                                                loading.stop();
////                                                                if (Integer.parseInt(response) > 0) {
////                                                                    ToastMaker.MakeToast(dep + " Order Sent Successfully", act);
////                                                                    laundryOrderId = Integer.parseInt(response);
////                                                                    myRefLaundry.setValue(laundryOrderId);
////                                                                    myRefdep.setValue(dep);
////                                                                    myRefDND.addListenerForSingleValueEvent(new ValueEventListener() {
////                                                                        @Override
////                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
////                                                                            if (Long.parseLong(snapshot.getValue().toString()) > 0) {
////                                                                                myRefDND.setValue(0);
////                                                                            }
////
////                                                                        }
////
////                                                                        @Override
////                                                                        public void onCancelled(@NonNull DatabaseError error) {
////
////                                                                        }
////                                                                    });
////                                                                    laundryOn();
////                                                                } else {
////                                                                    Toast.makeText(act, response, Toast.LENGTH_LONG).show();
////                                                                }
////
////                                                            }
////                                                        }, new Response.ErrorListener() {
////                                                            @Override
////                                                            public void onErrorResponse(VolleyError error) {
////                                                                loading.stop();
////                                                            }
////                                                        }) {
////                                                            @Override
////                                                            protected Map<String, String> getParams() throws AuthFailureError {
////                                                                Map<String, String> params = new HashMap<String, String>();
////                                                                params.put("roomNumber", String.valueOf(LogIn.room.getRoomNumber()));
////                                                                params.put("time", String.valueOf(timee));
////                                                                params.put("dep", dep);
////                                                                params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
////                                                                params.put("RorS", String.valueOf(RoomOrSuite));
////                                                                params.put("Reservation", String.valueOf(RESERVATION));
////                                                                return params;
////                                                            }
////
////                                                        };
////                                                        Volley.newRequestQueue(act).add(addOrder);
////                                                    } else if (LaundryStatus && dpStr.get("switch_3").toString().equals("false")) {
////                                                        LoadingDialog loading = new LoadingDialog(act);
////                                                        myRefLaundry.addListenerForSingleValueEvent(new ValueEventListener() {
////                                                            @Override
////                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                                                                if (Long.parseLong(snapshot.getValue().toString()) > 0) {
////                                                                    laundryOrderId = Long.parseLong(snapshot.getValue().toString());
////                                                                }
////                                                            }
////
////                                                            @Override
////                                                            public void onCancelled(@NonNull DatabaseError error) {
////
////                                                            }
////                                                        });
////                                                        final String dep = "Laundry";
////                                                        LaundryStatus = false;
////                                                        StringRequest removOrder = new StringRequest(Request.Method.POST, removeServiceOrderUrl, new Response.Listener<String>() {
////                                                            @Override
////                                                            public void onResponse(String response) {
////                                                                loading.stop();
////                                                                if (response.equals("1")) {
////                                                                    myRefLaundry.setValue(0);
////                                                                    laundryOff();
////                                                                    ToastMaker.MakeToast(dep + " Order Cancelled", act);
////                                                                } else {
////                                                                    //Toast.makeText(act , response,Toast.LENGTH_LONG).show();
////                                                                }
////
////                                                            }
////                                                        }, new Response.ErrorListener() {
////                                                            @Override
////                                                            public void onErrorResponse(VolleyError error) {
////                                                                loading.stop();
////                                                                // Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
////                                                            }
////                                                        }) {
////                                                            @Override
////                                                            protected Map<String, String> getParams() throws AuthFailureError {
////                                                                Map<String, String> params = new HashMap<String, String>();
////                                                                params.put("id", String.valueOf(laundryOrderId));
////                                                                params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
////                                                                params.put("dep", dep);
////                                                                params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
////                                                                return params;
////                                                            }
////                                                        };
////                                                        Volley.newRequestQueue(act).add(removOrder);
////                                                    }
////                                                }
////                                                //Checkout
////                                                if (dpStr.get("switch_4") != null) {
////                                                    if (!CheckoutStatus && dpStr.get("switch_4").toString().equals("true")) {
////                                                        LoadingDialog loading = new LoadingDialog(act);
////                                                        final String dep = "Checkout";
////                                                        Calendar xxx = Calendar.getInstance(Locale.getDefault());
////                                                        long timee = xxx.getTimeInMillis();
////                                                        CheckoutStatus = true;
////                                                        final StringRequest request = new StringRequest(Request.Method.POST, insertServiceOrderUrl, new Response.Listener<String>() {
////                                                            @Override
////                                                            public void onResponse(String response) {
////                                                                loading.stop();
////                                                                if (response.equals("0")) {
////
////                                                                } else {
////                                                                    ToastMaker.MakeToast(dep + " Order Sent Successfully", act);
////                                                                    checkOutId = Integer.parseInt(response);
////                                                                    myRefCheckout.setValue(timee);
////                                                                    myRefdep.setValue(dep);
////                                                                    myRefDND.addListenerForSingleValueEvent(new ValueEventListener() {
////                                                                        @Override
////                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
////                                                                            if (Long.parseLong(snapshot.getValue().toString()) > 0) {
////                                                                                myRefDND.setValue(0);
////                                                                            }
////
////                                                                        }
////
////                                                                        @Override
////                                                                        public void onCancelled(@NonNull DatabaseError error) {
////
////                                                                        }
////                                                                    });
////                                                                    checkoutOn();
////                                                                    Dialog RatingDialog = new Dialog(act);
////                                                                    RatingDialog.setContentView(R.layout.rating_dialog);
////                                                                    RatingDialog.setCancelable(false);
////                                                                    Button sendRating = (Button) RatingDialog.findViewById(R.id.sendRatingButton);
////                                                                    RatingBar RatingD = (RatingBar) RatingDialog.findViewById(R.id.ratingBar);
////                                                                    final String[] RATING = {""};
////                                                                    RatingD.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
////                                                                        @Override
////                                                                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
////                                                                            //ToastMaker.MakeToast(String.valueOf(rating),act);
////                                                                            RATING[0] = String.valueOf(rating);
////                                                                        }
////                                                                    });
////                                                                    sendRating.setOnClickListener(new View.OnClickListener() {
////                                                                        @Override
////                                                                        public void onClick(View v) {
////                                                                            RatingDialog.dismiss();
////                                                                            LoadingDialog loading = new LoadingDialog(act);
////                                                                            //ToastMaker.MakeToast(RATING[0],act);
////                                                                            String url = LogIn.URL + "insertRating.php";
////                                                                            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
////                                                                                @Override
////                                                                                public void onResponse(String response) {
////                                                                                    loading.stop();
////
////                                                                                }
////                                                                            }, new Response.ErrorListener() {
////                                                                                @Override
////                                                                                public void onErrorResponse(VolleyError error) {
////                                                                                    loading.stop();
////                                                                                }
////                                                                            }) {
////                                                                                @Override
////                                                                                protected Map<String, String> getParams() throws AuthFailureError {
////                                                                                    Map<String, String> Params = new HashMap<String, String>();
////                                                                                    Params.put("Reservation", String.valueOf(RESERVATION));
////                                                                                    Params.put("Rating", RATING[0]);
////                                                                                    return Params;
////                                                                                }
////                                                                            };
////                                                                            Volley.newRequestQueue(act).add(request);
////                                                                        }
////                                                                    });
////                                                                    RatingDialog.show();
////                                                                }
////
////                                                            }
////                                                        }, new Response.ErrorListener() {
////                                                            @Override
////                                                            public void onErrorResponse(VolleyError error) {
////                                                                loading.stop();
////                                                            }
////                                                        }) {
////                                                            @Override
////                                                            protected Map<String, String> getParams() throws AuthFailureError {
////                                                                Map<String, String> params = new HashMap<String, String>();
////                                                                params.put("roomNumber", String.valueOf(LogIn.room.getRoomNumber()));
////                                                                params.put("time", String.valueOf(timee));
////                                                                params.put("dep", dep);
////                                                                params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
////                                                                params.put("RorS", String.valueOf(RoomOrSuite));
////                                                                params.put("Reservation", String.valueOf(RESERVATION));
////                                                                return params;
////                                                            }
////                                                        };
////                                                        Volley.newRequestQueue(act).add(request);
////                                                    } else if (CheckoutStatus && dpStr.get("switch_4").toString().equals("false")) {
////                                                        Log.d("checkoutProblem", " from here " + CheckoutStatus + dpStr.get("switch_4").toString());
////                                                        final String dep = "Checkout";
////                                                        LoadingDialog loading = new LoadingDialog(act);
////                                                        CheckoutStatus = false;
////                                                        StringRequest re = new StringRequest(Request.Method.POST, removeServiceOrderUrl, new Response.Listener<String>() {
////                                                            @Override
////                                                            public void onResponse(String response) {
////                                                                loading.stop();
////                                                                checkoutOff();
////                                                                myRefCheckout.setValue(0);
////                                                                ToastMaker.MakeToast(dep + " Order Cancelled", act);
////                                                            }
////                                                        }, new Response.ErrorListener() {
////                                                            @Override
////                                                            public void onErrorResponse(VolleyError error) {
////                                                                //d.dismiss();
////                                                                loading.stop();
////                                                            }
////                                                        }) {
////                                                            @Override
////                                                            protected Map<String, String> getParams() throws AuthFailureError {
////                                                                Map<String, String> params = new HashMap<String, String>();
////                                                                params.put("id", String.valueOf(checkOutId));
////                                                                params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
////                                                                params.put("dep", dep);
////                                                                params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
////                                                                return params;
////                                                            }
////                                                        };
////                                                        Volley.newRequestQueue(act).add(re);
////                                                    }
////                                                }
////                                            }
////                                            //For Switch With 3 Buttons
////                                            else if (dpStr.toString().contains("switch_1") && dpStr.toString().contains("switch_2") && dpStr.toString().contains("switch_3")) {
////                                                //Cleanup
////                                                if (dpStr.get("switch_1") != null) {
////                                                    if (!CleanupStatus && dpStr.get("switch_1").toString().equals("true")) {
////                                                        addCleanupOrderInDataBaseFor3Switch();
////                                                    } else if (CleanupStatus && dpStr.get("switch_1").toString().equals("false")) {
////                                                        removeCleanupOrderInDataBaseFor3Switch();
////                                                    }
////                                                }
////                                                //Laundry
////                                                if (dpStr.get("switch_2") != null) {
////                                                    if (!CleanupStatus && dpStr.get("switch_2").toString().equals("true")) {
////                                                        addLaundryOrderInDataBaseFor3Switch();
////                                                    } else if (CleanupStatus && dpStr.get("switch_2").toString().equals("false")) {
////                                                        removeLaundryOrderInDataBaseFor3Switch();
////                                                    }
////                                                }
////                                                //DND
////                                                if (dpStr.get("switch_3") != null) {
////                                                    if (!DNDStatus && dpStr.get("switch_3").toString().equals("true")) {
////                                                        String dep = "DND";
////                                                        Calendar xxx = Calendar.getInstance(Locale.getDefault());
////                                                        long timee = xxx.getTimeInMillis();
////                                                        LoadingDialog loading = new LoadingDialog(act);
////                                                        DNDStatus = true;
////                                                        StringRequest request = new StringRequest(Request.Method.POST, insertServiceOrderUrl, new Response.Listener<String>() {
////                                                            @Override
////                                                            public void onResponse(String response) {
////                                                                if (THEROOM.getSERVICE_B() != null) {
////                                                                    Log.d("serviceSwitch", "not null");
////                                                                    Log.d("serviceSwitch", THEROOM.getSERVICE_B().dps.toString());
////
////                                                                    if (THEROOM.getSERVICE_B().dps.get("3").toString().equals("false")) {
////
////                                                                        THEROOM.getSERVICE().publishDps("{\"3\":true}", new IResultCallback() {
////                                                                            @Override
////                                                                            public void onError(String code, String error) {
////                                                                                Log.d("serviceSwitch", error);
////                                                                            }
////
////                                                                            @Override
////                                                                            public void onSuccess() {
////                                                                                Log.d("serviceSwitch", "success");
////                                                                            }
////                                                                        });
////                                                                    } else {
////                                                                        Log.d("serviceSwitch", "is null");
////                                                                    }
////
////                                                                }
////                                                                if (CleanupStatus) {
////                                                                    removeCleanupOrderInDataBaseFor3Switch();
////                                                                }
////                                                                if (LaundryStatus) {
////                                                                    removeLaundryOrderInDataBaseFor3Switch();
////                                                                }
////                                                                try {
////                                                                    Log.e("DND", response);
////                                                                    if (Integer.parseInt(response) > 0) {
////                                                                        loading.stop();
////                                                                        dndId = Integer.parseInt(response);
////                                                                        myRefDND.setValue(timee);
////                                                                        myRefdep.setValue("DND");
////                                                                        dndOn();
////                                                                    }
////                                                                } catch (Exception e) {
////                                                                    Log.e("DND", e.getMessage());
////                                                                }
////
////                                                            }
////                                                        }
////                                                                , new Response.ErrorListener() {
////                                                            @Override
////                                                            public void onErrorResponse(VolleyError error) {
////                                                                Log.e("DNDerror", error.getMessage());
////                                                            }
////                                                        }) {
////                                                            @Override
////                                                            protected Map<String, String> getParams() throws AuthFailureError {
////                                                                Map<String, String> params = new HashMap<String, String>();
////                                                                params.put("roomNumber", String.valueOf(LogIn.room.getRoomNumber()));
////                                                                params.put("time", String.valueOf(timee));
////                                                                params.put("dep", dep);
////                                                                params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
////                                                                params.put("RorS", String.valueOf(RoomOrSuite));
////                                                                params.put("Reservation", String.valueOf(RESERVATION));
////                                                                return params;
////                                                            }
////                                                        };
////                                                        Volley.newRequestQueue(act).add(request);
////                                                    }
////                                                    else if (DNDStatus && dpStr.get("switch_3").toString().equals("false")) {
////                                                        String dep = "DND";
////                                                        LoadingDialog loading = new LoadingDialog(act);
////                                                        DNDStatus = false;
////                                                        StringRequest rrr = new StringRequest(Request.Method.POST, removeServiceOrderUrl, new Response.Listener<String>() {
////                                                            @Override
////                                                            public void onResponse(String response) {
////                                                                if (THEROOM.getSERVICE_B() != null) {
////                                                                    if (THEROOM.getSERVICE_B().dps.get("3").toString().equals("true")) {
////                                                                        THEROOM.getSERVICE().publishDps("{\"3\":false}", new IResultCallback() {
////                                                                            @Override
////                                                                            public void onError(String code, String error) {
////
////                                                                            }
////
////                                                                            @Override
////                                                                            public void onSuccess() {
////
////                                                                            }
////                                                                        });
////                                                                    } else {
////                                                                        Log.d("serviceSwitch", "is null");
////                                                                    }
////                                                                }
////                                                                if (response.equals("1")) {
////                                                                    loading.stop();
////                                                                    myRefDND.setValue(0);
////                                                                    dndOff();
////                                                                }
////
////                                                            }
////                                                        }, new Response.ErrorListener() {
////                                                            @Override
////                                                            public void onErrorResponse(VolleyError error) {
////
////                                                            }
////                                                        }) {
////                                                            @Override
////                                                            protected Map<String, String> getParams() throws AuthFailureError {
////                                                                Map<String, String> params = new HashMap<String, String>();
////                                                                params.put("id", String.valueOf(dndId));
////                                                                params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
////                                                                params.put("dep", dep);
////                                                                params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
////                                                                return params;
////                                                            }
////                                                        };
////                                                        Volley.newRequestQueue(act).add(rrr);
////                                                    }
////                                                }
////                                            }
////                                        }
////
////                                        @Override
////                                        public void onRemoved(String devId) {
////
////                                        }
////
////                                        @Override
////                                        public void onStatusChanged(String devId, boolean online) {
////
////                                        }
////
////                                        @Override
////                                        public void onNetworkStatusChanged(String devId, boolean status) {
////
////                                        }
////
////                                        @Override
////                                        public void onDevInfoUpdate(String devId) {
////
////                                        }
////                                    });
//                                }
//                                else if (zigbeeDevices.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch1")) {
//                                    Switch1Status = true ;
//                                    Switch1Bean = new DeviceBean() ;
//                                    Switch1Bean = zigbeeDevices.get(i) ;
//                                    THEROOM.setSWITCH1_B(zigbeeDevices.get(i));
//                                    Log.d("switch1" , Switch1Bean.dps.toString());
//                                    Switch1 = TuyaHomeSdk.newDeviceInstance(Switch1Bean.getDevId());
//                                    THEROOM.setSWITCH1(TuyaHomeSdk.newDeviceInstance(Switch1Bean.getDevId()));
//                                    Switch1.registerDeviceListener(new IDeviceListener() {
//                                        @Override
//                                        public void onDpUpdate(String devId, Map<String, Object> dpStr)
//                                        {
//                                            //Log.d("switch1Update" , dpStr.toString());
////                                            if (dpStr.get("switch_1") != null ) {
////                                                if (dpStr.get("switch_1").toString().equals("true")){
////                                                    //Button b1 = (Button)findViewById(R.id.button15);
////                                                    //b1.setBackgroundResource(R.drawable.light_on);
////                                                    if (Switch1Bean.dps.get("3") != null ) {
////                                                        if (Switch1Bean.dps.get("3").toString().equals("true")) {
////                                                            Switch1.publishDps("{\" 3\":false}", new IResultCallback() {
////                                                                @Override
////                                                                public void onError(String code, String error) {
////
////                                                                }
////
////                                                                @Override
////                                                                public void onSuccess() {
////
////                                                                }
////                                                            });
////                                                        }
////                                                    }
////                                                }
////                                                else {
////                                                    //Button b1 = (Button)findViewById(R.id.button15);
////                                                    //b1.setBackgroundResource(R.drawable.group_62);
////                                                }
////                                            }
////                                            if (dpStr.get("switch_2") != null ){
////                                                if (dpStr.get("switch_2").toString().equals("true")){
////                                                    //Button b1 = (Button)findViewById(R.id.button14);
////                                                    //b1.setBackgroundResource(R.drawable.light_on);
////                                                    if (Switch1Bean.dps.get("3") != null) {
////                                                        if (Switch1Bean.dps.get("3").toString().equals("true")) {
////                                                            Switch1.publishDps("{\" 3\":false}", new IResultCallback() {
////                                                                @Override
////                                                                public void onError(String code, String error) {
////
////                                                                }
////
////                                                                @Override
////                                                                public void onSuccess() {
////
////                                                                }
////                                                            });
////                                                        }
////                                                    }
////                                                }
////                                                else {
////                                                    //Button b1 = (Button)findViewById(R.id.button14);
////                                                    //b1.setBackgroundResource(R.drawable.group_62);
////                                                }
////                                            }
////                                            if (dpStr.get("switch_3") != null ) {
//////                                                if (dpStr.get("switch_3").toString().equals("true")){
//////                                                    Button b1 = (Button)findViewById(R.id.button17);
//////                                                    b1.setBackgroundResource(R.drawable.light_on);
//////                                                }
//////                                                else {
//////                                                    Button b1 = (Button)findViewById(R.id.button17);
//////                                                    b1.setBackgroundResource(R.drawable.group_62);
//////                                                }
////                                                //Button b1 = (Button)findViewById(R.id.button17);
////                                                //b1.setBackgroundResource(R.drawable.light_on);
////                                                if (dpStr.get("switch_3").toString().equals("true")) {
////                                                    if (Switch1Bean.dps.get("1").toString().equals("true")) {
////                                                        Switch1.publishDps("{\" 1\":false}", new IResultCallback() {
////                                                            @Override
////                                                            public void onError(String code, String error) {
////
////                                                            }
////
////                                                            @Override
////                                                            public void onSuccess() {
////
////                                                            }
////                                                        });
////                                                    }
////                                                    if (Switch1Bean.dps.get("2").toString().equals("true")) {
////                                                        Switch1.publishDps("{\" 2\":false}", new IResultCallback() {
////                                                            @Override
////                                                            public void onError(String code, String error) {
////
////                                                            }
////
////                                                            @Override
////                                                            public void onSuccess() {
////
////                                                            }
////                                                        });
////                                                    }
////                                                    if (Switch2Bean != null ) {
////                                                        if (Switch2Bean.dps.get("1").toString().equals("true")) {
////                                                            Switch2.publishDps("{\" 1\":false}", new IResultCallback() {
////                                                                @Override
////                                                                public void onError(String code, String error) {
////
////                                                                }
////
////                                                                @Override
////                                                                public void onSuccess() {
////
////                                                                }
////                                                            });
////                                                        }
////                                                        if (Switch2Bean.dps.get("2").toString().equals("true")) {
////                                                            Switch2.publishDps("{\" 2\":false}", new IResultCallback() {
////                                                                @Override
////                                                                public void onError(String code, String error) {
////
////                                                                }
////
////                                                                @Override
////                                                                public void onSuccess() {
////
////                                                                }
////                                                            });
////                                                        }
////                                                    }
////                                                    if (Switch3Bean != null ) {
////                                                        if (Switch3Bean.dps.get("1").toString().equals("true")) {
////                                                            Switch3.publishDps("{\" 1\":false}", new IResultCallback() {
////                                                                @Override
////                                                                public void onError(String code, String error) {
////
////                                                                }
////
////                                                                @Override
////                                                                public void onSuccess() {
////
////                                                                }
////                                                            });
////                                                        }
////                                                        if (Switch3Bean.dps.keySet().contains("2")) {
////                                                            if (Switch3Bean.dps.get("2").toString().equals("true")) {
////                                                                Switch3.publishDps("{\" 2\":false}", new IResultCallback() {
////                                                                    @Override
////                                                                    public void onError(String code, String error) {
////
////                                                                    }
////
////                                                                    @Override
////                                                                    public void onSuccess() {
////
////                                                                    }
////                                                                });
////                                                            }
////                                                        }
////                                                    }
////                                                }
////                                                else {
////                                                    //b1.setBackgroundResource(R.drawable.group_62);
////                                                }
////                                            }
////                                            if (dpStr.get("switch_4") != null ){
////                                                if (dpStr.get("switch_4").toString().equals("true")){
////                                                    Button b1 = (Button)findViewById(R.id.button19);
////                                                    b1.setBackgroundResource(R.drawable.light_on);
////                                                }
////                                                else {
////                                                    Button b1 = (Button)findViewById(R.id.button19);
////                                                    b1.setBackgroundResource(R.drawable.group_62);
////                                                }
////                                            }
//                                        }
//                                        @Override
//                                        public void onRemoved(String devId) {
//
//                                        }
//
//                                        @Override
//                                        public void onStatusChanged(String devId, boolean online) {
//
//                                        }
//
//                                        @Override
//                                        public void onNetworkStatusChanged(String devId, boolean status) {
//
//                                        }
//
//                                        @Override
//                                        public void onDevInfoUpdate(String devId) {
//
//                                        }
//                                    });
//
//                                }
//                                else if (zigbeeDevices.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch2")) {
//                                    Switch2Status = true ;
//                                    Switch2Bean = new DeviceBean() ;
//                                    Switch2Bean = zigbeeDevices.get(i) ;
//                                    THEROOM.setSWITCH2_B(zigbeeDevices.get(i));
//                                    Switch2 = TuyaHomeSdk.newDeviceInstance(Switch2Bean.getDevId());
//                                    THEROOM.setSWITCH2(TuyaHomeSdk.newDeviceInstance(Switch2Bean.getDevId()));
//                                    Switch2.registerDeviceListener(new IDeviceListener() {
//                                        @Override
//                                        public void onDpUpdate(String devId, Map<String, Object> dpStr) {
//                                            /*
//                                            Log.d("switch2" , dpStr.toString());
//                                            if (dpStr.get("switch_1") != null ){
//                                                if (dpStr.get("switch_1").toString().equals("true")){
//                                                    if (Switch1 != null){
//                                                        Switch1.publishDps("{\"1\":true}", new IResultCallback() {
//                                                            @Override
//                                                            public void onError(String code, String error) {
//
//                                                            }
//
//                                                            @Override
//                                                            public void onSuccess() {
//
//                                                            }
//                                                        });
//                                                    }
//
//                                                }
//                                                else{
//                                                    if (Switch1 != null){
//                                                        Switch1.publishDps("{\"1\":false}", new IResultCallback() {
//                                                            @Override
//                                                            public void onError(String code, String error) {
//
//                                                            }
//
//                                                            @Override
//                                                            public void onSuccess() {
//
//                                                            }
//                                                        });
//                                                    }
//
//                                                }
//                                            }
//                                            if (dpStr.get("switch_2") != null ){
//                                                if (dpStr.get("switch_2").toString().equals("true")){
//                                                    if (Switch1 != null){
//                                                        Switch1.publishDps("{\"2\":true}", new IResultCallback() {
//                                                            @Override
//                                                            public void onError(String code, String error) {
//
//                                                            }
//
//                                                            @Override
//                                                            public void onSuccess() {
//
//                                                            }
//                                                        });
//                                                    }
//
//                                                }
//                                                else {
//                                                    if (Switch1 != null){
//                                                        Switch1.publishDps("{\"2\":false}", new IResultCallback() {
//                                                            @Override
//                                                            public void onError(String code, String error) {
//
//                                                            }
//
//                                                            @Override
//                                                            public void onSuccess() {
//
//                                                            }
//                                                        });
//                                                    }
//
//                                                }
//                                            }
//                                            if (dpStr.get("switch_3") != null ){
//                                                if (dpStr.get("switch_3").toString().equals("true")){
//                                                    if (Switch1 != null){
//                                                        Switch1.publishDps("{\"3\":true}", new IResultCallback() {
//                                                            @Override
//                                                            public void onError(String code, String error) {
//
//                                                            }
//
//                                                            @Override
//                                                            public void onSuccess() {
//
//                                                            }
//                                                        });
//                                                    }
//
//                                                }
//                                                else {
//                                                    if (Switch1 != null){
//                                                        Switch1.publishDps("{\"3\":false}", new IResultCallback() {
//                                                            @Override
//                                                            public void onError(String code, String error) {
//
//                                                            }
//
//                                                            @Override
//                                                            public void onSuccess() {
//
//                                                            }
//                                                        });
//                                                    }
//
//                                                }
//                                            }
//                                            if (dpStr.get("switch_4") != null ){
//                                                if (dpStr.get("switch_4").toString().equals("true")){
//                                                    if (Switch1 != null){
//                                                        Switch1.publishDps("{\"4\":true}", new IResultCallback() {
//                                                            @Override
//                                                            public void onError(String code, String error) {
//
//                                                            }
//
//                                                            @Override
//                                                            public void onSuccess() {
//
//                                                            }
//                                                        });
//                                                    }
//
//                                                }
//                                                else {
//                                                    if (Switch1 != null){
//                                                        Switch1.publishDps("{\"4\":false}", new IResultCallback() {
//                                                            @Override
//                                                            public void onError(String code, String error) {
//
//                                                            }
//
//                                                            @Override
//                                                            public void onSuccess() {
//
//                                                            }
//                                                        });
//                                                    }
//
//                                                }
//                                            }
//
//                                             */
//
////                                            if (dpStr != null ) {
////                                                if (dpStr.get("switch_1") != null ) {
////                                                    if (dpStr.get("switch_1").toString().equals("true")) {
////                                                        Switch1.publishDps("{\" 3\":false}", new IResultCallback() {
////                                                            @Override
////                                                            public void onError(String code, String error) {
////
////                                                            }
////
////                                                            @Override
////                                                            public void onSuccess() {
////
////                                                            }
////                                                        });
////                                                    }
////                                                }
////
////                                                if (dpStr.get("switch_2") != null ) {
////                                                    if (dpStr.get("switch_2").toString().equals("true") ) {
////                                                        Switch1.publishDps("{\" 3\":false}", new IResultCallback() {
////                                                            @Override
////                                                            public void onError(String code, String error) {
////
////                                                            }
////
////                                                            @Override
////                                                            public void onSuccess() {
////
////                                                            }
////                                                        });
////                                                    }
////                                                }
////
////                                            }
//
//                                        }
//
//                                        @Override
//                                        public void onRemoved(String devId) {
//
//                                        }
//
//                                        @Override
//                                        public void onStatusChanged(String devId, boolean online) {
//                                            if (THEROOM.getSWITCH3_B() == null) {
//                                                AutoLightOn = online ;
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onNetworkStatusChanged(String devId, boolean status) {
//
//                                        }
//
//                                        @Override
//                                        public void onDevInfoUpdate(String devId) {
//
//                                        }
//                                    });
//                                    /*
//                                    iTuyaDeviceMultiControl = TuyaHomeSdk.getDeviceMultiControlInstance();
//                                    iTuyaDeviceMultiControl.getDeviceDpInfoList(Switch2Bean.devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
//                                        @Override
//                                        public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
//                                            for (int i=0 ; i<result.size(); i++){
//                                                Log.d("switch2DeviceDp" , result.get(i).getName());
//                                            }
//
//                                        }
//
//                                        @Override
//                                        public void onError(String errorCode, String errorMessage) {
//                                            Log.d("switch2DeviceDp" , errorMessage );
//                                        }
//                                    });
//                                    JSONObject groupdetailes = new JSONObject();
//                                    try
//                                    {
//                                        groupdetailes.put("devId",Switch2Bean.devId);
//                                        groupdetailes.put("dpId" ,1 );
//                                        groupdetailes.put("id" ,1 );
//                                        groupdetailes.put("enable" ,true );
//
//                                    }
//                                    catch (JSONException e){}
//                                    JSONObject multiControlBean = new JSONObject();
//                                    try
//                                    {
//                                        multiControlBean.put("groupName","Lighting");
//                                        multiControlBean.put("groupType",1);
//                                        multiControlBean.put("groupDetail",groupdetailes);
//                                        multiControlBean.put("id",1);
//                                    }
//                                    catch (JSONException e)
//                                    {
//
//                                    }
//                                    iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString() , new ITuyaResultCallback<MultiControlBean>() {
//                                        @Override
//                                        public void onSuccess(MultiControlBean result) {
//                                            //ToastUtil.shortToast(mContext,"success");
//                                            Log.d("switch2DeviceDp" , result.getGroupName() );
//                                        }
//
//                                        @Override
//                                        public void onError(String errorCode, String errorMessage) {
//                                            //ToastUtil.shortToast(mContext,errorMessage);
//                                            Log.d("switch2DeviceDp" , errorMessage );
//                                        }
//                                    });
//
//                                    iTuyaDeviceMultiControl.enableMultiControl(1, new ITuyaResultCallback<Boolean>() {
//                                        @Override
//                                        public void onSuccess(Boolean result) {
//                                            //ToastUtil.shortToast(mContext,"success");
//                                            Log.d("MultiControlResult" , result.toString());
//                                        }
//
//                                        @Override
//                                        public void onError(String errorCode, String errorMessage) {
//                                            //ToastUtil.shortToast(mContext,errorMessage);
//                                            Log.d("MultiControlResult" , errorMessage);
//                                        }
//                                    });
//
//                                     */
//
//                                }
//                                else if (zigbeeDevices.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch3")) {
//                                    Switch3Status = true ;
//                                    Switch3Bean = new DeviceBean() ;
//                                    Switch3Bean = zigbeeDevices.get(i) ;
//                                    THEROOM.setSWITCH3_B(zigbeeDevices.get(i));
//                                    Switch3 = TuyaHomeSdk.newDeviceInstance(Switch3Bean.getDevId());
//                                    THEROOM.setSWITCH3(TuyaHomeSdk.newDeviceInstance(Switch3Bean.getDevId()));
//                                    Switch3.registerDeviceListener(new IDeviceListener() {
//                                        @Override
//                                        public void onDpUpdate(String devId, Map<String, Object> dpStr) {
//
//                                        }
//
//                                        @Override
//                                        public void onRemoved(String devId) {
//
//                                        }
//
//                                        @Override
//                                        public void onStatusChanged(String devId, boolean online) {
//                                            Log.d("DpUpdates", online+"" ) ;
//                                            AutoLightOn = online ;
//                                        }
//
//                                        @Override
//                                        public void onNetworkStatusChanged(String devId, boolean status) {
//
//                                        }
//
//                                        @Override
//                                        public void onDevInfoUpdate(String devId) {
//
//                                        }
//                                    });
//                                }
//                                else if (zigbeeDevices.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch4")) {
//                                    Switch4Status = true ;
//                                    Switch4Bean = new DeviceBean() ;
//                                    Switch4Bean = zigbeeDevices.get(i) ;
//                                    THEROOM.setSWITCH4_B(zigbeeDevices.get(i));
//                                    Switch4 = TuyaHomeSdk.newDeviceInstance(Switch4Bean.getDevId());
//                                    THEROOM.setSWITCH4(TuyaHomeSdk.newDeviceInstance(Switch4Bean.getDevId()));
//                                    Switch4.registerDeviceListener(new IDeviceListener() {
//                                        @Override
//                                        public void onDpUpdate(String devId, Map<String, Object> dpStr) {
//
//                                        }
//
//                                        @Override
//                                        public void onRemoved(String devId) {
//
//                                        }
//
//                                        @Override
//                                        public void onStatusChanged(String devId, boolean online) {
//
//                                        }
//
//                                        @Override
//                                        public void onNetworkStatusChanged(String devId, boolean status) {
//
//                                        }
//
//                                        @Override
//                                        public void onDevInfoUpdate(String devId) {
//
//                                        }
//                                    });
//                                }
//                                else if (zigbeeDevices.get(i).getName().equals(MyApp.Room.RoomNumber+"Power")) {
//                                    PowerControllerStatus = true ;
//                                    THEROOM.setPOWER_B(zigbeeDevices.get(i));
//                                    THEROOM.setPOWER(TuyaHomeSdk.newDeviceInstance(THEROOM.getPOWER_B().getDevId()));
//                                    THEROOM.getPOWER().registerDeviceListener(new IDeviceListener() {
//                                        @Override
//                                        public void onDpUpdate(String devId, Map<String, Object> dpStr) {
//                                            Log.d("DpUpdates" , devId+"  " + String.valueOf(dpStr)) ;
//                                            if (dpStr.get("switch_1") != null)
//                                            {
//                                                String S1 = dpStr.get("switch_1").toString() ;
//                                                if (S1.equals("false"))
//                                                {
//                                                    myRefPower.setValue(0);
//                                                    setPowerOnOff("0");
//                                                }
//                                                else
//                                                {
//                                                    myRefPower.setValue(1);
//                                                    setPowerOnOff("1");
//                                                }
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onRemoved(String devId) {
//
//                                        }
//
//                                        @Override
//                                        public void onStatusChanged(String devId, boolean online) {
//
//                                        }
//
//                                        @Override
//                                        public void onNetworkStatusChanged(String devId, boolean status) {
//
//                                        }
//
//                                        @Override
//                                        public void onDevInfoUpdate(String devId) {
//
//                                        }
//                                    });
//                                }
//                                else if (zigbeeDevices.get(i).getName().equals(MyApp.Room.RoomNumber+"ServiceSwitch2")) {
//                                    Log.d("ServiceBind", "2 found");
//                                    THEROOM.setSERVICE2_B(zigbeeDevices.get(i));
//                                    THEROOM.setSERVICE2(TuyaHomeSdk.newDeviceInstance(zigbeeDevices.get(i).getDevId()));
//                                    if (THEROOM.getSERVICE1_B() != null && THEROOM.getSERVICE2_B() != null ) {
//                                        Log.d("ServiceBind", "not Null");
//                                        if (THEROOM.getSERVICE1_B().getDps().get("1") != null && THEROOM.getSERVICE2_B().getDps().get("1") != null ) {
//                                            Log.d("ServiceBind", "1");
//                                            Random r = new Random();
//                                            int x = r.nextInt(30);
//                                            JSONObject groupdetailes1 = new JSONObject(), groupdetailes2 = new JSONObject();
//                                            try {
//                                                groupdetailes1.put("devId", THEROOM.getSERVICE1_B().devId);
//                                                groupdetailes1.put("dpId", "1");
//                                                groupdetailes1.put("id", x);
//                                                groupdetailes1.put("enable", true);
//
//                                            } catch (JSONException e) {
//                                                Log.d("ServiceBind", e.getMessage());
//                                            }
//                                            try {
//                                                groupdetailes2.put("devId", THEROOM.getSERVICE2_B().devId);
//                                                groupdetailes2.put("dpId", "1");
//                                                groupdetailes2.put("id", x);
//                                                groupdetailes2.put("enable", true);
//
//                                            } catch (JSONException e) {
//                                                Log.d("ServiceBind", e.getMessage());
//                                            }
//                                            JSONArray arr = new JSONArray();
//                                            arr.put(groupdetailes2);
//                                            arr.put(groupdetailes1);
//                                            JSONObject multiControlBean = new JSONObject();
//                                            try {
//                                                multiControlBean.put("groupName", MyApp.Room.RoomNumber + "Service" + x);
//                                                multiControlBean.put("groupType", 1);
//                                                multiControlBean.put("groupDetail", arr);
//                                                multiControlBean.put("id", x);
//                                            } catch (JSONException e) {
//                                                Log.d("ServiceBind", e.getMessage());
//                                            }
//                                            Log.d("ServiceBind", "no problem");
//                                            iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString(), new ITuyaResultCallback<MultiControlBean>() {
//                                                @Override
//                                                public void onSuccess(MultiControlBean result) {
//                                                    Log.d("ServiceBind", "created ");
////                                                    iTuyaDeviceMultiControl.enableMultiControl(xxx, new ITuyaResultCallback<Boolean>() {
////                                                        @Override
////                                                        public void onSuccess(Boolean result) {
////                                                            Log.d("ServiceBind", "enabled ");
////                                                        }
////
////                                                        @Override
////                                                        public void onError(String errorCode, String errorMessage) {
////                                                            Log.d("ServiceBind", "not enabled  "+errorMessage);
////
////                                                        }
////                                                    });
//                                                }
//
//                                                @Override
//                                                public void onError(String errorCode, String errorMessage) {
//                                                    Log.d("ServiceBind", "error "+errorMessage);
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSERVICE1_B().getDps().get("2") != null && THEROOM.getSERVICE2_B().getDps().get("2") != null ) {
//                                            Random r = new Random();
//                                            int x = r.nextInt(30);
//                                            JSONObject groupdetailes1 = new JSONObject(), groupdetailes2 = new JSONObject();
//                                            try {
//                                                groupdetailes1.put("devId", THEROOM.getSERVICE1_B().devId);
//                                                groupdetailes1.put("dpId", "2");
//                                                groupdetailes1.put("id", x);
//                                                groupdetailes1.put("enable", true);
//
//                                            } catch (JSONException e) {
//                                            }
//                                            try {
//                                                groupdetailes2.put("devId", THEROOM.getSERVICE2_B().devId);
//                                                groupdetailes2.put("dpId", "2");
//                                                groupdetailes2.put("id", x);
//                                                groupdetailes2.put("enable", true);
//
//                                            } catch (JSONException e) {
//                                            }
//                                            JSONArray arr = new JSONArray();
//                                            arr.put(groupdetailes2);
//                                            arr.put(groupdetailes1);
//                                            JSONObject multiControlBean = new JSONObject();
//                                            try {
//                                                multiControlBean.put("groupName", MyApp.Room.RoomNumber + "Service" + x);
//                                                multiControlBean.put("groupType", 2);
//                                                multiControlBean.put("groupDetail", arr);
//                                                multiControlBean.put("id", x);
//                                            } catch (JSONException e) {
//
//                                            }
//
//                                            iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString(), new ITuyaResultCallback<MultiControlBean>() {
//                                                @Override
//                                                public void onSuccess(MultiControlBean result) {
//                                                    Log.d("ServiceBind", "created ");
//                                                    iTuyaDeviceMultiControl.enableMultiControl(x, new ITuyaResultCallback<Boolean>() {
//                                                        @Override
//                                                        public void onSuccess(Boolean result) {
//                                                            Log.d("ServiceBind", "enabled ");
//                                                        }
//
//                                                        @Override
//                                                        public void onError(String errorCode, String errorMessage) {
//                                                            Log.d("ServiceBind", "not enabled "+errorMessage);
//                                                        }
//                                                    });
//                                                }
//
//                                                @Override
//                                                public void onError(String errorCode, String errorMessage) {
//                                                    Log.d("ServiceBind", "error "+errorMessage);
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSERVICE1_B().getDps().get("3") != null && THEROOM.getSERVICE2_B().getDps().get("3") != null ) {
//                                            Random r = new Random();
//                                            int x = r.nextInt(30);
//                                            JSONObject groupdetailes1 = new JSONObject(), groupdetailes2 = new JSONObject();
//                                            try {
//                                                groupdetailes1.put("devId", THEROOM.getSERVICE1_B().devId);
//                                                groupdetailes1.put("dpId", "3");
//                                                groupdetailes1.put("id", x);
//                                                groupdetailes1.put("enable", true);
//
//                                            } catch (JSONException e) {
//                                            }
//                                            try {
//                                                groupdetailes2.put("devId", THEROOM.getSERVICE2_B().devId);
//                                                groupdetailes2.put("dpId", "3");
//                                                groupdetailes2.put("id", x);
//                                                groupdetailes2.put("enable", true);
//
//                                            } catch (JSONException e) {
//                                            }
//                                            JSONArray arr = new JSONArray();
//                                            arr.put(groupdetailes2);
//                                            arr.put(groupdetailes1);
//                                            JSONObject multiControlBean = new JSONObject();
//                                            try {
//                                                multiControlBean.put("groupName", MyApp.Room.RoomNumber + "Service" + x);
//                                                multiControlBean.put("groupType", 3);
//                                                multiControlBean.put("groupDetail", arr);
//                                                multiControlBean.put("id", x);
//                                            } catch (JSONException e) {
//
//                                            }
//
//                                            iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString(), new ITuyaResultCallback<MultiControlBean>() {
//                                                @Override
//                                                public void onSuccess(MultiControlBean result) {
//                                                    //ToastUtil.shortToast(mContext,"success");
//                                                    Log.d("ServiceBind", "created ");
//                                                    iTuyaDeviceMultiControl.enableMultiControl(x, new ITuyaResultCallback<Boolean>() {
//                                                        @Override
//                                                        public void onSuccess(Boolean result) {
//                                                            Log.d("ServiceBind", "enabled ");
//                                                        }
//
//                                                        @Override
//                                                        public void onError(String errorCode, String errorMessage) {
//                                                            Log.d("ServiceBind", "not enabled "+errorMessage);
//                                                        }
//                                                    });
//                                                }
//
//                                                @Override
//                                                public void onError(String errorCode, String errorMessage) {
//                                                    Log.d("ServiceBind", "error "+errorMessage);
//                                                }
//                                            });
//                                        }
//                                        if (THEROOM.getSERVICE1_B().getDps().get("4") != null && THEROOM.getSERVICE2_B().getDps().get("4") != null ) {
//                                            Random r = new Random();
//                                            int x = r.nextInt(30);
//                                            JSONObject groupdetailes1 = new JSONObject(), groupdetailes2 = new JSONObject();
//                                            try {
//                                                groupdetailes1.put("devId", THEROOM.getSERVICE1_B().devId);
//                                                groupdetailes1.put("dpId", "4");
//                                                groupdetailes1.put("id", x);
//                                                groupdetailes1.put("enable", true);
//
//                                            } catch (JSONException e) {
//                                            }
//                                            try {
//                                                groupdetailes2.put("devId", THEROOM.getSERVICE2_B().devId);
//                                                groupdetailes2.put("dpId", "4");
//                                                groupdetailes2.put("id", x);
//                                                groupdetailes2.put("enable", true);
//
//                                            } catch (JSONException e) {
//                                            }
//                                            JSONArray arr = new JSONArray();
//                                            arr.put(groupdetailes2);
//                                            arr.put(groupdetailes1);
//                                            JSONObject multiControlBean = new JSONObject();
//                                            try {
//                                                multiControlBean.put("groupName", MyApp.Room.RoomNumber + "Service" + x);
//                                                multiControlBean.put("groupType", 4);
//                                                multiControlBean.put("groupDetail", arr);
//                                                multiControlBean.put("id", x);
//                                            } catch (JSONException e) {
//
//                                            }
//
//                                            iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString(), new ITuyaResultCallback<MultiControlBean>() {
//                                                @Override
//                                                public void onSuccess(MultiControlBean result) {
//                                                    Log.d("ServiceBind", "created ");
//                                                    iTuyaDeviceMultiControl.enableMultiControl(x, new ITuyaResultCallback<Boolean>() {
//                                                        @Override
//                                                        public void onSuccess(Boolean result) {
//                                                            Log.d("ServiceBind", "enabled ");
//                                                        }
//
//                                                        @Override
//                                                        public void onError(String errorCode, String errorMessage) {
//                                                            Log.d("ServiceBind", "not enabled "+errorMessage);
//                                                        }
//                                                    });
//                                                }
//
//                                                @Override
//                                                public void onError(String errorCode, String errorMessage) {
//                                                    Log.d("ServiceBind", "error "+errorMessage);
//                                                }
//                                            });
//                                        }
//                                    }
//                                }
//                                else if (zigbeeDevices.get(i).getName().equals(MyApp.Room.RoomNumber+"IR")) {
//                                    Log.d("IR" , "IR Found ");
//                                }
//                            }
//                            if (DoorSensorStatus ) {
//                                setDoorSensorStatus("1");
//                            }
//                            else {
//                                setDoorSensorStatus("0");
//                            }
//                            if (MotionSensorStatus) {
//                                setMotionSensorStatus("1");
//                            }
//                            else {
//                                setMotionSensorStatus("0");
//                            }
//                            if (CurtainControllerStatus) {
//                                setCurtainSwitchStatus("1");
//                                curtainBtn.setVisibility(View.VISIBLE);
//                            }
//                            else {
//                                setCurtainSwitchStatus("0");
//                                curtainBtn.setVisibility(View.INVISIBLE);
//                            }
//                            if (ServiceSwitchStatus) {
//                                setServiceSwitchStatus("1");
//                            }
//                            else {
//                                setServiceSwitchStatus("0");
//                            }
//                            if (Switch1Status) {
//                                setSwitch1Status("1");
//                            }
//                            else {
//                                setSwitch1Status("0");
//                            }
//                            if (Switch2Status) {
//                                setSwitch2Status("1");
//                            }
//                            else {
//                                setSwitch2Status("0");
//                            }
//                            if (Switch3Status) {
//                                setSwitch3Status("1");
//                            }
//                            else {
//                                setSwitch3Status("0");
//                            }
//                            if (Switch4Status) {
//                                setSwitch4Status("1");
//                            }
//                            else {
//                                setSwitch4Status("0");
//                            }
//                            if (!Switch1Status && !Switch2Status && !Switch3Status && !Switch4Status) {
//                                ShowLighting.setVisibility(View.GONE);
//                            }
//                            else if (Switch1Status || Switch2Status || Switch3Status || Switch4Status) {
//                                ShowLighting.setVisibility(View.VISIBLE);
//                                Log.d("masterOff" ,"i am here "+lightsDB.getMasterOffButtons().size());
//                                if (lightsDB.getMasterOffButtons().size() > 0 ) {
//                                        Log.d("masterOff" ,lightsDB.getMasterOffButtons().size()+"");
//                                        if (String.valueOf(lightsDB.getMasterOffButtons().get(0).Switch ).contains("0")) {
//                                            Log.d("masterOff" , "yes I am button "+lightsDB.getMasterOffButtons().get(0).Switch+" "+lightsDB.getMasterOffButtons().get(0).button);
//                                            String xx = String.valueOf(lightsDB.getMasterOffButtons().get(0).Switch ).replace("0","");
//                                            if (xx.equals("1")) {
//                                                MasterOffDevice = THEROOM.getSWITCH1_B() ;
//                                                MasterOffBtn = String.valueOf(lightsDB.getMasterOffButtons().get(0).button) ;
//                                            }
//                                            else if (xx.equals("2")) {
//                                                MasterOffDevice = THEROOM.getSWITCH2_B() ;
//                                                MasterOffBtn = String.valueOf(lightsDB.getMasterOffButtons().get(0).button) ;
//                                            }
//                                            else if (xx.equals("3")) {
//                                                MasterOffDevice = THEROOM.getSWITCH3_B() ;
//                                                MasterOffBtn = String.valueOf(lightsDB.getMasterOffButtons().get(0).button) ;
//                                            }
//                                            else if (xx.equals("4")) {
//                                                MasterOffDevice = THEROOM.getSWITCH4_B() ;
//                                                MasterOffBtn = String.valueOf(lightsDB.getMasterOffButtons().get(0).button) ;
//                                            }
//                                            MasterOffDevicesWithoutBtn = new ArrayList<DeviceBean>();
//                                            MasterOffDevicesWithoutBtnButtons = new ArrayList<String>();
//                                            for (int i = 1 ; i < lightsDB.getMasterOffButtons().size();i++) {
//                                                if (THEROOM.getSWITCH1_B().getName().split("Switch")[1].equals(String.valueOf(lightsDB.getMasterOffButtons().get(i).Switch))) {
//                                                    MasterOffDevicesWithoutBtn.add(THEROOM.getSWITCH1_B());
//                                                    MasterOffDevicesWithoutBtnButtons.add(String.valueOf(lightsDB.getMasterOffButtons().get(i).button));
//                                                }
//                                                else if (THEROOM.getSWITCH2_B().getName().split("Switch")[1].equals(String.valueOf(lightsDB.getMasterOffButtons().get(i).Switch))) {
//                                                    MasterOffDevicesWithoutBtn.add(THEROOM.getSWITCH2_B());
//                                                    MasterOffDevicesWithoutBtnButtons.add(String.valueOf(lightsDB.getMasterOffButtons().get(i).button));
//                                                }
//                                                else if (THEROOM.getSWITCH3_B().getName().split("Switch")[1].equals(String.valueOf(lightsDB.getMasterOffButtons().get(i).Switch))) {
//                                                    MasterOffDevicesWithoutBtn.add(THEROOM.getSWITCH3_B());
//                                                    MasterOffDevicesWithoutBtnButtons.add(String.valueOf(lightsDB.getMasterOffButtons().get(i).button));
//                                                }
//                                                else if (THEROOM.getSWITCH4_B().getName().split("Switch")[1].equals(String.valueOf(lightsDB.getMasterOffButtons().get(i).Switch))) {
//                                                    MasterOffDevicesWithoutBtn.add(THEROOM.getSWITCH4_B());
//                                                    MasterOffDevicesWithoutBtnButtons.add(String.valueOf(lightsDB.getMasterOffButtons().get(i).button));
//                                                }
//                                            }
//                                            Log.d("masterOff" , MasterOffDevicesWithoutBtn.size()+" "+MasterOffDevice.getName()) ;
//                                            if (MasterOffDevice != null ) {
//                                                //Log.d("addMasteroffBtn" , "not null");
//                                                //View MasterOffScreenBtn = LayoutInflater.from(act).inflate(R.layout.masteroff_unit,lightsLayout,false);
//                                                LinearLayout MasterOffScreenBtn = new LinearLayout(act);
//                                                MasterOffScreenBtn.setOrientation(LinearLayout.VERTICAL);
//                                                TextView text = new TextView(act);
//                                                Button image = new Button(act);
//                                                image.setBackgroundResource(R.drawable.group_62);
//                                                MasterOffScreenBtn.addView(text);
//                                                text.setPadding(0,0,0,10);
//                                                MasterOffScreenBtn.addView(image);
//                                                text.setGravity(Gravity.CENTER);
//                                                text.setText("Master Off");
//                                                text.setTextColor(Color.LTGRAY);
//                                                Log.d("addMasteroffBtn" , lightsLayout.getChildCount()+"");
//                                                MasterOffScreenBtn.setPadding(10,0,10,0);
//                                                lightsLayout.addView(MasterOffScreenBtn);
//                                                image.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View v) {
//                                                        if (MasterOffDevice.getDps().get(MasterOffBtn) != null ) {
//                                                            if (MasterOffDevice.getDps().get(MasterOffBtn).toString().equals("false")) {
//                                                                image.setBackgroundResource(R.drawable.group_62);
//                                                                text.setTextColor(Color.LTGRAY);
//                                                                TuyaHomeSdk.newDeviceInstance(MasterOffDevice.devId).publishDps("{\""+MasterOffBtn+"\": true}", new IResultCallback() {
//                                                                    @Override
//                                                                    public void onError(String code, String error) {
//
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onSuccess() {
//
//                                                                    }
//                                                                });
//                                                            }
//                                                            else if(MasterOffDevice.getDps().get(MasterOffBtn).toString().equals("true")) {
//                                                                image.setBackgroundResource(R.drawable.light_on);
//                                                                text.setTextColor(Color.YELLOW);
//                                                                TuyaHomeSdk.newDeviceInstance(MasterOffDevice.devId).publishDps("{\""+MasterOffBtn+"\": false}", new IResultCallback() {
//                                                                    @Override
//                                                                    public void onError(String code, String error) {
//
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onSuccess() {
//
//                                                                    }
//                                                                });
//                                                            }
//                                                        }
//                                                    }
//                                                });
//                                                TuyaHomeSdk.newDeviceInstance(MasterOffDevice.devId).registerDeviceListener(new IDeviceListener() {
//                                                    @Override
//                                                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
//                                                        Log.d("masterOff" , dpStr.toString()+" "+MasterOffBtn) ;
//                                                        if (dpStr.get("switch_"+MasterOffBtn) != null ) {
//                                                            if (dpStr.get("switch_"+MasterOffBtn).toString().equals("true")) {
//                                                                if (MasterOffDevicesWithoutBtn.size() > 0 ) {
//                                                                    for (int j = 0; j < MasterOffDevicesWithoutBtn.size();j++) {
//                                                                        TuyaHomeSdk.newDeviceInstance(MasterOffDevicesWithoutBtn.get(j).devId).publishDps("{\"" + MasterOffDevicesWithoutBtnButtons.get(j) + "\": false}", new IResultCallback() {
//                                                                            @Override
//                                                                            public void onError(String code, String error) {
//
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onSuccess() {
//
//                                                                            }
//                                                                        });
//                                                                    }
//                                                                }
//                                                                image.setBackgroundResource(R.drawable.light_on);
//                                                                text.setTextColor(Color.YELLOW);
//                                                            }
//                                                            else if (dpStr.get("switch_"+MasterOffBtn).toString().equals("false")) {
//                                                                image.setBackgroundResource(R.drawable.group_62);
//                                                                text.setTextColor(Color.LTGRAY);
//                                                            }
//                                                        }
//                                                    }
//                                                    @Override
//                                                    public void onRemoved(String devId) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onStatusChanged(String devId, boolean online) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onNetworkStatusChanged(String devId, boolean status) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onDevInfoUpdate(String devId) {
//
//                                                    }
//                                                });
//                                            }
//                                        }
//                                        else {
//                                            MasterOffDevicesWithoutBtn = new ArrayList<DeviceBean>();
//                                            MasterOffDevicesWithoutBtnButtons = new ArrayList<String>();
//                                            for (int i = 0 ; i < lightsDB.getMasterOffButtons().size();i++) {
//                                                if (THEROOM.getSWITCH1_B().getName().split("Switch")[1].equals(String.valueOf(lightsDB.getMasterOffButtons().get(i).Switch))) {
//                                                    MasterOffDevicesWithoutBtn.add(THEROOM.getSWITCH1_B());
//                                                    MasterOffDevicesWithoutBtnButtons.add(String.valueOf(lightsDB.getMasterOffButtons().get(i).button));
//                                                }
//                                                else if (THEROOM.getSWITCH2_B().getName().split("Switch")[1].equals(String.valueOf(lightsDB.getMasterOffButtons().get(i).Switch))) {
//                                                    MasterOffDevicesWithoutBtn.add(THEROOM.getSWITCH2_B());
//                                                    MasterOffDevicesWithoutBtnButtons.add(String.valueOf(lightsDB.getMasterOffButtons().get(i).button));
//                                                }
//                                                else if (THEROOM.getSWITCH3_B().getName().split("Switch")[1].equals(String.valueOf(lightsDB.getMasterOffButtons().get(i).Switch))) {
//                                                    MasterOffDevicesWithoutBtn.add(THEROOM.getSWITCH3_B());
//                                                    MasterOffDevicesWithoutBtnButtons.add(String.valueOf(lightsDB.getMasterOffButtons().get(i).button));
//                                                }
//                                                else if (THEROOM.getSWITCH4_B().getName().split("Switch")[1].equals(String.valueOf(lightsDB.getMasterOffButtons().get(i).Switch))) {
//                                                    MasterOffDevicesWithoutBtn.add(THEROOM.getSWITCH4_B());
//                                                    MasterOffDevicesWithoutBtnButtons.add(String.valueOf(lightsDB.getMasterOffButtons().get(i).button));
//                                                }
//                                            }
//                                            LinearLayout MasterOffScreenBtn = new LinearLayout(act);
//                                            MasterOffScreenBtn.setOrientation(LinearLayout.VERTICAL);
//                                            TextView text = new TextView(act);
//                                            Button image = new Button(act);
//                                            MasterOffScreenBtn.addView(text);
//                                            text.setPadding(0,0,0,10);
//                                            MasterOffScreenBtn.addView(image);
//                                            text.setGravity(Gravity.CENTER);
//                                            text.setText("Master Off");
//                                            text.setTextColor(Color.LTGRAY);
//                                            Log.d("addMasteroffBtn" , lightsLayout.getChildCount()+"");
//                                            boolean OnOff = false ;
//                                            MasterOffScreenBtn.setPadding(10,0,10,0);
//                                            lightsLayout.addView(MasterOffScreenBtn);
//                                            image.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                        if (text.getCurrentTextColor() == Color.YELLOW) {
//                                                            image.setBackgroundResource(R.drawable.group_62);
//                                                            text.setTextColor(Color.LTGRAY);
//                                                        }
//                                                        else if(text.getCurrentTextColor() == Color.LTGRAY) {
//                                                            image.setBackgroundResource(R.drawable.light_on);
//                                                            text.setTextColor(Color.YELLOW);
//                                                            for (int i=0;i<MasterOffDevicesWithoutBtn.size();i++) {
//                                                                TuyaHomeSdk.newDeviceInstance(MasterOffDevicesWithoutBtn.get(i).devId).publishDps("{\""+MasterOffDevicesWithoutBtnButtons.get(i)+"\": false}", new IResultCallback() {
//                                                                    @Override
//                                                                    public void onError(String code, String error) {
//
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onSuccess() {
//
//                                                                    }
//                                                                });
//                                                            }
//                                                        }
//
//                                                }
//                                            });
//                                        }
//                                    }
//                                Log.d("lightsBtns" ,"i am here "+lightsDB.getScreenButtons().size());
//                                if (lightsDB.getScreenButtons().size() > 0 ) {
//                                    lightsLayout.setDividerPadding(10);
//                                    for (int i=0 ; i < lightsDB.getScreenButtons().size(); i++) {
//                                        Log.d("lightsBtns" ,lightsDB.getScreenButtons().get(i).Switch+" "+lightsDB.getScreenButtons().get(i).button+" "+lightsDB.getScreenButtons().get(i).name);
//                                        if (THEROOM.getSWITCH1_B() != null ) {
//                                            String s = THEROOM.getSWITCH1_B().getName().split("Switch")[1];
//                                            if (lightsDB.getScreenButtons().get(i).Switch == Integer.parseInt(s)) {
//                                                LinearLayout LightButton = new LinearLayout(act);
//                                                LightButton.setOrientation(LinearLayout.VERTICAL);
//                                                TextView text = new TextView(act);
//                                                Button image = new Button(act);
//                                                image.setBackgroundResource(R.drawable.group_62);
//                                                LightButton.addView(text);
//                                                text.setPadding(0,0,0,10);
//                                                LightButton.addView(image);
//                                                text.setGravity(Gravity.CENTER);
//                                                text.setText(lightsDB.getScreenButtons().get(i).name);
//                                                text.setTextColor(Color.LTGRAY);
//                                                Log.d("addLightButton" , lightsLayout.getChildCount()+"");
//                                                LightButton.setPadding(10,0,10,0);
//                                                lightsLayout.addView(LightButton);
//                                                int finalI = i;
//                                                boolean LightStatus[] = {false};
//                                                image.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View v) {
//                                                        if (THEROOM.getSWITCH1_B().getDps().get(String.valueOf(lightsDB.getScreenButtons().get(finalI).button)) != null ) {
//                                                            if (!LightStatus[0]) { //THEROOM.getSWITCH1_B().getDps().get(String.valueOf(lightsDB.getScreenButtons().get(finalI).button)).toString().equals("false")
//                                                                image.setBackgroundResource(R.drawable.group_62);
//                                                                text.setTextColor(Color.LTGRAY);
//                                                                TuyaHomeSdk.newDeviceInstance(THEROOM.getSWITCH1_B().devId).publishDps("{\""+lightsDB.getScreenButtons().get(finalI).button+"\": true}", new IResultCallback() {
//                                                                    @Override
//                                                                    public void onError(String code, String error) {
//                                                                        Log.d("lights 3 "+lightsDB.getScreenButtons().get(finalI).button , error );
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onSuccess() {
//
//                                                                    }
//                                                                });
//                                                            }
//                                                            else if(LightStatus[0]) { //THEROOM.getSWITCH1_B().getDps().get(String.valueOf(lightsDB.getScreenButtons().get(finalI).button)).toString().equals("true")
//                                                                image.setBackgroundResource(R.drawable.light_on);
//                                                                text.setTextColor(Color.YELLOW);
//                                                                TuyaHomeSdk.newDeviceInstance(THEROOM.getSWITCH1_B().devId).publishDps("{\""+lightsDB.getScreenButtons().get(finalI).button+"\": false}", new IResultCallback() {
//                                                                    @Override
//                                                                    public void onError(String code, String error) {
//                                                                        Log.d("lights 3 "+lightsDB.getScreenButtons().get(finalI).button , error );
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onSuccess() {
//
//                                                                    }
//                                                                });
//                                                            }
//                                                        }
//                                                        x=0 ;
//                                                    }
//                                                });
//                                                TuyaHomeSdk.newDeviceInstance(THEROOM.getSWITCH1_B().devId).registerDeviceListener(new IDeviceListener() {
//                                                    @Override
//                                                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
//                                                        Log.d("lights1" , dpStr.toString()+" "+String.valueOf(lightsDB.getScreenButtons().get(finalI).button)) ;
//                                                        if (dpStr.get("switch_"+lightsDB.getScreenButtons().get(finalI).button) != null ) {
//                                                            if (dpStr.get("switch_"+lightsDB.getScreenButtons().get(finalI).button).toString().equals("true")) {
//                                                                LightStatus[0] = true ;
//                                                                image.setBackgroundResource(R.drawable.light_on);
//                                                                text.setTextColor(Color.YELLOW);
//                                                                if (MasterOffDevice != null ) {
//                                                                    TuyaHomeSdk.newDeviceInstance(MasterOffDevice.devId).publishDps("{\""+MasterOffBtn+"\": false}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//                                                            else if (dpStr.get("switch_"+lightsDB.getScreenButtons().get(finalI).button).toString().equals("false")) {
//                                                                LightStatus[0] = false ;
//                                                                image.setBackgroundResource(R.drawable.group_62);
//                                                                text.setTextColor(Color.LTGRAY);
//                                                            }
//                                                        }
//                                                    }
//                                                    @Override
//                                                    public void onRemoved(String devId) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onStatusChanged(String devId, boolean online) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onNetworkStatusChanged(String devId, boolean status) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onDevInfoUpdate(String devId) {
//
//                                                    }
//                                                });
//                                            }
//                                        }
//                                        if (THEROOM.getSWITCH2_B() != null ) {
//                                            String s = THEROOM.getSWITCH2_B().getName().split("Switch")[1];
//                                            if (lightsDB.getScreenButtons().get(i).Switch == Integer.parseInt(s)) {
//                                                LinearLayout LightButton = new LinearLayout(act);
//                                                LightButton.setOrientation(LinearLayout.VERTICAL);
//                                                TextView text = new TextView(act);
//                                                Button image = new Button(act);
//                                                image.setBackgroundResource(R.drawable.group_62);
//                                                LightButton.addView(text);
//                                                text.setPadding(0,0,0,10);
//                                                LightButton.addView(image);
//                                                text.setGravity(Gravity.CENTER);
//                                                text.setText(lightsDB.getScreenButtons().get(i).name);
//                                                text.setTextColor(Color.LTGRAY);
//                                                //Log.d("addLightButton" , lightsLayout.getChildCount()+"");
//                                                LightButton.setPadding(10,0,10,0);
//                                                lightsLayout.addView(LightButton);
//                                                int finalI = i;
//                                                boolean LightStatus[] = {false} ;
//                                                image.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View v) {
//                                                        if (THEROOM.getSWITCH2_B().getDps().get(String.valueOf(lightsDB.getScreenButtons().get(finalI).button)) != null ) {
//                                                            if (!LightStatus[0]) { // THEROOM.getSWITCH2_B().getDps().get(String.valueOf(lightsDB.getScreenButtons().get(finalI).button)).toString().equals("false")
//                                                                image.setBackgroundResource(R.drawable.group_62);
//                                                                text.setTextColor(Color.LTGRAY);
//                                                                TuyaHomeSdk.newDeviceInstance(THEROOM.getSWITCH2_B().devId).publishDps("{\""+lightsDB.getScreenButtons().get(finalI).button+"\": true}", new IResultCallback() {
//                                                                    @Override
//                                                                    public void onError(String code, String error) {
//                                                                        Log.d("lights 3 "+lightsDB.getScreenButtons().get(finalI).button , error );
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onSuccess() {
//
//                                                                    }
//                                                                });
//                                                            }
//                                                            else if(LightStatus[0]) { //THEROOM.getSWITCH2_B().getDps().get(String.valueOf(lightsDB.getScreenButtons().get(finalI).button)).toString().equals("true")
//                                                                image.setBackgroundResource(R.drawable.light_on);
//                                                                text.setTextColor(Color.YELLOW);
//                                                                TuyaHomeSdk.newDeviceInstance(THEROOM.getSWITCH2_B().devId).publishDps("{\""+lightsDB.getScreenButtons().get(finalI).button+"\": false}", new IResultCallback() {
//                                                                    @Override
//                                                                    public void onError(String code, String error) {
//                                                                        Log.d("lights 3 "+lightsDB.getScreenButtons().get(finalI).button , error );
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onSuccess() {
//
//                                                                    }
//                                                                });
//                                                            }
//                                                        }
//                                                        x=0 ;
//                                                    }
//                                                });
//                                                TuyaHomeSdk.newDeviceInstance(THEROOM.getSWITCH2_B().devId).registerDeviceListener(new IDeviceListener() {
//                                                    @Override
//                                                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
//                                                        Log.d("lights2" , dpStr.toString()+" "+String.valueOf(lightsDB.getScreenButtons().get(finalI).button)) ;
//                                                        if (dpStr.get("switch_"+lightsDB.getScreenButtons().get(finalI).button) != null ) {
//                                                            if (dpStr.get("switch_"+lightsDB.getScreenButtons().get(finalI).button).toString().equals("true")) {
//                                                                LightStatus[0] = true ;
//                                                                image.setBackgroundResource(R.drawable.light_on);
//                                                                text.setTextColor(Color.YELLOW);
//                                                                if (MasterOffDevice != null ) {
//                                                                    TuyaHomeSdk.newDeviceInstance(MasterOffDevice.devId).publishDps("{\""+MasterOffBtn+"\": false}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//                                                            else if (dpStr.get("switch_"+lightsDB.getScreenButtons().get(finalI).button).toString().equals("false")) {
//                                                                LightStatus[0] = false ;
//                                                                image.setBackgroundResource(R.drawable.group_62);
//                                                                text.setTextColor(Color.LTGRAY);
//                                                            }
//                                                        }
//                                                    }
//                                                    @Override
//                                                    public void onRemoved(String devId) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onStatusChanged(String devId, boolean online) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onNetworkStatusChanged(String devId, boolean status) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onDevInfoUpdate(String devId) {
//
//                                                    }
//                                                });
//                                            }
//                                        }
//                                        if (THEROOM.getSWITCH3_B() != null ) {
//                                            String s = THEROOM.getSWITCH3_B().getName().split("Switch")[1];
//                                            if (lightsDB.getScreenButtons().get(i).Switch == Integer.parseInt(s)) {
//                                                LinearLayout LightButton = new LinearLayout(act);
//                                                LightButton.setOrientation(LinearLayout.VERTICAL);
//                                                TextView text = new TextView(act);
//                                                Button image = new Button(act);
//                                                image.setBackgroundResource(R.drawable.group_62);
//                                                LightButton.addView(text);
//                                                text.setPadding(0,0,0,10);
//                                                LightButton.addView(image);
//                                                text.setGravity(Gravity.CENTER);
//                                                text.setText(lightsDB.getScreenButtons().get(i).name);
//                                                text.setTextColor(Color.LTGRAY);
//                                                //Log.d("addLightButton" , lightsLayout.getChildCount()+"");
//                                                LightButton.setPadding(10,0,10,0);
//                                                lightsLayout.addView(LightButton);
//                                                int finalI = i;
//                                                final boolean[] LightStatus = {false};
//                                                image.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View v) {
//                                                        Log.d("lights3"+lightsDB.getScreenButtons().get(finalI).button , THEROOM.getSWITCH3_B().getDps().get(String.valueOf(lightsDB.getScreenButtons().get(finalI).button)).toString());
//                                                        if (THEROOM.getSWITCH3_B().dps.get(String.valueOf(lightsDB.getScreenButtons().get(finalI).button)) != null ) {
//                                                            if (!LightStatus[0] ) {  //THEROOM.getSWITCH3_B().getDps().get(String.valueOf(lightsDB.getScreenButtons().get(finalI).button)).toString().equals("false")
//                                                                image.setBackgroundResource(R.drawable.group_62);
//                                                                text.setTextColor(Color.LTGRAY);
//                                                                TuyaHomeSdk.newDeviceInstance(THEROOM.getSWITCH3_B().devId).publishDps("{\""+lightsDB.getScreenButtons().get(finalI).button+"\": true}", new IResultCallback() {
//                                                                    @Override
//                                                                    public void onError(String code, String error) {
//                                                                        Log.d("lights3"+lightsDB.getScreenButtons().get(finalI).button , error );
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onSuccess() {
//                                                                        Log.d("lights3", "success");
//                                                                    }
//                                                                });
//                                                            }
//                                                            else if(LightStatus[0]) { //THEROOM.getSWITCH3_B().dps.get(String.valueOf(lightsDB.getScreenButtons().get(finalI).button)).toString().equals("true")
//                                                                image.setBackgroundResource(R.drawable.light_on);
//                                                                text.setTextColor(Color.YELLOW);
//                                                                TuyaHomeSdk.newDeviceInstance(THEROOM.getSWITCH3_B().devId).publishDps("{\""+lightsDB.getScreenButtons().get(finalI).button+"\": false}", new IResultCallback() {
//                                                                    @Override
//                                                                    public void onError(String code, String error) {
//                                                                        Log.d("lights3"+lightsDB.getScreenButtons().get(finalI).button , error );
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onSuccess() {
//
//                                                                    }
//                                                                });
//                                                            }
//                                                        }
//                                                        x=0 ;
//                                                    }
//                                                });
//                                                TuyaHomeSdk.newDeviceInstance(THEROOM.getSWITCH3_B().devId).registerDeviceListener(new IDeviceListener() {
//                                                    @Override
//                                                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
//                                                        Log.d("lights3" , dpStr.toString()+" "+String.valueOf(lightsDB.getScreenButtons().get(finalI).button)) ;
//                                                        if (dpStr.get("switch_"+lightsDB.getScreenButtons().get(finalI).button) != null ) {
//                                                            if (dpStr.get("switch_"+lightsDB.getScreenButtons().get(finalI).button).toString().equals("true")) {
//                                                                LightStatus[0] = true ;
//                                                                image.setBackgroundResource(R.drawable.light_on);
//                                                                text.setTextColor(Color.YELLOW);
//                                                                if (MasterOffDevice != null ) {
//                                                                    TuyaHomeSdk.newDeviceInstance(MasterOffDevice.devId).publishDps("{\""+MasterOffBtn+"\": false}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//                                                            else if (dpStr.get("switch_"+lightsDB.getScreenButtons().get(finalI).button).toString().equals("false")) {
//                                                                LightStatus[0] = false ;
//                                                                image.setBackgroundResource(R.drawable.group_62);
//                                                                text.setTextColor(Color.LTGRAY);
//                                                            }
//                                                        }
//                                                    }
//                                                    @Override
//                                                    public void onRemoved(String devId) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onStatusChanged(String devId, boolean online) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onNetworkStatusChanged(String devId, boolean status) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onDevInfoUpdate(String devId) {
//
//                                                    }
//                                                });
//                                            }
//                                        }
//                                        if (THEROOM.getSWITCH4_B() != null ) {
//                                            String s = THEROOM.getSWITCH4_B().getName().split("Switch")[1];
//                                            if (lightsDB.getScreenButtons().get(i).Switch == Integer.parseInt(s)) {
//                                                LinearLayout LightButton = new LinearLayout(act);
//                                                LightButton.setOrientation(LinearLayout.VERTICAL);
//                                                TextView text = new TextView(act);
//                                                Button image = new Button(act);
//                                                image.setBackgroundResource(R.drawable.group_62);
//                                                LightButton.addView(text);
//                                                text.setPadding(0,0,0,10);
//                                                LightButton.addView(image);
//                                                text.setGravity(Gravity.CENTER);
//                                                text.setText(lightsDB.getScreenButtons().get(i).name);
//                                                text.setTextColor(Color.LTGRAY);
//                                                Log.d("addLightButton" , lightsLayout.getChildCount()+"");
//                                                LightButton.setPadding(10,0,10,0);
//                                                lightsLayout.addView(LightButton);
//                                                int finalI = i;
//                                                boolean LightStatus[] = {false} ;
//                                                image.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View v) {
//                                                        if (THEROOM.getSWITCH4_B().getDps().get(String.valueOf(lightsDB.getScreenButtons().get(finalI).button)) != null ) {
//                                                            if (!LightStatus[0]) { //THEROOM.getSWITCH4_B().getDps().get(String.valueOf(lightsDB.getScreenButtons().get(finalI).button)).toString().equals("false")
//                                                                image.setBackgroundResource(R.drawable.group_62);
//                                                                text.setTextColor(Color.LTGRAY);
//                                                                TuyaHomeSdk.newDeviceInstance(THEROOM.getSWITCH4_B().devId).publishDps("{\""+lightsDB.getScreenButtons().get(finalI).button+"\": true}", new IResultCallback() {
//                                                                    @Override
//                                                                    public void onError(String code, String error) {
//                                                                        Log.d("lights 3 "+lightsDB.getScreenButtons().get(finalI).button , error );
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onSuccess() {
//
//                                                                    }
//                                                                });
//                                                            }
//                                                            else if(LightStatus[0]) { //THEROOM.getSWITCH4_B().getDps().get(String.valueOf(lightsDB.getScreenButtons().get(finalI).button)).toString().equals("true")
//                                                                image.setBackgroundResource(R.drawable.light_on);
//                                                                text.setTextColor(Color.YELLOW);
//                                                                TuyaHomeSdk.newDeviceInstance(THEROOM.getSWITCH4_B().devId).publishDps("{\""+lightsDB.getScreenButtons().get(finalI).button+"\": false}", new IResultCallback() {
//                                                                    @Override
//                                                                    public void onError(String code, String error) {
//                                                                        Log.d("lights 3 "+lightsDB.getScreenButtons().get(finalI).button , error );
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onSuccess() {
//
//                                                                    }
//                                                                });
//                                                            }
//                                                        }
//                                                        x=0 ;
//                                                    }
//                                                });
//                                                TuyaHomeSdk.newDeviceInstance(THEROOM.getSWITCH4_B().devId).registerDeviceListener(new IDeviceListener() {
//                                                    @Override
//                                                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
//                                                        Log.d("lights4" , dpStr.toString()+" "+String.valueOf(lightsDB.getScreenButtons().get(finalI).button)) ;
//                                                        if (dpStr.get("switch_"+String.valueOf(lightsDB.getScreenButtons().get(finalI).button)) != null ) {
//                                                            if (dpStr.get("switch_"+String.valueOf(lightsDB.getScreenButtons().get(finalI).button)).toString().equals("true")) {
//                                                                LightStatus[0] = true ;
//                                                                image.setBackgroundResource(R.drawable.light_on);
//                                                                text.setTextColor(Color.YELLOW);
//                                                                if (MasterOffDevice != null ) {
//                                                                    TuyaHomeSdk.newDeviceInstance(MasterOffDevice.devId).publishDps("{\""+MasterOffBtn+"\": false}", new IResultCallback() {
//                                                                        @Override
//                                                                        public void onError(String code, String error) {
//
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onSuccess() {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//                                                            else if (dpStr.get("switch_"+lightsDB.getScreenButtons().get(finalI).button).toString().equals("false")) {
//                                                                LightStatus[0] = false ;
//                                                                image.setBackgroundResource(R.drawable.group_62);
//                                                                text.setTextColor(Color.LTGRAY);
//                                                            }
//                                                        }
//                                                    }
//                                                    @Override
//                                                    public void onRemoved(String devId) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onStatusChanged(String devId, boolean online) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onNetworkStatusChanged(String devId, boolean status) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onDevInfoUpdate(String devId) {
//
//                                                    }
//                                                });
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//
////                            if (THEROOM.getSWITCH3() == null ) {
////                                setSwitch1DB2(THEROOM);
////                            }
////                            else {
////                                bindSwitch1ToSwitch3btn1(THEROOM);
////                                bindSwitch1ToSwitch3btn2(THEROOM);
////                            }
//
////                            setSwitch1DB1();
////                            setSwitch1DB2();
////                            setSwitch1DB3();
////                            setSwitch1DB4();
//
//                            /*
//                            iTuyaDeviceMultiControl.getDeviceDpInfoList(THEROOM.getSWITCH1_B().devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
//                                @Override
//                                public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
//                                    Log.d("switch1DeviceDp" , result.get(0).getDpId() );
//                                    iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH1_B().devId, result.get(0).getDpId(), new ITuyaDataCallback<MultiControlLinkBean>() {
//                                        @Override
//                                        public void onSuccess(MultiControlLinkBean result) {
//
//                                            if (result.getMultiGroup() != null ){
//                                                Log.d("switch1DeviceDp" , "1 DP subscriped to "+result.getMultiGroup().getGroupName() );
//                                            }
//                                            else {
//                                                if (THEROOM.getSWITCH2_B() != null){
//                                                    iTuyaDeviceMultiControl.getDeviceDpInfoList(THEROOM.getSWITCH2_B().devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
//                                                        @Override
//                                                        public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
//                                                            Log.d("switch2DeviceDp", result.get(0).getDpId());
//                                                            iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH2_B().devId, result.get(1).getDpId(), new ITuyaDataCallback<MultiControlLinkBean>() {
//                                                                @Override
//                                                                public void onSuccess(MultiControlLinkBean result) {
//                                                                    if (result.getMultiGroup() != null) {
//                                                                        Log.d("switch2DeviceDp", "2 DP subscriped to " + result.getMultiGroup().getGroupName());
//
//                                                                    } else
//                                                                        {
//                                                                            Random r = new Random();
//                                                                            int xxx = r.nextInt(99);
//                                                                        JSONObject groupdetailes1 = new JSONObject(), groupdetailes2 = new JSONObject();
//                                                                        try {
//                                                                            groupdetailes1.put("devId", Switch1Bean.devId);
//                                                                            groupdetailes1.put("dpId", 1);
//                                                                            groupdetailes1.put("id", xxx);
//                                                                            groupdetailes1.put("enable", true);
//
//                                                                        } catch (JSONException e) {
//                                                                        }
//                                                                        try {
//                                                                            groupdetailes2.put("devId", Switch2Bean.devId);
//                                                                            groupdetailes2.put("dpId", 1);
//                                                                            groupdetailes2.put("id", xxx);
//                                                                            groupdetailes2.put("enable", true);
//
//                                                                        } catch (JSONException e) {
//                                                                        }
//                                                                        JSONArray arr = new JSONArray();
//                                                                        arr.put(groupdetailes2);
//                                                                        arr.put(groupdetailes1);
//                                                                        JSONObject multiControlBean = new JSONObject();
//                                                                        try {
//                                                                            multiControlBean.put("groupName", LogIn.room.getRoomNumber()+"Lighting"+xxx);
//                                                                            multiControlBean.put("groupType", 1);
//                                                                            multiControlBean.put("groupDetail", arr);
//                                                                            multiControlBean.put("id", xxx);
//                                                                        } catch (JSONException e) {
//
//                                                                        }
//                                                                        iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString(), new ITuyaResultCallback<MultiControlBean>() {
//                                                                            @Override
//                                                                            public void onSuccess(MultiControlBean result) {
//                                                                                //ToastUtil.shortToast(mContext,"success");
//                                                                                Log.d("switch1DeviceDp", result.getGroupName());
//                                                                                iTuyaDeviceMultiControl.enableMultiControl(xxx, new ITuyaResultCallback<Boolean>() {
//                                                                                    @Override
//                                                                                    public void onSuccess(Boolean result) {
//                                                                                        //ToastUtil.shortToast(mContext,"success");
//                                                                                        Log.d("MultiControlResult", result.toString());
//                                                                                    }
//
//                                                                                    @Override
//                                                                                    public void onError(String errorCode, String errorMessage) {
//                                                                                        //ToastUtil.shortToast(mContext,errorMessage);
//                                                                                        Log.d("MultiControlResult", errorMessage);
//                                                                                    }
//                                                                                });
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onError(String errorCode, String errorMessage) {
//                                                                                //ToastUtil.shortToast(mContext,errorMessage);
//                                                                                Log.d("switch1DeviceDp", errorMessage+"here");
//                                                                            }
//                                                                        });
//                                                                    }
//                                                                }
//
//                                                                @Override
//                                                                public void onError(String errorCode, String errorMessage) {
//
//                                                                }
//                                                            });
//
//                                                        }
//
//                                                        @Override
//                                                        public void onError(String errorCode, String errorMessage) {
//                                                            Log.d("switch2DeviceDp", errorMessage);
//                                                        }
//                                                    });
//                                                }
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onError(String errorCode, String errorMessage) {
//                                            // ToastUtil.shortToast(mContext,errorMessage);
//                                        }
//                                    });
//
//                                }
//
//                                @Override
//                                public void onError(String errorCode, String errorMessage) {
//                                    Log.d("switch1DeviceDp" , errorMessage );
//                                }
//                            });
//
//
//
//                            iTuyaDeviceMultiControl.getDeviceDpInfoList(THEROOM.getSWITCH1_B().devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
//                                @Override
//                                public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
//                                    Log.d("switch1DeviceDp1" , result.get(1).getDpId() );
//                                    iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH1_B().devId, result.get(1).getDpId(), new ITuyaDataCallback<MultiControlLinkBean>() {
//                                        @Override
//                                        public void onSuccess(MultiControlLinkBean result) {
//
//                                            if (result.getMultiGroup() != null ){
//                                                Log.d("switch1DeviceDp1" , "1 DP subscriped to "+result.getMultiGroup().getGroupName() );
//                                            }
//                                            else {
//                                                if (Switch2Bean != null){
//                                                    iTuyaDeviceMultiControl.getDeviceDpInfoList(THEROOM.getSWITCH2_B().devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
//                                                        @Override
//                                                        public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
//                                                            Log.d("switch2DeviceDp1" , result.get(1).getDpId() );
//                                                            iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH2_B().devId, result.get(1).getDpId(), new ITuyaDataCallback<MultiControlLinkBean>() {
//                                                                @Override
//                                                                public void onSuccess(MultiControlLinkBean result) {
//                                                                    if (result.getMultiGroup() != null){
//                                                                        Log.d("switch2DeviceDp1" , "2 DP subscriped to "+result.getMultiGroup().getGroupName() );
//
//                                                                    }
//                                                                    else {
//                                                                        Random r = new Random();
//                                                                        int xxx = r.nextInt(40);
//                                                                        int y = r.nextInt(20);
//                                                                        JSONObject groupdetailes1 = new JSONObject(),groupdetailes2 = new JSONObject();
//                                                                        try
//                                                                        {
//                                                                            groupdetailes1.put("devId",Switch1Bean.devId);
//                                                                            groupdetailes1.put("dpId" ,2 );
//                                                                            groupdetailes1.put("id" , y );
//                                                                            groupdetailes1.put("enable" ,true );
//
//                                                                        }
//                                                                        catch (JSONException e){}
//                                                                        try
//                                                                        {
//                                                                            groupdetailes2.put("devId",Switch2Bean.devId);
//                                                                            groupdetailes2.put("dpId" ,2 );
//                                                                            groupdetailes2.put("id" , y );
//                                                                            groupdetailes2.put("enable" ,true );
//
//                                                                        }
//                                                                        catch (JSONException e){}
//                                                                        JSONArray arr = new JSONArray( );
//                                                                        arr.put(groupdetailes2);
//                                                                        arr.put(groupdetailes1);
//                                                                        JSONObject multiControlBean = new JSONObject();
//                                                                        try
//                                                                        {
//                                                                            multiControlBean.put("groupName",LogIn.room.getRoomNumber()+"Lighting"+y);
//                                                                            multiControlBean.put("groupType",1);
//                                                                            multiControlBean.put("groupDetail" , arr);
//                                                                            multiControlBean.put("id",y);
//                                                                        }
//                                                                        catch (JSONException e)
//                                                                        {
//
//                                                                        }
//                                                                        iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString() , new ITuyaResultCallback<MultiControlBean>() {
//                                                                            @Override
//                                                                            public void onSuccess(MultiControlBean result) {
//                                                                                //ToastUtil.shortToast(mContext,"success");
//                                                                                Log.d("switch1DeviceDp2" , result.getGroupName() );
//                                                                                iTuyaDeviceMultiControl.enableMultiControl(y, new ITuyaResultCallback<Boolean>() {
//                                                                                    @Override
//                                                                                    public void onSuccess(Boolean result) {
//                                                                                        //ToastUtil.shortToast(mContext,"success");
//                                                                                        Log.d("MultiControlResult" , result.toString());
//                                                                                    }
//
//                                                                                    @Override
//                                                                                    public void onError(String errorCode, String errorMessage) {
//                                                                                        //ToastUtil.shortToast(mContext,errorMessage);
//                                                                                        Log.d("MultiControlResult" , errorMessage);
//                                                                                    }
//                                                                                });
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onError(String errorCode, String errorMessage) {
//                                                                                //ToastUtil.shortToast(mContext,errorMessage);
//                                                                                Log.d("switch1DeviceDp" , errorMessage );
//                                                                            }
//                                                                        });
//                                                                    }
//                                                                }
//
//                                                                @Override
//                                                                public void onError(String errorCode, String errorMessage) {
//
//                                                                }
//                                                            });
//
//                                                        }
//
//                                                        @Override
//                                                        public void onError(String errorCode, String errorMessage) {
//                                                            Log.d("switch2DeviceDp" , errorMessage );
//                                                        }
//                                                    });
//                                                }
//
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onError(String errorCode, String errorMessage) {
//                                            // ToastUtil.shortToast(mContext,errorMessage);
//                                        }
//                                    });
//
//                                }
//
//                                @Override
//                                public void onError(String errorCode, String errorMessage) {
//                                    Log.d("switch1DeviceDp" , errorMessage );
//                                }
//                            });
//
//
//
//                            if (THEROOM.getSWITCH1_B().dps.get("3") !=null && THEROOM.getSWITCH2_B().dps.get("3") != null){
//                                iTuyaDeviceMultiControl.getDeviceDpInfoList(THEROOM.getSWITCH1_B().devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
//                                    @Override
//                                    public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
//                                        Log.d("switch1DeviceDp" , result.get(2).getDpId() );
//                                        iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH1_B().devId, result.get(2).getDpId(), new ITuyaDataCallback<MultiControlLinkBean>() {
//                                            @Override
//                                            public void onSuccess(MultiControlLinkBean result) {
//
//                                                if (result.getMultiGroup() != null ){
//                                                    Log.d("switch1DeviceDp" , "1 DP subscriped to "+result.getMultiGroup().getGroupName() );
//                                                }
//                                                else {
//                                                    iTuyaDeviceMultiControl.getDeviceDpInfoList(THEROOM.getSWITCH2_B().devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
//                                                        @Override
//                                                        public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
//                                                            Log.d("switch1DeviceDp" , result.get(2).getDpId() );
//                                                            iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH2_B().devId, result.get(2).getDpId(), new ITuyaDataCallback<MultiControlLinkBean>() {
//                                                                @Override
//                                                                public void onSuccess(MultiControlLinkBean result) {
//                                                                    if (result.getMultiGroup() != null){
//                                                                        Log.d("switch1DeviceDp" , "2 DP subscriped to "+result.getMultiGroup().getGroupName() );
//
//                                                                    }
//                                                                    else {
//                                                                        Random r = new Random();
//                                                                        int xxx = r.nextInt(30);
//                                                                        JSONObject groupdetailes1 = new JSONObject(),groupdetailes2 = new JSONObject();
//                                                                        try
//                                                                        {
//                                                                            groupdetailes1.put("devId",Switch1Bean.devId);
//                                                                            groupdetailes1.put("dpId" ,3 );
//                                                                            groupdetailes1.put("id" ,xxx );
//                                                                            groupdetailes1.put("enable" ,true );
//
//                                                                        }
//                                                                        catch (JSONException e){}
//                                                                        try
//                                                                        {
//                                                                            groupdetailes2.put("devId",Switch2Bean.devId);
//                                                                            groupdetailes2.put("dpId" ,3 );
//                                                                            groupdetailes2.put("id" ,xxx );
//                                                                            groupdetailes2.put("enable" ,true );
//
//                                                                        }
//                                                                        catch (JSONException e){}
//                                                                        JSONArray arr = new JSONArray( );
//                                                                        arr.put(groupdetailes2);
//                                                                        arr.put(groupdetailes1);
//                                                                        JSONObject multiControlBean = new JSONObject();
//                                                                        try
//                                                                        {
//                                                                            multiControlBean.put("groupName",LogIn.room.getRoomNumber()+"Lighting2");
//                                                                            multiControlBean.put("groupType",1);
//                                                                            multiControlBean.put("groupDetail" , arr);
//                                                                            multiControlBean.put("id",xxx);
//                                                                        }
//                                                                        catch (JSONException e)
//                                                                        {
//
//                                                                        }
//                                                                        iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString() , new ITuyaResultCallback<MultiControlBean>() {
//                                                                            @Override
//                                                                            public void onSuccess(MultiControlBean result) {
//                                                                                //ToastUtil.shortToast(mContext,"success");
//                                                                                Log.d("switch1DeviceDp" , result.getGroupName() );
//                                                                                iTuyaDeviceMultiControl.enableMultiControl(xxx, new ITuyaResultCallback<Boolean>() {
//                                                                                    @Override
//                                                                                    public void onSuccess(Boolean result) {
//                                                                                        //ToastUtil.shortToast(mContext,"success");
//                                                                                        Log.d("MultiControlResult" , result.toString());
//                                                                                    }
//
//                                                                                    @Override
//                                                                                    public void onError(String errorCode, String errorMessage) {
//                                                                                        //ToastUtil.shortToast(mContext,errorMessage);
//                                                                                        Log.d("MultiControlResult" , errorMessage);
//                                                                                    }
//                                                                                });
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onError(String errorCode, String errorMessage) {
//                                                                                //ToastUtil.shortToast(mContext,errorMessage);
//                                                                                Log.d("switch1DeviceDp" , errorMessage );
//                                                                            }
//                                                                        });
//                                                                    }
//                                                                }
//
//                                                                @Override
//                                                                public void onError(String errorCode, String errorMessage) {
//
//                                                                }
//                                                            });
//
//                                                        }
//
//                                                        @Override
//                                                        public void onError(String errorCode, String errorMessage) {
//                                                            Log.d("switch2DeviceDp" , errorMessage );
//                                                        }
//                                                    });
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onError(String errorCode, String errorMessage) {
//                                                // ToastUtil.shortToast(mContext,errorMessage);
//                                            }
//                                        });
//
//                                    }
//
//                                    @Override
//                                    public void onError(String errorCode, String errorMessage) {
//                                        Log.d("switch1DeviceDp" , errorMessage );
//                                    }
//                                });
//                            }
//
//                            if (THEROOM.getSWITCH1_B().dps.get("4") != null && THEROOM.getSWITCH2_B().dps.get("4") != null){
//                                iTuyaDeviceMultiControl.getDeviceDpInfoList(THEROOM.getSWITCH1_B().devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
//                                    @Override
//                                    public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
//                                        Log.d("switch1DeviceDp" , result.get(3).getDpId() );
//                                        iTuyaDeviceMultiControl.queryLinkInfoByDp(Switch1Bean.devId, result.get(3).getDpId(), new ITuyaDataCallback<MultiControlLinkBean>() {
//                                            @Override
//                                            public void onSuccess(MultiControlLinkBean result) {
//
//                                                if (result.getMultiGroup() != null ){
//                                                    Log.d("switch1DeviceDp" , "1 DP subscriped to "+result.getMultiGroup().getGroupName() );
//                                                }
//                                                else {
//                                                    iTuyaDeviceMultiControl.getDeviceDpInfoList(Switch2Bean.devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
//                                                        @Override
//                                                        public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
//                                                            Log.d("switch1DeviceDp" , result.get(3).getDpId() );
//                                                            iTuyaDeviceMultiControl.queryLinkInfoByDp(Switch2Bean.devId, result.get(3).getDpId(), new ITuyaDataCallback<MultiControlLinkBean>() {
//                                                                @Override
//                                                                public void onSuccess(MultiControlLinkBean result) {
//                                                                    if (result.getMultiGroup() != null){
//                                                                        Log.d("switch1DeviceDp" , "2 DP subscriped to "+result.getMultiGroup().getGroupName() );
//
//                                                                    }
//                                                                    else {
//                                                                        Random r = new Random();
//                                                                        int xxx = r.nextInt(30);
//                                                                        JSONObject groupdetailes1 = new JSONObject(),groupdetailes2 = new JSONObject();
//                                                                        try
//                                                                        {
//                                                                            groupdetailes1.put("devId",Switch1Bean.devId);
//                                                                            groupdetailes1.put("dpId" ,4 );
//                                                                            groupdetailes1.put("id" ,xxx );
//                                                                            groupdetailes1.put("enable" ,true );
//
//                                                                        }
//                                                                        catch (JSONException e){}
//                                                                        try
//                                                                        {
//                                                                            groupdetailes2.put("devId",Switch2Bean.devId);
//                                                                            groupdetailes2.put("dpId" ,4 );
//                                                                            groupdetailes2.put("id" ,xxx);
//                                                                            groupdetailes2.put("enable" ,true );
//
//                                                                        }
//                                                                        catch (JSONException e){}
//                                                                        JSONArray arr = new JSONArray( );
//                                                                        arr.put(groupdetailes2);
//                                                                        arr.put(groupdetailes1);
//                                                                        JSONObject multiControlBean = new JSONObject();
//                                                                        try
//                                                                        {
//                                                                            multiControlBean.put("groupName",LogIn.room.getRoomNumber()+"Lighting"+xxx);
//                                                                            multiControlBean.put("groupType",1);
//                                                                            multiControlBean.put("groupDetail" , arr);
//                                                                            multiControlBean.put("id",xxx);
//                                                                        }
//                                                                        catch (JSONException e)
//                                                                        {
//
//                                                                        }
//                                                                        iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString() , new ITuyaResultCallback<MultiControlBean>() {
//                                                                            @Override
//                                                                            public void onSuccess(MultiControlBean result) {
//                                                                                //ToastUtil.shortToast(mContext,"success");
//                                                                                Log.d("switch1DeviceDp" , result.getGroupName() );
//                                                                                iTuyaDeviceMultiControl.enableMultiControl(xxx, new ITuyaResultCallback<Boolean>() {
//                                                                                    @Override
//                                                                                    public void onSuccess(Boolean result) {
//                                                                                        //ToastUtil.shortToast(mContext,"success");
//                                                                                        Log.d("MultiControlResult" , result.toString());
//                                                                                    }
//
//                                                                                    @Override
//                                                                                    public void onError(String errorCode, String errorMessage) {
//                                                                                        //ToastUtil.shortToast(mContext,errorMessage);
//                                                                                        Log.d("MultiControlResult" , errorMessage);
//                                                                                    }
//                                                                                });
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onError(String errorCode, String errorMessage) {
//                                                                                //ToastUtil.shortToast(mContext,errorMessage);
//                                                                                Log.d("switch1DeviceDp" , errorMessage );
//                                                                            }
//                                                                        });
//                                                                    }
//                                                                }
//
//                                                                @Override
//                                                                public void onError(String errorCode, String errorMessage) {
//
//                                                                }
//                                                            });
//
//                                                        }
//
//                                                        @Override
//                                                        public void onError(String errorCode, String errorMessage) {
//                                                            Log.d("switch2DeviceDp" , errorMessage );
//                                                        }
//                                                    });
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onError(String errorCode, String errorMessage) {
//                                                // ToastUtil.shortToast(mContext,errorMessage);
//                                            }
//                                        });
//
//                                    }
//
//                                    @Override
//                                    public void onError(String errorCode, String errorMessage) {
//                                        Log.d("switch1DeviceDp" , errorMessage );
//                                    }
//                                });
//                            }
//
//                             */
//                        }
//                        else
//                        {
//                            ToastMaker.MakeToast("No Zigbee Router" , act);
//                        }
//                    }
//                    catch (Exception e)
//                    {
//                        Log.e("DevicesError" , e.getMessage());
//                    }
//                    adapter.notifyDataSetChanged();
//
//                }
//                @Override
//                public void onError(String errorCode, String errorMessage) {
//
//                }
//            });
//
//        }
//        if (Tuya_Devices.AC == null ) {
//            ACStatus = false ;
//            setThermostatStatus("0");
//            showAc.setVisibility(View.GONE);
//            THEROOM.setAC(null);
//            THEROOM.setAC_B(null);
//        }
//        else {
//            ACStatus = true ;
//            setThermostatStatus("1");
//            showAc.setVisibility(View.VISIBLE);
//            THEROOM.setAC(Tuya_Devices.AC);
//            THEROOM.setAC_B(Tuya_Devices.ACbean);
//            try {
//                Tuya_Devices.AC.registerDeviceListener(new IDeviceListener() {
//                    @Override
//                    public void onDpUpdate(String devId, Map<String, Object> dpStr)
//                    {
//                        Log.d("ACeee" , dpStr.toString());
//                        if (dpStr.get("temp_current") != null)
//                        {
//                            CurrentTemp = dpStr.get("temp_current").toString() ;
//                            double temp = (Integer.parseInt(dpStr.get("temp_current").toString())*0.1);
//                            myRefTemp.setValue(String.valueOf(temp));
//                            TextView currentTempText = (TextView) findViewById(R.id.currentTemp);
//                            currentTempText.setText(String.valueOf(temp));
//                        }
//                        if ( dpStr.get("temp_set") != null )
//                        {
//                            if (Double.parseDouble(dpStr.get("temp_set").toString()) !=  Double.parseDouble(TempSetPoint))
//                            {
//                                ClientTemp = dpStr.get("temp_set").toString();
//                                int x = Integer.parseInt(dpStr.get("temp_set").toString());
//                                double y = x*0.1 ;
//                                TextView clientTempText = (TextView) findViewById(R.id.clientTemp);
//                                clientTempText.setText(String.valueOf(y));
//                                FromAc = true ;
//                                arcSeekBar.setProgress((int) y-16);
//                            }
//                        }
//                        if (dpStr.get("switch") != null) {
//                            if (dpStr.get("switch").toString().equals("true")) {
//                                Button onOff = (Button) findViewById(R.id.onOffBtn);
//                                onOff.setBackgroundResource(R.drawable.ac_on);
//                                TextView clientSelectedTemp = (TextView) findViewById(R.id.clientTemp);
//                                TextView fanText = (TextView) findViewById(R.id.fanSpeed);
//                                Button fanSpeed = (Button) findViewById(R.id.fanSpeedBtn);
//                                Button tempUp = (Button) findViewById(R.id.tempUpBtn);
//                                Button tempDown = (Button) findViewById(R.id.tempDownBtn);
//                                clientSelectedTemp.setVisibility(View.VISIBLE);
//                                fanSpeed.setVisibility(View.VISIBLE);
//                                tempUp.setVisibility(View.VISIBLE);
//                                tempDown.setVisibility(View.VISIBLE);
//                                arcSeekBar.setVisibility(View.VISIBLE);
//                                fanText.setVisibility(View.VISIBLE);
//                            }
//                            else {
//                                Button onOff = (Button) findViewById(R.id.onOffBtn);
//                                onOff.setBackgroundResource(R.drawable.ac_off);
//                                TextView clientSelectedTemp = (TextView) findViewById(R.id.clientTemp);
//                                Button fanSpeed = (Button) findViewById(R.id.fanSpeedBtn);
//                                Button tempUp = (Button) findViewById(R.id.tempUpBtn);
//                                Button tempDown = (Button) findViewById(R.id.tempDownBtn);
//                                TextView fanText = (TextView) findViewById(R.id.fanSpeed);
//                                clientSelectedTemp.setVisibility(View.INVISIBLE);
//                                fanSpeed.setVisibility(View.INVISIBLE);
//                                tempUp.setVisibility(View.INVISIBLE);
//                                tempDown.setVisibility(View.INVISIBLE);
//                                arcSeekBar.setVisibility(View.INVISIBLE);
//                                fanText.setVisibility(View.INVISIBLE);
//                            }
//                        }
//                        if (dpStr.get("level") != null) {
//                            TextView fanSpeedText = (TextView) findViewById(R.id.fanSpeed);
//                            if (dpStr.get("level").toString().equals("med")) {
//                                fanSpeedText.setText("Med");
//                            }
//                            else if (dpStr.get("level").toString().equals("high")) {
//                                fanSpeedText.setText("High");
//                            }
//                            else if (dpStr.get("level").toString().equals("auto")) {
//                                fanSpeedText.setText("Auto");
//                            }
//                            else if (dpStr.get("level").toString().equals("low")) {
//                                fanSpeedText.setText("Low");
//                            }
//                        }
//                    }
//                    @Override
//                    public void onRemoved(String devId) {
//
//                    }
//
//                    @Override
//                    public void onStatusChanged(String devId, boolean online) {
//
//                    }
//
//                    @Override
//                    public void onNetworkStatusChanged(String devId, boolean status) {
//
//                    }
//
//                    @Override
//                    public void onDevInfoUpdate(String devId)
//                    {
//
//                    }
//                });
//            }
//            catch (Exception e )
//            {
//
//            }
//        }
//        if (Tuya_Devices.mDevice == null ) {
//            PowerControllerStatus = false ;
//            setPowerSwitchStatus("0");
//        }
//        else {
//            Log.d("PowerDps",Tuya_Devices.powerBean.dps.toString() );
//            PowerControllerStatus = true ;
//            setPowerSwitchStatus("1");
//            try {
//                Tuya_Devices.mDevice.registerDeviceListener(new IDeviceListener() {
//                    @Override
//                    public void onDpUpdate(String devId, Map<String, Object> dpStr)
//                    {
//                        Log.d("DpUpdates" , devId+"  " + String.valueOf(dpStr)) ;
//                        if (dpStr.get("switch_2") != null)
//                        {
//                            String S1 = dpStr.get("switch_2").toString() ;
//                            if (S1.equals("false"))
//                            {
//                                //myRefPower.setValue(0);
//                            }
//                            else
//                            {
//                                //myRefPower.setValue(1);
//                            }
//                        }
//
//                    }
//                    @Override
//                    public void onRemoved(String devId)
//                    {
//
//                    }
//                    @Override
//                    public void onStatusChanged(String devId, boolean online)
//                    {
//                        Log.d("DpStatusChanged" , String.valueOf(online) + " " + devId ) ;
//                    }
//                    @Override
//                    public void onNetworkStatusChanged(String devId, boolean status)
//                    {
//
//                    }
//                    @Override
//                    public void onDevInfoUpdate(String devId)
//                    {
//                        Log.d("onDevInfoUpdate" ,  devId ) ;
//                    }
//                });
//            }
//            catch (Exception e)
//            {
//
//            }
//        }
//        if (LogIn.myLock == null ) {
//            LockStatus = false ;
//            setLockStatus("0");
//            OpenDoor.setVisibility(View.INVISIBLE);
//            THEROOM.setLock(null);
//        }
//        else {
//            LockStatus = true ;
//            myTestLockEKey = LogIn.myLock ;
//            setLockStatus("1");
//            THEROOM.setLock(LogIn.myLock);
//            OpenDoor.setVisibility(View.VISIBLE);
//        }

    }

    void setActivityActions() {
        TextView roomnumber = findViewById(R.id.RoomNumber_MainScreen);
        ServicesBtn.setOnClickListener(v -> hideMainBtns());
        serviceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x=0;
            }
        });
        lightsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x=0;
            }
        });
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x=0;
            }
        });
        mainLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                x=0;
                return false;
            }
        });
        showAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAcLayout();
                x=0;
            }
        });
        laundryPriceList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                /*
                Button home = (Button) findViewById(R.id.home_inLaundry);
                home.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        LaundryLayout.setVisibility(View.GONE);
                        AcLayout.setVisibility(View.GONE);
                        Lighting.setVisibility(View.GONE);
                        Services.setVisibility(View.VISIBLE);
                        MinibarLayout.setVisibility(View.GONE);
                    }
                });
                if (Laundries != null)
                {
                    if (Laundries.size() > 1) {
                        String[] laundryNames = new String[Laundries.size()];
                        for (int i = 0; i < Laundries.size(); i++) {
                            laundryNames[i] = Laundries.get(i).Name;
                        }
                        LinearLayout l = (LinearLayout) findViewById(R.id.many_Laundries_layout);
                        l.setVisibility(View.VISIBLE);
                        Spinner laundriesSpinner = (Spinner) findViewById(R.id.laundries_spinner);
                        ArrayAdapter adapter = new ArrayAdapter(act, R.layout.spinners_item, laundryNames);
                        laundriesSpinner.setAdapter(adapter);
                        laundriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                getLaundryMenu(Laundries.get(position).id);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else {
                        LinearLayout l = (LinearLayout) findViewById(R.id.many_Laundries_layout);
                        l.setVisibility(View.GONE);
                    }
                    LaundryLayout.setVisibility(View.VISIBLE);
                    AcLayout.setVisibility(View.GONE);
                    Lighting.setVisibility(View.GONE);
                    Services.setVisibility(View.GONE);
                    MinibarLayout.setVisibility(View.GONE);
                    getLaundryMenu(Laundries.get(0).id);
                }
                else
                {
                    ToastMaker.MakeToast("Getting Laundry Data Please Wait ",act);
                }

                 */
            }
        });
        ShowMiniBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout Services = findViewById(R.id.ServicesLayout);
                LinearLayout Lighting = findViewById(R.id.lightingLayout);
                LinearLayout AcLayout = findViewById(R.id.ac_layout);
                LinearLayout LaundryLayout = findViewById(R.id.Laundry_layout);
                LinearLayout MinibarLayout = findViewById(R.id.Minibar_layout);
                MinibarLayout.setVisibility(View.VISIBLE);
                LaundryLayout.setVisibility(View.GONE);
                AcLayout.setVisibility(View.GONE);
                Lighting.setVisibility(View.GONE);
                Services.setVisibility(View.GONE);
                if (Minibar.size()>0) {
                    getMiniBarMenu(Minibar.get(0).id);
                }
            }
        });
        roomnumber.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Dialog  dd = new Dialog(act);
                dd.setContentView(R.layout.logout_of_room_dialog);
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
                        final LoadingDialog loading = new LoadingDialog(act);
                        final String pass = password.getText().toString() ;
                        StringRequest re = new StringRequest(Request.Method.POST, MyApp.ProjectURL + "users/loginProject", new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                loading.stop();
                                if (response != null) {
                                    try {
                                        JSONObject resp = new JSONObject(response);
                                        if (resp.getString("result").equals("success")) {
                                            dd.dismiss();
                                            logout();
                                        }
                                        else {
                                            Toast.makeText(act,"Logout Failed " + resp.getString("error"),Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(act,"Logout Failed " + e,Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                loading.stop();
                                new messageDialog(error.toString(),"Failed",act);
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> par = new HashMap<String, String>();
                                par.put( "password" , pass ) ;
                                par.put( "project_name" ,MyApp.ProjectName) ;
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
        LaundryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (THEROOM.getSERVICE1_B() != null) {
                    if (THEROOM.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.laundryButton)) != null) {
                        if (LaundryStatus) { //Boolean.parseBoolean(Objects.requireNonNull(THEROOM.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.laundryButton))).toString())
                            THEROOM.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.laundryButton+"\":false}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    THEROOM.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.laundryButton+"\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {
                                            new messageDialog(error+" "+code,"failed",act);
                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                        else {
                            THEROOM.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.laundryButton+"\":true}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    THEROOM.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.laundryButton+"\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {
                                            new messageDialog(error+" "+code,"failed",act);
                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                    }
                }
                x=0;
            }
        });
        CleanUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (THEROOM.getSERVICE1_B() != null) {
                    if (THEROOM.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.cleanupButton)) != null) {
                        if (CleanupStatus) { //Boolean.parseBoolean(Objects.requireNonNull(THEROOM.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.cleanupButton))).toString())
                            THEROOM.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.cleanupButton+"\":false}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    THEROOM.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.cleanupButton+"\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {
                                            new messageDialog(error+" "+code,"failed",act);
                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                        else {
                            THEROOM.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.cleanupButton+"\":true}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    THEROOM.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.cleanupButton+"\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {
                                            new messageDialog(error+" "+code,"failed",act);
                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                    }
                }
                x=0;
            }
        });
        CheckOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (THEROOM.getSERVICE1_B() != null) {
                    if (THEROOM.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.checkoutButton)) != null) {
                        Log.d("checkout", "not null");
                        if (CheckoutStatus) { //Boolean.parseBoolean(Objects.requireNonNull(THEROOM.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.checkoutButton))).toString())
                            THEROOM.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.checkoutButton+"\":false}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    THEROOM.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.checkoutButton+"\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {
                                            new messageDialog(error+" "+code,"failed",act);
                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                        else {
                            THEROOM.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.checkoutButton+"\":true}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    THEROOM.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.checkoutButton+"\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {
                                            new messageDialog(error+" "+code,"failed",act);
                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                    }
                    else {
                        Log.d("checkout", "null");
                        if (CheckoutStatus) {
                            myRefCheckout.setValue(0);
                            Log.d("checkout", "1000");
                        }
                        else {
                            myRefCheckout.setValue(10);
                            Log.d("checkout", "0");
                        }
                    }
                }
                x=0;
            }
        });
        DNDBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (THEROOM.getSERVICE1_B() != null) {
                    if (THEROOM.getSERVICE1_B().getDps().get(String.valueOf(MyApp.ProjectVariables.dndButton)) != null) {
                        if (DNDStatus) { //Boolean.parseBoolean(Objects.requireNonNull(THEROOM.getSERVICE1_B().getDps().get(String.valueOf(MyApp.ProjectVariables.dndButton))).toString())
                            THEROOM.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.dndButton+"\":false}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    THEROOM.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.dndButton+"\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {
                                            new messageDialog(error+" "+code,"failed",act);
                                        }
                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }
                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                        else {
                            THEROOM.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.dndButton+"\":true}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    THEROOM.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.dndButton+"\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {
                                            new messageDialog(error+" "+code,"failed",act);
                                        }
                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }
                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                    }
                }
            }
        });
        SOSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CURRENT_ROOM_STATUS == 2) {
                    if (!SosStatus) {
                        final Dialog d = new Dialog(act);
                        d.setContentView(R.layout.confermation_dialog);
                        Window w = d.getWindow();
                        w.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                        TextView message = d.findViewById(R.id.confermationDialog_Text);
                        message.setText("Send Emergency Order .. ?");
                        Button cancel = (Button)d.findViewById(R.id.confermationDialog_cancel);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                d.dismiss();
                            }
                        });
                        Button ok = (Button)d.findViewById(R.id.messageDialog_ok);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SosStatus = true ;
                                sosOn();
                                Calendar c = Calendar.getInstance(Locale.getDefault());
                                myRefSos.setValue(c.getTimeInMillis());
                                d.dismiss();
                                String url = MyApp.ProjectURL + "reservations/addSOSOrder";
                                StringRequest addOrder = new StringRequest(Request.Method.POST, url , new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("sosResp" , response);
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("sosResp" , error.toString());
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String,String> params = new HashMap<>();
                                        params.put("room_id" ,String.valueOf(THEROOM.id));
                                        return params;
                                    }
                                };
                                Volley.newRequestQueue(act).add(addOrder);
                            }
                        });
                        d.show();
                    }
                    else {
                        SosStatus = false ;
                        sosOff();
                        myRefSos.setValue(0);
                        String url = MyApp.ProjectURL + "reservations/cancelServiceOrderControlDevice"+sosCounter;
                        StringRequest removOrder = new StringRequest(Request.Method.POST, url , new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("sosResp" , response);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("room_id" , String.valueOf(MyApp.Room.id));
                                params.put("order_type" , "SOS");
                                return params;
                            }
                        };
                        Volley.newRequestQueue(act).add(removOrder);
                        sosCounter++ ;
                        if (sosCounter == 5) {
                            sosCounter = 1 ;
                        }
                    }
                }
                else {
                    ToastMaker.MakeToast("This Room Is Vacant" , act);
                }
            }
        });
        ShowLighting.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Dialog  dd = new Dialog(act);
                dd.setContentView(R.layout.logout_of_room_dialog);
                Button cancel = (Button) dd.findViewById(R.id.confermationDialog_cancel);
                Button lock = (Button) dd.findViewById(R.id.messageDialog_ok);
                TextView title = dd.findViewById(R.id.textView2);
                title.setText(getResources().getString(R.string.lights));
                TextView message = dd.findViewById(R.id.confermationDialog_Text);
                message.setText(getResources().getString(R.string.lights));
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
                        LoadingDialog loading = new LoadingDialog(act);
                        StringRequest re = new StringRequest(Request.Method.POST, MyApp.ProjectURL + "users/loginProject", new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                loading.stop();
                                if (response != null) {
                                    try {
                                        JSONObject resp = new JSONObject(response);
                                        if (resp.getString("result").equals("success")) {
                                            dd.dismiss();
                                            Intent i = new Intent(act,LightingControl.class);
                                            startActivity(i);
                                        }
                                        else {
                                            Toast.makeText(act,"Logout Failed " + resp.getString("error"),Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(act,"Logout Failed " + e.toString(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                loading.stop();
                                new messageDialog(error.toString(),"Failed",act);
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> par = new HashMap<String, String>();
                                par.put( "password" , password.getText().toString() ) ;
                                par.put( "project_name" ,MyApp.ProjectName) ;
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
    }

    public void setFireRoomListeners() {
        myRefLogo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null && !snapshot.getValue().toString().isEmpty() ){
                    LOGO = snapshot.getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefDND.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (Long.parseLong(snapshot.getValue().toString()) > 0 ) {
                        dndOn();
                        DNDStatus = true ;
                        THEROOM.DND = 10 ;
                    }
                    else {
                        dndOff();
                        DNDStatus = false ;
                        THEROOM.DND = 0 ;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
        myRefSos.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (Long.parseLong(snapshot.getValue().toString()) > 0 ) {
                        SosStatus = true ;
                        sosOn();
                        THEROOM.SOS = 10 ;
                    }
                    else {
                        SosStatus = false ;
                        sosOff();
                        THEROOM.SOS = 0 ;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefLaundry.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (Long.parseLong(snapshot.getValue().toString()) > 0 ) {
                        THEROOM.Laundry = 10 ;
                        LaundryStatus = true ;
                        laundryOn();
                    }
                    else {
                        THEROOM.Laundry = 0 ;
                        LaundryStatus = false ;
                        laundryOff();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefCleanup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (Long.parseLong(snapshot.getValue().toString())>0) {
                        cleanupOn();
                        CleanupStatus = true ;
                        THEROOM.Cleanup = 10 ;
                    }
                    else {
                        cleanupOff();
                        CleanupStatus = false ;
                        THEROOM.Cleanup = 0 ;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefCheckout.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (Long.parseLong(snapshot.getValue().toString()) > 0 ) {
                        CheckoutStatus = true ;
                        checkoutOn();
                        THEROOM.Checkout = 10 ;
                    }
                    else {
                        checkoutOff();
                        CheckoutStatus = false ;
                        THEROOM.Checkout = 0 ;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefRoomService.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if ( !snapshot.getValue().toString().equals("0") ) {
                        roomServiceOn();
                        RoomServiceStatus = true ;
                        THEROOM.RoomService = 10 ;
                    }
                    else {
                        RoomServiceStatus = false ;
                        roomServiceOff();
                        THEROOM.RoomService = 0 ;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefRestaurant.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (Long.parseLong(snapshot.getValue().toString()) > 0 ) {
                        THEROOM.Restaurant = 10 ;
                        RestaurantStatus = true ;
                        restaurantOn();
                    }
                    else {
                        THEROOM.Restaurant = 0 ;
                        restaurantOff();
                        RestaurantStatus = false ;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
        myRefRorS.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (Integer.parseInt(snapshot.getValue().toString()) == 1 ) {
                        RoomOrSuite = 1 ;
                    }
                    else if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                        RoomOrSuite = 2 ;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefReservation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (Integer.parseInt(snapshot.getValue().toString()) > 0 ) {
                        RESERVATION = Integer.parseInt(snapshot.getValue().toString()) ;
                        getReservation();
                    }
                    else {
                        RESERVATION = 0 ;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    ID = Integer.parseInt(snapshot.getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefRoomStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (snapshot.getValue().toString().equals("2")) {
                        TextView text = (TextView) findViewById(R.id.textView36);
                        text.setText(getResources().getString(R.string.welcomeRoom));
                    }
                    else if (snapshot.getValue().toString().equals("1")) {
                        TextView fname = (TextView) findViewById(R.id.client_Name);
                        TextView text = (TextView) findViewById(R.id.textView36);
                        text.setText("");
                        fname.setText(getResources().getString(R.string.roomVacant));
                    }
                    else if (snapshot.getValue().toString().equals("3")) {
                        TextView fname = (TextView) findViewById(R.id.client_Name);
                        TextView text = (TextView) findViewById(R.id.textView36);
                        text.setText("");
                        fname.setText(getResources().getString(R.string.roomIsUnready));
                    }
                    else if (snapshot.getValue().toString().equals("4")) {
                        TextView fname = (TextView) findViewById(R.id.client_Name);
                        TextView text = (TextView) findViewById(R.id.textView36);
                        text.setText("");
                        fname.setText(getResources().getString(R.string.roomIsOutOfService));
                    }
                    CURRENT_ROOM_STATUS = Integer.parseInt( snapshot.getValue().toString() );
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //        myRefCheckOutDuration.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.getValue() != null ){
//                    if (!snapshot.getValue().toString().equals("0")) {
//                        checkOutModeTime = Integer.parseInt( snapshot.getValue().toString());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        myRefCheckInDuration.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.getValue() != null ) {
//                    if (snapshot.getValue().toString().equals("0")) {
//                        checkInModeTime = Integer.parseInt( snapshot.getValue().toString());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        myRefDoorWarning.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot)
//            {
//                if (snapshot.getValue() != null ) {
//                    try {
//                        theDoorTime = 1000 * 60 * Integer.parseInt(snapshot.getValue().toString());
//                        Log.d("Doorinterval", theDoorTime + "");
//                    } catch (Exception e) {
//
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        myRefSetpoint.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot)
//            {
//                try
//                {
//                    if (Integer.parseInt(snapshot.getValue().toString()) > 15 )
//                    {
//                        String temp = snapshot.getValue().toString() ;
//                        if (temp.length()==2)
//                        {
//                            temp = temp+"0";
//                            TempSetPoint = temp ;
//                        }
//                        else if (temp.length()>2)
//                        {
//                            TempSetPoint = temp ;
//                        }
//                        //TempSetPoint[finalI] = snapshot.getValue().toString();
//                    }
//
//                }
//                catch (Exception e)
//                {
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        myRefSetpointInterval.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot)
//            {
//                if (snapshot.getValue() != null)
//                {
//                    try {
//                        theTime = 1000 * 60 * Integer.parseInt(snapshot.getValue().toString());
//                        Log.d("intervalsetpoint", theTime + "");
//                    } catch (Exception e) {
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    public static void roomServiceShowDialog(View view) {
            if (CURRENT_ROOM_STATUS == 2) {
                if (RoomServiceStatus) {
                    removeRoomServiceOrderInDataBase();
//                    final Dialog d = new Dialog(act);
//                    d.setContentView(R.layout.confermation_dialog);
//                    TextView message = (TextView) d.findViewById(R.id.confermationDialog_Text);
//                    message.setText("You Are Cancelling RoomService Order .. Are You Sure");
//                    Button cancel = (Button)d.findViewById(R.id.confermationDialog_cancel);
//                    cancel.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v)
//                        {
//                            d.dismiss();
//                        }
//                    });
//                    Button ok = (Button)d.findViewById(R.id.messageDialog_ok);
//                    ok.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            d.dismiss();
//                        }
//                    });
//                    d.show();
                }
                else {
                    addRoomServiceOrderInDataBase();
//                    final Dialog d = new Dialog(act);
//                    d.setContentView(R.layout.confermation_dialog);
//                    TextView message = (TextView) d.findViewById(R.id.confermationDialog_Text);
//                    message.setText("You Are Sending RoomService Order .. Are You Sure");
//                    Button cancel = (Button)d.findViewById(R.id.confermationDialog_cancel);
//                    cancel.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v)
//                        {
//                            d.dismiss();
//                        }
//                    });
//                    Button ok = (Button)d.findViewById(R.id.messageDialog_ok);
//                    ok.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            d.dismiss();
//
//                        }
//                    });
//                    d.show();
                }
            }
            else {
                ToastMaker.MakeToast("This Room Is Vacant" , act);
            }
        x=0;
    }

    public void goToRestaurant(View view) {
        if (CURRENT_ROOM_STATUS == 2) {
            if (THEROOM.Restaurant > 0) {
                new messageDialog("لديك طلب من المطعم مسبقا","Tou have Order",act);
            }
            else {
                Intent i = new Intent(act , RESTAURANTS.class);
                startActivity(i);
            }
        }
        else {
            ToastMaker.MakeToast("This Room Is Vacant" , act);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void GymBtn(View view) {
        if (CURRENT_ROOM_STATUS == 2) {
            final Dialog d = new Dialog(act);//d.setContentView(R.layout.gym_dialog_select_time);
            View v = LayoutInflater.from(act).inflate(R.layout.gym_dialog_select_time, null);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            v.setMinimumHeight(height);
            v.setMinimumWidth(width);
            d.setContentView(v);
            final TextView timeText = (TextView) d.findViewById(R.id.gym_dialog_text);
            final TimePicker t = (TimePicker) d.findViewById(R.id.timpicker);
            Button ok = (Button) d.findViewById(R.id.button7);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timeText.setText(t.getHour() + ":" + t.getMinute());
                    Calendar x = Calendar.getInstance(Locale.getDefault());
                    double timee = x.getTimeInMillis();

                    String url = LogIn.URL+"insertGymOrder.php";
                    StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            d.dismiss();
                            if (response.equals("0")) {
                                ToastMaker.MakeToast("Gym Order not Sent ", act);
                            } else {
                                ToastMaker.MakeToast("Gym Order Sent ", act);
                                myRefGym.child("orderNumber").setValue(String.valueOf(response));
                                myRefGym.child("status").setValue(1);
                                myRefGym.child("time").setValue(timee);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            d.dismiss();
                            ToastMaker.MakeToast(error.getMessage(), act);

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> par = new HashMap<String, String>();
                            par.put("roomNumber", String.valueOf(MyApp.Room.RoomNumber));
                            Calendar x = Calendar.getInstance(Locale.getDefault());
                            double time = x.getTimeInMillis();
                            par.put("orderTime", String.valueOf(time));
                            Calendar f = Calendar.getInstance();
                            f.set(x.get(Calendar.YEAR), x.get(Calendar.MONTH), x.get(Calendar.DAY_OF_MONTH), t.getHour(), t.getMinute());
                            par.put("gymTime", String.valueOf(f.getTimeInMillis()));
                            par.put("RorS", String.valueOf(RoomOrSuite));
                            par.put("Reservation", String.valueOf(RESERVATION));
                            return par;
                        }
                    };
                    Volley.newRequestQueue(act).add(request);
                }
            });
            Button cancel = (Button) d.findViewById(R.id.button9);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });
            d.show();
        }
        else
        {
            ToastMaker.MakeToast("This Room Is Vacant" , act);
        }

    }

    public void getReservation() {
        String url = MyApp.ProjectURL + "reservations/getRoomReservation";
        StringRequest getReservationRe = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("reservationResp",response);
                try {
                    JSONObject result = new JSONObject(response);
                    if (result.getString("result").equals("success")) {
                        JSONObject row = result.getJSONObject("reservation");
                        THERESERVATION = new RESERVATION(row.getInt("id"),row.getInt("RoomNumber"),row.getInt("ClientId"),row.getInt("Status"),
                                row.getInt("RoomOrSuite"),row.getInt("MultiRooms"),row.getString("AddRoomNumber"),row.getString("AddRoomId"),row.getString("StartDate"),
                                row.getInt("Nights"),row.getString("EndDate"),row.getInt("Hotel"),row.getInt("BuildingNo"),row.getInt("Floor"),row.getString("ClientFirstName"),row.getString("ClientLastName"),row.getString("IdType"),
                                row.getInt("IdNumber"),row.getInt("MobileNumber"),row.getString("Email"),row.getInt("Rating"));
                        TextView fname = (TextView) findViewById(R.id.client_Name);
                        TextView checkin = (TextView) findViewById(R.id.check_In_Date);
                        TextView checkout = (TextView) findViewById(R.id.check_out_Date);
                        fname.setText(THERESERVATION.ClientFirstName + " " + THERESERVATION.ClientLastName);
                        checkin.setText("in:"+THERESERVATION.StartDate);
                        checkout.setText("out:"+THERESERVATION.EndDate);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    new messageDialog(e.getMessage(),"Failed to get reservation",act);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new messageDialog(error.toString(),"Failed to get reservation",act);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> Params = new HashMap<String,String>();
                Params.put("reservation_id", String.valueOf(RESERVATION));
                Params.put("room_number" , String.valueOf(THEROOM.RoomNumber));
                return Params;
            }
        };
        Volley.newRequestQueue(act).add(getReservationRe);
    }

//-------------------------------------------------------------

    void createAcLayout() {
        if (THEROOM.getAC_B() != null) {
            LinearLayout Btns = findViewById(R.id.MainBtns_Layout);
            TextView Text = findViewById(R.id.RoomNumber_MainScreen);
            TextView Caption = findViewById(R.id.textView37);
            LinearLayout home = findViewById(R.id.home_Btn);
            LinearLayout AcLayout = findViewById(R.id.ac_layout);
            home.setVisibility(View.VISIBLE);
            AcLayout.setVisibility(View.VISIBLE);
            Btns.setVisibility(View.GONE);
            Text.setVisibility(View.GONE);
            Caption.setVisibility(View.VISIBLE);
            Caption.setText(getResources().getString(R.string.ac));
            startBackHomeThread();
        }
    }

    void setTheAcLayout() {
        TextView clientSelectedTemp = findViewById(R.id.clientTemp);
        TextView currentTempText = findViewById(R.id.currentTemp);
        TextView fanSpeedText = findViewById(R.id.fanSpeed);
        Button onOf = findViewById(R.id.onOffBtn);
        Button fanSpeed = findViewById(R.id.fanSpeedBtn);
        Button tempUp = findViewById(R.id.tempUpBtn);
        Button tempDown = findViewById(R.id.tempDownBtn);
        TuyaHomeSdk.getSceneManagerInstance().getDeviceConditionOperationList(THEROOM.getAC_B().devId, new ITuyaResultCallback<List<TaskListBean>>() {
            @Override
            public void onSuccess(List<TaskListBean> result) {
                long SetId = 0 ;
                TaskListBean SetTask = null ;
                long PowerId = 0 ;
                long CurrentId = 0 ;
                long FanId = 0;
                for (int i=0 ; i<result.size();i++) {
                    if (result.get(i).getName().contains("Set temp") || result.get(i).getName().contains("temp_set") || result.get(i).getName().contains("Set Temperature")) {
                        SetId = result.get(i).getDpId() ;
                        SetTask = result.get(i) ;
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
                if (SetId != 0 && CurrentId != 0) {
                    boolean[] POWER_STATUS = {false} ;
                    String UNIT = SetTask.getValueSchemaBean().getUnit() ;
                    String MAX = String.valueOf(SetTask.getValueSchemaBean().getMax()) ;
                    int max = SetTask.getValueSchemaBean().getMax() ;
                    int min = SetTask.getValueSchemaBean().getMin() ;
                    if (MAX.length() > 2 ) {
                        int roomTempInt = (int) (Integer.parseInt(Objects.requireNonNull(THEROOM.getAC_B().dps.get(String.valueOf(CurrentId))).toString()) * 0.1);
                        int setTempInt = (int) (Integer.parseInt(Objects.requireNonNull(THEROOM.getAC_B().dps.get(String.valueOf(SetId))).toString()) * 0.1);
                        String roomTemp = String.valueOf(roomTempInt);
                        String setTemp = String.valueOf(setTempInt);
                        currentTempText.setText(roomTemp+" "+UNIT);
                        clientSelectedTemp.setText(setTemp+" "+UNIT);
                    }
                    else {
                        int roomTempInt =  Integer.parseInt(THEROOM.getAC_B().dps.get(String.valueOf(CurrentId)).toString()) ;
                        int setTempInt = Integer.parseInt(THEROOM.getAC_B().dps.get(String.valueOf(SetId)).toString());
                        String roomTemp = String.valueOf(roomTempInt);
                        String setTemp = String.valueOf(setTempInt);
                        currentTempText.setText(roomTemp+" "+UNIT);
                        clientSelectedTemp.setText(setTemp+" "+UNIT);
                    }
                    if (PowerId != 0) {
                        POWER_STATUS[0] = Boolean.parseBoolean(THEROOM.getAC_B().dps.get(String.valueOf(PowerId)).toString());
                        if (POWER_STATUS[0]) {
                            tempUp.setVisibility(View.VISIBLE);
                            tempDown.setVisibility(View.VISIBLE);
                            fanSpeed.setVisibility(View.VISIBLE);
                            onOf.setBackgroundResource(R.drawable.ac_on);
                        }
                        else {
                            tempUp.setVisibility(View.INVISIBLE);
                            tempDown.setVisibility(View.INVISIBLE);
                            fanSpeed.setVisibility(View.INVISIBLE);
                            onOf.setBackgroundResource(R.drawable.ac_off);
                        }
                    }
                    long finalSetId = SetId;
                    long finalPowerId = PowerId;
                    long finalFanId = FanId;
                    if (THEROOM.getAC_B().dps.get(String.valueOf(finalFanId)) != null) {
                        if (Objects.requireNonNull(THEROOM.getAC_B().dps.get(String.valueOf(finalFanId))).toString().equals("low") || Objects.requireNonNull(THEROOM.getAC_B().dps.get(String.valueOf(finalFanId))).toString().equals("Low") || Objects.requireNonNull(THEROOM.getAC_B().dps.get(String.valueOf(finalFanId))).toString().equals("1")) {
                            fanSpeedText.setText(getResources().getString(R.string.low));
                        }
                        else if (Objects.requireNonNull(THEROOM.getAC_B().dps.get(String.valueOf(finalFanId))).toString().equals("auto") || Objects.requireNonNull(THEROOM.getAC_B().dps.get(String.valueOf(finalFanId))).toString().equals("Auto") ) {
                            fanSpeedText.setText(getResources().getString(R.string.auto));
                        }
                        else if (Objects.requireNonNull(THEROOM.getAC_B().dps.get(String.valueOf(finalFanId))).toString().equals("High") || Objects.requireNonNull(THEROOM.getAC_B().dps.get(String.valueOf(finalFanId))).toString().equals("high") || Objects.requireNonNull(THEROOM.getAC_B().dps.get(String.valueOf(finalFanId))).toString().equals("3")) {
                            fanSpeedText.setText(getResources().getString(R.string.high));
                        }
                        else if (Objects.requireNonNull(THEROOM.getAC_B().dps.get(String.valueOf(finalFanId))).toString().equals("Med") || Objects.requireNonNull(THEROOM.getAC_B().dps.get(String.valueOf(finalFanId))).toString().equals("med") || Objects.requireNonNull(THEROOM.getAC_B().dps.get(String.valueOf(finalFanId))).toString().equals("2")) {
                            fanSpeedText.setText(getResources().getString(R.string.med));
                        }
                    }
                    View.OnClickListener off = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalPowerId)).setValue("4");
                            RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalPowerId)).setValue("2");
                            x=0;
                        }
                    };
                    View.OnClickListener on = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalPowerId)).setValue("4");
                            RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalPowerId)).setValue("1");
                            x=0;
                        }
                    };
                    RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(PowerId)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                int Val = Integer.parseInt(snapshot.getValue().toString());
                                if (Val == 1 || Val == 3) {
                                    tempUp.setVisibility(View.VISIBLE);
                                    tempDown.setVisibility(View.VISIBLE);
                                    fanSpeed.setVisibility(View.VISIBLE);
                                    onOf.setBackgroundResource(R.drawable.ac_on);
                                    onOf.setOnClickListener(off);
                                }
                                else if (Val == 0 || Val == 2) {
                                    tempUp.setVisibility(View.INVISIBLE);
                                    tempDown.setVisibility(View.INVISIBLE);
                                    fanSpeed.setVisibility(View.INVISIBLE);
                                    onOf.setBackgroundResource(android.R.drawable.ic_lock_power_off);
                                    onOf.setOnClickListener(on);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(SetId)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                int Val = Integer.parseInt(snapshot.getValue().toString());
                                if (MAX.length() > 2) {
                                    String res = String.valueOf((int) (Val*0.1)) ;
                                    clientSelectedTemp.setText(res+" "+UNIT);
                                    int newUpTemp = Val+10;
                                    if (newUpTemp <= max) {
                                        tempUp.setOnClickListener(setTempButtonClick(String.valueOf(newUpTemp),RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalSetId))));
                                    }
                                    int newDownTemp = Val-10;
                                    if (newDownTemp >= min) {
                                        tempDown.setOnClickListener(setTempButtonClick(String.valueOf(newDownTemp),RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalSetId))));
                                    }
                                }
                                else {
                                    String res = String.valueOf(Val) ;
                                    clientSelectedTemp.setText(res+" "+UNIT);
                                    int newTemp = Val+1;
                                    if (newTemp <= max) {
                                        tempUp.setOnClickListener(setTempButtonClick(String.valueOf(newTemp),RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalSetId))));
                                    }
                                    int newDownTemp = Val-1;
                                    if (newDownTemp >= min) {
                                        tempDown.setOnClickListener(setTempButtonClick(String.valueOf(newDownTemp),RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalSetId))));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(FanId)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                String Val = snapshot.getValue().toString();
                                fanSpeedText.setText(Val);
                                if (Val.equals("low") || Val.equals("Low") || Val.equals("LOW") || Val.equals("0")) {
                                    switch (Val) {
                                        case "low" :
                                            fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("med",RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalFanId))));
                                            break;
                                        case "Low" :
                                            fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("Med",RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalFanId))));
                                            break;
                                        case "LOW" :
                                            fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("MED",RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalFanId))));
                                            break;
                                        case "0" :
                                            fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("1",RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalFanId))));
                                            break;
                                    }
                                }
                                else if (Val.equals("med") || Val.equals("Med") || Val.equals("MED") || Val.equals("1")) {
                                    switch (Val) {
                                        case "med" :
                                            fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("high",RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalFanId))));
                                            break;
                                        case "Med" :
                                            fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("High",RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalFanId))));
                                            break;
                                        case "MED" :
                                            fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("HIGH",RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalFanId))));
                                            break;
                                        case "1" :
                                            fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("2",RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalFanId))));
                                            break;
                                    }
                                }
                                else if (Val.equals("high") || Val.equals("High") || Val.equals("HIGH") || Val.equals("2")) {
                                    switch (Val) {
                                        case "high" :
                                            fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("auto",RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalFanId))));
                                            break;
                                        case "High" :
                                            fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("Auto",RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalFanId))));
                                            break;
                                        case "HIGH" :
                                            fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("AUTO",RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalFanId))));
                                            break;
                                        case "2" :
                                            fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("3",RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalFanId))));
                                            break;
                                    }
                                }
                                else if (Val.equals("auto") || Val.equals("Auto") || Val.equals("AUTO") || Val.equals("3")) {
                                    switch (Val) {
                                        case "auto" :
                                            fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("low",RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalFanId))));
                                            break;
                                        case "Auto" :
                                            fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("Low",RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalFanId))));
                                            break;
                                        case "AUTO" :
                                            fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("LOW",RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalFanId))));
                                            break;
                                        case "3" :
                                            fanSpeed.setOnClickListener(setFanSpeedButtonOnClick("0",RoomDevicesRef.child(THEROOM.getAC_B().getName()).child(String.valueOf(finalFanId))));
                                            break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
//                    THEROOM.getAC().registerDeviceListener(new IDeviceListener() {
//                        @Override
//                        public void onDpUpdate(String devId, Map<String, Object> dpStr) {
//                            Log.d("acAction",dpStr.toString());
//                            Log.d("acProblems", THEROOM.getAC_B().getDps().toString());
//                            if (THEROOM.getAC_B().getDps().get(String.valueOf(finalSetId)) != null) {
//                                String res = THEROOM.getAC_B().getDps().get(String.valueOf(finalSetId)).toString();
//                                int xx = Integer.parseInt(res);
//                                if (MAX.length() > 2) {
//                                    res = String.valueOf((int) (xx*0.1)) ;
//                                    clientSelectedTemp.setText(res+" "+UNIT);
//                                }
//                                else {
//                                    res = String.valueOf(xx) ;
//                                    clientSelectedTemp.setText(res+" "+UNIT);
//                                }
//                            }
//                            if (THEROOM.getAC_B().getDps().get(String.valueOf(finalPowerId)) != null) {
//                                if (Boolean.parseBoolean(THEROOM.getAC_B().getDps().get(String.valueOf(finalPowerId)).toString())) {
//                                    tempUp.setVisibility(View.VISIBLE);
//                                    tempDown.setVisibility(View.VISIBLE);
//                                    fanSpeed.setVisibility(View.VISIBLE);
//                                    onOf.setBackgroundResource(R.drawable.ac_on);
//                                    POWER_STATUS[0] = true ;
//                                }
//                                else {
//                                    tempUp.setVisibility(View.INVISIBLE);
//                                    tempDown.setVisibility(View.INVISIBLE);
//                                    fanSpeed.setVisibility(View.INVISIBLE);
//                                    onOf.setBackgroundResource(R.drawable.ac_off);
//                                    POWER_STATUS[0] = false ;
//                                }
//                            }
//                            if (THEROOM.getAC_B().getDps().get(String.valueOf(finalCurrentId)) != null) {
//                                String res = THEROOM.getAC_B().getDps().get(String.valueOf(finalCurrentId)).toString() ;
//                                if (MAX.length() > 2) {
//                                    int xx = Integer.parseInt(res);
//                                    res = String.valueOf((int) (xx * 0.1));
//                                }
//                                currentTempText.setText(res+" "+UNIT);
//                            }
////                            if (dpStr.get("temp_set") != null) {
////                                String res = dpStr.get("temp_set").toString();
////                                if (MAX.length() > 2) {
////                                    int xx = Integer.parseInt(res);
////                                    res = String.valueOf(xx * 0.1) ;
////                                }
////                                clientSelectedTemp.setText(res+" "+UNIT);
////                                int t = Integer.parseInt(dpStr.get("temp_set").toString())-min;
////                                arcSeekBar.setProgress(t);
////                            }
////                            if (dpStr.get("temp_current") != null) {
////                                String res = dpStr.get("temp_current").toString() ;
////                                if (MAX.length() > 2) {
////                                    int xx = Integer.parseInt(res);
////                                    res = String.valueOf(xx * 0.1);
////                                }
////                                currentTempText.setText(res+" "+UNIT);
////                            }
////                            if (dpStr.get("windspeed") != null) {
////                                try {
////                                    int x = Integer.parseInt(dpStr.get("windspeed").toString()) ;
////                                    if (x == 1) {
////                                        fanSpeedText.setText("low");
////                                    }
////                                    else if (x == 2) {
////                                        fanSpeedText.setText("med");
////                                    }
////                                    else if (x == 3) {
////                                        fanSpeedText.setText("high");
////                                    }
////                                }
////                                catch (Exception e) {
////                                    fanSpeedText.setText(dpStr.get("windspeed").toString());
////                                }
////
////                            }
////                            if (dpStr.get("switch") != null) {
////                                if (Boolean.parseBoolean(dpStr.get("switch").toString())) {
////                                    tempUp.setVisibility(View.VISIBLE);
////                                    tempDown.setVisibility(View.VISIBLE);
////                                    fanSpeed.setVisibility(View.VISIBLE);
////                                    onOf.setBackgroundResource(R.drawable.ac_on);
////                                }
////                                else {
////                                    tempUp.setVisibility(View.INVISIBLE);
////                                    tempDown.setVisibility(View.INVISIBLE);
////                                    fanSpeed.setVisibility(View.INVISIBLE);
////                                    onOf.setBackgroundResource(R.drawable.ac_off);
////                                }
////                            }
//                        }
//
//                        @Override
//                        public void onRemoved(String devId) {
//
//                        }
//
//                        @Override
//                        public void onStatusChanged(String devId, boolean online) {
//
//                        }
//
//                        @Override
//                        public void onNetworkStatusChanged(String devId, boolean status) {
//
//                        }
//
//                        @Override
//                        public void onDevInfoUpdate(String devId) {
//
//                        }
//                    });
//                    onOf.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (POWER_STATUS[0]) {
//                                THEROOM.getAC().publishDps("{\"" + finalPowerId + "\":false}", new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        THEROOM.getAC().publishDps("{\"" + finalPowerId + "\":true}", new IResultCallback() {
//                                            @Override
//                                            public void onError(String code, String error) {
//
//                                            }
//
//                                            @Override
//                                            public void onSuccess() {
//
//                                            }
//                                        });
//                                    }
//
//                                    @Override
//                                    public void onSuccess() {
//
//                                    }
//                                });
//                            } else {
//                                THEROOM.getAC().publishDps("{\"" + finalPowerId + "\":true}", new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        THEROOM.getAC().publishDps("{\"" + finalPowerId + "\":false}", new IResultCallback() {
//                                            @Override
//                                            public void onError(String code, String error) {
//
//                                            }
//
//                                            @Override
//                                            public void onSuccess() {
//
//                                            }
//                                        });
//                                    }
//
//                                    @Override
//                                    public void onSuccess() {
//
//                                    }
//                                });
//                            }
//                            x = 0;
//                        }
//                    });
//                    fanSpeed.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            TextView fanSpeedText = findViewById(R.id.fanSpeed);
//                            if (THEROOM.getAC_B().getDps().get("5") != null) {
//                                if (THEROOM.getAC_B().getDps().get("5").toString().equals("low")) {
//                                    THEROOM.getAC().publishDps("{\" "+ finalFanId +"\": \"med\"}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//
//                                        @Override
//                                        public void onSuccess() {
//                                            fanSpeedText.setText("Med");
//                                        }
//                                    });
//                                } else if (THEROOM.getAC_B().getDps().get("5").toString().equals("med")) {
//                                    THEROOM.getAC().publishDps("{\" "+ finalFanId +"\": \"high\"}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//
//                                        @Override
//                                        public void onSuccess() {
//                                            fanSpeedText.setText("High");
//                                        }
//                                    });
//                                } else if (THEROOM.getAC_B().getDps().get("5").toString().equals("high")) {
//                                    THEROOM.getAC().publishDps("{\" "+ finalFanId +"\": \"auto\"}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//
//                                        @Override
//                                        public void onSuccess() {
//                                            fanSpeedText.setText("Auto");
//                                        }
//                                    });
//                                } else if (THEROOM.getAC_B().getDps().get("5").toString().equals("auto")) {
//                                    THEROOM.getAC().publishDps("{\" "+ finalFanId +"\": \"low\"}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//
//                                        @Override
//                                        public void onSuccess() {
//                                            fanSpeedText.setText("Low");
//                                        }
//                                    });
//                                }
//                            }
//                            x = 0;
//                        }
//                    });
//                    tempUp.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Log.d("acButtons" , "up pressed");
//                            if (MAX.length() > 2) {
//                                String current = THEROOM.getAC_B().getDps().get(String.valueOf(finalSetId)).toString();
//                                int newt = Integer.parseInt(current)+10;
//                                THEROOM.getAC().publishDps("{\"" + finalSetId + "\":" + newt + "}", new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new messageDialog(error+" "+code,"failed",act);
//                                    }
//
//                                    @Override
//                                    public void onSuccess() {
//
//                                    }
//                                });
//                            }
//                            else {
//                                String current = THEROOM.getAC_B().getDps().get(String.valueOf(finalSetId)).toString();
//                                int newt = Integer.parseInt(current)+1;
//                                THEROOM.getAC().publishDps("{\"" + finalSetId + "\":" + newt + "}", new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new messageDialog(error+" "+code,"failed",act);
//                                    }
//
//                                    @Override
//                                    public void onSuccess() {
//
//                                    }
//                                });
//                                //RoomDevicesRef.child(THEROOM.RoomNumber+"AC").child(String.valueOf(finalSetId)).setValue(newt);
//                            }
//                            x = 0;
//                        }
//                    });
//                    tempDown.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Log.d("acButtons" , "down pressed");
//                            if (MAX.length() > 2) {
//                                String current = THEROOM.getAC_B().dps.get(String.valueOf(finalSetId)).toString();
//                                int newt = (Integer.parseInt(current) - 10);
//                                //RoomDevicesRef.child(THEROOM.RoomNumber + "AC").child(String.valueOf(finalSetId)).setValue(newt);
//                                THEROOM.getAC().publishDps("{\"" + finalSetId + "\":" + newt + "}", new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new messageDialog(error+" "+code,"failed",act);
//                                    }
//
//                                    @Override
//                                    public void onSuccess() {
//
//                                    }
//                                });
//                            }
//                            else {
//                                String current = THEROOM.getAC_B().dps.get(String.valueOf(finalSetId)).toString();
//                                int newt = (Integer.parseInt(current) - 1);
//                                //RoomDevicesRef.child(THEROOM.RoomNumber + "AC").child(String.valueOf(finalSetId)).setValue(newt);
//                                THEROOM.getAC().publishDps("{\"" + finalSetId + "\":" + newt + "}", new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new messageDialog(error+" "+code,"failed",act);
//                                    }
//
//                                    @Override
//                                    public void onSuccess() {
//
//                                    }
//                                });
//                            }
//                            x = 0;
//                        }
//                    });
                }
            }
            @Override
            public void onError(String errorCode, String errorMessage) {

            }
        });
    }

    View.OnClickListener setTempButtonClick(String temp,DatabaseReference ref) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.setValue(temp);
                x=0;
            }
        };
    }

    View.OnClickListener setFanSpeedButtonOnClick(String newFan,DatabaseReference ref) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.setValue(newFan);
                x=0;
            }
        };
    }

    public void sendRegistrationToServer(final String token) {
        String url = MyApp.ProjectURL + "roomsManagement/modifyRoomFirebaseToken";
        StringRequest r = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("registToken", response );
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("registToken", error.toString() );
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("token",token);
                params.put("room_id",String.valueOf(THEROOM.id));
                return params;
            }
        };
        if (FirebaseTokenRegister == null) {
            FirebaseTokenRegister = Volley.newRequestQueue(act) ;
        }
        FirebaseTokenRegister.add(r);
    }

    static void openMessageDialog(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Dialog d = new Dialog(act);
                d.setCancelable(false);
                d.setContentView(R.layout.reception_message_dialog);
                TextView m = (TextView) d.findViewById(R.id.receptionMessage);
                m.setText(message);
                Button b = (Button) d.findViewById(R.id.closeReceptionMessage);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                d.show();
            }
        });
    }

    public static void addRoomServiceOrderInDataBase() {
        final Dialog d = new Dialog(act);
        View v = LayoutInflater.from(act).inflate(R.layout.room_service_dialog , null);
        d.setContentView(v);
        Window w = d.getWindow();
        w.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        w.getDecorView().setSystemUiVisibility(uiOptions);
        final EditText ordereditetext = (EditText) d.findViewById(R.id.RoomServiceDialog_Text);
        Button cancel = (Button) d.findViewById(R.id.RoomServiceDialog_Cancel);
        final String[] xxx = new String[] {"","","","",""};
        final CheckBox slippers = (CheckBox) d.findViewById(R.id.checkBox_slippers);
        final CheckBox towels = (CheckBox) d.findViewById(R.id.checkBox_towels);
        final CheckBox minibar = (CheckBox) d.findViewById(R.id.checkBox_minibar);
        final CheckBox bath = (CheckBox) d.findViewById(R.id.checkBox_bath);
        final CheckBox other = (CheckBox) d.findViewById(R.id.checkBox_others);
        slippers.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (slippers.isChecked())
            {
                xxx[0] = "Slipper";
            }
            else
            {
                xxx[0] = "";
            }
        });
        towels.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (towels.isChecked())
            {
                xxx[1] = "Towels";
            }
            else
            {
                xxx[1] = "";
            }
        });
        minibar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (minibar.isChecked())
            {
                xxx[2] = "Mini Bar";
            }
            else
            {
                xxx[2] = "";
            }
        });
        bath.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(bath.isChecked())
            {
                xxx[3] = "BathSet";
            }
            else
            {
                xxx[3] = "";
            }
        });
        other.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (other.isChecked())
            {
                xxx[4] = "Other";
                ordereditetext.setVisibility(View.VISIBLE);
            }
            else
            {
                xxx[4] = "";
                ordereditetext.setVisibility(View.INVISIBLE);
            }
        });
        ordereditetext.setVisibility(View.INVISIBLE);
        d.show();
        cancel.setOnClickListener(v1 -> {
            d.dismiss();
            w.getDecorView().setSystemUiVisibility(uiOptions);
        });
        Button ok = (Button) d.findViewById(R.id.RoomServiceDialog_OK);
        ok.setOnClickListener(v12 -> {
            for (int i =0 ; i<xxx.length;i++) {
                if (!xxx[i].equals("") && !xxx[i].equals("Other")) {
                    if (roomServiceOrder.equals("")) {
                        roomServiceOrder = xxx[i];
                    }
                    else {
                        roomServiceOrder = roomServiceOrder + "-"+ xxx[i];
                    }

                }else if (xxx[i].equals("Other")) {
                    if (roomServiceOrder.equals("")) {
                        roomServiceOrder = ordereditetext.getText().toString();
                    }
                    else {
                        roomServiceOrder = roomServiceOrder + "-" + ordereditetext.getText().toString();
                    }
                }
            }
            if (roomServiceOrder.length() > 0) {
                Calendar x = Calendar.getInstance(Locale.getDefault());
                long timee =  x.getTimeInMillis();
                RoomServiceStatus = true;
                myRefRoomService.setValue(timee);
                myRefRoomServiceText.setValue(roomServiceOrder);
                myRefdep.setValue("RoomService");
                myRefDND.setValue(0);
                String url = MyApp.ProjectURL + "reservations/addRoomServiceOrderRoomDevice";
                StringRequest request = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("roomserviceResp" , response);
                        roomServiceOrder = "";
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("roomserviceResp" , error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("room_id", String.valueOf(MyApp.Room.id));
                        params.put("order", roomServiceOrder);
                        return params;
                    }
                };
                Volley.newRequestQueue(act).add(request);
                d.dismiss();
            }
            else
            {
                ToastMaker.MakeToast("Please Enter Your Order" , act);
            }
            w.getDecorView().setSystemUiVisibility(uiOptions);
        });
    }

    public static void removeRoomServiceOrderInDataBase() {
        RoomServiceStatus = false ;
        myRefRoomService.setValue(0);
        myRefRoomServiceText.setValue("0");
        String url = MyApp.ProjectURL + "reservations/cancelServiceOrderControlDevice"+roomserviceCounter;
        StringRequest removOrder = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("roomserviceResp" , response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("roomserviceResp" , error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("order_type" ,"RoomService");
                params.put("room_id" , String.valueOf( MyApp.Room.id));
                return params;
            }
        };
        Volley.newRequestQueue(act).add(removOrder);
        roomserviceCounter++ ;
        if (roomserviceCounter == 5) {
            roomserviceCounter = 1 ;
        }
    }

    private void blink() {
        final Calendar x = Calendar.getInstance(Locale.getDefault());
        final Handler hander = new Handler();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hander.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("Time is : ",x.getTime().toString());
                    String currentTime = x.get(Calendar.HOUR_OF_DAY)+":"+x.get(Calendar.MINUTE)+":"+x.get(Calendar.SECOND);
                    time.setText(currentTime);
                    String currentDate = x.get(Calendar.DAY_OF_MONTH)+ "-" + (x.get(Calendar.MONTH)+1)+"-" + x.get(Calendar.YEAR);
                    date.setText(currentDate);
                    blink();
                }
            });
        }).start();
    }

    private void KeepScreenFull() {
        final Handler hander = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                hander.postDelayed(this,100);
                hideSystemUI();
            }
        }).start();
    }

    public void logout() {
        String url = MyApp.ProjectURL + "roomsManagement/logoutRoom" ;
        StringRequest logoutRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("logoutResp" , response);
                try {
                    JSONObject result = new JSONObject(response);
                    if (result.getString("result").equals("success")) {
                        Toast.makeText(act,"Logout Success",Toast.LENGTH_LONG).show();
                        editor.putString("projectName" ,null);
                        editor.putString("tuyaUser" , null);
                        editor.putString("tuyaPassword" ,null);
                        editor.putString("lockUser" ,null);
                        editor.putString("lockPassword" ,null);
                        editor.putString("url" ,null);
                        editor.putString("RoomNumber" ,null);
                        editor.apply();
                        Intent i = new Intent(act , LogIn.class);
                        startActivity(i);
                        act.finish();
                    }
                    else {
                        new messageDialog(result.getString("error"),"failed",act);
                    }
                } catch (JSONException e) {
                    Log.d("logoutResp" , e.getMessage());
                    new messageDialog(e.getMessage(),"failed",act);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("logoutResp" , error.toString());
                new messageDialog(error.toString(),"failed",act);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("room_id" ,String.valueOf(THEROOM.id));
                return params;
            }
        };
        Volley.newRequestQueue(act).add(logoutRequest);
    }

    public void getFacilities() {
        String url = MyApp.ProjectURL + "facilitys/getfacilitys" ;
        Log.d("facilitiesResp" , url);
        StringRequest facilityRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("facilitiesResp" , response);
                if (response != null) {
                    try {
                        JSONArray arr = new JSONArray(response);
                        for (int i=0;i<arr.length();i++) {
                            JSONObject row = arr.getJSONObject(i);
                            Facilities.add(new FACILITY(row.getInt("id"),row.getInt("Hotel"),row.getInt("TypeId"),row.getString("TypeName"),row.getString("Name"),row.getInt("Control"),row.getString("photo")));
                        }
                        getLaundries();
                        getRestaurants();
                        getGyms();
                        getMiniBar();
                    } catch (JSONException e) {
                        Log.d("facilitiesResp" , e.getMessage());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("facilitiesResp" , error.toString());
            }
        });
        Volley.newRequestQueue(act).add(facilityRequest);
    }

    private void getLaundries() {
        for (int i=0;i<Facilities.size();i++) {
            if (Facilities.get(i).TypeName.equals("Laundry")) {
                Laundries.add(new LAUNDRY(Facilities.get(i).id,Facilities.get(i).Hotel,Facilities.get(i).TypeId,Facilities.get(i).TypeName,Facilities.get(i).Name,Facilities.get(i).Control,Facilities.get(i).photo));
            }
        }
        if (Laundries.size() > 0 ) {
            LaundryBtn.setVisibility(View.VISIBLE);
            getLaundryMenu();
        }
        else {
            LaundryBtn.setVisibility(View.GONE);
        }
    }

    void getLaundryMenu() {
        if (Laundries.size() > 0) {
            List<LAUNDRYITEM> list = new ArrayList<LAUNDRYITEM>();
            String url = MyApp.ProjectURL + "facilitys/getLaundryItemsRoomDevice";
            StringRequest laundryRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null) {
                        try {
                            JSONObject result = new JSONObject(response);
                            result.getString("result");
                            if (result.getString("result").equals("success")) {
                                JSONArray arr = new JSONArray(result.getString("items"));
                                for (int i=0 ; i<arr.length();i++) {
                                    JSONObject row = arr.getJSONObject(i);
                                    list.add(new LAUNDRYITEM(row.getString("icon"),row.getString("Name"),row.getString("Price")));
                                }
                                if (list.size()>0) {
                                    LAUNDRYMENU_ADAPTER adapter = new LAUNDRYMENU_ADAPTER(list);
                                    LAUNDRYMENU.setAdapter(adapter);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("facility_id" , String.valueOf(Laundries.get(0).id));
                    return params;
                }
            };
            Volley.newRequestQueue(act).add(laundryRequest);
        }
    }

    private void getRestaurants() {
        for (int i=0;i<Facilities.size();i++) {
            if (Facilities.get(i).TypeName.equals("Restaurant") || Facilities.get(i).TypeName.equals("CoffeeShop")) {
                RESTAURANT_UNIT r = new RESTAURANT_UNIT(Facilities.get(i).id,Facilities.get(i).Hotel,Facilities.get(i).TypeId,Facilities.get(i).TypeName,Facilities.get(i).Name,Facilities.get(i).Control,Facilities.get(i).photo);
                Log.d("restaurantsAre", Facilities.get(i).Name +" "+Facilities.get(i).TypeName);
                Restaurants.add(r);
            }
        }
        if (Restaurants.size() > 0) {
            for (int i=0;i<Restaurants.size();i++) {
                Log.d("restaurantsAre", Restaurants.get(i).Name +" "+Restaurants.get(i).TypeName );
            }
            RestaurantBtn.setVisibility(View.VISIBLE);
        }
        else {
            RestaurantBtn.setVisibility(View.GONE);
        }
        Log.d("restaurantsAre", Restaurants.size()+" " );
    }

    private void getGyms() {
        for (int i=0;i<Facilities.size();i++) {
            if (Facilities.get(i).TypeName.equals("GYM")) {
                Gyms.add(Facilities.get(i));
            }
        }
    }

    private void getMiniBar() {
        for (int i=0;i<Facilities.size();i++) {
            if (Facilities.get(i).TypeName.equals("MiniBar")) {
                Minibar.add(new MINIBAR(Facilities.get(i).id,Facilities.get(i).Hotel,Facilities.get(i).TypeId,Facilities.get(i).TypeName,Facilities.get(i).Name,Facilities.get(i).Control,Facilities.get(i).photo));
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
            }
        });
    }

    void getScenes() {
        TuyaHomeSdk.getSceneManagerInstance().getSceneList(MyApp.HOME.getHomeId(), new ITuyaResultCallback<List<SceneBean>>() {
            @Override
            public void onSuccess(List<SceneBean> result) {
                SCENES = result ;
                Log.d("scenesAre",SCENES.size()+"");
                for (SceneBean s : SCENES) {
                    Log.d("scenesAre",s.getName());
                    if (s.getName().contains(String.valueOf(THEROOM.RoomNumber))) {
                        MY_SCENES.add(s);
                    }
                }
                MyApp.MY_SCENES = MY_SCENES ;
                if (MY_SCENES.size() > 0) {
                    for (int i=0;i<MY_SCENES.size();i++) {
                        Log.d("scenesAre","my scenes "+MY_SCENES.get(i).getName());
                        if (MY_SCENES.get(i).getName().contains("Living")) {
                            LivingMood.add(MY_SCENES.get(i));
                        }
                        else if (MY_SCENES.get(i).getName().contains("Sleep")) {
                            SleepMood.add(MY_SCENES.get(i));
                        }
                        else if (MY_SCENES.get(i).getName().contains("Work")) {
                            WorkMood.add(MY_SCENES.get(i));
                        }
                        else if (MY_SCENES.get(i).getName().contains("Romance")) {
                            RomanceMood.add(MY_SCENES.get(i));
                        }
                        else if (MY_SCENES.get(i).getName().contains("Read")) {
                            ReadMood.add(MY_SCENES.get(i));
                        }
                        else if (MY_SCENES.get(i).getName().contains("MasterOff")) {
                            MasterOffMood.add(MY_SCENES.get(i));
                        }
                    }
                    prepareMoodButtons();
                    TextView lightsText = findViewById(R.id.textView40);
                    lightsText.setText(getResources().getString(R.string.lightsAndMoods));
                }
            }
            @Override
            public void onError(String errorCode, String errorMessage) {
                Log.d("scenesAre",errorCode+" "+errorMessage);
            }
        });
    }

    void prepareLights() {
        if (THEROOM.getSWITCH1_B() != null) {
            Switch1Status = true ;
        }
        if (THEROOM.getSWITCH2_B() != null) {
            Switch1Status = true ;
        }
        if (THEROOM.getSWITCH3_B() != null) {
            Switch3Status = true ;
        }
        if (THEROOM.getSWITCH4_B() != null) {
            Switch4Status = true ;
        }
        if (!Switch1Status && !Switch2Status && !Switch3Status && !Switch4Status) {
            ShowLighting.setVisibility(View.GONE);
        }
        else {
            lightsLayout.removeAllViews();
            ShowLighting.setVisibility(View.VISIBLE);
            if (lightsDB.getScreenButtons().size() > 0 ) {
                lightsLayout.setDividerPadding(10);
                for (int i=0 ; i < lightsDB.getScreenButtons().size(); i++) {
                    if (THEROOM.getSWITCH1_B() != null ) {
                        String s = THEROOM.getSWITCH1_B().getName().split("Switch")[1];
                        if (lightsDB.getScreenButtons().get(i).Switch == Integer.parseInt(s)) {
                            LinearLayout LightButton = new LinearLayout(act);
                            LightButton.setOrientation(LinearLayout.VERTICAL);
                            TextView text = new TextView(act);
                            Button image = new Button(act);
                            if (Build.MODEL.equals("YS4B")) {
                                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                            }
                            image.setBackgroundResource(R.drawable.light_off_new);
                            LightButton.addView(text);
                            LightButton.addView(image);
                            text.setGravity(Gravity.CENTER);
                            text.setText(lightsDB.getScreenButtons().get(i).name);
                            text.setTextColor(Color.LTGRAY);
                            text.setTextSize(20);
                            LightButton.setGravity(Gravity.CENTER);
                            LightButton.setPadding(2,2,2,2);
                            lightsLayout.addView(LightButton);
                            int finalI = i;
                            if (lightsDB.getScreenButtons().get(i).button == 1) {
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (THEROOM.S1B1) {
                                            clickSwitchButton(THEROOM.getSWITCH1(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),false);
                                        }
                                        else {
                                            clickSwitchButton(THEROOM.getSWITCH1(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),true);
                                        }
                                        x=0;
                                    }
                                });
                                if (THEROOM.getSWITCH1_B().dps.get("1") != null) {
                                    RoomDevicesRef.child(THEROOM.getSWITCH1_B().getName()).child("1").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                Log.d("s1b1",snapshot.getValue().toString());
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THEROOM.S1B1 = true ;
                                                    image.setBackgroundResource(R.drawable.light_on_new);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THEROOM.S1B1 = false ;
                                                    image.setBackgroundResource(R.drawable.light_off_new);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 2) {
                                if (THEROOM.getSWITCH1_B().dps.get("2") != null) {
                                    RoomDevicesRef.child(THEROOM.getSWITCH1_B().getName()).child("2").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                Log.d("s1b2",snapshot.getValue().toString());
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THEROOM.S1B2 = true ;
                                                    image.setBackgroundResource(R.drawable.light_on_new);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THEROOM.S1B2 = false ;
                                                    image.setBackgroundResource(R.drawable.light_off_new);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (THEROOM.S1B2) {
                                            clickSwitchButton(THEROOM.getSWITCH1(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),false);
                                        }
                                        else {
                                            clickSwitchButton(THEROOM.getSWITCH1(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),true);
                                        }
                                        x=0;
                                    }
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 3) {
                                if (THEROOM.getSWITCH1_B().dps.get("3") != null) {
                                    RoomDevicesRef.child(THEROOM.getSWITCH1_B().getName()).child("3").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                Log.d("s1b3",snapshot.getValue().toString());
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THEROOM.S1B3= true ;
                                                    image.setBackgroundResource(R.drawable.light_on_new);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THEROOM.S1B3 = false ;
                                                    image.setBackgroundResource(R.drawable.light_off_new);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (THEROOM.S1B3) {
                                            clickSwitchButton(THEROOM.getSWITCH1(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),false);
                                        }
                                        else {
                                            clickSwitchButton(THEROOM.getSWITCH1(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),true);
                                        }
                                        x=0;
                                    }
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 4) {
                                if (THEROOM.getSWITCH1_B().dps.get("4") != null) {
                                    RoomDevicesRef.child(THEROOM.getSWITCH1_B().getName()).child("4").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                Log.d("s1b4",snapshot.getValue().toString());
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THEROOM.S1B4 = true ;
                                                    image.setBackgroundResource(R.drawable.light_on_new);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THEROOM.S1B4 = false ;
                                                    image.setBackgroundResource(R.drawable.light_off_new);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (THEROOM.S1B4) {
                                            clickSwitchButton(THEROOM.getSWITCH1(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),false);
                                        }
                                        else {
                                            clickSwitchButton(THEROOM.getSWITCH1(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),true);
                                        }
                                        x=0;
                                    }
                                });
                            }
                        }
                    }
                    if (THEROOM.getSWITCH2_B() != null ) {
                        String s = THEROOM.getSWITCH2_B().getName().split("Switch")[1];
                        if (lightsDB.getScreenButtons().get(i).Switch == Integer.parseInt(s)) {
                            LinearLayout LightButton = new LinearLayout(act);
                            LightButton.setOrientation(LinearLayout.VERTICAL);
                            TextView text = new TextView(act);
                            Button image = new Button(act);
                            if (Build.MODEL.equals("YS4B")) {
                                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                            }
                            image.setBackgroundResource(R.drawable.light_off_new);
                            LightButton.addView(text);
                            LightButton.addView(image);
                            text.setGravity(Gravity.CENTER);
                            text.setText(lightsDB.getScreenButtons().get(i).name);
                            text.setTextColor(Color.LTGRAY);
                            text.setTextSize(20);
                            LightButton.setGravity(Gravity.CENTER);
                            LightButton.setPadding(2,2,2,2);
                            lightsLayout.addView(LightButton);
                            int finalI = i;
                            if (lightsDB.getScreenButtons().get(i).button == 1) {
                                if (THEROOM.getSWITCH2_B().dps.get("1") != null) {
                                    RoomDevicesRef.child(THEROOM.getSWITCH2_B().getName()).child("1").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                Log.d("s2b1",snapshot.getValue().toString());
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THEROOM.S2B1 = true ;
                                                    image.setBackgroundResource(R.drawable.light_on_new);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THEROOM.S2B1 = false ;
                                                    image.setBackgroundResource(R.drawable.light_off_new);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (THEROOM.S2B1) {
                                            clickSwitchButton(THEROOM.getSWITCH2(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),false);
                                        }
                                        else {
                                            clickSwitchButton(THEROOM.getSWITCH2(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),true);
                                        }
                                        x=0;
                                    }
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 2) {
                                if (THEROOM.getSWITCH2_B().dps.get("2") != null) {
                                    RoomDevicesRef.child(THEROOM.getSWITCH2_B().getName()).child("2").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                Log.d("s2b2",snapshot.getValue().toString());
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THEROOM.S2B2 = true ;
                                                    image.setBackgroundResource(R.drawable.light_on_new);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THEROOM.S2B2 = false ;
                                                    image.setBackgroundResource(R.drawable.light_off_new);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (THEROOM.S2B2) {
                                            clickSwitchButton(THEROOM.getSWITCH2(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),false);
                                        }
                                        else {
                                            clickSwitchButton(THEROOM.getSWITCH2(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),true);
                                        }
                                        x=0;
                                    }
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 3) {
                                if (THEROOM.getSWITCH2_B().dps.get("3") != null) {
                                    RoomDevicesRef.child(THEROOM.getSWITCH2_B().getName()).child("3").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                Log.d("s2b3",snapshot.getValue().toString());
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THEROOM.S2B3= true ;
                                                    image.setBackgroundResource(R.drawable.light_on_new);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THEROOM.S2B3 = false ;
                                                    image.setBackgroundResource(R.drawable.light_off_new);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (THEROOM.S2B3) {
                                            clickSwitchButton(THEROOM.getSWITCH2(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),false);
                                        }
                                        else {
                                            clickSwitchButton(THEROOM.getSWITCH2(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),true);
                                        }
                                        x=0;
                                    }
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 4) {
                                if (THEROOM.getSWITCH2_B().dps.get("4") != null) {
                                    RoomDevicesRef.child(THEROOM.getSWITCH2_B().getName()).child("4").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                Log.d("s2b4",snapshot.getValue().toString());
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THEROOM.S2B4 = true ;
                                                    image.setBackgroundResource(R.drawable.light_on_new);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THEROOM.S2B4 = false ;
                                                    image.setBackgroundResource(R.drawable.light_off_new);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (THEROOM.S2B4) {
                                            clickSwitchButton(THEROOM.getSWITCH2(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),false);
                                        }
                                        else {
                                            clickSwitchButton(THEROOM.getSWITCH2(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),true);
                                        }
                                        x=0;
                                    }
                                });
                            }
                        }
                    }
                    if (THEROOM.getSWITCH3_B() != null ) {
                        String s = THEROOM.getSWITCH3_B().getName().split("Switch")[1];
                        if (lightsDB.getScreenButtons().get(i).Switch == Integer.parseInt(s)) {
                            LinearLayout LightButton = new LinearLayout(act);
                            LightButton.setOrientation(LinearLayout.VERTICAL);
                            TextView text = new TextView(act);
                            Button image = new Button(act);
                            if (Build.MODEL.equals("YS4B")) {
                                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                            }
                            image.setBackgroundResource(R.drawable.light_off_new);
                            LightButton.addView(text);
                            LightButton.addView(image);
                            text.setGravity(Gravity.CENTER);
                            text.setText(lightsDB.getScreenButtons().get(i).name);
                            text.setTextColor(Color.LTGRAY);
                            text.setTextSize(20);
                            LightButton.setGravity(Gravity.CENTER);
                            LightButton.setPadding(2,2,2,2);
                            lightsLayout.addView(LightButton);
                            int finalI = i;
                            if (lightsDB.getScreenButtons().get(i).button == 1) {
                                if (THEROOM.getSWITCH3_B().dps.get("1") != null) {
                                    RoomDevicesRef.child(THEROOM.getSWITCH3_B().getName()).child("1").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THEROOM.S3B1 = true ;
                                                    image.setBackgroundResource(R.drawable.light_on_new);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THEROOM.S3B1 = false ;
                                                    image.setBackgroundResource(R.drawable.light_off_new);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (THEROOM.S3B1) {
                                            clickSwitchButton(THEROOM.getSWITCH3(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),false);
                                        }
                                        else {
                                            clickSwitchButton(THEROOM.getSWITCH3(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),true);
                                        }
                                        x=0;
                                    }
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 2) {
                                if (THEROOM.getSWITCH3_B().dps.get("2") != null) {
                                    RoomDevicesRef.child(THEROOM.getSWITCH3_B().getName()).child("2").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THEROOM.S3B2 = true ;
                                                    image.setBackgroundResource(R.drawable.light_on_new);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THEROOM.S3B2 = false ;
                                                    image.setBackgroundResource(R.drawable.light_off_new);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (THEROOM.S3B2) {
                                            clickSwitchButton(THEROOM.getSWITCH3(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),false);
                                        }
                                        else {
                                            clickSwitchButton(THEROOM.getSWITCH3(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),true);
                                        }
                                        x=0;
                                    }
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 3) {
                                if (THEROOM.getSWITCH3_B().dps.get("3") != null) {
                                    RoomDevicesRef.child(THEROOM.getSWITCH3_B().getName()).child("3").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THEROOM.S3B3= true ;
                                                    image.setBackgroundResource(R.drawable.light_on_new);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THEROOM.S3B3 = false ;
                                                    image.setBackgroundResource(R.drawable.light_off_new);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (THEROOM.S3B3) {
                                            clickSwitchButton(THEROOM.getSWITCH3(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),false);
                                        }
                                        else {
                                            clickSwitchButton(THEROOM.getSWITCH3(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),true);
                                        }
                                        x=0;
                                    }
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 4) {
                                if (THEROOM.getSWITCH3_B().dps.get("4") != null) {
                                    RoomDevicesRef.child(THEROOM.getSWITCH3_B().getName()).child("4").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THEROOM.S3B4 = true ;
                                                    image.setBackgroundResource(R.drawable.light_on_new);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THEROOM.S3B4 = false ;
                                                    image.setBackgroundResource(R.drawable.light_off_new);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (THEROOM.S3B4) {
                                            clickSwitchButton(THEROOM.getSWITCH3(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),false);
                                        }
                                        else {
                                            clickSwitchButton(THEROOM.getSWITCH3(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),true);
                                        }
                                        x=0;
                                    }
                                });
                            }
                        }
                    }
                    if (THEROOM.getSWITCH4_B() != null ) {
                        String s = THEROOM.getSWITCH4_B().getName().split("Switch")[1];
                        if (lightsDB.getScreenButtons().get(i).Switch == Integer.parseInt(s)) {
                            LinearLayout LightButton = new LinearLayout(act);
                            LightButton.setOrientation(LinearLayout.VERTICAL);
                            TextView text = new TextView(act);
                            Button image = new Button(act);
                            if (Build.MODEL.equals("YS4B")) {
                                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                            }
                            image.setBackgroundResource(R.drawable.light_off_new);
                            LightButton.addView(text);
                            LightButton.addView(image);
                            text.setGravity(Gravity.CENTER);
                            text.setText(lightsDB.getScreenButtons().get(i).name);
                            text.setTextColor(Color.LTGRAY);
                            text.setTextSize(20);
                            LightButton.setGravity(Gravity.CENTER);
                            LightButton.setPadding(2,2,2,2);
                            lightsLayout.addView(LightButton);
                            int finalI = i;
                            if (lightsDB.getScreenButtons().get(i).button == 1) {
                                if (THEROOM.getSWITCH4_B().dps.get("1") != null) {
                                    RoomDevicesRef.child(THEROOM.getSWITCH4_B().getName()).child("1").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THEROOM.S4B1 = true ;
                                                    image.setBackgroundResource(R.drawable.light_on_new);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THEROOM.S4B1 = false ;
                                                    image.setBackgroundResource(R.drawable.light_off_new);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (THEROOM.S4B1) {
                                            clickSwitchButton(THEROOM.getSWITCH4(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),false);
                                        }
                                        else {
                                            clickSwitchButton(THEROOM.getSWITCH4(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),true);
                                        }
                                        x=0;
                                    }
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 2) {
                                if (THEROOM.getSWITCH4_B().dps.get("2") != null) {
                                    RoomDevicesRef.child(THEROOM.getSWITCH4_B().getName()).child("2").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THEROOM.S4B2 = true ;
                                                    image.setBackgroundResource(R.drawable.light_on_new);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THEROOM.S4B2 = false ;
                                                    image.setBackgroundResource(R.drawable.light_off_new);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (THEROOM.S4B2) {
                                            clickSwitchButton(THEROOM.getSWITCH4(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),false);
                                        }
                                        else {
                                            clickSwitchButton(THEROOM.getSWITCH4(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),true);
                                        }
                                        x=0;
                                    }
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 3) {
                                if (THEROOM.getSWITCH4_B().dps.get("3") != null) {
                                    RoomDevicesRef.child(THEROOM.getSWITCH4_B().getName()).child("3").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THEROOM.S4B3= true ;
                                                    image.setBackgroundResource(R.drawable.light_on_new);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THEROOM.S4B3 = false ;
                                                    image.setBackgroundResource(R.drawable.light_off_new);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (THEROOM.S4B3) {
                                            clickSwitchButton(THEROOM.getSWITCH4(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),false);
                                        }
                                        else {
                                            clickSwitchButton(THEROOM.getSWITCH4(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),true);
                                        }
                                        x=0;
                                    }
                                });
                            }
                            if (lightsDB.getScreenButtons().get(i).button == 4) {
                                if (THEROOM.getSWITCH4_B().dps.get("4") != null) {
                                    RoomDevicesRef.child(THEROOM.getSWITCH4_B().getName()).child("4").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getValue() != null) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                                    THEROOM.S4B4 = true ;
                                                    image.setBackgroundResource(R.drawable.light_on_new);
                                                }
                                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                                    THEROOM.S4B4 = false ;
                                                    image.setBackgroundResource(R.drawable.light_off_new);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (THEROOM.S4B4) {
                                            clickSwitchButton(THEROOM.getSWITCH4(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),false);
                                        }
                                        else {
                                            clickSwitchButton(THEROOM.getSWITCH4(), String.valueOf(lightsDB.getScreenButtons().get(finalI).button),true);
                                        }
                                        x=0;
                                    }
                                });
                            }
//                            View.OnClickListener lon =  new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    RoomDevicesRef.child(THEROOM.getSWITCH4_B().getName()).child(String.valueOf(lightsDB.getScreenButtons().get(finalI).button)).setValue("4");
//                                    RoomDevicesRef.child(THEROOM.getSWITCH4_B().getName()).child(String.valueOf(lightsDB.getScreenButtons().get(finalI).button)).setValue("1");
//                                    x=0;
//                                }
//                            };
//                            View.OnClickListener loff =  new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    RoomDevicesRef.child(THEROOM.getSWITCH4_B().getName()).child(String.valueOf(lightsDB.getScreenButtons().get(finalI).button)).setValue("4");
//                                    RoomDevicesRef.child(THEROOM.getSWITCH4_B().getName()).child(String.valueOf(lightsDB.getScreenButtons().get(finalI).button)).setValue("2");
//                                    x=0;
//                                }
//                            };
//                            RoomDevicesRef.child(THEROOM.getSWITCH4_B().getName()).child(String.valueOf(lightsDB.getScreenButtons().get(i).button)).addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    if (snapshot.getValue() != null) {
//                                        Log.d("btnPressed" , snapshot.getValue().toString());
//                                        int Val = Integer.parseInt(snapshot.getValue().toString());
//                                        if (Val == 3 || Val == 1) {
//                                            image.setBackgroundResource(R.drawable.light_on_new);
//                                            text.setTextColor(Color.WHITE);
//                                            image.setOnClickListener(loff);
//                                        }
//                                        else if (Val == 0 || Val == 2) {
//                                            image.setBackgroundResource(R.drawable.light_off_new);
//                                            text.setTextColor(Color.LTGRAY);
//                                            image.setOnClickListener(lon);
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//                                    Log.d("btnPressed" , error.toString());
//                                }
//                            });
                        }
                    }
                }
            }
        }
    }

    void clickSwitchButton(ITuyaDevice d,String Button,boolean OnOff) {
        d.publishDps("{\" "+Button+"\": "+OnOff+"}", new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Log.d("lightTurn" , Button+" "+OnOff+" "+error+ " " + code);
            }

            @Override
            public void onSuccess() {
                Log.d("lightTurn" , THEROOM.S1B1+" "+Button+" "+OnOff+" success");
            }
        });
    }

    void prepareMoodButtons() {
        if (LivingMood.size() > 0) {
            LinearLayout LightButton = new LinearLayout(act);
            LightButton.setOrientation(LinearLayout.VERTICAL);
            TextView text = new TextView(act);
            Button image = new Button(act);
            if (Build.MODEL.equals("YS4B")) {
                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            }
            image.setBackgroundResource(R.drawable.light_off_new);
            LightButton.addView(text);
            LightButton.addView(image);
            text.setGravity(Gravity.CENTER);
            text.setText(getResources().getString(R.string.livingMood));
            text.setTextColor(Color.LTGRAY);
            text.setTextSize(20);
            LightButton.setGravity(Gravity.CENTER);
            LightButton.setPadding(2,2,2,2);
            lightsLayout.addView(LightButton);
            String btn = "";
            if (LivingMood.get(0).getConditions() == null) {
                for (int i=0;i<LivingMood.size();i++) {
                    TuyaHomeSdk.newSceneInstance(LivingMood.get(i).getId()).executeScene(new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {

                        }

                        @Override
                        public void onSuccess() {

                        }
                    });
                }
            }
            else {
                for (int i=0;i<LivingMood.size();i++) {
                    if (LivingMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH1_B().devId)) {
                        Living = THEROOM.getSWITCH1_B() ;
                        btn = LivingMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                    else if (LivingMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH2_B().devId)) {
                        Living = THEROOM.getSWITCH2_B() ;
                        btn = LivingMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                    else if (LivingMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH3_B().devId)) {
                        Living = THEROOM.getSWITCH3_B() ;
                        btn = LivingMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                    else if (LivingMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH4_B().devId)) {
                        Living = THEROOM.getSWITCH4_B() ;
                        btn = LivingMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                    Log.d("MoodCond", THEROOM.getSWITCH2_B().getDevId() + " " + LivingMood.get(i).getConditions().get(0).getEntityId() + " " + LivingMood.get(i).getConditions().get(0).getId() + " " + LivingMood.get(i).getConditions().get(0).getEntitySubIds() + " " + LivingMood.get(i).getConditions().get(0).getEntityType());
                }
            }
            String finalBtn = btn;
            if (Living != null) {
//                View.OnClickListener lon =  new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        RoomDevicesRef.child(Living.getName()).child(finalBtn).setValue("4");
//                        RoomDevicesRef.child(Living.getName()).child(finalBtn).setValue("1");
//                        x=0;
//                    }
//                };
//                View.OnClickListener loff =  new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        RoomDevicesRef.child(Living.getName()).child(finalBtn).setValue("4");
//                        RoomDevicesRef.child(Living.getName()).child(finalBtn).setValue("2");
//                        x=0;
//                    }
//                };
//                RoomDevicesRef.child(Living.getName()).child(btn).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.getValue() != null) {
//                            Log.d("btnPressed" , snapshot.getValue().toString());
//                            int Val = Integer.parseInt(snapshot.getValue().toString());
//                            if (Val == 3 || Val == 1) {
//                                image.setBackgroundResource(R.drawable.light_on_new);
//                                text.setTextColor(Color.WHITE);
//                                image.setOnClickListener(loff);
//                            }
//                            else if (Val == 0 || Val == 2) {
//                                image.setBackgroundResource(R.drawable.light_off_new);
//                                text.setTextColor(Color.LTGRAY);
//                                image.setOnClickListener(lon);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.d("btnPressed" , error.toString());
//                    }
//                });
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (THEROOM.living) {
                            clickSwitchButton(TuyaHomeSdk.newDeviceInstance(Living.devId),finalBtn,false);
                        }
                        else {
                            clickSwitchButton(TuyaHomeSdk.newDeviceInstance(Living.devId),finalBtn,true);
                        }
                        x=0;
                    }
                });
                if (Living.dps.get(finalBtn) != null) {
                    RoomDevicesRef.child(Living.getName()).child(finalBtn).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                Log.d("s1b1",snapshot.getValue().toString());
                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    THEROOM.living = true ;
                                    image.setBackgroundResource(R.drawable.light_on_new);
                                }
                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    THEROOM.living = false ;
                                    image.setBackgroundResource(R.drawable.light_off_new);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
//            image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (Living != null && finalBtn != null) {
//                        Log.d("livingClicked" , Living.getDps().get(finalBtn).toString()+" "+finalBtn);
//                        if (LIVING_STATUS[0]) {
//                            TuyaHomeSdk.newDeviceInstance(Living.devId).publishDps("{\" "+ finalBtn +"\": false}", new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//                                    Log.d("livingClicked" , error + " "+code);
//                                    TuyaHomeSdk.newDeviceInstance(Living.devId).publishDps("{\" "+ finalBtn +"\": true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//                                            Log.d("livingClicked" , error + " "+code);
//                                        }
//
//                                        @Override
//                                        public void onSuccess() {
//                                            Log.d("livingClicked" , "success");
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onSuccess() {
//                                    Log.d("livingClicked" , "success");
//                                }
//                            });
//                        }
//                        else {
//                            TuyaHomeSdk.newDeviceInstance(Living.devId).publishDps("{\" "+ finalBtn +"\": true}", new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//                                    Log.d("livingClicked" , error + " "+code);
//                                    TuyaHomeSdk.newDeviceInstance(Living.devId).publishDps("{\" "+ finalBtn +"\": false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//                                            Log.d("livingClicked" , error + " "+code);
//                                        }
//
//                                        @Override
//                                        public void onSuccess() {
//                                            Log.d("livingClicked" , "success");
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onSuccess() {
//                                    Log.d("livingClicked" , "success");
//                                }
//                            });
//                        }
//                    }
//                    x=0;
//                }
//            });
//            if (Living != null) {
//                if (Living.getDps().get(finalBtn) != null) {
//                    if (Boolean.parseBoolean(Living.dps.get(finalBtn).toString())) {
//                        image.setBackgroundResource(R.drawable.light_on_new);
//                        text.setTextColor(Color.WHITE);
//                        LIVING_STATUS[0] = true ;
//                    }
//                    else {
//                        image.setBackgroundResource(R.drawable.light_off_new);
//                        text.setTextColor(Color.LTGRAY);
//                        LIVING_STATUS[0] = false ;
//                    }
//                }
//                TuyaHomeSdk.newDeviceInstance(Living.devId).registerDeviceListener(new IDeviceListener() {
//                    @Override
//                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
//                        if (Living.getDps().get(finalBtn) != null) {
//                            if (Boolean.parseBoolean(Living.getDps().get(finalBtn).toString())) {
//                                image.setBackgroundResource(R.drawable.light_on_new);
//                                text.setTextColor(Color.WHITE);
//                                LIVING_STATUS[0] = true ;
//                            }
//                            else {
//                                image.setBackgroundResource(R.drawable.light_off_new);
//                                text.setTextColor(Color.LTGRAY);
//                                LIVING_STATUS[0] = false ;
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onRemoved(String devId) {
//
//                    }
//
//                    @Override
//                    public void onStatusChanged(String devId, boolean online) {
//
//                    }
//
//                    @Override
//                    public void onNetworkStatusChanged(String devId, boolean status) {
//
//                    }
//
//                    @Override
//                    public void onDevInfoUpdate(String devId) {
//
//                    }
//                });
//            }
        }
        if (SleepMood.size() > 0) {
            LinearLayout LightButton = new LinearLayout(act);
            LightButton.setOrientation(LinearLayout.VERTICAL);
            TextView text = new TextView(act);
            Button image = new Button(act);
            if (Build.MODEL.equals("YS4B")) {
                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            }
            image.setBackgroundResource(R.drawable.light_off_new);
            LightButton.addView(text);
            LightButton.addView(image);
            text.setGravity(Gravity.CENTER);
            text.setText(getResources().getString(R.string.sleepingMood));
            text.setTextColor(Color.LTGRAY);
            text.setTextSize(20);
            LightButton.setGravity(Gravity.CENTER);
            LightButton.setPadding(2,2,2,2);
            lightsLayout.addView(LightButton);
            String btn = "";
            if (SleepMood.get(0).getConditions() == null) {
                for (int i=0;i<SleepMood.size();i++) {
                    TuyaHomeSdk.newSceneInstance(SleepMood.get(i).getId()).executeScene(new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {

                        }

                        @Override
                        public void onSuccess() {

                        }
                    });
                }
            }
            else {
                for (int i=0;i<SleepMood.size();i++) {
                    if (SleepMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH1_B().devId)) {
                        Sleep = THEROOM.getSWITCH1_B() ;
                        btn = SleepMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                    else if (SleepMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH2_B().devId)) {
                        Sleep = THEROOM.getSWITCH2_B() ;
                        btn = SleepMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                    else if (SleepMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH3_B().devId)) {
                        Sleep = THEROOM.getSWITCH3_B() ;
                        btn = SleepMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                    else if (SleepMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH4_B().devId)) {
                        Sleep = THEROOM.getSWITCH4_B() ;
                        btn = SleepMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                }
            }
            String finalBtn = btn;
            if (Sleep != null) {
//                View.OnClickListener lon =  new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        RoomDevicesRef.child(Sleep.getName()).child(finalBtn).setValue("4");
//                        RoomDevicesRef.child(Sleep.getName()).child(finalBtn).setValue("1");
//                        x=0;
//                    }
//                };
//                View.OnClickListener loff =  new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        RoomDevicesRef.child(Sleep.getName()).child(finalBtn).setValue("4");
//                        RoomDevicesRef.child(Sleep.getName()).child(finalBtn).setValue("2");
//                        x=0;
//                    }
//                };
//                RoomDevicesRef.child(Sleep.getName()).child(btn).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.getValue() != null) {
//                            Log.d("btnPressed" , snapshot.getValue().toString());
//                            int Val = Integer.parseInt(snapshot.getValue().toString());
//                            if (Val == 3 || Val == 1) {
//                                image.setBackgroundResource(R.drawable.light_on_new);
//                                text.setTextColor(Color.WHITE);
//                                image.setOnClickListener(loff);
//                            }
//                            else if (Val == 0 || Val == 2) {
//                                image.setBackgroundResource(R.drawable.light_off_new);
//                                text.setTextColor(Color.LTGRAY);
//                                image.setOnClickListener(lon);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.d("btnPressed" , error.toString());
//                    }
//                });
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (THEROOM.sleeping) {
                            clickSwitchButton(TuyaHomeSdk.newDeviceInstance(Sleep.devId),finalBtn,false);
                        }
                        else {
                            clickSwitchButton(TuyaHomeSdk.newDeviceInstance(Sleep.devId),finalBtn,true);
                        }
                        x=0;
                    }
                });
                if (Sleep.dps.get(finalBtn) != null) {
                    RoomDevicesRef.child(Sleep.getName()).child(finalBtn).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                Log.d("s1b1",snapshot.getValue().toString());
                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    THEROOM.sleeping = true ;
                                    image.setBackgroundResource(R.drawable.light_on_new);
                                }
                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    THEROOM.sleeping = false ;
                                    image.setBackgroundResource(R.drawable.light_off_new);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
//            image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (Sleep != null && finalBtn != null) {
//                        if (SLEEP_STATUS[0]) {
//                            TuyaHomeSdk.newDeviceInstance(Sleep.devId).publishDps("{\" "+ finalBtn +"\": false}", new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//                                    TuyaHomeSdk.newDeviceInstance(Sleep.devId).publishDps("{\" "+ finalBtn +"\": true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//
//                                        @Override
//                                        public void onSuccess() {
//
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onSuccess() {
//
//                                }
//                            });
//                        }
//                        else {
//                            TuyaHomeSdk.newDeviceInstance(Sleep.devId).publishDps("{\" "+ finalBtn +"\": true}", new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//                                    TuyaHomeSdk.newDeviceInstance(Sleep.devId).publishDps("{\" "+ finalBtn +"\": false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//
//                                        @Override
//                                        public void onSuccess() {
//
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onSuccess() {
//
//                                }
//                            });
//                        }
//                    }
//                    x=0;
//                }
//            });
//            if (Sleep != null) {
//                if (Sleep.getDps().get(finalBtn) != null) {
//                    if (Boolean.parseBoolean(Sleep.dps.get(finalBtn).toString())) {
//                        image.setBackgroundResource(R.drawable.light_on_new);
//                        text.setTextColor(Color.WHITE);
//                        SLEEP_STATUS[0] = true ;
//                    }
//                    else {
//                        image.setBackgroundResource(R.drawable.light_off_new);
//                        text.setTextColor(Color.LTGRAY);
//                        SLEEP_STATUS[0] = false ;
//                    }
//                }
//                TuyaHomeSdk.newDeviceInstance(Sleep.devId).registerDeviceListener(new IDeviceListener() {
//                    @Override
//                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
//                        if (Sleep.getDps().get(finalBtn) != null) {
//                            if (Boolean.parseBoolean(Sleep.getDps().get(finalBtn).toString())) {
//                                image.setBackgroundResource(R.drawable.light_on_new);
//                                text.setTextColor(Color.WHITE);
//                                SLEEP_STATUS[0] = true ;
//                            }
//                            else {
//                                image.setBackgroundResource(R.drawable.light_off_new);
//                                text.setTextColor(Color.LTGRAY);
//                                SLEEP_STATUS[0] = false ;
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onRemoved(String devId) {
//
//                    }
//
//                    @Override
//                    public void onStatusChanged(String devId, boolean online) {
//
//                    }
//
//                    @Override
//                    public void onNetworkStatusChanged(String devId, boolean status) {
//
//                    }
//
//                    @Override
//                    public void onDevInfoUpdate(String devId) {
//
//                    }
//                });
//            }
        }
        if (WorkMood.size() > 0) {
            LinearLayout LightButton = new LinearLayout(act);
            LightButton.setOrientation(LinearLayout.VERTICAL);
            TextView text = new TextView(act);
            Button image = new Button(act);
            if (Build.MODEL.equals("YS4B")) {
                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            }
            image.setBackgroundResource(R.drawable.light_off_new);
            LightButton.addView(text);
            LightButton.addView(image);
            text.setGravity(Gravity.CENTER);
            text.setText(getResources().getString(R.string.workMood));
            text.setTextColor(Color.LTGRAY);
            text.setTextSize(20);
            LightButton.setGravity(Gravity.CENTER);
            LightButton.setPadding(2,2,2,2);
            lightsLayout.addView(LightButton);
            String btn = "";
            if (WorkMood.get(0).getConditions() == null) {
                for (int i=0;i<WorkMood.size();i++) {
                    TuyaHomeSdk.newSceneInstance(WorkMood.get(i).getId()).executeScene(new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {

                        }

                        @Override
                        public void onSuccess() {

                        }
                    });
                }
            }
            else {
                for (int i=0;i<WorkMood.size();i++) {
                    if (WorkMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH1_B().devId)) {
                        Work = THEROOM.getSWITCH1_B() ;
                        btn = WorkMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                    else if (WorkMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH2_B().devId)) {
                        Work = THEROOM.getSWITCH2_B() ;
                        btn = WorkMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                    else if (WorkMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH3_B().devId)) {
                        Work = THEROOM.getSWITCH3_B() ;
                        btn = WorkMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                    else if (WorkMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH4_B().devId)) {
                        Work = THEROOM.getSWITCH4_B() ;
                        btn = WorkMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                    Log.d("MoodCondS", THEROOM.getSWITCH2_B().getDevId() + " " + SleepMood.get(i).getConditions().get(0).getEntityId() + " " + SleepMood.get(i).getConditions().get(0).getId() + " " + SleepMood.get(i).getConditions().get(0).getEntitySubIds() + " " + SleepMood.get(i).getConditions().get(0).getEntityType());
                }
            }
            String finalBtn = btn;
            if (Work != null) {
//                View.OnClickListener lon =  new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        RoomDevicesRef.child(Work.getName()).child(finalBtn).setValue("4");
//                        RoomDevicesRef.child(Work.getName()).child(finalBtn).setValue("1");
//                        x=0;
//                    }
//                };
//                View.OnClickListener loff =  new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        RoomDevicesRef.child(Work.getName()).child(finalBtn).setValue("4");
//                        RoomDevicesRef.child(Work.getName()).child(finalBtn).setValue("2");
//                        x=0;
//                    }
//                };
//                RoomDevicesRef.child(Work.getName()).child(btn).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.getValue() != null) {
//                            Log.d("btnPressed" , snapshot.getValue().toString());
//                            int Val = Integer.parseInt(snapshot.getValue().toString());
//                            if (Val == 3 || Val == 1) {
//                                image.setBackgroundResource(R.drawable.light_on_new);
//                                text.setTextColor(Color.WHITE);
//                                image.setOnClickListener(loff);
//                            }
//                            else if (Val == 0 || Val == 2) {
//                                image.setBackgroundResource(R.drawable.light_off_new);
//                                text.setTextColor(Color.LTGRAY);
//                                image.setOnClickListener(lon);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.d("btnPressed" , error.toString());
//                    }
//                });
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (THEROOM.work) {
                            clickSwitchButton(TuyaHomeSdk.newDeviceInstance(Work.devId),finalBtn,false);
                        }
                        else {
                            clickSwitchButton(TuyaHomeSdk.newDeviceInstance(Work.devId),finalBtn,true);
                        }
                        x=0;
                    }
                });
                if (Work.dps.get(finalBtn) != null) {
                    RoomDevicesRef.child(Work.getName()).child(finalBtn).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                Log.d("s1b1",snapshot.getValue().toString());
                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    THEROOM.work = true ;
                                    image.setBackgroundResource(R.drawable.light_on_new);
                                }
                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    THEROOM.work = false ;
                                    image.setBackgroundResource(R.drawable.light_off_new);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
//            image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (Work != null && finalBtn != null) {
//                        if (WORK_STATUS[0]) {
//                            TuyaHomeSdk.newDeviceInstance(Work.devId).publishDps("{\" "+ finalBtn +"\": false}", new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//                                    TuyaHomeSdk.newDeviceInstance(Work.devId).publishDps("{\" "+ finalBtn +"\": true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//
//                                        @Override
//                                        public void onSuccess() {
//
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onSuccess() {
//
//                                }
//                            });
//                        }
//                        else {
//                            TuyaHomeSdk.newDeviceInstance(Work.devId).publishDps("{\" "+ finalBtn +"\": true}", new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//                                    TuyaHomeSdk.newDeviceInstance(Work.devId).publishDps("{\" "+ finalBtn +"\": false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//
//                                        @Override
//                                        public void onSuccess() {
//
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onSuccess() {
//
//                                }
//                            });
//                        }
//                    }
//                    x=0;
//                }
//            });
//            if (Work != null) {
//                if (Work.getDps().get(finalBtn) != null) {
//                    if (Boolean.parseBoolean(Work.dps.get(finalBtn).toString())) {
//                        image.setBackgroundResource(R.drawable.light_on_new);
//                        text.setTextColor(Color.WHITE);
//                        WORK_STATUS[0] = true ;
//                    }
//                    else {
//                        image.setBackgroundResource(R.drawable.light_off_new);
//                        text.setTextColor(Color.LTGRAY);
//                        WORK_STATUS[0] = false ;
//                    }
//                }
//                TuyaHomeSdk.newDeviceInstance(Work.devId).registerDeviceListener(new IDeviceListener() {
//                    @Override
//                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
//                        if (Work.getDps().get(finalBtn) != null) {
//                            if (Boolean.parseBoolean(Work.getDps().get(finalBtn).toString())) {
//                                image.setBackgroundResource(R.drawable.light_on_new);
//                                text.setTextColor(Color.WHITE);
//                                WORK_STATUS[0] = true ;
//                            }
//                            else {
//                                image.setBackgroundResource(R.drawable.light_off_new);
//                                text.setTextColor(Color.LTGRAY);
//                                WORK_STATUS[0] = false ;
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onRemoved(String devId) {
//
//                    }
//
//                    @Override
//                    public void onStatusChanged(String devId, boolean online) {
//
//                    }
//
//                    @Override
//                    public void onNetworkStatusChanged(String devId, boolean status) {
//
//                    }
//
//                    @Override
//                    public void onDevInfoUpdate(String devId) {
//
//                    }
//                });
//            }
        }
        if (RomanceMood.size() > 0) {
            LinearLayout LightButton = new LinearLayout(act);
            LightButton.setOrientation(LinearLayout.VERTICAL);
            TextView text = new TextView(act);
            Button image = new Button(act);
            if (Build.MODEL.equals("YS4B")) {
                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            }
            image.setBackgroundResource(R.drawable.light_off_new);
            LightButton.addView(text);
            LightButton.addView(image);
            text.setGravity(Gravity.CENTER);
            text.setText(getResources().getString(R.string.romanceMood));
            text.setTextColor(Color.LTGRAY);
            text.setTextSize(20);
            LightButton.setGravity(Gravity.CENTER);
            LightButton.setPadding(2,2,2,2);
            lightsLayout.addView(LightButton);
            String btn = "";
            if (RomanceMood.get(0).getConditions() == null) {
                for (int i=0;i<RomanceMood.size();i++) {
                    TuyaHomeSdk.newSceneInstance(RomanceMood.get(i).getId()).executeScene(new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {

                        }

                        @Override
                        public void onSuccess() {

                        }
                    });
                }
            }
            else {
                for (int i=0;i<RomanceMood.size();i++) {
                    if (RomanceMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH1_B().devId)) {
                        Romance = THEROOM.getSWITCH1_B() ;
                        btn = RomanceMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                    else if (RomanceMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH2_B().devId)) {
                        Romance = THEROOM.getSWITCH2_B() ;
                        btn = RomanceMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                    else if (RomanceMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH3_B().devId)) {
                        Romance = THEROOM.getSWITCH3_B() ;
                        btn = RomanceMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                    else if (RomanceMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH4_B().devId)) {
                        Romance = THEROOM.getSWITCH4_B() ;
                        btn = RomanceMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                }
            }
            String finalBtn = btn;
            if (Romance != null) {
//                View.OnClickListener lon =  new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        RoomDevicesRef.child(Romance.getName()).child(finalBtn).setValue("4");
//                        RoomDevicesRef.child(Romance.getName()).child(finalBtn).setValue("1");
//                        x=0;
//                    }
//                };
//                View.OnClickListener loff =  new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        RoomDevicesRef.child(Romance.getName()).child(finalBtn).setValue("4");
//                        RoomDevicesRef.child(Romance.getName()).child(finalBtn).setValue("2");
//                        x=0;
//                    }
//                };
//                RoomDevicesRef.child(Romance.getName()).child(btn).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.getValue() != null) {
//                            Log.d("btnPressed" , snapshot.getValue().toString());
//                            int Val = Integer.parseInt(snapshot.getValue().toString());
//                            if (Val == 3 || Val == 1) {
//                                image.setBackgroundResource(R.drawable.light_on_new);
//                                text.setTextColor(Color.WHITE);
//                                image.setOnClickListener(loff);
//                            }
//                            else if (Val == 0 || Val == 2) {
//                                image.setBackgroundResource(R.drawable.light_off_new);
//                                text.setTextColor(Color.LTGRAY);
//                                image.setOnClickListener(lon);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.d("btnPressed" , error.toString());
//                    }
//                });
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (THEROOM.romance) {
                        clickSwitchButton(TuyaHomeSdk.newDeviceInstance(Romance.devId),finalBtn,false);
                    }
                    else {
                        clickSwitchButton(TuyaHomeSdk.newDeviceInstance(Romance.devId),finalBtn,true);
                    }
                    x=0;
                }
            });
            if (Romance.dps.get(finalBtn) != null) {
                RoomDevicesRef.child(Romance.getName()).child(finalBtn).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            Log.d("s1b1",snapshot.getValue().toString());
                            if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                THEROOM.romance = true ;
                                image.setBackgroundResource(R.drawable.light_on_new);
                            }
                            else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                THEROOM.romance = false ;
                                image.setBackgroundResource(R.drawable.light_off_new);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            }
//            image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (Romance != null && finalBtn != null) {
//                        if (ROMANCE_STATUS[0]) {
//                            TuyaHomeSdk.newDeviceInstance(Romance.devId).publishDps("{\" "+ finalBtn +"\": false}", new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//                                    TuyaHomeSdk.newDeviceInstance(Romance.devId).publishDps("{\" "+ finalBtn +"\": true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//
//                                        @Override
//                                        public void onSuccess() {
//
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onSuccess() {
//
//                                }
//                            });
//                        }
//                        else {
//                            TuyaHomeSdk.newDeviceInstance(Romance.devId).publishDps("{\" "+ finalBtn +"\": true}", new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//                                    TuyaHomeSdk.newDeviceInstance(Romance.devId).publishDps("{\" "+ finalBtn +"\": false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//
//                                        @Override
//                                        public void onSuccess() {
//
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onSuccess() {
//
//                                }
//                            });
//                        }
//                    }
//                    x=0;
//                }
//            });
//            if (Romance != null) {
//                if (Romance.getDps().get(finalBtn) != null) {
//                    if (Boolean.parseBoolean(Romance.getDps().get(finalBtn).toString())) {
//                        image.setBackgroundResource(R.drawable.light_on_new);
//                        text.setTextColor(Color.WHITE);
//                        ROMANCE_STATUS[0] = true ;
//                    }
//                    else {
//                        image.setBackgroundResource(R.drawable.light_off_new);
//                        text.setTextColor(Color.LTGRAY);
//                        ROMANCE_STATUS[0] = false ;
//                    }
//                }
//                TuyaHomeSdk.newDeviceInstance(Romance.devId).registerDeviceListener(new IDeviceListener() {
//                    @Override
//                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
//                        if (Romance.getDps().get(finalBtn) != null) {
//                            if (Boolean.parseBoolean(Romance.getDps().get(finalBtn).toString())) {
//                                image.setBackgroundResource(R.drawable.light_on_new);
//                                text.setTextColor(Color.WHITE);
//                                ROMANCE_STATUS[0] = true ;
//                            }
//                            else {
//                                image.setBackgroundResource(R.drawable.light_off_new);
//                                text.setTextColor(Color.LTGRAY);
//                                ROMANCE_STATUS[0] = false ;
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onRemoved(String devId) {
//
//                    }
//
//                    @Override
//                    public void onStatusChanged(String devId, boolean online) {
//
//                    }
//
//                    @Override
//                    public void onNetworkStatusChanged(String devId, boolean status) {
//
//                    }
//
//                    @Override
//                    public void onDevInfoUpdate(String devId) {
//
//                    }
//                });
//            }
        }
        if (ReadMood.size() > 0) {
            LinearLayout LightButton = new LinearLayout(act);
            LightButton.setOrientation(LinearLayout.VERTICAL);
            TextView text = new TextView(act);
            Button image = new Button(act);
            if (Build.MODEL.equals("YS4B")) {
                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            }
            image.setBackgroundResource(R.drawable.light_off_new);
            LightButton.addView(text);
            LightButton.addView(image);
            text.setGravity(Gravity.CENTER);
            text.setText(getResources().getString(R.string.readingMood));
            text.setTextColor(Color.LTGRAY);
            text.setTextSize(20);
            LightButton.setGravity(Gravity.CENTER);
            LightButton.setPadding(2,2,2,2);
            lightsLayout.addView(LightButton);
            String btn = "";
            if (ReadMood.get(0).getConditions() == null) {
                for (int i=0;i<ReadMood.size();i++) {
                    TuyaHomeSdk.newSceneInstance(ReadMood.get(i).getId()).executeScene(new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {

                        }

                        @Override
                        public void onSuccess() {

                        }
                    });
                }
            }
            else {
                for (int i=0;i<ReadMood.size();i++) {
                    if (ReadMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH1_B().devId)) {
                        Read = THEROOM.getSWITCH1_B() ;
                        btn = ReadMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                    else if (ReadMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH2_B().devId)) {
                        Read = THEROOM.getSWITCH2_B() ;
                        btn = ReadMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                    else if (ReadMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH3_B().devId)) {
                        Read = THEROOM.getSWITCH3_B() ;
                        btn = ReadMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                    else if (ReadMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH4_B().devId)) {
                        Read = THEROOM.getSWITCH4_B() ;
                        btn = ReadMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                }
            }
            String finalBtn = btn;
            if (Read != null) {
//                View.OnClickListener lon =  new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        RoomDevicesRef.child(Read.getName()).child(finalBtn).setValue("4");
//                        RoomDevicesRef.child(Read.getName()).child(finalBtn).setValue("1");
//                        x=0;
//                    }
//                };
//                View.OnClickListener loff =  new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        RoomDevicesRef.child(Read.getName()).child(finalBtn).setValue("4");
//                        RoomDevicesRef.child(Read.getName()).child(finalBtn).setValue("2");
//                        x=0;
//                    }
//                };
//                RoomDevicesRef.child(Read.getName()).child(btn).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.getValue() != null) {
//                            Log.d("btnPressed" , snapshot.getValue().toString());
//                            int Val = Integer.parseInt(snapshot.getValue().toString());
//                            if (Val == 3 || Val == 1) {
//                                image.setBackgroundResource(R.drawable.light_on_new);
//                                text.setTextColor(Color.WHITE);
//                                image.setOnClickListener(loff);
//                            }
//                            else if (Val == 0 || Val == 2) {
//                                image.setBackgroundResource(R.drawable.light_off_new);
//                                text.setTextColor(Color.LTGRAY);
//                                image.setOnClickListener(lon);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.d("btnPressed" , error.toString());
//                    }
//                });
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (THEROOM.read) {
                        clickSwitchButton(TuyaHomeSdk.newDeviceInstance(Read.devId),finalBtn,false);
                    }
                    else {
                        clickSwitchButton(TuyaHomeSdk.newDeviceInstance(Read.devId),finalBtn,true);
                    }
                    x=0;
                }
            });
            if (Read.dps.get(finalBtn) != null) {
                RoomDevicesRef.child(Read.getName()).child(finalBtn).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            Log.d("s1b1",snapshot.getValue().toString());
                            if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                THEROOM.read = true ;
                                image.setBackgroundResource(R.drawable.light_on_new);
                            }
                            else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                THEROOM.read = false ;
                                image.setBackgroundResource(R.drawable.light_off_new);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            }
//            image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (Read != null && finalBtn != null) {
//                        if (READ_STATUS[0]) {
//                            TuyaHomeSdk.newDeviceInstance(Read.devId).publishDps("{\" "+ finalBtn +"\": false}", new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//                                    TuyaHomeSdk.newDeviceInstance(Read.devId).publishDps("{\" "+ finalBtn +"\": true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//
//                                        @Override
//                                        public void onSuccess() {
//
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onSuccess() {
//
//                                }
//                            });
//                        }
//                        else {
//                            TuyaHomeSdk.newDeviceInstance(Read.devId).publishDps("{\" "+ finalBtn +"\": true}", new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//                                    TuyaHomeSdk.newDeviceInstance(Read.devId).publishDps("{\" "+ finalBtn +"\": false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//
//                                        @Override
//                                        public void onSuccess() {
//
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onSuccess() {
//
//                                }
//                            });
//                        }
//                    }
//                    x=0;
//                }
//            });
//            if (Read != null) {
//                if (Read.getDps().get(finalBtn) != null) {
//                    if (Boolean.parseBoolean(Read.getDps().get(finalBtn).toString())) {
//                        image.setBackgroundResource(R.drawable.light_on_new);
//                        text.setTextColor(Color.WHITE);
//                        READ_STATUS[0] = true ;
//                    }
//                    else {
//                        image.setBackgroundResource(R.drawable.light_off_new);
//                        text.setTextColor(Color.LTGRAY);
//                        READ_STATUS[0] = false ;
//                    }
//                }
//                TuyaHomeSdk.newDeviceInstance(Read.devId).registerDeviceListener(new IDeviceListener() {
//                    @Override
//                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
//                        if (Read.getDps().get(finalBtn) != null) {
//                            if (Boolean.parseBoolean(Read.getDps().get(finalBtn).toString())) {
//                                image.setBackgroundResource(R.drawable.light_on_new);
//                                text.setTextColor(Color.WHITE);
//                                READ_STATUS[0] = true ;
//                            }
//                            else {
//                                image.setBackgroundResource(R.drawable.light_off_new);
//                                text.setTextColor(Color.LTGRAY);
//                                READ_STATUS[0] = true ;
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onRemoved(String devId) {
//
//                    }
//
//                    @Override
//                    public void onStatusChanged(String devId, boolean online) {
//
//                    }
//
//                    @Override
//                    public void onNetworkStatusChanged(String devId, boolean status) {
//
//                    }
//
//                    @Override
//                    public void onDevInfoUpdate(String devId) {
//
//                    }
//                });
//            }
        }
        if (MasterOffMood.size() > 0) {
            LinearLayout LightButton = new LinearLayout(act);
            LightButton.setOrientation(LinearLayout.VERTICAL);
            TextView text = new TextView(act);
            Button image = new Button(act);
            if (Build.MODEL.equals("YS4B")) {
                image.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            }
            image.setBackgroundResource(R.drawable.light_off_new);
            LightButton.addView(text);
            LightButton.addView(image);
            text.setGravity(Gravity.CENTER);
            text.setText(getResources().getString(R.string.masterOffMood));
            text.setTextColor(Color.LTGRAY);
            text.setTextSize(20);
            LightButton.setGravity(Gravity.CENTER);
            LightButton.setPadding(2,2,2,2);
            lightsLayout.addView(LightButton);
            String btn = "";
            if (MasterOffMood.get(0).getConditions() == null) {
                for (int i=0;i<MasterOffMood.size();i++) {
                    TuyaHomeSdk.newSceneInstance(MasterOffMood.get(i).getId()).executeScene(new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {

                        }

                        @Override
                        public void onSuccess() {

                        }
                    });
                }
            }
            else {
                for (int i=0;i<MasterOffMood.size();i++) {
                    if (MasterOffMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH1_B().devId)) {
                        MasterOff = THEROOM.getSWITCH1_B() ;
                        btn = MasterOffMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                    else if (MasterOffMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH2_B().devId)) {
                        MasterOff = THEROOM.getSWITCH2_B() ;
                        btn = MasterOffMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                    else if (MasterOffMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH3_B().devId)) {
                        MasterOff = THEROOM.getSWITCH3_B() ;
                        btn = MasterOffMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                    else if (MasterOffMood.get(i).getConditions().get(0).getEntityId().equals(THEROOM.getSWITCH4_B().devId)) {
                        MasterOff = THEROOM.getSWITCH4_B() ;
                        btn = MasterOffMood.get(i).getConditions().get(0).getEntitySubIds();
                    }
                }
            }
            String finalBtn = btn;
            if (MasterOff != null) {
//                View.OnClickListener lon =  new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        RoomDevicesRef.child(MasterOff.getName()).child(finalBtn).setValue("4");
//                        RoomDevicesRef.child(MasterOff.getName()).child(finalBtn).setValue("1");
//                        x=0;
//                    }
//                };
//                View.OnClickListener loff =  new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        RoomDevicesRef.child(MasterOff.getName()).child(finalBtn).setValue("4");
//                        RoomDevicesRef.child(MasterOff.getName()).child(finalBtn).setValue("2");
//                        x=0;
//                    }
//                };
//                RoomDevicesRef.child(MasterOff.getName()).child(btn).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.getValue() != null) {
//                            Log.d("btnPressed" , snapshot.getValue().toString());
//                            int Val = Integer.parseInt(snapshot.getValue().toString());
//                            if (Val == 3 || Val == 1) {
//                                image.setBackgroundResource(R.drawable.light_on_new);
//                                text.setTextColor(Color.WHITE);
//                                image.setOnClickListener(loff);
//                            }
//                            else if (Val == 0 || Val == 2) {
//                                image.setBackgroundResource(R.drawable.light_off_new);
//                                text.setTextColor(Color.LTGRAY);
//                                image.setOnClickListener(lon);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.d("btnPressed" , error.toString());
//                    }
//                });
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (THEROOM.masterOff) {
                            clickSwitchButton(TuyaHomeSdk.newDeviceInstance(MasterOff.devId),finalBtn,false);
                        }
                        else {
                            clickSwitchButton(TuyaHomeSdk.newDeviceInstance(MasterOff.devId),finalBtn,true);
                        }
                        x=0;
                    }
                });
                if (MasterOff.dps.get(finalBtn) != null) {
                    RoomDevicesRef.child(MasterOff.getName()).child(finalBtn).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                Log.d("s1b1",snapshot.getValue().toString());
                                if (Integer.parseInt(snapshot.getValue().toString()) == 3 || Integer.parseInt(snapshot.getValue().toString()) == 1) {
                                    THEROOM.masterOff = true ;
                                    image.setBackgroundResource(R.drawable.light_on_new);
                                }
                                else if (Integer.parseInt(snapshot.getValue().toString()) == 0 || Integer.parseInt(snapshot.getValue().toString()) == 2) {
                                    THEROOM.masterOff = false ;
                                    image.setBackgroundResource(R.drawable.light_off_new);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
//            image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (MasterOff != null && finalBtn != null) {
//                        if (MASTEROFF_STATUS[0]) {
//                            TuyaHomeSdk.newDeviceInstance(MasterOff.devId).publishDps("{\" "+ finalBtn +"\": false}", new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//                                    TuyaHomeSdk.newDeviceInstance(MasterOff.devId).publishDps("{\" "+ finalBtn +"\": true}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//
//                                        @Override
//                                        public void onSuccess() {
//
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onSuccess() {
//
//                                }
//                            });
//                        }
//                        else {
//                            TuyaHomeSdk.newDeviceInstance(MasterOff.devId).publishDps("{\" "+ finalBtn +"\": true}", new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//                                    TuyaHomeSdk.newDeviceInstance(MasterOff.devId).publishDps("{\" "+ finalBtn +"\": false}", new IResultCallback() {
//                                        @Override
//                                        public void onError(String code, String error) {
//
//                                        }
//
//                                        @Override
//                                        public void onSuccess() {
//
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onSuccess() {
//
//                                }
//                            });
//                        }
//                    }
//                    x=0;
//                }
//            });
//            if (MasterOff != null) {
//                if (MasterOff.getDps().get(finalBtn) != null) {
//                    if (Boolean.parseBoolean(MasterOff.getDps().get(finalBtn).toString())) {
//                        image.setBackgroundResource(R.drawable.light_on_new);
//                        text.setTextColor(Color.WHITE);
//                        MASTEROFF_STATUS[0] = true ;
//                    }
//                    else {
//                        image.setBackgroundResource(R.drawable.light_off_new);
//                        text.setTextColor(Color.LTGRAY);
//                        MASTEROFF_STATUS[0] = false ;
//                    }
//                }
//                TuyaHomeSdk.newDeviceInstance(MasterOff.devId).registerDeviceListener(new IDeviceListener() {
//                    @Override
//                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
//                        if (MasterOff.getDps().get(finalBtn) != null) {
//                            if (Boolean.parseBoolean(MasterOff.getDps().get(finalBtn).toString())) {
//                                image.setBackgroundResource(R.drawable.light_on_new);
//                                text.setTextColor(Color.WHITE);
//                                MASTEROFF_STATUS[0] = true ;
//                            }
//                            else {
//                                image.setBackgroundResource(R.drawable.light_off_new);
//                                text.setTextColor(Color.LTGRAY);
//                                MASTEROFF_STATUS[0] = false ;
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onRemoved(String devId) {
//
//                    }
//
//                    @Override
//                    public void onStatusChanged(String devId, boolean online) {
//
//                    }
//
//                    @Override
//                    public void onNetworkStatusChanged(String devId, boolean status) {
//
//                    }
//
//                    @Override
//                    public void onDevInfoUpdate(String devId) {
//
//                    }
//                });
//            }
        }
    }

    public static void OpenTheDoor(View view) {
        AVLoadingIndicatorView doorLoading = act.findViewById(R.id.loadingIcon);
        ImageView doorImage = act.findViewById(R.id.imageView17);
        if (MyApp.BluetoothLock != null) {
            doorImage.setVisibility(View.GONE);
            doorLoading.setVisibility(View.VISIBLE);
            String url = MyApp.ProjectURL + "roomsManagement/addClientDoorOpen";
            StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("doorOpenResp" , "BT"+response);
                    try {
                        JSONObject result = new JSONObject(response);
                        result.getString("result");
                        if (result.getString("result").equals("success")) {
                            TTLockClient.getDefault().controlLock(ControlAction.UNLOCK,THEROOM.getLock().getLockData(), THEROOM.getLock().getLockMac(),new ControlLockCallback() {
                                @Override
                                public void onControlLockSuccess(ControlLockResult controlLockResult) {
                                    ToastMaker.MakeToast("door opened",act);
                                    doorImage.setVisibility(View.VISIBLE);
                                    doorLoading.setVisibility(View.GONE);
                                }
                                @Override
                                public void onFail(LockError error) {
                                    ToastMaker.MakeToast(error.getErrorMsg(),act);
                                    doorImage.setVisibility(View.VISIBLE);
                                    doorLoading.setVisibility(View.GONE);
                                }
                            });
                        }
                        else {
                            ToastMaker.MakeToast(result.getString("error"),act);
                            doorImage.setVisibility(View.VISIBLE);
                            doorLoading.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        ToastMaker.MakeToast(e.getMessage(),act);
                        doorImage.setVisibility(View.VISIBLE);
                        doorLoading.setVisibility(View.GONE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ToastMaker.MakeToast(error.toString(),act);
                    doorImage.setVisibility(View.VISIBLE);
                    doorLoading.setVisibility(View.GONE);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("room_id", String.valueOf(THEROOM.id));
                    return params;
                }
            };
            if (FirebaseTokenRegister == null) {
                FirebaseTokenRegister = Volley.newRequestQueue(act) ;
            }
            FirebaseTokenRegister.add(req);
        }
        else {
            if (THEROOM.getLOCK_B() != null) {
                doorImage.setVisibility(View.GONE);
                doorLoading.setVisibility(View.VISIBLE);
                String url = MyApp.ProjectURL + "roomsManagement/addClientDoorOpen";
                StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("doorOpenResp" , "ZB"+response);
                        try {
                            JSONObject result = new JSONObject(response);
                            result.getString("result");
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
                                                        ToastMaker.MakeToast("door opened",act);
                                                        doorImage.setVisibility(View.VISIBLE);
                                                        doorLoading.setVisibility(View.GONE);
                                                    }

                                                    @Override
                                                    public void onFailed(String error) {
                                                        Log.d("openDoorResp" , "res "+error);
                                                        ToastMaker.MakeToast(error,act);
                                                        doorImage.setVisibility(View.VISIBLE);
                                                        doorLoading.setVisibility(View.GONE);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailed(String error) {
                                                Log.d("doorOpenResp" , "ticket "+error);
                                                ToastMaker.MakeToast(error,act);
                                                doorImage.setVisibility(View.VISIBLE);
                                                doorLoading.setVisibility(View.GONE);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailed(String error) {
                                        Log.d("doorOpenResp" , "token "+error);
                                        ToastMaker.MakeToast(error,act);
                                        doorImage.setVisibility(View.VISIBLE);
                                        doorLoading.setVisibility(View.GONE);
                                    }
                                });
                            }
                            else {
                                ToastMaker.MakeToast(result.getString("error"),act);
                                doorImage.setVisibility(View.VISIBLE);
                                doorLoading.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            Log.d("doorOpenResp" , e.getMessage());
                            ToastMaker.MakeToast(e.getMessage(),act);
                            doorImage.setVisibility(View.VISIBLE);
                            doorLoading.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("doorOpenResp" , error.toString());
                        ToastMaker.MakeToast(error.toString(),act);
                        doorImage.setVisibility(View.VISIBLE);
                        doorLoading.setVisibility(View.GONE);
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("room_id", String.valueOf(THEROOM.id));
                        return params;
                    }
                };
                if (FirebaseTokenRegister == null) {
                    FirebaseTokenRegister = Volley.newRequestQueue(act) ;
                }
                FirebaseTokenRegister.add(req);
            }
            else {
                new messageDialog("no lock detected in this room ","failed",act);
            }
        }
    }

    void getMiniBarMenu(int Facility) {
            LoadingDialog loading = new LoadingDialog(act);
            List<MINIBARITEM> list = new ArrayList<MINIBARITEM>();
            String url = LogIn.URL+"getMiniBarMenu.php";
            StringRequest laundryRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    if (response.equals("0"))
                    {
                        ToastMaker.MakeToast("No Items Recorded" , act );
                    }
                    else
                    {

                        try
                        {
                            JSONArray  arr = new JSONArray(response);
                            for (int i=0 ; i<arr.length();i++)
                            {
                                JSONObject row = arr.getJSONObject(i);
                                list.add(new MINIBARITEM(row.getInt("id"),row.getInt("Hotel"),row.getInt("Facility"),row.getString("Name"),row.getDouble("Price"),row.getString("photo")));
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                        if (list.size()>0)
                        {
                            MINIBAR_ADAPTER adapter = new MINIBAR_ADAPTER(list);
                            MINIBARMENU.setAdapter(adapter);
                        }
                        else
                        {
                            ToastMaker.MakeToast("No Items Recorded" , act );
                        }
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
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Hotel" ,"1");
                    params.put("Facility" , String.valueOf(Facility));
                    return params;
                }
            };
            Volley.newRequestQueue(act).add(laundryRequest);
    }

    private void hideSystemUI() {
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }

    private void hideMainBtns(){
        LinearLayout Btns = findViewById(R.id.MainBtns_Layout);
        TextView Text = findViewById(R.id.RoomNumber_MainScreen);
        LinearLayout Services = findViewById(R.id.Service_Btns);
        LinearLayout homeBtn = findViewById(R.id.home_Btn);
        TextView serviceText = findViewById(R.id.textView37);
        serviceText.setVisibility(View.VISIBLE);
        serviceText.setText(getResources().getString(R.string.services));
        homeBtn.setVisibility(View.VISIBLE);
        Services.setVisibility(View.VISIBLE);
        if (Laundries.size()>0){
            laundryPriceList.setVisibility(View.VISIBLE);
            LaundryBtn.setVisibility(View.VISIBLE);
        }
        else {
            laundryPriceList.setVisibility(View.GONE);
            LaundryBtn.setVisibility(View.GONE);
        }
        if (Minibar.size()>0){
              minibarPriceList.setVisibility(View.VISIBLE);
        }
        else {
               minibarPriceList.setVisibility(View.GONE);
        }
        Btns.setVisibility(View.GONE);
        Text.setVisibility(View.GONE);
        startBackHomeThread();
    }

    private static void roomServiceOn(){
        roomserviceimage.setImageResource(R.drawable.towels_on);
        roomserviceicon.setVisibility(View.VISIBLE);
        roomservicetext.setTextColor(RESOURCES.getColor(R.color.red));
    }
    private static void roomServiceOff(){
        roomserviceimage.setImageResource(R.drawable.towels);
        roomserviceicon.setVisibility(View.GONE);
        roomservicetext.setTextColor(Color.WHITE);
    }

    private static void checkoutOn(){
        checkoutimage.setImageResource(R.drawable.checkout_on);
        checkouticon.setVisibility(View.VISIBLE);
        text.setTextColor(RESOURCES.getColor(R.color.red));
    }
    private static void checkoutOff(){
        checkoutimage.setImageResource(R.drawable.checkout);
        checkouticon.setVisibility(View.GONE);
        text.setTextColor(Color.WHITE);
    }

    private static void laundryOn(){
        laundryImage.setImageResource(R.drawable.laundry_btn_on);
        laundryIcon.setVisibility(View.VISIBLE);
        laundryText.setTextColor(RESOURCES.getColor(R.color.red));
    }
    private static void laundryOff(){
        laundryImage.setImageResource(R.drawable.laundry_btn);
        laundryIcon.setVisibility(View.GONE);
        laundryText.setTextColor(Color.WHITE);
    }

    private static void cleanupOn(){
        cleanupImage.setImageResource(R.drawable.cleanup_btn_on);
        cleanupIcon.setVisibility(View.VISIBLE);
        cleanupText.setTextColor(RESOURCES.getColor(R.color.red));
    }
    private static void cleanupOff(){
        cleanupImage.setImageResource(R.drawable.cleanup_btn);
        cleanupIcon.setVisibility(View.GONE);
        cleanupText.setTextColor(Color.WHITE);
    }

    private static void dndOn(){
        dndImage.setImageResource(R.drawable.union_6);
        dndIcon.setVisibility(View.VISIBLE);
        dndText.setTextColor(RESOURCES.getColor(R.color.red));
    }
    private static void dndOff(){
        dndImage.setImageResource(R.drawable.union_2);
        dndIcon.setVisibility(View.GONE);
        dndText.setTextColor(Color.WHITE);
    }

    private static void sosOn(){
        sosImage.setImageResource(R.drawable.group_54);
        sosIcon.setVisibility(View.VISIBLE);
        sosText.setTextColor(RESOURCES.getColor(R.color.red));
    }
    private static void sosOff(){
        sosImage.setImageResource(R.drawable.group_33);
        sosIcon.setVisibility(View.GONE);
        sosText.setTextColor(Color.WHITE);
    }

    private static void restaurantOn(){
        //restaurantIcon.setImageResource(R.drawable.group_54);
        restaurantIcon.setVisibility(View.VISIBLE);
        //sosText.setTextColor(RESOURCES.getColor(R.color.red));
    }
    private static void restaurantOff(){
        //sosImage.setImageResource(R.drawable.group_33);
        restaurantIcon.setVisibility(View.GONE);
        //sosText.setTextColor(Color.WHITE);
    }

    public void backToMain(View view) {
        LinearLayout Btns = findViewById(R.id.MainBtns_Layout);
        TextView Text = findViewById(R.id.RoomNumber_MainScreen);
        LinearLayout Services = findViewById(R.id.Service_Btns);
        TextView serviceText = findViewById(R.id.textView37);
        HorizontalScrollView l = findViewById(R.id.light_buttons);
        LinearLayout laundryPricesLayout = findViewById(R.id.laundryList_Layout);
        LinearLayout minibarLayout = findViewById(R.id.Minibar_layout);
        LinearLayout minibarBtn = findViewById(R.id.minibar_priceList);
        LinearLayout home = findViewById(R.id.home_Btn);
        LinearLayout AcLayout = findViewById(R.id.ac_layout);
        serviceText.setVisibility(View.GONE);
        Services.setVisibility(View.GONE);
        laundryPricesLayout.setVisibility(View.GONE);
        laundryPriceList.setVisibility(View.GONE);
        home.setVisibility(View.GONE);
        l.setVisibility(View.GONE);
        minibarLayout.setVisibility(View.GONE);
        minibarBtn.setVisibility(View.GONE);
        AcLayout.setVisibility(View.GONE);
        Btns.setVisibility(View.VISIBLE);
        Text.setVisibility(View.VISIBLE);
    }

    public void goToLights(View view) {
        LinearLayout Btns = findViewById(R.id.MainBtns_Layout);
        TextView Text = findViewById(R.id.RoomNumber_MainScreen);
        Btns.setVisibility(View.GONE);
        Text.setVisibility(View.GONE);
        //Visible
        TextView Caption = findViewById(R.id.textView37);
        Caption.setVisibility(View.VISIBLE);
        Caption.setText(getResources().getString(R.string.lights));
        HorizontalScrollView lights = findViewById(R.id.light_buttons);
        lights.setVisibility(View.VISIBLE);
        LinearLayout home = findViewById(R.id.home_Btn);
        home.setVisibility(View.VISIBLE);
        startBackHomeThread();
    }

    void startBackHomeThread() {
        Log.d("backThread" , "started");
            backHomeThread.run();
    }

    public void showHideLaundryPriceList(View view) {
        LinearLayout btns = (LinearLayout) findViewById(R.id.Service_Btns);
        LinearLayout l = (LinearLayout) findViewById(R.id.laundryList_Layout);
        TextView caption = (TextView) findViewById(R.id.laundryList_caption);
        if (l.getVisibility() == View.GONE){
            btns.setVisibility(View.GONE);
            l.setVisibility(View.VISIBLE);
            caption.setText(getResources().getString(R.string.backToService));
        }
        else
        {
            btns.setVisibility(View.VISIBLE);
            l.setVisibility(View.GONE);
            caption.setText(getResources().getString(R.string.laundryPriceList));
        }
        x=0;
    }

    public void showHideMinibarPriceList(View view) {
        LinearLayout btns = (LinearLayout) findViewById(R.id.Service_Btns);
        LinearLayout m = (LinearLayout) findViewById(R.id.Minibar_layout);
        TextView caption = (TextView) findViewById(R.id.textView48);
        if (m.getVisibility() == View.GONE){
            getMiniBarMenu(Minibar.get(0).id);
            btns.setVisibility(View.GONE);
            m.setVisibility(View.VISIBLE);
            //caption.setText("Back To Services");
        }
        else
        {
            btns.setVisibility(View.VISIBLE);
            m.setVisibility(View.GONE);
            //caption.setText("Minibar PriceList");
        }
        x=0;
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
                            id = Integer.parseInt(Objects.requireNonNull(child.child("id").getValue()).toString());
                        }
                        String name = "";
                        if (child.child("name").getValue() != null ) {
                            try {
                                name = Objects.requireNonNull(child.child("name").getValue()).toString();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        int jobnum = 0;
                        if (child.child("jobNumber").getValue() != null ) {
                            try {
                                jobnum = Integer.parseInt(Objects.requireNonNull(child.child("jobNumber").getValue()).toString());
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        String department = "";
                        if (child.child("department").getValue() != null ) {
                            try {
                                department = Objects.requireNonNull(child.child("department").getValue()).toString();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        String mobile = "";
                        if (child.child("Mobile").getValue() != null ) {
                            try {
                                mobile = Objects.requireNonNull(child.child("Mobile").getValue()).toString();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        String token = "";
                        if (child.child("token").getValue() != null ) {
                            try {
                                token = Objects.requireNonNull(child.child("token").getValue()).toString();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Emps.add(new ServiceEmps(id,1,name,jobnum,department,mobile,token));
                    }
                    Log.d("EmpsAre",Emps.size()+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getMyDevices() {
        TuyaHomeSdk.newHomeInstance(MyApp.HOME.getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean homeBean) {
                Log.d("refreshDevices" , homeBean.getDeviceList().size()+" "+homeBean.getName());
                List<DeviceBean> TheDevicesList = homeBean.getDeviceList();
                if (TheDevicesList.size() == 0) {
                    ToastMaker.MakeToast("no devices detected" , act );
                }
                else {
                    for (int i=0;i<TheDevicesList.size();i++) {
                        if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Power")) {
                            MyApp.Room.setPOWER_B(TheDevicesList.get(i));
                            MyApp.Room.setPOWER(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getPOWER_B().devId));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"ZGatway")) {
                            MyApp.Room.setGATEWAY_B(TheDevicesList.get(i));
                            MyApp.Room.setGATEWAY(TuyaHomeSdk.newGatewayInstance(MyApp.Room.getGATEWAY_B().devId));
                        }
                        else if(TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"AC")) {
                            MyApp.Room.setAC_B(TheDevicesList.get(i));
                            MyApp.Room.setAC(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getAC_B().devId));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"DoorSensor")) {
                            MyApp.Room.setDOORSENSOR_B(TheDevicesList.get(i));
                            MyApp.Room.setDOORSENSOR(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getDOORSENSOR_B().devId));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"MotionSensor")) {
                            MyApp.Room.setMOTIONSENSOR_B(TheDevicesList.get(i));
                            MyApp.Room.setMOTIONSENSOR(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getMOTIONSENSOR_B().getDevId()));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Curtain")) {
                            MyApp.Room.setCURTAIN_B(TheDevicesList.get(i));
                            MyApp.Room.setCURTAIN(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getCURTAIN_B().getDevId()));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"ServiceSwitch")) {
                            MyApp.Room.setSERVICE1_B(TheDevicesList.get(i));
                            MyApp.Room.setSERVICE1(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSERVICE1_B().getDevId()));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch1")) {
                            MyApp.Room.setSWITCH1_B(TheDevicesList.get(i));
                            MyApp.Room.setSWITCH1(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH1_B().getDevId()));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch2")) {
                            MyApp.Room.setSWITCH2_B(TheDevicesList.get(i));
                            MyApp.Room.setSWITCH2(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH2_B().getDevId()));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch3")) {
                            MyApp.Room.setSWITCH3_B(TheDevicesList.get(i));
                            MyApp.Room.setSWITCH3(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH3_B().getDevId()));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch4")) {
                            MyApp.Room.setSWITCH4_B(TheDevicesList.get(i));
                            MyApp.Room.setSWITCH4(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getSWITCH4_B().getDevId()));
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Lock")) {
                            MyApp.Room.setLOCK_B(TheDevicesList.get(i));
                            MyApp.Room.setLOCK(TuyaHomeSdk.newDeviceInstance(MyApp.Room.getLOCK_B().getDevId()));
                        }
                    }
                    THEROOM = MyApp.Room ;
                }
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Log.d("refreshDevices" , errorCode + " " + errorMsg);
            }
        });
    }

    public void collapseNow() {

        // Initialize 'collapseNotificationHandler'
        if (collapseNotificationHandler == null) {
            collapseNotificationHandler = new Handler();
        }

        // If window focus has been lost && activity is not in a paused state
        // Its a valid check because showing of notification panel
        // steals the focus from current activity's window, but does not
        // 'pause' the activity
        if (!currentFocus && !isPaused) {

            // Post a Runnable with some delay - currently set to 300 ms
            collapseNotificationHandler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    // Use reflection to trigger a method from 'StatusBarManager'

                    @SuppressLint("WrongConstant") Object statusBarService = getSystemService("statusbar");
                    Class<?> statusBarManager = null;

                    try {
                        statusBarManager = Class.forName("android.app.StatusBarManager");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    Method collapseStatusBar = null;

                    try {
                        collapseStatusBar = Objects.requireNonNull(statusBarManager).getMethod("collapsePanels");
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }

                    assert collapseStatusBar != null;
                    collapseStatusBar.setAccessible(true);

                    try {
                        collapseStatusBar.invoke(statusBarService);
                    } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }

                    // Check if the window focus has been returned
                    // If it hasn't been returned, post this Runnable again
                    // Currently, the delay is 100 ms. You can change this
                    // value to suit your needs.
                    if (!currentFocus && !isPaused) {
                        collapseNotificationHandler.postDelayed(this, 100L);
                    }

                }
            }, 300L);
        }
    }

//    public static void setDND(View view) {
//        if (CURRENT_ROOM_STATUS == 2 ) {
//            if (!DNDStatus) {
//                Dialog d = new Dialog(act);
//                d.setContentView(R.layout.confermation_dialog);
//                d.setCancelable(false);
//                TextView head = (TextView) d.findViewById(R.id.textView2);
//                head.setText("Turn On (Don't Disturb) Mood");
//                TextView text = (TextView) d.findViewById(R.id.confermationDialog_Text);
//                text.setText("Do You Want To Turn On (Don't Disturb) Mood");
//                Button cancel = (Button) d.findViewById(R.id.confermationDialog_cancel);
//                cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        d.dismiss();
//                    }
//                });
//                Button ok = (Button) d.findViewById(R.id.messageDialog_ok);
//                ok.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        d.dismiss();
//                        if (THEROOM.getSERVICE1_B() != null ){
//                            List keys = new ArrayList(THEROOM.getSERVICE1_B().getDps().keySet());
//                            if (keys.contains("1") && keys.contains("2") && keys.contains("3") && keys.contains("4")) {
//                                if (THEROOM.getSERVICE1_B().dps.get("1") != null ) {
//                                    if (THEROOM.getSERVICE1_B().dps.get("1").toString().equals("false")) {
//                                        THEROOM.getSERVICE1().publishDps("{\"1\":true}", new IResultCallback() {
//                                            @Override
//                                            public void onError(String code, String error) {
//                                                Log.d("serviceSwitch", error);
//                                            }
//
//                                            @Override
//                                            public void onSuccess() {
//                                                Log.d("serviceSwitch", "success");
//                                            }
//                                        });
//                                    }
//                                }
//                            }
//                            else if (keys.contains("1") && keys.contains("2") && keys.contains("3")) {
//                                if (THEROOM.getSERVICE1_B().dps.get("3") != null ) {
//                                    if (THEROOM.getSERVICE1_B().dps.get("3").toString().equals("false")) {
//                                        THEROOM.getSERVICE1().publishDps("{\"3\":true}", new IResultCallback() {
//                                            @Override
//                                            public void onError(String code, String error) {
//                                                Log.d("serviceSwitch", error);
//                                            }
//
//                                            @Override
//                                            public void onSuccess() {
//                                                Log.d("serviceSwitch", "success");
//                                            }
//                                        });
//                                    }
//                                }
//                            }
//                        }
////                        LoadingDialog loading = new LoadingDialog(act);
////                        StringRequest request = new StringRequest(Request.Method.POST, insertServiceOrderUrl, new Response.Listener<String>() {
////                            @Override
////                            public void onResponse(String response) {
////
////                                if (CleanupStatus){
////                                    removeCleanupOrderInDataBase();
////                                }
////                                if (LaundryStatus){
////                                    removeLaundryOrderInDataBase();
////                                }
////                                if (RoomServiceStatus){
////                                    removeRoomServiceOrderInDataBase();
////                                }
////                                try {
////                                    Log.e("DND", response);
////                                    if (Integer.parseInt(response) > 0) {
////                                        loading.stop();
////                                        dndId = Integer.parseInt(response);
////                                        DNDStatus = true;
////                                        myRefDND.setValue(timee);
////                                        myRefdep.setValue("DND");
////                                        dndOff();
////                                    }
////                                } catch (Exception e) {
////                                    Log.e("DND", e.getMessage());
////                                }
////                                for(ServiceEmps emp : Emps) {
////                                    if (emp.department.equals("Service") || emp.department.equals("Cleanup") || emp.department.equals("Laundry") || emp.department.equals("RoomService")) {
////                                        emp.makemessage(emp.token,"DND",true,act);
////                                    }
////                                }
////                            }
////                        }
////                                , new Response.ErrorListener() {
////                            @Override
////                            public void onErrorResponse(VolleyError error) {
////                                Log.e("DNDerror", error.getMessage());
////                            }
////                        }) {
////                            @Override
////                            protected Map<String, String> getParams() throws AuthFailureError {
////                                Map<String, String> params = new HashMap<String, String>();
////                                params.put("roomNumber", String.valueOf(LogIn.room.getRoomNumber()));
////                                params.put("time", String.valueOf(timee));
////                                params.put("dep", dep);
////                                params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
////                                params.put("RorS", String.valueOf(RoomOrSuite));
////                                params.put("Reservation", String.valueOf(RESERVATION));
////                                return params;
////                            }
////                        };
////                        Volley.newRequestQueue(act).add(request);
//                    }
//                });
//                d.show();
//            } else {
//                Dialog d = new Dialog(act);
//                d.setContentView(R.layout.confermation_dialog);
//                d.setCancelable(false);
//                TextView head = (TextView) d.findViewById(R.id.textView2);
//                head.setText("Turn Off (Don't Disturb) Mood");
//                TextView text = (TextView) d.findViewById(R.id.confermationDialog_Text);
//                text.setText("Do You Want To Turn Off (Don't Disturb) Mood");
//                Button cancel = (Button) d.findViewById(R.id.confermationDialog_cancel);
//                cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        d.dismiss();
//                    }
//                });
//                Button ok = (Button) d.findViewById(R.id.messageDialog_ok);
//                ok.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        d.dismiss();
//                        if (THEROOM.getSERVICE1_B() != null ){
//                            List keys = new ArrayList(THEROOM.getSERVICE1_B().getDps().keySet());
//                            if (keys.contains("1") && keys.contains("2") && keys.contains("3") && keys.contains("4")) {
//                                if (THEROOM.getSERVICE1_B().dps.get("1") != null ) {
//                                    if (THEROOM.getSERVICE1_B().dps.get("1").toString().equals("true")) {
//                                        THEROOM.getSERVICE1().publishDps("{\"1\": false}", new IResultCallback() {
//                                            @Override
//                                            public void onError(String code, String error) {
//                                                Log.d("serviceSwitch", error);
//                                            }
//
//                                            @Override
//                                            public void onSuccess() {
//                                                Log.d("serviceSwitch", "success");
//                                            }
//                                        });
//                                    }
//                                }
//                            }
//                            else if (keys.contains("1") && keys.contains("2") && keys.contains("3")) {
//                                if (THEROOM.getSERVICE1_B().dps.get("3") != null ) {
//                                    if (THEROOM.getSERVICE1_B().dps.get("3").toString().equals("true")) {
//                                        THEROOM.getSERVICE1().publishDps("{\"3\": false}", new IResultCallback() {
//                                            @Override
//                                            public void onError(String code, String error) {
//                                                Log.d("serviceSwitch", error);
//                                            }
//
//                                            @Override
//                                            public void onSuccess() {
//                                                Log.d("serviceSwitch", "success");
//                                            }
//                                        });
//                                    }
//                                }
//                            }
//                        }
////                        LoadingDialog loading = new LoadingDialog(act);
////                        StringRequest rrr = new StringRequest(Request.Method.POST, removeServiceOrderUrl, new Response.Listener<String>() {
////                            @Override
////                            public void onResponse(String response) {
////
////                                if (response.equals("1")) {
////                                    loading.stop();
////                                    DNDStatus = false;
////                                    myRefDND.setValue(0);
////                                    //dnd.setBackgroundResource(R.drawable.dnd_off0);
////                                    dndOff();
////                                }
////                                for(ServiceEmps emp : Emps) {
////                                    if (emp.department.equals("Service") || emp.department.equals("Cleanup") || emp.department.equals("Laundry")) {
////                                        emp.makemessage(emp.token,"DND",false,act);
////                                    }
////                                }
////                            }
////                        }, new Response.ErrorListener() {
////                            @Override
////                            public void onErrorResponse(VolleyError error) {
////
////                            }
////                        }) {
////                            @Override
////                            protected Map<String, String> getParams() throws AuthFailureError {
////                                Map<String, String> params = new HashMap<String, String>();
////                                params.put("id", String.valueOf(dndId));
////                                params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
////                                params.put("dep", dep);
////                                params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
////                                return params;
////                            }
////                        };
////                        Volley.newRequestQueue(act).add(rrr);
//                    }
//                });
//                d.show();
//            }
//        }
//        else
//        {
//            ToastMaker.MakeToast("This Room Is Vacant" , act);
//        }
//    }

//    static void saveOpenDoorToDB() {
//        StringRequest request = new StringRequest(Request.Method.POST, registerDoorOpenUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d("registerOpen" , response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //dd.stop();
//                Toast.makeText(act,error.getMessage() , Toast.LENGTH_LONG);
//                Log.d("registerOpen" , error.getMessage());
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Calendar c = Calendar.getInstance(Locale.getDefault());
//                String Date = c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DAY_OF_MONTH);
//                String Time = c.get(Calendar.HOUR)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND);
//                Map<String,String> par = new HashMap<>();
//                par.put("EmpID" , "0");
//                par.put("JNum" , "0");
//                String name;
//                if (THERESERVATION != null ){
//                    name = THERESERVATION.ClientFirstName+" "+THERESERVATION.ClientLastName ;
//                }
//                else {
//                    name = "Client" ;
//                }
//                par.put("Name" , name);
//                par.put("Department" , "Client");
//                par.put("Room" , String.valueOf(THEROOM.RoomNumber));
//                par.put("Date" , Date);
//                par.put("Time" , Time);
//                return par;
//            }
//        };
//        Volley.newRequestQueue(act).add(request);
//    }

//    void getServiceEmps() {
//        StringRequest request = new StringRequest(Request.Method.POST, "", new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                if (response != null && !response.equals("0")) {
//                    try {
//                        JSONArray arr = new JSONArray(response);
//                        for (int i=0;i<arr.length();i++) {
//                            JSONObject row = arr.getJSONObject(i);
//                            ServiceEmps emp = new ServiceEmps(row.getInt("id"),row.getInt("projectId"),row.getString("name"),row.getInt("jobNumber"),row.getString("department"),row.getString("mobile"),row.getString("token"));
//                            Emps.add(emp);
//                        }
//                        Log.d("EmpsCount" , Emps.size()+"");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                else {
//                    ToastMaker.MakeToast("No service emps",act);
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        })
//        {
//
//        };
//        Volley.newRequestQueue(act).add(request);
//    }
//
//    void stopBackThread(){
//
//        Log.d("backThread" , "stoped");
//        H.removeCallbacks(backHomeThread);
//
//    }
//
//    void setbackThreadLayouts(){
//
//    }
//
//    static void openDoorByGateway() {
//        LoadingDialog dd = new LoadingDialog(act);
//        StringRequest re = new StringRequest(Request.Method.POST, "https://api.ttlock.com/v3/lock/unlock", new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                dd.stop();
//                Log.d("unLockResponse",response);
//
//                try {
//                    JSONObject j = new JSONObject(response);
//                    int r = j.getInt("errcode");
//                    Log.d("unLockResponse",r+"");
//                    if (r == 1 || r == 0) {
//                        //ToastMaker.MakeToast("Door Opened",act);
//                        saveOpenDoorToDB();
//                    }
//                    else {
//                        ToastMaker.MakeToast("Open Failed",act);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                dd.stop();
//                Log.d("unLockResponse",error.getMessage());
//                //ToastMaker.MakeToast("Open Fail!  "+error,act);
//            }
//        })
//        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> par = new HashMap<String, String>();
//                Calendar c = Calendar.getInstance(Locale.getDefault());
//                par.put("clientId", ApiService.CLIENT_ID);
//                par.put("accessToken",LogIn.acc.getAccess_token());
//                par.put("lockId",String.valueOf(myTestLockEKey.getLockId()));
//                par.put("date", String.valueOf(c.getTimeInMillis()));
//                return par;
//            }
//        };
//        Volley.newRequestQueue(act).add(re);
//    }
//
//    static void openZigbeeLock() {
//        ZigbeeLock.getTokenFromApi(MyApp.cloudClientId, MyApp.cloudSecret, act, new RequestOrder() {
//            @Override
//            public void onSuccess(String token) {
//                ZigbeeLock.getTicketId(token, MyApp.cloudClientId, MyApp.cloudSecret, THEROOM.getLOCK_B().devId, act, new RequestOrder() {
//                    @Override
//                    public void onSuccess(String ticket) {
//
//                        ZigbeeLock.unlockWithoutPassword(token, ticket, MyApp.cloudClientId, MyApp.cloudSecret, THEROOM.getLOCK_B().devId, act, new RequestOrder() {
//                            @Override
//                            public void onSuccess(String token) {
//
//                            }
//
//                            @Override
//                            public void onFailed(String error) {
//
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onFailed(String error) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onFailed(String error) {
//
//            }
//        });
//    }
//
//    public void setButtons() {
//        myRefLaundry.addListenerForSingleValueEvent(new ValueEventListener() {
//            @TargetApi(Build.VERSION_CODES.M)
//            @RequiresApi(api = Build.VERSION_CODES.M)
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                try {
//                    String s = dataSnapshot.getValue().toString();
//                    if ( Long.parseLong(s) > 0 ) {
//                        LaundryStatus = true ;
//                        laundryOn();
//                    }
//                }
//                catch (Exception e) {
//                    Log.e("laundry" , e.getMessage());
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        myRefCleanup.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//            {
//                try
//                {
//                    String s = dataSnapshot.getValue().toString();
//                    if ( Long.parseLong(s) > 0 )
//                    {
//                        CleanupStatus = true ;
//                        cleanupOn();
//                    }
//                }
//                catch (Exception e )
//                {
//                    Log.e("cleanup" , e.getMessage());
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError)
//            {
//                //Toast.makeText(act , databaseError.getMessage(),Toast.LENGTH_LONG).show();
//            }
//        });
//        myRefRoomService.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                try {
//                    String s = dataSnapshot.getValue().toString();
//                    if ( Long.parseLong(s) > 0 ) {
//                        RoomServiceStatus = true ;
//                        roomServiceOn();
//                    }
//                }
//                catch (Exception e) {
//                    Log.e("roomservice" , e.getMessage());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError)
//            {
//                //Toast.makeText(act , databaseError.getMessage(),Toast.LENGTH_LONG).show();
//            }
//        });
//        myRefSos.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//            {
//                try
//                {
//                    Log.e("SOS" , dataSnapshot.getValue().toString() );
//                    String s = dataSnapshot.getValue().toString();
//                    if ( Long.parseLong(s) > 0 )
//                    {
//                        SosStatus = true ;
//                        sosOn();
//                    }
//                }
//                catch (Exception e )
//                {
//                    Log.e("SOS" , e.getMessage());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError)
//            {
//                //Toast.makeText(act , databaseError.getMessage(),Toast.LENGTH_LONG).show();
//            }
//        });
//        myRefRestaurant.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                try {
//                    String s = dataSnapshot.getValue().toString();
//                    if ( Long.parseLong(s) > 0 ) {
//                        RestaurantStatus = true ;
//                        //RestaurantBtn.setBackgroundResource(R.drawable.restaurant_icon2);
//                    }
//                }
//                catch (Exception e) {
//                    Log.e("Restaurant" , e.getMessage() );
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError)
//            {
//                //Toast.makeText(act , databaseError.getMessage(),Toast.LENGTH_LONG).show();
//            }
//        });
//        myRefCheckout.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//            {
//                try
//                {
//                    String s = dataSnapshot.getValue().toString();
//                    if ( Long.parseLong(s) > 0 )
//                    {
//                        CheckoutStatus = true ;
//                        checkoutOn();
//                    }
//                }
//                catch (Exception e)
//                {
//                    Log.e("checkout" , e.getMessage());
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError)
//            {
//                //Toast.makeText(act , databaseError.getMessage(),Toast.LENGTH_LONG).show();
//            }
//        });
//        myRefDND.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot)
//            {
//                String s = snapshot.getValue().toString();
//                if ( Long.parseLong(s) > 0 )
//                {
//                    DNDStatus = true ;
//                    dndOn();
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    private void getHotelTempSetpoint() {
//        String url = LogIn.URL+"getTempSetPointAndroid.php";
//        StringRequest re = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response)
//            {
//                //ToastMaker.MakeToast(response , act);
//                String temp ="";
//                try
//                {
//                    JSONObject row = new JSONObject(response);
//                    if ( !row.getString("Temp").equals("0"))
//                    {
//                        temp = row.getString("Temp") ;
//                        if (temp.length()==2)
//                        {
//                            temp = temp+"0";
//                            TempSetPoint = temp ;
//                        }
//                        else if (temp.length()>2)
//                        {
//                            TempSetPoint = temp ;
//                        }
//                    }
//                    if (!row.getString("Logo").equals("0")){
//                        LOGO = row.getString("Logo") ;
//                    }
//                    if (row.getInt("CheckInModeTime") != 0 )
//                    {
//                        checkInModeTime = row.getInt("CheckInModeTime") ;
//                    }
//                    if (row.getInt("CheckOutModeTime") != 0 )
//                    {
//                        checkOutModeTime = row.getInt("CheckOutModeTime");
//                    }
//                    Log.d("Duration" , "checkin "+checkInModeTime+" checkout "+checkOutModeTime);
//
//                }
//                catch (JSONException e)
//                {
//                    e.printStackTrace();
//                }
//                //Toast.makeText(act,temp+" "+TempSetPoint,Toast.LENGTH_LONG).show();
//                Log.d("tempSetPoint",temp+" "+TempSetPoint);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        })
//        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError
//            {
//                Map<String,String> pars = new HashMap<String, String>();
//                pars.put("Hotel","1");
//                return pars;
//            }
//        };
//        Volley.newRequestQueue(act).add(re);
//    }
//
//    public void showConnectLockToast() {
//        // ToastMaker.MakeToast("start connect lock...",act);
//        Dialog d = new Dialog(act);
//        d.setContentView(R.layout.loading_layout);
//        TextView t = (TextView) d.findViewById(R.id.textViewdfsdf);
//        t.setText("Door Opening");
//        d.setCancelable(false);
//        d.show();
//    }
//
//    public void goToLaundry(View view) {
//        if (CURRENT_ROOM_STATUS == 2) {
//            if (!LaundryStatus) {
//                final Dialog d = new Dialog(act);
//                d.setContentView(R.layout.confermation_dialog);
//                TextView message = (TextView) d.findViewById(R.id.confermationDialog_Text);
//                message.setText("You Are Sending Laundry Order .. Are You Sure");
//                Button cancel = (Button)d.findViewById(R.id.confermationDialog_cancel);
//                cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        d.dismiss();
//                    }
//                });
//                Button ok = (Button)d.findViewById(R.id.messageDialog_ok);
//                ok.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        d.dismiss();
//                        //addLaundryOrderInDataBase();
//                        List keys = new ArrayList(THEROOM.getSERVICE1_B().getDps().keySet());
//                        if (keys.contains("1") && keys.contains("2") && keys.contains("3") && keys.contains("4")) {
//                            if (THEROOM.getSERVICE1_B() != null ){
//                                if (THEROOM.getSERVICE1_B().dps.get("3") != null ){
//                                    if (THEROOM.getSERVICE1_B().dps.get("3").toString().equals("false")){
//                                        THEROOM.getSERVICE1().publishDps("{\"3\": true}", new IResultCallback() {
//                                            @Override
//                                            public void onError(String code, String error) {
//
//                                            }
//
//                                            @Override
//                                            public void onSuccess() {
//
//                                            }
//                                        });
//                                    }
//                                }
//
//                            }
//                        }
//                        else if (keys.contains("1") && keys.contains("2") && keys.contains("3")) {
//                            //turning laundry on
//                            if (THEROOM.getSERVICE1_B() != null ){
//                                if (THEROOM.getSERVICE1_B().dps.get("2") != null ){
//                                    if (THEROOM.getSERVICE1_B().dps.get("2").toString().equals("false")){
//                                        THEROOM.getSERVICE1().publishDps("{\"2\": true}", new IResultCallback() {
//                                            @Override
//                                            public void onError(String code, String error) {
//
//                                            }
//
//                                            @Override
//                                            public void onSuccess() {
//
//                                            }
//                                        });
//                                    }
//                                }
//
//                            }
//                        }
//                    }
//                });
//                d.show();
//            }
//            else {
//                final Dialog d = new Dialog(act);
//                d.setContentView(R.layout.confermation_dialog);
//                TextView message = (TextView) d.findViewById(R.id.confermationDialog_Text);
//                message.setText("You Are Cancelling Laundry Order .. Are You Sure");
//                Button cancel = (Button)d.findViewById(R.id.confermationDialog_cancel);
//                cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        d.dismiss();
//                    }
//                });
//                Button ok = (Button)d.findViewById(R.id.messageDialog_ok);
//                ok.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        d.dismiss();
//                        //removeLaundryOrderInDataBase();
//                        List keys = new ArrayList(THEROOM.getSERVICE1_B().getDps().keySet());
//                        if (keys.contains("1") && keys.contains("2") && keys.contains("3") && keys.contains("4")) {
//                            if (THEROOM.getSERVICE1_B() != null ){
//                                if (THEROOM.getSERVICE1_B().dps.get("3") != null ){
//                                    if (THEROOM.getSERVICE1_B().dps.get("3").toString().equals("true")){
//                                        THEROOM.getSERVICE1().publishDps("{\"3\": false}", new IResultCallback() {
//                                            @Override
//                                            public void onError(String code, String error) {
//
//                                            }
//
//                                            @Override
//                                            public void onSuccess() {
//
//                                            }
//                                        });
//                                    }
//                                }
//
//                            }
//                        }
//                        else if (keys.contains("1") && keys.contains("2") && keys.contains("3")) {
//                            //turning laundry on
//                            if (THEROOM.getSERVICE1_B() != null ){
//                                if (THEROOM.getSERVICE1_B().dps.get("2") != null ){
//                                    if (THEROOM.getSERVICE1_B().dps.get("2").toString().equals("true")){
//                                        THEROOM.getSERVICE1().publishDps("{\"2\": false}", new IResultCallback() {
//                                            @Override
//                                            public void onError(String code, String error) {
//
//                                            }
//
//                                            @Override
//                                            public void onSuccess() {
//
//                                            }
//                                        });
//                                    }
//                                }
//
//                            }
//                        }
//                    }
//                });
//                d.show();
//            }
//        }
//        else {
//            ToastMaker.MakeToast("This Room Is Vacant" , act);
//        }
//        x=0;
//    }
//
//    public static void requestCleanUp(View view) {
//        if (CURRENT_ROOM_STATUS == 2) {
//            if (CleanupStatus == false) {
//                final Dialog d = new Dialog(act);
//                d.setContentView(R.layout.confermation_dialog);
//                TextView message = (TextView) d.findViewById(R.id.confermationDialog_Text);
//                message.setText("You Are Sending CleanUp Order .. Are You Sure");
//                Button cancel = (Button)d.findViewById(R.id.confermationDialog_cancel);
//                cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        d.dismiss();
//                    }
//                });
//                Button ok = (Button)d.findViewById(R.id.messageDialog_ok);
//                ok.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        d.dismiss();
//                        //addCleanupOrderInDataBase();
//                        List keys = new ArrayList(THEROOM.getSERVICE1_B().getDps().keySet());
//                        if (keys.contains("1") && keys.contains("2") && keys.contains("3") && keys.contains("4")) {
//                            if (THEROOM.getSERVICE1_B() != null ){
//                                if (THEROOM.getSERVICE1_B().dps.get("2") != null ){
//                                    if (THEROOM.getSERVICE1_B().dps.get("2").toString().equals("false")){
//                                        THEROOM.getSERVICE1().publishDps("{\"2\": true}", new IResultCallback() {
//                                            @Override
//                                            public void onError(String code, String error) {
//
//                                            }
//
//                                            @Override
//                                            public void onSuccess() {
//
//                                            }
//                                        });
//                                    }
//                                }
//
//                            }
//                        }
//                        else if  (keys.contains("1") && keys.contains("2") && keys.contains("3")) {
//                            if (THEROOM.getSERVICE1_B() != null ){
//                                if (THEROOM.getSERVICE1_B().dps.get("1") != null ){
//                                    if (THEROOM.getSERVICE1_B().dps.get("1").toString().equals("false")){
//                                        THEROOM.getSERVICE1().publishDps("{\"1\": true}", new IResultCallback() {
//                                            @Override
//                                            public void onError(String code, String error) {
//
//                                            }
//
//                                            @Override
//                                            public void onSuccess() {
//
//                                            }
//                                        });
//                                    }
//                                }
//
//                            }
//                        }
//                    }
//                });
//                d.show();
//            }
//            else
//            {
//                final Dialog d = new Dialog(act);
//                d.setContentView(R.layout.confermation_dialog);
//                TextView message = (TextView) d.findViewById(R.id.confermationDialog_Text);
//                message.setText("You Are Cancelling CleanUp Order .. Are You Sure");
//                Button cancel = (Button)d.findViewById(R.id.confermationDialog_cancel);
//                cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        d.dismiss();
//                    }
//                });
//                Button ok = (Button)d.findViewById(R.id.messageDialog_ok);
//                ok.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        d.dismiss();
//                        //removeCleanupOrderInDataBase();
//                        List keys = new ArrayList(THEROOM.getSERVICE1_B().getDps().keySet());
//                        if (keys.contains("1") && keys.contains("2") && keys.contains("3") && keys.contains("4")) {
//                            if (THEROOM.getSERVICE1_B() != null ){
//                                if (THEROOM.getSERVICE1_B().dps.get("2") != null ){
//                                    if (THEROOM.getSERVICE1_B().dps.get("2").toString().equals("true")){
//                                        THEROOM.getSERVICE1().publishDps("{\"2\": false}", new IResultCallback() {
//                                            @Override
//                                            public void onError(String code, String error) {
//
//                                            }
//
//                                            @Override
//                                            public void onSuccess() {
//
//                                            }
//                                        });
//                                    }
//                                }
//
//                            }
//                        }
//                        else if (keys.contains("1") && keys.contains("2") && keys.contains("3")) {
//                            if (THEROOM.getSERVICE1_B() != null ){
//                                if (THEROOM.getSERVICE1_B().dps.get("1") != null ){
//                                    if (THEROOM.getSERVICE1_B().dps.get("1").toString().equals("true")){
//                                        THEROOM.getSERVICE1().publishDps("{\"1\": false}", new IResultCallback() {
//                                            @Override
//                                            public void onError(String code, String error) {
//
//                                            }
//
//                                            @Override
//                                            public void onSuccess() {
//
//                                            }
//                                        });
//                                    }
//                                }
//
//                            }
//                        }
//                    }
//                });
//                d.show();
//
//            }
//        }
//        else
//        {
//            ToastMaker.MakeToast("This Room Is Vacant" , act);
//        }
//        x=0;
//    }
//
//    static void finishCleanup() {
//        //Toast.makeText(act , "done",Toast.LENGTH_LONG).show();
//        myRefCleanup.setValue(0);
//        CleanupStatus = false ;
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                ToastMaker.MakeToast("تم تنفيذ طلب تنظيف الغرفة" , act );
//            }
//        });
//    }
//
//    static void finishLaundry() {
//        myRefLaundry.setValue(0);
//        LaundryStatus = false ;
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                ToastMaker.MakeToast("تم تنفيذ طلب المغسلة" , act );
//            }
//        });
//    }
//
//    static void finishRoomService() {
//        myRefRoomService.setValue(0);
//        RoomServiceStatus = false ;
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                ToastMaker.MakeToast("تم تنفيذ طلب خدمة الغرف" , act );
//            }
//        });
//    }
//
//    void finishSOS() {
//        myRefSos.setValue(0);
//        SosStatus = false ;
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                sosImage.setImageResource(R.drawable.union_2);
//                sosText.setTextColor( getResources().getColor(R.color.light_blue_A200)) ;
//                sosIcon.setVisibility(View.GONE);
//            }
//        });
//
//    }
//
//    static void finishRestaurant() {
//        myRefRestaurant.setValue(0);
//        RestaurantStatus = false ;
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                restaurantOff();
//                ToastMaker.MakeToast("تم تنفيذ طلب المطعم" ,act);
//            }
//        });
//    }
//
//    static void finishCheckout() {
//        myRefCheckout.setValue(0);
//        CheckoutStatus = false ;
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run()
//            {
//                //checkOut.setBackgroundResource(R.drawable.checkout_btn);
//                //RestaurantBtn.setTextColor(Color.parseColor("#FFE083") );
//                // messageDialog d = new messageDialog("تم تنفيذ طلب الطوارئ","تاكيد"  ,act);
//                ToastMaker.MakeToast("تم تنفيذ طلب CheckOut" ,act);
//
//            }
//        });
//    }
//
//    public static void PowerOff() {
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                if (THEROOM.getPOWER() != null ) {
//                    THEROOM.getPOWER().publishDps("{\"1\": false}", new IResultCallback() {
//                        @Override
//                        public void onError(String code, String error) {
//                            Toast.makeText(act, "turn on the light failure", Toast.LENGTH_SHORT).show();
//                        }
//                        @Override
//                        public void onSuccess() {
//                            Toast.makeText(act, "turn Off 1 success "+THEROOM.RoomNumber, Toast.LENGTH_SHORT).show();
//                            //myRefPower.setValue(0);
//                        }
//                    });
//                    THEROOM.getPOWER().publishDps("{\"2\": false}", new IResultCallback() {
//                        @Override
//                        public void onError(String code, String error) {
//                            Toast.makeText(act, "turn on the light failure", Toast.LENGTH_SHORT).show();
//                        }
//                        @Override
//                        public void onSuccess()
//                        {
//                            Toast.makeText(act, "turn Off 2 success "+THEROOM.RoomNumber, Toast.LENGTH_SHORT).show();
//
//                        }
//                    });
//                }
//            }
//        });
//    }
//
//    public static void PowerOn() {
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                if (THEROOM.getPOWER() != null ) {
//                    THEROOM.getPOWER().publishDps("{\"1\": true}", new IResultCallback() {
//                        @Override
//                        public void onError(String code, String error) {
//                            Toast.makeText(act, "turn on the light failure", Toast.LENGTH_SHORT).show();
//                        }
//                        @Override
//                        public void onSuccess() {
//                            Toast.makeText(act, "turn on 1 success "+THEROOM.RoomNumber, Toast.LENGTH_SHORT).show();
//                            //myRefPower.setValue(0);
//                        }
//                    });
//                }
//            }
//        });
//    }
}
