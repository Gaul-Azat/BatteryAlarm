package com.gauldev.azat.batteryalarm.device_database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DeviceDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "device_store.db"; // название бд
    private static final int SCHEMA = 1; // версия базы данных
    public static final String TABLE = "device"; // название таблицы в бд
    // названия столбцов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DEVICE = "device_name";
    public static final String COLUMN_BATTERY_STATE = "battery_state";
    public static final String COLUMN_BATTERY_PERCENT = "battery_percent";
    public static final String COLUMN_LAST_UPDATE = "last_update";

    public DeviceDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE+" (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_DEVICE
                + " TEXT NOT NULL UNIQUE, " + COLUMN_BATTERY_STATE + " TEXT, " + COLUMN_LAST_UPDATE + " TEXT, "+ COLUMN_BATTERY_PERCENT  + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE);
        onCreate(db);
    }
}
