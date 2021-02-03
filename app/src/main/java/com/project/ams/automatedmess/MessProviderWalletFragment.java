package com.project.ams.automatedmess;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class MessProviderWalletFragment extends Fragment {

    private FirebaseAuth mAuth;
    private TextView balanceAmountView;
    private Button redeemView;

    public MessProviderWalletFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mess_provider_wallet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        balanceAmountView = view.findViewById(R.id.refundWallet_balanceAmount);
        redeemView = view.findViewById(R.id.wallet_redeemBtn);

        // Get previous bank balance amount for this customer
        DatabaseReference messProviderBalanceDbRef = FirebaseDatabase.getInstance().getReference().child("MessProviders_balance");

        messProviderBalanceDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double customerBankBalance;

                if (dataSnapshot.child(mAuth.getUid()).child("balance").exists()) {
                    customerBankBalance = dataSnapshot.child(mAuth.getUid()).child("balance").getValue(Double.class);
                } else {
                    customerBankBalance = 0.0;
                }

                balanceAmountView.setText(String.valueOf(customerBankBalance));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        redeemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "This amount will be redemmed in your account", Toast.LENGTH_LONG).show();
            }
        });
    }

}
