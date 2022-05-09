package edu.ucsd.cse110.team66.zooseeker;

import android.content.Context;

import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class PlanListItem {
    public String street_name;
    public String street_id;
    public String source_id;
    public String target_id;
    public String source_name;
    public String target_name;
    public double weight;

    private static String exhibitName;


    PlanListItem(String street_name, String street_id, String source_id,
                 String target_id, String source_name, String target_name,
                 double weight) {
        this.street_name = street_name;
        this.street_id = street_id;
        this.source_id = source_id;
        this.target_id = target_id;
        this.source_name = source_name;
        this.target_name = target_name;
        this.weight = weight;
    }

    public static String toMessage(List<PlanListItem> items) {
        String message ="";
        for (int i = 0; i < items.size(); ++i) {
            if (i == 0) {
                message+= "From ";
            }
            else {
                message+="Then ";
            }
            message += items.get(i).source_name + ", walk down " + items.get(i).street_name  +
                    " for " + String.valueOf(items.get(i).weight) + " feet towards " +
                    items.get(i).target_name + ".";
        }
        return message;
    }


    public static String toMessageDestination(List<PlanListItem> items) {
        return items.get(items.size()-1).target_name;
    }


    public static List<PlanListItem> loadJSON(Context context, String node_info_path, String zoo_graph_path, String edge_info_path) {
        try {
            InputStream input = context.getAssets().open(node_info_path);
            Reader reader = new InputStreamReader(input);
            //JsonParser parser = new JsonParser();
            //JsonArray obj = (JsonArray) parser.parse(reader.toString());
            Gson gson = new Gson();
            Type type = new TypeToken<List<PlanListItem>>(){}.getType();
            return gson.fromJson(reader,type);

        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
