package com.example.postory.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.postory.R;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.jetbrains.annotations.NotNull;

public class ExploreActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     *
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(ExploreActivity.this);

        mMap.setOnMapLongClickListener(ExploreActivity.this);


        // Add a marker in Sydney and move the camera
        LatLng boun = new LatLng(41.084668, 29.047341);

        mMap.addMarker(new MarkerOptions()
                .position(boun)
                .title("Boğaziçi Üniversitesi Güney Kampüs"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(boun, (float) (mMap.getMaxZoomLevel() - 1.9)));



    }

    @Override
    public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
        SuperActivityToast.create(ExploreActivity.this, new Style(), Style.TYPE_BUTTON)
                .setProgressBarColor(Color.WHITE)
                .setText("Welcome to " + marker.getTitle() + ".")
                .setDuration(Style.DURATION_LONG)
                .setFrame(Style.FRAME_LOLLIPOP)
                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_BLUE))
                .setAnimations(Style.ANIMATIONS_POP).show();

        return  true;
    }

    @Override
    public void onMapLongClick(@NonNull @NotNull LatLng latLng) {




        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("New Marker"));

    }
}
