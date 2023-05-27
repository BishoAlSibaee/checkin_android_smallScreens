package com.syriasoft.hotelservices;

import com.tuya.smart.sdk.bean.DeviceBean;

public class MoodBtn {

    DeviceBean Switch ;
    int SwitchButton ;
    boolean status ;

    public MoodBtn(DeviceBean aSwitch, int switchButton,boolean status) {
        Switch = aSwitch;
        SwitchButton = switchButton;
        this.status = status ;
    }
}
