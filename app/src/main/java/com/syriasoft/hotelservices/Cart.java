package com.syriasoft.hotelservices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Cart extends AppCompatActivity
{

    static Activity act;
    static RestaurantOrderAdapter adapter;
    static RecyclerView itemsGridView;
    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAQmygXvw:APA91bFt5CiONiZPDDj4_kz9hmKXlL1cjfTa_ZNGfobMPmt0gamhzEoN2NHiOxypCDr_r5yfpLvJy-bQSgrykXvaqKkThAniTr-0hpXPBrXm7qWThMmkiaN9o6qaUqfIUwStMMuNedTw";
    final private String contentType = "application/json";
    static List<RestaurantOrderItem> list = new ArrayList<RestaurantOrderItem>();
    String sendUrl =LogIn.URL+"insertRestaurantOrder.php";
    String sendItemsUrl =LogIn.URL+ "insertRestaurantOrderItems.php";
    static TextView orderTotal ;
    static double total = 0  ;
    static String orderNumber = "";
    private int Reservation = 0 ;
    private int RoomId = 0 ;
    private int Facility ;
    static public DatabaseReference Room ,  Reserv , id , restaurant , dnd,myRefRorS ;
    private FirebaseDatabase database ;
    static private int  RoomOrSuite =1 ;
    private DatabaseReference myRefDND , myRefSos , myRefLaundry , myRefCleanup , myRefCheckout , myRefRoomService ,myRefdep ,myRefRestaurant;
    private static String insertServiceOrderUrl = LogIn.URL+"insertServiceOrder.php";
    private static String removeServiceOrderUrl = LogIn.URL+"removeServiceOrder.php";
    private TextView time , date;
    private static ImageView restaurantIcon ;
    static Runnable backHomeThread ;
    static long x = 0 ;
    static Handler H ;
    private ConstraintLayout mainlayout ;
    String geRestEmpsUrl = LogIn.URL+"getRestaurantEmps.php";
    List<REST_EMPS_CLASS> restEmps ;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        act = this;
        mainlayout = (ConstraintLayout) findViewById(R.id.rightSlide);
        restaurantIcon = (ImageView) findViewById(R.id.imageView2);
        orderTotal = (TextView) findViewById(R.id.totalOrder);
        restEmps = new ArrayList<REST_EMPS_CLASS>();
        itemsGridView = (RecyclerView) findViewById(R.id.recycler);
        GridLayoutManager manager = new GridLayoutManager(act, 1);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        FullscreenActivity.RestaurantActivities.add(act);
        list = FullscreenActivity.order.getItems();
        itemsGridView.setLayoutManager(manager);
        adapter = new RestaurantOrderAdapter(list, act);
        itemsGridView.stopNestedScroll();
        itemsGridView.setAdapter(adapter);
//        total = 0 ;
//        for (int i = 0 ; i <list.size() ; i++)
//        {
//            total = total + (list.get(i).price * list.get(i).quantity );
//        }
//        orderTotal.setText(String.valueOf(total));
        Facility = getIntent().getExtras().getInt("Facility");
        database = FirebaseDatabase.getInstance("https://hotelservices-ebe66.firebaseio.com/");
        Room = database.getReference(LogIn.room.getProjectName()+"/B"+LogIn.room.getBuilding()+"/F"+LogIn.room.getFloor()+"/R"+LogIn.room.getRoomNumber());
        Reserv = Room.child("ReservationNumber");
        restaurant = Room.child("Restaurant");
        myRefRorS=Room.child("SuiteStatus");
        date = (TextView) findViewById(R.id.mainDate);
        time = (TextView) findViewById(R.id.mainTime);
        id = Room.child("id");
        dnd = Room.child("DND");
        myRefRestaurant = FullscreenActivity.myRefRestaurant ;
        myRefRestaurant.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (!snapshot.getValue().toString().equals("0") )
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
                if (!snapshot.getValue().toString().equals("0"))
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
                if (!snapshot.getValue().toString().equals("0") )
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
                if (!snapshot.getValue().toString().equals("0") )
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
        /*restaurant.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String s = dataSnapshot.getValue().toString();
                if ( Integer.parseInt(s) > 0 )
                {
                    Button totalBtn = (Button) findViewById(R.id.button3);
                    totalBtn.setVisibility(View.INVISIBLE);
                    for (int i = 0 ; i < Cart.itemsGridView.getAdapter().getItemCount(); i++)
                    {
                        Button delete = (Button) Cart.itemsGridView.getChildAt(i).findViewById(R.id.button4);
                        delete.setVisibility(View.GONE);
                        Button update = (Button) Cart.itemsGridView.getChildAt(i).findViewById(R.id.button2);
                        update.setVisibility(View.GONE);
                    }
                    LinearLayout doneLayout = (LinearLayout) findViewById(R.id.doneLayout);
                    doneLayout.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                //Toast.makeText(act , databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });*/
        Reserv.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Reservation = Integer.parseInt( snapshot.getValue().toString() );
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        id.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                RoomId = Integer.parseInt( snapshot.getValue().toString() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefRorS.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (Integer.parseInt(snapshot.getValue().toString()) == 1 )
                {
                    RoomOrSuite = 1 ;
                }
                else if (Integer.parseInt(snapshot.getValue().toString()) == 2)
                {
                    RoomOrSuite = 2 ;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        KeepScreenFull();
        blink();
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
        setTotal();
        getRestEmps();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (x == 0 ){
            backHomeThread.run();
        }

    }

    static void refreshGrid()
    {
        //list.add(o);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                itemsGridView.invalidate();
                //Toast.makeText(act , "New Order",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void sendOrder(View view)
    {
        x=0;
        LoadingDialog d = new LoadingDialog(act);
        StringRequest re = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                if (Integer.parseInt(response) > 0 )
                {
                    orderNumber = response ;
                    final String dep = "Restaurant";
                    long localOrderNumber = FullscreenActivity.order.insertOldOrder(FullscreenActivity.THERESERVATION.ClientFirstName,FullscreenActivity.THERESERVATION.ClientLastName, total);
                    StringRequest req = new StringRequest(Request.Method.POST, sendItemsUrl, new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {

                            if (response.equals("1"))
                            {
                                FullscreenActivity.myRefFacility.setValue(Facility);
                                FullscreenActivity.myRefRestaurant.setValue(orderNumber);
                                dnd.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                    {
                                        if (Integer.parseInt(snapshot.getValue().toString()) > 0)
                                        {
                                            dnd.setValue(0);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                //makemessage("/topics/Restaurant" , LogIn.room.getRoomNumber() , dep );
//                                for(REST_EMPS_CLASS emp : restEmps) {
//                                    if (emp.Facility == Facility) {
//                                        emp.makemessage(emp.token,"Restaurant",true,act);
//                                    }
//                                }
                                Button totalBtn = (Button) findViewById(R.id.button3);
                                totalBtn.setVisibility(View.INVISIBLE);
                                for (int i = 0 ; i < Cart.itemsGridView.getAdapter().getItemCount(); i++)
                                {
                                   Button delete = (Button) Cart.itemsGridView.getChildAt(i).findViewById(R.id.button4);
                                   delete.setVisibility(View.GONE);
                                    Button update = (Button) Cart.itemsGridView.getChildAt(i).findViewById(R.id.button2);
                                    update.setVisibility(View.GONE);
                                }
                                for (int i =0 ; i<list.size();i++)
                                {
                                    FullscreenActivity.order.insertOldOrderItem((int)localOrderNumber,list.get(i).type,list.get(i).name,list.get(i).desc,list.get(i).quantity,list.get(i).price,list.get(i).discount,list.get(i).total,list.get(i).photo);
                                }
                                LinearLayout doneLayout = (LinearLayout) findViewById(R.id.doneLayout);
                                doneLayout.setVisibility(View.VISIBLE);
                                FullscreenActivity.order.removeOrder();
                                ToastMaker.MakeToast("تم ارسال طلبك " ,act);
                            }
                            d.stop();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            d.stop();
                            Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<String, String>();
                            params.put("room" , String.valueOf( LogIn.room.getRoomNumber()));
                            params.put("orderNumber" , orderNumber );
                            params.put("countItems" ,String.valueOf( list.size()));
                            for (int i =0 ; i<list.size();i++)
                            {
                                params.put("itemNo"+i ,String.valueOf( list.get(i).id));
                                params.put("name"+i , list.get(i).name);
                                params.put("desc"+i , list.get(i).desc);
                                params.put("quantity"+i , String.valueOf(list.get(i).quantity ));
                                params.put("price"+i , String.valueOf(list.get(i).price));
                                params.put("total"+i , String.valueOf(list.get(i).price * list.get(i).quantity));
                            }
                            return params;
                        }
                    };

                    Volley.newRequestQueue(act).add(req);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(act , error.getMessage(),Toast.LENGTH_LONG).show();

            }
        })
        {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<String, String>();
                params.put("roomNumber" , String.valueOf( LogIn.room.getRoomNumber() ));
                Calendar x = Calendar.getInstance(Locale.getDefault());
                double time =  x.getTimeInMillis();
                params.put( "time" , String.valueOf( time ) ) ;
                params.put( "total" , String.valueOf( total) ) ;
                params.put( "roomId" , String.valueOf( RoomId ) ) ;
                params.put( "Reservation" , String.valueOf( Reservation) ) ;
                params.put( "Facility" , String.valueOf( Facility ) ) ;
                params.put( "Hotel" , String.valueOf( LogIn.room.getHotel() ) ) ;
                params.put("RorS" ,String.valueOf( RoomOrSuite));
                return params;
            }
        };
        if ( Reservation > 0 && Facility > 0 )
        {
            Volley.newRequestQueue(act).add(re);
        }
        else
        {
            messageDialog m = new messageDialog("Couldn't Get Reservation Number " , "Error Reservation Number" ,act);
        }

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

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration
    {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }
    }

    public void makemessage(String topic ,int room , String dep)
    {

        String TOPIC = topic ;
        String NOTIFICATION_TITLE = "Restaurant";
        String NOTIFICATION_MESSAGE = "New Order From " + LogIn.room.getRoomNumber();

        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);
            //notifcationBody.put("service", "Restaurant");
            notifcationBody.put("room", room);
            notifcationBody.put("orderAction", false);
            notifcationBody.put("dep", dep);
            //notifcationBody.put("emp", LogIn.db.getUser().name);
            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            //Log.e(TAG, "onCreate: " + e.getMessage() );
        }
        sendNotification(notification);
    }

    void sendNotification(JSONObject notification)
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_MESSAGE_URL, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.i(TAG, "onResponse: " + response.toString());
                        ToastMaker.MakeToast("تم ارسال طلبك",act);
                        //messageDialog d = new messageDialog("تم ارسال طلبك","تاكيد"  ,act);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(act, "Request error", Toast.LENGTH_LONG).show();
                        //Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        Volley.newRequestQueue(act).add(jsonObjectRequest);

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
        //ImageView checkoutimage = (ImageView) findViewById(R.id.DND_Image);
        //checkoutimage.setImageResource(R.drawable.union_6);
        ImageView checkouticon = (ImageView) findViewById(R.id.DND_Icon);
        checkouticon.setVisibility(View.VISIBLE);
        //TextView text = (TextView) findViewById(R.id.DND_Text);
        //text.setTextColor(getResources().getColor(R.color.red));
    }
    private void dndOff(){
        //ImageView checkoutimage = (ImageView) findViewById(R.id.DND_Image);
        //checkoutimage.setImageResource(R.drawable.union_2);
        ImageView checkouticon = (ImageView) findViewById(R.id.DND_Icon);
        checkouticon.setVisibility(View.GONE);
        //TextView text = (TextView) findViewById(R.id.DND_Text);
        //text.setTextColor(getResources().getColor(R.color.light_blue_A200));
    }

    private void sosOn(){
        //ImageView checkoutimage = (ImageView) findViewById(R.id.SOS_Image);
        //checkoutimage.setImageResource(R.drawable.group_54);
        ImageView checkouticon = (ImageView) findViewById(R.id.SOS_Icon);
        checkouticon.setVisibility(View.VISIBLE);
        //TextView text = (TextView) findViewById(R.id.SOS_Text);
        //text.setTextColor(getResources().getColor(R.color.red));
    }
    private void sosOff(){
        //ImageView checkoutimage = (ImageView) findViewById(R.id.SOS_Image);
        //checkoutimage.setImageResource(R.drawable.group_33);
        ImageView checkouticon = (ImageView) findViewById(R.id.SOS_Icon);
        checkouticon.setVisibility(View.GONE);
        //TextView text = (TextView) findViewById(R.id.SOS_Text);
        //text.setTextColor(getResources().getColor(R.color.light_blue_A200));
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


    void getRestEmps() {
        StringRequest request = new StringRequest(Request.Method.POST, geRestEmpsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.equals("0")) {
                    try {
                        JSONArray arr = new JSONArray(response);
                        for (int i=0;i<arr.length();i++) {
                            JSONObject row = arr.getJSONObject(i);
                            REST_EMPS_CLASS emp = new REST_EMPS_CLASS(row.getInt("id"),row.getInt("Facility"),row.getString("UserName"),row.getString("Name"),row.getString("Mobile"),row.getString("token"));
                            restEmps.add(emp);
                        }
                        Log.d("EmpsCount" , restEmps.size()+"");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    ToastMaker.MakeToast("No service emps",act);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {

        };
        Volley.newRequestQueue(act).add(request);
    }

    public static void setTotal() {
        total = 0 ;
        list = FullscreenActivity.order.getItems();
        for (int i = 0 ; i <list.size() ; i++)
        {
            total = total + (list.get(i).price * list.get(i).quantity );
        }
        orderTotal.setText(String.valueOf(total));
    }

    public static void refreshItems() {
        list = FullscreenActivity.order.getItems();
        adapter = new RestaurantOrderAdapter(list, act);
        itemsGridView.setAdapter(adapter);
    }

}
