package com.foodfinder.friends;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.SearchView;

import com.foodfinder.acount.Account;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsViewModel extends ViewModel {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private ArrayList<Account> users;
    private FriendsAdpter adapter;

    private Context mContext;

    public void initializeViewModel(Context context)
    {
        mDatabase=FirebaseDatabase.getInstance();
        mRef=mDatabase.getReference();

        users=new ArrayList<Account>();

        mContext=context;
    }

    public void readUsers(final RecyclerView recyclerView)
    {
        DatabaseReference ref = mRef.child("users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count " ,""+snapshot.getChildrenCount());
                users=new ArrayList<Account>();
                for (DataSnapshot postSnapshot: snapshot.getChildren())
                {
                    Account acc = postSnapshot.getValue(Account.class);
                    users.add(acc);
                    Log.e("Get Data", acc.getUserName());
                }

                setAdapter(recyclerView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }

        });
    }

    private void setAdapter(RecyclerView recyclerView)
    {
        adapter=new FriendsAdpter(users,mContext);
        recyclerView.setAdapter(adapter);
    }

    public SearchView.OnQueryTextListener getSearchListener()
    {
        SearchView.OnQueryTextListener searchViewListener=new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                ArrayList<Account> newList = new ArrayList<>();
                for (Account user : users) {
                    String username = user.getUserName().toLowerCase();
                    String lastName = user.getLastName().toLowerCase();

                    if (username.contains(newText) || lastName.contains(newText)) {
                        newList.add(user);
                    }
                }

                if(adapter!=null)
                {
                    adapter.updateAdapter(newList);
                }

                return true;
            }
        };

        return searchViewListener;
    }


}
