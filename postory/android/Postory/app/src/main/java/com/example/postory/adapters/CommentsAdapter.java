package com.example.postory.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.postory.R;
import com.example.postory.models.CommentModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CommentsAdapter extends ArrayAdapter<CommentModel> {

    private Context context;

    public CommentsAdapter(@NonNull Context context, @NonNull ArrayList<CommentModel> commentModels) {
        super(context, 0, commentModels);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final CommentModel model = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_comment, parent, false);
        }

        TextView commentator = (TextView) convertView.findViewById(R.id.commentator_name);
        TextView comment = (TextView) convertView.findViewById(R.id.comment);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.profile_picture);
        comment.setText(model.getComment());
        commentator.setText(model.getUsername());


        return convertView;
    }
}
