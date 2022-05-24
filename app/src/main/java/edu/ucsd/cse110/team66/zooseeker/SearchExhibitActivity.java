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
    private Button clearButton;
    private TextView countView;
    private Button listButton;

    //private Runnable onClearExhibits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_exhibit);

        setExhibitItemAdapter();

        recyclerView.setAlpha(0);
    }

    // Create a menu at the top for the search and voice search icons
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater()
                .inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.exhibit_search);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            // Show all exhibits when search icon is clicked
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                recyclerView.setAlpha(1);
                return true;
            }
            // Hide all exhibits when search icon is clicked
            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                recyclerView.setAlpha(0);
                return true;
            }
        });
        SearchView searchView = (SearchView) searchItem.getActionView();
        exhibitItemAdapter.getSearchView(searchView);
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
        //setOnClearExhibitsHandler(viewModel::toggleClear);
        viewModel.getExhibitItems().observe(this, exhibitItemAdapter::setExhibitItems);

        recyclerView = findViewById(R.id.exhibit_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(exhibitItemAdapter);

        countView = findViewById(R.id.exhibit_count);
        clearButton = findViewById(R.id.clear_btn);
        planButton = findViewById(R.id.plan_btn);
        listButton = findViewById(R.id.selected_exhibits_btn);

        exhibitItemAdapter.getCountView(countView);
        exhibitItemAdapter.getClearBtn(clearButton);
        exhibitItemAdapter.getPlanBtn(planButton);
        exhibitItemAdapter.getListBtn(listButton);
        //exhibitItemAdapter.setExhibitItems(ExhibitItem.loadExhibits(this,"sample_node_info.json"));

        planButton.setOnClickListener(v -> openExhibitRouteActivity());

        clearButton.setOnClickListener(view -> {
            //if (onClearExhibits == null) return;
            viewModel.toggleClear();
        });

    }

    /*public void setOnClearExhibitsHandler(Runnable onClearExhibits) {
        this.onClearExhibits=onClearExhibits;
    }*/

    // Store added exhibits for use by planning route fragment
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