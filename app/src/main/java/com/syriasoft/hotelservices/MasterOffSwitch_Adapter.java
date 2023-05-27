package com.syriasoft.hotelservices;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;

public class MasterOffSwitch_Adapter extends RecyclerView.Adapter<MasterOffSwitch_Adapter.HOLDER> {

    List<DeviceBean> list ;

    MasterOffSwitch_Adapter(List<DeviceBean> list) {
        this.list = list ;
    }

    @NonNull
    @Override
    public MasterOffSwitch_Adapter.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.double_control_unit,parent,false);
        MasterOffSwitch_Adapter.HOLDER holder = new MasterOffSwitch_Adapter.HOLDER(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MasterOffSwitch_Adapter.HOLDER holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(list.get(position).getName());
        if (list.get(position).getDps().keySet().contains("1") && list.get(position).getDps().keySet().contains("2") && list.get(position).getDps().keySet().contains("3") && list.get(position).getDps().keySet().contains("4")) {
            holder.dps.setText("4 Buttons");
        }
        else if (list.get(position).getDps().keySet().contains("1") && list.get(position).getDps().keySet().contains("2") && list.get(position).getDps().keySet().contains("3")) {
            holder.dps.setText("3 Buttons");
        }
        else if (list.get(position).getDps().keySet().contains("1") && list.get(position).getDps().keySet().contains("2")) {
            holder.dps.setText("2 Buttons");
        }
        else if (list.get(position).getDps().keySet().contains("1") ) {
            holder.dps.setText("1 Buttons");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MasterOff.Buttons.clear();
                MasterOff.SelectedSwitch = list.get(position);
                List keys = new ArrayList(list.get(position).getDps().keySet()) ;
                if (keys.contains("1")) {
                    MasterOff.Buttons.add("1") ;
                }
                if (keys.contains("2")) {
                    MasterOff.Buttons.add("2") ;
                }
                if (keys.contains("3")) {
                    MasterOff.Buttons.add("3") ;
                }
                if (keys.contains("4")) {
                    MasterOff.Buttons.add("4") ;
                }
                MasterOff.Badapter = new MasterOffButton_Adapter(MasterOff.Buttons);
                MasterOff.ButtonsRecycler.setAdapter(MasterOff.Badapter);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HOLDER extends RecyclerView.ViewHolder {
        TextView name , dps ;
        public HOLDER(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.dev_name);
            dps = (TextView) itemView.findViewById(R.id.dev_dps);
        }
    }
}
