package com.foodfinder.rank;

import com.foodfinder.acount.Account;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class RankFragment extends Fragment {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
   // private StorageReference mStorageRef;

    private RankViewModel mViewModel;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private List<Account> users=new ArrayList<Account>();
    private Context mContext;
    private RankAdapter rankAdapter;

    public static RankFragment newInstance() {
        return new RankFragment();
    }

    private void readUsers()
    {
        DatabaseReference ref = mRef.child("users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count " ,""+snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: snapshot.getChildren())
                {
                    Account acc = postSnapshot.getValue(Account.class);
                    users.add(acc);
                    Log.e("Get Data", acc.getUserName());
                }

                setAdapter();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }

        });
    }

    private void initializeComponent()
    {
        mDatabase=FirebaseDatabase.getInstance();
        mRef=mDatabase.getReference();

      //  mStorageRef = FirebaseStorage.getInstance().getReference();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        initializeComponent();
        View view =inflater.inflate(R.layout.rank_fragment, container, false);
        final FragmentActivity c = getActivity();
        recyclerView = (RecyclerView) view.findViewById(R.id.rank_recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);
        recyclerView.setLayoutManager(layoutManager);
        mContext=c.getApplicationContext();
        readUsers();

        searchView=(SearchView) view.findViewById(R.id.search_view_rank);
        search();



        return view;
    }

    private void setAdapter()
    {
        rankAdapter=new RankAdapter(users,mContext);
        recyclerView.setAdapter(rankAdapter);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RankViewModel.class);
        // TODO: Use the ViewModel
    }


    private void search() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

                if(rankAdapter!=null)
                {
                    rankAdapter.updateAdapter(newList);
                }

                return true;
            }
        });
    }
}
