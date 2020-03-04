package com.example.instantcab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

        ButtonCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clicked when the rider cancels the request
                // need to notify the driver

                // move back to the map activity

            }
        });

        if (driverAccept) {
            driverStatus.setText("Driver picked up request");
            showDriver.setText(driverName);
            ButtonConfirmRequest.setVisibility(View.VISIBLE);
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
            }
        });
    }
}
