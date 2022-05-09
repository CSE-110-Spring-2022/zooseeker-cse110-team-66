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

public class ExhibitItemAdapter extends RecyclerView.Adapter<ExhibitItemAdapter.ViewHolder> implements Filterable {
    private List<ExhibitItem> exhibits;
    private List<ExhibitItem> exhibitsAll;
    private Consumer<ExhibitItem> onAddExhibit;
    public TextView countView;

    public void setExhibitItems(List<ExhibitItem> exhibits) {
        this.exhibits = exhibits;
        exhibitsAll = new ArrayList<>(exhibits);
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
            Collections.sort(filteredExhibits, ExhibitItem.ExhibitNameComparator);
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

    private void updateAddedCount(TextView textview) {
        String strCount = textview.getText().toString();
        int numCount = Integer.parseInt(strCount);
        numCount++;
        textview.setText(String.format("%d",numCount));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView exhibitTextView;
        private Button addExhibitBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.exhibitTextView = itemView.findViewById(R.id.exhibit_item_text);
            this.addExhibitBtn = itemView.findViewById(R.id.add_exhibit_btn);

            this.addExhibitBtn.setOnClickListener(view -> {
                updateAddedCount(countView);
                SearchExhibitActivity.enablePlanButton();
                if (onAddExhibit == null) return;
                for (int i = 0; i < exhibitsAll.size(); ++i) {
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
                }
            });
        }

        // Set the exhibit name and whether it has been added
        public void setExhibitItem(ExhibitItem item) {
            this.exhibitTextView.setText(item.getName());
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
