package com.gauldev.azat.batteryalarm.model;


import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DeviceEntity {

    String name;
    String batteryState;
    String batteryPercent;
    Long dateLastUpdate;

    public DeviceEntity() {
    }

    public DeviceEntity(String name, String batteryState, String batteryPercent, Long dateLastUpdate) {
        this.name = name;
        this.batteryState = batteryState;
        this.batteryPercent = batteryPercent;
        this.dateLastUpdate = dateLastUpdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getBatteryState() {
        return batteryState;
    }

    public void setBatteryState(String batteryState) {
        this.batteryState = batteryState;
    }

    public String getBatteryPercent() {
        return batteryPercent;
    }

    public void setBatteryPercent(String batteryPercent) {
        this.batteryPercent = batteryPercent;
    }

    public String getDateLastUpdateToString() {
        DateFormat dateTimeInstance = SimpleDateFormat.getDateTimeInstance();
        return dateTimeInstance.format(dateLastUpdate);
    }

    public Long getDateLastUpdate(){
        return dateLastUpdate;
    }

    public void setDateLastUpdate(Long dateLastUpdate) {
        this.dateLastUpdate = dateLastUpdate;
    }

    @Override
    public String toString() {
        return "DeviceEntity{" +
                "name='" + name + '\'' +
                ", batteryState='" + batteryState + '\'' +
                ", batteryPercent='" + batteryPercent + '\'' +
                ", dateLastUpdate=" + getDateLastUpdateToString() +
                '}';
    }
}
