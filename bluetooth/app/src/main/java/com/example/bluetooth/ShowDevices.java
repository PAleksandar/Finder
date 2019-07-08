package com.example.bluetooth;

import android.support.v7.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.bluetooth.MainActivity.mBluetoothAdapter;

public class ShowDevices extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_devices);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();

        ArrayList<String> array = this.getIntent().getStringArrayListExtra("devices");

        TextView textView = findViewById(R.id.textView);
        //textView.setText(array.toString());

        Button btnDiscover = findViewById(R.id.btnDiscover);

        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mBluetoothAdapter.isDiscovering())
                {
                    Log.d("BLUETOOTH","DISCOVERING STARTED");
                    mBluetoothAdapter.startDiscovery();
                    IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mReceiver, discoverDevicesIntent);
                }else{
                    //Log.d("MAIN","BLUETOOTH STILL DISCOVERING");
                    //mBluetoothAdapter.cancelDiscovery();
                }
            }
        });
        //KORISNIK SELEKTUJE 1 DEVICE I UZME MU SE ADRESA PROSLEDI DOLE
//        BluetoothDevice device =
//                mBluetoothAdapter.getRemoteDevice("30:21:93:2D:E2:3B");//"30:21:93:2D:E2:3B");
//
//
//
//        Intent data = new Intent();
//        data.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
//        setResult(RESULT_OK, data);
//
//        mBluetoothAdapter.cancelDiscovery();

        //IntentFilter filter= new Intent(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //finish();
    }

    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("BLUETOOTH","UPADA ALI NE I U IF\n");
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                Log.d("BLUETOOTH","I U IF UPADA");
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                TextView textView = (TextView) findViewById(R.id.textView);

                textView.setText("PRONADJEN JE:"+ device.getName() + " : " + device.getAddress() + "\n");
            }
        }
    };
}
