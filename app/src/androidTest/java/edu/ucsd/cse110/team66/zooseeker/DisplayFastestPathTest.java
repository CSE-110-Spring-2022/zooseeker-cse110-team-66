package edu.ucsd.cse110.team66.zooseeker;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;


import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
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
public class DisplayFastestPathTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void planButtonTest() {
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

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.add_exhibit_btn), withText("ADD"),
                        childAtPosition(
                                allOf(withId(R.id.exhibit_item_layout),
                                        childAtPosition(
                                                withId(R.id.exhibit_items),
                                                1)),
                                1),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.add_exhibit_btn), withText("ADD"),
                        childAtPosition(
                                allOf(withId(R.id.exhibit_item_layout),
                                        childAtPosition(
                                                withId(R.id.exhibit_items),
                                                2)),
                                1),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction materialButton4 = onView(
                allOf(withId(R.id.add_exhibit_btn), withText("ADD"),
                        childAtPosition(
                                allOf(withId(R.id.exhibit_item_layout),
                                        childAtPosition(
                                                withId(R.id.exhibit_items),
                                                3)),
                                1),
                        isDisplayed()));
        materialButton4.perform(click());

        ViewInteraction materialButton5 = onView(
                allOf(withId(R.id.add_exhibit_btn), withText("ADD"),
                        childAtPosition(
                                allOf(withId(R.id.exhibit_item_layout),
                                        childAtPosition(
                                                withId(R.id.exhibit_items),
                                                4)),
                                1),
                        isDisplayed()));
        materialButton5.perform(click());

        ViewInteraction materialButton6 = onView(
                allOf(withId(R.id.plan_btn), withText("Plan\n"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        materialButton6.perform(click());

        ViewInteraction textView = onView(

                allOf(withId(R.id.plan_item_text), withText("To Entrance Plaza: \nFrom Entrance and Exit Gate, walk down Entrance Way for 10.0 feet towards Entrance Plaza.\n"),
                        withParent(withParent(withId(R.id.plan_items))),
                        isDisplayed()));
        textView.check(matches(withText("To Entrance Plaza:  From Entrance and Exit Gate, walk down Entrance Way for 10.0 feet towards Entrance Plaza. ")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.plan_item_text), withText("To Alligators: \nFrom Entrance Plaza, walk down Reptile Road for 100.0 feet towards Alligators.\n"),
                        withParent(withParent(withId(R.id.plan_items))),
                        isDisplayed()));
        textView2.check(matches(withText("To Alligators:  From Entrance Plaza, walk down Reptile Road for 100.0 feet towards Alligators. ")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.plan_item_text), withText("To Lions: \nFrom Alligators, walk down Sharp Teeth Shortcut for 200.0 feet towards Lions.\n"),
                        withParent(withParent(withId(R.id.plan_items))),
                        isDisplayed()));
        textView3.check(matches(withText("To Lions:  From Alligators, walk down Sharp Teeth Shortcut for 200.0 feet towards Lions. ")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.plan_item_text), withText("To Gorillas: \nFrom Lions, walk down Africa Rocks Street for 200.0 feet towards Gorillas.\n"),
                        withParent(withParent(withId(R.id.plan_items))),
                        isDisplayed()));
        textView4.check(matches(withText("To Gorillas:  From Lions, walk down Africa Rocks Street for 200.0 feet towards Gorillas. ")));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.plan_item_text), withText("To Arctic Foxes: \nFrom Elephant Odyssey, walk down Africa Rocks Street for 200.0 feet towards Lions.\nThen Lions, walk down Sharp Teeth Shortcut for 200.0 feet towards Alligators.\nThen Alligators, walk down Reptile Road for 100.0 feet towards Entrance Plaza.\nThen Entrance Plaza, walk down Arctic Avenue for 300.0 feet towards Arctic Foxes.\n"),
                        withParent(withParent(withId(R.id.plan_items))),
                        isDisplayed()));
        textView5.check(matches(withText("To Arctic Foxes:  From Elephant Odyssey, walk down Africa Rocks Street for 200.0 feet towards Lions. Then Lions, walk down Sharp Teeth Shortcut for 200.0 feet towards Alligators. Then Alligators, walk down Reptile Road for 100.0 feet towards Entrance Plaza. Then Entrance Plaza, walk down Arctic Avenue for 300.0 feet towards Arctic Foxes. ")));

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
