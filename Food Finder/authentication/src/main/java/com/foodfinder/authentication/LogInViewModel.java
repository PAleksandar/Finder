package com.foodfinder.authentication;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

import java.util.concurrent.Executor;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.foodfinder.authentication.LogInActivity.LOG_IN_DATA;

public class LogInViewModel extends ViewModel {

    private FirebaseAuth mAuth;

    private Context mContext;
    private Activity mActivity;


    public void initializeViewModel(Context context, Activity activity)
    {
        mAuth = FirebaseAuth.getInstance();
        mContext=context;
        mActivity=activity;
    }


    public String getLastEmail()
    {
        SharedPreferences prefs =  mContext.getSharedPreferences(LOG_IN_DATA, MODE_PRIVATE);
        if(prefs!=null)
        {
            String email = prefs.getString("email", null);
            if (email != null) {

               return email;

            }
        }

        return "";

    }

    public String getLastPassword()
    {
        SharedPreferences prefs =  mContext.getSharedPreferences(LOG_IN_DATA, MODE_PRIVATE);
        if(prefs!=null)
        {
            String password = prefs.getString("password", null);
            if (password!=null) {

                return password;

            }
        }

        return "";

    }

    public View.OnClickListener getLogInOnClickListener(final EditText email, final EditText password)
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                logIn(email.getText().toString(),password.getText().toString());

            }
        };
    }

    private void logIn(String email, String password)
    {

        if(!validation(email, password))
        {
            Toast.makeText(mContext, "Nisu popunjena sva polja", Toast.LENGTH_LONG).show();
            return;
        }


        logInUser(email,password);


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

    public void logInUser(final String email, final String password) {


        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener( mActivity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {

                    Toast.makeText(mContext, "Login Successful", Toast.LENGTH_LONG).show();

                    String uid = "";
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        uid = user.getUid();
                    }

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("active");
                    ref.setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });


                    SharedPreferences.Editor editor = mContext.getSharedPreferences(LOG_IN_DATA, MODE_PRIVATE).edit();
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.apply();

                    Bundle conData = new Bundle();
                    conData.putString("results", uid);
                    Intent intent = new Intent();
                    intent.putExtras(conData);
                    mActivity.setResult(RESULT_OK, intent);
                    mActivity.finish();


                } else {

                    Toast.makeText(mContext, "Authentication failed: Please Check UserName or Password", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

}
