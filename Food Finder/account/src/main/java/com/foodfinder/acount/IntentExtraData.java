package com.foodfinder.acount;

import android.app.Activity;

import java.io.Serializable;

public class IntentExtraData implements Serializable
{
    Class<? extends Activity> activity;

    public IntentExtraData() {
    }

    public IntentExtraData(Class<? extends Activity> activity) {
        this.activity = activity;
    }

    public Class<? extends Activity> getActivity() {
        return activity;
    }

    public void setActivity(Class<? extends Activity> activity) {
        this.activity = activity;
    }
}
