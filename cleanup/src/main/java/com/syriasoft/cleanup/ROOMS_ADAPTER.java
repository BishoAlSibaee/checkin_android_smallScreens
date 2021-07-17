package com.syriasoft.cleanup;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.ControlLockCallback;
import com.ttlock.bl.sdk.constant.ControlAction;
import com.ttlock.bl.sdk.entity.ControlLockResult;
import com.ttlock.bl.sdk.entity.LockError;
import com.tuya.smart.sdk.api.IResultCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ROOMS_ADAPTER extends RecyclerView.Adapter<ROOMS_ADAPTER.HOLDER> {

    String registerDoorOpenUrl="https://ratco-solutions.com/HotelServicesTest/TestProject/p/insertDoorOpen.php";

    List<ROOM> list = new ArrayList<ROOM>();

    public ROOMS_ADAPTER(List<ROOM> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ROOMS_ADAPTER.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rooms_unit,parent,false);
        HOLDER holder = new HOLDER(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ROOMS_ADAPTER.HOLDER holder, final int position) {
            holder.room.setText(String.valueOf(list.get(position).RoomNumber));

            holder.room.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog d = new Dialog(holder.itemView.getContext());
                    d.setContentView(R.layout.room_dialog);
                    TextView text = (TextView)d.findViewById(R.id.room_dialog_text);
                    text.setText("Room : "+list.get(position).RoomNumber);
                    Button door = (Button)d.findViewById(R.id.room_dialog_door);
                    Button power = (Button)d.findViewById(R.id.room_dialog_power);
                    door.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (list.get(position).getLOCK() != null ){
                                final LoadingDialog dd = new LoadingDialog(holder.itemView.getContext());
                                StringRequest request = new StringRequest(Request.Method.POST, registerDoorOpenUrl, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("registerOpen" , response);
                                        if (response.equals("1")){
                                            TTLockClient.getDefault().controlLock(ControlAction.UNLOCK, list.get(position).getLOCK().getLockData(), list.get(position).getLOCK().getLockMac(),new ControlLockCallback()
                                            {
                                                @Override
                                                public void onControlLockSuccess(ControlLockResult controlLockResult) {
                                                    dd.close();
                                                    Log.d("registerOpen" , "opened");
                                                    d.dismiss();
                                                    //
                                                    //Toast.makeText(holder.itemView.getContext(),"Room "+list.get(position).RoomNumber+" Door Opened" , Toast.LENGTH_LONG);
                                                    messageDialog m = new messageDialog("Room "+list.get(position).RoomNumber+" Door Opened","Door Opened",holder.itemView.getContext());
                                                }

                                                @Override
                                                public void onFail(LockError error) {
                                                    dd.close();
                                                    Log.d("registerOpen" , error.getErrorMsg());
                                                    d.dismiss();
                                                    //Toast.makeText(holder.itemView.getContext(),error.getErrorMsg() , Toast.LENGTH_LONG);
                                                    messageDialog m = new messageDialog("Room "+list.get(position).RoomNumber+" Door Open Failed .. Try to be Closer","Door Open Failed",holder.itemView.getContext());
                                                }
                                            });
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        dd.close();
                                        Toast.makeText(holder.itemView.getContext(),error.getMessage() , Toast.LENGTH_LONG);
                                        Log.d("registerOpen" , error.getMessage());
                                    }
                                }){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Calendar c = Calendar.getInstance(Locale.getDefault());
                                        String Date = c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DAY_OF_MONTH);
                                        String Time = c.get(Calendar.HOUR)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND);
                                        Map<String,String> par = new HashMap<String, String>();
                                        par.put("EmpID" , String.valueOf(LogIn.db.getUser().id));
                                        par.put("JNum" , String.valueOf(LogIn.db.getUser().jobNumber));
                                        par.put("Name" , LogIn.db.getUser().name);
                                        par.put("Department" , LogIn.db.getUser().department);
                                        par.put("Room" , String.valueOf(list.get(position).RoomNumber));
                                        par.put("Date" , Date);
                                        par.put("Time" , Time);
                                        return par;
                                    }
                                };
                                Volley.newRequestQueue(holder.itemView.getContext()).add(request);
                            }
                        }
                    });
                    power.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (list.get(position).getPOWER() != null){
                                ROOMS.FireRooms.get(position).child("roomStatus").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null ){
                                            if ( !dataSnapshot.getValue().toString().equals("2") ){

                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        final boolean[] A = {false};
                                                        final boolean[] B = {false};
                                                        final boolean[] C = {false};
                                                        list.get(position).getPower().publishDps("{\"1\": true}", new IResultCallback() {
                                                            @Override
                                                            public void onError(String code, String error) {
                                                                //Toast.makeText(act, error, Toast.LENGTH_SHORT).show();
                                                                Log.e("power", error);
                                                                messageDialog dd = new messageDialog("Power Couldn't Turn On at Room "+list.get(position).RoomNumber,"Room "+list.get(position).RoomNumber+" Power On Failed" ,holder.itemView.getContext());
                                                                d.dismiss();
                                                            }

                                                            @Override
                                                            public void onSuccess() {
                                                                //Toast.makeText(act, "turn on the light success", Toast.LENGTH_SHORT).show();
                                                                //myRefPower.setValue(1);
                                                                Log.d("power", 1+" on");
                                                                A[0] =true ;
                                                                list.get(position).getPower().publishDps("{\"2\": true}", new IResultCallback() {
                                                                    @Override
                                                                    public void onError(String code, String error) {
                                                                        //Toast.makeText(act, "turn on the light failure", Toast.LENGTH_SHORT).show();
                                                                        Log.e("power", error);
                                                                        messageDialog dd = new messageDialog("Power Couldn't Turn On at Room "+list.get(position).RoomNumber,"Room "+list.get(position).RoomNumber+" Power On Failed" ,holder.itemView.getContext());
                                                                        d.dismiss();
                                                                    }

                                                                    @Override
                                                                    public void onSuccess() {
                                                                        Log.d("power", 2+" on");
                                                                        B[0] = true ;
                                                                        list.get(position).getPower().publishDps("{\"8\":300}", new IResultCallback() {
                                                                            @Override
                                                                            public void onError(String code, String error) {
                                                                                //Toast.makeText(act, "turn on the light failure", Toast.LENGTH_SHORT).show();
                                                                                Log.e("power", error);
                                                                                messageDialog dd = new messageDialog("Power Couldn't Turn On at Room "+list.get(position).RoomNumber,"Room "+list.get(position).RoomNumber+" Power On Failed" ,holder.itemView.getContext());
                                                                                d.dismiss();
                                                                            }

                                                                            @Override
                                                                            public void onSuccess() {
                                                                                Log.d("power", 2+" counting on");
                                                                                C[0] = true ;
                                                                                if (A[0] && B[0] && C[0]) {
                                                                                    messageDialog dd = new messageDialog("Power At Room "+list.get(position).RoomNumber+"is On " ,"Room "+list.get(position).RoomNumber+" Power On" ,holder.itemView.getContext());
                                                                                    d.dismiss();
                                                                                }
                                                                                else {
                                                                                    messageDialog dd = new messageDialog("Power Couldn't Turn On at Room "+list.get(position).RoomNumber,"Room "+list.get(position).RoomNumber+" Power On Failed" ,holder.itemView.getContext());
                                                                                    d.dismiss();
                                                                                }
                                                                            }
                                                                        });

                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                            else {

                                                messageDialog dd = new messageDialog("Room Is Occupied Now " ,"Room Occupied" ,holder.itemView.getContext());
                                                d.dismiss();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            else{
                                Log.d("power", "power is null");
                            }
                        }
                    });
                    if (list.get(position).getPOWER() == null ){
                        power.setActivated(false);
                        power.setClickable(false);
                        power.setTextColor(Color.GRAY);
                    }
                    if (list.get(position).getLOCK() == null ){
                        door.setActivated(false);
                        door.setClickable(false);
                        door.setTextColor(Color.GRAY);
                    }
                    d.show();
                }
            });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HOLDER extends RecyclerView.ViewHolder {
        Button room ;
        public HOLDER(@NonNull View itemView) {
            super(itemView);
            room = (Button) itemView.findViewById(R.id.rooms_roomBtn);
        }
    }
}
