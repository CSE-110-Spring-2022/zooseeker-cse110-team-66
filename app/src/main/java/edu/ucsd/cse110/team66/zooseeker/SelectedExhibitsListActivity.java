package edu.ucsd.cse110.team66.zooseeker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SelectedExhibitsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("Selected exhibits");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_exhibits_list);
        setUpBackButton();

        // Get selected animal exhibits
        Gson gson = new Gson();
        String exhibitsAdded = getIntent().getExtras().getString("exhibitsAddedName");
        List<String> exhibitsAddedName = gson.fromJson(exhibitsAdded, List.class);

        // Format list of selected exhibits to display
        TextView selectedExhibitsList = findViewById(R.id.selected_exhibits_text);
        String s = exhibitsAddedName.stream()
                .collect(Collectors.joining("\n"));
        selectedExhibitsList.setText(s);
    }

    /** Set up back button at the top left **/
    private void setUpBackButton() {
        ActionBar actionBar = getSupportActionBar();
        // Customize the back button
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /** Go back to Search_Exhibit when the back button is clicked **/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}