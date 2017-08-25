package com.systers.conference.main;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import com.systers.conference.MainActivity;
import com.systers.conference.R;
import com.systers.conference.testutils.OrientationHelper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.AllOf.allOf;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivity_OrientationChangeTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void afterActivityLaunched() {
            OrientationHelper.rotateOrientation(mainActivityActivityTestRule);
        }
    };

    @Test
    public void landscape_IsSurvived() {
        String title = mainActivityActivityTestRule.getActivity().getTitle().toString();
        OrientationHelper.rotateOrientation(mainActivityActivityTestRule);
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar)))).
                check(matches(withText(title)));
    }
}
