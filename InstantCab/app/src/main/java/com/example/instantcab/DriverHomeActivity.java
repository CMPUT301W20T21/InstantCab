package com.example.instantcab;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.firestore.SetOptions;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
/**
 * This class is the activity driver sees after successfully logging in. It shows the map with driver's current location.
 * Drivers can view nearby requests by clicking corresponding markers on the map.
 * If driver clicks on accept_request button, details of the request will appear on another activity.
 *
 * @author peiyuan1
 */
public class DriverHomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Location currentLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public Location mLastKnownLocation;

    private static final int REQUEST_CODE = 101;

    private GoogleMap mMap;
    private View mapView;

    private Marker previousMarker = null;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private Button btnAccept;
    private TextView textDest;
    private TextView textFare;
    private TextView textStart;

    private String TAG = "Rider at this marker is: ";
    public Request markerRequest = null;
    public static String driverEmail;
    public boolean HasAcceptedRequest;

    public Geocoder geocoder;

    public static final LatLng DEFAULT_LOCATION = new LatLng(53.524620, -113.515890);
    public static final int DEFAULT_ZOOM = 15;

    private Request tmpRequest;

    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0x0011;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);

        getLocationPermission();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        driverEmail =user.getEmail();
        db = FirebaseFirestore.getInstance();

        geocoder = new Geocoder(DriverHomeActivity.this, Locale.getDefault());

        textDest = findViewById(R.id.destination);
        textFare = findViewById(R.id.fare);
        textStart = findViewById(R.id.start);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        updateUI();

        // accept_request button
        btnAccept = findViewById(R.id.accept_request);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jump to DriverAcceptRequest Activity
                //if a marker was clicked before, information about the marker would be carried to new Activity
                if (tmpRequest != null) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        // User is signed in
                        String email = user.getEmail();
                        Log.i("have user", email);

                        db = FirebaseFirestore.getInstance();

                        db.collection("DriverRequest").document(email).set(tmpRequest);

                        saveLocalRequest(tmpRequest);

                        Intent intent = new Intent(DriverHomeActivity.this, DriverRequest.class);
                        startActivity(intent);
                    } else {
                        // No user is signed in
                        Log.i("does not have user", "fail");
                    }
                }
            }
        });
    }

    private void retrieveData() {
        // connect to firebase and load neighbouring markers
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //final Map<String, Object> userInfo = new HashMap<>();
        db.collection("Request").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Log.i("data", doc.getData().toString());

                        //double dest_lat = (double) doc.get("dest_lat");
                        //double dest_lon = (double) doc.get("dest_lon");
                        String email = (String) doc.get("email");
                        Double start_lat = doc.get("startLatitude", Double.class);
                        Double start_lon = doc.get("startLongitude", Double.class);
                        String status = doc.get("status", String.class);
                        //userInfo.put("dest_lat",dest_lat);
                        //userInfo.put("dest_lon",dest_lon);
                        //userInfo.put("email",email);
                        //userInfo.put("start_lat",start_lat);
                        //userInfo.put("start_lon",start_lon);
                        if(status.equals("pending")) {
                            createMarker(start_lat, start_lon, email);
                        }
                    }
                }
            }
        });
        //Log.i("info", userInfo.);
        return;
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public  void updateUI() {
        if (user != null) {
            String email = user.getEmail();

            if (checkInternetConnectivity()) {
                DocumentReference docIdRef = db.collection("DriverRequest").document(email);
                docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "You already accept a request!");
                                btnAccept.setVisibility(View.GONE);
                            }
                            else {
                                Log.d(TAG, "Failed with: ", task.getException());
                                btnAccept.setVisibility(View.VISIBLE);
                            }
                        }
                        else {
                            Log.d(TAG, "Failed with: ", task.getException());
                        }
                    }
                });
                Log.i("have user", email);
            }
            else{
                SharedPreferences preferences = getSharedPreferences("localRequest", 0);
                if(preferences.contains(email)){
                    Log.d(TAG, "Document exists!");
                    btnAccept.setVisibility(View.GONE);
                }
                else{
                    Log.d(TAG, "Document does not exist!");
                    btnAccept.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private Boolean checkInternetConnectivity(){
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    private void createMarker (double dest_lat, double dest_lon, String email) {
        //create a marker with a location and a title
        LatLng latLng = new LatLng(dest_lat, dest_lon);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(email);
        mMap.addMarker(markerOptions);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //initialization google map
        mMap = googleMap;

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        mMap.setOnMapClickListener((GoogleMap.OnMapClickListener) this);

        if(mMap == null){
        }
        else {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }

        //add markers into map
        retrieveData();

        //click on marker event
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // only one marker is allowed to be selected once. If a marker is selected, revert last marker back.
                if (previousMarker != null) {
                    previousMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                // click marker should edit textView below.
                String markerEmail = marker.getTitle();
                // marker selected should not be driver's own location marker
                if (!markerEmail.equals("Driver Location")){
                    DocumentReference docRef = db.collection("Request").document(markerEmail);
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            tmpRequest = documentSnapshot.toObject(Request.class);
                            textDest.setText(tmpRequest.getDestinationName());
                            textFare.setText(tmpRequest.getFare());
                            textStart.setText(tmpRequest.getStartLocationName());
                            Log.i(TAG, textDest.toString());
                        }
                    });
                }

                previousMarker = marker;
                return true;
            }
        });
    }

    //handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateLocationUI();
                }
                break;
        }
    }

    /*
    ideas from https://developer.android.com/guide/topics/ui/menus
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_driver, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.current_request && markerRequest == null) {
//            setContentView(R.layout.activity_no_request);
//            ButtonBack = findViewById(R.id.back);
//
//            ButtonBack.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Clicked when the rider confirms his request
//                    Intent intent = new Intent(DriverLocationActivity.this, DriverAcceptRequest.class);
//                    startActivity(intent);
//                }
//            });
//        }

        if (item.getItemId() == R.id.current_request && markerRequest.getStatus().equals("accepted")) {
            Intent intent = new Intent(this, DriverAcceptRequest.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.current_request && markerRequest.getStatus().equals("confirmed")) {
            Intent intent = new Intent(this, RiderConfirmRequest.class);
            startActivity(intent);
        }

        else if (item.getItemId() == R.id.profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }

        else if (item.getItemId() == R.id.signOut){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.scan){
            Intent intent = new Intent(this, ScanQR.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void showNotification() {
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                // build notification
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentTitle("Offer accepted")
                .setContentText("Your offer has been accepted, please pick up the rider");

        // show the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    private void loadLocalRequest(){
        SharedPreferences sharedPreferences = getSharedPreferences("localRequest", 0);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(RiderMapsActivity.userEmail, "");
        Type type = new TypeToken<Request>() {}.getType();
        Request request = gson.fromJson(json, type);

        // check if there is any data
        if(request == null){
            // set text "no active request"
            setContentView(R.layout.activity_no_request);
//            ButtonBack = findViewById(R.id.back);
//
//            ButtonBack.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Clicked when the rider confirms his request
//                    setContentView(R.layout.activity_driver_location);
//                }
//            });
        }
        else{
            String email = request.getEmail();
            // expect to send in the driver's name and also the fare
            String fare = request.getFare();
            // show the fare

            // show pick-up point and destination
            String destinationName = request.getDestinationName();
            String startLocationName = request.getStartLocationName();

            String driverName = request.getDriverName();
            Log.i("load1", driverName+"here status"+request.getStatus());

            if (request.getStatus() != null) {
//                displayDriver(driverEmail);
                Log.i("load2", driverName+"here status"+request.getStatus());
                String a = request.getStatus();
                switch (a) {
                    case "accepted":
                        Intent intent1 = new Intent(this, DriverAcceptRequest.class);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("from", startLocationName);
                        bundle1.putString("to", destinationName);
                        bundle1.putString("email", email);
                        bundle1.putString("fare", fare);
                        intent1.putExtras(bundle1);
                        startActivity(intent1);
                        break;
                    case "confirmed":
                        Intent intent2 = new Intent(this, RiderConfirmRequest.class);
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("from", startLocationName);
                        bundle2.putString("to", destinationName);
                        bundle2.putString("email", email);
                        bundle2.putString("fare", fare);
                        intent2.putExtras(bundle2);
                        startActivity(intent2);
                        break;
//                    case "picked up":
//                        driverStatus.setText("Driver picked up rider");
//                        ButtonConfirmRequest.setVisibility(View.INVISIBLE);
//                        ButtonCancelRequest.setVisibility(View.INVISIBLE);
//                        ButtonPickedUp.setVisibility(View.INVISIBLE);
//                        ButtonArrive.setVisibility(View.VISIBLE);
//                        showDriver.setText(driverName);
//                        break;
//                    case "pending":
//                        driverStatus.setText("Waiting for driver to pick up");
//                        break;
                }
            }
            else{
                Log.i("load3", driverName+"here status"+request.getStatus());
            }
        }
    }

    public void saveLocalRequest(Request request){
        if (driverEmail != null) {
            // User is signed in
            Log.i("have user", driverEmail);

            SharedPreferences sharedPreferences = getSharedPreferences("localRequest", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(request);
            editor.putString(driverEmail, json);
            editor.apply();
        } else {
            // No user is signed in
            Log.i("does not have user", "fail");
        }
    }

    public void updateLocalRequest(){
        db = FirebaseFirestore.getInstance();
        final String driverEmail;
        final String[] riderEmail = new String[1];
        final Request[] req = new Request[1];

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            driverEmail = user.getEmail();
        }
        else {driverEmail = "test@email.com";}

        CollectionReference driverRequests = db.collection("Driver's Request");
        final DocumentReference driverRequest = driverRequests.document(driverEmail);
        driverRequest.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                riderEmail[0] = documentSnapshot.get("riderEmail", String.class);

                CollectionReference requests = db.collection("Request");
                final DocumentReference request = requests.document(riderEmail[0]);
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
                            editor.putString(driverEmail, json);
                            editor.apply();
                        }
                    }
                });
            }
        });
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * move map centre to current location
     * update mLastKnownLocation
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            if(mLastKnownLocation == null){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        DEFAULT_LOCATION, DEFAULT_ZOOM));
                                Log.i("no location", "no location accquired");
                            }
                            else {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

}