package com.systers.conference.profile;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditProfileActivity_ViewTests {
    @Rule
    public ActivityTestRule<EditProfileActivity> editProfileActivityActivityTestRule = new ActivityTestRule<>(EditProfileActivity.class);
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
    public void allViews_arePresent() {
        onView(withId(R.id.avatar)).check(matches(DrawableMatcherHelper.withDrawable(DrawableMatcherHelper.ANY)));
        onView(withId(R.id.edit_icon)).check(matches(isDisplayed()));
        onView(withId(R.id.twitter_button)).check(matches(isDisplayed()));
        onView(withId(R.id.google_button)).check(matches(isDisplayed()));
    }

    @Test
    public void preFilledDetails_areConsistent() {
        if (attendee.getFirstName() != null) {
            onView(withId(R.id.edit_first_name)).check(matches(withText(attendee.getFirstName())));
        }
        if (attendee.getLastName() != null) {
            onView(withId(R.id.edit_last_name)).check(matches(withText(attendee.getLastName())));
        }
        if (attendee.getEmail() != null) {
            onView(withId(R.id.edit_email)).check(matches(withText(attendee.getEmail())));
        }
        if (attendee.getCompany() != null) {
            onView(withId(R.id.edit_company_name)).check(matches(withText(attendee.getCompany())));
        }
        if (attendee.getTitle() != null) {
            onView(withId(R.id.edit_role)).check(matches(withText(attendee.getTitle())));
        }
        if (attendee.getAttendeeType() != null) {
            onView(withId(R.id.edit_attendee_type)).check(matches(withText(attendee.getAttendeeType())));
        }
        if (attendee.isTwitterLoggedIn()) {
            onView(withId(R.id.twitter_button)).check(matches(withText(InstrumentationRegistry.getTargetContext().getString(R.string.connected))));
        } else {
            onView(withId(R.id.twitter_button)).check(matches(withText(InstrumentationRegistry.getTargetContext().getString(R.string.twitter_button))));
        }
        if (attendee.isGoogleLoggedIn()) {
            onView(withId(R.id.google_button)).check(matches(withText(InstrumentationRegistry.getTargetContext().getString(R.string.connected))));
        } else {
            onView(withId(R.id.google_button)).check(matches(withText(InstrumentationRegistry.getTargetContext().getString(R.string.google_button))));
        }
    }
}
