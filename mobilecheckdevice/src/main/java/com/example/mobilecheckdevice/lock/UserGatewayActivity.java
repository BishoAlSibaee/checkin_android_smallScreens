package com.example.mobilecheckdevice.lock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecheckdevice.R;
import com.example.mobilecheckdevice.Rooms;
import com.example.mobilecheckdevice.lodingDialog;
import com.google.gson.reflect.TypeToken;
import com.ttlock.bl.sdk.util.GsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class UserGatewayActivity extends AppCompatActivity {

    private int pageNo = 1;
    private int pageSize = 100;
    private UserGatewayListAdapter mListApapter;
   // private ActivityUserGatewayBinding binding;
    Activity act = this ;
    RecyclerView ll ;
    UserGatewayListAdapter adapter ;
    public ArrayList<GatewayObj> mDataList = new ArrayList<GatewayObj>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_gateway);
        //LogIn.ActList.add(act);
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_user_gateway);
        ll =(RecyclerView) findViewById(R.id.rv_gateway_list);
        LinearLayoutManager manager = new LinearLayoutManager(act ,RecyclerView.VERTICAL,false);
        ll.setLayoutManager(manager);
       // binding.btnScan.setOnClickListener(v -> {startTargetActivity(GatewayActivity.class);});
        Button b = (Button) findViewById(R.id.btn_scan_gate);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(act ,GatewayActivity.class );
                startActivity(i);
            }
        });
        initList();
        gatewayList();
    }

    private void initList()
    {

    }

    private void gatewayList()
    {
        final lodingDialog loading = new lodingDialog(act);
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.getGatewayList(ApiService.CLIENT_ID, Rooms.acc.getAccess_token(), pageNo, pageSize, System.currentTimeMillis());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response)
            {
                loading.stop();
                String json = response.body();
                if (json.contains("list"))
                {
                    try
                    {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray array = jsonObject.getJSONArray("list");
                        ArrayList<GatewayObj> gatewayObjs = GsonUtil.toObject(array.toString(), new TypeToken<ArrayList<GatewayObj>>(){});
                        mDataList = gatewayObjs ;
                        adapter = new UserGatewayListAdapter(act,mDataList);
                        ll.setAdapter(adapter);
                       // mListApapter.updateData(gatewayObjs);
                    }
                    catch (JSONException e)
                    {

                        e.printStackTrace();
                        Calendar c = Calendar.getInstance(Locale.getDefault());
                        long time = c.getTimeInMillis();
                        //ErrorRegister.rigestError(act , LogIn.room.getProjectName(),LogIn.room.getRoomNumber(),time , 006 , e.getMessage() , "error getting lock Gateways list");
                    }
                } else
                {
                    //ToastMaker.MakeToast(json,act);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t)
            {
                loading.stop();
                Calendar c = Calendar.getInstance(Locale.getDefault());
                long time = c.getTimeInMillis();
                //ErrorRegister.rigestError(act , LogIn.room.getProjectName(),LogIn.room.getRoomNumber(),time , 006 , t.getMessage() , "error getting lock Gateways list");
                //ToastMaker.MakeToast(t.getMessage(),act);
            }
        });
    }

    public void keepGoing(View view)
    {/*
        if (LogIn.room.getLockName().equals("0"))
        {
            Intent in = new Intent(act , UserLockActivity.class );
            startActivity(in);
        }
        else if (LogIn.room.getLockGateway().equals("0"))
        {
            ToastMaker.MakeToast("Please Select Gatway Or Intialize One",act);
        }
        else
        {
            Intent in = new Intent(act , Tuya_Login.class );
            startActivity(in);
        }
        */


    }
}
