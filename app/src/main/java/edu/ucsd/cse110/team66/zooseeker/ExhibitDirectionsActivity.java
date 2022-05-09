package edu.ucsd.cse110.team66.zooseeker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.TypedValue;

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

        displayDirection();
        setNextDirectionButton();
    }

    // Display the direction(s) to the next closest exhibit on the screen
    private void displayDirection() {
        Gson gson= new Gson();
        exhibitDirections = gson.fromJson(getIntent().getExtras().getString("exhibitDirections"), ArrayList.class);
        directionDisplay = findViewById(R.id.direction_display);
        directionDisplay.setText(exhibitDirections.get(directionIndex));
        directionDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
    }

    // Set up direction button
    private void setNextDirectionButton() {
        directionIndex = 0;

        nextDirection = findViewById(R.id.next_exhibit_direction_btn);
        nextDirection.setOnClickListener(view -> nextExhibitDirection());
        ++directionIndex;
    }


    // Go to the next direction; return to plan route if all directions have been displayed
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