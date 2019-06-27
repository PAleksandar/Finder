package com.foodfinder.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;


public class SettingsAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    SettingsModel settingsData;
    Context mContext;

    public void updateAdapter(SettingsModel settings)
    {
        settingsData=settings;
        notifyDataSetChanged();
    }


    public SettingsAdapter(SettingsModel s, Context mContext) {
        this.settingsData = s;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater=LayoutInflater.from(mContext);
        View row=inflater.inflate(R.layout.settigs_row,viewGroup,false);
        RankItem item=new RankItem(row);
        return item;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        RankItem item=((RankItem)viewHolder);

        item.name.setText(settingsData.getSettingsFunctions().get(i));

        switch (i)
        {
            case 0:
                if(settingsData.isLocation())
                {
                    item.switchButton.setChecked(true);
                }
                break;
            case 1:
                if(settingsData.isNotification())
                {
                    item.switchButton.setChecked(true);
                }
                break;
            case 2:
                if(settingsData.isBluetooth())
                {
                    item.switchButton.setChecked(true);
                }
                break;
            case 3:
                if(settingsData.isSound())
                {
                    item.switchButton.setChecked(true);
                }
                break;
            case 4:
                if(settingsData.isDriver())
                {
                    item.switchButton.setChecked(true);
                }
                break;

                default: break;
        }

    }

    @Override
    public int getItemCount() {
        return settingsData.getSettingsFunctions().size();
    }

    public class RankItem extends RecyclerView.ViewHolder{

        TextView name;
        Switch switchButton;


        public RankItem(@NonNull View itemView) {
            super(itemView);
            name=(TextView) itemView.findViewById(R.id.settings_name_item);
            switchButton=(Switch) itemView.findViewById(R.id.settings_switch_item);

        }
    }
}

