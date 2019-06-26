package com.foodfinder.rank;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodfinder.acount.Account;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RankAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Account> users;
    Context mContext;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;


    private void readUsers()
    {
        DatabaseReference ref = mRef.child("users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                users.clear();
                Log.e("Count " ,""+snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: snapshot.getChildren())
                {
                    Account acc = postSnapshot.getValue(Account.class);
                    users.add(acc);
                    Log.e("Get Data", acc.getUserName());
                }

               notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }

        });
    }

    public void updateAdapter(ArrayList<Account> newList)
    {
        users=new ArrayList<>();
        users.addAll(newList);
        notifyDataSetChanged();
    }


    public RankAdapter(List<Account> u, Context mContext) {
        this.users = u;
        this.mContext = mContext;
//        mDatabase=FirebaseDatabase.getInstance();
//        mRef=mDatabase.getReference();
//        users=new ArrayList<Account>();
//        readUsers();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater=LayoutInflater.from(mContext);
        View row=inflater.inflate(R.layout.rank_row,viewGroup,false);
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

        if(a.getRanks()!=null)
        {
            float result=0;
            for(Float f: a.getRanks())
            {
                result+=f;
            }
            result=result/a.getRanks().size();


            Log.e("Rank size: " , String.valueOf(result));

            if(result>0.5) {

                item.star1.setImageResource(R.drawable.gold_star);

                if(result>1.5)
                {
                    item.star2.setImageResource(R.drawable.gold_star);

                    if(result>2.5)
                    {
                        item.star3.setImageResource(R.drawable.gold_star);

                        if(result>3.5)
                        {
                            item.star4.setImageResource(R.drawable.gold_star);

                            if(result>4.5)
                            {
                                item.star5.setImageResource(R.drawable.gold_star);
                            }
                        }
                    }
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class RankItem extends RecyclerView.ViewHolder{

        TextView name, lastName;
        CircleImageView profileImage;
        ImageView star1,star2,star3,star4,star5;

        public RankItem(@NonNull View itemView) {
            super(itemView);
            name=(TextView) itemView.findViewById(R.id.name_item);
            lastName=(TextView) itemView.findViewById(R.id.last_name_item);
            profileImage=(CircleImageView) itemView.findViewById(R.id.image_item);
            star1=(ImageView) itemView.findViewById(R.id.star1);
            star2=(ImageView) itemView.findViewById(R.id.star2);
            star3=(ImageView) itemView.findViewById(R.id.star3);
            star4=(ImageView) itemView.findViewById(R.id.star4);
            star5=(ImageView) itemView.findViewById(R.id.star5);
        }
    }
}
