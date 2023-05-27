package com.example.mobilecheckdevice.TUYA;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.sdk.api.INeedLoginListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Tuya_Login extends AppCompatActivity {
    Activity act = this ;
    EditText Country , email , password ;
    RecyclerView r , d ;
    LinearLayoutManager l ,ll;
    static HomeBean selectedHome ;
    List<HomeBean> Homs ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.example.mobilecheckdevice.R.layout.tuya__login);
        ///LogIn.ActList.add(act);
        setTuyaApplication() ;
        setActivity();
        goLogIn();
    }

    public void goLogIn()
    {
        final com.example.mobilecheckdevice.lodingDialog d = new com.example.mobilecheckdevice.lodingDialog(act);
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
                            Toast.makeText (act, "Login succeeded, username:" + user.getUsername(), Toast.LENGTH_SHORT).show();
                            getFamilies();
                        }

                        @Override
                        public void onError (String code, String error) {
                            d.stop();
                            Toast.makeText (act, "code:" + code + "error:" + error, Toast.LENGTH_SHORT) .show();
                            Calendar c = Calendar.getInstance(Locale.getDefault());
                            long time = c.getTimeInMillis();
                            //ErrorRegister.rigestError( act , LogIn.room.getProjectName() , LogIn.room.getRoomNumber() , time ,9,error,"error logging in to tuya account");
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
        Country = (EditText) findViewById(com.example.mobilecheckdevice.R.id.Tuya_Login_countryCode);
        email = (EditText) findViewById(com.example.mobilecheckdevice.R.id.Tuya_Login_email);
        password = (EditText) findViewById(com.example.mobilecheckdevice.R.id.Tuya_Login_password);
        r = (RecyclerView) findViewById(com.example.mobilecheckdevice.R.id.family_Recycler);
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
               // ErrorRegister.rigestError(act , LogIn.room.getProjectName(),LogIn.room.getRoomNumber() , time ,8 ,error,"getting families from tuya");
            }
            @Override
            public void onSuccess(List<HomeBean> homeBeans)
            {
                // do something
                Homs = homeBeans ;
                Family_List_Adapter adapter = new Family_List_Adapter(homeBeans);
                r.setAdapter(adapter);
                //checkIfProjectRecorded();
            }
        });
    }

    public void goToDeviceSearch(View view)
    {
        Intent i = new Intent(act , Tuya_Devices.class);
        startActivity(i);
    }

    /*void checkIfProjectRecorded()
    {
        if (LogIn.room.getTuyaProject().equals("0"))
        {
            Toast.makeText(this,"No Projects Recorded", Toast.LENGTH_SHORT).show();
        }
        else
        {
            for (int i=0; i<Homs.size(); i++)
            {
                if (LogIn.room.getTuyaProject().equals(Homs.get(i).getName()))
                {
                    selectedHome = Homs.get(i);
                    Toast.makeText(this,selectedHome.getName()+" Project Selected", Toast.LENGTH_SHORT).show();
                    Intent j = new Intent(act , Tuya_Devices.class);
                    startActivity(j);
                }
            }
        }
    }*/

}