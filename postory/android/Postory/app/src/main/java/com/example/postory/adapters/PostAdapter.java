package com.example.postory.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.postory.R;
import com.example.postory.activities.CreatePostActivity;
import com.example.postory.activities.MainActivity;
import com.example.postory.models.PostModel;

import com.example.postory.models.TagItem;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PostAdapter extends ArrayAdapter<PostModel> {
    private String imageUrl;
    private String location;
    private Context context;
    public PostAdapter(Context context, ArrayList<PostModel> posts) {
        super(context, 0, posts);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final PostModel post = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_post, parent, false);
        }
        TextView opName = (TextView) convertView.findViewById(R.id.op_name_field);
        TextView opTitle = (TextView) convertView.findViewById(R.id.op_title_field);
        TextView dateText = (TextView) convertView.findViewById(R.id.op_date_text);
        TextView locationText = (TextView) convertView.findViewById(R.id.op_location_text);
        TextView sharedDateText = (TextView) convertView.findViewById(R.id.share_date);

        RecyclerView tagRecyclerView = (RecyclerView) convertView.findViewById(R.id.tags_list);
        RecyclerView locationRecyclerView = (RecyclerView) convertView.findViewById(R.id.locations_list);
        LinearLayoutManager tagLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        tagRecyclerView.setLayoutManager(tagLayoutManager);

        LinearLayoutManager locationLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        locationRecyclerView.setLayoutManager(locationLayoutManager);

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

        ImageView postPicture = (ImageView) convertView.findViewById(R.id.post_photo);
        postPicture.setImageResource(R.drawable.placeholder);
        ImageView editText = (ImageView) convertView.findViewById(R.id.edit_text);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editPostIntent = new Intent(context, CreatePostActivity.class);
                editPostIntent.putExtra("id",post.getId() + "");
                editPostIntent.putExtra("goal","edit");
                context.startActivity(editPostIntent);
            }
        });

        final TextView postText = (TextView) convertView.findViewById(R.id.post_story_text_field);
        final Button continueReading = (Button) convertView.findViewById(R.id.post_continue_reading);

        continueReading.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   postText.setMaxLines(1000);
                                                   continueReading.setVisibility(View.GONE);
                                               }
                                           }
        );
        setContinueReadingVisibility(postText,continueReading);


        if(post.getImages().size() != 0) {
            Glide
                    .with(getContext())
                    .load(post.getImages().get(0))
                    .placeholder(R.drawable.placeholder)
                    .apply(new RequestOptions().override(400,400))
                    .centerCrop()
                    .into(postPicture);
        }



        opName.setText(post.getOwner());
        postText.setText(post.getStory());
        opTitle.setText(post.getTitle());
        if(post.getStoryDate() != null) {


            dateText.setText(formatDate(post.getStoryDate()));
        }

        if(post.getPostDate() != null) {
            sharedDateText.setText(formatDate(post.getPostDate()));
        }

        return convertView;

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
