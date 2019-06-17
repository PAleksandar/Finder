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
import android.widget.EditText;
import android.widget.Toast;

import com.foodfinder.acount.Account;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText userEmail, userPassword, userName, userPhone, userBirthday;
    Button DelivererRegisterButton;
    Button CustomRegisterButton;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    public static final int registerRequestCode=91;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setActionBar();
        setStatusBar();
        initializeComponent();

        DelivererRegisterButton.setOnClickListener(delivererRegisterListener);
        CustomRegisterButton.setOnClickListener(customerRegisterListener);

    }


    View.OnClickListener delivererRegisterListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String Email = userEmail.getText().toString();
            String Password = userPassword.getText().toString();

            registerUser(Email, Password, true);
        }
    };

    View.OnClickListener customerRegisterListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String Email = userEmail.getText().toString();
            String Password = userPassword.getText().toString();

            registerUser(Email, Password, false);
        }
    };

    private void initializeComponent()
    {
        mAuth = FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance();
        mRef=mDatabase.getReference();

        userEmail = (EditText) findViewById(R.id.email_register);
        userPassword = (EditText) findViewById(R.id.password_register);
        userName = (EditText) findViewById(R.id.user_name_register);
        userPhone = (EditText) findViewById(R.id.phone_register);
        userBirthday = (EditText) findViewById(R.id.birthday_register);

        DelivererRegisterButton = (Button) findViewById(R.id.button_deliverer_reg);
        CustomRegisterButton = (Button) findViewById(R.id.button_custom_reg);
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

    private void saveUser(boolean isDriver)
    {
        String uid = "";
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }
        String Email = userEmail.getText().toString();
        String Password = userPassword.getText().toString();
        String UserName = userName.getText().toString();
        String UserPhone = userPhone.getText().toString();
        String UserBirthday = userBirthday.getText().toString();
        String UserImage = "no image";

        Account account=new Account();
        account.setUserId(uid);
        account.setUserName(UserName);
        account.setEmail(Email);
        account.setPassword(Password);
        account.setPhone(UserPhone);
        account.setBirthday(UserBirthday);
        account.setProfileImage(UserImage);
        account.setActive(true);
        account.setDriver(isDriver);

        DatabaseReference ref = mRef.child("users").child(uid);
        ref.setValue(account).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                // ...
                Toast.makeText(RegisterActivity.this, "Registration Successfully completed", Toast.LENGTH_LONG).show();
                startNavigationActivity();


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        // ...
                        Toast.makeText(RegisterActivity.this, "Registration failed ", Toast.LENGTH_LONG).show();

                    }
                });;
    }

    private void startNavigationActivity()
    {
        Bundle conData = new Bundle();
        conData.putString("results", "Thanks Thanks");
        Intent intent = new Intent();
        intent.putExtras(conData);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void registerUser(String email, String password, final boolean isDriver) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Authentication Successfully completed", Toast.LENGTH_LONG).show();

                    saveUser(isDriver);



                } else {
                    Toast.makeText(RegisterActivity.this, "Authentication failed User already exits", Toast.LENGTH_LONG).show();
                }

            }


        });




    }
}
