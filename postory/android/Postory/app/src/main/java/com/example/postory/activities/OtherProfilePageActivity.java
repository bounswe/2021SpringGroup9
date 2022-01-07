package com.example.postory.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.example.postory.models.OtherUser;
import com.example.postory.dialogs.DelayedProgressDialog;
import com.example.postory.models.Post;
import com.example.postory.models.UserModel;
import com.example.postory.models.UserGeneralModel;
import com.example.postory.utils.UserModelConverter;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
/**
 * The activity for profile pages of other users.
 * The users can send other people's posts, number of followers, number of followings, number of posts, etc.
 * The users can send the other user follow request or report her.
 * Uses the toolbar.
 * @author niyaziulke
 */
public class OtherProfilePageActivity extends ToolbarActivity {
    private PostAdapter postAdapter;
    private Request requestPosts;
    private Request requestUserData;
    private UserModel thisUserHelper;
    private UserGeneralModel thisUser;
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

    private DelayedProgressDialog dialog;

    /**
     * Check ToolbarActivity
     */
    @Override
    protected void goProfileClicked() {
        Intent i = new Intent(OtherProfilePageActivity.this, SelfProfilePageActivity.class);
        startActivity(i);
    }

    /**
     * Check ToolbarActivity
     */
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

    /**
     * Check ToolbarActivity
     */
    @Override
    protected void goHomeClicked() {
        Intent intent = new Intent(OtherProfilePageActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Check ToolbarActivity
     */
    @Override
    protected void goCreatePostClicked() {
        Intent intent = new Intent(OtherProfilePageActivity.this, CreatePostActivity.class);
        intent.putExtra("goal", "create");
        startActivity(intent);
    }

    /**
     * Check ToolbarActivity
     */
    @Override
    protected void refreshClicked() {
        return;
    }

    @Override
    protected void goExploreClicked() {
        Intent intent = new Intent(OtherProfilePageActivity.this, ExploreActivity.class);
        startActivity(intent);
    }

    /**
     * Triggered when the activity is first created, sets things up.
     * @param savedInstanceState The state of instance
     */
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

        dialog = new DelayedProgressDialog();
        sharedPreferences = getSharedPreferences("MY_APP", MODE_PRIVATE);
        accessToken = sharedPreferences.getString("access_token", "");
        int viewerId = Integer.parseInt(sharedPreferences.getString("user_id", ""));
        String url1 = BuildConfig.API_IP + "/user/get/" + userId;
        client = new OkHttpClient();
        // Request to get the data of the user.
        requestUserData = new Request.Builder()
                .url(url1)
                .addHeader("Authorization", "JWT " + accessToken)
                .build();
        // Make the request.
        client.newCall(requestUserData).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Gson gson = new Gson();
                String respString = response.body().string();
                try {
                    JSONObject jsonResponse = new JSONObject(respString);
                    if (jsonResponse.getBoolean("isPrivate")) {
                        try {
                            thisUser = gson.fromJson(respString, UserGeneralModel.class);
                        } catch (Exception e) {
                            thisUserHelper = gson.fromJson(respString, UserModel.class);
                            thisUser = UserModelConverter.convert(thisUserHelper);
                        }
                    } else {
                        thisUserHelper = gson.fromJson(respString, UserModel.class);
                        thisUser = UserModelConverter.convert(thisUserHelper);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Fill the layout according to the fields.
                        setUserFields();
                        // Check if the user can follow or unfollow and change the layout accordingly.
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

        // Make a request to the backend for the posts of this user.
        callForPosts();
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followButtonClicked();
            }
        });
        // Report icon onClick defined
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show(getSupportFragmentManager(),"The user is being reported...");
                String url = BuildConfig.API_IP + "/user/report/user/" + userId;
                MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
                MultipartBody.Builder builder = bodyBuilder.setType(MultipartBody.FORM);
                RequestBody body = RequestBody.create(null, new byte[]{});

                Request reportRequest = new Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("Authorization", "JWT " + accessToken)
                        .build();

                // Send the report request.
                client.newCall(reportRequest).enqueue(new Callback() {
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
                        if (response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.cancel();
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

    /**
     * Makes an API call for posts of this user.
     */
    private void callForPosts() {
        String url2 = BuildConfig.API_IP + "/post/all/user/" + userId;

        requestPosts = new Request.Builder()
                .url(url2)
                .addHeader("Authorization", "JWT " + accessToken)
                .build();

        callAllPosts();
    }

    /**
     * If the profile of the user is public, quickly follows it without sending a request.
     */
    @SuppressLint("DefaultLocale")
    private void quickFollow() {
        if (!followed) {
            followed = true;
            followButton.setText("Unfollow");
            try {
                String followedCount = followedBy.getText().toString();
                // Update followed by info.
                followedBy.setText(String.format("%d", Integer.parseInt(followedCount) + 1));
            } catch (Exception ignored) {


            }

        }
    }

    /**
     * Unfollows the user.
     */
    @SuppressLint("DefaultLocale")
    private void unfollow() {
        if (followed) {
            followed = false;
            followButton.setText("Follow");
            try {
                String followedCount = followedBy.getText().toString();
                followedBy.setText(String.format("%d", Integer.parseInt(followedCount) - 1));
            } catch (Exception ignored) {

            }
        }

    }


    /**
     * Fills the layout according to the information that comes from the API.
     */
    private void setUserFields() {
        name.setText(thisUser.getName());
        surname.setText(thisUser.getSurname());
        username.setText(thisUser.getUsername());
        followedBy.setText("" + thisUser.getFollowerUsers().size());
        following.setText("" + thisUser.getFollowedUsers().size());
        try {
            numPosts.setText("" + thisUser.getPosts().size());
            // If the user has a profile image, show it.
            Glide
                    .with(OtherProfilePageActivity.this)
                    .load(thisUser.getUserPhoto())
                    .placeholder(R.drawable.placeholder)
                    .apply(new RequestOptions().override(400, 400))
                    .centerCrop()
                    .into(profilePicture);
        } catch (Exception e) {

        }

    }

    /**
     * Handles follow button click operation
     */
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

        // Send the follow request.
        client.newCall(followRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!followed) {
                            if (response.isSuccessful()) {
                                if (thisUser.isPrivate()) {
                                    // Inform the user that follow request is successfully sent.
                                    SuperActivityToast.create(OtherProfilePageActivity.this, new Style(), Style.TYPE_BUTTON)
                                            .setProgressBarColor(Color.WHITE)
                                            .setText("Follow request is sent.")
                                            .setDuration(Style.DURATION_LONG)
                                            .setFrame(Style.FRAME_LOLLIPOP)
                                            .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_BLUE))
                                            .setAnimations(Style.ANIMATIONS_POP).show();
                                    followButton.setVisibility(View.INVISIBLE);
                                } else {
                                    // Inform the user that the user now follows the owner of this profile.
                                    SuperActivityToast.create(OtherProfilePageActivity.this, new Style(), Style.TYPE_BUTTON)
                                            .setProgressBarColor(Color.WHITE)
                                            .setText("You are now following this user.")
                                            .setDuration(Style.DURATION_LONG)
                                            .setFrame(Style.FRAME_LOLLIPOP)
                                            .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_BLUE))
                                            .setAnimations(Style.ANIMATIONS_POP).show();

                                    quickFollow();

                                }
                            } else {
                                // Inform the user that the follow request could not be sent because of a pending request.
                                SuperActivityToast.create(OtherProfilePageActivity.this, new Style(), Style.TYPE_BUTTON)
                                        .setProgressBarColor(Color.WHITE)
                                        .setText("You cannot send a follow request.")
                                        .setDuration(Style.DURATION_LONG)
                                        .setFrame(Style.FRAME_LOLLIPOP)
                                        .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED))
                                        .setAnimations(Style.ANIMATIONS_POP).show();
                            }
                        } else {
                            if (response.isSuccessful()) {
                                // Inform the user that unfollow operation is successful.
                                SuperActivityToast.create(OtherProfilePageActivity.this, new Style(), Style.TYPE_BUTTON)
                                        .setProgressBarColor(Color.WHITE)
                                        .setText("User unfollowed.")
                                        .setDuration(Style.DURATION_LONG)
                                        .setFrame(Style.FRAME_LOLLIPOP)
                                        .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_BLUE))
                                        .setAnimations(Style.ANIMATIONS_POP).show();

                                unfollow();
                            }


                        }
                    }
                });

            }
        });
    }


    /**
     * Method that calls for all the posts of this user.
     */
    private void callAllPosts() {
        client.newCall(requestPosts).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (response.isSuccessful()) {
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
                            // Put the posts to the listView.
                            listView.setAdapter(postAdapter);
                        }
                    });
                }
            }

        });
    }

    /**
     * Check ToolbarActivity
     */
    @Override
    protected void goActivitiesClicked() {
        Intent i = new Intent(OtherProfilePageActivity.this, ActivityStreamActivity.class);
        startActivity(i);
    }
}