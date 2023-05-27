package com.syriasoft.cleanup;

import android.app.Dialog;
import android.content.Context;

public class LoadingDialog {
    Context c;
    Dialog d;

    LoadingDialog(Context c) {
        this.c = c;
        d = new Dialog(c);
        d.setContentView(R.layout.loading_dialog);
        d.show();
    }

    void close() {
        d.dismiss();
    }


}
