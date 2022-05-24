package edu.ucsd.cse110.team66.zooseeker;

import android.content.Context;

import com.google.gson.Gson;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class VisitingRoute {
    public Context context;
    public static List<ZooData.VertexInfo> zooExhibitsData;
    public static String entrance_and_exit_gate_id;
    public static List<String> exhibitsAdded;
    public static Graph<String, IdentifiedWeightedEdge> g;
    public static Map<String, ZooData.EdgeInfo> edgeinfo;
    public static Map<String, String> exhibit_id_to_name;


    public VisitingRoute(Context context, String exhibitsAll) {
        this.context = context;

        // Get selected animal exhibits
        if (VisitingRoute.exhibitsAdded == null) {
            Gson gson = new Gson();
            exhibitsAdded = gson.fromJson(exhibitsAll, ArrayList.class);
        }

        // Get Zoo Exhibits Data
        if (VisitingRoute.zooExhibitsData == null) {
            VisitingRoute.zooExhibitsData = ZooData.loadZooItemJSON(this.context,
                    this.context.getString(R.string.exhibit_node_info_json),"all");

            // Creating quick search of exhibit names by mapping exhibit ids to names
            VisitingRoute.exhibit_id_to_name = new HashMap<String, String>();
            for (int i = 0; i < VisitingRoute.zooExhibitsData.size(); ++i) {
                VisitingRoute.exhibit_id_to_name.put(VisitingRoute.zooExhibitsData.get(i).id,
                        VisitingRoute.zooExhibitsData.get(i).name);
            }
        }


        // Get Entrance/Exit id
        if (VisitingRoute.entrance_and_exit_gate_id == null) {
            for (int i = 0; i < zooExhibitsData.size();++i) {
                if (zooExhibitsData.get(i).kind.toString() == "GATE") {
                    entrance_and_exit_gate_id = zooExhibitsData.get(i).id;
                    break;
                }
            }
        }

        if (VisitingRoute.edgeinfo == null) {
            edgeinfo = ZooData.loadEdgeInfoJSON(this.context,
                    this.context.getString(R.string.trail_edge_info_json));
        }

        // Code for calculating route based on chosen exhibits, and storing
        // 1. Load the graph...
        if (VisitingRoute.g == null) {
            VisitingRoute.g = ZooData.loadZooGraphJSON(this.context,this.context.getString(R.string.zoo_graph_json));
        }


    }

    public static List<List<PlanListItem>> getRoute() {
        Vector<List<IdentifiedWeightedEdge>> Directions = VisitingRoute.get_fastest_path_to_end(VisitingRoute.entrance_and_exit_gate_id, VisitingRoute.exhibitsAdded);
        return VisitingRoute.get_planned_directions(VisitingRoute.entrance_and_exit_gate_id,Directions);
    }



    public static Vector<List<IdentifiedWeightedEdge>> get_fastest_path_to_end(String currentPosition, List<String> exhibitsAdded) {
        Vector<List<IdentifiedWeightedEdge>> Directions = new Vector<List<IdentifiedWeightedEdge>>();

        // 2. Find the fastest path.
        Vector<String> toPathFind = new Vector<String>();
        for (int i = 0; i < exhibitsAdded.size(); ++i) {
            toPathFind.add(exhibitsAdded.get(i));
        }
        for (int i = 0; i < exhibitsAdded.size(); ++i) {
            double currentDistance = 999999999;
            String closestExhibit = currentPosition;
            List<IdentifiedWeightedEdge> nextDirection = null;
            Boolean possible_reverse = false;
            for (String nextPosition: toPathFind) {
                GraphPath<String, IdentifiedWeightedEdge> path
                        = VisitingRoute.get_fastest_direction(currentPosition, nextPosition);
                if (currentDistance > path.getWeight()) {
                    currentDistance =  path.getWeight();
                    nextDirection = path.getEdgeList();
                    closestExhibit = nextPosition;
                }
            }

            Directions.add(nextDirection);
            currentPosition=closestExhibit;
            toPathFind.remove(currentPosition);
        }

        Directions.add(VisitingRoute.get_fastest_direction(currentPosition, VisitingRoute.entrance_and_exit_gate_id).getEdgeList());

        return Directions;
    }

    public static GraphPath<String, IdentifiedWeightedEdge> get_fastest_direction(String pos1, String pos2) {
        return DijkstraShortestPath.findPathBetween(VisitingRoute.g, pos1, pos2);
    }

    public static List<List<PlanListItem>> get_planned_directions(String previous, Vector<List<IdentifiedWeightedEdge>> Directions) {
        // Create directions object to help with generating direction texts
        List<List<PlanListItem>> plannedDirections = new ArrayList<>();
        for (int i = 0; i < Directions.size(); ++i) {
            List<PlanListItem> currentDirection = new ArrayList<>();
            for (int j = 0; j < Directions.get(i).size(); ++j) {
                IdentifiedWeightedEdge to_add = Directions.get(i).get(j);
                String street_name = edgeinfo.get(to_add.getId()).street;
                String street_id = to_add.getId();
                String source_id;
                String target_id;
                if (to_add.getSource().equals(previous)) {
                    source_id = to_add.getSource();
                    target_id = to_add.getTarget();
                }
                else {
                    source_id = to_add.getTarget();
                    target_id = to_add.getSource();
                }
                previous = target_id;
                String source_name = exhibit_id_to_name.get(source_id);
                String target_name = exhibit_id_to_name.get(target_id);
                double weight = to_add.getWeight();
                currentDirection.add(new PlanListItem(street_name,street_id,
                        source_id,target_id,source_name,target_name,weight));
            }
            plannedDirections.add(currentDirection);
        }

        return plannedDirections;
    }
}
