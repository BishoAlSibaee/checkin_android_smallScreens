package com.syriasoft.cleanup;

import android.content.Context;
import android.util.Log;
import androidx.annotation.Nullable;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tuya.smart.android.common.utils.SHA256Util;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ZigbeeLock {

    static void getTokenFromApi (String clientId, String secret, Context co, RequestOrder order) {
        Calendar c = Calendar.getInstance();
        String d = String.valueOf(c.getTimeInMillis());
        String url = "/v1.0/token?grant_type=1";
        String tokenUrl = "https://openapi.tuyaeu.com/v1.0/token?grant_type=1";

        String stringToSign = "GET" + "\n" + "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855" + "\n" +"\n"+ url ;

        String sign = clientId+d+stringToSign;

        String signRequest = RequestSignUtils.Sha256Util.sha256HMAC(sign,secret).toUpperCase();

        JsonObjectRequest tokenReq = new JsonObjectRequest(Request.Method.GET, tokenUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("ticketRequestResult",response.toString());
                try {
                    boolean status = response.getBoolean("success");
                    if (status) {
                        JSONObject result = response.getJSONObject("result");
                        String token = result.getString("access_token");
                        order.onSuccess(token);
                    }
                    else {
                        order.onFailed("failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    order.onFailed(e.getMessage());
                }
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                order.onFailed(error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("client_id", clientId);
                params.put("t", d);
                params.put("sign_method", "HMAC-SHA256");
                params.put("sign", signRequest);
                return params;
            }
        };
        Volley.newRequestQueue(co).add(tokenReq);
    }

    static void getTicketId(String token,String clientId,String secret,String deviceId,Context co,RequestOrder order) {
        Calendar c = Calendar.getInstance();
        String t = String.valueOf(c.getTimeInMillis());
        String url = "/v1.0/devices/"+deviceId+"/door-lock/password-ticket";
        String ticketUrl = "https://openapi.tuyaeu.com"+url; //https://openapi.tuyaeu.com
        String nonce = "";
        String stringToSign = "POST\n"+
                "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\n"+
                "\n"+
                url;

        String str = clientId + token + t + nonce + stringToSign ;

        String sign = RequestSignUtils.Sha256Util.sha256HMAC(str,secret).toUpperCase();
        //Log.d("ticketRequestResult","sign is: "+ticketUrl+"\n\n"+"stringToSign is: "+sign+"\n\n"+t);
        JsonObjectRequest tokenReq = new JsonObjectRequest(Request.Method.POST, ticketUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("ticketRequestResult",response.toString());
                try {
                    boolean status = response.getBoolean("success");
                    if (status) {
                        JSONObject result = response.getJSONObject("result");
                        String ticketId = result.getString("ticket_id");
                        order.onSuccess(ticketId);
                    }
                    else {
                        order.onFailed("failed");
                    }
                } catch (JSONException e) {
                    order.onFailed(e.getMessage());
                }
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                order.onFailed(error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("client_id", clientId);
                params.put("t", t);
                params.put("sign_method", "HMAC-SHA256");
                params.put("sign", sign);
                params.put("access_token",token);
                return params;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("device_id", deviceId);
                return params;
            }
        };
        Volley.newRequestQueue(co).add(tokenReq);
    }

    static void unlockWithoutPassword(String token,String ticketId,String clientId,String secret,String deviceId,Context co,RequestOrder order) {
        Calendar c = Calendar.getInstance();
        String t = String.valueOf(c.getTimeInMillis());
        //String deviceId = "d75a969dcc525e714fdijj";
        String url = "/v1.0/devices/"+deviceId+"/door-lock/password-free/open-door";
        String unlockUrl = "https://openapi.tuyaeu.com"+url;
        //String client_id = "d9hyvtdshnm3uvaun59d";
        String nonce = "";
        String contentSHA256 = "";
        JSONObject params = new JSONObject();
        try {
            params.put("ticket_id", ticketId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            contentSHA256 = SHA256Util.sha256(params.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String stringToSign = "POST\n"+
                contentSHA256+"\n"+  //"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
                "\n"+
                url;

        String str = clientId + token + t + nonce + stringToSign ;

        String signn = RequestSignUtils.Sha256Util.sha256HMAC(str,secret).toUpperCase();

        JsonObjectRequest tokenReq = new JsonObjectRequest(Request.Method.POST, unlockUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("ticketRequestResult",response.toString());
                try {
                    boolean status = response.getBoolean("success");
                    if (status) {
                        order.onSuccess("success");
                    }
                    else {
                        order.onFailed("failed");
                    }
                } catch (JSONException e) {
                    order.onFailed(e.getMessage());
                }
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                order.onFailed(error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("client_id", clientId);
                params.put("t", t);
                params.put("sign_method", "HMAC-SHA256");
                params.put("sign", signn);
                params.put("access_token",token);
                return params;
            }

        };
        Volley.newRequestQueue(co).add(tokenReq);
    }
}

interface RequestOrder {
    void onSuccess(String token);
    void onFailed(String error);
}