package com.example.hotelservicesstandalone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hotelservicesstandalone.TUYA.Tuya_Login;
import com.example.hotelservicesstandalone.lock.AccountInfo;
import com.example.hotelservicesstandalone.lock.ApiService;
import com.example.hotelservicesstandalone.lock.GatewayObj;
import com.example.hotelservicesstandalone.lock.LockObj;
import com.example.hotelservicesstandalone.lock.RetrofitAPIManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.reflect.TypeToken;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.ControlLockCallback;
import com.ttlock.bl.sdk.constant.ControlAction;
import com.ttlock.bl.sdk.entity.ControlLockResult;
import com.ttlock.bl.sdk.entity.LockError;
import com.ttlock.bl.sdk.util.DigitUtil;
import com.ttlock.bl.sdk.util.GsonUtil;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.api.INeedLoginListener;
import com.tuya.smart.sdk.api.IResultCallback;
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

import retrofit2.Call;
import retrofit2.Callback;

public class Rooms extends AppCompatActivity
{
    private TextView hotelName ;
    private ListView devicesListView , roomsListView ;
    static List<ROOM> list ;
    private String getRoomsUrl = Login.SelectedHotel.URL+"getAllRooms.php" ;
    private Activity act = this ;
    private ListView  RoomsRecycler ;
    static ArrayList<LockObj> Locks ;
    static List<GatewayObj> LockGateways ;
    public AccountInfo accountInfo;
    public static AccountInfo acc;
    private List<HomeBean> Homs;
    private List<DeviceBean> Devices ;
    private List<DeviceBean> UnRecognizedDevices_B ;
    private int removeParameter =0;
    private static String insertServiceOrderUrl = Login.SelectedHotel.URL+"insertServiceOrder.php";
    private static String removeServiceOrderUrl = Login.SelectedHotel.URL+"removeServiceOrder.php";
    private FirebaseDatabase database ;
    private List<DatabaseReference> FireRooms ;
    private boolean[] CLEANUP , LAUNDRY , DND ;
    private Runnable[] TempRonnableList , DoorRunnable ;
    private Handler[] Handlers ;
    private long THE_AC_INTERVAL_TIME = 1000*30 , THEDOORWARNING_INTERVAL = 1000*60*5 ;
    private boolean[] AC_SENARIO_Status , DOORSTATUS;
    private long[] start , period , DoorStart , DoorPeriod ;
    private String[] ClientTemp , TempSetPoint ;
    private Rooms_Adapter_Base adapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rooms);
        setActivity();
        setTuyaApplication();
        getRooms();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getRooms();
        loginTTLock();
    }

    private void setActivity()
    {
        hotelName = (TextView) findViewById(R.id.hotelName);
        hotelName.setText(Login.THEHOTELDB.getHotelName());
        list = new ArrayList<ROOM>();
        Locks = new ArrayList<LockObj>();
        UnRecognizedDevices_B = new ArrayList<DeviceBean>();
        Devices = new ArrayList<DeviceBean>();
        //RoomsRecycler = (ListView) findViewById(R.id.rooms_recycler);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(act, LinearLayoutManager.VERTICAL,false);
        //RoomsRecycler.setLayoutManager(manager);
        roomsListView = (ListView) findViewById(R.id.RoomsListView);
        devicesListView = (ListView) findViewById(R.id.DevicesListView);
        database = FirebaseDatabase.getInstance("https://hotelservices-ebe66.firebaseio.com/");
        FireRooms = new ArrayList<DatabaseReference>();
    }

    private void getRooms()
    {

        final lodingDialog loading = new lodingDialog(act);
        StringRequest re = new StringRequest(Request.Method.POST, getRoomsUrl, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Log.d("rooms" , response);
                loading.stop();
                try
                {

                    JSONArray arr = new JSONArray(response);
                    list.clear();
                    FireRooms.clear();
                    for (int i=0;i<arr.length();i++)
                    {
                        JSONObject row = arr.getJSONObject(i);
                        int id = row.getInt("id");
                        int rNum = row.getInt("RoomNumber");
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
                        int ch = row.getInt("Checkout");
                        int res = row.getInt("Restaurant");
                        int sos = row.getInt("SOS");
                        int dnd = row.getInt("DND");
                        int PowerSwitch = row.getInt("PowerSwitch");
                        int DoorSensor = row.getInt("DoorSensor");
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
                        int cu = row.getInt("curtainStatus");
                        int doo = row.getInt("doorStatus");
                        int temp = row.getInt("temp");
                        String token =row.getString("token");
                        ROOM room = new ROOM(id,rNum,Hotel,b,bId,f,fId,rType,ss,sn,si,rn,rs,t,dep,c,l,roomS,ch,res,sos,dnd,PowerSwitch,DoorSensor,MotionSensor,Thermostat,zbgateway,CurtainSwitch,ServiceSwitch,lock,Switch1,Switch2,Switch3,Switch4,LockGateway,LockName,po,cu,doo,temp,token);
                        room.printRoomOnLog();
                        list.add(room);
                        FireRooms.add(database.getReference(Login.THEHOTELDB.getHotelName()+"/B"+room.Building+"/F"+room.Floor+"/R"+room.RoomNumber));

                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                AC_SENARIO_Status = new boolean[list.size()];
                DOORSTATUS = new boolean[list.size()];
                start = new long[list.size()];
                DoorStart = new long[list.size()];
                period = new long[list.size()];
                DoorPeriod = new long[list.size()];
                ClientTemp = new String[list.size()];
                TempSetPoint = new String[list.size()];
                TempRonnableList = new Runnable[list.size()];
                DoorRunnable = new Runnable[list.size()];
                CLEANUP = new boolean[list.size()];
                LAUNDRY = new boolean[list.size()];
                DND = new boolean[list.size()];
                Handlers = new Handler[list.size()];
                for (int t =0;t<list.size();t++)
                {
                    CLEANUP[t] = false ;
                    LAUNDRY[t] = false ;
                    DND[t] = false ;
                    AC_SENARIO_Status[t] = false ;
                    DOORSTATUS[t] = false ;
                    start[t] = 0 ;
                    DoorStart[t]=0;
                    period[t]=0 ;
                    DoorPeriod[t]=0;
                    ClientTemp[t] = "0" ;
                    TempSetPoint[t] = "250" ;
                    int finalT = t;
                    int finalT1 = t;
                    int finalT2 = t;
                    DoorRunnable[t] = new Runnable() {
                        @Override
                        public void run()
                        {
                            long millis = System.currentTimeMillis() - DoorStart[finalT] ;
                            int seconds = (int) (millis / 1000);
                            int minutes = seconds / 60;
                            seconds = seconds % 60;
                            //Handlers[finalT].postDelayed(this,1000) ;
                            Handler timerDoorHandler = new Handler();
                            timerDoorHandler.postDelayed(this,1000) ;
                            DoorPeriod[finalT] = System.currentTimeMillis() - DoorStart[finalT] ;
                            Log.d("theSTATUSDOOR"+list.get(finalT).RoomNumber , String.valueOf(DOORSTATUS[finalT])+" " +minutes+":"+seconds);
                            if ( DoorPeriod[finalT] >=  THEDOORWARNING_INTERVAL  && DOORSTATUS[finalT])
                            {
                                FireRooms.get(finalT).child("doorStatus").setValue(2);
                                timerDoorHandler.removeCallbacks(DoorRunnable[finalT]);
                            }
                            else if (DoorPeriod[finalT] >=  THEDOORWARNING_INTERVAL  && !DOORSTATUS[finalT])
                            {
                                timerDoorHandler.removeCallbacks(DoorRunnable[finalT]);
                            }

                        }
                    };
                    TempRonnableList[t] = new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            long millis = System.currentTimeMillis() - start[finalT] ;
                            int seconds = (int) (millis / 1000);
                            int minutes = seconds / 60;
                            seconds = seconds % 60;
                            Handler hsndler = new Handler();
                            hsndler.postDelayed(this, 1000);
                            //Handlers[finalT].postDelayed(TempRonnableList[finalT], 1000);
                            period[finalT] = System.currentTimeMillis() - start[finalT] ;
                            Log.d(list.get(finalT).RoomNumber+" theSTATUS " , String.valueOf(AC_SENARIO_Status[finalT])+" "+ClientTemp[finalT]+" " +minutes+":"+seconds);
                            if ( period[finalT] >=  THE_AC_INTERVAL_TIME  && AC_SENARIO_Status[finalT])
                            {
                                if (list.get(finalT2).getAC() != null )
                                {
                                    list.get(finalT2).getAC().publishDps("{\" 2\": "+TempSetPoint[finalT]+"}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess()
                                        {
                                            //ToastMaker.MakeToast("Temp Set to Setpoint " , act);
                                            AC_SENARIO_Status[finalT1] = false ;
                                            hsndler.removeCallbacks(TempRonnableList[finalT]);
                                        }
                                    });
                                }

                            }
                            else if (period[finalT] >=  THE_AC_INTERVAL_TIME  && !AC_SENARIO_Status[finalT])
                            {
                                hsndler.removeCallbacks(TempRonnableList[finalT]);
                            }
                        }
                    };
                }
                adapter = new Rooms_Adapter_Base(list,act);
                roomsListView.setAdapter(adapter);
                getTuyaDevices() ;
                loginTTLock();
                getHotelTempSetpoint();
            }
        }, new Response.ErrorListener()
        {
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
                par.put("Hotel" , String.valueOf(Login.THEHOTELDB.getHotelId()));
                return par;
            }
        };
        Volley.newRequestQueue(act).add(re);
    }

    private void loginTTLock()
    {
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        String user = "basharsebai@gmail.com";
        String pass = "Freesyria579251";
        pass = DigitUtil.getMD5(pass);
        Call<String> call = apiService.auth(ApiService.CLIENT_ID, ApiService.CLIENT_SECRET, "password", user, pass, ApiService.REDIRECT_URI);
        String finalPass = pass;
        call.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response)
            {
                String json = response.body();
                accountInfo = GsonUtil.toObject(json, AccountInfo.class);
                if (accountInfo != null)
                {
                    if (accountInfo.errcode == 0)
                    {
                        //Toast.makeText(act,"login success",Toast.LENGTH_LONG).show();
                        accountInfo.setMd5Pwd(finalPass);
                        acc = accountInfo;
                        getLocks();
                    }
                    else
                    {
                       // Toast.makeText(act,accountInfo.errmsg,Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    //Toast.makeText(act,"acc = null",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t)
            {
                //d.dismiss();
                //ToastMaker.MakeToast(t.getMessage() , act);
                Calendar x = Calendar.getInstance(Locale.getDefault());
                long time =  x.getTimeInMillis();
                //ErrorRegister.rigestError(activ , LogIn.room.getProjectName() , LogIn.room.getRoomNumber() , time ,004 ,accountInfo.errmsg , "LogIn To TTlock Account" );
            }
        });
    }

    private void getLocks()
    {
        final Dialog d = new Dialog(this);
        d.setContentView(R.layout.loading_layout);
        d.setCancelable(false);
        d.show();
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.getLockList(ApiService.CLIENT_ID,acc.getAccess_token(), 1, 100, System.currentTimeMillis());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response)
            {
                d.dismiss();
                String json = response.body();
                if (json.contains("list"))
                {
                    try
                    {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray array = jsonObject.getJSONArray("list");
                        Locks = GsonUtil.toObject(array.toString(), new TypeToken<ArrayList<LockObj>>(){});
                        Log.d("locksNum" ,String.valueOf( Locks.size() ));
                        //Toast.makeText(act,"Locks Are "+String.valueOf( Locks.size()),Toast.LENGTH_LONG).show();
                    }
                    catch (JSONException e)
                    {
                    }
                    setLocks(Locks);
                }
                else
                {
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t)
            {
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

    void getTuyaDevices()
    {
        UnRecognizedDevices_B.clear();
        lodingDialog loading = new lodingDialog(act);
        TuyaHomeSdk.newHomeInstance(Login.THEHOME.getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean homeBean)
            {
                loading.stop();
                Devices = homeBean.getDeviceList();
                String[] dd = new String[Devices.size()];
                for (int i=0;i<Devices.size();i++)
                {
                    dd[i] = Devices.get(i).name ;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,R.layout.spinners_item,dd);
                devicesListView.setAdapter(adapter);
                if (Devices.size() == 0)
                {
                    Toast.makeText(act,"no devices",Toast.LENGTH_LONG).show();
                    Log.d("devicesAre " ,"no devices" );
                }
                else
                {
                    Toast.makeText(act,String.valueOf(Devices.size()),Toast.LENGTH_LONG).show();
                    Log.d("devicesAre " ,String.valueOf(Devices.size()) );
                    for (int i = 0 ; i < Devices.size() ; i++ )
                    {
                        for (int j = 0 ; j < list.size() ; j++ )
                        {
                            if (Devices.get(i).getName().equals(list.get(j).RoomNumber+"Power"))
                            {
                                list.get(j).setPOWER_B(Devices.get(i));
                                list.get(j).setPOWER(TuyaHomeSdk.newDeviceInstance(list.get(j).getPOWER_B().devId));

                            }
                            else if (Devices.get(i).getName().equals(list.get(j).RoomNumber+"ZGatway"))
                            {
                                list.get(j).setGATEWAY_B(Devices.get(i));
                                try
                                {
                                    list.get(j).setGATEWAY(TuyaHomeSdk.newDeviceInstance(list.get(j).getGATEWAY_B().devId));
                                    list.get(j).setWiredZBGateway(TuyaHomeSdk.newGatewayInstance(list.get(j).getGATEWAY_B().devId));
                                }
                                catch (Exception e)
                                {

                                }


                            }
                            else if (Devices.get(i).getName().equals(list.get(j).RoomNumber+"AC"))
                            {
                                list.get(j).setAC_B(Devices.get(i));
                                list.get(j).setAC(TuyaHomeSdk.newDeviceInstance(list.get(j).getAC_B().devId));

                            }
                            else if (Devices.get(i).getName().equals(list.get(j).RoomNumber+"DoorSensor"))
                            {
                                list.get(j).setDOORSENSOR_B(Devices.get(i));
                                list.get(j).setDOORSENSOR(TuyaHomeSdk.newDeviceInstance(list.get(j).getDOORSENSOR_B().devId));

                            }
                            else if (Devices.get(i).getName().equals(list.get(j).RoomNumber+"MotionSensor"))
                            {
                                list.get(j).setMOTIONSENSOR_B(Devices.get(i));
                                list.get(j).setMOTIONSENSOR(TuyaHomeSdk.newDeviceInstance(list.get(j).getMOTIONSENSOR_B().devId));

                            }
                            else if (Devices.get(i).getName().equals(list.get(j).RoomNumber+"Curtain"))
                            {
                                list.get(j).setCURTAIN_B(Devices.get(i));
                                list.get(j).setCURTAIN(TuyaHomeSdk.newDeviceInstance(list.get(j).getCURTAIN_B().devId));

                            }
                            else if (Devices.get(i).getName().equals(list.get(j).RoomNumber+"ServiceSwitch"))
                            {
                                list.get(j).setSERVICE_B(Devices.get(i));
                                list.get(j).setSERVICE(TuyaHomeSdk.newDeviceInstance(list.get(j).getSERVICE_B().devId));

                            }
                            else if (Devices.get(i).getName().equals(list.get(j).RoomNumber+"Switch1"))
                            {
                                list.get(j).setSWITCH1_B(Devices.get(i));
                                list.get(j).setSWITCH1(TuyaHomeSdk.newDeviceInstance(list.get(j).getSWITCH1_B().devId));

                            }
                            else if (Devices.get(i).getName().equals(list.get(j).RoomNumber+"Switch2"))
                            {
                                list.get(j).setSWITCH2_B(Devices.get(i));
                                list.get(j).setSWITCH2(TuyaHomeSdk.newDeviceInstance(list.get(j).getSWITCH2_B().devId));

                            }
                            else if (Devices.get(i).getName().equals(list.get(j).RoomNumber+"Switch3"))
                            {
                                list.get(j).setSWITCH3_B(Devices.get(i));
                                list.get(j).setSWITCH3(TuyaHomeSdk.newDeviceInstance(list.get(j).getSWITCH3_B().devId));

                            }
                            else if (Devices.get(i).getName().equals(list.get(j).RoomNumber+"Switch4"))
                            {
                                list.get(j).setSWITCH4_B(Devices.get(i));
                                list.get(j).setSWITCH4(TuyaHomeSdk.newDeviceInstance(list.get(j).getSWITCH4_B().devId));

                            }
                            else
                            {
                                UnRecognizedDevices_B.add(Devices.get(i));
                            }
                        }
                    }
                    if (UnRecognizedDevices_B.size() > 0 )
                    {
                        Toast.makeText(act,"Some Devices Not Recognized" , Toast.LENGTH_LONG);
                        Log.d("unrecognized " , String.valueOf(UnRecognizedDevices_B.size()+" all "+Devices.size() ));
                    }
                }

                setDevicesListiners();
                setFireRoomsListiner();
            }
            @Override
            public void onError(String errorCode, String errorMsg)
            {
                loading.stop();
                //long time = ca.getTimeInMillis();
                //ErrorRegister.rigestError(act,LogIn.room.getProjectName(),LogIn.room.getRoomNumber(),time,16,errorMsg,"error Getting Project Registered Devices");
            }
        });
    }

    void setLocks(ArrayList<LockObj> Locks)
    {
        for (int j=0;j<Locks.size();j++)
        {
            Log.d("locks" , Locks.get(j).getLockName());
            for (int i=0;i<list.size();i++)
            {
                if (Locks.get(j).getLockName().equals(list.get(i).RoomNumber+"Lock"))
                {
                    list.get(i).setLock(Locks.get(j));
                    break;
                }
            }
        }

    }

    public void resetAllDevices(View view)
    {
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

            String url = Login.SelectedHotel.URL+"removeAllDevices.php";
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

    public void setDevicesListiners()
    {
        Toast.makeText(act,"Rooms are : "+list.size(),Toast.LENGTH_LONG).show();
        Log.d("Rooms" , "Rooms are : "+list.size());
        for (int i=0;i<list.size();i++)
        {
            int finalI3 = i;
            if(list.get(i).getDOORSENSOR_B() != null )
            {
                int finalI = i;
                list.get(i).getDOORSENSOR().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr)
                    {
                        //Toast.makeText(act,list.get(finalI).RoomNumber+"door",Toast.LENGTH_SHORT).show();
                        if (dpStr.get("doorcontact_state") != null )
                        {

                            if (dpStr.get("doorcontact_state").toString().equals("true") )
                            {
                                //ToastMaker.MakeToast("Door is Open" , act);
                                //myRefDoor.setValue("1");
                                FireRooms.get(finalI).child("doorStatus").setValue(1);
                                //thermostatStartTime = System.currentTimeMillis() ;
                                start[finalI] = System.currentTimeMillis() ;
                                DoorStart[finalI] = System.currentTimeMillis() ;
                                AC_SENARIO_Status[finalI] = true ;
                                DOORSTATUS[finalI] = true ;
                                period[finalI] = 0;
                                DoorPeriod[finalI]= 0;
                                TempRonnableList[finalI].run();
                                DoorRunnable[finalI].run();
                                //x[0] = true ;
                                //theThermoPeriod = 0 ;
                                //TempRonnable.run();
                            }
                            else
                            {
                                FireRooms.get(finalI).child("doorStatus").setValue(0);
                                DOORSTATUS[finalI] = false ;
                                //ToastMaker.MakeToast("Door Closed" , act);
                            }
                        }
                    }
                    @Override
                    public void onRemoved(String devId)
                    {
                        Log.d("DoorSensor" , "Removed" );
                        //setDoorSensorStatus(list.get(finalI),"0");
                    }
                    @Override
                    public void onStatusChanged(String devId, boolean online)
                    {
                        Log.d("DoorSensor" , "status changed " + online );

                    }

                    @Override
                    public void onNetworkStatusChanged(String devId, boolean status)
                    {
                        Log.d("DoorSensor" , "network status changed " + status );
                    }

                    @Override
                    public void onDevInfoUpdate(String devId)
                    {
                        Log.d("DoorSensor" , "DevInfo"  );
                    }
                });
            }
            if (list.get(i).getSERVICE_B() != null)
            {
                CLEANUP[i] = Boolean.getBoolean(list.get(i).getSERVICE_B().dps.get("2").toString()) ;
                LAUNDRY[i] = Boolean.getBoolean( list.get(i).getSERVICE_B().dps.get("3").toString());
                DND[i] = Boolean.getBoolean( list.get(i).getSERVICE_B().dps.get("1").toString());

                int finalI1 = i;
                list.get(i).getSERVICE().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr)
                    {
                        Log.d("serviceswitch" , "1 "+list.get(finalI1).getSERVICE_B().dps.get("1").toString()+" 2 "+list.get(finalI1).getSERVICE_B().dps.get("2").toString()+" 3 "+list.get(finalI1).getSERVICE_B().dps.get("3").toString()+ "    c "+CLEANUP[finalI1]+" l "+LAUNDRY[finalI1]+" d "+DND[finalI1]+"    "+dpStr.toString());


                        if (dpStr.get("switch_2") != null)
                        {
                            if (dpStr.get("switch_2").toString().equals("true") && !CLEANUP[finalI1])
                            {
                                CLEANUP[finalI1] = true ;
                                FireRooms.get(finalI1).child("Cleanup").setValue(1);
                                FireRooms.get(finalI1).child("dep").setValue("Cleanup");
                                addCleanupOrder(list.get(finalI1),finalI1);
                                if (list.get(finalI1).getSERVICE_B().dps.get("1").toString().equals("true"))
                                {
                                    DND[finalI1] = false ;
                                    list.get(finalI1).getSERVICE().publishDps("{\" 1\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                    FireRooms.get(finalI1).child("DND").setValue(0);
                                    cancelDNDOrder(list.get(finalI1),finalI1);
                                }
                            }
                            else if (dpStr.get("switch_2").toString().equals("false") && CLEANUP[finalI1])
                            {
                                CLEANUP[finalI1] = false ;
                                cancelCleanupOrder(list.get(finalI1),finalI1);
                                FireRooms.get(finalI1).child("Cleanup").setValue(0);
                                if (DND[finalI1])
                                {
                                    FireRooms.get(finalI1).child("dep").setValue("DND");
                                    //cancelCleanupOrder(list.get(finalI1),finalI1);
                                }
                                else if (LAUNDRY[finalI1])
                                {
                                    FireRooms.get(finalI1).child("dep").setValue("Laundry");
                                }
                                else
                                {
                                    FireRooms.get(finalI1).child("dep").setValue(0);
                                }


                            }
                        }
                        if(dpStr.get("switch_3") != null)
                        {
                            if (dpStr.get("switch_3").toString().equals("true") && !LAUNDRY[finalI1])
                            {
                                LAUNDRY[finalI1] = true ;
                                addLaundryOrder(list.get(finalI1) , finalI1);
                                FireRooms.get(finalI1).child("Laundry").setValue(1);
                                FireRooms.get(finalI1).child("dep").setValue("Laundry");
                                if (list.get(finalI1).getSERVICE_B().dps.get("1").toString().equals("true"))
                                {
                                    list.get(finalI1).getSERVICE().publishDps("{\" 1\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                    DND[finalI1] = false ;
                                    FireRooms.get(finalI1).child("DND").setValue(0);
                                    cancelDNDOrder(list.get(finalI1) , finalI1);
                                }
                            }
                            else if(dpStr.get("switch_3").toString().equals("false") && LAUNDRY[finalI1])
                            {
                                LAUNDRY[finalI1] = false ;
                                cancelLaundryOrder(list.get(finalI1) , finalI1);
                                FireRooms.get(finalI1).child("Laundry").setValue(0);
                                if (DND[finalI1])
                                {
                                    FireRooms.get(finalI1).child("dep").setValue("DND");
                                }
                                else if (CLEANUP[finalI1])
                                {
                                    FireRooms.get(finalI1).child("dep").setValue("Cleanup");
                                }
                                else
                                {
                                    FireRooms.get(finalI1).child("dep").setValue(0);
                                }
                            }
                        }
                        if (dpStr.get("switch_1") != null)
                        {
                            if (dpStr.get("switch_1").toString().equals("true") && !DND[finalI1])
                            {
                                DND[finalI1] = true ;
                                FireRooms.get(finalI1).child("DND").setValue(1);
                                FireRooms.get(finalI1).child("dep").setValue("DND");
                                addDNDOrder(list.get(finalI1) , finalI1);
                                if (list.get(finalI1).getSERVICE_B().dps.get("2").toString().equals("true"))
                                {
                                    list.get(finalI1).getSERVICE().publishDps("{\" 2\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                    CLEANUP[finalI1] = false ;
                                    FireRooms.get(finalI1).child("Cleanup").setValue(0);
                                    cancelCleanupOrder(list.get(finalI1) , finalI1);
                                }
                                if (list.get(finalI1).getSERVICE_B().dps.get("3").toString().equals("true"))
                                {
                                    list.get(finalI1).getSERVICE().publishDps("{\" 3\":false}", new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                    LAUNDRY[finalI1] = false ;
                                    FireRooms.get(finalI1).child("Laundry").setValue(0);
                                    cancelLaundryOrder(list.get(finalI1) , finalI1);
                                }
                            }
                            else if (dpStr.get("switch_1").toString().equals("false") && DND[finalI1])
                            {
                                DND[finalI1] = false ;
                                cancelDNDOrder(list.get(finalI1) , finalI1);
                                FireRooms.get(finalI1).child("DND").setValue(0);
                                if (LAUNDRY[finalI1])
                                {
                                    FireRooms.get(finalI1).child("dep").setValue("Laundry");
                                }
                                else if (CLEANUP[finalI1])
                                {
                                    FireRooms.get(finalI1).child("dep").setValue("Cleanup");
                                }
                                else
                                {
                                    FireRooms.get(finalI1).child("dep").setValue(0);
                                }
                            }
                        }

                    }
                    @Override
                    public void onRemoved(String devId)
                    {
                        setServiceSwitchStatus(list.get(finalI1),"0");
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
            if (list.get(i).getAC_B() !=null)
            {
                int finalI2 = i;
                list.get(i).getAC().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr)
                    {
                        //Toast.makeText(act,list.get(finalI2).RoomNumber+"AC",Toast.LENGTH_LONG).show();
                        if (dpStr.get("temp_current") != null)
                        {
                            double temp = (Integer.parseInt(dpStr.get("temp_current").toString())*0.1);
                            FireRooms.get(finalI2).child("temp").setValue(temp) ;
                        }
                        if ( dpStr.get("temp_set") != null )
                        {
                            if (Double.parseDouble(dpStr.get("temp_set").toString()) !=  Double.parseDouble(TempSetPoint[finalI2]))
                            {
                                ClientTemp[finalI2] = dpStr.get("temp_set").toString();
                            }

                        }
                    }
                    @Override
                    public void onRemoved(String devId)
                    {
                        setThermostatStatus(list.get(finalI2) , "0");
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
            if (list.get(i).getPOWER_B() != null)
            {

                list.get(i).getPOWER().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr)
                    {
                        //Toast.makeText(act,list.get(finalI3).RoomNumber+"power",Toast.LENGTH_LONG).show();
                        Log.d("powerdps" , list.get(finalI3).getPOWER_B().dps.toString() );
                        if (list.get(finalI3).getPOWER_B().dps.get("1").toString().equals("true"))
                        {
                            FireRooms.get(finalI3).child("powerStatus").setValue(1);
                        }
                        else
                        {
                            FireRooms.get(finalI3).child("powerStatus").setValue(0);
                        }
                    }

                    @Override
                    public void onRemoved(String devId)
                    {
                        setPowerSwitchStatus(list.get(finalI3),"0");
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
            if (list.get(i).getCURTAIN_B() != null)
            {
                list.get(i).getCURTAIN().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr)
                    {
                        //Toast.makeText(act,list.get(finalI3).RoomNumber+"curtain",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onRemoved(String devId)
                    {
                        setCurtainSwitchStatus(list.get(finalI3),"0");
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
            if (list.get(i).getMOTIONSENSOR_B() != null )
            {
                list.get(i).getMOTIONSENSOR().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr)
                    {
                        Log.d("motion" , dpStr.toString());
                        //Toast.makeText(act,list.get(finalI3).RoomNumber+"motion",Toast.LENGTH_LONG).show();
                        if (AC_SENARIO_Status[finalI3])
                        {
                            AC_SENARIO_Status[finalI3] = false ;
                        }
                        else
                        {
                            String t ="";
                            if (ClientTemp[finalI3].equals("0"))
                            {
                                t="240";
                            }
                            else
                            {
                                t = ClientTemp[finalI3] ;
                            }
                            String dp = "{\" 2\": "+t+"}";
                            if (list.get(finalI3).getAC() != null )
                            {
                                list.get(finalI3).getAC().publishDps(dp, new IResultCallback() {
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
                    }

                    @Override
                    public void onRemoved(String devId)
                    {
                        setMotionSensorStatus(list.get(finalI3),"0");
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
            if (list.get(i).getSWITCH1_B() != null)
            {
                list.get(i).getSWITCH1().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr)
                    {
                        //Toast.makeText(act,list.get(finalI3).RoomNumber+"switch1",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onRemoved(String devId)
                    {
                        setSwitch1Status(list.get(finalI3),"0");
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
            if (list.get(i).getSWITCH2_B() != null)
            {
                list.get(i).getSWITCH2().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr)
                    {
                        //Toast.makeText(act,list.get(finalI3).RoomNumber+"switch2",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onRemoved(String devId)
                    {
                        setSwitch2Status(list.get(finalI3),"0");
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
            if (list.get(i).getSWITCH3_B() != null)
            {
                list.get(i).getSWITCH3().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr)
                    {
                        //Toast.makeText(act,list.get(finalI3).RoomNumber+"switch3",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onRemoved(String devId)
                    {
                        setSwitch3Status(list.get(finalI3),"0");
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
            if (list.get(i).getSWITCH4_B() != null)
            {
                list.get(i).getSWITCH4().registerDeviceListener(new IDeviceListener() {
                    @Override
                    public void onDpUpdate(String devId, Map<String, Object> dpStr)
                    {
                        //Toast.makeText(act,list.get(finalI3).RoomNumber+"switch4",Toast.LENGTH_LONG).show();
                        Log.d("switch4" , dpStr.toString() );
                        if ( dpStr.get("switch_1") != null )
                        {
                            if (dpStr.get("switch_1").toString().equals("true")) {
                                if (list.get(finalI3).getLock() != null) {
                                    Log.d("switch4", list.get(finalI3).getLock().getLockName());
                                    TTLockClient.getDefault().controlLock(ControlAction.UNLOCK, list.get(finalI3).getLock().getLockData(), list.get(finalI3).getLock().getLockMac(), new ControlLockCallback() {
                                        @Override
                                        public void onControlLockSuccess(ControlLockResult controlLockResult) {
                                            //Toast.makeText(act,"lock is unlock  success!",Toast.LENGTH_LONG).show();
                                            //d.dismiss();
                                            //ToastMaker.MakeToast("Door Opened",act);
                                            Log.d("switch4", "open");
                                            list.get(finalI3).getSWITCH4().publishDps("{\" 1\":false}", new IResultCallback() {
                                                @Override
                                                public void onError(String code, String error) {

                                                }

                                                @Override
                                                public void onSuccess() {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onFail(LockError error) {
                                            // Toast.makeText(UnlockActivity.this,"unLock fail!--" + error.getDescription(),Toast.LENGTH_LONG).show();
                                            //d.dismiss();
                                            //ToastMaker.MakeToast("Open Fail!  "+error,act);
                                        }
                                    });
                                } else {
                                    Log.d("switch4", "Lock in null");
                                }

                            }
                        }
                    }

                    @Override
                    public void onRemoved(String devId)
                    {
                        setSwitch4Status(list.get(finalI3),"0");
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
    }

    public void setFireRoomsListiner()
    {
        //Toast.makeText(act,"rooms "+list.size()+"   fires "+FireRooms.size() ,Toast.LENGTH_LONG).show();
        Log.d("theProblem" , "rooms "+list.size()+"   fires "+FireRooms.size());

        for (int i=0;i<FireRooms.size();i++)
        {
            int finalI = i;
            if (list.get(i).getSERVICE_B() != null)
            {

                FireRooms.get(i).child("Cleanup").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.getValue().toString().equals("0"))
                        {
                            list.get(finalI).getSERVICE().publishDps("{\" 2\":false}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

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
                FireRooms.get(i).child("Laundry").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.getValue().toString().equals("0"))
                        {
                            list.get(finalI).getSERVICE().publishDps("{\" 3\":false}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

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
            FireRooms.get(i).child("TempSetPoint").addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                       if (snapshot.getValue() != null)
                       {
                           try
                           {
                               if (Integer.parseInt(snapshot.getValue().toString()) > 15 )
                               {
                                   String temp = snapshot.getValue().toString() ;
                                   if (temp.length()==2)
                                   {
                                       temp = temp+"0";
                                       TempSetPoint[finalI] = temp ;
                                   }
                                   else if (temp.length()>2)
                                   {
                                       TempSetPoint[finalI] = temp ;
                                   }
                                   //TempSetPoint[finalI] = snapshot.getValue().toString();
                               }

                           }
                           catch (Exception e)
                           {

                           }

                       }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
            FireRooms.get(i).child("SetPointInterval").addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if (snapshot.getValue() != null)
                    {
                        try
                        {
                            THE_AC_INTERVAL_TIME = 1000*60* Integer.parseInt(snapshot.getValue().toString());
                            Log.d("intervalsetpoint" , THE_AC_INTERVAL_TIME+"" );
                        }
                        catch (Exception e)
                        {

                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            FireRooms.get(i).child("DoorWarning").addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if (snapshot.getValue() != null )
                    {
                        try {
                            THEDOORWARNING_INTERVAL = 1000 * 60 * Integer.parseInt(snapshot.getValue().toString());
                            Log.d("Doorinterval", THEDOORWARNING_INTERVAL + "");
                        } catch (Exception e) {

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            FireRooms.get(i).child("roomStatus").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null ){
                        if (snapshot.getValue().toString().equals("2")){
                            if(list.get(finalI).getPOWER() != null ){
                                list.get(finalI).getPOWER().publishDps("{\"1\": true}", new IResultCallback() {
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
                                list.get(finalI).getPOWER().publishDps("{\"2\": true}", new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {
                                        //Toast.makeText(act, "turn on the light failure", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onSuccess() {
                                        //Toast.makeText(act, "turn on the light success", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                list.get(finalI).getPOWER().publishDps("{\"10\": 20}", new IResultCallback() {
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

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    public void setRoomStatusListiner(){
        for (int i=0;i<FireRooms.size();i++){
            int finalI = i;
            FireRooms.get(i).child("roomStatus").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null ){
                        if (snapshot.getValue().toString().equals("2")){
                            list.get(finalI).getPOWER().publishDps("{\"1\": true}", new IResultCallback() {
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
                            list.get(finalI).getPOWER().publishDps("{\"2\": true}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    //Toast.makeText(act, "turn on the light failure", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onSuccess() {
                                    //Toast.makeText(act, "turn on the light success", Toast.LENGTH_SHORT).show();
                                }
                            });
                            list.get(finalI).getPOWER().publishDps("{\"10\": 20}", new IResultCallback() {
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
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    //Add & Cancel Orders

    public void addCleanupOrder(ROOM room , int index)
    {
        try
        {
            final String dep = "Cleanup";
            Calendar x = Calendar.getInstance(Locale.getDefault());
            long timee =  x.getTimeInMillis();

            StringRequest addOrder = new StringRequest(Request.Method.POST, insertServiceOrderUrl , new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    //loading.stop();
                    if (Integer.parseInt(response) > 0 )
                    {
                        //Toast.makeText(act,room.RoomNumber+" CleanUp",Toast.LENGTH_LONG).show();
                        //CLEANUP = true ;
                        room.Cleanup = Integer.parseInt(response);
                        FireRooms.get(index).child(dep).setValue(Integer.parseInt(response));
                        FireRooms.get(index).child("dep").setValue(dep);
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
                    //loading.stop();
                    Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("roomNumber" ,String.valueOf(room.RoomNumber));
                    params.put("time" ,String.valueOf(timee));
                    params.put( "dep" , dep );
                    params.put("Hotel" ,String.valueOf( room.Hotel));
                    params.put("RorS" ,String.valueOf( room.SuiteStatus));
                    params.put("Reservation" ,String.valueOf( room.ReservationNumber));
                    return params;
                }

            };
            //Volley volley = new Volley();
            Volley.newRequestQueue(act).add(addOrder);
        }
        catch (Exception e)
        {

        }
    }

    public void cancelCleanupOrder(ROOM room , int index)
    {
        try
        {
            //lodingDialog loading = new lodingDialog(act);
            final String dep = "Cleanup";
            StringRequest removOrder = new StringRequest(Request.Method.POST, removeServiceOrderUrl , new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    //loading.stop();
                    if (response.equals("1")  )
                    {
                        //Toast.makeText(act,room.RoomNumber+" Cancel CleanUp",Toast.LENGTH_LONG).show();
                        //CLEANUP = false ;
                        room.Cleanup = 0 ;
                        FireRooms.get(index).child(dep).setValue(0);

                        //FireRooms.get(index).child("dep").setValue(0);
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
                    //loading.stop();
                    // Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("id" ,String.valueOf( room.Cleanup));
                    params.put("room" , String.valueOf( room.RoomNumber));
                    params.put("dep" , dep);
                    params.put("Hotel" , String.valueOf( room.Hotel));
                    return params;
                }
            };
            //Volley volley = new Volley();
            Volley.newRequestQueue(act).add(removOrder);
        }
        catch (Exception e )
        {

        }
    }

    public void addLaundryOrder(ROOM room , int index)
    {
        try
        {
            //lodingDialog loading = new lodingDialog(act);
            final String dep = "Laundry";
            Calendar x = Calendar.getInstance(Locale.getDefault());
            long timee =  x.getTimeInMillis();

            StringRequest addOrder = new StringRequest(Request.Method.POST, insertServiceOrderUrl , new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    //loading.stop();
                    if (Integer.parseInt(response) > 0 )
                    {
                        room.Laundry = Integer.parseInt(response);
                        //Toast.makeText(act,room.RoomNumber+" Laundry",Toast.LENGTH_LONG).show();
                        //LAUNDRY = true ;
                        FireRooms.get(index).child(dep).setValue(Integer.parseInt(response));
                        FireRooms.get(index).child("dep").setValue(dep);
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
                    //loading.stop();
                    //Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("roomNumber" ,String.valueOf(room.RoomNumber));
                    params.put("time" ,String.valueOf(timee));
                    params.put("dep" ,dep);
                    params.put("Hotel" ,String.valueOf( room.Hotel));
                    params.put("RorS" ,String.valueOf( room.SuiteStatus));
                    params.put("Reservation" ,String.valueOf( room.ReservationNumber));
                    return params;
                }

            };
            //Volley volley = new Volley();
            Volley.newRequestQueue(act).add(addOrder);
        }
        catch (Exception e)
        {

        }
    }

    public void cancelLaundryOrder(ROOM room , int index)
    {
        try
        {
            //lodingDialog loading = new lodingDialog(act);
            final String dep = "Laundry";
            StringRequest removOrder = new StringRequest(Request.Method.POST, removeServiceOrderUrl , new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    //loading.stop();
                    if (response.equals("1")  )
                    {
                        room.Laundry = 0;
                        //Toast.makeText(act,room.RoomNumber+" Cancel Laundry",Toast.LENGTH_LONG).show();
                        //LAUNDRY = false ;
                        FireRooms.get(index).child(dep).setValue(0);
                        //FireRooms.get(index).child("dep").setValue(0);
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
                    //loading.stop();
                    // Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("id" , String.valueOf( room.Laundry));
                    params.put("room" , String.valueOf(room.RoomNumber));
                    params.put("dep" , dep);
                    params.put("Hotel" , String.valueOf(room.Hotel));
                    return params;
                }
            };
            //Volley volley = new Volley();
            Volley.newRequestQueue(act).add(removOrder);
        }
        catch (Exception e)
        {

        }
    }

    public void addDNDOrder(ROOM room , int index)
    {
        String dep = "DND";
        Calendar x = Calendar.getInstance(Locale.getDefault());
        long timee =  x.getTimeInMillis();
        StringRequest request = new StringRequest(Request.Method.POST, insertServiceOrderUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("DND", response);
                    if (Integer.parseInt(response) > 0)
                    {
                        room.DND = Integer.parseInt(response);
                        //Toast.makeText(act,room.RoomNumber+" DND",Toast.LENGTH_LONG).show();
                        //DND = true ;
                        FireRooms.get(index).child(dep).setValue(Integer.parseInt(response));
                        FireRooms.get(index).child("dep").setValue(dep);
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
                params.put("roomNumber", String.valueOf(room.RoomNumber));
                params.put("time", String.valueOf(timee));
                params.put("dep", "DND");
                params.put("Hotel", String.valueOf(room.Hotel));
                params.put("RorS", String.valueOf(room.SuiteStatus));
                params.put("Reservation", String.valueOf(room.ReservationNumber));
                return params;
            }
        };
        //Volley volley = new Volley();
        Volley.newRequestQueue(act).add(request);
    }

    public void cancelDNDOrder(ROOM room , int index)
    {
        String dep = "DND";
        Calendar x = Calendar.getInstance(Locale.getDefault());
        long timee =  x.getTimeInMillis();
        StringRequest rrr = new StringRequest(Request.Method.POST, removeServiceOrderUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("1"))
                {
                    room.DND = 0 ;
                    //Toast.makeText(act,room.RoomNumber+" Cancel DND",Toast.LENGTH_LONG).show();
                    //DND = false ;
                    FireRooms.get(index).child(dep).setValue(0);
                    //FireRooms.get(index).child("dep").setValue(0);
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
                params.put("id", String.valueOf(room.DND));
                params.put("room", String.valueOf(room.RoomNumber));
                params.put("dep", dep);
                params.put("Hotel", String.valueOf(room.Hotel));
                return params;
            }
        };
        //Volley volley = new Volley();
        Volley.newRequestQueue(act).add(rrr);
    }

    private void getHotelTempSetpoint()
    {
        String url = Login.SelectedHotel.URL+"getTempSetPointAndroid.php";
        StringRequest re = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
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
                            for (int i=0;i<TempSetPoint.length;i++)
                            {
                                TempSetPoint[i] = temp ;
                            }

                        }
                        else if (temp.length()>2)
                        {
                            for (int i=0;i<TempSetPoint.length;i++)
                            {
                                TempSetPoint[i] = temp ;
                            }
                        }
                    }
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
                pars.put("Hotel",String.valueOf(Login.THEHOTELDB.getHotelId()));
                return pars;
            }
        };
        Volley.newRequestQueue(act).add(re);
    }

    public  void OpenTheDoor(LockObj lock)
    {
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

    void setDoorSensorStatus(ROOM Room, String status)
    {
        try
        {
            lodingDialog loading = new lodingDialog(act);
            String url = Login.SelectedHotel.URL+"setDoorSensorStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    //ToastMaker.MakeToast("Door "+status+"   " + response , act);
                    if (response.equals("1"))
                    {
                        Room.setDOORSENSOR_B(null);
                        Room.setDOORSENSOR(null);
                        Room.DoorSensor = 0 ;
                        adapter.notifyDataSetChanged();
                        AlertDialog.Builder d = new AlertDialog.Builder(act);
                        d.setTitle(Room.RoomNumber+"DoorSensor Deleted ");
                        d.setMessage("The Door Sensor Of Room "+Room.RoomNumber+" Has Deleted");
                        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        d.create().show();
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
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(Room.RoomNumber));
                    Params.put("id",String.valueOf(Room.id) );
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

    void setServiceSwitchStatus(ROOM Room, String status)
    {
        try
        {
            lodingDialog loading = new lodingDialog(act);
            String url = Login.SelectedHotel.URL+"setServiceSwitchStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    if (response.equals("1"))
                    {
                        Room.setSERVICE_B(null);
                        Room.setSERVICE(null);
                        Room.ServiceSwitch = 0 ;
                        adapter.notifyDataSetChanged();
                        AlertDialog.Builder d = new AlertDialog.Builder(act);
                        d.setTitle(Room.RoomNumber+"Service Switch Deleted ");
                        d.setMessage("The Service Switch Of Room "+Room.RoomNumber+" Has Deleted");
                        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        d.create().show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    loading.stop();
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(Room.RoomNumber));
                    Params.put("id",String.valueOf(Room.id) );
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

    void setThermostatStatus(ROOM Room, String status)
    {
        try
        {
            lodingDialog loading = new lodingDialog(act);
            String url = Login.SelectedHotel.URL+"setThermostatStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    if (response.equals("1"))
                    {
                        //myRefThermostat.setValue(status);
                        Room.setAC_B(null);
                        Room.setAC(null);
                        Room.Thermostat = 0 ;
                        adapter.notifyDataSetChanged();
                        AlertDialog.Builder d = new AlertDialog.Builder(act);
                        d.setTitle(Room.RoomNumber+"AC Controller Deleted ");
                        d.setMessage("The AC Controller Of Room "+Room.RoomNumber+" Has Deleted");
                        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        d.create().show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    loading.stop();
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(Room.RoomNumber));
                    Params.put("id",String.valueOf(Room.id) );
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

    void setPowerSwitchStatus(ROOM Room, String status)
    {
        try
        {
            lodingDialog loading = new lodingDialog(act);
            String url = Login.SelectedHotel.URL+"setPowerSwitchStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    Log.e("power " , response +" " + status);
                    if (response.equals("1"))
                    {
                        //myRefSwitch4.setValue(status);
                        Room.setPOWER_B(null);
                        Room.setPOWER(null);
                        Room.PowerSwitch = 0 ;
                        adapter.notifyDataSetChanged();
                        AlertDialog.Builder d = new AlertDialog.Builder(act);
                        d.setTitle(Room.RoomNumber+" Power Switch Deleted ");
                        d.setMessage("The Power Switch Of Room "+Room.RoomNumber+" Has Deleted");
                        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        d.create().show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    loading.stop();
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(Room.RoomNumber));
                    Params.put("id",String.valueOf(Room.id) );
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

    void setCurtainSwitchStatus(ROOM Room, String status)
    {
        try
        {
            lodingDialog loading = new lodingDialog(act);
            String url = Login.SelectedHotel.URL+"setCurtainSwitchStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    if (response.equals("1"))
                    {
                        //myRefCurtainSwitch.setValue(status);
                        Room.setCURTAIN_B(null);
                        Room.setCURTAIN(null);
                        Room.CurtainSwitch = 0 ;
                        adapter.notifyDataSetChanged();
                        AlertDialog.Builder d = new AlertDialog.Builder(act);
                        d.setTitle(Room.RoomNumber+" Curtain Switch Deleted ");
                        d.setMessage("The Curtain Switch Of Room "+Room.RoomNumber+" Has Deleted");
                        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        d.create().show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    loading.stop();
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(Room.RoomNumber));
                    Params.put("id",String.valueOf(Room.id) );
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

    void setMotionSensorStatus(ROOM Room, String status)
    {
        try
        {
            lodingDialog loading = new lodingDialog(act);
            String url = Login.SelectedHotel.URL+"setMotionSensorStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    if (response.equals("1"))
                    {
                        //myRefMotionSensor.setValue(status);
                        Room.setMOTIONSENSOR_B(null);
                        Room.setMOTIONSENSOR(null);
                        Room.MotionSensor = 0 ;
                        adapter.notifyDataSetChanged();
                        AlertDialog.Builder d = new AlertDialog.Builder(act);
                        d.setTitle(Room.RoomNumber+" Motion Sensor Deleted ");
                        d.setMessage("The Motion Sensor Of Room "+Room.RoomNumber+" Has Deleted");
                        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        d.create().show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    loading.stop();
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(Room.RoomNumber));
                    Params.put("id",String.valueOf(Room.id) );
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

    void setSwitch1Status(ROOM Room, String status)
    {
        try
        {
            lodingDialog loading = new lodingDialog(act);
            String url = Login.SelectedHotel.URL+"setSwitch1StatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    if (response.equals("1"))
                    {
                        //myRefSwitch1.setValue(status);
                        Room.setSWITCH1_B(null);
                        Room.setSWITCH1(null);
                        Room.Switch1 = 0 ;
                        adapter.notifyDataSetChanged();
                        AlertDialog.Builder d = new AlertDialog.Builder(act);
                        d.setTitle(Room.RoomNumber+" Switch 1 Deleted ");
                        d.setMessage("The Switch 1 Of Room "+Room.RoomNumber+" Has Deleted");
                        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        d.create().show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    loading.stop();
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(Room.RoomNumber));
                    Params.put("id",String.valueOf(Room.id) );
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

    void setSwitch2Status(ROOM Room, String status)
    {
        try
        {
            lodingDialog loading = new lodingDialog(act);
            String url = Login.SelectedHotel.URL+"setSwitch2StatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    if (response.equals("1"))
                    {
                        //myRefSwitch2.setValue(status);
                        Room.setSWITCH2_B(null);
                        Room.setSWITCH2(null);
                        Room.Switch2=0;
                        adapter.notifyDataSetChanged();
                        AlertDialog.Builder d = new AlertDialog.Builder(act);
                        d.setTitle(Room.RoomNumber+" Switch 2 Deleted ");
                        d.setMessage("The Switch 2 Of Room "+Room.RoomNumber+" Has Deleted");
                        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        d.create().show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    loading.stop();
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(Room.RoomNumber));
                    Params.put("id",String.valueOf(Room.id) );
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

    void setSwitch3Status(ROOM Room, String status)
    {
        try
        {
            lodingDialog loading = new lodingDialog(act);
            String url = Login.SelectedHotel.URL+"setSwitch3StatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    if (response.equals("1"))
                    {
                        //myRefSwitch3.setValue(status);
                        Room.setSWITCH3_B(null);
                        Room.setSWITCH3(null);
                        Room.Switch3 = 0 ;
                        adapter.notifyDataSetChanged();
                        AlertDialog.Builder d = new AlertDialog.Builder(act);
                        d.setTitle(Room.RoomNumber+" Switch 3 Deleted ");
                        d.setMessage("The Switch 3 Of Room "+Room.RoomNumber+" Has Deleted");
                        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        d.create().show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    loading.stop();
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(Room.RoomNumber));
                    Params.put("id",String.valueOf(Room.id) );
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

    void setSwitch4Status(ROOM Room, String status)
    {
        try
        {
            lodingDialog loading =  new lodingDialog(act);
            String url = Login.SelectedHotel.URL+"setSwitch4StatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    if (response.equals("1"))
                    {
                        //myRefSwitch4.setValue(status);
                        Room.setSWITCH4_B(null);
                        Room.setSWITCH4(null);
                        Room.Switch4 = 0 ;
                        adapter.notifyDataSetChanged();
                        AlertDialog.Builder d = new AlertDialog.Builder(act);
                        d.setTitle(Room.RoomNumber+" Switch 4 Deleted ");
                        d.setMessage("The Switch 4 Of Room "+Room.RoomNumber+" Has Deleted");
                        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        d.create().show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    loading.stop();
                    //ToastMaker.MakeToast(error.getMessage() , act);
                    //Log.e("Tablet" , error.getMessage() );
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> Params = new HashMap<String,String>();
                    Params.put("room", String.valueOf(Room.RoomNumber));
                    Params.put("id",String.valueOf(Room.id) );
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
}