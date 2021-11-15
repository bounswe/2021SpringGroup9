package com.example.postory;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

import com.example.postory.activities.MainActivity;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PostViewInstrumentedTest {
    @Test
    public void pressPostContinueReading() {
        // The "continue reading" button should first be visible for a long text and after it's clicked it should be gone.

        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        onData(anything()).inAdapterView(withId(R.id.list_view_posts)).atPosition(0)
                .onChildView(withId(R.id.post_continue_reading))
                .check(matches(isDisplayed()));

        onData(anything()).inAdapterView(withId(R.id.list_view_posts)).atPosition(0)
                .onChildView(withId(R.id.post_continue_reading))
                .perform(click())
                .check(matches(not(isDisplayed())));
    }
}