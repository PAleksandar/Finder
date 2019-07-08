package com.foodfinder.acount;

import java.io.Serializable;
import java.util.Random;

public class Position implements Serializable {

    private double latitude;
    private double longitude;

    public Position()
    {
        this.latitude = 0.0;
        this.longitude=0.0;

    }

    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
