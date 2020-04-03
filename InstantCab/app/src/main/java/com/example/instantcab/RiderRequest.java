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

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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
    private Button ButtonBack;
    private TextView driverStatus;
    private TextView showDriver;
    private TextView showFare;
    private TextView starting;
    private TextView destination;
    private String driverName;
    private String driverEmail;
    private String fare;
    private FirebaseFirestore db;
    int indicator;


    private String email;
    private Request[] req = {new Request()};

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


        if(checkInternetConnectivity()){
            Log.i("connectivity", "yes");
            db = FirebaseFirestore.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                email = user.getEmail();
            }
            else {email = "test@email.com";}

            CollectionReference requests = db.collection("Request");
            final DocumentReference request = requests.document(email);
            request.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    req[0] = documentSnapshot.toObject(Request.class);
                    if (req[0] == null) {
                        // indicate that there is no request
                        setContentView(R.layout.activity_no_request);
                        ButtonBack = findViewById(R.id.back);

                        ButtonBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Clicked when the rider confirms his request
                                Intent intent = new Intent(RiderRequest.this, RiderMapsActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                    else {
                        updateLocalRequest(req[0]);
                        updateUi(req);
                    }
                }
            });

            request.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (req[0] == null) {
                        request.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                req[0] = documentSnapshot.toObject(Request.class);
                            }
                        });
                    }
                    if (snapshot != null && snapshot.exists()) {
                        driverEmail = snapshot.getString("driver");
                        if (driverEmail != null && indicator == 0) {
                            indicator += 1;

                            // display the driver name
                            DocumentReference doc = db.collection("Users").document(driverEmail);
                            doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    driverName = documentSnapshot.getString("username");
                                    showDriver.setText(driverName);
                                    req[0] = documentSnapshot.toObject(Request.class);
                                    if (driverStatus.getText() == "Waiting for driver to pick up" && req[0] != null) {
                                        req[0].setDriver(driverEmail);
                                        ButtonConfirmRequest.setVisibility(View.VISIBLE);
                                        driverStatus.setText("Driver picked up request");
                                        //changeStatus(req, email, request, "accepted");
                                        // need the app to issue a notification
                                        showNotification();
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
        else{
            Log.i("connectivity", "no");
            loadLocalRequest();
        }

        ButtonCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clicked when the rider cancels the request
                if(checkInternetConnectivity()) {
                    db = FirebaseFirestore.getInstance();
                    db.collection("Request").document(email)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                    if(driverEmail != null) {
                        db.collection("DriverRequest").document(driverEmail)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }
                    // remove request from local data
                    SharedPreferences preferences = getSharedPreferences("localRequest", 0);
                    preferences.edit().remove(email).apply();

                    // move back to the map activity
                    Intent intent = new Intent(RiderRequest.this, RiderMapsActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(RiderRequest.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ButtonConfirmRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInternetConnectivity()) {
                    db = FirebaseFirestore.getInstance();
                    // Clicked when the rider confirms his request
                    driverStatus.setText("Driver is on the way");
                    ButtonPickedUp.setVisibility(View.VISIBLE);
                    ButtonConfirmRequest.setVisibility(View.INVISIBLE);

                    changeStatus(req, email, "confirmed");
                }
                else{
                    Toast.makeText(RiderRequest.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ButtonPickedUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInternetConnectivity()) {
                    db = FirebaseFirestore.getInstance();
                    // Clicked when the rider meets the driver
                    driverStatus.setText("Driver picked up rider");
                    ButtonPickedUp.setVisibility(View.INVISIBLE);
                    ButtonArrive.setVisibility(View.VISIBLE);
                    ButtonCancelRequest.setVisibility(View.INVISIBLE);

                    changeStatus(req, email, "picked up");
                }
                else{
                    Toast.makeText(RiderRequest.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ButtonArrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInternetConnectivity()) {
                    db = FirebaseFirestore.getInstance();
                    // Clicked when the driver arrives the destination
                    // go to the payment intent
                    db.collection("Request").document(email)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    SharedPreferences preferences = getSharedPreferences("localRequest", 0);
                                    preferences.edit().remove(email).apply();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });

                    db.collection("DriverRequest").document(driverEmail)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });

                    Intent intent = new Intent(RiderRequest.this, PaymentActivity.class);
                    intent.putExtra("FARE", fare);
                    intent.putExtra("Driver", driverEmail);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(RiderRequest.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // when click on the driver's username, show his/her contact info
        showDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!driverName.equals("")) {
                    Intent intent = new Intent(RiderRequest.this, DriverActivity.class);
                    intent.putExtra("DRIVER", driverEmail);
                    startActivity(intent);
                }
            }
        });

        ButtonBack = findViewById(R.id.back);

        ButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clicked when the rider confirms his request
                Intent intent = new Intent(RiderRequest.this, RiderMapsActivity.class);
                startActivity(intent);
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

        //
        if (req[0].getStatus() != null) {
            displayDriver(driverEmail);
            String a = req[0].getStatus();
            switch (a) {
                case "accepted":
                    driverStatus.setText("Driver picked up request");
                    ButtonConfirmRequest.setVisibility(View.VISIBLE);
                    break;
                case "confirmed":
                    driverStatus.setText("Driver is on the way");
                    ButtonConfirmRequest.setVisibility(View.INVISIBLE);
                    ButtonPickedUp.setVisibility(View.VISIBLE);
                    break;
                case "picked up":
                    driverStatus.setText("Driver picked up rider");
                    ButtonConfirmRequest.setVisibility(View.INVISIBLE);
                    ButtonCancelRequest.setVisibility(View.INVISIBLE);
                    ButtonPickedUp.setVisibility(View.INVISIBLE);
                    ButtonArrive.setVisibility(View.VISIBLE);
                    break;
                case "pending":
                    driverStatus.setText("Waiting for driver to pick up");
                    break;
            }
        }
    }

    /**
     * show the name of the driver
     * @param driverEmail
     * the rider request object
     */
    private void displayDriver(String driverEmail) {
        if (driverEmail != null) {
            DocumentReference doc = db.collection("Users").document(driverEmail);
            doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    driverName = documentSnapshot.getString("username");
                    showDriver.setText(driverName);
                }
            });
        }
    }

    /**
     * change the status of the request
     */
    private void changeStatus(final Request []req, final String email, final String status) {
        DocumentReference request = db.collection("Request").document(email);
        request.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                req[0] = documentSnapshot.toObject(Request.class);
                req[0].setStatus(status);
                db.collection("Request").document(email).set(req[0]);
                db.collection("DriverRequest").document(driverEmail).set(req[0]);

                updateLocalRequest(req[0]);
            }
        });

    }

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Request Accepted!")
                .setContentText("Your request has been accepted by one of our driver.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    /**
     * update the UI using locally stored date when there is no internet connection
     */
    public void loadLocalRequest(){
        /*
            Stackoverflow post by Piraba https://stackoverflow.com/users/831498/piraba
            Answer https://stackoverflow.com/questions/7145606/how-android-sharedpreferences-save-store-object/18463758#18463758
            Stackoverflow post by Piraba https://stackoverflow.com/users/831498/piraba
            Answer https://stackoverflow.com/questions/7145606/how-android-sharedpreferences-save-store-object/18904599#18904599
         */
        SharedPreferences sharedPreferences = getSharedPreferences("localRequest", 0);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(RiderMapsActivity.userEmail, "");
        Type type = new TypeToken<Request>() {}.getType();
        Request request = gson.fromJson(json, type);

        // check if there is any data
        if(request == null){
            // set text "no active request"
            setContentView(R.layout.activity_no_request);
            ButtonBack = findViewById(R.id.back);

            ButtonBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Clicked when the rider confirms his request
                    Intent intent = new Intent(RiderRequest.this, RiderMapsActivity.class);
                    startActivity(intent);
                }
            });
        }
        else{
            email = request.getEmail();
            // expect to send in the driver's name and also the fare
            fare = request.getFare();
            // show the fare
            showFare.setText(fare);

            // show pick-up point and destination
            destination.setText(request.getDestinationName());
            starting.setText(request.getStartLocationName());

            driverName = request.getDriverName();
            Log.i("load1", driverName+"here status"+request.getStatus());

            if (request.getStatus() != null) {
//                displayDriver(driverEmail);
                Log.i("load2", driverName+"here status"+request.getStatus());
                String a = request.getStatus();
                switch (a) {
                    case "accepted":
                        driverStatus.setText("Driver picked up request");
                        ButtonConfirmRequest.setVisibility(View.VISIBLE);
                        showDriver.setText(driverName);
                        break;
                    case "confirmed":
                        driverStatus.setText("Driver is on the way");
                        ButtonConfirmRequest.setVisibility(View.INVISIBLE);
                        ButtonPickedUp.setVisibility(View.VISIBLE);
                        showDriver.setText(driverName);
                        break;
                    case "picked up":
                        driverStatus.setText("Driver picked up rider");
                        ButtonConfirmRequest.setVisibility(View.INVISIBLE);
                        ButtonCancelRequest.setVisibility(View.INVISIBLE);
                        ButtonPickedUp.setVisibility(View.INVISIBLE);
                        ButtonArrive.setVisibility(View.VISIBLE);
                        showDriver.setText(driverName);
                        break;
                    case "pending":
                        driverStatus.setText("Waiting for driver to pick up");
                        break;
                }
            }
            else{
                Log.i("load3", driverName+"here status"+request.getStatus());
            }
        }
    }

    /**
     * update local request date when there is internet connection
     */
    public void updateLocalRequest(Request request){
                if (request == null) {

                }
                else {
                    SharedPreferences sharedPreferences = getSharedPreferences("localRequest", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(request);
                    editor.putString(email, json);
                    editor.apply();
                }
    }

    /**
     * check if has internet connection
     * @return boolean whether has internet connection
     */
    private Boolean checkInternetConnectivity(){
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }
}
