package com.example.cis_carpool.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cis_carpool.FirebaseConnection;
import com.example.cis_carpool.R;
import com.example.cis_carpool.abstraction.FirebaseResponseFragmentActivityListener;
import com.example.cis_carpool.data.User;

/**
 * This class represents the activity that the user can use to navigate.
 * For details on the function of each override method, check FirebaseResponseFragmentActivityListener.java for details.
 * @author joshuachasnov
 * @version 0.1
 */
public class UserDashboardActivity extends FirebaseResponseFragmentActivityListener {
    private TextView greetingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        greetingTextView = findViewById(R.id.name_greeting);

        FirebaseConnection.getUserFromDocumentReference(this, FirebaseConnection.getDocumentReference("users/" + FirebaseConnection.ACTIVE_USER_UID));
    }

    @Override
    public void onGetUserSuccessful(User user) {
        greetingTextView.setText("Hello, " + user.getNameGiven() + "!");
    }

    @Override
    public void onGetUserFail() {
        Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorReceived(Exception e) {
        e.printStackTrace();
    }

    /**
     * Open the Google Map viewer.
     * @param view the current view
     */
    public void onSeeMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    /**
     * Open the user's listings viewer.
     * @param view the current view
     */
    public void onSeeListings(View view) {
        Intent intent = new Intent(this, MyListingsActivity.class);
        startActivity(intent);
    }

    /**
     * Open the user's joined carpools viewer.
     * @param view the current view
     */
    public void onSeeCarpools(View view) {
        Intent intent = new Intent(this, JoinedListingsActivity.class);
        startActivity(intent);
    }
}