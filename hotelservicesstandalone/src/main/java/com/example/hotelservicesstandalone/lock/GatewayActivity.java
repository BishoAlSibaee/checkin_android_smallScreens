package com.example.hotelservicesstandalone.lock;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ttlock.bl.sdk.api.ExtendedBluetoothDevice;
import com.ttlock.bl.sdk.gateway.api.GatewayClient;
import com.ttlock.bl.sdk.gateway.callback.ScanGatewayCallback;

import java.util.LinkedList;


public class GatewayActivity extends AppCompatActivity {

   // private ActivityGatewayBinding binding;
    private GatewayListAdapter mListApapter;
    protected static final int REQUEST_PERMISSION_REQ_CODE = 11;
    public LinkedList<ExtendedBluetoothDevice> mDataList = new LinkedList<ExtendedBluetoothDevice>();
    Activity act = this ;
    RecyclerView kk ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.hotelservicesstandalone.R.layout.activity_gateway);
        GatewayClient.getDefault().prepareBTService(getApplicationContext());
        initListener();
        initList();
    }

    private void initList(){
        //mListApapter = new GatewayListAdapter(act);
        //binding.rvGatewayList.setAdapter(mListApapter);
       // binding.rvGatewayList.setLayoutManager(new LinearLayoutManager(this));
        kk = (RecyclerView)findViewById(com.example.hotelservicesstandalone.R.id.rv_gateway_list);
        LinearLayoutManager manager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        kk.setLayoutManager(manager);
    }

    private void initListener() {
        Button b = (Button) findViewById(com.example.hotelservicesstandalone.R.id.btn_scan_gw);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isBtEnable =  GatewayClient.getDefault().isBLEEnabled(GatewayActivity.this);
                if(isBtEnable){
                    startScan();
                } else {
                    GatewayClient.getDefault().requestBleEnable(GatewayActivity.this);
                }
            }
        });
       /* binding.btnScan.setOnClickListener(v -> {
            boolean isBtEnable =  GatewayClient.getDefault().isBLEEnabled(GatewayActivity.this);
            if(isBtEnable){
                startScan();
            } else {
                GatewayClient.getDefault().requestBleEnable(GatewayActivity.this);
            }
        });*/
    }

    /**
     * before call startScanGateway,the location permission should be granted.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void startScan(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_REQ_CODE);
            return;
        }
        getScanGatewayCallback();
    }

    /**
     * start scan BT lock
     */
    private void getScanGatewayCallback(){
        GatewayClient.getDefault().startScanGateway(new ScanGatewayCallback() {
            @Override
            public void onScanGatewaySuccess(ExtendedBluetoothDevice device) {
//                LogUtil.d("device:" + device);
                mDataList.add(device);
                mListApapter= new GatewayListAdapter(act,mDataList);
                kk.setAdapter(mListApapter);
                if (mListApapter != null)
                    mListApapter.updateData(device);
            }

            @Override
            public void onScanFailed(int errorCode) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length == 0 ){
            return;
        }

        switch (requestCode) {
            case REQUEST_PERMISSION_REQ_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getScanGatewayCallback();
                } else {
                    if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)){

                    }
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GatewayClient.REQUEST_ENABLE_BT && requestCode == Activity.RESULT_OK) {
            startScan();
        }
    }
}
