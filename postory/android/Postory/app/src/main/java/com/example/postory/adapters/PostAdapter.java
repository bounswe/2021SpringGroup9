package com.example.postory.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.postory.R;
import com.example.postory.models.PostModel;

import java.util.ArrayList;

public class PostAdapter extends ArrayAdapter<PostModel> {
    public PostAdapter(Context context, ArrayList<PostModel> posts) {
        super(context,0,posts);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PostModel post = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_post, parent, false);
        }
        TextView postText = (TextView) convertView.findViewById(R.id.post_story_text_field);
        TextView opName = (TextView) convertView.findViewById(R.id.op_name_field);
        ImageView profilePicture = (ImageView) convertView.findViewById(R.id.profile_picture);
        ImageView postPicture = (ImageView) convertView.findViewById(R.id.post_photo);
        postText.setText(post.getPostText());
        opName.setText(post.getUserName());
        byte[] imageBytesPost = Base64.decode(post.getPostPicture().getBytes(),Base64.DEFAULT);
        byte[] imageBytesProfile = Base64.decode(post.getProfilePicture().getBytes(),Base64.DEFAULT);
        postPicture.setImageBitmap(BitmapFactory.decodeByteArray(imageBytesPost,0,imageBytesPost.length));
        profilePicture.setImageBitmap(BitmapFactory.decodeByteArray(imageBytesProfile,0,imageBytesProfile.length));
        return convertView;

    }
}
