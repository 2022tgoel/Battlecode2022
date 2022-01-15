package exploratoryplayer;

import battlecode.common.*;
import java.util.*;
// shared code across the units
public class Unit{

    RobotController rc;
    int archon_index = -1;
    final Random rng = new Random();
    static final int goldToLeadConversionRate = 200;
    MapLocation homeArchon;
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
        rng.setSeed((long) rc.getID());
        homeArchon = findHomeArchon();
    }

    /**
     * run() is a placeholder implemented in the specific files
     **/
    public void run() throws GameActionException{
        
    }

    static int turn = 0;
    public void turn_update(){
        turn++;
    }
    //when you sense or detect, you get an archon_index
    /**
     * detectArchon() looks through the archon positions for a new one, then stores it in archon_index
     * @return true if it found an archon
     **/
    public boolean detectArchon() throws GameActionException {
        for (int i = 0; i < 4; i++) {
            int data = rc.readSharedArray(i);
            if (data != 0) {
                // rc.setIndicatorString("archon found UWU");
                archon_index = i;
                return true;
            }
        }
        return false;
    }
    /**
     * checks if any of the nearby robots are enemy archons, if so broadcasts it
     * @return true if archon was found, false otherwise
     **/
    public boolean senseArchon() throws GameActionException {
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        // if there are any nearby enemy robots, attack the one with the least health
        boolean found = false;
        if (nearbyBots.length > 0) {
            for (RobotInfo bot : nearbyBots) {
                if (bot.type == RobotType.ARCHON) {
                    int ind = broadcastArchon(bot.location);
                    //store index of last archon sensed 
                    archon_index = ind;
                    found = true;
                }  
            }
        }
        return found;
    }

    public int broadcastArchon(MapLocation loc) throws GameActionException{
        //check that the loc is not already broadcasted
        int indToPut = 0; // where to put the archon (if all spots are filled, it will be put at 0)
        for (int i= 0; i < 4; i++){
            int data = rc.readSharedArray(i);
            int x = data / 1000;
            int y = data % 1000;
            if (loc.x == x && loc.y == y) {
                return i; //already broadcasted, return index where it is stored
            }
            if (data == 0){
                indToPut = i;
            }
        }
        int loc_int = loc.x * 1000 + loc.y;
        rc.writeSharedArray(indToPut, loc_int);
        // rc.setIndicatorString("broadcasting succesful, archon_index " + available_index);
        return indToPut;
    }

    /**
     * approachArchon() moves towards the archon specified by archon_index
     * @return false is the archon is not longer there, true otherwise
     **/
    public boolean approachArchon() throws GameActionException{
        int data = rc.readSharedArray(archon_index);
        if (data != 0) {
            int x = data / 1000;
            int y = data % 1000;
            MapLocation target = new MapLocation(x, y);
            if (rc.canSenseLocation(target) && !rc.canSenseRobotAtLocation(target)){
                rc.writeSharedArray(archon_index, 0);
                archon_index = -1;
                return false;
            }
            else {
                fuzzyMove(target);
                return true;
            }
        }
        else {
            archon_index = -1; 
            return false; //no longer there
        }
    }

    
    /**
     * validCoords() check if the x and y are on the map
     * @return bool, true if on the map
     **/
    public boolean validCoords(int x_coord, int y_coord){
        return x_coord >= 0 && x_coord < rc.getMapWidth() && y_coord >= 0 && y_coord < rc.getMapHeight();
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

    public void moveInDirection(int[] toDest) throws GameActionException{
        MapLocation loc = rc.getLocation();
        MapLocation dest = new MapLocation(loc.x + toDest[0], loc.y + toDest[1]);
        fuzzyMove(dest);
        // rc.setIndicatorString("I JUST MOVED TO " + toDest[0] + " " + toDest[1]);
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

    /**
     * goToAdjacentLocation() moves the robot to the passed location (which must be adjacent)
     **/
    public void goToAdjacentLocation(MapLocation loc) throws GameActionException{
        MapLocation my = rc.getLocation();
        for (Direction d : directions){
            MapLocation adj = my.add(d);
            if (adj.equals(loc)){
                if (rc.canMove(d)){
                    rc.move(d);  
                }  
                break;
            }
        }
        //should not reach here, if it does the location you were passing is not adjacent!
    }
    /**
     * waitATurn() stays near the home archon
     **/
    public void waitATurn() throws GameActionException{
        //stays at around an ideal dist
        MapLocation myLocation = rc.getLocation(); 
        int idealDistSquared = 10;
        int buffer = 5;
        rc.setIndicatorString("" + Math.abs(myLocation.distanceSquaredTo(homeArchon)-idealDistSquared));
        if (Math.abs(myLocation.distanceSquaredTo(homeArchon)-idealDistSquared) < buffer){
            rc.setIndicatorString("here");
            return; //you're already in range
        }
        int[] costs = new int[8];
        for (int i = 0; i < 8; i++){
            MapLocation newLocation = myLocation.add(directions[i]);
            if (!rc.onTheMap(newLocation)) {
                costs[i] = 999999;
            }
            else {
                costs[i] = 100*Math.abs(newLocation.distanceSquaredTo(homeArchon)-idealDistSquared);
                costs[i] += rc.senseRubble(newLocation);
            }
        }
        int cost = 99999;
        Direction optimalDir = null;
        for (int i = 0; i < directions.length; i++) {
            Direction dir = directions[i];
            if (rc.canMove(dir)) {
                if (costs[i] < cost) {
                    cost = costs[i];
                    optimalDir = dir;
                }
            }
        }
        if (optimalDir != null) {
            if (rc.canMove(optimalDir)) {
                rc.setIndicatorString("here2");
                rc.move(optimalDir);
            }
        }

    }

    //1. bug pathing

    /**
     * bugMove() is the method that moves to a location using the bug pathing algorithm 
     * https://www.cs.cmu.edu/~motionplanning/lecture/Chap2-Bug-Alg_howie.pdf (bug 0)
     *
     * @param loc  location you wish to move to
     *            
     **/
    public void bugMove(MapLocation loc) throws GameActionException{
        bugMove(loc, 20); //will not go to squares with more that 20 rubble
    }
    static Direction bugDirection = null;
    public void bugMove(MapLocation loc, int rubbleThreshold) throws GameActionException{
        MapLocation cur = rc.getLocation();
        if (cur.equals(loc)){
            bugDirection = null;
            return; //you're already there!
        }
        else {
            Direction d = cur.directionTo(loc); // direction you would move if taking the direct path
            if (rc.canMove(d) && rc.senseRubble(cur.add(d)) <= rubbleThreshold){
                rc.move(d);
                bugDirection = null;
            }
            else {
                if (bugDirection == null) {
                    bugDirection = d;
                }
                for (int i = 0; i < 8; ++i) {
                    if (rc.canMove(bugDirection) && rc.senseRubble(cur.add(bugDirection)) <= rubbleThreshold){
                        rc.move(bugDirection);
                        bugDirection = bugDirection.rotateLeft();
                    }
                    bugDirection = bugDirection.rotateRight();
                }
                
            }
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
    //keep updating this so that you can see stagnation
    static int calls = 0; //# of fuzzy move calls
    static MapLocation last = null;
    static MapLocation cur =null;
    public Direction fuzzyMove(MapLocation dest) throws GameActionException{
        return fuzzyMove(dest, 2); //will not go to squares with more that 20 rubble
    }
    public Direction fuzzyMove(MapLocation dest, int rubbleWeight) throws GameActionException{
        MapLocation myLocation = rc.getLocation();
        if (myLocation.equals(dest)){
            return null; //you're already there!
        }
        Direction toDest = myLocation.directionTo(dest);
        if (myLocation.add(toDest).equals(dest)){
            if (rc.canMove(toDest)){
                rc.move(toDest);
            }
        }
        else{
            Direction optimalDir = getBestDirectionFuzzy(toDest, rubbleWeight);
            if (optimalDir != null) {
                if (rc.canMove(optimalDir)){ //if you can move in the optimalDir, then you can move toDest - toDest is into a wall
                    rc.move(optimalDir);
                    calls++; //only considered a call if you actually move
                    if (((calls>>4)& 1) > 0) { //just completed your 8th, 16th, etc, call
                        last = cur;
                        cur = myLocation;
                    }
                }
            }
            return optimalDir;
        }
        return null;

    }
    public Direction getBestDirectionFuzzy(Direction toDest, int rubbleWeight) throws GameActionException{
        MapLocation myLocation = rc.getLocation();
        Direction[] dirs = {toDest, toDest.rotateLeft(), toDest.rotateRight(), toDest.rotateLeft().rotateLeft(),
                toDest.rotateRight().rotateRight(), toDest.opposite().rotateLeft(), toDest.opposite().rotateRight(), toDest.opposite()};
        int[] costs = new int[8];
       // if (false) {
        if (last!= null && (((calls>>4)&1) > 0) && (myLocation.distanceSquaredTo(last) <=4)) { //just completed your 8th, 16th, etc, call last turn
            //you're stagnating
            for (int i = 0; i < dirs.length; i++) {
                MapLocation newLocation = myLocation.add(dirs[i]);
                // Movement invalid, set higher cost than starting value
                if (!rc.onTheMap(newLocation)) {
                    costs[i] = 999999;
                }
                else {
                    int cost = 0;
                    // Preference tier for moving towards target
                    if (i >=1){
                        cost+=5;
                    }
                    if (i >= 3) {
                        cost += 50;
                    }
                    if (i >=5 ){
                        cost+=30;
                    }
                    costs[i] = cost;
                }
                
                
            }
            int cost = 99999;
            Direction optimalDir = null;
            for (int i = 0; i < dirs.length; i++) {
                Direction dir = dirs[i];
                if (rc.canMove(dir)) {
                    if (costs[i] < cost) {
                        cost = costs[i];
                        optimalDir = dir;
                    }
                }
            }
            return optimalDir;
        }
        else {
            // Ignore repel factor in beginning and when close to target
            for (int i = 0; i < dirs.length; i++) {
                MapLocation newLocation = myLocation.add(dirs[i]);
                // Movement invalid, set higher cost than starting value
                if (!rc.onTheMap(newLocation)) {
                    costs[i] = 999999;
                }
                else {
                    int cost = rc.senseRubble(newLocation)* rubbleWeight;
                    // Preference tier for moving towards target
                    if (i >=1){
                        cost+=5;
                    }
                    if (i >= 3) {
                        cost += 30;
                    }
                    if (i >=5 ){
                        cost+=30;
                    }
                    costs[i] = cost+ rng.nextInt(10); //some randomness
                }
                
            }
            /*
            String s = "";
            for (int i= 0; i < dirs.length;i++){
                s+=String.valueOf(costs[i])+ " ";
            }
            s+=String.valueOf(rc.canMove(toDest));
            rc.setIndicatorString(s);*/
            int cost = 99999;
            Direction optimalDir = null;
            for (int i = 0; i < dirs.length; i++) {
                Direction dir = dirs[i];
                if (rc.canMove(dir)) {
                    if (costs[i] < cost) {
                        cost = costs[i];
                        optimalDir = dir;
                    }
                }
            }
            return optimalDir;
        }
    }
    
    public int cooldown(MapLocation loc) throws GameActionException{
        //returns cooldown of movement
        return (int) Math.floor((1+rc.senseRubble(loc)/10.0)*rc.getType().movementCooldown);
    }

    public int[] getExploratoryDir() {
        int[] dir;
        MapLocation cur = rc.getLocation();
        MapLocation center = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2);
        if (center.x - cur.x > 0) {
            if (center.y - cur.y > 0) {
                dir = new int[]{8,8};
            } else {
                dir = new int[]{8,-8};
            }
        } else {
            if (center.y - cur.y > 0) {
                dir = new int[]{-8,8};
            } else {
                dir = new int[]{-8,-8};
            }
        }
        int[][] dirs = new int[5][2];
        int counter = 0;

        int increment;
        if (dir[0] < 0) {
            increment = -4;
        } else {
            increment = 4;
        }

        for (int i = 0; i != dir[0]; i+= increment) {
            dirs[counter] = new int[]{i, dir[1]};
            counter += 1;
        }

        if (dir[1] < 0) {
            increment = -4;
        } else {
            increment = 4;
        }


        for (int i = 0; i != dir[1]; i+= increment) {
            dirs[counter] = new int[]{dir[0], i};
            counter += 1;
        }

        dirs[4] = dir;
        // print directions
        rc.setIndicatorString("dirs: " + dirs[0][0] + " " + dirs[0][1] + " " + dirs[1][0] + " " + dirs[1][1] + " " + dirs[2][0] + " " + dirs[2][1] + " " + dirs[3][0] + " " + dirs[3][1] + " " + dirs[4][0] + " " + dirs[4][1] + " | " + (center.x - cur.x) + " " + (center.y - cur.y) + " | " + center.x + " " + center.y + " | " + cur.x + " " + cur.y);

        return dirs[rng.nextInt(dirs.length)];
    }

    public boolean adjacentToEdge() throws GameActionException {
        MapLocation cur = rc.getLocation();
        int mapheight = rc.getMapHeight();
        int mapwidth = rc.getMapWidth();
        if (mapheight - cur.y < 3 || cur.y < 3 || cur.x < 3 || mapwidth - cur.x < 3) {
            return true;
        }
        return false;
    }

    public MapLocation findHomeArchon() throws GameActionException {
        if (rc.getType() == RobotType.ARCHON) {
            return rc.getLocation();
        }
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(-1, rc.getTeam());
        // if there are any nearby enemy robots, attack the one with the least health
        if (nearbyBots.length > 0) {
            for (RobotInfo bot : nearbyBots) {
                if (bot.type == RobotType.ARCHON) {
                    return bot.location;
                }
            }
        }
        return rc.getLocation();
    }
}
