package edu.ucsd.cse110.team66.zooseeker;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RouteDisplaysChosenExhibitsTest {
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public GrantPermissionRule permissionRule2 = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION);

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * After choosing Flamingos, Crocodiles, and Bali Mynah, check that the
     * appropriate exhibits are displayed in the route summary
     *  - Flamingos -> To Flamingos
     *  - Crocodiles -> To Crocodiles
     *  - Bali Mynah -> To Owens Aviary
     *  - Entrance and Exit Gate -> To Entrance and Exit Gate
     */
    @Test
    public void routeDisplaysChosenExhibitsTest() {
        ViewInteraction cbutton = onView(
                allOf(withId(R.id.clear_btn), withText("CLEAR"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        cbutton.perform(click());

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

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.add_exhibit_btn), withText("ADD"),
                        childAtPosition(
                                allOf(withId(R.id.exhibit_item_layout),
                                        childAtPosition(
                                                withId(R.id.exhibit_items),
                                                6)),
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

        ViewInteraction materialButton5 = onView(
                allOf(withId(R.id.plan_btn), withText("Plan\n"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        3),
                                0),
                        isDisplayed()));
        materialButton5.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.plan_items),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        recyclerView.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withId(R.id.plan_name_item), withText("To Flamingos"),
                        withParent(withParent(withId(R.id.plan_items))),
                        isDisplayed()));
        textView.check(matches(withText("To Flamingos")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.plan_name_item), withText("To Owens Aviary"),
                        withParent(withParent(withId(R.id.plan_items))),
                        isDisplayed()));
        textView2.check(matches(withText("To Owens Aviary")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.plan_name_item), withText("To Crocodiles"),
                        withParent(withParent(withId(R.id.plan_items))),
                        isDisplayed()));
        textView3.check(matches(withText("To Crocodiles")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.plan_name_item), withText("To Entrance and Exit Gate"),
                        withParent(withParent(withId(R.id.plan_items))),
                        isDisplayed()));
        textView4.check(matches(withText("To Entrance and Exit Gate")));
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
