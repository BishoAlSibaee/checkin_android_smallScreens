package com.syriasoft.hotelservices;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BTN_ADAPTER extends RecyclerView.Adapter<BTN_ADAPTER.HOLDER>
{
    List<BTN> list = new ArrayList<BTN>();

    public BTN_ADAPTER(List<BTN> list)
    {
        this.list = list;
    }

    @NonNull
    @Override
    public BTN_ADAPTER.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_screen_btn,parent ,false);
        BTN_ADAPTER.HOLDER holder = new BTN_ADAPTER.HOLDER(row);
        return holder ;
    }

    @Override
    public void onBindViewHolder(@NonNull BTN_ADAPTER.HOLDER holder, int position)
    {
        holder.text.setText(list.get(position).text);
        holder.image.setImageResource(list.get(position).image);
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (list.get(position).text.equals("CLEANUP"))
                {
                    //FullscreenActivity.requestCleanUp(v);
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }


    public class HOLDER extends RecyclerView.ViewHolder
    {
        ImageView image ;
        TextView text ;
        public HOLDER(@NonNull View itemView)
        {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.mainScreenBtn_image);
            text = (TextView) itemView.findViewById(R.id.mainScreenBtn_text);
        }
    }
}
