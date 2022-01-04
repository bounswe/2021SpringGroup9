package com.example.postory.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.example.postory.R;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

public abstract class ToolbarActivity extends AppCompatActivity {
    protected Toolbar toolbar;
    protected SearchView searchView;
    protected SearchManager searchManager;

    protected abstract void goHomeClicked();

    protected abstract void goCreatePostClicked();

    protected abstract void refreshClicked();

    protected abstract void goExploreClicked();

    protected abstract void goProfileClicked();

    protected abstract void logoutClicked();

    protected abstract void goActivitiesClicked();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getApplicationContext(), UserSearchActivity.class);
                intent.putExtra("query",query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return true;

            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

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

            case R.id.go_profile:
                goProfileClicked();
                return true;
            case R.id.logout:
                logoutClicked();
                return true;

            case R.id.go_activities:
                goActivitiesClicked();
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