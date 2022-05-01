package edu.ucsd.cse110.team66.zooseeker;

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

    @Query("SELECT * FROM `exhibit_items` WHERE `id`=:id")
    ExhibitItem get(String id);

    @Query("SELECT * FROM `exhibit_items` ORDER BY `id`")
    List<ExhibitItem> getAll();

    @Update
    int update(ExhibitItem exhibitItem);

    @Delete
    int delete(ExhibitItem exhibitItem);

}
