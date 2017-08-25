package com.systers.conference.register;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.filters.FlakyTest;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.systers.conference.R;
import com.systers.conference.util.AccountUtils;

import junit.framework.AssertionFailedError;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterActivity_ValidFieldValuesTest {
    private Context context = InstrumentationRegistry.getTargetContext();
    @Rule
    public ActivityTestRule<RegisterActivity> registerActivityTestRule = new ActivityTestRule<RegisterActivity>(RegisterActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            AccountUtils.clearRegisterVisited(context);
            super.beforeActivityLaunched();
        }

        @Override
        protected void afterActivityFinished() {
            AccountUtils.setRegisterVisited(context);
            super.afterActivityFinished();
        }
    };

    @FlakyTest(detail = "This test would not work when there is no internet connection")
    @Test
    public void testWithValidFieldValues_isFlaky() {
        String firstName = "Aagam";
        String lastName = "Shah";
        String email = "aagams68@gmail.com";
        String companyName = "Google";
        String role = "Intern";
        onView(withId(R.id.register_first_name)).perform(replaceText(firstName), closeSoftKeyboard());
        onView(withId(R.id.register_last_name)).perform(replaceText(lastName), closeSoftKeyboard());
        try {
            onView(withId(R.id.register_email)).check(matches(not(isEnabled())));
        } catch (AssertionFailedError error) {
            onView(withId(R.id.register_email)).perform(replaceText(email), closeSoftKeyboard());
        }
        onView(withId(R.id.register_company_name)).perform(replaceText(companyName), closeSoftKeyboard());
        onView(withId(R.id.register_role)).perform(replaceText(role), closeSoftKeyboard());
        onView(withId(R.id.profile_coordinator_layout)).perform(ViewActions.swipeUp());
        onView(withId(R.id.register_button)).perform(click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.content)).check(matches(isDisplayed()));
    }
}
