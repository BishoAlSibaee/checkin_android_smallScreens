package com.syriasoft.hotelservices;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.List;

public class DoubleControlSecond_Adapter extends RecyclerView.Adapter<DoubleControlSecond_Adapter.HOLDER> {

    List<DeviceBean> list ;
    DoubleControlSecond_Adapter(List<DeviceBean> list){
        this.list = list ;
    }

    @NonNull
    @Override
    public DoubleControlSecond_Adapter.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.double_control_unit,parent,false);
        DoubleControlSecond_Adapter.HOLDER holder = new DoubleControlSecond_Adapter.HOLDER(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull DoubleControlSecond_Adapter.HOLDER holder, int position) {
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
                if (LightingDoubleControl.FIRST != null ) {
                    if (list.get(position) == LightingDoubleControl.FIRST) {
                        ToastMaker.MakeToast("don't select same first device " ,holder.itemView.getContext());
                        return;
                    }
                    else {
                        LightingDoubleControl.SECOND = list.get(position);
                    }
                }
                else {
                    ToastMaker.MakeToast("please select first device  " ,holder.itemView.getContext());
                }

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
