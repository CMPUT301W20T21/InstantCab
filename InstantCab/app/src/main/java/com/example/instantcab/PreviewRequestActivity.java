package com.example.instantcab;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
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

    Double originLat;
    Double originLon;

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

        originLat = intent.getExtras().getDouble("currentLat");
        originLon = intent.getExtras().getDouble("currentLon");

        //Calculating the distance in kilometers

//        Double distance = SphericalUtil.computeDistanceBetween(RiderMapsActivity.DEFAULT_LOCATION, latLng);
        Double distance = getDistance(new LatLng(originLat, originLon), latLng);

        fare.setText(String.format("$%s", calculateRate(distance)));

        destination_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreviewRequestActivity.this, EnterRouteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("Lat", originLat);
                bundle.putDouble("Lon", originLon);
                intent.putExtras(bundle);
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

    private String getRouteUrl(LatLng origin, LatLng dest) {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude + "," +
                origin.longitude + "&destination=" + dest.latitude + "," + dest.longitude +
                "&sensor=false&units=metric&mode=driving" + "&key=" + getString(R.string.google_maps_key);
    }

    public Double getDistance(final LatLng origin, final LatLng dest){

        // https://stackoverflow.com/questions/18310126/get-the-distance-between-two-locations-in-android

        final String[] parsedDistance = new String[1];
        final String[] response = new String[1];
        final double[] distanceDouble = new double[1];

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getRouteUrl(origin, dest));
                    Log.i("PreviewRequestFlag", getRouteUrl(origin, dest));
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response[0] = IOUtils.toString(in, "UTF-8");

                    JSONObject jsonObject = new JSONObject(response[0]);
                    JSONArray array = jsonObject.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject steps = legs.getJSONObject(0);
                    JSONObject distance = steps.getJSONObject("distance");
                    parsedDistance[0] =distance.getString("value");
                    distanceDouble[0] = Double.parseDouble(parsedDistance[0]);

                    // https://stackoverflow.com/questions/56176753/how-implements-the-google-distance-matrix-api-at-android-studio
//                    if (parsedDistance[0].contains("km")){
//                        distanceDouble[0] = Double.parseDouble(parsedDistance[0].replace("km", ""));
//
//                    }
//                    else {
//                        distanceDouble[0] = Double.parseDouble("0." + parsedDistance[0].replace("m", ""));
//                    }

                    Log.i("distance", Double.toString(distanceDouble[0]));

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.i("PreviewRequestFlag", "here"+parsedDistance[0]);
        return distanceDouble[0]/1000;
    }
}
