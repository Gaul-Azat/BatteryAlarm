package com.gauldev.azat.batteryalarm;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.gauldev.azat.batteryalarm.adapter.DeviceListAdapter;
import com.gauldev.azat.batteryalarm.device_database.DeviceDatabase;
import com.gauldev.azat.batteryalarm.device_database.DeviceDatabaseHelper;
import com.gauldev.azat.batteryalarm.model.DeviceEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 1;
    private RecyclerView recyclerView;
    private Cursor dataCursor;
    private DeviceListAdapter deviceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if (user==null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(intent,REQUEST_LOGIN);
        }
        else{
            initDeviceList();
            printUserInfo();
            sendDeviceInfo();
         }

        FirebaseDatabase.getInstance().getReference().child("devices").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addChildEventListener(new DevicesListener(this));
        startService(new Intent(this,ChangeDataService.class));
    }

    private void initDeviceList(){
        recyclerView=(RecyclerView)findViewById(R.id.deviceList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        deviceListAdapter=new DeviceListAdapter(dataCursor);
        recyclerView.setAdapter(deviceListAdapter);
        getDeviceFromDb();
    }

    public void getDeviceFromDb(){
        DeviceDatabaseHelper helper=new DeviceDatabaseHelper(getApplicationContext());
        DeviceDatabase.getDeviceObservable(helper).cache()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(cursor->{
                deviceListAdapter.changeCursor(cursor);
                dataCursor=cursor;
                Log.d("kjk","hh");
            },e->dbError(e));
    }

    private void dbError(Throwable e) {
        e.printStackTrace();
    }

    private void printUserInfo(){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
           // Log.d("username",user.getDisplayName());
            Log.d("user_email",user.getEmail());
        }
    }

    private void sendDeviceInfo(){
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference().child("devices").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(Build.SERIAL);
        reference.setValue(getDeviceInfo());
    }

    private DeviceEntity getDeviceInfo(){
        String deviceName= Build.MODEL;
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                printUserInfo();
                initDeviceList();
                sendDeviceInfo();
            }
        }
    }
}
