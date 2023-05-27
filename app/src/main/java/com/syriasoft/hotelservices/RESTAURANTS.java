package com.syriasoft.hotelservices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.ControlLockCallback;
import com.ttlock.bl.sdk.constant.ControlAction;
import com.ttlock.bl.sdk.entity.ControlLockResult;
import com.ttlock.bl.sdk.entity.LockError;
import com.tuya.smart.sdk.api.IResultCallback;
import com.wang.avi.AVLoadingIndicatorView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RESTAURANTS extends AppCompatActivity {
    public static Activity act ;
    RecyclerView rests ;
    private List<RESTAURANT_UNIT> list ;
    private RESTAURANTS_ADAPTER adapter ;
    private TextView CAPTION ;
    private LinearLayoutManager manager ;
    public static  TextView time , date,dndText;
    public static ImageView dndImage,dndIcon,leftArrow,rightArrow ;
    private DatabaseReference myRefDND , myRefSos , myRefLaundry , myRefCleanup , myRefCheckout , myRefRoomService ,myRefdep,myRefRestaurant ;
    private static ImageView restaurantIcon ;
    static Runnable backHomeThread ;
    public static long Current = 0 ;
    static long x = 0 ;
    static Handler H ;
    private LinearLayout mainlayout ;
    WindowInsetsControllerCompat windowInsetsController;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurants);
        setActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (x == 0 ){
            backHomeThread.run();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void setActivity() {
        act = this ;
        list = FullscreenActivity.Restaurants;
        date = (TextView) findViewById(R.id.mainDate);
        time = (TextView) findViewById(R.id.mainTime);
        dndImage = findViewById(R.id.DND_Image);
        dndIcon = findViewById(R.id.DND_Icon);
        dndText = findViewById(R.id.DND_Text);
        restaurantIcon = findViewById(R.id.imageView2);
        leftArrow = findViewById(R.id.leftSlide2);
        rightArrow = findViewById(R.id.imageView12);
        manager = new LinearLayoutManager(act,RecyclerView.HORIZONTAL,false);
        rests = findViewById(R.id.restaurants_recycler);
        rests.setLayoutManager(manager);
        CAPTION = findViewById(R.id.CAPTION2);
        CAPTION.setText("RESTAURANT");
        myRefRestaurant = FullscreenActivity.myRefRestaurant ;
        myRefRestaurant.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (!snapshot.getValue().toString().equals("0"))
                {
                    FullscreenActivity.RestaurantStatus = true ;
                    restaurantOn();
                }
                else
                {
                    restaurantOff();
                    FullscreenActivity.RestaurantStatus = false ;
                    //ImageView i = (ImageView) findViewById(R.id.imageView2);
                    //i.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
        myRefDND = FullscreenActivity.myRefDND;
        myRefDND.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Long.parseLong(snapshot.getValue().toString()) > 0 ) {
                    dndOn();
                    FullscreenActivity.DNDStatus = true ;
                }
                else {
                    dndOff();
                    FullscreenActivity.DNDStatus = false ;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
        myRefSos = FullscreenActivity.myRefSos;
        myRefSos.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (!snapshot.getValue().toString().equals("0"))
                {
                    sosOn();
                }
                else
                {
                    sosOff();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefLaundry = FullscreenActivity.myRefLaundry;
        myRefLaundry.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (!snapshot.getValue().toString().equals("0") )
                {
                    laundryOn();
                }
                else
                {
                    laundryOff();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefCleanup = FullscreenActivity.myRefCleanup;
        myRefCleanup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (!snapshot.getValue().toString().equals("0"))
                {
                    cleanupOn();
                }
                else
                {
                    cleanupOff();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefCheckout = FullscreenActivity.myRefCheckout;
        myRefCheckout.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (!snapshot.getValue().toString().equals("0"))
                {
                    checkoutOn();
                }
                else
                {
                    checkoutOff();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefRoomService = FullscreenActivity.myRefRoomService;
        myRefRoomService.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if ( !snapshot.getValue().toString().equals("0") )
                {
                    roomServiceOn();
                }
                else
                {
                    roomServiceOff();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefdep = FullscreenActivity.myRefdep ;
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE);
        blink();
        FullscreenActivity.RestaurantActivities.add(act);
        mainlayout = findViewById(R.id.main_layout);
        mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x=0;
            }
        });
        backHomeThread = new Runnable() {
            @Override
            public void run() {
                H = new Handler();
                x = x+1000 ;
                Log.d("backThread" , x+"");
                H.postDelayed(this,1000);
                if (x >= 60000){
                    LinearLayout v = (LinearLayout) findViewById(R.id.home_Btn);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            backToMain(v);
                            H.removeCallbacks(backHomeThread);
                            x=0;
                        }
                    });
                }

            }
        };
        backHomeThread.run();
        adapter = new RESTAURANTS_ADAPTER(list);
        rests.setAdapter(adapter);
        rests.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.d("currentRest" , Current+" ");
                x=0;
                if (list.size() > 1) {
                    if (Current == 0) {
                        leftArrow.setImageResource(R.drawable.subtraction_4);
                        rightArrow.setImageResource(R.drawable.subtraction_3);
                    }
                    else if (Current+1 == list.size()) {
                        leftArrow.setImageResource(R.drawable.subtraction_15);
                        rightArrow.setImageResource(R.drawable.subtraction_14);
                    }
                    else {
                        leftArrow.setImageResource(R.drawable.subtraction_15);
                        rightArrow.setImageResource(R.drawable.subtraction_3);
                    }
                }
                else {
                    leftArrow.setVisibility(View.INVISIBLE);
                    rightArrow.setVisibility(View.INVISIBLE);
                }

            }
        });
        KeepScreenFull();
    }

    public void backToMain(View view) {
        if (FullscreenActivity.RestaurantActivities.size() > 0 ){
            for (Activity a:FullscreenActivity.RestaurantActivities){
                a.finish();
            }
        }
        H.removeCallbacks(backHomeThread);
    }

    private void hideSystemUI() {
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }

    private void KeepScreenFull() {
        final Handler hander = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                hander.postDelayed(this,300);
                hideSystemUI();
            }
        }).start();
    }

    private void blink() {
        final Calendar x = Calendar.getInstance(Locale.getDefault());
        final Handler hander = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hander.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Time is : ",x.getTime().toString());
                        String currentTime = x.get(Calendar.HOUR_OF_DAY)+":"+x.get(Calendar.MINUTE)+":"+x.get(Calendar.SECOND);
                        time.setText(currentTime);
                        String currentDate = x.get(Calendar.DAY_OF_MONTH)+ "-" + (x.get(Calendar.MONTH)+1)+"-" + x.get(Calendar.YEAR);
                        date.setText(currentDate);
                        //hideSystemUI();
                        blink();
                    }
                });
            }
        }).start();
    }

    private void roomServiceOn(){

        ImageView roomserviceicon = (ImageView) findViewById(R.id.imageView7);
        roomserviceicon.setVisibility(View.VISIBLE);

    }
    private void roomServiceOff(){
        ImageView roomserviceicon = (ImageView) findViewById(R.id.imageView7);
        roomserviceicon.setVisibility(View.GONE);
    }

    private void checkoutOn(){

        ImageView checkouticon = (ImageView) findViewById(R.id.imageView20);
        checkouticon.setVisibility(View.VISIBLE);

    }
    private void checkoutOff(){

        ImageView checkouticon = (ImageView) findViewById(R.id.imageView20);
        checkouticon.setVisibility(View.GONE);

    }

    private  void laundryOn(){

        ImageView checkouticon = (ImageView) findViewById(R.id.imageView10);
        checkouticon.setVisibility(View.VISIBLE);

    }
    private  void laundryOff(){

        ImageView checkouticon = (ImageView) findViewById(R.id.imageView10);
        checkouticon.setVisibility(View.GONE);

    }

    private void cleanupOn(){
        ImageView checkouticon = (ImageView) findViewById(R.id.imageView9);
        checkouticon.setVisibility(View.VISIBLE);

    }
    private void cleanupOff(){
        ImageView checkouticon = (ImageView) findViewById(R.id.imageView9);
        checkouticon.setVisibility(View.GONE);

    }

    private void dndOn(){
        dndImage.setImageResource(R.drawable.union_6);
        dndIcon.setVisibility(View.VISIBLE);
        dndText.setTextColor(getResources().getColor(R.color.red));
    }
    private void dndOff(){
        dndImage.setImageResource(R.drawable.union_2);
        dndIcon.setVisibility(View.GONE);
        dndText.setTextColor(getResources().getColor(R.color.light_blue_A200));
    }

    private void sosOn(){
        ImageView checkoutimage = (ImageView) findViewById(R.id.SOS_Image);
        checkoutimage.setImageResource(R.drawable.group_54);
        ImageView checkouticon = (ImageView) findViewById(R.id.SOS_Icon);
        checkouticon.setVisibility(View.VISIBLE);
        TextView text = (TextView) findViewById(R.id.SOS_Text);
        text.setTextColor(getResources().getColor(R.color.red));
    }
    private void sosOff(){
        ImageView checkoutimage = (ImageView) findViewById(R.id.SOS_Image);
        checkoutimage.setImageResource(R.drawable.group_33);
        ImageView checkouticon = (ImageView) findViewById(R.id.SOS_Icon);
        checkouticon.setVisibility(View.GONE);
        TextView text = (TextView) findViewById(R.id.SOS_Text);
        text.setTextColor(getResources().getColor(R.color.light_blue_A200));
    }

    private static void restaurantOn(){
        //restaurantIcon.setImageResource(R.drawable.group_54);
        restaurantIcon.setVisibility(View.VISIBLE);
        //sosText.setTextColor(RESOURCES.getColor(R.color.red));
    }
    private static void restaurantOff(){
        //sosImage.setImageResource(R.drawable.group_33);
        restaurantIcon.setVisibility(View.GONE);
        //sosText.setTextColor(Color.WHITE);
    }

    public void setDND(View view) {
        if (MyApp.Room.getSERVICE1_B() != null) {
            if (MyApp.Room.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton)) != null) {
                if (Boolean.parseBoolean(MyApp.Room.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton)).toString())) {
                    MyApp.Room.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.dndButton+"\":false}", new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {

                        }

                        @Override
                        public void onSuccess() {

                        }
                    });
                }
                else {
                    MyApp.Room.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.dndButton+"\":true}", new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {

                        }

                        @Override
                        public void onSuccess() {

                        }
                    });
                }
            }
        }
    }

    public void OpenTheDoor(View view) {
        AVLoadingIndicatorView doorLoading = act.findViewById(R.id.loadingIcon);
        ImageView doorImage = act.findViewById(R.id.imageView17);
        if (MyApp.BluetoothLock != null) {
            doorImage.setVisibility(View.GONE);
            doorLoading.setVisibility(View.VISIBLE);
            String url = MyApp.ProjectURL + "roomsManagement/addClientDoorOpen";
            StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject result = new JSONObject(response);
                        if (result.getString("result") != null) {
                            if (result.getString("result").equals("success")) {
                                TTLockClient.getDefault().controlLock(ControlAction.UNLOCK, MyApp.BluetoothLock.getLockData(), MyApp.BluetoothLock.getLockMac(),new ControlLockCallback() {
                                    @Override
                                    public void onControlLockSuccess(ControlLockResult controlLockResult) {
                                        ToastMaker.MakeToast("door opened",act);
                                        doorImage.setVisibility(View.VISIBLE);
                                        doorLoading.setVisibility(View.GONE);
                                    }
                                    @Override
                                    public void onFail(LockError error) {
                                        ToastMaker.MakeToast(error.getErrorMsg(),act);
                                        doorImage.setVisibility(View.VISIBLE);
                                        doorLoading.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        ToastMaker.MakeToast(e.getMessage(),act);
                        doorImage.setVisibility(View.VISIBLE);
                        doorLoading.setVisibility(View.GONE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ToastMaker.MakeToast(error.toString(),act);
                    doorImage.setVisibility(View.VISIBLE);
                    doorLoading.setVisibility(View.GONE);
                }
            });
            Volley.newRequestQueue(act).add(req);
        }
        else {
            if (MyApp.Room.getLOCK_B() != null) {
                doorImage.setVisibility(View.GONE);
                doorLoading.setVisibility(View.VISIBLE);
                String url = MyApp.ProjectURL + "roomsManagement/addClientDoorOpen";
                StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("doorOpenResp" , response);
                        try {
                            JSONObject result = new JSONObject(response);
                            if (result.getString("result") != null) {
                                if (result.getString("result").equals("success")) {
                                    ZigbeeLock.getTokenFromApi(MyApp.cloudClientId, MyApp.cloudSecret, act, new RequestOrder() {
                                        @Override
                                        public void onSuccess(String token) {
                                            Log.d("doorOpenResp" , "token "+token);
                                            ZigbeeLock.getTicketId(token, MyApp.cloudClientId, MyApp.cloudSecret, MyApp.Room.getLOCK_B().devId, act, new RequestOrder() {
                                                @Override
                                                public void onSuccess(String ticket) {
                                                    Log.d("doorOpenResp" , "ticket "+ticket);
                                                    ZigbeeLock.unlockWithoutPassword(token, ticket, MyApp.cloudClientId, MyApp.cloudSecret, MyApp.Room.getLOCK_B().devId, act, new RequestOrder() {
                                                        @Override
                                                        public void onSuccess(String res) {
                                                            Log.d("doorOpenResp" , "res "+res);
                                                            ToastMaker.MakeToast("door opened",act);
                                                            doorImage.setVisibility(View.VISIBLE);
                                                            doorLoading.setVisibility(View.GONE);
                                                        }

                                                        @Override
                                                        public void onFailed(String error) {
                                                            Log.d("openDoorResp" , "res "+error);
                                                            ToastMaker.MakeToast(error,act);
                                                            doorImage.setVisibility(View.VISIBLE);
                                                            doorLoading.setVisibility(View.GONE);
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onFailed(String error) {
                                                    Log.d("doorOpenResp" , "ticket "+error);
                                                    ToastMaker.MakeToast(error,act);
                                                    doorImage.setVisibility(View.VISIBLE);
                                                    doorLoading.setVisibility(View.GONE);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailed(String error) {
                                            Log.d("doorOpenResp" , "token "+error);
                                            ToastMaker.MakeToast(error,act);
                                            doorImage.setVisibility(View.VISIBLE);
                                            doorLoading.setVisibility(View.GONE);
                                        }
                                    });
                                }
                                else {
                                    ToastMaker.MakeToast(result.getString("error"),act);
                                    doorImage.setVisibility(View.VISIBLE);
                                    doorLoading.setVisibility(View.GONE);
                                }
                            }

                        } catch (JSONException e) {
                            Log.d("doorOpenResp" , e.getMessage());
                            ToastMaker.MakeToast(e.getMessage(),act);
                            doorImage.setVisibility(View.VISIBLE);
                            doorLoading.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("doorOpenResp" , error.toString());
                        ToastMaker.MakeToast(error.toString(),act);
                        doorImage.setVisibility(View.VISIBLE);
                        doorLoading.setVisibility(View.GONE);
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("room_id", String.valueOf(MyApp.Room.id));
                        return params;
                    }
                };
                Volley.newRequestQueue(act).add(req);
            }
            else {
                new messageDialog("no lock detected in this room ","failed",act);
            }
        }
    }

    public void SOS(View view) {
        if (FullscreenActivity.CURRENT_ROOM_STATUS == 2) {
            if (!FullscreenActivity.SosStatus) {
                final Dialog d = new Dialog(act);
                d.setContentView(R.layout.confermation_dialog);
                TextView message = (TextView) d.findViewById(R.id.confermationDialog_Text);
                message.setText("Send Emergency Order .. ?");
                Button cancel = (Button)d.findViewById(R.id.confermationDialog_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        d.dismiss();
                    }
                });
                Button ok = (Button)d.findViewById(R.id.messageDialog_ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FullscreenActivity.SosStatus = true ;
                        sosOn();
                        Calendar c = Calendar.getInstance(Locale.getDefault());
                        myRefSos.setValue(c.getTimeInMillis());
                        d.dismiss();
                        String url = MyApp.ProjectURL + "reservations/addSOSOrder";
                        StringRequest addOrder = new StringRequest(Request.Method.POST, url , new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("sosResp" , response);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("sosResp" , error.toString());
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> params = new HashMap<>();
                                params.put("room_id" ,String.valueOf(MyApp.Room.id));
                                return params;
                            }
                        };
                        Volley.newRequestQueue(act).add(addOrder);
                    }
                });
                d.show();
            }
            else {
                FullscreenActivity.SosStatus = false ;
                sosOff();
                myRefSos.setValue(0);
                String url = MyApp.ProjectURL + "reservations/cancelServiceOrderControlDevice"+FullscreenActivity.sosCounter;
                StringRequest removOrder = new StringRequest(Request.Method.POST, url , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("sosResp" , response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("room_id" , String.valueOf(MyApp.Room.id));
                        params.put("order_type" , "SOS");
                        return params;
                    }
                };
                Volley.newRequestQueue(act).add(removOrder);
                FullscreenActivity.sosCounter++ ;
                if (FullscreenActivity.sosCounter == 5) {
                    FullscreenActivity.sosCounter = 1 ;
                }
            }
        }
        else {
            ToastMaker.MakeToast("This Room Is Vacant" , act);
        }
//        if (FullscreenActivity.CURRENT_ROOM_STATUS == 2)
//        {
//            if (FullscreenActivity.SosStatus == false)
//            {
//                final Dialog d = new Dialog(act);
//                d.setContentView(R.layout.confermation_dialog);
//                TextView message = (TextView) d.findViewById(R.id.confermationDialog_Text);
//                message.setText("Send Emergency Order .. ?                   ");
//                Button cancel = (Button)d.findViewById(R.id.confermationDialog_cancel);
//                cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        d.dismiss();
//                    }
//                });
//                Button ok = (Button)d.findViewById(R.id.messageDialog_ok);
//                ok.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        d.dismiss();
//                        final String depo = "SOS";
//                        Calendar x = Calendar.getInstance(Locale.getDefault());
//                        long timee =  x.getTimeInMillis();
//                        myRefSos.setValue(timee);
//                        myRefdep.setValue(depo);
//                        myRefDND.setValue(0);
//                        FullscreenActivity.SosStatus = true ;
//                        sosOn();
//                        for(ServiceEmps emp : FullscreenActivity.Emps) {
//                            if (emp.department.equals("Service") || emp.department.equals("RoomService") || emp.department.equals("Cleanup")) {
//                                emp.makemessage(emp.token,"SOS",true,act);
//                            }
//                        }
//                        LoadingDialog dd = new LoadingDialog(act);
//                        StringRequest addOrder = new StringRequest(Request.Method.POST, insertServiceOrderUrl , new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response)
//                            {
//                                dd.stop();
//                                if (Integer.parseInt(response) > 0 )
//                                {
//                                    FullscreenActivity.sosId = Integer.parseInt(response);
//                                    ToastMaker.MakeToast("تم ارسال طلب " +"SOS" , act);
//                                    Calendar x = Calendar.getInstance(Locale.getDefault());
//                                }
//                                else
//                                {
//                                    Toast.makeText(act , response,Toast.LENGTH_LONG).show();
//                                }
//
//                            }
//                        }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error)
//                            {
//                                dd.stop();
//                                //Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
//                            }
//                        })
//                        {
//                            @Override
//                            protected Map<String, String> getParams() throws AuthFailureError
//                            {
//                                Map<String,String> params = new HashMap<String, String>();
//                                params.put("roomNumber" ,String.valueOf(MyApp.Room.RoomNumber));
//                                params.put("time" ,String.valueOf(timee));
//                                params.put("dep" ,depo);
//                                params.put("Hotel" ,"1");
//                                params.put("RorS" ,String.valueOf( FullscreenActivity.RoomOrSuite));
//                                params.put("Reservation" ,String.valueOf( FullscreenActivity.RESERVATION));
//                                return params;
//                            }
//
//                        };
//                        Volley.newRequestQueue(act).add(addOrder);
//                    }
//                });
//                d.show();
//            }
//            else
//            {
//                myRefSos.setValue(0);
//                FullscreenActivity.SosStatus = false ;
//                sosOff();
//                for(ServiceEmps emp : FullscreenActivity.Emps) {
//                    if (emp.department.equals("Service") || emp.department.equals("RoomService") || emp.department.equals("Cleanup")) {
//                        emp.makemessage(emp.token,"SOS",false,act);
//                    }
//                }
//                LoadingDialog ddd = new LoadingDialog(act);
//                myRefSos.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot)
//                    {
//                        if (Long.parseLong(snapshot.getValue().toString()) > 0 )
//                        {
//                            FullscreenActivity.sosId = Long.parseLong(snapshot.getValue().toString()) ;
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//                final String depo = "SOS";
//                StringRequest removOrder = new StringRequest(Request.Method.POST, removeServiceOrderUrl , new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response)
//                    {
//                        ddd.stop();
//                        if (response.equals("1")  )
//                        {
//                            ToastMaker.MakeToast("تم الغاء طلب " + "SOS" , act);
//                            Calendar x = Calendar.getInstance(Locale.getDefault());
//                        }
//                        else
//                        {
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error)
//                    {
//                        ddd.stop();
//                    }
//                })
//                {
//                    @Override
//                    protected Map<String, String> getParams() throws AuthFailureError
//                    {
//                        Map<String,String> params = new HashMap<String, String>();
//                        params.put("id" , String.valueOf( FullscreenActivity.sosId));
//                        params.put("room" , String.valueOf(MyApp.Room.RoomNumber));
//                        params.put("dep" , "SOS");
//                        params.put("Hotel" ,"1");
//                        return params;
//                    }
//                };
//                Volley.newRequestQueue(act).add(removOrder);
//            }
//        }
//        else
//        {
//            ToastMaker.MakeToast("This Room Is Vacant" , act);
//        }
    }

}