package edu.ucsd.cse110.team66.zooseeker;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExhibitItem {


    private String id;
    private String kind;
    private String name;
    private String[] tags;

    public ExhibitItem(String id, String name, String[] tags) {
        this.id = id;
        this.kind = "exhibit";
        this.name = name;
        this.tags = tags;
    }

    public String getId() { return id; }

    public String getName() {
        return name;
    }

    public String[] getTags() {
        return tags;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public static List<ExhibitItem> loadExhibits(Context context, String path) {
        try {
            InputStream inputStream = context.getAssets().open(path);
            Reader reader = new InputStreamReader(inputStream);

            Gson gson = new Gson();
            Type type = new TypeToken<List<ExhibitItem>>(){}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public String toString() {
        return "ExhibitItem{" +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }
}
