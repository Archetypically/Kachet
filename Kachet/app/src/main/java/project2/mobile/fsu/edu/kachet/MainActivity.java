package project2.mobile.fsu.edu.kachet;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends AppCompatActivity
    implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private MapFragment mapFragment;
    protected GoogleMap gMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng currLoc;
    private Marker locMarker;

    //**************************************
    // Activity Lifecycle
    //**************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up fragment
        //TODO: I set up the fragment from the main activity rather internally from the fragment, if we want to do this differently we'll have to code this differently

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //Get the map fragment and return the map (async)
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //**************************************
    // GoogleApiClient Required Implements
    //**************************************

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            CoordinatorLayout mCoordLayout = (CoordinatorLayout) findViewById(R.id.coordLayout);
            Snackbar.make(mCoordLayout,
                            "You do not have the proper permission to access current location.",
                            Snackbar.LENGTH_INDEFINITE).show();
            return;
        }
        Location mLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLocation != null) {
            currLoc = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            locMarker = gMap.addMarker(new MarkerOptions().position(currLoc).title("You!"));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLoc, 5));
            gMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
