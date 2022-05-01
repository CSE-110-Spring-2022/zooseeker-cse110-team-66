package edu.ucsd.cse110.team66.zooseeker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;

@Database(entities= {ExhibitItem.class}, version = 1, exportSchema = false)
public abstract class ExhibitDatabase extends RoomDatabase {
    private static ExhibitDatabase singleton = null;

    public abstract ExhibitItemDao exhibitItemDao();

    public synchronized static ExhibitDatabase getSingleton(Context context) {
        if (singleton == null) {
            singleton = ExhibitDatabase.makeDatabase(context);
        }
        return singleton;
    }

    private static ExhibitDatabase makeDatabase(Context context) {
        return Room.databaseBuilder(context, ExhibitDatabase.class, "todo_app.db")
                .allowMainThreadQueries()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadExecutor().execute(() -> {
                            List<ExhibitItem> todos = ExhibitItem
                                    .loadExhibits(context, "demo_todos.json");
                            getSingleton(context).exhibitItemDao().insertAll(todos);
                        });
                    }
                })
                .build();
    }
}
