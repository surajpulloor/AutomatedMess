package com.project.ams.automatedmess;

import android.app.FragmentTransaction;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Random;

public class MessProviderHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MessProviderHome";

    // Some Firebase stuff
    FirebaseAuth mAuth;


    // Declare a few fields
    private TextView emailValueView;

    // Used by notification builder
    private String CHANNEL_ID = "messNot";

    final int notificationId = new Random().nextInt(61) + 20;

    private boolean notificationOnStart = false;

    private int orderNo;

    private boolean ordersPresent;

    public class Item {
        public String itemName;
        public Double itemPrice;
        public Long itemQuantity;
        public String itemType;
        public String itemCategory;

        public Item() {

        }

        public Item(String itemName, Double itemPrice, Long itemQuantity, String itemType, String itemCategory) {
            this.itemName = itemName;
            this.itemPrice = itemPrice;
            this.itemQuantity = itemQuantity;
            this.itemType = itemType;
            this.itemCategory = itemCategory;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_provider_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Start coding from here...

        // Init Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set the connections
        emailValueView = navigationView.getHeaderView(0).findViewById(R.id.messOwnerEmail);

        String emailValue = mAuth.getCurrentUser().getEmail().toString();

        // Get the email of the currently signed in user and set it to the email textview
        emailValueView.setText(emailValue);

        // Create an onclick Listener for the profile image in the navigation drawer
        ImageView profileImg = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.messOwnerProfileImg);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onProfileImg: Profile Image Clicked");
            }
        });

        DatabaseReference newOrdersDbRef = FirebaseDatabase.getInstance().getReference().child("TotalNoOfOrders");
        newOrdersDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderNo = dataSnapshot.getValue(Integer.class);
                if (!notificationOnStart) {
                    notificationOnStart = true;
                } else {
                    DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("OrderHistory_MessProviders").child(mAuth.getUid()).child("orderNo_" + orderNo);
                    orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                createNotification("New Order_MessProvider Placed"
                                        ,"Order_MessProvider No. " + orderNo + " was placed right now, tap to view details", orderNo);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("OrderHistory_MessProviders").child(mAuth.getUid());
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0) {
                    ordersPresent = true;
                } else {
                    ordersPresent = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // This is the default fragment that should be included as soon as this activity launches
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.messProviderFLayout, new MessProfileEditorFragment());
        ft.commit();

        // Highlight the first option(Edit Profile) in the Navigation Drawer
        navigationView.setCheckedItem(R.id.nav_edit_profile);

    }

    private void createNotification(String title, String content, Integer orderNo) {

        createNotificationChannel();

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MessProviderCustomerOrders.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("fromNotification", "true");
        intent.putExtra("orderNo", String.valueOf(orderNo));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_food)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, mBuilder.build());

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mess_provider_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_edit_profile) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.messProviderFLayout, new MessProfileEditorFragment()).addToBackStack(null);
            ft.commit();
        } else if (id == R.id.nav_add_menu) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.messProviderFLayout, new MenuEditorFragment()).addToBackStack(null);
            ft.commit();
        } else if (id == R.id.nav_customers_orders) {
            Intent intent = new Intent(MessProviderHome.this, MessProviderCustomerOrders.class);
            intent.putExtra("fromNotification", "false");
            intent.putExtra("orderNo", String.valueOf(orderNo));
            startActivity(intent);
        } else if (id == R.id.nav_wallet) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.messProviderFLayout, new MessProviderWalletFragment()).addToBackStack(null);
            ft.commit();
        } else if (id == R.id.nav_user_reviews) {
            if (ordersPresent) {
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.messProviderFLayout, new CustomerReviewOrderList_MessProviderFragment()).addToBackStack(null);
                ft.commit();
            } else {
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.messProviderFLayout, new EmptyCustomerReviewFragment()).addToBackStack(null);
                ft.commit();
            }
        } else if (id == R.id.nav_sign_out) {
            mAuth.signOut();
            Toast.makeText(MessProviderHome.this, "You've Signed Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MessProviderHome.this, MessReg_SignInActivity.class);
            startActivity(intent);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setAppBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
