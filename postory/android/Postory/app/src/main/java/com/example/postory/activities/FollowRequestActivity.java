package com.example.postory.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.postory.BuildConfig;
import com.example.postory.R;
import com.example.postory.adapters.RequestAdapter;
import com.example.postory.adapters.UserAdapter;
import com.example.postory.models.UserModel;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

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

/**
 * The activity for accepting or declining follow requests. It can be reached from SelfProfilePageActivity.
 * If there are no pending requests for the user, the user is warned that there is no follow request.
 * Uses the toolbar.
 * @author niyaziulke
 */
public class FollowRequestActivity extends ToolbarActivity {
    private SharedPreferences sharedPreferences;
    private ListView requestsListView;
    private TextView noResultWarning;
    private String accessToken;
    private String TAG = "FollowRequestActivity";
    private OkHttpClient client;


    @Override
    protected void goHomeClicked() {
        Intent i = new Intent(FollowRequestActivity.this, MainActivity.class);
        finish();
        startActivity(i);
    }

    @Override
    protected void goCreatePostClicked() {
        Intent createPostIntent = new Intent(FollowRequestActivity.this, CreatePostActivity.class);
        createPostIntent.putExtra("goal", "create");
        startActivity(createPostIntent);
    }

    @Override
    protected void refreshClicked() {

    }

    @Override
    protected void goExploreClicked() {
        Intent i = new Intent(FollowRequestActivity.this, ExploreActivity.class);
        startActivity(i);
    }

    @Override
    protected void goProfileClicked() {
        Intent i = new Intent(FollowRequestActivity.this, SelfProfilePageActivity.class);
        startActivity(i);
    }

    @Override
    protected void logoutClicked() {
        sharedPreferences = getSharedPreferences("MY_APP",MODE_PRIVATE);
        sharedPreferences.edit().remove("valid_until").apply();
        sharedPreferences.edit().remove("user_id").apply();
        sharedPreferences.edit().remove("access_token").apply();
        Intent i = new Intent(FollowRequestActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    protected void goActivitiesClicked() {
        Intent i = new Intent(FollowRequestActivity.this, ActivityStreamActivity.class);
        startActivity(i);
    }

    /**
     * Triggered when the activity is first created, sets things up.
     * @param savedInstanceState The state of instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_request);
        sharedPreferences = getSharedPreferences("MY_APP",MODE_PRIVATE);
        super.initToolbar();
        noResultWarning = (TextView) findViewById(R.id.noResults);
        requestsListView = (ListView) findViewById(R.id.requestList);

        client = new OkHttpClient();
        // api request url
        String url = BuildConfig.API_IP+"/user/getRequests";
        Request getRequests = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "JWT " + accessToken)
                .build();

        client.newCall(getRequests).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Gson gson = new Gson();
                try {
                    String respString = response.body().string();

                    // Convert the json to UserModel class
                    UserModel[] requests = gson.fromJson(respString, UserModel[].class);
                    Log.i(TAG, "onResponse: ");
                    ArrayList<UserModel> requestArray = new ArrayList<>();
                    Collections.addAll(requestArray, requests);
                    if (requestArray.size() != 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // No need for this warning
                                noResultWarning.setVisibility(View.GONE);
                            }
                        });
                    }
                    RequestAdapter adapter = new RequestAdapter(FollowRequestActivity.this, requestArray);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Set the adapter for the ListView
                            requestsListView.setAdapter(adapter);
                        }
                    });
                }catch(Exception e){

                }

            }
        });

    }
}