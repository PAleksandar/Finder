package com.foodfinder.home;

import android.Manifest;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;

import java.util.Arrays;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;
    private Context mContext;

    private LocationManager locationManager;
    private Button orderButton;
    private String mprovider;
    private Dialog sendRequestDialog;
    private Dialog orderDialog;
    private Button sendRequestDialogCancelBtn;
    private Button orderDialogCancelBtn;
    private Button orderDialogNextBtn;


    public HomeFragment(){}

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.home_fragment, container, false);
        final View sendDialogView=inflater.inflate(R.layout.processing_send_request_dialog, container,false);
        final View orderDialogView=inflater.inflate(R.layout.order_dialog, container,false);

        initializeComponent(view);
        initSendRequestDialog(sendDialogView);
        initOrderDialog(orderDialogView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mViewModel.initializeViewModel(mContext, getActivity(),sendRequestDialog,orderDialog);

        mViewModel.getRestaurantPlace().observe(this, getRestaurantPlaceObserver());
        mViewModel.getSelectedDriverId().observe(this,getSelectDriverObserver());
        orderButton.setOnClickListener(mViewModel.getDisableOrderButtonOnClickListener());

        setMap();
        setCurrentLocation();
        setAutoComplete();
        initDialogButtons();

    }

    private void initializeComponent(View view)
    {
        orderButton=(Button) view.findViewById(R.id.button_order);
        mContext=getActivity().getApplicationContext();
        locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
        mprovider = locationManager.getBestProvider(new Criteria(), true);
        Places.initialize(mContext, "key");
    }

    private void setMap()
    {
        if(getActivity()!=null) {

            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(mViewModel.getOnMapReadyCallback());
            }

        }
    }

    private void setCurrentLocation()
    {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        else {

            locationManager.requestLocationUpdates(mprovider, 1500, 1, mViewModel.getLocationListener());

        }
    }

    private void setAutoComplete()
    {


        AutocompleteSupportFragment  autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setHint("Choose food location");

        autocompleteFragment.setCountry("RS");

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(mViewModel.getPlaceSelectionListener());
    }

    private void initSendRequestDialog(View view)
    {

        sendRequestDialog=new Dialog(getContext(),android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        sendRequestDialog.setContentView(view);

        sendRequestDialogCancelBtn=(Button) view.findViewById(R.id.cancelDialog);

    }

    private void initOrderDialog(View view)
    {
        orderDialog=new Dialog(getContext(),android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        orderDialog.setContentView(view);

        orderDialogNextBtn=(Button) view.findViewById(R.id.order_dialog_btn);
        orderDialogCancelBtn=(Button) view.findViewById(R.id.cancel_order_dialog_btn);

    }

    private void initDialogButtons()
    {
        sendRequestDialogCancelBtn.setOnClickListener(mViewModel.getSendRequestCancelButtonOnClickListener());

        orderDialogNextBtn.setOnClickListener(mViewModel.getDialogOrderButtonOnClickListener());
        orderDialogCancelBtn.setOnClickListener(mViewModel.getDialogOrderCancelButtonOnClickListener());
    }

    private Observer<Place> getRestaurantPlaceObserver()
    {
        return new Observer<Place>() {
            @Override
            public void onChanged(@Nullable final Place newPlace) {

                orderButton.setText("Find driver");
                orderButton.setTextAppearance(mContext, R.style.orange_order_round_button);
                orderButton.setBackground(getResources().getDrawable(R.drawable.orange_order_button));
                orderButton.setAlpha(1);

                orderButton.setOnClickListener(mViewModel.getFindDriverButtonOnClickListener());

            }
        };
    }

    private Observer<String> getSelectDriverObserver()
    {
        return new Observer<String>() {
            @Override
            public void onChanged(@Nullable String driverId) {

                orderButton.setText("Order");
                orderButton.setTextAppearance(mContext, R.style.orange_order_round_button);
                orderButton.setBackground(getResources().getDrawable(R.drawable.orange_order_button));
                orderButton.setAlpha(1);
                orderButton.setOnClickListener(mViewModel.getOrderButtonOnClickListener());


            }
        };
    }





}
