package com.project.ams.automatedmess;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessProfileEditorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessProfileEditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessProfileEditorFragment extends Fragment implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Declare a few fields
    private TextView editProfileMessName;
    private TextView editProfileMobileNo;
    private TextView editProfileTelephoneNo;
    private Button editProfileSaveBtn;

    // Google Map Stuff
    private GoogleMap mMap;

    // Declare a few firebase stuff
    private FirebaseAuth mAuth;

    // Used for storing the latitude and longitude that we get from the user's current location
    private double messLatitude = 0.0;
    private double messLongitude = 0.0;

    // Store a reference to the userID
    private String userID;

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;


//    private OnFragmentInteractionListener mListener;

    public MessProfileEditorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessProfileEditorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessProfileEditorFragment newInstance(String param1, String param2) {
        MessProfileEditorFragment fragment = new MessProfileEditorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Start Coding from here...

        ((MessProviderHome) getActivity()).setAppBarTitle("Profile Editor");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mess_profile_editor, container, false);

        // Init Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set the connections
        editProfileMessName = view.findViewById(R.id.editProfile_messName);
        editProfileMobileNo = view.findViewById(R.id.editProfile_mobileNo);
        editProfileTelephoneNo = view.findViewById(R.id.editProfile_telephoneNo);
        editProfileSaveBtn = view.findViewById(R.id.editProfile_saveBtn);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        // Get the userID for the currently signed in mess provider
        userID = mAuth.getCurrentUser().getUid();
        final DatabaseReference currentDBRef = FirebaseDatabase.getInstance().getReference().child("Users").child("MessProviders").child(userID).child("ProfileInformation");
        currentDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String messName = snapshot.child("mProviderBrandName").getValue().toString();
                    messLatitude = Double.parseDouble(snapshot.child("mProviderLocation").child("mProviderLatitude").getValue().toString());
                    messLongitude = Double.parseDouble(snapshot.child("mProviderLocation").child("mProviderLongitude").getValue().toString());
                    String messMobileNo = snapshot.child("mProviderMobileNo").getValue().toString();
                    String messTelephoneNo = snapshot.child("mProviderTelephoneNo").getValue().toString();

                    // Put these values in the activity fields
                    editProfileMessName.setText(messName);

                    editProfileMobileNo.setText(messMobileNo);
                    editProfileTelephoneNo.setText(messTelephoneNo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // Set an click event listener on the Save button
        editProfileSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messName = editProfileMessName.getText().toString();
                String messMobileNo = editProfileMobileNo.getText().toString();
                String messTelephoneNo = editProfileTelephoneNo.getText().toString();
                if (messName.isEmpty()) {
                    editProfileMessName.requestFocus();
                    Toast.makeText(getActivity(), "Please enter a name/brand name for your mess", Toast.LENGTH_LONG).show();
                    return;
                }

                if (messLatitude == 0.0 && messLongitude == 0.0) {
                    Toast.makeText(getActivity(), "Please select your current location\nusing the My Location Button\nfrom the map above", Toast.LENGTH_LONG).show();
                    return;
                }

                if (messMobileNo.isEmpty()) {
                    editProfileMobileNo.requestFocus();
                    Toast.makeText(getActivity(), "Please enter a mobile no. of your mess", Toast.LENGTH_LONG).show();
                    return;
                }

                if (messTelephoneNo.isEmpty()) {
                    editProfileTelephoneNo.requestFocus();
                    Toast.makeText(getActivity(), "Please enter a telephone no. of your mess", Toast.LENGTH_LONG).show();
                    return;
                }

                // Store these values in the database
                DatabaseReference currentDBRef = FirebaseDatabase.getInstance().getReference().child("Users").child("MessProviders").child(userID).child("ProfileInformation");
                currentDBRef.child("mProviderBrandName").setValue(messName);
                currentDBRef.child("mProviderLocation").child("mProviderLatitude").setValue(messLatitude);
                currentDBRef.child("mProviderLocation").child("mProviderLongitude").setValue(messLongitude);
                currentDBRef.child("mProviderMobileNo").setValue(messMobileNo);
                currentDBRef.child("mProviderTelephoneNo").setValue(messTelephoneNo);

                Toast.makeText(getActivity(), "Values saved in the database", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        messLatitude = location.getLatitude();
        messLongitude = location.getLongitude();
        Toast.makeText(getActivity(), "Latitude (" + messLatitude + ") & Longitude (" + messLongitude + ") Values copied", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.

            PermissionUtils.requestPermission((MessProviderHome)getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {

            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);

            // Refresh the maps fragment because permission
            // has been granted to access the users current location
//            Fragment mapFragment = getChildFragmentManager().findFragmentByTag("messProfileMapFragment");
//            final FragmentTransaction ft = getChildFragmentManager().beginTransaction();
//            ft.detach(mapFragment);
//            ft.attach(mapFragment);
//            ft.commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getFragmentManager(), "dialog");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
