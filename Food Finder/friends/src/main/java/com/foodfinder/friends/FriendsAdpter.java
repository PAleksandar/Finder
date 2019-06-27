package com.foodfinder.friends;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foodfinder.acount.Account;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Account> users;
    Context mContext;

    public void updateAdapter(ArrayList<Account> newList)
    {
        users=new ArrayList<>();
        users.addAll(newList);
        notifyDataSetChanged();
    }


    public FriendsAdpter(List<Account> u, Context mContext) {
        this.users = u;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater=LayoutInflater.from(mContext);
        View row=inflater.inflate(R.layout.friends_row,viewGroup,false);
        RankItem item=new RankItem(row);
        return item;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        RankItem item=((RankItem)viewHolder);

        Account a=users.get(i);
        item.name.setText(a.getUserName());
        item.lastName.setText(a.getLastName());
        Picasso.get().load(a.getProfileImage()).into(item.profileImage);
        if(a.isActive())
        {
            item.status.setImageResource(R.color.orange_1);
        }



    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class RankItem extends RecyclerView.ViewHolder{

        TextView name, lastName;
        CircleImageView profileImage, status;



        public RankItem(@NonNull View itemView) {
            super(itemView);
            name=(TextView) itemView.findViewById(R.id.name_item);
            lastName=(TextView) itemView.findViewById(R.id.last_name_item);
            profileImage=(CircleImageView) itemView.findViewById(R.id.image_item);
            status=(CircleImageView) itemView.findViewById(R.id.active_status_item);



        }
    }

}
