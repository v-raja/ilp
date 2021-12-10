package uk.ac.ed.inf;

import uk.ac.ed.inf.AStar.Search;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Drone {

    private ArrayList<Move> moves;
    private List<Order> orders;
    private LongLat currPos;
    private List<Order> ordersDelivered = new ArrayList<>();
    private LongLat start;

    /**
     * A special junk value that indicates the drone is hovering.
     */
    public static final int SPECIAL_HOVERING_ANGLE = -999;

    /**
     * The distance (in degrees) of one move of the drone.
     */
    public static final double MOVE_LENGTH_IN_DEGREES = 0.00015;
//            0.00015;

    /**
     * The maximum moves a drone can make in a day.
     */
    public static final int MAX_MOVES = 1500;

    Drone(LongLat start, ArrayList<Order> orders) {
        this.moves = new ArrayList<>();
        this.orders = TSPSolver.solveForOrders(start, orders);
//        this.orders = orders;
        this.currPos = start.copy();
        this.start = start;

        // visit every sensor in order
        this.orders.forEach(this::completeOrder);

        // return to starting position
        returnToStart();
    }

    private void completeOrder(Order order) {
        if (order.getIsComplete()) {
            return;
        }

        // make local copies of state
        ArrayList currMoves = new ArrayList(moves);
        var currDronePos = currPos.copy();

        // add all stops for this order to a list
        ArrayList<LongLat> allStops = new ArrayList<>();
        for (Shop shop : order.getShops()) {
            allStops.add(shop.locationInLongLat);
        }
        allStops.add(order.getDeliverToInLongLat());

        // loop through all stops and visit them
        // if we're unable to make one stop on the visit, we don't bother with the order
        for (LongLat stop : allStops) {
            // if we're unable to make it back to the base after this stop, we won't deliver this order
            var pathToBase = getPathTo(this.start, stop, currMoves, null);
            if (pathToBase == null) {
                System.out.println("Unable to complete order due to not enough moves back to base.");
                return;
            }


            List<Move> pathToStop = getPathTo(stop, currDronePos, currMoves, order);
            if (pathToStop == null) {
                System.out.println("Unable to complete order");
                return;
            } else {
                // update drone position
                if (pathToStop.size() > 0) {
                    LongLat positionAfterSteps = pathToStop.get(pathToStop.size() - 1).getDest();
                    currDronePos = positionAfterSteps.copy();
                }

                // hover to pick or drop off a sandwich
                var hoveringMove = new Move(currDronePos, SPECIAL_HOVERING_ANGLE, order);
                pathToStop.add(hoveringMove);
                currMoves.addAll(pathToStop);
            }
        }

        // Mark order as completed
        order.markCompleted();
        System.out.println("\nORDER COMPLETED " + order.getOrderNumber());
        ordersDelivered.add(order);
        this.currPos = currDronePos;
        this.moves = currMoves;
    }

    /**
     * Tries to find a path to the where the drone started from.
     */
    private void returnToStart() {
        int movesRemaining = getRemainingMoves(this.moves);
        List<Move> path = getPathTo(this.start, this.currPos, this.moves, null);
        if (path != null) {
            this.moves.addAll(path);
        } else {
            System.out.println("Couldn't return to start as path couldn't be found.");
        }
    }


    /**
     * Returns List of Steps to the given position wrapped in Optional object.
     * If no such series of step exists, return an empty Optional object.
     *
     * @param dest position to return the drone to
     * @return Optional object which may contain valid series of steps
     */
    private List<Move> getPathTo(LongLat dest, LongLat dronePos, ArrayList<Move> currMoves, Order order) {
        ArrayList<Move> pathMoves = new ArrayList<>();
        int movesLeft = getRemainingMoves(currMoves);

        // Case 2 - Destination already in range:
        // Hover to either collect or drop off delivery
        // TODO: Should we check if we're able to make it back to AT here?
//        if (dronePos.closeTo(dest) && movesLeft >= 2) {
//            var move = new Move(dronePos, SPECIAL_HOVERING_ANGLE, order);
//            pathMoves.add(move);
////            predictedSteps.add(new Step(step.getEndPos(), pos));
//            return pathMoves;
////            for (int angle = -180; angle < 180; angle += 10) {
////                var move = new Move(dronePos, angle, order);
////                if (!sensorMap.collisionWith(step)) {
////                    predictedSteps.add(step);
////                    predictedSteps.add(new Step(step.getEndPos(), pos));
////                    return Optional.of(predictedSteps);
////                }
////            }
//        }

        // Case 3 - General Case:
        // Calculate non-colliding series of steps to the destination.
        while (movesLeft > 0 && !dronePos.closeTo(dest)) {
            Move nextMove = new Move(dronePos, dest, order);

//            var five_steps_in_dir = new Move(dronePos, nextMove.getAngle(), 2, order);
            if (nextMove.isValid()) {
//                    && !five_steps_in_dir.intersectsWithNoFlyZone()) {
                dronePos = nextMove.getDest();
                pathMoves.add(nextMove);
                movesLeft--;
            } else {
                // the step collides with either confinement area border or with no-fly zone
                // perform AStar search instead to calculate path to the destination
                Search astar = new Search(dronePos, dest, order);


                // If Path is found by AStar search, append all steps and return
                // If no such path is found, return empty Optional object.
                List<Move> aStarPath = astar.findPath(movesLeft);

                if (aStarPath != null) {
                    dronePos = aStarPath.get(aStarPath.size() - 1).getDest();
                    pathMoves.addAll(aStarPath);
                    movesLeft -= aStarPath.size();

//                    currDronePos = positionAfterSteps.copy();

//                    var move = new Move(aStarPath.get(aStarPath.size() -1).getDest(), SPECIAL_HOVERING_ANGLE, order);
//                    pathMoves.add(move);
                    System.out.println("Found A* path");
//                    return pathMoves;
                } else {
                    System.out.println("Couldn't find A* path");
                    return null;
                }
//                return astar.findPath().map(stepsAvoidingCollision -> {
//                    pathMoves.addAll(stepsAvoidingCollision);
//                    return pathMoves;
//                });
            }
        }

        if (dronePos.closeTo(dest) && movesLeft > 0) {
            // reached destination within available number of moves; return steps.
//            var move = new Move(dronePos, SPECIAL_HOVERING_ANGLE, order);
//            pathMoves.add(move);
            return pathMoves;
        } else {
            // otherwise return empty.
            System.out.println("Didn't reach destination with enough moves");
            return null;
        }
    }

    private int getRemainingMoves(ArrayList<Move> moves) {
        return MAX_MOVES - moves.size();
    }

    public ArrayList<Move> getMoves() {
        return moves;
    }

    public List<Order> getOrdersDelivered() {
        return ordersDelivered;
    }
}
