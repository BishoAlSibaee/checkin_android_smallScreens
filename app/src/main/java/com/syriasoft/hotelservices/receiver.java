package com.syriasoft.hotelservices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.syriasoft.hotelservices.lock.MyApplication;

public class receiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(MyApp.App,LogIn.class);


    }
}
