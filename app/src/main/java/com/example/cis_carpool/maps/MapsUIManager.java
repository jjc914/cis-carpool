package com.example.cis_carpool.maps;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cis_carpool.DownloadRunnable;
import com.example.cis_carpool.R;
import com.example.cis_carpool.Utils;
import com.example.cis_carpool.abstraction.ParameterizedRunnable;
import com.example.cis_carpool.activity.MapsActivity;
import com.example.cis_carpool.maps.MapsAutocompleteRecyclerViewAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class represents the UI changing methods that MapsActivity.java uses.
 * @author joshuachasnov
 * @version 0.1
 */
public class MapsUIManager {
    private final MapsActivity activity;

    private ViewGroup sceneRoot;
    private Scene mapScene;
    private Scene searchOriginScene;
    private Scene searchDestinationScene;

    private SlidingUpPanelLayout slidingUpPanelLayout;

    private MapsAutocompleteRecyclerViewAdapter adapter;
    private OnBackPressedCallback onBackPressedCallback;

    private TransitionManager transitionManager;

    private boolean isOrigin;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public MapsUIManager(MapsActivity mapsActivity) {
        this.activity = mapsActivity;

        // hide sliding panel
        slidingUpPanelLayout = (SlidingUpPanelLayout) activity.findViewById(R.id.sliding_up_panel);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        // set scenes
        sceneRoot = (ViewGroup) activity.findViewById(R.id.scene_root);
        mapScene = Scene.getSceneForLayout(sceneRoot, R.layout.activity_maps_map, activity);
        searchOriginScene = Scene.getSceneForLayout(sceneRoot, R.layout.activity_maps_origin_search, activity);
        searchDestinationScene = Scene.getSceneForLayout(sceneRoot, R.layout.activity_maps_destination_search, activity);

        // init listeners
        initializeStopSearchBackPressedListener();
        setStartSearchFocusChangeListeners();

        // set transitions for animations
        transitionManager = new TransitionManager();
        transitionManager.setTransition(searchOriginScene, new Fade());
        transitionManager.setTransition(searchDestinationScene, new Fade());
        searchOriginScene.setEnterAction(getStartSearchRunnable(R.id.search_origin_search, R.layout.activity_maps_origin_search));
        searchDestinationScene.setEnterAction(getStartSearchRunnable(R.id.search_destination_search, R.layout.activity_maps_destination_search));
    }

    public boolean isOrigin() {
        return isOrigin;
    }

    /**
     * Opens the journey options that is shown to the user when a route is available.
     * @param hasCarpool has join carpool option
     */
    public void openJourneyOptions(boolean hasCarpool) {
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        activity.setMapMovement(false);
        slidingUpPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        Button joinCarpoolButton = activity.findViewById(R.id.join_carpool_button);
        if (hasCarpool) {
            joinCarpoolButton.setEnabled(true);
        } else {
            joinCarpoolButton.setEnabled(false);
        }
    }

