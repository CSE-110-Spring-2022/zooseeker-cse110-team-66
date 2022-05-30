package edu.ucsd.cse110.team66.zooseeker;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ActionMenuView;
import android.widget.Filter;
import android.widget.SearchView;

import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
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
public class SearchEmulatorTests {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void SearchInputExisting() {
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

        ViewInteraction searchAutoComplete = onView(
                allOf(withId(androidx.appcompat.R.id.search_src_text),
                        childAtPosition(
                                allOf(withId(androidx.appcompat.R.id.search_plate),
                                        childAtPosition(
                                                withId(androidx.appcompat.R.id.search_edit_frame),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete.perform(replaceText("Mammal"), closeSoftKeyboard());

        ViewInteraction searchAutoComplete2 = onView(
                allOf(withId(androidx.appcompat.R.id.search_src_text), withText("Mammal"),
                        childAtPosition(
                                allOf(withId(androidx.appcompat.R.id.search_plate),
                                        childAtPosition(
                                                withId(androidx.appcompat.R.id.search_edit_frame),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete2.perform(pressImeActionButton());

        ViewInteraction textView = onView(
                allOf(withId(R.id.exhibit_item_text), withText("Capuchin Monkeys"),
                        withParent(allOf(withId(R.id.exhibit_item_layout),
                                withParent(withId(R.id.exhibit_items)))),
                        isDisplayed()));
        textView.check(matches(withText("Capuchin Monkeys")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.exhibit_item_text), withText("Gorillas"),
                        withParent(allOf(withId(R.id.exhibit_item_layout),
                                withParent(withId(R.id.exhibit_items)))),
                        isDisplayed()));
        textView2.check(matches(withText("Gorillas")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.exhibit_item_text), withText("Hippos"),
                        withParent(allOf(withId(R.id.exhibit_item_layout),
                                withParent(withId(R.id.exhibit_items)))),
                        isDisplayed()));
        textView4.check(matches(withText("Hippos")));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.exhibit_item_text), withText("Orangutans"),
                        withParent(allOf(withId(R.id.exhibit_item_layout),
                                withParent(withId(R.id.exhibit_items)))),
                        isDisplayed()));
        textView5.check(matches(withText("Orangutans")));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.exhibit_item_text), withText("Siamangs"),
                        withParent(allOf(withId(R.id.exhibit_item_layout),
                                withParent(withId(R.id.exhibit_items)))),
                        isDisplayed()));
        textView6.check(matches(withText("Siamangs")));
    }

    @Test
    public void searchInputNotExisting() {
        ActivityScenario<SearchExhibitActivity> scenario = ActivityScenario.launch(SearchExhibitActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            assertEquals(5,activity.exhibitItemAdapter.getItemCount());
        });
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
