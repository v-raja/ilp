package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentMap {


    private final List<Order> orders;

    public EnvironmentMap(LongLat start, List<Order> orders) {
        this.orders = TSPSolver.solveForOrders(start, orders);
    }

    public List<Order> getOrders() {
        return orders;
    }

}
