package com.example.postory.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.postory.R;
import com.example.postory.activities.ExploreActivity;
import com.example.postory.activities.MainActivity;
import com.example.postory.models.TagItem;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.ViewHolder> {
    public static final String TAG = "";
    private int listItemLayout;
    private ArrayList<TagItem> itemList;
    static Context context;

    public KeywordAdapter(int layoutId, ArrayList<TagItem> itemList, Context context) {
        listItemLayout = layoutId;
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
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
                ((ExploreActivity)context).removeKeyword(holder.getLayoutPosition());
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
