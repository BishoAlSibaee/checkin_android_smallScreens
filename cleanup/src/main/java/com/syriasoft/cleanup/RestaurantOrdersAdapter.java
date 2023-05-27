package com.syriasoft.cleanup;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RestaurantOrdersAdapter extends BaseAdapter {

    List<restaurant_order_unit> list = new ArrayList<restaurant_order_unit>();
    LayoutInflater inflater;

    public RestaurantOrdersAdapter(List<restaurant_order_unit> list, Context c) {
        this.list = list;
        inflater = (LayoutInflater.from(c));

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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        RestaurantOrders.SELECTED_ORDER = list.get(position);
        convertView = inflater.inflate(R.layout.restaurant_order_unit, null);

        TextView room = convertView.findViewById(R.id.roomNumber_restOrder);
        TextView orderNumber = convertView.findViewById(R.id.orderNumber);
        ImageView img = convertView.findViewById(R.id.imageView3);
        img.setImageResource(R.drawable.restaurant_btn);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(list.get(position).dateTime);
        room.setText(String.valueOf(list.get(position).room));
        orderNumber.setText("Order No " + String.valueOf(list.get(position).id));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < RestaurantOrders.Rooms.size(); i++) {
                    if (RestaurantOrders.Rooms.get(i).RoomNumber == list.get(position).room) {
                        RestaurantOrders.SELECTED_ROOM = RestaurantOrders.Rooms.get(i) ;
                        break;
                    }
                }
                Intent i = new Intent(parent.getContext(), RestaurantOrderItems.class);
                i.putExtra("id", list.get(position).id);
                i.putExtra("room", list.get(position).room);
                i.putExtra("total", list.get(position).total);
                i.putExtra("dateTime", list.get(position).dateTime);
                i.putExtra("status", list.get(position).status);
                i.putExtra("RorS", list.get(position).RorS);
                i.putExtra("Reservation", list.get(position).Reservation);
                parent.getContext().startActivity(i);
            }
        });

        return convertView;
    }
}
