package pathfindingplayer;

import battlecode.common.*;
import java.util.*;
// shared code across the units
public class Unit{

    RobotController rc;

    final Random rng = new Random();
    static final int goldToLeadConversionRate = 200;
    /** Array containing all the possible movement directions. */
    static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };

	public Unit(RobotController robotController) throws GameActionException {
        rc = robotController;
    }

    /**
     * run() is a placeholder implemented in the specific files
     **/
    public void run() throws GameActionException{
    }

    //pathfinding strategies
    /**
     * moveToLocation() is the moving function that will actually be used by the bots (made a separate function for 
     * easy replacement)
     * 
     * @param loc is where to go
     **/
    public void moveToLocation(MapLocation loc) throws GameActionException{
        fuzzyMove(loc); // best pathfinding strat 
    }
    /**
     * moveInDirection() moves in a direction while avoiding rubble in that direction
     **/
    public void moveInDirection(Direction toDest) throws GameActionException{
        Direction optimalDir = getBestDirectionFuzzy(toDest, 2);
        if (optimalDir != null) {
            rc.move(optimalDir);
        }
    }


    //1. bug pathing

    /**
     * bugMove() is the method that moves to a location using the bug pathing algorithm
     *
     * @param loc  location you wish to move to
     *            
     **/
    public void bugMove(MapLocation loc) throws GameActionException{
        bugMove(loc, 20); //will not go to squares with more that 20 rubble
    }
    public void bugMove(MapLocation loc, int rubbleThreshold) throws GameActionException{
        MapLocation cur = rc.getLocation();
        if (cur.equals(loc)){
            return; //you're already there!
        }
        else {
            Direction direct = cur.directionTo(loc); // direction you would move if taking the direct path
            Direction d = cur.directionTo(loc); // direction you move in
            if (rc.canMove(d) && rc.senseRubble(cur.add(d)) <= rubbleThreshold){
                rc.move(d);
            }
            else {
                while (d.rotateRight()!=direct){
                    d = d.rotateRight();
                   // assert(!d.equals(direct));
                   // rc.setIndicatorDot(rc.getLocation().add(d), 0, 255, 255);
                    if (rc.canMove(d) && rc.senseRubble(cur.add(d)) <= rubbleThreshold){
                        rc.move(d);
                    }

                }
                // you are surrounded by bad squares, just move directly
                //if (rc.canMove(d)){ 
                //   rc.move(d.rotateRight());
                //}

            }
            //need to navigate around obstacle
            
        }
    }

    //2. fuzzy moves
    /**
     * fuzzyMove() is the method that moves to a location using a weight of how within the correct direction you are
     *             how much rubble is in a square (rather that just thresholding rubbles)
     *
     * @param dest  location you wish to move to
     *            
     **/
    public void fuzzyMove(MapLocation dest) throws GameActionException{
        fuzzyMove(dest, 2); //will not go to squares with more that 20 rubble
    }
    public void fuzzyMove(MapLocation dest, double rubbleWeight) throws GameActionException{
        MapLocation myLocation = rc.getLocation();
        if (myLocation.equals(dest)){
            return; //you're already there!
        }
        Direction toDest = myLocation.directionTo(dest);
        Direction optimalDir = getBestDirectionFuzzy(toDest, rubbleWeight);
        if (optimalDir != null) {
            rc.move(optimalDir);
        }

    }

    public Direction getBestDirectionFuzzy(Direction toDest, double rubbleWeight) throws GameActionException{
        MapLocation myLocation = rc.getLocation();
        Direction[] dirs = {toDest, toDest.rotateLeft(), toDest.rotateRight(), toDest.rotateLeft().rotateLeft(),
            toDest.rotateRight().rotateRight(), toDest.opposite().rotateLeft(), toDest.opposite().rotateRight(), toDest.opposite()};
        double[] costs = new double[8];
        // Ignore repel factor in beginning and when close to target
        for (int i = 0; i < dirs.length; i++) {
            MapLocation newLocation = myLocation.add(dirs[i]);
            // Movement invalid, set higher cost than starting value
            if (!rc.onTheMap(newLocation)) {
                costs[i] = 999999;
                continue;
            }
            double cost = rc.senseRubble(newLocation)* rubbleWeight;
            // Preference tier for moving towards target
            if (i >= 3) {
                cost += 50;
            }
            costs[i] = cost;
        }
        double cost = 99999;
        Direction optimalDir = null;
        for (int i = 0; i < dirs.length; i++) {
            Direction dir = dirs[i];
            if (rc.canMove(dir)) {
                double newCost = costs[i];
                // add epsilon to forward direction
                if (dir == toDest) {
                    newCost -= 0.001;
                }
                if (newCost < cost) {
                    cost = newCost;
                    optimalDir = dir;
                }
            }
        }
        return optimalDir;
    }
    //3. bfs/dijkstra of visible locations
}
