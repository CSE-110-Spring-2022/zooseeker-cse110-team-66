package edu.ucsd.cse110.team66.zooseeker;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ExhibitItemViewModel extends AndroidViewModel {
    private LiveData<List<ExhibitItem>> exhibitItems;
    private final ExhibitItemDao exhibitItemDao;

    public ExhibitItemViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        exhibitItemDao = db.exhibitItemDao();
    }

    public LiveData<List<ExhibitItem>> getExhibitItems() {
        if (exhibitItems == null) {
            loadUsers();
        }
        return exhibitItems;
    }

    public void loadUsers() { exhibitItems = exhibitItemDao.getAllLive();}

    public void toggleAdded(ExhibitItem exhibitItem) {
        exhibitItem.added = !exhibitItem.added;
        exhibitItemDao.update(exhibitItem);
    }
}
