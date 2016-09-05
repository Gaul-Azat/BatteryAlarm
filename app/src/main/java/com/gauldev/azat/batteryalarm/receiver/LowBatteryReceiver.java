package com.gauldev.azat.batteryalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;

import com.gauldev.azat.batteryalarm.model.DeviceEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LowBatteryReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("reciver", "Low Battery");
        Log.d("reciverAction", intent.getAction());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null) {
            DeviceEntity entity = getDeviceInfo(context);
            if (intent.getAction() == Intent.ACTION_BATTERY_LOW) {
                entity.setBatteryState("Battery_Low");
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference().child("devices").child(user.getUid()).child(Build.SERIAL);
                reference.setValue(entity);
            }else if (intent.getAction()==Intent.ACTION_POWER_CONNECTED||intent.getAction()==Intent.ACTION_BATTERY_OKAY||intent.getAction()==Intent.ACTION_POWER_DISCONNECTED){
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference().child("devices").child(user.getUid()).child(Build.SERIAL);
                reference.setValue(entity);
            }
        }
    }
    private DeviceEntity getDeviceInfo(Context context){
        String deviceName= Build.MODEL;
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        String batteryState;
        if (status==BatteryManager.BATTERY_STATUS_CHARGING){
            batteryState="Charging";
        }else if (status==BatteryManager.BATTERY_STATUS_FULL){
            batteryState="Full";
        }else{
            batteryState="Discharging";
        }
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale=batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
        Log.d("dfdg",level+"");
        Log.d("dfdg",scale+"");
        float batteryPct=level/(float)scale*100;

        return new DeviceEntity(deviceName,batteryState,batteryPct+"", System.currentTimeMillis());

    }
}
