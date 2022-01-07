package com.example.postory.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.postory.R;
import com.example.postory.activities.OtherProfilePageActivity;
import com.example.postory.activities.SelfProfilePageActivity;
import com.example.postory.models.UserModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Lists the resulting users for UserSearchActivity
 * @author niyaziulke
 */
public class UserAdapter extends ArrayAdapter<UserModel> {
    private Context context;
    private ImageView profilePicture;
    private ImageView goProfileButton;
    private TextView username;
    private String userId;

    public UserAdapter(@NonNull Context context, ArrayList<UserModel> users) {
        super(context, 0, users);
        this.context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences("MY_APP", MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id","");
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final UserModel user = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_info, parent, false);
        }
        profilePicture = (ImageView) convertView.findViewById(R.id.profilePicture);
        goProfileButton = (ImageView) convertView.findViewById(R.id.goProfileButton);
        username = (TextView) convertView.findViewById(R.id.username);

        String userPhoto = user.getUserPhoto();
        if (userPhoto!=null && !userPhoto.equals("")) {
            Glide
                    .with(getContext())
                    .load(userPhoto)
                    .placeholder(R.drawable.placeholder)
                    .apply(new RequestOptions().override(80, 80))
                    .centerCrop()
                    .into(profilePicture);
        }
        username.setText(user.getUsername());

        goProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                if (userId.equals(user.getId())){
                    i = new Intent(context, SelfProfilePageActivity.class);
                }
                else{
                    i = new Intent(context, OtherProfilePageActivity.class);
                    i.putExtra("user_id",""+user.getId());
                }
                context.startActivity(i);
            }
        });
        return convertView;

    }
}
