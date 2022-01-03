package com.example.postory.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.postory.R;
import com.example.postory.models.ActorObjectGeneralModel;
import com.example.postory.models.ActorObjectModel;
import com.example.postory.models.CommentModel;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ActivityStreamAdapter extends ArrayAdapter<ActorObjectGeneralModel> {
    ArrayList<ActorObjectGeneralModel> objects;

    public ActivityStreamAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ActorObjectGeneralModel> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ActorObjectGeneralModel model = getItem(position);


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_activity_stream, parent, false);
        }
        TextView idUser = (TextView) convertView.findViewById(R.id.actor_name);
        TextView sentence = (TextView) convertView.findViewById(R.id.type_name);
        TextView object = (TextView) convertView.findViewById(R.id.object_name);



        idUser.setText(model.getActor().getUsername());

        sentence.setText(formatString(model.getType()));


        switch (model.getType()) {
            case "PostCreate":
            case "PostUpdate":
            case "PostComment":
            case "PostLike":
                object.setText("a post.");
                break;
            case "UserAddPhoto":
                object.setText("a photo.");
                break;
            case "UserFollow":
                object.setText("a user.");
                break;
        }




        return  convertView;
    }

    private String formatString(String type) {
        String formatted = "";
        switch (type){
            case "PostCreate":
                formatted = "has created";
                break;
            case "PostUpdate":
                formatted = "has updated";
                break;
            case "PostComment":
                formatted = "has commented on";
                break;
            case "PostLike":
                formatted = "has liked";
                break;
            case "UserAddPhoto":
                formatted = "has added";
                break;
            case "UserFollow":
                formatted = "has followed";
                break;
        }

        return formatted;
    }

    /*
    PostCreate PostUpdate PostComment PostLike UserAddPhoto UserFollow
     */

}
