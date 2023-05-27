package com.example.mobilecheckdevice;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Rooms_Adapter extends RecyclerView.Adapter<Rooms_Adapter.Holder>
{
    List<ROOM> list = new ArrayList<ROOM>();

    public Rooms_Adapter(List<ROOM> list)
    {
        this.list = list;
        Log.d("RoomsFromAdapter" , this.list.size()+"");
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_unit,parent ,false);
        Holder holder = new Holder(row);
        return holder ;
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position)
    {
        Log.d("theroomFromadapter" , "room "+list.get(position).RoomNumber+" power "+list.get(position).PowerSwitch+" zb "+list.get(position).ZBGateway);
        holder.Room.setText(String.valueOf(list.get(position).RoomNumber));

        if (list.get(position).lock == 1)
        {
            holder.lock.setImageResource(R.drawable.lock);
        }
        if (list.get(position).Thermostat == 1 )
        {
            holder.ac.setImageResource(R.drawable.ac);
        }
        if(list.get(position).PowerSwitch == 1  )
        {
            holder.power.setImageResource(R.drawable.power);
        }
        if ( list.get(position).ZBGateway  == 1 )
        {
            holder.gateway.setImageResource(R.drawable.gateway);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(holder.itemView.getContext() , RoomManager.class);
                Toast.makeText(holder.itemView.getContext(),String.valueOf(list.get(position).ZBGateway) , Toast.LENGTH_LONG).show();
                i.putExtra("RoomId" , list.get(position).id);
                holder.itemView.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder
    {
        TextView Room ;
        ImageView lock , power , gateway , ac ;

        public Holder(@NonNull View itemView)
        {
            super(itemView);
            Room = (TextView) itemView.findViewById(R.id.room_unit_roomNumber);
            lock = (ImageView) itemView.findViewById(R.id.room_unit_lock);
            power = (ImageView) itemView.findViewById(R.id.room_unit_power);
            gateway = (ImageView) itemView.findViewById(R.id.room_unit_gateway);
            ac = (ImageView) itemView.findViewById(R.id.room_unit_ac);
        }
    }
}
