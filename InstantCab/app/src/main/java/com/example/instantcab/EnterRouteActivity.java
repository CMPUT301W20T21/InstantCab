package com.example.instantcab;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class EnterRouteActivity extends AppCompatActivity {
    public final static int AUTOCOMPLETE_REQUEST_CODE = 21;
    public final static int SEARCH_RESULT_CODE = 22;
    View start_location;
    String TAG = "EnterRouteActivity";
    Double currentLat;
    Double currentLon;

    // Set the fields to specify which types of place data to
    // return after the user has made a selection.
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_route);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        //String message = intent.getStringExtra(RiderMapsActivity.START_LOCATION);
        currentLat = intent.getExtras().getDouble("Lat");
        currentLon = intent.getExtras().getDouble("Lon");

        start_location = findViewById(R.id.start_location);

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        start_location.setOnClickListener(new View.OnClickListener() {
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

                Intent intent = new Intent(EnterRouteActivity.this, PreviewRequestActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("Lat", res.latitude);
                bundle.putDouble("Lon", res.longitude);
                bundle.putString("Address", place.getName());
                bundle.putDouble("currentLat", currentLat);
                bundle.putDouble("currentLon", currentLon);
                intent.putExtras(bundle);
//                setResult(RiderMapsActivity.ROUTE_RESULT_CODE, intent);
//                finish();
                startActivity(intent);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == SEARCH_RESULT_CODE) {
            Intent intent = new Intent();
//            Bundle bundle = new Bundle();
//            bundle.putDouble("Lat", data.getExtras().getDouble("Lat"));
//            bundle.putDouble("Lon", data.getExtras().getDouble("Lon"));
//            bundle.putString("Address", data.getExtras().getString("Address"));
//            intent.putExtras(bundle);
            intent.putExtras(data.getExtras());
            setResult(RiderMapsActivity.ROUTE_RESULT_CODE, intent);
            finish();
        }

    }
}
