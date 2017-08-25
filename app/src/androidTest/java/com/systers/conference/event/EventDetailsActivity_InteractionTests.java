package com.systers.conference.event;


import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.systers.conference.MainActivity;
import com.systers.conference.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EventDetailsActivity_InteractionTests {
    /*
    * Since we need to pass session id in order to retrieve data from Realm. We need to start with Main Activity
    * and navigate to the Event Details Activity.
    * */
    @Rule
    public IntentsTestRule<MainActivity> eventDetailActivityTestRule = new IntentsTestRule<>(MainActivity.class);

    @Before
    public void navigateToEventDetailsActivity() {
        onView(allOf(isCompletelyDisplayed(), withId(R.id.list))).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @Test
    public void floatingActionMenu_clicked() {
        onView(withId(R.id.fab_expand_menu_button)).check(matches(isDisplayed()));
        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.calendar_fab)).check(matches(isDisplayed()));
        onView(withId(R.id.share_fab)).check(matches(isDisplayed()));
    }

    @Test
    public void calendarFloatingActionButton_clicked_calendarIntentFired() {
        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.calendar_fab)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.calendar_fab)).perform(click());
        intended(allOf(IntentMatchers.hasAction(Intent.ACTION_EDIT),
                IntentMatchers.hasType("vnd.android.cursor.item/event")));
    }

    @Test
    public void shareFloatingActionButton_clicked_shareIntentFired() {
        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.share_fab)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.share_fab)).perform(click());
        intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER));
    }

    @Test
    public void back_pressed_navigatesToParent() {
        pressBack();
        onView(withId(R.id.day_tabs)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.schedule_view_pager)).check(matches(isCompletelyDisplayed()));
    }
}
