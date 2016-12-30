package edu.upc.eetac.dsa.pokeetacgo;

import android.util.SparseArray;

import edu.upc.eetac.dsa.pokeetacgo.entity.LocationMarker;

/**
 * Created by ericmassip on 17/12/16.
 */
public class PokEETACGo {
    private static PokEETACGo instance = null;
    private int currentUserId;
    public SparseArray<LocationMarker> profemonLocationMarkers = new SparseArray<>();

    private PokEETACGo() {}

    public static synchronized PokEETACGo getInstance() {
        if (instance == null) instance = new PokEETACGo();
        return instance;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }
}
