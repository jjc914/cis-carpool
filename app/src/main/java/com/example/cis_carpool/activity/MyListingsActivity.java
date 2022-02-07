package com.example.cis_carpool.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.cis_carpool.FirebaseConnection;
import com.example.cis_carpool.ListingRecyclerViewAdapter;
import com.example.cis_carpool.R;
import com.example.cis_carpool.abstraction.FirebaseResponseFragmentActivityListener;
import com.example.cis_carpool.data.User;
import com.example.cis_carpool.data.Vehicle;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents the activity that shows the listings that the user has created.
 * For details on the function of each override method, check FirebaseResponseFragmentActivityListener.java for details.
 * @author joshuachasnov
 * @version 0.1
 */
public class MyListingsActivity extends FirebaseResponseFragmentActivityListener implements ListingRecyclerViewAdapter.ItemClickListener {
    private ListingRecyclerViewAdapter adapter;

    private DocumentReference vehicleDocumentReference;
    private User user;
    private HashMap<String, Integer> vehiclesIndex;
    private int k = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_listings);

        FirebaseConnection.getUserFromDocumentReference(this, FirebaseConnection.getDocumentReference("users/" + FirebaseConnection.ACTIVE_USER_UID));
    }

    @Override
    public void onItemClick(View view, int position) {
        List<DocumentReference> vehicles = user.getVehicles();
        vehicleDocumentReference = vehicles.get(position);
        FirebaseConnection.getVehicleMatch(this, "id", vehicleDocumentReference.getId());
    }

    @Override
    public void onErrorReceived(Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onGetVehicleSuccessful(Vehicle vehicle) {
        if (k < user.getVehicles().size()) {
            adapter.setItem(vehiclesIndex.get(vehicle.getID()), vehicle.getType());
            adapter.notifyItemChanged(vehiclesIndex.get(vehicle.getID()));
            k++;
        } else {
            vehicle.setOpen(!vehicle.isOpen());
            if (vehicle.isOpen())
                Toast.makeText(getApplicationContext(), "Opened vehicle.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "Closed vehicle.", Toast.LENGTH_SHORT).show();
            FirebaseConnection.setVehicle(this, vehicle);
        }
    }

    @Override
    public void onGetVehicleFail() {
        Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSetVehicleSuccessful() {

    }

    @Override
    public void onSetVehicleFail() {
        Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetUserSuccessful(User user) {
        this.user = user;
        vehiclesIndex = new HashMap<>();
        // get recyclerview data
        ArrayList<String> vehicles = new ArrayList<>();
        for (int i = 0; i < user.getVehicles().size(); i++) {
            vehicles.add(user.getVehicles().get(i).getId());
            vehiclesIndex.put(user.getVehicles().get(i).getId(), i);
            FirebaseConnection.getVehicleMatch(this, "id", user.getVehicles().get(i).getId());
        }
        // init recyclerview
        RecyclerView recyclerView = findViewById(R.id.listing_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListingRecyclerViewAdapter(this, vehicles);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }
}