package com.example.postory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.postory.BuildConfig;
import com.example.postory.R;
import com.example.postory.adapters.CommentsAdapter;
import com.example.postory.adapters.LocationAdapter;
import com.example.postory.adapters.PostAdapter;
import com.example.postory.adapters.TagsAdapter;
import com.example.postory.models.CommentModel;
import com.example.postory.models.Post;
import com.example.postory.utils.TimeController;
import com.example.postory.models.TagItem;
import com.google.gson.Gson;
import com.like.LikeButton;
import com.like.OnLikeListener;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SinglePostActivity extends ToolbarActivity{
    public static final String TAG ="SinglePostActivity";

    private Request request;
    private Request likeRequest;
    private OkHttpClient client;
    private String url;
    private SharedPreferences sharedPreferences;
    private String accessToken;
    LikeButton likeButton;
    TextView commentButton;
    String postId;
    Post post;
    Button continueReading;
    CommentsAdapter commentsAdapter;
    ArrayList<CommentModel> commentModels = new ArrayList<>();

    ListView commentsList;



    RecyclerView locationRecyclerView;
    RecyclerView tagRecyclerView;

    TextView opName;
    TextView opTitle;
    TextView dateText;
    TextView locationText;
    TextView sharedDateText;
    ImageView postPicture;
    ImageView editText;
    ImageView profilePicture;
    TextView postText;
    LinearLayout likeLayout;
    TextView likesText;
    String selfId;
    int likeCount = 0;
    boolean liked = false;



    @Override
    protected void goHomeClicked() {

        Intent i = new Intent(SinglePostActivity.this, MainActivity.class);
        finish();
        startActivity(i);
    }

    @Override
    protected void goCreatePostClicked() {
        Intent createPostIntent = new Intent(SinglePostActivity.this, CreatePostActivity.class);
        createPostIntent.putExtra("goal", "create");
        startActivity(createPostIntent);

    }

    @Override
    protected void refreshClicked() {
        return;

    }

    @Override
    protected void goExploreClicked() {
        Intent intent = new Intent(SinglePostActivity.this, ExploreActivity.class);
        startActivity(intent);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);
        super.initToolbar();


        profilePicture = (ImageView) findViewById(R.id.profile_picture);
        likeButton = (LikeButton) findViewById(R.id.like_button);
        commentButton = (TextView) findViewById(R.id.btn_comment);
        opName = (TextView) findViewById(R.id.op_name_field);
        opTitle = (TextView) findViewById(R.id.op_title_field);
        dateText = (TextView) findViewById(R.id.op_date_text);
        locationText = (TextView) findViewById(R.id.op_location_text);
        sharedDateText = (TextView) findViewById(R.id.share_date);
        likeLayout = (LinearLayout) findViewById(R.id.like_layout);
        likesText = (TextView) findViewById(R.id.likes);

        postId = getIntent().getStringExtra("post_id");

        tagRecyclerView = (RecyclerView) findViewById(R.id.tags_list);
        locationRecyclerView= (RecyclerView) findViewById(R.id.locations_list);
        postPicture= (ImageView) findViewById(R.id.post_photo);
        postPicture.setImageResource(R.drawable.placeholder);
        editText = (ImageView) findViewById(R.id.edit_text);
        postText = (TextView) findViewById(R.id.post_story_text_field);
        continueReading = (Button) findViewById(R.id.post_continue_reading);
        commentsList = (ListView) findViewById(R.id.comments_section);



        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeButton.performClick();
            }
        });

        continueReading.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   postText.setMaxLines(1000);
                                                   continueReading.setVisibility(View.GONE);
                                               }
                                           }
        );
        setContinueReadingVisibility(postText,continueReading);



        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editPostIntent = new Intent(SinglePostActivity.this, CreatePostActivity.class);
                editPostIntent.putExtra("id",post.getId() + "");
                editPostIntent.putExtra("goal","edit");
                startActivity(editPostIntent);
            }
        });


        LinearLayoutManager tagLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        tagRecyclerView.setLayoutManager(tagLayoutManager);

        LinearLayoutManager locationLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        locationRecyclerView.setLayoutManager(locationLayoutManager);





        sharedPreferences = getSharedPreferences("MY_APP",MODE_PRIVATE);


        accessToken = sharedPreferences.getString("access_token","");
        selfId = sharedPreferences.getString("user_id","");
        client = new OkHttpClient();
        url = BuildConfig.API_IP + "/post/get/" + postId;
        RequestBody reqbody = RequestBody.create(null, new byte[0]);
        request = new Request.Builder()
                .addHeader("Authorization", "JWT " + accessToken)
                .url(url)
                .build();

        likeRequest = new Request.Builder()
                .addHeader("Authorization", "JWT " + accessToken)
                .url(BuildConfig.API_IP + "/post/like/" + postId )
                .post(reqbody)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i(TAG, "onFailure: ");

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                Log.i(TAG, "onResponse: ");
                Gson gson = new Gson();
                post = gson.fromJson(response.body().string(), Post.class);
                Log.i(TAG, "onResponse: ");

                for(List<Object> singleComment : post.getComments()) {
                    CommentModel model = new CommentModel((String) singleComment.get(1) , "" ,(String)singleComment.get(2));
                    commentModels.add(model);


                }





                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        commentsAdapter =  new CommentsAdapter(SinglePostActivity.this, commentModels);
                        commentsList.setAdapter(commentsAdapter);
                        if(post.getImages().size() != 0) {
                            Glide
                                    .with(SinglePostActivity.this)
                                    .load(post.getImages().get(0))
                                    .placeholder(R.drawable.placeholder)
                                    .apply(new RequestOptions().override(400,400))
                                    .centerCrop()
                                    .into(postPicture);
                        }
                       likeCount =  post.getLikeList().size();


                        setLikeCount();


                        List<List<Object>> list = post.getLikeList();

                        ArrayList<TagItem> tagsList = new ArrayList<>();
                        TagsAdapter tagsAdapter = new TagsAdapter(R.layout.single_tag,tagsList);
                        tagRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        tagRecyclerView.setAdapter(tagsAdapter);

                        if(post.getTags().size() != 0) {
                            for(String tag : post.getTags()) {
                                tagsList.add(new TagItem(tag));
                            }
                        }


                        ArrayList<TagItem> locationList = new ArrayList<>();
                        LocationAdapter locationsAdapter = new LocationAdapter(R.layout.single_tag,locationList);
                        locationRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        locationRecyclerView.setAdapter(locationsAdapter);
                        if(post.getLocations().size() != 0) {
                            for(List<Object> tag : post.getLocations()) {
                                locationList.add(new TagItem((String) tag.get(0)));
                            }
                        }


                        ArrayList<String> likedPeople = new ArrayList<>();
                        for (int i = 0; i < list.size() ; i++) {
                            List<Object> singleLike = list.get(i);

                            if((Double)singleLike.get(0)  == Double.parseDouble(selfId)) {
                                liked = true;
                            }
                            else {
                                likedPeople.add((String) singleLike.get(1));
                            }
                        }

                        if(liked){
                            likeButton.setLiked(true);
                        }



                        if(post.getUserPhoto() != "") {

                            Glide
                                    .with(SinglePostActivity.this)
                                    .load(post.getUserPhoto())
                                    .placeholder(R.drawable.placeholder)
                                    .apply(new RequestOptions().override(400,400))
                                    .centerCrop()
                                    .into(profilePicture);


                        }
                        opName.setText(post.getUsername());
                        postText.setText(post.getStory());
                        opTitle.setText(post.getTitle());
                        List <Integer> yearList = post.getYear();
                        List <Integer> monthList = post.getMonth();
                        List <Integer> dayList = post.getDay();
                        List <Integer> hourList = post.getHour();
                        List <Integer> minuteList = post.getMinute();
                        TimeController t;
                        if(minuteList.size()>0){
                            t = new TimeController(yearList.get(0),yearList.get(1),
                                    monthList.get(0),monthList.get(1),
                                    dayList.get(0),dayList.get(1),
                                    hourList.get(0),hourList.get(1),
                                    minuteList.get(0),minuteList.get(1));
                            t.createDate();
                            String startDateString = t.getDateFormat().format(t.getStartDate());
                            String endDateString = t.getDateFormat().format(t.getEndDate());
                            dateText.setText(startDateString + " - " + endDateString);
                        }
                        else if(dayList.size()>0){
                            t = new TimeController(yearList.get(0),yearList.get(1),
                                    monthList.get(0),monthList.get(1),
                                    dayList.get(0),dayList.get(1));
                            t.createDate();
                            String startDateString = t.getDateFormat().format(t.getStartDate());
                            String endDateString = t.getDateFormat().format(t.getEndDate());
                            dateText.setText(startDateString + " - " + endDateString);
                        }
                        else if(monthList.size()>0){
                            t = new TimeController(yearList.get(0),yearList.get(1),
                                    monthList.get(0),monthList.get(1));
                            t.createDate();
                            String startDateString = t.getDateFormat().format(t.getStartDate());
                            String endDateString = t.getDateFormat().format(t.getEndDate());
                            dateText.setText(startDateString + " - " + endDateString);
                        }
                        else if(yearList.size()>0){
                            t = new TimeController(yearList.get(0),yearList.get(1));
                            t.createDate();
                            String startDateString = t.getDateFormat().format(t.getStartDate());
                            String endDateString = t.getDateFormat().format(t.getEndDate());
                            dateText.setText(startDateString + " - " + endDateString);
                        }
                        else{

                        }
                        if(post.getPostDate() != null) {
                            sharedDateText.setText(formatDate(post.getPostDate()));
                        }

                        if(post.getPostDate() != null) {
                            sharedDateText.setText(formatDate(post.getPostDate()));
                        }

                    }
                });





            }
        });






        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: ");
            }
        });

        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Log.i(TAG, "liked: ");
                client.newCall(likeRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.i(TAG, "onFailure: ");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Log.i(TAG, "onResponse: ");
                        if(response.code() == 200) {
                            likeCount += 1;
                            setLikeCount();

                        }
                    }
                });
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Log.i(TAG, "unLiked: ");
                client.newCall(likeRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.i(TAG, "onFailure: ");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Log.i(TAG, "onResponse: ");
                        if(response.code() == 200) {
                            likeCount -= 1;
                            setLikeCount();

                        }
                    }
                });

            }
        });

    }

    public void sendLikeRequest() {

    }

    public void setLikeCount() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if( likeCount == 0) {
                    likesText.setText("No Likes For This Post Yet :(");
                }

                else if(likeCount == 1) {
                    likesText.setText(likeCount + " person liked this.");
                }
                else {
                    likesText.setText(likeCount + " people liked this.");
                }

            }
        });



    }

    public void setContinueReadingVisibility(final TextView postText, final Button continueReading) {
        postText.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (postText.getLineCount() > postText.getMaxLines()) {
                    continueReading.setVisibility(View.VISIBLE);
                    postText.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return true;
            }
        });

        if (postText.getLineCount() > postText.getMaxLines()) {
            continueReading.setVisibility(View.VISIBLE);
        }
    }

    public String formatDate(Date d) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(d);
    }
}
