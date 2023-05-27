package com.syriasoft.cleanup;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class messageDialog {
    private String title;
    private String message;
    private Context c;
    Dialog d;

    messageDialog(String message, String title, Context c) {
        this.message = message;
        this.title = title;
        this.c = c;
        d = new Dialog(c);
        d.setContentView(R.layout.message_dialog);
        Window w = d.getWindow();
        w.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView t = d.findViewById(R.id.messageDialog_title);
        t.setText(title);
        TextView m = d.findViewById(R.id.messageDialog_message);
        m.setText(message);
        Button b = d.findViewById(R.id.messageDialog_ok);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }
}
