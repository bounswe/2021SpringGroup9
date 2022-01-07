package com.example.postory.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.postory.BuildConfig;
import com.example.postory.R;
import com.example.postory.adapters.KeywordAdapter;
import com.example.postory.adapters.PostAdapter;
import com.example.postory.adapters.TagFilterAdapter;
import com.example.postory.dialogs.DelayedProgressDialog;
import com.example.postory.fragments.TimeChooserFragment;
import com.example.postory.models.Post;
import com.example.postory.models.TagItem;
import com.example.postory.utils.TimeController;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * The activity for the Discovery page. Here a map is shown on which markers of all public users' posts are marked.
 * If the user wishes to filter posts further, he/she can click the blue text "Click for an advanced search"
 * This opens another scrollable view from which the user can specify various parameters for the search.
 * @author melihozcan
 *
 */
public class ExploreActivity extends  ToolbarActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener {


    private TextView enableSearch;
    private ScrollView searchSection;
    private Post[] posts;
    private Request request;
    private Request requestFilter;
    private OkHttpClient client;
    private String url;
    private String urlFilter;
    private static final String  TAG = "ExploreActivity";
    private GoogleMap mMap;
    private SharedPreferences sharedPreferences;
    private String accessToken;
    private  ArrayList<Post> arrayOfPosts;
    private HashMap<String, Integer > markerId = new HashMap<>();
    private Post post;
    private Button addTag;
    private Button addKeyword;
    private Button displayOnMap;
    private Button displayAnotherPage;
    private Button clearFilters;
    private ImageView pickDate;
    private EditText addedTag;
    private EditText addedKeyword;
    private EditText userField;
    private EditText distanceField;
    public TextView dateText;
    public LinearLayout stdLayout;
    public FrameLayout mapContainer;
    private TagFilterAdapter tagAdapter;
    private KeywordAdapter keywordAdapter;
    private RecyclerView tagsRecyclerView;
    private RecyclerView keywordsRecyclerView;
    private ArrayList<Marker> markers = new ArrayList<>();
    private String filterId = "";
    DelayedProgressDialog dialog;
    Marker m;
    TimeController t;

    public TimeController getT() {
        return t;
    }

    public void setT(TimeController t) {
        this.t = t;
    }

    private ArrayList<TagItem> tagsList = new ArrayList<>();
    private ArrayList<TagItem> keywords = new ArrayList<>();


    /**
     * Sends the user to the homepage.
     */
    @Override
    protected void goHomeClicked() {
        Intent i = new Intent(ExploreActivity.this, MainActivity.class);
        finish();
        startActivity(i);

    }



    /**
     * Sends the user to the create post page.
     */
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
    /**
     * Sends the user to the profile page.
     */
    @Override
    protected void goProfileClicked() {
        Intent i = new Intent(ExploreActivity.this, SelfProfilePageActivity.class);
        startActivity(i);
    }

    /**
     * Logs out the user by clearing the entries in the shared preferences, this way app doesn't redirect the user to
     * home page, instead user is prompted to enter the credentials.
     *
     */
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

    /**
     * Remove a tag from the list of tags from the UI and from the tagsList arraylist.
     *
     * @param i the position of the tag
     */
    public void removeTag (int i) {
        tagsList.remove(i);
        tagAdapter = new TagFilterAdapter(R.layout.single_keyword,tagsList, this);
        tagsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        tagsRecyclerView.setAdapter(tagAdapter);

    }

    /**
     * Remove a keyword from the list of keywords from the UI and from the keywords arraylist.
     *
     * @param i the position of the keyword
     */
    public void removeKeyword (int i) {
        keywords.remove(i);
        keywordAdapter = new KeywordAdapter(R.layout.single_keyword,keywords, this);
        keywordsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        keywordsRecyclerView.setAdapter(keywordAdapter);

    }

