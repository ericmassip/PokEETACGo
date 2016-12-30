package edu.upc.eetac.dsa.pokeetacgo.entity;

import com.google.android.gms.maps.model.Marker;

import edu.upc.eetac.dsa.pokeetacgo.entity.serviceLibraryResults.ProfemonLocationResult;

/**
 * Created by ericmassip on 24/12/16.
 */

public class LocationMarker {
    private ProfemonLocationResult profemonLocationResult;
    private Marker marker;

    public LocationMarker(ProfemonLocationResult profemonLocationResult, Marker marker) {
        this.profemonLocationResult = profemonLocationResult;
        this.marker = marker;
    }

    public ProfemonLocationResult getProfemonLocationResult() {
        return profemonLocationResult;
    }

    public void setProfemonLocationResult(ProfemonLocationResult profemonLocationResult) {
        this.profemonLocationResult = profemonLocationResult;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
