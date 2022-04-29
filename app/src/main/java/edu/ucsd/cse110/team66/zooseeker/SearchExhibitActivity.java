package edu.ucsd.cse110.team66.zooseeker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class SearchExhibitActivity extends AppCompatActivity {
    private Button planButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_exhibit);

        List<ExhibitItem> exhibits = ExhibitItem.loadExhibits(this,"sample_node_info.json");
        Log.d("SearchExhibitActivity", exhibits.toString());

        planButton = (Button) findViewById(R.id.plan_btn);
        planButton.setOnClickListener(v -> openExhibitRouteActivity());
    }

    private void openExhibitRouteActivity() {

        // Code for calculating route based on chosen exhibits, and storing

        Intent intent = new Intent(this, ExhibitRouteActivity.class);
        startActivity(intent);
    }
}