    /**
     * Clear all the markers from the map provided by Google Maps Android SDK. (Except the blue marker which denotes
     * the area of focus which the user specifies for his/her search.)
     */
    public void clearAllMarkers() {
        Marker excls = null;
        for(Marker mrk : markers) {
            if(!mrk.getId().equals(filterId)) {
                mrk.remove();
            }
            else {
                excls = mrk;
            }
        }
        markers.clear();
        if(excls != null) {
            markers.add(excls);
        }

    }


    /**
     * All the views are created and initiated here.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        super.initToolbar();
        sharedPreferences = getSharedPreferences("MY_APP",MODE_PRIVATE);
        enableSearch = (TextView) findViewById(R.id.enable_search);
        searchSection = (ScrollView) findViewById(R.id.search_section);
        addTag = (Button) findViewById(R.id.add_tag);
        addKeyword = (Button) findViewById(R.id.add_keyword);
        displayOnMap = (Button) findViewById(R.id.display_on_map);
        displayAnotherPage = (Button) findViewById(R.id.display_on_page);
        clearFilters = (Button) findViewById(R.id.clear_filters);
        pickDate = (ImageView) findViewById(R.id.pick_date_btn);
        dialog = new DelayedProgressDialog();

        stdLayout = (LinearLayout) findViewById(R.id.explore_std_layout);
        mapContainer = (FrameLayout) findViewById(R.id.frame_placeholder);


        addedTag = (EditText) findViewById(R.id.tag_edit);
        addedKeyword = (EditText) findViewById(R.id.keyword_edit);
        tagsRecyclerView = (RecyclerView) findViewById(R.id.list_tags);
        keywordsRecyclerView = (RecyclerView) findViewById(R.id.list_keywords);
        userField = (EditText) findViewById(R.id.user_field);
        distanceField = (EditText) findViewById(R.id.distance_field);
        dateText = (TextView) findViewById(R.id.date_field);

        LinearLayoutManager tagLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager keywordLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        tagsRecyclerView.setLayoutManager(tagLayoutManager);
        keywordsRecyclerView.setLayoutManager(keywordLayoutManager);

        /**
         * Clear all the filters. Take the map back to the default state.
         */
        clearFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterId  ="";
                for(Marker marker : markers){
                    marker.remove();
                }
                markers.clear();
                tagsList.clear();
                keywords.clear();
                tagAdapter = new TagFilterAdapter(R.layout.single_keyword,tagsList, ExploreActivity.this);
                tagsRecyclerView.setItemAnimator(new DefaultItemAnimator());
                tagsRecyclerView.setAdapter(tagAdapter);
                userField.setText("");
                distanceField.setText("");
                t = null;
                dateText.setText("Pick Date");
                keywordAdapter = new KeywordAdapter(R.layout.single_keyword,keywords, ExploreActivity.this);
                keywordsRecyclerView.setItemAnimator(new DefaultItemAnimator());
                keywordsRecyclerView.setAdapter(keywordAdapter);

