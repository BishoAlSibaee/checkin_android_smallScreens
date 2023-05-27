package com.syriasoft.hotelservices;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.syriasoft.hotelservices.TUYA.Tuya_Login;
import com.syriasoft.hotelservices.lock.LockObj;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.sdk.api.INeedLoginListener;

import java.util.List;

public class MyApp extends Application {
    public static Application App ;
    public static PROJECT THE_PROJECT ;
    public static List<BUILDING> Buildings ;
    public static List<FLOOR> Floors ;
    public static List<ROOM> Rooms ;
    public static List<ROOM_TYPE> Types ;
    public static List<SceneBean> MY_SCENES ;
    public static PROJECT_VARIABLES ProjectVariables ;
    public static BUILDING Building ;
    public static FLOOR Floor ;
    public static ROOM Room ;
    public static HomeBean HOME ;
    public static LockObj BluetoothLock ;
    public static String ProjectName , ProjectURL ,TuyaUser,TuyaPassword,LockUser,LockPassword ;
    static String cloudClientId = "d9hyvtdshnm3uvaun59d" , cloudSecret = "825f9def941f456099798ccdc19112e9";

    public MyApp() {
        App = this ;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App = this ;
        setTuyaApplication();
    }

    void setTuyaApplication() {
        TuyaHomeSdk.setDebugMode(true);
        TuyaHomeSdk.init(App);
        TuyaHomeSdk.setOnNeedLoginListener(new INeedLoginListener() {
            @Override
            public void onNeedLogin(Context context) {
                Intent intent = new Intent(context, Tuya_Login.class);
                if (!(context instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(intent);
            }
        });

    }
}
