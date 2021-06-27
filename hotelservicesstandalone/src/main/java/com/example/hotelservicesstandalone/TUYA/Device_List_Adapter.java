package com.example.hotelservicesstandalone.TUYA;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelservicesstandalone.R;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;

public class Device_List_Adapter  extends RecyclerView.Adapter<Device_List_Adapter.Holder> {

    List<DeviceBean> list = new ArrayList<DeviceBean>();

    public Device_List_Adapter(List<DeviceBean> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.family_module , parent , false);
        Holder h = new Holder(v);
        return h ;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position)
    {
        /*
        holder.Name.setText(list.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Tuya_Devices.selectedDevics.setText(list.get(position).name);

                if (list.get(position).name.equals(LogIn.room.getRoomNumber()+"Power") )
                {
                    Tuya_Devices.powerBean = list.get(position) ;
                    Tuya_Devices.mDevice = TuyaHomeSdk.newDeviceInstance(Tuya_Devices.powerBean.devId);
                    ToastMaker.MakeToast("Power selected" , holder.itemView.getContext());
                }
                else if (list.get(position).name.equals(LogIn.room.getRoomNumber()+"ZGatway"))
                {
                    Tuya_Devices.zgatwayBean = list.get(position) ;
                    Tuya_Devices.mgate = TuyaHomeSdk.newGatewayInstance(Tuya_Devices.zgatwayBean.devId);
                    LogIn.room.insertGateway(list.get(position).name);
                    ToastMaker.MakeToast("Gate Selected" , holder.itemView.getContext());
                }
                else if (list.get(position).name.equals(LogIn.room.getRoomNumber()+"AC"))
                {
                    Tuya_Devices.ACbean = list.get(position) ;
                    Tuya_Devices.AC = TuyaHomeSdk.newDeviceInstance(Tuya_Devices.ACbean.devId);
                    Log.d("ACdps" , Tuya_Devices.ACbean.dps.toString() + Tuya_Devices.ACbean.panelConfig.toString());
                    //Log.d("ACdps" , Tuya_Devices.ACbean.panelConfig.toString());
                    //ToastMaker.MakeToast(Tuya_Devices.ACbean.dps.toString(),holder.itemView.getContext());
                    ToastMaker.MakeToast("AC Controller selected" , holder.itemView.getContext());
                }
                else
                {
                        ToastMaker.MakeToast("Device Not Detected" , holder.itemView.getContext());
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                Dialog d = new Dialog(holder.itemView.getContext());
                d.setContentView(R.layout.confermation_dialog);
                TextView heading = (TextView) d.findViewById(R.id.textView2);
                heading.setText("Conferm Device Deleting ");
                TextView text = (TextView) d.findViewById(R.id.confermationDialog_Text) ;
                text.setText("Are You Sure .. ? ");
                Button cancel = (Button) d.findViewById(R.id.confermationDialog_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                Button ok = (Button) d.findViewById(R.id.messageDialog_ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        TuyaHomeSdk.newDeviceInstance(list.get(position).getDevId()).removeDevice(new IResultCallback() {
                            @Override
                            public void onError(String errorCode, String errorMsg)
                            {
                                //ToastMaker.MakeToast(errorMsg , holder.itemView.getContext());
                                Calendar c = Calendar.getInstance(Locale.getDefault());
                                long time =c.getTimeInMillis() ;
                                ErrorRegister.rigestError(holder.itemView.getContext(),LogIn.room.getProjectName(),LogIn.room.getRoomNumber(),time,18,errorMsg,"Error Deleting Tuya Device");
                            }
                            @Override
                            public void onSuccess()
                            {
                                ToastMaker.MakeToast("Device Removed" , holder.itemView.getContext());
                                d.dismiss();
                            }

                        });
                    }
                });
                d.show();

                return false ;
            }
        });

         */
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }


    class Holder extends RecyclerView.ViewHolder {

        TextView Name ;

        public Holder(@NonNull View itemView) {
            super(itemView);
            Name = (TextView) itemView.findViewById(R.id.Tuya_familyModule_name);
        }
    }
}
