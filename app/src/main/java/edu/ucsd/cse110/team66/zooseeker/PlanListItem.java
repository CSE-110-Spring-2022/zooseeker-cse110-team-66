package edu.ucsd.cse110.team66.zooseeker;

public class PlanListItem {
    public String street_name;
    public String street_id;
    public String source_id;
    public String target_id;
    public String source_name;
    public String target_name;
    public double weight;


    PlanListItem(String direction) {
        this.street = direction;
    }

    public String toMessage() {
        return "From " + source_name + ", walk down " + street_name  +
                " for " + String.valueOf(weight) + " feet towards " +
                target_name
    }

    public static List<PlanListItem>
}
