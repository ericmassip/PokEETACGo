package edu.upc.eetac.dsa.pokeetacgo;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;

import java.util.Collections;
import java.util.List;

/**
 * Created by ericmassip on 26/12/16.
 */
public class GeofenceTransitionsIntentService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    final String TAG = "GeofenceIntentService";
    private GoogleApiClient mGoogleApiClient;

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    protected void onHandleIntent(Intent intent) {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "Geofencing Event Error");
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL || geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            Log.i(TAG, triggeringGeofences.toString());
            int requestIdOfGeofenceTriggered = Integer.parseInt(triggeringGeofences.get(0).getRequestId());
            goToProfemonAppearedActivity(requestIdOfGeofenceTriggered);
            removeGeofence(requestIdOfGeofenceTriggered);
        } else {
            Log.e(TAG, "Geofence Transition Invalid Type");
        }
        mGoogleApiClient.disconnect();
    }

    private void goToProfemonAppearedActivity(int requestIdOfGeofenceTriggered) {
        Intent profemonAppearedIntent = new Intent(this, ProfemonAppearedActivity.class);
        profemonAppearedIntent.putExtra("requestIdOfGeofenceTriggered", requestIdOfGeofenceTriggered);
        startActivity(profemonAppearedIntent);
    }

    private void removeGeofence(int requestIdOfGeofenceToRemove) {
        List<String> geofenceRequestIdsToDelete = Collections.singletonList(String.valueOf(requestIdOfGeofenceToRemove));
        try {
            LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, geofenceRequestIdsToDelete);
        } catch (IllegalStateException e) {
            Log.e(TAG, "GoogleApiClient not connected yet");
            removeGeofence(requestIdOfGeofenceToRemove);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
