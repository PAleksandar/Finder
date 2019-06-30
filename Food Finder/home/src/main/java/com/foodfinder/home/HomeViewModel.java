package com.foodfinder.home;

import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodfinder.acount.Account;
import com.foodfinder.acount.Position;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeViewModel extends ViewModel  implements TaskLoadedCallback {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Context mContext;
    private Activity mActivity;

    MarkerOptions place1,place2;
    Polyline currentPolyline;
    Dialog sendRequestDialog, orderDialog;

    private Location myCurrentPlace;
    private MutableLiveData<Place> RestaurantPlace;
    private MutableLiveData<String> selectedDriverId;

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

    public void initializeViewModel(Context context, Activity activity,Dialog sendRequestDialog, Dialog orderDialog)
    {
        mContext=context;
        mActivity=activity;
        mDatabase=FirebaseDatabase.getInstance();
        mRef=mDatabase.getReference();
        this.sendRequestDialog=sendRequestDialog;
        this.orderDialog=orderDialog;
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
                Log.e("langitude", String.valueOf(location.getLatitude()));
                Log.e("langitude", String.valueOf(location.getLongitude()));

                if(mMap!=null) {
                    LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

                    myCurrentPlace=location;

                    mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title("Iron Man")
                            .snippet("His Talent : Plenty of money"));


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
                addMarker(place);
                drawPrimaryLinePath(RestaurantPlace.getValue().getLatLng(), new LatLng(myCurrentPlace.getLatitude(), myCurrentPlace.getLongitude()));


            }

            @Override
            public void onError(@NonNull Status status) {

            }
        };
    }

    private void drawPath()
    {

    }

    private void addMarker(Place place)
    {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.restaurant_marker_round_orange);

        MarkerOptions markerOptions = new MarkerOptions().position(place.getLatLng())
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

                orderDialog.show();


            }
        };
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



            }
        };
    }

    public View.OnClickListener getSendRequestCancelButtonOnClickListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendRequestDialog.dismiss();



            }
        };
    }

    private void showDrivers()
    {
        DatabaseReference ref = mRef.child("users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count " ,""+snapshot.getChildrenCount());
               // List<Account> users=new ArrayList<Account>();
                for (DataSnapshot postSnapshot: snapshot.getChildren())
                {
                    Account acc = postSnapshot.getValue(Account.class);
                    ImageView v=new ImageView(mContext);
                    Picasso.get().load(acc.getProfileImage()).into(v);
                   // users.add(acc);
                    Log.e("Get Data", acc.getUserName());
                    if(acc!=null) {
                        addUserMarker(acc);
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
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.testuser);
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

    public void createCustomMarkerPom( Activity activity, Context context, String imageURI, String _name, Bitmap bmp, Account user) {

        final View marker = ((LayoutInflater) activity.getApplicationContext().getSystemService(activity.getApplicationContext().LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

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
        m.setTitle("Test");
        m.setTag("!!!!!:"+user.getUserId());





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
}




