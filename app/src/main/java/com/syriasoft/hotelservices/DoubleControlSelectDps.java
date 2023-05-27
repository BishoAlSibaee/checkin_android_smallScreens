package com.syriasoft.hotelservices;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tuya.smart.android.device.api.ITuyaDeviceMultiControl;
import com.tuya.smart.android.device.bean.MultiControlBean;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DoubleControlSelectDps extends AppCompatActivity {

    Activity act ;
    DeviceBean First ,Second ;
    LinearLayout FirstLayout , SecondLayout ;
    int FirstDP=0 , SecondDP=0 ;
    ITuyaDeviceMultiControl iTuyaDeviceMultiControl ;
    TextView fname , sname , fbutton , sbutton ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_control_select_dps);
        First = LightingDoubleControl.FIRST ;
        Second = LightingDoubleControl.SECOND ;
        setActivity();
    }

    void setActivity() {
        act = this ;
        FirstLayout = (LinearLayout) findViewById(R.id.firestDeviceLayout);
        SecondLayout = (LinearLayout) findViewById(R.id.secondDeviceLayout);
        fname = (TextView) findViewById(R.id.device1_name);
        sname = (TextView) findViewById(R.id.device2_name);
        fbutton = (TextView) findViewById(R.id.device1_button);
        sbutton = (TextView) findViewById(R.id.device2_button);
        iTuyaDeviceMultiControl = TuyaHomeSdk.getDeviceMultiControlInstance();
        if(First != null ) {
            fname.setText(First.getName());
            List keys = new ArrayList(First.getDps().keySet());
            for( int i=0; i< keys.size();i++) {
                if (Integer.parseInt(keys.get(i).toString()) < 5) {
                    Button  f = new Button(act);
                    f.setText(keys.get(i).toString());
                    int finalI = i;
                    f.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirstDP = Integer.parseInt(keys.get(finalI).toString());
                            fbutton.setText(keys.get(finalI).toString());
                        }
                    });
                    FirstLayout.addView(f);
                }
            }
        }
        if(Second != null ) {
            sname.setText(Second.getName());
            List keys = new ArrayList(Second.getDps().keySet());
            for( int i=0; i< keys.size();i++) {
                if (Integer.parseInt(keys.get(i).toString()) < 5) {
                    Button  f = new Button(act);
                    f.setText(keys.get(i).toString());
                    int finalI = i;
                    f.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SecondDP = Integer.parseInt(keys.get(finalI).toString());
                            sbutton.setText(keys.get(finalI).toString());
                        }
                    });
                    SecondLayout.addView(f);
                }
            }
        }
    }

    public void createDoubleControl(View view) {

        if (First != null && Second != null && FirstDP != 0 && SecondDP !=0  ) {
            Random r = new Random();
            int x = r.nextInt(30);
            JSONObject groupdetailes1 = new JSONObject(), groupdetailes2 = new JSONObject();
            try {
                groupdetailes1.put("devId", First.devId);
                groupdetailes1.put("dpId", FirstDP);
                groupdetailes1.put("id", x);
                groupdetailes1.put("enable", true);

            } catch (JSONException e) {
            }
            try {
                groupdetailes2.put("devId", Second.devId);
                groupdetailes2.put("dpId", SecondDP);
                groupdetailes2.put("id", x);
                groupdetailes2.put("enable", true);

            } catch (JSONException e) {
            }
            JSONArray arr = new JSONArray();
            arr.put(groupdetailes2);
            arr.put(groupdetailes1);
            JSONObject multiControlBean = new JSONObject();
            try {
                multiControlBean.put("groupName", MyApp.Room.RoomNumber + "Lighting" + x);
                multiControlBean.put("groupType", 1);
                multiControlBean.put("groupDetail", arr);
                multiControlBean.put("id", x);
            } catch (JSONException e) {

            }

            iTuyaDeviceMultiControl.saveDeviceMultiControl(LogIn.selectedHome.getHomeId(), multiControlBean.toString(), new ITuyaResultCallback<MultiControlBean>() {
                @Override
                public void onSuccess(MultiControlBean result) {
                    //ToastUtil.shortToast(mContext,"success");
                    Toast.makeText(act,"double control created",Toast.LENGTH_SHORT);
                    Log.d("switch1Dp1", result.getGroupName());
                    iTuyaDeviceMultiControl.enableMultiControl(x, new ITuyaResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            //ToastUtil.shortToast(mContext,"success");
                            Log.d("switch1Dp1", result.toString());
                            //Toast.makeText(act,"double control enabled",Toast.LENGTH_SHORT);
                        }

                        @Override
                        public void onError(String errorCode, String errorMessage) {
                            //ToastUtil.shortToast(mContext,errorMessage);
                            Log.d("switch1Dp1", errorMessage);
                            //Toast.makeText(act,"failed "+errorMessage,Toast.LENGTH_SHORT);
                        }
                    });
                }

                @Override
                public void onError(String errorCode, String errorMessage) {
                    //ToastUtil.shortToast(mContext,errorMessage);
                    Toast.makeText(act,"failed "+errorMessage,Toast.LENGTH_SHORT);
                    Log.d("switch1Dp1", errorMessage + "here "+x);
                }
            });
        }
        else {
            ToastMaker.MakeToast("please select the buttons ",act);
        }

    }
}