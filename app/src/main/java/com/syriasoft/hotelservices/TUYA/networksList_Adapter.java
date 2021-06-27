package com.syriasoft.hotelservices.TUYA;

import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syriasoft.hotelservices.R;

import java.util.List;

public class networksList_Adapter  extends RecyclerView.Adapter<networksList_Adapter.Holder> {

    List<ScanResult> wifiList;

    public networksList_Adapter(List<ScanResult> wifiList) {
        this.wifiList = wifiList;
    }

    @NonNull
    @Override
    public networksList_Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.family_module , parent , false);
        networksList_Adapter.Holder h = new networksList_Adapter.Holder(v);
        return h ;
    }

    @Override
    public void onBindViewHolder(@NonNull networksList_Adapter.Holder holder, int position)
    {
        holder.Name.setText(wifiList.get(position).SSID);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Tuya_Devices.selectedNetwork = wifiList.get(position).SSID ;
                Tuya_Devices.Name.setText(wifiList.get(position).SSID);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wifiList.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView Name ;

        public Holder(@NonNull View itemView) {
            super(itemView);
            Name = (TextView) itemView.findViewById(R.id.Tuya_familyModule_name);
        }
    }
}
