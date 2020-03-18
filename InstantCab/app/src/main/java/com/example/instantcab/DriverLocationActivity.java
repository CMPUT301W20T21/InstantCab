package com.example.instantcab;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


public class DriverLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private GoogleMap mMap;
    private Marker previousMarker = null;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button btnAccept;
    private TextView textDest;
    private TextView textFare;
    private String TAG = "Rider at this marker is: ";
    public Request markerRequest = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_location);
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        updateLocationUI();

        btnAccept = findViewById(R.id.accept_request);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jump to DriverAcceptRequest Activity
                Intent intent = new Intent(DriverLocationActivity.this, DriverAcceptRequest.class);
                //if a marker was clicked before, information about the marker would be carried to new Activity
                if (markerRequest != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("from", markerRequest.getStartLocationName());
                    bundle.putString("to", markerRequest.getDestinationName());
                    bundle.putString("email", markerRequest.getEmail());
                    bundle.putString("fare", markerRequest.getFare());
                    intent.putExtras(bundle);
                }
                startActivity(intent);
            }
        });

    }

    private Map<String, Object> retrieveData() {
        // connect to firebase and load neighbouring markers
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        final Map<String, Object> userInfo = new HashMap<>();
        db.collection("Request").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Log.i("data", doc.getData().toString());

                        //double dest_lat = (double) doc.get("dest_lat");
                        //double dest_lon = (double) doc.get("dest_lon");
                        String email = (String) doc.get("email");
                        double start_lat = doc.get("startLatitude", double.class);
                        double start_lon = doc.get("startLongitude", double.class);
                        //userInfo.put("dest_lat",dest_lat);
                        //userInfo.put("dest_lon",dest_lon);
                        userInfo.put("email",email);
                        userInfo.put("start_lat",start_lat);
                        userInfo.put("start_lon",start_lon);
                        createMarker(start_lat, start_lon, email);
                    }
                }
            }
        });
        //Log.i("info", userInfo.);
        return userInfo;
    }

    private void updateLocationUI() {
        /*get driver's current location if permission is granted by user;
        otherwise, no action.
         */
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;}
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    //Log.i("current location",Double.toString(location.getLatitude()));
                    //Toast.makeText(getApplicationContext(), currentLocation.getLatitude()
                    //        +""+currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment)
                            getSupportFragmentManager().findFragmentById(R.id.google_map);
                    supportMapFragment.getMapAsync(DriverLocationActivity.this);
                }
            }
        });
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
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        //add markers into map
        retrieveData();
        //display driver's current position
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Driver Location");
        mMap.addCircle(new CircleOptions().center(latLng).radius(20));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        mMap.addMarker(markerOptions);
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
                            markerRequest = documentSnapshot.toObject(Request.class);
                            textDest = findViewById(R.id.destination);
                            textDest.setText(markerRequest.getDestinationName());
                            textFare = findViewById(R.id.fare);
                            textFare.setText(markerRequest.getFare());
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

}