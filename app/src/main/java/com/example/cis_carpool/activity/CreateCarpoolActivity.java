package com.example.cis_carpool.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cis_carpool.FirebaseConnection;
import com.example.cis_carpool.R;
import com.example.cis_carpool.Utils;
import com.example.cis_carpool.abstraction.FirebaseResponseFragmentActivityListener;
import com.example.cis_carpool.data.User;
import com.example.cis_carpool.data.Vehicle;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class represents the activity that asks for details when creating a new carpool.
 * For details on the function of each override method, check FirebaseResponseFragmentActivityListener.java for details.
 * @author joshuachasnov
 * @version 0.1
 */
public class CreateCarpoolActivity extends FirebaseResponseFragmentActivityListener {
    private EditText capacityEditText, carTypeEditText;
    private CheckBox openCheckBox;

    private Vehicle vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_carpool);

        capacityEditText = (EditText) findViewById(R.id.capacity_form);
        carTypeEditText = (EditText) findViewById(R.id.type_form);
        openCheckBox = (CheckBox) findViewById(R.id.open_checkbox);
    }

    @Override
    public void onSetVehicleSuccessful() {
        FirebaseConnection.getUserFromDocumentReference(this, FirebaseConnection.getDocumentReference("users/" + FirebaseConnection.ACTIVE_USER_UID));
    }

    @Override
    public void onSetVehicleFail() {
        Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetUserSuccessful(User user) {
        List<DocumentReference> vehicles = user.getVehicles();
        vehicles.add(FirebaseConnection.getDocumentReference("vehicles/" + vehicle.getID()));
        user.setVehicles(vehicles);
        FirebaseConnection.setUser(this, user);
    }

    @Override
    public void onSetUserSuccessful() {
        Toast.makeText(getApplicationContext(), "Successfully created carpool.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, UserDashboardActivity.class);
        startActivity(intent);
    }

    @Override
    public void onGetUserFail() {
        Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSetUserFail() {
        Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorReceived(Exception e) {
        Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    public void onCreatePressed(View view) {
        // check if fields are filled
        if (Utils.strip(capacityEditText.getText().toString()).equals("")) {
            Toast.makeText(getApplicationContext(), "Not all fields filled.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Utils.strip(carTypeEditText.getText().toString()).equals("")) {
            Toast.makeText(getApplicationContext(), "Not all fields filled.", Toast.LENGTH_SHORT).show();
            return;
        }

        // create vehicle to be added to database
        Intent intent = getIntent();
        double locationLat = intent.getDoubleExtra("locationLat", -1.0d);
        double locationLng = intent.getDoubleExtra("locationLng", -1.0d);
        double destinationLat = intent.getDoubleExtra("destinationLat", -1.0d);
        double destinationLng = intent.getDoubleExtra("destinationLng", -1.0d);

        ArrayList<DocumentReference> members = new ArrayList<>();
        members.add(FirebaseConnection.getDocumentReference("users/" + FirebaseConnection.ACTIVE_USER_UID));
        vehicle = new Vehicle(
                UUID.randomUUID().toString(),
                members.get(0),
                members,
                Utils.strip(carTypeEditText.getText().toString()),
                Integer.parseInt(capacityEditText.getText().toString()),
                1,
                locationLat, locationLng,
                destinationLat, destinationLng,
                openCheckBox.isChecked()
        );

        FirebaseConnection.setVehicle(this, vehicle);
    }
}