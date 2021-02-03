package com.project.ams.automatedmess;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReviewAndRating_MessProviderFragment extends Fragment {

    private int orderNo;

    private CustomerReview_MessProviders customerReview_messProviders = new CustomerReview_MessProviders();

    // UI controls
    private TextView customerNameView;
    private TextView customerAddressView;
    private TextView customerContactView;

    private RatingBar ratingBarView;
    private EditText reviewView;
    private Button okView;

    // Firebase stuff
    private FirebaseAuth mAuth;

    private String orderGivenBy;


    public ReviewAndRating_MessProviderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        orderNo = getArguments().getInt("orderNo");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review_and_rating__mess, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ((MessProviderHome) getActivity()).getSupportActionBar().setTitle("Review's and Rating's for Order No. " + orderNo);

        // make the ui connections
        customerNameView = view.findViewById(R.id.customerReviewAndRating_messName);
        customerAddressView = view.findViewById(R.id.customerReviewAndRating_messAddress);
        customerContactView = view.findViewById(R.id.customerReviewAndRating_messContactInfo);

        ratingBarView = view.findViewById(R.id.customerReviewAndRating_rating);
        reviewView = view.findViewById(R.id.customerReviewAndRating_review);
        okView = view.findViewById(R.id.customerReviewAndRating_submitBtn);

        // disable the Review EditText box
        reviewView.setEnabled(false);

        // Gather an existing review for this particular order
        DatabaseReference reviewDbRef = FirebaseDatabase.getInstance().getReference().child("Reviews_MessProviders").child(mAuth.getUid()).child("orderNo_" + orderNo);

        reviewDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    customerReview_messProviders = dataSnapshot.getValue(CustomerReview_MessProviders.class);

                    // Set the review and rating
                    ratingBarView.setRating(customerReview_messProviders.getRating());
                    reviewView.setText(customerReview_messProviders.getReview());

                    orderGivenBy = customerReview_messProviders.getReviewGivenBy();

                    // We also need Customers's info
                    DatabaseReference messProviderDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(orderGivenBy).child("PersonalInformation");
                    messProviderDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CustomerProfile profile = dataSnapshot.getValue(CustomerProfile.class);
                            // Set mess name, mess address and contact info
                            customerNameView.setText(profile.name);
                            customerAddressView.setText(profile.address);
                            customerContactView.setText("Phone No.: +020 " +
                                    profile.phoneNo +
                                    ", Mobile No.: +91 " + profile.mobileNo);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else { // We don't have review yet for this order
                    // Get the messUID for this order
                    DatabaseReference orderHistoryDbRef = FirebaseDatabase.getInstance().getReference().child("OrderHistory_MessProviders").child(mAuth.getUid()).child("orderNo_" + orderNo);
                    orderHistoryDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            orderGivenBy = dataSnapshot.child("orderGivenBy").getValue(String.class);

                            // Once we have the messUID we can access mess info from the Users node
                            DatabaseReference messProviderDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(orderGivenBy).child("PersonalInformation");
                            messProviderDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    CustomerProfile profile = dataSnapshot.getValue(CustomerProfile.class);
                                    // Set mess name, mess address and contact info
                                    customerNameView.setText(profile.name);
                                    customerAddressView.setText(profile.address);
                                    customerContactView.setText("Phone No.: +020 " +
                                            profile.phoneNo +
                                            ", Mobile No.: +91 " + profile.mobileNo);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        okView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });


    }
}
