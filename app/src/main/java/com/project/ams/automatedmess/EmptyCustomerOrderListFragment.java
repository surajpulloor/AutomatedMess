package com.project.ams.automatedmess;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmptyCustomerOrderListFragment extends Fragment {


    private FirebaseAuth mAuth;

    private Long orderNo;


    public EmptyCustomerOrderListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_empty_customer_order_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatabaseReference totalNoOfOrdersDbRef = FirebaseDatabase.getInstance().getReference().child("TotalNoOfOrders");

        totalNoOfOrdersDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderNo = dataSnapshot.getValue(Long.class);

                DatabaseReference orderDbRef = FirebaseDatabase.getInstance().getReference().child("OrderHistory_MessProviders").child(mAuth.getUid()).child("orderNo_" + orderNo);

                orderDbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.messProviderCustomerOrders_frame, new MessProviderOrderListFragment()).addToBackStack(null);
                            ft.commit();
                        }
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
