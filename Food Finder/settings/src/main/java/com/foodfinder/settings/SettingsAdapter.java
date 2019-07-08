package com.foodfinder.settings;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.foodfinder.acount.IntentExtraData;
import com.foodfinder.acount.Position;
import com.foodfinder.acount.Request;
import com.foodfinder.service.NotificationService;
import com.foodfinder.service.UpdatePositionService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.support.v4.app.ActivityCompat.startActivityForResult;


public class SettingsAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int REQUEST_ENABLE_BT = 0;
    int REQUEST_DISCOVER_BT = 1;
    int SELECT_SERVER = 2;
    SettingsModel settingsData;
    Context mContext;
    Context mActivity;
    public static BluetoothAdapter mBluetoothAdapter;

    public void updateAdapter(SettingsModel settings)
    {
        settingsData=settings;
        notifyDataSetChanged();
    }


    public SettingsAdapter(SettingsModel s, Context mContext, Activity activity) {
        this.settingsData = s;
        this.mContext = mContext;
        this.mActivity=activity;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater=LayoutInflater.from(mContext);
        View row=inflater.inflate(R.layout.settigs_row,viewGroup,false);
        RankItem item=new RankItem(row);
        return item;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        final RankItem item=((RankItem)viewHolder);

        item.name.setText(settingsData.getSettingsFunctions().get(i));

        switch (i)
        {
            case 0:
                if(settingsData.isLocation())
                {
                    item.switchButton.setChecked(true);
                }
                item.switchButton.setOnCheckedChangeListener(getLocationListener());
                break;
            case 1:
                if(settingsData.isNotification())
                {
                    item.switchButton.setChecked(true);
                }
                item.switchButton.setOnCheckedChangeListener(getNotificationListener());
                break;
            case 2:
                if(settingsData.isBluetooth())
                {
                    item.switchButton.setChecked(true);
                }
                item.switchButton.setOnCheckedChangeListener(getBluetoothionListener());
                break;
            case 3:
                if(settingsData.isSound())
                {
                    item.switchButton.setChecked(true);
                }
                item.switchButton.setOnCheckedChangeListener(getSoundListener());
                break;
            case 4:
                if(settingsData.isDriver())
                {

                    item.switchButton.setChecked(true);
                }
                item.switchButton.setOnCheckedChangeListener(getDrivingListener());
                break;

                default: break;
        }

    }

    @Override
    public int getItemCount() {
        return settingsData.getSettingsFunctions().size();
    }

    public class RankItem extends RecyclerView.ViewHolder{

        TextView name;
        Switch switchButton;


        public RankItem(@NonNull View itemView) {
            super(itemView);
            name=(TextView) itemView.findViewById(R.id.settings_name_item);
            switchButton=(Switch) itemView.findViewById(R.id.settings_switch_item);

        }
    }

    private  CompoundButton.OnCheckedChangeListener getLocationListener()
    {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    startUpdatePositionService();
                }
                else
                {
                    stopUpdatePositionService();
                }
            }
        };
    }

    private  CompoundButton.OnCheckedChangeListener getNotificationListener()
    {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    startNotificationService();
                }
                else
                {
                    stopNotificationService();
                }
            }
        };
    }

    private  CompoundButton.OnCheckedChangeListener getBluetoothionListener()
    {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


//                if(isChecked)
//                {
//                    if(mBluetoothAdapter == null){
//                        //no bluetooth support
//                        Toast.makeText(mContext,"NO BLUETOOTH SUPPORT",Toast.LENGTH_LONG).show();
//                        return;
//                    }
//
//                    if(!mBluetoothAdapter.isEnabled())
//                    {
//                        turnOnBluetooth();
//                    }
//
//                }
//                else
//                {
//
//                    mBluetoothAdapter.disable();
//                }

            }
        };
    }

    private void turnOnBluetooth()
    {
        Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        ((Activity) mContext).startActivityForResult(enableBluetoothIntent,REQUEST_ENABLE_BT);
    }



    private  CompoundButton.OnCheckedChangeListener getSoundListener()
    {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        };
    }

    private  CompoundButton.OnCheckedChangeListener getDrivingListener()
    {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                setDrivingMode(isChecked);

            }
        };
    }

    public void startUpdatePositionService() {

        if(!checkServiceRunning("com.foodfinder.service.UpdatePositionService")) {

            Intent serviceIntent = new Intent(mContext, UpdatePositionService.class);
            mContext.startService( serviceIntent);
        }

    }

    public void stopUpdatePositionService() {
        if(checkServiceRunning("com.foodfinder.service.UpdatePositionService")) {

            Intent serviceIntent = new Intent(mContext, UpdatePositionService.class);
            mContext.stopService(serviceIntent);
        }

    }

    public void startNotificationService() {
        if(!checkServiceRunning("com.foodfinder.service.NotificationService")) {

            Intent serviceIntent = new Intent(mContext, NotificationService.class);
            IntentExtraData data=new IntentExtraData((Class<? extends Activity>) mActivity.getClass());
            serviceIntent.putExtra("inputExtra", data);
            mContext.startService( serviceIntent);
        }

    }

    public void stopNotificationService() {
        if(checkServiceRunning("com.foodfinder.service.NotificationService")) {

            Intent serviceIntent = new Intent(mContext, NotificationService.class);
            mContext.stopService(serviceIntent);
        }


    }

    public boolean checkServiceRunning(String serviceName){
        ActivityManager manager = (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceName.equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }

    private void setDrivingMode(boolean isDriver)
    {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String userId=currentFirebaseUser.getUid();

        Log.e("set driving mode", "setDrivingMode: "+userId);

       DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("driver");
        ref.setValue(isDriver).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                    }
                });
    }

    private void setIsDriver(final Switch button)
    {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String userId=currentFirebaseUser.getUid();

        Log.e("set driving mode", "setDrivingMode: "+userId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("driver");


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                boolean isDriver = snapshot.getValue(boolean.class);

                if(isDriver)
                {
                    button.setChecked(true);
                }
                else
                {
                    button.setChecked(false);
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }
}

