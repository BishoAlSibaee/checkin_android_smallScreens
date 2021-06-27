package com.example.hotelservicesstandalone.lock;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelservicesstandalone.R;
import com.example.hotelservicesstandalone.ROOM;
import com.example.hotelservicesstandalone.Rooms;
import com.example.hotelservicesstandalone.TUYA.Tuya_Login;
import com.google.gson.reflect.TypeToken;
import com.ttlock.bl.sdk.util.GsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

;

public class UserLockActivity extends AppCompatActivity {

    private int pageNo = 1;
    private int pageSize = 100;
    private UserLockListAdapter mListApapter;
    //ActivityUserLockBinding binding;
    static Activity act ;
    public static final String CLIENT_ID = "439063e312444f1f85050a52efcecd2e";
    ArrayList<LockObj> lockObjs ;
    RecyclerView locks ;
    public static LockObj myLock ;
    public static List<ROOM>  ROOMS ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.example.hotelservicesstandalone.R.layout.activity_user_lock);
        act = this ;
        //LogIn.ActList.add(act);
        ROOMS = new ArrayList<ROOM>();
        //getRooms();
        lockObjs = new ArrayList<LockObj>();
        locks = (RecyclerView) findViewById(R.id.rv_lock_list);
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

    private void lockList()
    {
        final Dialog d = new Dialog(this);
        d.setContentView(R.layout.loading_layout);
        d.setCancelable(false);
        d.show();
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.getLockList(ApiService.CLIENT_ID,Rooms.acc.getAccess_token(), pageNo, pageSize, System.currentTimeMillis());
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
                        //checkIfLockRegestired();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        Calendar c = Calendar.getInstance(Locale.getDefault());
                        long time = c.getTimeInMillis();
                        //ErrorRegister.rigestError(act ,LogIn.room.getProjectName(),LogIn.room.getRoomNumber(),time,007,e.getMessage(),"error Getting Locks List");
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
                //ErrorRegister.rigestError(act ,LogIn.room.getProjectName(),LogIn.room.getRoomNumber(),time,007,t.getMessage(),"error Getting Locks List");
            }
        });
    }

    static void go()
    {
        Intent i = new Intent(act , Tuya_Login.class);
        act.startActivity(i);
    }

    /*void checkIfLockRegestired()
    {
        if (LogIn.room.getLockName().equals("0"))
        {
            Toast.makeText(this,LogIn.room.getLockName(), Toast.LENGTH_SHORT).show();
        }
        else
        {
                //Toast.makeText(this , String.valueOf(lockObjs.size()) , Toast.LENGTH_SHORT).show();
                for (int i=0; i<lockObjs.size(); i++)
                {
                    if (LogIn.room.getLockName().equals(lockObjs.get(i).getLockName()))
                    {
                        myLock = lockObjs.get(i);
                        Toast.makeText(this,"here "+ myLock.getLockName(), Toast.LENGTH_SHORT).show();
                        go();
                    }
                }
        }
    }

     */
    /*
    public void keepGoing(View view)
    {
        if (LogIn.room.getLockName().equals("0"))
        {
            ToastMaker.MakeToast("Please Select Lock Or Intialize One",act);
        }
        else if (LogIn.room.getLockGateway().equals("0"))
        {
            Intent in = new Intent(act , UserGatewayActivity.class );
            startActivity(in);
        }
        else
        {
            Intent in = new Intent(act , Tuya_Login.class );
            startActivity(in);
        }
    }

     */
    /*
    public void getRooms()
    {
        com.syriasoft.hotelservices.lodingDialog loading = new lodingDialog(act);
        String url="https://bait-elmoneh.online/hotel-service/getAllRooms.php";
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
                        for (int i = 0;i<arr.length();i++)
                        {
                            JSONObject row = arr.getJSONObject(i);
                            ROOMS.add(new ROOM(row.getInt("id"),row.getInt("RoomNumber"),row.getInt("hotel"),row.getInt("Building"),row.getInt("BuildingId"),row.getInt("Floor"),row.getInt("FloorId"),row.getString("RoomType"),row.getInt("SuiteStatus"),row.getInt("SuiteNumber"),row.getInt("SuiteId"),row.getInt("ReservationNumber"),row.getInt("roomStatus"),row.getInt("Tablet"),row.getString("dep"),row.getInt("Cleanup"),row.getInt("Laundry"),row.getInt("RoomService"),row.getInt("Checkout"),row.getInt("Restaurant"),row.getInt("SOS"),row.getInt("DND"),row.getInt("PowerSwitch"),row.getInt("DoorSensor"),row.getInt("MotionSensor"),row.getInt("Thermostat"),row.getInt("CurtainSwitch"),row.getInt("lock"),row.getInt("Switch1"),row.getInt("Switch2"),row.getInt("Switch3"),row.getInt("Switch4"),row.getString("LockGateway"),row.getString("LockName"),row.getInt("powerStatus"),row.getInt("curtainStatus"),row.getInt("doorStatus"),row.getInt("temp"),row.getString("token")));

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
                Params.put("Hotel" , String.valueOf( LogIn.room.getHotel()));
                return Params;
            }
        };
        Volley.newRequestQueue(act).add(re);
    }

     */
}
