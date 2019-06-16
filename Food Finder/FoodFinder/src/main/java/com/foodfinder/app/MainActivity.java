package com.foodfinder.app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    EditText userEmail, userPassword;
    Button DelivererRegisterButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar= getSupportActionBar();
        actionBar.hide();

        //TestBase bt=new TestBase();

        Log.d("!!!!!!!!radi!!!!", "onCreate:  ");
       // bt.readData();
        Log.d("!!!!!!!!radi!!!!", "onCreate:  ");

        Intent intent = new Intent(this, NavigationActivity.class);
        startActivity(intent);

//        mAuth = FirebaseAuth.getInstance();
//
//        mAuth.createUserWithEmailAndPassword("aca@gmail.com", "sifra12345").addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//
//                if (task.isSuccessful()) {
//                    // SignUp in success, Direct to the dashboard Page...
//                    Toast.makeText(MainActivity.this, "Authentication Successfully completed", Toast.LENGTH_SHORT).show();
//                   // Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                   // startActivity(intent);
//
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Toast.makeText(MainActivity.this, "Authentication failed User already exits",
//                            Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//
//        });

    }
}
