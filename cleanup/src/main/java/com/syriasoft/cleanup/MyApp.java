package com.syriasoft.cleanup;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.INeedLoginListener;

import java.util.ArrayList;
import java.util.List;

public class MyApp extends Application {
    static Application app;
    static List<ROOM> Rooms;
    public static User My_USER ;
    public static String Project = "";
    public static String TuyaUser = "";
    public static String TuyaPassword = "";
    public static String LockUser = "";
    public static String LockPassword = "";
    public static String URL;
    public static String Token ;
    public static ProjectsVariablesClass ProjectVariables ;
    static String cloudClientId = "d9hyvtdshnm3uvaun59d" , cloudSecret = "825f9def941f456099798ccdc19112e9";

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        Rooms = new ArrayList<>();
        setTuyaApplication();
    }

    public static void setTuyaApplication() {
        TuyaHomeSdk.setDebugMode(true);
        TuyaHomeSdk.init(MyApp.app);
        TuyaHomeSdk.setOnNeedLoginListener(new INeedLoginListener() {
            @Override
            public void onNeedLogin(Context context) {
                Intent intent = new Intent(context, LogIn.class);
                if (!(context instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
            }
        });
    }
}
