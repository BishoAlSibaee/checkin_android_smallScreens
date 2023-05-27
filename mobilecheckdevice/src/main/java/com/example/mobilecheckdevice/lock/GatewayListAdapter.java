package com.example.mobilecheckdevice.lock;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecheckdevice.R;
import com.ttlock.bl.sdk.api.ExtendedBluetoothDevice;
import com.ttlock.bl.sdk.gateway.api.GatewayClient;
import com.ttlock.bl.sdk.gateway.callback.ConnectCallback;
import com.ttlock.bl.sdk.util.LogUtil;

import java.util.LinkedList;

/**
 * Created on  2019/4/12 0012 14:19
 *
 * @author theodre
 */
public class GatewayListAdapter extends  RecyclerView.Adapter<GatewayListAdapter.DeviceViewHolder>{

    public LinkedList<ExtendedBluetoothDevice> mDataList = new LinkedList<ExtendedBluetoothDevice>();

    private Activity mContext;
    private static final int TIMEOUT = 5000;
    private LinkedList<ExtendedBluetoothDevice> mAddStatusList = new LinkedList<ExtendedBluetoothDevice>();
    private LinkedList<ExtendedBluetoothDevice> mNormalStatusList = new LinkedList<ExtendedBluetoothDevice>();
    private long lastSyncTimeStamp = 0;

    public GatewayListAdapter(Activity context ,LinkedList<ExtendedBluetoothDevice> mDataList){
        this.mContext = context;
        this.mDataList = mDataList ;

    }

    public synchronized void updateData(ExtendedBluetoothDevice device){
        if(device != null) {
            if(device.isSettingMode()){
                addOrSortLock(device,mAddStatusList);
                removeOtherStatusLock(device,mNormalStatusList);
            }else {
                addOrSortLock(device,mNormalStatusList);
                removeOtherStatusLock(device,mAddStatusList);
            }

            long currentTime = System.currentTimeMillis();
            if((currentTime - lastSyncTimeStamp) >= 800 ){
                if(!mDataList.isEmpty()){
                    mDataList.clear();
                }

                mDataList.addAll(0,mAddStatusList);
                mDataList.addAll(mNormalStatusList);
                notifyDataSetChanged();
                lastSyncTimeStamp = currentTime;
            }
        }
    }


    /**
     * you can sort the lock that be discovered by signal value.
     */
    private void addOrSortLock(ExtendedBluetoothDevice scanDevice, LinkedList<ExtendedBluetoothDevice> lockList){
        boolean isContained = false;
        int length = lockList.size();
        ExtendedBluetoothDevice mTopOneDevice;
        scanDevice.setDate(System.currentTimeMillis());
        if(length > 0){

            mTopOneDevice = lockList.get(0);

            for(int i = 0;i < length;i++) {
                if(i >= length){
                    break;
                }

                ExtendedBluetoothDevice currentDevice = lockList.get(i);

                if(scanDevice.getAddress().equals(currentDevice.getAddress()) ){
                    isContained = true;
                    if(i != 0 && scanDevice.getRssi() > mTopOneDevice.getRssi()){
                        lockList.remove(i);
                        lockList.add(0,scanDevice);
                    }else {
                        currentDevice.setDate(System.currentTimeMillis());
                        lockList.set(i,currentDevice);
                    }
                }else {
                    if(System.currentTimeMillis() - currentDevice.getDate() >= TIMEOUT) {
                        lockList.remove(i);
                        length = lockList.size();
                    }
                }
            }

            if(!isContained){
                if(scanDevice.getRssi() > mTopOneDevice.getRssi()){
                    lockList.add(0,scanDevice);
                }else {
                    lockList.add(scanDevice);
                }
            }

        }else {
            lockList.add(scanDevice);
        }

    }

    /**
     * the lock mode will be changed,so should update the list when lock mode changed.
     * @param scanDevice the lock that be discovered.
     */
    private void removeOtherStatusLock(ExtendedBluetoothDevice scanDevice, LinkedList<ExtendedBluetoothDevice> lockList){
        if(!lockList.isEmpty()){
            int length = lockList.size();
            for(int i = 0; i < length ; i++){
                ExtendedBluetoothDevice device = lockList.get(i);
                if(device.getAddress().equals(scanDevice.getAddress())){
                    lockList.remove(i);
                    length --;
                }else {
                    if(System.currentTimeMillis() - device.getDate() >= TIMEOUT) {
                        lockList.remove(i);
                        length --;
                    }
                }
            }
        }
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.gateway_scan_list_item, parent, false);
        return new DeviceViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder _holder, int position) {
        final ExtendedBluetoothDevice item = mDataList.get(position);
        _holder.t.setText(item.getName());
        _holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"--connect gateway--",Toast.LENGTH_LONG).show();
                GatewayClient.getDefault().connectGateway(item, new ConnectCallback() {
                    @Override
                    public void onConnectSuccess(ExtendedBluetoothDevice device) {
                        InitGatewayActivity.launch(mContext, item);
                        LogUtil.d("connect success");
                    }

                    @Override
                    public void onDisconnected() {
                        Toast.makeText(mContext,"Timeout. The gateway is out of setting mode", Toast.LENGTH_LONG).show();
                    }

                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder {

       // GatewayScanListItemBinding itemBinding;
        TextView t ;


        public DeviceViewHolder(View itemView){
            super(itemView);
            //itemBinding = DataBindingUtil.bind(itemView);
            t = (TextView) itemView.findViewById(R.id.tv_gateway_name);
        }

      /*  public void Bind(ExtendedBluetoothDevice item){
            itemBinding.tvGatewayName.setText(item.getName());

            itemBinding.getRoot().setOnClickListener(view -> {

                Toast.makeText(mContext,"--connect gateway--",Toast.LENGTH_LONG).show();
                GatewayClient.getDefault().connectGateway(item, new ConnectCallback() {
                    @Override
                    public void onConnectSuccess(ExtendedBluetoothDevice device) {
                        InitGatewayActivity.launch(mContext, item);
                        LogUtil.d("connect success");
                    }

                    @Override
                    public void onDisconnected() {
                        Toast.makeText(mContext,"Timeout. The gateway is out of setting mode", Toast.LENGTH_LONG).show();
                    }

                });
            });
        }*/

    }

}
