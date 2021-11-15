package com.example.postory.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
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

import com.bumptech.glide.Glide;
import com.example.postory.R;
import com.example.postory.models.PostModel;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostAdapter extends ArrayAdapter<PostModel> {
    private String imageUrl;
    private String location;
    public PostAdapter(Context context, ArrayList<PostModel> posts) {
        super(context, 0, posts);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PostModel post = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_post, parent, false);
        }
        TextView opName = (TextView) convertView.findViewById(R.id.op_name_field);
        TextView opTitle = (TextView) convertView.findViewById(R.id.op_title_field);
        TextView dateText = (TextView) convertView.findViewById(R.id.op_date_text);
        TextView locationText = (TextView) convertView.findViewById(R.id.op_location_text);

        ImageView postPicture = (ImageView) convertView.findViewById(R.id.post_photo);
        postPicture.setImageResource(R.drawable.placeholder);

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
                    .centerCrop()
                    .into(postPicture);
        }



        opName.setText(post.getOwner());
        postText.setText(post.getStory());
        opTitle.setText(post.getTitle());
        dateText.setText(formatDate(post.getStoryDate()));
        if(post.getLocations().size() != 0) {
            locationText.setText((String) post.getLocations().get(0).get(0));
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
