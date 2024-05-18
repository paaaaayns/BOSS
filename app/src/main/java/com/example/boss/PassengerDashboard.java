package com.example.boss;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PassengerDashboard extends AppCompatActivity {

    DrawerLayout drawerLayout;
    TextView tvName;
    LinearLayout layoutHome, layoutHome2, layoutHome3, layoutLogout;
    String firstName, lastName, email, contactNo;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_dashboard);

        // Get the UID extra from the intent
        String uid = getIntent().getStringExtra("uid");

        // Checking
//        Toast.makeText(this, "UID: " + uid, Toast.LENGTH_SHORT).show();
//        Log.d(TAG, "User: " + uid)

        DocumentReference userRef = db.collection("users").document(uid);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        firstName = document.getString("firstName");
                        lastName = document.getString("lastName");
                        email = document.getString("email");
                        contactNo = document.getString("contactNo");

                        tvName.setText(firstName);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });



        //DocumentReference userRef = db.collection("users").document(uid);
//        CollectionReference activeTripsRef = db.collection("activeTrips");
//        activeTripsRef
//                .whereEqualTo("isActive", true)
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
//                        if (e != null) {
//                            // If there is an error getting documents
//                            Log.d(TAG, "Error getting documents: ", e);
//                            Toast.makeText(getApplicationContext(), "Error getting documents", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        // Get the parent layout
//                        ViewGroup parentLayout = findViewById(R.id.parentActiveTrips);
//
//                        // Clear existing views
//                        parentLayout.removeAllViews();
//
//                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
//                            // Iterate through each document in the query snapshot
//                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//
//                                // Inflate a new layout for each active trip
//                                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
//                                View newLayout = inflater.inflate(R.layout.layout_active_trip_true, parentLayout, false);
//
//                                // Find TextViews in the new layout and set their values
//                                TextView tvBusPlateNumber = newLayout.findViewById(R.id.tvBusPlateNumber);
//                                TextView tvOrigin = newLayout.findViewById(R.id.tvOrigin);
//                                TextView tvDestination = newLayout.findViewById(R.id.tvDestination);
//                                TextView tvOccupiedSeats = newLayout.findViewById(R.id.tvOccupiedSeats);
//                                TextView tvTotalSeats = newLayout.findViewById(R.id.tvTotalSeats);
//
//                                tvBusPlateNumber.setText(document.getString("busPlateNumber"));
//                                tvOrigin.setText(document.getString("origin"));
//                                tvDestination.setText(document.getString("destination"));
//                                tvOccupiedSeats.setText(String.valueOf(document.getLong("occupiedSeats"))); // Use String.valueOf() to convert long to String
//                                tvTotalSeats.setText(String.valueOf(document.getLong("totalSeats"))); // Use String.valueOf() to convert long to String
//
//                                // Set margin for the new layout
//                                int marginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()); // Convert 16dp to pixels
//                                ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Set layout params
//                                layoutParams.setMargins(marginPx, 0, marginPx, marginPx);
//                                newLayout.setLayoutParams(layoutParams); // Set layout params to the inflated view
//
//                                // Add the new layout to the parent layout
//                                parentLayout.addView(newLayout);
//
//                                newLayout.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Toast.makeText(getApplicationContext(), "Trip ID: " + document.getId(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        } else {
//                            // If there are no active trips
//                            Toast.makeText(getApplicationContext(), "No active trip", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });





















        //DocumentReference userRef = db.collection("users").document(uid);
        CollectionReference activeTripsRef = db.collection("activeTrips");
        activeTripsRef
                .whereEqualTo("isActive", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        // Get the parent layout
                        ViewGroup parentLayout = findViewById(R.id.parentActiveTrips);

                        // Remove existing views
                        parentLayout.removeAllViews();

                        List<String> activeTrips = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value) {
                            document.getId();
                            activeTrips.add(document.getId());

                            Log.d(TAG, document.getId() + " => " + document.getData());

                            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                            View newLayout = inflater.inflate(R.layout.layout_active_trips, parentLayout, false);

                            // Inflate a new layout for each active trip

                            // Set margin for the new layout
                            int marginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()); // Convert 16dp to pixels
                            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) newLayout.getLayoutParams();
                            layoutParams.setMargins(marginPx, 0, marginPx, marginPx);

                            // Find TextViews in the new layout and set their values
                            TextView tvBusPlateNumber = newLayout.findViewById(R.id.tvBusPlateNumber);
                            TextView tvOrigin = newLayout.findViewById(R.id.tvOrigin);
                            TextView tvDestination = newLayout.findViewById(R.id.tvDestination);
                            TextView tvDepartureTime = newLayout.findViewById(R.id.tvDepartureTime);
                            TextView tvOccupiedSeats = newLayout.findViewById(R.id.tvOccupiedSeats);
                            TextView tvTotalSeats = newLayout.findViewById(R.id.tvTotalSeats);

                            tvBusPlateNumber.setText(document.getString("busPlateNumber"));
                            tvOrigin.setText(document.getString("origin"));
                            tvDestination.setText(document.getString("destination"));
                            tvDepartureTime.setText(document.getString("departureTime"));
                            tvOccupiedSeats.setText(String.valueOf(document.getLong("occupiedSeats"))); // Use String.valueOf() to convert long to String
                            tvTotalSeats.setText(String.valueOf(document.getLong("totalSeats"))); // Use String.valueOf() to convert long to String

                            // Add the new layout to the parent layout
                            parentLayout.addView(newLayout);

                            newLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), BusDetailsPage.class);
                                    intent.putExtra("uid", uid);
                                    intent.putExtra("tripID", document.getId());
                                    startActivity(intent);
                                }
                            });
                        }

                        Log.d(TAG, "Current active trips: " + activeTrips.size());
                    }
                });



        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your refresh action here
                // This code will be executed when the user swipes down to refresh
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Update your UI or reload data here
                        recreate();

                        // Stop the refreshing animation
                        swipeRefreshLayout.setRefreshing(false);
                    };
                }, 500);// Simulate a delay of 2 seconds
            }
        });


        // Toolbar
        View toolbar = findViewById(R.id.toolbar);
        TextView tvTitle = toolbar.findViewById(R.id.tvTitle);
        tvTitle.setText("Home");

        ImageView icLeft = toolbar.findViewById(R.id.icLeft);
        icLeft.setImageResource(R.drawable.ic_menu);

        ImageView icRight = toolbar.findViewById(R.id.icRight);
        icRight.setImageResource(R.drawable.ic_info);

        drawerLayout = findViewById(R.id.drawerLayout);
        tvName = findViewById(R.id.tvName);
        layoutHome = findViewById(R.id.layoutHome);
        layoutLogout = findViewById(R.id.layoutLogout);

        // Open Navigation Drawer
        icLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });


        // Redirect Page - Home Page
        layoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });



        // Redirect Page - Logout
        layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), "Account signed out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
                finish();
            }
        });


    }



    public static void displayActiveTrips(){

    }

    // OPEN NAVIGATION DRAWER
    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    // CLOSE NAVIGATION DRAWER
    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    // REDIRECT ON NAVIGATION DRAWER CLICK
    public void redirectActivity(Class secondActivity) {
        Intent intent = new Intent(getApplicationContext(), secondActivity);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    private boolean backPressedOnce = false;
    @Override
    public void onBackPressed() {

        if (backPressedOnce) {
            super.onBackPressed();
            finish();
        } else {
            this.backPressedOnce = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

            // Reset the flag after a certain time period
            new Handler().postDelayed(() -> backPressedOnce = false, 2000);
        }
    }
}