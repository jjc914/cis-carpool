package com.example.cis_carpool.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cis_carpool.FirebaseConnection;
import com.example.cis_carpool.R;
import com.example.cis_carpool.abstraction.FirebaseResponseFragmentActivityListener;

/**
 * This class represents the activity that asks for login details.
 * For details on the function of each override method, check FirebaseResponseFragmentActivityListener.java for details.
 * @author joshuachasnov
 * @version 0.1
 */
public class LoginActivity extends FirebaseResponseFragmentActivityListener {
    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = (EditText) findViewById(R.id.email_form);
        passwordEditText = (EditText) findViewById(R.id.password_form);

        FirebaseConnection.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onErrorReceived(Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onLoginSuccessful() {
        Toast.makeText(getApplicationContext(), "Successfully logged in!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, UserDashboardActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLoginFail() {
        Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    public void onLoginPressed(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        // check for empty fields
        if (email.equals("") || password.equals("")) {
            onLoginFail();
            return;
        }

        FirebaseConnection.loginUser(this, email, password);
    }

    public void onGoToRegisterPage(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}