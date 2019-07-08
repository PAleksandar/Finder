package com.foodfinder.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foodfinder.acount.Account;
import com.foodfinder.acount.Position;
import com.foodfinder.acount.Request;
import com.foodfinder.acount.UUID;
import com.foodfinder.home.RoutesHelper.FetchURL;
import com.foodfinder.home.RoutesHelper.TaskLoadedCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeViewModel extends ViewModel  implements TaskLoadedCallback {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Marker userMarker;
    private Context mContext;
    private Activity mActivity;

    MarkerOptions place1,place2;
    Polyline currentPolyline;
    Dialog sendRequestDialog, orderDialog, orderArrivedDialog, rateDialog, driverNotificationDialog;

    private Location myCurrentPlace;
    private MutableLiveData<Place> RestaurantPlace;
    private MutableLiveData<String> selectedDriverId;

    private Account selectedDriverAccount;
    private Bitmap driverImage;
    private Marker selectedDriverMarker;


    private MutableLiveData<Bitmap> selectedDriverImage;
    private MutableLiveData<String> selectedDriverName;
    private MutableLiveData<Boolean> isDriver;
    private MutableLiveData<Boolean> isShow;
    private MutableLiveData<Boolean> isAccept;
    private MutableLiveData<Boolean> isFinish;
    private MutableLiveData<Location> currentLocation;

    private String currentRequestId;
    private String currentCreateRequestId;
    private int rank;
    private String orderMessage="";
    private Location finishPosition;


    public MutableLiveData<Location> getCurrentLocation() {
        if (currentLocation == null) {
            currentLocation = new MutableLiveData<Location>();
        }
        return currentLocation;
    }

    public MutableLiveData<Boolean> getIsFinish() {
        if (isFinish == null) {
            isFinish = new MutableLiveData<Boolean>();
        }
        return isFinish;
    }

    public MutableLiveData<Boolean> getIsAccept() {
        if (isAccept == null) {
            isAccept = new MutableLiveData<Boolean>();
        }
        return isAccept;
    }

    public MutableLiveData<Boolean> getIsDriver() {
        if (isDriver == null) {
            isDriver = new MutableLiveData<Boolean>();
        }
        return isDriver;
    }

    public MutableLiveData<Boolean> getIsShow() {
        if (isShow == null) {
            isShow = new MutableLiveData<Boolean>();
        }
        return isShow;
    }

    public MutableLiveData<String> getSelectDriverName() {
        if (selectedDriverName == null) {
            selectedDriverName = new MutableLiveData<String>();
        }
        return selectedDriverName;
    }

    public MutableLiveData<Bitmap> getSelectDriverImage() {
        if (selectedDriverImage == null) {
            selectedDriverImage = new MutableLiveData<Bitmap>();
        }
        return selectedDriverImage;
    }

    public MutableLiveData<Place> getRestaurantPlace() {
        if (RestaurantPlace == null) {
            RestaurantPlace = new MutableLiveData<Place>();
        }
        return RestaurantPlace;
    }

    public MutableLiveData<String> getSelectedDriverId() {
        if (selectedDriverId == null) {
            selectedDriverId = new MutableLiveData<String>();
        }
        return selectedDriverId;
    }

    public void initializeViewModel(Context context, Activity activity,Dialog sendRequestDialog, Dialog orderDialog, Dialog orderArrivedDialog, Dialog rateDialog, Dialog driverNotificationDialog)
    {
        mContext=context;
        mActivity=activity;
        mDatabase=FirebaseDatabase.getInstance();
        mRef=mDatabase.getReference();
        this.sendRequestDialog=sendRequestDialog;
        this.orderDialog=orderDialog;
        this.orderArrivedDialog=orderArrivedDialog;
        this.rateDialog=rateDialog;
        this.driverNotificationDialog=driverNotificationDialog;
        isDriver();
    }

    public OnMapReadyCallback getOnMapReadyCallback()
    {
       return new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        if(marker !=null && marker.getTag()!=null)
                        {
                            String[] separated = marker.getTag().toString().split(":");
                            if(separated[0].equals("!!!!!"))
                            {
                                Log.e("map click marker", separated[1]);
                                selectedDriverId.setValue(separated[1]);
                               // readDriverData();
                            }

                        }


                        return false;
                    }
                });


            }
        };
    }

    public GoogleMap getMap() {
        return mMap;
    }

    public LocationListener getLocationListener()
    {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if(currentLocation.getValue()==null) {
                    currentLocation.setValue(location);
                }

                if(finishPosition!=null)
                {
                    float distance=finishPosition.distanceTo(location);
                    Log.e("distance ::", "onLocationChanged: "+distance );
                    if(distance<15)
                    {
                        showFinishMessage();
                    }
                }

                if(mMap!=null) {
                    LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

                    myCurrentPlace=location;

                    boolean isSet=false;
                    if(userMarker!=null)
                    {
                        userMarker.remove();
                        isSet=true;
                    }

                   userMarker= mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title("My position"));



                    if(!isSet) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15), 2000, new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {

                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    }
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    public com.google.android.libraries.places.widget.listener.PlaceSelectionListener getPlaceSelectionListener()
    {
        return new com.google.android.libraries.places.widget.listener.PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull com.google.android.libraries.places.api.model.Place place) {

                RestaurantPlace.setValue(place);
                addMarker(place.getLatLng());
                drawPrimaryLinePath(RestaurantPlace.getValue().getLatLng(), new LatLng(myCurrentPlace.getLatitude(), myCurrentPlace.getLongitude()));


            }

            @Override
            public void onError(@NonNull Status status) {

            }
        };
    }


    private void addMarker(LatLng place)
    {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.restaurant_marker_round_orange);

        MarkerOptions markerOptions = new MarkerOptions().position(place)
                .title("Current Location")
                .snippet("Thinking of finding some thing...")
                .icon(icon);

        mMap.addMarker(markerOptions);
    }

    public View.OnClickListener getDisableOrderButtonOnClickListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("click disable  order", "onClick: ");

            }
        };
    }

    public View.OnClickListener getFindDriverButtonOnClickListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("click find driver", "onClick: ");
                showDrivers();

            }
        };
    }

    public View.OnClickListener getOrderButtonOnClickListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                readDriverData();
