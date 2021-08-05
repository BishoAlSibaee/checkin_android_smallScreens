package com.syriasoft.hotelservices;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
import com.squareup.picasso.Picasso;
import com.syriasoft.hotelservices.TUYA.Tuya_Devices;
import com.syriasoft.hotelservices.TUYA.Tuya_Login;
import com.syriasoft.hotelservices.lock.LockObj;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.ControlLockCallback;
import com.ttlock.bl.sdk.constant.ControlAction;
import com.ttlock.bl.sdk.entity.ControlLockResult;
import com.ttlock.bl.sdk.entity.LockError;
import com.tuya.smart.android.device.api.ITuyaDeviceMultiControl;
import com.tuya.smart.android.device.bean.MultiControlBean;
import com.tuya.smart.android.device.bean.MultiControlLinkBean;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.api.INeedLoginListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDataCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    static Activity act  ;
    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    final static private String serverKey = "key=" + "AAAAQmygXvw:APA91bFt5CiONiZPDDj4_kz9hmKXlL1cjfTa_ZNGfobMPmt0gamhzEoN2NHiOxypCDr_r5yfpLvJy-bQSgrykXvaqKkThAniTr-0hpXPBrXm7qWThMmkiaN9o6qaUqfIUwStMMuNedTw";
    final static private String contentType = "application/json";
    private String checkoutToken = LogIn.URL+"getReceptionTokens.php";
    private static String insertServiceOrderUrl = LogIn.URL+"insertServiceOrder.php";
    private static String removeServiceOrderUrl = LogIn.URL+"removeServiceOrder.php";
    private static String roomServiceOrderUrl = LogIn.URL+"insertRoomServiceOrder.php";
    private String getServiceEmpsUrl = LogIn.URL+"getServiceEmps.php";
    private String LogInUrl = LogIn.URL+"logInToHotel.php";
    final String TAG = "NOTIFICATION TAG";
    static Button  GymBtn , curtainBtn , Light1 , Light2 , Light3 ;
    static long  laundryOrderId , cleanupOrderId , roomServiceOrderId , sosId = 0 , checkOutId , dndId = 0 ;
    private FirebaseDatabase database ;
    static public DatabaseReference  ServiceUsers,myRefLogo,myRefCheckOutDuration,myRefCheckInDuration, myRefDoorWarning,myRefSetpointInterval,myRefSetpoint,myRefFacility,myRefRoomServiceText,myRefServiceSwitch,myRefPowerSwitch, myRefId, myRefRorS,myRefTemp,myRefdep,myRefStatus,myRefReservation ,myRefPower ,myRefCurtain , myRefDoor ,myRefRoomStatus , Room , myRefDND, myRefTabStatus, myRefLaundry , myRefCleanup , myRefRoomService , myRefSos , myRefRestaurant , myRefGym , myRefCheckout ,myRefDoorSensor,myRefMotionSensor,myRefCurtainSwitch,myRefSwitch1,myRefSwitch2,myRefSwitch3,myRefSwitch4,myRefThermostat,myRefLock;
    static boolean DNDStatus = false , LaundryStatus , CleanupStatus , RoomServiceStatus , SosStatus , RestaurantStatus , GymStatus = false , CheckoutStatus = false;
    static String roomServiceOrder ="";
    TextView time , date;
    static LockObj myTestLockEKey ;
    static int  RoomOrSuite =1 , ID ,CURRENT_ROOM_STATUS=0 ,RESERVATION =0 ;
    public static DeviceBean DoorSensorBean , MotionSensorBean , CurtainBean,ServiceSwitch , Switch1Bean , Switch2Bean , Switch3Bean , Switch4Bean;
    public static ITuyaDevice DoorSensor , MotionSensor , Curtain,ServiceS , Switch1  ,Switch2 , Switch3 , Switch4 ;
    long theTime = 1000*30 ;
    long theDoorTime = 1000*60*5 ;
    long thermostatStartTime = 0 , theThermoPeriod = 0 , theDoorStartTime=0 , theDoorPeriod=0  ;
    private String TempSetPoint = "250" ;
    private String ClientTemp = "0" ;
    private String CurrentTemp = "0" ;
    private Runnable TempRonnable ,DoorRunnable ;
    static  boolean  ACStatus = false , ZBRouterStatus =false , DoorSensorStatus=false , MotionSensorStatus=false , PowerControllerStatus=false , LockStatus=false ,CurtainControllerStatus=false,ServiceSwitchStatus=false , Switch1Status=false,Switch2Status=false ,Switch3Status=false , Switch4Status=false  ;
    private Button ShowServices , ShowAc , ShowLaundry , ShowMiniBar  ;
    private List<DeviceBean> zigbeeDevices ;
    static RESERVATION THERESERVATION ;
    static OrderDB order ;
    List<BTN> BtnsList =new ArrayList<BTN>();
    RecyclerView BTNS , LAUNDRYMENU , MINIBARMENU ;
    BTN_ADAPTER adapter ;
    static DisplayMetrics displayMetrics ;
    private List<FACILITY> Facilities = new ArrayList<FACILITY>();
    private List<LAUNDRY> Laundries = new ArrayList<LAUNDRY>();
    private List<MINIBAR> Minibar = new ArrayList<MINIBAR>();
    private List<RESTAURANT_UNIT> Restaurants = new ArrayList<RESTAURANT_UNIT>();
    private List<FACILITY> Gyms = new ArrayList<FACILITY>();
    ITuyaDeviceMultiControl iTuyaDeviceMultiControl ;
    static ROOM THEROOM  ;
    private static ImageView DNDImage , DNDIcon ,SOSImage , SOSIcon ;
    private static TextView DNDText , SOSText ;
    private CardView  RestaurantBtn, OpenDoor , ServicesBtn , RoomServiceBtn , LaundryBtn , CheckOutBtn , CleanUpBtn , ShowLighting ;
    LinearLayout homeBtn  ;
    static List<Activity> RestaurantActivities ;
    static ImageView laundryImage , laundryIcon ,dndImage,dndIcon , sosImage , sosIcon,cleanupImage ,cleanupIcon ,checkoutimage,checkouticon,roomserviceimage,roomserviceicon , restaurantIcon;
    static TextView laundryText ,dndText , sosText,cleanupText,text,roomservicetext;
    private LinearLayout laundryPriceList , minibarPriceList , lightsLayout  ;
    static Resources RESOURCES ;
    private Runnable backHomeThread ;
    static long x = 0 ;
    private Handler H ;
    private LinearLayout serviceLayout ;
    private ConstraintLayout   mainLayout ;
    static String LOGO ;
    static int checkInModeTime=0 , checkOutModeTime=0 ;
    static String registerDoorOpenUrl="https://ratco-solutions.com/HotelServicesTest/TestProject/p/insertDoorOpen.php";
    private int WelcomeLight = 0 ;
    private LinearLayoutManager Laundrymanager ;
    public static MediaPlayer mediaPlayer , lightPlayer;
    private static List<ServiceEmps> Emps ;
    private boolean LightingBind = false;


    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        setTuyaApplication() ;
        TTLockClient.getDefault().prepareBTService(getApplicationContext());
        BTN cleanup = new BTN(R.drawable.restaurant_background , "CLEANUP");
        BTN roomservice = new BTN(R.drawable.restaurant_background , "ROOM SERVICE");
        BTN dnd = new BTN(R.drawable.restaurant_background , "DND");
        BTN checkout = new BTN(R.drawable.restaurant_background , "CHECK OUT");
        BtnsList.add(cleanup);
        BtnsList.add(roomservice);
        BtnsList.add(dnd);
        BtnsList.add(checkout);
        BTNS = (RecyclerView) findViewById(R.id.btnsRecycler);
        final GridLayoutManager manager = new GridLayoutManager(this,4);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        BTNS.setLayoutManager(manager);
        setActivity();
        order = new OrderDB(act);
        order.removeOrder();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>()
                {
                    @Override
                    public void onComplete(@NonNull Task<String> task)
                    {
                        if (!task.isSuccessful())
                        {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.e("tokeneee" , token);
                        sendRegistrationToServer(token);
                    }
                });
        KeepScreenFull();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // put your code here...
        setButtons();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        setTabValue("0");
    }

    @Override
    public void onBackPressed()
    {

    }

    void setActivity()
    {
        act = this ;
        LogIn.ActList.add(act);
        //THEROOM = new ROOM(LogIn.THEROOM.id,LogIn.THEROOM.RoomNumber,LogIn.THEROOM.Hotel,LogIn.THEROOM.Building,LogIn.THEROOM.BuildingId,LogIn.THEROOM.Floor,LogIn.THEROOM.FloorId,LogIn.THEROOM.RoomType,LogIn.THEROOM.SuiteStatus,LogIn.THEROOM.SuiteNumber,LogIn.THEROOM.SuiteId,LogIn.THEROOM.ReservationNumber,LogIn.THEROOM.roomStatus,LogIn.THEROOM.Tablet,LogIn.THEROOM.dep,LogIn.THEROOM.Cleanup,LogIn.THEROOM.Laundry,LogIn.THEROOM.RoomService,LogIn.THEROOM.Checkout,LogIn.THEROOM.Restaurant,LogIn.THEROOM.SOS,LogIn.THEROOM.DND,LogIn.THEROOM.PowerSwitch,LogIn.THEROOM.DoorSensor,LogIn.THEROOM.MotionSensor,LogIn.THEROOM.Thermostat,LogIn.THEROOM.ZBGateway,LogIn.THEROOM.CurtainSwitch,LogIn.THEROOM.ServiceSwitch,LogIn.THEROOM.lock,LogIn.THEROOM.Switch1,LogIn.THEROOM.Switch2,LogIn.THEROOM.Switch3,LogIn.THEROOM.Switch4,LogIn.THEROOM.LockGateway,LogIn.THEROOM.LockName,LogIn.THEROOM.powerStatus,LogIn.THEROOM.curtainStatus,LogIn.THEROOM.doorStatus,LogIn.THEROOM.temp,LogIn.THEROOM.token);
        THEROOM = Tuya_Devices.THEROOM ;
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getLaundries();
        RestaurantActivities = new ArrayList<Activity>();
        Emps = new ArrayList<ServiceEmps>();
        //getRestaurants();
        //getGyms();
        //getMiniBar();
        RESOURCES = getResources();
        getHotelTempSetpoint();
        mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.click);
        lightPlayer = MediaPlayer.create(getBaseContext(), R.raw.light_click);
        RestaurantBtn = (CardView) findViewById(R.id.Restaurant);
        GymBtn = (Button) findViewById(R.id.button6);
        LaundryBtn = (CardView) findViewById(R.id.laundry_btn);
        OpenDoor = (CardView) findViewById(R.id.Door_Button);
        ServicesBtn = (CardView) findViewById(R.id.ServicesBtn_cardview);
        ServicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMainBtns();
            }
        });
        RoomServiceBtn = (CardView) findViewById(R.id.roomservice_btn);
        curtainBtn = (Button) findViewById(R.id.curtain);
        date = (TextView) findViewById(R.id.mainDate);
        time = (TextView) findViewById(R.id.mainTime);
        Light1 = (Button) findViewById(R.id.button15);
        Light2 = (Button) findViewById(R.id.button14);
        Light3 = (Button) findViewById(R.id.button17);
        DNDImage = (ImageView) findViewById(R.id.DND_Image);
        DNDIcon = (ImageView)findViewById(R.id.DND_Icon);
        SOSImage = (ImageView) findViewById(R.id.SOS_Image);
        SOSIcon = (ImageView) findViewById(R.id.SOS_Icon);
        SOSText = (TextView) findViewById(R.id.SOS_Text);
        DNDText = (TextView) findViewById(R.id.DND_Text);
        homeBtn = (LinearLayout)findViewById(R.id.home_Btn);
        homeBtn.setVisibility(View.GONE);
        laundryImage  = (ImageView) findViewById(R.id.imageView16);
        laundryIcon = (ImageView) findViewById(R.id.imageView10);
        laundryText= (TextView) findViewById(R.id.textView44);
        dndImage = (ImageView) findViewById(R.id.DND_Image);
        dndIcon = (ImageView) findViewById(R.id.DND_Icon);
        dndText = (TextView) findViewById(R.id.DND_Text);
        sosImage = (ImageView) findViewById(R.id.SOS_Image);
        sosText = (TextView) findViewById(R.id.SOS_Text);
        sosIcon = (ImageView) findViewById(R.id.SOS_Icon);
        roomserviceimage = (ImageView) findViewById(R.id.imageView8);
        roomserviceicon = (ImageView) findViewById(R.id.imageView7);
        roomservicetext = (TextView) findViewById(R.id.textView38);
        checkoutimage = (ImageView) findViewById(R.id.imageView11);
        text = (TextView) findViewById(R.id.textView42);
        checkouticon = (ImageView) findViewById(R.id.imageView20);
        cleanupImage = (ImageView) findViewById(R.id.imageView19);
        cleanupText = (TextView) findViewById(R.id.textView45);
        cleanupIcon = (ImageView) findViewById(R.id.imageView9);
        restaurantIcon = (ImageView) findViewById(R.id.imageView2);
        serviceLayout = (LinearLayout) findViewById(R.id.Service_Btns);
        serviceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x=0;
            }
        });
        lightsLayout = (LinearLayout) findViewById(R.id.lightingLayout);
        lightsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x=0;
            }
        });
        mainLayout = (ConstraintLayout) findViewById(R.id.main_layout);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x=0;
            }
        });
        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSystemUI();
                return false;
            }
        });
        LinearLayout Services = (LinearLayout) findViewById(R.id.ServicesLayout);
        LinearLayout Lighting = (LinearLayout) findViewById(R.id.lightingLayout);
        LinearLayout AcLayout = (LinearLayout) findViewById(R.id.ac_layout);
        LinearLayout LaundryLayout = (LinearLayout) findViewById(R.id.Laundry_layout);
        LinearLayout MinibarLayout = (LinearLayout) findViewById(R.id.Minibar_layout);
        LAUNDRYMENU = (RecyclerView) findViewById(R.id.laundryMenu_recycler);
        MINIBARMENU = (RecyclerView) findViewById(R.id.minibar_recycler);
        Laundrymanager = new LinearLayoutManager(act,RecyclerView.HORIZONTAL,false);
        final GridLayoutManager manager1 = new GridLayoutManager(this,4);
        manager1.setOrientation(LinearLayoutManager.VERTICAL);
        LAUNDRYMENU.setLayoutManager(Laundrymanager);
        MINIBARMENU.setLayoutManager(manager1);
        ShowLighting = (CardView)  findViewById(R.id.LightsBtn_cardview);
        ShowAc = (Button)  findViewById(R.id.hideShowAcLayout);
        ShowAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try
                {
                    TextView currentTempText = (TextView) findViewById(R.id.currentTemp);
                    int x = Integer.parseInt(Tuya_Devices.ACbean.dps.get("3").toString()) ;
                    double y = x*0.1 ;
                    currentTempText.setText(String.valueOf(y));
                    TextView clientTempText = (TextView) findViewById(R.id.clientTemp);
                    //ToastMaker.MakeToast(Tuya_Devices.ACbean.dps.get("2").toString(),act);
                    int xx = Integer.parseInt(Tuya_Devices.ACbean.dps.get("2").toString());
                    double yy = xx*0.1 ;
                    clientTempText.setText(String.valueOf(yy));
                    Button acback = (Button)findViewById(R.id.ac_back);
                    acback.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            AcLayout.setVisibility(View.GONE);
                            Lighting.setVisibility(View.GONE);
                            Services.setVisibility(View.VISIBLE);
                            LaundryLayout.setVisibility(View.GONE);
                            MinibarLayout.setVisibility(View.GONE);
                        }
                    });
                    AcLayout.setVisibility(View.VISIBLE);
                    Lighting.setVisibility(View.GONE);
                    Services.setVisibility(View.GONE);
                    LaundryLayout.setVisibility(View.GONE);
                    MinibarLayout.setVisibility(View.GONE);
                    Button onOf = (Button)findViewById(R.id.onOffBtn);
                    Button fanSpeed = (Button) findViewById(R.id.fanSpeedBtn);
                    Button tempUp = (Button) findViewById(R.id.tempUpBtn);
                    Button tempDown = (Button) findViewById(R.id.tempDownBtn);
                    onOf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            if (Tuya_Devices.ACbean.dps.get("1").toString().equals("true"))
                            {
                                try
                                {
                                    Tuya_Devices.AC.publishDps("{\" 1\": false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess()
                                        {
                                            //ToastMaker.MakeToast("AC Off " , act);
                                        }
                                    });
                                }
                                catch (Exception e)
                                {

                                }

                            }
                            else
                            {
                                try
                                {
                                    Tuya_Devices.AC.publishDps("{\" 1\": true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }
                                        @Override
                                        public void onSuccess()
                                        {
                                            //ToastMaker.MakeToast("AC On " , act);
                                        }
                                    });
                                }
                                catch (Exception e)
                                {

                                }

                            }

                        }
                    });
                    fanSpeed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            TextView fanSpeedText = (TextView) findViewById(R.id.fanSpeed);
                            Log.d("fan" , Tuya_Devices.ACbean.dps.toString());
                            if (Tuya_Devices.ACbean.dps.get("5").toString().equals("low"))
                            {
                                try
                                {
                                    Tuya_Devices.AC.publishDps("{\" 5\": \"med\"}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess()
                                        {
                                            //ToastMaker.MakeToast("fan middle" , act);
                                            fanSpeedText.setText("med");
                                        }
                                    });
                                }
                                catch (Exception e)
                                {

                                }

                            }
                            else if (Tuya_Devices.ACbean.dps.get("5").toString().equals("med"))
                            {
                                try
                                {
                                    Tuya_Devices.AC.publishDps("{\" 5\": \"high\"}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess()
                                        {
                                            //ToastMaker.MakeToast("fan high" , act);
                                            fanSpeedText.setText("high");
                                        }
                                    });
                                }
                                catch (Exception e)
                                {

                                }
                            }
                            else if (Tuya_Devices.ACbean.dps.get("5").toString().equals("high"))
                            {
                                try
                                {
                                    Tuya_Devices.AC.publishDps("{\" 5\": \"auto\"}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess()
                                        {
                                            //ToastMaker.MakeToast("fan auto" , act);
                                            fanSpeedText.setText("auto");
                                        }
                                    });
                                }
                                catch (Exception e)
                                {

                                }

                            }
                            else if (Tuya_Devices.ACbean.dps.get("5").toString().equals("auto"))
                            {
                                try
                                {
                                    Tuya_Devices.AC.publishDps("{\" 5\": \"low\"}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess()
                                        {
                                            //ToastMaker.MakeToast("fan low" , act);
                                            fanSpeedText.setText("low");
                                        }
                                    });
                                }
                                catch (Exception e)
                                {

                                }

                            }

                        }
                    });
                    tempUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            try
                            {
                                String current = Tuya_Devices.ACbean.dps.get("2").toString() ;
                                int x = Integer.parseInt(current);
                                int newt =  x+5 ;
                                Tuya_Devices.AC.publishDps("{\" 2\": " + String.valueOf(newt) + "}", new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess()
                                    {
                                        //ToastMaker.MakeToast("new temp "+String.valueOf(newt),act);
                                    }
                                });
                            }
                            catch (Exception e)
                            {

                            }
                        }
                    });
                    tempDown.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            try
                            {
                                String current = Tuya_Devices.ACbean.dps.get("2").toString() ;
                                int x = Integer.parseInt(current);
                                int newt =  x-5 ;
                                Tuya_Devices.AC.publishDps("{\" 2\": " + String.valueOf(newt) + "}", new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess()
                                    {
                                        //ToastMaker.MakeToast("new temp "+String.valueOf(newt),act);
                                    }
                                });
                            }
                            catch (Exception e )
                            {

                            }

                        }
                    });
                }
                catch (Exception e)
                {

                }

            }
        });
        minibarPriceList = (LinearLayout) findViewById(R.id.minibar_priceList);
        laundryPriceList = (LinearLayout)  findViewById(R.id.laundry_pricelist);
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
        ShowMiniBar = (Button)  findViewById(R.id.hideShowMinibarLayout);
        ShowMiniBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Button home = (Button)findViewById(R.id.minibar_homeBtn);
