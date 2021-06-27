package com.syriasoft.cleanup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

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

    public static String URL = "https://ratco-solutions.com/HotelServicesTest/TestProject/p/";
    public static String Project = "Test";
    private int SelectedHotel = 1 ;
    private String jobNumber ;
    private String password ;
    private messageDialog message ;
    private Activity act = this ;
    private String loginUrl ;
    public static UserDB db ;
    public static List<Activity> actList = new ArrayList<Activity>();
    private List<RESTAURANT_UNIT> Restaurants = new ArrayList<RESTAURANT_UNIT>();
    private Spinner facilities , deps;
    private String[] RESTAURANTS ;
    private RESTAURANT_UNIT THERESTAURANT ;
    private TextInputLayout pass , job ;
    LinearLayout loginLayout ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        actList.add(act);
        loginLayout = (LinearLayout) findViewById(R.id.layout);
        loginLayout.setVisibility(View.GONE);
        job = (TextInputLayout) findViewById(R.id.Login_jobNumber);
        pass = (TextInputLayout) findViewById(R.id.Login_password);
        facilities = (Spinner) findViewById(R.id.facility_spinner);
        facilities.setVisibility(View.GONE);
        facilities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                THERESTAURANT = Restaurants.get(position);
                Log.d("selectedfacility" , THERESTAURANT.Name+" " +THERESTAURANT.id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (!isNetworkConnected())
        {
            Toast.makeText(act, "لا يوجد اتصال بالانترنت .. تاكد من الاتصال بالانترنت", Toast.LENGTH_LONG).show();
            this.finish();
        }
        else
        {

                db = new UserDB(this);
                //db.logout();
                deps = (Spinner) findViewById(R.id.Login_department);
                final String[] items = new String[]{"Laundry", "Cleanup", "Restaurant", "RoomService", "Gym", "Service"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
                deps.setAdapter(adapter);
                deps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        if (items[position].equals("Restaurant"))
                        {
                            getRestaurants();
                            job.setHint("Enter User ");
                        }
                        else
                        {
                            facilities.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);

                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (db.isLogedIn())
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LinearLayout logo = (LinearLayout)findViewById(R.id.logo_layout);
                                Animation anim =  AnimationUtils.loadAnimation(act , R.anim.main_anim);
                                anim.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation)
                                    {
                                        Log.d("Yvalue",logo.getY()+"start");
                                        //logo.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        Log.d("Yvalue",logo.getY()+"end");
                                        logo.setY(360);
                                        //loginLayout.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {
                                        Log.d("Yvalue",logo.getY()+"repeat");
                                    }
                                });
                                logo.startAnimation(anim);

                            }
                        });

                        if (db.getUser().department.equals("Restaurant"))
                        {
                            Intent i = new Intent(act, RestaurantOrders.class);
                            i.putExtra("id" , db.getFacility());
                            startActivity(i);
                            act.finish();
                        }
                        else
                        {
                            Intent i = new Intent(act, MainActivity.class);
                            startActivity(i);
                            act.finish();
                        }
                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LinearLayout logo = (LinearLayout)findViewById(R.id.logo_layout);
                                Animation anim =  AnimationUtils.loadAnimation(act , R.anim.main_anim);
                                anim.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation)
                                    {
                                        Log.d("Yvalue",logo.getY()+"start");
                                        logo.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        Log.d("Yvalue",logo.getY()+"end");
                                        logo.setY(360);
                                        loginLayout.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {
                                        Log.d("Yvalue",logo.getY()+"repeat");
                                    }
                                });
                                logo.startAnimation(anim);

                            }
                        });

                    }
                }
            };
                Thread t = new Thread(r);
                t.start();

        }
    }

    public void logInBtn(View view)
    {


        if (deps.getSelectedItem().toString().equals("Restaurant"))
        {
            loginUrl = URL+"logInFacilityUser.php";
        }
        else
        {
            loginUrl = URL+"logInEmployees.php";
        }
        //Log.d("loginproblem" , jobNumber + password +THERESTAURANT.id +" " + loginUrl);
        final Spinner deps = (Spinner) findViewById(R.id.Login_department);
        jobNumber = job.getEditText().getText().toString();
        password = pass.getEditText().getText().toString();
        //Toast.makeText(act,String.valueOf(jobNumber.length()),Toast.LENGTH_LONG).show();
        if (jobNumber.length() > 0 )
        {
            if (password.length() >0)
            {
                final LoadingDialog d = new LoadingDialog(act);
                StringRequest re = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        //Toast.makeText(act , response , Toast.LENGTH_LONG).show();
                        if (response.equals("0"))
                        {
                            d.close();
                            message = new messageDialog("wrong job number or password" , "wrong entry",act);
                        }
                        else
                        {
                                d.close();
                                try {
                                    JSONArray arr = new JSONArray(response);
                                    JSONObject user = arr.getJSONObject(0);
                                    int id=0 ;
                                    String name="";
                                    int mobile=0 ;
                                    String department="" ;
                                    String token ="";
                                    int jobNumbe =0  ;
                                    if (deps.getSelectedItem().toString().equals("Restaurant"))
                                    {

                                            id = user.getInt("id");
                                            name = user.getString("Name");
                                            mobile = user.getInt("Mobile");
                                            department = "Restaurant";
                                            token = user.getString("token");
                                            jobNumbe = 0;

                                    }
                                    else
                                    {
                                         id = user.getInt("id");
                                         name = user.getString("name");
                                         mobile = user.getInt("mobile");
                                         department = user.getString("department");
                                         token = user.getString("token");
                                         jobNumbe = user.getInt("jobNumber");
                                    }

                                    int facility = 0 ;
                                    if (deps.getSelectedItem().toString().equals("Restaurant"))
                                    {
                                        facility = THERESTAURANT.id ;
                                    }
                                    if (db.insertUser(id,name,mobile,token,department,jobNumbe,facility))
                                    {
                                        Log.d("dabahtna" , "DABAHTNA" );
                                        if (db.getUser().department.equals("Restaurant"))
                                        {

                                            //db.insertFacility(THERESTAURANT.id);
                                            Log.d("facilityid" , LogIn.db.getFacility()+"" );
                                            Intent i = new Intent(act,RestaurantOrders.class);
                                            i.putExtra("id",THERESTAURANT.id);
                                            i.putExtra("hotel" , THERESTAURANT.Hotel);
                                            i.putExtra("name" , THERESTAURANT.Name);
                                            i.putExtra("photo" , THERESTAURANT.photo);
                                            i.putExtra("control" , THERESTAURANT.Control);
                                            i.putExtra("type" , THERESTAURANT.TypeName);
                                            i.putExtra("typeId" , THERESTAURANT.TypeId);
                                            startActivity(i);
                                            act.finish();
                                        }
                                        else
                                        {
                                            Intent i = new Intent(act,MainActivity.class);
                                            startActivity(i);
                                            act.finish();
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(act , "fdskfbjskhdf" , Toast.LENGTH_LONG).show();
                                    }


                                    } catch (JSONException e)
                                {
                                    e.printStackTrace();
                                    Toast.makeText(act , e.getMessage() , Toast.LENGTH_LONG).show();
                                }

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        d.close();
                        Toast.makeText(act , error.getMessage() , Toast.LENGTH_LONG).show();
                        Log.e("loginError" , error.getMessage());
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError
                    {
                        Map<String,String> params = new HashMap<String,String>();
                        if (deps.getSelectedItem().toString().equals("Restaurant"))
                        {
                            params.put("user" , jobNumber);
                            params.put("password",password);
                            params.put("facility",String.valueOf( THERESTAURANT.id ));
                            //Log.d("loginproblem" , jobNumber + password +THERESTAURANT.id +" " + loginUrl+ params.toString());
                        }
                        else
                        {
                            params.put("jobNumber" , jobNumber);
                            params.put("password",password);
                            params.put("department", deps.getSelectedItem().toString());
                            //Log.d("loginproblem" , jobNumber + password +THERESTAURANT.id +" " + loginUrl+ params.toString());
                        }

                        return params ;
                    }
                };
                Volley.newRequestQueue(act).add(re);
            }
            else
                {
                    message = new messageDialog("enter password" , "password",act);
                }
        }
        else
            {
                message = new messageDialog("enter jobnumber" , "job number",act);
            }
    }

    private boolean isNetworkConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void getRestaurants()
    {
        try
        {
            final LoadingDialog d = new LoadingDialog(act);
            String url = URL+"getRestaurantsOrCoffeeShops.php";
            StringRequest laundryRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    d.close();
                    if (response.equals("0"))
                    {

                    }
                    else
                    {
                        try
                        {
                            JSONArray arr = new JSONArray(response);
                            for (int i=0 ; i<arr.length();i++)
                            {
                                JSONObject row =arr.getJSONObject(i);
                                Restaurants.add(new RESTAURANT_UNIT(row.getInt("id"),row.getInt("Hotel"),row.getInt("TypeId"),row.getString("TypeName"),row.getString("Name"),row.getInt("Control"),row.getString("photo")));
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        if (Restaurants.size() > 0)
                        {
                            RESTAURANTS = new String[Restaurants.size()];
                            for (int i=0;i<Restaurants.size();i++)
                            {
                                RESTAURANTS[i] = Restaurants.get(i).Name ;
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,R.layout.spinner_item,RESTAURANTS);
                            facilities.setAdapter(adapter);
                            facilities.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            Toast.makeText(act,"No Restaurants In Your Hotel" , Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    d.close();
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String,String> par = new HashMap<String, String>();
                    par.put("Hotel" , String.valueOf(SelectedHotel));
                    return par;
                }
            };

            Volley.newRequestQueue(act).add(laundryRequest);
        }
        catch (Exception e)
        {

        }

    }

}
