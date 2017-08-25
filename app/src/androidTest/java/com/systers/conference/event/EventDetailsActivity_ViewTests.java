package com.systers.conference.event;


import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.LinearLayout;

import com.systers.conference.MainActivity;
import com.systers.conference.R;
import com.systers.conference.db.RealmDataRepository;
import com.systers.conference.model.Session;
import com.systers.conference.util.DateTimeUtil;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import io.realm.Realm;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EventDetailsActivity_ViewTests {
    private Session session;
    private Realm realm;

    /*
    * Since we need to pass session id in order to retrieve data from Realm. We need to start with Main Activity
    * and navigate to the Event Details Activity.
    * */
    @Rule
    public ActivityTestRule<MainActivity> eventDetailActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void navigateToEventDetailsActivity() {
        onView(allOf(isCompletelyDisplayed(), withId(R.id.list))).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        realm = Realm.getDefaultInstance();
        RealmDataRepository realmDataRepository = RealmDataRepository.getInstance(realm);
        //Since I am clicking view with Index 0 I am hardcoding the id. This should be changed if you are clicking on any other session.
        String sessionId = "15781844";
        session = realmDataRepository.getSessionById(sessionId);
    }

    @After
    public void close_Realm() {
        realm.close();
    }

    @Test
    public void allViews_arePresent() {
        onView(withId(R.id.fab_menu)).check(matches(isDisplayed()));
        onView(withId(R.id.event_title)).check(matches(isDisplayed()));
        onView(withId(R.id.room)).check(matches(isDisplayed()));
        onView(withId(R.id.time)).check(matches(isDisplayed()));
        onView(withId(R.id.audience_level)).check(matches(isDisplayed()));
        onView(withId(R.id.description_header)).check(matches(isDisplayed()));
        onView(withId(R.id.event_description)).check(matches(isDisplayed()));
        onView(withId(R.id.speakers_header)).check(matches(isDisplayed()));
        onView(withId(R.id.speakers_container)).check(matches(isDisplayed()));
    }

    @Test
    public void eventDetails_areConsistent_withRealm() {
        onView(withId(R.id.event_title)).check(matches(withText(session.getName())));
        onView(withId(R.id.room)).check(matches(withText(session.getLocation())));
        String startTime = DateTimeUtil.getTimeFromTimeStamp(DateTimeUtil.FORMAT_24H, Long.valueOf(session.getStartTime()));
        String endTime = DateTimeUtil.getTimeFromTimeStamp(DateTimeUtil.FORMAT_24H, Long.valueOf(session.getEndTime()));
        Date date = DateTimeUtil.getDate(session.getSessionDate());
        String descriptiveDate = DateTimeUtil.getDateDescriptive(date);
        onView(withId(R.id.time)).check(matches(withText(descriptiveDate + ", " + startTime + " - " + endTime)));
        onView(withId(R.id.audience_level)).check(matches(withText(session.getSessionType())));
        onView(withId(R.id.event_description)).check(matches(withText(session.getDescription())));
        onView(withId(R.id.speakers_container)).check(matches(withChildViewCount(session.getSpeakers().size(), withId(R.id.speaker_item))));
    }

    private Matcher<View> withChildViewCount(final int count, final Matcher<View> childMatcher) {
        return new BoundedMatcher<View, LinearLayout>(LinearLayout.class) {
            @Override
            protected boolean matchesSafely(LinearLayout viewGroup) {
                int matchCount = 0;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    if (childMatcher.matches(viewGroup.getChildAt(i))) {
                        matchCount++;
                    }
                }
                return matchCount == count;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("ViewGroup with child-count=" + count + " and");
                childMatcher.describeTo(description);
            }
        };
    }
}
