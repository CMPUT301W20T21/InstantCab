package com.example.instantcab;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.WriteResult;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PreviewRequestActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private TextView destinationRequest;
    private LatLng destinationLatLng;
    private TextView fare;
    private static double CAB_START_RATE = 3.75;
    private static double RATE_PER_KM = 1.65;
    private Button sendRequestButton;

    private View mapView;

    private Double originLat;
    private Double originLon;

    private FirebaseFirestore db;

    String TAG = "PreviewRequestActivity";

    private Geocoder geocoder;

    private String originAddr;
    private String destAddr;

    private int DEFAULT_ZOOM = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_request_activity);

        destinationRequest = findViewById(R.id.destination_request);
        fare = findViewById(R.id.fare);
        sendRequestButton = findViewById(R.id.send_request_button);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        destinationRequest.setText(intent.getExtras().getString("Address"));
        destinationLatLng = new LatLng(intent.getExtras().getDouble("Lat"), intent.getExtras().getDouble("Lon"));

        originLat = intent.getExtras().getDouble("currentLat");
        originLon = intent.getExtras().getDouble("currentLon");
        destAddr = intent.getExtras().getString("Address");

        geocoder = new Geocoder(PreviewRequestActivity.this, Locale.getDefault());

        originAddr = getAddressFromLatLon(new LatLng(originLat, originLon));

        //Calculating the distance in kilometers
        Double distance = getDistance(new LatLng(originLat, originLon), destinationLatLng);

        fare.setText(String.format("$%s", calculateRate(distance)));

        destinationRequest.setOnClickListener(new View.OnClickListener() {
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

        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
//                Intent intent = new Intent(PreviewRequestActivity.this, RiderRequest.class);
//                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions().position(destinationLatLng).title(destAddr));
        mMap.addMarker(new MarkerOptions().position(destinationLatLng).title(destAddr));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(originLat, originLon), DEFAULT_ZOOM));

        mMap.getUiSettings().setZoomControlsEnabled(true);

        /*
        draw route between origin and destination
         */
        drawRoute(new LatLng(originLat, originLon), destinationLatLng);
    }

    /**
     * calculate fare based on distance
     * @param distance
     * @return fare
     */
    public String calculateRate(double distance) {
        DecimalFormat df2 = new DecimalFormat("#.##");
        String estimate_fare = df2.format(CAB_START_RATE + RATE_PER_KM*distance);
        return estimate_fare;
    }

    /**
     * get request URL to get route information
     * @param origin
     * @param dest
     * @return a URL string
     */
    private String getRouteUrl(LatLng origin, LatLng dest) {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude + "," +
                origin.longitude + "&destination=" + dest.latitude + "," + dest.longitude +
                "&sensor=false&units=metric&mode=driving" + "&key=" + getString(R.string.google_maps_key);
    }

    /**
     * get driving distance from starting location to destination
     * @param origin
     * @param dest
     * @return driving distance in km
     */
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

    /**
     * generate a list of polylines consisting the route
     * @param origin
     * @param dest
     * @return a list of polylines of route
     */
    public List<LatLng> generateRoute(final LatLng origin, final LatLng dest){
        final String[] response = new String[1];
        final String[] encodedString = new String[1];

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getRouteUrl(origin, dest));
                    Log.i("PreviewRequestDrawRoute", getRouteUrl(origin, dest));
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response[0] = IOUtils.toString(in, "UTF-8");

                    JSONObject jsonObject = new JSONObject(response[0]);
                    JSONArray array = jsonObject.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                    encodedString[0] = overviewPolylines.getString("points");

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
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        return decodePolyline(encodedString[0]);
    }

    /**
     * draws a route froom starting location and destination on map
     * @param origin
     * @param dest
     */
    private void drawRoute(LatLng origin, LatLng dest){
        List<LatLng> polylineList = generateRoute(new LatLng(originLat, originLon), destinationLatLng);

        Polyline line = mMap.addPolyline(new PolylineOptions()
                .addAll(polylineList)
                .width(12)
                .color(Color.parseColor("#05b1fb"))//Google maps blue color
                .geodesic(true)
        );
    }

    /**
     * decode google coded polyline
     * @param encoded
     * @return a list of Latitude and Longitude consisting the route
     */
    private List<LatLng> decodePolyline(String encoded) {
        //https://stackoverflow.com/questions/14702621/draw-path-between-two-points-using-google-maps-android-api-v2

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    /**
     * add request to firebase
     */
    private void sendRequest(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            String email = user.getEmail();
            Log.i("have user", email);

            db = FirebaseFirestore.getInstance();

//            DatabaseReference mDatabase;
//// ...
//            mDatabase = FirebaseDatabase.getInstance().getReference();
//
////            DocumentReference dbDoc = db.collection("Users").document(email);
//
//            mDatabase.child("users").child(email).child("email").setValue(email);

            // Create a Map to store the data we want to set
//            Map<String, Object> docData = new HashMap<>();
//            docData.put("email", email);
//            docData.put("start_lat", originLat);
//            docData.put("start_lon", originLon);
//            docData.put("dest_lat", latLng.latitude);
//            docData.put("dest_lon", latLng.longitude);
//            docData.put("fare", fare.getText());
//            docData.put("status", "pending");
//            // Add a new document (asynchronously) in collection "cities" with id "LA"
//            db.collection("Request").document(email).set(docData);

            Request request = new Request(email, originLat, originLon, destinationLatLng.latitude, destinationLatLng.longitude, fare.getText().toString(), "pending", originAddr, destAddr);
            db.collection("Request").document(email).set(request);
            Log.i("origin location", originAddr);
        } else {
            // No user is signed in
            Log.i("does not have user", "fail");
        }
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
        return builder.toString().split(",")[0];
    }
}
