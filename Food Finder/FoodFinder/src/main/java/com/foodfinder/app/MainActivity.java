package com.foodfinder.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.foodfinder.authentication.TestBase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FireBaseTest bt=new FireBaseTest();
        TestBase bt=new TestBase();

        Log.d("!!!!!!!!radi!!!!", "onCreate:  ");
        bt.readData();
        Log.d("!!!!!!!!radi!!!!", "onCreate:  ");

    }
}
