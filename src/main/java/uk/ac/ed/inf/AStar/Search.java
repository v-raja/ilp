package uk.ac.ed.inf.AStar;

import uk.ac.ed.inf.LongLat;
import uk.ac.ed.inf.Move;
import uk.ac.ed.inf.Order;

import java.util.*;


/**
 * Performs AStar search to find series of steps to get to
 * drone's reading range of the destination. It represents each
 * step as node and searches a path.
 */
public class Search {
    /**
     * list of nodes to explore
     */
    private final PriorityQueue<Node> nodesToExplore;

    /**
     * list of nodes already explored
     */
    private final Set<Node> exploredNodes;

    /**
     * initial node to find a path from
     */
    private final Node initialNode;

    /**
     * destination to find a path to
     */
    private final LongLat destinationPos;

    private Order order;

    /**
     * Construct an AStar search instance for the given initial and destination position.
     * SensorMap is also given to check for any collision
     *
     * @param initialPos     Starting position of the search
     * @param destinationPos Target destination of the AStar search
     * @param order      SensorMap to search over
     */
    public Search(LongLat initialPos, LongLat destinationPos, Order order) {
        this.initialNode = new Node(initialPos, destinationPos, null);
        this.destinationPos = destinationPos;
        this.nodesToExplore = new PriorityQueue<>(Comparator.comparingDouble(Node::getTotalCost));
        this.exploredNodes = new HashSet<>();
        this.order = order;
    }


    /**
     * Find a path from the initial to destination position using A* search
     *
     * @return Path as list of positions from initial to final position. Empty list if no path exists.
     */
    public ArrayList<Move> findPath(int maxMoves) {
        nodesToExplore.add(initialNode);
        while (!nodesToExplore.isEmpty()) {
            Node currentNode = nodesToExplore.poll();
            exploredNodes.add(currentNode);

            // If near to destination pos, then stop and return path from current node
            if (currentNode.closeTo(destinationPos)) {
                var path = getPath(currentNode);

                ArrayList<Move> output = new ArrayList<>();
                for (int i = 0; i < path.size() - 1; i++) {
                    output.add(new Move(path.get(i), path.get(i + 1), this.order));
                }
                return output;
            } else {
                generateNewNodes(currentNode, maxMoves);
            }
        }
        return null;
    }


    /**
     * Gets the path from the current node to the initial node
     *
     * @param node Node to get a path from
     * @return List of position to represent the path from initialNode to destination
     */
    private ArrayList<LongLat> getPath(Node node) {
        ArrayList<LongLat> path = new ArrayList<>();
        path.add(node);
        Node parentNode;
        while ((parentNode = node.getParent()) != null) {
            path.add(0, parentNode);
            node = parentNode;
        }
        return path;
    }


    /**
     * Generates new nodes to explore
     *
     * @param currentFrontierNode The node to generate new nodes and expand the frontier from
     */
    private void generateNewNodes(Node currentFrontierNode, int maxMoves) {
        var angleToDest = currentFrontierNode.angleTo(destinationPos);
        for (int angle = -90; angle <= 90; angle += 30) {
            var angleToTry = (angleToDest + angle) % 360;
            LongLat nextPos = currentFrontierNode.nextPosition(angleToTry);
            Node newNode = new Node(nextPos, destinationPos, currentFrontierNode);

            var step = new Move(currentFrontierNode, nextPos, order);
            var five_steps_in_dir = new Move(currentFrontierNode, step.getAngle(), 100, order);
            // If node not previously explored, and isn't in no fly zone, then explore it, else skip it
            if (!exploredNodes.contains(newNode) && step.isValid() && !five_steps_in_dir.intersectsWithNoFlyZone()) {
                // If newNode not already generated previously, then add to list of nodes to explore
                if (!nodesToExplore.contains(newNode)) {
                    if (newNode.getNodeDistance() <= maxMoves) {
                        // only include node if it doesn't exceed maximum moves remaining
                        nodesToExplore.add(newNode);
                    }
                } else {
                    // newNode has already been generated, but not yet explored
                    boolean newNodeNeedsUpdate = newNode.compareParent(currentFrontierNode);

                    // if better path is available for newNode from currentFrontierNode
                    if (newNodeNeedsUpdate) {
                        newNode.changeParent(currentFrontierNode);

                        // Remove and add the changed node, so that the PriorityQueue can sort again its
                        // contents with the modified "totalCost" value of the modified node
                        nodesToExplore.remove(newNode);
                        nodesToExplore.add(newNode);
                    }
                }
            }
        }
    }
}
