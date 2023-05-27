package com.example.mobilecheckdevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WifiReceiver extends BroadcastReceiver
{

    WifiManager wifiManager;
    StringBuilder sb;
    ListView wifiDeviceList;
    TextView selectedwifi ;

    public WifiReceiver(WifiManager wifiManager, ListView wifiDeviceList , TextView selected)
    {
        this.wifiManager = wifiManager;
        this.wifiDeviceList = wifiDeviceList;
        this.selectedwifi = selected ;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action))
        {
            sb = new StringBuilder();
            List<ScanResult> wifiList = wifiManager.getScanResults();
            ArrayList<String> deviceList = new ArrayList<>();
            for (ScanResult scanResult : wifiList)
            {
                sb.append("\n").append(scanResult.SSID).append(" - ").append(scanResult.capabilities);
                deviceList.add(scanResult.SSID);
            }
            //Toast.makeText(context, sb, Toast.LENGTH_SHORT).show();
            ArrayAdapter arrayAdapter = new ArrayAdapter(context, R.layout.spinners_item, deviceList.toArray());
            wifiDeviceList.setAdapter(arrayAdapter);
            wifiDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    selectedwifi.setText(deviceList.get(position));
                }
            });
        }
    }
}
