package com.example.postory.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.postory.R;
import com.example.postory.activities.ExploreActivity;
import com.example.postory.activities.OtherProfilePageActivity;
import com.example.postory.activities.SelfProfilePageActivity;
import com.example.postory.activities.SinglePostActivity;
import com.example.postory.models.ActorObjectGeneralModel;
import com.example.postory.models.ActorObjectModel;
import com.example.postory.models.CommentModel;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.util.ArrayList;


/**
 * The adapter to display data dynamically on the listview.
 *
 * @author melihozcan
 *
 */
public class ActivityStreamAdapter extends ArrayAdapter<ActorObjectGeneralModel> {
    ArrayList<ActorObjectGeneralModel> objects;
    private SharedPreferences sharedPreferences;
    private Context context;
    String selfId;

    /**
     * The constructor for the adapter.
     * @param context the activity context
     * @param resource 0
     * @param objects the arraylist of models to populate the listview.
     */
    public ActivityStreamAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ActorObjectGeneralModel> objects) {
        super(context, resource, objects);
        sharedPreferences = context.getSharedPreferences("MY_APP",context.MODE_PRIVATE);
        selfId = sharedPreferences.getString("user_id","");
        this.context = context;
        this.objects = objects;
    }


    /**
     *
     * This method formats each childview according to the model at that position.
     *
     *
     * @param position the position of the item
     * @param convertView the view for each item position
     * @param parent the parent view
     * @return the formatted view
     */
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


        object.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (model.getType()) {
                    case "PostCreate":
                    case "PostUpdate":
                    case "PostComment":
                    case "PostLike":
                        Intent intent = new Intent(context, SinglePostActivity.class);
                        intent.putExtra("post_id",model.getReceiverPost().getId() + "");
                        context.startActivity(intent);

                        break;
                    case "UserAddPhoto":
                    case "UserFollow":
                        Intent i;
                        if (selfId.equals(model.getReceiverUser().getId()  + "")){
                            i = new Intent(context, SelfProfilePageActivity.class);
                        }
                        else{
                            i = new Intent(context, OtherProfilePageActivity.class);
                            i.putExtra("user_id",model.getReceiverUser().getId() + "");
                        }
                        context.startActivity(i);
                        break;
                }
            }
        });



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


    /**
     * Format the string for the messages to be displayed on the activity stream page.
     * @param type the activity type
     * @return the message which will be shown
     */
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
