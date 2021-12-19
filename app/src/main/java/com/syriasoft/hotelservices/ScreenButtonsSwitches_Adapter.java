package com.syriasoft.hotelservices;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;

public class ScreenButtonsSwitches_Adapter extends RecyclerView.Adapter<ScreenButtonsSwitches_Adapter.HOLDER> {

    List<DeviceBean> list ;

    ScreenButtonsSwitches_Adapter(List<DeviceBean> list) {
        this.list = list ;
    }

    @NonNull
    @Override
    public ScreenButtonsSwitches_Adapter.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.double_control_unit,parent,false);
        ScreenButtonsSwitches_Adapter.HOLDER holder = new ScreenButtonsSwitches_Adapter.HOLDER(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ScreenButtonsSwitches_Adapter.HOLDER holder, int position) {
        holder.dp.setVisibility(View.GONE);
        holder.name.setText(list.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenButtons.SelectedSwitch = list.get(position);
                ScreenButtons.Buttons.clear();
                List keys = new ArrayList(list.get(position).getDps().keySet());
                if (keys.contains("1")) {
                    ScreenButtons.Buttons.add("1") ;
                }
                if (keys.contains("2")) {
                    ScreenButtons.Buttons.add("2") ;
                }
                if (keys.contains("3")) {
                    ScreenButtons.Buttons.add("3") ;
                }
                if (keys.contains("4")) {
                    ScreenButtons.Buttons.add("4") ;
                }
                ScreenButtons.ButtonsAdapter = new ScreenButtonsButtons_Adapter(ScreenButtons.Buttons);
                ScreenButtons.SwitchesButtons.setAdapter(ScreenButtons.ButtonsAdapter);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HOLDER extends RecyclerView.ViewHolder {
        TextView name , dp;
        public HOLDER(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.dev_name);
            dp = (TextView) itemView.findViewById(R.id.dev_dps);
        }
    }
}
