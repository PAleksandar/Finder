package com.example.bluetooth;

import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static BluetoothAdapter mBluetoothAdapter;
    int REQUEST_ENABLE_BT = 0;
    int REQUEST_DISCOVER_BT = 1;
    int SELECT_SERVER = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            //no bluetooth support
            Toast.makeText(this,"NO BLUETOOTH SUPPORT",Toast.LENGTH_LONG).show();
            finish();
        }

        if(!mBluetoothAdapter.isEnabled())//ako nije ukljucen izadje dialog da upali
        {
            turnOnBluetooth();
        }else{
            Toast.makeText(this, "Please turn on bluetooth", Toast.LENGTH_LONG).show();
        }

        Button turnOn = (Button)findViewById(R.id.btnOn);

        turnOn.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                turnOnBluetooth();
            }
        });

        Button selectDevice = (Button) findViewById(R.id.btnSelect);

        selectDevice.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                selectServer();
            }
        });

        Button btnOff = (Button) findViewById(R.id.btnOff);

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothAdapter.disable();
            }
        });

        Button btnDiscoverable = (Button) findViewById(R.id.btnDiscoverable);

        btnDiscoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDiscoverable();
            }
        });

//        Button showDevices = (Button) findViewById(R.id.showDevices);
//
//        showDevices.setOnClickListener(new View.OnClickListener( ) {
//            @Override
//            public void onClick(View view) {
//                selectServer();
//            }
//        });
    }

    private void turnOnBluetooth()
    {
        Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBluetoothIntent,REQUEST_ENABLE_BT);
    }

    private void setDiscoverable()
    {
        //ako nije discoverable da ga vide pa sa ovaj intent akcija da upali da je vidljiv blutut
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivity(discoverableIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK)
        {
            //podaci o lokalnom adapteru
            String address = mBluetoothAdapter.getAddress();
            String name = mBluetoothAdapter.getName();
            String toastText = name + " : " + address;
            Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
        }
        if(requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED)
        {
            Toast.makeText(this, "Please turn on bluetooth", Toast.LENGTH_LONG).show();
        }
    }

    //prikaz uparenih uredjaja- sa koji si bio nekad povezan
    private void selectServer() {
        Set<BluetoothDevice> pairedDevices =
                mBluetoothAdapter.getBondedDevices();
        ArrayList<String> pairedDeviceStrings = new ArrayList<String>();

        TextView textView2 = findViewById(R.id.textView2);
        textView2.setText(" ");
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceStrings.add(device.getName() + "\n" + device.getAddress());

                textView2.setText(textView2.getText() +
                        device.getName() + " - " +
                        device.getAddress() + "\n");
            }
        }

        Intent showDevicesIntent = new Intent(this, ShowDevices.class);
        showDevicesIntent.putStringArrayListExtra("devices", pairedDeviceStrings);
        startActivityForResult(showDevicesIntent, SELECT_SERVER);
    }
}
