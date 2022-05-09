package edu.ucsd.cse110.team66.zooseeker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ExhibitRouteActivity extends AppCompatActivity {
    private final String start = "entrance_exit_gate";
    public RecyclerView recyclerView;
    private ArrayList<String> exhibitDirections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_route);
        setUpBackButton();

        Gson gson = new Gson();
        String exhibitsAll = getIntent().getExtras().getString("exhibitsAll");
        ArrayList<String> exhibitsAdded = gson.fromJson(exhibitsAll, ArrayList.class);
        // Code for calculating route based on chosen exhibits, and storing
        // 1. Load the graph...
        Graph<String, IdentifiedWeightedEdge> g
                = ZooData.loadZooGraphJSON(this,"sample_zoo_graph.json");
        DijkstraShortestPath graph = new DijkstraShortestPath(g);
        Vector<String> fastestPath = new Vector<String>();
        Vector<Double> pathDistances = new Vector<Double>();
        Vector<Boolean> to_reverse = new Vector<Boolean>();
        Vector<List<IdentifiedWeightedEdge>> Directions = new Vector<List<IdentifiedWeightedEdge>>();
        String currentPosition = "entrance_plaza";
        fastestPath.add(start);
        fastestPath.add(currentPosition);
        pathDistances.add(0.0);
        pathDistances.add(10.0);
        Vector<String> toPathFind = new Vector<String>();
        for (int i = 0; i < exhibitsAdded.size(); ++i) {
            toPathFind.add(exhibitsAdded.get(i));
        }
        for (int i = 0; i < exhibitsAdded.size(); ++i) {
            double currentDistance = Integer.MAX_VALUE;
            String closestExhibit = currentPosition;
            List<IdentifiedWeightedEdge> nextDirection = null;
            Boolean possible_reverse = false;
            for (String nextExhibit: toPathFind) {
                GraphPath<String, IdentifiedWeightedEdge> path
                    = DijkstraShortestPath.findPathBetween(g, currentPosition, nextExhibit);
                if (currentDistance > path.getWeight()) {
                    currentDistance =  path.getWeight();
                    nextDirection = path.getEdgeList();
                    closestExhibit = nextExhibit;
                }
            }
            to_reverse.add(possible_reverse);
            fastestPath.add(closestExhibit);
            pathDistances.add(currentDistance);
            Directions.add(nextDirection);
            currentPosition=closestExhibit;
            toPathFind.remove(currentPosition);
        }

        List<ZooData.VertexInfo> zooData =
                ZooData.loadZooItemJSON(this, "sample_node_info.json","exhibits");
        Map<String, String> exhibit_id_to_name = new HashMap<String, String>();
        exhibit_id_to_name.put("entrance_exit_gate", "Entrance and Exit Gate");
        exhibit_id_to_name.put("entrance_plaza", "Entrance Plaza");
        for (int i = 0; i < zooData.size(); ++i) {
            exhibit_id_to_name.put(zooData.get(i).id,zooData.get(i).name);
        }
        Map<String, ZooData.EdgeInfo> edgeinfo =
                ZooData.loadEdgeInfoJSON(this, "sample_edge_info.json");

        List<List<PlanListItem>> plannedDirections = new ArrayList<>();
        List<PlanListItem> firstDirection = new ArrayList<>();
        firstDirection.add(new PlanListItem("Entrance Way",
                "edge-0","entrance_exit_gate",
                "entrance_plaza","Entrance and Exit Gate",
                "Entrance Plaza",10.0));
        String previous = "entrance_plaza";
        plannedDirections.add(firstDirection);
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

        PlanListAdapter adapter = new PlanListAdapter();
        adapter.setHasStableIds(true);
        adapter.setPlanListItems(plannedDirections);

        recyclerView = findViewById(R.id.plan_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        exhibitDirections = new ArrayList<String>();
        for (int i = 0; i < plannedDirections.size(); ++i) {
            exhibitDirections.add(PlanListItem.toMessage(plannedDirections.get(i)));
        }

        Button directions_btn = findViewById(R.id.directions_btn);
        directions_btn.setOnClickListener(view -> openExhibitDirectionsActivity());
    }

    // Set up back button at the top left
    private void setUpBackButton() {
        ActionBar actionBar = getSupportActionBar();
        // Customize the back button
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    // Go back to Search_Exhibit when the back button is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openExhibitDirectionsActivity() {
        Gson gson = new Gson();
        String json = gson.toJson(exhibitDirections);
        Intent intent = new Intent(this, ExhibitDirectionsActivity.class);
        intent.putExtra("exhibitDirections",json);
        startActivity(intent);
    }

}
