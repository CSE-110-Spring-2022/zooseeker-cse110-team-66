package edu.ucsd.cse110.team66.zooseeker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Entity(tableName = "exhibit_items")
@TypeConverters(LanguageConverter.class)
public class ExhibitItem {

    @PrimaryKey(autoGenerate = true)
    public long numID;

    public String id;
    public String name;
    public List<String> tags;

    @ColumnInfo(name = "added")
    public boolean added;

    public double lat;
    public double lng;

    @Ignore
    public ExhibitItem(@NonNull String id, String name, List<String> tags) {
        this.id = id;
        this.name = name;
        this.tags = tags;
        this.added = false;
    }

    public ExhibitItem(@NonNull String id, String name, List<String> tags, boolean added) {
        this.id = id;
        this.name = name;
        this.tags = tags;
        this.added = added;
    }

    public ExhibitItem(@NonNull String id, String name, List<String> tags, boolean added, double lat, double lng) {
        this.id = id;
        this.name = name;
        this.tags = tags;
        this.added = added;
        this.lat = lat;
        this.lng = lng;
    }

    public ExhibitItem(ZooData.VertexInfo item) {
        this.id = item.id;
        this.name = item.name;
        this.tags = item.tags;
        this.added = item.added;
        this.lat = item.lat;
        this.lng = item.lng;
    }

    public String getId() { return id; }

    public String getName() {
        return name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getAdded(){return added;}

    public void setAdded(boolean added){this.added=added;}

    public void setAdded(View itemView) {
        Button button = itemView.findViewById(R.id.add_exhibit_btn);
        button.setText("ADDED");
        button.setBackgroundColor(Color.rgb(192,192,192));
    }

    public void setAdd(View itemView) {
        Button button = itemView.findViewById(R.id.add_exhibit_btn);
        button.setText("ADD");
        button.setBackgroundColor(Color.rgb(21,71,52));
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public static List<ExhibitItem> loadExhibits(Context context, String path) {
        List<ExhibitItem> exhibits = new ArrayList<>();
        for (ZooData.VertexInfo item : ZooData.loadZooItemJSON(context, path, "exhibits")) {
            exhibits.add(new ExhibitItem(item));
        }
        return exhibits;
    }

    // Display exhibits in alphabetical order
    public static Comparator<ExhibitItem> ExhibitNameComparator = new Comparator<ExhibitItem>() {
        @Override
        public int compare(ExhibitItem e1, ExhibitItem e2) {
            return e1.getName().compareTo(e2.getName());
        }
    };

    @Override
    public String toString() {
        return "ExhibitItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", tags=" + tags +
                ", added=" + added +
                '}';
    }
}
