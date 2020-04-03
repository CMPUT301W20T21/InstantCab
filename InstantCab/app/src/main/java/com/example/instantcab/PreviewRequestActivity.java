package com.example.instantcab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.WriteResult;
import com.google.gson.Gson;
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

/**
 * This activity has a map that shows the driving route between start location and destination
 * Shows estimated fare
 * When user clicks the send request button, send the request in database and jump to open request page
 * Also show the address of destination over the map, when rider clicks on it, jump to EnterRouteActivity to change the request
 *
 * @author lshang
 */
public class PreviewRequestActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private View mapView;

    private TextView destinationRequest;
    private TextView fare;

    private static double CAB_START_RATE = 3.75;
    private static double RATE_PER_KM = 1.65;

    private Button sendRequestButton;

    private Double currentLat;
    private Double currentLon;

    private FirebaseFirestore db;

    String TAG = "PreviewRequestActivity";

    private Geocoder geocoder;

    private String originAddr;
    private String destAddr;
    private String startAddr;

    private LatLng destinationLatLng;
    private LatLng startLatLng;

    private int DEFAULT_ZOOM = 13;

    private String riderEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_request_activity);

        riderEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        destinationRequest = findViewById(R.id.destination_request);
        fare = findViewById(R.id.fare);
        sendRequestButton = findViewById(R.id.send_request_button);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        destinationRequest.setText(intent.getExtras().getString("destAddress"));

        destinationLatLng = new LatLng(intent.getExtras().getDouble("destLat"), intent.getExtras().getDouble("destLon"));
        startLatLng = new LatLng(intent.getExtras().getDouble("startLat"), intent.getExtras().getDouble("startLon"));

        currentLat = intent.getExtras().getDouble("currentLat");
        currentLon = intent.getExtras().getDouble("currentLon");

        startAddr = intent.getExtras().getString("startAddress");
        destAddr = intent.getExtras().getString("destAddress");

        geocoder = new Geocoder(PreviewRequestActivity.this, Locale.getDefault());

//        originAddr = getAddressFromLatLon(new LatLng(currentLat, currentLon));

        //Calculating the distance in kilometers
        Double distance = getDistance(startLatLng, destinationLatLng);

        fare.setText(String.format("$%s", calculateRate(distance)));

        destinationRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreviewRequestActivity.this, EnterRouteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("Lat", currentLat);
                bundle.putDouble("Lon", currentLon);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInternetConnectivity()) {

                    sendRequest();
                }
                else{
                    Toast.makeText(PreviewRequestActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions().position(destinationLatLng).title(destAddr));
        mMap.addMarker(new MarkerOptions().position(destinationLatLng).title(destAddr));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, DEFAULT_ZOOM));

        mMap.getUiSettings().setZoomControlsEnabled(true);

        /*
        draw route between origin and destination
         */
        drawRoute(startLatLng, destinationLatLng);
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
        /*
        Stackoverflow post by Kostya Khuta https://stackoverflow.com/users/2101843/kostya-khuta
        Answer https://stackoverflow.com/a/43258912/12826510
         */

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
        /*
        Based on
        Stackoverflow post by Kostya Khuta https://stackoverflow.com/users/2101843/kostya-khuta
        Answer https://stackoverflow.com/a/43258912/12826510
         */

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
        List<LatLng> polylineList = generateRoute(startLatLng, destinationLatLng);

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
        /*
        Stackoverflow post by Zeeshan Mirza https://stackoverflow.com/users/1547539/zeeshan-mirza
        Answer by https://stackoverflow.com/a/14702636/12826510
         */

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
            final String email = user.getEmail();
            Log.i("have user", email);

            db = FirebaseFirestore.getInstance();

            CollectionReference users = db.collection("Users");
            final DocumentReference rider = users.document(email);
            rider.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String riderName = documentSnapshot.get("username", String.class);
                    Request request = new Request(email, startLatLng.latitude, startLatLng.longitude, destinationLatLng.latitude,
                            destinationLatLng.longitude, fare.getText().toString(), "pending", startAddr, destAddr, riderName);
                    db.collection("Request").document(email).set(request);

                    saveLocalRequest(request);

                    Intent intent = new Intent(PreviewRequestActivity.this, RiderRequest.class);
                    startActivity(intent);
                }
            });


//            Log.i("origin location", originAddr);
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

        if(builder.toString() == ""){
            return (Double.toString(latLng.latitude) + ", " + Double.toString(latLng.longitude));
        }

        return builder.toString().split(",")[0];
    }

    /**
     * save new request to local database
     */
    public void saveLocalRequest(Request request){
        /*
            Stackoverflow post by Piraba https://stackoverflow.com/users/831498/piraba
            Answer https://stackoverflow.com/questions/7145606/how-android-sharedpreferences-save-store-object/18463758#18463758
            Stackoverflow post by Piraba https://stackoverflow.com/users/831498/piraba
            Answer https://stackoverflow.com/questions/7145606/how-android-sharedpreferences-save-store-object/18904599#18904599
        */
        SharedPreferences sharedPreferences = getSharedPreferences("localRequest", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(request);
        editor.putString(riderEmail, json);
        editor.apply();
    }

    /**
     * check if has internet connection
     * @return boolean whether has internet connection
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
