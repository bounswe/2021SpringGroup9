package com.example.postory.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.postory.BuildConfig;
import com.example.postory.R;
import com.example.postory.adapters.PostAdapter;
import com.example.postory.models.Post;
import com.example.postory.models.UserModel;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
    private ImageView profilePicture;
    private ImageView report;
    private SharedPreferences sharedPreferences;
    private String userId;
    String accessToken;
    private ListView listView;
    public static final String TAG = "OtherProfilePageActivity";
    private Post[] posts;
    private boolean followed;

    @Override
    protected void goProfileClicked() {
        Intent i = new Intent(OtherProfilePageActivity.this, SelfProfilePageActivity.class);
        startActivity(i);
    }

    @Override
    protected void logoutClicked() {
        sharedPreferences = getSharedPreferences("MY_APP", MODE_PRIVATE);
        sharedPreferences.edit().remove("valid_until").apply();
        sharedPreferences.edit().remove("user_id").apply();
        sharedPreferences.edit().remove("access_token").apply();
        Intent i = new Intent(OtherProfilePageActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

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
        name = (TextView) findViewById(R.id.name);
        surname = (TextView) findViewById(R.id.surname);
        username = (TextView) findViewById(R.id.username);
        followedBy = (TextView) findViewById(R.id.followedBy);
        following = (TextView) findViewById(R.id.following);
        numPosts = (TextView) findViewById(R.id.numPosts);
        profilePicture = (ImageView) findViewById(R.id.profilePicture);
        report = (ImageView) findViewById(R.id.report);

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
                String respString = response.body().string();
                thisUser = gson.fromJson(respString, UserModel.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setUserFields();
                        if (thisUser.getFollowerUsers().contains(viewerId)) {
                            followed = true;
                            followButton.setText("Unfollow");
                        } else {
                            followed = false;
                            followButton.setText("Follow");
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
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = BuildConfig.API_IP + "/user/report/user/" + userId;
                MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
                MultipartBody.Builder builder = bodyBuilder.setType(MultipartBody.FORM);
                RequestBody body = RequestBody.create(null, new byte[]{});

                Request reportRequest = new Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("Authorization", "JWT " + accessToken)
                        .build();

                client.newCall(reportRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.i(TAG, "onFailure: ");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SuperActivityToast.create(OtherProfilePageActivity.this, new Style(), Style.TYPE_BUTTON)
                                            .setProgressBarColor(Color.WHITE)
                                            .setText("The user is reported.")
                                            .setDuration(Style.DURATION_LONG)
                                            .setFrame(Style.FRAME_LOLLIPOP)
                                            .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_BLUE))
                                            .setAnimations(Style.ANIMATIONS_POP).show();
                                }
                            });

                        }
                    }
                });
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


    private void changeFollowStatus() {
        followed = !followed;
        if (followed) {
            followButton.setText("Unfollow");
        } else {
            followButton.setText("Follow");
        }
    }


    private void setUserFields() {
        name.setText(thisUser.getName());
        surname.setText(thisUser.getSurname());
        username.setText(thisUser.getUsername());
        followedBy.setText("" + thisUser.getFollowerUsers().size());
        following.setText("" + thisUser.getFollowedUsers().size());
        numPosts.setText("" + thisUser.getPosts().size());
        Glide
                .with(OtherProfilePageActivity.this)
                .load(thisUser.getUserPhoto())
                .placeholder(R.drawable.placeholder)
                .apply(new RequestOptions().override(400, 400))
                .centerCrop()
                .into(profilePicture);
    }

    private void followButtonClicked() {
        String url = BuildConfig.API_IP + "/user/follow/" + userId;
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        MultipartBody.Builder builder = bodyBuilder.setType(MultipartBody.FORM);
        RequestBody body = RequestBody.create(null, new byte[]{});

        Request followRequest = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "JWT " + accessToken)
                .build();

        client.newCall(followRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                changeFollowStatus();
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
    @Override
    protected void goActivitiesClicked() {
        Intent i = new Intent(OtherProfilePageActivity.this, ActivityStreamActivity.class);
        startActivity(i);
    }
}