//                View order = ((LayoutInflater) mActivity.getApplicationContext().getSystemService(mActivity.getApplicationContext().LAYOUT_INFLATER_SERVICE)).inflate(R.layout.order_dialog, null);
//                CircleImageView image = (CircleImageView) order.findViewById(R.id.order_dialog_image);
//                if(driverImage!=null) {
//
//                    image.setImageBitmap(driverImage);
//                    Log.e("set driver image", "onClick: " );
//                }
//                else
//                {
//                    Log.e("driver image null", "onClick: ");
//                   // setDialogImage(image);
//                }
                orderDialog.show();


            }
        };
    }

    private void setDialogImage(final CircleImageView image)
    {
        StorageReference islandRef = FirebaseStorage.getInstance().getReference().child("profileImages/"+selectedDriverId.getValue()+".jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                image.setImageBitmap(bitmap);
                orderDialog.dismiss();
                orderDialog.show();
                Log.e("driver image dialog set", "onSuccess: " );

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public View.OnClickListener getDialogOrderCancelButtonOnClickListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                orderDialog.dismiss();



            }
        };
    }
    public View.OnClickListener getDialogOrderButtonOnClickListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                orderDialog.dismiss();
                sendRequestDialog.show();

                sendRequest();

            }
        };
    }



    public View.OnClickListener getDialogOrderArrivedCancelButtonOnClickListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // orderDialog.dismiss();
                orderArrivedDialog.dismiss();
                deleteFinishRequests();



            }
        };
    }
    public View.OnClickListener getDialogOrderArrivedConfirmButtonOnClickListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                orderArrivedDialog.dismiss();
                rateDialog.show();
                deleteFinishRequests();

            }
        };
    }

    public View.OnClickListener getDialogDriverNotificationCancelButtonOnClickListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // orderDialog.dismiss();
                setFinishInRequest();
                driverNotificationDialog.dismiss();
                mActivity.finish();



            }
        };
    }
    public View.OnClickListener getDialogDriverNotificationShowButtonOnClickListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDriverRequest();
                isShow.setValue(true);
                driverNotificationDialog.dismiss();


            }
        };
    }


    public View.OnClickListener getSendRequestCancelButtonOnClickListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isShow.setValue(false);
                sendRequestDialog.dismiss();
                if(currentCreateRequestId!=null && selectedDriverId!=null) {
                    deleteRequest(selectedDriverId.getValue(), currentCreateRequestId);
                }



            }
        };
    }

    public View.OnClickListener getRateButtonOnClickListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                        rateDialog.dismiss();
                        refreshHome();

                        updateRank(rank);



            }
        };
    }


    public View.OnClickListener getAcceptButtonOnClickListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAccept.setValue(true);
                setAcceptInRequest();
            }
        };
    }

    public View.OnClickListener getCancelRequestButtonOnClickListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isAccept.setValue(false);
                setFinishInRequest();

            }
        };
    }

    public View.OnClickListener getFinishButtonOnClickListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setFinishInRequest();
                mActivity.finish();

            }
        };
    }


    private void showDrivers()
    {
        DatabaseReference ref = mRef.child("users");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count " ,""+snapshot.getChildrenCount());
               // List<Account> users=new ArrayList<Account>();
                for (DataSnapshot postSnapshot: snapshot.getChildren())
                {
                    Account acc = postSnapshot.getValue(Account.class);
                    if(acc!=null) {

                        Location driverLocaction=new Location("");
                        driverLocaction.setLatitude(acc.getPlace().getLatitude());
                        driverLocaction.setLongitude(acc.getPlace().getLongitude());
                        float distance=driverLocaction.distanceTo(currentLocation.getValue());

                        if(distance<=5000) {
                            addUserMarker(acc);
                        }
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }

        });

    }

    private void addUserMarker(Account user)
    {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.default_profile);
        if(user.getPlace()==null) {
            return;
        }
       loadImage(user.getUserId(),mActivity,mContext,user.getProfileImage(),"test!!!",user);

    }

    private void loadImage(final String name, final Activity activity, final Context context, final String imageURI, final String _name, final Account user)
    {
        StorageReference islandRef = FirebaseStorage.getInstance().getReference().child("profileImages/"+name+".jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                createCustomMarkerPom(activity,context,imageURI,_name,bitmap, user);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private void isFriend(final Activity activity, final String _name, final Bitmap bmp, final Account user)
    {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String userId=currentFirebaseUser.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("friends");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                ArrayList<String> friends = snapshot.getValue(t);

                //////////////////
                final View marker = ((LayoutInflater) activity.getApplicationContext().getSystemService(activity.getApplicationContext().LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

                if(friends!=null) {
                    if (!inArray(user.getUserId(), friends)) {
                        RelativeLayout layout = (RelativeLayout) marker.findViewById(R.id.driver_marker_color);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            layout.setBackgroundTintList(mActivity.getResources().getColorStateList(R.color.grey));
                        }
                    }
                }

                CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
                markerImage.setImageBitmap(bmp);

                TextView txt_name = (TextView)marker.findViewById(R.id.name);
                txt_name.setText(_name);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
                marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
                marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
                marker.buildDrawingCache();
                Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                marker.draw(canvas);

                Position p = user.getPlace();
                LatLng position = new LatLng(p.getLatitude(), p.getLongitude());

                Marker m=mMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                m.setTitle(user.getUserName()+"  "+user.getLastName());
                m.setSnippet("Phone: "+user.getPhone());
                m.setTag("!!!!!:"+user.getUserId());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private boolean inArray(String user, ArrayList<String> friends)
    {
        boolean pom=false;

        for(String friend:friends)
        {
            if(friend.equals(user))
            {
                pom=true;
            }
        }

        return pom;
    }

    public void createCustomMarkerPom( Activity activity, Context context, String imageURI, String _name, Bitmap bmp, Account user) {

        isFriend(activity,_name,bmp,user);

//        final View marker = ((LayoutInflater) activity.getApplicationContext().getSystemService(activity.getApplicationContext().LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
//
//        RelativeLayout layout=(RelativeLayout) marker.findViewById(R.id.driver_marker_color);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            layout.setBackgroundTintList(mActivity.getResources().getColorStateList(R.color.grey));
//        }
//
//        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
//        markerImage.setImageBitmap(bmp);
//
//        TextView txt_name = (TextView)marker.findViewById(R.id.name);
//        txt_name.setText(_name);
//
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
//        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
//        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
//        marker.buildDrawingCache();
//        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        marker.draw(canvas);
//
//        Position p = user.getPlace();
//        LatLng position = new LatLng(p.getLatitude(), p.getLongitude());
//
//        Marker m=mMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
//        m.setTitle("Test");
//        m.setTag("!!!!!:"+user.getUserId());

    }

    private void drawPrimaryLinePath(LatLng startPlace, LatLng endPlace)
    {

//        place1=new MarkerOptions().position(new LatLng(43.338183, 21.884959)).title("place 1");
//        place2=new MarkerOptions().position(new LatLng(43.329067, 21.889423)).title("place 2");

        place1=new MarkerOptions().position(startPlace);
        place2=new MarkerOptions().position(endPlace);

        new FetchURL(mContext,getLoadedCallback()).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");


        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(place1.getPosition());
        builder.include(place2.getPosition());

        LatLngBounds bounds = builder.build();
        int padding = 250;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.moveCamera(cu);

        mMap.animateCamera(cu, 2000, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onCancel() {

            }
        });
        //googleMap.animateCamera(cu);
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "key";
        return url;
    }


    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
       // currentPolyline.setColor();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    public TaskLoadedCallback getLoadedCallback()
    {
        return new TaskLoadedCallback() {
            @Override
            public void onTaskDone(Object... values) {
                if (currentPolyline != null)
                    currentPolyline.remove();
                PolylineOptions pl=(PolylineOptions) values[0];
                PolylineOptions pol=new PolylineOptions().addAll(pl.getPoints()).color(Color.rgb(255, 59, 23));
                currentPolyline = mMap.addPolyline(pol);



            }
        };
    }

    public TextWatcher getTextWatcher()
    {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                orderMessage=s.toString();
            }
        };
    }

    private void sendRequest()
    {
        Request request=new Request();
        LatLng position=RestaurantPlace.getValue().getLatLng();
        request.setStart(new Position(position.latitude,position.longitude));
        request.setEnd(new Position(myCurrentPlace.getLatitude(), myCurrentPlace.getLongitude()));
        request.setReceiver(selectedDriverId.getValue());
        request.setArrived(false);
        request.setFinished(false);

       // EditText msg=(EditText)orderDialog.getLayoutInflater().inflate(R.layout.order_dialog,).findViewById(R.id.order_message);
        request.setMessage("test request message");

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String senderId=currentFirebaseUser.getUid();
        request.setSender(senderId);
        String requestId=senderId+UUID.getNextNumber();
        request.setId(requestId);

        currentCreateRequestId=requestId;

        DatabaseReference ref = mRef.child("requests").child(selectedDriverId.getValue()).child(requestId);
        ref.setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                // ...
               Toast.makeText(mContext, "request Successfully write", Toast.LENGTH_LONG).show();
                //startNavigationActivity();


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        // ...
                        Toast.makeText(mContext, "request write  failed ", Toast.LENGTH_LONG).show();

                    }
                });

        setRequestListener(requestId);
    }

    private void setRequestListener(String requestId){


        final DatabaseReference ref = mRef.child("requests").child(selectedDriverId.getValue()).child(requestId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Request req = snapshot.getValue(Request.class);
                if(req!=null)
                {
                    if(req.getArrived())
                    {
                        sendRequestDialog.dismiss();
                        refreshMap();
                    }

                    if(req.getFinished() && req.getArrived())
                    {
                        //start arrived dialog
                        orderArrivedDialog.show();
                    }

                    if(req.getFinished() && !req.getArrived())
                    {
                        sendRequestDialog.dismiss();
                        mMap.clear();
                        userMarker=null;
                        isFinish.setValue(true);
                        deleteFinishRequests();

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }

        });
    }

    private void refreshMap()
    {
        mMap.clear();
        LatLng position = new LatLng(myCurrentPlace.getLatitude(), myCurrentPlace.getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(position)
                .title("Iron Man")
                .snippet("His Talent : Plenty of money"));
        addMarker(RestaurantPlace.getValue().getLatLng());
        drawPrimaryLinePath(RestaurantPlace.getValue().getLatLng(), new LatLng(myCurrentPlace.getLatitude(), myCurrentPlace.getLongitude()));

        setDriverPositionListener();
    }

    private void setDriverPositionListener(){

        final DatabaseReference ref = mRef.child("users").child(selectedDriverId.getValue()).child("place");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Position position = snapshot.getValue(Position.class);
                if(position!=null)
                {
                   // showDriver(selectedDriverId.getValue());
                    selectedDriverAccount.setPlace(position);
                    refreshDriverMarker();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }

        });
    }



    private void readDriverData()
    {
        DatabaseReference ref = mRef.child("users").child(selectedDriverId.getValue());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Account driver = snapshot.getValue(Account.class);
                if(driver !=null)
                {
                   selectedDriverAccount=driver;
                   selectedDriverName.setValue(driver.getUserName()+"  "+driver.getLastName());
                   readDriverImage();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void readDriverImage()
    {
        StorageReference islandRef = FirebaseStorage.getInstance().getReference().child("profileImages/"+selectedDriverId.getValue()+".jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                driverImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                selectedDriverImage.setValue(driverImage);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private void refreshDriverMarker()
    {
        final View marker = ((LayoutInflater) mActivity.getApplicationContext().getSystemService(mActivity.getApplicationContext().LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        if(driverImage!=null) {
            markerImage.setImageBitmap(driverImage);
        }

        if(selectedDriverAccount!=null) {
            TextView txt_name = (TextView) marker.findViewById(R.id.name);
            txt_name.setText("txt_name");

            DisplayMetrics displayMetrics = new DisplayMetrics();
            mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
            marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
            marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
            marker.buildDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            marker.draw(canvas);

            Position p = selectedDriverAccount.getPlace();
            LatLng position = new LatLng(p.getLatitude(), p.getLongitude());

            if(selectedDriverMarker!=null)
            {
                selectedDriverMarker.remove();
            }
            selectedDriverMarker = mMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
            selectedDriverMarker.setTitle("Test");
            selectedDriverMarker.setTag("!!!!!:" + selectedDriverId);
        }
    }

    private void rate(ArrayList<ImageView> stars, int rate)
    {
        rank=rate+1;
        for(int i=rate; i>=0; i--)
        {
            stars.get(i).setImageResource(R.drawable.gold_star);
        }

        for(int i=rate+1; i<5; i++)
        {
            stars.get(i).setImageResource(R.drawable.black_star);
        }
    }

    public View.OnClickListener getStarsButtonOnClickListener(final ArrayList<ImageView> stars, final int rate)
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rate(stars,rate);

            }
        };
    }

    private void updateRank(final float rank)
    {

        DatabaseReference ref = mRef.child("users").child(selectedDriverId.getValue()).child("ranks");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                GenericTypeIndicator<ArrayList<Float>> t = new GenericTypeIndicator<ArrayList<Float>>() {};

                ArrayList<Float> ranks = snapshot.getValue(t);
                if(ranks !=null)
                {
                   ranks.add(rank);

                }
                else
                {
                    ranks=new ArrayList<Float>();
                    ranks.add(rank);
                }
                saveRanks(ranks);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    private void saveRanks(List<Float> ranks)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(selectedDriverId.getValue()).child("ranks");
        ref.setValue(ranks).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                    }
                });
    }

    public void isDriver()
    {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String userId=currentFirebaseUser.getUid();

        DatabaseReference ref = mRef.child("users").child(userId).child("driver");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {



                boolean ranks = snapshot.getValue(boolean.class);
                isDriver.setValue(ranks);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }


    private void deleteFinishRequests()
    {
         DatabaseReference ref = mRef.child("requests").child(selectedDriverId.getValue());


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for(DataSnapshot ds : snapshot.getChildren()) {
                    Request r = ds.getValue(Request.class);

                    if(r.getFinished())
                    {
                        deleteRequest(r.getReceiver(), r.getId());
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void deleteRequest(String driver, String request)
    {
        mRef.child("requests").child(driver).child(request).removeValue();
    }

    public void setCurrentRequestId(String currentRequestId) {
        this.currentRequestId = currentRequestId;
    }

    private void showDriverRequest()
    {

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String userId=currentFirebaseUser.getUid();

        DatabaseReference ref = mRef.child("requests").child(userId).child(currentRequestId);


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Request r = snapshot.getValue(Request.class);

                Position start=r.getStart();
                Position end=r.getEnd();
                finishPosition=new Location("");
                finishPosition.setLatitude(end.getLatitude());
                finishPosition.setLongitude(end.getLongitude());


                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.flag_marker);

                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(end.getLatitude(), end.getLongitude()))
                        .title("Final Location")
                        .icon(icon);

                mMap.addMarker(markerOptions);

                addMarker(new LatLng(start.getLatitude(), start.getLongitude()));
                drawPrimaryLinePath(new LatLng(start.getLatitude(), start.getLongitude()), new LatLng(end.getLatitude(), end.getLongitude()));



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    private void setFinishInRequest()
    {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String userId=currentFirebaseUser.getUid();

        DatabaseReference ref = mRef.child("requests").child(userId).child(currentRequestId).child("isFinished");

        ref.setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                    }
                });
    }

    private void setAcceptInRequest()
    {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String userId=currentFirebaseUser.getUid();

        DatabaseReference ref = mRef.child("requests").child(userId).child(currentRequestId).child("isArrived");

        ref.setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                    }
                });
    }

    private void refreshHome()
    {
        mMap.clear();
        userMarker=null;
        isFinish.setValue(true);
    }

    public void driverNotification()
    {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String userId=currentFirebaseUser.getUid();

        final DatabaseReference ref = mRef.child("requests").child(userId).child(currentRequestId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if(snapshot==null)
                {
                    showDeleteMessage();
                }
                else
                {
                    Request r = snapshot.getValue(Request.class);

                    if(r==null)
                    {
                        showDeleteMessage();
                    }
                    else {
                        driverNotificationDialog.show();
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }

        });
//
    }

    private void showDeleteMessage()
    {
        new AlertDialog.Builder(mActivity)
                .setTitle("Delete entry")
                .setMessage("Request is delete")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        mActivity.finish();
                    }
                })

                //.setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    private void showFinishMessage()
    {
        new AlertDialog.Builder(mActivity)
                .setTitle("Finish")
                .setMessage("You are at your finish destination")


                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        setFinishInRequest();
                        mActivity.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();


    }
}




