package com.project.ams.automatedmess;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

public class CustomerCart extends AppCompatActivity {

    private OrderItemsViewModel mOrderItemsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_cart);
        getSupportActionBar().setTitle("Customer Cart");

        mOrderItemsViewModel = ViewModelProviders.of(this).get(OrderItemsViewModel.class);


        mOrderItemsViewModel.getAllItems().observe(this, new Observer<List<OrderItem>>() {
            @Override
            public void onChanged(@Nullable final List<OrderItem> orderItems) {
                if (orderItems.size() != 0) {
                    android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.customerCartFrame, new NonEmpty_CustomerCartFragment());
                    ft.commit();
                } else {
                    android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.customerCartFrame, new Empty_CustomerCartFragment());
                    ft.commit();
                }
            }
        });


    }
}
