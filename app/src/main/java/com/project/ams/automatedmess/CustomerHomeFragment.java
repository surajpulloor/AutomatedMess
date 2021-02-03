package com.project.ams.automatedmess;


import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerHomeFragment extends Fragment  implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks{

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION=101;
    private static final int MY_PERMISSION_REQUEST_COARSE_LOCATION=102;
    private boolean permissionIsGranted=false;
    LocationManager locationManager;
    String provider;

    // Firebase Stuff
    private DatabaseReference dbRef;

    // Used to Store Mess Providers Info.
    ArrayList<MessProvider> messProvidersInfo = new ArrayList<>();

    private static final String TAG = "MapsActivity";

    private boolean doubleClicked = false;

    private OrderItemsViewModel mOrderItemsViewModel;


    public CustomerHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mOrderItemsViewModel = ViewModelProviders.of(getActivity()).get(OrderItemsViewModel.class);

        mOrderItemsViewModel.getAllItems().observe(this, new Observer<List<OrderItem>>() {
            @Override
            public void onChanged(@Nullable final List<OrderItem> items) {
                ((TextView) getActivity().findViewById(R.id.badge_notification)).setText(String.valueOf(items.size()));
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        checkLocationPermission();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        buildGoogleApiClient();


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.addMarker(new MarkerOptions().position(latLng).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));


        // ---- Retrive Mess Prover Info ---- //
        // ---- Start ---- //

        // Init Firebase
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child("MessProviders");

        // Get the MessProviders values from the database
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                double latitude = 0;
                double longitude = 0;

                // Gather the location values
                for (DataSnapshot messProviders : dataSnapshot.getChildren()) {

                    // Create an instance of MessProvider
                    MessProvider mp = new MessProvider();

                    // Set the uId for mp object
                    mp.setuID(messProviders.getKey());

                    for (DataSnapshot ds : messProviders.getChildren()) {

                        if (ds.getKey().equals("ProfileInformation")) {
                            for (DataSnapshot ds1 : ds.getChildren()) {
                                if (ds1.getKey().equals("mProviderLocation")) {

                                    for (DataSnapshot ds2 : ds1.getChildren()) {
                                        if (ds2.getKey().equals("mProviderLatitude")) {
                                            latitude = Double.parseDouble(ds2.getValue().toString());
                                        } else if (ds2.getKey().equals("mProviderLongitude")) {
                                            longitude = Double.parseDouble(ds2.getValue().toString());
                                        }
                                    }

                                    // Create an instance of LatLng
                                    LatLng latLng = new LatLng(latitude, longitude);
                                    mp.setLatLng(latLng);

                                } else if (ds1.getKey().equals("mProviderBrandName")) {
                                    mp.setBrandName(ds1.getValue().toString());
                                } else if (ds1.getKey().equals("mProviderMobileNo")) {
                                    mp.setMobileNo(ds1.getValue().toString());
                                } else if (ds1.getKey().equals("mProviderTelephoneNo")) {
                                    mp.setPhoneNo(ds1.getValue().toString());
                                }
                            }
                        } else if (ds.getKey().equals("PersonalInformation")) {
                            for (DataSnapshot ds1 : ds.getChildren()) {
                                if (ds1.getKey().equals("mProviderAddress")) {
                                    mp.setAddress(ds1.getValue().toString());
                                }
                            }
                        }

                    }

                    messProvidersInfo.add(mp);

                }

                Log.d(TAG, "onDataChange: mp = " + messProvidersInfo);

                for (MessProvider mp : messProvidersInfo) {
                    mMap.addMarker(new MarkerOptions().position(mp.getLatLng()).title(mp.getBrandName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                    Log.d(TAG, "onLocationChanged: lat and long = " + mp.getLatLng());
                    Log.d(TAG, "onLocationChanged: Brand Name = " + mp.getBrandName());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // ---- End ---- //


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker myMarker) {

                if (!myMarker.getTitle().equals("You")) {


                    // Get the position of the mess in messProviderInfo by going through it
                    int index = 0;

                    for (MessProvider mp : messProvidersInfo) {
                        if (myMarker.getTitle().equals(mp.getBrandName())) {
                            break;
                        }
                        index++;
                    }

                    // Create a Bundle of mess values
                    // i.e Brand Name, Address, MobileNo, Phone No.
                    // We need to pass the Category Name, And Food Type to the next fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("uID",  messProvidersInfo.get(index).getuID());
                    bundle.putString("brandName", messProvidersInfo.get(index).getBrandName());
                    bundle.putString("address", messProvidersInfo.get(index).getAddress());
                    bundle.putString("phoneNo", messProvidersInfo.get(index).getPhoneNo());
                    bundle.putString("mobileNo", messProvidersInfo.get(index).getMobileNo());

                    //set Fragmentclass Arguments
                    MessInfoFragment fragobj = new MessInfoFragment();
                    fragobj.setArguments(bundle);

                    // Replace it with DeleteMenu_DeleteItems_Fragment
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.customerHomeFrame, fragobj).addToBackStack(null);
                    fragmentTransaction.commit();


                }

                return false;
            }

        });

        /*mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int position = (int)(marker.getTag());
                startActivity(new Intent(MapsActivity.this, Popup.class));
                return false;
            }
        });*/
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        //mLocationRequest.setInterval(1000);
        //mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
            }
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSION_REQUEST_FINE_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_REQUEST_FINE_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

}
