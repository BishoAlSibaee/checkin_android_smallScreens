package com.syriasoft.hotelservices;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RestaurantOrderAdapter extends RecyclerView.Adapter<RestaurantOrderAdapter.Holder> {

    List<RestaurantOrderItem> list ;
    Context c ;

    RestaurantOrderAdapter(List<RestaurantOrderItem> list , Context c) {
        this.list = list ;
        this.c = c ;
    }

    @NonNull
    @Override
    public RestaurantOrderAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_order_unit , null);
        Holder holder = new Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RestaurantOrderAdapter.Holder holder, @SuppressLint("RecyclerView") final int position) {
        holder.name.setText(list.get(position).name);
        holder.quantity.setText(String.valueOf(list.get(position).quantity));
        holder.price.setText(String.valueOf(list.get(position).price));
        holder.total.setText(String.valueOf(list.get(position).price * list.get(position).quantity));
        Picasso.get().load(list.get(position).photo).fit().into(holder.photo);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FullscreenActivity.order.removeItem(list.get(position).id , list.get(position).quantity)) {
                    Cart.x=0;
                    list = FullscreenActivity.order.getItems();
                    Cart.adapter = new RestaurantOrderAdapter(list , c);
                    Cart.itemsGridView.setAdapter(Cart.adapter);
                    ToastMaker.MakeToast("Deleted", c);
                    Cart.setTotal();
                    Cart.refreshItems();
                    if (FullscreenActivity.order.getItems().size() == 0 ) {
                        RestaurantActivity.items.setText("");
                    }
                    else {
                        RestaurantActivity.items.setText(String.valueOf(FullscreenActivity.order.getItems().size()));
                    }

                }
                else {
                    ToastMaker.MakeToast("Not Deleted", c);
                }

            }
        });
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart.x=0;
                Dialog d = new Dialog(holder.itemView.getContext());
                d.setContentView(R.layout.modify_restaurant_item);
                Window w = d.getWindow();
                w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView PRICE = d.findViewById(R.id.item_price);
                TextView TOTAL = d.findViewById(R.id.item_total);
                EditText NEWQ = d.findViewById(R.id.new_item_quantity);
                NEWQ.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (NEWQ.getText() == null || NEWQ.getText().toString().isEmpty() ) {
                            ToastMaker.MakeToast("Enter New Quantity",holder.itemView.getContext());
                            return;
                        }
                        double tot = Double.parseDouble(NEWQ.getText().toString()) *  list.get(position).price ;
                        TOTAL.setText(String.valueOf(tot));
                    }
                });
                Button CANCEL = d.findViewById(R.id.cancel_modify_item);
                Button MODIFY = d.findViewById(R.id.modify_item_btn);
                PRICE.setText(String.valueOf(list.get(position).price));
                NEWQ.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count)
                    {
                        TOTAL.setText(String.valueOf(Integer.parseInt(NEWQ.getText().toString()) * list.get(position).price ));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                CANCEL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                MODIFY.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        if (NEWQ.getText().toString() == null || NEWQ.getText().toString().length() == 0 )
                        {
                            ToastMaker.MakeToast("Enter New Quantity",holder.itemView.getContext());
                        }
                        else
                        {
                            Log.d("modify" , "not modified");
                            if (FullscreenActivity.order.modifyItem(list.get(position).id,Integer.parseInt(NEWQ.getText().toString())))
                            {
                                holder.quantity.setText(NEWQ.getText().toString());
                                holder.total.setText(TOTAL.getText().toString());
                                Cart.setTotal();
                                Cart.refreshItems();
                                d.dismiss();
                            }
                        }
                    }
                });
                d.show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart.x=0;
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView name;
        TextView quantity;
        TextView price ;
        TextView total ;
        ImageView photo ;
        Button delete;
        Button update ;

        public Holder(@NonNull View itemView) {
            super(itemView);
             name = (TextView) itemView.findViewById(R.id.restaurantOrderUnit_name);
             quantity = (TextView) itemView.findViewById(R.id.restaurantOrderUnit_quantity);
             price = (TextView) itemView.findViewById(R.id.restaurantOrderUnit_price);
             total = (TextView) itemView.findViewById(R.id.restaurantOrderUnit_total);
             photo= (ImageView) itemView.findViewById(R.id.restaurantOrderUnit_photo);
             delete = (Button) itemView.findViewById(R.id.button4);
             update = (Button) itemView.findViewById(R.id.button2);
        }
    }
}
