package com.syriasoft.cleanup;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class messagingService extends FirebaseMessagingService {
    public final static String KEY_MyRooms = "MyRooms";
    SharedPreferences sharedPreferences;
    public static final String SHARED_PREF_NAME = "MyPref";
    private NotificationManager notificationManager;
    String rooms;
    String[] arrRoom;
    String Project;
    String URL;
    UserDB db;
    String KEY_PROJECT = "proj";

    @Override
    public void onCreate() {
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        Project = sharedPreferences.getString(KEY_PROJECT, null);
        URL = "https://ratco-solutions.com/Checkin/" + Project + "/php/";
        db = new UserDB(this);
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        super.onCreate();
    }

    public void onMessageReceived(RemoteMessage remoteMessage) {// ...
        rooms = sharedPreferences.getString(KEY_MyRooms, null);
        arrRoom = rooms.split("-");
        Random r = new Random();
        int reqCode = r.nextInt();
        if (remoteMessage.getData() != null) {
            Log.d("IncomingMessage", remoteMessage.getData().get("title") + remoteMessage.getData().get("message"));
            boolean roomExists = false;
            if (remoteMessage.getData().get("RoomNumber") != null) {
                int roomNumber = Integer.parseInt(remoteMessage.getData().get("RoomNumber"));
                if (arrRoom != null) {
                    for (int i = 0; i < arrRoom.length; i++) {
                        if (Integer.parseInt(arrRoom[i]) == (roomNumber)) {
                            Log.d("TestshowMyRooms", "Enter if ");
                            roomExists = true;
                            break;
                        }
                    }
                }
            }
            if (roomExists) {
                Log.d("IncomingMessage", "room found");
                if (remoteMessage.getData().get("title") != null && remoteMessage.getData().get("message") != null) {
                    if (remoteMessage.getData().get("title").contains("SOS") && remoteMessage.getData().get("message").contains("New")) {
                        Log.d("IncomingMessage", "sos found");
                        Intent i = new Intent(getApplicationContext(), SOSService.class);
                        startService(i);
                        Intent ii = new Intent(getApplicationContext(), MainActivity.class);
                        showNotification(getApplicationContext(), remoteMessage.getData().get("title"), remoteMessage.getData().get("message"), ii, reqCode);
                    } else {
                        Intent i = new Intent(getApplicationContext(), LogIn.class);
                        showNotification(getApplicationContext(), remoteMessage.getData().get("title"), remoteMessage.getData().get("message"), i, reqCode);
                    }
                }
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        if (db.isLogedIn()) {
            sendRegistrationToServer(token);
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://hotelservices-ebe66.firebaseio.com/");
            DatabaseReference MyFireUser = database.getReference(Project + "ServiceUsers/" + db.getUser().jobNumber);
            MyFireUser.child("token").setValue(token);
        }
    }

    void sendRegistrationToServer(final String token) {
        String url = URL + "registCleanupUserToken.php";
        StringRequest r = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                params.put("jnum", String.valueOf(db.getUser().jobNumber));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(r);
    }

    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent p = stackBuilder.getPendingIntent(reqCode, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        String CHANNEL_ID = "channel_name";// The id of the channel.
        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.notification_sound);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(p)
                .setColor(Color.parseColor("#0E223B"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setSound(soundUri, null);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id
    }
}
