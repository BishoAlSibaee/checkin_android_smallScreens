package com.syriasoft.hotelservices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class ToastMaker
{
    public static void MakeToast(String message , Context c )
    {
        Toast t = new Toast(c);
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.big_toast, null);
        TextView m = (TextView) view.findViewById(R.id.toast_Text);
        ImageView i = (ImageView) view.findViewById(R.id.imageView);
        i.setImageResource(R.drawable.logo_android);
        m.setText(message);
        t.setView(view);
        t.show();
    }
}
