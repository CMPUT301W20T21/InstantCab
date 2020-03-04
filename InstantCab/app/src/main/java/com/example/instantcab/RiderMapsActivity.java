package com.example.instantcab;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class RiderMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private Marker mapMarker;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0x0011;
    View mapView;

    public static final String START_LOCATION = "com.example.testmap.START_LOCATION";
    private TextView makeRequest;

    String TAG = "EnterRouteActivity";

    public static final int ENTER_ROUTE_REQUEST = 10;
    public static final int ROUTE_RESULT_CODE = 11;

    public Geocoder geocoder;

    public static final LatLng DEFAULT_LOCATION = new LatLng(53.524620, -113.515890);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_home);
        getLocationPermission();

        geocoder = new Geocoder(RiderMapsActivity.this, Locale.getDefault());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        makeRequest = findViewById(R.id.request);
        makeRequest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RiderMapsActivity.this, EnterRouteActivity.class);
                startActivityForResult(intent, ENTER_ROUTE_REQUEST);
            }
        });
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

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(53.524620, -113.515890);

//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.addCircle(new CircleOptions().center(DEFAULT_LOCATION).radius(20));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15));
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMapClickListener(this);
        //mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if(mMap == null){
        }
        else {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
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
            layoutParams.setMargins(0, 0, 30, 30);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mapMarker == null) {
            mapMarker = mMap.addMarker(new MarkerOptions().position(latLng));
        }
        else {
            mapMarker.setPosition(latLng);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private void getDeviceLocation() {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        getDeviceLocation();
        return false;
    }

    private void getLocationPermission() {
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
//                Log.i("return", "Lat: " + data.getExtras().getDouble("Lat") + ", " + "Lon: " + data.getExtras().getDouble("Lon")
//                        + ", " + "Address: " + data.getExtras().getString("Address"));
//                Toast.makeText(this, data.getExtras().getString("Address"), Toast.LENGTH_SHORT).show();
                double lat = data.getExtras().getDouble("Lat");
                double lon = data.getExtras().getDouble("Lon");
                makeRequest.setText(data.getExtras().getString("Address"));
                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(data.getExtras().getString("Address")));
//                List<Address> addresses = null;
//                StringBuilder builder = new StringBuilder();
//                try {
//                    addresses = geocoder.getFromLocation(
//                            lat,
//                            lon, 1);
//                } catch (IOException ioException) {
//                    // Catch network or other I/O problems.
//                    //errorMessage = getString(R.string.service_not_available);
//                } catch (IllegalArgumentException illegalArgumentException) {
//                    // Catch invalid latitude or longitude values.
//                    //errorMessage = getString(R.string.invalid_lat_long_used);
//                }
//                if (addresses != null && addresses.size() > 0) {
//
////                    Address address = addresses.get(0);
////
////                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
////                        builder.append(address.getAddressLine(i));
////                    }
//                    makeRequest.setText(addresses.get(0).getAddressLine(0));
//                }
            }
//            makeRequest.setText(data.getExtras().getString("Address").split(",")[0]);
//            makeRequest.setText(Integer.toString(resultCode));
        }
    }
}
