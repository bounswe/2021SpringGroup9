package com.example.postory.activities;

import android.os.Bundle;
import com.example.postory.R;

public class SinglePostActivity extends ToolbarActivity{
    @Override
    protected void goHomeClicked() {

    }

    @Override
    protected void goCreatePostClicked() {

    }

    @Override
    protected void refreshClicked() {

    }

    @Override
    protected void goExploreClicked() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);
        super.initToolbar();

    }
}
