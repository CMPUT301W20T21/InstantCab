package com.example.instantcab;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

/**
 * This activity shows a map centred at rider's current location
 * When user clicks on the make a request text box it brings user to EnterRouteActivity
 * @author lshang
 */
public class RiderMapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private Marker mapMarker;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0x0011;
    private View mapView;

    public static final String START_LOCATION = "com.example.testmap.START_LOCATION";
    private TextView makeRequest;

    String TAG = "EnterRouteActivity";

    public static final int ENTER_ROUTE_REQUEST = 10;
    public static final int ROUTE_RESULT_CODE = 11;

    public Geocoder geocoder;

    public static final LatLng DEFAULT_LOCATION = new LatLng(53.524620, -113.515890);
    public static final int DEFAULT_ZOOM = 15;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    public Location mLastKnownLocation;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    public static String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_home);

        getLocationPermission();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userEmail =user.getEmail();
        db = FirebaseFirestore.getInstance();

        geocoder = new Geocoder(RiderMapsActivity.this, Locale.getDefault());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        makeRequest = findViewById(R.id.request);
        makeRequest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RiderMapsActivity.this, EnterRouteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("Lat", mLastKnownLocation.getLatitude());
                bundle.putDouble("Lon", mLastKnownLocation.getLongitude());
                intent.putExtras(bundle);
                startActivityForResult(intent, ENTER_ROUTE_REQUEST);
            }
        });

        updateUI();


        // sign in for testing
//        mAuth = FirebaseAuth.getInstance();
//        mAuth.signInWithEmailAndPassword("1111@email.com","12345678")
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()){
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInAnonymously:success");
//                        }
//                        else{
//                            Log.i("signinFail", "failed");
//                        }
//                    }
//                });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15));
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMapClickListener(this);
        //mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if(mMap == null){
        }
        else {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }

        /*
        https://stackoverflow.com/questions/36785542/how-to-change-the-position-of-my-location-button-in-google-maps-using-android-st/49038586
        Stackoverflow post by user5710756 https://stackoverflow.com/users/5710756/user5710756
        Answer https://stackoverflow.com/a/39179202/12826510
         */

        // adjust myLocation button position
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 150, 40);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
//        if (mapMarker == null) {
//            mapMarker = mMap.addMarker(new MarkerOptions().position(latLng));
//        }
//        else {
//            mapMarker.setPosition(latLng);
//        }
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    /**
     * get current location
     * @return
     */
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        getDeviceLocation();
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == ENTER_ROUTE_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                Place place = Autocomplete.getPlaceFromIntent(data);
//                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
//            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
//                // TODO: Handle the error.
//                Status status = Autocomplete.getStatusFromIntent(data);
//                Log.i(TAG, status.getStatusMessage());
//            } else if (resultCode == RESULT_CANCELED) {
//                // The user canceled the operation.
//            }
//        }
        if (requestCode == ENTER_ROUTE_REQUEST) {
            if (resultCode == ROUTE_RESULT_CODE) {
                Log.i("return", "Lat: " + data.getExtras().getDouble("Lat") + ", " + "Lon: " + data.getExtras().getDouble("Lon")
                        + ", " + "Address: " + data.getExtras().getString("Address"));
//                Toast.makeText(this, data.getExtras().getString("Address"), Toast.LENGTH_SHORT).show();
                double lat = data.getExtras().getDouble("Lat");
                double lon = data.getExtras().getDouble("Lon");
                makeRequest.setText(data.getExtras().getString("Address"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(data.getExtras().getString("Address")));
            }
        }
    }

    /**
     * get location permission from user
     */
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * set map centre to current location
     * enable my location button
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
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
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.current_request) {
            Intent intent = new Intent(this, RiderRequest.class);
            startActivity(intent);
        }
        else if (id == R.id.profile){
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }

        else if (id == R.id.signOut){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateUI(){
        if (user != null) {
            // User is signed in
            String email = user.getEmail();
            /*
            https://stackoverflow.com/questions/53332471/checking-if-a-document-exists-in-a-firestore-collection
             */
            if(checkInternetConnectivity()) {
                DocumentReference docIdRef = db.collection("Request").document(email);
                docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "Document exists!");
                                makeRequest.setText("You have an active request");
                                makeRequest.setTextColor(Color.BLACK);
                                makeRequest.setClickable(false);
                            } else {
                                Log.d(TAG, "Document does not exist!");
                                makeRequest.setHint("Make a request");
                                makeRequest.setClickable(true);
                            }
                        } else {
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
                    makeRequest.setText("You have an active request");
                    makeRequest.setTextColor(Color.BLACK);
                    makeRequest.setClickable(false);
                }
                else{
                    Log.d(TAG, "Document does not exist!");
                    makeRequest.setHint("Make a request");
                    makeRequest.setClickable(true);
                }
            }
        }

        else {
            // No user is signed in
            Log.i("does not have user", "fail");
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
}
