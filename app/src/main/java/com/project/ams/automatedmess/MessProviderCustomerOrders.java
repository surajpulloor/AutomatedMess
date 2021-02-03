package com.project.ams.automatedmess;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MessProviderCustomerOrders extends AppCompatActivity {

    private String fromNotification;
    private String orderNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_provider_customer_orders);

//        if (savedInstanceState != null) {
//            fromNotification = savedInstanceState.getString("fromNotification");
//            orderNo = savedInstanceState.getString("orderNo");
//        }

        Bundle extras = getIntent().getExtras();
        fromNotification = extras.getString("fromNotification");
        orderNo = extras.getString("orderNo");

        if (fromNotification.equals("true")) {
            // Create a Bundle of orderNo value
            // Send it to the next fragment
            Bundle bundle = new Bundle();
            bundle.putInt("orderNo", Integer.parseInt(orderNo));

            //set Fragmentclass Arguments
            MessProviderOrderDetailsFragment fragobj = new MessProviderOrderDetailsFragment();
            fragobj.setArguments(bundle);

            // Replace the default fragment
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.messProviderCustomerOrders_frame, fragobj).addToBackStack(null);
            ft.commit();
        } else {
            if (Integer.parseInt(orderNo) != 0) {
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.messProviderCustomerOrders_frame, new MessProviderOrderListFragment()).addToBackStack(null);
                ft.commit();
            } else {
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.messProviderCustomerOrders_frame, new EmptyCustomerOrderListFragment());
                ft.commit();
            }

        }
    }

//    @Override
//    public void onNewIntent(Intent newIntent) {
//        this.setIntent(newIntent);
//
//        fromNotification = newIntent.getStringExtra("fromNotification");
//        orderNo = newIntent.getStringExtra("orderNo");
//    }
}
