package com.systers.conference.maps;

import android.app.Activity;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.view.ViewPager;

import com.systers.conference.MainActivity;
import com.systers.conference.R;
import com.systers.conference.testutils.AmbiguousViewMatcherHelper;
import com.systers.conference.testutils.DrawableMatcherHelper;
import com.systers.conference.testutils.ViewPagerIdlingResourceUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MapsFragment_InteractionTests {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class, true, false);
    private Activity activity;

    @Before
    public void navigate_to_mapsFragment() {
        activity = startActivity();
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_maps));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void verticalSwiping_showsCorrectMap() {
        ViewPagerIdlingResourceUtil viewPagerIdlingResourceUtil = new ViewPagerIdlingResourceUtil((ViewPager) activity.findViewById(R.id.map_view_pager), "");
        registerIdlingResources(viewPagerIdlingResourceUtil);
        /*
            To understand how the index is passed to the {@link AmbiguousViewMatcherHelper} see the below description:
            ViewPager actually loads three fragments at once, calling their onResume(). It loads the visible fragment ant both of its neighbours (the one on the left and one on the right.
         */
        onView(AmbiguousViewMatcherHelper.withIndex(withId(R.id.map_image_view), 0)).check(matches(DrawableMatcherHelper.withDrawable(R.drawable.level_1)));
        onView(withId(R.id.map_view_pager)).perform(swipeUp());
        onView(AmbiguousViewMatcherHelper.withIndex(withId(R.id.map_image_view), 1)).check(matches(DrawableMatcherHelper.withDrawable(R.drawable.level_2)));
        onView(withId(R.id.map_view_pager)).perform(swipeUp());
        onView(AmbiguousViewMatcherHelper.withIndex(withId(R.id.map_image_view), 1)).check(matches(DrawableMatcherHelper.withDrawable(R.drawable.level_3)));
        unregisterIdlingResources(viewPagerIdlingResourceUtil);
    }

    private MainActivity startActivity() {
        return mainActivityActivityTestRule.launchActivity(null);
    }
}
