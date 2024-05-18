package com.example.boss;

import static android.content.ContentValues.TAG;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class AddTripPage extends AppCompatActivity {

    Button btnCancel, btnConfirm;
    TextInputEditText edtBusPlateNumber, edtTotalSeats;

    AutoCompleteTextView edtETD;

    AutoCompleteTextView ddownRoute;

    String[] routeItems = {};

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip_page);

        // Get the UID extra from the intent
        String uid = getIntent().getStringExtra("uid");


        edtBusPlateNumber = findViewById(R.id.edtBusPlateNumber);
        edtETD = findViewById(R.id.edtETD);
        edtTotalSeats = findViewById(R.id.edtTotalSeats);

        ddownRoute = findViewById(R.id.ddownRoute);
        List<String> routeItemsList = new ArrayList<>(); // Use a List to dynamically add document IDs
        CollectionReference routeRef = db.collection("busRoutes");
        routeRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String documentId = document.getId(); // Get the document ID
                            routeItemsList.add(documentId); // Add document ID to the list
                        }

                        // Convert the list to an array
                        routeItems = routeItemsList.toArray(new String[0]);

                        // Sort the array alphabetically
                        Arrays.sort(routeItems);

                        // Initialize the AutoCompleteTextView and set the adapter
                        ArrayAdapter<String> adapterOrigin = new ArrayAdapter<>(this, R.layout.dropdown_item, routeItems);
                        ddownRoute.setAdapter(adapterOrigin);

                        Log.d(TAG, "Route items: " + Arrays.toString(routeItems));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting documents.", e);
                });




        edtETD = findViewById(R.id.edtETD);
        edtETD.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    openTimeDialog(edtETD);
                }
            }
        });



        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> {
            finish();
        });

        btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(v -> {
            String busPlateNumber = edtBusPlateNumber.getText().toString();
            String route = ddownRoute.getText().toString();
            String departureTime = edtETD.getText().toString();
            String totalSeatsString = edtTotalSeats.getText().toString();

            if (busPlateNumber.isEmpty() || route.isEmpty() || departureTime.isEmpty() || totalSeatsString.isEmpty()) {
                // Show a toast message if the email or password is empty
                Toast.makeText(AddTripPage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {

                int totalSeats = Integer.parseInt(totalSeatsString);
                // Add the trip to the database
                Map<String, Object> trip = new HashMap<>();
                trip.put("conductor", uid);
                trip.put("isActive", true);
                trip.put("hasDeparted", false);
                trip.put("hasArrived", false);
                trip.put("busPlateNumber", busPlateNumber.toUpperCase());
                trip.put("route", route);
                trip.put("departureTime", departureTime);
                trip.put("arrivalTime", "");
                trip.put("totalSeats", totalSeats);
                trip.put("occupiedSeats", 0);
                trip.put("dateCreated", Timestamp.now());

                if (route.equals(routeItems[0])) {
                    trip.put("origin", "Pasig City Hall");
                    trip.put("destination", "Kenneth");
                } else if (route.equals(routeItems[1])) {
                    trip.put("origin", "Kenneth");
                    trip.put("destination", "Pasig City Hall");
                } else if (route.equals(routeItems[2])) {
                    trip.put("origin", "Pasig City Hall");
                    trip.put("destination", "Ligaya (via PCGH)");
                } else if (route.equals(routeItems[3])) {
                    trip.put("origin", "Ligaya");
                    trip.put("destination", "Pasig City Hall (via Rosario)");
                }

                String documentId = String.valueOf(System.currentTimeMillis());

                // Add the trip to the tripHistory collection with the custom document ID
                DocumentReference tripRef = db.collection("users").document(uid).collection("activeTrip").document(documentId);
                tripRef.set(trip)
                        .addOnSuccessListener(documentReference -> {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentId);
                            Toast.makeText(AddTripPage.this, "Trip added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Log.w(TAG, "Error adding document", e);
                            Toast.makeText(AddTripPage.this, "Failed to add trip", Toast.LENGTH_SHORT).show();
                        });

                // Add the trip to the list of activeTrips collection with the custom document ID
                DocumentReference activeTripsRef = db.collection("activeTrips").document(documentId);
                activeTripsRef.set(trip)
                        .addOnSuccessListener(documentReference -> {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentId);
                            Toast.makeText(AddTripPage.this, "Trip added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Log.w(TAG, "Error adding document", e);
                            Toast.makeText(AddTripPage.this, "Failed to add trip", Toast.LENGTH_SHORT).show();
                        });
            }
        });



        // Toolbar
        View toolbar = findViewById(R.id.toolbar);
        TextView tvTitle = toolbar.findViewById(R.id.tvTitle);
        tvTitle.setText("Add Trip");

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

    private void openTimeDialog(final AutoCompleteTextView editText) {

        // Set the time zone to Philippines
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Manila");

        // Get current time
        final Calendar currentTime = Calendar.getInstance(timeZone);
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(AddTripPage.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Check if selected time is after current time
                if (hourOfDay > currentHour || (hourOfDay == currentHour && minute > currentMinute)) {
                    // Convert to 12-hour format
                    String timeFormat = (hourOfDay < 12) ? "AM" : "PM";
                    int hour12 = (hourOfDay == 0 || hourOfDay == 12) ? 12 : hourOfDay % 12;
                    String selectedTime = String.format(Locale.US, "%02d:%02d %s", hour12, minute, timeFormat);
                    editText.setText(selectedTime);
                } else {
                    // Show error message or handle invalid time
                    Toast.makeText(AddTripPage.this, "Selected time must be after the current time", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Current time: " + currentHour + ":" + currentMinute);
                    Log.d(TAG, "Selected time: " + hourOfDay + ":" + minute);
                    Log.d(TAG, "Selected time must be after the current time");

                }
            }
        }, currentHour, currentMinute, false); // Set 12-hour mode

        // Lose focus on editText if dialog is dismissed
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                editText.clearFocus();
            }
        });

        // Lose focus on editText if dialog is cancelled
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                editText.clearFocus();
            }
        });

        dialog.show();
    }

}