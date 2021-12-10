package uk.ac.ed.inf.AStar;

import uk.ac.ed.inf.Drone;
import uk.ac.ed.inf.LongLat;

/**
 * A class that represents a node in A* graph search.
 */
public class Node extends LongLat {

    /**
     * Heurisitc cost - the estimate of how much it'll cost to reach the goal node.
     * In this case, we use Eucledian distance.
     */
    private final double hCost;

    /**
     * Cost to get to this node from the initial node.
     */
    private double actualCost;

    /**
     * Sum of the heuristic and actual costs.
     */
    private double totalCost;

    /**
     * The distance (in number of nodes) away from the initial node.
     * Used to keep track of how far we are from the initial node so that we can end early
     * if we are getting too far from the initial node.
     * Note: numbering starts from 1.
     */
    private int nodeDistance;

    /**
     * The parent node of this node.
     */
    private Node parent;

    /**
     * Instantiates a Node object for use in A* search.
     *
     * @param curr Position (in LongLat) of the node
     * @param dest Position (in LongLat) of the dest
     * @param parent The parent node of this node. Should be null if this is a root node.
     */
    public Node(LongLat curr, LongLat dest, Node parent) {
        // Call super since this extends the LongLat object.
        super(curr.longitude, curr.latitude);
        this.hCost = this.distanceTo(dest);

        // if this is a root node
        if (parent == null) {
            this.parent = null;
            this.actualCost = 0;
            this.nodeDistance = 1;
            this.totalCost = this.actualCost + this.hCost;
        } else {
            // keep all logic encapsulated with changing the parent in one function.
            changeParent(parent);
        }
    }

    /**
     * Checks if the alternative parent node given provides a path with a lower
     * actual cost to the current node.
     *
     * @param altParentNode Alternative parent node to compare to
     * @return boolean          whether altParentNode provides a better path
     */
    public boolean compareParent(Node altParentNode) {
        double altActualCost = altParentNode.getActualCost() + Drone.MOVE_LENGTH_IN_DEGREES;
        return altActualCost < this.actualCost;
    }

    /**
     * Changes the parent node and updates all the relevant costs and fields.
     * @param parent the node to be made the parent of this node.
     */
    public void changeParent(Node parent) {
        double newActualCost = parent.getActualCost() + Drone.MOVE_LENGTH_IN_DEGREES;
        this.parent = parent;
        this.actualCost = newActualCost;
        this.totalCost = newActualCost + this.hCost;
        this.nodeDistance = parent.getNodeDistance() + 1;
    }

    /**
     * Returns the actual cost to get to this node
     *
     * @return The actual cost to this node
     */
    public double getActualCost() {
        return actualCost;
    }

    public int getNodeDistance() {
        return this.nodeDistance;
    }


    /**
     * Returns the total cost to get to this node
     * which is the sum of actual cost and the heuristic cost
     *
     * @return double   The total cost to this node
     */
    public double getTotalCost() {
        return totalCost;
    }


    /**
     * Return the parent node of this node
     *
     * @return Node     The parent node of this node
     */
    public Node getParent() {
        return parent;
    }
}
