package com.syriasoft.hotelservices.lock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.reflect.TypeToken;
import com.syriasoft.hotelservices.ErrorRegister;
import com.syriasoft.hotelservices.LogIn;
import com.syriasoft.hotelservices.MyApp;
import com.syriasoft.hotelservices.R;
import com.syriasoft.hotelservices.TUYA.Tuya_Login;
import com.syriasoft.hotelservices.ToastMaker;
import com.syriasoft.hotelservices.LoadingDialog;
import com.syriasoft.hotelservices.messageDialog;
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
        LogIn.ActList.add(act);
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
        LoadingDialog loading = new LoadingDialog(act);
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.getGatewayList(ApiService.CLIENT_ID, LogIn.acc.getAccess_token(), pageNo, pageSize, System.currentTimeMillis());
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
                        //Log.d("lockGatewaysAre",mDataList.size()+" "+mDataList.get(0).getIsOnline());
                    }
                    catch (JSONException e)
                    {

                        e.printStackTrace();
                        Calendar c = Calendar.getInstance(Locale.getDefault());
                        long time = c.getTimeInMillis();
                        ErrorRegister.rigestError(act , MyApp.THE_PROJECT.projectName,MyApp.Room.RoomNumber,time , 006 , e.getMessage() , "error getting lock Gateways list");
                    }
                } else
                {
                    ToastMaker.MakeToast(json,act);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t)
            {
                loading.stop();
                Calendar c = Calendar.getInstance(Locale.getDefault());
                long time = c.getTimeInMillis();
                ErrorRegister.rigestError(act , MyApp.THE_PROJECT.projectName, MyApp.Room.RoomNumber,time , 006 , t.getMessage() , "error getting lock Gateways list");
                ToastMaker.MakeToast(t.getMessage(),act);
            }
        });
    }

    public void keepGoing(View view)
    {
//        if (LogIn.room.getLockName().equals("0")) {
//            Intent in = new Intent(act , UserLockActivity.class );
//            startActivity(in);
//        }
//        else if (LogIn.room.getLockGateway().equals("0")) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(act);
//            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    Intent in = new Intent(act , Tuya_Login.class );
//                    startActivity(in);
//                }
//            });
//            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    com.syriasoft.hotelservices.messageDialog m = new messageDialog("Install Gatway","Please Select Gatway Or Intialize One",act);
//                }
//            });
//            builder.setTitle("Gateway .. ?");
//            builder.setMessage("Do you want to install Lock Gateway");
//            builder.create();
//            builder.show();
//            //ToastMaker.MakeToast("Please Select Gatway Or Intialize One",act);
//        }
//        else {
//            Intent in = new Intent(act , Tuya_Login.class );
//            startActivity(in);
//        }

    }
}
