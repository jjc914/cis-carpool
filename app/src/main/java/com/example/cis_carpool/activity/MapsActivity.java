package com.example.cis_carpool.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cis_carpool.DownloadRunnable;
import com.example.cis_carpool.FirebaseConnection;
import com.example.cis_carpool.Utils;
import com.example.cis_carpool.data.Vehicle;
import com.example.cis_carpool.maps.MapsAutocompleteRecyclerViewAdapter;
import com.example.cis_carpool.maps.MapsUIManager;
import com.example.cis_carpool.R;
import com.example.cis_carpool.abstraction.FirebaseResponseFragmentActivityListener;
import com.example.cis_carpool.abstraction.ParameterizedRunnable;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * This class represents the activity that contains the map for selecting journey details.
 * It handles all Google Maps API calls and Open Route Service API calls.
 * For details on the function of each override method, check FirebaseResponseFragmentActivityListener.java for details.
 * @author joshuachasnov
 * @version 0.1
 */
public class MapsActivity extends FirebaseResponseFragmentActivityListener implements OnMapReadyCallback, OnMapLoadedCallback, MapsAutocompleteRecyclerViewAdapter.ItemClickListener {
    private static final String TAG = "MapsActivity";

    private GoogleMap map;
    private MapsUIManager mapsUIManager;
    private FusedLocationProviderClient fusedLocationClient;

    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private Marker locationMarker;
    private Circle locationAccuracyCircle;
    private Marker originMarker;
    private Marker destinationMarker;
    private Polyline walkingRoute;
    private Polyline drivingRoute;

    private ArrayList<Vehicle> vehicles;
    private Vehicle activeVehicle;

