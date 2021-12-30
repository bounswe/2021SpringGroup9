package com.example.postory.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.postory.BuildConfig;
import com.example.postory.R;
import com.example.postory.adapters.UserAdapter;
import com.example.postory.models.Post;
import com.example.postory.models.UserModel;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

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

public class UserSearchActivity extends ToolbarActivity {
    private String apiString;
    private String accessToken;
    private final String TAG = "UserSearchActivity";

    private OkHttpClient client;
    private ListView userList;

    @Override
    protected void goProfileClicked() {
        Intent i = new Intent(UserSearchActivity.this, SelfProfilePageActivity.class);
        startActivity(i);
    }

    @Override
    protected void logoutClicked() {
        SharedPreferences sharedPreferences = getSharedPreferences("MY_APP", MODE_PRIVATE);
        sharedPreferences.edit().remove("valid_until").apply();
        sharedPreferences.edit().remove("user_id").apply();
        sharedPreferences.edit().remove("access_token").apply();
        Intent i = new Intent(UserSearchActivity.this, LoginActivity.class);
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
        Intent createPostIntent = new Intent(UserSearchActivity.this, CreatePostActivity.class);
        createPostIntent.putExtra("goal", "create");
        startActivity(createPostIntent);
    }

    @Override
    protected void refreshClicked() {
    }

    @Override
    protected void goExploreClicked() {

        Intent intent = new Intent(UserSearchActivity.this, ExploreActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
        super.initToolbar();
        userList = (ListView) findViewById(R.id.userList);
        apiString = BuildConfig.API_IP+"/user/search/";

        SharedPreferences sharedPreferences = getSharedPreferences("MY_APP", MODE_PRIVATE);
        accessToken = sharedPreferences.getString("access_token","");
        client = new OkHttpClient();
        Intent intent = getIntent();
        String usernameString = intent.getStringExtra("query");
        listUsers(usernameString);
    }
    private void listUsers(String usernameString){
        String url = apiString + usernameString;
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
                Gson gson = new Gson();
                String respString = response.body().string();
                UserModel[] users = gson.fromJson(respString, UserModel[].class);
                Log.i(TAG, "onResponse: ");
                ArrayList<UserModel> userArray = new ArrayList<>();
                Collections.addAll(userArray, users);
                UserAdapter adapter = new UserAdapter(UserSearchActivity.this, userArray);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        userList.setAdapter(adapter);
                    }
                });
            }
        });

    }
}