    /**
     * Hides the journey options when a route is no longer available.
     */
    public void hideJourneyOptions() {
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        activity.setMapMovement(false);
        slidingUpPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    /**
     * Closes the search bar.
     * @param originName name of the origin location
     * @param destinationName name of the destination location
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void closeSearch(final String originName, final String destinationName) {
        transitionManager.setTransition(mapScene, new Fade());
        // when transition finished
        mapScene.setEnterAction(new Runnable() {
            @Override
            public void run() {
                // set query strings for origin and destination + run finished runnable
                getStopSearchRunnable().run();
                SearchView originSearchView = (SearchView) activity.findViewById(R.id.search_origin_map);
                originSearchView.setQuery(originName, false);
                SearchView destinationSearchView = (SearchView) activity.findViewById(R.id.search_destination_map);
                destinationSearchView.setQuery(destinationName, false);
            }
        });
        transitionManager.transitionTo(mapScene);
    }

    /**
     * Sets the listeners for when the search has started
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setStartSearchFocusChangeListeners() {
        onBackPressedCallback.setEnabled(false);

        SearchView mapOriginSearchView = (SearchView) activity.findViewById(R.id.search_origin_map);
        SearchView mapDestinationSearchView = (SearchView) activity.findViewById(R.id.search_destination_map);

        setViewFocusChangeListener(mapOriginSearchView, getStartSearchFocusChangeListener(searchOriginScene));
        setViewFocusChangeListener(mapDestinationSearchView, getStartSearchFocusChangeListener(searchDestinationScene));
    }

    /**
     * Builds the start search focus change listeners for when the search has started.
     * @param targetScene
     * @return the built OnFocusChangeListener.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private OnFocusChangeListener getStartSearchFocusChangeListener(Scene targetScene) {
        return new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    transitionManager.transitionTo(targetScene);
                }
            }
        };
    }

    /**
     * Builds the runnable that should be ran when the search is started.
     * @param toFocusID the id of the view to focus
     * @param originLayoutID the id of the origin layout
     * @return the built runanble
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Runnable getStartSearchRunnable(int toFocusID, int originLayoutID) {
        return new Runnable() {
            @Override
            public void run() {
                onBackPressedCallback.setEnabled(true);

                isOrigin = originLayoutID == R.layout.activity_maps_origin_search;

                // request focus
                SearchView toFocusView = (SearchView) activity.findViewById(toFocusID);
                toFocusView.requestFocus();
                setStopSearchFocusChangeListener(toFocusView);
                setSearchTextQueryTextListener(toFocusView);
                initializeAutocompleteRecyclerView();
                activity.setMapMovement(false);

                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
            }
        };
    }

    /**
     * Sets the listener for when the focus changed on/off the searchView.
     * @param searchView the searchView to listen to
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setStopSearchFocusChangeListener(SearchView searchView) {
        setViewFocusChangeListener(searchView, new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (Utils.strip(searchView.getQuery().toString()).equals("")) {
                        closeSearch(activity.getOriginName(), activity.getDestinationName());
                    }
                }
            }
        });
    }

    /**
     * Initialize the back button pressed listener.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeStopSearchBackPressedListener() {
        onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                closeSearch(activity.getOriginName(), activity.getDestinationName());
            }
        };
        activity.getOnBackPressedDispatcher().addCallback(activity, onBackPressedCallback);
    }

    /**
     * Sets the searchView listener.
     * @param searchView the searchView to listen to
     */
    private void setSearchTextQueryTextListener(SearchView searchView) {
        searchView.setOnQueryTextListener(getSearchTextQueryTextListener(searchView));
    }

    /**
     * Builds the query text listener that should be run when listening to query changes.
     * @param searchView the view to listen to
     * @return the built listener
     */
    private OnQueryTextListener getSearchTextQueryTextListener(SearchView searchView) {
        return new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String lastText = newText;
                Handler handler = new Handler();
                Runnable waitCheck = new Runnable() {
                    @Override
                    public void run() {
                        String currentText = searchView.getQuery().toString();
                        if (lastText.equals(currentText)) {
                            setAutofillData(currentText);
                        }
                    }
                };
                handler.postDelayed(waitCheck, 500);
                return false;
            }
        };
    }

    /**
     * Initializes the autocomplete recycler view.
     */
    private void initializeAutocompleteRecyclerView() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView autocompleteRecyclerView = activity.findViewById(R.id.autocomplete_recyclerview);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
                autocompleteRecyclerView.setLayoutManager(layoutManager);
                adapter = new MapsAutocompleteRecyclerViewAdapter(activity, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                adapter.setClickListener(activity);
                autocompleteRecyclerView.setAdapter(adapter);
            }
        });
    }

    /**
     * Sets the autocomplete data.
     * @param text the text to autocomplete
     */
    private void setAutofillData(String text) {
        String url = getAutofillURL(text);
        DownloadRunnable dr = new DownloadRunnable(url, getAutofillSuggestionsRunnable());
        Thread t = new Thread(dr);
        t.start();
    }

    /**
     * Builds a autocomplete url from OpenRouteService API.
     * @param text the query to search for
     * @return the built URL string
     */
    private String getAutofillURL(String text) {
        LatLng mapCenter = activity.getMapCenter();
        return "https://api.openrouteservice.org/geocode/autocomplete?"
                + "api_key=" + activity.getResources().getString(R.string.open_route_service_key)
                + "&text=" + text
                + "&focus.point.lon=" + mapCenter.longitude
                + "&focus.point.lat=" + mapCenter.latitude
                + "&size=25";
    }

    /**
     * Builds the runnable to get autocomplete data from the OpenRouteService API.
     * @return the built runnable.
     */
    private ParameterizedRunnable getAutofillSuggestionsRunnable() {
        return new ParameterizedRunnable() {
            @Override
            public void run(Object... args) {
                JSONObject response;
                try {
                    response = new JSONObject((String) args[0]);
                    JSONArray suggestions = response.getJSONArray("features");

                    // parse JSON data
                    ArrayList<LatLng> coordinates = new ArrayList<>();
                    ArrayList<String> names = new ArrayList<>();
                    ArrayList<String> countries = new ArrayList<>();
                    for (int i = 0; i < suggestions.length(); i++) {
                        JSONObject suggestion = (JSONObject) suggestions.get(i);
                        JSONObject geometry = suggestion.getJSONObject("geometry");
                        JSONArray coords = geometry.getJSONArray("coordinates");

                        JSONObject properties = suggestion.getJSONObject("properties");
                        String name = properties.getString("name");
                        String country;
                        try {
                            country = properties.getString("country");
                        } catch (JSONException e) {
                            country = "";
                        }

                        names.add(name);
                        countries.add(country);
                        coordinates.add(new LatLng(coords.getDouble(1), coords.getDouble(0)));
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // set data based on recieved
                            adapter.setNamesData(names);
                            adapter.setCountriesData(countries);
                            adapter.setCoordinatesData(coordinates);
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * Builds the runnable to stop the search bar.
     * @return the built runnable
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Runnable getStopSearchRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                activity.setMapMovement(true);

                setStartSearchFocusChangeListeners();

                // hide keyboard
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                View view = activity.getCurrentFocus();
                if (view == null) {
                    view = new View(activity);
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        };
    }

    /**
     * Sets the focus change listener of a specified view.
     * @param view the view to listen to
     * @param onFocusChangeListener the listener
     */
    private void setViewFocusChangeListener(View view, OnFocusChangeListener onFocusChangeListener) {
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof LinearLayout || child instanceof RelativeLayout) {
                    setViewFocusChangeListener(child, onFocusChangeListener);
                }
                child.setOnFocusChangeListener(onFocusChangeListener);
            }
        }
    }
}
