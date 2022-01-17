package com.syriasoft.hotelservices;

import android.app.Application;

public class MyApp extends Application {
    public static Application App ;

    public MyApp() {
        App = this ;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App = this ;
    }
}
