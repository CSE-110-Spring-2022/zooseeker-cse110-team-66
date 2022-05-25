package edu.ucsd.cse110.team66.zooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(AndroidJUnit4.class)
public class ZooSeekerTest {
    private ExhibitItemDao dao;
    private ExhibitDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.exhibitItemDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void testInsert() {
        List<String> tag1 = Arrays.asList("monkey");
        List<String> tag2 = Arrays.asList("lion");
        ExhibitItem exhibitItem1 = new ExhibitItem("monkey", "Monkey", tag1);
        ExhibitItem exhibitItem2 = new ExhibitItem("lion", "Lion", tag2);

        long numID1 = dao.insert(exhibitItem1);
        long numID2 = dao.insert(exhibitItem2);

        //check that the numbered ids of items should be different
        assertNotEquals(numID1, numID2);
    }

    @Test
    public void testGet() {
        List<String> tag1 = Arrays.asList("monkey");
        ExhibitItem exhibitItem1 = new ExhibitItem("monkey", "Monkey", tag1);

        long numID1 = dao.insert(exhibitItem1);
        ExhibitItem item = dao.get("monkey");

        //test that the fields should be maintained
        assertEquals(numID1, item.numID);
        assertEquals(exhibitItem1.id, item.id);
        assertEquals(exhibitItem1.name, item.name);
        assertEquals(exhibitItem1.tags, item.tags);
    }

    @Test
    public void testCountZero() {
        try(ActivityScenario<SearchExhibitActivity> scenario = ActivityScenario.launch(SearchExhibitActivity.class)) {
            scenario.onActivity(activity -> {

                TextView text_display =(TextView)activity.findViewById(R.id.exhibit_count);
                assertEquals("0", text_display.getText());

            });
        }
    }

    /** Test that the plan button is disabled when there are no exhibits in the database **/
    @Test
    public void testPlanBtnUnavailable() {
        try(ActivityScenario<SearchExhibitActivity> scenario = ActivityScenario.launch(SearchExhibitActivity.class)) {
            scenario.onActivity(activity -> {

                Button Plan_button =(Button)activity.findViewById(R.id.plan_btn);
                assertEquals(false, Plan_button.isEnabled());

            });
        }
    }

    /** Test that the clear button is disabled when there are no exhibits in the database **/
    @Test
    public void testClearBtnUnavailable() {
        try(ActivityScenario<SearchExhibitActivity> scenario = ActivityScenario.launch(SearchExhibitActivity.class)) {
            scenario.onActivity(activity -> {

                Button clear_button =(Button)activity.findViewById(R.id.clear_btn);
                assertEquals(false, clear_button.isEnabled());

            });
        }
    }

    /** Test that the selected exhibtis button is disabled when there are no exhibits in the database **/
    @Test
    public void testSelectedExhibitsBtnUnavailable() {
        try(ActivityScenario<SearchExhibitActivity> scenario = ActivityScenario.launch(SearchExhibitActivity.class)) {
            scenario.onActivity(activity -> {
                Button selected_exhibits_btn =(Button)activity.findViewById(R.id.selected_exhibits_btn);
                assertEquals(false, selected_exhibits_btn.isEnabled());
            });
        }
    }

    @Test
    public void testClear() {
        List<String> tag1 = Arrays.asList("monkey");
        ExhibitItem exhibitItem1 = new ExhibitItem("monkey", "Monkey", tag1);
        exhibitItem1.added = true;

        long numID1 = dao.insert(exhibitItem1);
        int size = dao.getDataCount();
        assertEquals(1, size);
        dao.clearAllAdded();
        size = dao.getDataCount();
        assertEquals(0, size);
    }


}
