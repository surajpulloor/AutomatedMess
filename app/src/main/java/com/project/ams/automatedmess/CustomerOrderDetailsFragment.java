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
public class CustomerOrderDetailsFragment extends Fragment {


    private TextView messNameView;
    private TextView messAddressView;
    private TextView messPhoneNoView;
    private ListView itemListView;
    private TextView totalAmountCustomerView;
    private TextView convChargesView;
    private TextView totalAmountMessProviderView;
    private Button cancelView;
    private ImageView orderStatusImage;

    private int orderNo;

    private FirebaseAuth mAuth;

    private String messProviderUID;
    private CustomerProfile customerProfile;
    private List<OrderItem> orderedItems = new ArrayList<>();
    private OrderItemsAdapter adapter;

    private Double totalAmountCustomer;

    private Double customerBankBalance;

    private Double messProviderBankBalance;

    private String status;

    private String UID;

    public CustomerOrderDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get the orderNo. from the previous fragment or activity
        orderNo = getArguments().getInt("orderNo");

        mAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_order_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Make the necessary connections
        messNameView = view.findViewById(R.id.orderDetails_messName);
        messAddressView = view.findViewById(R.id.orderDetails_messAddress);
        messPhoneNoView = view.findViewById(R.id.orderDetails_messContact);
        itemListView = view.findViewById(R.id.orderDetails_listView);
        totalAmountCustomerView = view.findViewById(R.id.orderDetails_totalAmountCustomer);
        cancelView = view.findViewById(R.id.orderDetails_cancelOrderBtn);
        orderStatusImage = view.findViewById(R.id.orderDetails_orderStatus);

        ((CustomerHome) getActivity()).getSupportActionBar().setTitle("Order No. " + orderNo);

        UID = mAuth.getUid();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("OrderHistory_Customers").child(UID).child("orderNo_" + String.valueOf(orderNo));

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Order_Customers orderCustomers = dataSnapshot.getValue(Order_Customers.class);

                Map<String, Map<String, Item>> items = orderCustomers.getItems();

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

                totalAmountCustomer = orderCustomers.getTotalAmount();

                totalAmountCustomerView.setText("₹ " + totalAmountCustomer);

                status = orderCustomers.getStatus();


                // check whether or not the order was accepted or rejected
                if (status.equals("accepted")) {
                    cancelView.setText("Cancel Order");
                    orderStatusImage.setImageResource(R.drawable.order_accepted);
                } else if (status.equals("rejected")) {
                    cancelView.setText("Ok");
                    orderStatusImage.setImageResource(R.drawable.order_rejected);
                } else {
                    cancelView.setText("Cancel Order");
                    orderStatusImage.setImageResource(R.drawable.confirmation_pending);
                }


                // Store the UID of the customer
                messProviderUID = dataSnapshot.child("orderGivenTo").getValue(String.class);

                // Set the custom adapter
                adapter = new OrderItemsAdapter(getActivity(), (ArrayList<OrderItem>) orderedItems);
                // Set the adapter
                itemListView.setAdapter(adapter);

                // Get previous bank balance amount for this customer
                DatabaseReference customerBalanceDbRef = FirebaseDatabase.getInstance().getReference().child("Customers_balance");

                customerBalanceDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(UID).child("balance").exists()) {
                            customerBankBalance = dataSnapshot.child(UID).child("balance").getValue(Double.class);
                        } else {
                            customerBankBalance = 0.0;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // Get previous bank balance amount for this customer
                DatabaseReference messProviderBalanceDbRef = FirebaseDatabase.getInstance().getReference().child("MessProviders_balance");

                messProviderBalanceDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(messProviderUID).child("balance").exists()) {
                            messProviderBankBalance = dataSnapshot.child(messProviderUID).child("balance").getValue(Double.class);
                        } else {
                            messProviderBankBalance = 0.0;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference().child("Users").child("MessProviders").child(messProviderUID);
                dbRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        messNameView.setText(dataSnapshot.child("ProfileInformation").child("mProviderBrandName").getValue(String.class));
                        messAddressView.setText(dataSnapshot.child("PersonalInformation").child("mProviderAddress").getValue(String.class));
                        messPhoneNoView.setText("Phone No.: +020 " +
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


        // set onclick listeners
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (status.equals("pending") || status.equals("accepted")) {
                    // Confirm the decline of the order with this alert box
                    String alertBoxString;
                    if (status.equals("pending")) {
                        alertBoxString = "Do you really want to cancel this order, \nyou'll receive ₹ " + totalAmountCustomer + " from the mess provider, Are you sure?";
                    } else {
                        alertBoxString = "The mess provider has accepted your order, do you want to cancel?\nIf yes then you'll receive only ₹ " + (totalAmountCustomer - totalAmountCustomer * 2 / 100) * 50 / 100 + " which is 50% of ₹ " + totalAmountCustomer + ". Including conv. charges";
                    }
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Are you sure ?")
                            .setMessage(alertBoxString)
                            .setIcon(R.drawable.exclamation_mark)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {

                                    // Also update the status to "rejected" for both OrderHistory_MessProviders and OrderHistory_Customers
                                    DatabaseReference orderMessProvidersDbRef = FirebaseDatabase.getInstance().getReference().child("OrderHistory_MessProviders");
                                    DatabaseReference orderCustomersDbRef = FirebaseDatabase.getInstance().getReference().child("OrderHistory_Customers");
                                    DatabaseReference customerBalanceDbRef = FirebaseDatabase.getInstance().getReference().child("Customers_balance");
                                    // Used in case if the mess has accepted the order and the customer is trying to cancel the order
                                    // In that case we need to deduct that amount from the messProviders local bank account
                                    DatabaseReference messProviderBalanceDbRef = FirebaseDatabase.getInstance().getReference().child("MessProviders_balance");

                                    orderMessProvidersDbRef.child(messProviderUID).child("orderNo_" + orderNo).child("status").setValue("rejected");
                                    orderCustomersDbRef.child(UID).child("orderNo_" + orderNo).child("status").setValue("rejected");

                                    if (status.equals("accepted")) {
                                        double messAmount = (totalAmountCustomer - totalAmountCustomer * 2 / 100) * 50 / 100;
                                        customerBalanceDbRef.child(UID).child("balance").setValue(customerBankBalance + messAmount);
                                        messProviderBalanceDbRef.child(messProviderUID).child("balance").setValue(messProviderBankBalance - messAmount);
                                    } else {
                                        customerBalanceDbRef.child(UID).child("balance").setValue(customerBankBalance + totalAmountCustomer);
                                    }

                                    getFragmentManager().popBackStack();
                                }})

                            .setNegativeButton(android.R.string.no, null).show();
                } else {
                    getFragmentManager().popBackStack();
                }
            }
        });


    }

}