//                home.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        MinibarLayout.setVisibility(View.GONE);
//                        LaundryLayout.setVisibility(View.GONE);
//                        AcLayout.setVisibility(View.GONE);
//                        Lighting.setVisibility(View.GONE);
//                        Services.setVisibility(View.VISIBLE);
//                    }
//                });
                MinibarLayout.setVisibility(View.VISIBLE);
                LaundryLayout.setVisibility(View.GONE);
                AcLayout.setVisibility(View.GONE);
                Lighting.setVisibility(View.GONE);
                Services.setVisibility(View.GONE);
                if (Minibar.size()>0)
                {
                    getMiniBarMenu(Minibar.get(0).id);
                }
            }
        });
        database = FirebaseDatabase.getInstance("https://hotelservices-ebe66.firebaseio.com/");
        ServiceUsers = database.getReference(LogIn.room.getProjectName()+"ServiceUsers");
        getServiceUsersFromFirebase();
        Room = database.getReference(LogIn.room.getProjectName()+"/B"+LogIn.room.getBuilding()+"/F"+LogIn.room.getFloor()+"/R"+LogIn.room.getRoomNumber());
        myRefLaundry = Room.child("Laundry");//
        myRefCleanup = Room.child("Cleanup");//
        myRefRoomService = Room.child("RoomService");//
        myRefRoomServiceText = Room.child("RoomServiceText");
        myRefSos = Room.child("SOS");//
        myRefRestaurant = Room.child("Restaurant");//
        myRefCheckout = Room.child("Checkout");//
        myRefRoomStatus = Room.child("roomStatus");//
        myRefStatus = Room.child("Status");//
        myRefDND = Room.child("DND");//
        myRefDoorSensor = Room.child("DoorSensor");
        myRefMotionSensor = Room.child("MotionSensor");
        myRefThermostat = Room.child("Thermostat");
        myRefCurtainSwitch = Room.child("CurtainSwitch");
        myRefLock = Room.child("Lock");
        myRefSwitch1 = Room.child("Switch1");
        myRefSwitch2 = Room.child("Switch2");
        myRefSwitch3 = Room.child("Switch3");
        myRefSwitch4 = Room.child("Switch4");
        myRefDoor = Room.child("doorStatus");//
        myRefCurtain = Room.child("curtainStatus");//
        myRefPower = Room.child("powerStatus");//
        myRefTabStatus = Room.child("Tablet");//
        myRefReservation = Room.child("ReservationNumber");//
        myRefdep = Room.child("dep");//
        myRefTemp = Room.child("temp");//
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
        myRefLogo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null && !snapshot.getValue().toString().isEmpty() ){

                    LOGO = snapshot.getValue().toString();
                    ImageView logo = (ImageView) findViewById(R.id.HotelLogo);
                    Picasso.get().load(LOGO).resize(145,70).into(logo);
                    Log.d("Logochanged" , LOGO);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefCheckOutDuration.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getValue() != null ){
                    if (!snapshot.getValue().toString().equals("0"))
                    {
                        checkOutModeTime = Integer.parseInt( snapshot.getValue().toString());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefCheckInDuration.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null )
                {
                    if (snapshot.getValue().toString().equals("0"))
                    {
                        checkInModeTime = Integer.parseInt( snapshot.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefDoorWarning.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.getValue() != null )
                {
                    try {
                        theDoorTime = 1000 * 60 * Integer.parseInt(snapshot.getValue().toString());
                        Log.d("Doorinterval", theDoorTime + "");
                    } catch (Exception e) {

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefSetpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                try
                {
                    if (Integer.parseInt(snapshot.getValue().toString()) > 15 )
                    {
                        String temp = snapshot.getValue().toString() ;
                        if (temp.length()==2)
                        {
                            temp = temp+"0";
                            TempSetPoint = temp ;
                        }
                        else if (temp.length()>2)
                        {
                            TempSetPoint = temp ;
                        }
                        //TempSetPoint[finalI] = snapshot.getValue().toString();
                    }

                }
                catch (Exception e)
                {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefSetpointInterval.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.getValue() != null)
                {
                    try {
                        theTime = 1000 * 60 * Integer.parseInt(snapshot.getValue().toString());
                        Log.d("intervalsetpoint", theTime + "");
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefDND.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (Long.parseLong(snapshot.getValue().toString()) > 0 )
                {
                    //dnd.setBackgroundResource(R.drawable.dnd_on0);
                    dndOn();
                    DNDStatus = true ;
                }
                else
                {
                    dndOff();
                    DNDStatus = false ;
                    if (THEROOM.getSERVICE_B() != null ){
                        if (THEROOM.getSERVICE_B().dps.get("1") != null ){
                            if (THEROOM.getSERVICE_B().dps.get("1").toString().equals("true")){
                                THEROOM.getSERVICE().publishDps("{\"1\":false}", new IResultCallback() {
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
                    /*
                    String dep = "DND" ;
                    //LoadingDialog loading = new LoadingDialog(act);
                    StringRequest rrr = new StringRequest(Request.Method.POST, removeServiceOrderUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(THEROOM.getSERVICE_B() != null ){
                                if (THEROOM.getSERVICE_B().dps.get("1").toString().equals("true")){
                                    THEROOM.getSERVICE().publishDps("{\"1\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }else {Log.d("serviceSwitch" , "is null");}
                            }
                            if (response.equals("1")) {
                                //loading.stop();
                                DNDStatus = false;
                                myRefDND.setValue(0);
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("id", String.valueOf(dndId));
                            params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
                            params.put("dep", dep);
                            params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
                            return params;
                        }
                    };
                    Volley.newRequestQueue(act).add(rrr);*/
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
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (Long.parseLong(snapshot.getValue().toString()) > 0 )
                {
                    //sos.setBackgroundResource(R.drawable.sos_icon1);
                    SosStatus = true ;
                    sosOn();
                }
                else
                {
                    //sos.setBackgroundResource(R.drawable.sos_icon0);
                    SosStatus = false ;
                    sosOff();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefLaundry.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (Long.parseLong(snapshot.getValue().toString()) > 0 )
                {
                    LaundryStatus = true ;
                    laundryOn();
                }
                else
                {
                    LaundryStatus = false ;
                    laundryOff();
                    if (THEROOM.getSERVICE_B() != null ){
                        if (THEROOM.getSERVICE_B().dps.get("3") != null ){
                            if (THEROOM.getSERVICE_B().dps.get("3").toString().equals("true")){
                                THEROOM.getSERVICE().publishDps("{\"3\":false}", new IResultCallback() {
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
                    /*
                    final String dep = "Laundry";
                    StringRequest removOrder = new StringRequest(Request.Method.POST, removeServiceOrderUrl , new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {
                            //loading.stop();
                            if (response.equals("1")  )
                            {
                                if (THEROOM.getSERVICE_B() != null){
                                    if (THEROOM.getSERVICE_B().dps.get("3").toString().equals("true"))
                                    {
                                        THEROOM.getSERVICE().publishDps("{\"3\":false}", new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {

                                            }

                                            @Override
                                            public void onSuccess() {

                                            }
                                        });
                                    }

                                }
                                LaundryStatus = false ;
                                myRefLaundry.setValue(0);
                                laundryOff();
                                //ToastMaker.MakeToast(dep+" Order Cancelled" , act);
                            }
                            else
                            {
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            //loading.stop();
                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError
                        {
                            Map<String,String> params = new HashMap<String, String>();
                            params.put("id" , String.valueOf( laundryOrderId));
                            params.put("room" , String.valueOf( LogIn.room.getRoomNumber()));
                            params.put("dep" , dep);
                            params.put("Hotel" , String.valueOf( LogIn.room.getHotel()));
                            return params;
                        }
                    };
                    Volley.newRequestQueue(act).add(removOrder);*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefCleanup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (Long.parseLong(snapshot.getValue().toString())>0)
                {
                    cleanupOn();
                    CleanupStatus = true ;

                }
                else
                {
                    cleanupOff();
                    CleanupStatus = false ;
                    if (THEROOM.getSERVICE_B() != null ){
                        if (THEROOM.getSERVICE_B().dps.get("2") != null ){
                            if (THEROOM.getSERVICE_B().dps.get("2").toString().equals("true")){
                                THEROOM.getSERVICE().publishDps("{\"2\":false}", new IResultCallback() {
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
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefCheckout.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (Long.parseLong(snapshot.getValue().toString()) > 0 )
                {
                    CheckoutStatus = true ;
                    checkoutOn();
                }
                else
                {
                    checkoutOff();
                    CheckoutStatus = false ;
                    if (THEROOM.getSERVICE_B() != null ){
                        if (THEROOM.getSERVICE_B().dps.get("4") != null ){
                            if (THEROOM.getSERVICE_B().dps.get("4").toString().equals("true")){
                                THEROOM.getSERVICE().publishDps("{\"4\": false}", new IResultCallback() {
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
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefRoomService.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if ( !snapshot.getValue().toString().equals("0") )
                {
                    roomServiceOn();
                    RoomServiceStatus = true ;
                }
                else
                {
                    RoomServiceStatus = false ;
                    roomServiceOff();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefRestaurant.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (Integer.parseInt(snapshot.getValue().toString()) > 0 )
                {
                    RestaurantStatus = true ;
                    restaurantOn();
                }
                else
                {
                    restaurantOff();
                    RestaurantStatus = false ;
                    //ImageView i = (ImageView) findViewById(R.id.imageView2);
                    //i.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
        myRefRorS.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (Integer.parseInt(snapshot.getValue().toString()) == 1 )
                {
                    RoomOrSuite = 1 ;
                }
                else if (Integer.parseInt(snapshot.getValue().toString()) == 2)
                {
                    RoomOrSuite = 2 ;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefReservation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (Integer.parseInt(snapshot.getValue().toString()) > 0 )
                {
                    RESERVATION = Integer.parseInt(snapshot.getValue().toString()) ;
                }
                else
                    {
                        RESERVATION = 0 ;
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot != null)
                {
                    ID = Integer.parseInt(snapshot.getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefRoomStatus.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (CURRENT_ROOM_STATUS == 1 || CURRENT_ROOM_STATUS == 0)
                {
                    if (snapshot.getValue().toString().equals("2"))
                    {
                        getReservation();
                        //CheckIn();
                    }
                }
                else if (CURRENT_ROOM_STATUS == 2)
                {
                    order.cleanOldOrders();
                }

                CURRENT_ROOM_STATUS = Integer.parseInt( snapshot.getValue().toString() );
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefRoomStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {

                    if (snapshot.getValue().toString().equals("2"))
                    {
                        TextView text = (TextView) findViewById(R.id.textView36);
                        text.setText("Welcome Mr. ");
                        getReservation();
                        //CheckIn();
                    }
                    else if (snapshot.getValue().toString().equals("1"))
                    {
                        TextView fname = (TextView) findViewById(R.id.client_Name);
                        TextView text = (TextView) findViewById(R.id.textView36);
                        text.setText("");
                        fname.setText("Room Is Vacant");
                        WelcomeLight = 0 ;
                    }
                    else if (snapshot.getValue().toString().equals("3"))
                    {
                        TextView fname = (TextView) findViewById(R.id.client_Name);
                        TextView text = (TextView) findViewById(R.id.textView36);
                        text.setText("");
                        fname.setText("Room Need Cleanup");
                        checkOut();
                    }
                    else if (snapshot.getValue().toString().equals("4"))
                    {
                        TextView fname = (TextView) findViewById(R.id.client_Name);
                        TextView text = (TextView) findViewById(R.id.textView36);
                        text.setText("");
                        fname.setText("Room Out Of Service");
                    }
                CURRENT_ROOM_STATUS = Integer.parseInt( snapshot.getValue().toString() );
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final TextView RoomNumber = (TextView) findViewById(R.id.RoomNumber_MainScreen);
        RoomNumber.setText(String.valueOf(LogIn.room.getRoomNumber()));
        Calendar xx = Calendar.getInstance(Locale.getDefault());
        String currentDate =String.valueOf( xx.get(Calendar.DAY_OF_MONTH))+ "/" + xx.get(Calendar.MONTH)+"/" + xx.get(Calendar.YEAR) + "  ";
        String currentTime = String.valueOf(xx.get(Calendar.HOUR_OF_DAY))+":"+xx.get(Calendar.MINUTE);
        date.setText(currentDate);
        time.setText(currentTime);
        ImageView roomnumber = (ImageView) findViewById(R.id.HotelLogo);
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
                        StringRequest re = new StringRequest(Request.Method.POST, LogInUrl, new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response)
                            {
                                Log.d("LoginResult" , response +" "+ LogInUrl );
                                loading.stop();
                                if (response.equals("1"))
                                {
                                    logout();
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


//                final Dialog d = new Dialog(act);
//                d.setContentView(R.layout.small_confermation_dialog);
//                Button ok = (Button) d.findViewById(R.id.messageDialog_ok);
//                Button can = (Button) d.findViewById(R.id.confermationDialog_cancel);
//                can.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        d.dismiss();
//                    }
//                });
//                ok.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        logout();
//                    }
//                });
//                d.show();
                return false;
            }

        });
        setButtons();
        if (THEROOM.getWiredZBGateway() == null )
        {
            ZBRouterStatus = false ;
            ShowLighting.setVisibility(View.GONE);
            curtainBtn.setVisibility(View.GONE);
            THEROOM.setWiredZBGateway(null);
        }
        else
        {
            //THEROOM.setWiredZBGateway(Tuya_Devices.mgate);
            final boolean[] x = {false};
            final boolean[] D = {false};
            final Handler[] timerHandler = {new Handler()};
            final Handler[] timerDoorHandler = {new Handler()};
            TempRonnable = new Runnable() {

                @Override
                public void run()
                {
                    long millis = System.currentTimeMillis() - thermostatStartTime ;
                    int seconds = (int) (millis / 1000);
                    int minutes = seconds / 60;
                    seconds = seconds % 60;
                    timerHandler[0].postDelayed(this, 1000);
                    theThermoPeriod = System.currentTimeMillis() - thermostatStartTime ;
                    Log.d("theSTATUS" , String.valueOf(x[0])+" "+ClientTemp+" " +minutes+":"+seconds);
                    if ( theThermoPeriod >=  theTime  && x[0])
                    {
                        if (Tuya_Devices.AC != null)
                        {
                            Tuya_Devices.AC.publishDps("{\" 2\": "+TempSetPoint+"}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess()
                                {
                                    //ToastMaker.MakeToast("Temp Set to Setpoint " , act);
                                    x[0] = false ;
                                }
                            });
                        }

                        timerHandler[0].removeCallbacks(TempRonnable);
                    }
                    else if (theThermoPeriod >=  theTime  && !x[0])
                    {
                        timerHandler[0].removeCallbacks(TempRonnable);
                    }


                }
            };
            DoorRunnable = new Runnable() {
                @Override
                public void run()
                {
                    long millis = System.currentTimeMillis() - theDoorStartTime ;
                    int seconds = (int) (millis / 1000);
                    int minutes = seconds / 60;
                    seconds = seconds % 60;
                    timerDoorHandler[0].postDelayed(this,1000) ;
                    theDoorPeriod = System.currentTimeMillis() - theDoorStartTime ;
                    Log.d("theSTATUSDOOR" , String.valueOf(D[0])+" " +minutes+":"+seconds);
                    if ( theDoorPeriod >=  theDoorTime  && D[0])
                    {
                        myRefDoor.setValue(2);
                        timerDoorHandler[0].removeCallbacks(DoorRunnable);
                    }
                    else if (theDoorPeriod >=  theDoorTime  && !D[0])
                    {
                        timerDoorHandler[0].removeCallbacks(DoorRunnable);
                    }
                }
            };
            ZBRouterStatus = true ;
            THEROOM.getWiredZBGateway().getSubDevList(new ITuyaDataCallback<List<DeviceBean>>() {
                @Override
                public void onSuccess(List<DeviceBean> result)
                {
                    zigbeeDevices = result ;
                    try
                    {
                        if (zigbeeDevices != null && zigbeeDevices.size()>0)
                        {
                            for (int i=0;i<zigbeeDevices.size();i++)
                            {
                                if (zigbeeDevices.get(i).getName().equals(LogIn.room.getRoomNumber()+"DoorSensor"))
                                {
                                    DoorSensorStatus = true ;
                                    DoorSensorBean = new DeviceBean();
                                    DoorSensorBean = zigbeeDevices.get(i) ;
                                    THEROOM.setDOORSENSOR_B(zigbeeDevices.get(i));
                                    DoorSensor = TuyaHomeSdk.newDeviceInstance(DoorSensorBean.getDevId());
                                    THEROOM.setDOORSENSOR(TuyaHomeSdk.newDeviceInstance(DoorSensorBean.getDevId()));
                                    DoorSensor.registerDeviceListener(new IDeviceListener() {
                                        @Override
                                        public void onDpUpdate(String devId, Map<String, Object> dpStr)
                                        {
                                            //Log.d("DoorS" , dpStr.toString() + " "+ THEROOM.getPOWER_B().dps.get("2").toString()+" "+CURRENT_ROOM_STATUS);
                                            if (dpStr.get("doorcontact_state") != null )
                                            {

                                                if (dpStr.get("doorcontact_state").toString().equals("true") )
                                                {
                                                    ToastMaker.MakeToast("Door is Open" , act);
                                                    setDoorOpenClosed("1");
                                                    myRefDoor.setValue("1");
                                                    thermostatStartTime = System.currentTimeMillis() ;
                                                    theDoorStartTime = System.currentTimeMillis() ;
                                                    x[0] = true ;
                                                    D[0] = true ;
                                                    theThermoPeriod = 0 ;
                                                    theDoorPeriod = 0 ;
                                                    TempRonnable.run();
                                                    DoorRunnable.run();
                                                    if (THEROOM.getPOWER_B() != null )
                                                    {
                                                        if (THEROOM.getPOWER_B().dps.get("2").toString().equals("false") && CURRENT_ROOM_STATUS == 2 & WelcomeLight == 0)
                                                        {
                                                            String g = "20";
                                                            WelcomeLight = 1 ;
                                                            THEROOM.getPOWER().publishDps("{\"1\": true}", new IResultCallback() {
                                                                @Override
                                                                public void onError(String code, String error) {
                                                                    //Toast.makeText(act, error, Toast.LENGTH_SHORT).show();
                                                                    Log.e("light", error);
                                                                }

                                                                @Override
                                                                public void onSuccess() {
                                                                    //Toast.makeText(act, "turn on the light success", Toast.LENGTH_SHORT).show();
                                                                    //myRefPower.setValue(1);
                                                                }
                                                            });
                                                            THEROOM.getPOWER().publishDps("{\"2\": true}", new IResultCallback() {
                                                                @Override
                                                                public void onError(String code, String error) {
                                                                    //Toast.makeText(act, "turn on the light failure", Toast.LENGTH_SHORT).show();
                                                                }

                                                                @Override
                                                                public void onSuccess() {
                                                                    //Toast.makeText(act, "turn on the light success", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                            THEROOM.getPOWER().publishDps("{\"8\": "+g+"}", new IResultCallback() {
                                                                @Override
                                                                public void onError(String code, String error) {
                                                                    //Toast.makeText(act, "turn on the light failure", Toast.LENGTH_SHORT).show();
                                                                }

                                                                @Override
                                                                public void onSuccess() {
                                                                    //Toast.makeText(act, "turn on the light success", Toast.LENGTH_SHORT).show();
                                                                    Log.d("LightWithWelcome" , "countdoun from door sent");
                                                                }
                                                            });
                                                            Log.d("LightWithWelcome" , THEROOM.getPOWER_B().dps.toString());
                                                            if (Switch1 != null ){
                                                                Thread t = new Thread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        try {
                                                                            Thread.sleep(20*1000);
                                                                            Switch1.publishDps("{\"1\": false}", new IResultCallback() {
                                                                                @Override
                                                                                public void onError(String code, String error) {
                                                                                    Log.d("LightWithWelcome" , error);
                                                                                }

                                                                                @Override
                                                                                public void onSuccess() {
                                                                                    Log.d("LightWithWelcome" , "Light is on ");
                                                                                }
                                                                            });
                                                                        } catch (InterruptedException e) {
                                                                            e.printStackTrace();
                                                                        }

                                                                    }
                                                                });
                                                                Switch1.publishDps("{\"1\": true}", new IResultCallback() {
                                                                    @Override
                                                                    public void onError(String code, String error) {
                                                                        Log.d("LightWithWelcome" , error);
                                                                    }

                                                                    @Override
                                                                    public void onSuccess() {
                                                                        Log.d("LightWithWelcome" , "Light is on ");
                                                                        t.start();
                                                                    }
                                                                });


                                                            }
                                                        }
                                                    }
                                                }
                                                else
                                                {
                                                    setDoorOpenClosed("0");
                                                    myRefDoor.setValue("0");
                                                    D[0] = false ;
                                                    ToastMaker.MakeToast("Door Closed" , act);
                                                }
                                            }

                                        }
                                        @Override
                                        public void onRemoved(String devId) {

                                        }

                                        @Override
                                        public void onStatusChanged(String devId, boolean online)
                                        {
                                            Log.d("DoorS" , String.valueOf( online));
                                        }
                                        @Override
                                        public void onNetworkStatusChanged(String devId, boolean status)
                                        {

                                        }
                                        @Override
                                        public void onDevInfoUpdate(String devId)
                                        {
                                            Log.d("DoorS" , devId );
                                        }
                                    });
                                }
                                else if (zigbeeDevices.get(i).getName().equals(LogIn.room.getRoomNumber()+"MotionSensor"))
                                {
                                    MotionSensorStatus = true ;
                                    MotionSensorBean = new DeviceBean();
                                    MotionSensorBean = zigbeeDevices.get(i) ;
                                    THEROOM.setMOTIONSENSOR_B(zigbeeDevices.get(i));
                                    MotionSensor = TuyaHomeSdk.newDeviceInstance(MotionSensorBean.getDevId());
                                    THEROOM.setMOTIONSENSOR(TuyaHomeSdk.newDeviceInstance(MotionSensorBean.getDevId()));
                                    MotionSensor.publishDps("{\" 10\": \"30s\"}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess()
                                        {
                                            //ToastMaker.MakeToast("MotionSensor Time Updated" , act);
                                            Log.d("MotionData",MotionSensorBean.dps.toString()+" "+MotionSensorBean.getDpCodes().toString());
                                        }
                                    });
                                    MotionSensor.registerDeviceListener(new IDeviceListener() {
                                        @Override
                                        public void onDpUpdate(String devId, Map<String, Object> dpStr)
                                        {
                                            Log.d("Motion" , dpStr.toString() ) ;

                                            if (x[0])
                                            {
                                                x[0] = false ;
                                            }
                                            else
                                            {
                                                String t ="";
                                                if (ClientTemp.equals("0"))
                                                {
                                                    t="240";
                                                }
                                                else
                                                {
                                                    t = ClientTemp ;
                                                }
                                                String dp = "{\" 2\": "+t+"}";

                                                Tuya_Devices.AC.publishDps(dp, new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }

                                                    @Override
                                                    public void onSuccess()
                                                    {
                                                        //ToastMaker.MakeToast("Temp Set to Client Temp " , act);

                                                    }
                                                });
                                            }


                                        }
                                        @Override
                                        public void onRemoved(String devId)
                                        {

                                        }
                                        @Override
                                        public void onStatusChanged(String devId, boolean online)
                                        {
                                            Log.d("Motion" ,String.valueOf( online) ) ;
                                        }
                                        @Override
                                        public void onNetworkStatusChanged(String devId, boolean status)
                                        {
                                            Log.d("Motion" , String.valueOf( status) ) ;
                                        }
                                        @Override
                                        public void onDevInfoUpdate(String devId) {

                                        }
                                    });
                                }
                                else if (zigbeeDevices.get(i).getName().equals(LogIn.room.getRoomNumber()+"Curtain"))
                                {
                                    CurtainControllerStatus = true ;
                                    CurtainBean = new DeviceBean();
                                    CurtainBean = zigbeeDevices.get(i) ;
                                    THEROOM.setCURTAIN_B(zigbeeDevices.get(i));
                                    Curtain = TuyaHomeSdk.newDeviceInstance(CurtainBean.getDevId());
                                    THEROOM.setCURTAIN(TuyaHomeSdk.newDeviceInstance(CurtainBean.getDevId()));
                                }
                                else if (zigbeeDevices.get(i).getName().equals(LogIn.room.getRoomNumber()+"ServiceSwitch"))
                                {
                                    ServiceSwitchStatus = true ;
                                    ServiceSwitch = new DeviceBean();
                                    ServiceSwitch = zigbeeDevices.get(i) ;
                                    THEROOM.setSERVICE_B(zigbeeDevices.get(i));
                                    ServiceS = TuyaHomeSdk.newDeviceInstance(ServiceSwitch.getDevId());
                                    THEROOM.setSERVICE(TuyaHomeSdk.newDeviceInstance(ServiceSwitch.getDevId()));
                                    THEROOM.getSERVICE().registerDeviceListener(new IDeviceListener() {
                                        @Override
                                        public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                                            Log.d("serviceSwitch" , dpStr.toString());
                                            if (dpStr.get("switch_1") != null ){
                                                if (!DNDStatus && dpStr.get("switch_1").toString().equals("true") ){
                                                    String dep = "DND";
                                                    Calendar x = Calendar.getInstance(Locale.getDefault());
                                                    long timee =  x.getTimeInMillis();
                                                    LoadingDialog loading = new LoadingDialog(act);
                                                    DNDStatus = true;
                                                    StringRequest request = new StringRequest(Request.Method.POST, insertServiceOrderUrl, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            if (THEROOM.getSERVICE_B() != null ){
                                                                Log.d("serviceSwitch" , "not null");
                                                                Log.d("serviceSwitch" , THEROOM.getSERVICE_B().dps.toString());

                                                                if (THEROOM.getSERVICE_B().dps.get("1").toString().equals("false")){

                                                                    THEROOM.getSERVICE().publishDps("{\"1\":true}", new IResultCallback() {
                                                                        @Override
                                                                        public void onError(String code, String error) {
                                                                            Log.d("serviceSwitch" , error);
                                                                        }

                                                                        @Override
                                                                        public void onSuccess() {
                                                                            Log.d("serviceSwitch" , "success");
                                                                        }
                                                                    });
                                                                }else {Log.d("serviceSwitch" , "is null");}

                                                            }
                                                            if(CleanupStatus){
                                                                removeCleanupOrderInDataBase();
                                                            }
                                                            if (LaundryStatus){
                                                                removeLaundryOrderInDataBase();
                                                            }
                                                            try {
                                                                Log.e("DND", response);
                                                                if (Integer.parseInt(response) > 0) {
                                                                    loading.stop();
                                                                    dndId = Integer.parseInt(response);
                                                                    myRefDND.setValue(dndId);
                                                                    myRefdep.setValue("DND");
                                                                    dndOn();
                                                                }
                                                            } catch (Exception e) {
                                                                Log.e("DND", e.getMessage());
                                                            }

                                                        }
                                                    }
                                                            , new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            Log.e("DNDerror", error.getMessage());
                                                        }
                                                    }) {
                                                        @Override
                                                        protected Map<String, String> getParams() throws AuthFailureError {
                                                            Map<String, String> params = new HashMap<String, String>();
                                                            params.put("roomNumber", String.valueOf(LogIn.room.getRoomNumber()));
                                                            params.put("time", String.valueOf(timee));
                                                            params.put("dep", dep);
                                                            params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
                                                            params.put("RorS", String.valueOf(RoomOrSuite));
                                                            params.put("Reservation", String.valueOf(RESERVATION));
                                                            return params;
                                                        }
                                                    };
                                                    Volley.newRequestQueue(act).add(request);
                                                }
                                                else if (DNDStatus && dpStr.get("switch_1").toString().equals("false")){
                                                    String dep = "DND";
                                                    LoadingDialog loading = new LoadingDialog(act);
                                                    DNDStatus = false;
                                                    StringRequest rrr = new StringRequest(Request.Method.POST, removeServiceOrderUrl, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            if(THEROOM.getSERVICE_B() != null ){
                                                                if (THEROOM.getSERVICE_B().dps.get("1").toString().equals("true")){
                                                                    THEROOM.getSERVICE().publishDps("{\"1\":false}", new IResultCallback() {
                                                                        @Override
                                                                        public void onError(String code, String error) {

                                                                        }

                                                                        @Override
                                                                        public void onSuccess() {

                                                                        }
                                                                    });
                                                                }else {Log.d("serviceSwitch" , "is null");}
                                                            }
                                                            if (response.equals("1")) {
                                                                loading.stop();
                                                                myRefDND.setValue(0);
                                                                dndOff();
                                                            }

                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {

                                                        }
                                                    }) {
                                                        @Override
                                                        protected Map<String, String> getParams() throws AuthFailureError {
                                                            Map<String, String> params = new HashMap<String, String>();
                                                            params.put("id", String.valueOf(dndId));
                                                            params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
                                                            params.put("dep", dep);
                                                            params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
                                                            return params;
                                                        }
                                                    };
                                                    Volley.newRequestQueue(act).add(rrr);
                                                }
                                            }
                                            if (dpStr.get("switch_2") != null ){
                                                if (!CleanupStatus && dpStr.get("switch_2").toString().equals("true")){
                                                    addCleanupOrderInDataBase();
                                                }
                                                else if (CleanupStatus && dpStr.get("switch_2").toString().equals("false")){
                                                    removeCleanupOrderInDataBase();
                                                }
                                            }
                                            if (dpStr.get("switch_3") != null ){
                                                if (!LaundryStatus && dpStr.get("switch_3").toString().equals("true")){
                                                    LoadingDialog loading = new LoadingDialog(act);
                                                    final String dep = "Laundry";
                                                    Calendar x = Calendar.getInstance(Locale.getDefault());
                                                    long timee =  x.getTimeInMillis();
                                                    LaundryStatus = true ;
                                                    StringRequest addOrder = new StringRequest(Request.Method.POST, insertServiceOrderUrl , new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response)
                                                        {
                                                            loading.stop();
                                                            if (Integer.parseInt(response) > 0 )
                                                            {
                                                                ToastMaker.MakeToast( dep + " Order Sent Successfully" , act);
                                                                laundryOrderId = Integer.parseInt(response);
                                                                myRefLaundry.setValue(laundryOrderId);
                                                                myRefdep.setValue(dep);
                                                                myRefDND.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                                                    {
                                                                        if (Long.parseLong(snapshot.getValue().toString()) > 0)
                                                                        {
                                                                            myRefDND.setValue(0);
                                                                        }

                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                                laundryOn();
                                                            }
                                                            else
                                                            {
                                                                Toast.makeText(act , response,Toast.LENGTH_LONG).show();
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
                                                            params.put("roomNumber" ,String.valueOf(LogIn.room.getRoomNumber()));
                                                            params.put("time" ,String.valueOf(timee));
                                                            params.put("dep" ,dep);
                                                            params.put("Hotel" ,String.valueOf( LogIn.room.getHotel()));
                                                            params.put("RorS" ,String.valueOf( RoomOrSuite));
                                                            params.put("Reservation" ,String.valueOf( RESERVATION));
                                                            return params;
                                                        }

                                                    };
                                                    Volley.newRequestQueue(act).add(addOrder);
                                                }
                                                else if (LaundryStatus && dpStr.get("switch_3").toString().equals("false")){
                                                    LoadingDialog loading = new LoadingDialog(act);
                                                    myRefLaundry.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot)
                                                        {
                                                            if (Long.parseLong(snapshot.getValue().toString()) > 0 )
                                                            {
                                                                laundryOrderId = Long.parseLong(snapshot.getValue().toString()) ;
                                                            }
                                                        }
                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                    final String dep = "Laundry";
                                                    LaundryStatus = false ;
                                                    StringRequest removOrder = new StringRequest(Request.Method.POST, removeServiceOrderUrl , new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response)
                                                        {
                                                            loading.stop();
                                                            if (response.equals("1")  )
                                                            {
                                                                myRefLaundry.setValue(0);
                                                                laundryOff();
                                                                ToastMaker.MakeToast(dep+" Order Cancelled" , act);
                                                            }
                                                            else
                                                            {
                                                                //Toast.makeText(act , response,Toast.LENGTH_LONG).show();
                                                            }

                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error)
                                                        {
                                                            loading.stop();
                                                            // Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
                                                        }
                                                    })
                                                    {
                                                        @Override
                                                        protected Map<String, String> getParams() throws AuthFailureError
                                                        {
                                                            Map<String,String> params = new HashMap<String, String>();
                                                            params.put("id" , String.valueOf( laundryOrderId));
                                                            params.put("room" , String.valueOf( LogIn.room.getRoomNumber()));
                                                            params.put("dep" , dep);
                                                            params.put("Hotel" , String.valueOf( LogIn.room.getHotel()));
                                                            return params;
                                                        }
                                                    };
                                                    Volley.newRequestQueue(act).add(removOrder);
                                                }
                                            }
                                            if (dpStr.get("switch_4") != null ){
                                                if (!CheckoutStatus && dpStr.get("switch_4").toString().equals("true")){
                                                    LoadingDialog loading = new LoadingDialog(act);
                                                    final String dep = "Checkout";
                                                    Calendar x = Calendar.getInstance(Locale.getDefault());
                                                    long timee = x.getTimeInMillis();
                                                    CheckoutStatus = true;
                                                    final StringRequest request = new StringRequest(Request.Method.POST, insertServiceOrderUrl, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response)
                                                        {
                                                            loading.stop();
                                                            if (response.equals("0"))
                                                            {

                                                            }
                                                            else
                                                            {
                                                                ToastMaker.MakeToast(dep+ " Order Sent Successfully" , act);
                                                                checkOutId = Integer.parseInt(response);
                                                                myRefCheckout.setValue(checkOutId);
                                                                myRefdep.setValue(dep);
                                                                myRefDND.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        if (Long.parseLong(snapshot.getValue().toString()) > 0) {
                                                                            myRefDND.setValue(0);
                                                                        }

                                                                    }
                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                                checkoutOn();

                                                                Dialog RatingDialog = new Dialog(act);
                                                                RatingDialog.setContentView(R.layout.rating_dialog);
                                                                RatingDialog.setCancelable(false);
                                                                Button sendRating = (Button) RatingDialog.findViewById(R.id.sendRatingButton);
                                                                RatingBar RatingD = (RatingBar) RatingDialog.findViewById(R.id.ratingBar);
                                                                final String[] RATING = {""};
                                                                RatingD.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                                                    @Override
                                                                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
                                                                    {
                                                                        //ToastMaker.MakeToast(String.valueOf(rating),act);
                                                                        RATING[0] = String.valueOf(rating) ;
                                                                    }
                                                                });
                                                                sendRating.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v)
                                                                    {
                                                                        RatingDialog.dismiss();
                                                                        LoadingDialog loading = new LoadingDialog(act);
                                                                        //ToastMaker.MakeToast(RATING[0],act);
                                                                        String url = LogIn.URL+"insertRating.php";
                                                                        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
                                                                        {
                                                                            @Override
                                                                            public void onResponse(String response)
                                                                            {
                                                                                loading.stop();

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
                                                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                                                Map<String,String> Params = new HashMap<String, String>();
                                                                                Params.put("Reservation" , String.valueOf( RESERVATION));
                                                                                Params.put("Rating" , RATING[0]);
                                                                                return Params;
                                                                            }
                                                                        };
                                                                        Volley.newRequestQueue(act).add(request);
                                                                    }
                                                                });
                                                                RatingDialog.show();
                                                            }

                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error)
                                                        {
                                                            loading.stop();
                                                        }
                                                    }) {
                                                        @Override
                                                        protected Map<String, String> getParams() throws AuthFailureError {
                                                            Map<String, String> params = new HashMap<String, String>();
                                                            params.put("roomNumber", String.valueOf(LogIn.room.getRoomNumber()));
                                                            params.put("time", String.valueOf(timee));
                                                            params.put("dep", dep);
                                                            params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
                                                            params.put("RorS", String.valueOf(RoomOrSuite));
                                                            params.put("Reservation", String.valueOf(RESERVATION));
                                                            return params;
                                                        }
                                                    };
                                                    Volley.newRequestQueue(act).add(request);
                                                }
                                                else if (CheckoutStatus && dpStr.get("switch_4").toString().equals("false")){
                                                    Log.d("checkoutProblem" , " from here " + CheckoutStatus+dpStr.get("switch_4").toString() ) ;
                                                    final String dep = "Checkout";
                                                    LoadingDialog loading = new LoadingDialog(act);
                                                    CheckoutStatus = false;
                                                    StringRequest re = new StringRequest(Request.Method.POST, removeServiceOrderUrl, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            loading.stop();
                                                            checkoutOff();
                                                            myRefCheckout.setValue(0);
                                                            ToastMaker.MakeToast( dep + " Order Cancelled" , act);
                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            //d.dismiss();
                                                            loading.stop();
                                                        }
                                                    }) {
                                                        @Override
                                                        protected Map<String, String> getParams() throws AuthFailureError {
                                                            Map<String, String> params = new HashMap<String, String>();
                                                            params.put("id", String.valueOf(checkOutId));
                                                            params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
                                                            params.put("dep", dep);
                                                            params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
                                                            return params;
                                                        }
                                                    };
                                                    Volley.newRequestQueue(act).add(re);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onRemoved(String devId) {

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
                                else if (zigbeeDevices.get(i).getName().equals(LogIn.room.getRoomNumber()+"Switch1"))
                                {
                                    Switch1Status = true ;
                                    Switch1Bean = new DeviceBean() ;
                                    Switch1Bean = zigbeeDevices.get(i) ;
                                    THEROOM.setSWITCH1_B(zigbeeDevices.get(i));
                                    Log.d("switch1" , Switch1Bean.dps.toString());
                                    Switch1 = TuyaHomeSdk.newDeviceInstance(Switch1Bean.getDevId());
                                    THEROOM.setSWITCH1(TuyaHomeSdk.newDeviceInstance(Switch1Bean.getDevId()));
                                    Switch1.registerDeviceListener(new IDeviceListener() {
                                        @Override
                                        public void onDpUpdate(String devId, Map<String, Object> dpStr)
                                        {
                                            Log.d("switch1Update" , dpStr.toString());
                                            if (dpStr.get("switch_1") != null ) {
                                                if (dpStr.get("switch_1").toString().equals("true")){
                                                    Button b1 = (Button)findViewById(R.id.button15);
                                                    b1.setBackgroundResource(R.drawable.light_on);
                                                    if (Switch1Bean.dps.get("3").toString().equals("true")) {
                                                        Switch1.publishDps("{\" 3\":false}", new IResultCallback() {
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
                                                    Button b1 = (Button)findViewById(R.id.button15);
                                                    b1.setBackgroundResource(R.drawable.group_62);
                                                }
                                            }
                                            if (dpStr.get("switch_2") != null ){
                                                if (dpStr.get("switch_2").toString().equals("true")){
                                                    Button b1 = (Button)findViewById(R.id.button14);
                                                    b1.setBackgroundResource(R.drawable.light_on);
                                                    if (Switch1Bean.dps.get("3").toString().equals("true")) {
                                                        Switch1.publishDps("{\" 3\":false}", new IResultCallback() {
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
                                                    Button b1 = (Button)findViewById(R.id.button14);
                                                    b1.setBackgroundResource(R.drawable.group_62);
                                                }
                                            }
                                            if (dpStr.get("switch_3") != null ) {
//                                                if (dpStr.get("switch_3").toString().equals("true")){
//                                                    Button b1 = (Button)findViewById(R.id.button17);
//                                                    b1.setBackgroundResource(R.drawable.light_on);
//                                                }
//                                                else {
//                                                    Button b1 = (Button)findViewById(R.id.button17);
//                                                    b1.setBackgroundResource(R.drawable.group_62);
//                                                }
                                                Button b1 = (Button)findViewById(R.id.button17);
                                                b1.setBackgroundResource(R.drawable.light_on);
                                                if (dpStr.get("switch_3").toString().equals("true")) {
                                                    if (Switch1Bean.dps.get("1").toString().equals("true")) {
                                                        Switch1.publishDps("{\" 1\":false}", new IResultCallback() {
                                                            @Override
                                                            public void onError(String code, String error) {

                                                            }

                                                            @Override
                                                            public void onSuccess() {

                                                            }
                                                        });
                                                    }
                                                    if (Switch1Bean.dps.get("2").toString().equals("true")) {
                                                        Switch1.publishDps("{\" 2\":false}", new IResultCallback() {
                                                            @Override
                                                            public void onError(String code, String error) {

                                                            }

                                                            @Override
                                                            public void onSuccess() {

                                                            }
                                                        });
                                                    }
                                                    if (Switch2Bean != null ) {
                                                        if (Switch2Bean.dps.get("1").toString().equals("true")) {
                                                            Switch2.publishDps("{\" 1\":false}", new IResultCallback() {
                                                                @Override
                                                                public void onError(String code, String error) {

                                                                }

                                                                @Override
                                                                public void onSuccess() {

                                                                }
                                                            });
                                                        }
                                                        if (Switch2Bean.dps.get("2").toString().equals("true")) {
                                                            Switch2.publishDps("{\" 2\":false}", new IResultCallback() {
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
                                                else {
                                                    b1.setBackgroundResource(R.drawable.group_62);
                                                }
                                            }
//                                            if (dpStr.get("switch_4") != null ){
//                                                if (dpStr.get("switch_4").toString().equals("true")){
//                                                    Button b1 = (Button)findViewById(R.id.button19);
//                                                    b1.setBackgroundResource(R.drawable.light_on);
//                                                }
//                                                else {
//                                                    Button b1 = (Button)findViewById(R.id.button19);
//                                                    b1.setBackgroundResource(R.drawable.group_62);
//                                                }
//                                            }
                                        }
                                        @Override
                                        public void onRemoved(String devId) {

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
                                else if (zigbeeDevices.get(i).getName().equals(LogIn.room.getRoomNumber()+"Switch2"))
                                {
                                    Switch2Status = true ;
                                    Switch2Bean = new DeviceBean() ;
                                    Switch2Bean = zigbeeDevices.get(i) ;
                                    THEROOM.setSWITCH2_B(zigbeeDevices.get(i));
                                    Switch2 = TuyaHomeSdk.newDeviceInstance(Switch2Bean.getDevId());
                                    THEROOM.setSWITCH2(TuyaHomeSdk.newDeviceInstance(Switch2Bean.getDevId()));
                                    Switch2.registerDeviceListener(new IDeviceListener() {
                                        @Override
                                        public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                                            /*
                                            Log.d("switch2" , dpStr.toString());
                                            if (dpStr.get("switch_1") != null ){
                                                if (dpStr.get("switch_1").toString().equals("true")){
                                                    if (Switch1 != null){
                                                        Switch1.publishDps("{\"1\":true}", new IResultCallback() {
                                                            @Override
                                                            public void onError(String code, String error) {

                                                            }

                                                            @Override
                                                            public void onSuccess() {

                                                            }
                                                        });
                                                    }

                                                }
                                                else{
                                                    if (Switch1 != null){
                                                        Switch1.publishDps("{\"1\":false}", new IResultCallback() {
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
                                            if (dpStr.get("switch_2") != null ){
                                                if (dpStr.get("switch_2").toString().equals("true")){
                                                    if (Switch1 != null){
                                                        Switch1.publishDps("{\"2\":true}", new IResultCallback() {
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
                                                    if (Switch1 != null){
                                                        Switch1.publishDps("{\"2\":false}", new IResultCallback() {
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
                                            if (dpStr.get("switch_3") != null ){
                                                if (dpStr.get("switch_3").toString().equals("true")){
                                                    if (Switch1 != null){
                                                        Switch1.publishDps("{\"3\":true}", new IResultCallback() {
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
                                                    if (Switch1 != null){
                                                        Switch1.publishDps("{\"3\":false}", new IResultCallback() {
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
                                            if (dpStr.get("switch_4") != null ){
                                                if (dpStr.get("switch_4").toString().equals("true")){
                                                    if (Switch1 != null){
                                                        Switch1.publishDps("{\"4\":true}", new IResultCallback() {
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
                                                    if (Switch1 != null){
                                                        Switch1.publishDps("{\"4\":false}", new IResultCallback() {
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

                                             */

                                        }

                                        @Override
                                        public void onRemoved(String devId) {

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
                                    /*
                                    iTuyaDeviceMultiControl = TuyaHomeSdk.getDeviceMultiControlInstance();
                                    iTuyaDeviceMultiControl.getDeviceDpInfoList(Switch2Bean.devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
                                        @Override
                                        public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
                                            for (int i=0 ; i<result.size(); i++){
                                                Log.d("switch2DeviceDp" , result.get(i).getName());
                                            }

                                        }

                                        @Override
                                        public void onError(String errorCode, String errorMessage) {
                                            Log.d("switch2DeviceDp" , errorMessage );
                                        }
                                    });
                                    JSONObject groupdetailes = new JSONObject();
                                    try
                                    {
                                        groupdetailes.put("devId",Switch2Bean.devId);
                                        groupdetailes.put("dpId" ,1 );
                                        groupdetailes.put("id" ,1 );
                                        groupdetailes.put("enable" ,true );

                                    }
                                    catch (JSONException e){}
                                    JSONObject multiControlBean = new JSONObject();
                                    try
                                    {
                                        multiControlBean.put("groupName","Lighting");
                                        multiControlBean.put("groupType",1);
                                        multiControlBean.put("groupDetail",groupdetailes);
                                        multiControlBean.put("id",1);
                                    }
                                    catch (JSONException e)
                                    {

                                    }
                                    iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString() , new ITuyaResultCallback<MultiControlBean>() {
                                        @Override
                                        public void onSuccess(MultiControlBean result) {
                                            //ToastUtil.shortToast(mContext,"success");
                                            Log.d("switch2DeviceDp" , result.getGroupName() );
                                        }

                                        @Override
                                        public void onError(String errorCode, String errorMessage) {
                                            //ToastUtil.shortToast(mContext,errorMessage);
                                            Log.d("switch2DeviceDp" , errorMessage );
                                        }
                                    });

                                    iTuyaDeviceMultiControl.enableMultiControl(1, new ITuyaResultCallback<Boolean>() {
                                        @Override
                                        public void onSuccess(Boolean result) {
                                            //ToastUtil.shortToast(mContext,"success");
                                            Log.d("MultiControlResult" , result.toString());
                                        }

                                        @Override
                                        public void onError(String errorCode, String errorMessage) {
                                            //ToastUtil.shortToast(mContext,errorMessage);
                                            Log.d("MultiControlResult" , errorMessage);
                                        }
                                    });

                                     */

                                }
                                else if (zigbeeDevices.get(i).getName().equals(LogIn.room.getRoomNumber()+"Switch3"))
                                {
                                    Switch3Status = true ;
                                    Switch3Bean = new DeviceBean() ;
                                    Switch3Bean = zigbeeDevices.get(i) ;
                                    THEROOM.setSWITCH3_B(zigbeeDevices.get(i));
                                    Switch3 = TuyaHomeSdk.newDeviceInstance(Switch3Bean.getDevId());
                                    THEROOM.setSWITCH3(TuyaHomeSdk.newDeviceInstance(Switch3Bean.getDevId()));
                                    Switch3.registerDeviceListener(new IDeviceListener() {
                                        @Override
                                        public void onDpUpdate(String devId, Map<String, Object> dpStr) {

                                        }

                                        @Override
                                        public void onRemoved(String devId) {

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
                                else if (zigbeeDevices.get(i).getName().equals(LogIn.room.getRoomNumber()+"Switch4"))
                                {
                                    Switch4Status = true ;
                                    Switch4Bean = new DeviceBean() ;
                                    Switch4Bean = zigbeeDevices.get(i) ;
                                    THEROOM.setSWITCH4_B(zigbeeDevices.get(i));
                                    Switch4 = TuyaHomeSdk.newDeviceInstance(Switch4Bean.getDevId());
                                    THEROOM.setSWITCH4(TuyaHomeSdk.newDeviceInstance(Switch4Bean.getDevId()));
                                    Switch4.registerDeviceListener(new IDeviceListener() {
                                        @Override
                                        public void onDpUpdate(String devId, Map<String, Object> dpStr) {

                                        }

                                        @Override
                                        public void onRemoved(String devId) {

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
                                else if (zigbeeDevices.get(i).getName().equals(LogIn.room.getRoomNumber()+"Power"))
                                {
                                    PowerControllerStatus = true ;
                                    THEROOM.setPOWER_B(zigbeeDevices.get(i));
                                    THEROOM.setPOWER(TuyaHomeSdk.newDeviceInstance(THEROOM.getPOWER_B().getDevId()));
                                    THEROOM.getPOWER().registerDeviceListener(new IDeviceListener() {
                                        @Override
                                        public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                                            Log.d("DpUpdates" , devId+"  " + String.valueOf(dpStr)) ;
                                            if (dpStr.get("switch_1") != null)
                                            {
                                                String S1 = dpStr.get("switch_1").toString() ;
                                                if (S1.equals("false"))
                                                {
                                                    myRefPower.setValue(0);
                                                    setPowerOnOff("0");
                                                }
                                                else
                                                {
                                                    myRefPower.setValue(1);
                                                    setPowerOnOff("1");
                                                }
                                            }
                                        }

                                        @Override
                                        public void onRemoved(String devId) {

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
                            }
                            if (DoorSensorStatus )
                            {
                                setDoorSensorStatus("1");
                            }
                            else
                            {
                                setDoorSensorStatus("0");
                            }

                            if (MotionSensorStatus)
                            {
                                setMotionSensorStatus("1");
                            }
                            else
                            {
                                setMotionSensorStatus("0");
                            }

                            if (CurtainControllerStatus)
                            {
                                setCurtainSwitchStatus("1");
                                curtainBtn.setVisibility(View.VISIBLE);
                                BtnsList.add(new BTN(R.drawable.restaurant_background , "CURTAIN"));
                            }
                            else
                            {
                                setCurtainSwitchStatus("0");
                                curtainBtn.setVisibility(View.INVISIBLE);
                            }

                            if (ServiceSwitchStatus)
                            {
                                setServiceSwitchStatus("1");
                            }
                            else
                            {
                                setServiceSwitchStatus("0");
                            }

                            if (Switch1Status)
                            {
                                setSwitch1Status("1");
                            }
                            else
                            {
                                setSwitch1Status("0");
                            }

                            if (Switch2Status)
                            {
                                setSwitch2Status("1");
                            }
                            else
                            {
                                setSwitch2Status("0");
                            }

                            if (Switch3Status)
                            {
                                setSwitch3Status("1");
                            }
                            else
                            {
                                setSwitch3Status("0");
                            }

                            if (Switch4Status)
                            {
                                setSwitch4Status("1");
                            }
                            else
                            {
                                setSwitch4Status("0");
                            }

                            if (!Switch1Status && !Switch2Status && !Switch3Status && !Switch4Status)
                            {
                                ShowLighting.setVisibility(View.GONE);
                            }
                            else if (Switch1Status || Switch2Status || Switch3Status || Switch4Status)
                            {
                                //ToastMaker.MakeToast(String.valueOf(Switch1Status),act);
                                ShowLighting.setVisibility(View.VISIBLE);
                                BtnsList.add(new BTN(R.drawable.restaurant_background , "LIGHTING"));
                            }

                            iTuyaDeviceMultiControl = TuyaHomeSdk.getDeviceMultiControlInstance();

//                            if (THEROOM.getSWITCH3() == null ) {
//                                setSwitch1DB2(THEROOM);
//                            }
//                            else {
//                                bindSwitch1ToSwitch3btn1(THEROOM);
//                                bindSwitch1ToSwitch3btn2(THEROOM);
//                            }

//                            setSwitch1DB1();
//                            setSwitch1DB2();
//                            setSwitch1DB3();
//                            setSwitch1DB4();

                            /*
                            iTuyaDeviceMultiControl.getDeviceDpInfoList(THEROOM.getSWITCH1_B().devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
                                @Override
                                public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
                                    Log.d("switch1DeviceDp" , result.get(0).getDpId() );
                                    iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH1_B().devId, result.get(0).getDpId(), new ITuyaDataCallback<MultiControlLinkBean>() {
                                        @Override
                                        public void onSuccess(MultiControlLinkBean result) {

                                            if (result.getMultiGroup() != null ){
                                                Log.d("switch1DeviceDp" , "1 DP subscriped to "+result.getMultiGroup().getGroupName() );
                                            }
                                            else {
                                                if (THEROOM.getSWITCH2_B() != null){
                                                    iTuyaDeviceMultiControl.getDeviceDpInfoList(THEROOM.getSWITCH2_B().devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
                                                        @Override
                                                        public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
                                                            Log.d("switch2DeviceDp", result.get(0).getDpId());
                                                            iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH2_B().devId, result.get(1).getDpId(), new ITuyaDataCallback<MultiControlLinkBean>() {
                                                                @Override
                                                                public void onSuccess(MultiControlLinkBean result) {
                                                                    if (result.getMultiGroup() != null) {
                                                                        Log.d("switch2DeviceDp", "2 DP subscriped to " + result.getMultiGroup().getGroupName());

                                                                    } else
                                                                        {
                                                                            Random r = new Random();
                                                                            int x = r.nextInt(99);
                                                                        JSONObject groupdetailes1 = new JSONObject(), groupdetailes2 = new JSONObject();
                                                                        try {
                                                                            groupdetailes1.put("devId", Switch1Bean.devId);
                                                                            groupdetailes1.put("dpId", 1);
                                                                            groupdetailes1.put("id", x);
                                                                            groupdetailes1.put("enable", true);

                                                                        } catch (JSONException e) {
                                                                        }
                                                                        try {
                                                                            groupdetailes2.put("devId", Switch2Bean.devId);
                                                                            groupdetailes2.put("dpId", 1);
                                                                            groupdetailes2.put("id", x);
                                                                            groupdetailes2.put("enable", true);

                                                                        } catch (JSONException e) {
                                                                        }
                                                                        JSONArray arr = new JSONArray();
                                                                        arr.put(groupdetailes2);
                                                                        arr.put(groupdetailes1);
                                                                        JSONObject multiControlBean = new JSONObject();
                                                                        try {
                                                                            multiControlBean.put("groupName", LogIn.room.getRoomNumber()+"Lighting"+x);
                                                                            multiControlBean.put("groupType", 1);
                                                                            multiControlBean.put("groupDetail", arr);
                                                                            multiControlBean.put("id", x);
                                                                        } catch (JSONException e) {

                                                                        }
                                                                        iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString(), new ITuyaResultCallback<MultiControlBean>() {
                                                                            @Override
                                                                            public void onSuccess(MultiControlBean result) {
                                                                                //ToastUtil.shortToast(mContext,"success");
                                                                                Log.d("switch1DeviceDp", result.getGroupName());
                                                                                iTuyaDeviceMultiControl.enableMultiControl(x, new ITuyaResultCallback<Boolean>() {
                                                                                    @Override
                                                                                    public void onSuccess(Boolean result) {
                                                                                        //ToastUtil.shortToast(mContext,"success");
                                                                                        Log.d("MultiControlResult", result.toString());
                                                                                    }

                                                                                    @Override
                                                                                    public void onError(String errorCode, String errorMessage) {
                                                                                        //ToastUtil.shortToast(mContext,errorMessage);
                                                                                        Log.d("MultiControlResult", errorMessage);
                                                                                    }
                                                                                });
                                                                            }

                                                                            @Override
                                                                            public void onError(String errorCode, String errorMessage) {
                                                                                //ToastUtil.shortToast(mContext,errorMessage);
                                                                                Log.d("switch1DeviceDp", errorMessage+"here");
                                                                            }
                                                                        });
                                                                    }
                                                                }

                                                                @Override
                                                                public void onError(String errorCode, String errorMessage) {

                                                                }
                                                            });

                                                        }

                                                        @Override
                                                        public void onError(String errorCode, String errorMessage) {
                                                            Log.d("switch2DeviceDp", errorMessage);
                                                        }
                                                    });
                                                }
                                            }
                                        }

                                        @Override
                                        public void onError(String errorCode, String errorMessage) {
                                            // ToastUtil.shortToast(mContext,errorMessage);
                                        }
                                    });

                                }

                                @Override
                                public void onError(String errorCode, String errorMessage) {
                                    Log.d("switch1DeviceDp" , errorMessage );
                                }
                            });



                            iTuyaDeviceMultiControl.getDeviceDpInfoList(THEROOM.getSWITCH1_B().devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
                                @Override
                                public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
                                    Log.d("switch1DeviceDp1" , result.get(1).getDpId() );
                                    iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH1_B().devId, result.get(1).getDpId(), new ITuyaDataCallback<MultiControlLinkBean>() {
                                        @Override
                                        public void onSuccess(MultiControlLinkBean result) {

                                            if (result.getMultiGroup() != null ){
                                                Log.d("switch1DeviceDp1" , "1 DP subscriped to "+result.getMultiGroup().getGroupName() );
                                            }
                                            else {
                                                if (Switch2Bean != null){
                                                    iTuyaDeviceMultiControl.getDeviceDpInfoList(THEROOM.getSWITCH2_B().devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
                                                        @Override
                                                        public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
                                                            Log.d("switch2DeviceDp1" , result.get(1).getDpId() );
                                                            iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH2_B().devId, result.get(1).getDpId(), new ITuyaDataCallback<MultiControlLinkBean>() {
                                                                @Override
                                                                public void onSuccess(MultiControlLinkBean result) {
                                                                    if (result.getMultiGroup() != null){
                                                                        Log.d("switch2DeviceDp1" , "2 DP subscriped to "+result.getMultiGroup().getGroupName() );

                                                                    }
                                                                    else {
                                                                        Random r = new Random();
                                                                        int x = r.nextInt(40);
                                                                        int y = r.nextInt(20);
                                                                        JSONObject groupdetailes1 = new JSONObject(),groupdetailes2 = new JSONObject();
                                                                        try
                                                                        {
                                                                            groupdetailes1.put("devId",Switch1Bean.devId);
                                                                            groupdetailes1.put("dpId" ,2 );
                                                                            groupdetailes1.put("id" , y );
                                                                            groupdetailes1.put("enable" ,true );

                                                                        }
                                                                        catch (JSONException e){}
                                                                        try
                                                                        {
                                                                            groupdetailes2.put("devId",Switch2Bean.devId);
                                                                            groupdetailes2.put("dpId" ,2 );
                                                                            groupdetailes2.put("id" , y );
                                                                            groupdetailes2.put("enable" ,true );

                                                                        }
                                                                        catch (JSONException e){}
                                                                        JSONArray arr = new JSONArray( );
                                                                        arr.put(groupdetailes2);
                                                                        arr.put(groupdetailes1);
                                                                        JSONObject multiControlBean = new JSONObject();
                                                                        try
                                                                        {
                                                                            multiControlBean.put("groupName",LogIn.room.getRoomNumber()+"Lighting"+y);
                                                                            multiControlBean.put("groupType",1);
                                                                            multiControlBean.put("groupDetail" , arr);
                                                                            multiControlBean.put("id",y);
                                                                        }
                                                                        catch (JSONException e)
                                                                        {

                                                                        }
                                                                        iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString() , new ITuyaResultCallback<MultiControlBean>() {
                                                                            @Override
                                                                            public void onSuccess(MultiControlBean result) {
                                                                                //ToastUtil.shortToast(mContext,"success");
                                                                                Log.d("switch1DeviceDp2" , result.getGroupName() );
                                                                                iTuyaDeviceMultiControl.enableMultiControl(y, new ITuyaResultCallback<Boolean>() {
                                                                                    @Override
                                                                                    public void onSuccess(Boolean result) {
                                                                                        //ToastUtil.shortToast(mContext,"success");
                                                                                        Log.d("MultiControlResult" , result.toString());
                                                                                    }

                                                                                    @Override
                                                                                    public void onError(String errorCode, String errorMessage) {
                                                                                        //ToastUtil.shortToast(mContext,errorMessage);
                                                                                        Log.d("MultiControlResult" , errorMessage);
                                                                                    }
                                                                                });
                                                                            }

                                                                            @Override
                                                                            public void onError(String errorCode, String errorMessage) {
                                                                                //ToastUtil.shortToast(mContext,errorMessage);
                                                                                Log.d("switch1DeviceDp" , errorMessage );
                                                                            }
                                                                        });
                                                                    }
                                                                }

                                                                @Override
                                                                public void onError(String errorCode, String errorMessage) {

                                                                }
                                                            });

                                                        }

                                                        @Override
                                                        public void onError(String errorCode, String errorMessage) {
                                                            Log.d("switch2DeviceDp" , errorMessage );
                                                        }
                                                    });
                                                }

                                            }
                                        }

                                        @Override
                                        public void onError(String errorCode, String errorMessage) {
                                            // ToastUtil.shortToast(mContext,errorMessage);
                                        }
                                    });

                                }

                                @Override
                                public void onError(String errorCode, String errorMessage) {
                                    Log.d("switch1DeviceDp" , errorMessage );
                                }
                            });



                            if (THEROOM.getSWITCH1_B().dps.get("3") !=null && THEROOM.getSWITCH2_B().dps.get("3") != null){
                                iTuyaDeviceMultiControl.getDeviceDpInfoList(THEROOM.getSWITCH1_B().devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
                                    @Override
                                    public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
                                        Log.d("switch1DeviceDp" , result.get(2).getDpId() );
                                        iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH1_B().devId, result.get(2).getDpId(), new ITuyaDataCallback<MultiControlLinkBean>() {
                                            @Override
                                            public void onSuccess(MultiControlLinkBean result) {

                                                if (result.getMultiGroup() != null ){
                                                    Log.d("switch1DeviceDp" , "1 DP subscriped to "+result.getMultiGroup().getGroupName() );
                                                }
                                                else {
                                                    iTuyaDeviceMultiControl.getDeviceDpInfoList(THEROOM.getSWITCH2_B().devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
                                                        @Override
                                                        public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
                                                            Log.d("switch1DeviceDp" , result.get(2).getDpId() );
                                                            iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH2_B().devId, result.get(2).getDpId(), new ITuyaDataCallback<MultiControlLinkBean>() {
                                                                @Override
                                                                public void onSuccess(MultiControlLinkBean result) {
                                                                    if (result.getMultiGroup() != null){
                                                                        Log.d("switch1DeviceDp" , "2 DP subscriped to "+result.getMultiGroup().getGroupName() );

                                                                    }
                                                                    else {
                                                                        Random r = new Random();
                                                                        int x = r.nextInt(30);
                                                                        JSONObject groupdetailes1 = new JSONObject(),groupdetailes2 = new JSONObject();
                                                                        try
                                                                        {
                                                                            groupdetailes1.put("devId",Switch1Bean.devId);
                                                                            groupdetailes1.put("dpId" ,3 );
                                                                            groupdetailes1.put("id" ,x );
                                                                            groupdetailes1.put("enable" ,true );

                                                                        }
                                                                        catch (JSONException e){}
                                                                        try
                                                                        {
                                                                            groupdetailes2.put("devId",Switch2Bean.devId);
                                                                            groupdetailes2.put("dpId" ,3 );
                                                                            groupdetailes2.put("id" ,x );
                                                                            groupdetailes2.put("enable" ,true );

                                                                        }
                                                                        catch (JSONException e){}
                                                                        JSONArray arr = new JSONArray( );
                                                                        arr.put(groupdetailes2);
                                                                        arr.put(groupdetailes1);
                                                                        JSONObject multiControlBean = new JSONObject();
                                                                        try
                                                                        {
                                                                            multiControlBean.put("groupName",LogIn.room.getRoomNumber()+"Lighting2");
                                                                            multiControlBean.put("groupType",1);
                                                                            multiControlBean.put("groupDetail" , arr);
                                                                            multiControlBean.put("id",x);
                                                                        }
                                                                        catch (JSONException e)
                                                                        {

                                                                        }
                                                                        iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString() , new ITuyaResultCallback<MultiControlBean>() {
                                                                            @Override
                                                                            public void onSuccess(MultiControlBean result) {
                                                                                //ToastUtil.shortToast(mContext,"success");
                                                                                Log.d("switch1DeviceDp" , result.getGroupName() );
                                                                                iTuyaDeviceMultiControl.enableMultiControl(x, new ITuyaResultCallback<Boolean>() {
                                                                                    @Override
                                                                                    public void onSuccess(Boolean result) {
                                                                                        //ToastUtil.shortToast(mContext,"success");
                                                                                        Log.d("MultiControlResult" , result.toString());
                                                                                    }

                                                                                    @Override
                                                                                    public void onError(String errorCode, String errorMessage) {
                                                                                        //ToastUtil.shortToast(mContext,errorMessage);
                                                                                        Log.d("MultiControlResult" , errorMessage);
                                                                                    }
                                                                                });
                                                                            }

                                                                            @Override
                                                                            public void onError(String errorCode, String errorMessage) {
                                                                                //ToastUtil.shortToast(mContext,errorMessage);
                                                                                Log.d("switch1DeviceDp" , errorMessage );
                                                                            }
                                                                        });
                                                                    }
                                                                }

                                                                @Override
                                                                public void onError(String errorCode, String errorMessage) {

                                                                }
                                                            });

                                                        }

                                                        @Override
                                                        public void onError(String errorCode, String errorMessage) {
                                                            Log.d("switch2DeviceDp" , errorMessage );
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onError(String errorCode, String errorMessage) {
                                                // ToastUtil.shortToast(mContext,errorMessage);
                                            }
                                        });

                                    }

                                    @Override
                                    public void onError(String errorCode, String errorMessage) {
                                        Log.d("switch1DeviceDp" , errorMessage );
                                    }
                                });
                            }

                            if (THEROOM.getSWITCH1_B().dps.get("4") != null && THEROOM.getSWITCH2_B().dps.get("4") != null){
                                iTuyaDeviceMultiControl.getDeviceDpInfoList(THEROOM.getSWITCH1_B().devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
                                    @Override
                                    public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
                                        Log.d("switch1DeviceDp" , result.get(3).getDpId() );
                                        iTuyaDeviceMultiControl.queryLinkInfoByDp(Switch1Bean.devId, result.get(3).getDpId(), new ITuyaDataCallback<MultiControlLinkBean>() {
                                            @Override
                                            public void onSuccess(MultiControlLinkBean result) {

                                                if (result.getMultiGroup() != null ){
                                                    Log.d("switch1DeviceDp" , "1 DP subscriped to "+result.getMultiGroup().getGroupName() );
                                                }
                                                else {
                                                    iTuyaDeviceMultiControl.getDeviceDpInfoList(Switch2Bean.devId, new ITuyaDataCallback<ArrayList<DeviceDpInfoBean>>() {
                                                        @Override
                                                        public void onSuccess(ArrayList<DeviceDpInfoBean> result) {
                                                            Log.d("switch1DeviceDp" , result.get(3).getDpId() );
                                                            iTuyaDeviceMultiControl.queryLinkInfoByDp(Switch2Bean.devId, result.get(3).getDpId(), new ITuyaDataCallback<MultiControlLinkBean>() {
                                                                @Override
                                                                public void onSuccess(MultiControlLinkBean result) {
                                                                    if (result.getMultiGroup() != null){
                                                                        Log.d("switch1DeviceDp" , "2 DP subscriped to "+result.getMultiGroup().getGroupName() );

                                                                    }
                                                                    else {
                                                                        Random r = new Random();
                                                                        int x = r.nextInt(30);
                                                                        JSONObject groupdetailes1 = new JSONObject(),groupdetailes2 = new JSONObject();
                                                                        try
                                                                        {
                                                                            groupdetailes1.put("devId",Switch1Bean.devId);
                                                                            groupdetailes1.put("dpId" ,4 );
                                                                            groupdetailes1.put("id" ,x );
                                                                            groupdetailes1.put("enable" ,true );

                                                                        }
                                                                        catch (JSONException e){}
                                                                        try
                                                                        {
                                                                            groupdetailes2.put("devId",Switch2Bean.devId);
                                                                            groupdetailes2.put("dpId" ,4 );
                                                                            groupdetailes2.put("id" ,x);
                                                                            groupdetailes2.put("enable" ,true );

                                                                        }
                                                                        catch (JSONException e){}
                                                                        JSONArray arr = new JSONArray( );
                                                                        arr.put(groupdetailes2);
                                                                        arr.put(groupdetailes1);
                                                                        JSONObject multiControlBean = new JSONObject();
                                                                        try
                                                                        {
                                                                            multiControlBean.put("groupName",LogIn.room.getRoomNumber()+"Lighting"+x);
                                                                            multiControlBean.put("groupType",1);
                                                                            multiControlBean.put("groupDetail" , arr);
                                                                            multiControlBean.put("id",x);
                                                                        }
                                                                        catch (JSONException e)
                                                                        {

                                                                        }
                                                                        iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString() , new ITuyaResultCallback<MultiControlBean>() {
                                                                            @Override
                                                                            public void onSuccess(MultiControlBean result) {
                                                                                //ToastUtil.shortToast(mContext,"success");
                                                                                Log.d("switch1DeviceDp" , result.getGroupName() );
                                                                                iTuyaDeviceMultiControl.enableMultiControl(x, new ITuyaResultCallback<Boolean>() {
                                                                                    @Override
                                                                                    public void onSuccess(Boolean result) {
                                                                                        //ToastUtil.shortToast(mContext,"success");
                                                                                        Log.d("MultiControlResult" , result.toString());
                                                                                    }

                                                                                    @Override
                                                                                    public void onError(String errorCode, String errorMessage) {
                                                                                        //ToastUtil.shortToast(mContext,errorMessage);
                                                                                        Log.d("MultiControlResult" , errorMessage);
                                                                                    }
                                                                                });
                                                                            }

                                                                            @Override
                                                                            public void onError(String errorCode, String errorMessage) {
                                                                                //ToastUtil.shortToast(mContext,errorMessage);
                                                                                Log.d("switch1DeviceDp" , errorMessage );
                                                                            }
                                                                        });
                                                                    }
                                                                }

                                                                @Override
                                                                public void onError(String errorCode, String errorMessage) {

                                                                }
                                                            });

                                                        }

                                                        @Override
                                                        public void onError(String errorCode, String errorMessage) {
                                                            Log.d("switch2DeviceDp" , errorMessage );
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onError(String errorCode, String errorMessage) {
                                                // ToastUtil.shortToast(mContext,errorMessage);
                                            }
                                        });

                                    }

                                    @Override
                                    public void onError(String errorCode, String errorMessage) {
                                        Log.d("switch1DeviceDp" , errorMessage );
                                    }
                                });
                            }

                             */
                        }
                        else
                        {
                            ToastMaker.MakeToast("No Zigbee Router" , act);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e("DevicesError" , e.getMessage());
                    }
                    adapter.notifyDataSetChanged();

                }
                @Override
                public void onError(String errorCode, String errorMessage) {

                }
            });

        }
        if (Tuya_Devices.AC == null )
        {
            ACStatus = false ;
            setThermostatStatus("0");
            ShowAc.setVisibility(View.INVISIBLE);
            THEROOM.setAC(null);
            THEROOM.setAC_B(null);
        }
        else
        {
            ACStatus = true ;
            setThermostatStatus("1");
            ShowAc.setVisibility(View.VISIBLE);
            BtnsList.add(new BTN(R.drawable.restaurant_background,"AC Control"));
            THEROOM.setAC(Tuya_Devices.AC);
            THEROOM.setAC_B(Tuya_Devices.ACbean);

            try
            {
                Tuya_Devices.AC.registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr)
                    {
                        Log.d("ACeee" , dpStr.toString());
                        if (dpStr.get("temp_current") != null)
                        {
                            CurrentTemp = dpStr.get("temp_current").toString() ;
                            double temp = (Integer.parseInt(dpStr.get("temp_current").toString())*0.1);
                            myRefTemp.setValue(String.valueOf(temp));
                            TextView currentTempText = (TextView) findViewById(R.id.currentTemp);
                            currentTempText.setText(String.valueOf(temp));
                        }
                        if ( dpStr.get("temp_set") != null )
                        {
                            if (Double.parseDouble(dpStr.get("temp_set").toString()) !=  Double.parseDouble(TempSetPoint))
                            {
                                ClientTemp = dpStr.get("temp_set").toString();
                                TextView clientTempText = (TextView) findViewById(R.id.clientTemp);
                                int x = Integer.parseInt(dpStr.get("temp_set").toString());
                                double y = x*0.1 ;
                                clientTempText.setText(String.valueOf(y));
                            }
                        }
                    }
                    @Override
                    public void onRemoved(String devId) {

                    }

                    @Override
                    public void onStatusChanged(String devId, boolean online) {

                    }

                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status) {

                    }

                    @Override
                    public void onDevInfoUpdate(String devId)
                    {

                    }
                });
            }
            catch (Exception e )
            {

            }
        }
        if (Tuya_Devices.mDevice == null )
        {
            PowerControllerStatus = false ;
            setPowerSwitchStatus("0");
        }
        else
        {
            Log.d("PowerDps",Tuya_Devices.powerBean.dps.toString() );
            PowerControllerStatus = true ;
            setPowerSwitchStatus("1");

            try
            {
                Tuya_Devices.mDevice.registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr)
                    {
                        Log.d("DpUpdates" , devId+"  " + String.valueOf(dpStr)) ;
                        if (dpStr.get("switch_2") != null)
                        {
                            String S1 = dpStr.get("switch_2").toString() ;
                            if (S1.equals("false"))
                            {
                                //myRefPower.setValue(0);
                            }
                            else
                            {
                                //myRefPower.setValue(1);
                            }
                        }

                    }
                    @Override
                    public void onRemoved(String devId)
                    {

                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online)
                    {
                        Log.d("DpStatusChanged" , String.valueOf(online) + " " + devId ) ;
                    }
                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status)
                    {

                    }
                    @Override
                    public void onDevInfoUpdate(String devId)
                    {
                        Log.d("onDevInfoUpdate" ,  devId ) ;
                    }
                });
            }
            catch (Exception e)
            {

            }
        }
        if (LogIn.myLock == null )
        {
            LockStatus = false ;
            setLockStatus("0");
            //OpenDoorBtn.setVisibility(View.INVISIBLE);
            OpenDoor.setVisibility(View.INVISIBLE);
            THEROOM.setLock(null);
        }
        else
        {
            LockStatus = true ;
            myTestLockEKey = LogIn.myLock ;
            setLockStatus("1");
            //OpenDoorBtn.setVisibility(View.VISIBLE);
            THEROOM.setLock(LogIn.myLock);
            OpenDoor.setVisibility(View.VISIBLE);
        }

        blink();
        setTabValue("1");
        adapter = new BTN_ADAPTER(BtnsList);
        BTNS.setAdapter(adapter);
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

        Light3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LightingBind = true ;
                Toast.makeText(act,"*",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        Light3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LightingBind = false ;
                Log.d("btn3Problem" , "clicked");
                if (Switch1Bean != null )
                {
                    Log.d("btn3Problem" , "iam not null");
                    if (Switch1Bean.dps.get("3") != null){
                        Log.d("btn3Problem" , "3 not null");
                        if (Switch1Bean.dps.get("3").toString().equals("false"))
                        {
                            try
                            {
                                Switch1.publishDps("{\" 3\":true}", new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {
                                        //ToastMaker.MakeToast(error + " "+ code ,act);
                                    }

                                    @Override
                                    public void onSuccess() {
                                        //ToastMaker.MakeToast("sent",act);
                                    }
                                });
                            }
                            catch (Exception e)
                            {

                            }

                        }
                        else
                        {
                            try
                            {
                                Switch1.publishDps("{\" 3\":false}", new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {
                                        //ToastMaker.MakeToast(error + " "+ code ,act);
                                    }

                                    @Override
                                    public void onSuccess() {
                                        //ToastMaker.MakeToast("sent",act);
                                    }
                                });
                            }
                            catch (Exception e)
                            {

                            }

                        }
                    }

                }
            }
        });
        Light1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (LightingBind) {
                    bindSwitch1_2DP1_2();
                    LightingBind = false ;
                    //Toast.makeText(act,"1ok",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        Light2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                bindSwitch1_2DP2_1();
                LightingBind = false ;
                //Toast.makeText(act,"2ok",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }


//-------------------------------------------------------------
    // Buttons
    public void goToLaundry(View view)
    {
        mediaPlayer.start();
        if (CURRENT_ROOM_STATUS == 2)
        {
            if (LaundryStatus == false)
            {
                final Dialog d = new Dialog(act);
                d.setContentView(R.layout.confermation_dialog);
                TextView message = (TextView) d.findViewById(R.id.confermationDialog_Text);
                message.setText("You Are Sending Laundry Order .. Are You Sure");
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
                    public void onClick(View v)
                    {
                        d.dismiss();
                        addLaundryOrderInDataBase();
                    }
                });
                d.show();
            }
            else
            {
                final Dialog d = new Dialog(act);
                d.setContentView(R.layout.confermation_dialog);
                TextView message = (TextView) d.findViewById(R.id.confermationDialog_Text);
                message.setText("You Are Cancelling Laundry Order .. Are You Sure");
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
                    public void onClick(View v)
                    {
                        d.dismiss();
                        removeLaundryOrderInDataBase();
                    }
                });
                d.show();

            }
        }
        else
        {
            ToastMaker.MakeToast("This Room Is Vacant" , act);
        }
        x=0;
    }

    public static void requestCleanUp(View view)
    {
        mediaPlayer.start();
        if (CURRENT_ROOM_STATUS == 2)
        {
            if (CleanupStatus == false)
            {
                final Dialog d = new Dialog(act);
                d.setContentView(R.layout.confermation_dialog);
                TextView message = (TextView) d.findViewById(R.id.confermationDialog_Text);
                message.setText("You Are Sending CleanUp Order .. Are You Sure");
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
                    public void onClick(View v)
                    {
                        d.dismiss();
                        addCleanupOrderInDataBase();

                    }
                });
                d.show();
            }
            else
            {
                final Dialog d = new Dialog(act);
                d.setContentView(R.layout.confermation_dialog);
                TextView message = (TextView) d.findViewById(R.id.confermationDialog_Text);
                message.setText("You Are Cancelling CleanUp Order .. Are You Sure");
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
                    public void onClick(View v)
                    {
                        d.dismiss();
                        removeCleanupOrderInDataBase();

                    }
                });
                d.show();

            }
        }
        else
        {
            ToastMaker.MakeToast("This Room Is Vacant" , act);
        }
        x=0;
    }

    public static void roomServiceShowDialog(View view)
    {
        mediaPlayer.start();
        try
        {
            if (CURRENT_ROOM_STATUS == 2)
            {
                if (RoomServiceStatus)
                {
                    final Dialog d = new Dialog(act);
                    d.setContentView(R.layout.confermation_dialog);
                    TextView message = (TextView) d.findViewById(R.id.confermationDialog_Text);
                    message.setText("You Are Cancelling RoomService Order .. Are You Sure");
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
                        public void onClick(View v)
                        {
                            d.dismiss();
                            removeRoomServiceOrderInDataBase();
                        }
                    });
                    d.show();

                }
                else
                {
                    final Dialog d = new Dialog(act);
                    d.setContentView(R.layout.confermation_dialog);
                    TextView message = (TextView) d.findViewById(R.id.confermationDialog_Text);
                    message.setText("You Are Sending RoomService Order .. Are You Sure");
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
                        public void onClick(View v)
                        {
                            d.dismiss();
                            addRoomServiceOrderInDataBase();

                        }
                    });
                    d.show();
                }
            }
            else
            {
                ToastMaker.MakeToast("This Room Is Vacant" , act);
            }

        }
        catch (Exception e)
        {

        }
        x=0;
    }

    public static void SOS(View view)
    {
        mediaPlayer.start();
        try
        {
            if (CURRENT_ROOM_STATUS == 2)
            {
                if (SosStatus == false)
                {
                    final Dialog d = new Dialog(act);
                    d.setContentView(R.layout.confermation_dialog);
                    TextView message = (TextView) d.findViewById(R.id.confermationDialog_Text);
                    message.setText("Send Emergency Order .. ?                   ");
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
                        public void onClick(View v)
                        {
                            d.dismiss();
                            final String depo = "SOS";
                            Calendar x = Calendar.getInstance(Locale.getDefault());
                            long timee =  x.getTimeInMillis();
                            LoadingDialog dd = new LoadingDialog(act);
                            StringRequest addOrder = new StringRequest(Request.Method.POST, insertServiceOrderUrl , new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response)
                                {
                                    dd.stop();
                                    if (Integer.parseInt(response) > 0 )
                                    {
                                        sosId = Integer.parseInt(response);
                                        myRefSos.setValue(timee);
                                        myRefdep.setValue(depo);
                                        myRefDND.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot)
                                            {
                                                if (Long.parseLong(snapshot.getValue().toString()) > 0)
                                                {
                                                    myRefDND.setValue(0);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        ToastMaker.MakeToast("تم ارسال طلب " +"SOS" , act);
                                        Calendar x = Calendar.getInstance(Locale.getDefault());
                                        long time =  x.getTimeInMillis();
                                        SosStatus = true ;
                                        sosOn();

                                    }
                                    else
                                    {
                                        Toast.makeText(act , response,Toast.LENGTH_LONG).show();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error)
                                {
                                    dd.stop();
                                    //Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            })
                            {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError
                                {
                                    Map<String,String> params = new HashMap<String, String>();
                                    params.put("roomNumber" ,String.valueOf(LogIn.room.getRoomNumber()));
                                    params.put("time" ,String.valueOf(timee));
                                    params.put("dep" ,depo);
                                    params.put("Hotel" , String.valueOf(LogIn.room.getHotel()));
                                    params.put("RorS" ,String.valueOf( RoomOrSuite));
                                    params.put("Reservation" ,String.valueOf( RESERVATION));
                                    return params;
                                }

                            };
                            Volley.newRequestQueue(act).add(addOrder);

                            for(ServiceEmps emp : Emps) {
                                if (emp.department.equals("Service") || emp.department.equals("RoomService") || emp.department.equals("Cleanup")) {
                                    emp.makemessage(emp.token,"SOS",true,act);
                                }
                            }
                        }
                    });
                    d.show();
                }
                else
                {
                    LoadingDialog ddd = new LoadingDialog(act);
                    myRefSos.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            if (Long.parseLong(snapshot.getValue().toString()) > 0 )
                            {
                                sosId = Long.parseLong(snapshot.getValue().toString()) ;
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    final String depo = "SOS";
                    StringRequest removOrder = new StringRequest(Request.Method.POST, removeServiceOrderUrl , new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {
                            ddd.stop();
                            if (response.equals("1")  )
                            {
                                //sos.setBackgroundResource(R.drawable.sos_icon1);
                                //sos.setTextColor(Color.parseColor("#FFE083") );
                                myRefSos.setValue(0);
                                ToastMaker.MakeToast("تم الغاء طلب " + "SOS" , act);
                                Calendar x = Calendar.getInstance(Locale.getDefault());
                                long time =  x.getTimeInMillis();
                                SosStatus = false ;
                                sosOff();

                            }
                            else
                            {
                                //Toast.makeText(act , response,Toast.LENGTH_LONG).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            ddd.stop();
                            // Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError
                        {
                            Map<String,String> params = new HashMap<String, String>();
                            params.put("id" , String.valueOf( sosId));
                            params.put("room" , String.valueOf( LogIn.room.getRoomNumber()));
                            params.put("dep" , "SOS");
                            params.put("Hotel" , String.valueOf( LogIn.room.getHotel()));
                            return params;
                        }
                    };
                    Volley.newRequestQueue(act).add(removOrder);
                    for(ServiceEmps emp : Emps) {
                        if (emp.department.equals("Service") || emp.department.equals("RoomService") || emp.department.equals("Cleanup")) {
                            emp.makemessage(emp.token,"SOS",false,act);
                        }
                    }
                }
            }
            else
            {
                ToastMaker.MakeToast("This Room Is Vacant" , act);
            }
        }
        catch (Exception e )
        {

        }

    }

    public void goToRestaurant(View view)
    {
        mediaPlayer.start();
        if (CURRENT_ROOM_STATUS == 2)
        {
            Intent i = new Intent(act , RESTAURANTS.class);
            startActivity(i);
        }
        else
        {
            ToastMaker.MakeToast("This Room Is Vacant" , act);
        }

    }

    @TargetApi(Build.VERSION_CODES.N)
    public void GymBtn(View view)
    {
        if (CURRENT_ROOM_STATUS == 2)
        {
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
                            par.put("roomNumber", String.valueOf(LogIn.room.getRoomNumber()));
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

    public  void makecheckOut(View view)
    {
        mediaPlayer.start();
        try
        {
            if (CURRENT_ROOM_STATUS == 2 )
            {
                final String dep = "Checkout";
                if (!CheckoutStatus)
                {

                    final Dialog d = new Dialog(act);
                    d.setContentView(R.layout.confermation_dialog);
                    TextView message = (TextView) d.findViewById(R.id.confermationDialog_Text);
                    message.setText("You Are Requesting Checkout .. Are You Sure");
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
                        public void onClick(View v)
                        {
                            d.dismiss();
                            LoadingDialog loading = new LoadingDialog(act);
                            final String dep = "Checkout";
                            Calendar x = Calendar.getInstance(Locale.getDefault());
                            long timee = x.getTimeInMillis();

                            final StringRequest request = new StringRequest(Request.Method.POST, insertServiceOrderUrl, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response)
                                {
                                    loading.stop();
                                    if (response.equals("0"))
                                    {

                                    }
                                    else
                                        {
                                        ToastMaker.MakeToast(dep+ " Order Sent Successfully" , act);
                                        checkOutId = Integer.parseInt(response);
                                        myRefCheckout.setValue(timee);
                                        myRefdep.setValue(dep);
                                        myRefDND.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (Integer.parseInt(snapshot.getValue().toString()) > 0) {
                                                    myRefDND.setValue(0);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        checkoutOn();
                                        CheckoutStatus = true;
                                        Dialog RatingDialog = new Dialog(act);
                                        RatingDialog.setContentView(R.layout.rating_dialog);
                                        RatingDialog.setCancelable(false);
                                        Button sendRating = (Button) RatingDialog.findViewById(R.id.sendRatingButton);
                                        RatingBar RatingD = (RatingBar) RatingDialog.findViewById(R.id.ratingBar);
                                        final String[] RATING = {""};
                                        RatingD.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                            @Override
                                            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
                                            {
                                                //ToastMaker.MakeToast(String.valueOf(rating),act);
                                                RATING[0] = String.valueOf(rating) ;
                                            }
                                        });
                                        if (THEROOM.getSERVICE_B() != null ){
                                            if (THEROOM.getSERVICE_B().dps.get("4") != null ){
                                                THEROOM.getSERVICE().publishDps("{\"4\":true}", new IResultCallback() {
                                                    @Override
                                                    public void onError(String code, String error) {

                                                    }

                                                    @Override
                                                    public void onSuccess() {

                                                    }
                                                });
                                            }
                                        }
                                        sendRating.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                RatingDialog.dismiss();
                                                LoadingDialog loading = new LoadingDialog(act);
                                                //ToastMaker.MakeToast(RATING[0],act);
                                                String url = LogIn.URL+"insertRating.php";
                                                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
                                                {
                                                    @Override
                                                    public void onResponse(String response)
                                                    {
                                                        loading.stop();//ToastMaker.MakeToast(response,act);

                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error)
                                                    {
                                                        loading.stop();
                                                        //ToastMaker.MakeToast(error.getMessage(),act);
                                                        //RatingDialog.dismiss();
                                                    }
                                                })
                                                {
                                                    @Override
                                                    protected Map<String, String> getParams() throws AuthFailureError {
                                                        Map<String,String> Params = new HashMap<String, String>();
                                                        Params.put("Reservation" , String.valueOf( RESERVATION));
                                                        Params.put("Rating" , RATING[0]);
                                                        return Params;
                                                    }
                                                };
                                                Volley.newRequestQueue(act).add(request);
                                            }
                                        });
                                        RatingDialog.show();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error)
                                {
                                    loading.stop();
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("roomNumber", String.valueOf(LogIn.room.getRoomNumber()));
                                    params.put("time", String.valueOf(timee));
                                    params.put("dep", dep);
                                    params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
                                    params.put("RorS", String.valueOf(RoomOrSuite));
                                    params.put("Reservation", String.valueOf(RESERVATION));
                                    return params;
                                }
                            };
                            Volley.newRequestQueue(act).add(request);
                        }
                    });
                    d.show();
                }
                else
                {
                    myRefCheckout.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (Long.parseLong(snapshot.getValue().toString()) > 0)
                            {
                                checkOutId = Long.parseLong(snapshot.getValue().toString());

                                final Dialog d = new Dialog(act);
                                d.setContentView(R.layout.confermation_dialog);
                                TextView message = (TextView) d.findViewById(R.id.confermationDialog_Text);
                                message.setText("You Are Cancelling CheckOut Order .. Are You Sure");
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
                                    public void onClick(View v)
                                    {
                                        d.dismiss();
                                        LoadingDialog loading = new LoadingDialog(act);
                                        StringRequest re = new StringRequest(Request.Method.POST, removeServiceOrderUrl, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                loading.stop();
                                                checkoutOff();
                                                myRefCheckout.setValue(0);
                                                CheckoutStatus = false;
                                                if (THEROOM.getSERVICE_B() != null ){
                                                    if (THEROOM.getSERVICE_B().dps.get("4") != null ){
                                                        THEROOM.getSERVICE().publishDps("{\"4\":false}", new IResultCallback() {
                                                            @Override
                                                            public void onError(String code, String error) {

                                                            }

                                                            @Override
                                                            public void onSuccess() {

                                                            }
                                                        });
                                                    }
                                                }
                                                ToastMaker.MakeToast( dep + " Order Cancelled" , act);
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                d.dismiss();
                                            }
                                        }) {
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                Map<String, String> params = new HashMap<String, String>();
                                                params.put("id", String.valueOf(checkOutId));
                                                params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
                                                params.put("dep", dep);
                                                params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
                                                return params;
                                            }
                                        };
                                        Volley.newRequestQueue(act).add(re);
                                    }
                                });
                                d.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {

                        }
                    });

                }
            }
            else
            {
                ToastMaker.MakeToast("This Room Is Vacant" , act);
            }

        }
        catch (Exception e)
        {

        }
        x=0;
    }

    public void getReservation()
    {
        String url = LogIn.URL+"getReservation.php";
        StringRequest getReservationRe = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                if (response.equals("0"))
                {

                }
                else
                {
                    try
                    {
                        JSONArray arr = new JSONArray(response);
                        JSONObject row = arr.getJSONObject(0);
                        THERESERVATION = new RESERVATION(row.getInt("id"),row.getInt("RoomNumber"),row.getInt("ClientId"),row.getInt("Status"),
                                    row.getInt("RoomOrSuite"),row.getInt("MultiRooms"),row.getString("AddRoomNumber"),row.getString("AddRoomId"),row.getString("StartDate"),
                                    row.getInt("Nights"),row.getString("EndDate"),row.getInt("Hotel"),row.getInt("BuildingNo"),row.getInt("Floor"),row.getString("ClientFirstName"),row.getString("ClientLastName"),row.getString("IdType"),
                                    row.getInt("IdNumber"),row.getInt("MobileNumber"),row.getString("Email"),row.getInt("Rating"));
                        TextView fname = (TextView) findViewById(R.id.client_Name);
                        TextView checkin = (TextView) findViewById(R.id.check_In_Date);
                        TextView checkout = (TextView) findViewById(R.id.check_out_Date);
                        fname.setText(THERESERVATION.ClientFirstName + " " + THERESERVATION.ClientLastName);
                        checkin.setText("CheckIn Date: "+THERESERVATION.StartDate);
                        checkout.setText("CheckOut Date: "+THERESERVATION.EndDate);
                    }
                    catch (JSONException e)
                    {
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
                Map<String,String> Params = new HashMap<String,String>();
                Params.put("id", String.valueOf(RESERVATION));
                return Params;
            }
        };
        Volley.newRequestQueue(act).add(getReservationRe);
    }

