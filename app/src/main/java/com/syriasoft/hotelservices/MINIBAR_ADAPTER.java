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

public class MINIBAR_ADAPTER extends RecyclerView.Adapter<MINIBAR_ADAPTER.HOLDER>
{

    List<MINIBARITEM> list = new ArrayList<MINIBARITEM>();

    public MINIBAR_ADAPTER(List<MINIBARITEM> list)
    {
        this.list = list;
    }

    @NonNull
    @Override
    public MINIBAR_ADAPTER.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.minibar_menu_unit,parent ,false);
        MINIBAR_ADAPTER.HOLDER holder = new MINIBAR_ADAPTER.HOLDER(row);
        return holder ;
    }

    @Override
    public void onBindViewHolder(@NonNull MINIBAR_ADAPTER.HOLDER holder, int position)
    {
        holder.desc.setText(list.get(position).Name);
        holder.price.setText(String.valueOf(list.get(position).price));
        Picasso.get().load(list.get(position).photo).fit().into(holder.image);
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public class HOLDER extends RecyclerView.ViewHolder
    {
        ImageView image ;
        TextView desc , price ;

        public HOLDER(@NonNull View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.minibarunit_image);
            desc = (TextView) itemView.findViewById(R.id.minibarunite_desc);
            price = (TextView) itemView.findViewById(R.id.minibarunit_price);
        }
    }
}
