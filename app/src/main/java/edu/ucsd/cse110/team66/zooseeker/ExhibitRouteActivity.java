package edu.ucsd.cse110.team66.zooseeker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ExhibitRouteActivity extends AppCompatActivity {
    private final String start = "entrance_exit_gate";
    public RecyclerView recyclerView;
    private ArrayList<String> detailedExhibitDirections;
    private ArrayList<String> exhibitDirections;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_route);
        setUpBackButton();

        VisitingRoute routePlanner = new VisitingRoute(this, getIntent().getExtras().getString("exhibitsAll"));
        List<List<PlanListItem>> plannedDirections = VisitingRoute.getRoute();

        PlanListAdapter adapter = new PlanListAdapter();
        adapter.setHasStableIds(true);
        adapter.setPlanListItems(plannedDirections);

        recyclerView = findViewById(R.id.plan_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        detailedExhibitDirections = new ArrayList<String>();
        exhibitDirections = new ArrayList<String>();
        for (int i = 0; i < plannedDirections.size(); ++i) {
            detailedExhibitDirections.add(PlanListItem.toDetailedMessage(plannedDirections.get(i)));
            exhibitDirections.add(PlanListItem.toBriefMessage(plannedDirections.get(i)));
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
        String jsonDetailed = gson.toJson(detailedExhibitDirections);
        String json = gson.toJson(exhibitDirections);
        Intent intent = new Intent(this, ExhibitDirectionsActivity.class);
        intent.putExtra("detailedExhibitDirections",jsonDetailed);
        intent.putExtra("exhibitDirections",json);
        startActivity(intent);
    }

}
