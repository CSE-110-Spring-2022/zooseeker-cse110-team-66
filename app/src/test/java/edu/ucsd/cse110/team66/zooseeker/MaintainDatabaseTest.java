package edu.ucsd.cse110.team66.zooseeker;

import org.junit.Test;
import org.junit.runner.RunWith;

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
    public void maintainDatabaseTest() {
        try(ActivityScenario<SearchExhibitActivity> scenario = ActivityScenario.launch(SearchExhibitActivity.class)) {
            scenario.onActivity(activity -> {

                TextView countView = activity.findViewById(R.id.exhibit_count);
                assertEquals("0", countView.getText().toString());
                RecyclerView recyclerView = activity.findViewById(R.id.exhibit_items);
                recyclerView.measure(0, 0);
                recyclerView.layout(0, 0, 100, 10000);
                Button exhibit1 = recyclerView.getChildAt(1).findViewById(R.id.add_exhibit_btn);
                exhibit1.performClick();
                assertEquals("1", countView.getText().toString());

            });

            scenario.recreate();
            scenario.onActivity(activity -> {
                TextView countView = activity.findViewById(R.id.exhibit_count);
                assertEquals("1", countView.getText().toString());
            });
        }
    }
}