package com.foodfinder.authentication;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    EditText userEmail, userPassword;
    Button DelivererRegisterButton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(this.getResources().getColor(R.color.grey));
        }

        mAuth = FirebaseAuth.getInstance();

        userEmail = (EditText) findViewById(R.id.email_register);
        userPassword = (EditText) findViewById(R.id.password_register);
        DelivererRegisterButton = (Button) findViewById(R.id.button_deliverer_reg);


        DelivererRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Email = userEmail.getText().toString();
                String Password = userPassword.getText().toString();

                registerUser(Email, Password);
            }
        });

    }

    private void registerUser(String email, String password) {

        //This method will create new User on firebase console...
//        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//
//                if (task.isSuccessful()) {
//                    // SignUp in success, Direct to the dashboard Page...
//                    Toast.makeText(RegisterActivity.this, "Authentication Successfully completed", Toast.LENGTH_SHORT).show();
//                   // Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                   // startActivity(intent);
//
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Toast.makeText(RegisterActivity.this, "Authentication failed User already exits",
//                            Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//
//        });

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    // Login in success, Direct to Dashboard page

//                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(LoginActivity.this,DashboardActivity.class);
//                    startActivity(intent);
                    Log.d("user!!!!!!!!!!!!!!!!!", "onComplete: radi auth !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");


                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("auth ERROR!!!!!!!!!!", "onComplete: ");
//                    Toast.makeText(LoginActivity.this, "Authentication failed: Please Check UserName or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
