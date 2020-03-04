package com.example.instantcab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.text.DecimalFormat;

public class PreviewRequestActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private TextView destination_request;
    private LatLng latLng;
    private String destination_name;
    private TextView fare;
    private static double CAB_START_RATE = 3.75;
    private static double RATE_PER_KM = 1.65;
    View mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_request_activity);

        destination_request = findViewById(R.id.destination_request);
        fare = findViewById(R.id.fare);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        destination_request.setText(intent.getExtras().getString("Address"));
        latLng = new LatLng(intent.getExtras().getDouble("Lat"), intent.getExtras().getDouble("Lon"));
        destination_name = intent.getExtras().getString("Address");
        //Calculating the distance in meters
        Double distance = SphericalUtil.computeDistanceBetween(RiderMapsActivity.DEFAULT_LOCATION, latLng);
        fare.setText(String.format("$%s", calculateRate(distance/1000)));

        destination_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreviewRequestActivity.this, EnterRouteActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(latLng).title(destination_name));
        mMap.addCircle(new CircleOptions().center(RiderMapsActivity.DEFAULT_LOCATION).radius(20));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(RiderMapsActivity.DEFAULT_LOCATION, 15));
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(RiderMapsActivity.DEFAULT_LOCATION, latLng));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    public String calculateRate(double distance) {
        DecimalFormat df2 = new DecimalFormat("#.##");
        String estimate_fare = df2.format(CAB_START_RATE + RATE_PER_KM*distance);
        return estimate_fare;
    }
}
