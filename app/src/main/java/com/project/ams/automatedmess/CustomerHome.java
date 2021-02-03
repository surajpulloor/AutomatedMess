package com.project.ams.automatedmess;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CustomerHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "CustomerHome";

    private TextView badgeNotification;

    // Some Firebase stuff
    FirebaseAuth mAuth;

    private boolean orderPresent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Init Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Find the Cart Fab Button
        FloatingActionButton cartBtn = findViewById(R.id.cartBtn);

        badgeNotification = findViewById(R.id.badge_notification);

        // Assign a click event to it
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerHome.this, CustomerCart.class);
                startActivity(intent);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (mAuth.getCurrentUser() == null) {
            navigationView.getMenu().findItem(R.id.nav_sign_out).setTitle("Sign In");
        }

        navigationView.setNavigationItemSelectedListener(this);

        if (mAuth.getCurrentUser() != null) {
            DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("OrderHistory_Customers").child(mAuth.getUid());
            orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() != 0) {
                        orderPresent = true;
                    } else {
                        orderPresent = false;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        // This is the default fragment that should be included as soon as this activity launches
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.customerHomeFrame, new CustomerHomeFragment());
        ft.commit();

        // Highlight the first option(Edit Profile) in the Navigation Drawer
        navigationView.setCheckedItem(R.id.nav_customer_home);
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
        getMenuInflater().inflate(R.menu.customer_home, menu);
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

        if (id == R.id.nav_customer_home) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.customerHomeFrame, new CustomerHomeFragment()).addToBackStack(null);
            ft.commit();
        } else if (id == R.id.nav_edit_profile) {
//            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.customerHomeFrame, new MessProfileEditorFragment()).addToBackStack(null);
//            ft.commit();
        } else if (id == R.id.nav_order_history) {
//            Intent intent = new Intent(CustomerHome.this, CustomerOrderHistory.class);
//            intent.putExtra("ordersPresent", orderPresent);
//            startActivity(intent);

            if (mAuth.getCurrentUser() != null) {
                if (orderPresent) {
                    android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.customerHomeFrame, new CustomerOrderListFragment()).addToBackStack(null);
                    ft.commit();
                } else {
                    android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.customerHomeFrame, new EmptyCustomerHistoryFragment()).addToBackStack(null);
                    ft.commit();
                }
            } else {
                Toast.makeText(CustomerHome.this, "Please Sign First", Toast.LENGTH_LONG).show();
            }

        } else if (id == R.id.nav_user_reviews) {
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(CustomerHome.this, "Please Sign First", Toast.LENGTH_LONG).show();
            } else {
                if (orderPresent) {
                    android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.customerHomeFrame, new OrderList_ForReviewsFragment()).addToBackStack(null);
                    ft.commit();
                } else {
                    android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.customerHomeFrame, new EmptyCustomerHistoryFragment()).addToBackStack(null);
                    ft.commit();
                }
            }
        } else if (id == R.id.nav_refundWallet) {
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(CustomerHome.this, "Please Sign First", Toast.LENGTH_LONG).show();
            } else {
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.customerHomeFrame, new RefundWalletFragment()).addToBackStack(null);
                ft.commit();
            }

        } else if (id == R.id.nav_sign_out) {
            if (mAuth.getCurrentUser() != null) {
                mAuth.signOut();
                Toast.makeText(CustomerHome.this, "You've Signed Out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CustomerHome.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(CustomerHome.this, "Please Sign In", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CustomerHome.this, CustomerSignInActivity.class);
                intent.putExtra("activityName", "CustomerHome");
                startActivity(intent);
                finish();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void setAppBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
