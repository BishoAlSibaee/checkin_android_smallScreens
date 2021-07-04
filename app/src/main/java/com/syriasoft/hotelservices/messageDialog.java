package com.syriasoft.hotelservices;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class messageDialog

{
     String title ;
     String message ;
     Context c ;
    Dialog d ;

    public messageDialog(String message, String title, Context c)
    {
        this.message = message ;
        this.title = title ;
        this.c = c ;

        d = new Dialog(c);
        d.setContentView(R.layout.message_dialog);

        TextView t = (TextView) d.findViewById(R.id.messageDialog_title);
        t.setText(title);
        TextView m = (TextView) d.findViewById(R.id.messageDialog_message);
        m.setText(message);
        Button b = (Button) d.findViewById(R.id.messageDialog_ok);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.show();


    }
}
