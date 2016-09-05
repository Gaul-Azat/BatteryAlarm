package com.gauldev.azat.batteryalarm.adapter;


import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gauldev.azat.batteryalarm.databinding.DeviceCardBinding;
import com.gauldev.azat.batteryalarm.device_database.DeviceDatabaseHelper;
import com.gauldev.azat.batteryalarm.model.DeviceEntity;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceCardHolder> {

    Cursor dataCursor;

    public DeviceListAdapter(Cursor dataCursor) {
        this.dataCursor = dataCursor;
    }

    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
        notifyItemRangeChanged(0,getItemCount());
    }

    private Cursor swapCursor(Cursor cursor) {
        if (dataCursor == cursor) {
            return null;
        }
        Cursor oldCursor = dataCursor;
        this.dataCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    private DeviceEntity getItem(int position) {
        dataCursor.moveToPosition(position);
        // Load data from dataCursor and return it...
        String deviceName=dataCursor.getString(dataCursor.getColumnIndex(DeviceDatabaseHelper.COLUMN_DEVICE));
        String batteryState=dataCursor.getString(dataCursor.getColumnIndex(DeviceDatabaseHelper.COLUMN_BATTERY_STATE));
        String batteryPercent=dataCursor.getString(dataCursor.getColumnIndex(DeviceDatabaseHelper.COLUMN_BATTERY_PERCENT));
        Long dateLastUpdate=dataCursor.getLong(dataCursor.getColumnIndex(DeviceDatabaseHelper.COLUMN_LAST_UPDATE));

        return new DeviceEntity(deviceName,batteryState,batteryPercent,dateLastUpdate);
    }

    @Override
    public DeviceCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        DeviceCardBinding cardBinding=DeviceCardBinding.inflate(inflater,parent,false);
        return new DeviceCardHolder(cardBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(DeviceCardHolder holder, int position) {
        DeviceEntity entity=getItem(position);
        holder.binding.setDevice(entity);
    }

    @Override
    public int getItemCount() {
        try {
            if (dataCursor == null || dataCursor.isClosed()) {
                return 0;
            } else {
                return dataCursor.getCount();
            }}
        catch (Throwable e){
            return 0;
        }
    }

    public static class DeviceCardHolder extends RecyclerView.ViewHolder{

        DeviceCardBinding binding;

        public DeviceCardHolder(View itemView) {
            super(itemView);
            binding= DataBindingUtil.bind(itemView);
        }
    }
}
