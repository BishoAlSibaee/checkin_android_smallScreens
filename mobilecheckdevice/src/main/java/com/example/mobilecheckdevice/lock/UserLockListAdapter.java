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
import com.example.mobilecheckdevice.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobilecheckdevice.lodingDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

;

/**
 * Created on  2019/4/12 0012 14:19
 *
 * @author theodre
 */
public class UserLockListAdapter extends  RecyclerView.Adapter<UserLockListAdapter.DeviceViewHolder>{

    public ArrayList<LockObj> mDataList = new ArrayList<LockObj>();
    private Context mContext;

    public UserLockListAdapter(Context context ,ArrayList<LockObj> mDataList )
    {
        mContext = context;
        this.mDataList = mDataList ;

    }

    public void updateData(ArrayList<LockObj> lockList)
    {
        if (lockList != null) {
            mDataList.clear();
            mDataList.addAll(lockList);
            notifyDataSetChanged();
        }
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.user_lock_list_item, parent, false);
        return new DeviceViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(final DeviceViewHolder _holder, @SuppressLint("RecyclerView") final int position)
    {

        _holder.lock.setText(mDataList.get(position).getLockName());
        for (int i = 0; i< UserLockActivity.ROOMS.size(); i++)
        {
            if (mDataList.get(position).getLockName().equals(UserLockActivity.ROOMS.get(i).LockName))
            {
                _holder.room.setText(String.valueOf(UserLockActivity.ROOMS.get(i).RoomNumber));
                break ;
            }
            else
            {
                _holder.room.setText("Empty");
            }
        }
        _holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (_holder.room.getText().toString().equals("Empty") || _holder.room.getText().toString().equals(String.valueOf(1)))
                {
                    AlertDialog.Builder d = new AlertDialog.Builder(_holder.itemView.getContext());
                    d.setTitle("Select Lock Name "+mDataList.get(position).getLockName());
                    d.setMessage("Do you Want To Select The Lock " + mDataList.get(position).getLockName());
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
                            dialog.dismiss();
                            String url = "https://bait-elmoneh.online/hotel-service/setLockName.php" ;
                            final lodingDialog loading = new lodingDialog(_holder.itemView.getContext());
                            StringRequest re = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response)
                                {
                                    loading.stop();
                                    if (response.equals("1"))
                                    {
                                        //LogIn.myLock = mDataList.get(position);
                                        //LogIn.room.insertLock(LogIn.myLock.getLockName());
                                        //ToastMaker.MakeToast(mDataList.get(position).getLockName()+ " Lock Selected", _holder.itemView.getContext());
                                    }
                                    else
                                    {
                                        //ToastMaker.MakeToast("Lock Save Failed " + mDataList.get(position).getLockName(),mContext);
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    loading.stop();
                                }
                            })
                            {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError
                                {
                                    Map<String,String> Params = new HashMap<String, String>();
                                    //Params.put("id" , String.valueOf( LogIn.room.getRoomDBid()));
                                    Params.put("Lock" , mDataList.get(position).getLockName());
                                    return Params;
                                }
                            };
                            Volley.newRequestQueue(_holder.itemView.getContext()).add(re);
                        }
                    });
                    d.create().show();
                }
                else
                {
                    //ToastMaker.MakeToast("this Lock is already Taken For Another Room" , _holder.itemView.getContext());
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

        TextView lock ,room ;

        public DeviceViewHolder(View itemView)
        {
            super(itemView);
            lock = (TextView) itemView.findViewById(R.id.tv_lock_name);
            room = (TextView) itemView.findViewById(R.id.room_lockadapter);
        }

    }

}