//-------------------------------------------------------------

    public void sendRegistrationToServer(final String token)
    {
        String url = LogIn.URL+"registToken.php";
        StringRequest r = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                if (response.equals("1"))
                {
                    Log.d(TAG, "Refreshed " );
                }
                else
                {
                    Log.d(TAG, "error " );
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d(TAG, "error " );
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> params = new HashMap<String,String>();
                params.put("token",token);
                params.put("roomNumber",String.valueOf(LogIn.room.getRoomNumber()));
                return params;
            }
        };

        Volley.newRequestQueue(this ).add(r);

    }

//--------------------------------------------------------------
    //Send Cloud Message

    static void sendNotification(final JSONObject notification )
    {

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
        Volley.newRequestQueue(act).add(jsonObjectRequest);

    }

    public static void makemessage(String t ,String Order , boolean addOrRemove)
    {

        String NOTIFICATION_TITLE = Order ;
        String NOTIFICATION_MESSAGE = "" ;
        if (addOrRemove) {
            NOTIFICATION_MESSAGE = "New " + Order + " Order From Room "+LogIn.room.getRoomNumber();
        }
        else {
            NOTIFICATION_MESSAGE = "Cancelled " + Order + " Order From Room "+LogIn.room.getRoomNumber();
        }


        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);
            notifcationBody.put("RoomNumber", LogIn.room.getRoomNumber());
            notification.put("to", t);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {

        }
        sendNotification(notification);
    }

