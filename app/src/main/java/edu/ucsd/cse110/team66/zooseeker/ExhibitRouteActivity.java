package edu.ucsd.cse110.team66.zooseeker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ExhibitRouteActivity extends AppCompatActivity {
    private final String start = "entrance_exit_gate";
    private String goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_route);

        Gson gson = new Gson();
        String exhibitsAll = getIntent().getExtras().getString("exhibitsAll");
        ArrayList<String> exhibitsAdded = gson.fromJson(exhibitsAll, ArrayList.class);
        // Code for calculating route based on chosen exhibits, and storing
        // 1. Load the graph...
        Graph<String, IdentifiedWeightedEdge> g
                = ZooData.loadZooGraphJSON(this,"sample_zoo_graph.json");
        Vector<String> fastestPath = new Vector<String>();
        String currentPosition = "entrance_plaza";
        fastestPath.add(start);
        fastestPath.add(currentPosition);
        Vector<String> toPathFind = new Vector<String>();
        for (int i = 0; i < exhibitsAdded.size(); ++i) {
            toPathFind.add(exhibitsAdded.get(i));
        }
        for (int i = 0; i < exhibitsAdded.size(); ++i) {
            int currentDistance = 999999999;
            String closestExhibit = currentPosition;
            for (String nextExhibit: toPathFind) {
                GraphPath<String, IdentifiedWeightedEdge> path
                    = DijkstraShortestPath.findPathBetween(g, currentPosition, nextExhibit);
                if (currentDistance > path.getWeight()) {
                    currentDistance = (int) path.getWeight();
                    closestExhibit = nextExhibit;
                }
            }
            fastestPath.add(closestExhibit);
            currentPosition=closestExhibit;
            toPathFind.remove(currentPosition);
        }


        // back button
        Button back_btn = (Button) findViewById(R.id.back_btn_plan);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // load plan list
    }

}
