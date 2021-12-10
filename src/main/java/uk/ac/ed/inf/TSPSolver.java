package uk.ac.ed.inf;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Solutions;

public class TSPSolver {

    /**
     * Finds the most optimal path to deliver the orders in.
     * The algorithm prioritises higher values orders based on the following:
     *   - The library uses 1 to 10 for highest to lowest priority.
     *   - We map orders to a 0 to 1 scale based on their total delivery cost (1 being the most costly order)
     *   - We then map the 0 to 10 and 1 to 1 using the linear equation y = -9x + 10 where x is the order value on
     *     the 0 to 1 scale.
     * @param start the start position of the drone
     * @param orders the list of orders the drone has to try to deliver
     * @return The list of orders in order of most optimal path (taking into account order value)
     */
    public static List<Order> solveForOrders(LongLat start, List<Order> orders) {

        // Create a vehicle
        VehicleType vehicleType = VehicleTypeImpl.Builder.newInstance("drone").build();
        VehicleImpl vehicle = VehicleImpl.Builder.newInstance("drone")
                .setStartLocation(loc(start))
                .setReturnToDepot(true)
                .setType(vehicleType)
                .build();

        // Create a vehicle routing problem and add the vehicle
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance().addVehicle(vehicle);

        // Get max delivery cost to map the orders to a 0 to 1 scale.
        int maxDeliveryCost = 0;
        for (Order o : orders) {
            int deliveryCost = o.getDeliveryCost();
            if (maxDeliveryCost < deliveryCost) {
                maxDeliveryCost = deliveryCost;
            }
        }

        for (int i = 0; i < orders.size(); i++) {
            var order = orders.get(i);

            // Map the order value to the 0 to 1 (highest priority) scale
            double orderValue = (double)order.getDeliveryCost() / (double) maxDeliveryCost;
            // Finally, map the value to a 1 (highest priority) to 10 scale which the jsprit library uses
            int priority = (int) (-9 * orderValue + 10);

            var firstShopLoc = loc(order.getShops().get(0).locationInLongLat);

            // Create a shipment from the first shop to where it needs to be delivered
            // Note that the second shop IS NOT taken into account when finding the optimal path for the orders
            vrpBuilder.addJob(Shipment.Builder.newInstance(Integer.toString(i))
                    .setPickupLocation(firstShopLoc)
                    .setDeliveryLocation(loc(order.getDeliverToInLongLat()))
                    .setPriority(priority)
                    .build());
        }
        VehicleRoutingProblem problem = vrpBuilder.build();


        // Get the best route
        var algorithm = Jsprit.createAlgorithm(problem);
        algorithm.setMaxIterations(512);
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
        List<TourActivity> bestRoute = Solutions.bestOf(solutions).getRoutes().iterator().next().getActivities();

        // The best route consists of a set of Activity objects (2 per order - 1 to pick up the order, 1 to drop off the
        // order). Thus, we map through the activities and get the order associated with the activity by dividing the
        // index of the activity by 2 (we also subtract 1 as the activities index starts at 1).
        return bestRoute.stream().map(a -> orders.get((a.getIndex() - 1)/2)).collect(Collectors.toList());
    }

    /**
     * Returns the equivalent jsprit.core.problem.Location object of a LongLat object.
     * @param pos The LongLat object to convert.
     * @return The jsprit.core.problem.Location object of pos.
     */
    private static Location loc(LongLat pos) {
        return Location.newInstance(pos.longitude, pos.latitude);
    }
}
