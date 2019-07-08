package com.foodfinder.friends;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddFriendActivity extends AppCompatActivity {


    Button listen, send, listDevice;
    ListView listView;
    TextView msg_box, status;

    Context mContext;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;
    List<BluetoothDevice> btArray2;
    List<String> devicesName;

    SendReceive sendReceive;

    static final int STATE_LISTENING=1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;

    int REQUEST_ENABLE_BLUETOOTH=1;
    private static final String APP_NAME="BTChat";
    private static final UUID MY_UUID=UUID.fromString("f2fcdb22-a0a9-11e9-a2a3-2a2ae2dbcce4");

    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("BLUETOOTH","UPADA ALI NE I U IF\n");
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                Log.d("BLUETOOTH","I U IF UPADA");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                btArray2.add(device);
                devicesName.add(device.getName());

                // Set<BluetoothDevice> bt=bluetoothAdapter.getBondedDevices();
                String[] strings=new String[1];//bt.size()];
                btArray=new BluetoothDevice[1];//bt.size()];
                btArray[0]=device;
                int index=0;

//                if(bt.size()>0)
//                {
//                    for(BluetoothDevice device: bt)
//                    {
//                        btArray[index]=device;
//                        strings[index]=device.getName();
//                        index++;
//                    }
//                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
//                    listView.setAdapter(arrayAdapter);
//
//                }

                strings[index]=device.getName();
                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,devicesName);
                listView.setAdapter(arrayAdapter);


            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        mContext=this;

        btArray2=new ArrayList<BluetoothDevice>();
        devicesName=new ArrayList<String>();

        listen=(Button) findViewById(R.id.ListenBtn);
        send=(Button) findViewById(R.id.SendBtn);
        listDevice=(Button) findViewById(R.id.ListDeviceBtn);

        listView=(ListView) findViewById(R.id.list_view);

        msg_box=(TextView) findViewById(R.id.textViewMessage);
        status=(TextView) findViewById(R.id.textViewStatus);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!bluetoothAdapter.isEnabled())
        {

            Intent enableIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BLUETOOTH);
        }

        implementListeners();
        setDiscoverable();

    }

    private void setDiscoverable()
    {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivity(discoverableIntent);
    }

    private void implementListeners() {

        listDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!bluetoothAdapter.isDiscovering())
                {
                    Log.d("BLUETOOTH","DISCOVERING STARTED");
                    bluetoothAdapter.startDiscovery();
                    IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mReceiver, discoverDevicesIntent);
                }

            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerClass serverClass=new ServerClass();
                serverClass.start();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                ClientClass clientClass=new ClientClass(btArray2.get(i));//ClientClass(btArray[i]);
                clientClass.start();

                status.setText("Connecting");
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(status.getText().toString().equals("Connected"))
                {
                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                    String userId=currentFirebaseUser.getUid();

                    String string=userId;
                    sendReceive.write(string.getBytes());

                    finishInteraction(userId);
                }
                else {
                    Toast.makeText(mContext, "Devices not conected!", Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what)
            {
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONECTION_FAILED:
                    status.setText("Connection failed");
                    break;
                case STATE_MESSAGE_RECEIVED:

                    byte[] readBuff=(byte[]) msg.obj;
                    String tempMsg=new String(readBuff,0,msg.arg1);
                    msg_box.setText(tempMsg);
                    saveFriends(tempMsg);
                    break;

            }

            return true;
        }
    });

    private void saveFriends(String friendId) {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String userId=currentFirebaseUser.getUid();

        updateFriends(friendId, userId);
        updateFriends(userId, friendId);


    }



    private void updateFriends(final String userId, final String friendId)
    {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("friends");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};

                ArrayList<String> friends = snapshot.getValue(t);
                if(friends !=null)
                {
                    friends.add(friendId);

                }
                else
                {
                    friends=new ArrayList<String>();
                    friends.add(friendId);
                }
                saveFriends(friends, userId);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    private void saveFriends(List<String> friends, final String userId)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("friends");
        ref.setValue(friends).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                finishInteraction(userId);

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                    }
                });
    }

    private void finishInteraction(String id)
    {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String userId=currentFirebaseUser.getUid();

        if(id.equals(userId))
        {

            showDeleteMessage();
        }
    }

    private void showDeleteMessage()
    {
        new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage("You became friends")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        finish();
                    }
                })

                //.setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    private class ServerClass extends Thread
    {
        private BluetoothServerSocket serverSocket;

        public ServerClass()
        {
            try {
                serverSocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME,MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            BluetoothSocket socket=null;

            while (socket==null)
            {
                try {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket=serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message=Message.obtain();
                    message.what=STATE_CONECTION_FAILED;
                    handler.sendMessage(message);
                }

                if(socket!=null)
                {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive=new SendReceive(socket);
                    sendReceive.start();
                    break;
                }
            }

        }
    }

    private class ClientClass extends Thread
    {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice device1)
        {
            this.device=device1;

            try {
                socket=device.createRfcommSocketToServiceRecord(MY_UUID);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public  void run()
        {
            try {
                socket.connect();

                Message message=Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive=new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what=STATE_CONECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket)
        {
            bluetoothSocket=socket;
            InputStream tempInputStream=null;
            OutputStream tempOutputStream=null;


            try {
                tempInputStream=bluetoothSocket.getInputStream();
                tempOutputStream=bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream=tempInputStream;
            outputStream=tempOutputStream;
        }

        public void run()
        {
            byte[] buffer=new byte[1024];
            int bytes;

            while (true)
            {
                try {
                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
//                    String s=new String(buffer);
//                    Toast.makeText(getApplicationContext(),"Received message: "+,Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        public void write(byte[] bytes)
        {

            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
