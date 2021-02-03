package com.project.ams.automatedmess;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button imCustomerBtn, imMessProvider;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private MessProviderViewModel messProviderViewModel;
    private AppUserViewModel appUserViewModel;

    private String appUser;

    private LifecycleOwner owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        owner = this;

        // Set the connections
        imCustomerBtn = findViewById(R.id.cUser);
        imMessProvider = findViewById(R.id.mUser);

        messProviderViewModel = ViewModelProviders.of(this).get(MessProviderViewModel.class);
        appUserViewModel = ViewModelProviders.of(this).get(AppUserViewModel.class);

        // Init a few firebase stuff
        mAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // Get the type of user of the app
                    appUserViewModel.getAllItems().observe(owner, new Observer<List<AppUser>>() {
                        @Override
                        public void onChanged(@Nullable final List<AppUser> items) {
                            appUser = items.get(0).getUserType();
                            if (appUser.equals("Customer")) {
                                Intent intent = new Intent(MainActivity.this, CustomerHome.class); // Change this, more sophistication needed
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(MainActivity.this, MessProviderHome.class); // Change this, more sophistication needed
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

                } else {
                    // Access SQLite to see if there are any orders.
                    // Checking whether or not there is a mess provider info in the table gives you the same result
                    messProviderViewModel.getAllItems().observe(owner, new Observer<List<MessProviderProfile>>() {
                        @Override
                        public void onChanged(@Nullable final List<MessProviderProfile> items) {
                            if (items.size() != 0) {
                                Intent intent = new Intent(MainActivity.this, CustomerHome.class);
                                startActivity(intent);
                                finish();
                                return;
                            }

                        }
                    });
                }
            }
        };


        imCustomerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CustomerHome.class);
                startActivity(intent);
            }
        });

        imMessProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MessReg_SignInActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }
}
