package com.example.mobilecheckdevice;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Rooms_Adapter_Base extends BaseAdapter
{
    List<ROOM> list = new ArrayList<ROOM>();
    LayoutInflater inflater ;
    Context c ;

    public Rooms_Adapter_Base(List<ROOM> list , Context c )
    {
        this.list = list ;
        this.c = c ;
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        convertView = inflater.inflate(R.layout.room_unit , null);

        TextView Room = (TextView) convertView.findViewById(R.id.room_unit_roomNumber);
        ImageView lock = (ImageView) convertView.findViewById(R.id.room_unit_lock);
        ImageView power = (ImageView) convertView.findViewById(R.id.room_unit_power);
        ImageView gateway = (ImageView) convertView.findViewById(R.id.room_unit_gateway);
        ImageView ac = (ImageView) convertView.findViewById(R.id.room_unit_ac);

        Room.setText(String.valueOf(list.get(position).RoomNumber));
        if (list.get(position).lock == 1)
        {
            lock.setImageResource(R.drawable.lock);
        }
        if (list.get(position).Thermostat == 1 )
        {
            ac.setImageResource(R.drawable.ac);
        }
        if(list.get(position).PowerSwitch == 1  )
        {
            power.setImageResource(R.drawable.power);
        }
        if ( list.get(position).ZBGateway  == 1 )
        {
            gateway.setImageResource(R.drawable.gateway);
        }
        convertView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(c , RoomManager.class);
                //Toast.makeText(c,String.valueOf(list.get(position).ZBGateway) , Toast.LENGTH_LONG).show();
                i.putExtra("RoomId" , list.get(position).id);
                c.startActivity(i);
            }
        });

        return convertView;
    }
}
