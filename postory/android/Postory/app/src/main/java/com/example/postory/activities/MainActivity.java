package com.example.postory.activities;

import android.content.Context;
import android.content.Intent;
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
import com.example.postory.models.PostModel;
import com.example.postory.models.PostReturnModel;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
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
        SuperActivityToast.create(MainActivity.this, new Style(), Style.TYPE_BUTTON)
                .setProgressBarColor(Color.WHITE)
                .setText("This feature is not available now.")
                .setDuration(Style.DURATION_LONG)
                .setFrame(Style.FRAME_LOLLIPOP)
                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED))
                .setAnimations(Style.ANIMATIONS_POP).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.initToolbar();

        client = new OkHttpClient();
        url = BuildConfig.API_IP + "/post/all";
        request = new Request.Builder()
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
                posts = gson.fromJson(response.body().string(), Post[].class);
                Log.i(TAG, "onResponse: ");
                ArrayList<PostModel> arrayOfPosts = new ArrayList<PostModel>();

                for (Post post : posts) {
                    arrayOfPosts.add(new PostModel(post.getId(), post.getTitle(), post.getStory(), post.getOwner(), post.getTags(), post.getLocations(), post.getImages(), post.getPostDate(), post.getEditDate(), post.getStoryDate(), post.getViewCount()));

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