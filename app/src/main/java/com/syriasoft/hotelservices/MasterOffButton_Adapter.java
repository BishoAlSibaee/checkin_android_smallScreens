package com.syriasoft.hotelservices;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MasterOffButton_Adapter extends RecyclerView.Adapter<MasterOffButton_Adapter.HOLDER> {

    List<String> list ;

    MasterOffButton_Adapter(List<String> list) {
        this.list = list ;
    }

    @NonNull
    @Override
    public MasterOffButton_Adapter.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.double_control_unit,parent,false);
        MasterOffButton_Adapter.HOLDER holder = new MasterOffButton_Adapter.HOLDER(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MasterOffButton_Adapter.HOLDER holder, int position) {
        holder.name.setText(list.get(position));
        holder.dps.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MasterOff.SelectedButton = list.get(position);
                AlertDialog.Builder b = new AlertDialog.Builder(holder.itemView.getContext());
                b.setTitle("Confirm");
                b.setMessage("Please confirm " +MasterOff.SelectedSwitch.getName()+" Button Number "+MasterOff.SelectedButton);
                b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MasterOff.SelectedButton = null ;
                        dialog.dismiss();
                    }
                });
                b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (FullscreenActivity.lightsDB != null ) {
                            if (MasterOff.SelectedSwitch != null ) {
                                String[] vv =  MasterOff.SelectedSwitch.getName().split("Switch") ;
                                String sss = vv[1] ;
                                if (MasterOff.FirstButton.isChecked() && FullscreenActivity.lightsDB.getMasterOffButtons().size() == 0) {
                                    sss = sss+"0" ;
                                }
                                FullscreenActivity.lightsDB.insertButtonToMasterOff(Integer.parseInt(sss),Integer.parseInt(MasterOff.SelectedButton));
                                ToastMaker.MakeToast("Button Added To Master Off ",holder.itemView.getContext());
                                dialog.dismiss();
                            }
                            else {
                                ToastMaker.MakeToast("please select switch" , holder.itemView.getContext());
                            }

                        }
                    }
                });
                b.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HOLDER extends RecyclerView.ViewHolder {
        TextView name , dps ;
        public HOLDER(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.dev_name);
            dps = (TextView) itemView.findViewById(R.id.dev_dps);
        }
    }
}
