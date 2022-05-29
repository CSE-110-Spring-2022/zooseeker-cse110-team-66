package edu.ucsd.cse110.team66.zooseeker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ExhibitDirectionsActivity extends AppCompatActivity {
    private ArrayList<String> exhibitDirections;
    private ArrayList<String> detailedExhibitDirections;
    private int directionIndex;
    int routeNum;
    TextView directionDisplay;
    Button nextDirection;
    SwitchCompat detailedBtn;
    boolean detailedDirections = false;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailedDirections = false;
        setContentView(R.layout.activity_exhibit_directions);
        SharedPreferences routeInfo = getSharedPreferences("routeInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = routeInfo.edit();

        routeNum = routeInfo.getInt("routeNum", 0);
        directionIndex = routeNum;
        Log.d("directionIndex", ""+directionIndex);
        displayDirection();
        setNextDirectionButton();


        var provider = LocationManager.GPS_PROVIDER;
        var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        var locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d("zooseeker", String.format("Location changed: %s", location));
                UserLocation.currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
                // if no longer on route, need to recalculate
                if (!VisitingRoute.followingCurrentDirection(directionIndex)) {

                    List<PlanListItem> nextFastestDirection = VisitingRoute.getNextFastestDirection(directionIndex);

                    // check if not too far off track, not enough to replan, just automatically update current direction
                    if (nextFastestDirection.get(nextFastestDirection.size()-1).target_id
                            .equals(VisitingRoute.getExhibitToVisitAtIndex(directionIndex))) {
                        VisitingRoute.route.set(directionIndex,nextFastestDirection);
                        exhibitDirections.set(directionIndex,PlanListItem.toMessage(VisitingRoute.route.get(directionIndex)));
                        detailedExhibitDirections.set(directionIndex,PlanListItem.toDetailedMessage(VisitingRoute.route.get(directionIndex)));
                        if(detailedDirections)
                            directionDisplay.setText(String.format("%s",detailedExhibitDirections.get(directionIndex)));
                        else
                            directionDisplay.setText(String.format("%s",exhibitDirections.get(directionIndex)));
                    }
                    // check if off track lots to replan
                    else {
                        //TODO: NEED TO GENERATE POPUP ASKING TO REPLAN, IF YES, THEN DO THE BELOW

//                        //replace exhibit directions from current index to end
//                        String startingExhibit = VisitingRoute.closestExhibit();
//                        Vector<List<IdentifiedWeightedEdge>> Directions = VisitingRoute.get_fastest_path_to_end(startingExhibit, VisitingRoute.getExhibitsLeft(directionIndex));
//                        List<List<PlanListItem>> route = VisitingRoute.get_planned_directions(startingExhibit,Directions);
//
//
//                        for (int i = directionIndex; i < VisitingRoute.route.size(); ++i) {
//                            // saved route
//                            VisitingRoute.route.set(i, route.get(i-directionIndex));
//                            VisitingRoute.saveExhibitsVisitingOrder();
//                            //generating correct exhibitDirections
//                            exhibitDirections.set(i,PlanListItem.toMessage(VisitingRoute.route.get(i)));
//                            detailedExhibitDirections.set(i,PlanListItem.toDetailedMessage(VisitingRoute.route.get(i)));
//                        }
//
//                        if(detailedDirections)
//                            directionDisplay.setText(String.format("%s",detailedExhibitDirections.get(directionIndex)));
//                        else
//                            directionDisplay.setText(String.format("%s",exhibitDirections.get(directionIndex)));
                    }
                }
            }
        };

        locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
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
            directionDisplay.setText(String.format("%s",detailedExhibitDirections.get(directionIndex)));
            detailedDirections = true;
        }
        else {
            directionDisplay.setText(String.format("%s",exhibitDirections.get(directionIndex)));
            detailedDirections = false;
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
        if(detailedDirections)
            directionDisplay.setText(String.format("%s",detailedExhibitDirections.get(directionIndex)));
        else
            directionDisplay.setText(String.format("%s",exhibitDirections.get(directionIndex)));
    }
}