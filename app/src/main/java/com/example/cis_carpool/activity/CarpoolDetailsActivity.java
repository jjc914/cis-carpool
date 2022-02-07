package com.example.cis_carpool.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cis_carpool.FirebaseConnection;
import com.example.cis_carpool.R;
import com.example.cis_carpool.abstraction.FirebaseResponseFragmentActivityListener;
import com.example.cis_carpool.data.User;
import com.example.cis_carpool.data.Vehicle;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.List;

/**
 * This class represents the activity that shows carpool details and confirmation when joining a carpool.
 * For details on the function of each override method, check FirebaseResponseFragmentActivityListener.java for details.
 * @author joshuachasnov
 * @version 0.1
 */
public class CarpoolDetailsActivity extends FirebaseResponseFragmentActivityListener {
    private Vehicle vehicle;

    private boolean init;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpool_details);
        // get vehicle from intent
        Intent intent = getIntent();
        vehicle = new Vehicle();
        vehicle.setID(intent.getStringExtra("id"));
        vehicle.setOwnerFromPath(intent.getStringExtra("owner"));
        for (String e : intent.getStringArrayListExtra("members")) {
            vehicle.addMember(e);
        }
        vehicle.setType(intent.getStringExtra("type"));
        vehicle.setCapacityTotal(intent.getIntExtra("capacityTotal", -1));
        vehicle.setCapacityOccupied(intent.getIntExtra("capacityOccupied", -1));
        vehicle.setLocationLat(intent.getDoubleExtra("locationLat", -1.0));
        vehicle.setLocationLng(intent.getDoubleExtra("locationLng", -1.0));
        vehicle.setDestinationLat(intent.getDoubleExtra("destinationLat", -1.0));
        vehicle.setDestinationLng(intent.getDoubleExtra("destinationLng", -1.0));
        vehicle.setOpen(intent.getBooleanExtra("open", true));

        // set visible data
        ((TextView)findViewById(R.id.car_type)).setText(vehicle.getType());
        ((TextView)findViewById(R.id.car_remaining_seats)).setText("Car capacity: " + vehicle.getCapacityOccupied() + "/" + vehicle.getCapacityTotal());
        FirebaseConnection.getUserFromDocumentReference(this, vehicle.getOwner());

        init = true;
    }

    @Override
    public void onGetUserSuccessful(User user) {
        if (init) {
            // initializing
            ((TextView)findViewById(R.id.car_owner_name)).setText(user.getNameGiven() + " " + user.getNameFamily() + "'s Carpool");
            init = false;
        } else {
            // update data
            List<DocumentReference> vehicles = user.getMemberVehicles();
            vehicles.add(FirebaseConnection.getDocumentReference("vehicles/" + vehicle.getID()));
            user.setMemberVehicles(vehicles);
            FirebaseConnection.setUser(this, user);
        }
    }

    @Override
    public void onGetUserFail() {
        Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSetVehicleSuccessful() {
        FirebaseConnection.getUserFromDocumentReference(this,
                FirebaseConnection.getDocumentReference("users/" + FirebaseConnection.ACTIVE_USER_UID));
    }

    @Override
    public void onSetVehicleFail() {
        Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSetUserSuccessful() {
        Toast.makeText(getApplicationContext(), "Successfully joined!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, UserDashboardActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSetUserFail() {
        Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorReceived(Exception e) {
        e.printStackTrace();
    }

    /**
     * Called when confirm button clicked
     * @param view the current view
     */
    public void onConfirmBooking(View view) {
        if (vehicle.containsMember(FirebaseConnection.ACTIVE_USER_UID)) {
            Toast.makeText(getApplicationContext(), "Already in the carpool.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (vehicle.getCapacityOccupied() >= vehicle.getCapacityTotal()) {
            Toast.makeText(getApplicationContext(), "Carpool full.", Toast.LENGTH_SHORT).show();
            return;
        }
        vehicle.addMember(FirebaseConnection.ACTIVE_USER_UID);
        vehicle.setCapacityOccupied(vehicle.getCapacityOccupied() + 1);
        FirebaseConnection.setVehicle(this, vehicle);
    }
}