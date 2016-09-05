package com.gauldev.azat.batteryalarm;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gauldev.azat.batteryalarm.model.DeviceEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeDataService extends Service {
    public ChangeDataService() {
    }

    ChildEventListener childListener;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Adding a childevent listener to firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null&&childListener==null) {
            // Log.d("username",user.getDisplayName());
            childListener=FirebaseDatabase.getInstance().getReference().child("devices").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    DeviceEntity deviceEntity = dataSnapshot.getValue(DeviceEntity.class);
                    Log.d("device_added", deviceEntity.getName());
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.common_google_signin_btn_text_dark)
                            .setContentTitle("Event device added")
                            .setContentText("Device "+deviceEntity.getName()+" added");
                    NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1,mBuilder.build());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    DeviceEntity deviceEntity = dataSnapshot.getValue(DeviceEntity.class);
                    Log.d("device_changed", deviceEntity.getName());
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.common_google_signin_btn_text_dark)
                            .setContentTitle("Event device changed")
                            .setContentText("Device "+deviceEntity.getName()+" changed");
                    NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1,mBuilder.build());
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        childListener=null;
    }
}
