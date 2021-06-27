package com.syriasoft.hotelservices;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class LAUNDRYMENU_ADAPTER extends RecyclerView.Adapter<LAUNDRYMENU_ADAPTER.HOLDER>
{

    List<LAUNDRYITEM> list = new ArrayList<LAUNDRYITEM>();

    public LAUNDRYMENU_ADAPTER(List<LAUNDRYITEM> list)
    {
        this.list = list;
    }

    @NonNull
    @Override
    public LAUNDRYMENU_ADAPTER.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_menu_unit,parent ,false);
        LAUNDRYMENU_ADAPTER.HOLDER holder = new LAUNDRYMENU_ADAPTER.HOLDER(row);
        return holder ;
    }

    @Override
    public void onBindViewHolder(@NonNull LAUNDRYMENU_ADAPTER.HOLDER holder, int position)
    {
        Picasso.get().load(list.get(position).image).fit().into(holder.image);
        holder.desc.setText(list.get(position).desc);
        holder.price.setText(list.get(position).price);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class HOLDER extends RecyclerView.ViewHolder
    {
        ImageView image ;
        TextView desc ;
        TextView price ;
        public HOLDER(@NonNull View itemView)
        {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.laundryMenuUnit_image);
            desc = (TextView) itemView.findViewById(R.id.laundryMenuUnit_desc);
            price = (TextView) itemView.findViewById(R.id.laundryMenuUnit_price);
        }
    }
}
