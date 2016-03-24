package project2.mobile.fsu.edu.kachet;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends AppCompatActivity
    implements OnMapReadyCallback{

    private MapFragment mapFragment;
    protected GoogleMap gMap;

    //**************************************
    // Activity Lifecycle
    //**************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up fragment
        //TODO: I set up the fragment from the main activity rather internally from the fragment, if we want to do this differently we'll have to code this differently


        //Get the map fragment and return the map (async)
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //**************************************
    // onMapReady Delegate Method
    //**************************************

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        //Checks for permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            gMap.setMyLocationEnabled(true);
        }
    }



    //**************************************
    // Helper Functions
    //**************************************

    private void addMarkerFromCoordinates (double Lat, double Long, final String markerTitle) {
        LatLng newLocation = new LatLng(Lat, Long);
        Marker newMarker = gMap.addMarker(new MarkerOptions().position(newLocation).title(markerTitle));

        //If we want to keep track of the markers, should put them in an array or something
        //TODO: I know Evan did this differently than I did for mine. I need to see why he did it the way he did and improve my code
        //markerArray.add(newMarker);
    }


}
