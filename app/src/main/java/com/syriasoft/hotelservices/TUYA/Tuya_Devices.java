package com.syriasoft.hotelservices.TUYA;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.syriasoft.hotelservices.ErrorRegister;
import com.syriasoft.hotelservices.FullscreenActivity;
import com.syriasoft.hotelservices.LogIn;
import com.syriasoft.hotelservices.MyApp;
import com.syriasoft.hotelservices.R;
import com.syriasoft.hotelservices.ROOM;
import com.syriasoft.hotelservices.ToastMaker;
import com.syriasoft.hotelservices.LoadingDialog;
import com.syriasoft.hotelservices.messageDialog;
import com.tuya.smart.android.hardware.bean.HgwBean;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.api.IGwSearchListener;
import com.tuya.smart.home.sdk.api.ITuyaGwSearcher;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.builder.ActivatorBuilder;
import com.tuya.smart.home.sdk.builder.TuyaGwActivatorBuilder;
import com.tuya.smart.home.sdk.builder.TuyaGwSubDevActivatorBuilder;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaActivator;
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken;
import com.tuya.smart.sdk.api.ITuyaDataCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.api.ITuyaGateway;
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.enums.ActivatorModelEnum;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Tuya_Devices extends AppCompatActivity {

    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    RecyclerView r ;
    LinearLayoutManager l ;
    static Activity act ;
    static String selectedNetwork ;
    static TextView Name , DeviceName , selectedDevics , LOCK , POWER ,GATEWAY,ACTEXT,SERVICE,CURTAIN,DOORSENSOR,MOTIONSENSOR,SWITCH1,SWITCH2,SWITCH3,SWITCH4;
    String Token ;
    EditText pass , newDeviceName ;
    Button turn , doRename;
    public static ITuyaDevice AC ,  mDevice , zDevice[] , zDeviceX ,CurrentDevice ;
    public static ITuyaGateway mgate ;
    LinearLayout renameLayout ;
    LoadingDialog dd ;
    LinearLayoutManager  ll;
    RecyclerView devices ;
    public static DeviceBean CurrentGateway , ACbean , powerBean , zgatwayBean , zdeviceBean[] ;
    public static List<DeviceBean> zigbeeDevices ;
    Calendar ca = Calendar.getInstance(Locale.getDefault());
    Spinner DeviceType ;
    public static ROOM THEROOM ;
    ITuyaActivator mTuyaGWActivator ;
    ITuyaActivator mTuyaActivator ;
    Device_List_Adapter adapter ;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tuya__devices);
        //THEROOM = new ROOM(LogIn.THEROOM.id,LogIn.THEROOM.RoomNumber,"1",LogIn.THEROOM.Building, MyApp.Room.building_id,LogIn.THEROOM.Floor,MyApp.Room.floor_id,LogIn.THEROOM.RoomType,LogIn.THEROOM.SuiteStatus,LogIn.THEROOM.SuiteNumber,LogIn.THEROOM.SuiteId,LogIn.THEROOM.ReservationNumber,LogIn.THEROOM.roomStatus,LogIn.THEROOM.Tablet,LogIn.THEROOM.dep,LogIn.THEROOM.Cleanup,LogIn.THEROOM.Laundry,LogIn.THEROOM.RoomService,LogIn.THEROOM.Checkout,LogIn.THEROOM.Restaurant,LogIn.THEROOM.SOS,LogIn.THEROOM.DND,LogIn.THEROOM.PowerSwitch,LogIn.THEROOM.DoorSensor,LogIn.THEROOM.MotionSensor,LogIn.THEROOM.Thermostat,LogIn.THEROOM.ZBGateway,LogIn.THEROOM.CurtainSwitch,LogIn.THEROOM.ServiceSwitch,LogIn.THEROOM.lock,LogIn.THEROOM.Switch1,LogIn.THEROOM.Switch2,LogIn.THEROOM.Switch3,LogIn.THEROOM.Switch4,,LogIn.THEROOM.LockName,LogIn.THEROOM.powerStatus,LogIn.THEROOM.curtainStatus,LogIn.THEROOM.doorStatus,LogIn.THEROOM.temp,LogIn.THEROOM.token);
        setActivity();
        mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        wifiList = new ArrayList<ScanResult>();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        TuyaHomeSdk.getActivatorInstance().getActivatorToken(LogIn.selectedHome.getHomeId(),
                new ITuyaActivatorGetToken() {
                    @Override
                    public void onSuccess(String token)
                    {
                        Token = token ;
                    }

                    @Override
                    public void onFailure(String s, String s1)
                    {
                        Calendar ca = Calendar.getInstance(Locale.getDefault());
                        long time = ca.getTimeInMillis();
                        ErrorRegister.rigestError(act ,MyApp.THE_PROJECT.projectName,MyApp.Room.RoomNumber,time,11,s+s1,"error Getting Token From Tuya");
                    }
                });
        Button b = (Button) findViewById(R.id.button12);
        getDevicess(b);
    }

    public void deviceSearch(View view)
    {
        LoadingDialog d = new LoadingDialog(act);
        ActivatorBuilder builder = new ActivatorBuilder()
                .setSsid(selectedNetwork)
                .setContext(this)
                .setPassword(pass.getText().toString())
                .setActivatorModel(ActivatorModelEnum.TY_EZ)
                .setTimeOut(100)
                .setToken(Token)
                .setListener(new ITuyaSmartActivatorListener() {

                                 @Override
                                 public void onError(String errorCode, String errorMsg)
                                 {
                                     d.stop();
                                     Toast.makeText(act , errorMsg , Toast.LENGTH_LONG).show();
                                     long time = ca.getTimeInMillis() ;
                                     ErrorRegister.rigestError(act,MyApp.THE_PROJECT.projectName,MyApp.Room.RoomNumber,time,12,errorMsg,"error Searching Wifi Device ");
                                 }

                                 @Override
                                 public void onActiveSuccess(DeviceBean devResp)
                                 {
                                     d.stop();
                                     DeviceName.setText(devResp.getName());
                                     mDevice = TuyaHomeSdk.newDeviceInstance(devResp.getDevId());
                                     //Toast.makeText(act, "Device Saved", Toast.LENGTH_LONG).show();
                                     //turn.setText(mDevice.toString());
                                     Toast.makeText(act, "Device Saved", Toast.LENGTH_LONG).show();
                                     renameLayout.setVisibility(View.VISIBLE);
                                        CurrentDevice = mDevice ;
                                     mTuyaActivator.stop();
                                 }

                                 @Override
                                 public void onStep(String step, Object data)
                                 {
                                     d.stop();
                                     Toast.makeText(act , step , Toast.LENGTH_LONG).show();
                                 }
                             }
                );

        mTuyaActivator = TuyaHomeSdk.getActivatorInstance().newMultiActivator(builder);

        mTuyaActivator.start();
    }

    public void Rename()
    {
        if (DeviceType.getSelectedItem().toString() != null )
        {
            if (CurrentDevice == null && CurrentGateway != null ) {
                CurrentGateway.setName(DeviceType.getSelectedItem().toString());
                messageDialog d = new messageDialog("Rename Done" , "Rename Done Successfully" , act) ;
                CurrentGateway = null ;
            }
            else if (CurrentDevice != null && CurrentGateway == null ) {
                LoadingDialog d = new LoadingDialog(act);
                CurrentDevice.renameDevice(DeviceType.getSelectedItem().toString(), new IResultCallback() {
                    @Override
                    public void onError(String code, String error)
                    {
                        // Renaming failed
                        d.stop();
                        ToastMaker.MakeToast(error , act);
                        long time = ca.getTimeInMillis();
                        ErrorRegister.rigestError(act,MyApp.THE_PROJECT.projectName,MyApp.Room.RoomNumber,time,13,error,"error changing Device Name ");
                    }
                    @Override
                    public void onSuccess()
                    {
                        // Renaming succeeded
                        d.stop();
                        ToastMaker.MakeToast("Name Changed Successfully" , act );
                        CurrentDevice = null ;
                    }
                });
            }
            else if (CurrentGateway == null && CurrentDevice == null ) {
                messageDialog d = new messageDialog("No Device " , "No Device Found To Rename" , act);
            }
        }
        else
        {
            ToastMaker.MakeToast("Write New Device Name " , act);
        }

    }

    public void searchGatway(View view)
    {
        LoadingDialog d = new LoadingDialog(act);
        ITuyaGwSearcher mTuyaGwSearcher = TuyaHomeSdk.getActivatorInstance().newTuyaGwActivator().newSearcher();
        mTuyaGwSearcher.registerGwSearchListener(new IGwSearchListener()
        {
            @Override
            public void onDevFind(HgwBean hgwBean)
            {
                ITuyaActivator mITuyaActivator = TuyaHomeSdk.getActivatorInstance().newGwActivator(
                        new TuyaGwActivatorBuilder()
                                .setToken(Token)
                                .setTimeOut(100)
                                .setContext(act)
                                .setHgwBean(hgwBean)
                                .setListener(new ITuyaSmartActivatorListener() {

                                                 @Override
                                                 public void onError(String errorCode, String errorMsg)
                                                 {
                                                     d.stop();
                                                     long time = ca.getTimeInMillis();
                                                     ErrorRegister.rigestError(act,MyApp.THE_PROJECT.projectName,MyApp.Room.RoomNumber,time,14,errorMsg,"error Searching Wire Zigbee Gateway");
                                                 }

                                                 @Override
                                                 public void onActiveSuccess(DeviceBean devResp)
                                                 {
                                                     d.stop();
                                                     CurrentGateway = devResp ;
                                                     mgate = TuyaHomeSdk.newGatewayInstance(devResp.devId);
                                                     DeviceName.setText(devResp.getName());
                                                     Toast.makeText(act, "Device Saved", Toast.LENGTH_LONG).show();
                                                     renameLayout.setVisibility(View.VISIBLE);
                                                     //newDeviceName.setText(LogIn.room.getRoomNumber()+"GateWay");
                                                 }

                                                 @Override
                                                 public void onStep(String step, Object data) {
                                                    d.stop();
                                                 }
                                             }
                                ));

                mITuyaActivator.start() ;
            }
        });

    }

    public void GoToRoom(View view)
    {
        Intent i = new Intent(act , FullscreenActivity.class);
        startActivity(i);
    }

    public void getDevicess(View view)
    {
        LoadingDialog loading = new LoadingDialog(act);
        TuyaHomeSdk.newHomeInstance(LogIn.selectedHome.getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean homeBean)
            {
                loading.stop();
                //TheDevicesList.clear();
                List<DeviceBean> TheDevicesList = homeBean.getDeviceList();
                adapter = new Device_List_Adapter(TheDevicesList);
                devices.setLayoutManager(ll);
                devices.setAdapter(adapter);
                if (TheDevicesList.size() == 0)
                {
                    ToastMaker.MakeToast("no devices" , act );
                }
                else
                {
                    //ToastMaker.MakeToast(TheDevicesList.get(0).name,act);
                    for (int i=0;i<TheDevicesList.size();i++)
                    {
                        if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"Power"))
                        {
                            powerBean = TheDevicesList.get(i);
                            mDevice = TuyaHomeSdk.newDeviceInstance(powerBean.devId);
                            THEROOM.setPOWER_B(powerBean);
                            THEROOM.setPOWER(TuyaHomeSdk.newDeviceInstance(powerBean.devId));
                            POWER.setText("YES");
                            POWER.setTextColor(Color.GREEN);
                        }
                        else if (TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"ZGatway"))
                        {
                            zgatwayBean = TheDevicesList.get(i);
                            mgate = TuyaHomeSdk.newGatewayInstance(Tuya_Devices.zgatwayBean.devId);
                            GATEWAY.setText("YES");
                            GATEWAY.setTextColor(Color.GREEN);
                            THEROOM.setWiredZBGateway(mgate);
                            mgate.getSubDevList(new ITuyaDataCallback<List<DeviceBean>>() {
                                @Override
                                public void onSuccess(List<DeviceBean> result)
                                {
                                    for (int i=0;i<result.size();i++)
                                    {
                                        if (result.get(i).getName().equals(MyApp.Room.RoomNumber+"DoorSensor"))
                                        {

                                            THEROOM.setDOORSENSOR_B(result.get(i));
                                            THEROOM.setDOORSENSOR(TuyaHomeSdk.newDeviceInstance(THEROOM.getDOORSENSOR_B().devId));
                                            DOORSENSOR.setText("YES");
                                            DOORSENSOR.setTextColor(Color.GREEN);
                                        }
                                        else if (result.get(i).getName().equals(MyApp.Room.RoomNumber+"MotionSensor"))
                                        {
                                            THEROOM.setMOTIONSENSOR_B(result.get(i));
                                            THEROOM.setMOTIONSENSOR(TuyaHomeSdk.newDeviceInstance(THEROOM.getMOTIONSENSOR_B().getDevId()));
                                            MOTIONSENSOR.setText("YES");
                                            MOTIONSENSOR.setTextColor(Color.GREEN);
                                        }
                                        else if (result.get(i).getName().equals(MyApp.Room.RoomNumber+"Curtain"))
                                        {
                                            THEROOM.setCURTAIN_B(result.get(i));
                                            THEROOM.setCURTAIN(TuyaHomeSdk.newDeviceInstance(THEROOM.getCURTAIN_B().getDevId()));
                                            CURTAIN.setText("YES");
                                            CURTAIN.setTextColor(Color.GREEN);
                                        }
                                        else if (result.get(i).getName().equals(MyApp.Room.RoomNumber+"ServiceSwitch"))
                                        {
                                            THEROOM.setSERVICE1_B(result.get(i));
                                            THEROOM.setSERVICE1(TuyaHomeSdk.newDeviceInstance(THEROOM.getSERVICE1_B().getDevId()));
                                            SERVICE.setText("YES");
                                            SERVICE.setTextColor(Color.GREEN);
                                        }
                                        else if (result.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch1"))
                                        {
                                            THEROOM.setSWITCH1_B(result.get(i));
                                            THEROOM.setSWITCH1(TuyaHomeSdk.newDeviceInstance(THEROOM.getSWITCH1_B().getDevId()));
                                            SWITCH1.setText("YES");
                                            SWITCH1.setTextColor(Color.GREEN);
                                        }
                                        else if (result.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch2"))
                                        {
                                            THEROOM.setSWITCH2_B(result.get(i));
                                            THEROOM.setSWITCH2(TuyaHomeSdk.newDeviceInstance(THEROOM.getSWITCH2_B().getDevId()));
                                            SWITCH2.setText("YES");
                                            SWITCH2.setTextColor(Color.GREEN);
                                        }
                                        else if (result.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch3"))
                                        {

                                            THEROOM.setSWITCH3_B(result.get(i));
                                            THEROOM.setSWITCH3(TuyaHomeSdk.newDeviceInstance(THEROOM.getSWITCH3_B().getDevId()));
                                            SWITCH3.setText("YES");
                                            SWITCH3.setTextColor(Color.GREEN);
                                        }
                                        else if (result.get(i).getName().equals(MyApp.Room.RoomNumber+"Switch4"))
                                        {

                                            THEROOM.setSWITCH4_B(result.get(i));
                                            THEROOM.setSWITCH4(TuyaHomeSdk.newDeviceInstance(THEROOM.getSWITCH4_B().getDevId()));
                                            SWITCH4.setText("YES");
                                            SWITCH4.setTextColor(Color.GREEN);
                                        }
                                    }
                                }

                                @Override
                                public void onError(String errorCode, String errorMessage) {

                                }
                            });
                        }
                        else if(TheDevicesList.get(i).getName().equals(MyApp.Room.RoomNumber+"AC"))
                        {
                            ACbean = TheDevicesList.get(i);
                            AC = TuyaHomeSdk.newDeviceInstance(Tuya_Devices.ACbean.devId);
                            THEROOM.setAC_B(ACbean);
                            THEROOM.setAC(TuyaHomeSdk.newDeviceInstance(Tuya_Devices.ACbean.devId));
                            ACTEXT.setText("YES");
                            ACTEXT.setTextColor(Color.GREEN);
                        }
                    }
                    if (powerBean != null && mDevice != null)
                    {
                        Intent i = new Intent(act , FullscreenActivity.class);
                        startActivity(i);
                    }
                    else
                    {
                        adapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onError(String errorCode, String errorMsg)
            {
                loading.stop();
                long time = ca.getTimeInMillis();
                ErrorRegister.rigestError(act,MyApp.THE_PROJECT.projectName,MyApp.Room.RoomNumber,time,16,errorMsg,"error Getting Project Registered Devices");
            }
        });
    }

    public void searchZDevice(View view)
    {
        if (zgatwayBean.getDevId() != null ) {
        LoadingDialog d = new LoadingDialog(act);
        TuyaGwSubDevActivatorBuilder builder = new TuyaGwSubDevActivatorBuilder()
                .setDevId(Tuya_Devices.zgatwayBean.devId)
                .setTimeOut(100)
                .setListener(new ITuyaSmartActivatorListener() {

                                 @Override
                                 public void onError(String errorCode, String errorMsg)
                                 {
                                    d.stop();
                                    ToastMaker.MakeToast(errorMsg , act);
                                     long time = ca.getTimeInMillis();
                                     ErrorRegister.rigestError(act,MyApp.THE_PROJECT.projectName,MyApp.Room.RoomNumber,time,17,errorMsg,"error Register Zigbee Device");
                                 }

                                 @Override
                                 public void onActiveSuccess(DeviceBean devResp)
                                 {
                                     d.stop();
                                     zDeviceX = TuyaHomeSdk.newDeviceInstance(devResp.getDevId());
                                     ToastMaker.MakeToast("Device Saved" , act);
                                     renameLayout.setVisibility(View.VISIBLE);
                                     DeviceName.setText(devResp.getName());
                                     CurrentDevice = zDeviceX ;
                                     mTuyaGWActivator.stop();
                                 }

                                 @Override
                                 public void onStep(String step, Object data)
                                 {
                                    d.stop();
                                    ToastMaker.MakeToast(step,act);
                                 }
                             }
                );

            mTuyaGWActivator = TuyaHomeSdk.getActivatorInstance(). newGwSubDevActivator(builder);
// Start network configuration
            mTuyaGWActivator.start();
        }
        else {
            com.syriasoft.hotelservices.messageDialog d = new messageDialog("Install Gateway First","No Gatway Detected" ,act) ;
        }

    }

    public void searchWifiNetworks(View view)
    {
        dd = new LoadingDialog(act) ;
        mainWifi.startScan();
    }

    void setActivity()
    {
        act = this ;
        LogIn.ActList.add(act);
        renameLayout = (LinearLayout) findViewById(R.id.RenameLayout);
        renameLayout.setVisibility(View.GONE);
        doRename = (Button) findViewById(R.id.doRename);
        Name = (TextView) findViewById(R.id.wifiNwtwork_Name);
        LOCK = (TextView) findViewById(R.id.room_Lock);
        if (LogIn.myLock != null)
        {
            LOCK.setTextColor(Color.GREEN);
            LOCK.setText("YES");
        }
        POWER = (TextView) findViewById(R.id.room_Power);
        POWER.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                if (THEROOM.getPOWER() == null )
                {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Power Switch");
                    d.create().show();
                }
                else
                {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Power ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            setPowerSwitchStatus("0");
                            //Room.getLock().
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }
                return false;
            }
        });
        GATEWAY = (TextView) findViewById(R.id.room_Gateway);
        GATEWAY.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                if (THEROOM.getGATEWAY() == null)
                {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No ZBGateway");
                    d.create().show();
                }
                else
                {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("Delete ZBGateway .. ?");
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
//                            THEROOM.getGATEWAY().removeDevice(new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//
//                                }
//
//                                @Override
//                                public void onSuccess()
//                                {
//                                    setZBGatewayStatus("0");
//                                }
//                            });
                        }
                    });
                }
                return false;
            }
        });
        CURTAIN = (TextView) findViewById(R.id.room_Curtain);
        CURTAIN.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                if (THEROOM.getCURTAIN() == null )
                {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Curtain ");
                    d.create().show();
                }
                else
                {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Curtain ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                            THEROOM.getCURTAIN().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess()
                                {
                                    setCurtainSwitchStatus("0");
                                }
                            });
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }
                return false;
            }
        });
        SERVICE = (TextView) findViewById(R.id.room_Service);
        SERVICE.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                if (THEROOM.getSERVICE1() == null )
                {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Service Switch");
                    d.create().show();
                }
                else
                {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Service Switch ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                            THEROOM.getSERVICE1().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess()
                                {
                                    setServiceSwitchStatus("0");
                                    //door.setText("NO");
                                    //door.setTextColor(Color.RED);
                                }
                            });
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }
                return false;
            }
        });
        DOORSENSOR = (TextView) findViewById(R.id.room_Doorsensor);
        DOORSENSOR.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                if (THEROOM.getDOORSENSOR() == null )
                {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Door Sensor");
                    d.create().show();
                }
                else
                {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Door Sensor ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                            THEROOM.getDOORSENSOR().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess()
                                {
                                    setDoorSensorStatus("0");
                                    //door.setText("NO");
                                    //door.setTextColor(Color.RED);
                                }
                            });
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }


                return false;
            }
        });
        MOTIONSENSOR = (TextView) findViewById(R.id.room_Motion);
        MOTIONSENSOR.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (THEROOM.getMOTIONSENSOR() == null )
                {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Motion Sensor");
                    d.create().show();
                }
                else
                {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Motion Sensor ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                            THEROOM.getMOTIONSENSOR().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess()
                                {
                                    setMotionSensorStatus("0");
                                    //door.setText("NO");
                                    //door.setTextColor(Color.RED);
                                }
                            });
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }

                return false;
            }
        });
        ACTEXT = (TextView) findViewById(R.id.room_AC);
        ACTEXT.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                if (THEROOM.getAC() == null)
                {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No AC Controller");
                    d.create().show();
                }
                else
                {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("Delete Ac Controller .. ?");
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            THEROOM.getAC().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess()
                                {
                                    setThermostatStatus("0");
                                }
                            });
                        }
                    });
                }
                return false;
            }
        });
        SWITCH1 = (TextView) findViewById(R.id.room_Switch1);
        SWITCH1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                if (THEROOM.getSWITCH1() == null )
                {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Switch 1");
                    d.create().show();
                }
                else
                {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Switch 1 ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                            THEROOM.getSWITCH1().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess()
                                {
                                    setSwitch1Status("0");
                                    //door.setText("NO");
                                    //door.setTextColor(Color.RED);
                                }
                            });
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }
                return false;
            }
        });
        SWITCH2 =(TextView) findViewById(R.id.room_Switch2);
        SWITCH2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                if (THEROOM.getSWITCH2() == null )
                {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Switch 2");
                    d.create().show();
                }
                else
                {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Switch 2 ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                            THEROOM.getSWITCH2().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess()
                                {
                                    setSwitch2Status("0");
                                    //door.setText("NO");
                                    //door.setTextColor(Color.RED);
                                }
                            });
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }
                return false;
            }
        });
        SWITCH3 = (TextView) findViewById(R.id.room_Switch3);
        SWITCH3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                if (THEROOM.getSWITCH3() == null )
                {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Switch 3");
                    d.create().show();
                }
                else
                {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Switch 3 ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                            THEROOM.getSWITCH3().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess()
                                {
                                    setSwitch3Status("0");
                                    //door.setText("NO");
                                    //door.setTextColor(Color.RED);
                                }
                            });
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }
                return false;
            }
        });
        SWITCH4 = (TextView) findViewById(R.id.room_Switch4);
        SWITCH4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                if (THEROOM.getSWITCH4() == null )
                {
                    AlertDialog.Builder d = new AlertDialog.Builder(act);
                    d.setTitle("No Switch 4");
                    d.create().show();
                }
                else
                {
                    AlertDialog.Builder b = new AlertDialog.Builder(act);
                    b.setTitle("Delete Switch 4 ?");
                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                            THEROOM.getSWITCH4().removeDevice(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess()
                                {
                                    setSwitch4Status("0");
                                    //door.setText("NO");
                                    //door.setTextColor(Color.RED);
                                }
                            });
                        }
                    });
                    b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    b.create().show();
                }
                return false;
            }
        });
        selectedDevics = (TextView) findViewById(R.id.textView20);
        pass = (EditText) findViewById(R.id.wifi_password);
        devices = (RecyclerView) findViewById(R.id.devices_recyclernn);
        DeviceName = (TextView) findViewById(R.id.Device_Res_Name);
        turn = (Button) findViewById(R.id.button10);
        r = (RecyclerView) findViewById(R.id.wifi_networks);
        ll = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        l = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        r.setLayoutManager(l);
        powerBean = new DeviceBean();
        zgatwayBean = new DeviceBean();
        zdeviceBean = new DeviceBean[2];
        zDevice = new ITuyaDevice[2] ;
        zigbeeDevices = new ArrayList<DeviceBean>();
        DeviceType = (Spinner) findViewById(R.id.spinner_devicetype);
        String [] Types = new String[]{MyApp.Room.RoomNumber+"Power",MyApp.Room.RoomNumber+"ZGatway",MyApp.Room.RoomNumber+"AC",MyApp.Room.RoomNumber+"DoorSensor",MyApp.Room.RoomNumber+"MotionSensor",MyApp.Room.RoomNumber+"Curtain",MyApp.Room.RoomNumber+"ServiceSwitch",MyApp.Room.RoomNumber+"Switch1",MyApp.Room.RoomNumber+"Switch2",MyApp.Room.RoomNumber+"Switch3",MyApp.Room.RoomNumber+"Switch3",MyApp.Room.RoomNumber+"Switch4"};
        ArrayAdapter x =  new ArrayAdapter<String>(act ,R.layout.spinner_item ,Types);
        DeviceType.setAdapter(x);
    }

    public void doRename(View view)
    {
            Rename();
    }

    class WifiReceiver extends BroadcastReceiver
    {
        public void onReceive(Context c, Intent intent)
        {
            try
            {
                wifiList = mainWifi.getScanResults();
                //ToastMaker.MakeToast(String.valueOf(wifiList.size()),act);
                networksList_Adapter adapter = new networksList_Adapter(wifiList);
                dd.stop();
                r.setAdapter(adapter);
            }
            catch (Exception e)
            {
                //ToastMaker.MakeToast(e.getMessage(),act);
                //Calendar ca = Calendar.getInstance(Locale.getDefault());
                //long time = ca.getTimeInMillis();
               // ErrorRegister.rigestError(act , LogIn.room.getProjectName(),LogIn.room.getRoomNumber(),time,10,e.getMessage(),"error Getting Wifi Neworks");
            }

        }

    }


    //Set DB Devices

    void setDoorSensorStatus(String status)
    {
        try
        {
            LoadingDialog loading = new LoadingDialog(act);
            String url = LogIn.URL+"setDoorSensorStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    //ToastMaker.MakeToast("Door "+status+"   " + response , act);
                    if (response.equals("1"))
                    {
                        //myRefDoorSensor.setValue(status);
                        if (status.equals("1"))
                        {
                            //THEROOM.setDOORSENSOR_B(FOUND);
                            //THEROOM.setDOORSENSOR(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
                            DOORSENSOR.setText("YES");
                            DOORSENSOR.setTextColor(Color.GREEN);
                        }
                        else if (status.equals("0"))
                        {
                            THEROOM.setDOORSENSOR_B(null);
                            THEROOM.setDOORSENSOR(null);
                            DOORSENSOR.setText("NO");
                            DOORSENSOR.setTextColor(Color.RED);
                        }
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
                    Params.put("room", String.valueOf(THEROOM.RoomNumber));
                    Params.put("id",String.valueOf(THEROOM.id) );
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
            LoadingDialog loading = new LoadingDialog(act);
            String url = LogIn.URL+"setMotionSensorStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    if (response.equals("1"))
                    {
                        if (status.equals("1"))
                        {
                            //THEROOM.setMOTIONSENSOR_B(FOUND);
                            //Room.setMOTIONSENSOR(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
                            MOTIONSENSOR.setText("YES");
                            MOTIONSENSOR.setTextColor(Color.GREEN);
                        }
                        else if (status.equals("0"))
                        {
                            THEROOM.setMOTIONSENSOR_B(null);
                            THEROOM.setMOTIONSENSOR(null);
                            MOTIONSENSOR.setText("NO");
                            MOTIONSENSOR.setTextColor(Color.RED);
                        }

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
                    Params.put("room", String.valueOf(THEROOM.RoomNumber));
                    Params.put("id",String.valueOf(THEROOM.id) );
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
            LoadingDialog loading = new LoadingDialog(act);
            String url = LogIn.URL+"setCurtainSwitchStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    if (response.equals("1"))
                    {
                        if (status.equals("1"))
                        {
                            //Room.setCURTAIN_B(FOUND);
                            //Room.setCURTAIN(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
                            CURTAIN.setText("YES");
                            CURTAIN.setTextColor(Color.GREEN);
                        }
                        else
                        {
                            THEROOM.setCURTAIN_B(null);
                            THEROOM.setCURTAIN(null);
                            CURTAIN.setText("NO");
                            CURTAIN.setTextColor(Color.RED);
                        }

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
                    Params.put("room", String.valueOf(THEROOM.RoomNumber));
                    Params.put("id",String.valueOf(THEROOM.id) );
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
            LoadingDialog loading = new LoadingDialog(act);
            String url = LogIn.URL+"setSwitch1StatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    if (response.equals("1"))
                    {
                        if (status.equals("1"))
                        {
                            //Room.setSWITCH1_B(FOUND);
                            //Room.setSWITCH1(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
                            SWITCH1.setText("YES");
                            SWITCH1.setTextColor(Color.GREEN);
                        }
                        else
                        {
                            THEROOM.setSWITCH1_B(null);
                            THEROOM.setSWITCH1(null);
                            SWITCH1.setText("NO");
                            SWITCH1.setTextColor(Color.RED);
                        }

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
                    Params.put("room", String.valueOf(THEROOM.RoomNumber));
                    Params.put("id",String.valueOf(THEROOM.id) );
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
            LoadingDialog loading = new LoadingDialog(act);
            String url = LogIn.URL+"setSwitch2StatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    if (response.equals("1"))
                    {
                        if (status.equals("1"))
                        {
                            //Room.setSWITCH2_B(FOUND);
                            //Room.setSWITCH2(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
                            SWITCH2.setText("YES");
                            SWITCH2.setTextColor(Color.GREEN);
                        }
                        else
                        {
                            THEROOM.setSWITCH2_B(null);
                            THEROOM.setSWITCH2(null);
                            SWITCH2.setText("NO");
                            SWITCH2.setTextColor(Color.RED);
                        }

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
                    Params.put("room", String.valueOf(THEROOM.RoomNumber));
                    Params.put("id",String.valueOf(THEROOM.id) );
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
            LoadingDialog loading = new LoadingDialog(act);
            String url = LogIn.URL+"setSwitch3StatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    if (response.equals("1"))
                    {
                        if (status.equals("1"))
                        {
                            //Room.setSWITCH3_B(FOUND);
                            //Room.setSWITCH3(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
                            SWITCH3.setText("YES");
                            SWITCH3.setTextColor(Color.GREEN);
                        }
                        else
                        {
                            THEROOM.setSWITCH3_B(null);
                            THEROOM.setSWITCH3(null);
                            SWITCH3.setText("NO");
                            SWITCH3.setTextColor(Color.RED);
                        }

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
                    Params.put("room", String.valueOf(THEROOM.RoomNumber));
                    Params.put("id",String.valueOf(THEROOM.id) );
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
            LoadingDialog loading =  new LoadingDialog(act);
            String url = LogIn.URL+"setSwitch4StatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    if (response.equals("1"))
                    {
                        if (status.equals("1"))
                        {
                            //Room.setSWITCH4_B(FOUND);
                            //Room.setSWITCH4(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
                            SWITCH4.setText("YES");
                            SWITCH4.setTextColor(Color.GREEN);
                        }
                        else
                        {
                            THEROOM.setSWITCH4_B(null);
                            THEROOM.setSWITCH4(null);
                            SWITCH4.setText("NO");
                            SWITCH4.setTextColor(Color.RED);
                        }

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
                    Params.put("room", String.valueOf(THEROOM.RoomNumber));
                    Params.put("id",String.valueOf(THEROOM.id) );
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
            LoadingDialog loading = new LoadingDialog(act);
            String url = LogIn.URL+"setPowerSwitchStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    Log.e("power " , response +" " + status);
                    if (response.equals("1"))
                    {
                        if (status.equals("1"))
                        {
                            //Room.setPOWER_B(FOUND);
                            //Room.setPOWER(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
                            POWER.setText("YES");
                            POWER.setTextColor(Color.GREEN);
                        }
                        else if (status.equals("0"))
                        {
                            THEROOM.setPOWER_B(null);
                            THEROOM.setPOWER(null);
                            POWER.setText("NO");
                            POWER.setTextColor(Color.RED);
                        }

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
                    Params.put("room", String.valueOf(THEROOM.RoomNumber));
                    Params.put("id",String.valueOf(THEROOM.id) );
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
            LoadingDialog loading = new LoadingDialog(act);
            String url = LogIn.URL+"setServiceSwitchStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    if (response.equals("1"))
                    {
                        if (status.equals("1"))
                        {
                            //Room.setSERVICE_B(FOUND);
                            //Room.setSERVICE(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
                            SERVICE.setText("YES");
                            SERVICE.setTextColor(Color.GREEN);
                        }
                        else if (status.equals("0"))
                        {
                            THEROOM.setSERVICE1_B(null);
                            THEROOM.setSERVICE1(null);
                            SERVICE.setText("NO");
                            SERVICE.setTextColor(Color.RED);
                        }

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
                    Params.put("room", String.valueOf(THEROOM.RoomNumber));
                    Params.put("id",String.valueOf(THEROOM.id) );
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

    void setZBGatewayStatus(String status)
    {
        try
        {
            LoadingDialog loading = new LoadingDialog(act);
            String url = LogIn.URL+"setGatewayStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    if (response.equals("1"))
                    {
                        if (status.equals("1"))
                        {
                            //Room.setGATEWAY_B(FOUND);
                            //Room.setGATEWAY(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
                            GATEWAY.setText("YES");
                            GATEWAY.setTextColor(Color.GREEN);
                        }
                        else
                        {
                            THEROOM.setGATEWAY_B(null);
                            THEROOM.setGATEWAY(null);
                            GATEWAY.setText("NO");
                            GATEWAY.setTextColor(Color.RED);
                        }

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
                    Params.put("room", String.valueOf(THEROOM.RoomNumber));
                    Params.put("id",String.valueOf(THEROOM.id) );
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

    void setThermostatStatus(String status)
    {
        try
        {
            LoadingDialog loading = new LoadingDialog(act);
            String url = LogIn.URL+"setThermostatStatusValue.php";
            StringRequest tabR = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    if (response.equals("1"))
                    {
                        //myRefThermostat.setValue(status);
                        if (status.equals("1"))
                        {
                            //Room.setAC_B(FOUND);
                            //Room.setAC(TuyaHomeSdk.newDeviceInstance(FOUND.getDevId()));
                            ACTEXT.setText("YES");
                            ACTEXT.setTextColor(Color.GREEN);
                        }
                        else if (status.equals("0"))
                        {
                            THEROOM.setAC_B(null);
                            THEROOM.setAC(null);
                            ACTEXT.setText("No");
                            ACTEXT.setTextColor(Color.RED);
                        }

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
                    Params.put("room", String.valueOf(THEROOM.RoomNumber));
                    Params.put("id",String.valueOf(THEROOM.id) );
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