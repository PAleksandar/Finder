package com.foodfinder.authentication;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LogInActivity extends AppCompatActivity {

    public static final int loginRequestCode=90;
    public static final String LOG_IN_DATA="LOG_IN_DATA";
    EditText emailInput, passwordInpit;
    Button btnLogIn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        setStatusBar();
        setActionBar();
        initializeComponent();
        setData();



        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logIn();
              //  logInUser("aleksandar@gmail.com","sifra123");



            }
        });

    }

    private void initializeComponent()
    {
        emailInput=(EditText) findViewById(R.id.email_log_in);
        passwordInpit=(EditText) findViewById(R.id.password_log_in);
        btnLogIn=(Button) findViewById(R.id.button_log_in);
        mAuth = FirebaseAuth.getInstance();

    }

    private void setActionBar()
    {
        ActionBar actionBar= getSupportActionBar();
        actionBar.hide();
    }

    private void setStatusBar()
    {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(this.getResources().getColor(R.color.grey));
        }
    }

    private void setData()
    {
        SharedPreferences prefs = getSharedPreferences(LOG_IN_DATA, MODE_PRIVATE);
        if(prefs!=null)
        {
            String email = prefs.getString("email", null);
            String password = prefs.getString("password", null);
            if (email != null && password!=null) {

                emailInput.setText(email);
                passwordInpit.setText(password);

            }
        }

    }

    private boolean validation(String email, String password)
    {
        boolean test;

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
        {
            test=false;
        }
        else {
            test=true;
        }

        return test;
    }

    private void logIn()
    {
        String email=emailInput.getText().toString();
        String password=passwordInpit.getText().toString();

        if(!validation(email, password))
        {
            Toast.makeText(LogInActivity.this, "Nisu popunjena sva polja", Toast.LENGTH_LONG).show();
            return;
        }

        logInUser(email, password);


    }

    private void logInUser(final String email, final String password) {


        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {

                    Toast.makeText(LogInActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                    Log.d("user!!!!!!!!!!!!!!!!!", "onComplete: radi auth !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                    String uid = "";
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        uid = user.getUid();
                    }

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("active");
                    ref.setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Write was successful!
                            // ...
                            Toast.makeText(LogInActivity.this, "LogOut Successfully completed", Toast.LENGTH_LONG).show();



                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Write failed
                                    // ...
                                    Toast.makeText(LogInActivity.this, "LogOut failed ", Toast.LENGTH_LONG).show();

                                }
                            });


                    SharedPreferences.Editor editor = getSharedPreferences(LOG_IN_DATA, MODE_PRIVATE).edit();
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.apply();

                    Bundle conData = new Bundle();
                    conData.putString("results", uid);
                    Intent intent = new Intent();
                    intent.putExtras(conData);
                    setResult(RESULT_OK, intent);
                    finish();


                } else {

                    Log.d("auth ERROR!!!!!!!!!!", "onComplete: ");
                    Toast.makeText(LogInActivity.this, "Authentication failed: Please Check UserName or Password", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

}
