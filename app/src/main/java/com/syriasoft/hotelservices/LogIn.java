package com.syriasoft.hotelservices;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.reflect.TypeToken;
import com.syriasoft.hotelservices.TUYA.Family_List_Adapter;
import com.syriasoft.hotelservices.TUYA.Tuya_Devices;
import com.syriasoft.hotelservices.TUYA.Tuya_Login;
import com.syriasoft.hotelservices.lock.AccountInfo;
import com.syriasoft.hotelservices.lock.ApiService;
import com.syriasoft.hotelservices.lock.GatewayObj;
import com.syriasoft.hotelservices.lock.IndexActivity;
import com.syriasoft.hotelservices.lock.LockObj;
import com.syriasoft.hotelservices.lock.RetrofitAPIManager;
import com.ttlock.bl.sdk.util.DigitUtil;
import com.ttlock.bl.sdk.util.GsonUtil;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.sdk.api.INeedLoginListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LogIn extends AppCompatActivity
{

    public static String URL = "https://ratco-solutions.com/HotelServicesTest/TestProject/p/";
    private String Project = "Test";
    private int SelectedHotel = 1 ;
    public static roomDataBase room ;
    private String password ;
    private EditText  passwordEntry ;
    private String url=URL+"insertUniqueRoom.php";
    Activity act = this ;
    private String projectsUrl = URL+"getProjects.php";
    private String buildingsUrl=URL+"getBuildings.php?Hotel=";
    private String floorsUrl = URL+"getFloors.php?buildingId=";
    private String roomsUrl = URL+"getRoomsAndLastOrder.php?Floor=";
    private String typesUrl = URL+"getRoomTypes.php?HotelId=";
    private String getTheRoom = URL+"getRoom.php?id=";
    private FirebaseDatabase database ;
    private int  SelectedBuilding , SelectedFloor , SelectedRoom  ;
    Spinner projects , buildings , floors , types , rooms;
    ArrayAdapter<String> hotelsadapter , buildingsadapter , floorsadapter , roomsAdapter, typesadapter ;
    static BUILDING Buildings[] ;
    static FLOOR Floors [] ;
    static ROOM Rooms [];
    public static ROOM THEROOM ;
    String[] hotelNames , buildingsNames , floorsNames ,roomNums , typeNames ;
    int[] hotelIds , buildingIds , floorsIds , typeIds ;
    static public DatabaseReference  myRefStatus, Hotel , Building ,Floor , Room , myRefTabStatus , myRefPower , myRefDoor , myRefCurtain , myRefDND, myRefRoomStatus , myRefLaundry , myRefCleanup , myRefRoomService , myRefSos , myRefRestaurant , myRefGym ,myRefCheckout ;
    LinearLayout Login , LoginImage ;
    private String logIn = "basharsebai@gmail.com";
    private String pass = "Freesyria579251";
    public static AccountInfo acc ;
    public AccountInfo accountInfo;
    private int pageNo = 1;
    private int pageSize = 100;
    ArrayList<LockObj> lockObjs ;
    public static LockObj myLock ;
    public ArrayList<GatewayObj> mDataList = new ArrayList<GatewayObj>();
    public static GatewayObj myLockGatway ;
    List<HomeBean> Homs ;
    public static HomeBean selectedHome ;
    public static List<Activity> ActList ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ActList = new ArrayList<Activity>();
        ActList.add(act);
        if (ActList.size() >1 )
        {
            for (int i=0;i<ActList.size();i++)
            {
                ActList.get(i).finish();
            }
        }
        room = new roomDataBase(this);  // init Room Database
        //room.logout();
        setActivityItems();                     // Set Activity Buttons
        setTuyaApplication() ;
        Login = (LinearLayout) findViewById(R.id.Login_Layout);
        LoginImage = (LinearLayout) findViewById(R.id.LoginImage);
        LoginImage.setVisibility(View.VISIBLE);
        Login.setVisibility(View.GONE);


        StringRequest request = new StringRequest(Request.Method.POST, projectsUrl, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Log.e("projects" , response) ;
                try
                {
                    JSONArray arr = new JSONArray(response) ;
                    hotelNames = new String[arr.length()];
                    //hotelNames[0] = "-" ;
                    hotelIds = new int[arr.length()];
                    //hotelIds[0] = 0 ;
                    for (int i = 0 ; i <= arr.length() ; i++)
                    {
                        JSONObject row = arr.getJSONObject(i);
                        hotelNames[i] = row.getString("projectName");
                        hotelIds[i] = row.getInt("id");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hotelsadapter = new ArrayAdapter<String>(act ,R.layout.spinners_item ,hotelNames);
                projects.setAdapter(hotelsadapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Calendar x = Calendar.getInstance(Locale.getDefault());
                long time =  x.getTimeInMillis();
                ErrorRegister.rigestError(act ,"NewProject" , 0 , time ,001,error.getMessage(),"getting Hotels List" );
            }
        });

        StringRequest request1 = new StringRequest(Request.Method.GET, buildingsUrl+SelectedHotel, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                Log.e("buildings" , response);
                //d.stop();
                try {

                    JSONArray arr = new JSONArray(response);
                    Buildings = new BUILDING[arr.length()];
                    buildingsNames = new String[arr.length()];
                    //buildingsNames[0] = "-" ;
                    buildingIds = new int[arr.length()];
                    //buildingIds[0] = 0 ;
                    for (int i = 0; i < arr.length(); i++)
                    {
                        JSONObject row = arr.getJSONObject(i);
                        buildingsNames[i] = String.valueOf(row.getInt("buildingNo"));
                        buildingIds[i] = row.getInt("id");
                        String BuildingName = row.getString("buildingName");
                        int bNo = row.getInt("buildingNo") ;
                        int Floors = row.getInt("floorsNumber");
                        int project = row.getInt("projectId");
                        Buildings[i] = new BUILDING(buildingIds[i],project,bNo,BuildingName,Floors);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                buildingsadapter = new ArrayAdapter<String>(act, R.layout.spinners_item, buildingsNames);
                buildings.setAdapter(buildingsadapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Calendar x = Calendar.getInstance(Locale.getDefault());
                long time = x.getTimeInMillis();
                //d.stop();
                ErrorRegister.rigestError(act, "NewUser", 0, time, 002, error.getMessage(), "Getting Room Types");
            }
        });

        projects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                    SelectedHotel = hotelIds[position];
                    LoadingDialog d = new LoadingDialog(act);
                    StringRequest request1 = new StringRequest(Request.Method.GET, buildingsUrl+SelectedHotel, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {
                            Log.e("buildings" , response);
                            d.stop();
                            try {

                                JSONArray arr = new JSONArray(response);
                                Buildings = new BUILDING[arr.length()];
                                buildingsNames = new String[arr.length()];
                                //buildingsNames[0] = "-" ;
                                buildingIds = new int[arr.length()];
                                //buildingIds[0] = 0 ;
                                for (int i = 0; i < arr.length(); i++)
                                {
                                    JSONObject row = arr.getJSONObject(i);
                                    buildingsNames[i] = String.valueOf(row.getInt("buildingNo"));
                                    buildingIds[i] = row.getInt("id");
                                    String BuildingName = row.getString("buildingName");
                                    int bNo = row.getInt("buildingNo") ;
                                    int Floors = row.getInt("floorsNumber");
                                    int project = row.getInt("projectId");
                                    Buildings[i] = new BUILDING(buildingIds[i],project,bNo,BuildingName,Floors);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            buildingsadapter = new ArrayAdapter<String>(act, R.layout.spinners_item, buildingsNames);
                            buildings.setAdapter(buildingsadapter);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Calendar x = Calendar.getInstance(Locale.getDefault());
                            long time = x.getTimeInMillis();
                            d.stop();
                            ErrorRegister.rigestError(act, "NewUser", 0, time, 002, error.getMessage(), "Getting Room Types");
                        }
                    });
                    Volley.newRequestQueue(act).add(request1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        buildings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

                if (Integer.parseInt(buildings.getSelectedItem().toString()) == SelectedBuilding )
                {

                }
                else
                {
                    SelectedBuilding = buildingIds[position];
                    LoadingDialog d = new LoadingDialog(act);
                    StringRequest req = new StringRequest(Request.Method.GET, floorsUrl+SelectedBuilding, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {
                            d.stop();
                            try {

                                JSONArray arr = new JSONArray(response);
                                Floors = new FLOOR[arr.length()];
                                floorsNames = new String[arr.length()];
                                //floorsNames[0] = " " ;
                                floorsIds = new int[arr.length()];
                                //floorsIds[0] = 0 ;
                                for (int i = 0 ; i < arr.length(); i++)
                                {
                                    JSONObject row = arr.getJSONObject(i);
                                    floorsNames[i] = row.getString("floorNumber");
                                    floorsIds[i] = row.getInt("id");
                                    int bId = row.getInt("buildingId");
                                    int fNum = row.getInt("floorNumber");
                                    int rooms = row.getInt("rooms");
                                    Floors[i] = new FLOOR(floorsIds[i],bId,fNum ,rooms);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            d.stop();
                            floorsadapter = new ArrayAdapter<String>(act, R.layout.spinners_item, floorsNames);
                            floors.setAdapter(floorsadapter);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            Calendar x = Calendar.getInstance(Locale.getDefault());
                            long time = x.getTimeInMillis();
                            d.stop();
                            ErrorRegister.rigestError(act, "NewUser", 0, time, 002, error.getMessage(), "Getting Floors ");

                        }
                    });
                    Volley.newRequestQueue(act).add(req);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        floors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (Integer.parseInt(floors.getSelectedItem().toString()) == SelectedFloor)
                {

                }
                else
                {
                    LoadingDialog d = new LoadingDialog(act);
                    SelectedFloor = floorsIds[position] ;
                    StringRequest requ = new StringRequest(Request.Method.GET, roomsUrl+SelectedFloor, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {

                            try {
                                d.stop();
                                JSONArray arr = new JSONArray(response);
                                roomNums = new String[arr.length()];
                                Rooms = new ROOM[arr.length()];
                                for (int i = 0 ; i < arr.length(); i++)
                                {
                                    JSONObject row = arr.getJSONObject(i);
                                    roomNums[i] = String.valueOf(row.getInt("RoomNumber"));
                                    int id = row.getInt("id");
                                    int rNum = row.getInt("RoomNumber");
                                    int status = row.getInt("Status");
                                    int Hotel = row.getInt("hotel");
                                    int b = row.getInt("Building");
                                    int bId = row.getInt("BuildingId");
                                    int f = row.getInt("Floor");
                                    int fId = row.getInt("FloorId");
                                    String rType = row.getString("RoomType");
                                    int ss = row.getInt("SuiteStatus");
                                    int sn = row.getInt("SuiteNumber");
                                    int si = row.getInt("SuiteId");
                                    int rn = row.getInt("ReservationNumber");
                                    int rs = row.getInt("roomStatus");
                                    int t = row.getInt("Tablet");
                                    String dep = row.getString("dep");
                                    int c = row.getInt("Cleanup");
                                    int l = row.getInt("Laundry");
                                    int roomS = row.getInt("RoomService");
                                    String roomserviceText = row.getString("RoomServiceText");
                                    int ch = row.getInt("Checkout");
                                    int res = row.getInt("Restaurant");
                                    int MiniBarCheck = row.getInt("MiniBarCheck");
                                    int fac = row.getInt("Facility");
                                    int sos = row.getInt("SOS");
                                    int dnd = row.getInt("DND");
                                    int PowerSwitch = row.getInt("PowerSwitch");
                                    int DoorSensor = row.getInt("DoorSensor");
                                    int DoorWarning = row.getInt("DoorWarning");
                                    int MotionSensor = row.getInt("MotionSensor");
                                    int Thermostat = row.getInt("Thermostat");
                                    int zbgateway = row.getInt("ZBGateway");
                                    int CurtainSwitch = row.getInt("CurtainSwitch");
                                    int ServiceSwitch = row.getInt("ServiceSwitch");
                                    int lock = row.getInt("lock");
                                    int Switch1 = row.getInt("Switch1");
                                    int Switch2 = row.getInt("Switch2");
                                    int Switch3 = row.getInt("Switch3");
                                    int Switch4 = row.getInt("Switch4");
                                    String LockGateway = row.getString("LockGateway");
                                    String LockName = row.getString("LockName");
                                    int po = row.getInt("powerStatus");
                                    int tempSetpoint = row.getInt("TempSetPoint");
                                    int SetpointInterval = row.getInt("SetPointInterval");
                                    int cu = row.getInt("curtainStatus");
                                    int doo = row.getInt("doorStatus");
                                    int temp = row.getInt("temp");
                                    String token =row.getString("token");
                                    Rooms[i] = new ROOM(id,rNum,Hotel,b,bId,f,fId,rType,ss,sn,si,rn,rs,t,dep,c,l,roomS,ch,res,sos,dnd,PowerSwitch,DoorSensor,MotionSensor,Thermostat,zbgateway,CurtainSwitch,ServiceSwitch,lock,Switch1,Switch2,Switch3,Switch4,LockGateway,LockName,po,cu,doo,temp,token);

                                }
                                roomsAdapter = new ArrayAdapter<String>(act, R.layout.spinners_item, roomNums);
                                rooms.setAdapter(roomsAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("roomRes" , e.getMessage());
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            d.stop();
                        }
                    });

                    Volley.newRequestQueue(act).add(requ);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        rooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                StringRequest rrr = new StringRequest(Request.Method.GET, typesUrl+SelectedHotel, new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String responseee)
                    {
                        Log.e("typeE" , responseee);
                        try
                        {
                            JSONArray arr = new JSONArray(responseee) ;
                            typeNames = new String[arr.length()];
                            //hotelNames[0] = "-" ;
                            typeIds = new int[arr.length()];
                            //hotelIds[0] = 0 ;
                            for (int i = 0 ; i <= arr.length() ; i++)
                            {
                                JSONObject row = arr.getJSONObject(i);
                                typeNames[i] = row.getString("type");
                                typeIds[i] = row.getInt("id");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        typesadapter =  new ArrayAdapter <String> (act ,R.layout.spinners_item,typeNames);
                        types.setAdapter(typesadapter);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                SelectedRoom =Integer.parseInt( rooms.getSelectedItem().toString());
                THEROOM = Rooms[position];
                Volley.newRequestQueue(act).add(rrr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (!isNetworkConnected())
        {
            ToastMaker.MakeToast("لا يوجد اتصال بالانترنت .. تأكد من توفر اتصال بالانترنت" , act );
            act.finish();
        }
        else
        {
            if (room.isLogedIn())
            {
                LoadingDialog d = new LoadingDialog(act);
                StringRequest re = new StringRequest(Request.Method.GET, getTheRoom + LogIn.room.getRoomDBid(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        d.stop();
                        JSONArray arr = null;
                        try
                        {
                            arr = new JSONArray(response);
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject row = arr.getJSONObject(i);
                                int id = row.getInt("id");
                                int rNum = row.getInt("RoomNumber");
                                int status = row.getInt("Status");
                                int Hotel = row.getInt("hotel");
                                int b = row.getInt("Building");
                                int bId = row.getInt("BuildingId");
                                int f = row.getInt("Floor");
                                int fId = row.getInt("FloorId");
                                String rType = row.getString("RoomType");
                                int ss = row.getInt("SuiteStatus");
                                int sn = row.getInt("SuiteNumber");
                                int si = row.getInt("SuiteId");
                                int rn = row.getInt("ReservationNumber");
                                int rs = row.getInt("roomStatus");
                                int t = row.getInt("Tablet");
                                String dep = row.getString("dep");
                                int c = row.getInt("Cleanup");
                                int l = row.getInt("Laundry");
                                int roomS = row.getInt("RoomService");
                                String roomserviceText = row.getString("RoomServiceText");
                                int ch = row.getInt("Checkout");
                                int res = row.getInt("Restaurant");
                                int MiniBarCheck = row.getInt("MiniBarCheck");
                                int fac = row.getInt("Facility");
                                int sos = row.getInt("SOS");
                                int dnd = row.getInt("DND");
                                int PowerSwitch = row.getInt("PowerSwitch");
                                int DoorSensor = row.getInt("DoorSensor");
                                int DoorWarning = row.getInt("DoorWarning");
                                int MotionSensor = row.getInt("MotionSensor");
                                int Thermostat = row.getInt("Thermostat");
                                int zbgateway = row.getInt("ZBGateway");
                                int CurtainSwitch = row.getInt("CurtainSwitch");
                                int ServiceSwitch = row.getInt("ServiceSwitch");
                                int lock = row.getInt("lock");
                                int Switch1 = row.getInt("Switch1");
                                int Switch2 = row.getInt("Switch2");
                                int Switch3 = row.getInt("Switch3");
                                int Switch4 = row.getInt("Switch4");
                                String LockGateway = row.getString("LockGateway");
                                String LockName = row.getString("LockName");
                                int po = row.getInt("powerStatus");
                                int tempSetpoint = row.getInt("TempSetPoint");
                                int SetpointInterval = row.getInt("SetPointInterval");
                                int cu = row.getInt("curtainStatus");
                                int doo = row.getInt("doorStatus");
                                int temp = row.getInt("temp");
                                String token = row.getString("token");
                                THEROOM = new ROOM(id,rNum,Hotel,b,bId,f,fId,rType,ss,sn,si,rn,rs,t,dep,c,l,roomS,ch,res,sos,dnd,PowerSwitch,DoorSensor,MotionSensor,Thermostat,zbgateway,CurtainSwitch,ServiceSwitch,lock,Switch1,Switch2,Switch3,Switch4,LockGateway,LockName,po,cu,doo,temp,token);
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        if (room.getLockName().equals("0"))
                        {
                            checkIfProjectRecorded();
                        }
                        else
                        {
                            getTheLock();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                Volley.newRequestQueue(act).add(re);

            }
            else
            {
                Login.setVisibility(View.VISIBLE);
                LoginImage.setVisibility(View.GONE);
                Volley.newRequestQueue(act).add(request1);
            }
        }
    }

    public void goRegist(View view)
    {
                    if (passwordEntry.getText().toString().length() > 0)
                    {
                        password = passwordEntry.getText().toString();
                        final LoadingDialog dialog = new LoadingDialog(act);
                        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response)
                            {
                                Log.e("login",response);
                                dialog.stop();
                                if (response.equals("0"))
                                {
                                    //error

                                }
                                else if (response.equals("-1"))
                                {
                                    //wrong password
                                    Toast.makeText(act, "كلمة المرور خطأ", Toast.LENGTH_LONG).show();
                                }
                                else if (response.equals("-2"))
                                {
                                    //this room already taken
                                    Toast.makeText(act, "هذه الغرفة مسجلة على جهاز اخر", Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    try
                                    {
                                        room.insertRoom(THEROOM.id,THEROOM.RoomNumber , types.getSelectedItem().toString(), THEROOM.Floor, THEROOM.token , THEROOM.Hotel , THEROOM.Building);
                                        room.insertProject(Project);
                                        database = FirebaseDatabase.getInstance("https://hotelservices-ebe66.firebaseio.com/");
                                        Room = database.getReference(LogIn.room.getProjectName()+"/B"+LogIn.room.getBuilding()+"/F"+LogIn.room.getFloor()+"/R"+LogIn.room.getRoomNumber());
                                        myRefStatus = Room.child("Status");
                                        myRefStatus.setValue(1);
                                    }
                                    catch (Exception e)
                                    {
                                        Calendar x = Calendar.getInstance(Locale.getDefault());
                                        long time =  x.getTimeInMillis();
                                        ErrorRegister.rigestError(act , "NewRoom" , 0 ,time ,19,e.getMessage(),"Error Register Room Variables In Firebase ");
                                    }
                                    Toast.makeText(act, "تم تسجيل الغرفة" , Toast.LENGTH_LONG).show();
                                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                                    d.setTitle("Add Lock");
                                    d.setMessage("Do You Want To Add Lock");
                                    d.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            dialog.cancel();
                                            checkIfProjectRecorded();
                                        }
                                    });
                                    d.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            LockAuth();
                                        }
                                    });
                                    d.create().show();
                                    //act.finish();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dialog.stop();
                                Toast.makeText(act, error.getMessage(), Toast.LENGTH_LONG).show();
                                Calendar x = Calendar.getInstance(Locale.getDefault());
                                long time =  x.getTimeInMillis();
                                ErrorRegister.rigestError(act,"NewUser" , 0 , time , 003,error.getMessage() , "Rigesting New Room " );
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("roomNumber", String.valueOf(rooms.getSelectedItem()));
                                params.put("floor", String.valueOf(floors.getSelectedItem()));
                                params.put("password", password);
                                params.put("hotel" , String.valueOf(SelectedHotel)) ;
                                params.put("RoomType" , types.getSelectedItem().toString()) ;
                                return params ;
                            }
                        };

                        Volley.newRequestQueue(act).add(request);
                    }
                    else
                    {
                        Toast.makeText(this, "يجب ادخال كلمة المرور", Toast.LENGTH_LONG).show();
                    }

    }

    private boolean isNetworkConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void setActivityItems ()
    {
        projects = (Spinner) findViewById(R.id.spinner_projects);
        types = (Spinner) findViewById(R.id.types_spinner);
        passwordEntry = (EditText) findViewById(R.id.passwordEntry);
        buildings = (Spinner) findViewById(R.id.spinner_buildings);
        floors  = (Spinner) findViewById(R.id.spinner_floors);
        rooms = (Spinner) findViewById(R.id.spinner_rooms);
    }

    private void LockAuth()
    {
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        pass = DigitUtil.getMD5(pass);
        Call<String> call = apiService.auth(ApiService.CLIENT_ID, ApiService.CLIENT_SECRET, "password", logIn, pass, ApiService.REDIRECT_URI);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response)
            {
                String json = response.body();
                accountInfo = GsonUtil.toObject(json, AccountInfo.class);
                if (accountInfo != null)
                {
                    if (accountInfo.errcode == 0)
                    {
                        accountInfo.setMd5Pwd(pass);
                        acc = accountInfo;
                        Intent i = new Intent(act,IndexActivity.class);
                        startActivity(i);
                    } else
                    {
                        //ToastMaker.MakeToast(accountInfo.errmsg,act);
                        Calendar x = Calendar.getInstance(Locale.getDefault());
                        long time =  x.getTimeInMillis();
                        ErrorRegister.rigestError(act ,LogIn.room.getProjectName(),LogIn.room.getRoomNumber() , time ,004 ,accountInfo.errmsg , "LogIn To TTlock Account" );
                    }
                } else
                {
                    //ToastMaker.MakeToast(response.message() , act);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t)
            {
                //d.dismiss();
                //ToastMaker.MakeToast(t.getMessage() , act);
                Calendar x = Calendar.getInstance(Locale.getDefault());
                long time =  x.getTimeInMillis();
                ErrorRegister.rigestError(act , LogIn.room.getProjectName() , LogIn.room.getRoomNumber() , time ,004 ,accountInfo.errmsg , "LogIn To TTlock Account" );
            }
        });
    }

    private void getTheLock()
    {
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        pass = DigitUtil.getMD5(pass);
        Call<String> call = apiService.auth(ApiService.CLIENT_ID, ApiService.CLIENT_SECRET, "password", logIn, pass, ApiService.REDIRECT_URI);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response)
            {
                String json = response.body();
                accountInfo = GsonUtil.toObject(json, AccountInfo.class);
                if (accountInfo != null)
                {
                    if (accountInfo.errcode == 0)
                    {
                        accountInfo.setMd5Pwd(pass);
                        acc = accountInfo;
                        Call<String> call1 = apiService.getLockList(ApiService.CLIENT_ID, acc.getAccess_token(), pageNo, pageSize, System.currentTimeMillis());
                        call1.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, retrofit2.Response<String> response)
                            {
                                //mListApapter = new UserLockListAdapter(act ,lockObjs );
                                String json = response.body();
                                if (json.contains("list"))
                                {
                                    try
                                    {
                                        JSONObject jsonObject = new JSONObject(json);
                                        JSONArray array = jsonObject.getJSONArray("list");
                                        lockObjs = GsonUtil.toObject(array.toString(), new TypeToken<ArrayList<LockObj>>(){});
                                        for (int i=0; i<lockObjs.size(); i++)
                                        {
                                            if (lockObjs.get(i).getLockName().equals(LogIn.room.getRoomNumber()+"Lock"))
                                            {
                                                myLock = lockObjs.get(i);
                                                //Toast.makeText(act, myLock.getLockName(), Toast.LENGTH_SHORT).show();
                                                if (room.getLockGateway().equals("0"))
                                                {
                                                    checkIfProjectRecorded();
                                                }
                                                else
                                                {
                                                    getTheLockGatway();
                                                }
                                            }
                                        }
                                        if (myLock == null ) {
                                            AlertDialog.Builder d = new AlertDialog.Builder(act);
                                            d.setTitle("Add Lock");
                                            d.setMessage("Do You Want To Add Lock");
                                            d.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which)
                                                {
                                                    dialog.cancel();
                                                    checkIfProjectRecorded();
                                                }
                                            });
                                            d.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which)
                                                {
                                                    LockAuth();
                                                }
                                            });
                                            d.create().show();
                                        }
                                    }
                                    catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                        Calendar c = Calendar.getInstance(Locale.getDefault());
                                        long time = c.getTimeInMillis();
                                        ErrorRegister.rigestError(act ,LogIn.room.getProjectName(),LogIn.room.getRoomNumber(),time,007,e.getMessage(),"error Getting Locks List");
                                    }
                                }
                                else
                                {
                                    //ToastMaker.MakeToast(json,act);
                                }

                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t)
                            {
                                //ToastMaker.MakeToast(t.getMessage(),act);
                                Calendar c = Calendar.getInstance(Locale.getDefault());
                                long time = c.getTimeInMillis();
                                ErrorRegister.rigestError(act ,LogIn.room.getProjectName(),LogIn.room.getRoomNumber(),time,007,t.getMessage(),"error Getting Locks List");
                            }
                        });
                        //LogInToTuyaAcc() ;

                    } else
                    {
                        //ToastMaker.MakeToast(accountInfo.errmsg,act);
                        Calendar x = Calendar.getInstance(Locale.getDefault());
                        long time =  x.getTimeInMillis();
                        ErrorRegister.rigestError(act ,LogIn.room.getProjectName(),LogIn.room.getRoomNumber() , time ,004 ,accountInfo.errmsg , "LogIn To TTlock Account" );
                    }
                } else
                {
                    //ToastMaker.MakeToast(response.message() , act);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t)
            {
                //d.dismiss();
                //ToastMaker.MakeToast(t.getMessage() , act);
                Calendar x = Calendar.getInstance(Locale.getDefault());
                long time =  x.getTimeInMillis();
                ErrorRegister.rigestError(act , LogIn.room.getProjectName() , LogIn.room.getRoomNumber() , time ,004 ,accountInfo.errmsg , "LogIn To TTlock Account" );
            }
        });



        //piService apiService = RetrofitAPIManager.provideClientApi();

    }

    private void getTheLockGatway()
    {
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.getGatewayList(ApiService.CLIENT_ID, acc.getAccess_token(), pageNo, pageSize, System.currentTimeMillis());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                String json = response.body();
                if (json.contains("list"))
                {
                    try
                    {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray array = jsonObject.getJSONArray("list");
                        ArrayList<GatewayObj> gatewayObjs = GsonUtil.toObject(array.toString(), new TypeToken<ArrayList<GatewayObj>>(){});
                        mDataList = gatewayObjs ;
                        for (int i=0 ; i<mDataList.size();i++)
                        {
                            if (room.getLockGateway().equals(mDataList.get(i).getGatewayName()))
                            {
                                myLockGatway = mDataList.get(i) ;
                            }
                            checkIfProjectRecorded();
                        }
                    }
                    catch (JSONException e)
                    {

                        e.printStackTrace();
                        Calendar c = Calendar.getInstance(Locale.getDefault());
                        long time = c.getTimeInMillis();
                        ErrorRegister.rigestError(act , LogIn.room.getProjectName(),LogIn.room.getRoomNumber(),time , 006 , e.getMessage() , "error getting lock Gateways list");
                    }
                } else
                {
                    //ToastMaker.MakeToast(json,act);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t)
            {
                Calendar c = Calendar.getInstance(Locale.getDefault());
                long time = c.getTimeInMillis();
                ErrorRegister.rigestError(act , LogIn.room.getProjectName(),LogIn.room.getRoomNumber(),time , 006 , t.getMessage() , "error getting lock Gateways list");
                //ToastMaker.MakeToast(t.getMessage(),act);
            }
        });
    }

    private void onlyLogInToTuya()
    {
        TuyaHomeSdk.getUserInstance().loginWithEmail("966", "basharsebai@gmail.com", "Freesyria579251", new ILoginCallback()
        {
            @Override
            public void onSuccess (User user)
            {

                Toast.makeText (act, "Login succeeded, username:" + user.getUsername(), Toast.LENGTH_SHORT).show();
                TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
                    @Override
                    public void onError(String errorCode, String error)
                    {
                        Calendar c  = Calendar.getInstance(Locale.getDefault());
                        long time = c.getTimeInMillis();
                        ErrorRegister.rigestError(act , LogIn.room.getProjectName(),LogIn.room.getRoomNumber() , time ,8 ,error,"getting families from tuya");
                    }
                    @Override
                    public void onSuccess(List<HomeBean> homeBeans)
                    {
                        // do something
                        Homs = homeBeans ;
                        Family_List_Adapter adapter = new Family_List_Adapter(homeBeans);
                        LockAuth();
                    }
                });
            }

            @Override
            public void onError (String code, String error) {

                Toast.makeText (act, "code:" + code + "error:" + error, Toast.LENGTH_SHORT) .show();
                Calendar c = Calendar.getInstance(Locale.getDefault());
                long time = c.getTimeInMillis();
                ErrorRegister.rigestError( act , LogIn.room.getProjectName() , LogIn.room.getRoomNumber() , time ,9,error,"error logging in to tuya account");
            }
        });
    }

    private void LogInToTuyaAcc()
    {
        TuyaHomeSdk.getUserInstance().loginWithEmail("966", "basharsebai@gmail.com", "Freesyria579251", new ILoginCallback()
        {
            @Override
            public void onSuccess (User user)
            {

                Toast.makeText (act, "Login succeeded, username:" + user.getUsername(), Toast.LENGTH_SHORT).show();
                TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
                    @Override
                    public void onError(String errorCode, String error)
                    {
                        Calendar c  = Calendar.getInstance(Locale.getDefault());
                        long time = c.getTimeInMillis();
                        ErrorRegister.rigestError(act , LogIn.room.getProjectName(),LogIn.room.getRoomNumber() , time ,8 ,error,"getting families from tuya");
                    }
                    @Override
                    public void onSuccess(List<HomeBean> homeBeans)
                    {
                        // do something
                        Homs = homeBeans ;
                        Family_List_Adapter adapter = new Family_List_Adapter(homeBeans);
                        //checkIfProjectRecorded();
                        Intent i = new Intent(act,Tuya_Login.class);
                        startActivity(i);

                    }
                });
            }

            @Override
            public void onError (String code, String error) {

                Toast.makeText (act, "code:" + code + "error:" + error, Toast.LENGTH_SHORT) .show();
                Calendar c = Calendar.getInstance(Locale.getDefault());
                long time = c.getTimeInMillis();
                ErrorRegister.rigestError( act , LogIn.room.getProjectName() , LogIn.room.getRoomNumber() , time ,9,error,"error logging in to tuya account");
            }
        });
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

    }

    void checkIfProjectRecorded()
    {

        TuyaHomeSdk.getUserInstance().loginWithEmail("966", "basharsebai@gmail.com", "Freesyria579251", new ILoginCallback()
        {
            @Override
            public void onSuccess (User user)
            {

                //Toast.makeText (act, "Login succeeded, username:" + user.getUsername(), Toast.LENGTH_SHORT).show();
                TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
                    @Override
                    public void onError(String errorCode, String error)
                    {
                        Calendar c  = Calendar.getInstance(Locale.getDefault());
                        long time = c.getTimeInMillis();
                        ErrorRegister.rigestError(act , LogIn.room.getProjectName(),LogIn.room.getRoomNumber() , time ,8 ,error,"getting families from tuya");
                    }
                    @Override
                    public void onSuccess(List<HomeBean> homeBeans)
                    {
                        // do something
                        Homs = homeBeans ;
                        Family_List_Adapter adapter = new Family_List_Adapter(homeBeans);
                        if (LogIn.room.getTuyaProject().equals("0"))
                        {
                            Toast.makeText(act,"No Projects Recorded", Toast.LENGTH_SHORT).show();
                            Intent j = new Intent(act , Tuya_Login.class);
                            startActivity(j);
                        }
                        else
                        {
                            for (int i=0; i<Homs.size(); i++)
                            {
                                if (LogIn.room.getTuyaProject().equals(Homs.get(i).getName()))
                                {
                                    selectedHome = Homs.get(i);
                                    //Toast.makeText(act,selectedHome.getName()+" Project Selected", Toast.LENGTH_SHORT).show();
                                    Intent j = new Intent(act , Tuya_Devices.class);
                                    startActivity(j);
                                }
                            }
                        }

                    }
                });
            }

            @Override
            public void onError (String code, String error) {

                Toast.makeText (act, "code:" + code + "error:" + error, Toast.LENGTH_SHORT) .show();
                Calendar c = Calendar.getInstance(Locale.getDefault());
                long time = c.getTimeInMillis();
                ErrorRegister.rigestError( act , LogIn.room.getProjectName() , LogIn.room.getRoomNumber() , time ,9,error,"error logging in to tuya account");
            }
        });




    }

}
