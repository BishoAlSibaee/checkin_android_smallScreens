package com.syriasoft.cleanup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Order_Items_Adapter extends BaseAdapter {
    List<OrderItem> list = new ArrayList<OrderItem>();
    LayoutInflater inflater;

    Order_Items_Adapter(List<OrderItem> list, Context c) {
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
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.item_unit, null);

        TextView name = convertView.findViewById(R.id.restaurantOrderItem_name);
        TextView quantity = convertView.findViewById(R.id.restaurantOrderItem_quantity);
        TextView price = convertView.findViewById(R.id.restaurantOrderItem_price);
        TextView total = convertView.findViewById(R.id.restaurantOrderItem_total);

        name.setText(list.get(position).name);
        quantity.setText(String.valueOf(list.get(position).quantity));
        price.setText(String.valueOf(list.get(position).price));
        total.setText(String.valueOf(list.get(position).total));

        return convertView;
    }
}
