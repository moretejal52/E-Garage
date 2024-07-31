package com.dss.e_garage.Garages;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dss.e_garage.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MapsFragmentSelLoc extends Fragment {
    FloatingActionButton fab_curr_loc,fab_done;
    LatLng selected_latlng;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            fab_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("Sel_latlng", ""+selected_latlng.latitude+","+selected_latlng.longitude).apply();
                    LoginActivity.Sel_Loc.dismiss();

                }
            });
            fab_curr_loc.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onClick(View view) {
                    if(LoginActivity.getLoc.Current_lat!=0) {
                        googleMap.clear();
                        LatLng crr_loc = new LatLng(LoginActivity.getLoc.Current_lat, LoginActivity.getLoc.Current_long);
                        googleMap.addMarker(new MarkerOptions().position(crr_loc).title("Your Location"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(crr_loc, 18));
                        selected_latlng=crr_loc;
                    }
                }

            });
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
                    selected_latlng=latLng;

                }
            });
        }
    };

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
                  return inflater.inflate(R.layout.fragment_maps_selloc, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab_curr_loc = view.findViewById(R.id.fab_curr_loc);
        fab_done=view.findViewById(R.id.fab_done);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }



    }



}