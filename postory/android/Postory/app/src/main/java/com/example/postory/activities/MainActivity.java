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

public class MainActivity extends AppCompatActivity {
    private ImageView createPost;
    private ImageView refreshPage;
    private ImageView worldButton;
    private PostAdapter postAdapter;

    private ListView listView;
    public static final int CREATE_POST = 1;
    public static final String TAG = "MainActivity";
    private Post[] posts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        createPost = (ImageView)toolbar.findViewById(R.id.create_post);
        refreshPage = (ImageView)toolbar.findViewById(R.id.refresh_button);
        worldButton = (ImageView) toolbar.findViewById(R.id.world_button);

        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createPostIntent = new Intent(MainActivity.this, CreatePostActivity.class);
                createPostIntent.putExtra("goal","create");
                startActivity(createPostIntent);
            }
        });
        worldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SuperActivityToast.create(MainActivity.this, new Style(), Style.TYPE_BUTTON)
                        .setProgressBarColor(Color.WHITE)
                        .setText("This feature is not available now.")
                        .setDuration(Style.DURATION_LONG)
                        .setFrame(Style.FRAME_LOLLIPOP)
                        .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED))
                        .setAnimations(Style.ANIMATIONS_POP).show();
            }
        });

        listView = (ListView) findViewById(R.id.list_view_posts);


        final OkHttpClient client = new OkHttpClient();
        String url = "http://35.158.95.81:8000/api/post/all";
        final Request request = new Request.Builder()
                .url(url)
                .build();

        refreshPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postAdapter.clear();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.i(TAG, "onFailure: ");



                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Log.i(TAG, "onResponse: ");
                        Gson gson = new Gson();
                        posts = gson.fromJson(response.body().string(),Post[].class);
                        Log.i(TAG, "onResponse: ");
                        ArrayList<PostModel> arrayOfPosts = new ArrayList<PostModel>();
                        postAdapter = new PostAdapter(MainActivity.this,arrayOfPosts);
                        for(Post post : posts) {

                            postAdapter.add(new PostModel(post.getId(),post.getTitle(),post.getStory(),post.getOwner(),post.getTags(),post.getLocations(),post.getImages(),post.getPostDate(),post.getEditDate(),post.getStoryDate(),post.getViewCount()));

                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listView.setAdapter(postAdapter);
                            }
                        });




                    }
                });


            }
        });

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.i(TAG, "onResponse: ");
                Gson gson = new Gson();
                posts = gson.fromJson(response.body().string(),Post[].class);
                Log.i(TAG, "onResponse: ");
                ArrayList<PostModel> arrayOfPosts = new ArrayList<PostModel>();
                postAdapter = new PostAdapter(MainActivity.this,arrayOfPosts);
                for(Post post : posts) {

                    postAdapter.add(new PostModel(post.getId(),post.getTitle(),post.getStory(),post.getOwner(),post.getTags(),post.getLocations(),post.getImages(),post.getPostDate(),post.getEditDate(),post.getStoryDate(),post.getViewCount()));

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(postAdapter);

                    }
                });







            }
        });



/*
        ArrayList<PostModel> arrayOfPosts = new ArrayList<PostModel>();
        Bitmap bmParis = BitmapFactory.decodeResource(getResources(),R.drawable.paris);
        Bitmap bmRock = BitmapFactory.decodeResource(getResources(),R.drawable.rock_pushing);
        Bitmap bmKeke = BitmapFactory.decodeResource(getResources(),R.drawable.post_picture_example);
        String a = toBase64(bmParis);
        String a1 = toBase64(bmRock);
        String a2 = toBase64(bmKeke);

        String post1 = "Proin et erat est. Nunc mollis mi ipsum, a suscipit ligula tempus ut. Aenean non dui ac mi elementum hendrerit in eget orci. Aliquam tristique augue eros, vitae sodales turpis luctus ut. Aenean ex lectus, feugiat sit amet ipsum non, molestie fringilla sapien. Sed varius sit amet nisi id varius. Donec arcu turpis, efficitur vitae dolor et, rutrum fermentum leo. Donec vitae velit in tortor aliquam vulputate. Vivamus et ipsum mattis, fermentum mauris molestie, eleifend sem. Fusce vestibulum convallis ornare. Proin at sem risus. Proin congue ullamcorper libero in interdum.";
        String post2 = " Aliquam erat volutpat. Suspendisse potenti. Donec ornare enim ac est laoreet, at ullamcorper tortor laoreet. Pellentesque tincidunt ullamcorper vulputate. In hac habitasse platea dictumst. Nunc et quam ultrices, malesuada felis id, interdum eros. Sed vitae mauris posuere, tempor augue eu, viverra risus. Vivamus nunc eros, tristique vel purus ut, elementum hendrerit nisi. Aliquam mollis quam at mi consequat faucibus. Praesent eu aliquam nulla.";
        String post3 = "Duis et facilisis dui. Duis vel urna vel quam pharetra aliquam at at erat. Pellentesque at metus quis lacus gravida elementum. Pellentesque quis arcu aliquet, aliquet arcu vel, hendrerit tortor. Aenean non risus convallis, imperdiet ipsum nec, auctor ligula. Proin sed sem quis diam finibus porttitor. Nulla facilisi. In at nunc arcu. Suspendisse id lorem sit amet magna ullamcorper porta. Praesent quis placerat ipsum. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Maecenas tellus lacus, pharetra in mattis id, imperdiet ac nunc. Integer sit amet sapien risus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent sit amet eros vitae ligula tristique varius nec vel lectus.";



        PostModel model1 = new PostModel(a,a1,post1,"Ahmet Ertan");
        PostModel model2 = new PostModel(a1,a2,post2,"Sefa Ertay");
        PostModel model3 = new PostModel(a2,a,post3,"Kutlu Sever");



 */



/*
        PostAdapter postAdapter = new PostAdapter(this,arrayOfPosts);
        postAdapter.add(model1);
        postAdapter.add(model2);
        postAdapter.add(model3);
        listView.setAdapter(postAdapter);


 */


    }


    private String toBase64(Bitmap bm) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos); // bm is the bitmap object
        byte[] b = baos.toByteArray();
        return  Base64.encodeToString(b, Base64.DEFAULT);
    }
}