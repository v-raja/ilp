package uk.ac.ed.inf;

/**
 * Represents a node in AStar graph search which has a corresponding
 * (Longitude,Latitude) Position in the map.
 */
public class Node extends LongLat {

    /**
     * Estimated cost to get to the destination position.
     * Measured in Euclidean distance
     */
    private final double heuristicCost;
    /**
     * Actual cost to get to the Node from the start
     */
    private double actualCost;
    /**
     * Total cost which is the sum of actual cost and heuristic cost
     */
    private double totalCost;

    /**
     * Reference to the parent node used to trace back the path
     */
    private Node parent;

    /**
     * Initialises the Node with given position and destination position,
     * and the parent node.
     *
     * @param pos            Position of the Node in the map
     * @param destinationPos Position of the destination
     * @param parentNode     Parent node in the graph.
     */
    public Node(LongLat pos, LongLat destinationPos, Node parentNode) {
        super(pos.longitude, pos.latitude);

        // parentNode is null when this node is the initial node
        if (parentNode == null) {
            this.actualCost = 0;
            this.parent = null;
        } else {
            // actual cost is parent's actual cost + cost of one move
            this.actualCost = parentNode.getActualCost() + Drone.MOVE_LENGTH_IN_DEGREES;
            this.parent = parentNode;
        }
        // use Euclidean distance as heuristic cost
        this.heuristicCost = this.distanceTo(destinationPos);
        this.totalCost = this.actualCost + this.heuristicCost;
    }


    /**
     * Updates the parent node of this node
     *
     * @param parentNode the node which will be made the parent of the current node
     */
    public void updateParentNode(Node parentNode) {
        double newActualCost = parentNode.getActualCost() + Drone.MOVE_LENGTH_IN_DEGREES;
        this.parent = parentNode;
        this.actualCost = newActualCost;
        this.totalCost = newActualCost + this.heuristicCost;
    }


    /**
     * Checks if the alternative parent node given provides a path with a lower
     * actual cost to the current node.
     *
     * @param altParentNode Alternative parent node to compare to
     * @return boolean          whether altParentNode provides a better path
     */
    public boolean isBetterParentNode(Node altParentNode) {
        double altActualCost = altParentNode.getActualCost() + Drone.MOVE_LENGTH_IN_DEGREES;
        return altActualCost < this.actualCost;
    }


    /**
     * Returns the actual cost to get to this node
     *
     * @return The actual cost to this node
     */
    public double getActualCost() {
        return actualCost;
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
