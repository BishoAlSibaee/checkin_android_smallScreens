package com.syriasoft.cleanup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RestaurantOrders extends AppCompatActivity {

    static String ordersUrl = LogIn.URL + "getRestaurantOrders.php";
    static RestaurantOrdersAdapter adapter;
    static List<restaurant_order_unit> list = new ArrayList<restaurant_order_unit>();
    static Activity act;
    static ListView orders;
    static ProgressBar p;
    static List<ROOM> Rooms;
    private String getFacilityUrl = LogIn.URL + "getFacility.php";
    public final String SHARED_PREF_NAME = "MyPref";
    private FirebaseDatabase database;
    private ValueEventListener[] RESTAURANTListiner;
    private int FACILITY_ID,TypeId;
    String TYPE,NAME,PHOTO;
    static FACILITY THEFACILITY;
    private GridView ORDERS;
    static int RoomId;
    private TextView FacilityName;
    private ImageView FacilityImage;
    Button logout;
    static public DatabaseReference MyFireUser;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor ;
    public static restaurant_order_unit SELECTED_ORDER ;
    public static ROOM SELECTED_ROOM ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_orders);
        setActivity();
        setActivityActions();
        getRooms();
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult().getToken();
                        sendRegistrationToServer(token);
                    }
                });
        Timer t = new Timer() ;
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult().getToken();
                        sendRegistrationToServer(token);
                    }
                });
            }},1000*60*15,1000*60*15);
        getRestaurantOrders();
    }

    void setActivity() {
        act = this;
        ORDERS = findViewById(R.id.gridView);
        Rooms = new ArrayList<>();
        p = findViewById(R.id.progressBar3);
        FacilityName = findViewById(R.id.facility_Name);
        FacilityImage = findViewById(R.id.facility_image);
        orders = findViewById(R.id.restaurant_orders);
        logout = findViewById(R.id.button5);
        database = FirebaseDatabase.getInstance("https://hotelservices-ebe66.firebaseio.com/");
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        FACILITY_ID = Integer.parseInt(sharedPreferences.getString("FacilityId", null));
        TypeId = Integer.parseInt(sharedPreferences.getString("FacilityTypeId", null));
        TYPE = sharedPreferences.getString("FacilityType", null);
        NAME = sharedPreferences.getString("FacilityName", null);
        PHOTO = sharedPreferences.getString("FacilityPhoto", null);
        THEFACILITY = new FACILITY(FACILITY_ID,1,TypeId,TYPE,NAME,0,PHOTO);
        FacilityName.setText(THEFACILITY.Name);
        Picasso.get().load(THEFACILITY.photo).into(FacilityImage);
    }

    void setActivityActions() {
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sgnOut(v);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button2:
                Button x = findViewById(R.id.button5);
                sgnOut(x);
        }
        return super.onOptionsItemSelected(item);
    }

    public void getRestaurantOrders() {
        p.setVisibility(View.VISIBLE);
        String url = MyApp.URL + "facilitys/getRestOrders";
        StringRequest re = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("ordersResp" ,response);
                p.setVisibility(View.GONE);
                try {
                    JSONObject result = new JSONObject(response);
                    if (result.getString("result").equals("success")) {
                        list.clear();
                        JSONArray arr = new JSONArray(result.getString("orders"));
                        if (arr.length() > 0) {
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject row = arr.getJSONObject(i);
                                restaurant_order_unit order = new restaurant_order_unit(row.getInt("id"), row.getInt("Hotel"), row.getInt("Facility"), row.getInt("Reservation"), row.getInt("room"), row.getInt("RorS"), row.getInt("roomId"), row.getLong("dateTime"), row.getDouble("total"), row.getInt("status"));
                                list.add(order);
                            }
                        }
                        adapter = new RestaurantOrdersAdapter(list, act);
                        ORDERS.setAdapter(adapter);
                    }
                    else {
                        new messageDialog(result.getString("error"),"failed",act);
                    }
                } catch (JSONException e) {
                    Log.d("ordersResp" ,e.getMessage());
                    p.setVisibility(View.GONE);
                    new messageDialog(e.getMessage(),"failed",act);
                }
