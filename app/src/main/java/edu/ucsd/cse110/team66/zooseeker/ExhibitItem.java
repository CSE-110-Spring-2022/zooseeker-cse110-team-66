package edu.ucsd.cse110.team66.zooseeker;

import android.content.Context;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ExhibitItem {
    private String id;
    private String name;
    private List<String> tags;

    public ExhibitItem(String id, String name, List<String> tags) {
        this.id = id;
        this.name = name;
        this.tags = tags;
    }

    public ExhibitItem(ZooData.VertexInfo item) {
        this.id = item.id;
        this.name = item.name;
        this.tags = item.tags;
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
                '}';
    }
}
