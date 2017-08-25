package com.systers.conference.schedule;

import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.systers.conference.MainActivity;
import com.systers.conference.R;
import com.systers.conference.event.EventDetailActivity;
import com.systers.conference.testutils.AmbiguousViewMatcherHelper;
import com.systers.conference.testutils.OrientationHelper;
import com.systers.conference.testutils.TabsMatcherHelper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ScheduleFragment_InteractionTests {
    @Rule
    public IntentsTestRule<MainActivity> mainActivityActivityTestRule = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void clickOnSession_opensSessionDetailsScreen() {
        onView(allOf(isCompletelyDisplayed(), withId(R.id.list))).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        intended(hasComponent(EventDetailActivity.class.getName()));
        onView(withId(R.id.event_title)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void tabs_changeOnSwipe_surviveOrientationChanges() {
        onView(TabsMatcherHelper.getTab("DAY 1")).check(matches(isSelected()));
        slideTabLeft();
        onView(TabsMatcherHelper.getTab("DAY 2")).check(matches(isSelected()));
        slideTabRight();
        onView(TabsMatcherHelper.getTab("DAY 1")).check(matches(isSelected()));
        OrientationHelper.rotateOrientation(mainActivityActivityTestRule);
        onView(TabsMatcherHelper.getTab("DAY 1")).check(matches(isSelected()));
    }

    @Test
    public void addToCalendarClicked_CalendarIntentFired() {
        onView(AmbiguousViewMatcherHelper.withIndex(withId(R.id.add_to_calendar), 0)).perform(click());
        intended(allOf(IntentMatchers.hasAction(Intent.ACTION_EDIT),
                IntentMatchers.hasType("vnd.android.cursor.item/event")));
    }

    private void slideTabRight() {
        onView(withId(R.id.schedule_view_pager)).perform(swipeRight());
    }

    private void slideTabLeft() {
        onView(withId(R.id.schedule_view_pager)).perform(swipeLeft());
    }
}