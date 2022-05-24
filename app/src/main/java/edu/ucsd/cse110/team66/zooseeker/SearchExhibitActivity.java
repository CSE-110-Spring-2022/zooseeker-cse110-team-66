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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchExhibitActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    public ExhibitItemAdapter exhibitItemAdapter;

    private ExhibitItemViewModel viewModel;
    private Button planButton;
    private Button clearButton;
    private Button showSelectedExhibitsButton;
    private TextView countView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_exhibit);
        setPlanButton();
        setClearButton();
        setSelectedExhibitsButton();
        setExhibitItemAdapter();
        setRecyclerView();
    }

    /** Create a menu at the top for the search and voice search icons **/
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

    /** Display selected exhibits in a compact list format **/
    public void openSelectedExhibitsListActivity() {
        List<String> exhibitsAddedName = exhibitItemAdapter.getSelectedExhibits().stream()
                .map(exhibit -> exhibit.name)
                .collect(Collectors.toList());

        Gson gson = new Gson();
        String json = gson.toJson(exhibitsAddedName);
        Intent intent = new Intent(this, SelectedExhibitsListActivity.class);
        intent.putExtra("exhibitsAddedName", json);
        startActivity(intent);
    }

    /** Store added exhibits for use by planning route fragment **/
    private void openExhibitRouteActivity() {
        List<String> exhibitsAdded = exhibitItemAdapter.getSelectedExhibits().stream()
                .map(exhibit -> exhibit.id)
                .collect(Collectors.toList());

        Gson gson = new Gson();
        String json = gson.toJson(exhibitsAdded);
        Intent intent = new Intent(this, ExhibitRouteActivity.class);
        intent.putExtra("exhibitsAll", json);
        startActivity(intent);
    }

    /** Set up the plan button and chosen exhibits counter **/
    private void setPlanButton() {
        planButton = findViewById(R.id.plan_btn);
        planButton.setOnClickListener(view -> openExhibitRouteActivity());
        countView = findViewById(R.id.exhibit_count);
    }

    /** Set up clear button **/
    private void setClearButton() {
        clearButton = findViewById(R.id.clear_btn);
        clearButton.setOnClickListener(view -> viewModel.toggleClear());
    }

    /** Set up selected exhibits button **/
    private void setSelectedExhibitsButton() {
        showSelectedExhibitsButton = findViewById(R.id.selected_exhibits_btn);
        showSelectedExhibitsButton.setOnClickListener(view -> openSelectedExhibitsListActivity());
    }

    /** Display the list of exhibits a user can choose **/
    private void setExhibitItemAdapter() {
        viewModel = new ViewModelProvider(this).get(ExhibitItemViewModel.class);

        exhibitItemAdapter = new ExhibitItemAdapter();
        exhibitItemAdapter.setHasStableIds(true);
        exhibitItemAdapter.setOnAddExhibitHandler(viewModel::toggleAdded);
        viewModel.getExhibitItems().observe(this, exhibitItemAdapter::setExhibitItems);

        exhibitItemAdapter.getCountView(countView);
        exhibitItemAdapter.getClearBtn(clearButton);
        exhibitItemAdapter.getPlanBtn(planButton);
        exhibitItemAdapter.getListBtn(showSelectedExhibitsButton);
    }

    /** Set up the recycler view to use the adapter **/
    private void setRecyclerView() {
        recyclerView = findViewById(R.id.exhibit_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(exhibitItemAdapter);
        recyclerView.setAlpha(0);
    }
}