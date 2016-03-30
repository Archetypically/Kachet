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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

// TODO: Hook up smaller FAB to the form layout

public class KacheMap extends AppCompatActivity
        implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback {

    private final static String TAG = "MainActivity";

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private static final String MEDIA_FOLDER_NAME = "Kachet";
    private Uri currentMediaUri;

    private final static int fenceRadius = 50;
    private MapFragment mapFragment;
    protected GoogleMap gMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng currLoc;
    private LocationRequest mLocationRequest;
    private ArrayList<Geofence> mGeofenceList;
    private HashMap<LatLng, Marker> kacheList;
    private static String inKache = null;
    protected PendingIntent mGeofencePendingIntent;
    private static FloatingActionButton fab;
    private FrameLayout mFrameLayout;
    private FocusFragment mFocusFragment;
    private KacheFragment mKacheFragment;

    //**************************************
    // Activity Lifecycle
    //**************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        kacheList = new HashMap<>();
        fab = (FloatingActionButton) findViewById(R.id.add_to_kache_button);
        fab.hide();

        mFrameLayout = (FrameLayout) findViewById(R.id.mFrame);
        mFrameLayout.getForeground().setAlpha(0);

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

        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(inKache != null) {
                    if (inKache.equals(marker.getTitle())) {
                        showKachePopup();
                    }
                }
                else {
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
    // Kaching Functions
    //**************************************

    public void addToKache(View v){
        Thread thread = new Thread() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    String response;
                    String parameters = "kache_id=3&message=fawefamwekflawmef";
                    URL url = new URL("http://www.tylerhunnefeld.com/android/db_addKacheData.php");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.connect();
                    OutputStreamWriter request = new OutputStreamWriter(urlConnection.getOutputStream());
                    request.write(parameters);
                    request.flush();
                    request.close();
                    String line = "";
                    InputStreamReader isr = new InputStreamReader(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    response = sb.toString();
                    Log.i("Message from Server", response);

                    isr.close();
                    reader.close();
                }
                catch (IOException ioe){
                    ioe.printStackTrace();
                }
                finally {
                    if(urlConnection != null)
                        urlConnection.disconnect();
                }
            }
        };
        thread.start();
    }

    private Marker addKachetoMap(LatLng coords){
        return gMap.addMarker(new MarkerOptions()
                .position(coords)
                .title("Kache " + String.valueOf(kacheList.size() + 1))
                .snippet(String.valueOf(coords.latitude) + ", " + String.valueOf(coords.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
    }

    public void viewOwnKache(View v) {
        showKachePopup();
    }

    private void showKachePopup(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        mKacheFragment = KacheFragment.newInstance();
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

    public static void setInKache(String code) {
        inKache = code;
        fab.show();
    }

    public static void setOutKache(String code) {
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

    //**************************************
    // Camera Functions
    //**************************************

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // React to captured image
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "onActivityResult: Image saved to: " + currentMediaUri.getPath());

                // TODO: Do something useful with saved image, located at currentMediaUri
            }
            else if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "onActivityResult: User cancelled the image capture");
            }
            else {
                // Image capture failed, advise user
                // TODO: Figure out how best to advise user
            }
        }

        // React to captured video
        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "onActivityResult: Video saved to: " + data.getData());

                // TODO: Do something useful with saved video, located at currentMediaUri
            }
            else if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "onActivityResult: User cancelled the video capture");
            }
            else {
                // Video capture failed, advise user
                // TODO: Figure out how best to advise user
            }
        }
    }

    /*
     * Uses an image capture intent to obtain an image from the device's camera
     */
    void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create a file to save the image
        currentMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        if (currentMediaUri == null) {
            Log.e(TAG, "CaptureImage: could not create file URI");
            return;
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentMediaUri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // TODO: Add ability to capture video

    /*
     * Creates a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
     * Creates a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // Check that external storage is mounted
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.e(TAG, "getOutputMediaFile: External storage is not mounted");
            return null;
        }

        // Set the directory in which the file will be stored
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), MEDIA_FOLDER_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "getOutputMediaFile: failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }
        else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        }
        else {
            return null;
        }

        return mediaFile;
    }
}
