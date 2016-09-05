package com.gauldev.azat.batteryalarm;

import android.content.Context;
import android.util.Log;

import com.gauldev.azat.batteryalarm.device_database.DeviceDatabase;
import com.gauldev.azat.batteryalarm.device_database.DeviceDatabaseHelper;
import com.gauldev.azat.batteryalarm.model.DeviceEntity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class DevicesListener implements ChildEventListener {

    MainActivity activity;

    public DevicesListener(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        DeviceEntity deviceEntity=dataSnapshot.getValue(DeviceEntity.class);
        Log.d("device_added",deviceEntity.toString());
        DeviceDatabase.addDeviceObservable(new DeviceDatabaseHelper(activity.getApplicationContext()),deviceEntity).cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(count->{
                    Log.d("insert_device",count+"");
                    activity.getDeviceFromDb();
                },e->dbError(e));
    }

    private void dbError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        DeviceEntity deviceEntity=dataSnapshot.getValue(DeviceEntity.class);
        Log.d("device_changed",deviceEntity.toString());
        DeviceDatabase.updDeviceObservable(new DeviceDatabaseHelper(activity.getApplicationContext()),deviceEntity).cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(count->{
                    Log.d("update_device",count+"");
                    activity.getDeviceFromDb();
                },e->dbError(e));
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
}
