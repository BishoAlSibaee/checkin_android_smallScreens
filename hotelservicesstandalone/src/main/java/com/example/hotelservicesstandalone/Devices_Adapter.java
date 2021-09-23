package com.example.hotelservicesstandalone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;

public class Devices_Adapter extends BaseAdapter {

    List<DeviceBean> list = new ArrayList<DeviceBean>();
    LayoutInflater inflater ;
    Context c ;

    Devices_Adapter(List<DeviceBean> list ,Context c ) {
        this.list = list ;
        this.c = c ;
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.device_unit , null);

        TextView name = (TextView) convertView.findViewById(R.id.deviceUnit_deviceName);
        ImageView local = (ImageView) convertView.findViewById(R.id.deviceUnit_local);
        ImageView net = (ImageView) convertView.findViewById(R.id.deviceUnit_net);
        ImageView cloud = (ImageView) convertView.findViewById(R.id.deviceUnit_cloud);

        name.setText(list.get(position).getName());
        if (list.get(position).getIsLocalOnline()) {
            local.setImageResource(android.R.drawable.presence_online);
        }
        else {
            local.setImageResource(android.R.drawable.ic_delete);
        }
        if (list.get(position).getIsOnline()) {
            net.setImageResource(android.R.drawable.presence_online);
        }
        else {
            net.setImageResource(android.R.drawable.ic_delete);
        }
        if (list.get(position).isCloudOnline()) {
            cloud.setImageResource(android.R.drawable.presence_online);
        }
        else {
            cloud.setImageResource(android.R.drawable.ic_delete);
        }

        return convertView;
    }
}
