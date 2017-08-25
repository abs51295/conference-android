package com.systers.conference.speaker;

import android.support.test.espresso.contrib.RecyclerViewActions;
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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SpeakerDetailsActivity_ViewTests {
    /*
    * Since we need to pass speaker id in order to retrieve data from Realm. We need to start with Main Activity
    * and navigate to the Speaker Details Activity.
    * */
    @Rule
    public IntentsTestRule<MainActivity> speakerDetailActivityTestRule = new IntentsTestRule<>(MainActivity.class);

    @Before
    public void navigateToSpeakerDetailsActivity() {
        onView(allOf(isCompletelyDisplayed(), withId(R.id.list))).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(AmbiguousViewMatcherHelper.withIndex(withId(R.id.speaker_avatar_icon), 0)).perform(click());
    }

    @Test
    public void allViews_arePresent() {
        onView(withId(R.id.speakers_details_appbar)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.speaker_image)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.speakers_details_title)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.speakers_details_designation)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.speakers_details_header_introduction)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.speakers_details_description)).check(matches(isCompletelyDisplayed()));
    }
}
