package com.syriasoft.hotelservices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BreakfastAdapter extends BaseAdapter
{
    List<restaurant_item> list = new ArrayList<restaurant_item>();
    LayoutInflater inflter;
    BreakfastAdapter(List<restaurant_item> list , Context c)
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
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        convertView = inflter.inflate(R.layout.restaurant_unit, null);
        Button decrease = (Button) convertView.findViewById(R.id.minuse_btn);
        Button increase = (Button) convertView.findViewById(R.id.add_btn);
        final TextView quantity = (TextView) convertView.findViewById(R.id.quantity_text);
        Button addToCart = (Button) convertView.findViewById(R.id.add_to_cart);
        final TextView price = (TextView) convertView.findViewById(R.id.restaurant_unit_price);
        final TextView name = (TextView) convertView.findViewById(R.id.restaurant_unit_name);
        final TextView total = (TextView) convertView.findViewById(R.id.restaurant_unit_total);
        quantity.setText("0");
        name.setText(list.get(position).Name);
        price.setText(String.valueOf(list.get(position).Price));
        total.setText(String.valueOf(list.get(position).Price * Double.parseDouble( quantity.getText().toString())));
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qu = Integer.parseInt(quantity.getText().toString());
                qu++;
                quantity.setText(String.valueOf(qu));
                total.setText(String.valueOf(list.get(position).Price * Double.parseDouble( quantity.getText().toString())));
            }
        });
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qu = Integer.parseInt(quantity.getText().toString());
                if (qu == 0)
                {
                    qu = 0 ;
                }
                else
                    {
                        qu--;
                    }
                quantity.setText(String.valueOf(qu));
                total.setText(String.valueOf(list.get(position).Price * Double.parseDouble( quantity.getText().toString())));
            }
        });

        return convertView;
    }
}
