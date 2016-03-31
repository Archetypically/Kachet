package project2.mobile.fsu.edu.kachet;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class KacheFencingService extends IntentService {
    private static final String TAG = "KacheFencingService";

    public KacheFencingService() {
        super("KacheFencingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "FencingEvent has error: " + geofencingEvent.getErrorCode());
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){
            if(!geofenceList.isEmpty()) {
                KacheMap.setInKache(geofenceList.get(0).getRequestId());
            }
        }
        else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            if(!geofenceList.isEmpty())
                KacheMap.setOutKache(geofenceList.get(0).getRequestId());
        }
        else {
            Log.e(TAG, "The Geofence transition has errored.");
        }
    }
}
