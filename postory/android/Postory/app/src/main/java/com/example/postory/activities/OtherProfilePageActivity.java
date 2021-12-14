package com.example.postory.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.postory.BuildConfig;
import com.example.postory.R;
import com.example.postory.adapters.PostAdapter;
import com.example.postory.models.Post;
import com.example.postory.models.UserModel;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

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
    private UserModel thisUser;
    private OkHttpClient client;
    private Button followButton;

    private TextView name;
    private TextView surname;
    private TextView username;
    private TextView followedBy;
    private TextView following;
    private TextView numPosts;

    private TextView followingWarning;
    private SharedPreferences sharedPreferences;
    private String userId;
    String accessToken;
    private ListView listView;
    public static final String TAG = "OtherProfilePageActivity";
    private Post[] posts;

    @Override
    protected void goHomeClicked() {
        Intent intent = new Intent(OtherProfilePageActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void goCreatePostClicked() {
        Intent intent = new Intent(OtherProfilePageActivity.this, CreatePostActivity.class);
        intent.putExtra("goal", "create");
        startActivity(intent);
    }

    @Override
    protected void refreshClicked() {
        return;
    }

    @Override
    protected void goExploreClicked() {
        Intent intent = new Intent(OtherProfilePageActivity.this, ExploreActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile_page);
        super.initToolbar();
        userId = getIntent().getStringExtra("user_id");
        listView = (ListView) findViewById(R.id.list_posts);
        followButton = (Button) findViewById(R.id.followButton);
        followingWarning = (TextView) findViewById(R.id.followingWarning);
        name = (TextView) findViewById(R.id.name);
        surname = (TextView) findViewById(R.id.surname);
        username = (TextView) findViewById(R.id.username);
        followedBy = (TextView) findViewById(R.id.followedBy);
        following = (TextView) findViewById(R.id.following);
        numPosts = (TextView) findViewById(R.id.numPosts);

        sharedPreferences = getSharedPreferences("MY_APP", MODE_PRIVATE);
        accessToken = sharedPreferences.getString("access_token", "");
        int viewerId = Integer.parseInt(sharedPreferences.getString("user_id", ""));
        String url1 = BuildConfig.API_IP + "/user/get/" + userId;
        client = new OkHttpClient();
        requestUserData = new Request.Builder()
                .url(url1)
                .addHeader("Authorization", "JWT " + accessToken)
                .build();
        client.newCall(requestUserData).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Gson gson = new Gson();
                thisUser = gson.fromJson(response.body().string(), UserModel.class);
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        setUserFields();
                        if (thisUser.getFollowerUsers().contains(viewerId)) {
                            showAlreadyFollowed();
                        }
                    }
                });
            }
        });

        callForPosts();
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followButtonClicked();
            }
        });
    }

    private void callForPosts() {
        String url2 = BuildConfig.API_IP + "/post/all/user/" + userId;

        requestPosts = new Request.Builder()
                .url(url2)
                .addHeader("Authorization", "JWT " + accessToken)
                .build();

        callAllPosts();
    }

    private void showAlreadyFollowed() {
        followingWarning.setVisibility(View.VISIBLE);
        followButton.setVisibility(View.INVISIBLE);
    }

    private void setUserFields() {
        name.setText(thisUser.getName());
        surname.setText(thisUser.getSurname());
        username.setText(thisUser.getUsername());
        followedBy.setText(""+thisUser.getFollowerUsers().size());
        following.setText(""+thisUser.getFollowedUsers().size());
        numPosts.setText(""+thisUser.getPosts().size());
    }

    private void followButtonClicked() {
        String url = BuildConfig.API_IP + "/user/follow/" + userId;

        Request followRequest = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "JWT " + accessToken)
                .build();

        client.newCall(followRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                OtherProfilePageActivity.this.recreate();
            }
        });
    }


    private void callAllPosts() {
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
                ArrayList<Post> arrayOfPosts = new ArrayList<Post>();

                for (Post post : posts) {
                    arrayOfPosts.add(post);
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