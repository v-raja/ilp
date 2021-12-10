package uk.ac.ed.inf;
import uk.ac.ed.inf.AStar.Search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Drone2 {

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
     * A class that encapsulates the logic of a drone for its daily deliveries.
     * Initialising the class finds the most optimal route and completes the delivers for the orders.
     * @author Vivek Raja s1864074
     */
    Drone2(LongLat start, ArrayList<Order> orders) {
        this.moves = new ArrayList<>();
//        this.orders = TSPSolver.solveForOrders(start, orders);
        this.orders = orders;
        this.start = start;
        this.currPos = start.copy();

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

        // make local copies of state
        ArrayList currMoves = new ArrayList(this.moves);
        var currDronePos = this.currPos.copy();

        // add all stops to a list
        ArrayList<LongLat> allStops = new ArrayList<>();
        for (Shop shop : order.getShops()) {
            allStops.add(shop.locationInLongLat);
        }
        allStops.add(order.getDeliverToInLongLat());

        // loop through all stops and visit them
        // if we're unable to make one stop on the visit, we don't bother with the order
        for (LongLat stop : allStops) {
            // if we're unable to make it back to the base after this stop, we won't deliver this order
            int movesRemaining = getRemainingMoves(currMoves);
            var pathToBase = getPathTo(this.start, stop, movesRemaining, null);
            if (pathToBase == null) {
                System.out.println("Unable to complete more orders due to not enough moves back to base.");
                return;
            }

            // get path to stop
            List<Move> pathToStop = getPathTo(stop, currDronePos, movesRemaining, order);
            if (pathToStop == null) {
                System.out.println("Unable to complete order " + order.getOrderNumber());
                return;
            } else {
                // update drone position to the last stop and update local drone state
                LongLat dronePosAfterStop = pathToStop.get(pathToStop.size() - 1).getDest();
                currDronePos = dronePosAfterStop.copy();

                // hover to pick or drop off a sandwich
                var hoveringMove = new Move(currDronePos, SPECIAL_HOVERING_ANGLE, order);
                pathToStop.add(hoveringMove);
                currMoves.addAll(pathToStop);
            }
        }

        // Mark order as completed
        order.markCompleted();
        System.out.println("ORDER COMPLETED " + order.getOrderNumber());
        ordersDelivered.add(order);
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


        // If we're already in range of the destination
        if (from.closeTo(dest) && movesRemaining >= 0) {
            return new ArrayList<>();
        } else {
            // find path
            Search astar = new Search(from, dest, order);
            List<Move> path = astar.findPath(movesRemaining);

            if (path != null) {
                return path;
            } else {
                System.out.println("Couldn't find A* path");
                return null;
            }
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


