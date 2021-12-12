package com.example.postory.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.postory.BuildConfig;
import com.example.postory.R;
import com.example.postory.adapters.PostAdapter;
import com.example.postory.models.Post;
import com.example.postory.models.PostModel;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OtherProfilePageActivity extends ToolbarActivity {
    private PostAdapter postAdapter;
    private Request requestPosts;
    private Request requestUserData;

    private OkHttpClient client;
    private String userId;
    private ListView listView;
    public static final String TAG = "OtherProfilePageActivity";
    private Post[] posts;

    @Override
    protected void goHomeClicked() {

    }

    @Override
    protected void goCreatePostClicked() {

    }

    @Override
    protected void refreshClicked() {

    }

    @Override
    protected void goExploreClicked() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile_page);
        super.initToolbar();
        listView = (ListView) findViewById(R.id.list_view_posts);

        client = new OkHttpClient();
        String url1 = BuildConfig.API_IP + "/post/all/user/" + userId;

        requestPosts = new Request.Builder()
                .url(url1)
                .build();

        callAllPosts();

        String url2 = BuildConfig.API_IP + "/user/get/" + userId;
        requestUserData = new Request.Builder()
                .url(url2)
                .build();
    }



    private void callAllPosts(){
        client.newCall(requestPosts).enqueue(new Callback() {
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
                postAdapter = new PostAdapter(OtherProfilePageActivity.this, arrayOfPosts);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(postAdapter);
                    }
                });
            }
        });

    }
}