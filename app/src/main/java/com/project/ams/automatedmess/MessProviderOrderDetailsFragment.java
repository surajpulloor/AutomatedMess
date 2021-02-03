package com.project.ams.automatedmess;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessProviderOrderDetailsFragment extends Fragment {

    private TextView customerNameView;
    private TextView customerAddressView;
    private TextView customerPhoneNoView;
    private ListView itemListView;
    private TextView totalAmountCustomerView;
    private TextView convChargesView;
    private TextView totalAmountMessProviderView;
    private Button acceptView;
    private Button declineView;
    private Button okView;
    private ImageView orderStatusImage;

    private int orderNo;

    private FirebaseAuth mAuth;

    private String customerUID;
    private CustomerProfile customerProfile;
    private List<OrderItem> orderedItems = new ArrayList<>();
    private OrderItemsAdapter adapter;

    private Double totalAmountCustomer;
    private Double convCharges;
    private Double totalAmountMessProvider;

    private Double messBankBalance;
    private Double customerBankBalance;

    private String status;

    private String UID;


    public MessProviderOrderDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get the orderNo. from the previous fragment or activity
        orderNo = getArguments().getInt("orderNo");

        mAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Make the necessary connections
        customerNameView = view.findViewById(R.id.orderDetails_customerName);
        customerAddressView = view.findViewById(R.id.orderDetails_customerAddress);
        customerPhoneNoView = view.findViewById(R.id.orderDetails_customerContact);
        itemListView = view.findViewById(R.id.orderDetails_listView);
        totalAmountCustomerView = view.findViewById(R.id.orderDetails_totalAmountCustomer);
        convChargesView = view.findViewById(R.id.orderDetails_convCharges);
        totalAmountMessProviderView = view.findViewById(R.id.orderDetails_totalAmountMessProvider);
        acceptView = view.findViewById(R.id.orderDetails_acceptBtn);
        declineView = view.findViewById(R.id.orderDetails_declineBtn);
        okView = view.findViewById(R.id.orderDetails_okBtn);
        orderStatusImage = view.findViewById(R.id.orderDetails_orderStatus);

        ((MessProviderCustomerOrders) getActivity()).getSupportActionBar().setTitle("Order No. " + orderNo);

        UID = mAuth.getUid();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("OrderHistory_MessProviders").child(UID).child("orderNo_" + String.valueOf(orderNo));

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Order_MessProvider order_messProvider = dataSnapshot.getValue(Order_MessProvider.class);

                Map<String, Map<String, Item>> items = order_messProvider.getItems();

                // We need to extract these items to our OrderItem Arraylist
                for (Map<String, Item> itemS : items.values()) {
                    for (Item item : itemS.values()) {
                        OrderItem orderItem = new OrderItem();
                        orderItem.setItemName(item.getItemName());
                        orderItem.setItemPrice(item.getItemPrice());
                        orderItem.setItemType(item.getItemType());
                        orderItem.setItemQuantity(item.getItemQuantity().intValue());
                        orderItem.setItemCategory(item.getItemCategory());
                        orderedItems.add(orderItem);
                    }
                }

                totalAmountCustomer = order_messProvider.getTotalAmountCustomer();
                convCharges = order_messProvider.getConvCharges();
                totalAmountMessProvider = order_messProvider.getTotalAmountMessProvider();


                totalAmountCustomerView.setText("₹ " + totalAmountCustomer);
                convChargesView.setText("- ₹ " + convCharges);
                totalAmountMessProviderView.setText("₹ " + totalAmountMessProvider);

                status = order_messProvider.getStatus();


                if (!status.equals("pending")) {
                    okView.setVisibility(View.VISIBLE);
                    acceptView.setVisibility(View.GONE);
                    declineView.setVisibility(View.GONE);

                    // check whether or not the order was accepted or rejected
                    if (status.equals("accepted")) {
                        orderStatusImage.setImageResource(R.drawable.order_accepted);
                    } else if (status.equals("rejected")) {
                        orderStatusImage.setImageResource(R.drawable.order_rejected);
                    }

                } else {
                    okView.setVisibility(View.GONE);
                    acceptView.setVisibility(View.VISIBLE);
                    declineView.setVisibility(View.VISIBLE);
                    orderStatusImage.setImageResource(R.drawable.confirmation_pending);
                }



                // Store the UID of the customer
                customerUID = dataSnapshot.child("orderGivenBy").getValue(String.class);

                // Set the custom adapter
                adapter = new OrderItemsAdapter(getActivity(), (ArrayList<OrderItem>) orderedItems);
                // Set the adapter
                itemListView.setAdapter(adapter);

                // Get previous bank balance amount for this mess provider
                DatabaseReference messBalanceDbRef = FirebaseDatabase.getInstance().getReference().child("MessProviders_balance");

                messBalanceDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(UID).child("balance").exists()) {
                            messBankBalance = dataSnapshot.child(UID).child("balance").getValue(Double.class);
                        } else {
                            messBankBalance = 0.0;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // Get previous bank balance amount for this customer
                DatabaseReference customerBalanceDbRef = FirebaseDatabase.getInstance().getReference().child("Customers_balance");

                customerBalanceDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(customerUID).child("balance").exists()) {
                            customerBankBalance = dataSnapshot.child(customerUID).child("balance").getValue(Double.class);
                        } else {
                            customerBankBalance = 0.0;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerUID).child("PersonalInformation");
                dbRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        CustomerProfile customerProfile = dataSnapshot.getValue(CustomerProfile.class);
                        customerNameView.setText(customerProfile.name);
                        customerAddressView.setText(customerProfile.address);
                        customerPhoneNoView.setText("Phone No.: +020 " + customerProfile.phoneNo + ", Mobile No.: +91 " + customerProfile.mobileNo);
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


        // set onclick listeners
        okView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        acceptView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Confirm the acceptance of the order with this alert box
                new AlertDialog.Builder(getActivity())
                        .setTitle("Are you sure ?")
                        .setMessage("Do you really want to accept this order, \nyou'll will recieve ₹ " + totalAmountMessProvider + " after accepting the order ?"

                        )
                        .setIcon(R.drawable.exclamation_mark)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                // Also update the status to "accepted" for both OrderHistory_MessProviders and OrderHistory_Customers
                                DatabaseReference orderMessProvidersDbRef = FirebaseDatabase.getInstance().getReference().child("OrderHistory_MessProviders");
                                DatabaseReference orderCustomersDbRef = FirebaseDatabase.getInstance().getReference().child("OrderHistory_Customers");

                                orderMessProvidersDbRef.child(UID).child("orderNo_" + orderNo).child("status").setValue("accepted");
                                orderCustomersDbRef.child(customerUID).child("orderNo_" + orderNo).child("status").setValue("accepted");

                                DatabaseReference balanceDbRef = FirebaseDatabase.getInstance().getReference().child("MessProviders_balance");
                                balanceDbRef.child(UID).child("balance").setValue(messBankBalance + totalAmountMessProvider);
                                getFragmentManager().popBackStack();
                            }})

                        .setNegativeButton(android.R.string.no, null).show();

            }
        });

        declineView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Confirm the decline of the order with this alert box
                new AlertDialog.Builder(getActivity())
                        .setTitle("Are you sure ?")
                        .setMessage("Do you really want to reject this order, \nyou'll have to refund the ₹ " + totalAmountMessProvider + " to the customer, Are you sure?"

                        )
                        .setIcon(R.drawable.exclamation_mark)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                // Also update the status to "rejected" for both OrderHistory_MessProviders and OrderHistory_Customers
                                DatabaseReference orderMessProvidersDbRef = FirebaseDatabase.getInstance().getReference().child("OrderHistory_MessProviders");
                                DatabaseReference orderCustomersDbRef = FirebaseDatabase.getInstance().getReference().child("OrderHistory_Customers");

                                orderMessProvidersDbRef.child(UID).child("orderNo_" + orderNo).child("status").setValue("rejected");
                                orderCustomersDbRef.child(customerUID).child("orderNo_" + orderNo).child("status").setValue("rejected");


                                DatabaseReference balanceDbRef = FirebaseDatabase.getInstance().getReference().child("Customers_balance");
                                balanceDbRef.child(customerUID).child("balance").setValue(customerBankBalance + totalAmountMessProvider);
                                getFragmentManager().popBackStack();
                            }})

                        .setNegativeButton(android.R.string.no, null).show();
            }
        });


    }
}
