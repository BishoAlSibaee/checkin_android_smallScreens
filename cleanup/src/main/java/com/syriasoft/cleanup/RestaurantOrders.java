package com.syriasoft.cleanup;

import android.app.Activity;
import android.content.Intent;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantOrders extends AppCompatActivity {

    static String ordersUrl = LogIn.URL+"getRestaurantOrders.php";
    static RestaurantOrdersAdapter adapter ;
    static List<restaurant_order_unit> list = new ArrayList<restaurant_order_unit>();
    static Activity act  ;
    static ListView orders ;
    static ProgressBar p ;
    static List<ROOM> Rooms ;
    private String getRoomsUrl = LogIn.URL+"getAllRooms.php" ;
    private String getFacilityUrl = LogIn.URL+"getFacility.php";
    static public List<DatabaseReference> FireRooms ;
    private FirebaseDatabase database ;
    private ValueEventListener[] RESTAURANTListiner ;
    private int FACILITY_ID ;
    static FACILITY THEFACILITY ;
    //private RecyclerView ORDERS ;
    private GridView ORDERS ;
    static int RoomId ;
    private TextView mainText ,FacilityName ;
    private ImageView FacilityImage ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_orders);
        act = this ;
        ORDERS = (GridView) findViewById(R.id.gridView);
        FireRooms = new ArrayList<DatabaseReference>();
        Rooms = new ArrayList<ROOM>();
        FACILITY_ID = LogIn.db.getFacility();
        //Log.d("facility" , LogIn.db.getFacility()+"");
        getFacility();
        p = (ProgressBar) findViewById(R.id.progressBar3);
        mainText = (TextView) findViewById(R.id.mainText2);
        FacilityName = (TextView) findViewById(R.id.facility_Name);
        FacilityImage = (ImageView) findViewById(R.id.facility_image);
        Button logout = (Button) findViewById(R.id.button5);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sgnOut(v);
            }
        });
        mainText.setText(LogIn.db.getUser().department + " Orders");
        orders = (ListView) findViewById(R.id.restaurant_orders);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task)
                    {
                        if (!task.isSuccessful())
                        {

                            return;
                        }
                        String token = task.getResult().getToken();
                        //Toast.makeText(act,token , Toast.LENGTH_LONG).show();
                        sendRegistrationToServer(token);
                    }
                });
        getRestaurantOrders();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {

            case R.id.button2:
                Button x =(Button)findViewById(R.id.button5);
                sgnOut(x);
                //return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    public void getRestaurantOrders()
    {
        //Toast.makeText(act , FACILITY_ID+"", Toast.LENGTH_LONG).show();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run()
            {
                p.setVisibility(View.VISIBLE);
            }
        });

        StringRequest re = new StringRequest(Request.Method.POST, ordersUrl, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                //Toast.makeText(act , response , Toast.LENGTH_LONG).show();
                if (response.equals("0"))
                {
                    list.clear();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run()
                        {
                            p.setVisibility(View.GONE);
                        }
                    });
                    Toast.makeText(act , "No Orders" , Toast.LENGTH_LONG).show();
                }
                else
                {
                    try
                    {
                        list.clear();
                        JSONArray arr = new JSONArray(response);
                        if (arr.length()>0)
                        {
                            for (int i=0;i<arr.length();i++)
                            {
                                JSONObject row = arr.getJSONObject(i);
                                int id = row.getInt("id");
                                int hotel = row.getInt("Hotel");
                                int facility = row.getInt("Facility");
                                int reservation = row.getInt("Reservation");
                                int room =row.getInt("room");
                                int rors =row.getInt("RorS");
                                int roomid =row.getInt("roomId");
                                long dateTime = row.getLong("dateTime") ;
                                double total = row.getDouble("total") ;
                                int status = row.getInt("status");
                                long responseDateTime = row.getLong("responseDateTime");
                                restaurant_order_unit order = new restaurant_order_unit(id,hotel,facility,reservation,room,rors,roomid,dateTime,total,status,responseDateTime);
                                list.add(order);
                            }
                        }

                    }
                    catch (JSONException e)
                    {
                        Toast.makeText(act , e.getMessage() , Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run()
                        {
                            p.setVisibility(View.GONE);
                        }
                    });

                    adapter = new RestaurantOrdersAdapter(list,act );
                    ORDERS.setAdapter(adapter);
                    //orders.setAdapter(adapter);
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run()
                    {
                        p.setVisibility(View.GONE);
                    }
                });
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> par = new HashMap<String, String>();
                par.put("Facility" , String.valueOf(FACILITY_ID) );
                return par;
            }
        };
        Volley.newRequestQueue(act).add(re);
    }

    private void getRooms()
    {

        final LoadingDialog loading = new LoadingDialog(act);
        StringRequest re = new StringRequest(Request.Method.POST, getRoomsUrl, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Log.d("rooms" , response);
                loading.close();
                try
                {

                    JSONArray arr = new JSONArray(response);
                    Rooms.clear();
                    FireRooms.clear();
                    for (int i=0;i<arr.length();i++)
                    {
                        JSONObject row = arr.getJSONObject(i);
                        int id = row.getInt("id");
                        int rNum = row.getInt("RoomNumber");
                        int Hotel = row.getInt("hotel");
                        int b = row.getInt("Building");
                        int bId = row.getInt("BuildingId");
                        int f = row.getInt("Floor");
                        int fId = row.getInt("FloorId");
                        String rType = row.getString("RoomType");
                        int ss = row.getInt("SuiteStatus");
                        int sn = row.getInt("SuiteNumber");
                        int si = row.getInt("SuiteId");
                        int rn = row.getInt("ReservationNumber");
                        int rs = row.getInt("roomStatus");
                        int t = row.getInt("Tablet");
                        String dep = row.getString("dep");
                        int c = row.getInt("Cleanup");
                        int l = row.getInt("Laundry");
                        int roomS = row.getInt("RoomService");
                        int ch = row.getInt("Checkout");
                        int res = row.getInt("Restaurant");
                        int sos = row.getInt("SOS");
                        int dnd = row.getInt("DND");
                        int PowerSwitch = row.getInt("PowerSwitch");
                        int DoorSensor = row.getInt("DoorSensor");
                        int MotionSensor = row.getInt("MotionSensor");
                        int Thermostat = row.getInt("Thermostat");
                        int zbgateway = row.getInt("ZBGateway");
                        int CurtainSwitch = row.getInt("CurtainSwitch");
                        int ServiceSwitch = row.getInt("ServiceSwitch");
                        int lock = row.getInt("lock");
                        int Switch1 = row.getInt("Switch1");
                        int Switch2 = row.getInt("Switch2");
                        int Switch3 = row.getInt("Switch3");
                        int Switch4 = row.getInt("Switch4");
                        String LockGateway = row.getString("LockGateway");
                        String LockName = row.getString("LockName");
                        int po = row.getInt("powerStatus");
                        int cu = row.getInt("curtainStatus");
                        int doo = row.getInt("doorStatus");
                        int temp = row.getInt("temp");
                        String token =row.getString("token");
                        ROOM room = new ROOM(id,rNum,Hotel,b,bId,f,fId,rType,ss,sn,si,rn,rs,t,dep,c,l,roomS,ch,res,sos,dnd,PowerSwitch,DoorSensor,MotionSensor,Thermostat,zbgateway,CurtainSwitch,ServiceSwitch,lock,Switch1,Switch2,Switch3,Switch4,LockGateway,LockName,po,cu,doo,temp,token);
                        room.printRoomOnLog();
                        Rooms.add(room);
                        database = FirebaseDatabase.getInstance("https://hotelservices-ebe66.firebaseio.com/");
                        FireRooms.add(database.getReference(LogIn.Project+"/B"+room.Building+"/F"+room.Floor+"/R"+room.RoomNumber));
                    }
                    Log.d("roomCount" , "room "+Rooms.size()+" fires "+FireRooms.size());
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                if (Rooms.size()>0)
                {
                    RESTAURANTListiner = new ValueEventListener[Rooms.size()];
                    Intent NotificationsService = new Intent(act,ReceivingService.class);
                    startService(NotificationsService);
                    setListiner();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                loading.close();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> par = new HashMap<String, String>();
                par.put("Hotel" , String.valueOf(1));
                return par;
            }
        };
        Volley.newRequestQueue(act).add(re);
    }

    public void sgnOut(View view)
    {

        //FirebaseMessaging.getInstance().unsubscribeFromTopic(LogIn.db.getUser().department);
        for (int i=0;i<FireRooms.size();i++)
        {
            FireRooms.get(i).child("Restaurant").removeEventListener(RESTAURANTListiner[i]);
            FireRooms.get(i).child("Restaurant").removeEventListener(ReceivingService.RESTAURANTListiner[i]);
        }
        LogIn.db.logout();
        Intent i = new Intent(act , LogIn.class);
        startActivity(i);
        act.finish();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // put your code here...
        getRestaurantOrders();
    }

    private void setListiner()
    {
         if (LogIn.db.getUser().department.equals("Restaurant"))
        {
            for (int i=0;i<FireRooms.size();i++)
            {
                final int finalI = i;
                RESTAURANTListiner[i] = FireRooms.get(i).child("Restaurant").addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        FireRooms.get(finalI).child("Facility").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (Integer.parseInt(dataSnapshot.getValue().toString()) == LogIn.db.getFacility())
                                {
                                    getRestaurantOrders();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });
            }

        }
    }

    private void getFacility()
    {
        final FACILITY[] f = new FACILITY[1];
        StringRequest request = new StringRequest(Request.Method.POST, getFacilityUrl , new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                Log.d("facilityResponse" , response+" "+FACILITY_ID );
                if (response.equals("0"))
                {

                }
                else
                {
                    try
                    {
                        JSONArray arr = new JSONArray(response);
                        JSONObject row = arr.getJSONObject(0);
                        f[0] = new FACILITY(row.getInt("id"),row.getInt("Hotel"),row.getInt("TypeId"),row.getString("TypeName"),row.getString("Name"),row.getInt("Control"),row.getString("photo"));
                        THEFACILITY = f[0];
                        getRooms();
                        FacilityName.setText(THEFACILITY.Name);
                        Picasso.get().load(THEFACILITY.photo).into(FacilityImage);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> par = new HashMap<String, String>();
                par.put("Hotel" , "1");
                par.put("id" , String.valueOf( FACILITY_ID));
                return par;
            }
        };
        Volley.newRequestQueue(act).add(request);
    }

    void sendRegistrationToServer(final String token)
    {
        String url = LogIn.URL+"registerRestaurantToken.php";
        StringRequest r = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                Log.d("TokenResp" , response) ;
                if (response.equals("1"))
                {

                }
                else
                {

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
                Map<String,String> params = new HashMap<String,String>();
                params.put("token",token);
                params.put("id", String.valueOf(LogIn.db.getUser().id));
                return params;
            }
        };

        Volley.newRequestQueue(this ).add(r);

    }

    @Override
    public void onBackPressed() {

    }
}
