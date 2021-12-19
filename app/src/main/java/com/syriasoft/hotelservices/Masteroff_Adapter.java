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

public class Masteroff_Adapter extends RecyclerView.Adapter<Masteroff_Adapter.HOLDER> {


    List<MasterOffButton> list ;

    Masteroff_Adapter(List<MasterOffButton> list) {
        this.list = list ;
    }

    @NonNull
    @Override
    public Masteroff_Adapter.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.masteroff_unit,parent,false);
        HOLDER holder = new HOLDER(v);
        return holder ;
    }

    @Override
    public void onBindViewHolder(@NonNull Masteroff_Adapter.HOLDER holder, int position) {
        holder.switche.setText("Switch "+list.get(position).Switch);
        holder.button.setText("Button "+list.get(position).button);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder b = new AlertDialog.Builder(holder.itemView.getContext());
                        b.setTitle("Delete ?")
                        .setMessage("Delete Button From Master Off ")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FullscreenActivity.lightsDB.deleteButtonFromMasterOff(list.get(position).Switch,list.get(position).button);
                                MasterOff.CurrentAdapter = new Masteroff_Adapter(FullscreenActivity.lightsDB.getMasterOffButtons());
                                MasterOff.CurrentMasteroff.setAdapter(MasterOff.CurrentAdapter);
                                dialog.dismiss();
                            }
                        }).create().show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size() ;
    }

    public class HOLDER extends RecyclerView.ViewHolder {
        TextView switche , button ;
        public HOLDER(@NonNull View itemView) {
            super(itemView);
            switche = (TextView) itemView.findViewById(R.id.masteroff_switch);
            button = (TextView) itemView.findViewById(R.id.masteroff_button);
        }
    }
}
