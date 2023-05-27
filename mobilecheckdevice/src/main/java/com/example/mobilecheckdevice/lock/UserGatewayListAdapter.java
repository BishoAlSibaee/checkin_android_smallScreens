package com.example.mobilecheckdevice.lock;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobilecheckdevice.lodingDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

;

/**
 * Created on  2019/4/12 0012 14:19
 *
 * @author theodre
 */
public class UserGatewayListAdapter extends  RecyclerView.Adapter<UserGatewayListAdapter.DeviceViewHolder>{

    public ArrayList<GatewayObj> mDataList = new ArrayList<GatewayObj>();

    private Context mContext;

    public UserGatewayListAdapter(Context context , ArrayList<GatewayObj> mDataList){
        mContext = context;
        this.mDataList = mDataList ;
    }

    public void updateData(ArrayList<GatewayObj> gatewayList)
    {
        if (gatewayList != null) {
            mDataList.clear();
            mDataList.addAll(gatewayList);
            notifyDataSetChanged();
        }
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(com.example.mobilecheckdevice.R.layout.user_gateway_list_item, parent, false);
        return new DeviceViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(final DeviceViewHolder _holder, @SuppressLint("RecyclerView") final int position)
    {
        final GatewayObj item = mDataList.get(position);
        _holder.name.setText(item.getGatewayName());
        _holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try
                {
                    AlertDialog.Builder d = new AlertDialog.Builder(_holder.itemView.getContext());
                    d.setTitle("Select Lock Gatewat "+ mDataList.get(position).getGatewayName());
                    d.setMessage("Do you Want To Select "+mDataList.get(position).getGatewayName()+"Gateway");
                    d.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                        }
                    });
                    d.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            final lodingDialog d = new lodingDialog(_holder.itemView.getContext());
                            String url = "https://bait-elmoneh.online/hotel-service/setLockGatewayName.php";
                            StringRequest re = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response)
                                {
                                    d.stop();
                                    if (response.equals("1"))
                                    {
                                        //LogIn.room.insertLockGateway(mDataList.get(position).getGatewayName());
                                        //ToastMaker.MakeToast("Lock Gateway Saved " , _holder.itemView.getContext());
                                    }
                                    else
                                        {
                                            //ToastMaker.MakeToast("Lock Gateway Save Failed " , _holder.itemView.getContext());
                                        }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    d.stop();
                                }
                            })
                            {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError
                                {
                                    Map<String,String> Params = new HashMap<String, String>();
                                    //Params.put("id" , String.valueOf( LogIn.room.getRoomDBid()));
                                    Params.put("gateway" , mDataList.get(position).getGatewayName());
                                    return Params;
                                }
                            };
                            Volley.newRequestQueue(_holder.itemView.getContext()).add(re);

                        }
                    });
                    d.create().show();
                }
                catch (Exception e)
                {
                    Calendar x = Calendar.getInstance(Locale.getDefault());
                    long time =  x.getTimeInMillis();
                    //ErrorRegister.rigestError(_holder.itemView.getContext() , LogIn.room.getProjectName() , LogIn.room.getRoomNumber(),time ,005 ,e.getMessage() , "error setting lock gateway in roomDB");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder
    {
        TextView name ;

        public DeviceViewHolder(View itemView)
        {
            super(itemView);
            name = (TextView)itemView.findViewById(com.example.mobilecheckdevice.R.id.tv_gateway_name);
        }

    }
}
