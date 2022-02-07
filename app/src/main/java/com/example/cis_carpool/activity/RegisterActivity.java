package com.example.cis_carpool.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.cis_carpool.FirebaseConnection;
import com.example.cis_carpool.R;
import com.example.cis_carpool.abstraction.FirebaseResponseFragmentActivityListener;
import com.example.cis_carpool.data.User;
import com.google.firebase.auth.AuthResult;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents the activity that asks for registration details.
 * For details on the function of each override method, check FirebaseResponseFragmentActivityListener.java for details.
 * @author joshuachasnov
 * @version 0.1
 */
public class RegisterActivity extends FirebaseResponseFragmentActivityListener {
    private EditText emailEditText,
            passwordEditText, passwordConfirmEditText,
            firstNameEditText, lastNameEditText;
    private Spinner typeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = (EditText) findViewById(R.id.email_form);
        passwordEditText = (EditText) findViewById(R.id.password_form);
        passwordConfirmEditText = (EditText) findViewById(R.id.password_confirm_form);
        firstNameEditText = (EditText) findViewById(R.id.name_first_form);
        lastNameEditText = (EditText) findViewById(R.id.name_last_form);

        // init dropdown
        typeSpinner = (Spinner) findViewById(R.id.type_form);
        String[] types = { "Student", "Teacher", "Parent", "Staff" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, types);
        typeSpinner.setAdapter(adapter);
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
    public void onRegisterSuccessful(AuthResult result) {
        // create user in database
        FirebaseConnection.setUser(this, new User(result.getUser().getUid(),
                lastNameEditText.getText().toString(),
                firstNameEditText.getText().toString(),
                typeSpinner.getSelectedItem().toString().toLowerCase(),
                new ArrayList<>(),
                new ArrayList<>()));
    }

    @Override
    public void onRegisterFail() {
        Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSetUserSuccessful() {
        Toast.makeText(getApplicationContext(), "Successfully registered!", Toast.LENGTH_SHORT).show();
        onGoToLoginPage(null);
    }

    @Override
    public void onSetUserFail() {
        Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    public void onRegisterPressed(View view) {
        // check if passwords are the same
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (!password.equals(passwordConfirmEditText.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Passwords not the same.", Toast.LENGTH_SHORT).show();
            return;
        }
        // check if email is cis email. not a great way to do this but it works most of the time.
        if (!email.contains("cis.edu.hk")) {
            Toast.makeText(getApplicationContext(), "Must be a CIS affiliated email.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseConnection.registerUser(this, email, password);
    }

    public void onGoToLoginPage(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}