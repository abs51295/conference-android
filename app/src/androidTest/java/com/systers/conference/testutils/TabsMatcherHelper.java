package com.systers.conference.testutils;


import android.view.View;

import com.systers.conference.R;

import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;


public class TabsMatcherHelper {
    public static Matcher<View> getTab(String tabTitle) {
        return allOf(withText(tabTitle), isDescendantOfA(withId(R.id.day_tabs)));
    }
}
