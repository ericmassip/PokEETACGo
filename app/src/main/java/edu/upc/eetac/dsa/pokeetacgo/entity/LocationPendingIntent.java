package edu.upc.eetac.dsa.pokeetacgo.entity;

import android.app.PendingIntent;

import com.google.android.gms.maps.model.Marker;

import edu.upc.eetac.dsa.pokeetacgo.entity.serviceLibraryResults.ProfemonLocationResult;

/**
 * Created by ericmassip on 24/12/16.
 */

public class LocationPendingIntent {
    private ProfemonLocationResult profemonLocationResult;
    private PendingIntent pendingIntent;
    private Marker marker;

    public LocationPendingIntent(ProfemonLocationResult profemonLocationResult, PendingIntent pendingIntent, Marker marker) {
        this.profemonLocationResult = profemonLocationResult;
        this.pendingIntent = pendingIntent;
        this.marker = marker;
    }

    public ProfemonLocationResult getProfemonLocationResult() {
        return profemonLocationResult;
    }

    public void setProfemonLocationResult(ProfemonLocationResult profemonLocationResult) {
        this.profemonLocationResult = profemonLocationResult;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
