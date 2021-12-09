package uk.ac.ed.inf;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Move {

    private LongLat orig, dest;
    private final Line2D line2D;
    private int angle;

    // The order for which this move is being made to complete
    private Order order;

    public Move(LongLat orig, int angle, Order order) {
        this.orig = orig;
        this.dest = orig.nextPosition(angle);
        this.order = order;
        this.angle = angle;
        var origPoint = new Point2D.Double(orig.longitude, orig.latitude);
        var destPoint = new Point2D.Double(dest.longitude, dest.latitude);
        this.line2D = new Line2D.Double(origPoint, destPoint);
    }

    public Move(LongLat orig, int angle, int numMoves, Order order) {
        this.orig = orig;
        this.dest = orig;
        for (int i = 0; i < numMoves; i++) {
            this.dest = this.dest.nextPosition(angle);
        }
        this.order = order;
        this.angle = angle;
        var origPoint = new Point2D.Double(orig.longitude, orig.latitude);
        var destPoint = new Point2D.Double(dest.longitude, dest.latitude);
        this.line2D = new Line2D.Double(origPoint, destPoint);
    }

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
        if (!this.dest.isConfined()) {
            return false;
        }

        for (Line2D side : LongLat.NO_FLY_ZONES_SIDES) {
            if (this.intersects(side)) {
                return false;
            }
        }

        return true;
    }

    public boolean intersects(Line2D side) {
        return this.line2D.intersectsLine(side);
    }

    @Override
    public String toString() {
        return String.format("[(%3f,%3f),(%3f,%3f),%d]", this.orig.longitude, this.orig.latitude, this.dest.longitude, this.dest.latitude, this.angle);
    }
}
