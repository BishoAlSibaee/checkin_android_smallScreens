package com.syriasoft.cleanup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogIn extends AppCompatActivity {
    public static String URLGETPROJECT = "https://ratco-solutions.com/Checkin/getProjects.php";
    public static String Project = "";
    public static String TuyaUser = "";
    public static String TuyaPassword = "";
    public static String LockUser = "";
    public static String LockPassword = "";
    public static String URL;
    public final String SHARED_PREF_NAME = "MyPref";
    public final String KEY_PROJECT = "Project";
    public final String KEY_TuyaUser = "TuyaUser";
    public final String KEY_TuyaPassword = "TuyaPassword";
    public final String KEY_LockUser = "LockUser";
    public final String KEY_LockPassword = "LockPassword";
    public final String KEY_URL = "URL";
    public static String getpro;
    private ArrayList<String> getproList ;
    private int SelectedHotel = 1;
    private String jobNumber;
    private String password;
    private Activity act;
    private String loginUrl, checkUserUrl;
    public static UserDB db;
    public static List<Activity> actList;
    private List<RESTAURANT_UNIT> Restaurants ;
    private Spinner facilities, deps, projectName;
    private String[] RESTAURANTS;
    private RESTAURANT_UNIT THERESTAURANT;
    private TextInputLayout pass, job;
    LinearLayout loginLayout,logoLayout;
    TextView versionTV;
    int Version;
    int count = 0;
    ImageView imageView5;
    List<Projects> projectsList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor ;
    User MyUser ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        setActivity();
        setActivityActions();
        prepareDepartments();
        if (!isNetworkConnected()) {
            Toast.makeText(act, "لا يوجد اتصال بالانترنت .. تاكد من الاتصال بالانترنت", Toast.LENGTH_LONG).show();
            act.finish();
        }
        else {
            if (sharedPreferences.getString(KEY_PROJECT, null) != null) {
                Project = sharedPreferences.getString(KEY_PROJECT, null);
                MyApp.Project = Project;
                TuyaUser = sharedPreferences.getString(KEY_TuyaUser, null);
                MyApp.TuyaUser = TuyaUser;
                TuyaPassword = sharedPreferences.getString(KEY_TuyaPassword, null);
                MyApp.TuyaPassword = TuyaPassword;
                LockUser = sharedPreferences.getString(KEY_LockUser, null);
                MyApp.LockUser = LockUser;
                LockPassword = sharedPreferences.getString(KEY_LockPassword, null);
                MyApp.LockPassword = LockPassword;
                URL = sharedPreferences.getString(KEY_URL, null);//"https://ratco-solutions.com/Checkin/" + Project + "/php/";
                MyApp.URL = URL;
                projectName.setVisibility(View.GONE);
                if (sharedPreferences.getString("JobNumber", null) == null) {
                    loginLayout.setVisibility(View.VISIBLE);
                }
                else {
                    if (sharedPreferences.getString("JobNumber", null).equals("0")) {
                        int id = Integer.parseInt(sharedPreferences.getString("Id", null));
                        int jn = Integer.parseInt(sharedPreferences.getString("JobNumber", null));
                        String name = sharedPreferences.getString("Name", null);
                        String dep = sharedPreferences.getString("Department", null);
                        MyApp.Token = sharedPreferences.getString("my_token", null);
                        MyApp.My_USER = new User(id,name,jn,0,dep,MyApp.Token,"",1);
                        Intent i = new Intent(act, RestaurantOrders.class);
                        startActivity(i);
                        act.finish();
                    }
                    else {
                        int id = Integer.parseInt(sharedPreferences.getString("Id", null));
                        int jn = Integer.parseInt(sharedPreferences.getString("JobNumber", null));
                        String name = sharedPreferences.getString("Name", null);
                        String dep = sharedPreferences.getString("Department", null);
                        String control = sharedPreferences.getString("Control", null);
                        MyApp.Token = sharedPreferences.getString("my_token", null);
                        checkUser(String.valueOf(id),new VolleyCallback() {
                            @Override
                            public void onSuccess(String res) {
                                if (res.equals("1")) {
                                    MyUser = new User(id,name,jn,0,dep,"",control,1);
                                    MyApp.My_USER = MyUser;
                                    Intent i = new Intent(act, MainActivity.class);
                                    startActivity(i);
                                    act.finish();
                                }
                                else {
                                    loginLayout.setVisibility(View.VISIBLE);
                                    logoLayout.setVisibility(View.VISIBLE);
                                    new messageDialog("Your account has been deleted", "Warning", act);
                                }
                            }

                            @Override
                            public void onFailed(String error) {
                                int id = Integer.parseInt(sharedPreferences.getString("Id", null));
                                int jn = Integer.parseInt(sharedPreferences.getString("JobNumber", null));
                                String name = sharedPreferences.getString("Name", null);
                                String dep = sharedPreferences.getString("Department", null);
                                String control = sharedPreferences.getString("Control", null);
                                MyUser = new User(id,name,jn,0,dep,"",control,1);
                                MyApp.My_USER = MyUser;
                                Intent i = new Intent(act, MainActivity.class);
                                startActivity(i);
                                act.finish();
                            }
                        });
                    }
                }
            } else {
                getProjectName();
                projectName.setVisibility(View.VISIBLE);
                if (sharedPreferences.getString("JobNumber", null) == null) {
                    loginLayout.setVisibility(View.VISIBLE);
                }
            }
        }
//        if (!isNetworkConnected()) {
//            Toast.makeText(act, "لا يوجد اتصال بالانترنت .. تاكد من الاتصال بالانترنت", Toast.LENGTH_LONG).show();
//            this.finish();
//        }
//        else {

//            Runnable r = new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    if (db.isLogedIn()) {
//                        String projectValue = sharedPreferences.getString(KEY_PROJECT, null);
//                        if (projectValue != null) {
//                            Project = projectValue;
//                            URL = "https://ratco-solutions.com/Checkin/" + Project + "/php/";
//                        }
//                        checkUser(new VolleyCallback() {
//                            @Override
//                            public void onSuccess(String res) {
//                                if (res.equals("1")) {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            loginLayout.setVisibility(View.GONE);
//                                        }
//                                    });
//                                    if (db.getUser().department.equals("Restaurant")) {
//                                        Intent i = new Intent(act, RestaurantOrders.class);
//                                        i.putExtra("id", db.getFacility());
//                                        startActivity(i);
//                                        act.finish();
//                                    } else {
//                                        Intent i = new Intent(act, MainActivity.class);
//                                        startActivity(i);
//                                        act.finish();
//                                    }
//                                } else if (res.equals("0")) {
//                                    LogIn.db.logout();
//                                    LinearLayout logo = findViewById(R.id.logo_layout);
//                                    loginLayout.setVisibility(View.VISIBLE);
//                                    logo.setVisibility(View.VISIBLE);
//                                    new messageDialog("Your account has been deleted", "Warning", act);
//                                }
//                            }
//
//                            @Override
//                            public void onFailed(String error) {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        loginLayout.setVisibility(View.GONE);
//                                    }
//                                });
//                                if (db.getUser().department.equals("Restaurant")) {
//                                    Intent i = new Intent(act, RestaurantOrders.class);
//                                    i.putExtra("id", db.getFacility());
//                                    startActivity(i);
//                                    act.finish();
//                                } else {
//                                    Intent i = new Intent(act, MainActivity.class);
//                                    startActivity(i);
//                                    act.finish();
//                                }
//                            }
//                        });
//                    } else {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                LinearLayout logo = (LinearLayout) findViewById(R.id.logo_layout);
//                                loginLayout.setVisibility(View.VISIBLE);
//                                logo.setVisibility(View.VISIBLE);
//                            }
//                        });
//                    }
//                }
//            };
//            Thread t = new Thread(r);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    void setActivity() {
        act = this;
        actList = new ArrayList<>();
        actList.add(act);
        projectsList = new ArrayList<>();
        Restaurants = new ArrayList<>();
        getproList = new ArrayList<>();
        deps = findViewById(R.id.Login_department);
        loginLayout = findViewById(R.id.loginLayout);
        loginLayout.setVisibility(View.GONE);
        job = findViewById(R.id.Login_jobNumber);
        pass = findViewById(R.id.Login_password);
        facilities = findViewById(R.id.facility_spinner);
        projectName = findViewById(R.id.projectName);
        versionTV = findViewById(R.id.textView10);
        Version = BuildConfig.VERSION_CODE;
        versionTV.setText("Version " + Version);
        imageView5 = findViewById(R.id.imageView5);
        logoLayout = findViewById(R.id.logo_layout);
        logoLayout.setVisibility(View.VISIBLE);
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    void setActivityActions() {
        imageView5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                count++;
                if (count == 4) {
                    Project = null;
                    URL = null;
                    getProjectName();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_PROJECT, null);
                    editor.putString(KEY_TuyaUser, null);
                    editor.putString(KEY_TuyaPassword, null);
                    editor.putString(KEY_LockUser, null);
                    editor.putString(KEY_LockPassword, null);
                    editor.apply();
                    LockPassword = TuyaUser = TuyaPassword = LockUser = null;
                    projectName.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        projectName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Project = projectsList.get(position).projectName;
                URL = projectsList.get(position).Url;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        facilities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                THERESTAURANT = Restaurants.get(position);
                Log.d("selectedfacility", THERESTAURANT.Name + " " + THERESTAURANT.id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void logInBtn(View view) {
        if (deps.getSelectedItem().toString().equals("Restaurant")) {
            loginUrl = URL + "facilitys/loginFacilityUser";
        } else {
            loginUrl = URL + "users/login";
        }
        jobNumber = job.getEditText().getText().toString();
        password = pass.getEditText().getText().toString();
        if (jobNumber.length() > 0) {
            if (password.length() > 0) {
                final LoadingDialog d = new LoadingDialog(act);
                StringRequest re = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("loginResp" , response+" "+loginUrl);
                        d.close();
                        try {
                            JSONObject result = new JSONObject(response);
                            if (result.getString("result").equals("success")) {
                                Log.d("loginResp" , "success");
                                if (sharedPreferences.getString(KEY_PROJECT, null) == null) {
                                    editor.putString(KEY_PROJECT, projectsList.get(projectName.getSelectedItemPosition()).projectName);
                                    MyApp.Project = projectsList.get(projectName.getSelectedItemPosition()).projectName;
                                    editor.putString(KEY_TuyaUser, projectsList.get(projectName.getSelectedItemPosition()).TuyaUser);
                                    MyApp.TuyaUser = projectsList.get(projectName.getSelectedItemPosition()).TuyaUser;
                                    editor.putString(KEY_TuyaPassword, projectsList.get(projectName.getSelectedItemPosition()).TuyaPassword);
                                    MyApp.TuyaPassword = projectsList.get(projectName.getSelectedItemPosition()).TuyaPassword;
                                    editor.putString(KEY_LockUser, projectsList.get(projectName.getSelectedItemPosition()).LockUser);
                                    MyApp.LockUser = projectsList.get(projectName.getSelectedItemPosition()).LockUser;
                                    editor.putString(KEY_LockPassword, projectsList.get(projectName.getSelectedItemPosition()).LockPassword);
                                    MyApp.LockPassword = projectsList.get(projectName.getSelectedItemPosition()).LockPassword;
                                    editor.putString(KEY_URL, projectsList.get(projectName.getSelectedItemPosition()).Url);
                                    MyApp.URL = projectsList.get(projectName.getSelectedItemPosition()).Url;
                                    editor.apply();
                                }
                                Project = sharedPreferences.getString(KEY_PROJECT, null);
                                URL = sharedPreferences.getString(KEY_URL, null);
                                JSONObject user = new JSONObject(result.getString("user"));
                                MyApp.Token = result.getString("my_token");
                                editor.putString("my_token", result.getString("my_token"));
                                editor.putString("Id", String.valueOf(user.getInt("id")));
                                Log.d("loginResp" , deps.getSelectedItem().toString());
                                if (deps.getSelectedItem().toString().equals("Restaurant")) {
                                    Log.d("loginResp" , "restaurant");
                                    MyApp.My_USER = new User(user.getInt("id"),user.getString("Name"),0,user.getInt("Mobile"),"Restaurant",user.getString("token"),"",1);
                                    editor.putString("JobNumber", String.valueOf(0));
                                    editor.putString("Name", user.getString("Name"));
                                    editor.putString("Department", "Restaurant");
                                    editor.putString("FacilityId", String.valueOf(THERESTAURANT.id));
                                    editor.putString("FacilityName",THERESTAURANT.Name);
                                    editor.putString("FacilityPhoto",THERESTAURANT.photo);
                                    editor.putString("FacilityType",THERESTAURANT.TypeName);
                                    editor.putString("FacilityTypeId", String.valueOf(THERESTAURANT.TypeId));
                                    editor.apply();
                                    Intent i = new Intent(act, RestaurantOrders.class);
                                    startActivity(i);
                                    act.finish();
                                }
                                else {
                                    if (user.getInt("logedin") == 0) {
                                        new messageDialog("Your account has been deleted", "Warning", act);
                                    }
                                    else {
                                        MyApp.My_USER = new User(user.getInt("id"),user.getString("name"),user.getInt("jobNumber"),user.getInt("mobile"),user.getString("department"),user.getString("token"),user.getString("control"),user.getInt("logedin"));
                                        editor.putString("JobNumber", String.valueOf(user.getInt("jobNumber")));
                                        editor.putString("Name", user.getString("name"));
                                        editor.putString("Control", user.getString("control"));
                                        editor.putString("Department", user.getString("department"));
                                        editor.apply();
                                        Intent i = new Intent(act, MainActivity.class);
                                        startActivity(i);
                                        act.finish();
                                    }
                                }
                            }
                            else {
                               new messageDialog(result.getString("error"),"Failed",act);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("loginResp" , e.getMessage());
                        }
//                        if (response.equals("0")) {
//                            d.close();
//                            message = new messageDialog("wrong job number or password", "wrong entry", act);
//                        } else {
//                            if (sharedPreferences.getString(KEY_PROJECT, null) != null && sharedPreferences.getString(KEY_TuyaUser, null) != null) {
//                                Project = sharedPreferences.getString(KEY_PROJECT, null);
//                                TuyaUser = sharedPreferences.getString(KEY_TuyaUser, null);
//                                TuyaPassword = sharedPreferences.getString(KEY_TuyaPassword, null);
//                                LockUser = sharedPreferences.getString(KEY_LockUser, null);
//                                LockPassword = sharedPreferences.getString(KEY_LockPassword, null);
//                                URL = sharedPreferences.getString(URL, null);//"https://ratco-solutions.com/Checkin/" + Project + "/php/";
//                            } else {
//                                Project = projectName.getSelectedItem().toString();
//                                URL = projectsList.get(projectName.getSelectedItemPosition()).Url ; //"https://ratco-solutions.com/Checkin/" + Project + "/php/";
//                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                editor.putString(KEY_PROJECT, Project);
//                                editor.putString(KEY_TuyaUser, projectsList.get(projectName.getSelectedItemPosition()).TuyaUser);
//                                editor.putString(KEY_TuyaPassword, projectsList.get(projectName.getSelectedItemPosition()).TuyaPassword);
//                                editor.putString(KEY_LockUser, projectsList.get(projectName.getSelectedItemPosition()).LockUser);
//                                editor.putString(KEY_LockPassword, projectsList.get(projectName.getSelectedItemPosition()).LockPassword);
//                                editor.putString(URL, projectsList.get(projectName.getSelectedItemPosition()).Url);
//                                editor.apply();
//                            }
//                            try {
//                                JSONObject user = new JSONObject(response);
//                                editor.putString("Id", String.valueOf(user.getInt("id")));
//                                editor.putString("Name", user.getString("name"));
//                                editor.putString("Department", "Restaurant");
//                                editor.putString("Control", user.getString("control"));
//                                if (deps.getSelectedItem().toString().equals("Restaurant")) {
//                                    MyApp.My_USER = new User(user.getInt("id"),user.getString("name"),0,user.getInt("mobile"),"Restaurant",user.getString("token"),user.getString("control"),user.getInt("logedin"));
//                                    editor.putString("JobNumber", String.valueOf(0));
//                                    Intent i = new Intent(act, RestaurantOrders.class);
//                                    i.putExtra("id", THERESTAURANT.id);
//                                    i.putExtra("hotel", THERESTAURANT.Hotel);
//                                    i.putExtra("name", THERESTAURANT.Name);
//                                    i.putExtra("photo", THERESTAURANT.photo);
//                                    i.putExtra("control", THERESTAURANT.Control);
//                                    i.putExtra("type", THERESTAURANT.TypeName);
//                                    i.putExtra("typeId", THERESTAURANT.TypeId);
//                                    startActivity(i);
//                                    act.finish();
//                                }
//                                else {
//                                    MyApp.My_USER = new User(user.getInt("id"),user.getString("name"),user.getInt("jobNumber"),user.getInt("mobile"),user.getString("department"),user.getString("token"),user.getString("control"),user.getInt("logedin"));
//                                    editor.putString("JobNumber", String.valueOf(user.getInt("jobNumber")));
//                                    Intent i = new Intent(act, MainActivity.class);
//                                    startActivity(i);
//                                    act.finish();
//                                }
//
//                                int facility = 0;
//                                if (deps.getSelectedItem().toString().equals("Restaurant")) {
//                                    facility = THERESTAURANT.id;
//                                }
//                                if (db.insertUser(id, name, mobile, token, department, jobNumbe, facility, Version)) {
//                                    if (db.getUser().department.equals("Restaurant")) {
//                                        Log.d("facilityid", LogIn.db.getFacility() + "");
//                                        Intent i = new Intent(act, RestaurantOrders.class);
//                                        i.putExtra("id", THERESTAURANT.id);
//                                        i.putExtra("hotel", THERESTAURANT.Hotel);
//                                        i.putExtra("name", THERESTAURANT.Name);
//                                        i.putExtra("photo", THERESTAURANT.photo);
//                                        i.putExtra("control", THERESTAURANT.Control);
//                                        i.putExtra("type", THERESTAURANT.TypeName);
//                                        i.putExtra("typeId", THERESTAURANT.TypeId);
//                                        startActivity(i);
//                                        act.finish();
//                                    } else {
//                                        Intent i = new Intent(act, MainActivity.class);
//                                        startActivity(i);
//                                        act.finish();
//                                    }
//                                } else {
//                                    Toast.makeText(act, "fdskfbjskhdf", Toast.LENGTH_LONG).show();
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                                Toast.makeText(act, e.getMessage(), Toast.LENGTH_LONG).show();
//                            }
//                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        d.close();
                        Toast.makeText(act, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        if (deps.getSelectedItem().toString().equals("Restaurant")) {
                            params.put("user", jobNumber);
                            params.put("password", password);
                            params.put("facility_id", String.valueOf(THERESTAURANT.id));
                        } else {
                            params.put("job_number", jobNumber);
                            params.put("password", password);
                            params.put("department", deps.getSelectedItem().toString());
                        }
                        return params;
                    }
                };
                Volley.newRequestQueue(act).add(re);
            }
            else {
                new messageDialog("enter password", "password", act);
            }
        } else {
            new messageDialog("enter jobnumber", "job number", act);
        }
    }

    void prepareDepartments() {
        final String[] items = new String[]{"Service", "Laundry", "Cleanup", "Restaurant", "RoomService", "Gym"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        deps.setAdapter(adapter);
        deps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (items[position].equals("Restaurant") || items[position].equals("CoffeeShop")) {
                    getRestaurants();
                    job.setHint("Enter User ");
                } else {
                    facilities.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void getProjectName() {
        StringRequest request = new StringRequest(Request.Method.POST, URLGETPROJECT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        projectsList.add(new Projects(object.getInt("id"), object.getString("projectName"), object.getString("city"), object.getString("salesman"), object.getString("TuyaUser"), object.getString("TuyaPassword"), object.getString("LockUser"), object.getString("LockPassword"),object.getString("url")));
                        getpro = object.getString("projectName");
                        getproList.add(getpro);
                    }
                    ArrayAdapter<String> adapterProj = new ArrayAdapter<>(act, R.layout.spinner_item, getproList);
                    projectName.setAdapter(adapterProj);
                } catch (JSONException e) {
                    new messageDialog(e.getMessage(),"error",act);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new AlertDialog.Builder(act).setTitle("Failed").setMessage("failed to get projects data .. try again ??").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        act.finish();
                    }
                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getProjectName();
                    }
                }).create().show();
            }
        });
        Volley.newRequestQueue(act).add(request);
    }

    private void checkUser(String userId,VolleyCallback callback) {
        checkUserUrl = URL + "users/checkUser";
        StringRequest request = new StringRequest(Request.Method.POST, checkUserUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("checkUser" , response);
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("checkUser" , error.toString());
                callback.onFailed(error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parms = new HashMap<String, String>();
                parms.put("user_id", userId);
                return parms;
            }
        };
        Volley.newRequestQueue(act).add(request);
    }

    private void getRestaurants() {
            LoadingDialog d = new LoadingDialog(act);
            String url = URL + "facilitys/getfacilitys";
            StringRequest laundryRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    d.close();
                    try {
                        JSONArray arr = new JSONArray(response);
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject row = arr.getJSONObject(i);
                            if (row.getString("TypeName").equals("Restaurant") || row.getString("TypeName").equals("CoffeeShop")) {
                                Restaurants.add(new RESTAURANT_UNIT(row.getInt("id"), row.getInt("Hotel"), row.getInt("TypeId"), row.getString("TypeName"), row.getString("Name"), row.getInt("Control"), row.getString("photo")));
                            }
                        }
                        if (Restaurants.size() > 0) {
                            RESTAURANTS = new String[Restaurants.size()];
                            for (int i = 0; i < Restaurants.size(); i++) {
                                RESTAURANTS[i] = Restaurants.get(i).Name;
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(act, R.layout.spinner_item, RESTAURANTS);
                            facilities.setAdapter(adapter);
                            facilities.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(act, "No Restaurants In Your Hotel", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        AlertDialog.Builder B = new AlertDialog.Builder(act);
                        B.setTitle("error").setMessage("error getting restaurants data .. try again ??").setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getRestaurants();
                            }
                        }).create().show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    AlertDialog.Builder B = new AlertDialog.Builder(act);
                    B.setTitle("error").setMessage("error getting restaurants data .. try again ??").setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getRestaurants();
                        }
                    }).create().show();
                    d.close();
                }
            });
            Volley.newRequestQueue(act).add(laundryRequest);
    }

}

interface VolleyCallback {
    void onSuccess(String res);

    void onFailed(String error);
}

//                                    Project = sharedPreferences.getString(KEY_PROJECT, null);
//                                    TuyaUser = sharedPreferences.getString(KEY_TuyaUser, null);
//                                    TuyaPassword = sharedPreferences.getString(KEY_TuyaPassword, null);
//                                    LockUser = sharedPreferences.getString(KEY_LockUser, null);
//                                    LockPassword = sharedPreferences.getString(KEY_LockPassword, null);
//                                    URL = sharedPreferences.getString(URL, null);//"https://ratco-solutions.com/Checkin/" + Project + "/php/";