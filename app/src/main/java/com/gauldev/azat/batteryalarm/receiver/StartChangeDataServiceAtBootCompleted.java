package com.gauldev.azat.batteryalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gauldev.azat.batteryalarm.ChangeDataService;


public class StartChangeDataServiceAtBootCompleted extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, ChangeDataService.class);
        context.startService(startServiceIntent);
    }
}
