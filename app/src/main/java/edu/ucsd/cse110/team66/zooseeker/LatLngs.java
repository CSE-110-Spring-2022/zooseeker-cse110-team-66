package edu.ucsd.cse110.team66.zooseeker;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class LatLngs {

    @NonNull
    public static LatLng midpoint(LatLng l1, LatLng l2) {
        return new LatLng(
                (l1.latitude + l2.latitude) / 2,
                (l1.longitude + l2.longitude) / 2
        );
    }

    @NonNull
    public static double distance(LatLng l1, LatLng l2) {
        return Math.sqrt(Math.pow(l1.latitude-l2.latitude, 2) + Math.pow(l1.longitude-l2.longitude,2));
    }

    // returns the index of the closest place to location
    @NonNull
    public static int closest(List<LatLng> places, LatLng location) {
        double current_closest = Integer.MAX_VALUE;
        int closest_index = -1;

        for (int i = 0; i < places.size(); ++i) {
            double current = LatLngs.distance(places.get(i),location);
            if (current < current_closest) {
                current_closest = current;
                closest_index = i;
            }
        }

        return closest_index;
    }
}
