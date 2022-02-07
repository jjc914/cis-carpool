package com.example.cis_carpool.data;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

/**
 * This class is the holder for all user data taken and provided to the database.
 * It mostly consists of getters and setters.
 * @author joshuachasnov
 * @version 0.1
 */
public class User {
    private String id;
    private String nameFamily;
    private String nameGiven;
    private String type;
    private List<DocumentReference> vehicles;
    private List<DocumentReference> memberVehicles;

    public User() {
    }

    public User(String id, String nameFamily, String nameGiven, String type, List<DocumentReference> vehicles, List<DocumentReference> memberVehicles) {
        this.id = id;
        this.nameFamily = nameFamily;
        this.nameGiven = nameGiven;
        this.type = type;
        this.vehicles = vehicles;
        this.memberVehicles = memberVehicles;
    }

    public List<DocumentReference> getMemberVehicles() {
        return memberVehicles;
    }

    public void setMemberVehicles(List<DocumentReference> memberVehicles) {
        this.memberVehicles = memberVehicles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameFamily() {
        return nameFamily;
    }

    public void setNameFamily(String nameFamily) {
        this.nameFamily = nameFamily;
    }

    public String getNameGiven() {
        return nameGiven;
    }

    public void setNameGiven(String nameGiven) {
        this.nameGiven = nameGiven;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DocumentReference> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<DocumentReference> vehicles) {
        this.vehicles = vehicles;
    }
}
