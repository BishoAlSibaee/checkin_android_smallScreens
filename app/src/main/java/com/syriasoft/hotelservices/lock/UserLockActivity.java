package com.syriasoft.hotelservices.lock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;
import com.syriasoft.hotelservices.ErrorRegister;
import com.syriasoft.hotelservices.LogIn;
import com.syriasoft.hotelservices.MyApp;
import com.syriasoft.hotelservices.R;
import com.syriasoft.hotelservices.ROOM;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

;

public class UserLockActivity extends AppCompatActivity {

    static int pageNo = 1;
    static int pageSize = 100;
    static UserLockListAdapter mListApapter;
    //ActivityUserLockBinding binding;
    static Activity act ;
    public static final String CLIENT_ID = "439063e312444f1f85050a52efcecd2e";
    static ArrayList<LockObj> lockObjs ;
    static RecyclerView locks ;
    public static LockObj myLock ;
    public static List<ROOM>  ROOMS ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.syriasoft.hotelservices.R.layout.activity_user_lock);
        act = this ;
        LogIn.ActList.add(act);
        ROOMS = new ArrayList<ROOM>();
        getRooms();
        lockObjs = new ArrayList<LockObj>();
        locks = (RecyclerView) findViewById(com.syriasoft.hotelservices.R.id.rv_lock_list);
        LinearLayoutManager manager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        locks.setLayoutManager(manager);
        initList();

    }

    private void initList()
    {

    }


    public void gotoScan(View view)
    {
        Intent i = new Intent(act , ScanLockActivity.class);
        startActivity(i);
    }

    static void lockList()
    {
        Dialog d = new Dialog(act);
        d.setContentView(R.layout.loading_layout);
        d.setCancelable(false);
        d.show();
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.getLockList(ApiService.CLIENT_ID,LogIn.acc.getAccess_token(), pageNo, pageSize, System.currentTimeMillis());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response)
            {
                d.dismiss();
                mListApapter = new UserLockListAdapter(act ,lockObjs );
                String json = response.body();
                if (json.contains("list"))
                {
                    try
                    {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray array = jsonObject.getJSONArray("list");
                        lockObjs = GsonUtil.toObject(array.toString(), new TypeToken<ArrayList<LockObj>>(){});
                        mListApapter = new UserLockListAdapter(act ,lockObjs );
                        locks.setAdapter(mListApapter);
                        checkIfLockRegestired();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        Calendar c = Calendar.getInstance(Locale.getDefault());
                        long time = c.getTimeInMillis();
                        ErrorRegister.rigestError(act,MyApp.THE_PROJECT.projectName,MyApp.Room.RoomNumber,time,007,e.getMessage(),"error Getting Locks List");
                    }
                }
                else
                {
                    //ToastMaker.MakeToast(json,act);
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t)
            {
                d.dismiss();
               //ToastMaker.MakeToast(t.getMessage(),act);
                Calendar c = Calendar.getInstance(Locale.getDefault());
                long time = c.getTimeInMillis();
                ErrorRegister.rigestError(act ,MyApp.THE_PROJECT.projectName,MyApp.Room.RoomNumber,time,007,t.getMessage(),"error Getting Locks List");
            }
        });
    }

    static void go()
    {
        Intent i = new Intent(act , Tuya_Login.class);
        act.startActivity(i);
    }

    static void checkIfLockRegestired() {
//        if (LogIn.room.getLockName().equals("0"))
//        {
//            Toast.makeText(act,LogIn.room.getLockName(), Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//                //Toast.makeText(this , String.valueOf(lockObjs.size()) , Toast.LENGTH_SHORT).show();
//                for (int i=0; i<lockObjs.size(); i++)
//                {
//                    if (LogIn.room.getLockName().equals(lockObjs.get(i).getLockName()))
//                    {
//                        myLock = lockObjs.get(i);
//                        Toast.makeText(act,"here "+ myLock.getLockName(), Toast.LENGTH_SHORT).show();
//                        go();
//                    }
//                }
//        }
    }

    public void keepGoing(View view) {
//        if (LogIn.room.getLockName().equals("0")) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(act);
//            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
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
//                    com.syriasoft.hotelservices.messageDialog m = new messageDialog("Please Select Lock Or Initialize One","add Lock",act);
//                }
//            });
//            builder.setTitle("Lock .. ?");
//            builder.setMessage("Do you want to install Lock ..?");
//            builder.create();
//            builder.show();
//            //ToastMaker.MakeToast("Please Select Lock Or Intialize One",act);
//        }
//        else if (LogIn.room.getLockGateway().equals("0")) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(act);
//            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
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
//                    Intent in = new Intent(act , UserGatewayActivity.class );
//                    startActivity(in);
//                }
//            });
//            builder.setTitle("GatWay .. ?");
//            builder.setMessage("Do you want to install Lock Gateway ..?");
//            builder.create();
//            builder.show();
//        }
//        else {
//            Intent in = new Intent(act , Tuya_Login.class );
//            startActivity(in);
//        }
    }

    public void getRooms()
    {
        LoadingDialog loading = new LoadingDialog(act);
        String url=LogIn.URL+"getAllRooms.php";
        StringRequest re = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                Log.d("gettingRooms" , response);
                loading.stop();
                if (response.equals("-1"))
                {

                }
                else
                {
                    try
                    {
                        JSONArray arr = new JSONArray(response);
                        for (int i = 0;i<arr.length();i++) {
                            JSONObject row = arr.getJSONObject(i);
                            //ROOMS.add(new ROOM(row.getInt("id"),row.getInt("RoomNumber"),row.getInt("hotel"),row.getInt("Building"),row.getInt("BuildingId"),row.getInt("Floor"),row.getInt("FloorId"),row.getString("RoomType"),row.getInt("SuiteStatus"),row.getInt("SuiteNumber"),row.getInt("SuiteId"),row.getInt("ReservationNumber"),row.getInt("roomStatus"),row.getInt("Tablet"),row.getString("dep"),row.getInt("Cleanup"),row.getInt("Laundry"),row.getInt("RoomService"),row.getInt("Checkout"),row.getInt("Restaurant"),row.getInt("SOS"),row.getInt("DND"),row.getInt("PowerSwitch"),row.getInt("DoorSensor"),row.getInt("MotionSensor"),row.getInt("Thermostat"),row.getInt("ZBGateway"),row.getInt("CurtainSwitch"),row.getInt("ServiceSwitch"),row.getInt("lock"),row.getInt("Switch1"),row.getInt("Switch2"),row.getInt("Switch3"),row.getInt("Switch4"),row.getString("LockGateway"),row.getString("LockName"),row.getInt("powerStatus"),row.getInt("curtainStatus"),row.getInt("doorStatus"),row.getInt("temp"),row.getString("token")));

                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    lockList();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                loading.stop();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> Params = new HashMap<String, String>();
                Params.put("Hotel" ,"1");
                return Params;
            }
        };
        Volley.newRequestQueue(act).add(re);
    }
}
