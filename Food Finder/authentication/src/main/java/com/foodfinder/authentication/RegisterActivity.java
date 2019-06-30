package com.foodfinder.authentication;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    public static final int registerRequestCode=91;
    private RegisterViewModel mViewModel;
    private EditText userEmail, userPassword, userName,lastName, userPhone, userBirthday;
    private Button DelivererRegisterButton;
    private Button CustomRegisterButton;
    private Button addImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mViewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);
        mViewModel.initializeViewModel(this, this);

        setActionBar();
        setStatusBar();
        initializeComponent();
        setCurrentDate();

        DelivererRegisterButton.setOnClickListener(delivererRegisterListener);
        CustomRegisterButton.setOnClickListener(customerRegisterListener);
        addImageButton.setOnClickListener(mViewModel.getAddImageListener());
        userBirthday.setOnClickListener(mViewModel.getAddDateListener());

        mViewModel.getBirthdayDate().observe(this, getBirthdayDateObserver());
    }

    private void initializeComponent()
    {
        userEmail = (EditText) findViewById(R.id.email_register);
        userPassword = (EditText) findViewById(R.id.password_register);
        userName = (EditText) findViewById(R.id.user_name_register);
        lastName=(EditText) findViewById(R.id.last_name_register) ;
        userPhone = (EditText) findViewById(R.id.phone_register);
        userBirthday = (EditText) findViewById(R.id.birthday_register);

        DelivererRegisterButton = (Button) findViewById(R.id.button_deliverer_reg);
        CustomRegisterButton = (Button) findViewById(R.id.button_custom_reg);
        addImageButton=(Button) findViewById(R.id.button_add_image);
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

    @Override
    protected void onStop() {
        super.onStop();

        mViewModel.stopProgresDialog();
    }

    private void setCurrentDate()
    {
        userBirthday.setText(DateFormat.format("yyyy.MM.dd",mViewModel.getCurrentDate()).toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mViewModel.onActivityResult(requestCode, resultCode, data);

    }

    View.OnClickListener delivererRegisterListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String Email = userEmail.getText().toString().trim();
            String Password = userPassword.getText().toString().trim();
            String UserName = userName.getText().toString().trim();
            String LastName=lastName.getText().toString().trim();
            String UserPhone = userPhone.getText().toString().trim();

            mViewModel.registerUser(true,Email,Password,UserName,LastName,UserPhone);
        }
    };

    View.OnClickListener customerRegisterListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String Email = userEmail.getText().toString().trim();
            String Password = userPassword.getText().toString().trim();
            String UserName = userName.getText().toString().trim();
            String LastName=lastName.getText().toString().trim();
            String UserPhone = userPhone.getText().toString().trim();

            mViewModel.registerUser(false,Email,Password,UserName,LastName,UserPhone);
        }
    };

    private Observer<Date> getBirthdayDateObserver()
    {
        return new Observer<Date>() {
            @Override
            public void onChanged(@Nullable Date date) {

                String s= DateFormat.format("yyyy.MM.dd", date).toString();
                userBirthday.setText(s);
            }
        };
    }

}
