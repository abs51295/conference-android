package com.systers.conference.testutils;

import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;


/**
 * Matches a single view from multiple views with same id.
 */
public class AmbiguousViewMatcherHelper {
    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            protected boolean matchesSafely(View item) {
                return matcher.matches(item) && currentIndex++ == index;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }
        };
    }
}
