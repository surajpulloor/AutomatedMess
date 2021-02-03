package com.project.ams.automatedmess;

import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerPayment extends AppCompatActivity {

    final int REQUEST_CODE = 1;
    final String get_token = "http://automatedmess.com/BraintreePayments/main.php";
    final String send_payment_details = "http://automatedmess.com/BraintreePayments/mycheckout.php";
    String token, amount;
    HashMap<String, String> paramHash;

    // Ui refs
    private TextView totalAmountView;
    private Button payView;
    private Button myWalletView;

    // Used to store totalAmount got from the parent activity
    private String totalAmount;

    // Used to save the order in the database
    private FirebaseAuth mAuth;

    // Used to delete the items in the cart once the order is successfully placed
    // i.e delete it from SQLite
    private OrderItemsViewModel orderItemsViewModel;

    // Used to get the Mess Provider Uid for this particular order
    private MessProviderViewModel messProviderViewModel;

    // UID of Mess provider with whom the customer is placing an order
    private String messProviderUID;

    // A LifeCycle owner ref.
    private LifecycleOwner owner;

    // The current total no. of orders
    private int totalNoOfOrders;

    private double myWalletBalance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_payment);

        getSupportActionBar().setTitle("Payment Section");

        owner = this;

        // setup a few connections
        totalAmountView = findViewById(R.id.payment_totalAmount);
        payView = findViewById(R.id.payment_payBtn);
        myWalletView = findViewById(R.id.payment_myWallet);

        // get the total amount
        totalAmount = getIntent().getStringExtra("totalAmount");

        // set the amount to the textview
        totalAmountView.setText("₹ " + totalAmount);

        // Setup Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        orderItemsViewModel = ViewModelProviders.of(this).get(OrderItemsViewModel.class);
        messProviderViewModel = ViewModelProviders.of(this).get(MessProviderViewModel.class);

        // Access SQLite and set messName, messAddress, messPhoneNo., messMobileNo.
        messProviderViewModel.getAllItems().observe(this, new Observer<List<MessProviderProfile>>() {
            @Override
            public void onChanged(@Nullable final List<MessProviderProfile> items) {
                if (items.size() != 0) {
                    messProviderUID = items.get(0).getUid();
                }
            }
        });

        // Get previous bank balance amount for this customer
        DatabaseReference customerBalanceDbRef = FirebaseDatabase.getInstance().getReference().child("Customers_balance");

        customerBalanceDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(mAuth.getUid()).child("balance").exists()) {
                    myWalletBalance = dataSnapshot.child(mAuth.getUid()).child("balance").getValue(Double.class);
                } else {
                    myWalletBalance = 0.0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Get the totalNoOfOrders from Firebase
        DatabaseReference currentDBRef = FirebaseDatabase.getInstance().getReference().child("TotalNoOfOrders");
        currentDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    totalNoOfOrders = snapshot.getValue(Integer.class);
                    totalNoOfOrders++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        payView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBraintreeSubmit();
            }
        });

        myWalletView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String transactionSuccessful;
                if (myWalletBalance != 0) {
                    transactionSuccessful = "true";
                    orderItemsViewModel.getAllItems().observe(owner, new Observer<List<OrderItem>>() {
                        @Override
                        public void onChanged(@Nullable final List<OrderItem> items) {
                            // Order_MessProvider object which will be passed to the database
                            Order_MessProvider orderMessProvider = new Order_MessProvider();
                            // Order_Customer object which will be passed to the database
                            Order_Customers orderCustomers = new Order_Customers();

                            // A list of items
                            Map<String, Map<String, Item>> orderedItems = new HashMap<>();

                            // A list of mapped items
                            Map<String, Item> itemMap = new HashMap<>();

                            // Database refs for OrderHistory_MessProviders and OrderHistory_Customers
                            DatabaseReference messProviderOrderDbRef = FirebaseDatabase.getInstance().getReference().child("OrderHistory_MessProviders").child(messProviderUID).child("orderNo_" + totalNoOfOrders);
                            DatabaseReference customerOrderDbRef = FirebaseDatabase.getInstance().getReference().child("OrderHistory_Customers").child(mAuth.getUid()).child("orderNo_" + totalNoOfOrders);

                            int itemCount = 1;

                            double totalAmount = 0.0;

                            for (OrderItem item : items) {
                                totalAmount += item.getItemPrice() * item.getItemQuantity();

                                Item it = new Item();
                                it.setItemName(item.getItemName());
                                it.setItemPrice(item.getItemPrice());
                                it.setItemQuantity(Long.valueOf(item.getItemQuantity()));
                                it.setItemType(item.getItemType());
                                it.setItemCategory(item.getItemCategory());

                                itemMap.put("item" + itemCount, it);
                                itemCount++;
                            }

                            // Round the total amount upto 2 decimal places
                            BigDecimal bd = new BigDecimal(totalAmount);
                            bd = bd.setScale(2, RoundingMode.HALF_UP);
                            totalAmount = bd.doubleValue();

                            // Put all the mapped items into orderedItems map
                            orderedItems.put("items", itemMap);

                            // Add a few misc. info to OrderHistory_Customers
                            orderCustomers.setItems(orderedItems);
                            orderCustomers.setOrderGivenTo(messProviderUID);
                            orderCustomers.setTotalAmount(totalAmount);
                            orderCustomers.setTransactionId("wallet_" + mAuth.getUid());
                            orderCustomers.setStatus("pending");


                            // Add a few misc. info to OrderHistory_Customers
                            orderMessProvider.setItems(orderedItems);
                            orderMessProvider.setTotalAmountCustomer(totalAmount);
                            orderMessProvider.setConvCharges(totalAmount * 2 / 100);
                            orderMessProvider.setTotalAmountMessProvider(totalAmount - (totalAmount * 2 / 100));
                            orderMessProvider.setTransactionId("wallet_" + mAuth.getUid());
                            orderMessProvider.setOrderGivenBy(mAuth.getUid());
                            orderMessProvider.setStatus("pending");

                            // Push these two object to their respective database refs
                            messProviderOrderDbRef.setValue(orderMessProvider);
                            customerOrderDbRef.setValue(orderCustomers);


                            // Also update totalNoOfOrders in Firebase
                            FirebaseDatabase.getInstance().getReference().child("TotalNoOfOrders").setValue(totalNoOfOrders);

                            // Also update the balance amount in My Wallet
                            DatabaseReference customerBalanceDbRef = FirebaseDatabase.getInstance().getReference().child("Customers_balance");
                            customerBalanceDbRef.child(mAuth.getUid()).child("balance").setValue(myWalletBalance - totalAmount);

                            Intent intent = new Intent(CustomerPayment.this, PaymentConfirmation.class);
                            intent.putExtra("transactionSuccessful", transactionSuccessful);
                            startActivity(intent);
                        }
                    });

                } else {
                    Toast.makeText(CustomerPayment.this, "Sorry but you don't have sufficient balance in My Wallet for this transaction", Toast.LENGTH_SHORT).show();
                }

            }
        });

        new HttpRequest().execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                String stringNonce = nonce.getNonce();
                Log.d("mylog", "Result: " + stringNonce);
                // Send payment price with the nonce
                // use the result to update your UI and send the payment method nonce to your server
                paramHash = new HashMap<>();
                paramHash.put("amount", String.valueOf(totalAmount));
                paramHash.put("nonce", stringNonce);
                sendPaymentDetails();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
                Log.d("mylog", "user canceled");
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d("mylog", "Error : " + error.toString());
            }
        }
    }

    public void onBraintreeSubmit() {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(token);
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }

    private void sendPaymentDetails() {
        RequestQueue queue = Volley.newRequestQueue(CustomerPayment.this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, send_payment_details,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String transactionSuccessful;
                        if(response.contains("Successful"))
                        {
                            final String transactionId = response.substring(53, 61);
                            transactionSuccessful = "true";
                            orderItemsViewModel.getAllItems().observe(owner, new Observer<List<OrderItem>>() {
                                @Override
                                public void onChanged(@Nullable final List<OrderItem> items) {
                                    // Order_MessProvider object which will be passed to the database
                                    Order_MessProvider orderMessProvider = new Order_MessProvider();
                                    // Order_Customer object which will be passed to the database
                                    Order_Customers orderCustomers = new Order_Customers();

                                    // A list of items
                                    Map<String, Map<String, Item>> orderedItems = new HashMap<>();

                                    // A list of mapped items
                                    Map<String, Item> itemMap = new HashMap<>();

                                    // Database refs for OrderHistory_MessProviders and OrderHistory_Customers
                                    DatabaseReference messProviderOrderDbRef = FirebaseDatabase.getInstance().getReference().child("OrderHistory_MessProviders").child(messProviderUID).child("orderNo_" + totalNoOfOrders);
                                    DatabaseReference customerOrderDbRef = FirebaseDatabase.getInstance().getReference().child("OrderHistory_Customers").child(mAuth.getUid()).child("orderNo_" + totalNoOfOrders);

                                    int itemCount = 1;

                                    double totalAmount = 0.0;

                                    for (OrderItem item : items) {
                                        totalAmount += item.getItemPrice() * item.getItemQuantity();

                                        Item it = new Item();
                                        it.setItemName(item.getItemName());
                                        it.setItemPrice(item.getItemPrice());
                                        it.setItemQuantity(Long.valueOf(item.getItemQuantity()));
                                        it.setItemType(item.getItemType());
                                        it.setItemCategory(item.getItemCategory());

                                        itemMap.put("item" + itemCount, it);
                                        itemCount++;
                                    }

                                    // Round the total amount upto 2 decimal places
                                    BigDecimal bd = new BigDecimal(totalAmount);
                                    bd = bd.setScale(2, RoundingMode.HALF_UP);
                                    totalAmount = bd.doubleValue();

                                    // Put all the mapped items into orderedItems map
                                    orderedItems.put("items", itemMap);

                                    // Add a few misc. info to OrderHistory_Customers
                                    orderCustomers.setItems(orderedItems);
                                    orderCustomers.setOrderGivenTo(messProviderUID);
                                    orderCustomers.setTotalAmount(totalAmount);
                                    orderCustomers.setTransactionId(transactionId);
                                    orderCustomers.setStatus("pending");


                                    // Add a few misc. info to OrderHistory_Customers
                                    orderMessProvider.setItems(orderedItems);
                                    orderMessProvider.setTotalAmountCustomer(totalAmount);
                                    orderMessProvider.setConvCharges(totalAmount * 2 / 100);
                                    orderMessProvider.setTotalAmountMessProvider(totalAmount - (totalAmount * 2 / 100));
                                    orderMessProvider.setTransactionId(transactionId);
                                    orderMessProvider.setOrderGivenBy(mAuth.getUid());
                                    orderMessProvider.setStatus("pending");

                                    // Push these two object to their respective database refs
                                    messProviderOrderDbRef.setValue(orderMessProvider);
                                    customerOrderDbRef.setValue(orderCustomers);


                                    // Also update totalNoOfOrders in Firebase
                                    FirebaseDatabase.getInstance().getReference().child("TotalNoOfOrders").setValue(totalNoOfOrders);
                                }
                            });
                        }
                        else {
                            transactionSuccessful = "false";
                        }

                        Intent intent = new Intent(CustomerPayment.this, PaymentConfirmation.class);
                        intent.putExtra("transactionSuccessful", transactionSuccessful);
                        startActivity(intent);

                        Log.d("mylog", "Final Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("mylog", "Volley error : " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                if (paramHash == null)
                    return null;
                Map<String, String> params = new HashMap<>();
                for (String key : paramHash.keySet()) {
                    params.put(key, paramHash.get(key));
                    Log.d("mylog", "Key : " + key + " Value : " + paramHash.get(key));
                }

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private class HttpRequest extends AsyncTask {
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(CustomerPayment.this, android.R.style.Theme_DeviceDefault_Dialog);
            progress.setCancelable(false);
            progress.setMessage("We are contacting our servers for token, Please wait");
            progress.setTitle("Getting token");
            progress.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            HttpClient client = new HttpClient();
            client.get(get_token, new HttpResponseCallback() {
                @Override
                public void success(String responseBody) {
                    Log.d("mylog", responseBody);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CustomerPayment.this, "Successfully got token", Toast.LENGTH_SHORT).show();
//                                llHolder.setVisibility(View.VISIBLE);
                        }
                    });
                    token = responseBody;
                }

                @Override
                public void failure(Exception exception) {
                    final Exception ex = exception;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CustomerPayment.this, "Failed to get token: " + ex.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progress.dismiss();
        }
    }

}
