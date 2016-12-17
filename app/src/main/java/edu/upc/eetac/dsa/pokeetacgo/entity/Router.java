package edu.upc.eetac.dsa.pokeetacgo.entity;

/**
 * Created by ericmassip on 30/11/16.
 */
public class Router {
    private int id;
    private String BSSID;
    private int floor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }
}
