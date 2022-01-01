package com.example.postory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.postory.BuildConfig;
import com.example.postory.R;
import com.example.postory.adapters.PostAdapter;
import com.example.postory.models.Post;
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
import java.util.HashMap;
import java.util.List;

public class ExploreActivity extends  ToolbarActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener {


    private Post[] posts;
    private Request request;
    private Request requestGetPost;
    private OkHttpClient client;
    private String url;
    private String urlGetPost;
    private static final String  TAG = "ExploreActivity";
    private GoogleMap mMap;
    private SharedPreferences sharedPreferences;
    private String accessToken;
    private  ArrayList<Post> arrayOfPosts;
    private HashMap<String, Integer > markerId = new HashMap<>();
    private Post post;

    @Override
    protected void goHomeClicked() {
        Intent i = new Intent(ExploreActivity.this, MainActivity.class);
        finish();
        startActivity(i);

    }

    @Override
    protected void goCreatePostClicked() {
        Intent createPostIntent = new Intent(ExploreActivity.this, CreatePostActivity.class);
        createPostIntent.putExtra("goal", "create");
        startActivity(createPostIntent);

    }

    @Override
    protected void refreshClicked() {
        callAllPosts();
    }

    @Override
    protected void goExploreClicked() {

        return;
    }
    @Override
    protected void goProfileClicked() {
        Intent i = new Intent(ExploreActivity.this, SelfProfilePageActivity.class);
        startActivity(i);
    }

    @Override
    protected void logoutClicked() {
        sharedPreferences = getSharedPreferences("MY_APP",MODE_PRIVATE);
        sharedPreferences.edit().remove("valid_until").apply();
        sharedPreferences.edit().remove("user_id").apply();
        sharedPreferences.edit().remove("access_token").apply();
        Intent i = new Intent(ExploreActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        super.initToolbar();
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
                arrayOfPosts = new ArrayList<Post>();

                for (Post post : posts) {
                    arrayOfPosts.add(post);

                }
                Collections.reverse(arrayOfPosts);
                for(Post post:arrayOfPosts) {
                    if(post.getLocations() != null) {
                        for(List<Object> locations : post.getLocations()) {
                            if(locations.size() == 3) {
                                LatLng location = new LatLng((Double)locations.get(1),(Double)locations.get(2));
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Marker m = mMap.addMarker(new MarkerOptions()
                                                .position(location)
                                                .title((String) locations.get(0)));

                                        markerId.put(m.getId(),post.getId());

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
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                Log.i(TAG, "onInfoWindowClick: ");
                Intent i = new Intent(ExploreActivity.this, SinglePostActivity.class);
                i.putExtra("post_id",markerId.get(marker.getId()) + "");
                startActivity(i);
            }
        });
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoContents(@NonNull Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.single_post_preview, null);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "onClick: ");
                                
                    }
                });

                ImageView imagePost = (ImageView) v.findViewById(R.id.post_photo_preview);
                TextView textTitle = (TextView) v.findViewById(R.id.title_preview);
                TextView textContent = (TextView) v.findViewById(R.id.content_preview);

                Post holdThePost = new Post();
                for(Post post : arrayOfPosts) {
                    if(post.getId() == markerId.get(marker.getId())) {
                        holdThePost = post;
                        break;
                    }
                }

                textTitle.setText(holdThePost.getTitle());
                textContent.setText(holdThePost.getStory());
                if(holdThePost.getImages().size() != 0) {
                    Glide
                            .with(ExploreActivity.this)
                            .load(holdThePost.getImages().get(0))
                            .placeholder(R.drawable.placeholder)
                            .apply(new RequestOptions().override(400,400))
                            .centerCrop()
                            .into(imagePost);
                }

                return v;
            }

            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                return null;
            }
        });
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.085097, 29.043101), 10));
        mMap.setOnMarkerClickListener(ExploreActivity.this);
        mMap.setOnMapLongClickListener(ExploreActivity.this);
    }

    @Override
    public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
        marker.showInfoWindow();
        return  true;
    }

    @Override
    public void onMapLongClick(@NonNull @NotNull LatLng latLng) {
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("New Marker"));

    }
}
