package com.example.hotelservicesstandalone;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.android.volley.toolbox.StringRequest;

public class MyApp  extends Application {

    @Override
    public void onTerminate() {
        super.onTerminate();
        Intent i = new Intent(this,Login.class);
        startActivity(i);
        Log.d("AppTerminated","Terminated");
    }


}
