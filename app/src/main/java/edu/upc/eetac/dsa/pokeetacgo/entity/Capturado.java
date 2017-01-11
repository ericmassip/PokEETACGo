package edu.upc.eetac.dsa.pokeetacgo.entity;

import java.util.Calendar;

public class Capturado {
    private int id;
    private int idUser;
    private User user;
    private int idProfemon;
    private Profemon profemon;
    private int idLocation;
    private Location location;
    private Calendar date;
    private int level;
    private boolean isSuccessful;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getIdProfemon() {
        return idProfemon;
    }

    public void setIdProfemon(int idProfemon) {
        this.idProfemon = idProfemon;
    }

    public Profemon getProfemon() {
        return profemon;
    }

    public void setProfemon(Profemon profemon) {
        this.profemon = profemon;
    }

    public int getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(int idLocation) {
        this.idLocation = idLocation;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean getIsSuccessful() {
        return isSuccessful;
    }

    public void setIsSuccessful(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }
}
