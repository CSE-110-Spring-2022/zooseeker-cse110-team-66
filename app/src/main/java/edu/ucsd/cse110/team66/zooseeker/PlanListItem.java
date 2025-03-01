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
    // Public fields for directions
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

    public static String toDetailedMessage(List<PlanListItem> items) {
        if (items.size() == 0) {
            return "To " + VisitingRoute.exhibit_id_to_name.get(VisitingRoute.closestExhibit()) + ":\nYou are there.";
        }
        String message = "To " + items.get(items.size()-1).target_name + ": \n";
        for (int i = 0; i < items.size(); ++i) {
            if (i == 0) {
                message+= "From the ";
            }
            else {
                message+="Then from the ";
            }
            message += items.get(i).source_name + " exhibit, walk down " + items.get(i).street_name  +
                    " for " + String.valueOf(items.get(i).weight) + " feet towards " +
                    items.get(i).target_name + ".\n";
        }
        return message;
    }

    public static String toBriefMessage(List<PlanListItem> items) {
        if (items.size() == 0) {
            return "To " + VisitingRoute.exhibit_id_to_name.get(VisitingRoute.closestExhibit()) + ":\nYou are there.";
        }
        String destination = "To " + items.get(items.size()-1).target_name + ": \n";
        String message = "";
        double weighttotal = 0;
        int i = 0;
        while (i < items.size()) {
            Log.d("test2", items.get(i).street_name);
            weighttotal = items.get(i).weight;
            if ((i+1) < items.size()) {
                while ((i+1) < items.size() && items.get(i).street_name.equals(items.get(i+1).street_name)) {
                    Log.d("test1", "test ok");
                    weighttotal += items.get(i+1).weight;
                    i++;
                    continue;
                }
            }
            message += "Walk " + String.valueOf(weighttotal) + " feet towards " +
                    items.get(i).target_name + ".\n";
            weighttotal = 0;
            i++;
        }
        return destination + message;
    }


    public static String toMessage(List<PlanListItem> items) {
        //String message = "To " + items.get(items.size()-1).target_name + ": \n";
        String message = "";
        for (int i = 0; i < items.size(); ++i) {
            message += "Walk " + String.valueOf(items.get(i).weight) + " feet towards " +
                    items.get(i).target_name + ".\n";
        }
        return message;
    }





    public static String getName (List<PlanListItem> items) {
        return items.get(items.size()-1).target_name;
    }

    public static String getStreet (List<PlanListItem> items) {
        return items.get(items.size()-1).street_name;
    }

    public static double getDistance (List<PlanListItem> items) {
        double length = 0;
        for (int i = 0; i < items.size(); ++i) {
            length += items.get(i).weight;
        }
        return length;
    }

    public static List<PlanListItem> loadJSON (Context context, String node_info_path, String zoo_graph_path, String edge_info_path) {
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
