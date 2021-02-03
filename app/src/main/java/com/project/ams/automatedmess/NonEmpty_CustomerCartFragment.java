package com.project.ams.automatedmess;


import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class NonEmpty_CustomerCartFragment extends Fragment {

    // Declare a few firebase stuff
    private FirebaseAuth mAuth;

    private ListView itemInCart;

    // Declare a few refs for the views for this fragment
    private TextView messName;
    private TextView messAddress;
    private TextView messPhoneNo;
    private TextView messMobileNo;
    private TextView orderTotalAmount;
    private Button deleteOrder;
    private Button checkOut;

    // Used for storing a ViewModel ref of MessProvider used to perform database ops.
    private MessProviderViewModel messProviderViewModel;
    private OrderItemsViewModel orderItemsViewModel;

    // Used to store the adapter ref.
    private OrderItemsAdapter adapter;

    private View fragRef;

    private List<OrderItem> orderItems;

    // Used to pass the totalAmount to the payment activity
    private double totalAmount = 0.0;


    public NonEmpty_CustomerCartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_non_empty__customer_cart, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Start coding from here

        // Gather a firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        // Define the ref's
        messName = view.findViewById(R.id.customerCart_MessName);
        messAddress = view.findViewById(R.id.customerCart_MessAddress);
        messMobileNo = view.findViewById(R.id.customerCart_MessMobileNo);
        orderTotalAmount = view.findViewById(R.id.customerCart_totalAmount);
        deleteOrder = view.findViewById(R.id.customerCart_deleteOrder);
        checkOut = view.findViewById(R.id.customerCart_Checkout);

        messProviderViewModel = ViewModelProviders.of(getActivity()).get(MessProviderViewModel.class);
        orderItemsViewModel = ViewModelProviders.of(getActivity()).get(OrderItemsViewModel.class);

        // assign listview ref
        itemInCart = view.findViewById(R.id.itemsInCart);

        // Access SQLite and set messName, messAddress, messPhoneNo., messMobileNo.
        messProviderViewModel.getAllItems().observe(this, new Observer<List<MessProviderProfile>>() {
            @Override
            public void onChanged(@Nullable final List<MessProviderProfile> items) {
                if (items.size() != 0) {
                    messName.setText(items.get(0).getBrandName());
                    messAddress.setText(items.get(0).getAddress());
                    messMobileNo.setText("Phone No.: +020 " + items.get(0).getPhoneNo() + ", Mobile No.: +91 " + items.get(0).getMobileNo());
                }

            }
        });

        orderItemsViewModel.getAllItems().observe(this, new Observer<List<OrderItem>>() {
            @Override
            public void onChanged(@Nullable final List<OrderItem> items) {

                if (items.size() != 0) {
                    // Assign orderItems
                    orderItems = items;

                    // Set the custom adapter
                    adapter = new OrderItemsAdapter(getActivity(), (ArrayList<OrderItem>) items);
                    // Set the adapter
                    itemInCart.setAdapter(adapter);

                    for (OrderItem item : items) {
                        totalAmount += item.getItemPrice() * item.getItemQuantity();
                    }

                    // Round the total amount to 2 decimal places
                    BigDecimal bd = new BigDecimal(totalAmount);
                    bd = bd.setScale(2, RoundingMode.HALF_UP);
                    totalAmount = bd.doubleValue();

                    // Set the total amount to the totalAmount TextView
                    orderTotalAmount.setText("₹ " + totalAmount);
                }


            }
        });


        itemInCart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                // Create a Bundle of mess values
                // i.e Brand Name, Address, MobileNo, Phone No.
                // We need to pass the Category Name, And Food Type to the next fragment
                Bundle bundle = new Bundle();
                bundle.putString("itemId", String.valueOf(orderItems.get(position).getId()));
                bundle.putString("itemName", orderItems.get(position).getItemName());
                bundle.putString("itemPrice", String.valueOf(orderItems.get(position).getItemPrice()));
                bundle.putString("itemQuantity", String.valueOf(orderItems.get(position).getItemQuantity()));
                bundle.putString("itemCategory", orderItems.get(position).getItemCategory());
                bundle.putString("itemType", orderItems.get(position).getItemType());

                //set Fragmentclass Arguments
                CartItemsDetailsFragment fragobj = new CartItemsDetailsFragment();
                fragobj.setArguments(bundle);

                // Replace the default fragment
                android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.customerCartFrame, fragobj).addToBackStack(null);;
                ft.commit();
            }
        });


        deleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Confirm the delete with this alert box
                new AlertDialog.Builder(getActivity())
                        .setTitle("Are you sure ?")
                        .setMessage("Do you really want to delete this order, \nyou'll have to order again, Are you sure?"

                        )
                        .setIcon(R.drawable.exclamation_mark)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                messProviderViewModel.deleteAll();
                                orderItemsViewModel.deleteAll();
                                Intent intent = new Intent(getActivity(), CustomerHome.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }})

                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth.getCurrentUser() != null) {

                    Intent intent = new Intent(getActivity(), CustomerPayment.class);
                    intent.putExtra("totalAmount", String.valueOf(totalAmount));
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(getActivity(), CustomerSignInActivity.class);
                    intent.putExtra("activityName", "CustomerCart");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            }
        });


    }
}
