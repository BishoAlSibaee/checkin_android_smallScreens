package com.example.hotelservicesstandalone;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientBackActions {

    boolean lights ;
    boolean curtain ;
    boolean ac ;

    public ClientBackActions (String actions) {
        if (actions != null) {
            try {
                JSONObject res = new JSONObject(actions);
                lights = res.getBoolean("lights");
                curtain = res.getBoolean("curtain");
                ac = res.getBoolean("ac");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
