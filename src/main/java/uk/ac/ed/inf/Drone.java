package uk.ac.ed.inf;

import uk.ac.ed.inf.AStar.Search;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that encapsulates the logic of a drone for its daily deliveries.
 * Initialising the class finds the most optimal route and completes the delivers for the orders.
 * @author Vivek Raja s1864074
 */
public class Drone {

    /**
     * The moves the drone has made to deliver the orders.
     */
    private ArrayList<Move> moves;

    /**
     * The list of orders the drone was instructed to deliver for the day. May include orders
     * that weren't delivered.
     */
    private List<Order> orders;

    /**
     * The current position of the drone.
     */
    private LongLat currPos;

    /**
     * A list of all the orders delivered today by the drone.
     */
    private List<Order> ordersDelivered = new ArrayList<>();

    /**
     * The location at which the drone starts from.
     */
    private LongLat start;

    /**
     * A special junk value that indicates the drone is hovering.
     */
    public static final int SPECIAL_HOVERING_ANGLE = -999;

    /**
     * The distance (in degrees) of one move of the drone.
     */
    public static final double MOVE_LENGTH_IN_DEGREES = 0.00015;

    /**
     * The maximum moves a drone can make in a day.
     */
    public static final int MAX_MOVES = 1500;

    /**
     * Instantiates a Drone object, finds the most optimal path, and completes
     * the deliveries.
     * @param start The start position of the drone.
     * @param orders The orders the drone has to deliver for the day.
     * @author Vivek Raja s1864074
     */
    Drone(LongLat start, ArrayList<Order> orders) {
        this.moves = new ArrayList<>();
        this.orders = TSPSolver.solveForOrders(start, orders);
        this.currPos = start;
        this.start = start;

        // deliver orders in order
        this.orders.forEach(this::completeOrder);

        // return to where the drone started from
        returnToStart();
    }

    /**
     * Tries to find a path to the shops and the customer to complete the order.
     * An order will only be successfully delivered if the drone can make all stops and have enough moves
     * to go back to the base. The class variables `moves` and `currPos` are updated by this function.
     *
     * @param order the order to deliver.
     */
    private void completeOrder(Order order) {
        if (order.getIsComplete()) {
            return;
        }

        // make local copies of drone state
        ArrayList currMoves = new ArrayList(moves);
        var currDronePos = currPos.copy();

        // add all stops to a list
        ArrayList<LongLat> allStops = new ArrayList<>();
        for (Shop shop : order.getShops()) {
            allStops.add(shop.locationInLongLat);
        }
        allStops.add(order.getDeliverToInLongLat());

        // loop through all stops and visit them
        // if we're unable to make one stop on the visit, we don't bother with the order
        for (LongLat stop : allStops) {
            int movesRemaining = getRemainingMoves(currMoves);

            // if we're unable to make it back to the base after this stop, we won't deliver this order
            var pathToBase = getPathTo(this.start, stop, movesRemaining, null);
            if (pathToBase == null) {
                System.out.println("Unable to complete order due to not enough moves back to base.");
                return;
            }

            // get path to stop
            List<Move> pathToStop = getPathTo(stop, currDronePos, movesRemaining, order);
            if (pathToStop == null) {
                System.out.println("Unable to complete order");
                return;
            } else {
                // update drone position
                if (pathToStop.size() > 0) {
                    // we could have already been close to the destination
                    LongLat positionAfterSteps = pathToStop.get(pathToStop.size() - 1).getDest();
                    currDronePos = positionAfterSteps.copy();
                }

                // hover to pick or drop off a sandwich
                var hoveringMove = new Move(currDronePos, SPECIAL_HOVERING_ANGLE, order);
                pathToStop.add(hoveringMove);
                currMoves.addAll(pathToStop);
            }
        }

        // mark order as completed
        order.markCompleted();
        System.out.println("ORDER COMPLETED " + order.getOrderNumber());
        ordersDelivered.add(order);

        // Update global drone position
        this.currPos = currDronePos;
        this.moves = currMoves;
    }

    /**
     * Tries to find a path to the where the drone started from.
     */
    private void returnToStart() {
        int movesRemaining = getRemainingMoves(this.moves);
        List<Move> path = getPathTo(this.start, this.currPos, movesRemaining, null);
        if (path != null) {
            this.moves.addAll(path);
        } else {
            System.out.println("Couldn't return to start as path couldn't be found.");
        }
    }


    /**
     * Finds and returns the path to the destination using A* search. A path is found only if constraints of total
     * moves are met.
     *
     * @param dest position to go to.
     * @param from the position to start the path from.
     * @param movesRemaining the number of moves remaining for the drone for the day.
     * @param order the order to associate the moves with.
     * @return A list of moves if a path is found, else null.
     */
    private List<Move> getPathTo(LongLat dest, LongLat from, int movesRemaining, Order order) {
        ArrayList<Move> pathMoves = new ArrayList<>();

        while (movesRemaining > 0 && !from.closeTo(dest)) {
            Move nextMove = new Move(from, dest, order);

            if (nextMove.isValid()) {
                from = nextMove.getDest();
                pathMoves.add(nextMove);
                movesRemaining--;
            } else {
                // The move collides with a No-Fly Zone so use A* search
                Search astar = new Search(from, dest, order);
                List<Move> aStarPath = astar.findPath(movesRemaining);

                if (aStarPath != null) {
                    from = aStarPath.get(aStarPath.size() - 1).getDest();
                    pathMoves.addAll(aStarPath);
                    movesRemaining -= aStarPath.size();
                } else {
                    System.out.println("Couldn't find A* path");
                    return null;
                }
            }
        }

        if (from.closeTo(dest) && movesRemaining > 0) {
            return pathMoves;
        } else {
            System.out.println("Couldn't reach destination either due to no path or not enough moves.");
            return null;
        }
    }

    /**
     * Returns the number of remaining moves
     *
     * @param moves An array of the all the moves that have been made for the day.
     * @return The number of moves that can be made for the day.
     */
    private int getRemainingMoves(ArrayList<Move> moves) {
        return MAX_MOVES - moves.size();
    }

    /**
     * @return The moves the drone has made for the day.
     */
    public ArrayList<Move> getMoves() {
        return moves;
    }

    /**
     * @return The orders that have been successfully delivered.
     */
    public List<Order> getOrdersDelivered() {
        return ordersDelivered;
    }
}
