package com.syriasoft.cleanup;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MiniBarCheck extends AppCompatActivity {
    private Activity act = this;
    private List<MINIBARITEM> list = new ArrayList<MINIBARITEM>();
    private String getMiniBarItemsUrl = LogIn.URL + "getMiniBarMenu.php";
    private String getTheFacilityId = LogIn.URL + "getTheMiniBarFacility.php";
    private String insertMinibarOrder = LogIn.URL + "insertMinibarOrder.php";
    private String insertMinibarOrderItems = LogIn.URL + "insertMinibarOrderItems.php";
    private RecyclerView MinibarItemsRecycler;
    private RecyclerView.LayoutManager manager;
    private int Facility;
    private int room;
    private int OrderId;
    private ROOM Room;
    private DatabaseReference FireRoom;
    private double Total = 0;
    static List<MINIBARITEM_ORDER> Used = new ArrayList<MINIBARITEM_ORDER>();
    int Reservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mini_bar_check);
        Bundle b = getIntent().getExtras();
        final LoadingDialog d = new LoadingDialog(act);
        room = Integer.parseInt(b.getString("Room"));
        OrderId = Integer.parseInt(b.getString("OrderId"));
        for (int i = 0; i < MainActivity.Rooms.size(); i++) {
            if (MainActivity.Rooms.get(i).RoomNumber == room) {
                Room = MainActivity.Rooms.get(i);
                FireRoom = MainActivity.Rooms.get(i).getFireRoom();
            }
        }
        FireRoom.child("ReservationNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                d.close();
                if (dataSnapshot.getValue() != null) {
                    Reservation = Integer.parseInt(dataSnapshot.getValue().toString());
                } else {
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d("ROOM", "room " + Room.RoomNumber + " OrderId " + OrderId + " reservation " + Room.ReservationNumber);
        Log.d("ROOM", b.toString());
        manager = new LinearLayoutManager(act, LinearLayoutManager.VERTICAL, false);
        MinibarItemsRecycler = (RecyclerView) findViewById(R.id.minibarItems_recycler);
        MinibarItemsRecycler.setLayoutManager(manager);
        getMinibarFacility();
    }

    void getMiniBarItems(final int Facility) {
        final LoadingDialog d = new LoadingDialog(act);
        StringRequest request = new StringRequest(Request.Method.POST, getMiniBarItemsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                d.close();
                if (response.equals("0")) {
                } else {
                    try {
                        JSONArray arr = new JSONArray(response);
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject row = arr.getJSONObject(i);
                            list.add(new MINIBARITEM(row.getInt("id"), row.getInt("Hotel"), row.getInt("Facility"), row.getString("Name"), row.getDouble("Price"), row.getString("photo")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MinibarItems_Adapter adapter = new MinibarItems_Adapter(list, act);
                    MinibarItemsRecycler.setAdapter(adapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                d.close();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> par = new HashMap<String, String>();
                par.put("Hotel", String.valueOf(1));
                par.put("Facility", String.valueOf(Facility));
                return par;
            }
        };
        Volley.newRequestQueue(act).add(request);
    }

    void getMinibarFacility() {
        final LoadingDialog d = new LoadingDialog(act);
        StringRequest request = new StringRequest(Request.Method.POST, getTheFacilityId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                d.close();
                try {
                    JSONArray arr = new JSONArray(response);
                    JSONObject row = arr.getJSONObject(0);
                    Facility = row.getInt("id");
                    getMiniBarItems(Facility);
                } catch (JSONException e) {
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                d.close();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> par = new HashMap<String, String>();
                par.put("Hotel", String.valueOf(1));
                return par;
            }
        };
        Volley.newRequestQueue(act).add(request);
    }

    public void sendMinibarOrder(View view) {
        if (Used.size() > 0) {
            for (int i = 0; i < Used.size(); i++) {
                Total += Used.get(i).Total;
            }
        }
        final LoadingDialog d = new LoadingDialog(act);
        StringRequest re = new StringRequest(Request.Method.POST, insertMinibarOrder, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (Integer.parseInt(response) > 0) {
                    String orderNumber = response;
                    StringRequest req = new StringRequest(Request.Method.POST, insertMinibarOrderItems, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            d.close();
                            if (response.equals("1")) {
                                Toast.makeText(act, "Order Sent ", Toast.LENGTH_LONG).show();
                                FireRoom.child("MiniBarCheck").setValue(0);
                                act.finish();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            d.close();
                            Toast.makeText(act, error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Room", String.valueOf(Room.RoomNumber));
                            params.put("OrderId", String.valueOf(OrderId));
                            params.put("Reservation", String.valueOf(Room.ReservationNumber));
                            params.put("count", String.valueOf(Used.size()));

                            for (int i = 0; i < Used.size(); i++) {
                                params.put("ItemId" + i, String.valueOf(Used.get(i).id));
                                params.put("Name" + i, Used.get(i).Name);
                                params.put("Quantity" + i, String.valueOf(Used.get(i).Quantity));
                                params.put("Price" + i, String.valueOf(Used.get(i).price));
                                params.put("Total" + i, String.valueOf(Used.get(i).price * Used.get(i).Quantity));
                                params.put("photo" + i, Used.get(i).photo);
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
                d.close();
                Toast.makeText(act, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Room", String.valueOf(Room.RoomNumber));
                Calendar x = Calendar.getInstance(Locale.getDefault());
                double time = x.getTimeInMillis();
                params.put("Time", String.valueOf(time));
                params.put("Total", String.valueOf(Total));
                params.put("RoomId", String.valueOf(Room.id));
                params.put("Reservation", String.valueOf(Room.ReservationNumber));
                params.put("Emp", String.valueOf(LogIn.db.getUser().jobNumber));
                params.put("OrderId", String.valueOf(OrderId));
                return params;
            }
        };
        if (Room.ReservationNumber > 0) {
            Volley.newRequestQueue(act).add(re);
        } else {
            messageDialog m = new messageDialog("Couldn't Get Reservation Number ", "Error Reservation Number", act);
        }
    }
}