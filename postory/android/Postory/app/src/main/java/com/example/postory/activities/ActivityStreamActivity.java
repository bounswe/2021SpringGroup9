package com.example.postory.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.postory.R;
import com.example.postory.adapters.ViewPagerAdapter;
import com.example.postory.fragments.FollowedActivityStreamFragment;
import com.example.postory.fragments.OwnActivityStreamFragment;
import com.google.android.material.tabs.TabLayout;


/**
 *
 * This is the activity, on which a user can see the activities performed by him/her or the users that he/she follows.
 * Activities include creating, editing a post, commenting on a post, liking a post, adding a photo and
 * following a user.
 *
 * @author melihozcan
 *
 *
 */

public class ActivityStreamActivity extends ToolbarActivity  {


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SharedPreferences sharedPreferences;

    /**
     * Sends the user to the Homepage.
     */
    @Override
    protected void goHomeClicked() {
        Intent i = new Intent(ActivityStreamActivity.this, MainActivity.class);
        finish();
        startActivity(i);

    }

    /**
     *
     * Sends the user to the Create Post Page.
     *
     *
     */
    @Override
    protected void goCreatePostClicked() {
        Intent createPostIntent = new Intent(ActivityStreamActivity.this, CreatePostActivity.class);
        createPostIntent.putExtra("goal", "create");
        startActivity(createPostIntent);

    }

    @Override
    protected void refreshClicked() {

    }

    /**
     * Sends the user to the Explore page.
     */
    @Override
    protected void goExploreClicked() {
        Intent i = new Intent(ActivityStreamActivity.this, ExploreActivity.class);
        startActivity(i);
    }

    /**
     * Sends the user to the Profile page.
     */
    @Override
    protected void goProfileClicked() {
        Intent i = new Intent(ActivityStreamActivity.this, SelfProfilePageActivity.class);
        startActivity(i);
    }

    /**
     * Logs out the user by clearing the entries in the shared preferences, this way app doesn't redirect the user to
     * home page, instead user is prompted to enter the credentials.
     *
     */
    @Override
    protected void logoutClicked() {
        sharedPreferences = getSharedPreferences("MY_APP",MODE_PRIVATE);
        sharedPreferences.edit().remove("valid_until").apply();
        sharedPreferences.edit().remove("user_id").apply();
        sharedPreferences.edit().remove("access_token").apply();
        Intent i = new Intent(ActivityStreamActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
    @Override
    protected void goActivitiesClicked() {

    }


    /**
     * The viewpager is set, which enables users to switch between different tabs.
     * Two different fragments are added to the ViewPagerAdapter, in order to perform different activities for the own and
     * followed activity streams.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_stream);
        super.initToolbar();
        sharedPreferences = getSharedPreferences("MY_APP",MODE_PRIVATE);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);


        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        vpAdapter.addFragment(new OwnActivityStreamFragment(),"OWN");
        vpAdapter.addFragment(new FollowedActivityStreamFragment(),"FOLLOWED");

        viewPager.setAdapter(vpAdapter);


    }
}
