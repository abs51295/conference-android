package com.systers.conference.profile;


import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import com.systers.conference.MainActivity;
import com.systers.conference.R;
import com.systers.conference.db.RealmDataRepository;
import com.systers.conference.model.Attendee;
import com.systers.conference.testutils.DrawableMatcherHelper;
import com.systers.conference.util.FirebaseAuthUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.realm.Realm;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileFragment_ViewTests {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void afterActivityLaunched() {
            super.afterActivityLaunched();
            onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
            onView(withId(R.id.nav_avatar_view)).perform(click());
        }
    };
    private Attendee attendee;
    private Realm realm;

    @Before
    public void fetch_attendee_fromRealm() {
        realm = Realm.getDefaultInstance();
        RealmDataRepository realmDataRepository = RealmDataRepository.getInstance(realm);
        attendee = realmDataRepository.getAttendeeFromRealmSync(FirebaseAuthUtil.getFirebaseAuthInstance().getCurrentUser().getUid());
    }

    @After
    public void close_Realm() {
        realm.close();
    }

    @Test
    public void profilePicture_isPresent() {
        onView(withId(R.id.avatar)).check(matches(DrawableMatcherHelper.withDrawable(DrawableMatcherHelper.ANY)));
    }

    @Test
    public void profileDetails_areConsistent() {
        onView(withId(R.id.name)).check(matches(withText(attendee.getFirstName() + " " + attendee.getLastName())));
        String companyName = attendee.getCompany();
        String role = attendee.getTitle();
        if (!TextUtils.isEmpty(role)) {
            if (!TextUtils.isEmpty(companyName)) {
                onView(withId(R.id.subhead)).check(matches(
                        withText(role + ", " + companyName)
                ));
            } else {
                onView(withId(R.id.subhead)).check(matches(
                        withText(role)
                ));
            }
        } else if (!TextUtils.isEmpty(companyName)) {
            onView(withId(R.id.subhead)).check(matches(withText(companyName)));
        } else {
            onView(withId(R.id.subhead)).check(matches(not(isDisplayed())));
        }
    }

    @Test
    public void socialAccounts_areDisplayed() {
        if (attendee.isGoogleLoggedIn()) {
            onView(withId(R.id.google_plus_box)).check(matches(isDisplayed()));
        } else {
            onView(withId(R.id.google_plus_box)).check(matches(not(isDisplayed())));
        }
        if (attendee.isTwitterLoggedIn()) {
            onView(withId(R.id.twitter_box)).check(matches(isDisplayed()));
        } else {
            onView(withId(R.id.twitter_box)).check(matches(not(isDisplayed())));
        }
    }
}
