package com.syriasoft.cleanup;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReceivingService extends Service {
    private FirebaseDatabase database;
    private List<ROOM> Rooms;
    static List<cleanOrder> list = new ArrayList<>();
    private Random r = new Random();
    private Intent NotificationIntent, RestNotificationIntent;
    private int[] CLEANUP_RCODE;
    private int[] LAUNDRY_RCODE;
    private int[] DND_RCODE;
    private int[] SOS_RCODE;
    private int[] ROOMSERVICE_RCODE;
    private int[] RESTAURANT_RCODE;
    private int[] MINIBAR_RCODE;
    private NotificationManager notificationManager;
    public static ValueEventListener[] CleanupListiner;
    public static ValueEventListener[] LaundryListiner;
    public static ValueEventListener[] RoomServiceListiner;
    public static ValueEventListener[] DNDListiner;
    public static ValueEventListener[] SOSListiner;
    public static ValueEventListener[] RESTAURANTListiner;
    public static ValueEventListener[] MiniBarCheck;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        list = new ArrayList<>();
        list = MainActivity.list;
        Rooms = new ArrayList<>();
        if (MyApp.My_USER.department.equals("Restaurant")) {
            Rooms = RestaurantOrders.Rooms;
        } else {
            Rooms = MainActivity.Rooms;
        }
        CLEANUP_RCODE = new int[Rooms.size()];
        LAUNDRY_RCODE = new int[Rooms.size()];
        ROOMSERVICE_RCODE = new int[Rooms.size()];
        DND_RCODE = new int[Rooms.size()];
        SOS_RCODE = new int[Rooms.size()];
        RESTAURANT_RCODE = new int[Rooms.size()];
        MINIBAR_RCODE = new int[Rooms.size()];
        NotificationIntent = new Intent(this, MainActivity.class);
        NotificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        RestNotificationIntent = new Intent(this, RestaurantOrders.class);
        CleanupListiner = new ValueEventListener[Rooms.size()];
        LaundryListiner = new ValueEventListener[Rooms.size()];
        RoomServiceListiner = new ValueEventListener[Rooms.size()];
        DNDListiner = new ValueEventListener[Rooms.size()];
        SOSListiner = new ValueEventListener[Rooms.size()];
        RESTAURANTListiner = new ValueEventListener[Rooms.size()];
        MiniBarCheck = new ValueEventListener[Rooms.size()];
        for (int i = 0; i < Rooms.size(); i++) {
            database = FirebaseDatabase.getInstance("https://hotelservices-ebe66.firebaseio.com/");
            Rooms.get(i).setFireRoom(database.getReference(LogIn.Project + "/B" + Rooms.get(i).Building + "/F" + Rooms.get(i).Floor + "/R" + Rooms.get(i).RoomNumber));
            CLEANUP_RCODE[i] = 0;
            LAUNDRY_RCODE[i] = 0;
            DND_RCODE[i] = 0;
            SOS_RCODE[i] = 0;
            ROOMSERVICE_RCODE[i] = 0;
            RESTAURANT_RCODE[i] = 0;
            MINIBAR_RCODE[i] = 0;
        }
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setRoomsListeners();
        return START_STICKY;
    }

    private void setRoomsListeners() {
        for (int i = 0; i < Rooms.size(); i++) {
            final int finalI = i;
            if (MyApp.My_USER.department.equals("Cleanup")) {
                CleanupListiner[i] = Rooms.get(i).getFireRoom().child("Cleanup").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Long.parseLong(dataSnapshot.getValue().toString()) > 0 && CLEANUP_RCODE[finalI] == 0) {
                            int reqCode = r.nextInt();
                            CLEANUP_RCODE[finalI] = reqCode;
                            showNotification(getApplicationContext(), "Cleanup Order " + Rooms.get(finalI).RoomNumber, "new cleanup order from " + Rooms.get(finalI).RoomNumber, NotificationIntent, reqCode);
                        } else if (Long.parseLong(dataSnapshot.getValue().toString()) == 0 && CLEANUP_RCODE[finalI] != 0) {
                            try {
                                notificationManager.cancel(CLEANUP_RCODE[finalI]);
                            } catch (Exception e) {

                            }
                            CLEANUP_RCODE[finalI] = 0;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                DNDListiner[i] = Rooms.get(i).getFireRoom().child("DND").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Long.parseLong(dataSnapshot.getValue().toString()) > 0 && DND_RCODE[finalI] == 0) {
                            int reqCode = r.nextInt();
                            DND_RCODE[finalI] = reqCode;
                            showNotification(getApplicationContext(), "DND " + Rooms.get(finalI).RoomNumber, "room " + Rooms.get(finalI).RoomNumber + " is on DND mode", NotificationIntent, reqCode);
                        } else if (Long.parseLong(dataSnapshot.getValue().toString()) == 0 && DND_RCODE[finalI] != 0) {
                            try {
                                notificationManager.cancel(DND_RCODE[finalI]);
                            } catch (Exception e) {
                            }
                            DND_RCODE[finalI] = 0;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else if (MyApp.My_USER.department.equals("Laundry")) {
                LaundryListiner[i] = Rooms.get(i).getFireRoom().child("Laundry").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Long.parseLong(dataSnapshot.getValue().toString()) > 0 && LAUNDRY_RCODE[finalI] == 0) {
                            int reqCode = r.nextInt();
                            LAUNDRY_RCODE[finalI] = reqCode;
                            showNotification(getApplicationContext(), "Laundry Order " + Rooms.get(finalI).RoomNumber, "new laundry order from " + Rooms.get(finalI).RoomNumber, NotificationIntent, reqCode);
                        } else if (Long.parseLong(dataSnapshot.getValue().toString()) == 0 && LAUNDRY_RCODE[finalI] != 0) {
                            try {
                                notificationManager.cancel(LAUNDRY_RCODE[finalI]);
                            } catch (Exception e) {

                            }
                            LAUNDRY_RCODE[finalI] = 0;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                DNDListiner[i] = Rooms.get(i).getFireRoom().child("DND").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Long.parseLong(dataSnapshot.getValue().toString()) > 0 && DND_RCODE[finalI] == 0) {
                            int reqCode = r.nextInt();
                            DND_RCODE[finalI] = reqCode;
                            showNotification(getApplicationContext(), "DND " + Rooms.get(finalI).RoomNumber, "room " + Rooms.get(finalI).RoomNumber + " is on DND mode", NotificationIntent, reqCode);
                        } else if (Long.parseLong(dataSnapshot.getValue().toString()) == 0 && DND_RCODE[finalI] != 0) {
                            try {
                                notificationManager.cancel(DND_RCODE[finalI]);
                            } catch (Exception e) {
                            }
                            DND_RCODE[finalI] = 0;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else if (MyApp.My_USER.department.equals("RoomService")) {
                RoomServiceListiner[i] = Rooms.get(i).getFireRoom().child("RoomService").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Long.parseLong(dataSnapshot.getValue().toString()) > 0 && ROOMSERVICE_RCODE[finalI] == 0) {
                            int reqCode = r.nextInt();
                            ROOMSERVICE_RCODE[finalI] = reqCode;
                            showNotification(getApplicationContext(), "RoomService Order " + Rooms.get(finalI).RoomNumber, "new room service order from " + Rooms.get(finalI).RoomNumber, NotificationIntent, reqCode);
                        } else if (Long.parseLong(dataSnapshot.getValue().toString()) == 0 && ROOMSERVICE_RCODE[finalI] != 0) {
                            try {
                                notificationManager.cancel(ROOMSERVICE_RCODE[finalI]);
                            } catch (Exception e) {

                            }
                            ROOMSERVICE_RCODE[finalI] = 0;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                DNDListiner[i] = Rooms.get(i).getFireRoom().child("DND").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Long.parseLong(dataSnapshot.getValue().toString()) > 0 && DND_RCODE[finalI] == 0) {
                            int reqCode = r.nextInt();
                            DND_RCODE[finalI] = reqCode;
                            showNotification(getApplicationContext(), "DND " + Rooms.get(finalI).RoomNumber, "room " + Rooms.get(finalI).RoomNumber + " is on DND mode", NotificationIntent, reqCode);
                        } else if (Long.parseLong(dataSnapshot.getValue().toString()) == 0 && DND_RCODE[finalI] != 0) {
                            try {
                                notificationManager.cancel(DND_RCODE[finalI]);
                            } catch (Exception e) {
                            }
                            DND_RCODE[finalI] = 0;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                SOSListiner[i] = Rooms.get(i).getFireRoom().child("SOS").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Long.parseLong(dataSnapshot.getValue().toString()) > 0 && SOS_RCODE[finalI] == 0) {
                            int reqCode = r.nextInt();
                            SOS_RCODE[finalI] = reqCode;
                            showNotification(getApplicationContext(), "SOS " + Rooms.get(finalI).RoomNumber, "SOS on room " + Rooms.get(finalI).RoomNumber, NotificationIntent, reqCode);
                            Intent i = new Intent(getApplicationContext(), SOSService.class);
                            startService(i);
                        } else if (Long.parseLong(dataSnapshot.getValue().toString()) == 0 && SOS_RCODE[finalI] != 0) {
                            try {
                                notificationManager.cancel(SOS_RCODE[finalI]);
                            } catch (Exception e) {

                            }
                            SOS_RCODE[finalI] = 0;
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
                            if (Long.parseLong(dataSnapshot.getValue().toString()) > 0 && MINIBAR_RCODE[finalI] == 0) {
                                int reqCode = r.nextInt();
                                MINIBAR_RCODE[finalI] = reqCode;
                                showNotification(getApplicationContext(), "MINIBAR Check Order " + Rooms.get(finalI).RoomNumber, "new room service order from " + Rooms.get(finalI).RoomNumber, NotificationIntent, reqCode);
                            } else if (Long.parseLong(dataSnapshot.getValue().toString()) == 0 && MINIBAR_RCODE[finalI] != 0) {
                                try {
                                    notificationManager.cancel(MINIBAR_RCODE[finalI]);
                                } catch (Exception e) {

                                }
                                MINIBAR_RCODE[finalI] = 0;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else if (MyApp.My_USER.department.equals("Service")) {
                CleanupListiner[i] = Rooms.get(i).getFireRoom().child("Cleanup").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Long.parseLong(dataSnapshot.getValue().toString()) > 0 && CLEANUP_RCODE[finalI] == 0) {
                            int reqCode = r.nextInt();
                            CLEANUP_RCODE[finalI] = reqCode;
                            showNotification(getApplicationContext(), "Cleanup Order " + Rooms.get(finalI).RoomNumber, "new cleanup order from " + Rooms.get(finalI).RoomNumber, NotificationIntent, reqCode);
                        } else if (Long.parseLong(dataSnapshot.getValue().toString()) == 0 && CLEANUP_RCODE[finalI] != 0) {
                            try {
                                notificationManager.cancel(CLEANUP_RCODE[finalI]);
                            } catch (Exception e) {

                            }
                            CLEANUP_RCODE[finalI] = 0;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                LaundryListiner[i] = Rooms.get(i).getFireRoom().child("Laundry").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Long.parseLong(dataSnapshot.getValue().toString()) > 0 && LAUNDRY_RCODE[finalI] == 0) {
                            int reqCode = r.nextInt();
                            LAUNDRY_RCODE[finalI] = reqCode;
                            showNotification(getApplicationContext(), "Laundry Order " + Rooms.get(finalI).RoomNumber, "new laundry order from " + Rooms.get(finalI).RoomNumber, NotificationIntent, reqCode);
                        } else if (Long.parseLong(dataSnapshot.getValue().toString()) == 0 && LAUNDRY_RCODE[finalI] != 0) {
                            try {
                                notificationManager.cancel(LAUNDRY_RCODE[finalI]);
                            } catch (Exception e) {

                            }
                            LAUNDRY_RCODE[finalI] = 0;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                RoomServiceListiner[i] = Rooms.get(i).getFireRoom().child("RoomService").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Long.parseLong(dataSnapshot.getValue().toString()) > 0 && ROOMSERVICE_RCODE[finalI] == 0) {
                            int reqCode = r.nextInt();
                            ROOMSERVICE_RCODE[finalI] = reqCode;
                            showNotification(getApplicationContext(), "RoomService Order " + Rooms.get(finalI).RoomNumber, "new room service order from " + Rooms.get(finalI).RoomNumber, NotificationIntent, reqCode);
                        } else if (Long.parseLong(dataSnapshot.getValue().toString()) == 0 && ROOMSERVICE_RCODE[finalI] != 0) {
                            try {
                                notificationManager.cancel(ROOMSERVICE_RCODE[finalI]);
                            } catch (Exception e) {

                            }
                            ROOMSERVICE_RCODE[finalI] = 0;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                DNDListiner[i] = Rooms.get(i).getFireRoom().child("DND").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Long.parseLong(dataSnapshot.getValue().toString()) > 0 && DND_RCODE[finalI] == 0) {
                            int reqCode = r.nextInt();
                            DND_RCODE[finalI] = reqCode;
                            showNotification(getApplicationContext(), "DND " + Rooms.get(finalI).RoomNumber, "room " + Rooms.get(finalI).RoomNumber + " is on DND mode", NotificationIntent, reqCode);
                        } else if (Long.parseLong(dataSnapshot.getValue().toString()) == 0 && DND_RCODE[finalI] != 0) {
                            try {
                                notificationManager.cancel(DND_RCODE[finalI]);
                            } catch (Exception e) {
                            }
                            DND_RCODE[finalI] = 0;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                SOSListiner[i] = Rooms.get(i).getFireRoom().child("SOS").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Long.parseLong(dataSnapshot.getValue().toString()) > 0 && SOS_RCODE[finalI] == 0) {
                            int reqCode = r.nextInt();
                            SOS_RCODE[finalI] = reqCode;
                            showNotification(getApplicationContext(), "SOS " + Rooms.get(finalI).RoomNumber, "SOS on room " + Rooms.get(finalI).RoomNumber, NotificationIntent, reqCode);
                            Intent i = new Intent(getApplicationContext(), SOSService.class);
                            startService(i);
                        } else if (Long.parseLong(dataSnapshot.getValue().toString()) == 0 && SOS_RCODE[finalI] != 0) {
                            try {
                                notificationManager.cancel(SOS_RCODE[finalI]);
                            } catch (Exception e) {

                            }
                            SOS_RCODE[finalI] = 0;
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
                            if (Long.parseLong(dataSnapshot.getValue().toString()) > 0 && MINIBAR_RCODE[finalI] == 0) {
                                int reqCode = r.nextInt();
                                MINIBAR_RCODE[finalI] = reqCode;
                                showNotification(getApplicationContext(), "MINIBAR Check Order " + Rooms.get(finalI).RoomNumber, "new room service order from " + Rooms.get(finalI).RoomNumber, NotificationIntent, reqCode);
                            } else if (Long.parseLong(dataSnapshot.getValue().toString()) == 0 && MINIBAR_RCODE[finalI] != 0) {
                                try {
                                    notificationManager.cancel(MINIBAR_RCODE[finalI]);
                                } catch (Exception e) {

                                }
                                MINIBAR_RCODE[finalI] = 0;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else if (MyApp.My_USER.department.equals("Restaurant")) {
                RESTAURANTListiner[i] = Rooms.get(i).getFireRoom().child("Restaurant").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Long.parseLong(dataSnapshot.getValue().toString()) > 0 && RESTAURANT_RCODE[finalI] == 0) {
                            Rooms.get(finalI).getFireRoom().child("Facility").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (Long.parseLong(dataSnapshot.getValue().toString()) == RestaurantOrders.THEFACILITY.id) {
                                        int reqCode = r.nextInt();
                                        RESTAURANT_RCODE[finalI] = reqCode;
                                        showNotification(getApplicationContext(), "New Order " + Rooms.get(finalI).RoomNumber, "new order from " + Rooms.get(finalI).RoomNumber, RestNotificationIntent, reqCode);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else if (Long.parseLong(dataSnapshot.getValue().toString()) == 0 && RESTAURANT_RCODE[finalI] != 0) {
                            try {
                                notificationManager.cancel(RESTAURANT_RCODE[finalI]);
                            } catch (Exception e) {

                            }
                            RESTAURANT_RCODE[finalI] = 0;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
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
