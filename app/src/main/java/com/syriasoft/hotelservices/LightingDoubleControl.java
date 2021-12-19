package com.syriasoft.hotelservices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.tuya.smart.android.device.api.ITuyaDeviceMultiControl;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;

public class LightingDoubleControl extends AppCompatActivity {

    Activity act ;
    LinearLayout FirstLayout , SecondLayout ;
    List<DeviceBean> FirstDeviceList , SecondDeviceList ;
    RecyclerView FirstDevicesRec , SeconfDevicesRec ;
    LinearLayoutManager fManager , sManager ;
    DoubleControlFirst_Adapter Fadapter ;
    DoubleControlSecond_Adapter Sadapter ;
    public static DeviceBean FIRST , SECOND ;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lighting_double_control);
        setActivity();
    }

    void setActivity() {
        act = this ;
        FirstLayout = ( LinearLayout) findViewById(R.id.FirstDeviceLayout);
        SecondLayout = (LinearLayout) findViewById(R.id.SecondDeviceLayout);
        FirstDevicesRec = (RecyclerView)  findViewById(R.id.FirstDevicesRecycler);
        SeconfDevicesRec = (RecyclerView)  findViewById(R.id.SecondDevicesRecycler);
        fManager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        sManager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        FirstDevicesRec.setLayoutManager(fManager);
        SeconfDevicesRec.setLayoutManager(sManager);
        FirstDeviceList = new ArrayList<DeviceBean>();
        SecondDeviceList = new ArrayList<DeviceBean>();
        setDevicesButtons();
    }

    void setDevicesButtons() {
        if (FullscreenActivity.THEROOM.getSWITCH1_B() != null ) {
            FirstDeviceList.add(FullscreenActivity.THEROOM.getSWITCH1_B());
            SecondDeviceList.add(FullscreenActivity.THEROOM.getSWITCH1_B());
        }
        if (FullscreenActivity.THEROOM.getSWITCH2_B() != null) {
            FirstDeviceList.add(FullscreenActivity.THEROOM.getSWITCH2_B());
            SecondDeviceList.add(FullscreenActivity.THEROOM.getSWITCH2_B());
        }
        if (FullscreenActivity.THEROOM.getSWITCH3_B() != null) {
            FirstDeviceList.add(FullscreenActivity.THEROOM.getSWITCH3_B());
            SecondDeviceList.add(FullscreenActivity.THEROOM.getSWITCH3_B());
        }
        if (FullscreenActivity.THEROOM.getSWITCH4_B() != null) {
            FirstDeviceList.add(FullscreenActivity.THEROOM.getSWITCH4_B());
            SecondDeviceList.add(FullscreenActivity.THEROOM.getSWITCH4_B());
        }
        Fadapter = new DoubleControlFirst_Adapter(FirstDeviceList);
        FirstDevicesRec.setAdapter(Fadapter);
        Sadapter = new DoubleControlSecond_Adapter(SecondDeviceList);
        SeconfDevicesRec.setAdapter(Sadapter);
    }

    public void nextToSelectDps(View view) {
        if (FIRST == null ) {
            ToastMaker.MakeToast("select first device",act);
            return;
        }
        if (SECOND == null ) {
            ToastMaker.MakeToast("select second device",act);
            return;
        }
        Intent i = new Intent(act,DoubleControlSelectDps.class);
        startActivity(i);
    }
}