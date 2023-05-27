package com.syriasoft.hotelservices;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.ControlLockCallback;
import com.ttlock.bl.sdk.constant.ControlAction;
import com.ttlock.bl.sdk.entity.ControlLockResult;
import com.ttlock.bl.sdk.entity.LockError;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IResultCallback;

import java.util.HashMap;
import java.util.Map;

public class messagingService extends FirebaseMessagingService {

    NotificationManager manager ;
    Service s = this ;
    private RequestQueue FirebaseTokenRegister ;

    public messagingService()
    {
       // manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
       if (remoteMessage.getData().get("title") != null) {
           Log.d("messageReceved" , remoteMessage.getData().get("title"));
           String title =  remoteMessage.getData().get("title");
           if (title.equals("message")) {
               FullscreenActivity.openMessageDialog(remoteMessage.getData().get("message"));
           }
//           if (title.equals("labor")) {
//               String service = remoteMessage.getData().get("service");
//               if (service.equals("Cleanup"))
//               {
//                   FullscreenActivity.finishCleanup();
//               }
//               else if (service.equals("Laundry"))
//               {
//                   FullscreenActivity.finishLaundry();
//               }
//               else if (service.length() >= 11)
//               {
//                   FullscreenActivity.finishRoomService();
//               }
//               else if (service.equals("SOS"))
//               {
//                   //FullscreenActivity.finishSOS();
//               }
//               else if (service.equals("Restaurant"))
//               {
//                   FullscreenActivity.finishRestaurant();
//               }
//           }
//           else if (title.equals("Restaurant")) {
//               FullscreenActivity.finishRestaurant();
//           }
//           else if(title.equals("opendoor")) {
//               new Handler(Looper.getMainLooper()).post(new Runnable() {
//                   @Override
//                   public void run()
//                   {
//                       TTLockClient.getDefault().controlLock(ControlAction.UNLOCK, FullscreenActivity.myTestLockEKey.getLockData(), FullscreenActivity.myTestLockEKey.getLockMac(),new ControlLockCallback() {
//                           @Override
//                           public void onControlLockSuccess(ControlLockResult controlLockResult) {
//                               Toast.makeText(s,"lock is unlock  success!",Toast.LENGTH_LONG).show();
//                               // d.dismiss();
//                               //ToastMaker.MakeToast("Door Opened",act);
//                           }
//
//                           @Override
//                           public void onFail(LockError error) {
//                               Toast.makeText(s,"unLock fail!--" + error.getDescription(),Toast.LENGTH_LONG).show();
//                               //d.dismiss();
//                               //ToastMaker.MakeToast("unLock fail!--",act);
//                           }
//                       });
//                   }
//               });
//
//           }
//           else if (title.equals("donecheckout")) {
//               FullscreenActivity.finishCheckout();
//           }
//           else if (title.equals("New Cleanup")) {
//               if (FullscreenActivity.THEROOM != null ) {
//                   if (FullscreenActivity.THEROOM.getSERVICE1_B() != null ) {
//                       if (FullscreenActivity.THEROOM.getSERVICE1_B().dps.get("2") != null ) {
//                           if (FullscreenActivity.THEROOM.getSERVICE1_B().dps.get("2").toString().equals("false")) {
//                               TuyaHomeSdk.newDeviceInstance(FullscreenActivity.THEROOM.getSERVICE1_B().devId).publishDps("{\"2\": true}", new IResultCallback() {
//                                   @Override
//                                   public void onError(String code, String error) {
//
//                                   }
//
//                                   @Override
//                                   public void onSuccess() {
//
//                                   }
//                               });
//                           }
//
//                       }
//
//                   }
//               }
//           }
//           else if (title.equals("poweroff")) {
//               FullscreenActivity.PowerOff() ;
//           }
//           else if (title.equals("checkin")) {
//               FullscreenActivity.CheckIn(); ;
//           }
//           else if (title.equals("poweron")) {
//               FullscreenActivity.PowerOn();
//           }
       }

    }
    @Override
    public void onNewToken(String token) {
        //Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
    }

    public void sendRegistrationToServer(final String token) {
        SharedPreferences pref = getSharedPreferences("MyProject", MODE_PRIVATE);
        if (pref.getString("RoomID", null) != null) {
            String roomId = pref.getString("RoomID", null) ;
            String url = MyApp.ProjectURL + "roomsManagement/modifyRoomFirebaseToken";
            StringRequest r = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("registToken", response );
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("registToken", error.toString() );
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("token",token);
                    params.put("room_id",roomId);
                    return params;
                }
            };
            if (FirebaseTokenRegister == null) {
                FirebaseTokenRegister = Volley.newRequestQueue(this) ;
            }
            FirebaseTokenRegister.add(r);
        }


//        String url = LogIn.URL+"registToken.php";
//        StringRequest r = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response)
//            {
//                if (response.equals("1"))
//                {
//                    Log.d("mmmm", "Refreshed " );
//                }
//                else
//                    {
//                        Log.d("mmmm", "error " );
//                    }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error)
//            {
//                Log.d("mmmm", "error " );
//            }
//        })
//        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError
//            {
//                Map<String,String> params = new HashMap<String,String>();
//                params.put("token",token);
//                params.put("roomNumber",String.valueOf(MyApp.Room.RoomNumber));
//                return params;
//            }
//        };
//
//        Volley.newRequestQueue(this ).add(r);

    }

    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {
        //SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(context);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);
        String CHANNEL_ID = "channel_name";// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id

        Log.d("showNotification", "showNotification: " + reqCode);
    }

    public void OpenTheDoor()
    {
        if(FullscreenActivity.myTestLockEKey == null){
            ToastMaker.MakeToast(" you should get your key list first " , s);
            return;
        }
        //final Dialog d = new Dialog(s);
       // d.setContentView(R.layout.loading_layout);
       // TextView t = (TextView) d.findViewById(R.id.textViewdfsdf);
       // t.setText("Door Opening");
        //d.setCancelable(false);
       // d.show();
        //ensureBluetoothIsEnabled();
        //showConnectLockToast();
        TTLockClient.getDefault().controlLock(ControlAction.UNLOCK,FullscreenActivity.myTestLockEKey.getLockData(),FullscreenActivity.myTestLockEKey.getLockMac(),new ControlLockCallback() {
            @Override
            public void onControlLockSuccess(ControlLockResult controlLockResult) {
                //Toast.makeText(act,"lock is unlock  success!",Toast.LENGTH_LONG).show();
                //d.dismiss();
                ToastMaker.MakeToast("Door Opened",s);
            }

            @Override
            public void onFail(LockError error) {
                // Toast.makeText(UnlockActivity.this,"unLock fail!--" + error.getDescription(),Toast.LENGTH_LONG).show();
               // d.dismiss();
                ToastMaker.MakeToast("unLock fail!--",s);
            }
        });

    }



}
