package com.syriasoft.hotelservices;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class REST_EMPS_CLASS {

    int id ;
    int Facility ;
    String UserName ;
    String Name ;
    String Mobile ;
    String token ;
    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String serverKey = "key=" + "AAAAQmygXvw:APA91bFt5CiONiZPDDj4_kz9hmKXlL1cjfTa_ZNGfobMPmt0gamhzEoN2NHiOxypCDr_r5yfpLvJy-bQSgrykXvaqKkThAniTr-0hpXPBrXm7qWThMmkiaN9o6qaUqfIUwStMMuNedTw";
    private static final String contentType = "application/json";

    public REST_EMPS_CLASS(int id, int facility, String userName, String name, String mobile, String token) {
        this.id = id;
        Facility = facility;
        UserName = userName;
        Name = name;
        Mobile = mobile;
        this.token = token;
    }

    public void makemessage(String t ,String Order , boolean addOrRemove , Context c)
    {

        String NOTIFICATION_TITLE = Order ;
        String NOTIFICATION_MESSAGE = "" ;

        if (Order.equals("DND")) {
            if (addOrRemove) {
                NOTIFICATION_MESSAGE = MyApp.Room.RoomNumber + " is on DND mode";
            }
            else {
                NOTIFICATION_MESSAGE = "DND mode for "+MyApp.Room.RoomNumber+ " is off";
            }
        }
        else {
            if (addOrRemove) {
                NOTIFICATION_MESSAGE = "New " + Order + " Order From Room "+MyApp.Room.RoomNumber;
            }
            else {
                NOTIFICATION_MESSAGE = "Cancelled " + Order + " Order From Room "+MyApp.Room.RoomNumber;
            }
        }



        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);
            notifcationBody.put("RoomNumber", MyApp.Room.RoomNumber);
            notification.put("to", t);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {

        }
        sendNotification(notification ,c );
    }

    public void sendNotification(final JSONObject notification , Context c)
    {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_MESSAGE_URL, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("MessageResponse" , response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MessageResponse" , error.getMessage());
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
        Volley.newRequestQueue(c).add(jsonObjectRequest);

    }
}
