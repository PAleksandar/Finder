package com.foodfinder.authentication;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class LogInActivity extends AppCompatActivity {

    public static final int loginRequestCode=90;
    public static final String LOG_IN_DATA="LOG_IN_DATA";
    private LogInViewModel mViewModel;
    private EditText emailInput;
    private EditText passwordInpit;
    private Button btnLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mViewModel = ViewModelProviders.of(this).get(LogInViewModel.class);
        mViewModel.initializeViewModel(this, this);

        setStatusBar();
        setActionBar();
        initializeComponent();
        setData();

        btnLogIn.setOnClickListener(mViewModel.getLogInOnClickListener(emailInput.getText().toString(),passwordInpit.getText().toString()));


    }

    private void initializeComponent()
    {
        emailInput=(EditText) findViewById(R.id.email_log_in);
        passwordInpit=(EditText) findViewById(R.id.password_log_in);
        btnLogIn=(Button) findViewById(R.id.button_log_in);

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
        emailInput.setText(mViewModel.getLastEmail());
        passwordInpit.setText(mViewModel.getLastPassword());
    }


}
