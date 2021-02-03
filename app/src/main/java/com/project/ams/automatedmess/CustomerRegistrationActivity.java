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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class CustomerRegistrationActivity extends AppCompatActivity {

    // Declare these here so that we can use them throught the this activity class
    private EditText cRegName, cRegEmail, cRegAddress, cRegPhoneNo, cRegPassword, cRegConfirmPassword, cRegMobileNo;

    private Button mSignUpBtn;

    // Declare some Firebase stuff
    private FirebaseAuth mAuth;

    // Used to store the parent activity name
    private String parentActivity;

    // Used for storing the type of user using the app in SQLite database
    private AppUserViewModel appUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_registration);

        // Make the connections
        cRegName = findViewById(R.id.cName);
        cRegEmail = findViewById(R.id.cEmail);
        cRegAddress = findViewById(R.id.cAddress);
        cRegPhoneNo = findViewById(R.id.cPhoneNo);
        cRegPassword = findViewById(R.id.cPassword);
        cRegConfirmPassword = findViewById(R.id.cCPassword);
        mSignUpBtn = findViewById(R.id.cRegSignUp);

        cRegMobileNo = findViewById(R.id.cMobileNo);

        // Init Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Get the name of the parent activity
        Intent intent = getIntent();
        parentActivity = intent.getStringExtra("activityName");

        // Init AppUserViewModel
        appUserViewModel = ViewModelProviders.of(this).get(AppUserViewModel.class);

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = cRegName.getText().toString();
                String email = cRegEmail.getText().toString();
                String address = cRegAddress.getText().toString();
                String phoneNo = cRegPhoneNo.getText().toString();
                String password = cRegPassword.getText().toString();
                String confirmPassword = cRegConfirmPassword.getText().toString();
                String mobileNo = cRegMobileNo.getText().toString();

                if (name.isEmpty()) {
                    cRegName.requestFocus();
                    Toast.makeText(CustomerRegistrationActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!Pattern.matches("[a-zA-z\\s]+", name)) { // Used for checking that the name field only contains letters
                    cRegName.requestFocus();
                    Toast.makeText(CustomerRegistrationActivity.this, "Please enter a person's name", Toast.LENGTH_SHORT).show();
                    return;
                } else if (name.length() < 3) {
                    cRegName.requestFocus();
                    Toast.makeText(CustomerRegistrationActivity.this, "Please enter a name with atleast 3 letters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (address.isEmpty()) {
                    cRegAddress.requestFocus();
                    Toast.makeText(CustomerRegistrationActivity.this, "Please enter an address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (phoneNo.isEmpty()) {
                    cRegPhoneNo.requestFocus();
                    Toast.makeText(CustomerRegistrationActivity.this, "Please enter a phone no.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!Pattern.matches("\\d+", phoneNo)) { // Check whether the value given by the user is in the proper phone no. format
                    cRegPhoneNo.requestFocus();
                    Toast.makeText(CustomerRegistrationActivity.this, "Please enter only numbers,\n in the phone no. field, \nlike eg. 27031460", Toast.LENGTH_SHORT).show();
                    return;
                } else if (phoneNo.length() > 8) {
                    cRegPhoneNo.requestFocus();
                    Toast.makeText(CustomerRegistrationActivity.this, "A phone no. can have only 8 digits \nlike eg. 27033526", Toast.LENGTH_SHORT).show();
                    return;
                } else if (phoneNo.length() < 8) {
                    cRegPhoneNo.requestFocus();
                    Toast.makeText(CustomerRegistrationActivity.this, "A phone no. needs 8 digits \nlike eg. 27033526", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mobileNo.isEmpty()) {
                    cRegMobileNo.requestFocus();
                    Toast.makeText(CustomerRegistrationActivity.this, "Please enter a mobile no.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!Pattern.matches("\\d+", mobileNo)) { // Check whether the value given by the user is in the proper phone no. format
                    cRegMobileNo.requestFocus();
                    Toast.makeText(CustomerRegistrationActivity.this, "Please enter only numbers,\n in the mobile no. field, \nlike eg. 7742536684", Toast.LENGTH_SHORT).show();
                    return;
                } else if (mobileNo.length() > 10) {
                    cRegMobileNo.requestFocus();
                    Toast.makeText(CustomerRegistrationActivity.this, "A mobile no. can have only 10 digits \nlike eg. 7742536684", Toast.LENGTH_SHORT).show();
                    return;
                } else if (mobileNo.length() < 10) {
                    cRegMobileNo.requestFocus();
                    Toast.makeText(CustomerRegistrationActivity.this, "A mobile no. needs 10 digits \nlike eg. 7742536684", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (email.isEmpty()) {
                    cRegEmail.requestFocus();
                    Toast.makeText(CustomerRegistrationActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", email)) { // Check whether the value given by the user is in the proper email format
                    cRegEmail.requestFocus();
                    Toast.makeText(CustomerRegistrationActivity.this, "Please enter an email in the right format, \nlike eg. johndoe@gmail.com", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    cRegPassword.requestFocus();
                    Toast.makeText(CustomerRegistrationActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (confirmPassword.isEmpty()) {
                    cRegConfirmPassword.requestFocus();
                    Toast.makeText(CustomerRegistrationActivity.this, "Please confirm the password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    cRegConfirmPassword.requestFocus();
                    Toast.makeText(CustomerRegistrationActivity.this, "Please enter the same password\n as in the above field", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a user after the input passes through these filters
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CustomerRegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(CustomerRegistrationActivity.this, "Something isn't right: \n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        } else {
                            // Store the misc information into the database
                            String customerName = cRegName.getText().toString();
                            String customerAddress = cRegAddress.getText().toString();
                            String customerPhoneNo = cRegPhoneNo.getText().toString();
                            String customerMobileNo = cRegMobileNo.getText().toString();


                            // Store this unique userID which is created when registration is successful
                            // Used to retrive misc information for this userID/Mess Owner
                            String userID = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentDBRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID).child("PersonalInformation");
                            currentDBRef.child("name").setValue(customerName);
                            currentDBRef.child("address").setValue(customerAddress);
                            currentDBRef.child("phoneNo").setValue(customerPhoneNo);
                            currentDBRef.child("mobileNo").setValue(customerMobileNo);

                            // As we have succesfully authenticated
                            // Insert the type of user as "Customer" which will be used at the start of the application in MainActivity
                            // First delete any user there is
                            appUserViewModel.deleteAll();
                            // Create a new AppUser Object
                            AppUser aUser = new AppUser();
                            aUser.setUserType("Customer");
                            // Insert the type of user in the db
                            appUserViewModel.insert(aUser);

                            // TODO: Change this later we want to add Email Verification, not ot show the MessProfileEditor
                            Toast.makeText(CustomerRegistrationActivity.this, "You've Signed In", Toast.LENGTH_SHORT).show();


                            if (parentActivity.equals("CustomerCart")) {
                                Intent intent = new Intent(CustomerRegistrationActivity.this, CustomerCart.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(CustomerRegistrationActivity.this, CustomerHome.class);
                                startActivity(intent);
                                finish();
                            }

                        }
                    }
                });
            }
        });
    }
}
