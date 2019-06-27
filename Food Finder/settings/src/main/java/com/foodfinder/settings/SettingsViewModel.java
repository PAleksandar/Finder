package com.foodfinder.settings;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

public class SettingsViewModel extends ViewModel {

    private SettingsModel settingsData;
    private SettingsAdapter adapter;

    private Context mContext;

    public void initializeViewModel(Context context)
    {
        settingsData=new SettingsModel();

        mContext=context;
    }

    public void setAdapter(RecyclerView recyclerView)
    {
        adapter=new SettingsAdapter(settingsData,mContext);
        recyclerView.setAdapter(adapter);
    }

}
