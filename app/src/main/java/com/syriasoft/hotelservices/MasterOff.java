package com.syriasoft.hotelservices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;

public class MasterOff extends AppCompatActivity {

    Activity act ;
    static RecyclerView SwitchesRecycler , ButtonsRecycler , CurrentMasteroff ;
    List<DeviceBean> Switches ;
    static List<String> Buttons ;
    LinearLayoutManager Smanager , Bmanager , CurrentManager ;
    MasterOffSwitch_Adapter Fadapter ;
    static MasterOffButton_Adapter Badapter ;
    static DeviceBean SelectedSwitch ;
    static String SelectedButton ;
    static CheckBox FirstButton ;
    static Masteroff_Adapter CurrentAdapter ;
    List<MasterOffButton> SELECTED ;
    TextView MasterOffInfo ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_off);
        setActivity();
    }

    void setActivity() {
        act = this ;
        SwitchesRecycler = (RecyclerView) findViewById(R.id.switchesRecycler);
        ButtonsRecycler = (RecyclerView) findViewById(R.id.buttonsRecycler);
        CurrentMasteroff = (RecyclerView) findViewById(R.id.currentMasteroffRecycler);
        FirstButton = (CheckBox) findViewById(R.id.checkBox);
        MasterOffInfo = (TextView) findViewById(R.id.currentMasteroffInfo);
        Switches = new ArrayList<>();
        Buttons = new ArrayList<>();
        Smanager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        Bmanager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        CurrentManager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        SwitchesRecycler.setLayoutManager(Smanager);
        ButtonsRecycler.setLayoutManager(Bmanager);
        CurrentMasteroff.setLayoutManager(CurrentManager);
        SwitchesRecycler.setNestedScrollingEnabled(false);
        ButtonsRecycler.setNestedScrollingEnabled(false);
        CurrentMasteroff.setNestedScrollingEnabled(false);
        if (FullscreenActivity.lightsDB.getMasterOffButtons() != null && FullscreenActivity.lightsDB.getMasterOffButtons().size() > 0) {
            MasterOffInfo.setText("Current Masteroff "+FullscreenActivity.lightsDB.getMasterOffButtons().size());
            CurrentAdapter = new Masteroff_Adapter(FullscreenActivity.lightsDB.getMasterOffButtons());
            CurrentMasteroff.setAdapter(CurrentAdapter);
        }
        if (FullscreenActivity.THEROOM.getSWITCH1_B() != null ) {
            Switches.add(FullscreenActivity.THEROOM.getSWITCH1_B());
        }
        if (FullscreenActivity.THEROOM.getSWITCH2_B() != null) {
            Switches.add(FullscreenActivity.THEROOM.getSWITCH2_B());
        }
        if (FullscreenActivity.THEROOM.getSWITCH3_B() != null) {
            Switches.add(FullscreenActivity.THEROOM.getSWITCH3_B());
        }
        if (FullscreenActivity.THEROOM.getSWITCH4_B() != null) {
            Switches.add(FullscreenActivity.THEROOM.getSWITCH4_B());
        }
        Fadapter = new MasterOffSwitch_Adapter(Switches);
        SwitchesRecycler.setAdapter(Fadapter);
    }
}