package com.syriasoft.hotelservices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.tuya.smart.sdk.api.IResultCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RESTAURANTS extends AppCompatActivity
{
    Activity act = this ;
    RecyclerView rests ;
    private String getRestaurantsUrl = LogIn.URL+"getRestaurantsOrCoffeeShops.php";
    private List<RESTAURANT_UNIT> list ;
    private RESTAURANTS_ADAPTER adapter ;
    private TextView CAPTION ;
    private LinearLayoutManager manager ;
    private TextView time , date;
    private static String insertServiceOrderUrl = LogIn.URL+"insertServiceOrder.php";
    private static String removeServiceOrderUrl = LogIn.URL+"removeServiceOrder.php";
    private DatabaseReference myRefDND , myRefSos , myRefLaundry , myRefCleanup , myRefCheckout , myRefRoomService ,myRefdep,myRefRestaurant ;
    private static ImageView restaurantIcon ;
    static Runnable backHomeThread ;
    static long x = 0 ;
    static Handler H ;
    private ConstraintLayout mainlayout ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurants);
        date = (TextView) findViewById(R.id.mainDate);
        time = (TextView) findViewById(R.id.mainTime);
        blink();
        restaurantIcon = (ImageView) findViewById(R.id.imageView2);
        manager = new LinearLayoutManager(act,RecyclerView.HORIZONTAL,false);
        rests = (RecyclerView) findViewById(R.id.restaurants_recycler);
        rests.setLayoutManager(manager);
        list = new ArrayList<RESTAURANT_UNIT>();
        CAPTION = (TextView) findViewById(R.id.CAPTION2);
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
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (!snapshot.getValue().toString().equals("0") )
                {
                    dndOn();
                }
                else
                {
                    dndOff();
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
        FullscreenActivity.RestaurantActivities.add(act);
        getAvaliableRestaurants() ;
        KeepScreenFull();
        mainlayout = (ConstraintLayout) findViewById(R.id.main_layout);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (x == 0 ){
            backHomeThread.run();
        }

    }

    void getAvaliableRestaurants ()
    {
        LoadingDialog d = new LoadingDialog(act);
        StringRequest request = new StringRequest(Request.Method.POST, getRestaurantsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                if (!response.equals("0"))
                {
                    try
                    {
                        JSONArray arr = new JSONArray(response);
                        for (int i=0 ; i<arr.length() ; i++)
                        {
                            JSONObject row = arr.getJSONObject(i);
                            int id = row.getInt("id");
                            int Hotel = row.getInt("Hotel");
                            int TypeId = row.getInt("TypeId");
                            String TypeName = row.getString("TypeName");
                            String Name = row.getString("Name");
                            int Control = row.getInt("Control");
                            String photo = row.getString("photo");
                            RESTAURANT_UNIT rest = new RESTAURANT_UNIT(id,Hotel,TypeId,TypeName,Name,Control,photo);
                            list.add(rest);
                        }
                        adapter = new RESTAURANTS_ADAPTER(list , act);
                        rests.setAdapter(adapter);
                        if (list.size()<2){
                            ImageView previous , next ;
                            previous = (ImageView) findViewById(R.id.leftSlide2);
                            next = (ImageView) findViewById(R.id.imageView12);
                            previous.setVisibility(View.GONE);
                            next.setVisibility(View.GONE);
                        }
                        else {
                            ImageView previous , next ;
                            previous = (ImageView) findViewById(R.id.leftSlide2);
                            next = (ImageView) findViewById(R.id.imageView12);
                            previous.setVisibility(View.VISIBLE);
                            next.setVisibility(View.VISIBLE);
                        }
                        d.stop();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Hotel" , String.valueOf( LogIn.room.getHotel() ) );
                return params;
            }
        };
        Volley.newRequestQueue(act).add(request);
    }

    public void backToMain(View view) {
        //Intent i = new Intent(act,FullscreenActivity.class);
        //startActivity(i);
        if (FullscreenActivity.RestaurantActivities.size() > 0 ){
            for (Activity a:FullscreenActivity.RestaurantActivities){
                a.finish();
            }
        }
        H.removeCallbacks(backHomeThread);
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void KeepScreenFull()
    {
        final Calendar x = Calendar.getInstance(Locale.getDefault());
        final Handler hander = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hander.post(new Runnable() {
                    @Override
                    public void run() {
                        hideSystemUI();
                        KeepScreenFull();
                    }
                });
            }
        }).start();
    }

    private void blink()
    {
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
        ImageView checkoutimage = (ImageView) findViewById(R.id.DND_Image);
        checkoutimage.setImageResource(R.drawable.union_6);
        ImageView checkouticon = (ImageView) findViewById(R.id.DND_Icon);
        checkouticon.setVisibility(View.VISIBLE);
        TextView text = (TextView) findViewById(R.id.DND_Text);
        text.setTextColor(getResources().getColor(R.color.red));
    }
    private void dndOff(){
        ImageView checkoutimage = (ImageView) findViewById(R.id.DND_Image);
        checkoutimage.setImageResource(R.drawable.union_2);
        ImageView checkouticon = (ImageView) findViewById(R.id.DND_Icon);
        checkouticon.setVisibility(View.GONE);
        TextView text = (TextView) findViewById(R.id.DND_Text);
        text.setTextColor(getResources().getColor(R.color.light_blue_A200));
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
        if (!FullscreenActivity.DNDStatus ) {
            if (FullscreenActivity.THEROOM.getSERVICE_B() != null) {
                if (FullscreenActivity.THEROOM.getSERVICE_B().dps.get("1") != null) {
                    if (FullscreenActivity.THEROOM.getSERVICE_B().dps.get("1").toString().equals("false")) {
                        FullscreenActivity.THEROOM.getSERVICE().publishDps("{\"1\": true}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {
                                Log.d("serviceSwitch", error);
                            }

                            @Override
                            public void onSuccess() {
                                Log.d("serviceSwitch", "success");
                            }
                        });
                    }
                }
            }
        }
        else {
            if(FullscreenActivity.THEROOM.getSERVICE_B() != null ){
                if (FullscreenActivity.THEROOM.getSERVICE_B().dps.get("1").toString().equals("true")){
                    FullscreenActivity.THEROOM.getSERVICE().publishDps("{\"1\":false}", new IResultCallback() {
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
        FullscreenActivity.OpenDoorAndSaveIt(view);
    }

    public void SOS(View view) {
        if (FullscreenActivity.CURRENT_ROOM_STATUS == 2)
        {
            if (FullscreenActivity.SosStatus == false)
            {
                final Dialog d = new Dialog(act);
                d.setContentView(R.layout.confermation_dialog);
                TextView message = (TextView) d.findViewById(R.id.confermationDialog_Text);
                message.setText("Send Emergency Order .. ?                   ");
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
                    public void onClick(View v)
                    {
                        d.dismiss();
                        final String depo = "SOS";
                        Calendar x = Calendar.getInstance(Locale.getDefault());
                        long timee =  x.getTimeInMillis();
                        myRefSos.setValue(timee);
                        myRefdep.setValue(depo);
                        myRefDND.setValue(0);
                        FullscreenActivity.SosStatus = true ;
                        sosOn();
                        for(ServiceEmps emp : FullscreenActivity.Emps) {
                            if (emp.department.equals("Service") || emp.department.equals("RoomService") || emp.department.equals("Cleanup")) {
                                emp.makemessage(emp.token,"SOS",true,act);
                            }
                        }
                        LoadingDialog dd = new LoadingDialog(act);
                        StringRequest addOrder = new StringRequest(Request.Method.POST, insertServiceOrderUrl , new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response)
                            {
                                dd.stop();
                                if (Integer.parseInt(response) > 0 )
                                {
                                    FullscreenActivity.sosId = Integer.parseInt(response);
                                    ToastMaker.MakeToast("تم ارسال طلب " +"SOS" , act);
                                    Calendar x = Calendar.getInstance(Locale.getDefault());
                                }
                                else
                                {
                                    Toast.makeText(act , response,Toast.LENGTH_LONG).show();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                dd.stop();
                                //Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        })
                        {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError
                            {
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("roomNumber" ,String.valueOf(LogIn.room.getRoomNumber()));
                                params.put("time" ,String.valueOf(timee));
                                params.put("dep" ,depo);
                                params.put("Hotel" , String.valueOf(LogIn.room.getHotel()));
                                params.put("RorS" ,String.valueOf( FullscreenActivity.RoomOrSuite));
                                params.put("Reservation" ,String.valueOf( FullscreenActivity.RESERVATION));
                                return params;
                            }

                        };
                        Volley.newRequestQueue(act).add(addOrder);
                    }
                });
                d.show();
            }
            else
            {
                myRefSos.setValue(0);
                FullscreenActivity.SosStatus = false ;
                sosOff();
                for(ServiceEmps emp : FullscreenActivity.Emps) {
                    if (emp.department.equals("Service") || emp.department.equals("RoomService") || emp.department.equals("Cleanup")) {
                        emp.makemessage(emp.token,"SOS",false,act);
                    }
                }
                LoadingDialog ddd = new LoadingDialog(act);
                myRefSos.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (Long.parseLong(snapshot.getValue().toString()) > 0 )
                        {
                            FullscreenActivity.sosId = Long.parseLong(snapshot.getValue().toString()) ;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                final String depo = "SOS";
                StringRequest removOrder = new StringRequest(Request.Method.POST, removeServiceOrderUrl , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        ddd.stop();
                        if (response.equals("1")  )
                        {
                            ToastMaker.MakeToast("تم الغاء طلب " + "SOS" , act);
                            Calendar x = Calendar.getInstance(Locale.getDefault());
                        }
                        else
                        {
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        ddd.stop();
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError
                    {
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("id" , String.valueOf( FullscreenActivity.sosId));
                        params.put("room" , String.valueOf( LogIn.room.getRoomNumber()));
                        params.put("dep" , "SOS");
                        params.put("Hotel" , String.valueOf( LogIn.room.getHotel()));
                        return params;
                    }
                };
                Volley.newRequestQueue(act).add(removOrder);
            }
        }
        else
        {
            ToastMaker.MakeToast("This Room Is Vacant" , act);
        }
    }

}