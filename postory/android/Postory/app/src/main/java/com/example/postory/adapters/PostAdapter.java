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

import com.example.postory.R;
import com.example.postory.models.PostModel;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PostAdapter extends ArrayAdapter<PostModel> {
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
        ImageView profilePicture = (ImageView) convertView.findViewById(R.id.profile_picture);
        ImageView postPicture = (ImageView) convertView.findViewById(R.id.post_photo);

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

        postText.setText(post.getPostText());
        opName.setText(post.getUserName());
        byte[] imageBytesPost = Base64.decode(post.getPostPicture().getBytes(), Base64.DEFAULT);
        byte[] imageBytesProfile = Base64.decode(post.getProfilePicture().getBytes(), Base64.DEFAULT);
        postPicture.setImageBitmap(BitmapFactory.decodeByteArray(imageBytesPost, 0, imageBytesPost.length));
        profilePicture.setImageBitmap(BitmapFactory.decodeByteArray(imageBytesProfile, 0, imageBytesProfile.length));
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
}
