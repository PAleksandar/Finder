package com.foodfinder.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SettingsModel implements Serializable {

    private boolean Location;
    private boolean Notification;
    private boolean Bluetooth;
    private boolean Sound;
    private boolean isDriver;

    private List<String> settingsFunctions;

    public List<String> getSettingsFunctions() {
        return settingsFunctions;
    }

    public void setSettingsFunctions(List<String> settingsFunctions) {
        this.settingsFunctions = settingsFunctions;
    }

    private void initializeSettings()
    {
        settingsFunctions=new ArrayList<String>();
        settingsFunctions.add("Location");
        settingsFunctions.add("Notification");
        settingsFunctions.add("Bluetooth");
        settingsFunctions.add("Sound");
        settingsFunctions.add("Driving");
    }

    public  SettingsModel()
    {
        Location = true;
        Notification = true;
        Bluetooth = false;
        Sound = true;
        this.isDriver = false;
        initializeSettings();
    }

    public SettingsModel(boolean location, boolean notification, boolean bluetooth, boolean sound, boolean isDriver) {
        Location = location;
        Notification = notification;
        Bluetooth = bluetooth;
        Sound = sound;
        this.isDriver = isDriver;
        initializeSettings();
    }

    public boolean isLocation() {
        return Location;
    }

    public void setLocation(boolean location) {
        Location = location;
    }

    public boolean isNotification() {
        return Notification;
    }

    public void setNotification(boolean notification) {
        Notification = notification;
    }

    public boolean isBluetooth() {
        return Bluetooth;
    }

    public void setBluetooth(boolean bluetooth) {
        Bluetooth = bluetooth;
    }

    public boolean isSound() {
        return Sound;
    }

    public void setSound(boolean sound) {
        Sound = sound;
    }

    public boolean isDriver() {
        return isDriver;
    }

    public void setDriver(boolean driver) {
        isDriver = driver;
    }

}
