package com.syriasoft.cleanup;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.ttlock.bl.sdk.api.TTLockClient;

import java.util.List;

public class ROOMS extends AppCompatActivity {

    private RecyclerView rooms ;
    private ROOMS_ADAPTER adapter ;
    private List<ROOM> list ;
    private GridLayoutManager manager ;
    private Activity act ;
    static public List<DatabaseReference> FireRooms ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_o_o_m_s);
        act = this ;
        list = MainActivity.Rooms ;
        FireRooms = MainActivity.FireRooms ;
        rooms = (RecyclerView) findViewById(R.id.rooms_recycler);
        manager = new GridLayoutManager(this,4);
        manager.offsetChildrenHorizontal(2);
        manager.offsetChildrenVertical(2);
        adapter = new ROOMS_ADAPTER(list);
        rooms.setLayoutManager(manager);
        rooms.setAdapter(adapter);
        ensureBluetoothIsEnabled();
    }

    public void ensureBluetoothIsEnabled()
    {
        if(!TTLockClient.getDefault().isBLEEnabled(act)){
            TTLockClient.getDefault().requestBleEnable(act);
        }
    }
}