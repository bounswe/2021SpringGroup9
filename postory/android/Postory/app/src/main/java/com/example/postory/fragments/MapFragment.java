package com.example.postory.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.postory.R;
import com.example.postory.activities.CreatePostActivity;
import com.example.postory.models.LocationModel;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import org.jetbrains.annotations.NotNull;

import java.util.Map;


/**
 * The fragment which contains the map. This fragment is used in the create/edit post page, to enable users to choose a location and name it.
 */
public class MapFragment extends Fragment {

    AlertDialog alertDialog;
    GoogleMap map;
    LatLng locationChosen;

    /**
     * Inflate the view, enable the user to long click on the map to specify a location and
     * then name it. The user is then returned to the create post activity.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment,container,false);
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        EditText editText = new EditText(getActivity());
        alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Choose The Location Name")
                .setView(editText)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ((CreatePostActivity)getActivity()).setLocationName(editText.getText().toString());
                        ((CreatePostActivity)getActivity()).stdLayout.setVisibility(View.VISIBLE);
                        ((CreatePostActivity)getActivity()).mapContainer.setVisibility(View.GONE);
                        ((CreatePostActivity)getActivity()).locationEditText.setText( ((CreatePostActivity)getActivity()).locationEditText.getText().toString() + " " + editText.getText().toString());
                        ((CreatePostActivity)getActivity()).addLocation(new LocationModel(editText.getText().toString(),locationChosen.latitude,locationChosen.longitude));
                        ((CreatePostActivity)getActivity()).getSupportFragmentManager().beginTransaction().remove(MapFragment.this).commit();
                        ((CreatePostActivity)getActivity()).refreshLocations();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
                map = googleMap;
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.085097, 29.043101), 10));
                map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(@NonNull @NotNull LatLng latLng) {
                        locationChosen = latLng;
                        alertDialog.show();

                    }
                });
            }
        });

        return view;

    }
}
