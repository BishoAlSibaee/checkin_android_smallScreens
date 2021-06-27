package com.syriasoft.laundryscreen;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ServiceOrder_Adapter extends BaseAdapter {

    List<ServiceOrder> list = new ArrayList<ServiceOrder>();
    LayoutInflater inflter;

    ServiceOrder_Adapter(List<ServiceOrder> list , Context c)
    {
        this.list = list ;
        inflter = (LayoutInflater.from(c));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflter.inflate(R.layout.order_unit, null);

        TextView room = convertView.findViewById(R.id.unit_roomNumber);
        room.setText(list.get(position).roomNumber);

        return convertView;
    }
}
