package com.example.hotelservicesstandalone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.sdk.api.IResultCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Login extends AppCompatActivity
{
    private Spinner PROJECTS_SPINNER, homes;
    private String[] Names ;
    private Activity act = this ;
    private String projectsUrl = "https://ratco-solutions.com/Checkin/getProjects.php";
    private String projectLoginUrl = "users/loginProject" ;
    private EditText password ;
    static HotelDB THEHOTELDB ;
    private List<HomeBean> Homs;
    public static HomeBean THEHOME ;
    private String COUNTRY_CODE = "966";
    List<PROJECT> projects ;
    PROJECT THE_PROJECT ;
    SharedPreferences pref ;
    SharedPreferences.Editor editor ;
    String projectName , tuyaUser , tuyaPassword , lockUser , lockPassword ,Device_ID , Device_Name ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        PROJECTS_SPINNER = findViewById(R.id.spinner);
        homes = findViewById(R.id.spinner2);
        pref = getSharedPreferences("MyProject", MODE_PRIVATE);
        editor = getSharedPreferences("MyProject", MODE_PRIVATE).edit();
        THEHOTELDB = new HotelDB(act);
        projects = new ArrayList<>();
        getProjects(new loginCallback() {
            @Override
            public void onSuccess() {
                goNext();
            }

            @Override
            public void onFailed() {
                Toast.makeText(act,"get projects failed",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getProjects(loginCallback callback) {
        StringRequest re = new StringRequest(Request.Method.POST, projectsUrl , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("getProjectsResp" , response);
                if (response != null ) {
                    try {
                        JSONArray arr = new JSONArray(response);
                        Names = new String[arr.length()];
                        for(int i=0;i<arr.length();i++) {
                            JSONObject row = arr.getJSONObject(i);
                            projects.add(new PROJECT(row.getInt("id"),row.getString("projectName"),row.getString("city"),row.getString("salesman"),row.getString("TuyaUser"),row.getString("TuyaPassword"),row.getString("LockUser"),row.getString("LockPassword"),row.getString("url")));
                            Names[i] = row.getString("projectName");
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("getProjectsResp" , e.toString());
                        callback.onFailed();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,R.layout.spinners_item,Names);
                    PROJECTS_SPINNER.setAdapter(adapter);
                    PROJECTS_SPINNER.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            THE_PROJECT = projects.get(PROJECTS_SPINNER.getSelectedItemPosition());
                            MyApp.THE_PROJECT = projects.get(PROJECTS_SPINNER.getSelectedItemPosition());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    callback.onSuccess();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("getProjectsResp" , error.toString());
                callback.onFailed();
            }
        });
        Volley.newRequestQueue(act).add(re);
    }

    public void LogIn(View view) {
        if (THE_PROJECT != null ) {
            final lodingDialog loading = new lodingDialog(act);
            final String pass = password.getText().toString();
            StringRequest re = new StringRequest(Request.Method.POST, THE_PROJECT.url + projectLoginUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    loading.stop();
                    if (response != null) {
                        try {
                            JSONObject resp = new JSONObject(response);
                            if (resp.getString("result").equals("success")) {
                                Toast.makeText(act,"Login Success",Toast.LENGTH_LONG).show();
                                editor.putString("projectName" , THE_PROJECT.projectName);
                                editor.putString("tuyaUser" , THE_PROJECT.TuyaUser);
                                editor.putString("tuyaPassword" , THE_PROJECT.TuyaPassword);
                                editor.putString("lockUser" , THE_PROJECT.LockUser);
                                editor.putString("lockPassword" , THE_PROJECT.LockPassword);
                                editor.putString("url" , THE_PROJECT.url);
                                editor.apply();
                                Device_ID = pref.getString("Device_Id", null);
                                Device_Name = pref.getString("Device_Name", null);
                                MyApp.my_token = resp.getString("token");
                                if (Device_ID == null && Device_Name == null) {
                                    Log.d("deviceName" , "null");
                                    addControlDevice(new loginCallback() {
                                        @Override
                                        public void onSuccess() {
                                            logInFunction(THE_PROJECT);
                                        }
                                        @Override
                                        public void onFailed() {
                                            Toast.makeText(act,"Add Device Failed",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                else {
                                    Log.d("deviceName" , Device_Name);
                                    logInFunction(THE_PROJECT);
                                }
                            }
                            else {
                                Toast.makeText(act,"Login Failed " + resp.getString("error"),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(act,"Login Failed " + e.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    loading.stop();
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> par = new HashMap<String, String>();
                    par.put( "password" , pass ) ;
                    par.put( "project_name" , THE_PROJECT.projectName ) ;
                    return par;
                }
            };
            Volley.newRequestQueue(act).add(re);
        }
        else {
            Toast.makeText(act,"please select project",Toast.LENGTH_LONG).show();
        }
    }

    private void goNext() {
        projectName = pref.getString("projectName", null);
        tuyaUser = pref.getString("tuyaUser", null);
        tuyaPassword = pref.getString("tuyaPassword", null);
        lockUser = pref.getString("lockUser", null);
        lockPassword = pref.getString("lockPassword", null);
        if (projectName == null) {
            LinearLayout loginLayout = findViewById(R.id.login_layout);
            LinearLayout loadingLayout = findViewById(R.id.loading_layout);
            loadingLayout.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);
            password = findViewById(R.id.editTextTextPersonName);
        }
        else {
            for (int i=0;i<projects.size();i++) {
                Log.d("projects" , projects.get(i).projectName + " " +projectName);
                if (projectName.equals(projects.get(i).projectName)) {
                    THE_PROJECT = projects.get(i);
                    Device_ID = pref.getString("Device_Id", null);
                    MyApp.Device_Id = Device_ID;
                    Device_Name = pref.getString("Device_Name", null);
                    MyApp.Device_Name = Device_Name;
                    MyApp.THE_PROJECT = projects.get(i);
                    if (Device_ID == null && Device_Name == null) {
                        addControlDevice(new loginCallback() {
                            @Override
                            public void onSuccess() {
                                logInFunction(THE_PROJECT);
                            }
                            @Override
                            public void onFailed() {
                                Toast.makeText(act,"Add Device Failed",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else {
                        logInFunction(THE_PROJECT);
                    }
                }
            }
        }
    }

    void logInFunction(PROJECT p) {
        TuyaHomeSdk.getUserInstance().loginWithEmail(COUNTRY_CODE,p.TuyaUser ,p.TuyaPassword , new ILoginCallback() {
            @Override
            public void onSuccess (User user) {
                Log.d("tuyaLoginResp",user.getNickName());
                MyApp.TuyaUser = user ;
                TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
                    @Override
                    public void onError(String errorCode, String error) {
                        Toast.makeText(act,"TUya Login Failed" + error,Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onSuccess(List<HomeBean> homeBeans) {
                        MyApp.homeBeans = homeBeans ;
                        Homs = homeBeans ;
                        for(int i=0;i<Homs.size();i++) {
                            if (MyApp.THE_PROJECT.projectName.contains(Homs.get(i).getName())) {
                                THEHOME = Homs.get(i) ;
                                MyApp.HOME = Homs.get(i);
                            }
                        }
                        if (THEHOME != null ) {
                            Log.d("homeFind" , "found");
                            Intent i = new Intent(act , Rooms.class);
                            act.startActivity(i);
                            act.finish();
                        }
                        else {
                            TuyaHomeSdk.getHomeManagerInstance().createHome(p.projectName, 0, 0,"ksa",null, new ITuyaHomeResultCallback() {
                                @Override
                                public void onSuccess(HomeBean bean) {
                                    // do something
                                    THEHOME = bean ;
                                    MyApp.HOME = bean;
                                    Intent i = new Intent(act , Rooms.class);
                                    act.startActivity(i);
                                    act.finish();
                                }
                                @Override
                                public void onError(String errorCode, String errorMsg) {
                                    // do something
                                    new MessageDialog(errorMsg+" "+errorCode,"Create Home Failed",act);
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onError (String code, String error) {
                Log.d("tuyaLoginResp",error+" "+code);
            }
        });
    }

    public void Continue(View view) {
        Intent i = new Intent(act , Rooms.class);
        act.startActivity(i);
    }

    public void addControlDevice(loginCallback callback) {
        StringRequest re = new StringRequest(Request.Method.GET, THE_PROJECT.url + "roomsManagement/addControlDevice", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("addControlDevice" , response);
                if (response != null) {
                    try {
                        JSONObject resp = new JSONObject(response);
                        if (resp.getString("result").equals("success")) {
                            Log.d("addControlDevice" , resp.getString("result"));
                            JSONObject device = resp.getJSONObject("device");
                            editor.putString("Device_Id" , String.valueOf(device.getInt("id")));
                            editor.putString("Device_Name" , device.getString("name"));
                            MyApp.Device_Id = String.valueOf(device.getInt("id"));
                            MyApp.Device_Name = device.getString("name");
                            editor.apply();
                            callback.onSuccess();
                        }
                        else {
                            Log.d("addControlDevice" , resp.getString("error"));
                            callback.onFailed();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("addControlDevice" , e.toString());
                        callback.onFailed();
                    }
                }
                else {
                    Log.d("addControlDevice" , "response null");
                    callback.onFailed();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("addControlDevice" , error.toString());
                callback.onFailed();
            }
        });
        Volley.newRequestQueue(act).add(re);
    }
}

interface loginCallback {
    void onSuccess();
    void onFailed();
}