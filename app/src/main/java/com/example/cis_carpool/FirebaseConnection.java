package com.example.cis_carpool;

import androidx.annotation.NonNull;

import com.example.cis_carpool.abstraction.FirebaseResponseFragmentActivityListener;
import com.example.cis_carpool.data.User;
import com.example.cis_carpool.data.Vehicle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * This class manages the app's connection to Firebase.
 * @author joshuachasnov
 * @version 0.1
 */
public class FirebaseConnection {
    private static FirebaseAuth FIREBASE_AUTH;
    private static FirebaseFirestore FIREBASE_FIRESTORE;
    public static String ACTIVE_USER_UID = null;
    public static User ACTIVE_USER = null;

    /**
     * Connects to Firebase.
     */
    public static void connect() {
        if (FIREBASE_AUTH == null) {
            FIREBASE_AUTH = FirebaseAuth.getInstance();
        }
        if (FIREBASE_FIRESTORE == null) {
            FIREBASE_FIRESTORE = FirebaseFirestore.getInstance();
        }
    }

    /**
     * Logs user in using Firebase auth.
     * @param listener the on finished listener
     * @param email the user email
     * @param password the user password
     */
    public static void loginUser(FirebaseResponseFragmentActivityListener listener, String email, String password) {
        FIREBASE_AUTH.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            if (task.isSuccessful()) {
                                ACTIVE_USER_UID = task.getResult().getUser().getUid();
                                listener.onLoginSuccessful();
                            } else {
                                listener.onLoginFail();
                            }
                        } catch (NoSuchMethodException e) {
                            listener.onErrorReceived(e);
                        }
                    }
                });
    }

    /**
     * Registers user using Firebase auth.
     * @param listener the on finished listener
     * @param email the user email
     * @param password the user password
     */
    public static void registerUser(FirebaseResponseFragmentActivityListener listener, String email, String password) {
        FIREBASE_AUTH.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            if (task.isSuccessful()) {
                                listener.onRegisterSuccessful(task.getResult());
                            } else {
                                listener.onRegisterFail();
                            }
                        } catch (NoSuchMethodException e) {
                            listener.onErrorReceived(e);
                        }
                    }
                });
    }

    /**
     * Gets vehicle where the vehicle field is the value.
     * @param listener the on finished listener
     * @param field the specified field
     * @param value the specified value
     */
    public static void getVehicleMatch(FirebaseResponseFragmentActivityListener listener, String field, Object value) {
        FIREBASE_FIRESTORE.collection("vehicles")
                .whereEqualTo(field, value)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        try {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot qds : task.getResult()) {
                                    Vehicle vehicle = qds.toObject(Vehicle.class);
                                    listener.onGetVehicleSuccessful(vehicle);
                                }
                            } else {
                                listener.onGetVehicleFail();
                            }
                        } catch (NoSuchMethodException e) {
                            listener.onErrorReceived(e);
                        }
                    }
                });
    }

    /**
     * Gets user data from a documentreference.
     * @param listener the on finished listener
     * @param reference the document reference
     */
    public static void getUserFromDocumentReference(FirebaseResponseFragmentActivityListener listener, DocumentReference reference) {
        reference.get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                try {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        listener.onGetUserSuccessful(user);
                    } else {
                        listener.onGetUserFail();
                    }
                } catch (NoSuchMethodException e) {
                    listener.onErrorReceived(e);
                }
            }
        });
    }

    /**
     * Stores vehicle in Firebase
     * @param listener the on finished listener
     * @param vehicle the vehicle
     */
    public static void setVehicle(FirebaseResponseFragmentActivityListener listener, Vehicle vehicle) {
        FIREBASE_FIRESTORE.collection("vehicles")
                .document(vehicle.getID())
                .set(vehicle)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        try {
                            if (task.isSuccessful()) {
                                listener.onSetVehicleSuccessful();
                            } else {
                                listener.onSetVehicleFail();
                            }
                        } catch (NoSuchMethodException e) {
                            listener.onErrorReceived(e);
                        }
                    }
                });
    }

    /**
     * Stores user in Firebase
     * @param listener the on finished listener
     * @param user the user
     */
    public static void setUser(FirebaseResponseFragmentActivityListener listener, User user) {
        FIREBASE_FIRESTORE.collection("users")
                .document(user.getId())
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        try {
                            if (task.isSuccessful()) {
                                listener.onSetUserSuccessful();
                            } else {
                                listener.onSetUserFail();
                            }
                        } catch (NoSuchMethodException e) {
                            listener.onErrorReceived(e);
                        }
                    }
                });
    }

    /**
     * Gets a document reference from a string path.
     * @param path the path
     * @return the document reference
     */
    public static DocumentReference getDocumentReference(String path) {
        return FIREBASE_FIRESTORE.document(path);
    }
}
