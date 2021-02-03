package com.project.ams.automatedmess;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderPlacedFragment extends Fragment {

    private TextView messNameView;
    private TextView messAddressView;
    private TextView messPhoneNoView;

    private TextView customerNameView;
    private TextView customerAddressView;
    private TextView customerPhoneNoView;

    private Button okView;

    // Used to delete the items in the cart once the order is successfully placed
    // i.e delete it from SQLite
    private OrderItemsViewModel orderItemsViewModel;

    // Used to get the Mess Provider Uid for this particular order
    private MessProviderViewModel messProviderViewModel;

    private FirebaseAuth mAuth;

    private String customerUID;


    public OrderPlacedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_placed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Start coding from here ...

        mAuth = FirebaseAuth.getInstance();

        // Setup a few connections
        messNameView = view.findViewById(R.id.orderPlaced_messName);
        messAddressView = view.findViewById(R.id.orderPlaced_messAddress);
        messPhoneNoView = view.findViewById(R.id.orderPlaced_messMobileNo);

        customerNameView = view.findViewById(R.id.orderPlaced_customerName);
        customerAddressView = view.findViewById(R.id.orderPlaced_customerAddress);
        customerPhoneNoView = view.findViewById(R.id.orderPlaced_customerMobileNo);

        okView = view.findViewById(R.id.orderPlaced_okBtn);

        orderItemsViewModel = ViewModelProviders.of(this).get(OrderItemsViewModel.class);
        messProviderViewModel = ViewModelProviders.of(this).get(MessProviderViewModel.class);

        // Access SQLite and set messName, messAddress, messPhoneNo., messMobileNo.
        messProviderViewModel.getAllItems().observe(this, new Observer<List<MessProviderProfile>>() {
            @Override
            public void onChanged(@Nullable final List<MessProviderProfile> items) {
                if (items.size() != 0) {
                    messNameView.setText(items.get(0).getBrandName());
                    messAddressView.setText(items.get(0).getAddress());
                    messPhoneNoView.setText("Phone No.: +020 " + items.get(0).getPhoneNo() + ", Mobile No.: +91 " + items.get(0).getMobileNo());
                }
            }
        });

        // Get Customer Info from Firebase Database
        customerUID = mAuth.getUid();

        final DatabaseReference currentDBRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerUID).child("PersonalInformation");
        currentDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    CustomerProfile customerProfile = snapshot.getValue(CustomerProfile.class);

                    // Put these values in the customer fields
                    customerNameView.setText(customerProfile.name);
                    customerAddressView.setText(customerProfile.address);
                    customerPhoneNoView.setText("Phone No.: +020 " + customerProfile.phoneNo + ", Mobile No.: +91 " + customerProfile.mobileNo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        okView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Delete the items from the internal database
                orderItemsViewModel.deleteAll();
                messProviderViewModel.deleteAll();

                Intent intent = new Intent(getActivity(), CustomerHome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }
}
