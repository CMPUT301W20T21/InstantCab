package com.example.instantcab;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class EnterRouteActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMapClickListener {
    private GoogleMap mMap;

    private View mapView;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    public Location mLastKnownLocation;

    public final static int AUTOCOMPLETE_REQUEST_CODE = 21;
    public final static int SEARCH_RESULT_CODE = 22;

    TextView startLocationBox;

    String TAG = "EnterRouteActivity";

    private Double currentLat;
    private Double currentLon;

    public static final LatLng DEFAULT_LOCATION = new LatLng(53.524620, -113.515890);
    public static final int DEFAULT_ZOOM = 15;

    private Marker tmpMarker;
    private Marker startMarker;
    private Marker destinationMarker;

    private Button startButton;
    private Button destinationButton;
    private Button nextButton;

    public Geocoder geocoder;

    private LatLng startLatLng;
    private LatLng destinationLatLng;

    private String startAddr;
    private String destinationAddr;

    // Set the fields to specify which types of place data to
    // return after the user has made a selection.
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_route);

        geocoder = new Geocoder(EnterRouteActivity.this, Locale.getDefault());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        startButton = findViewById(R.id.start_button);
        destinationButton = findViewById(R.id.destination_button);
        nextButton = findViewById(R.id.next_button);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        //String message = intent.getStringExtra(RiderMapsActivity.START_LOCATION);
        currentLat = intent.getExtras().getDouble("Lat");
        currentLon = intent.getExtras().getDouble("Lon");

        startLatLng = new LatLng(currentLat, currentLon);
        startAddr = getAddressFromLatLon(startLatLng);

        startLocationBox = findViewById(R.id.start_location);

        // initiate place api
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        startLocationBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(EnterRouteActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        // Initialize the AutocompleteSupportFragment.
        final AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setActivityMode(AutocompleteActivityMode.FULLSCREEN);
        autocompleteFragment.setHint("Enter a destination");

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID,
                Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                LatLng res = place.getLatLng();
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());

//                Intent intent = new Intent(EnterRouteActivity.this, PreviewRequestActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putDouble("Lat", res.latitude);
//                bundle.putDouble("Lon", res.longitude);
//                bundle.putString("Address", place.getName());
//                bundle.putDouble("currentLat", currentLat);
//                bundle.putDouble("currentLon", currentLon);
//                intent.putExtras(bundle);
//
//                startActivity(intent);

                destinationLatLng = place.getLatLng();
                if (destinationMarker == null) {
                    destinationMarker = mMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));
                }
                else {
                    destinationMarker.setPosition(destinationLatLng);
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(destinationLatLng));
                autocompleteFragment.setText(place.getName());
                destinationAddr = place.getName();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tmpMarker == null){
                    return;
                }
                if (startMarker == null) {
                    startMarker = mMap.addMarker(new MarkerOptions().position(tmpMarker.getPosition()).title("Start"));
                }
                else {
                    startMarker.setPosition(tmpMarker.getPosition());
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(startMarker.getPosition()));
                startLatLng = startMarker.getPosition();

                startAddr = getAddressFromLatLon(startLatLng);
                startLocationBox.setText(startAddr);
            }
        });

        destinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tmpMarker == null){
                    Log.i("destinationButton", "null?");
                    return;
                }
                if (destinationMarker == null) {
                    destinationMarker = mMap.addMarker(new MarkerOptions().position(tmpMarker.getPosition()).title("Destination"));
                }
                else {
                    destinationMarker.setPosition(tmpMarker.getPosition());
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(destinationMarker.getPosition()));
                destinationLatLng = destinationMarker.getPosition();

                destinationAddr = getAddressFromLatLon(destinationLatLng);
                autocompleteFragment.setText(destinationAddr);

                Log.i("destinationButton", destinationAddr);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((startLatLng == null) || (destinationLatLng == null)){
                    Toast.makeText(EnterRouteActivity.this, "You need a start location and a destination", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(EnterRouteActivity.this, PreviewRequestActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("startLat", startLatLng.latitude);
                    bundle.putDouble("startLon", startLatLng.longitude);
                    bundle.putDouble("destLat", destinationLatLng.latitude);
                    bundle.putDouble("destLon", destinationLatLng.longitude);
                    bundle.putString("startAddress", startAddr);
                    bundle.putString("destAddress", destinationAddr);
                    bundle.putDouble("currentLat", currentLat);
                    bundle.putDouble("currentLon", currentLon);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == SEARCH_RESULT_CODE) {
//            Intent intent = new Intent();
////            Bundle bundle = new Bundle();
////            bundle.putDouble("Lat", data.getExtras().getDouble("Lat"));
////            bundle.putDouble("Lon", data.getExtras().getDouble("Lon"));
////            bundle.putString("Address", data.getExtras().getString("Address"));
////            intent.putExtras(bundle);
//            intent.putExtras(data.getExtras());
//            setResult(RiderMapsActivity.ROUTE_RESULT_CODE, intent);
//            finish();
//        }
//
//    }

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
            startMarker = mMap.addMarker(new MarkerOptions().position(startLatLng).title("Start"));
        }

        /*
        https://stackoverflow.com/questions/36785542/how-to-change-the-position-of-my-location-button-in-google-maps-using-android-st/49038586
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
            layoutParams.setMargins(0, 0, 0, 250);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
//            if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
//            } else {
//                mMap.setMyLocationEnabled(false);
//                mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                mLastKnownLocation = null;
//                getLocationPermission();
//            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
//            if (mLocationPermissionGranted) {
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
//            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (tmpMarker == null) {
            tmpMarker = mMap.addMarker(new MarkerOptions().position(latLng));
        }
        else {
            tmpMarker.setPosition(latLng);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        getDeviceLocation();
        return false;
    }



    /**
     * find the address name by latLng
     * @param latLng
     * @return address street name
     */
    private String getAddressFromLatLon(LatLng latLng) {
        List<Address> addresses = null;
        String errorMessage = "";
        StringBuilder builder = new StringBuilder();
        try {
            addresses = geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude, 1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            //errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            //errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + latLng.latitude +
                    ", Longitude = " +
                    latLng.longitude, illegalArgumentException);
        }
        if (addresses != null && addresses.size() > 0) {

            Address address = addresses.get(0);

            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                builder.append(address.getAddressLine(i));
            }
            Log.i(TAG, "onClickMap: " + builder.toString());
            Log.i("locality", address.getLocality());
            Log.i("feature name", address.getFeatureName());
            Log.i("admin area", address.getAdminArea());
//            Log.i("premises", address.getPremises());
            Log.i("subadmin area", address.getSubAdminArea());
            Log.i("address line", address.getAddressLine(0));
        }

        if(builder.toString() == ""){
            return (Double.toString(latLng.latitude) + ", " + Double.toString(latLng.longitude));
        }

        return builder.toString().split(",")[0];
    }
}
