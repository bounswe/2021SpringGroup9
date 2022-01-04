package com.example.postory.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.postory.BuildConfig;
import com.example.postory.R;
import com.example.postory.activities.CreatePostActivity;
import com.example.postory.activities.ExploreActivity;
import com.example.postory.activities.MainActivity;
import com.example.postory.dialogs.DelayedProgressDialog;
import com.example.postory.models.TagItem;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TagFilterAdapter extends RecyclerView.Adapter<TagFilterAdapter.ViewHolder> {
    public static final String TAG = "TagFilterAdapter";
    private int listItemLayout;
    private ArrayList<TagItem> itemList;
    static Context context;

    private Request request;
    private Request requestFilter;
    private OkHttpClient client;
    private String url;
    private String accessToken;
    private DelayedProgressDialog dialog;

    public TagFilterAdapter(int layoutId, ArrayList<TagItem> itemList, Context context) {
        listItemLayout = layoutId;
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        client = new OkHttpClient();
        accessToken = context.getSharedPreferences("MY_APP",context.MODE_PRIVATE).getString("access_token","");
        dialog = new DelayedProgressDialog();
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Button item = holder.tag;
        Button removeTag = holder.removeTag;
        removeTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ExploreActivity)context).removeTag(holder.getLayoutPosition());
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = BuildConfig.API_IP + "/post/related/" + item.getText().toString().replace("#","");
                request = new Request.Builder()
                        .addHeader("Authorization", "JWT " + accessToken)
                        .url(url)
                        .build();
                dialog.show(((ExploreActivity)context).getSupportFragmentManager(), "Filtering posts...");

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        dialog.cancel();
                        Log.i(TAG, "onFailure: ");
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                        ((ExploreActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.cancel();
                                Log.i(TAG, "onResponse: ");
                                String [] tagsRelated = new String[0];
                                try {
                                    tagsRelated = new Gson().fromJson(response.body().string(),String[].class);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String tags = "";
                                for (int i = 0; i <tagsRelated.length ; i++) {
                                    String atag = tagsRelated[i];
                                    if(i != tagsRelated.length - 1) {
                                        tags += (atag + " - " );
                                    }
                                    else {
                                        tags += atag;
                                    }

                                }

                                SuperActivityToast.create(context, new Style(), Style.TYPE_BUTTON)
                                        .setProgressBarColor(Color.WHITE)
                                        .setText((tags.equals("")) ? "No related tags for this tag!" : tags)
                                        .setDuration(Style.DURATION_LONG)
                                        .setFrame(Style.FRAME_LOLLIPOP)
                                        .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_DEEP_ORANGE))
                                        .setAnimations(Style.ANIMATIONS_POP).show();

                            }
                        });


                    }
                });


            }
        });

        item.setText("#" +itemList.get(position).getTag());

    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        public Button tag;
        public Button removeTag;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tag = (Button) itemView.findViewById(R.id.txt_tag);
            removeTag = (Button) itemView.findViewById(R.id.remove_tag);

        }

    }
}
