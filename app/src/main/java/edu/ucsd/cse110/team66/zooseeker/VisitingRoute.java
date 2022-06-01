package edu.ucsd.cse110.team66.zooseeker;

import android.content.Context;
import android.util.Log;

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
        Gson gson = new Gson();
        exhibitsAdded = gson.fromJson(exhibitsAll, ArrayList.class);


        VisitingRoute.zooExhibitsData = ZooData.loadZooItemJSON(this.context,
                this.context.getString(R.string.exhibit_node_info_json),"all");

        // Creating quick search of exhibit names by mapping exhibit ids to names
        VisitingRoute.exhibit_id_to_name = new HashMap<String, String>();
        for (int i = 0; i < VisitingRoute.zooExhibitsData.size(); ++i) {
            VisitingRoute.exhibit_id_to_name.put(VisitingRoute.zooExhibitsData.get(i).id,
                    VisitingRoute.zooExhibitsData.get(i).name);
        }


        for (int i = 0; i < zooExhibitsData.size();++i) {
            if (zooExhibitsData.get(i).kind.toString().equals("GATE")) {
                entrance_and_exit_gate_id = zooExhibitsData.get(i).id;
                break;
            }
        }



        edgeinfo = ZooData.loadEdgeInfoJSON(this.context,
                this.context.getString(R.string.trail_edge_info_json));

        // Code for calculating route based on chosen exhibits, and storing
        // 1. Load the graph...

        VisitingRoute.g = ZooData.loadZooGraphJSON(this.context,this.context.getString(R.string.zoo_graph_json));
        VisitingRoute.saveRoute();
        VisitingRoute.generateCoordMap();
    }

    public static String closestExhibit() {
        double distance = Integer.MAX_VALUE;
        String closest = "";
        for (Map.Entry<String,LatLng> place:VisitingRoute.coordMap.entrySet()) {
            if (getDistanceFT(place.getValue()) < distance) {
                distance = getDistanceFT(place.getValue());
                closest = place.getKey();
            }
        }
        return closest;
    }

    public static double getDistanceFT(LatLng place) {
        return Math.sqrt(Math.pow(UserLocation.DEG_LAT_IN_FT * (UserLocation.currentLocation.latitude - place.latitude), 2) +
                Math.pow(UserLocation.DEG_LNG_IN_FT * (UserLocation.currentLocation.longitude - place.longitude), 2));
    }

    public static double getDistanceDegree(LatLng place) {
        return Math.sqrt(Math.pow(UserLocation.currentLocation.latitude - place.latitude, 2) +
                Math.pow(UserLocation.currentLocation.longitude - place.longitude, 2));
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
        return VisitingRoute.getDistanceDegree(place) < VisitingRoute.deltaDistance;
    }

    public static List<LatLng> getCoordsOnRouteDirection(int index) {
        List<PlanListItem> direction = VisitingRoute.route.get(index);
        List<LatLng> coords = new ArrayList<>();
        //if not looking at last direction
        coords.add(new LatLng(coordMap.get(direction.get(0).source_id).latitude,coordMap.get(direction.get(0).source_id).longitude));


        for (PlanListItem planListItem:direction) {
            coords.add(new LatLng(coordMap.get(planListItem.target_id).latitude, coordMap.get(planListItem.target_id).longitude));
        }

        return coords;
    }

    public static List<String> getExhibitsLeft(int index) {
        List<String> exhibitsLeft = new ArrayList<String>();
        for (int i = index; i < exhibit_visiting_order.size(); ++i) {
            exhibitsLeft.add(exhibit_visiting_order.get(i));
        }
        return exhibitsLeft;
    }

    // find direction to next closest exhibit from currentlocation to remaining exhibits
    public static List<PlanListItem> getNextFastestDirection(int index) {
        List<String> exhibitsLeft = VisitingRoute.getExhibitsLeft(index);
        //get rid of exit gate
        exhibitsLeft.remove(exhibitsLeft.size()-1);

        double currentDistance = Integer.MAX_VALUE;
        String currentPosition = VisitingRoute.closestExhibit();
        String closestExhibit = currentPosition;
        List<IdentifiedWeightedEdge> nextDirection = null;
        for (String nextPosition: exhibitsLeft) {
            GraphPath<String, IdentifiedWeightedEdge> path
                    = VisitingRoute.getFastestDirection(currentPosition, nextPosition);
            if (currentDistance > path.getWeight()) {
                currentDistance =  path.getWeight();
                nextDirection = path.getEdgeList();
                closestExhibit = nextPosition;
            }
        }

        if (nextDirection == null) {
            nextDirection = VisitingRoute.getFastestDirection(currentPosition, VisitingRoute.entrance_and_exit_gate_id).getEdgeList();
        }

        List<PlanListItem> currentDirection = new ArrayList<>();
        for (int j = 0; j < nextDirection.size(); ++j) {
            IdentifiedWeightedEdge to_add = nextDirection.get(j);
            String street_name = edgeinfo.get(to_add.getId()).street;
            String street_id = to_add.getId();
            String source_id;
            String target_id;
            if (to_add.getSource().equals(currentPosition)) {
                source_id = to_add.getSource();
                target_id = to_add.getTarget();
            }
            else {
                source_id = to_add.getTarget();
                target_id = to_add.getSource();
            }
            currentPosition = target_id;
            String source_name = exhibit_id_to_name.get(source_id);
            String target_name = exhibit_id_to_name.get(target_id);
            double weight = to_add.getWeight();
            currentDirection.add(new PlanListItem(street_name,street_id,
                    source_id,target_id,source_name,target_name,weight));
        }

        return currentDirection;

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
        return VisitingRoute.route;
    }

    public static void saveRoute() {
        Vector<List<IdentifiedWeightedEdge>> Directions = VisitingRoute.getFastestPathToEnd(VisitingRoute.entrance_and_exit_gate_id, VisitingRoute.exhibitsAdded);
        VisitingRoute.route = VisitingRoute.getPlannedDirections(VisitingRoute.entrance_and_exit_gate_id,Directions);
        VisitingRoute.saveExhibitsVisitingOrder();
    }

    public static String getExhibitToVisitAtIndex(int index) {
        return VisitingRoute.exhibit_visiting_order.get(index);
    }

    public static List<String> getExhibitsToVisitOrder() {
        return VisitingRoute.exhibit_visiting_order;
    }

    public static void saveExhibitsVisitingOrder() {
        exhibit_visiting_order = new ArrayList<String>();
        for (List<PlanListItem> direction : VisitingRoute.route) {
            exhibit_visiting_order.add(direction.get(direction.size() - 1).target_id);
        }
    }

    public static Vector<List<IdentifiedWeightedEdge>> getFastestPathToEnd(String currentPosition, List<String> exhibitsAdded) {
        Vector<List<IdentifiedWeightedEdge>> Directions = new Vector<List<IdentifiedWeightedEdge>>();

        // 2. Find the fastest path.
        Vector<String> toPathFind = new Vector<String>();
        for (int i = 0; i < exhibitsAdded.size(); ++i) {
            toPathFind.add(exhibitsAdded.get(i));
        }
        for (int i = 0; i < exhibitsAdded.size(); ++i) {
            double currentDistance = Integer.MAX_VALUE;
            String closestExhibit = currentPosition;
            List<IdentifiedWeightedEdge> nextDirection = null;
            for (String nextPosition: toPathFind) {
                GraphPath<String, IdentifiedWeightedEdge> path
                        = VisitingRoute.getFastestDirection(currentPosition, nextPosition);
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

        Directions.add(VisitingRoute.getFastestDirection(currentPosition, VisitingRoute.entrance_and_exit_gate_id).getEdgeList());

        return Directions;
    }

    public static GraphPath<String, IdentifiedWeightedEdge> getFastestDirection(String pos1, String pos2) {
        return DijkstraShortestPath.findPathBetween(VisitingRoute.g, pos1, pos2);
    }

    public static List<PlanListItem> getExhibitDirections(String start, String dest){
        List<IdentifiedWeightedEdge> path = getFastestDirection(start, dest).getEdgeList();
        return constructDirection(start, path);
    }

    public static List<List<PlanListItem>> getPlannedDirections(String previous, Vector<List<IdentifiedWeightedEdge>> Directions) {
        // Create directions object to help with generating direction texts
        List<List<PlanListItem>> plannedDirections = new ArrayList<>();
        for (int i = 0; i < Directions.size(); ++i) {
            List<IdentifiedWeightedEdge> path = Directions.get(i);
            List<PlanListItem> constructDirection = constructDirection(previous, path);
            plannedDirections.add(constructDirection);
            previous=constructDirection.get(constructDirection.size()-1).target_id;
        }
        return plannedDirections;
    }

    /** Construct directions from one exhibit to another exhibit based on given edges **/
    private static List<PlanListItem> constructDirection(String previous, List<IdentifiedWeightedEdge> path) {
        List<PlanListItem> currentDirection = new ArrayList<>();
        for (int j = 0; j < path.size(); ++j) {
            IdentifiedWeightedEdge to_add = path.get(j);
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
        return currentDirection;
    }
}
