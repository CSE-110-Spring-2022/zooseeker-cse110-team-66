package edu.ucsd.cse110.team66.zooseeker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowActivity;

import static org.junit.Assert.*;

import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MaintainDatabaseTest {

    @Test
    public void MaintainDatabaseUITest() {
        ActivityController<SearchExhibitActivity> activityController = Robolectric.buildActivity(SearchExhibitActivity.class);
        activityController.create().start().visible();

        ShadowActivity myActivityShadow = Shadows.shadowOf(activityController.get());

        myActivityShadow.clickMenuItem(R.id.exhibit_search);

        TextView countView = activityController.get().findViewById(R.id.exhibit_count);
        assertEquals("0", countView.getText().toString());

        Button button = activityController.get().findViewById(R.id.add_exhibit_btn);
        button.performClick();

        assertEquals("1", countView.getText().toString());

        activityController.recreate();
        assertEquals("1", countView.getText().toString());

    }

}