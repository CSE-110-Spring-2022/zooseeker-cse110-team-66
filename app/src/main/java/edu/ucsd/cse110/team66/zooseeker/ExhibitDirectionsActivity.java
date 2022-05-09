package edu.ucsd.cse110.team66.zooseeker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class ExhibitDirectionsActivity extends AppCompatActivity {
    private ArrayList<String> exhibitDirections;
    private int directionIndex;
    TextView directionDisplay;
    Button nextDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_directions);
        Gson gson= new Gson();
        exhibitDirections = gson.fromJson(getIntent().getExtras().getString("exhibitDirections"), ArrayList.class);
        directionIndex = 0;
        directionDisplay = findViewById(R.id.direction_display);
        directionDisplay.setText(exhibitDirections.get(directionIndex));
        nextDirection = findViewById(R.id.next_exhibit_direction_btn);
        nextDirection.setOnClickListener(view -> nextExhibitDirection());
        ++directionIndex;
    }

    private void nextExhibitDirection() {
        if (directionIndex == exhibitDirections.size())
        {
            finish();
            return;
        }
        directionDisplay.setText(exhibitDirections.get(directionIndex));
        ++directionIndex;
    }
}