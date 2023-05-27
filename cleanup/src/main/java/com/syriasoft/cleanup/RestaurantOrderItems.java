package com.syriasoft.cleanup;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class RestaurantOrderItems extends AppCompatActivity {

    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAQmygXvw:APA91bFt5CiONiZPDDj4_kz9hmKXlL1cjfTa_ZNGfobMPmt0gamhzEoN2NHiOxypCDr_r5yfpLvJy-bQSgrykXvaqKkThAniTr-0hpXPBrXm7qWThMmkiaN9o6qaUqfIUwStMMuNedTw";
    final private String contentType = "application/json";
    static List<OrderItem> list = new ArrayList<OrderItem>();
    static Activity act;
    static String itemsUrl = LogIn.URL + "getRestaurantOrderItems.php";
    String makeOrderDone = LogIn.URL + "makeRestaurantOrderDone.php";
    static ListView items;
    private static GridView ITEMS;
    Order_Items_Adapter adapter;
    static int orderNumber;
    int RoomId;
    private double TOTAL;
    private long DATETIME;
    private int RESERVATION;
    private int RORS;
    private TextView mainText,RorS, Total, DateTime, Reservation, FacilityName;
    private ImageView FacilityImage;
    private int room;
    static restaurant_order_unit ORDER ;
    static RequestQueue Q ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_order_items);
        setActivity();
        for (int i = 0; i < RestaurantOrders.Rooms.size(); i++) {
            if (RestaurantOrders.Rooms.get(i).RoomNumber == room) {
                RoomId = i;
            }
        }
        getorderItems();
    }

    void setActivity() {
        act = this;
        Q = Volley.newRequestQueue(act);
        FacilityName = findViewById(R.id.facility_Name2);
        FacilityName.setText(RestaurantOrders.THEFACILITY.Name);
        FacilityImage = findViewById(R.id.facility_image2);
        mainText = findViewById(R.id.mainText3);
        ORDER = RestaurantOrders.SELECTED_ORDER;
        Total = findViewById(R.id.orderTotal);
        DateTime = findViewById(R.id.dateTime);
        Reservation = findViewById(R.id.textView12);
        items = findViewById(R.id.orderItems);
        ITEMS = findViewById(R.id.itemsGrid);
        RorS = findViewById(R.id.roomOrSuite);
        Bundle b = getIntent().getExtras();
        room = b.getInt("room");
        mainText.setText("ORDER " + ORDER.id + " Room " + room);
        Picasso.get().load(RestaurantOrders.THEFACILITY.photo).into(FacilityImage);
        Total.setText(String.valueOf(ORDER.total));
        DateTime.setText(convertMillisToTime(ORDER.dateTime));
        Reservation.setText(String.valueOf(ORDER.Reservation));
        RORS = ORDER.RorS;
        if (RORS == 1) {
            RorS.setText("ROOM");
        } else {
            RorS.setText("SUITE");
        }
    }

    public static void getorderItems() {
        list.clear();
        LoadingDialog loadingDialog = new LoadingDialog(act);
        String url = MyApp.URL + "facilitys/getRestOrderItems";
        StringRequest re = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("itemsResp",response);
                loadingDialog.close();
                list.clear();
                try {
                    JSONObject result = new JSONObject(response);
                    if (result.getString("result").equals("success")) {
                        JSONArray arr = new JSONArray(result.getString("items"));
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject row = arr.getJSONObject(i);
                            int id = row.getInt("id");
                            int orderNumber = row.getInt("restaurantorder_id");
                            int room = row.getInt("room");
                            int itemNo = row.getInt("itemNo");
                            String name = row.getString("name");
                            int quantity = row.getInt("quantity");
                            double price = row.getLong("price");
                            double total = row.getDouble("total");
                            String descr = row.getString("desc");
                            String notes = row.getString("notes");
                            OrderItem item = new OrderItem(id, orderNumber, room, itemNo, name, descr, quantity, price, total, notes);
                            list.add(item);
                        }
                        Order_Items_Adapter adapter = new Order_Items_Adapter(list, act);
                        ITEMS.setAdapter(adapter);
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
                Log.d("itemsResp",error.toString());
                loadingDialog.close();
                new messageDialog(error.toString(),"error",act);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("order_id", String.valueOf(ORDER.id));
                return params;
            }
        };
        Q.add(re);
    }

    public void maeOrderDone(View view) {
        LoadingDialog loadingDialog = new LoadingDialog(act);
        String url = MyApp.URL + "facilitys/finishRestOrder";
        StringRequest request = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("doneResp",response);
                loadingDialog.close();
                try {
                    JSONObject result = new JSONObject(response);
                    if (result.getString("result").equals("success")) {
                        RestaurantOrders.SELECTED_ROOM.getFireRoom().child("Restaurant").setValue(0);
                        RestaurantOrders.SELECTED_ROOM.getFireRoom().child("Facility").setValue(0);
                        act.finish();
                    }
                    else {
                        new messageDialog(result.getString("error"),"error",act);
                    }
                } catch (JSONException e) {
                    Log.d("doneResp",e.getMessage());
                    new messageDialog(e.getMessage(),"error",act);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("doneResp",error.toString());
                loadingDialog.close();
                new messageDialog(error.toString(),"error",act);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("order_id", String.valueOf(ORDER.id));
                return params;
            }
        };

        Q.add(request);
    }

    //-------------------------------------------------------------------------
    //Send Cloud Message

    @Override
    public void onResume() {
        super.onResume();
        // put your code here...
        getorderItems();
    }

    String convertMillisToTime(long millies) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millies);
        c.setTimeZone(TimeZone.getDefault());
        int month = c.get(Calendar.MONTH) + 1;
        String g = c.getTime().toString();
        String Time = c.get(Calendar.DAY_OF_MONTH) + "/" + month + "/" + c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
        return g;
    }
}
