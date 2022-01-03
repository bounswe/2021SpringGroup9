package com.example.postory.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.postory.BuildConfig;
import com.example.postory.R;
import com.example.postory.adapters.ActivityStreamAdapter;
import com.example.postory.dialogs.DelayedProgressDialog;
import com.example.postory.models.ActorObjectGeneralModel;
import com.example.postory.models.ActorObjectModel;
import com.example.postory.models.Post;
import com.example.postory.models.UserModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OwnActivityStreamFragment extends Fragment {
    private static final String TAG ="OwnActivityFragment";
    private String accessToken;
    private OkHttpClient client;
    private Request request;
    private String url;
    private ListView listView;
    DelayedProgressDialog dialog;


    private SharedPreferences sharedPreferences;
    private ActorObjectModel[] actions;
    private ArrayList<ActorObjectGeneralModel> generalModel = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_own_activity_stream, container, false);
        listView = (ListView) view.findViewById(R.id.list_activity_stream);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("MY_APP",getContext().MODE_PRIVATE);
        accessToken = sharedPreferences.getString("access_token","");
        client = new OkHttpClient();
        dialog = new DelayedProgressDialog();
        url = BuildConfig.API_IP + "/activitystream/own";
        request = new Request.Builder()
                .addHeader("Authorization", "JWT " + accessToken)
                .url(url)
                .build();
        dialog.show(getActivity().getSupportFragmentManager(), "Filtering posts...");
        callActivityStreamAPI();

    }

    private void callActivityStreamAPI() {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.cancel();
                    }
                });
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.cancel();
                    }
                });
                Log.i(TAG, "onResponse: ");
                String respStr = response.body().string();
                Gson gson = new Gson();
                actions = gson.fromJson(respStr, ActorObjectModel[].class);
                Log.i(TAG, "onResponse: ");
                for(ActorObjectModel model : actions) {
                    UserModel objectUser = null;
                    Post objectPost = null;

                    if(model.getType().toLowerCase().contains("post"))
                    {
                        objectUser = null;
                        objectPost = gson.fromJson(model.getObject(),Post.class);
                    }
                    else {
                        objectUser = gson.fromJson(model.getObject(),UserModel.class);
                        objectPost = null;

                    }

                    generalModel.add(new ActorObjectGeneralModel(model.getActor(), objectUser, objectPost,model.getType()));
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ActivityStreamAdapter adapter = new ActivityStreamAdapter(getContext(),0,generalModel);
                        listView.setAdapter(adapter);

                    }
                });


            }
        });
    }


}