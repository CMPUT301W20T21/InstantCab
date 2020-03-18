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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MetadataChanges;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * This class provides an activity to show a request page to the
 * rider and allow the rider to confirm or cancel the trip.
 *
 *  @author lijiangn
 */
public class RiderRequest extends AppCompatActivity {
    private Button ButtonCancelRequest;
    private Button ButtonConfirmRequest;
    private Button ButtonPickedUp;
    private Button ButtonArrive;
    private TextView driverStatus;
    private TextView showDriver;
    private TextView showFare;
    private TextView starting;
    private TextView destination;
    private String driverName;
    private String driverEmail = "";
    private String fare;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    int indicator = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rider_request);

        ButtonCancelRequest = findViewById(R.id.cancel_request);
        ButtonConfirmRequest = findViewById(R.id.confirm_request);
        ButtonPickedUp = findViewById(R.id.picked_up);
        ButtonArrive = findViewById(R.id.arrive);
        driverStatus = findViewById(R.id.driver_status);
        showDriver = findViewById(R.id.driver_name);
        showFare = findViewById(R.id.fare);
        starting = findViewById(R.id.start);
        destination = findViewById(R.id.end);

        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String email;
        if (user != null) email = user.getEmail();
        else {email = "test@email.com";}
        mAuth = FirebaseAuth.getInstance();
        final Request[] req = {new Request()};
        //req[0] = new Request("", 0.0, 0.0, 0.0, 0.0, "", "", "", "");
        CollectionReference requests = db.collection("Request");
        DocumentReference request = requests.document(email);
        request.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                req[0] = documentSnapshot.toObject(Request.class);
                updateUi(req);
            }
        });

        request.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (snapshot != null && snapshot.exists()) {
                    driverEmail = snapshot.getString("driver");
                    if (driverEmail != null && indicator == 0) {
                        indicator += 1;

                        // TODO: the driver name cannot be shown
                        DocumentReference doc = db.collection("Users").document(driverEmail);
                        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                driverName = documentSnapshot.getString("username");
                                showDriver.setText(driverName);
                            }
                        });

                        req[0].setDriver(driverEmail);
                        driverStatus.setText("Driver picked up request");
                        ButtonConfirmRequest.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        ButtonCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clicked when the rider cancels the request
                req[0].setStatus("cancelled");
                db.collection("Request").document(email).set(req[0]);
                // move back to the map activity
                Intent intent = new Intent(RiderRequest.this, RiderMapsActivity.class);
                startActivity(intent);
            }
        });

        // TODO: need the app to issue a notification

        ButtonConfirmRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clicked when the rider confirms his request
                ButtonPickedUp.setVisibility(View.VISIBLE);
                ButtonCancelRequest.setVisibility(View.INVISIBLE);
                ButtonConfirmRequest.setVisibility(View.INVISIBLE);

                req[0].setStatus("confirmed");
                db.collection("Request").document(email).set(req[0]);
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
                db.collection("Request").document(email).set(req[0]);
                Intent intent = new Intent(RiderRequest.this, PaymentActivity.class);
                intent.putExtra("FARE", fare);
                startActivity(intent);
            }
        });

        // when click on the driver's username, show his/her contact info
        showDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!driverName.equals("")) {
                    Intent intent = new Intent(RiderRequest.this, ProfileActivity.class);
                    intent.putExtra("DRIVER", driverEmail);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * update the start and destination location
     * update the fare
     * @param req
     * the rider request object
     */
    private void updateUi(Request []req){
        // expect to send in the driver's name and also the fare
        fare = req[0].getFare();
        // show the fare
        showFare.setText(fare);

        // show pick-up point and destination
        destination.setText(req[0].getDestinationName());
        starting.setText(req[0].getStartLocationName());
    }
}
