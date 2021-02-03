package com.project.ams.automatedmess;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
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
public class OrderList_ForReviewsFragment extends Fragment {


    // The listview in this fragments layout file
    private ListView orderHistoryListView;

    // Used to store all the orders currently in the database for this messprovider
    private List<OrderHistory> orderHistories = new ArrayList<>();

    // Some Firebase stuff
    private FirebaseAuth mAuth;

    private Activity context;


    public OrderList_ForReviewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_list__for_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();

        ((CustomerHome) getActivity()).getSupportActionBar().setTitle("My Review's and Rating's");

        orderHistoryListView = view.findViewById(R.id.customerOrderList_listView);

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("OrderHistory_Customers").child(mAuth.getUid());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                orderHistories.clear();


                for (DataSnapshot order : dataSnapshot.getChildren()) {
                    Order_Customers orderCustomers = order.getValue(Order_Customers.class);

                    OrderHistory history = new OrderHistory();
                    history.setOrderNo(Integer.parseInt(order.getKey().substring(8, 9)));
                    history.setTotalAmount(orderCustomers.getTotalAmount());
                    history.setOrderStatus(orderCustomers.getStatus());
                    orderHistories.add(history);
                }


                OrderHistoryAdapter orderHistoryAdapter = new OrderHistoryAdapter(context, (ArrayList<OrderHistory>) orderHistories);
                orderHistoryListView.setAdapter(orderHistoryAdapter);

                orderHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        // Create a Bundle of orderNo value
                        // Send it to the next fragment
                        Bundle bundle = new Bundle();
                        bundle.putInt("orderNo", orderHistories.get(i).getOrderNo());

                        //set Fragmentclass Arguments
                        CustomerReviewAndRatingFragment fragobj = new CustomerReviewAndRatingFragment();
                        fragobj.setArguments(bundle);

                        // Replace the default fragment
                        android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.customerHomeFrame, fragobj).addToBackStack(null);
                        ft.commit();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
