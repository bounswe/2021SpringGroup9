package com.example.postory.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.postory.R;
import com.example.postory.models.TagItem;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Lists the tags of a post.
 * @author niyaziulke
 */
public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {
    public static final String TAG = "";
    private int listItemLayout;
    private ArrayList<TagItem> itemList;

    public TagsAdapter(int layoutId, ArrayList<TagItem> itemList) {
        listItemLayout = layoutId;
        this.itemList = itemList;
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
        TextView item = holder.tag;
        item.setText("#" +itemList.get(position).getTag());

    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tag;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tag = (TextView) itemView.findViewById(R.id.txt_tag);

        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick: ");
        }
    }
}
