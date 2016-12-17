package edu.upc.eetac.dsa.pokeetacgo.entity;

import java.util.List;

/**
 * Created by ericmassip on 12/11/16.
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private boolean isAdmin;

//    private List<Profemon> profemons;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

//    public List<Profemon> getProfemons() {
//        return profemons;
//    }

//    public void setProfemons(List<Profemon> profemons) {
//        this.profemons = profemons;
//    }
}
