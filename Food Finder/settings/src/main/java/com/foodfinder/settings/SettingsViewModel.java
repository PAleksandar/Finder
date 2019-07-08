package com.foodfinder.settings;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.foodfinder.acount.Account;

public class SettingsViewModel extends ViewModel {

    private SettingsModel settingsData;
    private SettingsAdapter adapter;

    private Context mContext;
    private Activity mActivity;

    public void initializeViewModel(Context context, Activity activity)
    {
        settingsData=new SettingsModel();

        mContext=context;
        mActivity=activity;
    }

    public void setAdapter(RecyclerView recyclerView)
    {
        adapter=new SettingsAdapter(settingsData,mContext,mActivity);
        recyclerView.setAdapter(adapter);
    }

}
