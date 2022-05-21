package edu.ucsd.cse110.team66.zooseeker;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExhibitItemDao {
    @Insert
    long insert(ExhibitItem exhibitItem);

    @Insert
    List<Long> insertAll(List<ExhibitItem> todoListItem);

    @Query("SELECT * FROM `exhibit_items` WHERE `id`=:id")
    ExhibitItem get(String id);

    @Query("SELECT * FROM `exhibit_items` ORDER BY `name`")
    List<ExhibitItem> getAll();

    @Query("SELECT * FROM `exhibit_items` ORDER BY `name`")
    LiveData<List<ExhibitItem>> getAllLive();

    @Update
    int update(ExhibitItem exhibitItem);

    @Delete
    int delete(ExhibitItem exhibitItem);

}
