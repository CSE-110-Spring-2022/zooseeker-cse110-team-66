package edu.ucsd.cse110.team66.zooseeker;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AddExhibitTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void addExhibitTest() {
        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.exhibit_search), withContentDescription("Search Exhibit"),
                        childAtPosition(
                                childAtPosition(
                                        withId(androidx.appcompat.R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.add_exhibit_btn), withText("ADD"),
                        childAtPosition(
                                allOf(withId(R.id.exhibit_item_layout),
                                        childAtPosition(
                                                withId(R.id.exhibit_items),
                                                0)),
                                1),
                        isDisplayed()));
        materialButton.check(matches(isEnabled()));
    }

    @Test
    public void repeatedAddExhibitTest() {
        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.exhibit_search), withContentDescription("Search Exhibit"),
                        childAtPosition(
                                childAtPosition(
                                        withId(androidx.appcompat.R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.add_exhibit_btn), withText("ADD"),
                        childAtPosition(
                                allOf(withId(R.id.exhibit_item_layout),
                                        childAtPosition(
                                                withId(R.id.exhibit_items),
                                                0)),
                                1),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Collapse"),
                        childAtPosition(
                                allOf(withId(androidx.appcompat.R.id.action_bar),
                                        childAtPosition(
                                                withId(androidx.appcompat.R.id.action_bar_container),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.exhibit_search), withContentDescription("Search Exhibit"),
                        childAtPosition(
                                childAtPosition(
                                        withId(androidx.appcompat.R.id.action_bar),
                                        0),
                                0),
                        isDisplayed()));
        actionMenuItemView2.perform(click());

        ViewInteraction button = onView(
                allOf(withId(R.id.add_exhibit_btn), withText("ADDED"),
                        withParent(allOf(withId(R.id.exhibit_item_layout),
                                withParent(withId(R.id.exhibit_items)))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));
        button.check(matches(isNotEnabled()));
    }



    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
