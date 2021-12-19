package com.syriasoft.hotelservices;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.List;

public class DoubleControlFirst_Adapter extends RecyclerView.Adapter<DoubleControlFirst_Adapter.HOLDER> {

    List<DeviceBean> list ;
    DoubleControlFirst_Adapter(List<DeviceBean> list){
        this.list = list ;
    }


    @NonNull
    @Override
    public DoubleControlFirst_Adapter.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.double_control_unit,parent,false);
        HOLDER holder = new HOLDER(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DoubleControlFirst_Adapter.HOLDER holder, int position) {

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
                LightingDoubleControl.FIRST = list.get(position);
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
