package edu.ucsd.cse110.team66.zooseeker;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchExhibitActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    public ExhibitItemAdapter exhibitItemAdapter;

    private ExhibitItemViewModel viewModel;
    private Button planButton;
    private Button clearButton;
    private Button showSelectedExhibitsButton;
    private TextView countView;
    //private final PermissionChecker permissionChecker = new PermissionChecker(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_exhibit);

        //if (permissionChecker.ensurePermissions()) return;

        setPlanButton();
        setClearButton();
        setSelectedExhibitsButton();
        setExhibitItemAdapter();
        setExhibitRecyclerView();
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
                recyclerView.setVisibility(View.VISIBLE);
                return true;
            }
            // Hide all exhibits when search icon is clicked
            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                recyclerView.setVisibility(View.INVISIBLE);
                return true;
            }
        });
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (exhibitItemAdapter != null) {
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
        }
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
        List<String> tempExhibitsAdded = exhibitItemAdapter.getSelectedExhibits().stream()
                .map(exhibit -> exhibit.id)
                .collect(Collectors.toList());

        List<ZooData.VertexInfo> exhibits = ZooData.loadZooItemJSON(this, getString(R.string.exhibit_node_info_json),"exhibits");
        Map<String,String> exhibits_to_groups = new HashMap<String, String>();
        Map<String, List<String>> groups_to_added_exhibits = new HashMap<>();
        for (int i = 0; i < exhibits.size(); ++i) {
            if (exhibits.get(i).group_id != null) {
                exhibits_to_groups.put(exhibits.get(i).id, exhibits.get(i).group_id);

                if (tempExhibitsAdded.contains(exhibits.get(i).id)) {
                    // Put selected exhibits with group ids in a map to access later
                    String group_id = exhibits.get(i).group_id;
                    String name = exhibits.get(i).name;
                    List<String> added_exhibits;
                    if (groups_to_added_exhibits.containsKey(group_id)) {
                        added_exhibits = groups_to_added_exhibits.get(group_id);
                    } else {
                        added_exhibits = new LinkedList<>();
                    }
                    added_exhibits.add(name);
                    groups_to_added_exhibits.put(exhibits.get(i).group_id, added_exhibits);
                }
            }
            else {
                exhibits_to_groups.put(exhibits.get(i).id,exhibits.get(i).id);
            }
        }

        VisitingRoute.groups_to_added_exhibits = groups_to_added_exhibits;

        Set<String> existingExhibits = new HashSet<String>();
        List<String> exhibitsAdded = new ArrayList<String>();
        for (String exhibit:tempExhibitsAdded) {
            if (!existingExhibits.contains(exhibits_to_groups.get(exhibit))) {
                existingExhibits.add(exhibits_to_groups.get(exhibit));
                exhibitsAdded.add(exhibits_to_groups.get(exhibit));
            }
        }

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
        SharedPreferences routeInfo = getSharedPreferences("routeInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = routeInfo.edit();
        int routeNum = routeInfo.getInt("routeNum", 0);

        clearButton = findViewById(R.id.clear_btn);
        clearButton.setOnClickListener(view -> {
            viewModel.toggleClear();
            editor.putInt("routeNum", 0);
            editor.apply();
        });
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
    private void setExhibitRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.exhibit_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(exhibitItemAdapter);
        recyclerView.setVisibility(View.INVISIBLE);
    }

}