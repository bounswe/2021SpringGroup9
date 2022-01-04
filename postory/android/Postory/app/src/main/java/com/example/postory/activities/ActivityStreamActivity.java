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

public class ActivityStreamActivity extends ToolbarActivity  {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SharedPreferences sharedPreferences;
    @Override
    protected void goHomeClicked() {
        Intent i = new Intent(ActivityStreamActivity.this, MainActivity.class);
        finish();
        startActivity(i);

    }

    @Override
    protected void goCreatePostClicked() {
        Intent createPostIntent = new Intent(ActivityStreamActivity.this, CreatePostActivity.class);
        createPostIntent.putExtra("goal", "create");
        startActivity(createPostIntent);

    }

    @Override
    protected void refreshClicked() {

    }

    @Override
    protected void goExploreClicked() {
        Intent i = new Intent(ActivityStreamActivity.this, ExploreActivity.class);
        startActivity(i);
    }
    @Override
    protected void goProfileClicked() {
        Intent i = new Intent(ActivityStreamActivity.this, SelfProfilePageActivity.class);
        startActivity(i);
    }

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
