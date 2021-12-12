package com.example.postory.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.postory.BuildConfig;
import com.example.postory.R;
import com.example.postory.adapters.PostAdapter;
import com.example.postory.models.Post;
import com.example.postory.models.PostModel;
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
import com.google.gson.Gson;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExploreActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener {


    private Post[] posts;
    private Request request;
    private OkHttpClient client;
    private String url;
    private static final String  TAG = "ExploreActivity";
    private GoogleMap mMap;
    private SharedPreferences sharedPreferences;
    private String accessToken;
    private  ArrayList<PostModel> arrayOfPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        sharedPreferences = getSharedPreferences("MY_APP",MODE_PRIVATE);


        accessToken = sharedPreferences.getString("access_token","");

        client = new OkHttpClient();
        url = BuildConfig.API_IP + "/post/all/discover";
        request = new Request.Builder()
                .addHeader("Authorization", "JWT " + accessToken)
                .url(url)
                .build();
        callAllPosts();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    private void callAllPosts(){
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.i(TAG, "onResponse: ");
                Gson gson = new Gson();
                posts = gson.fromJson(response.body().string(), Post[].class);
                Log.i(TAG, "onResponse: ");
                arrayOfPosts = new ArrayList<PostModel>();

                for (Post post : posts) {
                    arrayOfPosts.add(new PostModel(post.getId(), post.getTitle(), post.getStory(), post.getOwner(), post.getTags(), post.getLocations(), post.getImages(), post.getPostDate(), post.getEditDate(), post.getStoryDate(), post.getViewCount()));

                }
                Collections.reverse(arrayOfPosts);
                for(PostModel post:arrayOfPosts) {
                    if(post.getLocations() != null) {
                        for(List<Object> locations : post.getLocations()) {
                            if(locations.size() == 3) {
                                LatLng location = new LatLng((Double)locations.get(1),(Double)locations.get(2));
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mMap.addMarker(new MarkerOptions()
                                                .position(location)
                                                .title((String) locations.get(0)));
                                    }
                                });

                            }
                        }
                    }
                }



            }
        });

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
