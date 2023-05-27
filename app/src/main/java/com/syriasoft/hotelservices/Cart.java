package com.syriasoft.hotelservices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
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
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
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
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.ControlLockCallback;
import com.ttlock.bl.sdk.constant.ControlAction;
import com.ttlock.bl.sdk.entity.ControlLockResult;
import com.ttlock.bl.sdk.entity.LockError;
import com.tuya.smart.sdk.api.IResultCallback;
import com.wang.avi.AVLoadingIndicatorView;

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
    static List<RestaurantOrderItem> list;
    static TextView orderTotal;
    static double total = 0  ;
    static String orderNumber = "";
    private int Reservation = 0 ;
    private int Facility ;
    static public DatabaseReference Room ,  Reserv , id , restaurant , dnd,myRefRorS ;
    private FirebaseDatabase database ;
    static private int  RoomOrSuite =1,RoomId ;
    private DatabaseReference myRefDND , myRefSos , myRefLaundry , myRefCleanup , myRefCheckout , myRefRoomService ,myRefdep ,myRefRestaurant;
    private TextView time , date;
    private static ImageView restaurantIcon ;
    static Runnable backHomeThread ;
    static long x = 0 ;
    static Handler H ;
    private LinearLayout mainlayout ;
    String geRestEmpsUrl = LogIn.URL+"getRestaurantEmps.php";
    List<REST_EMPS_CLASS> restEmps ;
    WindowInsetsControllerCompat windowInsetsController;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        setActivity();
        setActivityActions();
        KeepScreenFull();
        blink();
        setTotal();
    }

    void setActivity() {
        act = this;
        mainlayout = findViewById(R.id.rightSlide);
        restaurantIcon = findViewById(R.id.imageView2);
        orderTotal = findViewById(R.id.totalOrder);
        restEmps = new ArrayList<>();
        list = new ArrayList<>();
        itemsGridView = findViewById(R.id.recycler);
        TextView Caption = findViewById(R.id.CAPTION4);
        Caption.setText(getResources().getString(R.string.restaurant));
        GridLayoutManager manager = new GridLayoutManager(act, 1);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        FullscreenActivity.RestaurantActivities.add(act);
        list = FullscreenActivity.order.getItems();
        itemsGridView.setLayoutManager(manager);
        adapter = new RestaurantOrderAdapter(list, act);
        itemsGridView.stopNestedScroll();
        itemsGridView.setAdapter(adapter);
        Facility = getIntent().getExtras().getInt("Facility");
        database = FirebaseDatabase.getInstance("https://hotelservices-ebe66.firebaseio.com/");
        Room = database.getReference(MyApp.ProjectName+"/B"+MyApp.Room.Building+"/F"+MyApp.Room.Floor+"/R"+MyApp.Room.RoomNumber);
        Reserv = Room.child("ReservationNumber");
        restaurant = Room.child("Restaurant");
        myRefRorS=Room.child("SuiteStatus");
        date = findViewById(R.id.mainDate);
        time = findViewById(R.id.mainTime);
        id = Room.child("id");
        dnd = Room.child("DND");
        myRefRestaurant = FullscreenActivity.myRefRestaurant ;
        myRefDND = FullscreenActivity.myRefDND;
        myRefSos = FullscreenActivity.myRefSos;
        myRefLaundry = FullscreenActivity.myRefLaundry;
        myRefCleanup = FullscreenActivity.myRefCleanup;
        myRefCheckout = FullscreenActivity.myRefCheckout;
        myRefRoomService = FullscreenActivity.myRefRoomService;
        myRefdep = FullscreenActivity.myRefdep ;
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE);
    }

    void setActivityActions() {
        myRefRestaurant.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (!snapshot.getValue().toString().equals("0") ) {
                        FullscreenActivity.RestaurantStatus = true ;
                        restaurantOn();
                    }
                    else {
                        restaurantOff();
                        FullscreenActivity.RestaurantStatus = false ;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
        myRefDND.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (!snapshot.getValue().toString().equals("0")) {
                        dndOn();
                    }
                    else {
                        dndOff();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
        myRefSos.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.getValue() != null) {
                    if (!snapshot.getValue().toString().equals("0") ) {
                        sosOn();
                    }
                    else {
                        sosOff();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefLaundry.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (!snapshot.getValue().toString().equals("0") ) {
                        laundryOn();
                    }
                    else {
                        laundryOff();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefCleanup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (!snapshot.getValue().toString().equals("0")) {
                        cleanupOn();
                    }
                    else {
                        cleanupOff();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefCheckout.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (!snapshot.getValue().toString().equals("0") ) {
                        checkoutOn();
                    }
                    else {
                        checkoutOff();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefRoomService.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if ( !snapshot.getValue().toString().equals("0") ) {
                        roomServiceOn();
                    }
                    else {
                        roomServiceOff();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Reserv.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Reservation = Integer.parseInt( snapshot.getValue().toString() );
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        id.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    RoomId = Integer.parseInt( snapshot.getValue().toString() );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefRorS.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (Integer.parseInt(snapshot.getValue().toString()) == 1 ) {
                        RoomOrSuite = 1 ;
                    }
                    else if (Integer.parseInt(snapshot.getValue().toString()) == 2) {
                        RoomOrSuite = 2 ;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    public void sendOrder(View view) {
        Long time = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
        x=0;
        String url = MyApp.ProjectURL + "facilitys/addRestaurantOrder";
        LoadingDialog d = new LoadingDialog(act);
        StringRequest re = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                d.stop();
                try {
                    JSONObject result = new JSONObject(response);
                    if (result.getString("result").equals("success")) {
                        long localOrderNumber = FullscreenActivity.order.insertOldOrder(FullscreenActivity.THERESERVATION.ClientFirstName,FullscreenActivity.THERESERVATION.ClientLastName, total);
                        for (int i =0 ; i<list.size();i++) {
                            FullscreenActivity.order.insertOldOrderItem((int)localOrderNumber,list.get(i).type,list.get(i).name,list.get(i).desc,list.get(i).quantity,list.get(i).price,list.get(i).discount,list.get(i).total,list.get(i).photo);
                        }
                        FullscreenActivity.myRefFacility.setValue(Facility);
                        FullscreenActivity.myRefRestaurant.setValue(time);
                        MyApp.Room.Restaurant = 1 ;
                        Button totalBtn = findViewById(R.id.button3);
                        totalBtn.setVisibility(View.INVISIBLE);
                        for (int i = 0 ; i < Cart.itemsGridView.getAdapter().getItemCount(); i++) {
                            Button delete = Cart.itemsGridView.getChildAt(i).findViewById(R.id.button4);
                            delete.setVisibility(View.GONE);
                            Button update = Cart.itemsGridView.getChildAt(i).findViewById(R.id.button2);
                            update.setVisibility(View.GONE);
                        }
                        LinearLayout doneLayout = findViewById(R.id.doneLayout);
                        doneLayout.setVisibility(View.VISIBLE);
                        FullscreenActivity.order.removeOrder();
                        new messageDialog("order sent successfully","Done",act);
                    }
                    else {
                        new messageDialog(result.getString("error"),"Failed",act);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    new messageDialog(e.getMessage(),"Failed",act);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                d.stop();
                if (error.getMessage() != null) {
                    new messageDialog(error.getMessage(),"failed",act);
                }
                else {
                    new messageDialog(error.toString(),"failed",act);
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                double total = 0 ;
                for (int i=0;i<list.size();i++) {
                    total = total + list.get(i).total ;
                }
                Map<String,String> params = new HashMap<>();
                params.put("room_id" , String.valueOf(MyApp.Room.id));
                params.put("facility_id" , String.valueOf(Facility));
                params.put("total" , String.valueOf(total));
                params.put("countItems" ,String.valueOf(list.size()));
                for (int i =0 ; i<list.size();i++) {
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
        if ( Reservation > 0 && Facility > 0 ) {
            Volley.newRequestQueue(act).add(re);
        }
        else {
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

//    public void makemessage(String topic ,int room , String dep) {
//        String TOPIC = topic ;
//        String NOTIFICATION_TITLE = "Restaurant";
//        String NOTIFICATION_MESSAGE = "New Order From " +MyApp.Room.RoomNumber;
//
//        JSONObject notification = new JSONObject();
//        JSONObject notifcationBody = new JSONObject();
//        try {
//            notifcationBody.put("title", NOTIFICATION_TITLE);
//            notifcationBody.put("message", NOTIFICATION_MESSAGE);
//            //notifcationBody.put("service", "Restaurant");
//            notifcationBody.put("room", room);
//            notifcationBody.put("orderAction", false);
//            notifcationBody.put("dep", dep);
//            //notifcationBody.put("emp", LogIn.db.getUser().name);
//            notification.put("to", TOPIC);
//            notification.put("data", notifcationBody);
//        } catch (JSONException e) {
//            //Log.e(TAG, "onCreate: " + e.getMessage() );
//        }
//        sendNotification(notification);
//    }
//
//    void sendNotification(JSONObject notification) {
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_MESSAGE_URL, notification,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        //Log.i(TAG, "onResponse: " + response.toString());
//                        ToastMaker.MakeToast("تم ارسال طلبك",act);
//                        //messageDialog d = new messageDialog("تم ارسال طلبك","تاكيد"  ,act);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        //Toast.makeText(act, "Request error", Toast.LENGTH_LONG).show();
//                        //Log.i(TAG, "onErrorResponse: Didn't work");
//                    }
//                }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("Authorization", serverKey);
//                params.put("Content-Type", contentType);
//                return params;
//            }
//        };
//        Volley.newRequestQueue(act).add(jsonObjectRequest);
//
//    }

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
        ImageView checkouticon = findViewById(R.id.DND_Icon);
        checkouticon.setVisibility(View.VISIBLE);
        ImageView checkoutimage = (ImageView) findViewById(R.id.DND_Image);
        checkoutimage.setImageResource(R.drawable.union_6);
        TextView text = (TextView) findViewById(R.id.DND_Text);
        text.setTextColor(getResources().getColor(R.color.red));
    }
    private void dndOff(){
        ImageView checkouticon = (ImageView) findViewById(R.id.DND_Icon);
        checkouticon.setVisibility(View.GONE);
        ImageView checkoutimage = (ImageView) findViewById(R.id.DND_Image);
        checkoutimage.setImageResource(R.drawable.union_2);
        TextView text = (TextView) findViewById(R.id.DND_Text);
        text.setTextColor(getResources().getColor(R.color.light_blue_A200));
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
        for (int i = 0 ; i <list.size() ; i++) {
            total = total + (list.get(i).price * list.get(i).quantity );
        }
        orderTotal.setText(String.valueOf(total));
    }

    public static void refreshItems() {
        list = FullscreenActivity.order.getItems();
        adapter = new RestaurantOrderAdapter(list, act);
        itemsGridView.setAdapter(adapter);
    }

    public void setDND(View view) {
        if (MyApp.Room.getSERVICE1_B() != null) {
            if (MyApp.Room.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton)) != null) {
                if (Boolean.parseBoolean(MyApp.Room.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton)).toString())) {
                    MyApp.Room.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.dndButton+"\":false}", new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {
                            new messageDialog(error+" "+code,"failed",act);
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
                            new messageDialog(error+" "+code,"failed",act);
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
    }

}
