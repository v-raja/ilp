package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Solutions;

public class TSPSolver {

    public static List<Order> solveForOrders(LongLat start, List<Order> orders) {


        var vehicleType = VehicleTypeImpl.Builder.newInstance("drone").addCapacityDimension(0, 1);

        // build a problem
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();

        for (int i = 0; i < orders.size(); i++) {
            var order = orders.get(i);
//            vehicleType = vehicleType.addCapacityDimension(i, 4);
            var shopLocation = loc(order.shops.get(0).locationInLongLat);
            vrpBuilder.addJob(Shipment.Builder.newInstance(order.orderNo).addSizeDimension(0, 1)
                    .setPickupLocation(shopLocation).setDeliveryLocation(loc(order.deliverToInLongLat)).build());
        }

        VehicleImpl vehicle = VehicleImpl.Builder.newInstance("drone")
                .setStartLocation(loc(start))
                .setType(vehicleType.build())
                .build();

        VehicleRoutingProblem problem = vrpBuilder.addVehicle(vehicle).build();

        // Solve and get the best route
        var algorithm = Jsprit.createAlgorithm(problem);
        algorithm.setMaxIterations(512);
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
        List<TourActivity> bestRoute = Solutions.bestOf(solutions).getRoutes().iterator().next().getActivities();

        var deliveries = bestRoute.stream().collect(Collectors.toList());
        List<Order> optimalOrders = new ArrayList<>();
        for (int i = 0; i < deliveries.size(); i = i + 2) {
            optimalOrders.add(orders.get(i / 2));
        }
//        System.out.println(orders.size());
//        System.out.println(bestRoute.stream().collect(Collectors.toList()).size());
//        bestRoute.stream().forEach(a -> System.out.println(a.getIndex()));
//        return bestRoute.stream().map(a -> orders.get( (a.getIndex() - 1) / 2 )).collect(Collectors.toList());
        return optimalOrders;

//        VehicleType vehicleType = VehicleTypeImpl.Builder.newInstance("drone").build();
//        VehicleImpl vehicle = VehicleImpl.Builder.newInstance("drone")
//                .setStartLocation(loc(start))
//                .setType(vehicleType)
//                .build();
//
//        // build a problem
//        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
//        vrpBuilder.addVehicle(vehicle);
//        for (int i = 0; i < orders.size(); i++) {
//            var order = orders.get(i);
//            var shopLocation = loc(order.shops.get(0).locationInLongLat);
//            vrpBuilder.addJob(Service.Builder.newInstance(Integer.toString(i))
//                    .setLocation(shopLocation).build());
//            vrpBuilder.addJob(Service.Builder.newInstance(Integer.toString(i) + 200)
//                    .setLocation(loc(order.deliverToInLongLat)).build());
//        }
//        VehicleRoutingProblem problem = vrpBuilder.build();
//
//        // Solve and get the best route
//        var algorithm = Jsprit.createAlgorithm(problem);
//        algorithm.setMaxIterations(512);
//        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
//        var bestRoute = Solutions.bestOf(solutions).getRoutes().iterator().next().getActivities();
//
//        return bestRoute.stream().map(a -> orders.get(a.getIndex() - 1)).collect(Collectors.toList());
    }

    private static Location loc(LongLat pos) {
        return Location.newInstance(pos.longitude, pos.latitude);
    }
}
