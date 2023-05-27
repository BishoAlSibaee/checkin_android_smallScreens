package com.syriasoft.cleanup;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MinibarItems_Adapter extends RecyclerView.Adapter<MinibarItems_Adapter.HOLDER> {
    List<MINIBARITEM> list = new ArrayList<MINIBARITEM>();
    public static boolean[] checkList;
    String[] quanList;
    ArrayAdapter<String> adapter;
    Context c;
    public static int[] ItemsQuantiy;

    public MinibarItems_Adapter(List<MINIBARITEM> list, Context c) {
        this.list = list;
        this.c = c;
        checkList = new boolean[this.list.size()];
        ItemsQuantiy = new int[this.list.size()];
        for (int i = 0; i < checkList.length; i++) {
            checkList[i] = false;
        }
        quanList = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        adapter = new ArrayAdapter<String>(c, R.layout.support_simple_spinner_dropdown_item, quanList);
    }

    @NonNull
    @Override
    public MinibarItems_Adapter.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.minibar_check_unit, null);
        HOLDER holder = new HOLDER(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MinibarItems_Adapter.HOLDER holder, final int position) {
        holder.name.setText(list.get(position).Name);
        Picasso.get().load(list.get(position).photo).into(holder.img);
        holder.check.setChecked(false);
        holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkList[position] = true;
                    holder.itemView.setBackgroundColor(Color.CYAN);
                    double total = list.get(position).price * Integer.parseInt(holder.quan.getSelectedItem().toString());
                    MINIBARITEM_ORDER item = new MINIBARITEM_ORDER(list.get(position).id, list.get(position).Hotel, list.get(position).Facility, list.get(position).Name, list.get(position).price, list.get(position).photo, Integer.parseInt(holder.quan.getSelectedItem().toString()), total);
                    MiniBarCheck.Used.add(item);
                    holder.quan.setEnabled(false);
                } else {
                    checkList[position] = false;
                    holder.itemView.setBackground(null);
                    double total = list.get(position).price * Integer.parseInt(holder.quan.getSelectedItem().toString());
                    MINIBARITEM_ORDER item = new MINIBARITEM_ORDER(list.get(position).id, list.get(position).Hotel, list.get(position).Facility, list.get(position).Name, list.get(position).price, list.get(position).photo, Integer.parseInt(holder.quan.getSelectedItem().toString()), total);
                    MiniBarCheck.Used.remove(item);
                    holder.quan.setEnabled(true);
                }
            }
        });

        holder.quan.setAdapter(adapter);

        holder.quan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int positionn, long id) {
                ItemsQuantiy[position] = Integer.parseInt(quanList[positionn]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (checkList[position]) {
            holder.itemView.setBackgroundColor(Color.CYAN);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HOLDER extends RecyclerView.ViewHolder {
        TextView name;
        ImageView img;
        CheckBox check;
        Spinner quan;

        public HOLDER(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView6);
            img = itemView.findViewById(R.id.imageView4);
            check = itemView.findViewById(R.id.checkBox);
            quan = itemView.findViewById(R.id.quantity_spinner);
        }
    }
}
