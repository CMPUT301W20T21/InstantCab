package com.example.instantcab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

public class RiderRequest extends AppCompatActivity {
    private Button ButtonCancelRequest;
    private Button ButtonConfirmRequest;
    private Button ButtonPickedUp;
    private Button ButtonArrive;
    private TextView driverStatus;
    private TextView showDriver;
    private TextView showFare;
    private Boolean driverAccept;
    private String driverName;
    private String fare;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_request);
        // expect to send in the driver's name and also the fare
        fare = getIntent().getStringExtra("FARE");
        driverName = getIntent().getStringExtra("DRIVER_NAME");

        ButtonCancelRequest = findViewById(R.id.cancel_request);
        ButtonConfirmRequest = findViewById(R.id.confirm_request);
        ButtonPickedUp = findViewById(R.id.picked_up);
        ButtonArrive = findViewById(R.id.arrive);
        driverStatus = findViewById(R.id.driver_status);
        showDriver = findViewById(R.id.driver_name);
        // show the fare
        showFare = findViewById(R.id.fare);
        showFare.setText("$"+fare);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final DatabaseReference request = db.getReference("server/saving-data/requests");

        Date currentTime = Calendar.getInstance().getTime();
        request.child("rider").setValue(user.getDisplayName());
        request.child("time").setValue(currentTime.toString());
        request.child("status").setValue("pending");

        ButtonCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clicked when the rider cancels the request
                request.child("status").setValue("cancelled");
                // need to notify the driver

                // move back to the map activity
                Intent intent = new Intent(RiderRequest.this, RiderMapsActivity.class);
            }
        });

        if (driverAccept) {
            driverStatus.setText("Driver picked up request");
            showDriver.setText(driverName);
            ButtonConfirmRequest.setVisibility(View.VISIBLE);
            request.child("status").setValue("accepted");
            request.child("driver").setValue(driverName);

            // need the app to fire a notification
        }

        ButtonConfirmRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clicked when the rider confirms his request
                ButtonPickedUp.setVisibility(View.VISIBLE);
                ButtonCancelRequest.setVisibility(View.INVISIBLE);
                ButtonConfirmRequest.setVisibility(View.INVISIBLE);

                // need to notify the driver
            }
        });

        ButtonPickedUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clicked when the rider meets the driver
                driverStatus.setText("Driver picked up rider");
                ButtonPickedUp.setVisibility(View.INVISIBLE);
                ButtonArrive.setVisibility(View.VISIBLE);
            }
        });

        ButtonArrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clicked when the driver arrives the destination
                // go to the payment intent
                Intent intent = new Intent(RiderRequest.this, PaymentActivity.class);
                intent.putExtra("FARE", fare);
                startActivity(intent);
                request.child("status").setValue("finished");
            }
        });
    }
}
