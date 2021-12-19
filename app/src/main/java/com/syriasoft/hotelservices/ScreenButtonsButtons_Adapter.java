package com.syriasoft.hotelservices;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScreenButtonsButtons_Adapter extends RecyclerView.Adapter<ScreenButtonsButtons_Adapter.HOLDER> {

    List<String> list ;

    ScreenButtonsButtons_Adapter(List<String> list) {
        this.list = list ;
    }

    @NonNull
    @Override
    public ScreenButtonsButtons_Adapter.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.double_control_unit,parent,false);
        HOLDER holder = new HOLDER(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ScreenButtonsButtons_Adapter.HOLDER holder, int position) {
            holder.switche.setVisibility(View.GONE);
            holder.button.setText(list.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder b = new AlertDialog.Builder(holder.itemView.getContext());
                    final EditText input = new EditText(holder.itemView.getContext());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    b.setView(input);
                    b.setTitle("Confirm ").setMessage("please Confirm Adding Button")
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (input.getText() == null || input.getText().toString().isEmpty()) {
                                ToastMaker.MakeToast("please enter button name",holder.itemView.getContext());
                            }
                            else {
                                if (ScreenButtons.SelectedSwitch != null ) {
                                    String xx = ScreenButtons.SelectedSwitch.getName().split("Switch")[1];
                                    if (FullscreenActivity.lightsDB.insertButtonToScreen(Integer.parseInt(xx),Integer.parseInt(list.get(position)),input.getText().toString())) {
                                        ToastMaker.MakeToast("button added successfully",holder.itemView.getContext());
                                        dialog.dismiss();
                                    }
                                    else {
                                        ToastMaker.MakeToast("error .. not saved",holder.itemView.getContext());
                                    }

                                }
                            }
                        }
                    }).create().show();
                }
            });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HOLDER extends RecyclerView.ViewHolder {
        TextView switche , button ;
        public HOLDER(@NonNull View itemView) {
            super(itemView);
            switche = (TextView) itemView.findViewById(R.id.dev_name);
            button = (TextView) itemView.findViewById(R.id.dev_dps);
        }
    }
}
