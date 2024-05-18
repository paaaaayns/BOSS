package com.example.boss;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginPage extends AppCompatActivity {

    Button btnLogin;
    TextView tvSignUp;

    TextInputEditText edtEmail, edtPassword;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        mAuth = FirebaseAuth.getInstance();

        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);



        // Login button click listener
        btnLogin.setOnClickListener(v -> {
            // Get the email and password from the user
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();

            // Check if the email and password are empty
            if (email.isEmpty() || password.isEmpty()) {
                // Show a toast message if the email or password is empty
                Toast.makeText(LoginPage.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            } else {
                // Sign in the user with the email and password
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String uid = user.getUid();

                                    // Get account type of the user
                                    getAccountType(uid);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });



        tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginPage.this, SignUpPage.class));
        });

    }


    public void getAccountType(String uid) {
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String accountType = document.getString("accountType");
                        if (accountType.equals("conductor")) {
                            // If the user is a conductor, redirect to the conductor dashboard
                            Intent intent = new Intent(LoginPage.this, ConductorDashboard.class);
                            // Pass the UID as an extra to the intent
                            intent.putExtra("uid", uid);
                            startActivity(intent);
                        } else {
                            // If the user is a passenger, redirect to the passenger dashboard
                            Intent intent = new Intent(LoginPage.this, PassengerDashboard.class);
                            // Pass the UID as an extra to the intent
                            intent.putExtra("uid", uid);
                            startActivity(intent);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            FirebaseUser user = mAuth.getCurrentUser();
            String uid = user.getUid();

            getAccountType(uid);

            Toast.makeText(this, "UID: " + uid, Toast.LENGTH_SHORT).show();
        }
    }
}