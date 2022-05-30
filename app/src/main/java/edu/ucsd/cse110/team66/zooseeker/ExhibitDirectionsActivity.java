package edu.ucsd.cse110.team66.zooseeker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    Button backDirection;
    Button skipDirection;
    Button nextDirection;
    SwitchCompat detailedBtn;
    boolean detailedDirections;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailedDirections = false;
        if (UserLocation.enable_mock_button) {
            setContentView(R.layout.activity_exhibit_directions_mock);
        }
        else {
            setContentView(R.layout.activity_exhibit_directions);
        }
        SharedPreferences routeInfo = getSharedPreferences("routeInfo", Context.MODE_PRIVATE);
        routeNum = routeInfo.getInt("routeNum", 0);
        directionIndex = routeNum;

        Log.d("directionIndex", ""+directionIndex);

        displayDirection();
        setBackDirectionButton();
        setSkipDirectionButton();
        setNextDirectionButton();

        if (UserLocation.enable_mock_button) {
            EditText text_current_lat = findViewById(R.id.current_location_latitude);
            EditText text_current_lng = findViewById(R.id.current_location_longitude);
            Button mock_location_button = findViewById(R.id.set_mock_location);

            UserLocation.currentLocation = new LatLng(VisitingRoute.coordMap.get(VisitingRoute.entrance_and_exit_gate_id).latitude,
                    VisitingRoute.coordMap.get(VisitingRoute.entrance_and_exit_gate_id).longitude);

            text_current_lat.setText(String.valueOf(UserLocation.currentLocation.latitude));
            text_current_lng.setText(String.valueOf(UserLocation.currentLocation.longitude));

            mock_location_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserLocation.currentLocation = new LatLng(Double.parseDouble(text_current_lat.getText().toString()),
                            Double.parseDouble(text_current_lng.getText().toString()));
                    handleLocationChange();
                }
            });

            text_current_lat.setOnFocusChangeListener((view, hasFocus) -> {
                if (!hasFocus && text_current_lat.getText().toString().equals("")) {
                    text_current_lat.setText(String.valueOf(UserLocation.currentLocation.latitude));
                }
            });

            text_current_lng.setOnFocusChangeListener((view, hasFocus) -> {
                if (!hasFocus && text_current_lng.getText().toString().equals("")) {
                    text_current_lng.setText(String.valueOf(UserLocation.currentLocation.longitude));
                }
            });

        }
        else {
            var provider = LocationManager.GPS_PROVIDER;
            var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            var locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Log.d("zooseeker", String.format("Location changed: %s", location));
                    UserLocation.currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
                    handleLocationChange();
                }
            };

            locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
        }
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

    private void briefOrDetailedDirections() {
        if (detailedDirections) {
            directionDisplay.setText(String.format("%s", detailedExhibitDirections.get(directionIndex)));
        }
        else {
            directionDisplay.setText(String.format("%s", exhibitDirections.get(directionIndex)));
        }
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

    /** Set up the back direction button **/
    private void setBackDirectionButton() {
        backDirection = findViewById(R.id.back_exhibit_direction_btn);
        backDirection.setOnClickListener(view -> backExhibitDirection());
    }

    private void backExhibitDirection() {
        Log.d("index", String.valueOf(directionIndex));
        if (directionIndex < 0) {
            finish();
            return;
        }
        String currentExhibit = VisitingRoute.getExhibitToVisitAtIndex(directionIndex);
        String previousExhibit = VisitingRoute.entrance_and_exit_gate_id;
        if (directionIndex > 0) {
            previousExhibit = VisitingRoute.getExhibitToVisitAtIndex(directionIndex - 1);
        }
        List<PlanListItem> previousDirection
                = VisitingRoute.getPreviousExhibitDirections(currentExhibit, previousExhibit);
        --directionIndex;
        if (detailedDirections)
            directionDisplay.setText(String.format("%s", PlanListItem.toDetailedMessage(previousDirection)));
        else
            directionDisplay.setText(String.format("%s", PlanListItem.toBriefMessage(previousDirection)));
    }

    private void setSkipDirectionButton() {
        skipDirection = findViewById(R.id.skip_exhibit_direction_btn);
        skipDirection.setOnClickListener(view -> skipExhibitDirection());
    }

    private void skipExhibitDirection() {

    }

    // Set up direction button
    private void setNextDirectionButton() {
        nextDirection = findViewById(R.id.next_exhibit_direction_btn);
        nextDirection.setOnClickListener(view -> nextExhibitDirection());
    }

    // Go to the next direction; return to plan route if all directions have been displayed
    private void nextExhibitDirection() {
        SharedPreferences routeInfo = getSharedPreferences("routeInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = routeInfo.edit();
        if (directionIndex >= exhibitDirections.size() - 1) {
            editor.putInt("routeNum", 0);
            editor.apply();
            finish();
            return;
        }
        editor.putInt("routeNum", directionIndex);
        editor.apply();
        ++directionIndex;
        briefOrDetailedDirections();
        handleLocationChange();
    }

    private void handleLocationChange() {
        // if no longer on route, need to recalculate
        if (!VisitingRoute.followingCurrentDirection(directionIndex)) {
            List<PlanListItem> nextFastestDirection = VisitingRoute.getNextFastestDirection(directionIndex);

            // check if not too far off track, not enough to replan, just automatically update current direction
            if (nextFastestDirection.get(nextFastestDirection.size() - 1).target_id
                    .equals(VisitingRoute.getExhibitToVisitAtIndex(directionIndex))) {
                VisitingRoute.route.set(directionIndex, nextFastestDirection);
                exhibitDirections.set(directionIndex, PlanListItem.toBriefMessage(VisitingRoute.route.get(directionIndex)));
                detailedExhibitDirections.set(directionIndex, PlanListItem.toDetailedMessage(VisitingRoute.route.get(directionIndex)));
                briefOrDetailedDirections();
            }
            // check if off track lots to replan
            else {
                showReplanPopup(this);
            }
        }
    }

    private void showReplanPopup(Activity activity) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder
                .setTitle("Replan?")
                .setMessage("You have gone off-route to reach the next exhibit. Do you wish to replan your route?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        replanDirections();
                    }
                })
                .setNegativeButton("No",null)
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public void replanDirections() {
        //replace exhibit directions from current index to end
        String startingExhibit = VisitingRoute.closestExhibit();
        Vector<List<IdentifiedWeightedEdge>> Directions = VisitingRoute.getFastestPathToEnd(startingExhibit, VisitingRoute.getExhibitsLeft(directionIndex));
        List<List<PlanListItem>> route = VisitingRoute.getPlannedDirections(startingExhibit,Directions);

        for (int i = directionIndex; i < VisitingRoute.route.size(); ++i) {
            // saved route
            VisitingRoute.route.set(i, route.get(i-directionIndex));
            VisitingRoute.saveExhibitsVisitingOrder();
            //generating correct exhibitDirections
            exhibitDirections.set(i,PlanListItem.toBriefMessage(VisitingRoute.route.get(i)));
            detailedExhibitDirections.set(i,PlanListItem.toDetailedMessage(VisitingRoute.route.get(i)));
        }
        briefOrDetailedDirections();
    }
}