package com.example.cis_carpool.maps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.example.cis_carpool.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * This class is the recycler view adapter for the Google Maps autocomplete.
 * @author joshuachasnov
 * @version 0.1
 */
public class MapsAutocompleteRecyclerViewAdapter extends Adapter<MapsAutocompleteRecyclerViewAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private ItemClickListener clickListener;

    private List<String> namesData;
    private List<String> countriesData;
    private List<LatLng> coordinatesData;

    public MapsAutocompleteRecyclerViewAdapter(Context context, List<String> namesData, List<String> countriesData, List<LatLng> coordinatesData) {
        this.inflater = LayoutInflater.from(context);

        this.namesData = namesData;
        this.countriesData = countriesData;
        this.coordinatesData = coordinatesData;

        this.namesData.add(0, "Clear Selection");
        this.countriesData.add(0, "");
        this.coordinatesData.add(0, null);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activity_maps_recyclerview_row, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = namesData.get(position);
        String country = countriesData.get(position);

        holder.nameTextView.setText(name);
        holder.countryTextView.setText(country);
        if (coordinatesData.get(position) == null) {
            holder.iconImageView.setBackgroundResource(R.drawable.cross_icon);
        }
    }

    @Override
    public int getItemCount() {
        return namesData.size();
    }

    /**
     * Sets the recyclerview names data to a new set of data.
     * @param newData the new data
     */
    public void setNamesData(List<String> newData) {
        namesData.clear();
        namesData.addAll(newData);
        namesData.add(0, "Clear Selection");
    }

    /**
     * Sets the recyclerview countries data to a new set of data.
     * @param newData the new data
     */
    public void setCountriesData(List<String> newData) {
        countriesData.clear();
        countriesData.addAll(newData);
        countriesData.add(0, "");
    }

    /**
     * Sets the recyclerview coordinate data to a new set of data.
     * @param newData the new data
     */
    public void setCoordinatesData(List<LatLng> newData) {
        coordinatesData.clear();
        coordinatesData.addAll(newData);
        coordinatesData.add(0, null);
    }

    /**
     * This class is the viewholder for the Google Maps autocomplete recyclerview.
     * @author joshuachasnov
     * @version 0.1
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MapsAutocompleteRecyclerViewAdapter adapter;

        private TextView nameTextView;
        private TextView countryTextView;
        private ImageView iconImageView;

        public ViewHolder(View itemView, MapsAutocompleteRecyclerViewAdapter adapter) {
            super(itemView);

            this.adapter = adapter;
            nameTextView = itemView.findViewById(R.id.place_name);
            countryTextView = itemView.findViewById(R.id.place_country);
            iconImageView = itemView.findViewById(R.id.place_icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, adapter, getAdapterPosition());
        }
    }

    public String getName(int id) {
        return namesData.get(id);
    }

    public String getCountry(int id) {
        return countriesData.get(id);
    }

    public LatLng getCoordinates(int id) {
        return coordinatesData.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    /**
     * A implementable interface for item click events.
     */
    public interface ItemClickListener {
        /**
         * This function will be called on recyclerview item click.
         * @param view the current view
         * @param adapter the recyclerview adapter
         * @param position the position in the recyclerview.
         */
        void onItemClick(View view, MapsAutocompleteRecyclerViewAdapter adapter, int position);
    }
}