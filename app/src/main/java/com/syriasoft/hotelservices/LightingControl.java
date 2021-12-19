package com.syriasoft.hotelservices;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LightingControl extends AppCompatActivity {

    Activity act ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lighting_control);
        setActivity();
    }

    void setActivity() {
        act = this ;
    }

    public void goToDoubleControl(View view) {
        Intent i = new Intent(act,LightingDoubleControl.class);
        startActivity(i);
    }

    public void goToMasterOff(View view) {
        Intent i = new Intent(act,MasterOff.class);
        startActivity(i);
    }

    public void goToScreenButtons(View view) {
        Intent i = new Intent(act,ScreenButtons.class);
        startActivity(i);
    }
}