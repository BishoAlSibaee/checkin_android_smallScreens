package com.syriasoft.hotelservices;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RestaurantMenuesAdapter extends BaseAdapter {

    List<Menu> list = new ArrayList<Menu>();
    LayoutInflater inflater ;

    RestaurantMenuesAdapter(List<Menu> list , Context c )
    {
        this.list = list ;

        inflater = LayoutInflater.from(c);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        convertView = inflater.inflate(R.layout.restaurant_menues_unit , null);

        ImageView image = (ImageView) convertView.findViewById(R.id.restaurantMenuesUnite_Photo);
        TextView text = (TextView) convertView.findViewById(R.id.restaurantMenuesUnite_Text);
        TextView arabic = (TextView) convertView.findViewById(R.id.restaurantMenuesUnite_ArabicText);
        Picasso.get().load(list.get(position).photo).into(image);
        text.setText(list.get(position).name);
        arabic.setText(list.get(position).arabic);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RESTAURANTS.H.removeCallbacks(RESTAURANTS.backHomeThread);
                RESTAURANTS.x=0;
                Intent i = new Intent(parent.getContext() , RestaurantActivity.class);
                i.putExtra("id" , list.get(position).id);
                i.putExtra("photo" , list.get(position).photo);
                i.putExtra("name" , list.get(position).name);
                i.putExtra("arabic" , list.get(position).arabic);
                i.putExtra("Hotel" , list.get(position).Hotel);
                i.putExtra("Facility" , list.get(position).Facility);
                i.putExtra("Type" , RestaurantMenues.Type);
                parent.getContext().startActivity(i);
            }
        });

        return convertView;
    }
    public Bitmap convertBase64ToBitmap(String encodedImage)
    {
        byte[] decodedString = Base64.decode(encodedImage , Base64.DEFAULT) ;
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte ;
    }
}
