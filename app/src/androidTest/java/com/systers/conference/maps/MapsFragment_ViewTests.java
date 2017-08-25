package com.systers.conference.maps;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.systers.conference.MainActivity;
import com.systers.conference.R;
import com.systers.conference.testutils.AmbiguousViewMatcherHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MapsFragment_ViewTests {
    @Rule
    public IntentsTestRule<MainActivity> mainActivityActivityTestRule = new IntentsTestRule<>(MainActivity.class);

    @Before
    public void navigate_to_mapsFragment() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_maps));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void allViews_arePresent() {
        onView(withId(R.id.map_view_pager)).check(matches(isCompletelyDisplayed()));
        onView(AmbiguousViewMatcherHelper.withIndex(withId(R.id.map_image_view), 0)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.map_circle_indicator)).check(matches(isCompletelyDisplayed()));
    }
}
