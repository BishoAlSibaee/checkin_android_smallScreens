package com.syriasoft.cleanup;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ttlock.bl.sdk.api.TTLockClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ROOMS extends AppCompatActivity {
    private RecyclerView rooms;
    static ROOMS_ADAPTER adapter;
    private List<ROOM> list;
    private GridLayoutManager manager;
    private Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_o_o_m_s);
        act = this;
        list = MainActivity.Rooms;
        rooms = findViewById(R.id.rooms_recycler);
        manager = new GridLayoutManager(this, 4);
        manager.offsetChildrenHorizontal(2);
        manager.offsetChildrenVertical(2);
        ensureBluetoothIsEnabled();
        adapter = new ROOMS_ADAPTER(list);
        rooms.setLayoutManager(manager);
        rooms.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainActivity.getRooms();
    }

    public void ensureBluetoothIsEnabled() {
        if (!TTLockClient.getDefault().isBLEEnabled(act)) {
            TTLockClient.getDefault().requestBleEnable(act);
        }
    }
}