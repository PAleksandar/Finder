package com.foodfinder.app;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.foodfinder.authentication.LogInActivity;
import com.foodfinder.authentication.RegisterActivity;
import com.foodfinder.authentication.TestBase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    public static final int  loginRequestCode=LogInActivity.loginRequestCode;
    public static final int registerRequestCode=RegisterActivity.registerRequestCode;

    Button btnLogIn;
    Button btnRegister;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setActionBar();
        initializeComponent();

        btnLogIn.setOnClickListener(logInListener);
        btnRegister.setOnClickListener(registerListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case loginRequestCode:
                if (resultCode == RESULT_OK) {
                    Bundle res = data.getExtras();
                    String result = res.getString("results");
                    Log.d("FIRST", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!result:"+result);
                    Intent intent = new Intent(mContext, NavigationActivity.class);
                    finish();
                    startActivity(intent);

                }
                break;
            case registerRequestCode:
                if (resultCode == RESULT_OK) {
                    Bundle res = data.getExtras();
                    String result = res.getString("results");
//                    Log.d("FIRST", "result:"+result);
                    Intent intent = new Intent(mContext, NavigationActivity.class);
                    finish();
                    startActivity(intent);
                }
                break;
        }
    }

    View.OnClickListener logInListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, LogInActivity.class);
            startActivityForResult(intent,loginRequestCode);
        }
    };

    View.OnClickListener registerListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, RegisterActivity.class);
            startActivityForResult(intent,registerRequestCode);
        }
    };

    private void setActionBar()
    {
        ActionBar actionBar= getSupportActionBar();
        actionBar.hide();
    }

    private void initializeComponent()
    {
        btnLogIn=(Button) findViewById(R.id.button_log_in);
        btnRegister=(Button) findViewById(R.id.button_register);
        mContext=getApplicationContext();
    }
}
