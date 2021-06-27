package com.syriasoft.laundryscreen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class OrdersAdapter extends BaseAdapter

{
    private final Context mContext;
    static List<Order>  orders = new ArrayList<Order>();

    LayoutInflater inflter;

    public OrdersAdapter(Context mContext, List<Order> orders) {
        this.mContext = mContext;
        this.orders = orders;
        inflter = (LayoutInflater.from(mContext));
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        convertView = inflter.inflate(R.layout.order_unit, null);
        TextView dummyTextView = convertView.findViewById(R.id.unit_roomNumber);
        TextView caption = convertView.findViewById(R.id.xxx);
        dummyTextView.setText(orders.get(position).roomNumber);
        caption.setText(orders.get(position).caption);
        return convertView;
    }
}
