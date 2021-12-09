package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Drone {

    private ArrayList<Move> moves;
    private List<Order> orders;
    private LongLat currPos;
    private List<Order> ordersDelivered = new ArrayList<>();

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
//        this.orders = TSPSolver.solveForOrders(start, orders);
        this.orders = orders;
        this.currPos = start.copy();

        // visit every sensor in order
        this.orders.forEach(this::completeOrder);

        // return to starting position
        returnTo(start);
    }

    private void completeOrder(Order order) {
        if (order.isComplete) {
            return;
        }

        ArrayList currMoves = new ArrayList(moves);
        var currDronePos = currPos.copy();

        ArrayList<LongLat> allStops = new ArrayList<>();
        for (Shop shop : order.shops) {
            allStops.add(shop.locationInLongLat);
        }
        allStops.add(order.deliverToInLongLat);

        for (LongLat stop : allStops) {
            List<Move> pathToStop = getPathTo(stop, currDronePos, currMoves, order);
            if (pathToStop == null) {
                System.out.println("Unable to complete order");
                return;
            } else {
                // update drone position
                LongLat positionAfterSteps = pathToStop.get(pathToStop.size() - 1).getDest();
                currDronePos = positionAfterSteps.copy();

                currMoves.addAll(pathToStop);
            }
        }

        // Mark order as completed
        order.markCompleted();
        System.out.println("order completed" + order.orderNo);
        ordersDelivered.add(order);
        this.currPos = currDronePos;
        this.moves = currMoves;
    }

    /**
     * Attempts to return the given position using the remaining moves
     *
     * @param pos position for drone to return to
     */
    private void returnTo(LongLat pos) {
        List<Move> path = getPathTo(pos, this.currPos, this.moves, null);
        if (path != null) {
            this.moves.addAll(path);
        }
        return;
    }


    /**
     * Returns List of Steps to the given position wrapped in Optional object.
     * If no such series of step exists, return an empty Optional object.
     *
     * @param dest position to return the drone to
     * @return Optional object which may contain valid series of steps
     */
    private List<Move> getPathTo(LongLat dest, LongLat currDronePos, ArrayList<Move> currMoves, Order order) {
        ArrayList<Move> pathMoves = new ArrayList<>();
        LongLat dronePos = currDronePos.copy();
        int movesLeft = getRemainingMoves(currMoves);

        // Case 1 - No moves left: Return empty.
        if (movesLeft < 1) {
            System.out.println("< 1 move left");
            return null;
        }

        // Case 2 - Destination already in range:
        // Hover to either collect or drop off delivery
        // TODO: Should we check if we're able to make it back to AT here?
        if (dronePos.closeTo(dest) && movesLeft >= 2) {
            var move = new Move(dronePos, SPECIAL_HOVERING_ANGLE, order);
            pathMoves.add(move);
//            predictedSteps.add(new Step(step.getEndPos(), pos));
            return pathMoves;
//            for (int angle = -180; angle < 180; angle += 10) {
//                var move = new Move(dronePos, angle, order);
//                if (!sensorMap.collisionWith(step)) {
//                    predictedSteps.add(step);
//                    predictedSteps.add(new Step(step.getEndPos(), pos));
//                    return Optional.of(predictedSteps);
//                }
//            }
        }

        // Case 3 - General Case:
        // Calculate non-colliding series of steps to the destination.
        while (movesLeft > 0 && !dronePos.closeTo(dest)) {
            Move nextMove = new Move(dronePos, dest, order);

            if (nextMove.isValid()) {
                dronePos = nextMove.getDest();
                pathMoves.add(nextMove);
                movesLeft--;
            } else {
                // the step collides with either confinement area border or with no-fly zone
                // perform AStar search instead to calculate path to the destination
                AStar astar = new AStar(dronePos, dest, order);


                // If Path is found by AStar search, append all steps and return
                // If no such path is found, return empty Optional object.
                List<Move> aStarPath = astar.findPath();

                if (aStarPath != null) {
                    pathMoves.addAll(aStarPath);
                    var move = new Move(dronePos, SPECIAL_HOVERING_ANGLE, order);
                    pathMoves.add(move);
                    System.out.println("Found A* path");
                    return pathMoves;
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