//--------------------------------------------------------------
    // Add & Remove Laundry Orders

    public  void addLaundryOrderInDataBase() //ok
    {
        try
        {
                LoadingDialog loading = new LoadingDialog(act);
                final String dep = "Laundry";
                Calendar x = Calendar.getInstance(Locale.getDefault());
                long timee =  x.getTimeInMillis();
                StringRequest addOrder = new StringRequest(Request.Method.POST, insertServiceOrderUrl , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        loading.stop();
                        if (Integer.parseInt(response) > 0 )
                        {
                            if (THEROOM.getSERVICE_B() != null){
                                if (THEROOM.getSERVICE_B().dps.get("3").toString().equals("false")){
                                    THEROOM.getSERVICE().publishDps("{\"3\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }
                            }
                            for(ServiceEmps emp : Emps) {
                                if (emp.department.equals("Service") || emp.department.equals("Laundry")) {
                                    emp.makemessage(emp.token,"Laundry",true,act);
                                }
                            }
                            LaundryStatus = true ;
                            ToastMaker.MakeToast( dep + " Order Sent Successfully" , act);
                            laundryOrderId = Integer.parseInt(response);
                            myRefLaundry.setValue(timee);
                            myRefdep.setValue(dep);
                            myRefDND.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot)
                                {
                                    if (Long.parseLong(snapshot.getValue().toString()) > 0)
                                    {
                                        myRefDND.setValue(0);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            laundryOn();
                        }
                        else
                            {
                                Toast.makeText(act , response,Toast.LENGTH_LONG).show();
                            }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        loading.stop();
                        //Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError
                    {
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("roomNumber" ,String.valueOf(LogIn.room.getRoomNumber()));
                        params.put("time" ,String.valueOf(timee));
                        params.put("dep" ,dep);
                        params.put("Hotel" ,String.valueOf( LogIn.room.getHotel()));
                        params.put("RorS" ,String.valueOf( RoomOrSuite));
                        params.put("Reservation" ,String.valueOf( RESERVATION));
                        return params;
                    }

                };
                Volley.newRequestQueue(act).add(addOrder);
        }
        catch (Exception e)
        {

        }
    }

    public static  void removeLaundryOrderInDataBase()
    {
        try
        {
                LoadingDialog loading = new LoadingDialog(act);
                myRefLaundry.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (Long.parseLong(snapshot.getValue().toString()) > 0 )
                        {
                            laundryOrderId = Long.parseLong(snapshot.getValue().toString()) ;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                final String dep = "Laundry";
                StringRequest removOrder = new StringRequest(Request.Method.POST, removeServiceOrderUrl , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        loading.stop();
                        if (response.equals("1")  )
                        {
                            if (THEROOM.getSERVICE_B() != null){
                                if (THEROOM.getSERVICE_B().dps.get("3").toString().equals("true"))
                                {
                                    THEROOM.getSERVICE().publishDps("{\"3\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }

                            }
                            for(ServiceEmps emp : Emps) {
                                if (emp.department.equals("Service") || emp.department.equals("Laundry")) {
                                    emp.makemessage(emp.token,"Laundry",false,act);
                                }
                            }
                            LaundryStatus = false ;
                            myRefLaundry.setValue(0);
                            laundryOff();
                            ToastMaker.MakeToast(dep+" Order Cancelled" , act);
                        }
                        else
                        {
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
                        params.put("id" , String.valueOf( laundryOrderId));
                        params.put("room" , String.valueOf( LogIn.room.getRoomNumber()));
                        params.put("dep" , dep);
                        params.put("Hotel" , String.valueOf( LogIn.room.getHotel()));
                        return params;
                    }
                };
                Volley.newRequestQueue(act).add(removOrder);
        }
        catch (Exception e)
        {

        }
    }

//--------------------------------------------------------------
    // Add & Remove Cleanup Orders

    public static void addCleanupOrderInDataBase()
    {
        try
        {
                LoadingDialog loading = new LoadingDialog(act);
                final String dep = "Cleanup";
                Calendar x = Calendar.getInstance(Locale.getDefault());
                long timee =  x.getTimeInMillis();
                CleanupStatus = true ;
                StringRequest addOrder = new StringRequest(Request.Method.POST, insertServiceOrderUrl , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        loading.stop();
                        if (Integer.parseInt(response) > 0 )
                        {
                            if (THEROOM.getSERVICE_B() != null)
                            {
                                if (THEROOM.getSERVICE_B().dps.get("2").toString().equals("false")){
                                    THEROOM.getSERVICE().publishDps("{\"2\":true}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }
                            }
                            for(ServiceEmps emp : Emps) {
                                if (emp.department.equals("Service") || emp.department.equals("Cleanup")) {
                                    emp.makemessage(emp.token,"Cleanup",true,act);
                                }
                            }
                            cleanupOrderId = Integer.parseInt(response);
                            ToastMaker.MakeToast(dep+ " Order Sent Successfully" , act);
                            myRefCleanup.setValue(timee);
                            myRefdep.setValue(dep);
                            myRefDND.addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot)
                                {
                                    if (Long.parseLong(snapshot.getValue().toString()) > 0)
                                    {
                                        myRefDND.setValue(0);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                        else
                        {

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
                        params.put("roomNumber" ,String.valueOf(LogIn.room.getRoomNumber()));
                        params.put("time" ,String.valueOf(timee));
                        params.put( "dep" , dep );
                        params.put("Hotel" ,String.valueOf( LogIn.room.getHotel()));
                        params.put("RorS" ,String.valueOf( RoomOrSuite));
                        params.put("Reservation" ,String.valueOf( RESERVATION));
                        return params;
                    }

                };
                Volley.newRequestQueue(act).add(addOrder);
        }
        catch (Exception e)
        {

        }
    }

    public static void removeCleanupOrderInDataBase()
    {
        try
        {
            LoadingDialog loading = new LoadingDialog(act);
            myRefCleanup.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if (Long.parseLong(snapshot.getValue().toString()) > 0 )
                    {
                        cleanupOrderId = Long.parseLong(snapshot.getValue().toString()) ;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            final String dep = "Cleanup";
            CleanupStatus = false ;
            StringRequest removOrder = new StringRequest(Request.Method.POST, removeServiceOrderUrl , new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    if (response.equals("1")  )
                    {
                        if (THEROOM.getSERVICE_B() != null)
                        {
                            if (THEROOM.getSERVICE_B().dps.get("2").toString().equals("true")){
                                THEROOM.getSERVICE().publishDps("{\"2\":false}", new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }
                        }
                        for(ServiceEmps emp : Emps) {
                            if (emp.department.equals("Service") || emp.department.equals("Cleanup")) {
                                emp.makemessage(emp.token,"Cleanup",false,act);
                            }
                        }
                        myRefCleanup.setValue(0);
                        ToastMaker.MakeToast( dep+" Order Cancelled" , act);
                    }
                    else
                    {
                        //Toast.makeText(act , response,Toast.LENGTH_LONG).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    loading.stop();
                    // Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("id" ,String.valueOf( cleanupOrderId));
                    params.put("room" , String.valueOf( LogIn.room.getRoomNumber()));
                    params.put("dep" , dep);
                    params.put("Hotel" , String.valueOf( LogIn.room.getHotel()));
                    return params;
                }
            };
            Volley.newRequestQueue(act).add(removOrder);
        }
        catch (Exception e )
        {

        }
    }

//--------------------------------------------------------------
    // Add & Remove RoomService Orders

    public static void addRoomServiceOrderInDataBase()
    {
        try
        {
            final String dep = "RoomService";
            final Dialog d = new Dialog(act);
            View v = LayoutInflater.from(act).inflate(R.layout.room_service_dialog , null);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            v.setMinimumHeight(height);
            v.setMinimumWidth(width);
            d.setContentView(v);
            final EditText ordereditetext = (EditText) d.findViewById(R.id.RoomServiceDialog_Text);
            Button cancel = (Button) d.findViewById(R.id.RoomServiceDialog_Cancel);
            final String[] xxx = new String[] {"","","","",""};
            final CheckBox slippers = (CheckBox) d.findViewById(R.id.checkBox_slippers);
            final CheckBox towels = (CheckBox) d.findViewById(R.id.checkBox_towels);
            final CheckBox minibar = (CheckBox) d.findViewById(R.id.checkBox_minibar);
            final CheckBox bath = (CheckBox) d.findViewById(R.id.checkBox_bath);
            final CheckBox other = (CheckBox) d.findViewById(R.id.checkBox_others);
            slippers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (slippers.isChecked())
                    {
                        xxx[0] = "Slipper";
                    }
                    else
                    {
                        xxx[0] = "";
                    }
                }
            });
            towels.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (towels.isChecked())
                    {
                        xxx[1] = "Towels";
                    }
                    else
                    {
                        xxx[1] = "";
                    }
                }
            });
            minibar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (minibar.isChecked())
                    {
                        xxx[2] = "Mini Bar";
                    }
                    else
                    {
                        xxx[2] = "";
                    }
                }
            });
            bath.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(bath.isChecked())
                    {
                        xxx[3] = "BathSet";
                    }
                    else
                    {
                        xxx[3] = "";
                    }
                }
            });
            other.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
                }
            });
            ordereditetext.setVisibility(View.INVISIBLE);
            d.show();
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });
            Button ok = (Button) d.findViewById(R.id.RoomServiceDialog_OK);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    LoadingDialog loading = new LoadingDialog(act);
                    for (int i =0 ; i<xxx.length;i++)
                    {
                        if (!xxx[i].equals("") && !xxx[i].equals("Other"))
                        {
                            if (roomServiceOrder.equals(""))
                            {
                                roomServiceOrder = xxx[i];
                            }
                            else
                            {
                                roomServiceOrder = roomServiceOrder + "-"+ xxx[i];
                            }

                        }else if (xxx[i].equals("Other"))
                        {
                            if (roomServiceOrder.equals(""))
                            {
                                roomServiceOrder = ordereditetext.getText().toString();
                            }
                            else
                            {
                                roomServiceOrder = roomServiceOrder + "-" + ordereditetext.getText().toString();
                            }
                        }
                    }

                    if (roomServiceOrder.length() > 0)
                    {
                        Calendar x = Calendar.getInstance(Locale.getDefault());
                        long timee =  x.getTimeInMillis();

                        StringRequest request = new StringRequest(Request.Method.POST, roomServiceOrderUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response)
                            {
                                loading.stop();
                                if (response.equals("0"))
                                {

                                }
                                else
                                {
                                    roomServiceOrderId = Integer.parseInt(response);
                                    RoomServiceStatus = true;
                                    myRefRoomService.setValue(timee);
                                    myRefRoomServiceText.setValue(roomServiceOrder);
                                    roomServiceOrder = "";
                                    myRefdep.setValue(dep);
                                    myRefDND.addListenerForSingleValueEvent(new ValueEventListener()
                                    {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot)
                                        {
                                            if (Long.parseLong(snapshot.getValue().toString()) > 0)
                                            {
                                                myRefDND.setValue(0);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error)
                                        {

                                        }
                                    });
                                    ToastMaker.MakeToast(dep+ " Order Sent Successfully" , act);
                                    for(ServiceEmps emp : Emps) {
                                        if (emp.department.equals("Service") || emp.department.equals("RoomService")) {
                                            emp.makemessage(emp.token,"RoomService",true,act);
                                        }
                                    }
                                    d.dismiss();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                loading.stop();
                                Toast.makeText(act , error.getMessage() , Toast.LENGTH_LONG).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("roomNumber", String.valueOf(LogIn.room.getRoomNumber()));
                                params.put("time", String.valueOf(timee));
                                params.put("dep", "RoomService");
                                params.put("order", roomServiceOrder);
                                params.put("Hotel" ,String.valueOf( LogIn.room.getHotel()));
                                params.put("RorS" ,String.valueOf( RoomOrSuite));
                                params.put("Reservation" ,String.valueOf( RESERVATION));
                                return params;
                            }
                        };
                        Volley.newRequestQueue(act).add(request);
                    }
                    else
                    {
                        ToastMaker.MakeToast("Please Enter Your Order" , act);
                    }
                }

            });
        }
        catch (Exception e)
        {

        }
    }

    public static void removeRoomServiceOrderInDataBase()
    {
        try
        {
            LoadingDialog loading = new LoadingDialog(act);
            final String dep = "RoomService";
            myRefRoomService.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if ( !snapshot.getValue().toString().equals("0") )
                    {
                        roomServiceOrderId = Long.parseLong(snapshot.getValue().toString()) ;
                    }
                    StringRequest removOrder = new StringRequest(Request.Method.POST, removeServiceOrderUrl , new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {
                            if (response.equals("1")  )
                            {
                                loading.stop();
                                //d.dismiss();
                                myRefRoomService.setValue(0);
                                myRefRoomServiceText.setValue("0");
                                ToastMaker.MakeToast( dep + " Order Cancelled" , act);
                                Calendar x = Calendar.getInstance(Locale.getDefault());
                                long time =  x.getTimeInMillis();
                                RoomServiceStatus = false ;
                                for(ServiceEmps emp : Emps) {
                                    if (emp.department.equals("Service") || emp.department.equals("RoomService")) {
                                        emp.makemessage(emp.token,"RoomService",false,act);
                                    }
                                }
                            }
                            else
                            {
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            // Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError
                        {
                            Map<String,String> params = new HashMap<String, String>();
                            params.put("id" , String.valueOf( roomServiceOrderId));
                            params.put("room" , String.valueOf( LogIn.room.getRoomNumber()));
                            params.put("dep" , dep);
                            params.put("Hotel" , String.valueOf( LogIn.room.getHotel()));
                            return params;
                        }
                    };
                    Volley.newRequestQueue(act).add(removOrder);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e)
        {

        }
    }

//--------------------------------------------------------------

    static void finishCleanup()
    {
        //Toast.makeText(act , "done",Toast.LENGTH_LONG).show();
        myRefCleanup.setValue(0);
        CleanupStatus = false ;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run()
            {
                //requestCleanup.setBackgroundResource(R.drawable.cleanup_butn);
                //requestCleanup.setTextColor(Color.parseColor("#FFE083") );
                //messageDialog d = new messageDialog("تم تنفيذ طلب تنظيف الغرفة","تاكيد"  ,act);
                ToastMaker.MakeToast("تم تنفيذ طلب تنظيف الغرفة" , act );
            }
        });

    }

    static void finishLaundry()
    {

        myRefLaundry.setValue(0);
        LaundryStatus = false ;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run()
            {
                //gotolaundry.setBackgroundResource(R.drawable.laundry_butn);
                //gotolaundry.setTextColor(Color.parseColor("#FFE083") );
                //messageDialog d = new messageDialog("تم تنفيذ طلب المغسلة","تاكيد"  ,act);
                ToastMaker.MakeToast("تم تنفيذ طلب المغسلة" , act );
                }
            });

    }

    static void finishRoomService()
    {

        myRefRoomService.setValue(0);
        RoomServiceStatus = false ;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run()
            {
                //roomServiceOff();
                //messageDialog d = new messageDialog("تم تنفيذ طلب خدمة الغرف","تاكيد"  ,act);
                ToastMaker.MakeToast("تم تنفيذ طلب خدمة الغرف" , act );
            }
        });

    }

    void finishSOS()
    {

        myRefSos.setValue(0);
        SosStatus = false ;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run()
            {
                SOSImage.setImageResource(R.drawable.union_2);
                SOSText.setTextColor( getResources().getColor(R.color.light_blue_A200)) ;
                SOSIcon.setVisibility(View.GONE);
               // messageDialog d = new messageDialog("تم تنفيذ طلب الطوارئ","تاكيد"  ,act);
                ToastMaker.MakeToast("تم تنفيذ طلب الطوارئ" ,act);
            }
        });

    }

    static void finishRestaurant()
    {
        myRefRestaurant.setValue(0);
        RestaurantStatus = false ;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run()
            {
                //RestaurantBtn.setBackgroundResource(R.drawable.restaurant_butn);
                //RestaurantBtn.setTextColor(Color.parseColor("#FFE083") );
                // messageDialog d = new messageDialog("تم تنفيذ طلب الطوارئ","تاكيد"  ,act);
                restaurantOff();
                ToastMaker.MakeToast("تم تنفيذ طلب المطعم" ,act);
            }
        });
    }

    static void finishCheckout()
    {
        myRefCheckout.setValue(0);
        CheckoutStatus = false ;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run()
            {
                //checkOut.setBackgroundResource(R.drawable.checkout_btn);
                //RestaurantBtn.setTextColor(Color.parseColor("#FFE083") );
                // messageDialog d = new messageDialog("تم تنفيذ طلب الطوارئ","تاكيد"  ,act);
                ToastMaker.MakeToast("تم تنفيذ طلب CheckOut" ,act);

            }
        });
    }

    static void openMessageDialog(final String message)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run()
            {
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

    public static void PowerOff()
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run()
            {
                if (THEROOM.getPOWER() != null )
                {
                    THEROOM.getPOWER().publishDps("{\"1\": false}", new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {
                            Toast.makeText(act, "turn on the light failure", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess() {
                            Toast.makeText(act, "turn Off 1 success "+THEROOM.RoomNumber, Toast.LENGTH_SHORT).show();
                            //myRefPower.setValue(0);
                        }
                    });
                    THEROOM.getPOWER().publishDps("{\"2\": false}", new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {
                            Toast.makeText(act, "turn on the light failure", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess()
                        {
                            Toast.makeText(act, "turn Off 2 success "+THEROOM.RoomNumber, Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }
        });
    }

    public static void PowerOn()
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run()
            {
                if (THEROOM.getPOWER() != null )
                {
                    THEROOM.getPOWER().publishDps("{\"1\": true}", new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {
                            Toast.makeText(act, "turn on the light failure", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess() {
                            Toast.makeText(act, "turn on 1 success "+THEROOM.RoomNumber, Toast.LENGTH_SHORT).show();
                            //myRefPower.setValue(0);
                        }
                    });
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
                }

            }
        });
    }

    public static void CheckIn()
    {
        String Duration = "" ;
        if (checkInModeTime != 0)
        {
            Duration = String.valueOf(checkInModeTime*60) ;
        }
        else {
            Duration = "60" ;
        }
        Log.d("checkoutDuration" , Duration);
        if (PowerControllerStatus)
        {
            String finalDuration = Duration;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Log.d("LightWithWelcome" , THEROOM.getPOWER_B().dps.toString());
                    if (THEROOM.getPOWER() != null )
                    {
                        THEROOM.getPOWER().publishDps("{\"1\": true}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {
                                //Toast.makeText(act, error, Toast.LENGTH_SHORT).show();
                                Log.e("light", error);
                            }

                            @Override
                            public void onSuccess() {
                                //Toast.makeText(act, "turn on the light success", Toast.LENGTH_SHORT).show();
                                //myRefPower.setValue(1);
                            }
                        });
                        THEROOM.getPOWER().publishDps("{\"2\": true}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {
                                //Toast.makeText(act, "turn on the light failure", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess() {
                                //Toast.makeText(act, "turn on the light success", Toast.LENGTH_SHORT).show();
                            }
                        });
                        THEROOM.getPOWER().publishDps("{\"8\": "+ finalDuration +"}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {
                                //Toast.makeText(act, "turn on the light failure", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess() {
                                //Toast.makeText(act, "turn on the light success", Toast.LENGTH_SHORT).show();
                                Log.d("LightWithWelcome" , "countdoun");
                            }
                        });
                    }
                    final long[] tt = {0};
                    long xx = Integer.parseInt( finalDuration ) ;
                    if (Switch1 != null ){
                        Handler H = new Handler();
                        Runnable d = new Runnable() {
                            @Override
                            public void run() {
                                tt[0] = tt[0] +1000 ;
                                H.postDelayed(this,1000);
                                Log.d("LightWithWelcome" , tt[0]+" "+(xx*1000));
                                if (tt[0] >= (xx*1000)){
                                    Switch1.publishDps("{\"1\": false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {
                                            Log.d("LightWithWelcome" , error);
                                        }

                                        @Override
                                        public void onSuccess() {
                                            Log.d("LightWithWelcome" , "Light is off ");
                                        }
                                    });
                                    H.removeCallbacks(this);
                                }

                            }
                        } ;

                        Switch1.publishDps("{\"1\": true}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {
                                Log.d("LightWithWelcome" , error);
                            }

                            @Override
                            public void onSuccess() {
                                Log.d("LightWithWelcome" , "Light is on "+finalDuration);
                                d.run();
                            }
                        });


                    }
                }
            });
        }

    }

    void checkOut()
    {
        String Duration = "" ;
        if (checkOutModeTime != 0 )
        {
            Duration = String.valueOf(checkInModeTime*60);
        }
        else
        {
            Duration = "60" ;
        }
        Log.d("checkoutDuration" , Duration);

        if (THEROOM.getPOWER_B() != null){
            THEROOM.getPOWER().publishDps("{\"7\": "+Duration+"}", new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    //Toast.makeText(act, "turn on the light failure", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess() {
                    //Toast.makeText(act, "turn on the light success", Toast.LENGTH_SHORT).show();
                    Log.d("LightWithWelcome" , "countdoun");
                    THEROOM.getPOWER().publishDps("{\"2\": false}", new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {
                            //Toast.makeText(act, "turn on the light failure", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess() {
                            //Toast.makeText(act, "turn on the light success", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

//--------------------------------------------------------------
    // open room door

    public static void OpenTheDoor(View view)
    {
        mediaPlayer.start();
        if(myTestLockEKey == null)
        {
            ToastMaker.MakeToast(" you should get your key list first " , act);
            return;
        }
        final Dialog d = new Dialog(act);
        d.setContentView(R.layout.loading_layout);
        TextView t = (TextView) d.findViewById(R.id.textViewdfsdf);
        t.setText("Door Opening");
        d.setCancelable(false);
        d.show();
        ensureBluetoothIsEnabled();
        //showConnectLockToast();
        TTLockClient.getDefault().controlLock(ControlAction.UNLOCK, myTestLockEKey.getLockData(), myTestLockEKey.getLockMac(),new ControlLockCallback()
        {
            @Override
            public void onControlLockSuccess(ControlLockResult controlLockResult) {
                //Toast.makeText(act,"lock is unlock  success!",Toast.LENGTH_LONG).show();
                d.dismiss();
                ToastMaker.MakeToast("Door Opened",act);
            }

            @Override
            public void onFail(LockError error) {
               // Toast.makeText(UnlockActivity.this,"unLock fail!--" + error.getDescription(),Toast.LENGTH_LONG).show();
                d.dismiss();
                ToastMaker.MakeToast("Open Fail!  "+error,act);
            }
        });

    }

    public static void OpenDoorAndSaveIt (View view){

        mediaPlayer.start();
        final LoadingDialog dd = new LoadingDialog(act);
        StringRequest request = new StringRequest(Request.Method.POST, registerDoorOpenUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("registerOpen" , response);
                if (response.equals("1")){
                    TTLockClient.getDefault().controlLock(ControlAction.UNLOCK, myTestLockEKey.getLockData(), myTestLockEKey.getLockMac(),new ControlLockCallback()
                    {
                        @Override
                        public void onControlLockSuccess(ControlLockResult controlLockResult) {
                            dd.stop();
                            Log.d("registerOpen" , "opened");
                            //
                            // Toast.makeText(holder.itemView.getContext(),"Room "+list.get(position).RoomNumber+" Door Opened" , Toast.LENGTH_LONG);
                           // messageDialog m = new messageDialog("Room "+THEROOM.RoomNumber+" Door Opened","Door Opened",act);
                            ToastMaker.MakeToast("Door Opened",act);
                        }

                        @Override
                        public void onFail(LockError error) {
                            dd.stop();
                            Log.d("registerOpen" , error.getErrorMsg());
                            //d.dismiss();
                            //Toast.makeText(holder.itemView.getContext(),error.getErrorMsg() , Toast.LENGTH_LONG);
                            //messageDialog m = new messageDialog("Room "+THEROOM.RoomNumber+" Door Open Failed .. Try to be Closer","Door Open Failed",act);
                            ToastMaker.MakeToast("Open Fail!  "+error,act);
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dd.stop();
                Toast.makeText(act,error.getMessage() , Toast.LENGTH_LONG);
                Log.d("registerOpen" , error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Calendar c = Calendar.getInstance(Locale.getDefault());
                String Date = c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DAY_OF_MONTH);
                String Time = c.get(Calendar.HOUR)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND);
                Map<String,String> par = new HashMap<String, String>();
                par.put("EmpID" , "0");
                par.put("JNum" , "0");
                String name = "";
                if (THERESERVATION != null ){
                    name = THERESERVATION.ClientFirstName+" "+THERESERVATION.ClientLastName ;
                }
                else {
                    name = "Client" ;
                }
                par.put("Name" , name);
                par.put("Department" , "Client");
                par.put("Room" , String.valueOf(THEROOM.RoomNumber));
                par.put("Date" , Date);
                par.put("Time" , Time);
                return par;
            }
        };
        Volley.newRequestQueue(act).add(request);
    }

//--------------------------------------------------------------

    public void setButtons()
    {
        FirebaseMessaging.getInstance().subscribeToTopic("RoomNumber"+LogIn.room.getRoomNumber());
        myRefLaundry.addListenerForSingleValueEvent(new ValueEventListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                try
                {
                    String s = dataSnapshot.getValue().toString();
                    if ( Long.parseLong(s) > 0 )
                    {
                        LaundryStatus = true ;
                        laundryOn();
                    }
                }
                catch (Exception e)
                {
                    Log.e("laundry" , e.getMessage());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myRefCleanup.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                try
                {
                    String s = dataSnapshot.getValue().toString();
                    if ( Long.parseLong(s) > 0 )
                    {
                        CleanupStatus = true ;
                        cleanupOn();
                    }
                }
                catch (Exception e )
                {
                    Log.e("cleanup" , e.getMessage());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                //Toast.makeText(act , databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        myRefRoomService.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                try
                {
                    String s = dataSnapshot.getValue().toString();
                    if ( Long.parseLong(s) > 0 )
                    {
                        RoomServiceStatus = true ;
                        roomServiceOn();
                    }
                }
                catch (Exception e)
                {
                    Log.e("roomservice" , e.getMessage());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                //Toast.makeText(act , databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        myRefSos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                try
                {
                    Log.e("SOS" , dataSnapshot.getValue().toString() );
                    String s = dataSnapshot.getValue().toString();
                    if ( Long.parseLong(s) > 0 )
                    {
                        SosStatus = true ;
                        sosOn();
                    }
                }
                catch (Exception e )
                {
                    Log.e("SOS" , e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                //Toast.makeText(act , databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        myRefRestaurant.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                try
                {
                    String s = dataSnapshot.getValue().toString();
                    if ( Long.parseLong(s) > 0 )
                    {
                        RestaurantStatus = true ;
                        //RestaurantBtn.setBackgroundResource(R.drawable.restaurant_icon2);

                    }
                }
                catch (Exception e)
                {
                    Log.e("Restaurant" , e.getMessage() );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                //Toast.makeText(act , databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        myRefCheckout.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                try
                {
                    String s = dataSnapshot.getValue().toString();
                    if ( Long.parseLong(s) > 0 )
                    {
                        CheckoutStatus = true ;
                        checkoutOn();
                    }
                }
                catch (Exception e)
                {
                    Log.e("checkout" , e.getMessage());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                //Toast.makeText(act , databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        myRefDND.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                String s = snapshot.getValue().toString();
                if ( Long.parseLong(s) > 0 )
                {
                    DNDStatus = true ;
                    dndOn();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void blink()
    {
        final Calendar x = Calendar.getInstance(Locale.getDefault());
        final Handler hander = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                        //hideSystemUI();
                        blink();
                    }
                });
            }
        }).start();
    }

    private void KeepScreenFull()
    {
        final Calendar x = Calendar.getInstance(Locale.getDefault());
        final Handler hander = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
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

    public static void ensureBluetoothIsEnabled()
    {
        if(!TTLockClient.getDefault().isBLEEnabled(act)){
            TTLockClient.getDefault().requestBleEnable(act);
        }
    }

    private void getHotelTempSetpoint()
    {
        String url = LogIn.URL+"getTempSetPointAndroid.php";
        StringRequest re = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                //ToastMaker.MakeToast(response , act);
                String temp ="";
                try
                {
                    JSONObject row = new JSONObject(response);
                    if ( !row.getString("Temp").equals("0"))
                    {
                        temp = row.getString("Temp") ;
                        if (temp.length()==2)
                        {
                            temp = temp+"0";
                            TempSetPoint = temp ;
                        }
                        else if (temp.length()>2)
                        {
                            TempSetPoint = temp ;
                        }
                    }
                    if (!row.getString("Logo").equals("0")){
                        ImageView logo = (ImageView) findViewById(R.id.HotelLogo);
                        Picasso.get().load(row.getString("Logo")).resize(145,70).into(logo);
                        LOGO = row.getString("Logo") ;
                    }
                    if (row.getInt("CheckInModeTime") != 0 )
                    {
                        checkInModeTime = row.getInt("CheckInModeTime") ;
                    }
                    if (row.getInt("CheckOutModeTime") != 0 )
                    {
                        checkOutModeTime = row.getInt("CheckOutModeTime");
                    }
                    Log.d("Duration" , "checkin "+checkInModeTime+" checkout "+checkOutModeTime);

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                //Toast.makeText(act,temp+" "+TempSetPoint,Toast.LENGTH_LONG).show();
                Log.d("tempSetPoint",temp+" "+TempSetPoint);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> pars = new HashMap<String, String>();
                pars.put("Hotel",String.valueOf(LogIn.room.getHotel()));
                return pars;
            }
        };
        Volley.newRequestQueue(act).add(re);
    }

    public void showConnectLockToast()
    {
       // ToastMaker.MakeToast("start connect lock...",act);
        Dialog d = new Dialog(act);
        d.setContentView(R.layout.loading_layout);
        TextView t = (TextView) d.findViewById(R.id.textViewdfsdf);
        t.setText("Door Opening");
        d.setCancelable(false);
        d.show();
    }

    public void logout()
    {
        try
        {
            String url = LogIn.URL+"logOutRoomTablet.php" ;
            StringRequest logoutRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    //ToastMaker.MakeToast(response,act);
                    if (response.equals("1"))
                    {
                        LogIn.room.logout();
                        myRefStatus.setValue(0);
                        Intent i = new Intent(act , LogIn.class);
                        startActivity(i);
                        act.finish();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("id" ,String.valueOf( ID));
                    return params;
                }
            };
            Volley.newRequestQueue(act).add(logoutRequest);
        }
        catch (Exception e )
        {

        }

    }

    void setTuyaApplication()
    {
        TuyaHomeSdk.setDebugMode(true);
        TuyaHomeSdk.init(getApplication());
        TuyaHomeSdk.setOnNeedLoginListener(new INeedLoginListener() {
            @Override
            public void onNeedLogin(Context context) {
                Intent intent = new Intent(context, Tuya_Login.class);
                if (!(context instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(intent);
            }
        });
       // FrescoManager.initFresco(this);
    }

    public void s1click(View view)
    {
        lightPlayer.start();
        if (Switch1Bean != null )
        {
            if (Switch1Bean.dps.get("1") != null){

                if (Switch1Bean.dps.get("1").toString().equals("false"))
                {
                    try
                    {
                        Switch1.publishDps("{\" 1\":true}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    }
                    catch (Exception e)
                    {

                    }

                }
                else
                {
                    try
                    {
                        Switch1.publishDps("{\" 1\":false}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    }
                    catch (Exception e)
                    {

                    }

                }
            }

        }
        x=0;
    }

    public void s2click(View view)
    {
        lightPlayer.start();
        if (Switch1Bean != null )
        {
            if (Switch1Bean.dps.get("2") != null){

                if (Switch1Bean.dps.get("2").toString().equals("false"))
                {
                    try
                    {
                        Switch1.publishDps("{\" 2\":true}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    }
                    catch (Exception e)
                    {

                    }

                }
                else
                {
                    try
                    {
                        Switch1.publishDps("{\" 2\":false}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    }
                    catch (Exception e)
                    {

                    }

                }
            }

        }
        x=0 ;
    }

    public void s3click(View view)
    {
//        lightPlayer.start();
//        if (Switch1Bean != null )
//        {
//            Log.d("btn3Problem" , "iam not null");
//            if (Switch1Bean.dps.get("3") != null){
//                Log.d("btn3Problem" , "3 not null");
//                if (Switch1Bean.dps.get("3").toString().equals("false"))
//                {
//                    try
//                    {
//                        Switch1.publishDps("{\" 3\":true}", new IResultCallback() {
//                            @Override
//                            public void onError(String code, String error) {
//                                //ToastMaker.MakeToast(error + " "+ code ,act);
//                            }
//
//                            @Override
//                            public void onSuccess() {
//                                //ToastMaker.MakeToast("sent",act);
//                            }
//                        });
//                    }
//                    catch (Exception e)
//                    {
//
//                    }
//
//                }
//                else
//                {
//                    try
//                    {
//                        Switch1.publishDps("{\" 3\":false}", new IResultCallback() {
//                            @Override
//                            public void onError(String code, String error) {
//                                //ToastMaker.MakeToast(error + " "+ code ,act);
//                            }
//
//                            @Override
//                            public void onSuccess() {
//                                //ToastMaker.MakeToast("sent",act);
//                            }
//                        });
//                    }
//                    catch (Exception e)
//                    {
//
//                    }
//
//                }
//            }
//
//        }
//        x=0;
    }

    public void s4click(View view)
    {
        lightPlayer.start();
        if (Switch1Bean != null )
        {
            if (Switch1Bean.dps.get("4") != null){

                if (Switch1Bean.dps.get("4").toString().equals("false"))
                {
                    try
                    {
                        Switch1.publishDps("{\" 4\":true}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    }
                    catch (Exception e)
                    {

                    }

                }
                else
                {
                    try
                    {
                        Switch1.publishDps("{\" 4\":false}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    }
                    catch (Exception e)
                    {

                    }

                }
            }

        }
        x=0;
    }

    public void s5click(View view)
    {
        if (Switch2Bean != null )
        {
            if (Switch2Bean.dps.get("1") != null ){

                if (Switch2Bean.dps.get("1").toString().equals("false"))
                {
                    try
                    {
                        Switch2.publishDps("{\" 1\":true}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    }
                    catch (Exception e)
                    {

                    }

                }
                else
                {
                    try
                    {
                        Switch2.publishDps("{\" 1\":false}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    }
                    catch (Exception e)
                    {

                    }

                }
            }

        }

    }

    public void s6click(View view)
    {
        if (Switch2Bean != null )
        {
            if (Switch2Bean.dps.get("2") != null ){

                if (Switch2Bean.dps.get("2").toString().equals("false"))
                {
                    try
                    {
                        Switch2.publishDps("{\" 2\":true}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    }
                    catch (Exception e)
                    {

                    }

                }
                else
                {
                    try
                    {
                        Switch2.publishDps("{\" 2\":false}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    }
                    catch (Exception e)
                    {

                    }

                }
            }

        }

    }

    public void s7click(View view)
    {
        if (Switch2Bean != null )
        {
            if (Switch2Bean.dps.get("2") != null ){

                if (Switch2Bean.dps.get("3").toString().equals("false"))
                {
                    try
                    {
                        Switch2.publishDps("{\" 3\":true}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    }
                    catch (Exception e)
                    {

                    }

                }
                else
                {
                    try
                    {
                        Switch2.publishDps("{\" 3\":false}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    }
                    catch (Exception e)
                    {

                    }

                }
            }

        }

    }

    public void s8click(View view)
    {
        if (Switch2Bean != null )
        {
            if (Switch2Bean.dps.get("2") != null ){

                if (Switch2Bean.dps.get("4").toString().equals("false"))
                {
                    try
                    {
                        Switch2.publishDps("{\" 4\":true}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    }
                    catch (Exception e)
                    {

                    }

                }
                else
                {
                    try
                    {
                        Switch2.publishDps("{\" 4\":false}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    }
                    catch (Exception e)
                    {

                    }

                }
            }

        }

    }

    public static void setDND(View view)
    {
        mediaPlayer.start();
        String dep = "DND";
        Calendar x = Calendar.getInstance(Locale.getDefault());
        long timee =  x.getTimeInMillis();

        if (CURRENT_ROOM_STATUS == 2 )
        {
            if (!DNDStatus) {
                Dialog d = new Dialog(act);
                d.setContentView(R.layout.confermation_dialog);
                d.setCancelable(false);
                TextView head = (TextView) d.findViewById(R.id.textView2);
                head.setText("Turn On (Don't Disturb) Mood");
                TextView text = (TextView) d.findViewById(R.id.confermationDialog_Text);
                text.setText("Do You Want To Turn On (Don't Disturb) Mood");
                Button cancel = (Button) d.findViewById(R.id.confermationDialog_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                Button ok = (Button) d.findViewById(R.id.messageDialog_ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                        LoadingDialog loading = new LoadingDialog(act);
                        StringRequest request = new StringRequest(Request.Method.POST, insertServiceOrderUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (THEROOM.getSERVICE_B() != null ){
                                    Log.d("serviceSwitch" , "not null");
                                    Log.d("serviceSwitch" , THEROOM.getSERVICE_B().dps.toString());

                                    if (THEROOM.getSERVICE_B().dps.get("1").toString().equals("false")){

                                        THEROOM.getSERVICE().publishDps("{\"1\":true}", new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                Log.d("serviceSwitch" , error);
                                            }

                                            @Override
                                            public void onSuccess() {
                                                Log.d("serviceSwitch" , "success");
                                            }
                                        });
                                    }else {Log.d("serviceSwitch" , "is null");}

                                }
                                if (CleanupStatus){
                                    removeCleanupOrderInDataBase();
                                }
                                if (LaundryStatus){
                                    removeLaundryOrderInDataBase();
                                }
                                if (RoomServiceStatus){
                                    removeRoomServiceOrderInDataBase();
                                }
                                try {
                                    Log.e("DND", response);
                                    if (Integer.parseInt(response) > 0) {
                                        loading.stop();
                                        dndId = Integer.parseInt(response);
                                        DNDStatus = true;
                                        myRefDND.setValue(timee);
                                        myRefdep.setValue("DND");
                                        //dnd.setBackgroundResource(R.drawable.dnd_on0);
                                        dndOff();
                                    }
                                } catch (Exception e) {
                                    Log.e("DND", e.getMessage());
                                }
                                for(ServiceEmps emp : Emps) {
                                    if (emp.department.equals("Service") || emp.department.equals("Cleanup") || emp.department.equals("Laundry") || emp.department.equals("RoomService")) {
                                        emp.makemessage(emp.token,"DND",true,act);
                                    }
                                }
                            }
                        }
                                , new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("DNDerror", error.getMessage());
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("roomNumber", String.valueOf(LogIn.room.getRoomNumber()));
                                params.put("time", String.valueOf(timee));
                                params.put("dep", dep);
                                params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
                                params.put("RorS", String.valueOf(RoomOrSuite));
                                params.put("Reservation", String.valueOf(RESERVATION));
                                return params;
                            }
                        };
                        Volley.newRequestQueue(act).add(request);
                    }
                });
                d.show();

            } else {
                Dialog d = new Dialog(act);
                d.setContentView(R.layout.confermation_dialog);
                d.setCancelable(false);
                TextView head = (TextView) d.findViewById(R.id.textView2);
                head.setText("Turn Off (Don't Disturb) Mood");
                TextView text = (TextView) d.findViewById(R.id.confermationDialog_Text);
                text.setText("Do You Want To Turn Off (Don't Disturb) Mood");
                Button cancel = (Button) d.findViewById(R.id.confermationDialog_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                Button ok = (Button) d.findViewById(R.id.messageDialog_ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                        LoadingDialog loading = new LoadingDialog(act);
                        StringRequest rrr = new StringRequest(Request.Method.POST, removeServiceOrderUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(THEROOM.getSERVICE_B() != null ){
                                    if (THEROOM.getSERVICE_B().dps.get("1").toString().equals("true")){
                                        THEROOM.getSERVICE().publishDps("{\"1\":false}", new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {

                                            }

                                            @Override
                                            public void onSuccess() {

                                            }
                                        });
                                    }else {Log.d("serviceSwitch" , "is null");}
                                }
                                if (response.equals("1")) {
                                    loading.stop();
                                    DNDStatus = false;
                                    myRefDND.setValue(0);
                                    //dnd.setBackgroundResource(R.drawable.dnd_off0);
                                    dndOff();
                                }
                                for(ServiceEmps emp : Emps) {
                                    if (emp.department.equals("Service") || emp.department.equals("Cleanup") || emp.department.equals("Laundry")) {
                                        emp.makemessage(emp.token,"DND",false,act);
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("id", String.valueOf(dndId));
                                params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
                                params.put("dep", dep);
                                params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
                                return params;
                            }
                        };
                        Volley.newRequestQueue(act).add(rrr);
                    }
                });
                d.show();

            }
        }
        else
        {
            ToastMaker.MakeToast("This Room Is Vacant" , act);
        }
    }

    void setTabValue(String status)
    {
        try
        {
            String url = LogIn.URL+"setTabletStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    if (response.equals("1"))
                    {
                        myRefTabStatus.setValue(status);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
                    Params.put("id",String.valueOf(ID) );
                    Params.put("value" , status);
                    return Params;
                }
            };
            Volley.newRequestQueue(act).add(tabR);
        }
        catch (Exception e )
        {
        }
    }

    void setThermostatStatus(String status)
    {
        try
        {
            String url = LogIn.URL+"setThermostatStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    if (response.equals("1"))
                    {
                        myRefThermostat.setValue(status);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
                    Params.put("id",String.valueOf(ID) );
                    Params.put("value" , status);
                    return Params;
                }
            };
            Volley.newRequestQueue(act).add(tabR);
        }
        catch (Exception e)
        {

        }
    }

    void setLockStatus(String status)
    {
        try
        {
            String url = LogIn.URL+"setLockStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    //ToastMaker.MakeToast(ID+"lock "+ status+" " + response ,act);
                    Log.e("lock" , response);
                    if (response.equals("1"))
                    {
                        myRefLock.setValue(status);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
                    Params.put("id", String.valueOf(ID));
                    Params.put("value" , status);
                    return Params;
                }
            };
            Volley.newRequestQueue(act).add(tabR);
        }
        catch (Exception e)
        {

        }
    }

    void setDoorSensorStatus(String status)
    {
        try
        {
            String url = LogIn.URL+"setDoorSensorStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    //ToastMaker.MakeToast("Door "+status+"   " + response , act);
                    if (response.equals("1"))
                    {
                        myRefDoorSensor.setValue(status);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
                    Params.put("id",String.valueOf(ID) );
                    Params.put("value" , status);
                    return Params;
                }
            };
            Volley.newRequestQueue(act).add(tabR);
        }
        catch (Exception e)
        {

        }
    }

    void setDoorOpenClosed(String status)
    {
        try
        {
            String url = LogIn.URL+"setDoorOpenClosed.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    Log.e("power " , response +" " + status);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
                    Params.put("id",String.valueOf(ID) );
                    Params.put("value" , status);
                    return Params;
                }
            };
            Volley.newRequestQueue(act).add(tabR);
        }
        catch (Exception e)
        {

        }
    }

    void setMotionSensorStatus(String status)
    {
        try
        {
            String url = LogIn.URL+"setMotionSensorStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    if (response.equals("1"))
                    {
                        myRefMotionSensor.setValue(status);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
                    Params.put("id",String.valueOf(ID) );
                    Params.put("value" , status);
                    return Params;
                }
            };
            Volley.newRequestQueue(act).add(tabR);
        }
        catch (Exception e)
        {

        }
    }

    void setCurtainSwitchStatus(String status)
    {
        try
        {
            String url = LogIn.URL+"setCurtainSwitchStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    if (response.equals("1"))
                    {
                        myRefCurtainSwitch.setValue(status);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
                    Params.put("id",String.valueOf(ID) );
                    Params.put("value" , status);
                    return Params;
                }
            };
            Volley.newRequestQueue(act).add(tabR);
        }
        catch (Exception e)
        {

        }
    }

    void setSwitch1Status(String status)
    {
        try
        {
            String url = LogIn.URL+"setSwitch1StatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    if (response.equals("1"))
                    {
                        myRefSwitch1.setValue(status);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
                    Params.put("id",String.valueOf(ID) );
                    Params.put("value" , status);
                    return Params;
                }
            };
            Volley.newRequestQueue(act).add(tabR);
        }
        catch (Exception e)
        {

        }
    }

    void setSwitch2Status(String status)
    {
        try
        {
            String url = LogIn.URL+"setSwitch2StatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    if (response.equals("1"))
                    {
                        myRefSwitch2.setValue(status);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
                    Params.put("id",String.valueOf(ID) );
                    Params.put("value" , status);
                    return Params;
                }
            };
            Volley.newRequestQueue(act).add(tabR);
        }
        catch (Exception e)
        {

        }
    }

    void setSwitch3Status(String status)
    {
        try
        {
            String url = LogIn.URL+"setSwitch3StatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    if (response.equals("1"))
                    {
                        myRefSwitch3.setValue(status);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
                    Params.put("id",String.valueOf(ID) );
                    Params.put("value" , status);
                    return Params;
                }
            };
            Volley.newRequestQueue(act).add(tabR);
        }
        catch (Exception e)
        {

        }
    }

    void setSwitch4Status(String status)
    {
        try
        {
            String url = LogIn.URL+"setSwitch4StatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    if (response.equals("1"))
                    {
                        myRefSwitch4.setValue(status);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
                    Params.put("id",String.valueOf(ID) );
                    Params.put("value" , status);
                    return Params;
                }
            };
            Volley.newRequestQueue(act).add(tabR);
        }
        catch (Exception e)
        {

        }
    }

    void setPowerSwitchStatus(String status)
    {
        try
        {
            String url = LogIn.URL+"setPowerSwitchStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    Log.e("power " , response +" " + status);
                    if (response.equals("1"))
                    {
                        myRefSwitch4.setValue(status);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
                    Params.put("id",String.valueOf(ID) );
                    Params.put("value" , status);
                    return Params;
                }
            };
            Volley.newRequestQueue(act).add(tabR);
        }
        catch (Exception e)
        {

        }
    }

    void setPowerOnOff(String status)
    {
        try
        {
            String url = LogIn.URL+"setCurrentPowerOnOff.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    Log.e("power " , response +" " + status);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
                    Params.put("id",String.valueOf(ID) );
                    Params.put("value" , status);
                    return Params;
                }
            };
            Volley.newRequestQueue(act).add(tabR);
        }
        catch (Exception e)
        {

        }
    }

    void setServiceSwitchStatus(String status)
    {
        try
        {
            String url = LogIn.URL+"setServiceSwitchStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    if (response.equals("1"))
                    {
                        myRefServiceSwitch.setValue(status);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
                    Params.put("id",String.valueOf(ID) );
                    Params.put("value" , status);
                    return Params;
                }
            };
            Volley.newRequestQueue(act).add(tabR);
        }
        catch (Exception e)
        {

        }
    }

    private void getLaundries()
    {
        try
        {
            String url = LogIn.URL+"getLaundries.php?Hotel="+LogIn.room.getHotel();
            StringRequest laundryRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    getRestaurants();
                    //ToastMaker.MakeToast(response,act);
                    if (response.equals("0"))
                    {

                    }
                    else
                    {
                        try
                        {
                            JSONArray arr = new JSONArray(response);
                            for (int i=0 ; i<arr.length();i++)
                            {
                                JSONObject row =arr.getJSONObject(i);
                                Laundries.add(new LAUNDRY(row.getInt("id"),row.getInt("Hotel"),row.getInt("TypeId"),row.getString("TypeName"),row.getString("Name"),row.getInt("Control"),row.getString("photo")));
                            }
                            if (Laundries.size()>0)
                            {
                                LaundryBtn.setVisibility(View.VISIBLE);
                                //laundryPriceList.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                LaundryBtn.setVisibility(View.INVISIBLE);
                                //laundryPriceList.setVisibility(View.INVISIBLE);
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {

                }
            });
            Volley.newRequestQueue(act).add(laundryRequest);
        }
        catch (Exception e)
        {

        }

    }

    private void getRestaurants()
    {
        try
        {
            String url = LogIn.URL+"getRestaurantsOrCoffeeShops.php";
            StringRequest laundryRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    getGyms();
                    //ToastMaker.MakeToast(response,act);
                    if (response.equals("0"))
                    {

                    }
                    else
                    {
                        try
                        {
                            JSONArray arr = new JSONArray(response);
                            for (int i=0 ; i<arr.length();i++)
                            {
                                JSONObject row =arr.getJSONObject(i);
                                Restaurants.add(new RESTAURANT_UNIT(row.getInt("id"),row.getInt("Hotel"),row.getInt("TypeId"),row.getString("TypeName"),row.getString("Name"),row.getInt("Control"),row.getString("photo")));
                            }
                            if (Restaurants.size()>0)
                            {
                                RestaurantBtn.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                RestaurantBtn.setVisibility(View.GONE);
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {

                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> par = new HashMap<String, String>();
                    par.put("Hotel" , String.valueOf(LogIn.room.getHotel()));
                    return par;
                }
            };

            Volley.newRequestQueue(act).add(laundryRequest);
        }
        catch (Exception e)
        {

        }

    }

    private void getGyms()
    {
        try
        {
            String url = LogIn.URL+"getGyms.php";
            StringRequest laundryRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    getMiniBar();
                    //ToastMaker.MakeToast(response,act);
                    if (response.equals("0"))
                    {

                    }
                    else
                    {
                        try
                        {
                            JSONArray arr = new JSONArray(response);
                            for (int i=0 ; i<arr.length();i++)
                            {
                                JSONObject row =arr.getJSONObject(i);
                                Gyms.add(new FACILITY(row.getInt("id"),row.getInt("Hotel"),row.getInt("TypeId"),row.getString("TypeName"),row.getString("Name"),row.getInt("Control"),row.getString("photo")));
                            }
                            if (Gyms.size()>0)
                            {
                                GymBtn.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                GymBtn.setVisibility(View.INVISIBLE);
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {

                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> par = new HashMap<String, String>();
                    par.put("Hotel" , String.valueOf(LogIn.room.getHotel()));
                    return par;
                }
            };

            Volley.newRequestQueue(act).add(laundryRequest);
        }
        catch (Exception e)
        {

        }

    }

    void getLaundryMenu(int Facility)
    {
        try
        {
            LoadingDialog loading = new LoadingDialog(act);
            List<LAUNDRYITEM> list = new ArrayList<LAUNDRYITEM>();
            String url = LogIn.URL+"getLaundryMenuItems.php";
            StringRequest laundryRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    if (response.equals("0"))
                    {
                        loading.stop();
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
                                list.add(new LAUNDRYITEM(row.getString("icon"),row.getString("Name"),row.getString("Price")));
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        loading.stop();
                        if (list.size()>0)
                        {
                            LAUNDRYMENU_ADAPTER adapter = new LAUNDRYMENU_ADAPTER(list);
                            LAUNDRYMENU.setAdapter(adapter);
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

                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Hotel" ,String.valueOf(LogIn.room.getHotel()));
                    params.put("Facility" , String.valueOf(Facility));
                    return params;
                }
             };
            Volley.newRequestQueue(act).add(laundryRequest);
        }
        catch (Exception e)
        {

        }
    }

    private void getMiniBar()
    {
        try
        {
            String url = LogIn.URL+"getMinibars.php?Hotel="+LogIn.room.getHotel();
            StringRequest laundryRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    //ToastMaker.MakeToast(response,act);
                    if (response.equals("0"))
                    {

                    }
                    else
                    {
                        try
                        {
                            JSONArray arr = new JSONArray(response);
                            for (int i=0 ; i<arr.length();i++)
                            {
                                JSONObject row =arr.getJSONObject(i);
                                Minibar.add(new MINIBAR(row.getInt("id"),row.getInt("Hotel"),row.getInt("TypeId"),row.getString("TypeName"),row.getString("Name"),row.getInt("Control"),row.getString("photo")));
                            }
                            if (Minibar.size()>0)
                            {
                                //ShowMiniBar.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                //ShowMiniBar.setVisibility(View.INVISIBLE);
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {

                }
            });
            Volley.newRequestQueue(act).add(laundryRequest);
        }
        catch (Exception e)
        {

        }

    }

    void getMiniBarMenu(int Facility)
    {
        try
        {
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
                    params.put("Hotel" ,String.valueOf(LogIn.room.getHotel()));
                    params.put("Facility" , String.valueOf(Facility));
                    return params;
                }
            };
            Volley.newRequestQueue(act).add(laundryRequest);
        }
        catch (Exception e)
        {

        }
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

    private void hideMainBtns(){
        ImageView Logo = (ImageView) findViewById(R.id.HotelLogo);
        LinearLayout Btns = (LinearLayout)findViewById(R.id.MainBtns_Layout);
        TextView Caption = (TextView)findViewById(R.id.textView12);
        TextView Text = (TextView)findViewById(R.id.RoomNumber_MainScreen);
        LinearLayout Services = (LinearLayout) findViewById(R.id.Service_Btns);
        LinearLayout homeBtn = (LinearLayout)findViewById(R.id.home_Btn);
        TextView serviceText = (TextView) findViewById(R.id.textView37);
        serviceText.setVisibility(View.VISIBLE);
        serviceText.setText("SERVICES");
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
        Logo.setVisibility(View.GONE);
        Btns.setVisibility(View.GONE);
        Caption.setVisibility(View.GONE);
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
        mediaPlayer.start();
        ImageView Logo = (ImageView) findViewById(R.id.HotelLogo);
        LinearLayout Btns = (LinearLayout)findViewById(R.id.MainBtns_Layout);
        TextView Caption = (TextView)findViewById(R.id.textView12);
        TextView Text = (TextView)findViewById(R.id.RoomNumber_MainScreen);
        LinearLayout Services = (LinearLayout) findViewById(R.id.Service_Btns);
        TextView serviceText = (TextView) findViewById(R.id.textView37);
        LinearLayout homeBtn = (LinearLayout)findViewById(R.id.home_Btn);
        LinearLayout l = (LinearLayout) findViewById(R.id.laundryList_Layout);
        homeBtn.setVisibility(View.GONE);
        serviceText.setVisibility(View.GONE);
        Services.setVisibility(View.GONE);
        Logo.setVisibility(View.VISIBLE);
        Btns.setVisibility(View.VISIBLE);
        Caption.setVisibility(View.VISIBLE);
        Text.setVisibility(View.VISIBLE);
        LinearLayout lights = (LinearLayout) findViewById(R.id.lightingLayout);
        lights.setVisibility(View.GONE);
        LinearLayout home = (LinearLayout) findViewById(R.id.home_Btn);
        home.setVisibility(View.GONE);
        laundryPriceList.setVisibility(View.GONE);
        l.setVisibility(View.GONE);
        LinearLayout minibarLayout = (LinearLayout) findViewById(R.id.Minibar_layout);
        LinearLayout minibarBtn = (LinearLayout) findViewById(R.id.minibar_priceList);
        minibarLayout.setVisibility(View.GONE);
        minibarBtn.setVisibility(View.GONE);
    }

    public void goToLights(View view) {
        mediaPlayer.start();
        ImageView Logo = (ImageView) findViewById(R.id.HotelLogo);
        LinearLayout Btns = (LinearLayout)findViewById(R.id.MainBtns_Layout);
        TextView text = (TextView)findViewById(R.id.textView12);
        TextView Caption = (TextView)findViewById(R.id.textView37);
        TextView Text = (TextView)findViewById(R.id.RoomNumber_MainScreen);
        Logo.setVisibility(View.GONE);
        Btns.setVisibility(View.GONE);
        text.setVisibility(View.GONE);
        Caption.setVisibility(View.VISIBLE);
        Caption.setText("LIGHTS");
        Text.setVisibility(View.GONE);
        LinearLayout lights = (LinearLayout) findViewById(R.id.lightingLayout);
        lights.setVisibility(View.VISIBLE);
        LinearLayout home = (LinearLayout) findViewById(R.id.home_Btn);
        home.setVisibility(View.VISIBLE);
        if (THEROOM.getSWITCH1_B() != null ){
            if (THEROOM.getSWITCH1_B().dps.get("1") != null){
                if (THEROOM.getSWITCH1_B().dps.get("1").toString().equals("true")){
                    Button b1 = (Button)findViewById(R.id.button15);
                    b1.setBackgroundResource(R.drawable.light_on);
                }
            }
            if (THEROOM.getSWITCH1_B().dps.get("2") != null){
                if (THEROOM.getSWITCH1_B().dps.get("2").toString().equals("true")){
                    Button b1 = (Button)findViewById(R.id.button14);
                    b1.setBackgroundResource(R.drawable.light_on);
                }
            }
            if (THEROOM.getSWITCH1_B().dps.get("3") != null ){
                if (THEROOM.getSWITCH1_B().dps.get("3").toString().equals("true")){
                    Button b1 = (Button)findViewById(R.id.button17);
                    b1.setBackgroundResource(R.drawable.light_on);
                }
            }
            if (THEROOM.getSWITCH1_B().dps.get("4") != null){
                if (THEROOM.getSWITCH1_B().dps.get("4").toString().equals("true")){
                    Button b1 = (Button)findViewById(R.id.button19);
                    b1.setBackgroundResource(R.drawable.light_on);
                }
            }
        }
        startBackHomeThread();
    }

    void startBackHomeThread() {
        Log.d("backThread" , "started");
            backHomeThread.run();
    }

    void stopBackThread(){

            Log.d("backThread" , "stoped");
            H.removeCallbacks(backHomeThread);

    }

    void setbackThreadLayouts(){

    }

    void bindSwitch1_2DP1_2(){
        if (THEROOM.getSWITCH1_B() != null && THEROOM.getSWITCH2_B() != null) {

            iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH1_B().devId, "1", new ITuyaDataCallback<MultiControlLinkBean>() {
                @Override
                public void onSuccess(MultiControlLinkBean result) {

                    if (result.getMultiGroup() == null )
                    {
                        Log.d("S1D1RES " , "null");
                        iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH2_B().devId, "2", new ITuyaDataCallback<MultiControlLinkBean>() {
                            @Override
                            public void onSuccess(MultiControlLinkBean result) {

                                if (result.getMultiGroup() == null ){
                                    Log.d("S2D1RES" , "null");
                                    Random r = new Random();
                                    int x = r.nextInt(30);
                                    JSONObject groupdetailes1 = new JSONObject(), groupdetailes2 = new JSONObject();
                                    try {
                                        groupdetailes1.put("devId", THEROOM.getSWITCH1_B().devId);
                                        groupdetailes1.put("dpId", 1);
                                        groupdetailes1.put("id", x);
                                        groupdetailes1.put("enable", true);

                                    } catch (JSONException e) {
                                    }
                                    try {
                                        groupdetailes2.put("devId", THEROOM.getSWITCH2_B().devId);
                                        groupdetailes2.put("dpId", 2);
                                        groupdetailes2.put("id", x);
                                        groupdetailes2.put("enable", true);

                                    } catch (JSONException e) {
                                    }
                                    JSONArray arr = new JSONArray();
                                    arr.put(groupdetailes2);
                                    arr.put(groupdetailes1);
                                    JSONObject multiControlBean = new JSONObject();
                                    try {
                                        multiControlBean.put("groupName", LogIn.room.getRoomNumber() + "Lighting" + x);
                                        multiControlBean.put("groupType", 1);
                                        multiControlBean.put("groupDetail", arr);
                                        multiControlBean.put("id", x);
                                    } catch (JSONException e) {

                                    }
                                    iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString(), new ITuyaResultCallback<MultiControlBean>() {
                                        @Override
                                        public void onSuccess(MultiControlBean result) {
                                            //ToastUtil.shortToast(mContext,"success");
                                            Toast.makeText(act,"1_2ok",Toast.LENGTH_SHORT);
                                            Log.d("switch1Dp1", result.getGroupName());
                                            iTuyaDeviceMultiControl.enableMultiControl(x, new ITuyaResultCallback<Boolean>() {
                                                @Override
                                                public void onSuccess(Boolean result) {
                                                    //ToastUtil.shortToast(mContext,"success");
                                                    Log.d("switch1Dp1", result.toString());
                                                }

                                                @Override
                                                public void onError(String errorCode, String errorMessage) {
                                                    //ToastUtil.shortToast(mContext,errorMessage);
                                                    Log.d("switch1Dp1", errorMessage);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(String errorCode, String errorMessage) {
                                            //ToastUtil.shortToast(mContext,errorMessage);
                                            Toast.makeText(act,"failed",Toast.LENGTH_SHORT);
                                            Log.d("switch1Dp1", errorMessage + "here "+x);
                                        }
                                    });
                                }
                                else
                                {
                                    Log.d("S2D1RES " , result.getMultiGroup().getGroupName());
                                }
                            }

                            @Override
                            public void onError(String errorCode, String errorMessage) {

                            }
                        });
                    }
                    else
                    {
                        Log.d("S1D1RES " , result.getMultiGroup().getGroupName());
                    }
                }

                @Override
                public void onError(String errorCode, String errorMessage) {

                }
            });


        }
    }
    void bindSwitch1_2DP2_1(){
        if (THEROOM.getSWITCH1_B() != null && THEROOM.getSWITCH2_B() != null) {

            iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH1_B().devId, "2", new ITuyaDataCallback<MultiControlLinkBean>() {
                @Override
                public void onSuccess(MultiControlLinkBean result) {

                    if (result.getMultiGroup() == null ){
                        Log.d("S1D2RES " , "null");
                        iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH2_B().devId, "1", new ITuyaDataCallback<MultiControlLinkBean>() {
                            @Override
                            public void onSuccess(MultiControlLinkBean result) {

                                if (result.getMultiGroup() == null ){
                                    Log.d("S2D2RES " , "null");
                                    Random r = new Random();
                                    int y = r.nextInt(30);
                                    JSONObject groupdetailes1 = new JSONObject(), groupdetailes2 = new JSONObject();
                                    try {
                                        groupdetailes1.put("devId", THEROOM.getSWITCH1_B().devId);
                                        groupdetailes1.put("dpId", 2);
                                        groupdetailes1.put("id", y);
                                        groupdetailes1.put("enable", true);

                                    } catch (JSONException e) {
                                    }
                                    try {
                                        groupdetailes2.put("devId", THEROOM.getSWITCH2_B().devId);
                                        groupdetailes2.put("dpId", 1);
                                        groupdetailes2.put("id", y);
                                        groupdetailes2.put("enable", true);

                                    } catch (JSONException e) {
                                    }
                                    JSONArray arr = new JSONArray();
                                    arr.put(groupdetailes2);
                                    arr.put(groupdetailes1);
                                    JSONObject multiControlBean = new JSONObject();
                                    try {
                                        multiControlBean.put("groupName", LogIn.room.getRoomNumber() + "Lighting" + y);
                                        multiControlBean.put("groupType", 2);
                                        multiControlBean.put("groupDetail", arr);
                                        multiControlBean.put("id", y);
                                    } catch (JSONException e) {

                                    }
                                    iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString(), new ITuyaResultCallback<MultiControlBean>() {
                                        @Override
                                        public void onSuccess(MultiControlBean result) {
                                            //ToastUtil.shortToast(mContext,"success");
                                            Toast.makeText(act,"2_1ok",Toast.LENGTH_SHORT);
                                            Log.d("switch1DeviceDp2", result.getGroupName());
                                            iTuyaDeviceMultiControl.enableMultiControl(y, new ITuyaResultCallback<Boolean>() {
                                                @Override
                                                public void onSuccess(Boolean result) {
                                                    //ToastUtil.shortToast(mContext,"success");
                                                    Log.d("switch1DeviceDp2", result.toString());
                                                }

                                                @Override
                                                public void onError(String errorCode, String errorMessage) {
                                                    //ToastUtil.shortToast(mContext,errorMessage);
                                                    Log.d("switch1DeviceDp2", errorMessage);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(String errorCode, String errorMessage) {
                                            //ToastUtil.shortToast(mContext,errorMessage);
                                            Toast.makeText(act,"failed",Toast.LENGTH_SHORT);
                                            Log.d("switch1DeviceDp2", errorMessage+" "+y);
                                        }
                                    });
                                }
                                else
                                {
                                    Log.d("S2D2RES " , result.getMultiGroup().getGroupName());
                                }
                            }

                            @Override
                            public void onError(String errorCode, String errorMessage) {

                            }
                        });
                    }
                    else
                    {
                        Log.d("S1D2RES " , result.getMultiGroup().getGroupName());
                    }
                }

                @Override
                public void onError(String errorCode, String errorMessage) {

                }
            });

        }
    }
    void setSwitch1DB3(){
        if (THEROOM.getSWITCH1_B() != null && THEROOM.getSWITCH2_B() != null) {

            iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH1_B().devId, "3", new ITuyaDataCallback<MultiControlLinkBean>() {
                @Override
                public void onSuccess(MultiControlLinkBean result) {

                    if (result.getMultiGroup() == null ){
                        Log.d("S1D3RES " , "null");
                        iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH2_B().devId, "3", new ITuyaDataCallback<MultiControlLinkBean>() {
                            @Override
                            public void onSuccess(MultiControlLinkBean result) {

                                if (result.getMultiGroup() == null ){
                                    Log.d("S2D3RES " , "null");
                                    Random r = new Random();
                                    int x = r.nextInt(30);
                                    JSONObject groupdetailes1 = new JSONObject(), groupdetailes2 = new JSONObject();
                                    try {
                                        groupdetailes1.put("devId", THEROOM.getSWITCH1_B().devId);
                                        groupdetailes1.put("dpId", 3);
                                        groupdetailes1.put("id", x);
                                        groupdetailes1.put("enable", true);

                                    } catch (JSONException e) {
                                    }
                                    try {
                                        groupdetailes2.put("devId", THEROOM.getSWITCH2_B().devId);
                                        groupdetailes2.put("dpId", 3);
                                        groupdetailes2.put("id", x);
                                        groupdetailes2.put("enable", true);

                                    } catch (JSONException e) {
                                    }
                                    JSONArray arr = new JSONArray();
                                    arr.put(groupdetailes2);
                                    arr.put(groupdetailes1);
                                    JSONObject multiControlBean = new JSONObject();
                                    try {
                                        multiControlBean.put("groupName", LogIn.room.getRoomNumber() + "Lighting" + x);
                                        multiControlBean.put("groupType", 3);
                                        multiControlBean.put("groupDetail", arr);
                                        multiControlBean.put("id", x);
                                    } catch (JSONException e) {

                                    }
                                    iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString(), new ITuyaResultCallback<MultiControlBean>() {
                                        @Override
                                        public void onSuccess(MultiControlBean result) {
                                            //ToastUtil.shortToast(mContext,"success");
                                            Log.d("switch1DeviceDp3", result.getGroupName());
                                            iTuyaDeviceMultiControl.enableMultiControl(x, new ITuyaResultCallback<Boolean>() {
                                                @Override
                                                public void onSuccess(Boolean result) {
                                                    //ToastUtil.shortToast(mContext,"success");
                                                    Log.d("switch1DeviceDp3", result.toString());
                                                }

                                                @Override
                                                public void onError(String errorCode, String errorMessage) {
                                                    //ToastUtil.shortToast(mContext,errorMessage);
                                                    Log.d("switch1DeviceDp3", errorMessage);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(String errorCode, String errorMessage) {
                                            //ToastUtil.shortToast(mContext,errorMessage);
                                            Log.d("switch1DeviceDp3", errorMessage+" "+x);
                                        }
                                    });
                                }
                                else
                                {
                                    Log.d("S2D3RES " , result.getMultiGroup().getGroupName());
                                }
                            }

                            @Override
                            public void onError(String errorCode, String errorMessage) {

                            }
                        });
                    }
                    else
                    {
                        Log.d("S1D3RES " , result.getMultiGroup().getGroupName());
                    }
                }

                @Override
                public void onError(String errorCode, String errorMessage) {

                }
            });

        }
    }
    void setSwitch1DB4(){
        if (THEROOM.getSWITCH1_B() != null && THEROOM.getSWITCH2_B() != null) {
            iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH1_B().devId, "4", new ITuyaDataCallback<MultiControlLinkBean>() {
                @Override
                public void onSuccess(MultiControlLinkBean result) {

                    if (result.getMultiGroup() == null ){
                        Log.d("S1D4RES " , "null");
                        iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH2_B().devId, "4", new ITuyaDataCallback<MultiControlLinkBean>() {
                            @Override
                            public void onSuccess(MultiControlLinkBean result) {

                                if (result.getMultiGroup() == null ){
                                    Log.d("S2D4RES " , "null");
                                    Random r = new Random();
                                    int x = r.nextInt(30);
                                    JSONObject groupdetailes1 = new JSONObject(), groupdetailes2 = new JSONObject();
                                    try {
                                        groupdetailes1.put("devId", THEROOM.getSWITCH1_B().devId);
                                        groupdetailes1.put("dpId", 4);
                                        groupdetailes1.put("id", x);
                                        groupdetailes1.put("enable", true);

                                    } catch (JSONException e) {
                                    }
                                    try {
                                        groupdetailes2.put("devId", THEROOM.getSWITCH2_B().devId);
                                        groupdetailes2.put("dpId", 4);
                                        groupdetailes2.put("id", x);
                                        groupdetailes2.put("enable", true);

                                    } catch (JSONException e) {
                                    }
                                    JSONArray arr = new JSONArray();
                                    arr.put(groupdetailes2);
                                    arr.put(groupdetailes1);
                                    JSONObject multiControlBean = new JSONObject();
                                    try {
                                        multiControlBean.put("groupName", LogIn.room.getRoomNumber() + "Lighting" + x);
                                        multiControlBean.put("groupType", 4);
                                        multiControlBean.put("groupDetail", arr);
                                        multiControlBean.put("id", x);
                                    } catch (JSONException e) {

                                    }
                                    iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString(), new ITuyaResultCallback<MultiControlBean>() {
                                        @Override
                                        public void onSuccess(MultiControlBean result) {
                                            //ToastUtil.shortToast(mContext,"success");
                                            Log.d("switch1DeviceDp4", result.getGroupName());
                                            iTuyaDeviceMultiControl.enableMultiControl(x, new ITuyaResultCallback<Boolean>() {
                                                @Override
                                                public void onSuccess(Boolean result) {
                                                    //ToastUtil.shortToast(mContext,"success");
                                                    Log.d("switch1DeviceDp4", result.toString());
                                                }

                                                @Override
                                                public void onError(String errorCode, String errorMessage) {
                                                    //ToastUtil.shortToast(mContext,errorMessage);
                                                    Log.d("switch1DeviceDp4", errorMessage);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(String errorCode, String errorMessage) {
                                            //ToastUtil.shortToast(mContext,errorMessage);
                                            Log.d("switch1DeviceDp4", errorMessage+" "+x);
                                        }
                                    });
                                }
                                else
                                {
                                    Log.d("S2D4RES " , result.getMultiGroup().getGroupName());
                                }
                            }

                            @Override
                            public void onError(String errorCode, String errorMessage) {

                            }
                        });
                    }
                    else
                    {
                        Log.d("S1D4RES " , result.getMultiGroup().getGroupName());
                    }
                }

                @Override
                public void onError(String errorCode, String errorMessage) {

                }
            });

        }
    }

    void bindSwitch1ToSwitch3btn1(ROOM THEROOM){
        if (THEROOM.getSWITCH1_B() != null && THEROOM.getSWITCH3_B() != null) {

            iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH1_B().devId, "2", new ITuyaDataCallback<MultiControlLinkBean>() {
                @Override
                public void onSuccess(MultiControlLinkBean result) {

                    if (result.getMultiGroup() == null )
                    {
                        Log.d("bindS1S3" , "null");
                        iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH3_B().devId, "1", new ITuyaDataCallback<MultiControlLinkBean>() {
                            @Override
                            public void onSuccess(MultiControlLinkBean result) {

                                if (result.getMultiGroup() == null ){
                                    Log.d("bindS1S3" , "null");
                                    Random r = new Random();
                                    int x = r.nextInt(30);
                                    JSONObject groupdetailes1 = new JSONObject(), groupdetailes2 = new JSONObject();
                                    try {
                                        groupdetailes1.put("devId", THEROOM.getSWITCH1_B().devId);
                                        groupdetailes1.put("dpId", 2);
                                        groupdetailes1.put("id", x);
                                        groupdetailes1.put("enable", true);

                                    } catch (JSONException e) {
                                    }
                                    try {
                                        groupdetailes2.put("devId", THEROOM.getSWITCH3_B().devId);
                                        groupdetailes2.put("dpId", 1);
                                        groupdetailes2.put("id", x);
                                        groupdetailes2.put("enable", true);

                                    } catch (JSONException e) {
                                    }
                                    JSONArray arr = new JSONArray();
                                    arr.put(groupdetailes2);
                                    arr.put(groupdetailes1);
                                    JSONObject multiControlBean = new JSONObject();
                                    try {
                                        multiControlBean.put("groupName", THEROOM.RoomNumber + "Lighting" + x);
                                        multiControlBean.put("groupType", 13);
                                        multiControlBean.put("groupDetail", arr);
                                        multiControlBean.put("id", x);
                                    } catch (JSONException e) {

                                    }
                                    iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString(), new ITuyaResultCallback<MultiControlBean>() {
                                        @Override
                                        public void onSuccess(MultiControlBean result) {
                                            //ToastUtil.shortToast(mContext,"success");
                                            Log.d("bindS1S3", result.getGroupName());
                                            iTuyaDeviceMultiControl.enableMultiControl(x, new ITuyaResultCallback<Boolean>() {
                                                @Override
                                                public void onSuccess(Boolean result) {
                                                    //ToastUtil.shortToast(mContext,"success");
                                                    Log.d("bindS1S3", result.toString());
                                                }

                                                @Override
                                                public void onError(String errorCode, String errorMessage) {
                                                    //ToastUtil.shortToast(mContext,errorMessage);
                                                    Log.d("bindS1S3", errorMessage);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(String errorCode, String errorMessage) {
                                            //ToastUtil.shortToast(mContext,errorMessage);
                                            Log.d("bindS1S3", errorMessage + "here "+x);
                                        }
                                    });
                                }
                                else
                                {
                                    Log.d("bindS1S3" , result.getMultiGroup().getGroupName());
                                }
                            }

                            @Override
                            public void onError(String errorCode, String errorMessage) {

                            }
                        });
                    }
                    else
                    {
                        Log.d("bindS1S3" , result.getMultiGroup().getGroupName());
                    }
                }

                @Override
                public void onError(String errorCode, String errorMessage) {

                }
            });


        }
    }
    void bindSwitch1ToSwitch3btn2(ROOM THEROOM){
        if (THEROOM.getSWITCH1_B() != null && THEROOM.getSWITCH3_B() != null) {

            iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH1_B().devId, "3", new ITuyaDataCallback<MultiControlLinkBean>() {
                @Override
                public void onSuccess(MultiControlLinkBean result) {

                    if (result.getMultiGroup() == null )
                    {
                        Log.d("bindS1S3" , "null");
                        iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH3_B().devId, "2", new ITuyaDataCallback<MultiControlLinkBean>() {
                            @Override
                            public void onSuccess(MultiControlLinkBean result) {

                                if (result.getMultiGroup() == null ){
                                    Log.d("bindS1S3" , "null");
                                    Random r = new Random();
                                    int x = r.nextInt(30);
                                    JSONObject groupdetailes1 = new JSONObject(), groupdetailes2 = new JSONObject();
                                    try {
                                        groupdetailes1.put("devId", THEROOM.getSWITCH1_B().devId);
                                        groupdetailes1.put("dpId", 3);
                                        groupdetailes1.put("id", x);
                                        groupdetailes1.put("enable", true);

                                    } catch (JSONException e) {
                                    }
                                    try {
                                        groupdetailes2.put("devId", THEROOM.getSWITCH3_B().devId);
                                        groupdetailes2.put("dpId", 2);
                                        groupdetailes2.put("id", x);
                                        groupdetailes2.put("enable", true);

                                    } catch (JSONException e) {
                                    }
                                    JSONArray arr = new JSONArray();
                                    arr.put(groupdetailes2);
                                    arr.put(groupdetailes1);
                                    JSONObject multiControlBean = new JSONObject();
                                    try {
                                        multiControlBean.put("groupName", THEROOM.RoomNumber + "Lighting" + x);
                                        multiControlBean.put("groupType", 14);
                                        multiControlBean.put("groupDetail", arr);
                                        multiControlBean.put("id", x);
                                    } catch (JSONException e) {

                                    }
                                    iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString(), new ITuyaResultCallback<MultiControlBean>() {
                                        @Override
                                        public void onSuccess(MultiControlBean result) {
                                            //ToastUtil.shortToast(mContext,"success");
                                            Log.d("bindS1S3", result.getGroupName());
                                            iTuyaDeviceMultiControl.enableMultiControl(x, new ITuyaResultCallback<Boolean>() {
                                                @Override
                                                public void onSuccess(Boolean result) {
                                                    //ToastUtil.shortToast(mContext,"success");
                                                    Log.d("bindS1S3", result.toString());
                                                }

                                                @Override
                                                public void onError(String errorCode, String errorMessage) {
                                                    //ToastUtil.shortToast(mContext,errorMessage);
                                                    Log.d("bindS1S3", errorMessage);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(String errorCode, String errorMessage) {
                                            //ToastUtil.shortToast(mContext,errorMessage);
                                            Log.d("bindS1S3", errorMessage + "here "+x);
                                        }
                                    });
                                }
                                else
                                {
                                    Log.d("bindS1S3" , result.getMultiGroup().getGroupName());
                                }
                            }

                            @Override
                            public void onError(String errorCode, String errorMessage) {

                            }
                        });
                    }
                    else
                    {
                        Log.d("bindS1S3" , result.getMultiGroup().getGroupName());
                    }
                }

                @Override
                public void onError(String errorCode, String errorMessage) {

                }
            });


        }
    }
    void setSwitch1DB2(ROOM THEROOM){
        if (THEROOM.getSWITCH1_B() != null && THEROOM.getSWITCH2_B() != null) {

            iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH1_B().devId, "2", new ITuyaDataCallback<MultiControlLinkBean>() {
                @Override
                public void onSuccess(MultiControlLinkBean result) {

                    if (result.getMultiGroup() == null ){
                        Log.d("MultiControlProblem"+THEROOM.RoomNumber , "null");
                        iTuyaDeviceMultiControl.queryLinkInfoByDp(THEROOM.getSWITCH2_B().devId, "2", new ITuyaDataCallback<MultiControlLinkBean>() {
                            @Override
                            public void onSuccess(MultiControlLinkBean result) {

                                if (result.getMultiGroup() == null ){
                                    Log.d("MultiControlProblem"+THEROOM.RoomNumber , "null");
                                    Random r = new Random();
                                    int y = r.nextInt(30);
                                    JSONObject groupdetailes1 = new JSONObject(), groupdetailes2 = new JSONObject();
                                    try {
                                        groupdetailes1.put("devId", THEROOM.getSWITCH1_B().devId);
                                        groupdetailes1.put("dpId", 2);
                                        groupdetailes1.put("id", y);
                                        groupdetailes1.put("enable", true);

                                    } catch (JSONException e) {
                                    }
                                    try {
                                        groupdetailes2.put("devId", THEROOM.getSWITCH2_B().devId);
                                        groupdetailes2.put("dpId", 2);
                                        groupdetailes2.put("id", y);
                                        groupdetailes2.put("enable", true);

                                    } catch (JSONException e) {
                                    }
                                    JSONArray arr = new JSONArray();
                                    arr.put(groupdetailes2);
                                    arr.put(groupdetailes1);
                                    JSONObject multiControlBean = new JSONObject();
                                    try {
                                        multiControlBean.put("groupName", THEROOM.RoomNumber + "Lighting" + y);
                                        multiControlBean.put("groupType", 2);
                                        multiControlBean.put("groupDetail", arr);
                                        multiControlBean.put("id", y);
                                    } catch (JSONException e) {

                                    }
                                    iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString(), new ITuyaResultCallback<MultiControlBean>() {
                                        @Override
                                        public void onSuccess(MultiControlBean result) {
                                            //ToastUtil.shortToast(mContext,"success");
                                            Log.d("MultiControlProblem"+THEROOM.RoomNumber, result.getGroupName());

                                            iTuyaDeviceMultiControl.enableMultiControl(y, new ITuyaResultCallback<Boolean>() {
                                                @Override
                                                public void onSuccess(Boolean result) {
                                                    //ToastUtil.shortToast(mContext,"success");
                                                    Log.d("MultiControlProblem"+THEROOM.RoomNumber, result.toString());
                                                }

                                                @Override
                                                public void onError(String errorCode, String errorMessage) {
                                                    //ToastUtil.shortToast(mContext,errorMessage);
                                                    Log.d("MultiControlProblem"+THEROOM.RoomNumber, errorMessage);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(String errorCode, String errorMessage) {
                                            //ToastUtil.shortToast(mContext,errorMessage);
                                            Log.d("MultiControlProblem"+THEROOM.RoomNumber, errorMessage+" "+y);
                                        }
                                    });
                                }
                                else
                                {
                                    Log.d("MultiControlProblem"+THEROOM.RoomNumber , result.getMultiGroup().getGroupName());
                                }
                            }

                            @Override
                            public void onError(String errorCode, String errorMessage) {

                            }
                        });
                    }
                    else
                    {
                        Log.d("MultiControlProblem"+THEROOM.RoomNumber , result.getMultiGroup().getGroupName());
                    }
                }

                @Override
                public void onError(String errorCode, String errorMessage) {

                }
            });

        }
    }

    public void showHideLaundryList(View view)
    {
        mediaPlayer.start();
        //ToastMaker.MakeToast("i am pressed",act);
        LinearLayout btns = (LinearLayout) findViewById(R.id.Service_Btns);
        LinearLayout l = (LinearLayout) findViewById(R.id.laundryList_Layout);
        TextView caption = (TextView) findViewById(R.id.laundryList_caption);
        if (l.getVisibility() == View.GONE){
            getLaundryMenu(Laundries.get(0).id);
            btns.setVisibility(View.GONE);
            l.setVisibility(View.VISIBLE);
            //caption.setText("Back To Services");
        }
        else
        {
            btns.setVisibility(View.VISIBLE);
            l.setVisibility(View.GONE);
            //caption.setText("Laundry PriceList");
        }
        x=0;
    }

    public void showHideMinibarPriceList(View view)
    {
        mediaPlayer.start();
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

    void getServiceEmps() {
        StringRequest request = new StringRequest(Request.Method.POST, getServiceEmpsUrl, new Response.Listener<String>() {
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
                    ToastMaker.MakeToast("No service emps",act);
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

    void getServiceUsersFromFirebase() {
        ServiceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null ) {
                    Emps.clear();
                    //Log.d("EmpsAre ",snapshot.getValue().toString());
                    for (DataSnapshot child : snapshot.getChildren()) {
                        //Log.d("EmpsAre ",child.getValue().toString());
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
                    //Toast.makeText(act,Emps.size()+"",Toast.LENGTH_LONG).show();
                    Log.d("EmpsAre ",Emps.size()+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
