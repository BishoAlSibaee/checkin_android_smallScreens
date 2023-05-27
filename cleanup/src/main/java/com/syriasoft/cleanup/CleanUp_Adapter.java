package com.syriasoft.cleanup;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CleanUp_Adapter extends BaseAdapter {

    List<cleanOrder> list = new ArrayList<cleanOrder>();
    LayoutInflater inflter;
    Context co;
    String setRoomStatus = LogIn.URL + "setRoomStatus.php";

    CleanUp_Adapter(List<cleanOrder> list, Context c) {
        this.list = sortList(list);
        inflter = (LayoutInflater.from(c));
        this.co = c;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = inflter.inflate(R.layout.order_unit, null);
        final LinearLayout l = (LinearLayout) convertView.findViewById(R.id.all);
        TextView room = convertView.findViewById(R.id.cleanOrder_room);
        TextView dep = convertView.findViewById(R.id.cleanOrder_orderType);
        TextView date = convertView.findViewById(R.id.cleanOrder_orderDate);
        TextView vv = convertView.findViewById(R.id.textView3);
        ImageView img = convertView.findViewById(R.id.imageView2);
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(list.get(position).date);
        int month = c.get(Calendar.MONTH) + 1;
        date.setText(c.get(Calendar.DAY_OF_MONTH) + "/" + month + "/" + c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
        room.setText(list.get(position).roomNumber);
        ROOM RR = searchRoomByNumber(Integer.parseInt(list.get(position).roomNumber),MainActivity.Rooms);
        if (RR != null) {
            if (RR.roomStatus == 1) {
                room.setTextColor(Color.WHITE);
                room.setBackgroundColor(co.getColor(R.color.greenRoom));
                img.setBackgroundColor(co.getColor(R.color.greenRoom));
            }
            else if (RR.roomStatus == 2) {
                room.setTextColor(Color.WHITE);
                room.setBackgroundColor(co.getColor(R.color.redRoom));
                room.setBackgroundResource(R.color.redRoom);
                vv.setBackgroundColor(co.getColor(R.color.redRoom));
                img.setBackgroundColor(co.getColor(R.color.redRoom));
            }
            else if (RR.roomStatus == 3) {
                room.setTextColor(Color.WHITE);
                room.setBackgroundColor(co.getColor(R.color.blueRoom));
                vv.setBackgroundColor(co.getColor(R.color.blueRoom));
                img.setBackgroundColor(co.getColor(R.color.blueRoom));
            }
            else if (RR.roomStatus == 4) {
                room.setTextColor(Color.WHITE);
                room.setBackgroundColor(Color.GRAY);
                vv.setBackgroundColor(Color.GRAY);
                img.setBackgroundColor(Color.GRAY);
            }
            RR.getFireRoom().child("SuiteStatus").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        if (dataSnapshot.getValue().toString().equals("2")) {
                            vv.setVisibility(View.VISIBLE);
                            vv.setText("S");
                            vv.setTextColor(Color.WHITE);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        if (list.get(position).dep.equals("Cleanup")) {
            img.setImageResource(R.drawable.cleanup_btn);
            dep.setText(list.get(position).dep);
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dep.setBackgroundColor(co.getColor(R.color.transparentGray));
                    date.setBackgroundColor(co.getColor(R.color.transparentGray));
                    final Dialog d = new Dialog(co);
                    d.setContentView(R.layout.confermation_dialog);
                    d.setCancelable(false);
                    Window w = d.getWindow();
                    w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    TextView message = d.findViewById(R.id.confermationDialog_Text);
                    message.setText("Are You Sure ..? ");
                    Button cancel = d.findViewById(R.id.confermationDialog_cancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d.dismiss();
                            dep.setBackgroundColor(co.getColor(R.color.white));
                            date.setBackgroundColor(co.getColor(R.color.white));
                        }
                    });
                    Button ok = d.findViewById(R.id.messageDialog_ok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            MainActivity.makeOrderDone(position, Integer.parseInt(list.get(position).roomNumber), Integer.parseInt(list.get(position).orderNumber), d, LogIn.db.getUser().jobNumber, list.get(position).dep);
//                            ROOM R = searchRoomByNumber(Integer.parseInt(list.get(position).roomNumber), MainActivity.Rooms);
//                            if (R != null) {
//                                R.getFireRoom().child("Cleanup").setValue(0);
//                                d.dismiss();
//                                R.getFireRoom().child("roomStatus").addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        if (dataSnapshot.getValue() != null) {
//                                            if (dataSnapshot.getValue().toString().equals("3")) {
//                                                StringRequest request = new StringRequest(Request.Method.POST, setRoomStatus, new Response.Listener<String>() {
//                                                    @Override
//                                                    public void onResponse(String response) {
//                                                        Log.d("roomStatusRes", response);
//                                                    }
//                                                }, new Response.ErrorListener() {
//                                                    @Override
//                                                    public void onErrorResponse(VolleyError error) {
//                                                        Log.e("roomStatusRes", error.getMessage());
//                                                    }
//                                                }) {
//                                                    @Override
//                                                    protected Map<String, String> getParams() throws AuthFailureError {
//                                                        Map<String, String> par = new HashMap<String, String>();
//                                                        par.put("room", String.valueOf(finalRoomNumber));
//                                                        return par;
//                                                    }
//                                                };
//                                                Volley.newRequestQueue(co).add(request);
//                                                R.getFireRoom().child("roomStatus").setValue(1);
//                                            }
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//                            }
                            if (RR.roomStatus == 3) {
                                prepareRoom(MainActivity.Q, String.valueOf(RR.id), new VolleyCallback() {
                                    @Override
                                    public void onSuccess(String res) {
                                        d.dismiss();
                                        Toast.makeText(MainActivity.act,"Order Done",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailed(String error) {
                                        new messageDialog(error,"failed",MainActivity.act);
                                    }
                                });
                            }
                            else {
                                finishServiceOrder(MainActivity.Q, String.valueOf(RR.id), "Cleanup", new VolleyCallback() {
                                    @Override
                                    public void onSuccess(String res) {
                                        d.dismiss();
                                        Toast.makeText(MainActivity.act,"Order Done",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailed(String error) {
                                        new messageDialog(error,"failed",MainActivity.act);
                                    }
                                });
                            }
                        }
                    });
                    d.show();
                    return false;
                }
            });
        }
        else if (list.get(position).dep.equals("Laundry")) {
            img.setImageResource(R.drawable.laundry_btn);
            dep.setText(list.get(position).dep);
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dep.setBackgroundColor(co.getColor(R.color.transparentGray));
                    date.setBackgroundColor(co.getColor(R.color.transparentGray));
                    final Dialog d = new Dialog(co);
                    d.setContentView(R.layout.confermation_dialog);
                    d.setCancelable(false);
                    Window w = d.getWindow();
                    w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    TextView message = d.findViewById(R.id.confermationDialog_Text);
                    message.setText("Are You Sure ..? ");
                    Button cancel = d.findViewById(R.id.confermationDialog_cancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d.dismiss();
                            dep.setBackgroundColor(co.getColor(R.color.white));
                            date.setBackgroundColor(co.getColor(R.color.white));
                        }
                    });
                    Button ok = d.findViewById(R.id.messageDialog_ok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finishServiceOrder(MainActivity.Q, String.valueOf(RR.id), "Laundry", new VolleyCallback() {
                                @Override
                                public void onSuccess(String res) {
                                    d.dismiss();
                                    Toast.makeText(MainActivity.act,"Order Done",Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailed(String error) {
                                    new messageDialog(error,"failed",MainActivity.act);
                                }
                            });
                        }
                    });
                    d.show();
                    return false;
                }
            });
        }
        else if (list.get(position).dep.equals("RoomService")) {
            img.setImageResource(R.drawable.roomservice);
            dep.setText(list.get(position).roomServiceText);
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dep.setBackgroundColor(co.getColor(R.color.transparentGray));
                    date.setBackgroundColor(co.getColor(R.color.transparentGray));
                    final Dialog d = new Dialog(co);
                    d.setContentView(R.layout.confermation_dialog);
                    d.setCancelable(false);
                    Window w = d.getWindow();
                    w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    TextView message = d.findViewById(R.id.confermationDialog_Text);
                    message.setText("Are You Sure ..? ");
                    Button cancel = d.findViewById(R.id.confermationDialog_cancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d.dismiss();
                            dep.setBackgroundColor(co.getColor(R.color.white));
                            date.setBackgroundColor(co.getColor(R.color.white));
                        }
                    });
                    Button ok = (Button) d.findViewById(R.id.messageDialog_ok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finishServiceOrder(MainActivity.Q, String.valueOf(RR.id), "RoomService", new VolleyCallback() {
                                @Override
                                public void onSuccess(String res) {
                                    d.dismiss();
                                    Toast.makeText(MainActivity.act,"Order Done",Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailed(String error) {
                                    new messageDialog(error,"failed",MainActivity.act);
                                }
                            });
                        }
                    });
                    d.show();
                    return false;
                }
            });
        }
        else if (list.get(position).dep.equals("SOS")) {
            img.setImageResource(R.drawable.sos_btn);
            dep.setText(list.get(position).dep);
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dep.setBackgroundColor(co.getColor(R.color.transparentGray));
                    date.setBackgroundColor(co.getColor(R.color.transparentGray));
                    final Dialog d = new Dialog(co);
                    d.setContentView(R.layout.confermation_dialog);
                    d.setCancelable(false);
                    Window w = d.getWindow();
                    w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    TextView message = d.findViewById(R.id.confermationDialog_Text);
                    message.setText("Are You Sure ..? ");
                    Button cancel = d.findViewById(R.id.confermationDialog_cancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d.dismiss();
                            dep.setBackgroundColor(co.getColor(R.color.white));
                            date.setBackgroundColor(co.getColor(R.color.white));
                        }
                    });
                    Button ok = d.findViewById(R.id.messageDialog_ok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finishServiceOrder(MainActivity.Q, String.valueOf(RR.id), "SOS", new VolleyCallback() {
                                @Override
                                public void onSuccess(String res) {
                                    d.dismiss();
                                    Toast.makeText(MainActivity.act,"Order Done",Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailed(String error) {
                                    new messageDialog(error,"failed",MainActivity.act);
                                }
                            });
                        }
                    });
                    d.show();
                    return false;
                }
            });
        }
        else if (list.get(position).dep.equals("MiniBarCheck")) {
            img.setImageResource(R.drawable.minibar);
            img.setPadding(10, 10, 10, 10);
            dep.setText(list.get(position).dep);
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dep.setBackgroundColor(co.getColor(R.color.transparentGray));
                    date.setBackgroundColor(co.getColor(R.color.transparentGray));
                    final Dialog d = new Dialog(co);
                    d.setContentView(R.layout.confermation_dialog);
                    d.setCancelable(false);
                    Window w = d.getWindow();
                    w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    TextView message = d.findViewById(R.id.confermationDialog_Text);
                    message.setText("Are You Sure ..? ");
                    Button cancel = d.findViewById(R.id.confermationDialog_cancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d.dismiss();
                            dep.setBackgroundColor(co.getColor(R.color.white));
                            date.setBackgroundColor(co.getColor(R.color.white));
                        }
                    });
                    Button ok = d.findViewById(R.id.messageDialog_ok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Intent i = new Intent(co, com.syriasoft.cleanup.MiniBarCheck.class);
//                            i.putExtra("Room", MainActivity.orderDB.getOrders().get(position).roomNumber);
//                            i.putExtra("OrderId", MainActivity.orderDB.getOrders().get(position).orderNumber);
//                            Toast.makeText(co, "room " + MainActivity.orderDB.getOrders().get(position).roomNumber + " ordr " + MainActivity.orderDB.getOrders().get(position).orderNumber, Toast.LENGTH_LONG).show();
//                            d.dismiss();
//                            co.startActivity(i);
                            finishServiceOrder(MainActivity.Q, String.valueOf(RR.id), "MiniBarCheck", new VolleyCallback() {
                                @Override
                                public void onSuccess(String res) {
                                    d.dismiss();
                                    Toast.makeText(MainActivity.act,"Order Done",Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailed(String error) {
                                    new messageDialog(error,"failed",MainActivity.act);
                                }
                            });
                        }
                    });
                    d.show();
                    return false;
                }
            });
        }
        else {
            img.setVisibility(View.GONE);
        }
        return convertView;
    }

    ROOM searchRoomByNumber(int roomNumber, List<ROOM> rooms) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).RoomNumber == roomNumber) {
                return rooms.get(i);
            }
        }
        return null;
    }

    private List<cleanOrder> sortList(List<cleanOrder> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 1; j < (list.size() - i); j++) {
                if (list.get(j - 1).date > list.get(j).date) {
                    Collections.swap(list, j, j - 1);
                }
            }
        }
        return list;
    }

    void finishServiceOrder(RequestQueue Q,String room_id,String type,VolleyCallback callback) {
        String url = MyApp.URL + "reservations/finishServiceOrder" ;
        StringRequest r = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("finishOrder",response);
                try {
                    JSONObject result = new JSONObject(response);
                    if (result.getString("result").equals("success")) {
                        callback.onSuccess("success");
                    }
                    else {
                        callback.onFailed(result.getString("error"));
                    }
                } catch (JSONException e) {
                    Log.d("finishOrder",e.getMessage());
                    callback.onFailed(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("finishOrder",error.toString());
                callback.onFailed(error.toString());
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("room_id",room_id);
                params.put("jobnumber", String.valueOf(MyApp.My_USER.jobNumber));
                params.put("order_type",type);
                params.put("my_token",MyApp.Token);
                return params;
            }
        };
        Q.add(r);
    }

    void prepareRoom(RequestQueue Q,String room_id,VolleyCallback callback) {
        String url = MyApp.URL + "reservations/prepareRoom" ;
        StringRequest r = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("finishOrder",response);
                try {
                    JSONObject result = new JSONObject(response);
                    if (result.getString("result").equals("success")) {
                        callback.onSuccess("success");
                    }
                    else {
                        callback.onFailed(result.getString("error"));
                    }
                } catch (JSONException e) {
                    Log.d("finishOrder",e.getMessage());
                    callback.onFailed(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("finishOrder",error.toString());
                callback.onFailed(error.toString());
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("room_id",room_id);
                params.put("job_number", String.valueOf(MyApp.My_USER.jobNumber));
                params.put("my_token",MyApp.Token);
                return params;
            }
        };
        Q.add(r);
    }
}