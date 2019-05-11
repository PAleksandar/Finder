package com.example.myplaces;

import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Activity;

public class MyPlacesMaps2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places_maps2);
        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

}
