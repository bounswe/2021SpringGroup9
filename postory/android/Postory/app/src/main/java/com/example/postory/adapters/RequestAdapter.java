package com.example.postory.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.postory.BuildConfig;
import com.example.postory.R;
import com.example.postory.activities.OtherProfilePageActivity;
import com.example.postory.models.UserModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestAdapter extends ArrayAdapter<UserModel> {
    private Context context;
    private TextView username;
    private String userId;
    private Button acceptButton;
    private Button rejectButton;
    private String accessToken;
    private String TAG = "RequestAdapter";

    public RequestAdapter(Context context, ArrayList<UserModel> requests) {
        super(context,0,requests);
        this.context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences("MY_APP", MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id","");
        accessToken = sharedPreferences.getString("access_token","");
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final UserModel user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_follow_request, parent, false);
        }

        acceptButton = (Button) convertView.findViewById(R.id.acceptButton);
        rejectButton = (Button) convertView.findViewById(R.id.rejectButton);
        username = (TextView) convertView.findViewById(R.id.username);

        username.setText(user.getUsername());
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, OtherProfilePageActivity.class);
                i.putExtra("user_id",""+user.getId());
                context.startActivity(i);
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = BuildConfig.API_IP + "/user/acceptRequest/" + user.getId();

                MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
                MultipartBody.Builder builder = bodyBuilder.setType(MultipartBody.FORM);
                RequestBody body = RequestBody.create(null, new byte[]{});

                Request followRequest = new Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("Authorization", "JWT " + accessToken)
                        .build();
                final OkHttpClient client = new OkHttpClient();
                client.newCall(followRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.i(TAG, "onFailure: ");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if(response.isSuccessful()){
                            Log.d(TAG,"Request accepted");
                        }
                    }
                });

            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String url = BuildConfig.API_IP + "/user/declineRequest/" + user.getId();

                MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
                MultipartBody.Builder builder = bodyBuilder.setType(MultipartBody.FORM);
                RequestBody body = RequestBody.create(null, new byte[]{});

                Request followRequest = new Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("Authorization", "JWT " + accessToken)
                        .build();
                final OkHttpClient client = new OkHttpClient();
                client.newCall(followRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.i(TAG, "onFailure: ");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if(response.isSuccessful()){
                            Log.d(TAG,"Request rejected");
                        }
                    }
                });

            }
        });
        return convertView;

    }
}
