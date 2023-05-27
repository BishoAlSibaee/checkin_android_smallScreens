package com.example.mobilecheckdevice;


import org.json.JSONException;
import org.json.JSONObject;

public class CheckInActions {

    boolean power ;
    boolean lights ;
    boolean ac ;
    boolean curtain ;

    public CheckInActions (String actions) {
        if (actions != null) {
            try {
                JSONObject res = new JSONObject(actions);
                power = res.getBoolean("power");
                lights = res.getBoolean("lights");
                ac = res.getBoolean("ac");
                curtain = res.getBoolean("curtain");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
