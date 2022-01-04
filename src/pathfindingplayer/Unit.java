package pathfindingplayer;

import battlecode.common.*;
import java.util.*;
// shared code across the units
public class Unit{

    RobotController rc;

    static final Random rng = new Random(6147);
    
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
                    rc.setIndicatorDot(rc.getLocation().add(d), 0, 255, 255);
                    if (rc.canMove(d) && rc.senseRubble(cur.add(d)) <= rubbleThreshold){
                        rc.move(d);
                    }

                }
                // you are surrounded by bad squares, just move directly
                if (rc.canMove(d)){ 
                    rc.move(d.rotateRight());
                }

            }
            //need to navigate around obstacle
            
        }
    }

    //2. fuzzy moves
    /**
     * fuzzyMove() is the method that moves to a location using a weight of how within the correct direction you are
     *             how much rubble is in a square (rather that just thresholding rubbles)
     *
     * @param loc  location you wish to move to
     *            
     **/
    public void fuzzyMove(MapLocation loc){

    }
    //3. bfs/dijkstra of visible locations
}
