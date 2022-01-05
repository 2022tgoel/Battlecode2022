package dijkstraplayer;

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
        dialMove(loc); // best pathfinding strat 
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
    public void fuzzyMove(MapLocation dest) throws GameActionException{
        fuzzyMove(dest, 2); //will not go to squares with more that 20 rubble
    }
    public void fuzzyMove(MapLocation dest, double rubbleWeight) throws GameActionException{
        MapLocation myLocation = rc.getLocation();
        if (myLocation.equals(dest)){
            return; //you're already there!
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
                if (rc.canMove(optimalDir)){
                    rc.move(optimalDir);
                }
            }
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
    
    //3. bfs/dijkstra of visible locations (this is probably a lot of bytecode)
    public int cooldown(MapLocation loc) throws GameActionException{
        //returns cooldown of movement
        return (int) Math.floor((1+rc.senseRubble(loc)/10.0)*rc.getType().movementCooldown);
    }
    public static class MapTerrain implements Comparable<MapTerrain>{

        public MapLocation loc;
        public int x;
        public int y;
        public int dist;
        public MapTerrain prev = null;
        public boolean visited = false;
       
        public MapTerrain(MapLocation location, int dist, int x, int y) {
            this.loc = location;
            this.dist = dist;
            this.x = x;
            this.y =y;
        }

        public int compareTo(MapTerrain mt){
            return this.dist - mt.dist;
        }

        public boolean sameAs(MapTerrain mt){
            return loc.equals(mt);
        }

        public String toString(){
            return "X: " + loc.x + " Y: " + loc.y + " Dist: " + dist;
        }
    }

    static int INF = 10000000;
    /**
     * dijkstraMove() applies dijkstra to find the best movement towards loc
     * this might be bytecode intensive
     * 
     * @param target where you want to go
     **/
    public void dijkstraMove(MapLocation target) throws GameActionException{ 
        // initialization
        MapLocation my = rc.getLocation();

        if (my.equals(target)){
            return; 
        }
        PriorityQueue<MapTerrain> queue = new PriorityQueue<MapTerrain>();
        int rad1d = (int) Math.ceil(Math.sqrt(rc.getType().visionRadiusSquared));
        MapTerrain[][] grid = new MapTerrain[rad1d*2+1][rad1d*2+1];
        //populate grid
        
        int closestDist = INF;
        MapTerrain closest = null; //closes to target
        MapTerrain home = null;

        for (int dx = -1*rad1d; dx <= rad1d; dx++) {
            for (int dy = -1*rad1d; dy <= rad1d; dy++) {
                int x_coord = my.x + dx;
                int y_coord = my.y + dy;
                MapLocation loc;
                if (validCoords(x_coord, y_coord)) {
                    loc = new MapLocation(x_coord, y_coord);
                }
                else {
                    continue;
                }
                if (rc.canSenseLocation(loc)){
                    int cost = INF;
                    if (dx==0 && dy==0) cost =0;
                    grid[rad1d+dx][rad1d+dy] = new MapTerrain(loc, cost, rad1d+dx, rad1d+dy);
                    if (dx==0 && dy==0) home = grid[rad1d+dx][rad1d+dy];
                    queue.add(grid[rad1d+dx][rad1d+dy]);
                    if (loc.distanceSquaredTo(target) < closestDist){
                        closestDist = loc.distanceSquaredTo(target);
                        closest = grid[rad1d+dx][rad1d+dy];
                    }
                    rc.setIndicatorString(grid[rad1d+dx][rad1d+dy].toString());
                }
            }
        }
        
        //get correct dists
        while (!(queue.size()==0)){
            MapTerrain cur = queue.poll();
            //System.out.println("Visiting: " + cur.toString());
            cur.visited = true;
            //visit neigbhors
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    MapTerrain next = grid[cur.x+dx][cur.y+dy];
                    if (next!=null){
                        if (!next.visited){
                            int d = cur.dist + cooldown(next.loc);
                            if (d < next.dist){
                                queue.remove(next);
                                next.dist = d;
                                next.prev = cur;
                                queue.add(next);
                            }
                        }
                    }
                }
            }
        }
        // now reconstruct path
        MapTerrain next = closest;
        while (!(next.prev).sameAs(home)){
            next = next.prev;
        }
        //System.out.println("The chosen spot is: " + next.toString());
        //go to next.loc
        goToAdjacentLocation(next.loc);
    }

    //dial's algorithm
    public static class DialItem {

        public MapLocation loc;
        public MapLocation prev;
       
        public DialItem(MapLocation loc, MapLocation prev) {
            this.loc = loc;
            this.prev = prev;
        }

        public String toString(){
            return "X: " + loc.x + "Y: " + loc.y;
        }
    }
    public void dialMove(MapLocation target) throws GameActionException{
        MapLocation my = rc.getLocation();

        if (my.equals(target)){
            return; 
        }

        int closestDist = INF;
        MapLocation closest = null; //closes to target
        HashSet<MapLocation> visited = new HashSet<MapLocation> ();
        HashMap<MapLocation, MapLocation> prev = new HashMap<MapLocation, MapLocation>();
        for (MapLocation loc : rc.getAllLocationsWithinRadiusSquared(my, rc.getType().visionRadiusSquared)){
            if (loc.distanceSquaredTo(target) < closestDist){
                closestDist = loc.distanceSquaredTo(target);
                closest = loc;
            }
        }
        
        ArrayList<DialItem>[] buckets = new ArrayList[80]; //cannot take this many moves
        buckets[0].add(new DialItem(my, null));
        for (int i = 0; i < 80; i++){
            for (DialItem item : buckets[i]){
                if (item.loc.equals(target)){
                    break;
                }
                visited.add(item.loc);
                prev.put(item.loc, item.prev);
                for (Direction d : directions){
                    MapLocation next = item.loc.add(d);
                    if (rc.canSenseLocation(next) && !visited.contains(next)){
                        int moves = i + cooldown(next)/10;
                        buckets[moves].add(new DialItem(next, item.loc));
                    }
                }
            }
        }
        // now reconstruct path
        MapLocation next = closest;
        while (!prev.get(next).equals(my)){
            next = prev.get(next);
        }
        rc.setIndicatorString(next.toString());
        //System.out.println("The chosen spot is: " + next.toString());
        //go to next.loc
        goToAdjacentLocation(next);

    }
}
