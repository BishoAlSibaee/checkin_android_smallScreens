package com.syriasoft.hotelservices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;

public class ScreenButtons extends AppCompatActivity {

    Activity act ;
    public static RecyclerView CurrentButtonsRecycler , SwitchesButtons , SwitchesRecycler ;
    List<ScreenButton> CurrentButtons ;
    LinearLayoutManager CurrentManager , ButtonsManager , SwitchesManager ;
    List<DeviceBean> Switches ;
    public static List<String> Buttons ;
    public static ScreenButtons_Adapter CurrentAdapter ;
    public static ScreenButtonsSwitches_Adapter SwitchesAdapter ;
    public static ScreenButtonsButtons_Adapter ButtonsAdapter ;
    public static DeviceBean SelectedSwitch ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_buttons);
        setActivity();
    }

    void setActivity() {
        act = this ;
        CurrentButtons = new ArrayList<>();
        Buttons = new ArrayList<>();
        Switches = new ArrayList<>();
        CurrentButtonsRecycler = (RecyclerView) findViewById(R.id.currentButtonsRecycler);
        SwitchesButtons = (RecyclerView) findViewById(R.id.buttonsRecycler);
        SwitchesRecycler = (RecyclerView) findViewById(R.id.switchesRecycler);
        ButtonsManager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        CurrentManager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        SwitchesManager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        CurrentButtonsRecycler.setLayoutManager(CurrentManager);
        SwitchesButtons.setLayoutManager(ButtonsManager);
        SwitchesRecycler.setLayoutManager(SwitchesManager);
        CurrentButtonsRecycler.setNestedScrollingEnabled(false);
        SwitchesButtons.setNestedScrollingEnabled(false);
        SwitchesRecycler.setNestedScrollingEnabled(false);
        CurrentAdapter = new ScreenButtons_Adapter(FullscreenActivity.lightsDB.getScreenButtons());
        CurrentButtonsRecycler.setAdapter(CurrentAdapter);
        if (FullscreenActivity.THEROOM.getSWITCH1_B() != null ) {
            Switches.add(FullscreenActivity.THEROOM.getSWITCH1_B());
        }
        if (FullscreenActivity.THEROOM.getSWITCH2_B() != null ) {
            Switches.add(FullscreenActivity.THEROOM.getSWITCH2_B());
        }
        if (FullscreenActivity.THEROOM.getSWITCH3_B() != null ) {
            Switches.add(FullscreenActivity.THEROOM.getSWITCH3_B());
        }
        if (FullscreenActivity.THEROOM.getSWITCH4_B() != null ) {
            Switches.add(FullscreenActivity.THEROOM.getSWITCH4_B());
        }
        SwitchesAdapter = new ScreenButtonsSwitches_Adapter(Switches);
        SwitchesRecycler.setAdapter(SwitchesAdapter);
    }
}