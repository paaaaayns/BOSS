package com.example.boss;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignUpPage extends AppCompatActivity {



    Button btnCancel, btnSignUp;

    TextInputLayout layoutAuthCode;
    TextInputEditText edtFirstName, edtLastName, edtEmail, edtContactNo, edtPassword1, edtPassword2, edtAuthCode;


    AutoCompleteTextView ddownAccountType;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    String[] accountTypes = {"Passenger", "Conductor"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);


        mAuth = FirebaseAuth.getInstance();

        btnCancel = findViewById(R.id.btnCancel);
        btnSignUp = findViewById(R.id.btnSignUp);

        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        edtContactNo = findViewById(R.id.edtContactNo);
        edtPassword1 = findViewById(R.id.edtPassword1);
        edtPassword2 = findViewById(R.id.edtPassword2);
        edtAuthCode = findViewById(R.id.edtAuthCode);








        // Initialize the AutoCompleteTextView
        //ddownAccountType = findViewById(R.id.ddownAccountType);
        // Create ArrayAdapter with the options and set it to the AutoCompleteTextView
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, accountTypes);
        //ddownAccountType.setAdapter(adapter);


        ddownAccountType = findViewById(R.id.ddownAccountType);
        // Create ArrayAdapter with the options and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, accountTypes);
        ddownAccountType.setAdapter(adapter);

        ddownAccountType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                layoutAuthCode = findViewById(R.id.layoutAuthCode);
                if (ddownAccountType.getText().toString().equals("Conductor")) {
                    layoutAuthCode.setVisibility(View.VISIBLE);
                } else {
                    layoutAuthCode.setVisibility(View.GONE);
                }
            }
        });


        btnCancel.setOnClickListener(v -> {
            finish();
        });

        btnSignUp.setOnClickListener(v -> {
            // Implement sign up logic here

            String firstname = edtFirstName.getText().toString();
            String lastname = edtLastName.getText().toString();
            String email = edtEmail.getText().toString();
            String contactNo = edtContactNo.getText().toString().replaceAll("\\s", "");
            String password1 = edtPassword1.getText().toString();
            String password2 = edtPassword2.getText().toString();
            String accountType = ddownAccountType.getText().toString();
            String authCode = edtAuthCode.getText().toString();

            if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || contactNo.isEmpty() || password1.isEmpty() || password2.isEmpty() || accountType.isEmpty()) {
                // Show error message
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            else if (!password1.equals(password2)) {
                // Show error message
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            else {
                if (accountType.equals("Passenger")) {
                    mAuth.createUserWithEmailAndPassword(email, password1)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String uid = user.getUid(); // Get the user ID
                                        DocumentReference usersRef = db.collection("users").document(uid);

                                        Map<String, Object> userData = new HashMap<>();

                                        if (accountType.equals("Conductor")) {
                                            userData.put("accountType", "conductor");
                                            userData.put("hasActiveTrip", false);
                                        } else {
                                            userData.put("accountType", "passenger")    ;
                                        }
                                        userData.put("firstName", firstname);
                                        userData.put("lastName", lastname);
                                        userData.put("email", email);
                                        userData.put("contactNo", contactNo);


                                        // Add a new document with the user data
                                        usersRef.set(userData)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "Document added: " + userData);
                                                            Toast.makeText(SignUpPage.this, "Account successfully created", Toast.LENGTH_SHORT).show();
                                                            FirebaseAuth.getInstance().signOut();
                                                            finish();
                                                        } else {
                                                            Log.w(TAG, "Error adding document", task.getException());
                                                            Toast.makeText(SignUpPage.this, "Failed to create account", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    if (accountType.equals("Conductor") && authCode.equals("tech_team")) {
                        mAuth.createUserWithEmailAndPassword(email, password1)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            FirebaseUser user = mAuth.getCurrentUser();
                                            String uid = user.getUid(); // Get the user ID
                                            DocumentReference usersRef = db.collection("users").document(uid);

                                            Map<String, Object> userData = new HashMap<>();

                                            if (accountType.equals("Conductor")) {
                                                userData.put("accountType", "conductor");
                                                userData.put("hasActiveTrip", false);
                                            } else {
                                                userData.put("accountType", "passenger")    ;
                                            }
                                            userData.put("firstName", firstname);
                                            userData.put("lastName", lastname);
                                            userData.put("email", email);
                                            userData.put("contactNo", contactNo);


                                            // Add a new document with the user data
                                            usersRef.set(userData)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d(TAG, "Document added: " + userData);
                                                                Toast.makeText(SignUpPage.this, "Account successfully created", Toast.LENGTH_SHORT).show();
                                                                FirebaseAuth.getInstance().signOut();
                                                                finish();
                                                            } else {
                                                                Log.w(TAG, "Error adding document", task.getException());
                                                                Toast.makeText(SignUpPage.this, "Failed to create account", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else {
                        Toast.makeText(this, "Invalid authentication code", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}