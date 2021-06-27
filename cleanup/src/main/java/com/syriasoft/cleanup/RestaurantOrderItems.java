package com.syriasoft.cleanup;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
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
    static Activity act ;
    static String itemsUrl = LogIn.URL+"getRestaurantOrderItems.php";
    String makeOrderDone = LogIn.URL+"makeRestaurantOrderDone.php";
    static ListView items ;
    private static GridView ITEMS ;
    Order_Items_Adapter adapter ;
    static int orderNumber ;
    int RoomId ;
    private double TOTAL ;
    private long DATETIME ;
    private int RESERVATION ;
    private int RORS ;
    private TextView RorS , Total , DateTime , Reservation , FacilityName;
    private ImageView FacilityImage ;
    private int room ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_order_items);
        act = this ;
        Bundle b = getIntent().getExtras();
        int id = b.getInt("id");
        room = b.getInt("room") ;
        //RoomId = RestaurantOrders.RoomId ;
        //
        TOTAL = b.getDouble("total");
        DATETIME=b.getLong("dateTime");
        RESERVATION = b.getInt("Reservation");
        RORS = b.getInt("RorS");
        FacilityName = (TextView)findViewById(R.id.facility_Name2);
        FacilityName.setText(RestaurantOrders.THEFACILITY.Name);
        FacilityImage = (ImageView)findViewById(R.id.facility_image2);
        Picasso.get().load(RestaurantOrders.THEFACILITY.photo).into(FacilityImage);
        //restaurant_order_unit re = new restaurant_order_unit(b.getInt("id"),b.getInt("Hotel"),b.getInt("Facility"),b.getInt("Reservation"),b.getInt("room"),b.getInt("RorS"),b.getInt("roomId"),b.getLong("dateTime") ,b.getDouble("total"),b.getInt("status"),b.getLong("responseDateTime"));
        TextView mainText = (TextView) findViewById(R.id.mainText3);
        RorS = (TextView) findViewById(R.id.roomOrSuite);
        if (RORS == 1)
        {
            RorS.setText("ROOM");
        }
        else
        {
            RorS.setText("SUITE");
        }
        Total = (TextView) findViewById(R.id.orderTotal);
        Total.setText(String.valueOf(TOTAL));
        DateTime = (TextView) findViewById(R.id.dateTime);
        DateTime.setText(convertMillisToTime(DATETIME));
        Reservation = (TextView) findViewById(R.id.textView12);
        Reservation.setText(String.valueOf(RESERVATION));
        mainText.setText("ORDER " + id+" Room "+ room);
        orderNumber = id ;
        items =(ListView) findViewById(R.id.orderItems);
        ITEMS = (GridView) findViewById(R.id.itemsGrid);
        for (int i=0 ; i<RestaurantOrders.Rooms.size() ; i++)
        {
            if (RestaurantOrders.Rooms.get(i).RoomNumber == room )
            {
                RoomId = i ;
            }
        }
        Toast.makeText(act,RoomId+"",Toast.LENGTH_LONG).show();
        getorderItems();
    }

    public static void getorderItems()
    {
        list.clear();
        final Dialog d = new Dialog(act);
        d.setCancelable(false);
        d.setContentView(R.layout.loading_dialog);
        d.show();
        StringRequest re = new StringRequest(Request.Method.POST, itemsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(act , response , Toast.LENGTH_LONG).show();
                list.clear();
                try {
                    JSONArray arr = new JSONArray(response);
                    for (int i=0;i<arr.length();i++)
                    {
                        JSONObject row = arr.getJSONObject(i);
                        int id = row.getInt("id");
                        int orderNumber = row.getInt("orderNumber");
                        int room =row.getInt("room");
                        int itemNo =row.getInt("itemNo");
                        String name = row.getString("name");
                        int quantity =row.getInt("quantity");
                        double price = row.getLong("price") ;
                        double total = row.getDouble("total") ;
                        String descr = row.getString("descr");
                        String notes = row.getString("notes");
                        OrderItem item  = new OrderItem(id,orderNumber,room,itemNo,name,descr,quantity,price,total,notes);
                        list.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                d.dismiss();
                Order_Items_Adapter adapter = new Order_Items_Adapter(list ,act);
                //items.setAdapter(adapter);
                ITEMS.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                d.dismiss();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params =new  HashMap<String, String>();
                params.put("orderNumber" , String.valueOf(orderNumber));
                return params;
            }
        };

        Volley.newRequestQueue(act).add(re);

    }

    public void maeOrderDone(View view)
    {

        final Dialog d = new Dialog(act);
        d.setContentView(R.layout.loading_dialog);
        d.setCancelable(false);
        d.show();
        StringRequest request = new StringRequest(Request.Method.POST, makeOrderDone, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                d.dismiss();
                if (response.equals("1"))
                {
                    RestaurantOrders.FireRooms.get(RoomId).child("Restaurant").setValue(0);
                    RestaurantOrders.FireRooms.get(RoomId).child("Facility").setValue(0);
                    act.finish();
                }
                else
                {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                d.dismiss();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String , String> params = new HashMap<String, String>();
                params.put("id" , String.valueOf( list.get(0).orderNumber));
                Calendar x = Calendar.getInstance(Locale.getDefault());
                double time =  x.getTimeInMillis();
                params.put("time" ,String.valueOf(time));
                params.put("room" ,String.valueOf(room) );
                return params;
            }
        };

        Volley.newRequestQueue(act).add(request);
    }

    //-------------------------------------------------------------------------
    //Send Cloud Message

    public void makemessage(String t , String service ,int room , String dep)
    {

        String TOPIC = t;
        String NOTIFICATION_TITLE = "Restaurant";
        String NOTIFICATION_MESSAGE = "Order Done";

        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);
            notifcationBody.put("service", service);
            notifcationBody.put("room", room);
            notifcationBody.put("orderAction", false);
            notifcationBody.put("dep", dep);
            notifcationBody.put("emp", LogIn.db.getUser().name);
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
                        Toast.makeText(act ,"تم تنفيذ طلبك", Toast.LENGTH_LONG ).show();
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

    @Override
    public void onResume()
    {
        super.onResume();
        // put your code here...
        getorderItems();
    }

    String convertMillisToTime(long millies)
    {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millies);
        c.setTimeZone(TimeZone.getDefault());
        int month = c.get(Calendar.MONTH)+1 ;
        String g = c.getTime().toString();
        String Time = c.get(Calendar.DAY_OF_MONTH)+"/"+month+"/"+c.get(Calendar.YEAR)+" " + c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);
        return g ;
    }

}
