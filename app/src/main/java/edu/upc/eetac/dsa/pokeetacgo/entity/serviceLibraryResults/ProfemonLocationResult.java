package edu.upc.eetac.dsa.pokeetacgo.entity.serviceLibraryResults;

/**
 * Created by ericmassip on 7/12/16.
 */
public class ProfemonLocationResult {
    public int profemonId;
    public String name;
    public int locationId;
    public double latitude;
    public double longitude;
    public int floor;

    public ProfemonLocationResult fillInTheFields(int profemonId, String name, int locationId, double latitude, double longitude, int floor) {
        this.profemonId = profemonId;
        this.name = name;
        this.locationId = locationId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.floor = floor;
        return this;
    }
}
