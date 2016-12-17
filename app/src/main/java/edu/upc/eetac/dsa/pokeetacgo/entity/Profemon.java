package edu.upc.eetac.dsa.pokeetacgo.entity;

/**
 * Created by ericmassip on 30/11/16.
 */
public class Profemon {
    private int id;
    private String name;
    private int initialLevel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getInitialLevel() {
        return initialLevel;
    }

    public void setInitialLevel(int initialLevel) {
        this.initialLevel = initialLevel;
    }
}
