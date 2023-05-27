package com.syriasoft.hotelservices;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RESTAURANT_MENUS_ADAPTER extends RecyclerView.Adapter<RESTAURANT_MENUS_ADAPTER.HOLDER> {

    List<Menu> list;

    public RESTAURANT_MENUS_ADAPTER(List<Menu> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RESTAURANT_MENUS_ADAPTER.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_menues_unit,parent,false);
        return new HOLDER(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RESTAURANT_MENUS_ADAPTER.HOLDER holder, @SuppressLint("RecyclerView") int position) {
        holder.text.setText(list.get(position).name);
        holder.arabic.setText(list.get(position).arabic);
        holder.itemView.setOnClickListener(v -> {
            RestaurantMenues.H.removeCallbacks(RestaurantMenues.backHomeThread);
            RestaurantMenues.x=0;
            Intent i = new Intent(holder.itemView.getContext() , RestaurantActivity.class);
            i.putExtra("id" , list.get(position).id);
            i.putExtra("photo" , list.get(position).photo);
            i.putExtra("name" , list.get(position).name);
            i.putExtra("arabic" , list.get(position).arabic);
            i.putExtra("Hotel" , list.get(position).Hotel);
            i.putExtra("Facility" , list.get(position).Facility);
            i.putExtra("Type" , RestaurantMenues.Type);
            holder.itemView.getContext().startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return list.size() ;
    }

    public static class HOLDER extends RecyclerView.ViewHolder {
        ImageView image ;
        TextView text , arabic ;

        public HOLDER(@NonNull View itemView) {
            super(itemView);
             image = itemView.findViewById(R.id.restaurantMenuesUnite_Photo);
             text = itemView.findViewById(R.id.restaurantMenuesUnite_Text);
             arabic = itemView.findViewById(R.id.restaurantMenuesUnite_ArabicText);
        }
    }
}
