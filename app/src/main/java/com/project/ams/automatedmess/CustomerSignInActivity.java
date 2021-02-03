package com.project.ams.automatedmess;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CustomerSignInActivity extends AppCompatActivity {

    private EditText cLoginEmail, cLoginPassword;

    private Button cSignInBtn, cSignUpBtn;

    private FirebaseAuth mAuth;

    private String parentActivity;

    // Used for storing the type of user using the app in SQLite database
    private AppUserViewModel appUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_sign_in);

        // Start coding from here...
        cLoginEmail = findViewById(R.id.cSignInEmail);
        cLoginPassword = findViewById(R.id.cSignInPass);

        cSignInBtn = findViewById(R.id.cSignIn);
        cSignUpBtn = findViewById(R.id.cSignUp);

        // Init a few firebase stuff
        mAuth = FirebaseAuth.getInstance();

        // Get the name of the parent activity
        Intent intent = getIntent();
        parentActivity = intent.getStringExtra("activityName");

        // Init AppUserViewModel
        appUserViewModel = ViewModelProviders.of(this).get(AppUserViewModel.class);

        cSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = cLoginEmail.getText().toString();
                String password = cLoginPassword.getText().toString();

                // Do Some Filtering
                if (email.isEmpty()) {
                    cLoginEmail.requestFocus();
                    Toast.makeText(CustomerSignInActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    cLoginPassword.requestFocus();
                    Toast.makeText(CustomerSignInActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(CustomerSignInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(CustomerSignInActivity.this, "Something isn't right: \n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            // As we have succesfully authenticated
                            // Insert the type of user as "Customer" which will be used at the start of the application in MainActivity
                            // First delete any user there is
                            appUserViewModel.deleteAll();
                            // Create a new AppUser Object
                            AppUser aUser = new AppUser();
                            aUser.setUserType("Customer");
                            // Insert the type of user in the db
                            appUserViewModel.insert(aUser);


                            Toast.makeText(CustomerSignInActivity.this, "You've Signed In", Toast.LENGTH_SHORT).show();
                            // If Login is successful then go to either CustomerHome Activity or CustomerCart Activity
                            if (parentActivity.equals("CustomerCart")) {
                                Intent intent = new Intent(CustomerSignInActivity.this, CustomerCart.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(CustomerSignInActivity.this, CustomerHome.class);
                                startActivity(intent);
                                finish();
                            }

                            return;
                        }
                    }
                });
            }
        });

        cSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerSignInActivity.this, CustomerRegistrationActivity.class);
                intent.putExtra("activityName", parentActivity);
                startActivity(intent);
            }
        });
    }
}
