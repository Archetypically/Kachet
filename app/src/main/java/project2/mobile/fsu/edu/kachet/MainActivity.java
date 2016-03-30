package project2.mobile.fsu.edu.kachet;

import android.Manifest;
import android.app.PendingIntent;
import android.app.TabActivity;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.Toast;

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

public class MainActivity extends TabActivity
        /*
        implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback
         */
{

    TabHost tabHost;
    TabHost.TabSpec spec;
    Intent tabIntent;

    /*
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
    */

    //**************************************
    // Activity Lifecycle
    //**************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabHost = this.getTabHost();

        tabIntent = new Intent(this, KacheMap.class);
        spec = tabHost.newTabSpec("my_posts").setIndicator("Posts", null)
                .setContent(tabIntent);
        tabHost.addTab(spec);

        tabIntent = new Intent(this, KacheMap.class);
        spec = tabHost.newTabSpec("Kachet_map").setIndicator("Kachet", null)
                .setContent(tabIntent);
        tabHost.addTab(spec);

        tabIntent = new Intent(this, Send_Media.class);
        spec = tabHost.newTabSpec("send_media").setIndicator("Send", null)
                .setContent(tabIntent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(1);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Toast.makeText(getApplicationContext(), "Tab ID = " + tabId,
                        Toast.LENGTH_SHORT).show();
            }
        });

        /*
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
        */
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {

        /*
        mGoogleApiClient.connect();
        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.main_title);
        else if (getActionBar() != null)
            getActionBar().setTitle(R.string.main_title);*/
        super.onResume();
    }

    @Override
    protected void onPause() {

        /*
        mGoogleApiClient.disconnect();
         */

        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        /*
        for(Marker m : kacheList.values()) {
            if (m != null)
                m.remove();
        }
        */

        super.onDestroy();
    }
}
