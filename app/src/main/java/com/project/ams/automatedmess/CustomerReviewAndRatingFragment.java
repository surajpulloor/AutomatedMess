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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerReviewAndRatingFragment extends Fragment {

    private int orderNo;

    private CustomerReview customerReview = new CustomerReview();

    // UI controls
    private TextView messNameView;
    private TextView messAddressView;
    private TextView messContactView;

    private RatingBar ratingBarView;
    private EditText reviewView;
    private Button submitView;

    // Firebase stuff
    private FirebaseAuth mAuth;

    private String orderGivenTo;


    public CustomerReviewAndRatingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        orderNo = getArguments().getInt("orderNo");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_review_and_rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ((CustomerHome) getActivity()).getSupportActionBar().setTitle("Review's and Rating's for Order No. " + orderNo);

        // make the ui connections
        messNameView = view.findViewById(R.id.customerReviewAndRating_messName);
        messAddressView = view.findViewById(R.id.customerReviewAndRating_messAddress);
        messContactView = view.findViewById(R.id.customerReviewAndRating_messContactInfo);

        ratingBarView = view.findViewById(R.id.customerReviewAndRating_rating);
        reviewView = view.findViewById(R.id.customerReviewAndRating_review);
        submitView = view.findViewById(R.id.customerReviewAndRating_submitBtn);

        // Gather an existing review for this particular order
        DatabaseReference reviewDbRef = FirebaseDatabase.getInstance().getReference().child("Reviews_Customers").child(mAuth.getUid()).child("orderNo_" + orderNo);

        reviewDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    customerReview = dataSnapshot.getValue(CustomerReview.class);

                    // Set the review and rating
                    ratingBarView.setRating(customerReview.getRating());
                    reviewView.setText(customerReview.getReview());

                    orderGivenTo = customerReview.getReviewGivenTo();

                    // We also need MessProvider's info
                    DatabaseReference messProviderDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child("MessProviders").child(orderGivenTo);
                    messProviderDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Set mess name, mess address and contact info
                            messNameView.setText(dataSnapshot.child("ProfileInformation").child("mProviderBrandName").getValue(String.class));
                            messAddressView.setText(dataSnapshot.child("PersonalInformation").child("mProviderAddress").getValue(String.class));
                            messContactView.setText("Phone No.: +020 " +
                                    dataSnapshot.child("ProfileInformation").child("mProviderTelephoneNo").getValue(String.class) +
                                    ", Mobile No.: +91 " + dataSnapshot.child("ProfileInformation").child("mProviderMobileNo").getValue(String.class));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else { // We don't have review yet for this order
                    // Get the messUID for this order
                    DatabaseReference orderHistoryDbRef = FirebaseDatabase.getInstance().getReference().child("OrderHistory_Customers").child(mAuth.getUid()).child("orderNo_" + orderNo);
                    orderHistoryDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            orderGivenTo = dataSnapshot.child("orderGivenTo").getValue(String.class);

                            // Once we have the messUID we can access mess info from the Users node
                            DatabaseReference messProviderDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child("MessProviders").child(orderGivenTo);
                            messProviderDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Set mess name, mess address and contact info
                                    messNameView.setText(dataSnapshot.child("ProfileInformation").child("mProviderBrandName").getValue(String.class));
                                    messAddressView.setText(dataSnapshot.child("PersonalInformation").child("mProviderAddress").getValue(String.class));
                                    messContactView.setText("Phone No.: +020 " +
                                            dataSnapshot.child("ProfileInformation").child("mProviderTelephoneNo").getValue(String.class) +
                                            ", Mobile No.: +91 " + dataSnapshot.child("ProfileInformation").child("mProviderMobileNo").getValue(String.class));
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

        submitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ratingBarView.getRating() == 0) {
                    Toast.makeText(getActivity(), "Please give a rating from 1 to 5", Toast.LENGTH_LONG).show();
                } else if (reviewView.getText().toString() == "") {
                    Toast.makeText(getActivity(), "Please give a review", Toast.LENGTH_LONG).show();
                } else {
                    // Add new values to the CustomerReview object
                    customerReview.setRating(ratingBarView.getRating());
                    customerReview.setReview(reviewView.getText().toString());
                    customerReview.setReviewGivenTo(orderGivenTo);

                    // Create a new CustomerReview_MessProvider object
                    CustomerReview_MessProviders customerReview_messProviders = new CustomerReview_MessProviders();
                    customerReview_messProviders.setRating(ratingBarView.getRating());
                    customerReview_messProviders.setReview(reviewView.getText().toString());
                    customerReview_messProviders.setReviewGivenBy(mAuth.getUid());

                    // Save the values to the Reviews_Customers node for orderNo
                    DatabaseReference customerReviewDbRef = FirebaseDatabase.getInstance().getReference().child("Reviews_Customers").child(mAuth.getUid()).child("orderNo_" + orderNo);
                    customerReviewDbRef.setValue(customerReview);

                    // Save the values to the Reviews_MessProviders node for orderNo
                    DatabaseReference messProvidersReviewDbRef = FirebaseDatabase.getInstance().getReference().child("Reviews_MessProviders").child(orderGivenTo).child("orderNo_" + orderNo);
                    messProvidersReviewDbRef.setValue(customerReview_messProviders);

                    Toast.makeText(getActivity(), "Review has been given for order no. " + orderNo, Toast.LENGTH_LONG).show();

                }
            }
        });


    }
}
