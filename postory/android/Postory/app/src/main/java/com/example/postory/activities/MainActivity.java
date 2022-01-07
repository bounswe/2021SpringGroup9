package com.example.postory.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.example.postory.BuildConfig;
import com.example.postory.R;
import com.example.postory.adapters.PostAdapter;
import com.example.postory.models.Post;
import com.google.gson.Gson;

import okhttp3.*;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


/**
 * This is the activity for our homepage. Here all the posts that are available to a user are displayed in a
 * scrollable view.
 *
 * @author melihozcan
 */
public class MainActivity extends ToolbarActivity {

    private PostAdapter postAdapter;
    private Request request;
    private OkHttpClient client;
    private String url;
    private ListView listView;
    public static final int CREATE_POST = 1;
    public static final String TAG = "MainActivity";
    private Post[] posts;
    private SharedPreferences sharedPreferences;
    private String accessToken;

    /**
     * Sends the user to a profile page.
     */
    @Override
    protected void goProfileClicked() {
        Intent i = new Intent(MainActivity.this, SelfProfilePageActivity.class);
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
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
    @Override
    protected void goHomeClicked() {
        return;
    }


    /**
     * Sends the user to the create posts page.
     */
    @Override
    protected void goCreatePostClicked() {
        Intent createPostIntent = new Intent(MainActivity.this, CreatePostActivity.class);
        createPostIntent.putExtra("goal", "create");
        startActivity(createPostIntent);
    }

    /**
     * Refreshes the page.
     */
    @Override
    protected void refreshClicked() {
        postAdapter.clear();
        callAllPosts();
    }

    /**
     * Sends the user to the explore  page.
     */
    @Override
    protected void goExploreClicked() {

      Intent intent = new Intent(MainActivity.this, ExploreActivity.class);
      startActivity(intent);
    }

    /**
     * In this method, all views are initialized and onCLickListeners are set.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.initToolbar();
        sharedPreferences = getSharedPreferences("MY_APP",MODE_PRIVATE);


        accessToken = sharedPreferences.getString("access_token","");

        client = new OkHttpClient();
        url = BuildConfig.API_IP + "/post/all";
        request = new Request.Builder()
                .addHeader("Authorization", "JWT " + accessToken)
                .url(url)
                .build();

        listView = (ListView) findViewById(R.id.list_view_posts);
        callAllPosts();
    }


    /**
     * Make a call to get all the posts visible to the user, parse the JSON responses into appropriate models
     * using GSON.
     * @see Gson
     * Add the models to an arraylist and feed this arraylist into an adapter.
     * @see PostAdapter
     * Set adapter on the listview.
     */
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
                String respString = response.body().string();
                posts = gson.fromJson(respString, Post[].class);
                Log.i(TAG, "onResponse: ");
                ArrayList<Post> arrayOfPosts = new ArrayList<Post>();

                for (Post post : posts) {
                    arrayOfPosts.add(post);

                }

                Collections.reverse(arrayOfPosts);
                postAdapter = new PostAdapter(MainActivity.this, arrayOfPosts);
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
     * Sends the user to the ActivityStreamPage.
     */
    @Override
    protected void goActivitiesClicked() {
        Intent i = new Intent(MainActivity.this, ActivityStreamActivity.class);
        startActivity(i);
    }
}