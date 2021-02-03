package com.project.ams.automatedmess;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CustomerOrderHistory extends AppCompatActivity {

    private boolean ordersPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_history);

//        if (savedInstanceState != null) {
//            fromNotification = savedInstanceState.getString("fromNotification");
//            orderNo = savedInstanceState.getString("orderNo");
//        }

        Bundle extras = getIntent().getExtras();
        ordersPresent = extras.getBoolean("ordersPresent");


        if (ordersPresent) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.customerOrderHistory_frame, new CustomerOrderListFragment()).addToBackStack(null);
            ft.commit();
        } else {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.customerOrderHistory_frame, new EmptyCustomerHistoryFragment());
            ft.commit();
        }
    }
}
