package com.syriasoft.cleanup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DND_Adapter extends RecyclerView.Adapter<DND_Adapter.HOLDER> {
    List<cleanOrder> list = new ArrayList<cleanOrder>();

    public DND_Adapter(List<cleanOrder> list) {
        this.list = sortList(list);
    }

    @NonNull
    @Override
    public DND_Adapter.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dnd_unit, null);
        HOLDER holder = new HOLDER(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DND_Adapter.HOLDER holder, int position) {
        holder.room.setText(list.get(position).roomNumber);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HOLDER extends RecyclerView.ViewHolder {
        TextView room;

        public HOLDER(@NonNull View itemView) {
            super(itemView);
            room = (TextView) itemView.findViewById(R.id.dnd_roomText);
        }
    }

    private List<cleanOrder> sortList(List<cleanOrder> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 1; j < (list.size() - i); j++) {
                if (Integer.parseInt(list.get(j - 1).roomNumber) > Integer.parseInt(list.get(j).roomNumber)) {
                    Collections.swap(list, j, j - 1);
                }
            }
        }
        return list;
    }
}
