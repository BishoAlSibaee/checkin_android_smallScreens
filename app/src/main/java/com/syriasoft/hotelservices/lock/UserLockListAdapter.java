package com.syriasoft.hotelservices.lock;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
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
import com.syriasoft.hotelservices.LogIn;
import com.syriasoft.hotelservices.MyApp;
import com.syriasoft.hotelservices.R;
import com.syriasoft.hotelservices.ToastMaker;
import com.syriasoft.hotelservices.LoadingDialog;
import com.syriasoft.hotelservices.messageDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

;import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

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
        View mView = LayoutInflater.from(mContext).inflate(com.syriasoft.hotelservices.R.layout.user_lock_list_item, parent, false);
        return new DeviceViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder _holder, @SuppressLint("RecyclerView") final int position)
    {

        _holder.lock.setText(mDataList.get(position).getLockName());
        for (int i=0;i<UserLockActivity.ROOMS.size();i++)
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
                if (_holder.room.getText().toString().equals("Empty") || _holder.room.getText().toString().equals(String.valueOf(MyApp.Room.RoomNumber)))
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
                            //mDataList.get(position).setLockName(LogIn.room.getRoomNumber()+"Lock");
                            String url = LogIn.URL+"setLockName.php" ;
                            LoadingDialog loading = new LoadingDialog(_holder.itemView.getContext());
                            StringRequest re = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response)
                                {
                                    loading.stop();
                                    if (response.equals("1"))
                                    {
                                        LogIn.myLock = mDataList.get(position);
                                        LogIn.myLock.setLockName(MyApp.Room.RoomNumber+"Lock");
                                        mDataList.get(position).getLockId();
                                        //LogIn.room.insertLock(LogIn.myLock.getLockName());
                                        Log.d("lockData",String.valueOf(mDataList.get(position).getSpecialValue()));
                                        ToastMaker.MakeToast(mDataList.get(position).getLockName()+ " Lock Selected", _holder.itemView.getContext());
                                    }
                                    else
                                    {
                                        ToastMaker.MakeToast("Lock Save Failed " + mDataList.get(position).getLockName(),mContext);
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
                                    Params.put("id" , String.valueOf(MyApp.Room.id));
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
                    ToastMaker.MakeToast("this Lock is already Taken For Another Room" , _holder.itemView.getContext());
                }

            }
        });

        _holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(_holder.itemView.getContext());
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Calendar c = Calendar.getInstance(Locale.getDefault());
                        ApiService apiService = RetrofitAPIManager.provideClientApi();
                        Call<ResponseBody> call = apiService.deleteLock(ApiService.CLIENT_ID,LogIn.acc.getAccess_token(),mDataList.get(position).getLockId(),c.getTimeInMillis());
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                                Log.d("deleteLockResponse" , response.message()+" "+response.isSuccessful());
                                com.syriasoft.hotelservices.messageDialog m = new messageDialog("Lock Deleted","Lock Deleted",_holder.itemView.getContext());
                                UserLockActivity.lockList();
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.d("deleteLockResponse" , t.getMessage() );
                                com.syriasoft.hotelservices.messageDialog m = new messageDialog("Error .. ","Lock Can not delete",_holder.itemView.getContext());
                            }
                        });
                    }
                });
                builder.setTitle("Delete Lock ..?");
                builder.setMessage("Are you sure to delete " + mDataList.get(position).getLockName()+" ..?");
                builder.create();
                builder.show();
                return false;
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
            lock = (TextView) itemView.findViewById(com.syriasoft.hotelservices.R.id.tv_lock_name);
            room = (TextView) itemView.findViewById(R.id.room_lockadapter);
        }

    }

}
