package com.example.postory.activities;


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
    protected void goProfileClicked() {

    }

    @Override
    protected void logoutClicked() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_stream);
        super.initToolbar();

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);


        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        vpAdapter.addFragment(new OwnActivityStreamFragment(),"OWN");
        vpAdapter.addFragment(new FollowedActivityStreamFragment(),"FOLLOWER");

        viewPager.setAdapter(vpAdapter);



    }
}
