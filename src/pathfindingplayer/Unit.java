package pathfindingplayer;

import battlecode.common.*;
import java.util.*;
// shared code across the units
public class Unit{

    RobotController rc;
    boolean archon_found = false;
    int archon_index = -1;
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

    public void senseArchon() throws GameActionException {
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        // if there are any nearby enemy robots, attack the one with the least health
        if (nearbyBots.length > 0) {
            for (RobotInfo bot : nearbyBots) {
                if (bot.type == RobotType.ARCHON) {
                    archon_found = true;
                    broadcastArchon(bot.location);
                }
            }
        }
    }

    public void broadcastArchon(MapLocation loc) throws GameActionException{
        int data = 0;
        int available_index = 0;
        int x;
        int y;
        for (int i = 4; i >= 0; i--) {
            data = rc.readSharedArray(i);
            if (data == 0) {
                available_index = i;
            }
            else {
                x = data / 1000;
                y = data % 1000;
                if (loc.x == x && loc.y == y) {
                    return;
                }
            }
        }

        // rc.setIndicatorString("broadcasting archon");
        int loc_int = loc.x * 1000 + loc.y;
        rc.writeSharedArray(available_index, loc_int);
        // rc.setIndicatorString("broadcasting succesful, archon_index " + available_index);
        archon_index = available_index;
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
                    // rc.setIndicatorString(grid[rad1d+dx][rad1d+dy].toString());
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

}
