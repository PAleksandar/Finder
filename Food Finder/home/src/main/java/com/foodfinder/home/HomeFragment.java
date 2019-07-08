package com.foodfinder.home;

import android.Manifest;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.internal.impl.net.pablo.PlaceResult;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;
    private Context mContext;

    private LocationManager locationManager;
    private AutocompleteSupportFragment  autocompleteFragment;
    private Button orderButton;
    private Button acceptButton;
    private String mprovider;
    private Dialog sendRequestDialog;
    private Dialog orderDialog;
    private Dialog orderArrivedDialog;
    private Dialog rateDialog;
    private Dialog driverNotificationDialog;
    private Button sendRequestDialogCancelBtn;
    private Button orderDialogCancelBtn;
    private Button orderDialogNextBtn;
    private Button orderArrivedDialogCancelBtn;
    private Button orderArrivedDialogConfirmBtn;
    private Button driverNotificationDialogCancelBtn;
    private Button driverNotificationDialogShowBtn;
    private List<ImageView> stars;
    private Button rateButton;
    private List<CircleImageView> dialogImages;
    private List<TextView> dialogNames;
    private EditText msg;


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
        final View orderArrivedDialogView=inflater.inflate(R.layout.order_arrived_dialog, container,false);
        final View rateDialogView=inflater.inflate(R.layout.rate_dialog, container,false);
        final View driverNotificationDialogView=inflater.inflate(R.layout.driver_notification_dialog, container,false);

        initializeComponent(view);
        initSendRequestDialog(sendDialogView);
        initOrderDialog(orderDialogView);
        initOrderArrivedDialog(orderArrivedDialogView);
        initRateDialog(rateDialogView);
        initDriverNotificationDialog(driverNotificationDialogView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mViewModel.initializeViewModel(mContext, getActivity(),sendRequestDialog,orderDialog,orderArrivedDialog,rateDialog,driverNotificationDialog);

        mViewModel.getRestaurantPlace().observe(this, getRestaurantPlaceObserver());
        mViewModel.getSelectedDriverId().observe(this,getSelectDriverObserver());
        mViewModel.getSelectDriverImage().observe(this,getSelectDriverImageObserver());
        mViewModel.getSelectDriverName().observe(this,getSelectDriverNameObserver());
        mViewModel.getIsDriver().observe(this,getIsDriverObserver());
        mViewModel.getIsShow().observe(this,getIsShowObserver());
        mViewModel.getIsAccept().observe(this,getIsAcceptObserver());
        mViewModel.getIsFinish().observe(this, getIsFinishObserver());
        mViewModel.getCurrentLocation().observe(this,getCurrentLocationObserver());
        orderButton.setOnClickListener(mViewModel.getDisableOrderButtonOnClickListener());

        setMap();
        setCurrentLocation();
        setAutoComplete();
        initDialogButtons();
        setNotificationStart();


    }

    private void initializeComponent(View view)
    {
        orderButton=(Button) view.findViewById(R.id.button_order);
        acceptButton=(Button) view.findViewById(R.id.button_accept);
        mContext=getActivity().getApplicationContext();
        locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
        mprovider = locationManager.getBestProvider(new Criteria(), true);
        Places.initialize(mContext, "AIzaSyBWywkgfdO8uiFe4BJxKPpZEqruuotsj6U");

        dialogImages=new ArrayList<CircleImageView>();
        dialogNames=new ArrayList<TextView>();
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

            Log.e("set location", "setCurrentLocation:  if " );
        }
        else {

            locationManager.requestLocationUpdates(mprovider, 1500, 1, mViewModel.getLocationListener());

            Log.e("set location", "setCurrentLocation:  else " );
        }
    }

    private void setAutoComplete()
    {


        autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setHint("Choose food location");

        autocompleteFragment.setCountry("RS");

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

//        autocompleteFragment.setLocationRestriction(RectangularBounds.newInstance(
//                new LatLng(43.293569, 21.853535),
//                new LatLng(43.343996, 21.947483)));
//       // autocompleteFragment.setOnPlaceSelectedListener(mViewModel.getPlaceSelectionListener());


    }

    private void setIsDriver()
    {
        autocompleteFragment.getView().setVisibility(View.GONE);
        orderButton.setVisibility(View.GONE);
    }

    private void setIsCustomer()
    {
        autocompleteFragment.getView().setVisibility(View.VISIBLE);
        orderButton.setVisibility(View.VISIBLE);
    }
    
    private void setNotificationStart()
    {
        Bundle arguments = getArguments();
        if(arguments!=null) {
            String id = arguments.getString("notification_start");
           // if(id!=null)
            {
                Log.e("notification start home", id);
                mViewModel.setCurrentRequestId(id);
//                driverNotificationDialog.show();
                mViewModel.driverNotification();
            }
        }
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

        msg=(EditText) view.findViewById(R.id.order_message);

        dialogImages.add((CircleImageView) view.findViewById(R.id.order_dialog_image));
        dialogNames.add((TextView) view.findViewById(R.id.order_dialog_name));



    }

    private void initOrderArrivedDialog(View view)
    {
        orderArrivedDialog=new Dialog(getContext(),android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        orderArrivedDialog.setContentView(view);

        orderArrivedDialogCancelBtn=(Button) view.findViewById(R.id.cancel_order_arrived_dialog_btn);
        orderArrivedDialogConfirmBtn=(Button) view.findViewById(R.id.confirm_order_arrived_dialog_btn);

        dialogImages.add((CircleImageView) view.findViewById(R.id.order_arrived_dialog_image));
        dialogNames.add((TextView) view.findViewById(R.id.order_arrived_dialog_name));

    }

    private void initRateDialog(View view)
    {
        rateDialog=new Dialog(getContext(),android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        rateDialog.setContentView(view);

        stars=new ArrayList<ImageView>();
        stars.add((ImageView) view.findViewById(R.id.star1));
        stars.add((ImageView) view.findViewById(R.id.star2));
        stars.add((ImageView) view.findViewById(R.id.star3));
        stars.add((ImageView) view.findViewById(R.id.star4));
        stars.add((ImageView) view.findViewById(R.id.star5));

        rateButton=(Button) view.findViewById(R.id.rate_dialog_btn);

        dialogImages.add((CircleImageView) view.findViewById(R.id.rate_dialog_image));
        dialogNames.add((TextView) view.findViewById(R.id.rate_dialog_name));

    }

    private void initDriverNotificationDialog(View view)
    {
        driverNotificationDialog=new Dialog(getContext(),android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        driverNotificationDialog.setContentView(view);

        driverNotificationDialogShowBtn=(Button) view.findViewById(R.id.show_driver_notification_dialog_btn);
        driverNotificationDialogCancelBtn=(Button) view.findViewById(R.id.cancel_driver_notification_dialog_btn);
    }

    private void initDialogButtons()
    {
        sendRequestDialogCancelBtn.setOnClickListener(mViewModel.getSendRequestCancelButtonOnClickListener());

        orderDialogNextBtn.setOnClickListener(mViewModel.getDialogOrderButtonOnClickListener());
        orderDialogCancelBtn.setOnClickListener(mViewModel.getDialogOrderCancelButtonOnClickListener());

        orderArrivedDialogCancelBtn.setOnClickListener(mViewModel.getDialogOrderArrivedCancelButtonOnClickListener());
        orderArrivedDialogConfirmBtn.setOnClickListener(mViewModel.getDialogOrderArrivedConfirmButtonOnClickListener());


        rateButton.setOnClickListener(mViewModel.getRateButtonOnClickListener());

        for (int i=0; i<5; i++)
        {
            stars.get(i).setOnClickListener(mViewModel.getStarsButtonOnClickListener((ArrayList<ImageView>) stars,i));
        }

        driverNotificationDialogShowBtn.setOnClickListener(mViewModel.getDialogDriverNotificationShowButtonOnClickListener());
        driverNotificationDialogCancelBtn.setOnClickListener(mViewModel.getDialogDriverNotificationCancelButtonOnClickListener());

        msg.addTextChangedListener(mViewModel.getTextWatcher());

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

    private Observer<Bitmap> getSelectDriverImageObserver()
    {
        return new Observer<Bitmap>() {
            @Override
            public void onChanged(@Nullable Bitmap image) {

                for (CircleImageView imageView: dialogImages)
                {
                    imageView.setImageBitmap(image);
                }

            }
        };
    }

    private Observer<String> getSelectDriverNameObserver()
    {
        return new Observer<String>() {
            @Override
            public void onChanged(@Nullable String name) {

                for (TextView nameView: dialogNames)
                {
                    nameView.setText(name);
                }

            }
        };
    }


    private Observer<Boolean> getIsDriverObserver()
    {
        return new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isDriver) {

                if(isDriver)
                {
                    setIsDriver();
                }
                else {
                    setIsCustomer();
                }


            }
        };
    }

    private Observer<Boolean> getIsShowObserver()
    {
        return new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isShow) {


                    setDriverShowRequest(isShow);



            }
        };
    }

    private void setDriverShowRequest(boolean isShow)
    {
        if(isShow)
        {
            acceptButton.setVisibility(View.VISIBLE);
            orderButton.setVisibility(View.VISIBLE);
            orderButton.setText("CANCEL");
           // orderButton.setBackgroundColor(getResources().getColor(R.color.grey));

            acceptButton.setOnClickListener(mViewModel.getAcceptButtonOnClickListener());
            orderButton.setOnClickListener(mViewModel.getCancelRequestButtonOnClickListener());
        }
        else {

            acceptButton.setVisibility(View.GONE);
            orderButton.setVisibility(View.VISIBLE);
        }
    }

    private Observer<Boolean> getIsAcceptObserver()
    {
        return new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isAccept) {


                setDriverAcceptRequest(isAccept);



            }
        };
    }

    private Observer<Boolean> getIsFinishObserver()
    {
        return new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isFinish) {


                if(isFinish)
                {
                    orderButton.setText("ORDER");
                   // orderButton.setTextAppearance(mContext, R.style.orange_order_round_button);
                    orderButton.setBackground(getResources().getDrawable(R.drawable.grey_order_button));
                    orderButton.setAlpha((float) 0.4);
                    orderButton.setOnClickListener(null);

                }



            }
        };
    }

    private Observer<Location> getCurrentLocationObserver()
    {
        return new Observer<Location>() {
            @Override
            public void onChanged(@Nullable Location currentLocation) {

                autocompleteFragment.setLocationRestriction(RectangularBounds.newInstance(
                        new LatLng(currentLocation.getLatitude()-0.09, currentLocation.getLongitude()-0.09),
                        new LatLng(currentLocation.getLatitude()+0.09, currentLocation.getLongitude()+0.09)));

                autocompleteFragment.setOnPlaceSelectedListener(mViewModel.getPlaceSelectionListener());

            }
        };
    }

    private void setDriverAcceptRequest(boolean isAccept)
    {
        if(isAccept)
        {
            acceptButton.setVisibility(View.GONE);
            orderButton.setVisibility(View.VISIBLE);
            orderButton.setText("FINISH");
            orderButton.setBackground(getResources().getDrawable(R.drawable.orange_order_button));
            orderButton.setAlpha(1);
            orderButton.setOnClickListener(mViewModel.getFinishButtonOnClickListener());
        }
        else
        {
            getActivity().finish();

        }
    }


}
