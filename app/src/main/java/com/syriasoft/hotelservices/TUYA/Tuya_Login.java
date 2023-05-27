package com.syriasoft.hotelservices.TUYA;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.syriasoft.hotelservices.ErrorRegister;
import com.syriasoft.hotelservices.LoadingDialog;
import com.syriasoft.hotelservices.LogIn;
import com.syriasoft.hotelservices.MyApp;
import com.syriasoft.hotelservices.R;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.sdk.api.INeedLoginListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Tuya_Login extends AppCompatActivity {
    Activity act = this ;
    EditText Country , email , password ,newProjectName;
    RecyclerView r , d ;
    LinearLayoutManager l ,ll;
    static HomeBean selectedHome ;
    List<HomeBean> Homs ;
    Button addHotel ;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.syriasoft.hotelservices.R.layout.tuya__login);
        newProjectName = (EditText) findViewById(R.id.editTextTextPersonName);
        addHotel = (Button) findViewById(R.id.button23);
        LogIn.ActList.add(act);
        setTuyaApplication() ;
        setActivity();
        goLogIn();
    }

    public void goLogIn()
    {
        LoadingDialog d = new LoadingDialog(act);
        String country_text , email_text , password_text ;

        if (Country.getText().toString().length()>0)
        {
            country_text = Country.getText().toString() ;

            if (email.getText().toString().length()>0)
            {
                email_text = email.getText().toString() ;
                if (password.getText().toString().length()>0)
                {
                    password_text = password.getText().toString() ;

                    TuyaHomeSdk.getUserInstance().loginWithEmail(country_text, email_text, password_text, new ILoginCallback()
                    {
                        @Override
                        public void onSuccess (User user)
                        {
                            d.stop();
                            //Toast.makeText (act, "Login succeeded, username:" + user.getUsername(), Toast.LENGTH_SHORT).show();
                            getFamilies();
                            Log.d("loginToya" , "done" );
                        }

                        @Override
                        public void onError (String code, String error) {
                            d.stop();
                            Toast.makeText (act, "code:" + code + "error:" + error, Toast.LENGTH_SHORT) .show();
                            Log.d("loginToya" , error );
                            Calendar c = Calendar.getInstance(Locale.getDefault());
                            long time = c.getTimeInMillis();
                            ErrorRegister.rigestError( act , MyApp.THE_PROJECT.projectName,MyApp.Room.RoomNumber, time ,9,error,"error logging in to tuya account");
                        }
                    });


                }
            }
        }

    }

    void setTuyaApplication()
    {
        TuyaHomeSdk.setDebugMode(true);
        TuyaHomeSdk.init(getApplication());
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

    void setActivity()
    {
        Homs = new ArrayList<HomeBean>();
        Country = (EditText) findViewById(com.syriasoft.hotelservices.R.id.Tuya_Login_countryCode);
        email = (EditText) findViewById(com.syriasoft.hotelservices.R.id.Tuya_Login_email);
        password = (EditText) findViewById(com.syriasoft.hotelservices.R.id.Tuya_Login_password);
        r = (RecyclerView) findViewById(R.id.family_Recycler);
        l = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        r.setLayoutManager(l);
    }

    void getFamilies()
    {
        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
            @Override
            public void onError(String errorCode, String error)
            {
                Calendar c  = Calendar.getInstance(Locale.getDefault());
                long time = c.getTimeInMillis();
                ErrorRegister.rigestError(act ,MyApp.THE_PROJECT.projectName,MyApp.Room.RoomNumber, time ,8 ,error,"getting families from tuya");
            }
            @Override
            public void onSuccess(List<HomeBean> homeBeans)
            {
                // do something
                Homs = homeBeans ;
                Family_List_Adapter adapter = new Family_List_Adapter(homeBeans);
                r.setAdapter(adapter);
                checkIfProjectRecorded();
            }
        });
    }

    public void goToDeviceSearch(View view)
    {
        Intent i = new Intent(act , Tuya_Devices.class);
        startActivity(i);
    }

    void checkIfProjectRecorded() {
//        if (LogIn.room.getTuyaProject().equals("0")) {
//            Toast.makeText(this,"No Projects Recorded", Toast.LENGTH_SHORT).show();
//        }
//        else {
//            for (int i=0; i<Homs.size(); i++) {
//                if (LogIn.room.getTuyaProject().equals(Homs.get(i).getName())) {
//                    selectedHome = Homs.get(i);
//                    Toast.makeText(this,selectedHome.getName()+" Project Selected", Toast.LENGTH_SHORT).show();
//                    Intent j = new Intent(act , Tuya_Devices.class);
//                    startActivity(j);
//                }
//            }
//        }
    }

    public void addHotel(View view) {
        if (newProjectName.getText().toString() != null) {
            AlertDialog.Builder d = new AlertDialog.Builder(act);
            d.setTitle("Add New Hotel ..?");
            d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            d.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    List<String> list = new ArrayList<String>();
                    list.add("Test");
                    TuyaHomeSdk.getHomeManagerInstance().createHome(newProjectName.getText().toString(), 0, 0, "Riyadh",list , new ITuyaHomeResultCallback() {
                        @Override
                        public void onError(String errorCode, String errorMsg)
                        {
                            // do something
                            Log.e("addingNewHome" , errorMsg);
                            Toast.makeText(act,errorMsg,Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onSuccess(HomeBean bean)
                        {
                            // do something
                            getFamilies();
                            Toast.makeText(act,bean.getName(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            d.create().show();
        }
    }
}