package com.example.hotelservicesstandalone;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckoutActions {

    boolean power ;
    boolean lights ;
    boolean ac ;
    boolean curtain ;

    public CheckoutActions (String actions) {
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
