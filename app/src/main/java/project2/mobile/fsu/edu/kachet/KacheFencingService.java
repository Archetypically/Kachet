package project2.mobile.fsu.edu.kachet;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class KacheFencingService extends IntentService {
    private static final String TAG = "KacheFencingService";

    public KacheFencingService() {
        super("KacheFencingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "FencingEvent has error: " + geofencingEvent.getErrorCode());
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){
            MainActivity.setInKache();
        }
        else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            MainActivity.setOutKache();
        }
        else {
            // Log the error.
            Log.e(TAG, "The Geofence transition has errored.");
        }
    }
}
