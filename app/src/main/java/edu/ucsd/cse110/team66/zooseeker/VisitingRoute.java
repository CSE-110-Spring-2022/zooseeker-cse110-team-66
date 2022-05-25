package edu.ucsd.cse110.team66.zooseeker;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
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
    public static List<List<PlanListItem>> route;
    public static List<String> exhibit_visiting_order;
    public static Map<String, LatLng> coordMap;
    public static double deltaDistance = 0.001;

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

    public static boolean followingCurrentDirection(int index) {
        List<LatLng> coordsOnRouteDirection = VisitingRoute.getCoordsOnRouteDirection(index);
        for (LatLng place:coordsOnRouteDirection) {
            if (VisitingRoute.isCloseTo(place)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCloseTo(LatLng place) {
        return Math.sqrt(Math.pow(UserLocation.currentLocation.latitude - place.latitude, 2) +
                Math.pow(UserLocation.currentLocation.longitude - place.longitude, 2)) < VisitingRoute.deltaDistance;
    }

    public static List<LatLng> getCoordsOnRouteDirection(int index) {
        List<PlanListItem> direction = VisitingRoute.route.get(index);
        List<LatLng> coords = new ArrayList<>();

        if (VisitingRoute.coordMap == null) {
            VisitingRoute.generateCoordMap();
        }

        for (PlanListItem planListItem:direction) {
            coords.add(new LatLng(coordMap.get(planListItem.target_id).latitude, coordMap.get(planListItem.target_id).longitude));
        }

        return coords;
    }

    public static void generateCoordMap() {
        coordMap = new HashMap<String, LatLng>();
        for (ZooData.VertexInfo vertexInfo:VisitingRoute.zooExhibitsData) {
            if (!(vertexInfo.kind == ZooData.VertexInfo.Kind.EXHIBIT && vertexInfo.group_id != null)) {
                VisitingRoute.coordMap.put(vertexInfo.id, new LatLng(vertexInfo.lat,vertexInfo.lng));
            }
        }

    }

    public static List<List<PlanListItem>> getRoute() {
        if (VisitingRoute.route == null) {
            VisitingRoute.saveRoute();
        }
        return VisitingRoute.route;
    }

    public static void saveRoute() {
        Vector<List<IdentifiedWeightedEdge>> Directions = VisitingRoute.get_fastest_path_to_end(VisitingRoute.entrance_and_exit_gate_id, VisitingRoute.exhibitsAdded);
        VisitingRoute.route = VisitingRoute.get_planned_directions(VisitingRoute.entrance_and_exit_gate_id,Directions);
    }

    public static String getExhibitToVisitAtIndex(int index) {
        if (VisitingRoute.exhibit_visiting_order == null) {
            VisitingRoute.saveExhibitsVisitingOrder();
        }
        return VisitingRoute.exhibit_visiting_order.get(index);
    }

    public static List<String> getExhibitsToVisitOrder() {
        if (VisitingRoute.exhibit_visiting_order == null) {
            VisitingRoute.saveExhibitsVisitingOrder();
        }
        return VisitingRoute.exhibit_visiting_order;
    }

    public static void saveExhibitsVisitingOrder() {
        exhibit_visiting_order = new ArrayList<String>();
        for (List<PlanListItem> direction:VisitingRoute.route) {
            exhibit_visiting_order.add(direction.get(direction.size() - 1).target_id);
        }
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
