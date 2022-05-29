package edu.ucsd.cse110.team66.zooseeker;

import static org.junit.Assert.assertEquals;

import android.widget.Button;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SearchActivityButtonsTest {
    /** Test that the plan button is disabled when there are no exhibits in the database **/
    @Test
    public void testPlanBtnUnavailable() {
        try(ActivityScenario<SearchExhibitActivity> scenario = ActivityScenario.launch(SearchExhibitActivity.class)) {
            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.onActivity(activity -> {
                Button Plan_button =(Button) activity.findViewById(R.id.plan_btn);
                assertEquals(true, Plan_button.isEnabled());
            });
        }
    }

    /** Test that the clear button is disabled when there are no exhibits in the database **/
    @Test
    public void testClearBtnUnavailable() {
        try(ActivityScenario<SearchExhibitActivity> scenario = ActivityScenario.launch(SearchExhibitActivity.class)) {
            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.onActivity(activity -> {
                Button clear_button = (Button) activity.findViewById(R.id.clear_btn);
                assertEquals(false, clear_button.isEnabled());

            });
        }
    }

    /** Test that the selected exhibits button is disabled when there are no exhibits in the database **/
    @Test
    public void testSelectedExhibitsBtnUnavailable() {
        try(ActivityScenario<SearchExhibitActivity> scenario = ActivityScenario.launch(SearchExhibitActivity.class)) {
            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.onActivity(activity -> {
                Button selected_exhibits_btn =(Button)activity.findViewById(R.id.selected_exhibits_btn);
                assertEquals(false, selected_exhibits_btn.isEnabled());
            });
        }
    }
}
