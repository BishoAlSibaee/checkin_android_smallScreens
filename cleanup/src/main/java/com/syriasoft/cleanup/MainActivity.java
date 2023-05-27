package com.syriasoft.cleanup;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
import com.google.gson.reflect.TypeToken;
import com.syriasoft.cleanup.TTLOCK.AccountInfo;
import com.syriasoft.cleanup.TTLOCK.ApiService;
import com.syriasoft.cleanup.TTLOCK.LockObj;
import com.syriasoft.cleanup.TTLOCK.RetrofitAPIManager;
import com.ttlock.bl.sdk.util.DigitUtil;
import com.ttlock.bl.sdk.util.GsonUtil;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {
    static ListView g;
    static List<cleanOrder> list ;
    private static DND_Adapter ada;
    private static CleanUp_Adapter adapter;
    public  static Activity act;
    private static String getRoomsUrl;
    static boolean activityStatus = false;
    private static FirebaseDatabase database;
    static List<ROOM> Rooms;
    private static RecyclerView dnds;
    private static ValueEventListener[] CleanupListiner;
    private static ValueEventListener[] LaundryListiner;
    private static ValueEventListener[] RoomServiceListiner;
    private static ValueEventListener[] DNDListiner;
    private static ValueEventListener[] SOSListiner;
    private static ValueEventListener[] MiniBarCheck;
    private static String DEP = "";
    static SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor ;
    public static final String SHARED_PREF_NAME = "MyPref";
    public final String KEY_TuyaUser = "TuyaUser";
    public final String KEY_TuyaPassword = "TuyaPassword";
    public final String KEY_LockUser = "LockUser";
    public final String KEY_LockPassword = "LockPassword";
    public final static String KEY_MyRooms = "MyRooms";
    private static AccountInfo accountInfo;
    static AccountInfo acc;
    static String password;
    static List<LockObj> lockObjs = new ArrayList<LockObj>();
    static List<HomeBean> Homs;
    static HomeBean THE_HOME;
    static List<DeviceBean> Devices;
    static OrdersDB orderDB;
    static DNDDB dndDB;
    static public DatabaseReference MyFireUser, myRoomsReference,DevicesRef;
    public static List<String> CurrentRoomsStatus;
    static String LockU;
    static String LockP;
    static String TuyaU;
    static String TuyaP;
    public static RequestQueue Q ;


    //--------------------------------------------------------
    //Activity Methods
    @Override
    protected void onStart() {
        super.onStart();
        activityStatus = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityStatus = false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActivity();
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        if (task.getResult() != null) {
                            String token = task.getResult().getToken();
                            MyFireUser.child("token").setValue(token);
                            sendRegistrationToServer(token, String.valueOf(MyApp.My_USER.id));
                        }
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
                    MyFireUser.child("token").setValue(token);
                    sendRegistrationToServer(token, String.valueOf(MyApp.My_USER.id));
                }
            });
            }},1000*60*15,1000*60*15);
        myRoomsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Log.d("controlValue", dataSnapshot.getValue().toString());
                    getRooms();
                } else {
                    Log.d("controlValue", "null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        getProjectVariables();
    }

    void setActivity() {
        act = this;
        User MYUSER = MyApp.My_USER;
        TextView mainText = findViewById(R.id.mainText);
        mainText.setText(DEP + " Orders");
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        dnds = findViewById(R.id.dnd_recycler);
        g = findViewById(R.id.cleanUpGrid);
        Q = Volley.newRequestQueue(act);
        orderDB = new OrdersDB(act);
        dndDB = new DNDDB(act);
        Rooms = new ArrayList<>();
        list = new ArrayList<>();
        ada = new DND_Adapter(dndDB.getOrders());
        adapter = new CleanUp_Adapter(orderDB.getOrders(), act);
        CurrentRoomsStatus = new ArrayList<>();
        dnds.setLayoutManager(manager);
        dnds.setAdapter(ada);
        orderDB.removeAll();
        g.setAdapter(adapter);
        database = FirebaseDatabase.getInstance("https://hotelservices-ebe66.firebaseio.com/");
        DEP = MYUSER.department;
        MyFireUser = database.getReference(MyApp.Project + "ServiceUsers/" + MYUSER.jobNumber);
        myRoomsReference = MyFireUser.child("control");
        DevicesRef = database.getReference(MyApp.Project+"Devices");
        MYUSER.control = "";
        MyFireUser.setValue(MYUSER);
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        LockU = sharedPreferences.getString(KEY_LockUser, null);
        LockP = sharedPreferences.getString(KEY_LockPassword, null);
        TuyaU = sharedPreferences.getString(KEY_TuyaUser, null);
        TuyaP = sharedPreferences.getString(KEY_TuyaPassword, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menu.getItem(0).setTitle(MyApp.My_USER.name);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button2:
                Button x = (Button) findViewById(R.id.button2);
                sgnOut(x);
                break;
            case R.id.goToRooms:
                Intent i = new Intent(act, ROOMS.class);
                startActivity(i);
                break;
            case R.id.changePassword:
                changePasswordDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void sendRegistrationToServer(String token,String id) {
        String url = MyApp.URL + "users/modifyUserFirebaseToken";
        StringRequest r = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TokenResp", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TokenResp", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("id", id);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(r);
    }

    public void sgnOut(View view) {
        removeOldListiners();
        deleteToken(String.valueOf(MyApp.My_USER.id),new VolleyCallback() {
            @Override
            public void onSuccess(String res) {
                try {
                    JSONObject result = new JSONObject(res);
                    if (result.getString("result").equals("success")) {
                        editor.putString("Id", null);
                        editor.putString("Name", null);
                        editor.putString("Control", null);
                        editor.putString("JobNumber", null);
                        editor.putString("Department", null);
                        editor.apply();
                        orderDB.removeAll();
                        dndDB.removeAll();
                        for (int i = 0; i < LogIn.actList.size(); i++) {
                            LogIn.actList.get(i).finish();
                        }
                        MyFireUser.child("token").setValue("");
                        Intent i = new Intent(act, LogIn.class);
                        startActivity(i);
                    }
                    else {
                        new messageDialog(result.getString("error"),"failed",act);
                    }
                } catch (JSONException e) {
                    new messageDialog(e.getMessage(),"failed",act);
                }
            }

            @Override
            public void onFailed(String error) {
                new messageDialog(error,"failed",act);
            }
        });
    }

    public static void getRooms() {
        LoadingDialog d = new LoadingDialog(act);
        getRoomsUrl = MyApp.URL + "users/getUserRooms";
        StringRequest re = new StringRequest(Request.Method.POST, getRoomsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                d.close();
                if (!response.equals("0")) {
                    try {
                        JSONArray arr = new JSONArray(response);
                        removeOldListiners();
                        Rooms.clear();
                        list.clear();
                        orderDB.removeAll();
                        dndDB.removeAll();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject row = arr.getJSONObject(i);
                            ROOM room = new ROOM(row.getInt("id"), row.getInt("RoomNumber"), row.getInt("hotel"), row.getInt("Building"), row.getInt("building_id"), row.getInt("Floor"), row.getInt("floor_id"), row.getString("RoomType"), row.getInt("SuiteStatus"), row.getInt("SuiteNumber"), row.getInt("SuiteId"), row.getInt("ReservationNumber"), row.getInt("roomStatus"), row.getInt("Tablet"), row.getString("dep"), row.getInt("Cleanup"), row.getInt("Laundry"), row.getInt("RoomService"), row.getInt("Checkout"), row.getInt("Restaurant"), row.getInt("SOS"), row.getInt("DND"), row.getInt("PowerSwitch"), row.getInt("DoorSensor"), row.getInt("MotionSensor"), row.getInt("Thermostat"), row.getInt("ZBGateway"), row.getInt("CurtainSwitch"), row.getInt("ServiceSwitch"), row.getInt("lock"), row.getInt("Switch1"), row.getInt("Switch2"), row.getInt("Switch3"), row.getInt("Switch4"), row.getString("LockGateway"), row.getString("LockName"), row.getInt("powerStatus"), row.getInt("curtainStatus"), row.getInt("doorStatus"), row.getInt("temp"), row.getString("token"));
                            room.setFireRoom(database.getReference(MyApp.Project + "/B" + room.Building + "/F" + room.Floor + "/R" + room.RoomNumber));
                            Rooms.add(room);
                        }
                        sortRoomsByNumber(Rooms);
                        MyApp.Rooms = Rooms;
                        saveMyRoomsInSharedPreferences(Rooms);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("userRooms",e.getMessage());
                    }
                    CleanupListiner = new ValueEventListener[Rooms.size()];
                    LaundryListiner = new ValueEventListener[Rooms.size()];


                    RoomServiceListiner = new ValueEventListener[Rooms.size()];
                    DNDListiner = new ValueEventListener[Rooms.size()];
                    SOSListiner = new ValueEventListener[Rooms.size()];
                    MiniBarCheck = new ValueEventListener[Rooms.size()];
                    setRoomsListeners();
                    filterOrderByRoomNumber(orderDB.getOrders(), Rooms);
                    filterDNDOrderByRoomNumber(dndDB.getOrders(), Rooms);
                    filterOrdersByDepartment(orderDB.getOrders());
                } else {
                    Rooms.clear();
                    list.clear();
                }
                if (ROOMS.adapter != null) {
                    ROOMS.adapter.notifyDataSetChanged();
                }
                auth(LockU, LockP); // lock
                goLogIn(TuyaU, TuyaP); // tuya
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                d.close();
                AlertDialog.Builder b = new AlertDialog.Builder(act);
                b.setTitle("Getting data failed")
                        .setMessage("failed to get data .. try again ?? ")
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                act.finish();
                            }
                        })
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                getRooms();
                            }
                        }).create().show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> par = new HashMap<String, String>();
                par.put("id", String.valueOf(MyApp.My_USER.id));
                return par;
            }
        };
        Volley.newRequestQueue(act).add(re);
    }

    public static void sortRoomsByNumber(List<ROOM> room) {
        for (int i = 0; i < room.size(); i++) {
            for (int j = 1; j < (room.size() - i); j++) {
                if (room.get(j - 1).RoomNumber > room.get(j).RoomNumber) {
                    Collections.swap(room, j, j - 1);
                }
            }
        }
    }

    public static void setRoomsListeners() {
        for (int i = 0; i < Rooms.size(); i++) {
            final int finalI = i;
            if (DEP.equals("Cleanup")) {
                CleanupListiner[i] = Rooms.get(i).getFireRoom().child("Cleanup").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (Long.parseLong(dataSnapshot.getValue().toString()) != 0) {
                            boolean status = false;
                            status = orderDB.searchOrder(Rooms.get(finalI).RoomNumber, "Cleanup");
                            if (!status) {
                                long timee = Long.parseLong(dataSnapshot.getValue().toString());
                                orderDB.insertOrder(Rooms.get(finalI).RoomNumber, "Cleanup", "", timee);
                                adapter = new CleanUp_Adapter(orderDB.getOrders(), act);
                                g.setAdapter(adapter);
                            }
                        } else {
                            for (int x = 0; x < orderDB.getOrders().size(); x++) {
                                if (Long.parseLong(orderDB.getOrders().get(x).roomNumber) == Rooms.get(finalI).RoomNumber && orderDB.getOrders().get(x).dep.equals("Cleanup")) {
                                    orderDB.removeRow(Long.parseLong(orderDB.getOrders().get(x).orderNumber));
                                    adapter = new CleanUp_Adapter(orderDB.getOrders(), act);
                                    g.setAdapter(adapter);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                DNDListiner[i] = Rooms.get(i).getFireRoom().child("DND").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Long.parseLong(dataSnapshot.getValue().toString()) > 0) {
                            boolean status = false;
                            status = dndDB.searchOrder(Rooms.get(finalI).RoomNumber, "DND");
                            //Log.d("DNDChanged" , status+"") ;
                            if (!status) {

                                long time = Long.parseLong(dataSnapshot.getValue().toString());
                                dndDB.insertOrder(Rooms.get(finalI).RoomNumber, "DND", "", time);
                                //Log.d("DNDChanged" , Rooms.get(finalI).RoomNumber+" i am changed to " + dataSnapshot.getValue().toString()+" in list after "+dndDB.getOrders().size()) ;
                            }
                            ada = new DND_Adapter(dndDB.getOrders());
                            dnds.setAdapter(ada);
                        } else if (Long.parseLong(dataSnapshot.getValue().toString()) == 0) {
                            for (int x = 0; x < dndDB.getOrders().size(); x++) {
                                if (Long.parseLong(dndDB.getOrders().get(x).roomNumber) == Rooms.get(finalI).RoomNumber && dndDB.getOrders().get(x).dep.equals("DND")) {
                                    dndDB.removeRow(Long.parseLong(dndDB.getOrders().get(x).orderNumber));
                                }
                            }
                            //Log.d("DNDChanged" , Rooms.get(finalI).RoomNumber+" i am changed to " + dataSnapshot.getValue().toString()+" in list after "+dndDB.getOrders().size()) ;
                            ada = new DND_Adapter(dndDB.getOrders());
                            dnds.setAdapter(ada);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                SOSListiner[i] = Rooms.get(i).getFireRoom().child("SOS").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.getValue().toString().equals("0")) {
                            boolean status = false;
                            status = orderDB.searchOrder(Rooms.get(finalI).RoomNumber, "SOS");
                            long time = Long.parseLong(dataSnapshot.getValue().toString());
                            if (!status) {
                                orderDB.insertOrder(Rooms.get(finalI).RoomNumber, "SOS", "", time);
                                adapter = new CleanUp_Adapter(orderDB.getOrders(), act);
                                g.setAdapter(adapter);
                            }
                        } else {
                            for (int x = 0; x < orderDB.getOrders().size(); x++) {
                                if (Long.parseLong(orderDB.getOrders().get(x).roomNumber) == Rooms.get(finalI).RoomNumber && orderDB.getOrders().get(x).dep.equals("SOS")) {
                                    orderDB.removeRow(Long.parseLong(orderDB.getOrders().get(x).orderNumber));
                                    adapter = new CleanUp_Adapter(orderDB.getOrders(), act);
                                    g.setAdapter(adapter);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else if (DEP.equals("Laundry")) {
                LaundryListiner[i] = Rooms.get(i).getFireRoom().child("Laundry").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Long.parseLong(dataSnapshot.getValue().toString()) != 0) {
                            boolean status = false;
                            status = orderDB.searchOrder(Rooms.get(finalI).RoomNumber, "Laundry");
                            if (!status) {
                                long timee = Long.parseLong(dataSnapshot.getValue().toString());
                                orderDB.insertOrder(Rooms.get(finalI).RoomNumber, "Laundry", "", timee);
                                List<cleanOrder> list = orderDB.getOrders();
                                adapter = new CleanUp_Adapter(list, act);
                                g.setAdapter(adapter);
                            }

                        } else {
                            for (int x = 0; x < orderDB.getOrders().size(); x++) {
                                if (Long.parseLong(orderDB.getOrders().get(x).roomNumber) == Rooms.get(finalI).RoomNumber && orderDB.getOrders().get(x).dep.equals("Laundry")) {
                                    orderDB.removeRow(Long.parseLong(orderDB.getOrders().get(x).orderNumber));
                                    List<cleanOrder> list = orderDB.getOrders();
                                    adapter = new CleanUp_Adapter(list, act);
                                    g.setAdapter(adapter);
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                DNDListiner[i] = Rooms.get(i).getFireRoom().child("DND").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Long.parseLong(dataSnapshot.getValue().toString()) > 0) {
                            boolean status = false;
                            status = dndDB.searchOrder(Rooms.get(finalI).RoomNumber, "DND");
                            //Log.d("DNDChanged" , status+"") ;
                            if (!status) {

                                long time = Long.parseLong(dataSnapshot.getValue().toString());
                                dndDB.insertOrder(Rooms.get(finalI).RoomNumber, "DND", "", time);
                                //Log.d("DNDChanged" , Rooms.get(finalI).RoomNumber+" i am changed to " + dataSnapshot.getValue().toString()+" in list after "+dndDB.getOrders().size()) ;
                            }
                            ada = new DND_Adapter(dndDB.getOrders());
                            dnds.setAdapter(ada);
                        } else if (Long.parseLong(dataSnapshot.getValue().toString()) == 0) {
                            for (int x = 0; x < dndDB.getOrders().size(); x++) {
                                if (Long.parseLong(dndDB.getOrders().get(x).roomNumber) == Rooms.get(finalI).RoomNumber && dndDB.getOrders().get(x).dep.equals("DND")) {
                                    dndDB.removeRow(Long.parseLong(dndDB.getOrders().get(x).orderNumber));
                                }
                            }
                            //Log.d("DNDChanged" , Rooms.get(finalI).RoomNumber+" i am changed to " + dataSnapshot.getValue().toString()+" in list after "+dndDB.getOrders().size()) ;
                            ada = new DND_Adapter(dndDB.getOrders());
                            dnds.setAdapter(ada);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                SOSListiner[i] = Rooms.get(i).getFireRoom().child("SOS").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.getValue().toString().equals("0")) {
                            boolean status = false;
                            status = orderDB.searchOrder(Rooms.get(finalI).RoomNumber, "SOS");
                            long time = Long.parseLong(dataSnapshot.getValue().toString());

                            if (!status) {
                                orderDB.insertOrder(Rooms.get(finalI).RoomNumber, "SOS", "", time);
                                adapter = new CleanUp_Adapter(orderDB.getOrders(), act);
                                g.setAdapter(adapter);
                            }
                        } else {
                            for (int x = 0; x < orderDB.getOrders().size(); x++) {
                                if (Long.parseLong(orderDB.getOrders().get(x).roomNumber) == Rooms.get(finalI).RoomNumber && orderDB.getOrders().get(x).dep.equals("SOS")) {
                                    orderDB.removeRow(Long.parseLong(orderDB.getOrders().get(x).orderNumber));
                                    adapter = new CleanUp_Adapter(orderDB.getOrders(), act);
                                    g.setAdapter(adapter);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else if (DEP.equals("RoomService")) {
                RoomServiceListiner[i] = Rooms.get(i).getFireRoom().child("RoomService").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                        if (Long.parseLong(dataSnapshot1.getValue().toString()) != 0) {
                            boolean status = false;
                            long time = Long.parseLong(dataSnapshot1.getValue().toString());
                            status = orderDB.searchOrder(Rooms.get(finalI).RoomNumber, "RoomService");
                            if (!status) {
                                Rooms.get(finalI).getFireRoom().child("RoomServiceText").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                        if (dataSnapshot2.getValue() != null) {
                                            orderDB.insertOrder(Rooms.get(finalI).RoomNumber, "RoomService", dataSnapshot2.getValue().toString(), time);
                                            List<cleanOrder> list = orderDB.getOrders();
                                            adapter = new CleanUp_Adapter(list, act);
                                            g.setAdapter(adapter);
                                        } else {
                                            orderDB.insertOrder(Rooms.get(finalI).RoomNumber, "RoomService", "", time);
                                            List<cleanOrder> list = orderDB.getOrders();
                                            adapter = new CleanUp_Adapter(list, act);
                                            g.setAdapter(adapter);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        } else {
                            for (int x = 0; x < orderDB.getOrders().size(); x++) {
                                if (Integer.parseInt(orderDB.getOrders().get(x).roomNumber) == Rooms.get(finalI).RoomNumber && orderDB.getOrders().get(x).dep.equals("RoomService")) {
                                    orderDB.removeRow(Long.parseLong(orderDB.getOrders().get(x).orderNumber));
                                    List<cleanOrder> list = orderDB.getOrders();
                                    adapter = new CleanUp_Adapter(list, act);
                                    g.setAdapter(adapter);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                DNDListiner[i] = Rooms.get(i).getFireRoom().child("DND").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("DNDChanged", "_____________________");
                        Log.d("DNDChanged", Rooms.get(finalI).RoomNumber + " i am changed to " + dataSnapshot.getValue().toString() + " in list befor " + dndDB.getOrders().size());
                        if (Long.parseLong(dataSnapshot.getValue().toString()) > 0) {
                            boolean status = false;
                            status = dndDB.searchOrder(Rooms.get(finalI).RoomNumber, "DND");
                            Log.d("DNDChanged", status + "");
                            if (!status) {

                                long time = Long.parseLong(dataSnapshot.getValue().toString());
                                dndDB.insertOrder(Rooms.get(finalI).RoomNumber, "DND", "", time);
                                Log.d("DNDChanged", Rooms.get(finalI).RoomNumber + " i am changed to " + dataSnapshot.getValue().toString() + " in list after " + dndDB.getOrders().size());
                            }
                            ada = new DND_Adapter(dndDB.getOrders());
                            dnds.setAdapter(ada);
                        } else if (Long.parseLong(dataSnapshot.getValue().toString()) == 0) {
                            for (int x = 0; x < dndDB.getOrders().size(); x++) {
                                if (Long.parseLong(dndDB.getOrders().get(x).roomNumber) == Rooms.get(finalI).RoomNumber && dndDB.getOrders().get(x).dep.equals("DND")) {
                                    dndDB.removeRow(Long.parseLong(dndDB.getOrders().get(x).orderNumber));
                                }
                            }
                            Log.d("DNDChanged", Rooms.get(finalI).RoomNumber + " i am changed to " + dataSnapshot.getValue().toString() + " in list after " + dndDB.getOrders().size());
                            ada = new DND_Adapter(dndDB.getOrders());
                            dnds.setAdapter(ada);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                SOSListiner[i] = Rooms.get(i).getFireRoom().child("SOS").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.getValue().toString().equals("0")) {
                            boolean status = false;
                            status = orderDB.searchOrder(Rooms.get(finalI).RoomNumber, "SOS");
                            long time = Long.parseLong(dataSnapshot.getValue().toString());
                            if (!status) {
                                orderDB.insertOrder(Rooms.get(finalI).RoomNumber, "SOS", "", time);
                                List<cleanOrder> list = orderDB.getOrders();
                                adapter = new CleanUp_Adapter(list, act);
                                g.setAdapter(adapter);
                            }
                        } else {
                            for (int x = 0; x < orderDB.getOrders().size(); x++) {
                                if (Long.parseLong(orderDB.getOrders().get(x).roomNumber) == Rooms.get(finalI).RoomNumber && orderDB.getOrders().get(x).dep.equals("SOS")) {
                                    orderDB.removeRow(Long.parseLong(orderDB.getOrders().get(x).orderNumber));
                                    List<cleanOrder> list = orderDB.getOrders();
                                    adapter = new CleanUp_Adapter(list, act);
                                    g.setAdapter(adapter);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                MiniBarCheck[i] = Rooms.get(i).getFireRoom().child("MiniBarCheck").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            if (dataSnapshot.getValue().toString().equals("0")) {
                                for (int x = 0; x < orderDB.getOrders().size(); x++) {
                                    if (Long.parseLong(orderDB.getOrders().get(x).roomNumber) == Rooms.get(finalI).RoomNumber && orderDB.getOrders().get(x).dep.equals("MiniBarCheck")) {
                                        orderDB.removeRow(Long.parseLong(orderDB.getOrders().get(x).orderNumber));
                                        List<cleanOrder> list = orderDB.getOrders();
                                        adapter = new CleanUp_Adapter(list, act);
                                        g.setAdapter(adapter);
                                    }
                                }
                            } else {
                                boolean status = false;
                                status = orderDB.searchOrder(Rooms.get(finalI).RoomNumber, "MiniBarCheck");
                                if (!status) {
                                    long timee = Long.parseLong(dataSnapshot.getValue().toString());
                                    orderDB.insertOrder(Rooms.get(finalI).RoomNumber, "MiniBarCheck", "", timee);
                                    List<cleanOrder> list = orderDB.getOrders();
                                    adapter = new CleanUp_Adapter(list, act);
                                    g.setAdapter(adapter);
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else if (DEP.equals("Service")) {
                DNDListiner[i] = Rooms.get(i).getFireRoom().child("DND").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            if (Long.parseLong(dataSnapshot.getValue().toString()) > 0) {
                                boolean status = false;
                                status = dndDB.searchOrder(Rooms.get(finalI).RoomNumber, "DND");
                                Log.d("DNDChanged", status + "");
                                if (!status) {
                                    long time = Long.parseLong(dataSnapshot.getValue().toString());
                                    dndDB.insertOrder(Rooms.get(finalI).RoomNumber, "DND", "", time);
                                    Log.d("DNDChanged", Rooms.get(finalI).RoomNumber + " i am changed to " + dataSnapshot.getValue().toString() + " in list after " + dndDB.getOrders().size());
                                }
                                ada = new DND_Adapter(dndDB.getOrders());
                                dnds.setAdapter(ada);
                            } else if (Long.parseLong(dataSnapshot.getValue().toString()) == 0) {
                                for (int x = 0; x < dndDB.getOrders().size(); x++) {
                                    if (Long.parseLong(dndDB.getOrders().get(x).roomNumber) == Rooms.get(finalI).RoomNumber && dndDB.getOrders().get(x).dep.equals("DND")) {
                                        dndDB.removeRow(Long.parseLong(dndDB.getOrders().get(x).orderNumber));
                                    }
                                }
                                Log.d("DNDChanged", Rooms.get(finalI).RoomNumber + " i am changed to " + dataSnapshot.getValue().toString() + " in list after " + dndDB.getOrders().size());
                                ada = new DND_Adapter(dndDB.getOrders());
                                dnds.setAdapter(ada);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                CleanupListiner[i] = Rooms.get(i).getFireRoom().child("Cleanup").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (int i = 0; i < Rooms.size(); i++) {
                            Log.d("roomsSort", Rooms.get(i).RoomNumber + "");
                        }
                        if (dataSnapshot.getValue() != null) {
                            if (Long.parseLong(dataSnapshot.getValue().toString()) != 0) {
                                boolean status = false;
                                status = orderDB.searchOrder(Rooms.get(finalI).RoomNumber, "Cleanup");
                                if (!status) {
                                    long timee = Long.parseLong(dataSnapshot.getValue().toString());
                                    orderDB.insertOrder(Rooms.get(finalI).RoomNumber, "Cleanup", "", timee);
                                    List<cleanOrder> list = orderDB.getOrders();
                                    adapter = new CleanUp_Adapter(list, act);
                                    g.setAdapter(adapter);
                                }
                            } else {
                                for (int x = 0; x < orderDB.getOrders().size(); x++) {
                                    if (Long.parseLong(orderDB.getOrders().get(x).roomNumber) == Rooms.get(finalI).RoomNumber && orderDB.getOrders().get(x).dep.equals("Cleanup")) {
                                        orderDB.removeRow(Long.parseLong(orderDB.getOrders().get(x).orderNumber));
                                        List<cleanOrder> list = orderDB.getOrders();
                                        adapter = new CleanUp_Adapter(list, act);
                                        g.setAdapter(adapter);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                LaundryListiner[i] = Rooms.get(i).getFireRoom().child("Laundry").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Log.d("bolleantest", "LAUNDRY " + finalI + "");
                            if (Long.parseLong(dataSnapshot.getValue().toString()) != 0) {
                                boolean status = false;
                                status = orderDB.searchOrder(Rooms.get(finalI).RoomNumber, "Laundry");
                                if (!status) {
                                    long timee = Long.parseLong(dataSnapshot.getValue().toString());
                                    orderDB.insertOrder(Rooms.get(finalI).RoomNumber, "Laundry", "", timee);
                                    List<cleanOrder> list = orderDB.getOrders();
                                    adapter = new CleanUp_Adapter(list, act);
                                    g.setAdapter(adapter);
                                }

                            } else {
                                for (int x = 0; x < orderDB.getOrders().size(); x++) {
                                    if (Long.parseLong(orderDB.getOrders().get(x).roomNumber) == Rooms.get(finalI).RoomNumber && orderDB.getOrders().get(x).dep.equals("Laundry")) {
                                        orderDB.removeRow(Long.parseLong(orderDB.getOrders().get(x).orderNumber));
                                        List<cleanOrder> list = orderDB.getOrders();
                                        adapter = new CleanUp_Adapter(list, act);
                                        g.setAdapter(adapter);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                RoomServiceListiner[i] = Rooms.get(i).getFireRoom().child("RoomService").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                        Rooms.get(finalI).getFireRoom().child("RoomServiceText").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot1.getValue() != null) {
                                    if (Long.parseLong(dataSnapshot1.getValue().toString()) != 0) {
                                        boolean status = false;
                                        long time = Long.parseLong(dataSnapshot1.getValue().toString());
                                        status = orderDB.searchOrder(Rooms.get(finalI).RoomNumber, "RoomService");
                                        if (!status) {
                                            if (dataSnapshot.getValue() != null) {
                                                list.add(new cleanOrder(String.valueOf(Rooms.get(finalI).RoomNumber), String.valueOf(1), "RoomService", dataSnapshot.getValue().toString(), time));
                                                adapter.notifyDataSetChanged();
                                                orderDB.insertOrder(Rooms.get(finalI).RoomNumber, "RoomService", dataSnapshot.getValue().toString(), time);
                                                List<cleanOrder> list = orderDB.getOrders();
                                                adapter = new CleanUp_Adapter(list, act);
                                                g.setAdapter(adapter);
                                            } else {
                                                list.add(new cleanOrder(String.valueOf(Rooms.get(finalI).RoomNumber), String.valueOf(1), "RoomService", "", time));
                                                adapter.notifyDataSetChanged();
                                                orderDB.insertOrder(Rooms.get(finalI).RoomNumber, "RoomService", "", time);
                                                List<cleanOrder> list = orderDB.getOrders();
                                                adapter = new CleanUp_Adapter(list, act);
                                                g.setAdapter(adapter);
                                            }
                                        }
                                    } else {
                                        for (int x = 0; x < orderDB.getOrders().size(); x++) {
                                            if (Long.parseLong(orderDB.getOrders().get(x).roomNumber) == Rooms.get(finalI).RoomNumber && orderDB.getOrders().get(x).dep.equals("RoomService")) {
                                                orderDB.removeRow(Long.parseLong(orderDB.getOrders().get(x).orderNumber));
                                                List<cleanOrder> list = orderDB.getOrders();
                                                adapter = new CleanUp_Adapter(list, act);
                                                g.setAdapter(adapter);
                                            }
                                        }
                                    }
                                }
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
                SOSListiner[i] = Rooms.get(i).getFireRoom().child("SOS").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            if (!dataSnapshot.getValue().toString().equals("0")) {
                                boolean status = false;
                                status = orderDB.searchOrder(Rooms.get(finalI).RoomNumber, "SOS");
                                long time = Long.parseLong(dataSnapshot.getValue().toString());
                                if (!status) {
                                    orderDB.insertOrder(Rooms.get(finalI).RoomNumber, "SOS", "", time);
                                    List<cleanOrder> list = orderDB.getOrders();
                                    adapter = new CleanUp_Adapter(list, act);
                                    g.setAdapter(adapter);
                                }
                            } else {
                                for (int x = 0; x < orderDB.getOrders().size(); x++) {
                                    if (Long.parseLong(orderDB.getOrders().get(x).roomNumber) == Rooms.get(finalI).RoomNumber && orderDB.getOrders().get(x).dep.equals("SOS")) {
                                        orderDB.removeRow(Long.parseLong(orderDB.getOrders().get(x).orderNumber));
                                        List<cleanOrder> list = orderDB.getOrders();
                                        adapter = new CleanUp_Adapter(list, act);
                                        g.setAdapter(adapter);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                MiniBarCheck[i] = Rooms.get(i).getFireRoom().child("MiniBarCheck").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            if (dataSnapshot.getValue().toString().equals("0")) {
                                for (int x = 0; x < orderDB.getOrders().size(); x++) {
                                    if (Long.parseLong(orderDB.getOrders().get(x).roomNumber) == Rooms.get(finalI).RoomNumber && orderDB.getOrders().get(x).dep.equals("MiniBarCheck")) {
                                        orderDB.removeRow(Long.parseLong(orderDB.getOrders().get(x).orderNumber));
                                        List<cleanOrder> list = orderDB.getOrders();
                                        adapter = new CleanUp_Adapter(list, act);
                                        g.setAdapter(adapter);
                                    }
                                }
                            } else {
                                boolean status = false;
                                status = orderDB.searchOrder(Rooms.get(finalI).RoomNumber, "MiniBarCheck");
                                if (!status) {
                                    long timee = Long.parseLong(dataSnapshot.getValue().toString());
                                    orderDB.insertOrder(Rooms.get(finalI).RoomNumber, "MiniBarCheck", "", timee);
                                    List<cleanOrder> list = orderDB.getOrders();
                                    adapter = new CleanUp_Adapter(list, act);
                                    g.setAdapter(adapter);
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            Rooms.get(i).getFireRoom().child("roomStatus").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Rooms.get(finalI).roomStatus = Integer.parseInt(dataSnapshot.getValue().toString());
                        adapter.notifyDataSetChanged();
                        if (ROOMS.adapter != null) {
                            ROOMS.adapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public static void removeOldListiners() {
        for (int i = 0; i < Rooms.size(); i++) {
            if (DEP.equals("Cleanup")) {
                Rooms.get(i).getFireRoom().child("Cleanup").removeEventListener(CleanupListiner[i]);
                Rooms.get(i).getFireRoom().child("DND").removeEventListener(DNDListiner[i]);
            } else if (DEP.equals("Laundry")) {
                Rooms.get(i).getFireRoom().child("Laundry").removeEventListener(LaundryListiner[i]);
                Rooms.get(i).getFireRoom().child("DND").removeEventListener(DNDListiner[i]);
            } else if (DEP.equals("RoomService")) {
                Rooms.get(i).getFireRoom().child("RoomService").removeEventListener(RoomServiceListiner[i]);
                Rooms.get(i).getFireRoom().child("DND").removeEventListener(DNDListiner[i]);
                Rooms.get(i).getFireRoom().child("SOS").removeEventListener(SOSListiner[i]);
                Rooms.get(i).getFireRoom().child("MiniBarCheck").removeEventListener(MiniBarCheck[i]);
            } else if (DEP.equals("Service")) {
                Rooms.get(i).getFireRoom().child("Cleanup").removeEventListener(CleanupListiner[i]);
                Rooms.get(i).getFireRoom().child("Laundry").removeEventListener(LaundryListiner[i]);
                Rooms.get(i).getFireRoom().child("DND").removeEventListener(DNDListiner[i]);
                Rooms.get(i).getFireRoom().child("RoomService").removeEventListener(RoomServiceListiner[i]);
                Rooms.get(i).getFireRoom().child("SOS").removeEventListener(SOSListiner[i]);
                Rooms.get(i).getFireRoom().child("MiniBarCheck").removeEventListener(MiniBarCheck[i]);
            }
        }
    }

    public static void auth(String LU, String LP) {
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        String account = LU.trim();
        password = LP.trim();
        password = DigitUtil.getMD5(password);
        Call<String> call = apiService.auth(ApiService.CLIENT_ID, ApiService.CLIENT_SECRET, "password", account, password, ApiService.REDIRECT_URI);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                String json = response.body();
                accountInfo = GsonUtil.toObject(json, AccountInfo.class);
                if (accountInfo != null) {
                    if (accountInfo.errcode == 0) {
                        accountInfo.setMd5Pwd(password);
                        acc = accountInfo;
                        lockList();
                        Log.d("ttlockLogin", LU + " " + LP);
                        Log.d("ttlockLogin", response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    public static void lockList() {
        if (lockObjs != null && lockObjs.size() != 0) {
            for (LockObj o : lockObjs) {
                Log.d("ttlockLogin", o.getLockName());
                for (ROOM r : Rooms) {
                    if (o.getLockAlias().equals(r.RoomNumber + "Lock")) {
                        r.setLOCK(o);
                    }
                }
            }
        } else {
            ApiService apiService = RetrofitAPIManager.provideClientApi();
            Call<String> call = apiService.getLockList(ApiService.CLIENT_ID, acc.getAccess_token(), 1, 100, System.currentTimeMillis());
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    String json = response.body();
                    if (json.contains("list")) {
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            JSONArray array = jsonObject.getJSONArray("list");
                            lockObjs = GsonUtil.toObject(array.toString(), new TypeToken<ArrayList<LockObj>>() {
                            });
                            for (LockObj o : lockObjs) {
                                for (ROOM r : Rooms) {
                                    if (o.getLockAlias().equals(r.RoomNumber + "Lock")) {
                                        r.setLOCK(o);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                        }
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                }
            });
        }
    }

    public static void goLogIn(String TU, String TP) {
//        final LoadingDialog d = new LoadingDialog(act);
        TuyaHomeSdk.getUserInstance().loginWithEmail("966", TU, TP, new ILoginCallback() {
            @Override
            public void onSuccess(com.tuya.smart.android.user.bean.User user) {
                getTuyaDevices();
            }

            @Override
            public void onError(String code, String error) {
//                d.close();
            }
        });
    }

    static void getTuyaDevices() {
        if (Devices != null && Devices.size() != 0) {
            for (DeviceBean d : Devices) {
                Log.d("tuyaHome", d.name);
                for (ROOM r : Rooms) {
                    if (d.getName().equals(r.RoomNumber + "Power")) {
                        r.setPOWER(d);
                        r.setPower(TuyaHomeSdk.newDeviceInstance(r.getPOWER().devId));
                    }
                    if (d.getName().equals(r.RoomNumber + "Lock")) {
                        r.setLOCK_T(d);
                        r.setLock_T(TuyaHomeSdk.newDeviceInstance(r.getLOCK_T().devId));
                    }
                }
            }
        } else {
            LoadingDialog d = new LoadingDialog(act);
            TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
                @Override
                public void onError(String errorCode, String error) {
                    d.close();
                    getTuyaDevices();
                }

                @Override
                public void onSuccess(List<HomeBean> homeBeans) {
                    // do something
                    Homs = homeBeans;
                    for (HomeBean h : Homs) {
                        if (MyApp.Project.contains(h.getName())) {
                            THE_HOME = h;
                        }
                    }
                    if (THE_HOME == null) {
                        d.close();
                        Log.d("tuyaHome", "No Home");
                        new messageDialog("no hotel found","error",act);
                    } else {
                        Log.d("tuyaHome", THE_HOME.getName());
                        TuyaHomeSdk.newHomeInstance(THE_HOME.getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
                            @Override
                            public void onSuccess(HomeBean homeBean) {
                                d.close();
                                List<DeviceBean> lis = new ArrayList<DeviceBean>();
                                lis = homeBean.getDeviceList();
                                if (lis.size() == 0) {
                                    Log.d("tuyaHome", lis.size() + "");
                                } else {
                                    for (DeviceBean d : lis) {
                                        Log.d("tuyaHome", d.name);
                                        for (ROOM r : Rooms) {
                                            if (d.getName().equals(r.RoomNumber + "Power")) {
                                                r.setPOWER(d);
                                                r.setPower(TuyaHomeSdk.newDeviceInstance(r.getPOWER().devId));
                                            }
                                            if (d.getName().equals(r.RoomNumber + "Lock")) {
                                                Log.d("tuyaHome", "lock selected");
                                                r.setLOCK_T(d);
                                                r.setLock_T(TuyaHomeSdk.newDeviceInstance(r.getLOCK_T().devId));
                                            }
                                        }
                                    }
                                    Devices = lis;
                                }
                            }

                            @Override
                            public void onError(String errorCode, String errorMsg) {
                                d.close();
                                getTuyaDevices();
                            }
                        });
                    }
                }
            });
        }
    }

    void changePasswordDialog() {
        Dialog D = new Dialog(act);
        D.setContentView(R.layout.change_password_dialog);
        Window window = D.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        EditText oldPassword = D.findViewById(R.id.changePassword_oldPassword);
        EditText newPassword = D.findViewById(R.id.changePassword_NewPassword);
        EditText Conferm = D.findViewById(R.id.changePassword_ConNewPassword);
        Button cancel = D.findViewById(R.id.changePassword_cancel);
        Button send = D.findViewById(R.id.changePassword_send);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                D.dismiss();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = MyApp.URL + "users/updatePassword";
                if (oldPassword.getText() == null || oldPassword.getText().toString().isEmpty()) {
                    Toast.makeText(act, "enter old password", Toast.LENGTH_SHORT).show();
                    oldPassword.setHint("old password");
                    oldPassword.setHintTextColor(Color.RED);
                    return;
                }
                if (newPassword.getText() == null || newPassword.getText().toString().isEmpty()) {
                    Toast.makeText(act, "enter new password", Toast.LENGTH_SHORT).show();
                    newPassword.setHint("new password");
                    newPassword.setHintTextColor(Color.RED);
                    return;
                }
                if (Conferm.getText() == null || Conferm.getText().toString().isEmpty()) {
                    Toast.makeText(act, "enter password confirmation", Toast.LENGTH_SHORT).show();
                    Conferm.setHint("password confirm");
                    Conferm.setHintTextColor(Color.RED);
                    return;
                }
                LoadingDialog l = new LoadingDialog(act);
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("passwordResp", response);
                        l.close();
                        try {
                            JSONObject result = new JSONObject(response);
                            if (result.getString("result").equals("success")) {
                                new messageDialog("updated","updated",act);
                            }
                            else {
                                new messageDialog(result.getString("error"),"error",act);
                            }
                        } catch (JSONException e) {
                            new messageDialog(e.getMessage(),"error",act);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("passwordResp", error.toString());
                        l.close();
                        new messageDialog(error.toString(),"error",act);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> par = new HashMap<String, String>();
                        par.put("old_password", oldPassword.getText().toString());
                        par.put("new_password", newPassword.getText().toString());
                        par.put("conf_password", newPassword.getText().toString());
                        par.put("job_number", String.valueOf(MyApp.My_USER.jobNumber));
                        par.put("my_token", MyApp.Token);
                        return par;
                    }
                };
                Volley.newRequestQueue(act).add(request);
            }
        });
        D.show();
    }

    void deleteToken(String id,VolleyCallback callback) {
//        StringRequest request = new StringRequest(Request.Method.POST, deleteUserToken, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                callback.onSuccess(response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                callback.onFailed(error.toString());
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> Parms = new HashMap<String, String>();
//                Parms.put("id", String.valueOf(LogIn.db.getUser().id));
//                Log.d("checktoken", "tmam");
//                return Parms;
//            }
//        };
//        Volley.newRequestQueue(act).add(request);
        String url = MyApp.URL + "users/modifyUserFirebaseToken";
        StringRequest r = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TokenResp", response);
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TokenResp", error.toString());
                callback.onFailed(error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "0");
                params.put("id", id);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(r);
    }

    static void filterOrderByRoomNumber(List<cleanOrder> orders, List<ROOM> rooms) {
        for (int j = 0; j < orders.size(); j++) {
            boolean state = false;
            for (int i = 0; i < rooms.size(); i++) {
                if (Integer.parseInt(orders.get(j).roomNumber) == rooms.get(i).RoomNumber) {
                    state = true;
                    break;
                }
            }
            if (!state) {
                orderDB.removeRow(Long.parseLong(orders.get(j).orderNumber));
            }
        }
        List<cleanOrder> list = orderDB.getOrders();
        adapter = new CleanUp_Adapter(list, act);
        g.setAdapter(adapter);
    }

    static void filterDNDOrderByRoomNumber(List<cleanOrder> orders, List<ROOM> rooms) {
        Log.d("dndOrders", orders.size() + "");
        for (int j = 0; j < orders.size(); j++) {
            boolean state = false;
            for (int i = 0; i < rooms.size(); i++) {
                if (Integer.parseInt(orders.get(j).roomNumber) == rooms.get(i).RoomNumber) {
                    state = true;
                    break;
                }
            }
            Log.d("dndOrders", orders.size() + " " + j);
            if (!state) {
                dndDB.removeRow(Long.parseLong(orders.get(j).orderNumber));
            }
        }
        ada = new DND_Adapter(dndDB.getOrders());
        dnds.setAdapter(ada);
    }

    static void filterOrdersByDepartment(List<cleanOrder> orders) {
        boolean state = false;
        if (DEP.equals("Cleanup")) {
            for (int j = 0; j < orders.size(); j++) {
                if (orders.get(j).dep.equals("Laundry") || orders.get(j).dep.equals("RoomService")) {
                    state = true;
                    break;
                }
                if (!state) {
                    orderDB.removeRow(Long.parseLong(orders.get(j).orderNumber));
                }
            }
        } else if (DEP.equals("Laundry")) {
            for (int j = 0; j < orders.size(); j++) {
                if (orders.get(j).dep.equals("Cleanup") || orders.get(j).dep.equals("RoomService")) {
                    state = true;
                    break;
                }
                if (!state) {
                    orderDB.removeRow(Long.parseLong(orders.get(j).orderNumber));
                }
            }
        } else if (DEP.equals("RoomService")) {
            for (int j = 0; j < orders.size(); j++) {
                if (orders.get(j).dep.equals("Cleanup") || orders.get(j).dep.equals("Laundry")) {
                    state = true;
                    break;
                }
                if (!state) {
                    orderDB.removeRow(Long.parseLong(orders.get(j).orderNumber));
                }
            }
        }
        List<cleanOrder> list = orderDB.getOrders();
        adapter = new CleanUp_Adapter(list, act);
        g.setAdapter(adapter);
    }

    static void saveMyRoomsInSharedPreferences(List<ROOM> rooms) {
        String theRooms = "";
        for (int i = 0; i < rooms.size(); i++) {
            if (i + 1 == rooms.size()) {
                theRooms = theRooms + rooms.get(i).RoomNumber;
            } else {
                theRooms = theRooms + rooms.get(i).RoomNumber + "-";
            }
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_MyRooms, theRooms);
        editor.apply();
    }

    void getProjectVariables() {
        LoadingDialog d = new LoadingDialog(act);
        String url = MyApp.URL + "roomsManagement/getProjectVariables" ;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                d.close();
                try {
                    JSONObject row = new JSONObject(response);
                    JSONObject ServiceSwitchButtons = new JSONObject(row.getString("ServiceSwitchButtons"));
                    MyApp.ProjectVariables = new ProjectsVariablesClass(row.getInt("id"),row.getString("projectName"),row.getInt("Hotel"),row.getInt("Temp"),row.getInt("Interval"),row.getInt("DoorWarning"),row.getInt("CheckinModeActive"),row.getInt("CheckInModeTime"),row.getString("CheckinActions"),row.getInt("CheckoutModeActive"),row.getInt("CheckOutModeTime"),row.getString("CheckoutActions"),row.getString("WelcomeMessage"),row.getString("Logo"),row.getInt("PoweroffClientIn"),row.getInt("PoweroffAfterHK"),row.getInt("ACSenarioActive"),row.getString("OnClientBack"),row.getInt("HKCleanupTime"));
                    MyApp.ProjectVariables.setServiceSwitchButtons(ServiceSwitchButtons);
                    Log.d("Respoonssss",MyApp.ProjectVariables.projectName+ " "+MyApp.ProjectVariables.HKCleanTime);
                }
                catch (JSONException e) {
                    Log.d("Respoonssss",e.getMessage());
                    new messageDialog("error getting project variables "+e,"error",act);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                d.close();
                new messageDialog("error getting project variables "+error,"error",act);
            }
        });
        Volley.newRequestQueue(act).add(request);
    }
}
