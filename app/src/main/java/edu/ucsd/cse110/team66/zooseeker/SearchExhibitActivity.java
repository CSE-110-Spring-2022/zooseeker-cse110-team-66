package edu.ucsd.cse110.team66.zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;

public class SearchExhibitActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    public ExhibitItemAdapter exhibitItemAdapter;

    private ExhibitItemViewModel viewModel;
    private Button planButton;
    private String goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_exhibit);

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("exhibitsToVisit",0);
        editor.apply();

        setExhibitItemAdapter();
        recyclerView.setAlpha(0);

        planButton = findViewById(R.id.plan_btn);
        //planButton.setEnabled(false);
        planButton.setOnClickListener(v -> openExhibitRouteActivity());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater()
                .inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.exhibit_search);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                recyclerView.setAlpha(1);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                recyclerView.setAlpha(0);
                return true;
            }
        });
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                exhibitItemAdapter.getFilter().filter(query);
                return false;
            }

            // Filter exhibits based on search query
            @Override
            public boolean onQueryTextChange(String newText) {
                exhibitItemAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    // Display the list of exhibits a user can choose
    private void setExhibitItemAdapter() {
        viewModel = new ViewModelProvider(this).get(ExhibitItemViewModel.class);

        exhibitItemAdapter = new ExhibitItemAdapter();
        exhibitItemAdapter.setHasStableIds(true);
        exhibitItemAdapter.setOnAddExhibitHandler(viewModel::toggleAdded);
        //Log.d("SearchExihibitActivityTEXT", viewModel.getExhibitItems().getValue().toString());
        //viewModel.getExhibitItems().observe(this, exhibitItemAdapter::setExhibitItems);

        recyclerView = findViewById(R.id.exhibit_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(exhibitItemAdapter);
        TextView countView = findViewById(R.id.exhibit_count);
        exhibitItemAdapter.setCountView(countView);

        exhibitItemAdapter.setExhibitItems(ExhibitItem.loadExhibits(this,"sample_node_info.json"));
    }

    private void openExhibitRouteActivity() {
        List<ExhibitItem> exhibitsAll = exhibitItemAdapter.getExhibitsAll();
        ArrayList<String> exhibitsAdded = new ArrayList<String>();
        for (ExhibitItem exhibit: exhibitsAll) {
            if (exhibit.added) {
                exhibitsAdded.add(exhibit.id);
            }
        }
        Gson gson = new Gson();
        String json = gson.toJson(exhibitsAdded);
        Intent intent = new Intent(this, ExhibitRouteActivity.class);
        intent.putExtra("exhibitsAll", json);
        startActivity(intent);
    }

}