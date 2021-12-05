package com.example.postory.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.postory.R;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

public abstract class ToolbarActivity extends AppCompatActivity {
    protected Toolbar toolbar;

    protected abstract void goHomeClicked();

    protected abstract void goCreatePostClicked();

    protected abstract void refreshClicked();

    protected abstract void goExploreClicked();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refreshClicked();
                return true;

            case R.id.go_create_post:
                goCreatePostClicked();
                return true;

            case R.id.go_explore:
                goExploreClicked();
                return true;

            case R.id.go_home:
                goHomeClicked();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
    }


}