package com.foodfinder.authentication;


import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
    Button btnLogIn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        setStatusBar();
        setActionBar();

        btnLogIn=(Button) findViewById(R.id.button_log_in);
        mAuth = FirebaseAuth.getInstance();

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logInUser("aleksandar@gmail.com","sifra123");



            }
        });

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

    private void logInUser(String email, String password) {

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

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("isActive");
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
