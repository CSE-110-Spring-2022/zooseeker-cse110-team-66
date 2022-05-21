package edu.ucsd.cse110.team66.zooseeker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ExhibitItemAdapter extends RecyclerView.Adapter<ExhibitItemAdapter.ViewHolder> implements Filterable {
    private List<ExhibitItem> exhibits = Collections.emptyList();
    private List<ExhibitItem> exhibitsAll = Collections.emptyList();
    private Consumer<ExhibitItem> onAddExhibit;
    public TextView countView;

    public void setExhibitItems(List<ExhibitItem> exhibits) {
        this.exhibits.clear();
        this.exhibits = exhibits;
        exhibitsAll = new ArrayList<>(exhibits);

        if (getAddedItemCount()!=0) SearchExhibitActivity.enablePlanButton();
        updateAddedCount();

        notifyDataSetChanged();
    }

    public void setOnAddExhibitHandler(Consumer<ExhibitItem> onAddExhibit) {
        this.onAddExhibit=onAddExhibit;
    }

    public List<ExhibitItem> getExhibitsAll() {
        return exhibitsAll;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.exhibit_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExhibitItemAdapter.ViewHolder holder, int position) {
        holder.setExhibitItem(exhibits.get(position));
    }

    @Override
    public int getItemCount() {
        return exhibits.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    /*
    Citation:
    Link: https://www.youtube.com/watch?v=sJ-Z9G0SDhc&t=23s
    Title: How to Filter a RecyclerView with SearchView - Android Studio Tutorial
    Date: Apr 28, 2022
    Usage: Information on creating filters
    */
    // Custom filter to filter the displayed exhibits based on the query
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<ExhibitItem> filteredExhibits = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredExhibits.addAll(exhibitsAll);
            } else {
                for (ExhibitItem exhibit : exhibitsAll) {
                    String loweredQuery = charSequence.toString().toLowerCase();
                    // Check if query is contained within exhibit name
                    if (exhibit.getName().toLowerCase().contains(loweredQuery)) {
                        if (!filteredExhibits.contains(exhibit)) filteredExhibits.add(exhibit);
                    }
                    // Check if query is contained within tags
                    for (String tag : exhibit.getTags()) {
                        if (tag.toLowerCase().startsWith(loweredQuery)) {
                            if (!filteredExhibits.contains(exhibit)) filteredExhibits.add(exhibit);
                        }
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            //Collections.sort(filteredExhibits, ExhibitItem.ExhibitNameComparator);
            filterResults.values = filteredExhibits;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            exhibits.clear();
            exhibits.addAll((Collection<? extends ExhibitItem>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public void setCountView(TextView textView) {
        countView = textView;
    }

    public int getAddedItemCount(){
        return exhibitsAll.stream()
                .filter(exhibit -> exhibit.added)
                .collect(Collectors.toList())
                .size();
    }

    public void updateAddedCount() {
        int count = getAddedItemCount();
        countView.setText(String.format("%d",count));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView exhibitTextView;
        private Button addExhibitBtn;
        private ExhibitItem exhibitItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.exhibitTextView = itemView.findViewById(R.id.exhibit_item_text);
            this.addExhibitBtn = itemView.findViewById(R.id.add_exhibit_btn);

            this.addExhibitBtn.setOnClickListener(view -> {
                updateAddedCount();
                SearchExhibitActivity.enablePlanButton();
                if (onAddExhibit == null) return;
                /* for (int i = 0; i < exhibitsAll.size(); ++i) {
                    if (exhibitsAll.get(i).name == exhibitTextView.getText()) {
                        exhibitsAll.get(i).added = !exhibitsAll.get(i).added;
                        if (exhibitsAll.get(i).added) {
                            addExhibitBtn.setText("ADDED");
                            addExhibitBtn.setEnabled(false);
                        }
                        else {
                            addExhibitBtn.setText("ADD");
                            addExhibitBtn.setEnabled(true);
                        }
                    }
                } */
                addExhibitBtn.setText("ADDED");
                addExhibitBtn.setEnabled(false);
                onAddExhibit.accept(exhibitItem);
                updateAddedCount();
            });
        }

        // Set the exhibit name and whether it has been added
        public void setExhibitItem(ExhibitItem item) {
            this.exhibitTextView.setText(item.getName());
            this.exhibitItem = item;

            if (item.added) {
                this.addExhibitBtn.setText("ADDED");
                this.addExhibitBtn.setEnabled(false);
            }
            else {
                this.addExhibitBtn.setText("ADD");
                this.addExhibitBtn.setEnabled(true);
            }
        }
    }
}
