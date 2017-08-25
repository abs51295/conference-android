package com.systers.conference.speaker;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.FlakyTest;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import com.systers.conference.MainActivity;
import com.systers.conference.R;
import com.systers.conference.testutils.AmbiguousViewMatcherHelper;
import com.systers.conference.testutils.CollapsingToolBarHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SpeakersDetailsActivity_InteractionTests {
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


    @FlakyTest(detail = "Test would fail when animations are enabled on the device because Espresso wouldn't wait for the toolbar to collapse")
    @Test
    public void collapseToolbar_hidesExtraText_isFlaky() {
        onView(withId(R.id.speakers_details_appbar)).perform(CollapsingToolBarHelper.collapseAppBarLayout());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.speaker_image)).check(matches(not(isCompletelyDisplayed())));
    }

    @Test
    public void backPressed_navigatesBackToParent() {
        pressBack();
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar)))).
                check(matches(withText(InstrumentationRegistry.getTargetContext().getString(R.string.title_activity_event_detail))));
    }
}
