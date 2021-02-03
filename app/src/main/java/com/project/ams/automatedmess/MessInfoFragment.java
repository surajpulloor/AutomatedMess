package com.project.ams.automatedmess;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessInfoFragment extends Fragment {

    // Used to store textview refs of the layout file
    private TextView brandNameView;
    private TextView addressBarView;
    private TextView phoneNoBarView;
    private TextView mobileNoView;
    private Button placeOrderBtn;
    private TextView averageRatingView;
    private ImageView starImage;

    // Used to store values we got from the CustomerHomeFragment
    private String brandName;
    private String address;
    private String phoneNo;
    private String mobileNo;
    private String uID;

    private float averageRating = 0;

    private OrderItemsViewModel mOrderItemsViewModel;

    public MessInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Get the arguments passed to this fragment
        brandName = getArguments().getString("brandName");
        address = getArguments().getString("address");
        phoneNo = getArguments().getString("phoneNo");
        mobileNo = getArguments().getString("mobileNo");
        uID = getArguments().getString("uID");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mess_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the all the reviews for this Mess Provider
        DatabaseReference reviewDbRef = FirebaseDatabase.getInstance().getReference().child("Reviews_MessProviders").child(uID);

        reviewDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CustomerReview_MessProviders customerProfile = snapshot.getValue(CustomerReview_MessProviders.class);
                        averageRating += customerProfile.getRating();
                    }

                    averageRating /= dataSnapshot.getChildrenCount();
                } else {
                    averageRating = 0;
                }

                //
                if (averageRating == 0) {
                    averageRatingView.setText("");
                    starImage.setVisibility(View.INVISIBLE);
                } else {
                    starImage.setVisibility(View.VISIBLE);
                    averageRatingView.setText(String.valueOf(averageRating));
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mOrderItemsViewModel = ViewModelProviders.of(getActivity()).get(OrderItemsViewModel.class);


        mOrderItemsViewModel.getAllItems().observe(this, new Observer<List<OrderItem>>() {
            @Override
            public void onChanged(@Nullable final List<OrderItem> orderItems) {
                ((TextView) getActivity().findViewById(R.id.badge_notification)).setText(String.valueOf(orderItems.size()));
            }
        });

        ((CustomerHome) getActivity()).setAppBarTitle("Mess Information");

        // Make a few connections
        brandNameView = view.findViewById(R.id.messInfoBrandName);
        addressBarView = view.findViewById(R.id.messInfoAddressBar);
        phoneNoBarView = view.findViewById(R.id.messInfoPhoneNoBar);
        mobileNoView = view.findViewById(R.id.messInfoMobileNoBar);
        placeOrderBtn = view.findViewById(R.id.messInfoPlaceOrderBtn);
        averageRatingView = view.findViewById(R.id.average_rating);
        starImage = view.findViewById(R.id.ratingStarImg);

        // Put in some values in these views
        brandNameView.setText(brandName);
        addressBarView.setText(address);
        phoneNoBarView.setText(phoneNo);
        mobileNoView.setText(mobileNo);

        // Give the button a click listener
        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create a Bundle of mess values
                // i.e Brand Name, Address, MobileNo, Phone No.
                // We need to pass the Category Name, And Food Type to the next fragment
                Bundle bundle = new Bundle();
                bundle.putString("uID",  uID);
                bundle.putString("brandName", brandName);
                bundle.putString("address", address);
                bundle.putString("phoneNo", phoneNo);
                bundle.putString("mobileNo", mobileNo);


                //set Fragmentclass Arguments
                CustomerOrder_CategorySectionFragment fragobj = new CustomerOrder_CategorySectionFragment();
                fragobj.setArguments(bundle);

                // Replace it with DeleteMenu_DeleteItems_Fragment
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.customerHomeFrame, fragobj).addToBackStack(null);
                fragmentTransaction.commit();


                Toast.makeText(getActivity(), "You want to order something", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
