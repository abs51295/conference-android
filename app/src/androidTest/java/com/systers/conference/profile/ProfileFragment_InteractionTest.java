package com.systers.conference.profile;


import android.support.test.espresso.contrib.DrawerActions;
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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileFragment_InteractionTest {
    @Rule
    public IntentsTestRule<MainActivity> mainActivityActivityTestRule = new IntentsTestRule<>(MainActivity.class);

    @Before
    public void navigate_to_profileFragment() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_avatar_view)).perform(click());
    }

    @Test
    public void editButton_whenClicked_launchesEditProfileActivity() {
        onView(withId(R.id.edit_profile)).perform(click());
        intended(hasComponent(EditProfileActivity.class.getName()));
        onView(withId(R.id.profile_coordinator_layout)).check(matches(isCompletelyDisplayed()));
    }
}
