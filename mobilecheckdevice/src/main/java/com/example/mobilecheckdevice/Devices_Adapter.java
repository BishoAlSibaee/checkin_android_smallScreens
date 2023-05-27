package com.example.mobilecheckdevice;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;

public class Devices_Adapter extends BaseAdapter {

    List<DeviceBean> list = new ArrayList<DeviceBean>();
    LayoutInflater inflater ;
    Context c ;

    Devices_Adapter(List<DeviceBean> list ,Context c ) {
        this.list = list ;
        this.c = c ;
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.device_unit , null);

        TextView name = (TextView) convertView.findViewById(R.id.deviceUnit_deviceName);
        TextView order = (TextView) convertView.findViewById(R.id.order);
        ImageView local = (ImageView) convertView.findViewById(R.id.deviceUnit_local);
        ImageView net = (ImageView) convertView.findViewById(R.id.deviceUnit_net);
        ImageView cloud = (ImageView) convertView.findViewById(R.id.deviceUnit_cloud);
        ITuyaDevice mDevice = TuyaHomeSdk.newDeviceInstance(list.get(position).devId);
        name.setText(list.get(position).getName());

        if (list.get(position).getIsOnline()) {
            net.setImageResource(android.R.drawable.presence_online);
        }
        else {
            net.setImageResource(android.R.drawable.ic_delete);
        }

        String STATUS = "" ;
        List kkk = new ArrayList(list.get(position).getDps().keySet());
        List vvv = new ArrayList(list.get(position).getDps().values());

        for (int i=0;i<kkk.size();i++) {
            STATUS = STATUS+ " ["+kkk.get(i)+" "+vvv.get(i)+"] " ;
        }

        //order.setText(STATUS);

        mDevice.registerDevListener(new IDevListener() {
            @Override
            public void onDpUpdate(String devId, String dpStr) {
                order.setText(dpStr);
            }

            @Override
            public void onRemoved(String devId) {

            }

            @Override
            public void onStatusChanged(String devId, boolean online) {
                Log.d("onlineDevice"+list.get(position).name , String.valueOf(online));
                if (online) {
                    net.setImageResource(android.R.drawable.presence_online);
                }
                else {
                    net.setImageResource(android.R.drawable.ic_delete);
                }
            }

            @Override
            public void onNetworkStatusChanged(String devId, boolean status) {

            }

            @Override
            public void onDevInfoUpdate(String devId) {

            }
        });

        View finalConvertView = convertView;
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                view.setBackgroundColor(Color.DKGRAY);
                Dialog d = new Dialog(finalConvertView.getContext());
                d.setContentView(R.layout.rename_device_dialog);
                Spinner s = (Spinner) d.findViewById(R.id.devicerenamespinner);
                Spinner rr = (Spinner) d.findViewById(R.id.roomsspinner);
                String[] Types = new String[]{"Power", "ZGatway", "AC", "DoorSensor", "MotionSensor", "Curtain", "ServiceSwitch", "Switch1", "Switch2", "Switch3", "Switch4","IR"};
                String[] therooms = new String[Rooms.ROOMS.size()];
                for (int i = 0; i < Rooms.ROOMS.size(); i++) {
                    therooms[i] = String.valueOf(Rooms.ROOMS.get(i).RoomNumber);
                }
                ArrayAdapter<String> a = new ArrayAdapter<String>(finalConvertView.getContext(), R.layout.spinners_item, Types);
                ArrayAdapter<String> r = new ArrayAdapter<String>(finalConvertView.getContext(), R.layout.spinners_item, therooms);
                s.setAdapter(a);
                rr.setAdapter(r);
                TextView title = (TextView) d.findViewById(R.id.RenameDialog_title);
                title.setText("Modify " + list.get(position).getName() + " Device " + list.get(position).getIsOnline().toString());
                Button cancel = (Button) d.findViewById(R.id.cancel_diallog);
                Button rename = (Button) d.findViewById(R.id.DoTheRename);
                Button delete = (Button) d.findViewById(R.id.deleteDevice);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                rename.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ITuyaDevice Device = TuyaHomeSdk.newDeviceInstance(list.get(position).getDevId());
                        Device.renameDevice(rr.getSelectedItem().toString() + s.getSelectedItem().toString(), new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {
                                Toast.makeText(finalConvertView.getContext(), "Error. " + error, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onSuccess() {
                                Rooms.CHANGE_STATUS = true;
                                Toast.makeText(finalConvertView.getContext(), "Device Renamed .", Toast.LENGTH_LONG).show();
                                d.dismiss();
                            }
                        });
                    }

                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        ITuyaDevice Device = TuyaHomeSdk.newDeviceInstance(list.get(position).getDevId());
                        Device.removeDevice(new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {
                                Toast.makeText(finalConvertView.getContext(), "Error. " + error, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onSuccess() {
                                Rooms.CHANGE_STATUS = true;
                                Toast.makeText(finalConvertView.getContext(), "Device Deleted .", Toast.LENGTH_LONG).show();
                                d.dismiss();
                            }
                        });
                    }

                });
                d.show();

                d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        view.setBackgroundColor(Color.LTGRAY);
                    }
                });
                return false;

            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("IR" , list.get(position).getCategoryCode());
                Log.d("SelectedDeviceInfo","name: "+list.get(position).getName()+" dps: "+list.get(position).getDps()+" category: "+list.get(position).getDeviceCategory());
                if (list.get(position).getIsOnline()) {
                    Toast.makeText(finalConvertView.getContext(),"online",Toast.LENGTH_SHORT).show();
                    net.setImageResource(android.R.drawable.presence_online);
                }
                else {
                    Toast.makeText(finalConvertView.getContext(),"offline",Toast.LENGTH_SHORT).show();
                    net.setImageResource(android.R.drawable.ic_delete);
                }
                if (list.get(position).getCategoryCode().equals("wf_wnykq")) {

                }
            }
        });

        return convertView;

    }
}
