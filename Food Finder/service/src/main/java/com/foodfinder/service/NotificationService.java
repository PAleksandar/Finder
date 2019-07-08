package com.foodfinder.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.foodfinder.acount.IntentExtraData;
import com.foodfinder.acount.Request;
import com.foodfinder.acount.UUID;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationService extends Service {


    public static final String CHANNEL_ID = "NotificationServiceChannel";
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private int notificationCount;
    private int notificationId;
    private List<String> requests;
    private ValueEventListener notificationListener;

    public NotificationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mDatabase= FirebaseDatabase.getInstance();
        mRef=mDatabase.getReference();
        notificationCount=0;
        notificationId=1;
        requests=new ArrayList<String>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       // sendNotification(intent);
        startRequestListener(intent);

        return START_NOT_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        if(currentFirebaseUser!=null) {
            String userId = currentFirebaseUser.getUid();
            DatabaseReference ref = mRef.child("requests").child(userId);
            ref.removeEventListener(notificationListener);
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void sendNotification(Intent intent, String id)
    {
        int idNotification=UUID.getNextNumber();
        IntentExtraData activity = (IntentExtraData) intent.getSerializableExtra("inputExtra");
        Class<? extends Activity> s=activity.getActivity();

        Intent notificationIntent = new Intent(this, s);
        notificationIntent.putExtra("inputExtra", id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                idNotification, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this ,CHANNEL_ID)
                .setContentTitle("Test content title")
                .setContentText("test content text"+id)
                .setSmallIcon(R.drawable.ic_sms_black_24dp)
                .setContentIntent(pendingIntent)
                .build();

        //startForeground(Unique_Integer_Number, notification);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(idNotification, notification);
    }

    private boolean inArray(String id)
    {
        boolean test=false;
        for (String s:requests)
        {
            if(id.equals(s))
            {
                test=true;
            }
        }

        return test;
    }

    private void startRequestListener(final Intent intent)
    {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String userId=currentFirebaseUser.getUid();
        DatabaseReference ref = mRef.child("requests").child(userId);

        setNotificationListener(intent);
        ref.addValueEventListener(notificationListener);
    }

    private void setNotificationListener(final Intent intent)
    {
        notificationListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                int newCount= (int) snapshot.getChildrenCount();

                Log.e("Request count", "notificationService: "+newCount );

                for(DataSnapshot ds : snapshot.getChildren()) {
                    Request r = ds.getValue(Request.class);

                    if(!inArray(r.getId()))
                    {
                        Log.e("send notification", "notificationService: "+r.getId() );
                        requests.add(r.getId());
                        sendNotification(intent,r.getId());
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        };
    }
}
