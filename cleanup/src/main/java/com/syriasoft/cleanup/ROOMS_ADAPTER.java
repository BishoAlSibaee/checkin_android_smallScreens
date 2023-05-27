package com.syriasoft.cleanup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.api.IResultCallback;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ROOMS_ADAPTER extends RecyclerView.Adapter<ROOMS_ADAPTER.HOLDER> {

    List<ROOM> list;

    public ROOMS_ADAPTER(List<ROOM> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ROOMS_ADAPTER.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rooms_unit, parent, false);
        return new HOLDER(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ROOMS_ADAPTER.HOLDER holder, @SuppressLint("RecyclerView") final int position) {
        holder.room.setText(String.valueOf(list.get(position).RoomNumber));
        if (list.get(position).roomStatus == 1) {
            holder.room.setBackgroundResource(R.drawable.green_room);//.setTextColor(holder.itemView.getResources().getColor(R.color.greenRoom, null));
        }
        else if (list.get(position).roomStatus == 2) {
            holder.room.setBackgroundResource(R.drawable.red_room);//.setTextColor(holder.itemView.getResources().getColor(R.color.redRoom, null));
        }
        else if (list.get(position).roomStatus == 3) {
            holder.room.setBackgroundResource(R.drawable.blue_room);//.setTextColor(holder.itemView.getResources().getColor(R.color.blueRoom, null));
        }
        else if (list.get(position).roomStatus == 4) {
            holder.room.setBackgroundResource(R.drawable.gray_room);//.setTextColor(holder.itemView.getResources().getColor(R.color.transparentGray, null));
        }
        holder.room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog d = new Dialog(holder.itemView.getContext());
                d.setContentView(R.layout.room_dialog);
                d.setCancelable(false);
                Window w = d.getWindow();
                w.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView text = d.findViewById(R.id.room_dialog_text);
                text.setText("Room : " + list.get(position).RoomNumber);
                Button door = d.findViewById(R.id.room_dialog_door);
                Button power = d.findViewById(R.id.room_dialog_power);
                Button powerOff = d.findViewById(R.id.button4);
                ImageView close = d.findViewById(R.id.imageView6);
                ProgressBar p = d.findViewById(R.id.progressBar4);
                p.setVisibility(View.INVISIBLE);
                door.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if (list.get(position).getLOCK() != null) {
//                            if (MainActivity.isConnected) {
//                                final LoadingDialog dd = new LoadingDialog(holder.itemView.getContext());
//                                TTLockClient.getDefault().controlLock(ControlAction.UNLOCK, list.get(position).getLOCK().getLockData(), list.get(position).getLOCK().getLockMac(), new ControlLockCallback() {
//                                    @Override
//                                    public void onControlLockSuccess(ControlLockResult controlLockResult) {
//                                        dd.close();
//                                        messageDialog m = new messageDialog("Room " + list.get(position).RoomNumber + " Door Opened", "Door Opened", holder.itemView.getContext());
//                                        StringRequest request = new StringRequest(Request.Method.POST, registerDoorOpenUrl, new Response.Listener<String>() {
//                                            @Override
//                                            public void onResponse(String response) {
//                                                if (response.equals("1")) {
//
//                                                }
//                                            }
//                                        }, new Response.ErrorListener() {
//                                            @Override
//                                            public void onErrorResponse(VolleyError error) {
//                                                dd.close();
//                                                Toast.makeText(holder.itemView.getContext(), error.getMessage(), Toast.LENGTH_LONG);
//                                                //Log.d("registerOpen" , error.getMessage());
//                                            }
//                                        }) {
//                                            @Override
//                                            protected Map<String, String> getParams() throws AuthFailureError {
//                                                Calendar c = Calendar.getInstance(Locale.getDefault());
//                                                String Date = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH);
//                                                String Time = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
//                                                Map<String, String> par = new HashMap<String, String>();
//                                                par.put("EmpID", String.valueOf(LogIn.db.getUser().id));
//                                                par.put("JNum", String.valueOf(LogIn.db.getUser().jobNumber));
//                                                par.put("Name", LogIn.db.getUser().name);
//                                                par.put("Department", LogIn.db.getUser().department);
//                                                par.put("Room", String.valueOf(list.get(position).RoomNumber));
//                                                par.put("Date", Date);
//                                                par.put("Time", Time);
//                                                return par;
//                                            }
//                                        };
//                                        Volley.newRequestQueue(holder.itemView.getContext()).add(request);
//                                    }
//
//                                    @Override
//                                    public void onFail(LockError error) {
//                                        dd.close();
//                                        Log.d("registerOpen", error.getErrorMsg());
//                                        d.dismiss();
//                                        //Toast.makeText(holder.itemView.getContext(),error.getErrorMsg() , Toast.LENGTH_LONG);
//                                        messageDialog m = new messageDialog("Room " + list.get(position).RoomNumber + " Door Open Failed .. Try to be Closer", "Door Open Failed", holder.itemView.getContext());
//                                    }
//                                });
//                            }
//                            else {
//                                new messageDialog("please connect to internet ", "No internet", holder.itemView.getContext());
//                            }
//                        }
                        if (list.get(position).getLOCK() != null) {
                            Log.d("doorOpenResp" , "b lock not null");
                            String url = MyApp.URL + "roomsManagement/addUserDoorOpen";
                            p.setVisibility(View.VISIBLE);
                            StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject result = new JSONObject(response);
                                        if (result.getString("result") != null) {
                                            if (result.getString("result").equals("success")) {
                                                TTLockClient.getDefault().controlLock(ControlAction.UNLOCK,list.get(position).getLOCK().getLockData(),list.get(position).getLOCK().getLockMac(),new ControlLockCallback() {
                                                    @Override
                                                    public void onControlLockSuccess(ControlLockResult controlLockResult) {
                                                        p.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(holder.itemView.getContext(),"door opened",Toast.LENGTH_SHORT).show();;
                                                    }
                                                    @Override
                                                    public void onFail(LockError error) {
                                                        p.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(holder.itemView.getContext(),error.getErrorMsg(),Toast.LENGTH_SHORT).show();;
                                                    }
                                                });
                                            }
                                        }
                                    } catch (JSONException e) {
                                        p.setVisibility(View.INVISIBLE);
                                        Toast.makeText(holder.itemView.getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    p.setVisibility(View.INVISIBLE);
                                    Toast.makeText(holder.itemView.getContext(),error.toString(),Toast.LENGTH_SHORT).show();
                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String,String> params = new HashMap<>();
                                    params.put("room_id", String.valueOf(list.get(position).id));
                                    params.put("user_id",String.valueOf(MyApp.My_USER.id));
                                    return params;
                                }
                            };
                            Volley.newRequestQueue(holder.itemView.getContext()).add(req);
                        }
                        else {
                            if (list.get(position).getLOCK_T() != null) {
                                Log.d("doorOpenResp" , "b lock null ");
                                String url = MyApp.URL + "roomsManagement/addUserDoorOpen";
                                p.setVisibility(View.VISIBLE);
                                StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("doorOpenResp" , response+ " "+list.get(position).id);
                                        try {
                                            JSONObject result = new JSONObject(response);
                                                if (result.getString("result").equals("success")) {
                                                    ZigbeeLock.getTokenFromApi(MyApp.cloudClientId, MyApp.cloudSecret, holder.itemView.getContext(), new RequestOrder() {
                                                        @Override
                                                        public void onSuccess(String token) {
                                                            Log.d("doorOpenResp" , "token "+token);
                                                            ZigbeeLock.getTicketId(token, MyApp.cloudClientId, MyApp.cloudSecret,list.get(position).getLOCK_T().devId, holder.itemView.getContext(), new RequestOrder() {
                                                                @Override
                                                                public void onSuccess(String ticket) {
                                                                    Log.d("doorOpenResp" , "ticket "+ticket);
                                                                    ZigbeeLock.unlockWithoutPassword(token, ticket, MyApp.cloudClientId, MyApp.cloudSecret,list.get(position).getLOCK_T().devId, holder.itemView.getContext(), new RequestOrder() {
                                                                        @Override
                                                                        public void onSuccess(String res) {
                                                                            p.setVisibility(View.INVISIBLE);
                                                                            Toast.makeText(holder.itemView.getContext(),"door opened",Toast.LENGTH_SHORT).show();
                                                                        }

                                                                        @Override
                                                                        public void onFailed(String error) {
                                                                            p.setVisibility(View.INVISIBLE);
                                                                            Toast.makeText(holder.itemView.getContext(),error,Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                }
                                                                @Override
                                                                public void onFailed(String error) {
                                                                    p.setVisibility(View.INVISIBLE);
                                                                    Toast.makeText(holder.itemView.getContext(),error,Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                        @Override
                                                        public void onFailed(String error) {
                                                            p.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(holder.itemView.getContext(),error,Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                                else {
                                                    p.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(holder.itemView.getContext(),"error",Toast.LENGTH_SHORT).show();
                                                }

                                        } catch (JSONException e) {
                                            p.setVisibility(View.INVISIBLE);
                                            Toast.makeText(holder.itemView.getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        p.setVisibility(View.INVISIBLE);
                                        Toast.makeText(holder.itemView.getContext(),error.toString(),Toast.LENGTH_SHORT).show();
                                    }
                                }){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String,String> params = new HashMap<>();
                                        params.put("room_id", String.valueOf(list.get(position).id));
                                        params.put("user_id",String.valueOf(MyApp.My_USER.id));
                                        return params;
                                    }
                                };
                                Volley.newRequestQueue(holder.itemView.getContext()).add(req);
                            }
                            else {
                                new messageDialog("no lock detected in this room ","failed",holder.itemView.getContext());
                            }
                        }
                    }
                });
                power.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (list.get(position).getPOWER() != null) {
                            p.setVisibility(View.VISIBLE);
                            MainActivity.DevicesRef.child(String.valueOf(list.get(position).RoomNumber)).child(list.get(position).RoomNumber+"Power").child("1").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        int Val = Integer.parseInt(snapshot.getValue().toString());
                                        if (Val == 2) {
                                                list.get(position).getPower().registerDeviceListener(new IDeviceListener() {
                                                    @Override
                                                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                                                        Log.d("powerLis" , dpStr.toString());
                                                        if (Boolean.parseBoolean(list.get(position).getPOWER().dps.get("1").toString()) && Boolean.parseBoolean(list.get(position).getPOWER().dps.get("2").toString())) {
                                                            int Minutes = MyApp.ProjectVariables.HKCleanTime;
                                                            Minutes = Minutes * 60;
                                                            if (MyApp.ProjectVariables.PoweroffAfterHK == 1) {
                                                                list.get(position).getPower().publishDps("{\"8\":" + Minutes + "}", new IResultCallback() {
                                                                    @Override
                                                                    public void onError(String code, String error) {
                                                                        p.setVisibility(View.INVISIBLE);
                                                                        new messageDialog("Power Couldn't Turn On at Room " + list.get(position).RoomNumber, "Room " + list.get(position).RoomNumber + " Power On Failed", holder.itemView.getContext());
                                                                        d.dismiss();
                                                                    }

                                                                    @Override
                                                                    public void onSuccess() {
                                                                        p.setVisibility(View.INVISIBLE);
                                                                        new messageDialog("Power At Room " + list.get(position).RoomNumber + " is On ", "Room " + list.get(position).RoomNumber + " Power On", holder.itemView.getContext());
                                                                    }
                                                                });
                                                                list.get(position).getPower().unRegisterDevListener();
                                                            }
                                                            else if(MyApp.ProjectVariables.PoweroffAfterHK == 0) {
                                                                if (list.get(position).roomStatus == 2) {
                                                                    list.get(position).getPower().publishDps("{\"8\":" + Minutes + "}", new IResultCallback() {
                                                                        @Override
                                                                        public void onError(String code, String error) {
                                                                            p.setVisibility(View.INVISIBLE);
                                                                            new messageDialog("Power Couldn't Turn On at Room " + list.get(position).RoomNumber, "Room " + list.get(position).RoomNumber + " Power On Failed", holder.itemView.getContext());
                                                                            d.dismiss();
                                                                        }

                                                                        @Override
                                                                        public void onSuccess() {
                                                                            p.setVisibility(View.INVISIBLE);
                                                                            new messageDialog("Power At Room " + list.get(position).RoomNumber + " is On ", "Room " + list.get(position).RoomNumber + " Power On", holder.itemView.getContext());
                                                                        }
                                                                    });
                                                                }
                                                                else {
                                                                    list.get(position).getPower().publishDps("{\"8\":" + Minutes + ", \"7\":" + Minutes + "}", new IResultCallback() {
                                                                        @Override
                                                                        public void onError(String code, String error) {
                                                                            p.setVisibility(View.INVISIBLE);
                                                                            new messageDialog("Power Couldn't Turn On at Room " + list.get(position).RoomNumber, "Room " + list.get(position).RoomNumber + " Power On Failed", holder.itemView.getContext());
                                                                            d.dismiss();
                                                                        }

                                                                        @Override
                                                                        public void onSuccess() {
                                                                            p.setVisibility(View.INVISIBLE);
                                                                            new messageDialog("Power At Room " + list.get(position).RoomNumber + " is On ", "Room " + list.get(position).RoomNumber + " Power On", holder.itemView.getContext());
                                                                        }
                                                                    });
                                                                }
                                                                list.get(position).getPower().unRegisterDevListener();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onRemoved(String devId) {

                                                    }

                                                    @Override
                                                    public void onStatusChanged(String devId, boolean online) {

                                                    }

                                                    @Override
                                                    public void onNetworkStatusChanged(String devId, boolean status) {

                                                    }

                                                    @Override
                                                    public void onDevInfoUpdate(String devId) {

                                                    }
                                                });
                                                MainActivity.DevicesRef.child(String.valueOf(list.get(position).RoomNumber)).child(list.get(position).RoomNumber+"Power").child("1").setValue("3");
                                                MainActivity.DevicesRef.child(String.valueOf(list.get(position).RoomNumber)).child(list.get(position).RoomNumber+"Power").child("1").setValue("2");
                                        }
                                        else {
                                            list.get(position).getPower().registerDeviceListener(new IDeviceListener() {
                                                @Override
                                                public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                                                    Log.d("powerLis" , dpStr.toString());
                                                    if (Boolean.parseBoolean(list.get(position).getPOWER().dps.get("1").toString()) && Boolean.parseBoolean(list.get(position).getPOWER().dps.get("2").toString())) {
                                                        int Minutes = MyApp.ProjectVariables.HKCleanTime;
                                                        Minutes = Minutes * 60;
                                                        if (MyApp.ProjectVariables.PoweroffAfterHK == 1) {
                                                            list.get(position).getPower().publishDps("{\"8\":" + Minutes + "}", new IResultCallback() {
                                                                @Override
                                                                public void onError(String code, String error) {
                                                                    p.setVisibility(View.INVISIBLE);
                                                                    new messageDialog("Power Couldn't Turn On at Room " + list.get(position).RoomNumber, "Room " + list.get(position).RoomNumber + " Power On Failed", holder.itemView.getContext());
                                                                    d.dismiss();
                                                                }

                                                                @Override
                                                                public void onSuccess() {
                                                                    p.setVisibility(View.INVISIBLE);
                                                                    new messageDialog("Power At Room " + list.get(position).RoomNumber + " is On ", "Room " + list.get(position).RoomNumber + " Power On", holder.itemView.getContext());
                                                                }
                                                            });
                                                            list.get(position).getPower().unRegisterDevListener();
                                                        }
                                                        else if(MyApp.ProjectVariables.PoweroffAfterHK == 0) {
                                                            if (list.get(position).roomStatus == 2) {
                                                                list.get(position).getPower().publishDps("{\"8\":" + Minutes + "}", new IResultCallback() {
                                                                    @Override
                                                                    public void onError(String code, String error) {
                                                                        p.setVisibility(View.INVISIBLE);
                                                                        new messageDialog("Power Couldn't Turn On at Room " + list.get(position).RoomNumber, "Room " + list.get(position).RoomNumber + " Power On Failed", holder.itemView.getContext());
                                                                        d.dismiss();
                                                                    }

                                                                    @Override
                                                                    public void onSuccess() {
                                                                        p.setVisibility(View.INVISIBLE);
                                                                        new messageDialog("Power At Room " + list.get(position).RoomNumber + " is On ", "Room " + list.get(position).RoomNumber + " Power On", holder.itemView.getContext());
                                                                    }
                                                                });
                                                            }
                                                            else {
                                                                list.get(position).getPower().publishDps("{\"8\":" + Minutes + ", \"7\":" + Minutes + "}", new IResultCallback() {
                                                                    @Override
                                                                    public void onError(String code, String error) {
                                                                        p.setVisibility(View.INVISIBLE);
                                                                        new messageDialog("Power Couldn't Turn On at Room " + list.get(position).RoomNumber, "Room " + list.get(position).RoomNumber + " Power On Failed", holder.itemView.getContext());
                                                                        d.dismiss();
                                                                    }

                                                                    @Override
                                                                    public void onSuccess() {
                                                                        p.setVisibility(View.INVISIBLE);
                                                                        new messageDialog("Power At Room " + list.get(position).RoomNumber + " is On ", "Room " + list.get(position).RoomNumber + " Power On", holder.itemView.getContext());
                                                                    }
                                                                });
                                                            }

                                                            list.get(position).getPower().unRegisterDevListener();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onRemoved(String devId) {

                                                }

                                                @Override
                                                public void onStatusChanged(String devId, boolean online) {

                                                }

                                                @Override
                                                public void onNetworkStatusChanged(String devId, boolean status) {

                                                }

                                                @Override
                                                public void onDevInfoUpdate(String devId) {

                                                }
                                            });
                                            MainActivity.DevicesRef.child(String.valueOf(list.get(position).RoomNumber)).child(list.get(position).RoomNumber+"Power").child("1").setValue("2");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    p.setVisibility(View.INVISIBLE);
                                    new messageDialog("un able to turn power on .. check your internet connection","failed",holder.itemView.getContext());
                                }
                            });
                        }
                    }
                });
                powerOff.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (list.get(position).getPOWER() != null) {
                            p.setVisibility(View.VISIBLE);
                            MainActivity.DevicesRef.child(String.valueOf(list.get(position).RoomNumber)).child(list.get(position).RoomNumber+"Power").child("1").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        int Val = Integer.parseInt(snapshot.getValue().toString());
                                        if (MyApp.ProjectVariables.PoweroffAfterHK == 1) {
                                            if (Val == 1) {
                                                list.get(position).getPower().registerDeviceListener(new IDeviceListener() {
                                                    @Override
                                                    public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                                                        Log.d("powerLis" , dpStr.toString());
                                                        if (Boolean.parseBoolean(list.get(position).getPOWER().getDps().get("1").toString()) && !Boolean.parseBoolean(list.get(position).getPOWER().getDps().get("2").toString())) {
                                                            p.setVisibility(View.INVISIBLE);
                                                            new messageDialog("Power At Room " + list.get(position).RoomNumber + " is by card ", "Room " + list.get(position).RoomNumber + " Power by card", holder.itemView.getContext());
                                                            list.get(position).getPower().unRegisterDevListener();
                                                        }
                                                    }

                                                    @Override
                                                    public void onRemoved(String devId) {

                                                    }

                                                    @Override
                                                    public void onStatusChanged(String devId, boolean online) {

                                                    }

                                                    @Override
                                                    public void onNetworkStatusChanged(String devId, boolean status) {

                                                    }

                                                    @Override
                                                    public void onDevInfoUpdate(String devId) {

                                                    }
                                                });
                                                MainActivity.DevicesRef.child(String.valueOf(list.get(position).RoomNumber)).child(list.get(position).RoomNumber+"Power").child("1").setValue("3");
                                                MainActivity.DevicesRef.child(String.valueOf(list.get(position).RoomNumber)).child(list.get(position).RoomNumber+"Power").child("1").setValue("1");
                                            }
                                        }
                                        else if (MyApp.ProjectVariables.PoweroffAfterHK == 0) {
                                            if (list.get(position).roomStatus == 2) {
                                                if (Val == 1) {
                                                    list.get(position).getPower().registerDeviceListener(new IDeviceListener() {
                                                        @Override
                                                        public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                                                            Log.d("powerLis" , list.get(position).getPOWER().getDps().get("1").toString()+" "+list.get(position).getPOWER().getDps().get("2").toString());
                                                            if (list.get(position).roomStatus == 2) {
                                                                if (Boolean.parseBoolean(list.get(position).getPOWER().getDps().get("1").toString()) && !Boolean.parseBoolean(list.get(position).getPOWER().getDps().get("2").toString())) {
                                                                    p.setVisibility(View.INVISIBLE);
                                                                    new messageDialog("Power At Room " + list.get(position).RoomNumber + " is Off ", "Room " + list.get(position).RoomNumber + " Power Off", holder.itemView.getContext());
                                                                    list.get(position).getPower().unRegisterDevListener();
                                                                }
                                                            }
                                                            else {
                                                                if (!Boolean.parseBoolean(list.get(position).getPOWER().getDps().get("1").toString()) && !Boolean.parseBoolean(list.get(position).getPOWER().getDps().get("2").toString())) {
                                                                    p.setVisibility(View.INVISIBLE);
                                                                    new messageDialog("Power At Room " + list.get(position).RoomNumber + " is Off ", "Room " + list.get(position).RoomNumber + " Power Off", holder.itemView.getContext());
                                                                    list.get(position).getPower().unRegisterDevListener();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onRemoved(String devId) {

                                                        }

                                                        @Override
                                                        public void onStatusChanged(String devId, boolean online) {

                                                        }

                                                        @Override
                                                        public void onNetworkStatusChanged(String devId, boolean status) {

                                                        }

                                                        @Override
                                                        public void onDevInfoUpdate(String devId) {

                                                        }
                                                    });
                                                    MainActivity.DevicesRef.child(String.valueOf(list.get(position).RoomNumber)).child(list.get(position).RoomNumber+"Power").child("1").setValue("3");
                                                    MainActivity.DevicesRef.child(String.valueOf(list.get(position).RoomNumber)).child(list.get(position).RoomNumber+"Power").child("1").setValue("1");
                                                }
                                                else {
                                                    list.get(position).getPower().registerDeviceListener(new IDeviceListener() {
                                                        @Override
                                                        public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                                                            Log.d("powerLis" , list.get(position).getPOWER().getDps().get("1").toString()+" "+list.get(position).getPOWER().getDps().get("2").toString());
                                                            if (list.get(position).roomStatus == 2) {
                                                                if (Boolean.parseBoolean(list.get(position).getPOWER().getDps().get("1").toString()) && !Boolean.parseBoolean(list.get(position).getPOWER().getDps().get("2").toString())) {
                                                                    p.setVisibility(View.INVISIBLE);
                                                                    new messageDialog("Power At Room " + list.get(position).RoomNumber + " is Off ", "Room " + list.get(position).RoomNumber + " Power Off", holder.itemView.getContext());
                                                                    list.get(position).getPower().unRegisterDevListener();
                                                                }
                                                            }
                                                            else {
                                                                if (!Boolean.parseBoolean(list.get(position).getPOWER().getDps().get("1").toString()) && !Boolean.parseBoolean(list.get(position).getPOWER().getDps().get("2").toString())) {
                                                                    p.setVisibility(View.INVISIBLE);
                                                                    new messageDialog("Power At Room " + list.get(position).RoomNumber + " is Off ", "Room " + list.get(position).RoomNumber + " Power Off", holder.itemView.getContext());
                                                                    list.get(position).getPower().unRegisterDevListener();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onRemoved(String devId) {

                                                        }

                                                        @Override
                                                        public void onStatusChanged(String devId, boolean online) {

                                                        }

                                                        @Override
                                                        public void onNetworkStatusChanged(String devId, boolean status) {

                                                        }

                                                        @Override
                                                        public void onDevInfoUpdate(String devId) {

                                                        }
                                                    });
                                                    MainActivity.DevicesRef.child(String.valueOf(list.get(position).RoomNumber)).child(list.get(position).RoomNumber+"Power").child("1").setValue("1");
                                                }

                                            }
                                            else {
                                                if (Val == 0) {
                                                    list.get(position).getPower().registerDeviceListener(new IDeviceListener() {
                                                        @Override
                                                        public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                                                            Log.d("powerLis" , list.get(position).getPOWER().getDps().get("1").toString()+" "+list.get(position).getPOWER().getDps().get("2").toString());
                                                            if (list.get(position).roomStatus == 2) {
                                                                if (Boolean.parseBoolean(list.get(position).getPOWER().getDps().get("1").toString()) && !Boolean.parseBoolean(list.get(position).getPOWER().getDps().get("2").toString())) {
                                                                    p.setVisibility(View.INVISIBLE);
                                                                    new messageDialog("Power At Room " + list.get(position).RoomNumber + " is Off ", "Room " + list.get(position).RoomNumber + " Power Off", holder.itemView.getContext());
                                                                    list.get(position).getPower().unRegisterDevListener();
                                                                }
                                                            }
                                                            else {
                                                                if (!Boolean.parseBoolean(list.get(position).getPOWER().getDps().get("1").toString()) && !Boolean.parseBoolean(list.get(position).getPOWER().getDps().get("2").toString())) {
                                                                    p.setVisibility(View.INVISIBLE);
                                                                    new messageDialog("Power At Room " + list.get(position).RoomNumber + " is Off ", "Room " + list.get(position).RoomNumber + " Power Off", holder.itemView.getContext());
                                                                    list.get(position).getPower().unRegisterDevListener();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onRemoved(String devId) {

                                                        }

                                                        @Override
                                                        public void onStatusChanged(String devId, boolean online) {

                                                        }

                                                        @Override
                                                        public void onNetworkStatusChanged(String devId, boolean status) {

                                                        }

                                                        @Override
                                                        public void onDevInfoUpdate(String devId) {

                                                        }
                                                    });
                                                    MainActivity.DevicesRef.child(String.valueOf(list.get(position).RoomNumber)).child(list.get(position).RoomNumber+"Power").child("1").setValue("3");
                                                    MainActivity.DevicesRef.child(String.valueOf(list.get(position).RoomNumber)).child(list.get(position).RoomNumber+"Power").child("1").setValue("0");
                                                }
                                                else {
                                                    list.get(position).getPower().registerDeviceListener(new IDeviceListener() {
                                                        @Override
                                                        public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                                                            Log.d("powerLis" , list.get(position).getPOWER().getDps().get("1").toString()+" "+list.get(position).getPOWER().getDps().get("2").toString());
                                                            if (list.get(position).roomStatus == 2) {
                                                                if (Boolean.parseBoolean(list.get(position).getPOWER().getDps().get("1").toString()) && !Boolean.parseBoolean(list.get(position).getPOWER().getDps().get("2").toString())) {
                                                                    p.setVisibility(View.INVISIBLE);
                                                                    new messageDialog("Power At Room " + list.get(position).RoomNumber + " is Off ", "Room " + list.get(position).RoomNumber + " Power Off", holder.itemView.getContext());
                                                                    list.get(position).getPower().unRegisterDevListener();
                                                                }
                                                            }
                                                            else {
                                                                if (!Boolean.parseBoolean(list.get(position).getPOWER().getDps().get("1").toString()) && !Boolean.parseBoolean(list.get(position).getPOWER().getDps().get("2").toString())) {
                                                                    p.setVisibility(View.INVISIBLE);
                                                                    new messageDialog("Power At Room " + list.get(position).RoomNumber + " is Off ", "Room " + list.get(position).RoomNumber + " Power Off", holder.itemView.getContext());
                                                                    list.get(position).getPower().unRegisterDevListener();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onRemoved(String devId) {

                                                        }

                                                        @Override
                                                        public void onStatusChanged(String devId, boolean online) {

                                                        }

                                                        @Override
                                                        public void onNetworkStatusChanged(String devId, boolean status) {

                                                        }

                                                        @Override
                                                        public void onDevInfoUpdate(String devId) {

                                                        }
                                                    });
                                                    MainActivity.DevicesRef.child(String.valueOf(list.get(position).RoomNumber)).child(list.get(position).RoomNumber+"Power").child("1").setValue("0");
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                });
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                if (list.get(position).getPOWER() == null) {
                    power.setActivated(false);
                    power.setClickable(false);
                    powerOff.setActivated(false);
                    powerOff.setClickable(false);
                    power.setTextColor(Color.GRAY);
                    powerOff.setTextColor(Color.GRAY);
                }
                if (list.get(position).getLOCK() == null && list.get(position).getLOCK_T() == null) {
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
        Button room;

        public HOLDER(@NonNull View itemView) {
            super(itemView);
            room = itemView.findViewById(R.id.rooms_roomBtn);
        }
    }

}











