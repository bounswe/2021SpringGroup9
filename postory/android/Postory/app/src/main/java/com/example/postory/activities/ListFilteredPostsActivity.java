package com.example.postory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.postory.BuildConfig;
import com.example.postory.R;
import com.example.postory.adapters.PostAdapter;
import com.example.postory.models.Post;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * The activity, which lists the posts that are returned from the previous explore activities filtering.
 * Works very similar to the MainActivity
 * @see MainActivity
 *
 *
 * @author melihozcan
 */
public class ListFilteredPostsActivity extends ToolbarActivity{

    private SharedPreferences sharedPreferences;
    private String accessToken;
    private ListView listView;
    private Request request;
    private OkHttpClient client;
    private String url;
    private Post[] posts;
    public static final String TAG = "MainActivity";
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_posts);
        super.initToolbar();

        sharedPreferences = getSharedPreferences("MY_APP",MODE_PRIVATE);

        /**
         *  The url that we made the call with in the previous activity.
         */

        url = getIntent().getStringExtra("request_url");

        accessToken = sharedPreferences.getString("access_token","");

        client = new OkHttpClient();
        /**
         * Build a request with the results of the last filtering query.
         */
        request = new Request.Builder()
                .addHeader("Authorization", "JWT " + accessToken)
                .url(url)
                .build();

        listView = (ListView) findViewById(R.id.list_view_posts);

        callFilteredPosts();


    }

    /**
     * Get the filtered posts with the call we made on the explore page. Display them on the
     * page using an adapter.
     */
    private void callFilteredPosts() {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.i(TAG, "onResponse: ");
                Gson gson = new Gson();
                String respString = response.body().string();
                posts = gson.fromJson(respString, Post[].class);
                Log.i(TAG, "onResponse: ");
                ArrayList<Post> arrayOfPosts = new ArrayList<Post>();

                for (Post post : posts) {
                    arrayOfPosts.add(post);

                }

                Collections.reverse(arrayOfPosts);
                postAdapter = new PostAdapter(ListFilteredPostsActivity.this, arrayOfPosts);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(postAdapter);
                    }
                });
            }
        });
    }

    /**
     * Sends the user to the Profile page.
     */
    protected void goProfileClicked() {
        Intent i = new Intent(ListFilteredPostsActivity.this, SelfProfilePageActivity.class);
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
        Intent i = new Intent(ListFilteredPostsActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
    /**
     * Sends the user to the homepage.
     */
    @Override
    protected void goHomeClicked() {

        Intent i = new Intent(ListFilteredPostsActivity.this, MainActivity.class);
        finish();
        startActivity(i);

    }


    /**
     * Sends the user to the create post activity.
     */
    @Override
    protected void goCreatePostClicked() {
        Intent createPostIntent = new Intent(ListFilteredPostsActivity.this, CreatePostActivity.class);
        createPostIntent.putExtra("goal", "create");
        startActivity(createPostIntent);
    }

    @Override
    protected void refreshClicked() {
        return;
    }

    /**
     * Sends the user to the explore page.
     */
    @Override
    protected void goExploreClicked() {

        Intent intent = new Intent(ListFilteredPostsActivity.this, ExploreActivity.class);
        startActivity(intent);
    }

    /**
     * Sends the user to the activity stream page.
     */
    @Override
    protected void goActivitiesClicked() {
        Intent i = new Intent(ListFilteredPostsActivity.this, ActivityStreamActivity.class);
        startActivity(i);
    }
}
