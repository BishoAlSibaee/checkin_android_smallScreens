package com.syriasoft.hotelservices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
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

public class RestaurantActivity extends AppCompatActivity {

    //List<restaurant_item> breakfastList = new ArrayList<restaurant_item>();
    List<restaurant_item> dinnerList = new ArrayList<restaurant_item>();
    //List<restaurant_item> drinksList = new ArrayList<restaurant_item>();
    List<RestaurantOrderItem> orderList = new ArrayList<RestaurantOrderItem>();
    static RecyclerView  dinner ,  ordersRecycler ;
    String url ;
    Activity act = this ;
    //static OrderDB order ;
    static RestaurantOrderAdapter adapter;
    int menuId ,Hotel , Facility ;
    String menuName , menuNameArabic ,Type ;
    static TextView items ;
    StringRequest re ;
    private TextView CAPTION ;
    private TextView time , date;
    private DatabaseReference myRefDND , myRefSos , myRefLaundry , myRefCleanup , myRefCheckout , myRefRoomService ,myRefdep ;
    private static String insertServiceOrderUrl = LogIn.URL+"insertServiceOrder.php";
    private static String removeServiceOrderUrl = LogIn.URL+"removeServiceOrder.php";
    static Runnable backHomeThread ;
    static long x = 0 ;
    static Handler H ;
    private ConstraintLayout mainlayout ;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant2);
        date = (TextView) findViewById(R.id.mainDate);
        time = (TextView) findViewById(R.id.mainTime);
        blink();
        //order.removeOrder();
        Bundle b = getIntent().getExtras();
        menuId = b.getInt("id");
        menuName = b.getString("name");
        menuNameArabic = b.getString("arabic");
        Hotel = b.getInt("Hotel");
        Facility = b.getInt("Facility");
        Type = b.getString("Type");
        CAPTION = (TextView) findViewById(R.id.CAPTION);
        CAPTION.setText("RESTAURANT");
        FullscreenActivity.RestaurantActivities.add(act);
        TextView caption = (TextView) findViewById(R.id.menuName);
        items = (TextView) findViewById(R.id.items_quantity_incart);
        if (FullscreenActivity.order.getItems().size() != 0 )
        {
            items.setText(String.valueOf(FullscreenActivity.order.getItems().size()));
        }
        caption.setText(menuNameArabic);
        final GridLayoutManager layoutManagerdinner = new GridLayoutManager(this,1,RecyclerView.HORIZONTAL,false);
        layoutManagerdinner.setOrientation(LinearLayoutManager.HORIZONTAL);
        dinner = (RecyclerView) findViewById(R.id.Dinner);
        dinner.setLayoutManager(layoutManagerdinner);
        dinner.stopNestedScroll();
        final LoadingDialog l = new LoadingDialog(act);
        if (Type.equals("Restaurant"))
        {
            url = LogIn.URL+"getRestaurantItems.php";
            re = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    l.stop();
                    //ToastMaker.MakeToast(response , act);
                    try
                    {
                        JSONArray arr = new JSONArray(response);
                        for (int i =0 ; i < arr.length() ; i++ )
                        {
                            JSONObject row = arr.getJSONObject(i);
                            String type = row.getString("menu");
                            int id = row.getInt("id");
                            int fac = row.getInt("Facility");
                            int hot = row.getInt("Hotel");
                            int menuId = row.getInt("menuId");
                            String name = row.getString("name");
                            String desc = row.getString("desc");
                            double price = row.getDouble("price");
                            double discount = row.getDouble("discount");
                            String photo = row.getString("photo");
                            restaurant_item item = new restaurant_item(id,hot,fac,menuId,type,name,desc,price,discount,photo);
                            dinnerList.add(item);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("rrr" , e.getMessage());
                        ToastMaker.MakeToast(e.getMessage() , act);
                    }
                    restaurant_adapter dadapter = new restaurant_adapter(dinnerList , act);
                    if (dinnerList.size() > 0)
                    {
                        dinner.setAdapter(dadapter);

                        if (dinnerList.size()<4){
                            ImageView previous , next ;
                            previous = (ImageView) findViewById(R.id.leftSlide);
                            next = (ImageView) findViewById(R.id.imageView18);
                            previous.setVisibility(View.GONE);
                            next.setVisibility(View.GONE);
                        }
                        else {
                            ImageView previous , next ;
                            previous = (ImageView) findViewById(R.id.leftSlide);
                            next = (ImageView) findViewById(R.id.imageView18);
                            previous.setVisibility(View.VISIBLE);
                            next.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    l.stop();
                    ToastMaker.MakeToast(error.getMessage() , act);
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("menuId" , String.valueOf(menuId));
                    params.put("Hotel" , String.valueOf(Hotel));
                    params.put("Facility" , String.valueOf(Facility));
                    return params;
                }
            };
        }
        else if (Type.equals("CoffeeShop"))
        {
            url = LogIn.URL+"getCoffeeShopItems.php";
            re = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                             l.stop();
                             try
                            {
                                JSONArray arr = new JSONArray(response);
                                for (int i =0 ; i < arr.length() ; i++ )
                                {
                                    JSONObject row = arr.getJSONObject(i);
                                    String Menu = row.getString("Menu");
                                    int Hotel = row.getInt("Hotel");
                                    int Facility = row.getInt("Facility");
                                    int id = row.getInt("id");
                                    int menuId = row.getInt("MenuId");
                                    String name = row.getString("Name");
                                    String desc = row.getString("Desc");
                                    double price = row.getDouble("Price");
                                    double discount = row.getDouble("Discount");
                                    String photo = row.getString("photo");
                                    restaurant_item item = new restaurant_item( id,Hotel,Facility,menuId,Menu,name,desc,price,discount,photo);
                                    dinnerList.add(item);
                                }
                                restaurant_adapter dadapter = new restaurant_adapter(dinnerList , act);
                                dinner.setAdapter(dadapter);
                                if (dinnerList.size()<4){
                                    ImageView previous , next ;
                                    previous = (ImageView) findViewById(R.id.leftSlide);
                                    next = (ImageView) findViewById(R.id.imageView18);
                                    previous.setVisibility(View.GONE);
                                    next.setVisibility(View.GONE);
                                }
                                else {
                                    ImageView previous , next ;
                                    previous = (ImageView) findViewById(R.id.leftSlide);
                                    next = (ImageView) findViewById(R.id.imageView18);
                                    previous.setVisibility(View.VISIBLE);
                                    next.setVisibility(View.VISIBLE);
                                }
                            }
                             catch (JSONException e)
                            {
                                e.printStackTrace();
                                Log.e("rrr" , e.getMessage());
                                //ToastMaker.MakeToast(response , act);
                            }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    l.stop();
                    ToastMaker.MakeToast(error.getMessage() , act);
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("menuId" , String.valueOf(menuId));
                    params.put("Hotel" , String.valueOf(LogIn.room.getHotel()));
                    params.put("Facility" , String.valueOf(Facility));
                    return params;
                }
            };
        }
        myRefDND = FullscreenActivity.myRefDND;
        myRefDND.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (Integer.parseInt(snapshot.getValue().toString()) > 0 )
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
                if (Integer.parseInt(snapshot.getValue().toString()) > 0 )
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
                if (Integer.parseInt(snapshot.getValue().toString()) > 0 )
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
                if (Integer.parseInt(snapshot.getValue().toString())>0)
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
                if (Integer.parseInt(snapshot.getValue().toString()) > 0 )
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
        getRestaurantItems();
        KeepScreenFull();
        mainlayout = (ConstraintLayout) findViewById(R.id.rightSlide);
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
    protected void onResume()
    {
        super.onResume();
        if (FullscreenActivity.order.getItems().size() != 0 )
        {
            items.setText(String.valueOf(FullscreenActivity.order.getItems().size()));
        }
        if (x == 0 ){
            backHomeThread.run();
        }
    }



    void getRestaurantItems()
    {
        Volley.newRequestQueue(act).add(re);
    }

    public void gToCarto(View view)
    {
        if (FullscreenActivity.order.isEmpty())
        {
            ToastMaker.MakeToast("لم تقم باضافة شيء" , act);
        }
        else
        {
            H.removeCallbacks(backHomeThread);
            x=0;
            Intent i = new Intent(this ,Cart.class );
            i.putExtra("Facility" , Facility ) ;
            startActivity(i);
        }
        x=0;
    }

    public void back(View view) {
        if (FullscreenActivity.RestaurantActivities.size() > 0) {
            FullscreenActivity.RestaurantActivities.get(FullscreenActivity.RestaurantActivities.size() - 1).finish();
            FullscreenActivity.RestaurantActivities.remove(FullscreenActivity.RestaurantActivities.size() - 1);
            H.removeCallbacks(backHomeThread);
        }
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

    public  void removeLaundryOrderInDataBase()
    {
        try
        {
            LoadingDialog loading = new LoadingDialog(act);
            final String dep = "Laundry";

            StringRequest removOrder = new StringRequest(Request.Method.POST, removeServiceOrderUrl , new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    loading.stop();
                    if (response.equals("1")  )
                    {
                        if (FullscreenActivity.THEROOM.getSERVICE_B() != null){
                            if (FullscreenActivity.THEROOM.getSERVICE_B().dps.get("3").toString().equals("true"))
                            {
                                FullscreenActivity.THEROOM.getSERVICE().publishDps("{\"3\":false}", new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }

                        }
                        FullscreenActivity.LaundryStatus = false ;
                        myRefLaundry.setValue(0);
                        laundryOff();
                        ToastMaker.MakeToast(dep+" Order Cancelled" , act);
                    }
                    else
                    {
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
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("id" , "1");
                    params.put("room" , String.valueOf( LogIn.room.getRoomNumber()));
                    params.put("dep" , dep);
                    params.put("Hotel" , String.valueOf( LogIn.room.getHotel()));
                    return params;
                }
            };
            Volley.newRequestQueue(act).add(removOrder);
        }
        catch (Exception e)
        {

        }
    }


    public void setDND(View view) {

        if (!FullscreenActivity.DNDStatus ) {
            String dep = "DND";
            Calendar x = Calendar.getInstance(Locale.getDefault());
            long timee = x.getTimeInMillis();
            LoadingDialog loading = new LoadingDialog(act);
            FullscreenActivity.DNDStatus = true;
            StringRequest request = new StringRequest(Request.Method.POST, insertServiceOrderUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (FullscreenActivity.THEROOM.getSERVICE_B() != null) {
                        Log.d("serviceSwitch", "not null");
                        Log.d("serviceSwitch", FullscreenActivity.THEROOM.getSERVICE_B().dps.toString());

                        if (FullscreenActivity.THEROOM.getSERVICE_B().dps.get("1").toString().equals("false")) {

                            FullscreenActivity.THEROOM.getSERVICE().publishDps("{\"1\":true}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    Log.d("serviceSwitch", error);
                                }

                                @Override
                                public void onSuccess() {
                                    Log.d("serviceSwitch", "success");
                                }
                            });
                        } else {
                            Log.d("serviceSwitch", "is null");
                        }

                    }
                    if (FullscreenActivity.CleanupStatus) {
                        FullscreenActivity.removeCleanupOrderInDataBase();
                    }
                    if (FullscreenActivity.LaundryStatus) {
                        removeLaundryOrderInDataBase();
                    }
                    try {
                        Log.e("DND", response);
                        if (Integer.parseInt(response) > 0) {
                            loading.stop();
                            int dndId = Integer.parseInt(response);
                            myRefDND.setValue(dndId);
                            myRefdep.setValue("DND");
                            dndOn();
                        }
                    } catch (Exception e) {
                        Log.e("DND", e.getMessage());
                    }

                }
            }
                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("DNDerror", error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("roomNumber", String.valueOf(LogIn.room.getRoomNumber()));
                    params.put("time", String.valueOf(timee));
                    params.put("dep", dep);
                    params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
                    params.put("RorS", String.valueOf(FullscreenActivity.RoomOrSuite));
                    params.put("Reservation", String.valueOf(FullscreenActivity.RESERVATION));
                    return params;
                }
            };
            Volley.newRequestQueue(act).add(request);
        }
        else {
            String dep = "DND";
            LoadingDialog loading = new LoadingDialog(act);
            FullscreenActivity.DNDStatus = false;
            StringRequest rrr = new StringRequest(Request.Method.POST, removeServiceOrderUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
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
                        }else {Log.d("serviceSwitch" , "is null");}
                    }
                    if (response.equals("1")) {
                        loading.stop();
                        myRefDND.setValue(0);
                        dndOff();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id", String.valueOf(FullscreenActivity.dndId));
                    params.put("room", String.valueOf(LogIn.room.getRoomNumber()));
                    params.put("dep", dep);
                    params.put("Hotel", String.valueOf(LogIn.room.getHotel()));
                    return params;
                }
            };
            Volley.newRequestQueue(act).add(rrr);
        }
    }

    public void OpenTheDoor(View view) {
        FullscreenActivity.OpenTheDoor(view);
    }

    public void SOS(View view) {
        FullscreenActivity.SOS(view);
    }
}
