package com.syriasoft.hotelservices;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public  class ErrorRegister
{
    private static String url = LogIn.URL+"insertError.php" ;

    public static void rigestError(Context c , String hotel, int room, long dateTime, int errorCode, String errorMsg, String errorCaption)
    {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
               // Toast.makeText(c,"error recorded" , Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                //Toast.makeText(c,"error recording error" , Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> params = new HashMap<String, String>();
                params.put("hotel" , hotel ) ;
                params.put("room" ,String.valueOf( room )) ;
                params.put("dateTime" , String.valueOf(dateTime));
                params.put("errorCode" , String.valueOf(errorCode));
                params.put("errorMsg" , errorMsg) ;
                params.put("caption" , errorCaption) ;
                 return params;
            }
        };

        Volley.newRequestQueue(c).add(request);
    }
}
