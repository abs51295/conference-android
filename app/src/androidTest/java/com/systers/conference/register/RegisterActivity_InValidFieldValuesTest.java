package com.systers.conference.register;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
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
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.systers.conference.testutils.TextInputLayoutHelper.hasTextInputLayoutErrorText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterActivity_InValidFieldValuesTest {
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
    private String firstName;
    private String lastName;
    private String email;

    @Test
    public void emptyFirstName_onRegisterClick_generatesError() {
        firstName = "";
        lastName = "Shah";
        email = "aagams68@gmail.com";
        onView(withId(R.id.register_first_name)).perform(replaceText(firstName), closeSoftKeyboard());
        onView(withId(R.id.register_last_name)).perform(replaceText(lastName), closeSoftKeyboard());
        try {
            onView(withId(R.id.register_email)).check(matches(not(isEnabled())));
        } catch (AssertionFailedError error) {
            onView(withId(R.id.register_email)).perform(replaceText(email), closeSoftKeyboard());
        }
        onView(withId(R.id.profile_coordinator_layout)).perform(ViewActions.swipeUp());
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.text_input_firstname)).check(matches(hasTextInputLayoutErrorText(context.getString(R.string.error_field_required))));
    }

    @Test
    public void emptyLastName_onRegisterClick_generatesError() {
        firstName = "Aagam";
        lastName = "";
        email = "aagams68@gmail.com";
        onView(withId(R.id.register_first_name)).perform(replaceText(firstName), closeSoftKeyboard());
        onView(withId(R.id.register_last_name)).perform(replaceText(lastName), closeSoftKeyboard());
        try {
            onView(withId(R.id.register_email)).check(matches(not(isEnabled())));
        } catch (AssertionFailedError error) {
            onView(withId(R.id.register_email)).perform(replaceText(email), closeSoftKeyboard());
        }
        onView(withId(R.id.profile_coordinator_layout)).perform(ViewActions.swipeUp());
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.text_input_last_name)).check(matches(hasTextInputLayoutErrorText(context.getString(R.string.error_field_required))));
    }

    /*
    * This test will fail incase the email is already pre-filled by either Google Sign In or Twitter Sign In
    * because the e-mail is guaranteed to be valid in that case.
    */
    @Test
    public void inValidEmail_onRegisterClick_generatesError() {
        firstName = "Aagam";
        lastName = "Shah";
        email = "aagams68@gmail";
        onView(withId(R.id.register_first_name)).perform(replaceText(firstName), closeSoftKeyboard());
        onView(withId(R.id.register_last_name)).perform(replaceText(lastName), closeSoftKeyboard());
        try {
            onView(withId(R.id.register_email)).check(matches(not(isEnabled())));
        } catch (AssertionFailedError error) {
            onView(withId(R.id.register_email)).perform(replaceText(email), closeSoftKeyboard());
        }
        onView(withId(R.id.profile_coordinator_layout)).perform(ViewActions.swipeUp());
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.text_input_email)).check(matches(hasTextInputLayoutErrorText(context.getString(R.string.error_invalid_email))));
    }
}
