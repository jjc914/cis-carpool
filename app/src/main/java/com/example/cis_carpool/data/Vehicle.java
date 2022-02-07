package com.example.cis_carpool.data;

import com.example.cis_carpool.FirebaseConnection;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.model.DocumentKey;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the holder for all vehicle data taken and provided to the database.
 * It mostly consists of getters and setters.
 * @author joshuachasnov
 * @version 0.1
 */
public class Vehicle {
    private String id;
    private DocumentReference owner;
    private List<DocumentReference> members;
    private String type;
    private int capacityTotal;
    private int capacityOccupied;
    private double locationLat;
    private double locationLng;
    private double destinationLat;
    private double destinationLng;
    private boolean open;

    public Vehicle(String id, DocumentReference owner, List<DocumentReference> members, String type, int capacityTotal, int capacityOccupied, double locationLat, double locationLng, double destinationLat, double destinationLng, boolean open) {
        this.id = id;
        this.owner = owner;
        this.members = members;
        this.type = type;
        this.capacityTotal = capacityTotal;
        this.capacityOccupied = capacityOccupied;
        this.locationLat = locationLat;
        this.locationLng = locationLng;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
        this.open = open;
    }

    public Vehicle() {
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public DocumentReference getOwner() {
        return owner;
    }

    public void setOwner(DocumentReference owner) {
        this.owner = owner;
    }

    public void setOwnerFromPath(String path) {
        owner = FirebaseConnection.getDocumentReference(path);
    }

    public List<DocumentReference> getMembers() {
        return members;
    }

    public void setMembers(List<DocumentReference> members) {
        this.members = members;
    }

    public void addMember(String uid) {
        if (members == null) members = new ArrayList<>();
        DocumentReference userDocumentReference = FirebaseConnection.getDocumentReference("users/" + uid);
        members.add(userDocumentReference);
    }

    public boolean containsMember(String uid) {
        if (members == null) return false;
        return members.contains(FirebaseConnection.getDocumentReference("users/" + uid));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCapacityTotal() {
        return capacityTotal;
    }

    public void setCapacityTotal(int capacityTotal) {
        this.capacityTotal = capacityTotal;
    }

    public int getCapacityOccupied() {
        return capacityOccupied;
    }

    public void setCapacityOccupied(int capacityOccupied) {
        this.capacityOccupied = capacityOccupied;
    }

    public double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(double locationLat) {
        this.locationLat = locationLat;
    }

    public double getLocationLng() {
        return locationLng;
    }

    public void setLocationLng(double locationLng) {
        this.locationLng = locationLng;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public double getDestinationLng() {
        return destinationLng;
    }

    public void setDestinationLng(double destinationLng) {
        this.destinationLng = destinationLng;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", owner='" + owner.getPath() + '\'' +
                ", type='" + type + '\'' +
                ", capacityTotal=" + capacityTotal +
                ", capacityOccupied=" + capacityOccupied +
                ", locationLat=" + locationLat +
                ", locationLng=" + locationLng +
                ", destinationLat=" + destinationLat +
                ", destinationLng=" + destinationLng +
                ", open=" + open +
                '}';
    }
}
