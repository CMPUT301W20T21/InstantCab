package com.example.instantcab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class DriverAcceptRequest extends AppCompatActivity {
    private TextView textFrom;
    private TextView textTo;
    private TextView textEmail;
    private TextView textFare;
    private String strFrom;
    private String strTo;
    private String strEmail;
    private String strFare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_accept_request);

        //retrieve data form DriverLocationActivity onMarkerClickListener
        Intent intent = getIntent();
        strFrom = intent.getExtras().getString("from");
        strTo = intent.getExtras().getString("to");
        strEmail = intent.getExtras().getString("email");
        strFare = intent.getExtras().getString("fare");
        //apply data into TextView
        textFrom = findViewById(R.id.from);
        textFrom.setText(strFrom);
        textTo = findViewById(R.id.to);
        textTo.setText(strTo);
        textEmail = findViewById(R.id.driver_name);
        textEmail.setText(strEmail);
        textFare = findViewById(R.id.fare);
        textFare.setText(strFare);
    }


    /* waiting for this activity to complete to work
    public void loadLocalRequest(){
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
     */

    /* waiting for this activity to complete to work
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