//                if (response.equals("0")) {
//                    list.clear();
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            p.setVisibility(View.GONE);
//                        }
//                    });
//                    Toast.makeText(act, "No Orders", Toast.LENGTH_LONG).show();
//                } else {
//                    try {
//                        list.clear();
//                        JSONArray arr = new JSONArray(response);
//                        if (arr.length() > 0) {
//                            for (int i = 0; i < arr.length(); i++) {
//                                JSONObject row = arr.getJSONObject(i);
//                                restaurant_order_unit order = new restaurant_order_unit(row.getInt("id"), row.getInt("Hotel"), row.getInt("Facility"), row.getInt("Reservation"), row.getInt("room"), row.getInt("RorS"), row.getInt("roomId"), row.getLong("dateTime"), row.getDouble("total"), row.getInt("status"), row.getLong("responseDateTime"));
//                                list.add(order);
//                            }
//                        }
//                    } catch (JSONException e) {
//                        Toast.makeText(act, e.getMessage(), Toast.LENGTH_LONG).show();
//                        e.printStackTrace();
//                    }
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            p.setVisibility(View.GONE);
//                        }
//                    });
//
//                    adapter = new RestaurantOrdersAdapter(list, act);
//                    ORDERS.setAdapter(adapter);
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ordersResp" ,error.toString());
                p.setVisibility(View.GONE);
                new messageDialog(error.toString(),"failed",act);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> par = new HashMap<>();
                par.put("facility_id", String.valueOf(THEFACILITY.id));
                return par;
            }
        };
        Volley.newRequestQueue(act).add(re);
    }

    private void getRooms() {
        String url = MyApp.URL + "roomsManagement/getRooms" ;
        LoadingDialog loading = new LoadingDialog(act);
        StringRequest re = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("rooms", response);
                loading.close();
                try {
                    JSONArray arr = new JSONArray(response);
                    Rooms.clear();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject row = arr.getJSONObject(i);
                        ROOM room = new ROOM(row.getInt("id"), row.getInt("RoomNumber"), row.getInt("hotel"), row.getInt("Building"), row.getInt("building_id"), row.getInt("Floor"), row.getInt("floor_id"), row.getString("RoomType"), row.getInt("SuiteStatus"), row.getInt("SuiteNumber"), row.getInt("SuiteId"), row.getInt("ReservationNumber"), row.getInt("roomStatus"), row.getInt("Tablet"), row.getString("dep"), row.getInt("Cleanup"), row.getInt("Laundry"), row.getInt("RoomService"), row.getInt("Checkout"), row.getInt("Restaurant"), row.getInt("SOS"), row.getInt("DND"), row.getInt("PowerSwitch"), row.getInt("DoorSensor"), row.getInt("MotionSensor"), row.getInt("Thermostat"), row.getInt("ZBGateway"), row.getInt("CurtainSwitch"), row.getInt("ServiceSwitch"), row.getInt("lock"), row.getInt("Switch1"), row.getInt("Switch2"), row.getInt("Switch3"), row.getInt("Switch4"), row.getString("LockGateway"), row.getString("LockName"), row.getInt("powerStatus"), row.getInt("curtainStatus"), row.getInt("doorStatus"), row.getInt("temp"), row.getString("token"));
                        room.setFireRoom(database.getReference(MyApp.Project + "/B" + room.Building + "/F" + room.Floor + "/R" + room.RoomNumber));
                        Rooms.add(room);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (Rooms.size() > 0) {
                    RESTAURANTListiner = new ValueEventListener[Rooms.size()];
                    Intent NotificationsService = new Intent(act, ReceivingService.class);
                    startService(NotificationsService);
                    setListiner();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.close();
            }
        });
        Volley.newRequestQueue(act).add(re);
    }

    public void sgnOut(View view) {
        for (int i = 0; i < Rooms.size(); i++) {
            Rooms.get(i).getFireRoom().child("Restaurant").removeEventListener(RESTAURANTListiner[i]);
            Rooms.get(i).getFireRoom().child("Restaurant").removeEventListener(ReceivingService.RESTAURANTListiner[i]);
        }
        editor.putString("Id", null);
        editor.putString("Name", null);
        editor.putString("Control", null);
        editor.putString("JobNumber", null);
        editor.putString("Department", null);
        editor.apply();
        Intent i = new Intent(act, LogIn.class);
        startActivity(i);
        act.finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        getRestaurantOrders();
    }

    private void setListiner() {
        if (MyApp.My_USER.department.equals("Restaurant") || MyApp.My_USER.department.equals("CoffeeShop")) {
            for (int i = 0; i < Rooms.size(); i++) {
                final int finalI = i;
                RESTAURANTListiner[i] = Rooms.get(i).getFireRoom().child("Restaurant").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Rooms.get(finalI).getFireRoom().child("Facility").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //if (Integer.parseInt(dataSnapshot.getValue().toString()) == THEFACILITY.id) {
                                    getRestaurantOrders();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }
    }

    void sendRegistrationToServer(final String token) {
        String url = MyApp.URL + "facilitys/setFacilityUserToken";
        StringRequest r = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TokenResp", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("user", String.valueOf(MyApp.My_USER.name));
                params.put("facility_id", String.valueOf(THEFACILITY.id));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(r);
    }

    @Override
    public void onBackPressed() {

    }
}
