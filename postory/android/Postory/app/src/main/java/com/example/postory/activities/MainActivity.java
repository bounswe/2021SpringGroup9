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
    @Override
    protected void goProfileClicked() {
        Intent i = new Intent(MainActivity.this, SelfProfilePageActivity.class);
        startActivity(i);
    }

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

    @Override
    protected void goCreatePostClicked() {
        Intent createPostIntent = new Intent(MainActivity.this, CreatePostActivity.class);
        createPostIntent.putExtra("goal", "create");
        startActivity(createPostIntent);
    }

    @Override
    protected void refreshClicked() {
        postAdapter.clear();
        callAllPosts();
    }

    @Override
    protected void goExploreClicked() {

      Intent intent = new Intent(MainActivity.this, ExploreActivity.class);
      startActivity(intent);
    }

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
    private String toBase64(Bitmap bm) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos); // bm is the bitmap object
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
}