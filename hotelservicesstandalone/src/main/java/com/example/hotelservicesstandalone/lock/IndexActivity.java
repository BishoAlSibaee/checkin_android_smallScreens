package com.example.hotelservicesstandalone.lock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelservicesstandalone.R;

public class IndexActivity extends AppCompatActivity {

    Activity act = this ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        //LogIn.ActList.add(act);
       //binding = DataBindingUtil.setContentView(this, R.layout.activity_index);
        /*
        if(! LogIn.room.getLockName().equals("0") && ! LogIn.room.getLockGateway().equals("0"))
        {
            Intent i = new Intent(act ,UserLockActivity.class);
            startActivity(i);
        }
        else
        {
            initListener();
        }
        */
    }

    private void initListener()
    {
        Button goToLock  = (Button) findViewById(R.id.btn_lock);
        goToLock.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(act ,UserLockActivity.class);
                startActivity(i);
            }
        });
        Button goToGateway = (Button) findViewById(R.id.btn_gateway_index);
        goToGateway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(act,UserGatewayActivity.class);
                startActivity(i);
            }
        });
       // binding.btnLock.setOnClickListener(v -> {startTargetActivity(UserLockActivity.class);});
        //binding.btnGateway.setOnClickListener(v -> {startTargetActivity(UserGatewayActivity.class);});
    }

    public void keepGoing(View view)
    {
        /*
        if (LogIn.room.getLockName().equals("0"))
        {
            Intent i = new Intent(act , UserLockActivity.class);
            startActivity(i);
        }
        else if (LogIn.room.getLockGateway().equals("0"))
        {
            Intent i = new Intent(act , UserGatewayActivity.class);
            startActivity(i);
        }
        else
        {
            Intent i = new Intent(act , Tuya_Login.class);
            startActivity(i);
        }

         */

    }
}