    private int screenWidth;
    private int screenHeight;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_root);

        // get screen width and height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        // create google map
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapsUIManager = new MapsUIManager(this);

        // when the user's location is received
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                // convert location to latlng
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                System.out.println(locationResult.getLastLocation().getAccuracy());
                if (locationMarker == null) {
                    // if user's location marker not created yet, create it
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(ll)
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.fromBitmap(
                                    Utils.resize(BitmapFactory.decodeResource(getResources(),
                                            R.drawable.my_location_icon),
                                            screenWidth / 28))
                            ));
                    locationAccuracyCircle = map.addCircle(new CircleOptions()
                            .center(ll)
                            .radius(location.getAccuracy())
                            .fillColor(Color.argb(50, 61, 161, 255))
                            .strokeColor(Color.TRANSPARENT));
                } else {
                    // set user's location marker to current location
                    locationMarker.setPosition(ll);
                    locationAccuracyCircle.setCenter(ll);
                    locationAccuracyCircle.setRadius(location.getAccuracy());
                }
            }

            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };

        checkAppPermissions();
    }

    // location functions
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        googleMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onMapLoaded() {
        if (map == null) return;
        FirebaseConnection.connect();
        FirebaseConnection.getVehicleMatch(this, "open", true);
    }

    /**
     * Initializes location listener that keeps track of user location.
     */
    // permissions handled already elsewhere
    @SuppressLint("MissingPermission")
    private void initializeLocationListening() {
        // start location requests
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Initialized Google Maps settings.
     */
    @SuppressLint("MissingPermission")
    private void initializeMapSettings() {
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setTiltGesturesEnabled(false);
        map.getUiSettings().setIndoorLevelPickerEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fusedLocationClient == null) return;
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    /**
     * Starts user location updates.
     */
    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {
        if (fusedLocationClient == null) return;
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissions granted, yay! continue
                    initializeLocationListening();
                    initializeMapSettings();
                } else {
                    // this is terrible. idc. don't feel like handling errors n stuff when location is not allowed.
                    Toast.makeText(this, "Location is a required permission for this app.",
                            Toast.LENGTH_LONG).show();
                    System.exit(0);
                }
            }
            break;
            default:
                break;
        }
    }

    // firebase listener functions
    @Override
    public void onGetVehicleSuccessful(Vehicle vehicle) {
        // show vehicle locations on map
        if (vehicles == null) vehicles = new ArrayList<>();
        LatLng coordinates = new LatLng(vehicle.getLocationLat(), vehicle.getLocationLng());
        MarkerOptions options = new MarkerOptions()
                .position(coordinates)
                .anchor(0.5f, 0.5f)
                .title(vehicle.getID())
                .icon(BitmapDescriptorFactory.fromBitmap(
                        Utils.resize(BitmapFactory.decodeResource(getResources(), R.drawable.car_icon), screenWidth / 12))
                );
        map.addMarker(options);
        vehicles.add(vehicle);
    }

    @Override
    public void onGetVehicleFail() throws NoSuchMethodException {
        System.out.println("Could not get vehicle");
    }

    @Override
    public void onErrorReceived(Exception e) {
        Toast.makeText(this, "Something went wrong.", Toast.LENGTH_LONG).show();
    }

    // recyclerview listener functions
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(View view, MapsAutocompleteRecyclerViewAdapter adapter, int position) {
        if (adapter.getCoordinates(position) == null) {
            if (destinationMarker == null && mapsUIManager.isOrigin()) {
                if (originMarker != null) {
                    originMarker.remove();
                    originMarker = null;
                }
                mapsUIManager.closeSearch("", "");
            } else if (originMarker == null && !mapsUIManager.isOrigin()) {
                if (destinationMarker != null) {
                    destinationMarker.remove();
                    destinationMarker = null;
                }
                mapsUIManager.closeSearch("", "");
            } else if (mapsUIManager.isOrigin()) {
                if (originMarker != null) {
                    originMarker.remove();
                    originMarker = null;
                }
                mapsUIManager.closeSearch("", destinationMarker.getTitle());
            } else {
                if (destinationMarker != null) {
                    destinationMarker.remove();
                    destinationMarker = null;
                }
                mapsUIManager.closeSearch(originMarker.getTitle(), "");
            }
            if (drivingRoute != null) drivingRoute.remove();
            if (walkingRoute != null) walkingRoute.remove();
            mapsUIManager.hideJourneyOptions();
            setMapMovement(true);
            return;
        }
        if (destinationMarker == null && mapsUIManager.isOrigin()) {
            mapsUIManager.closeSearch(adapter.getName(position), "");
        } else if (originMarker == null && !mapsUIManager.isOrigin()) {
            mapsUIManager.closeSearch("", adapter.getName(position));
        } else if (mapsUIManager.isOrigin()) {
            mapsUIManager.closeSearch(adapter.getName(position), destinationMarker.getTitle());
        } else {
            mapsUIManager.closeSearch(originMarker.getTitle(), adapter.getName(position));
        }
        if (mapsUIManager.isOrigin()) {
            setOriginLocation(adapter.getCoordinates(position), adapter.getName(position));
        } else {
            setDestinationLocation(adapter.getCoordinates(position), adapter.getName(position));
        }
    }

    /**
     * Checks the app's permissions, acting accordingly depending on results.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkAppPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE }, 2);
        } else {
            initializeLocationListening();
        }
    }

    /**
     * Gets the origin point name.
     * @return name of the origin point.
     */
    public String getOriginName() {
        if (originMarker == null) return "";
        return originMarker.getTitle();
    }

    /**
     * Gets the destination point name.
     * @return name of the destination point.
     */
    public String getDestinationName() {
        if (destinationMarker == null) return "";
        return destinationMarker.getTitle();
    }

    /**
     * Gets the center of the map.
     * @return coordinates of the center of the map.
     */
    public LatLng getMapCenter() {
        return map.getCameraPosition().target;
    }

    /**
     * Sets if the map movement is locked.
     * @param locked to lock movement or not.
     */
    public void setMapMovement(boolean locked) {
        map.getUiSettings().setScrollGesturesEnabled(locked);
    }

    /**
     * Moves the camera to the user's location on the map.
     * @param view the current view
     */
    public void onGoToMyLocation(View view) {
        if (locationMarker == null) return;
        if (originMarker != null && destinationMarker != null) return;
        map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(locationMarker.getPosition())
                .zoom(15f)
                .build()));
    }

    /**
     * Starts CarpoolDetailsActivity.java activity.
     * @param view the current view.
     */
    public void onJoinCarpool(View view) {
        Intent intent = new Intent(this, CarpoolDetailsActivity.class);
        intent.putExtra("id", activeVehicle.getID());
        intent.putExtra("owner", activeVehicle.getOwner().getPath());
        ArrayList<String> membersString = new ArrayList<>();
        for (DocumentReference e : activeVehicle.getMembers()) {
            membersString.add(e.getId());
        }
        intent.putStringArrayListExtra("members", membersString);
        intent.putExtra("type", activeVehicle.getType());
        intent.putExtra("capacityTotal", activeVehicle.getCapacityTotal());
        intent.putExtra("capacityOccupied", activeVehicle.getCapacityOccupied());
        intent.putExtra("locationLat", activeVehicle.getLocationLat());
        intent.putExtra("locationLng", activeVehicle.getLocationLng());
        intent.putExtra("destinationLat", activeVehicle.getDestinationLat());
        intent.putExtra("destinationLng", activeVehicle.getDestinationLng());
        intent.putExtra("open", activeVehicle.isOpen());
        startActivity(intent);
    }

    /**
     * Starts CreateCarpoolActivity.java activity.
     * @param view the current view.
     */
    public void onCreateCarpool(View view) {
        Intent intent = new Intent(this, CreateCarpoolActivity.class);
        intent.putExtra("locationLat", drivingRoute.getPoints().get(0).latitude);
        intent.putExtra("locationLng", drivingRoute.getPoints().get(0).longitude);
        intent.putExtra("destinationLat", destinationMarker.getPosition().latitude);
        intent.putExtra("destinationLng", destinationMarker.getPosition().longitude);
        startActivity(intent);
    }

    /**
     * Sets the origin marker location.
     * @param coordinates coordinates of the location.
     * @param name name of the location.
     */
    private void setOriginLocation(LatLng coordinates, String name) {
        if (originMarker != null) {
            // reset marker
            originMarker.remove();
        }
        // create marker
        MarkerOptions options = new MarkerOptions()
                .position(coordinates)
                .title(name);
        originMarker = map.addMarker(options);
        map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(coordinates)
                .zoom(15f)
                .build()));
        // creates route if two locations selected
        checkTwoLocationsSelected();
    }

    /**
     * Sets the destination marker location.
     * @param coordinates coordinates of the location.
     * @param name name of the location.
     */
    private void setDestinationLocation(LatLng coordinates, String name) {
        if (destinationMarker != null) {
            // reset makrer
            destinationMarker.remove();
        }
        // create marker
        MarkerOptions options = new MarkerOptions()
                .position(coordinates)
                .title(name);
        destinationMarker = map.addMarker(options);
        map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(coordinates)
                .zoom(10f)
                .build()));
        // creates route if two locaations selected
        checkTwoLocationsSelected();
    }

    /**
     * This method checks if two locations are selected, and if they are, creates a route between them.
     */
    private void checkTwoLocationsSelected() {
        if (destinationMarker == null || originMarker == null) return;
        boolean hasCarpool = createRoute();
        mapsUIManager.openJourneyOptions(hasCarpool);
    }

    /**
     * This method creates a route between the origin and destination markers.
     * @return true if there's a valid carpool, else false.
     */
    private boolean createRoute() {
        // reset route
        if (walkingRoute != null) {
            walkingRoute.remove();
        }
        if (drivingRoute != null) {
            drivingRoute.remove();
        }
        // checks if valid carpool within minRideDistance km.
        double minRideDistance = 1d;
        LatLng minLatLng = null;
        if (vehicles != null) {
            for (int i = 0; i < vehicles.size(); i++) {
                LatLng org = originMarker.getPosition();
                LatLng des = new LatLng(vehicles.get(i).getLocationLat(), vehicles.get(i).getLocationLng());
                double distance = Utils.haversine(org.latitude, org.longitude, des.latitude, des.longitude);
                double distanceDest = Utils.haversine(vehicles.get(i).getDestinationLat(), vehicles.get(i).getDestinationLng(),
                        destinationMarker.getPosition().latitude,
                        destinationMarker.getPosition().longitude);
                if (distance < minRideDistance &&
                        distanceDest < minRideDistance &&
                        vehicles.get(i).getCapacityOccupied() < vehicles.get(i).getCapacityTotal()) {
                    activeVehicle = vehicles.get(i);
                    minRideDistance = distance;
                    minLatLng = des;
                }
            }
        }
        if (minLatLng == null) {
            // bring directly
            // gets route node data from OpenRouteService API
            String url = getDirectionsUrl("driving-car", originMarker.getPosition(), destinationMarker.getPosition());
            DownloadRunnable dr = new DownloadRunnable(url, getPathNodesRunnable(new ParameterizedRunnable() {
                @Override
                public void run(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // display polyline
                            displayDrivingPolylineOnMap((List<LatLng>) args[0], true);
                        }
                    });
                }
            }));
            Thread t = new Thread(dr);
            t.start();
            return false;
        } else {
            // bring through carpool
            // gets route node data from OpenRouteService API
            String urlcp = getDirectionsUrl("driving-car", minLatLng, destinationMarker.getPosition());
            DownloadRunnable drcp = new DownloadRunnable(urlcp, getPathNodesRunnable(new ParameterizedRunnable() {
                @Override
                public void run(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // display polyline
                            displayDrivingPolylineOnMap((List<LatLng>) args[0], true);
                        }
                    });
                }
            }));
            Thread tcp = new Thread(drcp);
            tcp.start();

            // get walking route node data
            String urlwk = getDirectionsUrl("foot-walking", originMarker.getPosition(), minLatLng);
            DownloadRunnable drwk = new DownloadRunnable(urlwk, getPathNodesRunnable(new ParameterizedRunnable() {
                @Override
                public void run(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // display polyline
                            displayWalkingPolylineOnMap((List<LatLng>) args[0], false);
                        }
                    });
                }
            }));
            Thread twk = new Thread(drwk);
            twk.start();
            return true;
        }
    }

    /**
     * This function creates a OpenRouteService API request URL string.
     * @param profile route type profile
     * @param start start location
     * @param end end location
     * @return the URL string
     */
    private String getDirectionsUrl(String profile, LatLng start, LatLng end) {
        String url = "https://api.openrouteservice.org/v2/directions/" + profile + "?"
                + "api_key=" + getResources().getString(R.string.open_route_service_key)
                + "&start=" + start.longitude + "," + start.latitude
                + "&end=" + end.longitude + "," + end.latitude;
        return url;
    }

    /**
     * This method builds a ParameterizedRunnable that gets path nodes.
     * @param onFinished the ParameterizedRunnable to be ran after completion
     * @return the ParameterizedRunnable requested
     */
    private ParameterizedRunnable getPathNodesRunnable(ParameterizedRunnable onFinished) {
        return new ParameterizedRunnable() {
            @Override
            public void run(Object... args) {
                JSONObject response = null;
                try {
                    response = new JSONObject((String) args[0]);
                    if (!response.has("error")) {
                        // parse JSON data to get nodes
                        JSONObject features = (JSONObject) response.getJSONArray("features").get(0);
                        JSONObject geometry = features.getJSONObject("geometry");
                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                        ArrayList<LatLng> locations = new ArrayList<>();
                        for (int i = 0; i < coordinates.length(); i++) {
                            JSONArray coord = (JSONArray) coordinates.get(i);
                            LatLng latLng = new LatLng((Double) coord.get(1), (Double) coord.get(0));
                            locations.add(latLng);
                        }
                        onFinished.run(locations);
                    } else {
                        JSONObject error = response.getJSONObject("error");
                        int code = error.getInt("code");
                        String message = error.getString("message");
                        System.out.println(code + ", " + message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * Displays the walking polyline on the map.
     * @param locations node locations
     * @param toFocus to focus the polyline or not
     */
    private void displayWalkingPolylineOnMap(List<LatLng> locations, boolean toFocus) {
        if (map == null) return;
        if (locations.size() < 2) return;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        PolylineOptions options = new PolylineOptions()
                .width(20f)
                .color(Color.rgb(61, 161, 255))
                .pattern(Arrays.asList(new Gap(15), new Dot()))
                .geodesic(true)
                .startCap(new RoundCap())
                .endCap(new RoundCap());
        for (LatLng location : locations) {
            options.add(location);
            builder.include(location);
        }
        walkingRoute = map.addPolyline(options);

        if (toFocus) {
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), screenWidth / 8));
        }
    }

    /**
     * Displays the driving polyline on the map.
     * @param locations node locations
     * @param toFocus to focus the polyline or not
     */
    private void displayDrivingPolylineOnMap(List<LatLng> locations, boolean toFocus) {
        if (map == null) return;
        if (locations.size() < 2) return;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        PolylineOptions options = new PolylineOptions()
                .width(25f)
                .color(Color.rgb(61, 161, 255))
                .geodesic(true)
                .startCap(new RoundCap())
                .endCap(new RoundCap());
        for (LatLng location : locations) {
            options.add(location);
            builder.include(location);
        }
        drivingRoute = map.addPolyline(options);

        if (toFocus) {
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), screenWidth / 8));
        }
    }
}