package com.syriasoft.hotelservices;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RESTAURANTS_ADAPTER extends RecyclerView.Adapter<RESTAURANTS_ADAPTER.HOLDER>
{
    List<RESTAURANT_UNIT> list ;

    public RESTAURANTS_ADAPTER(List<RESTAURANT_UNIT> list) {
        this.list = list ;
    }


    @NonNull
    @Override
    public RESTAURANTS_ADAPTER.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurants_unite,parent,false);
        HOLDER holder = new HOLDER(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RESTAURANTS_ADAPTER.HOLDER holder, @SuppressLint("RecyclerView") int position) {

        if (list.get(position).photo == null || list.get(position).photo == "" || list.get(position).photo == "0") {

        }
        else {
            Picasso.get().load(list.get(position).photo).into(holder.image);
        }
        holder.text.setText(list.get(position).Name);
        holder.text0.setText(list.get(position).TypeName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("restaurantsAre", list.get(position).Name+" " );
                RESTAURANTS.H.removeCallbacks(RESTAURANTS.backHomeThread);
                RESTAURANTS.x=0;
                Intent i = new Intent(holder.itemView.getContext() , RestaurantMenues.class ) ;
                i.putExtra("id",list.get(position).id);
                i.putExtra( "Hotel" ,list.get(position).Hotel);
                i.putExtra( "TypeId",list.get(position).TypeId);
                i.putExtra("TypeName" , list.get(position).TypeName);
                i.putExtra("Control" , list.get(position).Control);
                i.putExtra("Name" , list.get(position).Name);
                i.putExtra("photo" , list.get(position).photo);
                holder.itemView.getContext().startActivity(i);
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(@NonNull HOLDER holder) {
        super.onViewAttachedToWindow(holder);
        RESTAURANTS.Current = holder.getPosition();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class HOLDER extends RecyclerView.ViewHolder {
        ImageView image ;
        TextView text , text0 ;
        public HOLDER(@NonNull View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.Restaurants_Unit_Text);
            text0 = (TextView) itemView.findViewById(R.id.Restaurant_Unit_Type);
            image = (ImageView) itemView.findViewById(R.id.Restaurants_Image);
        }
    }
}
