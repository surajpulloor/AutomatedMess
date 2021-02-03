package com.project.ams.automatedmess;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PaymentConfirmation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmation);

        if (getIntent().getStringExtra("transactionSuccessful").equals("true")) {
            // This is the default fragment that should be included as soon as this activity launches
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.paymentConfirmation_frame, new OrderPlacedFragment());
            ft.commit();
        } else {
            // This is the default fragment that should be included as soon as this activity launches
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.paymentConfirmation_frame, new OrderNotPlacedFragment());
            ft.commit();
        }

    }
}
