package edu.ucsd.cse110.team66.zooseeker;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities= {ExhibitItem.class}, version = 1, exportSchema = false)
public abstract class ExhibitDatabase extends RoomDatabase {
    public abstract ExhibitItemDao exhibitItemDao();
}
