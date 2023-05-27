package com.syriasoft.hotelservices.TUYA;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syriasoft.hotelservices.LogIn;
import com.syriasoft.hotelservices.R;
import com.syriasoft.hotelservices.ToastMaker;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken;

import java.util.ArrayList;
import java.util.List;

public class Family_List_Adapter extends RecyclerView.Adapter<Family_List_Adapter.Holder> {

    List<HomeBean> list = new ArrayList<HomeBean>();
    String Token ;

    public Family_List_Adapter(List<HomeBean> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Family_List_Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.family_module , parent , false);
        Holder h = new Holder(v);
        return h ;
    }

    @Override
    public void onBindViewHolder(@NonNull Family_List_Adapter.Holder holder, @SuppressLint("RecyclerView") int position)
    {
        holder.Name.setText(list.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Dialog d = new Dialog(holder.itemView.getContext()) ;
                d.setContentView(R.layout.small_confermation_dialog);
                TextView head = (TextView) d.findViewById(R.id.textView2);
                TextView text = (TextView) d.findViewById(R.id.confermationDialog_Text);
                Button cancel = (Button) d.findViewById(R.id.confermationDialog_cancel);
                Button ok = (Button) d.findViewById(R.id.messageDialog_ok);
                head.setText("Conferm Selected Project "+list.get(position).getName());
                text.setText("Are You Sure .. ? ("+list.get(position).getName()+")");
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        //long homeId  = list.get(position).getHomeId() ;
                        LogIn.selectedHome = list.get(position) ;
                        ToastMaker.MakeToast( "Hotel Selected" , holder.itemView.getContext());
                        holder.itemView.setBackgroundColor(Color.BLUE);
                        //LogIn.room.insertTuyaProject(list.get(position).getName());
                        d.dismiss();
                    }
                });
                d.show();
            }
        });
    }

    void createConfermationDialog(Context c)
    {
        Dialog d = new Dialog(c) ;
        d.setContentView(R.layout.confermation_dialog);
        TextView head = (TextView) d.findViewById(R.id.textView2);
        TextView text = (TextView) d.findViewById(R.id.confermationDialog_Text);
        Button cancel = (Button) d.findViewById(R.id.confermationDialog_cancel);
        Button ok = (Button) d.findViewById(R.id.messageDialog_ok);
        head.setText("Conferm Selected Project ");
        text.setText("Are You Sure .. ? ");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

            }
        });
        d.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder
    {

        TextView Name ;

        public Holder(@NonNull View itemView) {
            super(itemView);
            Name = (TextView) itemView.findViewById(R.id.Tuya_familyModule_name);
        }
    }
}
