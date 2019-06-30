package com.foodfinder.friends;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

public class FriendsFragment extends Fragment {

    private FriendsViewModel mViewModel;

    private Context mContext;

    private RecyclerView recyclerView;
    private SearchView searchView;
    private ImageView addFriends;

    public static FriendsFragment newInstance() {
        return new FriendsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.friends_fragment, container, false);

        initializeComponent(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);

        mViewModel.initializeViewModel(mContext);
        mViewModel.readUsers(recyclerView);
        searchView.setOnQueryTextListener(mViewModel.getSearchListener());
        addFriends.setOnClickListener(mViewModel.getAddFriendsListener());

    }

    private void initializeComponent(View view)
    {
        recyclerView = (RecyclerView) view.findViewById(R.id.friends_recycle_view);
        searchView=(SearchView) view.findViewById(R.id.search_view_friends);
        addFriends=(ImageView) view.findViewById(R.id.add_friends_view);

        mContext=getActivity().getApplicationContext();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

    }

}
