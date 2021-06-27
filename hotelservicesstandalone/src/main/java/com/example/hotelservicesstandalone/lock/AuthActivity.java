package com.example.hotelservicesstandalone.lock;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.hotelservicesstandalone.* ;
import com.ttlock.bl.sdk.util.DigitUtil;
import com.ttlock.bl.sdk.util.GsonUtil;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class AuthActivity extends AppCompatActivity {

    //ActivityAuthBinding binding;
    public AccountInfo accountInfo;
    private String password;
    String url = "https://open.ttlock.com.cn/oauth2/token";
    Context act ;
    Activity activ = this ;
    public static final String CLIENT_ID = "439063e312444f1f85050a52efcecd2e";
    public static final String CLIENT_SECRET = "0ef1c49b70c02ae6314bde603d4e9b05";
    public static final String REDIRECT_URI = "https://open.ttlock.com.cn";
    TextView user , pass ;
    public static AccountInfo acc ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_auth);
        //initListener();
        act = this ;
        user = (TextView) findViewById(R.id.et_account);
        pass = (TextView) findViewById(R.id.et_password);
        user.setText("basharsebai@gmail.com");
        pass.setText("Freesyria579251");
        auth();
    }



    public void go(View view)
    {
        auth();
    }

    private void auth() {
        final Dialog d = new Dialog(activ) ;
        d.setContentView(R.layout.loading_layout);
        d.setCancelable(false);
        d.show();
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        String account = user.getText().toString().trim();
        password = pass.getText().toString().trim();
        password = DigitUtil.getMD5(password);
        Call<String> call = apiService.auth(ApiService.CLIENT_ID, ApiService.CLIENT_SECRET, "password", account, password, ApiService.REDIRECT_URI);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                d.dismiss();
                String json = response.body();
                accountInfo = GsonUtil.toObject(json, AccountInfo.class);
                if (accountInfo != null)
                {
                    if (accountInfo.errcode == 0)
                    {
                        accountInfo.setMd5Pwd(password);
                        acc = accountInfo;
                        Intent i = new Intent(act ,IndexActivity.class );
                        startActivity(i);
                    } else
                    {
                        //ToastMaker.MakeToast(accountInfo.errmsg,act);
                        Calendar x = Calendar.getInstance(Locale.getDefault());
                        long time =  x.getTimeInMillis();
                        //ErrorRegister.rigestError(activ ,LogIn.room.getProjectName(),LogIn.room.getRoomNumber() , time ,004 ,accountInfo.errmsg , "LogIn To TTlock Account" );
                    }
                } else
                {
                    //ToastMaker.MakeToast(response.message() , act);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t)
            {
                //d.dismiss();
                //ToastMaker.MakeToast(t.getMessage() , act);
                Calendar x = Calendar.getInstance(Locale.getDefault());
                long time =  x.getTimeInMillis();
                //ErrorRegister.rigestError(activ , LogIn.room.getProjectName() , LogIn.room.getRoomNumber() , time ,004 ,accountInfo.errmsg , "LogIn To TTlock Account" );
            }
        });
    }
}
