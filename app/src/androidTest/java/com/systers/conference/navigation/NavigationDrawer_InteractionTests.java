package com.systers.conference.navigation;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import com.systers.conference.MainActivity;
import com.systers.conference.R;
import com.systers.conference.testutils.AmbiguousViewMatcherHelper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NavigationDrawer_InteractionTests {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    private Context context = InstrumentationRegistry.getTargetContext();

    @Test
    public void navigationItemSchedule_selected() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_schedule));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
        onView(AmbiguousViewMatcherHelper.withIndex(withId(R.id.list), 0)).check(matches(isCompletelyDisplayed()));
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar)))).
                check(matches(withText(InstrumentationRegistry.getTargetContext().getString(R.string.schedule_title))));
    }

    @Test
    public void navigationItemMySchedule_selected() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_myschedule));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
        onView(AmbiguousViewMatcherHelper.withIndex(withId(R.id.list), 0)).check(matches(isCompletelyDisplayed()));
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar)))).
                check(matches(withText(InstrumentationRegistry.getTargetContext().getString(R.string.myschedule_title))));
    }

    @Test
    public void navigationItemMaps_selected() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_maps));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
        onView(withId(R.id.map_view_pager)).check(matches(isCompletelyDisplayed()));
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar)))).
                check(matches(withText(context.getString(R.string.title_map_fragment))));
    }

    @Test
    public void navigationItemProfile_selected() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        onView(withId(R.id.nav_avatar_view)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
        onView(withId(R.id.avatar)).check(matches(isCompletelyDisplayed()));
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar)))).
                check(matches(withText(context.getString(R.string.profile_title))));
    }
}
