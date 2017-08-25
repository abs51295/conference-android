package com.systers.conference.schedule;


import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.systers.conference.MainActivity;
import com.systers.conference.R;
import com.systers.conference.db.RealmDataRepository;
import com.systers.conference.model.Session;
import com.systers.conference.testutils.AmbiguousViewMatcherHelper;
import com.systers.conference.testutils.RecyclerViewItemCountAssertionHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ScheduleFragment_ViewTests {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    private RealmResults<Session> sessions;
    private Realm realm;
    private RealmDataRepository realmDataRepository;
    private List<Session> mSessions = new ArrayList<>();

    @Before
    public void setUp_realm() {
        realm = Realm.getDefaultInstance();
        realmDataRepository = RealmDataRepository.getInstance(realm);
    }

    @After
    public void close_realm() {
        realm.close();
    }

    @Test
    public void allViews_areDisplayed() {
        onView(withId(R.id.day_tabs)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.schedule_view_pager)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void dayOneSessions_areConsistentWithRealm() {
        sessions = realmDataRepository.getSessionsByDaySync("2017-07-03");
        mSessions.clear();
        mSessions.addAll(sessions);
        onView(AmbiguousViewMatcherHelper.withIndex(withId(R.id.list), 0)).check(new RecyclerViewItemCountAssertionHelper(mSessions.size()));
    }

    @Test
    public void dayTwoSessions_areConsistentWithRealm() {
        sessions = realmDataRepository.getSessionsByDaySync("2017-07-04");
        mSessions.clear();
        mSessions.addAll(sessions);
        onView(AmbiguousViewMatcherHelper.withIndex(withId(R.id.list), 1)).check(new RecyclerViewItemCountAssertionHelper(mSessions.size()));
    }
}
