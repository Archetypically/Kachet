package project2.mobile.fsu.edu.kachet;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final static String TAG = "MainActivity";

    private MapFragment mapFragment;
    protected GoogleMap gMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng currLoc;
    private Marker locMarker;
    private PopupWindow kachePop;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerAdapter;
    private View popupView;

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

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);



        LayoutInflater layoutInflater = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.popup_kache, null);
        kachePop = new PopupWindow(
                popupView,
                (int)(size.x*.8),
                (int)(size.y*.8));

        mRecyclerView = (RecyclerView) popupView.findViewById(R.id.kache_recycler);
        LinearLayoutManager mRecyclerManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mRecyclerManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerAdapter = new KacheAdapter(null);
        mRecyclerView.setAdapter(mRecyclerAdapter);
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
        }
        else {
            currLoc = new LatLng(30.4461, -84.2996);
        }
        locMarker = gMap.addMarker(new MarkerOptions()
                .position(currLoc)
                .title("You!")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLoc, 5));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
    }


    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onResume(){
        mGoogleApiClient.connect();
        if(locMarker != null)
            locMarker.setVisible(true);
        super.onResume();
    }

    @Override
    protected void onPause(){
        if(locMarker != null)
            locMarker.setVisible(false);
        mGoogleApiClient.disconnect();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        locMarker.remove();
        super.onDestroy();
    }


    @Override
    public void onConnectionSuspended(int i) {
        locMarker.setVisible(false);
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
                // TODO: Implement proximity checking here

                kachePop.setTouchable(true);
                kachePop.setOutsideTouchable(true);
                kachePop.setAttachedInDecor(true);
                kachePop.setAnimationStyle(android.R.style.Animation_Dialog);
                kachePop.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                kachePop.setElevation(24);
                kachePop.showAtLocation(popupView, Gravity.CENTER, 0, -10);

                CoordinatorLayout mCoordLayout = (CoordinatorLayout) findViewById(R.id.coordLayout);
                Snackbar.make(mCoordLayout,
                        "You are not close enough to this kache!",
                        Snackbar.LENGTH_LONG);//.show();
            }
        });
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

    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "mipmap", getPackageName()));
        //Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return getResizedBitmap(imageBitmap, height, width);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth)
    {
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


}
