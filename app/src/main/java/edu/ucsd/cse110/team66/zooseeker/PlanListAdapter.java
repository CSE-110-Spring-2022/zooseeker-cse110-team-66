package edu.ucsd.cse110.team66.zooseeker;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class PlanListAdapter extends RecyclerView.Adapter<PlanListAdapter.ViewHolder> {
    private List<List<PlanListItem>> planItems = Collections.emptyList();

    public void setPlanListItems(List<List<PlanListItem>> newPlanItems) {
        this.planItems.clear();;
        this.planItems = newPlanItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.plan_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setPlanItem(planItems.get(position));
    }

    @Override
    public int getItemCount() {
        return planItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private List<PlanListItem> planItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.plan_item_text);
        }

        public List<PlanListItem> getPlanItem() {return planItem; }

        public void setPlanItem(List<PlanListItem> planListItem) {
            this.planItem = planListItem;
            this.textView.setText(PlanListItem.toMessage(planItem));
        }
    }

}
