package com.example.hotelservicesstandalone.TUYA;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelservicesstandalone.R;
import com.example.hotelservicesstandalone.lodingDialog;
import com.tuya.smart.android.hardware.bean.HgwBean;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.api.IGwSearchListener;
import com.tuya.smart.home.sdk.api.ITuyaGwSearcher;
import com.tuya.smart.home.sdk.builder.ActivatorBuilder;
import com.tuya.smart.home.sdk.builder.TuyaGwActivatorBuilder;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaActivator;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.api.ITuyaGateway;
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.enums.ActivatorModelEnum;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Tuya_Devices extends AppCompatActivity {

    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    RecyclerView r ;
    LinearLayoutManager l ;
    static Activity act ;
    static String selectedNetwork ;
    static TextView Name , DeviceName , selectedDevics;
    String Token ;
    EditText pass , newDeviceName ;
    Button turn , doRename;
    public static ITuyaDevice AC ,  mDevice , zDevice[] , zDeviceX ,CurrentDevice ;
    public static ITuyaGateway mgate ;
    LinearLayout renameLayout ;
    com.example.hotelservicesstandalone.lodingDialog dd ;
    LinearLayoutManager  ll;
    RecyclerView devices ;
    public static DeviceBean ACbean , powerBean , zgatwayBean , zdeviceBean[] ;
    public static List<DeviceBean> zigbeeDevices ;
    Calendar ca = Calendar.getInstance(Locale.getDefault());
    Spinner DeviceType ;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.example.hotelservicesstandalone.R.layout.tuya__devices);
        setActivity();
        mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        wifiList = new ArrayList<ScanResult>();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        /*TuyaHomeSdk.getActivatorInstance().getActivatorToken(LogIn.selectedHome.getHomeId(),
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
                        ErrorRegister.rigestError(act , LogIn.room.getProjectName(),LogIn.room.getRoomNumber(),time,11,s+s1,"error Getting Token From Tuya");
                    }
                });
        Button b = (Button) findViewById(R.id.button12);
        getDevicess(b);*/
    }

    public void deviceSearch(View view)
    {
        final com.example.hotelservicesstandalone.lodingDialog d = new com.example.hotelservicesstandalone.lodingDialog(act);
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
                                     //ErrorRegister.rigestError(act,LogIn.room.getProjectName(),LogIn.room.getRoomNumber(),time,12,errorMsg,"error Searching Wifi Device ");
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
                                 }

                                 @Override
                                 public void onStep(String step, Object data)
                                 {
                                     d.stop();
                                     Toast.makeText(act , step , Toast.LENGTH_LONG).show();
                                 }
                             }
                );

        ITuyaActivator mTuyaActivator = TuyaHomeSdk.getActivatorInstance().newMultiActivator(builder);

        mTuyaActivator.start();
    }

    public void Rename()
    {
        if (DeviceType.getSelectedItem().toString() != null )
        {
            final com.example.hotelservicesstandalone.lodingDialog d = new com.example.hotelservicesstandalone.lodingDialog(act);
            CurrentDevice.renameDevice(DeviceType.getSelectedItem().toString(), new IResultCallback() {
                @Override
                public void onError(String code, String error)
                {
                    // Renaming failed
                    d.stop();
                    //ToastMaker.MakeToast(error , act);
                    long time = ca.getTimeInMillis();
                    //ErrorRegister.rigestError(act,LogIn.room.getProjectName(),LogIn.room.getRoomNumber(),time,13,error,"error changing Device Name ");
                }
                @Override
                public void onSuccess()
                {
                    // Renaming succeeded
                    d.stop();
                    //ToastMaker.MakeToast("Name Changed Successfully" , act );
                }
            });
        }
        else
        {
            //ToastMaker.MakeToast("Write New Device Name " , act);
        }

    }

    public void searchGatway(View view)
    {
        final com.example.hotelservicesstandalone.lodingDialog d = new com.example.hotelservicesstandalone.lodingDialog(act);
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
                                                     long time = ca.getTimeInMillis();
                                                     //ErrorRegister.rigestError(act,LogIn.room.getProjectName(),LogIn.room.getRoomNumber(),time,14,errorMsg,"error Searching Wire Zigbee Gateway");
                                                 }

                                                 @Override
                                                 public void onActiveSuccess(DeviceBean devResp)
                                                 {
                                                     d.stop();
                                                    mgate = TuyaHomeSdk.newGatewayInstance(devResp.devId);
                                                     DeviceName.setText(devResp.getName());
                                                     Toast.makeText(act, "Device Saved", Toast.LENGTH_LONG).show();
                                                     renameLayout.setVisibility(View.VISIBLE);
                                                     //newDeviceName.setText(LogIn.room.getRoomNumber()+"GateWay");
                                                 }

                                                 @Override
                                                 public void onStep(String step, Object data) {

                                                 }
                                             }
                                ));

                mITuyaActivator.start() ;
            }
        });

    }

    public void GoToRoom(View view)
    {
        //Intent i = new Intent(act , FullscreenActivity.class);
        //startActivity(i);
    }

    public void getDevicess(View view)
    {
        /*lodingDialog loading = new lodingDialog(act);
        TuyaHomeSdk.newHomeInstance(LogIn.selectedHome.getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean homeBean)
            {
                loading.stop();
                List<DeviceBean> lis = new ArrayList<DeviceBean>();
                lis = homeBean.getDeviceList();
                if (lis.size() == 0)
                {
                    //ToastMaker.MakeToast("no devices" , act );
                }
                else
                {
                    for (int i=0;i<lis.size();i++)
                    {
                        if (lis.get(i).getName().equals(LogIn.room.getRoomNumber()+"Power"))
                        {
                            powerBean = lis.get(i);
                            mDevice = TuyaHomeSdk.newDeviceInstance(powerBean.devId);
                        }
                        else if (lis.get(i).getName().equals(LogIn.room.getRoomNumber()+"ZGatway"))
                        {
                            zgatwayBean = lis.get(i);
                            mgate = TuyaHomeSdk.newGatewayInstance(Tuya_Devices.zgatwayBean.devId);
                        }
                        else if(lis.get(i).getName().equals(LogIn.room.getRoomNumber()+"AC"))
                        {
                            ACbean = lis.get(i);
                            AC = TuyaHomeSdk.newDeviceInstance(Tuya_Devices.ACbean.devId);
                        }
                    }
                    if (powerBean != null && mDevice != null)
                    {
                        Intent i = new Intent(act , FullscreenActivity.class);
                        startActivity(i);
                    }
                    else
                    {
                        //ToastMaker.MakeToast(String.valueOf(lis.size()),act);
                        Device_List_Adapter adapter = new Device_List_Adapter(lis);
                        devices.setLayoutManager(ll);
                        devices.setAdapter(adapter);
                    }

                }
            }

            @Override
            public void onError(String errorCode, String errorMsg)
            {
                loading.stop();
                long time = ca.getTimeInMillis();
                ErrorRegister.rigestError(act,LogIn.room.getProjectName(),LogIn.room.getRoomNumber(),time,16,errorMsg,"error Getting Project Registered Devices");
            }
        });

         */
    }

    public void searchZDevice(View view)
    {
        lodingDialog d = new lodingDialog(act);
        /*TuyaGwSubDevActivatorBuilder builder = new TuyaGwSubDevActivatorBuilder()
                .setDevId(Tuya_Devices.zgatwayBean.devId)
                .setTimeOut(100)
                .setListener(new ITuyaSmartActivatorListener() {

                                 @Override
                                 public void onError(String errorCode, String errorMsg)
                                 {
                                    d.stop();
                                    ToastMaker.MakeToast(errorMsg , act);
                                     long time = ca.getTimeInMillis();
                                     ErrorRegister.rigestError(act,LogIn.room.getProjectName(),LogIn.room.getRoomNumber(),time,17,errorMsg,"error Register Zigbee Device");
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
                                 }

                                 @Override
                                 public void onStep(String step, Object data)
                                 {
                                    d.stop();
                                    ToastMaker.MakeToast(step,act);
                                 }
                             }
                );
        ITuyaActivator mTuyaGWActivator = TuyaHomeSdk.getActivatorInstance(). newGwSubDevActivator(builder);
// Start network configuration
        mTuyaGWActivator.start();

         */
    }

    public void searchWifiNetworks(View view)
    {
        dd = new lodingDialog(act) ;
        mainWifi.startScan();
    }

    void setActivity()
    {
        act = this ;
        //LogIn.ActList.add(act);
        renameLayout = (LinearLayout) findViewById(R.id.RenameLayout);
        renameLayout.setVisibility(View.GONE);
        doRename = (Button) findViewById(R.id.doRename);
        Name = (TextView) findViewById(R.id.wifiNwtwork_Name);
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
        //String [] Types = new String[]{LogIn.room.getRoomNumber()+"Power",LogIn.room.getRoomNumber()+"ZGatway",LogIn.room.getRoomNumber()+"AC",LogIn.room.getRoomNumber()+"DoorSensor",LogIn.room.getRoomNumber()+"MotionSensor",LogIn.room.getRoomNumber()+"Curtain",LogIn.room.getRoomNumber()+"Switch1",LogIn.room.getRoomNumber()+"Switch2",LogIn.room.getRoomNumber()+"Switch3",LogIn.room.getRoomNumber()+"Switch3",LogIn.room.getRoomNumber()+"Switch4"};
        //ArrayAdapter x =  new ArrayAdapter<String>(act ,android.R.layout.simple_spinner_item ,Types);
        //DeviceType.setAdapter(x);
    }

    public void doRename(View view)
    {
        if (CurrentDevice != null)
        {
            Rename();
        }
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
                Calendar ca = Calendar.getInstance(Locale.getDefault());
                long time = ca.getTimeInMillis();
                //ErrorRegister.rigestError(act , LogIn.room.getProjectName(),LogIn.room.getRoomNumber(),time,10,e.getMessage(),"error Getting Wifi Neworks");
            }

        }

    }



}