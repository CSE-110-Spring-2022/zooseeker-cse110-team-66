package edu.ucsd.cse110.team66.zooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

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

}
