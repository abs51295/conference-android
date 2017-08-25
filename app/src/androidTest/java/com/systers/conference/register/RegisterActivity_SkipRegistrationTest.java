package com.systers.conference.register;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.systers.conference.R;
import com.systers.conference.util.AccountUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterActivity_SkipRegistrationTest {
    private Context context = InstrumentationRegistry.getTargetContext();
    @Rule
    public ActivityTestRule<RegisterActivity> registerActivityTestRule = new ActivityTestRule<RegisterActivity>(RegisterActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            AccountUtils.clearRegisterVisited(context);
            super.beforeActivityLaunched();
        }

        @Override
        protected void afterActivityLaunched() {
            super.afterActivityLaunched();
        }

        @Override
        protected void afterActivityFinished() {
            AccountUtils.setRegisterVisited(context);
            super.afterActivityFinished();
        }
    };

    @Test
    public void skipRegistration_onClicked_opensSchedule() {
        openActionBarOverflowOrOptionsMenu(context);
        onView(withText(context.getString(R.string.skip_registration))).perform(click());
        onView(withId(R.id.content)).check(matches(isDisplayed()));
    }
}
