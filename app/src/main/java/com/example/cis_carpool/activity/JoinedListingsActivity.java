package com.example.cis_carpool.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.cis_carpool.FirebaseConnection;
import com.example.cis_carpool.ListingRecyclerViewAdapter;
import com.example.cis_carpool.R;
import com.example.cis_carpool.abstraction.FirebaseResponseFragmentActivityListener;
import com.example.cis_carpool.data.User;
import com.example.cis_carpool.data.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represents the activity that shows the listings that the user has joined.
 * For details on the function of each override method, check FirebaseResponseFragmentActivityListener.java for details.
 * @author joshuachasnov
 * @version 0.1
 */
public class JoinedListingsActivity extends FirebaseResponseFragmentActivityListener implements ListingRecyclerViewAdapter.ItemClickListener {
    private ListingRecyclerViewAdapter adapter;

    private HashMap<String, Integer> vehiclesIndex;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_listings);

        // get current user
        FirebaseConnection.getUserFromDocumentReference(this, FirebaseConnection.getDocumentReference("users/" + FirebaseConnection.ACTIVE_USER_UID));
    }

    @Override
    public void onErrorReceived(Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onGetVehicleSuccessful(Vehicle vehicle) {
        // update recyclerview adapter
        adapter.setItem(vehiclesIndex.get(vehicle.getID()), vehicle.getType());
        adapter.notifyItemChanged(vehiclesIndex.get(vehicle.getID()));
    }

    @Override
    public void onGetVehicleFail() {
        Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetUserSuccessful(User user) {
        this.user = user;
        vehiclesIndex = new HashMap<>();
        // add user member vehicles to recyclerview, saving their indices by their uuids.
        ArrayList<String> vehicles = new ArrayList<>();
        for (int i = 0; i < user.getMemberVehicles().size(); i++) {
            vehicles.add(user.getMemberVehicles().get(i).getId());
            vehiclesIndex.put(user.getMemberVehicles().get(i).getId(), i);
            FirebaseConnection.getVehicleMatch(this, "id", user.getMemberVehicles().get(i).getId());
        }
        // init recyclerview
        RecyclerView recyclerView = findViewById(R.id.listing_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListingRecyclerViewAdapter(this, vehicles);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) { }
}