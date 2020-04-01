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
import java.sql.Driver;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * This class provides an activity to show a request page to the
 * rider and allow the rider to confirm or cancel the trip.
 *
 *  @author lijiangn
 */
public class DriverRequest extends AppCompatActivity {
    private Button ButtonPickedUp;
    private Button ButtonBack;
    private TextView riderStatus;
    private TextView showRider;
    private TextView showFare;
    private TextView starting;
    private TextView destination;
    private String riderName;
    private String riderEmail;
    private String fare;
    private FirebaseFirestore db;
    int indicator;


    private String email;
    private Request[] req = {new Request()};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_request);
        riderStatus = findViewById(R.id.rider_status);
        showRider = findViewById(R.id.rider_name);
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

            CollectionReference requests = db.collection("DriverRequest");
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
                                Intent intent = new Intent(DriverRequest.this, DriverHomeActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                    else updateUi(req);
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
                        riderEmail = snapshot.getString("email");
                        if (riderEmail != null && indicator == 0) {
                            indicator += 1;

                            // display the rider name
                            DocumentReference doc = db.collection("Users").document(riderEmail);
                            doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    riderName = documentSnapshot.getString("username");
                                    showRider.setText(riderName);
                                    req[0] = documentSnapshot.toObject(Request.class);
                                    if (riderStatus.getText() == "Waiting for rider to confirm" && req[0] != null) {
                                        //req[0].setDriver(driverEmail);
                                        //ButtonConfirmRequest.setVisibility(View.VISIBLE);
                                        //driverStatus.setText("Driver picked up request");
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

            updateLocalRequest();
        }
        else{
            Log.i("connectivity", "no");
            loadLocalRequest();
        }

        // when click on the rider's username, show his/her contact info
        showRider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!riderName.equals("")) {
                    Intent intent = new Intent(DriverRequest.this, DriverActivity.class);
                    intent.putExtra("DRIVER", riderEmail);
                    startActivity(intent);
                }
            }
        });


        ButtonBack = findViewById(R.id.back);

        ButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clicked when the rider confirms his request
                Intent intent = new Intent(DriverRequest.this, DriverHomeActivity.class);
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
            displayRider(req[0].getEmail());
            String a = req[0].getStatus();
            switch (a) {
                case "confirmed":
                    riderStatus.setText("Rider confirmed request");
                    break;
                case "picked up":
                    riderStatus.setText("You picked up rider");
                    break;
                case "accepted":
                    riderStatus.setText("Waiting for rider to confirm");
                    break;
            }
        }
    }

    /**
     * show the name of the driver
     * @param riderEmail
     * the rider request object
     */
    private void displayRider(String riderEmail) {
        if (riderEmail != null) {
            DocumentReference doc = db.collection("Users").document(riderEmail);
            doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    riderName = documentSnapshot.getString("username");
//                    showRider.setText(riderName);
                }
            });
        }
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

    public void loadLocalRequest(){
        SharedPreferences sharedPreferences = getSharedPreferences("localRequest", 0);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(DriverHomeActivity.driverEmail, "");
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
                    Intent intent = new Intent(DriverRequest.this, RiderMapsActivity.class);
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

            Log.i("load1", riderName+"here status"+request.getStatus());

            if (request.getStatus() != null) {
//                displayDriver(driverEmail);
                Log.i("load2", riderName+"here status"+request.getStatus());
                String a = request.getStatus();
                switch (a) {
                    case "accepted":
                        riderStatus.setText("Rider confirmed request");
                        showRider.setText(riderName);
                        break;
                    case "confirmed":
                        riderStatus.setText("Waiting for rider to confirm");
                        showRider.setText(riderName);
                        break;
                }
            }
            else{
                Log.i("load3", riderName+"here status"+request.getStatus());
            }
        }
    }

    public void updateLocalRequest(){
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            // User is signed in
//            String email = user.getEmail();
//            Log.i("have user", email);
//
//            SharedPreferences sharedPreferences = getSharedPreferences("localRequest", 0);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            Gson gson = new Gson();
//            String json = gson.toJson(request);
//            editor.putString(email, json);
//            editor.apply();
//        } else {
//            // No user is signed in
//            Log.i("does not have user", "fail");
//        }


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

                }
                else {
                    SharedPreferences sharedPreferences = getSharedPreferences("localRequest", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(req[0]);
                    editor.putString(email, json);
                    editor.apply();
                }
            }
        });
    }

    private Boolean checkInternetConnectivity(){
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }
}
