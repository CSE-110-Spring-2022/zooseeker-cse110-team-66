package edu.ucsd.cse110.team66.zooseeker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class ExhibitDirectionsActivity extends AppCompatActivity {
    private ArrayList<String> exhibitDirections;
    private int directionIndex;
    int routeNum;
    TextView directionDisplay;
    Button nextDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_directions);
        SharedPreferences routeInfo = getSharedPreferences("routeInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = routeInfo.edit();

        routeNum = routeInfo.getInt("routeNum", 0);
        directionIndex = routeNum;
        Log.d("directionIndex", ""+directionIndex);
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
        nextDirection = findViewById(R.id.next_exhibit_direction_btn);
        nextDirection.setOnClickListener(view -> nextExhibitDirection());
        ++directionIndex;
    }

    // Go to the next direction; return to plan route if all directions have been displayed
    private void nextExhibitDirection() {
        SharedPreferences routeInfo = getSharedPreferences("routeInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = routeInfo.edit();
        if (directionIndex == exhibitDirections.size())
        {
            editor.putInt("routeNum", 0);
            editor.apply();
            finish();
            return;
        }
        directionDisplay.setText(exhibitDirections.get(directionIndex));
        editor.putInt("routeNum", directionIndex);
        editor.apply();
        ++directionIndex;
    }
}