                callAllPosts();

            }
        });

        /**
         * Show the results of the posts on another page.
         */
        displayAnotherPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                String requestUrl = buildRequest().toString();

                Intent intent = new Intent(ExploreActivity.this,ListFilteredPostsActivity.class);
                intent.putExtra("request_url",requestUrl);
                startActivity(intent);
                Log.i(TAG, "onClick: ");
                
            }
        });


        /**
         * Display the results of the filtering on the map.
         */
        displayOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO make call

                filterCall(buildRequest());
            }
        });

        /**
         * Pick a date to narrow down your search. Directs you to the TimeChooserFragment.
         */
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stdLayout.setVisibility(View.GONE);
                mapContainer.setVisibility(View.VISIBLE);
                TimeChooserFragment tf = new TimeChooserFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_placeholder,tf)
                        .commit();

            }
        });

        /**
         * Add keyword by clicking on this button. An adapter is used to show added keywords.
         */
        addKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keywords.add(new TagItem(addedKeyword.getText().toString()));
                keywordAdapter = new KeywordAdapter(R.layout.single_keyword,keywords, ExploreActivity.this);
                keywordsRecyclerView.setItemAnimator(new DefaultItemAnimator());
                keywordsRecyclerView.setAdapter(keywordAdapter);
                addedKeyword.setText("");
                hideSoftKeyboard(ExploreActivity.this);
            }
        });

        /**
         * Add a tag by clicking on this button. An adapter is used to show added tags.
         */
        addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagsList.add(new TagItem(addedTag.getText().toString()));
                tagAdapter = new TagFilterAdapter(R.layout.single_keyword,tagsList, ExploreActivity.this);
                tagsRecyclerView.setItemAnimator(new DefaultItemAnimator());
                tagsRecyclerView.setAdapter(tagAdapter);
                addedTag.setText("");
                hideSoftKeyboard(ExploreActivity.this);
            }
        });

        /**
         * Make the search layout visible.
         */
        enableSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchSection.setVisibility(View.VISIBLE);
            }
        });


        accessToken = sharedPreferences.getString("access_token","");

        client = new OkHttpClient();
        url = BuildConfig.API_IP + "/post/all/discover";
        urlFilter = BuildConfig.API_IP + "/post/all/filter";
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

    /**
     * Build a request with the input values gotten from the user.
     * @return a HttpUrl.
     */
    
    public HttpUrl buildRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(urlFilter).newBuilder();
        int [] years = new int[2];
        int [] months = new int[2];
        int [] days = new int[2];
        int [] hours = new int[2];
        int [] minutes = new int[2];
        if(t != null) {
            int precision = t.getPrecision();
            switch (precision){
                case 4:
                    years[0] = t.getStartYear();
                    years[1] = t.getEndYear();
                    months[0] = t.getStartMonth();
                    months[1] = t.getEndMonth();
                    days[0] = t.getStartDay();
                    days[1] = t.getEndDay();
                    hours[0] = t.getStartHour();
                    hours[1] = t.getEndHour();
                    minutes[0] = t.getStartMinute();
                    minutes[1] = t.getEndMinute();
                    urlBuilder.addQueryParameter("startYear", years[0] + "");
                    urlBuilder.addQueryParameter("endYear", years[1] + "");
                    urlBuilder.addQueryParameter("startMonth", months[0] + "");
                    urlBuilder.addQueryParameter("endMonth", months[1] + "");
                    urlBuilder.addQueryParameter("startDay", days[0] + "");
                    urlBuilder.addQueryParameter("endDay", days[1] + "");
                    urlBuilder.addQueryParameter("startHour", hours[0] + "");
                    urlBuilder.addQueryParameter("endHour", hours[1] + "");
                    urlBuilder.addQueryParameter("startMinute", minutes[0] + "");
                    urlBuilder.addQueryParameter("endMinute", minutes[1] + "");
                    break;
                case 3:
                    years[0] = t.getStartYear();
                    years[1] = t.getEndYear();
                    months[0] = t.getStartMonth();
                    months[1] = t.getEndMonth();
                    days[0] = t.getStartDay();
                    days[1] = t.getEndDay();
                    urlBuilder.addQueryParameter("startYear", years[0] + "");
                    urlBuilder.addQueryParameter("endYear", years[1] + "");
                    urlBuilder.addQueryParameter("startMonth", months[0] + "");
                    urlBuilder.addQueryParameter("endMonth", months[1] + "");
                    urlBuilder.addQueryParameter("startDay", days[0] + "");
                    urlBuilder.addQueryParameter("endDay", days[1] + "");
                    break;
                case 2:
                    years[0] = t.getStartYear();
                    years[1] = t.getEndYear();
                    months[0] = t.getStartMonth();
                    months[1] = t.getEndMonth();
                    urlBuilder.addQueryParameter("startYear", years[0] + "");
                    urlBuilder.addQueryParameter("endYear", years[1] + "");
                    urlBuilder.addQueryParameter("startMonth", months[0] + "");
                    urlBuilder.addQueryParameter("endMonth", months[1] + "");
                    break;
                case 1 :
                    years[0] = t.getStartYear();
                    years[1] = t.getEndYear();

                    urlBuilder.addQueryParameter("startYear", years[0] + "");
                    urlBuilder.addQueryParameter("endYear", years[1] + "");

                    break;
            }
        }


        for(TagItem tag : tagsList) {
            urlBuilder.addQueryParameter("tag",tag.getTag());
        }

        String keywordStart = "";
        for(TagItem keyword : keywords) {
            keywordStart += " " + keyword.getTag();

        }
        if(!keywordStart.equals("")) {
            urlBuilder.addQueryParameter("keyword",keywordStart);
        }



        if(!userField.getText().toString().equals("")) {
            urlBuilder.addQueryParameter("user",userField.getText().toString());
        }

        if(m != null) {
            urlBuilder.addQueryParameter("latitude", String.valueOf(m.getPosition().latitude));
            urlBuilder.addQueryParameter("longitude", String.valueOf(m.getPosition().longitude));
        }

        if(!distanceField.getText().toString().equals("")) {
            urlBuilder.addQueryParameter("distance",distanceField.getText().toString());
        }

        urlBuilder.addQueryParameter("related","true");


        
        
        return urlBuilder.build();
    }


    /**
     * A call to get filtered posts. The url given here is already edited before according to the values input by the
     * user.
     * @param url The given HttpUrl, which is built by adding different input values gotten from the UI as parameters
     */
    public void filterCall(HttpUrl url) {

        requestFilter = new Request.Builder()
                .addHeader("Authorization", "JWT " + accessToken)
                .url(url)
                .build();

        
        dialog.show(getSupportFragmentManager(), "Filtering posts...");
        
        
        client.newCall(requestFilter).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i(TAG, "onFailure: ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.cancel();

                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.cancel();

                    }
                });

                Gson gson = new Gson();
                posts = gson.fromJson(response.body().string(), Post[].class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clearAllMarkers();

                        displayOnMap();
                    }
                });


                Log.i(TAG, "onResponse: ");
            }
        });

    }


    /**
     * Get all the posts
     */
    private void callAllPosts(){
        dialog.show(getSupportFragmentManager(), "Filtering posts...");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i(TAG, "onFailure: ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.cancel();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.i(TAG, "onResponse: ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.cancel();
                    }
                });

                /**
                 * Display the results on the map.
                 */
                Gson gson = new Gson();
                posts = gson.fromJson(response.body().string(), Post[].class);
                Log.i(TAG, "onResponse: ");

                displayOnMap();





            }
        });

    }


    /**
     * Display all the returned posts on the map, by adding markers to the corresponding positions.
     */
    public void displayOnMap() {

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

                                markers.add(m);

                                markerId.put(m.getId(),post.getId());

                            }
                        });

                    }
                }
            }
        }

    }


    /**
     * Hides the keyboards.
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(800, 150);
                View v = getLayoutInflater().inflate(R.layout.single_post_preview, null);
                v.setLayoutParams(layoutParams);
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.085097, 29.043101), 4));
        mMap.setOnMarkerClickListener(ExploreActivity.this);
        mMap.setOnMapLongClickListener(ExploreActivity.this);
    }


    /**
     * When a marker is clicked, show the related post preview.
     * @param marker the marker clicked
     * @return
     */
    @Override
    public boolean onMarkerClick(@NonNull @NotNull Marker marker) {

        if(!marker.getId().equals(filterId)) {
            marker.showInfoWindow();
        }

        return  true;
    }

    /**
     * On longer click to the map, a blue marker is added to the map which denotes the center of the search for
     * the user.
     * @param latLng
     */
    @Override
    public void onMapLongClick(@NonNull @NotNull LatLng latLng) {

        if (!filterId.equals("")) {
            Marker remove = null;
            for(Marker m : markers) {
                if(m.getId().equals(filterId)) {
                    remove = m;
                    markers.remove(m);
                    break;
                }
            }
            if(remove != null) {
                remove.remove();
            }
        }


         m = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .title("Filter location"));

        markers.add(m);
        filterId = m.getId();







    }
    @Override
    protected void goActivitiesClicked() {
        Intent i = new Intent(ExploreActivity.this, ActivityStreamActivity.class);
        startActivity(i);
    }
}
