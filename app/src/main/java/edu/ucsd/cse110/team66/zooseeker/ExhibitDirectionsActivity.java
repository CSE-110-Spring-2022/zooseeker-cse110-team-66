package edu.ucsd.cse110.team66.zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

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
    private ArrayList<String> detailedExhibitDirections;
    private int directionIndex;
    int routeNum;
    TextView directionDisplay;
    Button nextDirection;
    SwitchCompat detailedBtn;
    Boolean isCheck = false;

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
        detailedExhibitDirections = gson.fromJson(getIntent().getExtras().getString("detailedExhibitDirections"), ArrayList.class);
        directionDisplay = findViewById(R.id.direction_display);

        // Show if Detailed Direction
        detailedBtn = findViewById(R.id.detailed_directions);
        detailedBtn.setOnCheckedChangeListener((compoundButton, checked) -> refresh());
        refresh();
        directionDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
    }

    private void refresh(){
        if(exhibitDirections.isEmpty() || detailedExhibitDirections.isEmpty())
            return;
        if (detailedBtn.isChecked()) {
            directionDisplay.setText(detailedExhibitDirections.get(directionIndex));
            isCheck = true;
        }
        else {
            directionDisplay.setText(exhibitDirections.get(directionIndex));
            isCheck = false;
        }
    }

    // Set up direction button
    private void setNextDirectionButton() {
        nextDirection = findViewById(R.id.next_exhibit_direction_btn);
        nextDirection.setOnClickListener(view -> nextExhibitDirection());
        //directionIndex++;
    }

    // Go to the next direction; return to plan route if all directions have been displayed
    private void nextExhibitDirection() {
        SharedPreferences routeInfo = getSharedPreferences("routeInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = routeInfo.edit();
        int i = exhibitDirections.size();
        if (directionIndex >= exhibitDirections.size()-1)
        {
            editor.putInt("routeNum", 0);
            editor.apply();
            finish();
            return;
        }
        editor.putInt("routeNum", directionIndex);
        editor.apply();
        ++directionIndex;
        if(isCheck)
            directionDisplay.setText(detailedExhibitDirections.get(directionIndex));
        else
            directionDisplay.setText(exhibitDirections.get(directionIndex));
    }
}