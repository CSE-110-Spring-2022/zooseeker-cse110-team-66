package edu.ucsd.cse110.team66.zooseeker;

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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class ExhibitItemAdapter extends RecyclerView.Adapter<ExhibitItemAdapter.ViewHolder> implements Filterable {
    private List<ExhibitItem> exhibits;
    private List<ExhibitItem> exhibitsAll;
    private Consumer<ExhibitItem> onAddExhibit;

    public void setExhibitItems(List<ExhibitItem> exhibits) {
        this.exhibits = exhibits;
        exhibitsAll = new ArrayList<>(exhibits);
        notifyDataSetChanged();
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
        holder.setExhibitName(exhibits.get(position));
        holder.set
    }

    @Override
    public int getItemCount() {
        return exhibits.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView exhibitTextView;
        private Button addExhibitBtn;
        private ExhibitItem exhibitItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.exhibitTextView = itemView.findViewById(R.id.exhibit_item_text);
            this.addExhibitBtn = itemView.findViewById(R.id.add_exhibit_btn);

            this.addExhibitBtn.setOnClickListener(view -> {
                if (onAddExhibit == null) return;
                onAddExhibit.accept(exhibitItem);
            });
        }

        public void setExhibitName(ExhibitItem item) {
            this.exhibitTextView.setText(item.getName());
        }

    }
}
