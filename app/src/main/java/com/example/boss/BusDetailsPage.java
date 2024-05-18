package com.example.boss;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class BusDetailsPage extends AppCompatActivity {



    Button btnSubtract, btnAdd, btnAction;
    TextView tvBusPlateNumber, tvOrigin, tvDestination, tvDepartureTime, tvArrivalTime, tvOccupiedSeats, tvTotalSeats;
    LinearLayout layoutArrivalTime;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_details_page);

        // Get the UID extra from the intent
        String tripID = getIntent().getStringExtra("tripID");
        String uid = getIntent().getStringExtra("uid");

        Log.d(TAG, "Trip ID: " + tripID);

        tvBusPlateNumber = findViewById(R.id.tvBusPlateNumber);
        tvOrigin = findViewById(R.id.tvOrigin);
        tvDestination = findViewById(R.id.tvDestination);
        tvDepartureTime = findViewById(R.id.tvDepartureTime);
        tvArrivalTime = findViewById(R.id.tvArrivalTime);
        tvOccupiedSeats = findViewById(R.id.tvOccupiedSeats);
        tvTotalSeats = findViewById(R.id.tvTotalSeats);

        layoutArrivalTime = findViewById(R.id.layoutArrivalTime);

        btnAdd = findViewById(R.id.btnAdd);
        btnSubtract = findViewById(R.id.btnSubtract);
        btnAction = findViewById(R.id.btnAction);

        // Checking
        //Toast.makeText(this, "UID: " + uid, Toast.LENGTH_SHORT).show();
        //Log.d(TAG, "User: " + uid);

        DocumentReference userRef = db.collection("users").document(uid);
        DocumentReference activeTripRef = db.collection("users").document(uid).collection("activeTrip").document(tripID);
        DocumentReference activeTripsRef = db.collection("activeTrips").document(tripID);

        activeTripsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    Boolean hasDeparted = document.getBoolean("hasDeparted");
                    Log.d(TAG, "Has Departed: " + hasDeparted);

                    if (!hasDeparted) {
                        // Hide the Finish Trip button
                        btnAction.setText("Start Trip");
                        btnAction.setOnClickListener(v -> {

                            AlertDialog.Builder builder = new AlertDialog.Builder(BusDetailsPage.this);
                            builder.setTitle("Start Trip");
                            builder.setMessage("Are you sure you want to start this trip?");
                            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Start the trip
                                    activeTripRef.update("hasDeparted", true);
                                    activeTripsRef.update("hasDeparted", true);

                                    // Set the time zone to Philippines
                                    TimeZone timeZone = TimeZone.getTimeZone("Asia/Manila");

                                    // Get current time
                                    final Calendar currentTime = Calendar.getInstance(timeZone);
                                    int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
                                    int currentMinute = currentTime.get(Calendar.MINUTE);

                                    // Convert to 12-hour format
                                    String timeFormat = (currentHour < 12) ? "AM" : "PM";
                                    int hour12 = (currentHour == 0 || currentHour == 12) ? 12 : currentHour % 12;
                                    String currentTimeString = String.format(Locale.US, "%02d:%02d %s", hour12, currentMinute, timeFormat);

                                    // Override departure time
                                    activeTripRef.update("departureTime", currentTimeString);
                                    activeTripsRef.update("departureTime", currentTimeString);

                                    Log.d(TAG, "Updated departure time: " + currentTimeString);

                                    recreate();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss(); // Dismiss the dialog
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();

                        });
                    }
                    else {
                        btnAction.setText("Finish Trip");
                        btnAction.setOnClickListener(v -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(BusDetailsPage.this);
                            builder.setTitle("Finish Trip");
                            builder.setMessage("Are you sure you want to finish this trip?");

                            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Finish the trip
                                    activeTripRef.update("isActive", false);
                                    activeTripsRef.update("isActive", false);

                                    // Set the time zone to Philippines
                                    TimeZone timeZone = TimeZone.getTimeZone("Asia/Manila");

                                    // Get current time
                                    final Calendar currentTime = Calendar.getInstance(timeZone);
                                    int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
                                    int currentMinute = currentTime.get(Calendar.MINUTE);

                                    // Convert to 12-hour format
                                    String timeFormat = (currentHour < 12) ? "AM" : "PM";
                                    int hour12 = (currentHour == 0 || currentHour == 12) ? 12 : currentHour % 12;
                                    String currentTimeString = String.format(Locale.US, "%02d:%02d %s", hour12, currentMinute, timeFormat);


                                    activeTripRef.update("arrivalTime", currentTimeString);
                                    activeTripsRef.update("arrivalTime", currentTimeString);

                                    activeTripRef.update("hasArrived", true);
                                    activeTripsRef.update("hasArrived", true);

                                    Log.d(TAG, "Updated arrival time: " + currentTimeString);

                                    finish();
                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss(); // Dismiss the dialog
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        });
                    }





                } else {
                    Log.d(TAG, "No such document");
                }
            }

            });


        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    //Toast.makeText(BusDetailsPage.this, "User: " + document.getString("name"), Toast.LENGTH_SHORT).show();

                    LinearLayout layoutSeatDetails;
                    layoutSeatDetails = findViewById(R.id.layoutSeatDetails);
                    LinearLayout layoutFinishTrip;
                    layoutFinishTrip = findViewById(R.id.layoutFinishTrip);

                    if (document.getString("accountType").equals("passenger")) {
                        // Hide the Add button
                        btnAdd.setVisibility(View.GONE);
                        // Hide the Subtract button
                        btnSubtract.setVisibility(View.GONE);
                        // Hide the Finish Trip button
                        btnAction.setVisibility(View.GONE);
                    }

                    else {
                        btnAdd.setOnClickListener(v -> {
                            // Add 1 to the occupied seats

                            String prevOccupied = tvOccupiedSeats.getText().toString();
                            int totalSeats = Integer.parseInt(tvTotalSeats.getText().toString());
                            int curOccupied = Integer.parseInt(prevOccupied);
                            if (curOccupied < totalSeats) {
                                curOccupied = curOccupied + 1;
                            }

                            activeTripRef.update("occupiedSeats", curOccupied);
                            activeTripsRef.update("occupiedSeats", curOccupied);
                        });

                        btnSubtract.setOnClickListener(v -> {
                            // Subtract 1 from the occupied seats
                            String prevOccupied = tvOccupiedSeats.getText().toString();
                            int curOccupied = Integer.parseInt(prevOccupied);
                            if (curOccupied > 0) {
                                curOccupied = curOccupied - 1;
                            }

                            activeTripRef.update("occupiedSeats", curOccupied);
                            activeTripsRef.update("occupiedSeats", curOccupied);
                        });


                    }
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        // Get the active trip details
        activeTripsRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    // Set the data
                    tvBusPlateNumber.setText(snapshot.getString("busPlateNumber"));
                    tvOrigin.setText(snapshot.getString("origin"));
                    tvDestination.setText(snapshot.getString("destination"));
                    tvDepartureTime.setText(snapshot.getString("departureTime"));
                    tvOccupiedSeats.setText(String.valueOf(snapshot.getLong("occupiedSeats")));
                    tvTotalSeats.setText(String.valueOf(snapshot.getLong("totalSeats")));
                } else {
                    Log.d(TAG, "Current data: null");
                }


                boolean boolArrived = snapshot.getBoolean("hasArrived");

                if (boolArrived) {
                    // Hide the Add button
                    btnAdd.setVisibility(View.GONE);
                    // Hide the Subtract button
                    btnSubtract.setVisibility(View.GONE);
                    // Hide the Finish Trip button
                    btnAction.setVisibility(View.GONE);

                    layoutArrivalTime.setVisibility(View.VISIBLE);

                    tvArrivalTime.setText(snapshot.getString("arrivalTime"));
                }
            }
        });

        // Get the route of the active trip
        activeTripsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            String route = document.getString("route");

                            DocumentReference busRoutesRef = db.collection("busRoutes").document(route);
                            busRoutesRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d(TAG, "Route ID: " + document.getId());
                                            //Toast.makeText(BusDetailsPage.this, "Route: " + document.getId(), Toast.LENGTH_SHORT).show();

                                            String mapURL = document.getString("mapURL");
                                            Log.d(TAG, "Map URL: " + mapURL);

                                            WebView webView = findViewById(R.id.webView);
                                            webView.getSettings().setJavaScriptEnabled(true);
                                            webView.setWebViewClient(new WebViewClient());
                                            webView.loadUrl(mapURL);

                                            int pinCount = 0;
                                            Map<String, Object> data = document.getData();
                                            if (data != null) {
                                                for (Map.Entry<String, Object> entry : data.entrySet()) {
                                                    String key = entry.getKey();
                                                    // Check if the key starts with "pin"
                                                    if (key.startsWith("pin")) {
                                                        pinCount++;
                                                    }
                                                }

                                                Log.d(TAG, "Pin Location Count: " + pinCount);
                                                //Toast.makeText(BusDetailsPage.this, "Pin Count: " + pinCount, Toast.LENGTH_SHORT).show();

                                                // Get the parent layout
                                                ViewGroup parentLayout = findViewById(R.id.parentPinLayout);

                                                // Remove existing views
                                                parentLayout.removeAllViews();

                                                for (int i = 1; i <= pinCount; i++) { // Assuming MAX_NUMBER_OF_PINS is the maximum number of pins you expect
                                                    String pinKey = "pin" + i;
                                                    if (document.contains(pinKey)) {
                                                        String pinValue = document.getString(pinKey);
                                                        // Get the resource ID of the pin drawable dynamically
                                                        int pinDrawableId = getResources().getIdentifier("ic_pin" + i, "drawable", getPackageName());

                                                        // Checking
                                                        Log.d(TAG, "Pin " + i + ": " + pinValue);
                                                        //Toast.makeText(getApplicationContext(), pinValue, Toast.LENGTH_SHORT).show();

                                                        // Inflate a new layout for each active trip
                                                        View newLayout = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_pin_item, parentLayout, false);


                                                        // Find TextViews in the new layout and set their values
                                                        ImageView imgPin = newLayout.findViewById(R.id.imgPin);
                                                        TextView tvPinLocation = newLayout.findViewById(R.id.tvPinLocation);

                                                        imgPin.setImageResource(pinDrawableId);
                                                        tvPinLocation.setText(document.getString(pinKey));

                                                        // Add the new layout to the parent layout
                                                        parentLayout.addView(newLayout);

                                                    }
                                                }
                                            }

                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
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
        tvTitle.setText("Bus Details");

        ImageView icLeft = toolbar.findViewById(R.id.icLeft);
        icLeft.setImageResource(R.drawable.ic_back);

        ImageView icRight = toolbar.findViewById(R.id.icRight);
        icRight.setImageResource(R.drawable.ic_info);

        // Open Navigation Drawer
        icLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}