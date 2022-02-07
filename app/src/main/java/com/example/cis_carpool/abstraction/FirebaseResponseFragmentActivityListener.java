package com.example.cis_carpool.abstraction;

import androidx.fragment.app.FragmentActivity;

import com.example.cis_carpool.data.User;
import com.example.cis_carpool.data.Vehicle;
import com.google.firebase.auth.AuthResult;

/**
 * This class holds all callback functions from FirebaseConnection.java.
 * It is to be implemented instead of FragmentActivity.
 * @author joshuachasnov
 * @version 0.1
 */
public abstract class FirebaseResponseFragmentActivityListener extends FragmentActivity {
    /**
     * This function is called when an error is received.
     * @param e the exception received.
     */
    public abstract void onErrorReceived(Exception e);

    /**
     * This function is called when login is successful.
     * @throws NoSuchMethodException when the method is not overwritten, but is called to.
     */
    public void onLoginSuccessful() throws NoSuchMethodException {
        throw new NoSuchMethodException();
    }

    /**
     * This function is called when login fails.
     * @throws NoSuchMethodException when the method is not overwritten, but is called to.
     */
    public void onLoginFail() throws NoSuchMethodException {
        throw new NoSuchMethodException();
    }

    /**
     * This function is called when registration fails.
     * @param result the authetication result.
     * @throws NoSuchMethodException when the method is not overwritten, but is called to.
     */
    public void onRegisterSuccessful(AuthResult result) throws NoSuchMethodException {
        throw new NoSuchMethodException();
    }

    /**
     * This function is called when registration fails.
     * @throws NoSuchMethodException when the method is not overwritten, but is called to.
     */
    public void onRegisterFail() throws NoSuchMethodException {
        throw new NoSuchMethodException();
    }

    /**
     * This function is called when getting a vehicle is successful.
     * @param vehicle the vehicle gotten.
     * @throws NoSuchMethodException when the method is not overwritten, but is called to.
     */
    public void onGetVehicleSuccessful(Vehicle vehicle) throws NoSuchMethodException {
        throw new NoSuchMethodException();
    }

    /**
     * This function is called when getting vehicle fails.
     * @throws NoSuchMethodException when the method is not overwritten, but is called to.
     */
    public void onGetVehicleFail() throws NoSuchMethodException {
        throw new NoSuchMethodException();
    }

    /**
     * This function is called when getting a user is successful.
     * @param user the user gotten.
     * @throws NoSuchMethodException when the method is not overwritten, but is called to.
     */
    public void onGetUserSuccessful(User user) throws NoSuchMethodException {
        throw new NoSuchMethodException();
    }

    /**
     * This function is called when getting user fails.
     * @throws NoSuchMethodException when the method is not overwritten, but is called to.
     */
    public void onGetUserFail() throws NoSuchMethodException {
        throw new NoSuchMethodException();
    }

    /**
     * This function is called when setting a vehicle is successful.
     * @throws NoSuchMethodException when the method is not overwritten, but is called to.
     */
    public void onSetVehicleSuccessful() throws NoSuchMethodException {
        throw new NoSuchMethodException();
    }

    /**
     * This function is called when setting vehicle fails.
     * @throws NoSuchMethodException when the method is not overwritten, but is called to.
     */
    public void onSetVehicleFail() throws NoSuchMethodException {
        throw new NoSuchMethodException();
    }

    /**
     * This function is called when setting a user is successful.
     * @throws NoSuchMethodException when the method is not overwritten, but is called to.
     */
    public void onSetUserSuccessful() throws NoSuchMethodException {
        throw new NoSuchMethodException();
    }

    /**
     * This function is called when setting user fails.
     * @throws NoSuchMethodException when the method is not overwritten, but is called to.
     */
    public void onSetUserFail() throws NoSuchMethodException {
        throw new NoSuchMethodException();
    }
}
