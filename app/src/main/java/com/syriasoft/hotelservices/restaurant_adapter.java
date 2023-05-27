package com.syriasoft.hotelservices;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class restaurant_adapter extends RecyclerView.Adapter<restaurant_adapter.restaurantholder> {
    List<restaurant_item> list = new ArrayList<restaurant_item>();
    Context c ;
    restaurant_adapter (List<restaurant_item> list , Context c)
    {
        this.list = list ;
        this.c = c;
    }


    @NonNull
    @Override
    public restaurant_adapter.restaurantholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_unit,parent ,false);
        restaurantholder holder = new restaurantholder(row);
        return holder ;
    }



    @Override
    public void onBindViewHolder(final restaurant_adapter.restaurantholder holder, @SuppressLint("RecyclerView") final int position)
    {
        holder.quantity.setText("0");
        holder.total.setText("0.0");
        holder.name.setText(list.get(position).Name);
        holder.price.setText(String.valueOf(list.get(position).Price));
        Picasso.get().load(list.get(position).photo).into(holder.photo);
        Log.e("image" , list.get(position).photo);
        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qu = Integer.parseInt(holder.quantity.getText().toString());
                qu++;
                holder.quantity.setText(String.valueOf(qu));
                holder.total.setText(String.valueOf(list.get(position).Price * Double.parseDouble( holder.quantity.getText().toString())));
                RestaurantActivity.x=0;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestaurantActivity.x=0;
            }
        });
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qu = Integer.parseInt(holder.quantity.getText().toString());
                if (qu == 0)
                {
                    qu = 0 ;
                }
                else
                {
                    qu--;
                }
                holder.quantity.setText(String.valueOf(qu));
                holder.total.setText(String.valueOf(list.get(position).Price * Double.parseDouble( holder.quantity.getText().toString())));
                RestaurantActivity.x=0;
            }
        });
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(holder.quantity.getText().toString()) == 0 ) {
                    ToastMaker.MakeToast("حدد الكمية" , holder.itemView.getContext());
                }
                else {
                        List<RestaurantOrderItem> listo = FullscreenActivity.order.getItems();
                        int Facility = 0 ;
                        if (listo.size()>0) {
                            Facility = listo.get(0).fac;
                        }
                        if (Facility != list.get(position).Facility) {
                            FullscreenActivity.order.removeOrder();
                        }
                        if (FullscreenActivity.order.insertOrder(list.get(position).id,list.get(position).Facility,list.get(position).Menu,list.get(position).Name,list.get(position).Desc,Integer.parseInt(holder.quantity.getText().toString()),list.get(position).Price ,list.get(position).Discount , Double.parseDouble(holder.total.getText().toString()),list.get(position).photo) ) {
                            holder.quantity.setText("0");
                            holder.total.setText("0.0");
                            ToastMaker.MakeToast("تمت الاضافة", holder.itemView.getContext());
                            RestaurantActivity.items.setText(String.valueOf(FullscreenActivity.order.getItems().size()));
                        }
                        else {
                            ToastMaker.MakeToast("لم تتم الاضافة", holder.itemView.getContext());
                        }
                    }
                RestaurantActivity.x=0;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public Bitmap convertBase64ToBitmap( String encodedImage)
    {
        byte[] decodedString =Base64.decode(encodedImage , Base64.DEFAULT) ;
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Bitmap x = getResizedBitmap(decodedByte,1000);

        return x ;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize)
    {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    class restaurantholder extends RecyclerView.ViewHolder
    {
        final TextView price ;
        final TextView name ;
        final TextView total ;
        Button decrease ;
        Button increase  ;
        ImageView photo ;
        final TextView quantity  ;
        Button addToCart ;

        public restaurantholder(@NonNull View itemView) {
            super(itemView);
            price = (TextView) itemView.findViewById(R.id.restaurant_unit_price);
            name  = (TextView) itemView.findViewById(R.id.restaurant_unit_name);
            total  = (TextView) itemView.findViewById(R.id.restaurant_unit_total);
            photo = (ImageView) itemView.findViewById(R.id.MealPhoto);
            decrease  = (Button) itemView.findViewById(R.id.minuse_btn);
            increase  = (Button) itemView.findViewById(R.id.add_btn);
            quantity  = (TextView) itemView.findViewById(R.id.quantity_text);
            addToCart = (Button) itemView.findViewById(R.id.add_to_cart);
        }


    }
}
