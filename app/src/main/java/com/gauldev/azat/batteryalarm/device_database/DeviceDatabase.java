package com.gauldev.azat.batteryalarm.device_database;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.gauldev.azat.batteryalarm.model.DeviceEntity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;

public class DeviceDatabase {

    public static Callable<Cursor> getDevice(final DeviceDatabaseHelper helper){
        return new Callable<Cursor>() {
            @Override
            public Cursor call() throws Exception {
                String query="SELECT * FROM " + DeviceDatabaseHelper.TABLE;
                return helper.getReadableDatabase().rawQuery(query,null);
            }
        };
    }

    public static Callable<Long> addDevice(DeviceDatabaseHelper helper, DeviceEntity entity){
        return new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                ContentValues cv=new ContentValues();
                cv.put(helper.COLUMN_DEVICE,entity.getName());
                cv.put(helper.COLUMN_BATTERY_STATE,entity.getBatteryState());
                cv.put(helper.COLUMN_BATTERY_PERCENT,entity.getBatteryPercent());
                cv.put(helper.COLUMN_LAST_UPDATE,entity.getDateLastUpdate()+"");
                SQLiteDatabase writableDatabase=helper.getWritableDatabase();
                Long addCount;
                try {
                    addCount=writableDatabase.insertOrThrow(helper.TABLE,null,cv);
                }catch (SQLiteConstraintException e){
                    addCount=updateDevice(helper,entity)+0L;
                }
                writableDatabase.close();
                helper.close();
                return addCount;
            }
        };
    }

    private static Integer updateDevice(DeviceDatabaseHelper helper, DeviceEntity entity){
        ContentValues cv=new ContentValues();
        cv.put(helper.COLUMN_BATTERY_STATE,entity.getBatteryState());
        cv.put(helper.COLUMN_BATTERY_PERCENT,entity.getBatteryPercent());
        cv.put(helper.COLUMN_LAST_UPDATE,entity.getDateLastUpdate()+"");
        SQLiteDatabase writableDatabase=helper.getWritableDatabase();
        int updCount=writableDatabase.update(helper.TABLE,cv,helper.COLUMN_DEVICE+" = ?",new String[]{entity.getName()});
        writableDatabase.close();
        helper.close();
        return updCount;
    }

    public static Callable<Integer> updateDevices(DeviceDatabaseHelper helper, DeviceEntity entity){
        return new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return updateDevice(helper,entity);
            }
        };
    }

    public static Observable<Cursor> getDeviceObservable(DeviceDatabaseHelper helper){
        return makeObservable(getDevice(helper));
    }

    public static Observable<Long> addDeviceObservable(DeviceDatabaseHelper helper, DeviceEntity entity){
        return makeObservable(addDevice(helper,entity));
    }

    public static Observable<Integer> updDeviceObservable(DeviceDatabaseHelper helper, DeviceEntity entity){
        return makeObservable(updateDevices(helper,entity));
    }

    public static <T> Observable<T> makeObservable(final Callable<T> func) {
        return Observable.create(
                new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        try {
                            T observed = func.call();
                            if (observed != null) { // to make defaultIfEmpty work
                                subscriber.onNext(observed);
                            }
                            subscriber.onCompleted();
                        } catch (Exception ex) {
                            subscriber.onError(ex);
                        }
                    }
                });
    }

}
