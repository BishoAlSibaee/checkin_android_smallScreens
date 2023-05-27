package com.example.mobilecheckdevice;

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

public class ServiceEmps {

    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String serverKey = "key=" + "AAAAQmygXvw:APA91bFt5CiONiZPDDj4_kz9hmKXlL1cjfTa_ZNGfobMPmt0gamhzEoN2NHiOxypCDr_r5yfpLvJy-bQSgrykXvaqKkThAniTr-0hpXPBrXm7qWThMmkiaN9o6qaUqfIUwStMMuNedTw";
    private static final String contentType = "application/json";

    int id ;
    int projectId ;
    String name ;
    int jobNumber ;
    String department ;
    String mobile ;
    String token ;

    public ServiceEmps(int id, int projectId, String name, int jobNumber, String department, String mobile, String token) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.jobNumber = jobNumber;
        this.department = department;
        this.mobile = mobile;
        this.token = token;
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

    public void makemessage(String t ,String Order , boolean addOrRemove , Context c)
    {

        String NOTIFICATION_TITLE = Order ;
        String NOTIFICATION_MESSAGE = "" ;
        if (addOrRemove) {
            //NOTIFICATION_MESSAGE = "New " + Order + " Order From Room "+LogIn.room.getRoomNumber();
        }
        else {
           // NOTIFICATION_MESSAGE = "Cancelled " + Order + " Order From Room "+LogIn.room.getRoomNumber();
        }


        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);
           // notifcationBody.put("RoomNumber", LogIn.room.getRoomNumber());
            notification.put("to", t);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {

        }
        sendNotification(notification ,c );
    }
}
