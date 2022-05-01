package edu.ucsd.cse110.team66.zooseeker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;

public class ExhibitRouteActivity extends AppCompatActivity {
    private final String start = "entrance_exit_gate";
    private String goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_route);

        // Code for calculating route based on chosen exhibits, and storing
        // 1. Load the graph...
//        Graph<String, IdentifiedWeightedEdge> g
//                = ZooData.loadZooGraphJSON(this,"sample_zoo_graph.json");
//        GraphPath<String, IdentifiedWeightedEdge> path
//                = DijkstraShortestPath.findPathBetween(g, start, goal);

        // back button
        Button back_btn = (Button) findViewById(R.id.back_btn_plan);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExhibitRouteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // load plan list
    }

}
