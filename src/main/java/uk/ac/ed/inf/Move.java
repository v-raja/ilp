package uk.ac.ed.inf;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;

public class Move {


    /**
     * The origin of the move.
     */
    private LongLat orig;
    /**
     * The destination of the move.
     */
    private LongLat dest;

    /**
     * The Line2D line of the move. This is used to check intersections with the sides of the polygons of the
     * no-fly zones.
     */
    private final Line2D moveLine;

    /**
     * The angle the move is made in.
     */
    private int angle;

    /**
     * The order for which this move is being made to complete.
     */
    private Order order;

    /**
     * A class that encapsulates the logic of a move of a drone. It includes details relevant (such as angle)
     * to construct the flight path of the drone and fields the give context about the move (the order which
     * it is being made for). It also includes a helper function to see if a move is valid (i.e. intersects
     * with no fly zones or goes out of the confinement area).
     * @param orig the position of the origin of the move.
     * @param angle the angle the move is made in.
     * @param numMoves the nubmer of moves to make at the specified `angle` from `orig`.
     * @param order the order for which this move is being made for.
     * @author Vivek Raja s1864074
     */
    public Move(LongLat orig, int angle, int numMoves, Order order) {
        this.orig = orig;
        this.dest = orig.nextPosition(angle, numMoves);
        this.order = order;
        this.angle = angle;
        var origPoint2D = new Point2D.Double(orig.longitude, orig.latitude);
        var destPoint2D = new Point2D.Double(dest.longitude, dest.latitude);
        this.moveLine = new Line2D.Double(origPoint2D, destPoint2D);
    }

    public Move(LongLat orig, int angle, Order order) {
        this(orig, angle, 1, order);
    }

    /**
     * A constructor of Move to specify the destination (instead of the angle). The actual destination of the
     * move is computed by computing the angle to the destination and then making a move in that direction. It
     * won't be the same as the computed angle is in increments of 10 degrees.
     * @param orig the position of the origin of the move.
     * @param dest the position to compute the angle of the destination towards.
     * @param order the order for which this move is being made for.
     * @author Vivek Raja s1864074
     */
    public Move(LongLat orig, LongLat dest, Order order) {
        this(orig, orig.angleTo(dest), order);
    }


    public LongLat getOrig() {
        return orig;
    }

    public LongLat getDest() {
        return dest;
    }

    public int getAngle() {
        return angle;
    }

    public Order getOrder() {return order;}

    public boolean isValid() {
        if (!this.dest.isConfined() || intersectsWithNoFlyZone()) {
            return false;
        }
        return true;
    }

    public boolean intersectsWithNoFlyZone() {
        for (Line2D side : LongLat.NO_FLY_ZONES_SIDES) {
            if (this.intersects(side)) {
                return true;
            }
        }
        return false;
    }

    public static void addOrder(List<Move> moves, Order order) {
        for (Move move : moves) {
            move.order = order;
        }
    }

//    public void setOrder(Order order) {
//        this.order = order;
//    }

    public boolean intersects(Line2D side) {
        return this.moveLine.intersectsLine(side);
    }

    @Override
    public String toString() {
        return String.format("[(%3f,%3f),(%3f,%3f),%d]", this.orig.longitude, this.orig.latitude, this.dest.longitude, this.dest.latitude, this.angle);
    }
}

