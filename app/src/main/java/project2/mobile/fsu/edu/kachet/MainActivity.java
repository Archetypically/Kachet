package project2.mobile.fsu.edu.kachet;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/* TODO: Change boolean inKache to possibly a Marker object, and then use onInfoWindowClickListener
 to compare clicked marker to active kache */

public class MainActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback {

    private final static String TAG = "MainActivity";

    private final static int fenceRadius = 50;
    private MapFragment mapFragment;
    protected GoogleMap gMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng currLoc;
    private PopupWindow kachePop;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerAdapter;
    private View popupView;
    private LocationRequest mLocationRequest;
    private ArrayList<Geofence> mGeofenceList;
    private HashMap<LatLng, Marker> kacheList;
    private static boolean inKache = false;
    protected PendingIntent mGeofencePendingIntent;
    private static FloatingActionButton fab;

    //**************************************
    // Activity Lifecycle
    //**************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        kacheList = new HashMap<>();
        fab = (FloatingActionButton) findViewById(R.id.add_to_kache_button);
        fab.hide();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.popup_kache, null);
        kachePop = new PopupWindow(
                popupView,
                (int) (size.x * .8),
                (int) (size.y * .6));

        mRecyclerView = (RecyclerView) popupView.findViewById(R.id.kache_recycler);
        LinearLayoutManager mRecyclerManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mRecyclerManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerAdapter = new KacheAdapter(null);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        mGoogleApiClient.connect();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mGoogleApiClient.disconnect();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        for(Marker m : kacheList.values()) {
            if (m != null)
                m.remove();
        }
        super.onDestroy();
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
        } else {
            currLoc = new LatLng(30.44611163, -84.29944217);
        }

        LatLng kache = new LatLng(30.44611163, -84.29944217);
        kacheList.put(kache, addKachetoMap(kache));
        kache = new LatLng(30.43818458, -84.3043828);
        kacheList.put(kache, addKachetoMap(kache));

        for(LatLng coords : kacheList.keySet()){
            drawGeofenceBoundary(coords);
        }
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setInterval(30000);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

        mGeofenceList = new ArrayList<>();
        for (LatLng coords : kacheList.keySet()){
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(kacheList.get(coords).getTitle())
                    .setCircularRegion(
                            coords.latitude,
                            coords.longitude,
                            fenceRadius)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setLoiteringDelay(5000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }

        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        ).setResultCallback(this);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLoc, 5));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
    }

    @Override
    public void onConnectionSuspended(int i) {
        for(Marker m : kacheList.values())
            m.remove();
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                getGeofencePendingIntent()
        ).setResultCallback(this); // Result processed in onResult().
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

        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (inKache) {
                    kachePop.setTouchable(true);
                    kachePop.setOutsideTouchable(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        kachePop.setAttachedInDecor(true);
                        kachePop.setElevation(24);
                    }
                    kachePop.setAnimationStyle(android.R.style.Animation_Dialog);
                    kachePop.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    kachePop.showAtLocation(popupView, Gravity.CENTER, 0, -10);
                } else {
                    CoordinatorLayout mCoordLayout = (CoordinatorLayout) findViewById(R.id.coordLayout);
                    Snackbar.make(mCoordLayout,
                            "You are not close enough to this kache!",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });

        gMap.getUiSettings().setMapToolbarEnabled(false);
    }


    //**************************************
    // Helper Functions
    //**************************************

    private Marker addKachetoMap(LatLng coords){
        return gMap.addMarker(new MarkerOptions()
                .position(coords)
                .title("Kache")
                .snippet(String.valueOf(coords.latitude) + ", " + String.valueOf(coords.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
    }

    private void addMarkerFromCoordinates(double Lat, double Long, final String markerTitle) {
        LatLng newLocation = new LatLng(Lat, Long);
        Marker newMarker = gMap.addMarker(new MarkerOptions()
                .position(newLocation)
                .title(markerTitle));

    }

    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "mipmap", getPackageName()));
        //Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return getResizedBitmap(imageBitmap, height, width);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    //**************************************
    // Location Listener Interface Functions
    //**************************************

    @Override
    public void onLocationChanged(Location location) {
        // TODO: UPDATE CURRLOC IF NEEDED
    }

    //**************************************
    // Geofencing Functions
    //**************************************

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, KacheFencingService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(Result result) {
        if (!result.getStatus().isSuccess()) {
            Snackbar.make(
                    (CoordinatorLayout) findViewById(R.id.coordLayout),
                    "You do not have the correct location permissions to enable geofencing.",
                    Snackbar.LENGTH_LONG
            ).show();
        }
    }

    public static void setInKache() {
        inKache = true;
        fab.show();
    }

    public static void setOutKache() {
        inKache = false;
        fab.hide();
    }

    private void drawGeofenceBoundary(LatLng center){
        gMap.addCircle(new CircleOptions()
                        .center(center)
                        .radius(fenceRadius)
                        .fillColor(Color.parseColor("#66FCE4EC"))
                        .strokeColor(Color.parseColor("#99EC407A"))
        );
    }
}
