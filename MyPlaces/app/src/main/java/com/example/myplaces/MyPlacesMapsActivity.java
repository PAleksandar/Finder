package com.example.myplaces;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.prefs.PreferenceChangeEvent;

import static com.example.myplaces.MainActivity.NEW_PLACE;

public class MyPlacesMapsActivity extends AppCompatActivity {

    MapView map=null;
    IMapController mapController=null;
    MyLocationNewOverlay myLocationOverlay;
    static int NEW_PLACE=1;
    static final int PERMISSION_ACCESS_FINE_LOCATION=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places_maps);

          // Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
           // setSupportActionBar(toolbar);


        // FloatingActionButton fab=(FloatingActionButton) findViewById(R.id.fab);


        FloatingActionButton fab=(FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MyPlacesMapsActivity.this, EditMyPlaceActivity.class);
                MyPlacesMapsActivity.this.startActivityForResult(intent, NEW_PLACE);
            }
        });

        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        map=(MapView) findViewById(R.id.map);
        map.setMultiTouchControls(true);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
        }

        mapController=map.getController();
        if(mapController!=null)
        {
            mapController.setZoom(15.0);
            GeoPoint startPoint=new GeoPoint(43.3209, 21.8958);
            mapController.setCenter(startPoint);
        }
    }

    private void setMyLocationOverlay(){
       myLocationOverlay =new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
       myLocationOverlay.enableMyLocation();
       map.getOverlays().add(myLocationOverlay);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case PERMISSION_ACCESS_FINE_LOCATION: {
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    setMyLocationOverlay();
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_my_places_maps, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();

        if(id==R.id.new_place_item)
        {
            Intent intent =new Intent(this, EditMyPlaceActivity.class);
            startActivityForResult(intent, 1);
        }
        else if(id==R.id.about_item)
        {
            Intent intent =new Intent(this, About.class);
            startActivity(intent);
        }
        else if(id==android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
