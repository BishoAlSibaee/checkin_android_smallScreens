package com.syriasoft.cleanup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RestaurantOrders_ADAPTER extends RecyclerView.Adapter<RestaurantOrders_ADAPTER.HOLDER> {

    List<restaurant_order_unit> list;

    public RestaurantOrders_ADAPTER(List<restaurant_order_unit> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RestaurantOrders_ADAPTER.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_order_unit, null);
        HOLDER holder = new HOLDER(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantOrders_ADAPTER.HOLDER holder, int position) {
        holder.room.setText(list.get(position).room);
        holder.order.setText(list.get(position).id);
        holder.img.setImageResource(R.drawable.restaurant_btn);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HOLDER extends RecyclerView.ViewHolder {
        TextView order, room;
        ImageView img;

        public HOLDER(@NonNull View itemView) {
            super(itemView);
            order = itemView.findViewById(R.id.orderNumber);
            room = itemView.findViewById(R.id.roomNumber_restOrder);
            img = itemView.findViewById(R.id.imageView3);
        }
    }
}
