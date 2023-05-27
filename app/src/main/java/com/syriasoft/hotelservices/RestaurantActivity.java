package com.syriasoft.hotelservices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
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

public class RestaurantActivity extends AppCompatActivity {

    List<restaurant_item> dinnerList = new ArrayList<restaurant_item>();
    List<RestaurantOrderItem> orderList = new ArrayList<RestaurantOrderItem>();
    static RecyclerView  dinner ,  ordersRecycler ;
    String url ;
    Activity act = this ;
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
    private LinearLayout mainlayout ;
    WindowInsetsControllerCompat windowInsetsController;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant2);
        date = findViewById(R.id.mainDate);
        time = findViewById(R.id.mainTime);
        blink();
        Bundle b = getIntent().getExtras();
        menuId = b.getInt("id");
        menuName = b.getString("name");
        menuNameArabic = b.getString("arabic");
        Hotel = b.getInt("Hotel");
        Facility = b.getInt("Facility");
        Type = b.getString("Type");
        CAPTION = findViewById(R.id.CAPTION);
        CAPTION.setText("RESTAURANT");
        FullscreenActivity.RestaurantActivities.add(act);
        TextView caption = findViewById(R.id.menuName);
        items = findViewById(R.id.items_quantity_incart);
        if (FullscreenActivity.order.getItems().size() != 0 ) {
            items.setText(String.valueOf(FullscreenActivity.order.getItems().size()));
        }
        caption.setText(menuNameArabic);
        final GridLayoutManager layoutManagerdinner = new GridLayoutManager(this,1,RecyclerView.HORIZONTAL,false);
        layoutManagerdinner.setOrientation(LinearLayoutManager.HORIZONTAL);
        dinner = findViewById(R.id.Dinner);
        dinner.setLayoutManager(layoutManagerdinner);
        dinner.stopNestedScroll();
        final LoadingDialog l = new LoadingDialog(act);
        if (Type.equals("Restaurant")) {
            url = MyApp.ProjectURL + "facilitys/getRestaurantMenueMealsForRoom";
            re = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    l.stop();
                    try {
                        JSONObject result = new JSONObject(response);
                        if (result.getString("result").equals("success")) {
                            JSONArray arr = new JSONArray(result.getString("meals"));
                            for (int i =0 ; i < arr.length() ; i++ ) {
                                JSONObject row = arr.getJSONObject(i);
                                String type = row.getString("menu");
                                int id = row.getInt("id");
                                int fac = row.getInt("facility_id");
                                int hot = row.getInt("Hotel");
                                int menuId = row.getInt("restaurantmenue_id");
                                String name = row.getString("name");
                                String desc = row.getString("desc");
                                double price = row.getDouble("price");
                                double discount = row.getDouble("descount");
                                String photo = row.getString("photo");
                                restaurant_item item = new restaurant_item(id,hot,fac,menuId,type,name,desc,price,discount,photo);
                                dinnerList.add(item);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("rrr" , e.getMessage());
                        new messageDialog(e.getMessage(),"failed",act);
                    }
                    restaurant_adapter dadapter = new restaurant_adapter(dinnerList , act);
                    if (dinnerList.size() > 0) {
                        dinner.setAdapter(dadapter);
                        if (dinnerList.size()<4){
                            ImageView previous , next ;
                            previous = findViewById(R.id.leftSlide);
                            next = findViewById(R.id.imageView18);
                            previous.setVisibility(View.GONE);
                            next.setVisibility(View.GONE);
                        }
                        else {
                            ImageView previous , next ;
                            previous = findViewById(R.id.leftSlide);
                            next = findViewById(R.id.imageView18);
                            previous.setVisibility(View.VISIBLE);
                            next.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    l.stop();
                    new messageDialog(error.toString(),"failed",act);
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("menue_id" , String.valueOf(menuId));
                    params.put("facility_id" , String.valueOf(Facility));
                    return params;
                }
            };
        }
        else if (Type.equals("CoffeeShop")) {
            url = MyApp.ProjectURL + "facilitys/getCoffeShopMenueMealsForRoom";
            re = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    l.stop();
                    try {
                        JSONObject result = new JSONObject(response);
                        if (result.getString("result").equals("success")) {
                            JSONArray arr = new JSONArray(result.getString("meals"));
                            for (int i =0 ; i < arr.length() ; i++ ) {
                                JSONObject row = arr.getJSONObject(i);
                                String type = row.getString("Menu");
                                int id = row.getInt("id");
                                int fac = row.getInt("facility_id");
                                int hot = row.getInt("Hotel");
                                int menuId = row.getInt("coffeeshopmenue_id");
                                String name = row.getString("Name");
                                String desc = row.getString("Desc");
                                double price = row.getDouble("Price");
                                double discount = row.getDouble("Discount");
                                String photo = row.getString("photo");
                                restaurant_item item = new restaurant_item(id,hot,fac,menuId,type,name,desc,price,discount,photo);
                                dinnerList.add(item);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("rrr" , e.getMessage());
                        new messageDialog(e.getMessage(),"failed",act);
                    }
                    restaurant_adapter dadapter = new restaurant_adapter(dinnerList , act);
                    if (dinnerList.size() > 0) {
                        dinner.setAdapter(dadapter);
                        if (dinnerList.size()<4){
                            ImageView previous , next ;
                            previous = findViewById(R.id.leftSlide);
                            next = findViewById(R.id.imageView18);
                            previous.setVisibility(View.GONE);
                            next.setVisibility(View.GONE);
                        }
                        else {
                            ImageView previous , next ;
                            previous = findViewById(R.id.leftSlide);
                            next = findViewById(R.id.imageView18);
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
                    Map<String,String> params = new HashMap<>();
                    params.put("menue_id" , String.valueOf(menuId));
                    params.put("facility_id" , String.valueOf(Facility));
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
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE);
        getRestaurantItems();
        KeepScreenFull();
        mainlayout = findViewById(R.id.rightSlide);
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
        if (FullscreenActivity.order.getItems().size() != 0 ) {
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

    public void gToCarto(View view) {
        if (FullscreenActivity.order.isEmpty()) {
            ToastMaker.MakeToast("لم تقم باضافة شيء" , act);
        }
        else {
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
                        if (FullscreenActivity.THEROOM.getSERVICE1_B() != null){
                            if (FullscreenActivity.THEROOM.getSERVICE1_B().dps.get("3").toString().equals("true"))
                            {
                                FullscreenActivity.THEROOM.getSERVICE1().publishDps("{\"3\":false}", new IResultCallback() {
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
                    params.put("room" , String.valueOf(MyApp.Room.RoomNumber));
                    params.put("dep" , dep);
                    params.put("Hotel" ,"1");
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
