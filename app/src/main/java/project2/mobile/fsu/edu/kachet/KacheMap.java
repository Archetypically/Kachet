package project2.mobile.fsu.edu.kachet;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class KacheMap extends AppCompatActivity
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
    private LocationRequest mLocationRequest;
    private ArrayList<Geofence> mGeofenceList;
    private HashMap<LatLng, Marker> kacheList;
    public static String inKache = null;
    protected PendingIntent mGeofencePendingIntent;
    private static FloatingActionButton fab;
    private FrameLayout mFrameLayout;
    private FocusFragment mFocusFragment;
    private KacheFragment mKacheFragment;
    private static KacheAdapter mKacheAdapter;

    //**************************************
    // Activity Lifecycle
    //**************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        kacheList = new HashMap<>();
        fab = (FloatingActionButton) findViewById(R.id.view_kache);
        fab.hide();

        mFrameLayout = (FrameLayout) findViewById(R.id.mFrame);

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
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        mGoogleApiClient.connect();
        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.main_title);
        else if (getActionBar() != null)
            getActionBar().setTitle(R.string.main_title);
        super.onResume();
    }

    @Override
    protected void onPause() {
        kacheList.clear();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
                if(fm.getBackStackEntryCount() == 1 && getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.main_title);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){

            @Override
            public boolean onMarkerClick(Marker marker) {
                if(inKache != null) {
                    if (inKache.equals(marker.getTitle())) {
                        fab.show();
                    }
                }
                return false;
            }
        });

        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(!inKache.equalsIgnoreCase(marker.getTitle())){
                    CoordinatorLayout mCoordLayout = (CoordinatorLayout) findViewById(R.id.coordLayout);
                    Snackbar.make(mCoordLayout,
                            "You are not close enough to this kache!",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });

        gMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
            @Override
            public void onInfoWindowClose(Marker marker) {
                if (fab.isShown())
                    fab.hide();
            }
        });
        gMap.getUiSettings().setMapToolbarEnabled(false);
    }

    //**************************************
    // Kaching Functions
    //**************************************

    private Marker addKachetoMap(LatLng coords){
        return gMap.addMarker(new MarkerOptions()
                .position(coords)
                .title("Kache " + String.valueOf(kacheList.size() + 1))
                .snippet(String.valueOf(coords.latitude) + ", " + String.valueOf(coords.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
    }

    public void viewKache(View v) {
        showKachePopup();
    }

    private void showKachePopup(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        mKacheFragment = KacheFragment.newInstance(mKacheAdapter);
        transaction.add(android.R.id.content, mKacheFragment)
                .addToBackStack(null).commit();
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    //**************************************
    // Helper Functions
    //**************************************

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

    public void setActionBarTitle(String title){
        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
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

    public static void setInKache(final String code) {
        inKache = code;
        Thread thread = new Thread() {
            @Override
            public void run() {
                ArrayList<KacheAdapter.KacheMessage> messages
                        = new ArrayList<>();
                HttpURLConnection urlConnection = null;
                try {
                    String response = "";
                    URL url = new URL(
                            "http://www.tylerhunnefeld.com/android/db_fetchKacheData.php?kache_id="
                                    + String.valueOf(code.charAt(code.length() - 1)));
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.connect();
                    OutputStreamWriter request = new OutputStreamWriter(urlConnection.getOutputStream());
                    request.flush();
                    request.close();
                    String line = "";
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    while ((line = reader.readLine()) != null) {
                        response += line + "\n";
                    }

                    response = response.substring(response.indexOf('"') + 1, response.lastIndexOf('"'));

                    JSONArray msg = new JSONArray(response);
                    KacheAdapter.KacheMessage kMsg;
                    JSONObject tmp;
                    for(int i = 0; i < msg.length(); i++){
                        tmp = msg.getJSONObject(i);

                        String name = null;
                        try{
                            name = tmp.getString("name");
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }

                        String ts = null;
                        try {
                            ts = tmp.getString("timestamp");
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                        String message = null;
                        try {
                            message = tmp.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Date date = null;
                        if(ts != null) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            date = dateFormat.parse(ts);
                        }
                        kMsg = new KacheAdapter.KacheMessage(
                                message,
                                name,
                                date,
                                null,
                                Character.getNumericValue(code.charAt(code.length() - 1)));
                        messages.add(kMsg);
                    }
                    in.close();
                    reader.close();
                }
                catch (IOException|ParseException|JSONException e){
                    e.printStackTrace();
                }
                finally {
                    mKacheAdapter = new KacheAdapter(messages);
                    if(urlConnection != null)
                        urlConnection.disconnect();
                }
            }
        };
        thread.start();
    }

    public static void setOutKache(String code) {
        mKacheAdapter = null;
        inKache = null;
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
