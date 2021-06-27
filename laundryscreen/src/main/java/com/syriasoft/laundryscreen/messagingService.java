package com.syriasoft.laundryscreen;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class messagingService extends FirebaseMessagingService {
    static String token ;
    public messagingService()
    {
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {

                MainActivity.getOrders();
    }
    @Override
    public void onNewToken(String token) {
       // Log.d(TAG, "Refreshed token: " + token);

        this.token = token ;
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    void sendRegistrationToServer(String token)
    {

    }

}
