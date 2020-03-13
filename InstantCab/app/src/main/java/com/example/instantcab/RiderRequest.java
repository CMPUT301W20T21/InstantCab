/**Copyright 2020 CMPUT301W20T21
 *
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.*/

package com.example.instantcab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.Calendar;
import java.util.Date;

/**
 * This class provides an activity to show a request page to the
 * rider and allow the rider to confirm or cancel the trip.
 */
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
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_request);
        // expect to send in the driver's name and also the fare
//        fare = getIntent().getStringExtra("FARE");

        ButtonCancelRequest = findViewById(R.id.cancel_request);
        ButtonConfirmRequest = findViewById(R.id.confirm_request);
        ButtonPickedUp = findViewById(R.id.picked_up);
        ButtonArrive = findViewById(R.id.arrive);
        driverStatus = findViewById(R.id.driver_status);
        showDriver = findViewById(R.id.driver_name);
        // show the fare
        showFare = findViewById(R.id.fare);
        showFare.setText("$5.15");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        final Request[] req = {new Request()};
        CollectionReference requests = db.collection("Request");
        final DocumentReference request = requests.document(email);
        request.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                req[0] = documentSnapshot.toObject(Request.class);
            }
        });

        ButtonCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clicked when the rider cancels the request
                req[0].setStatus("cancelled");
                db.collection("Request").document(email).set(request);
                // need to notify the driver

                // move back to the map activity
                Intent intent = new Intent(RiderRequest.this, RiderMapsActivity.class);
            }
        });

        if (driverAccept) {
            driverStatus.setText("Driver picked up request");
            showDriver.setText(driverName);
            ButtonConfirmRequest.setVisibility(View.VISIBLE);
            req[0].setStatus("accepted");
            db.collection("Request").document(email).set(request);

            // need the app to fire a notification
        }

        ButtonConfirmRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clicked when the rider confirms his request
                ButtonPickedUp.setVisibility(View.VISIBLE);
                ButtonCancelRequest.setVisibility(View.INVISIBLE);
                ButtonConfirmRequest.setVisibility(View.INVISIBLE);

                // does need to notify the driver
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
                req[0].setStatus("finished");
                db.collection("Request").document(email).set(request);
                Intent intent = new Intent(RiderRequest.this, PaymentActivity.class);
                intent.putExtra("FARE", fare);
                startActivity(intent);
            }
        });
    }
}
