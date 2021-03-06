package cereusplayer;

import battlecode.common.*;
import java.util.*;
// shared code across the units
public class Unit{

    RobotController rc;
    int archon_index = -1;
    RANK[] rank_map = initializeRankMap();
    final Random rng = new Random();
    static final int goldToLeadConversionRate = 200;
    int seed_increment = 5;
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
    int mapArea;
    public Unit(RobotController robotController) throws GameActionException {
        rc = robotController;
        rng.setSeed((long) rc.getID() + seed_increment);
        homeArchon = findHomeArchon();
        initializeRankMap();
        mapArea =  rc.getMapHeight() * rc.getMapWidth();;
        calcExploreDirs(5);
    }

    /**
     * run() is a placeholder implemented in the specific files
     **/
    public void run() throws GameActionException{
        
    }
    //when you sense or detect, you get an archon_index
    /**
     * detectArchon() looks through the archon positions for a new one, then stores it in archon_index
     * @return true if it found an archon
     **/
    public boolean detectArchon() throws GameActionException {
        if (archon_index != -1) {
            int data = rc.readSharedArray(CHANNEL.ARCHON_LOC_1.getValue() + archon_index);
            if (data != 0) {
                rc.setIndicatorString("archon found UWU1 " + archon_index);
                assert(archon_index != -1);
                return true;
            }
        }
        for (int i = 0; i < 4; i++) {
            int data = rc.readSharedArray(CHANNEL.ARCHON_LOC_1.getValue() + i);
            if (data != 0) {
                rc.setIndicatorString("archon found UWU2 " + archon_index);
                archon_index = i;
                assert(archon_index != -1);
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
                    assert(archon_index != -1);
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
            int data = rc.readSharedArray(CHANNEL.ARCHON_LOC_1.getValue() + i);
            int x = data / 64;
            int y = data % 64;
            if (loc.x == x && loc.y == y) {
                return i; //already broadcasted, return index where it is stored
            }
            if (data == 0){
                indToPut = i;
            }
        }
        int loc_int = locationToInt(loc);
        rc.writeSharedArray(indToPut, loc_int);
        // rc.setIndicatorString("broadcasting succesful, archon_index " + available_index);
        return indToPut;
    }

    /**
     * approachArchon() moves towards the archon specified by archon_index
     * @return false is the archon is not longer there, true otherwise
     **/
    public boolean approachArchon() throws GameActionException{
        assert(archon_index != -1);
        int data = rc.readSharedArray(archon_index);
        if (data != 0) {
            int x = data / 64;
            int y = data % 64;
            MapLocation target = new MapLocation(x, y);
            if (rc.canSenseLocation(target)){
                RobotInfo r = rc.senseRobotAtLocation(target);
                if (r == null || r.type != RobotType.ARCHON){
                    rc.writeSharedArray(archon_index, 0);
                    archon_index = -1;
                    return false;
                }
                
            }
            fuzzyMove(target);
            return true;
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
        assert(validCoords(loc.x, loc.y));
        fuzzyMove(loc); // best pathfinding strat 
    }

    public boolean moveInDirection(int[] toDest) throws GameActionException{ 
        MapLocation loc = rc.getLocation();
        //scaling this directional vector so that it is on the map 
        double scaleFactor = 1;
        if (loc.x + toDest[0] >= rc.getMapWidth()-1){
            scaleFactor = Math.min(scaleFactor, ((double) (rc.getMapWidth() - 1 - loc.x))/((double) toDest[0]));
        }
        if (loc.y + toDest[1] >= rc.getMapHeight()-1){
            scaleFactor = Math.min(scaleFactor, ((double) (rc.getMapHeight() - 1 - loc.y))/((double) toDest[1]));
        }
        if (loc.x + toDest[0] < 1){
            scaleFactor = Math.min(scaleFactor, ((double) (loc.x))/((double) -1*toDest[0]));
        }
        if (loc.y + toDest[1] < 1){
            scaleFactor = Math.min(scaleFactor, ((double) (loc.y))/((double) -1*toDest[1]));
        }
        //if scale factor is really small then return this to conduct flipping???
        //fuzzy moving to this loc
        if (scaleFactor == 0){
            return false;
        }
        MapLocation dest = new MapLocation(loc.x + (int) (scaleFactor * (double) toDest[0]), loc.y + (int) (scaleFactor * (double) toDest[1]) );
        try {
            assert(validCoords(dest.x, dest.y));
        }
        catch (AssertionError e){
            System.out.println(scaleFactor + " " + loc.x + " " + loc.y + " " + toDest[0] + " " + toDest[1]);
            return false;
        }
        fuzzyMove(dest);
        return true;
        // rc.setIndicatorString("I JUST MOVED TO " + toDest[0] + " " + toDest[1]);
    }
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
        return fuzzyMove(dest, 0.1); //will not go to squares with more that 20 rubble
    }
    public Direction fuzzyMove(MapLocation dest, double rubbleWeight) throws GameActionException{
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
      //      rc.setIndicatorString("here");
            if (optimalDir != null) {
                if (rc.canMove(optimalDir)){ //if you can move in the optimalDir, then you can move toDest - toDest is into a wall
                    rc.move(optimalDir);
                    calls++; //only considered a call if you actually move
                    if (((calls>>3)& 1) > 0) { //just completed your 8th, 16th, etc, call
                        last = cur;
                        cur = myLocation;
                    }
                } 
            }
            return optimalDir;
            // find location

        }
        return null;

    }

    public Direction getBestDirectionFuzzy(Direction toDest, double rubbleWeight) throws GameActionException{
        MapLocation myLocation = rc.getLocation();
        Direction[] dirs = {toDest, toDest.rotateLeft(), toDest.rotateRight(), toDest.rotateLeft().rotateLeft(),
                toDest.rotateRight().rotateRight(), toDest.opposite().rotateLeft(), toDest.opposite().rotateRight(), toDest.opposite()};
        int[] costs = new int[8];
       // if (false) {
        if (last!= null && (((calls>>3)&1) > 0) && (myLocation.distanceSquaredTo(last) <=4)) { //just completed your 8th, 16th, etc, call last turn
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
                if (!validCoords(newLocation.x, newLocation.y)) {
                    costs[i] = 999999;
                }
                else {
                    int cost = (int) (rubbleWeight * Math.pow((double) rc.senseRubble(newLocation), 2.0));
                    // Preference tier for moving towards target
                    if (i >=1){
                        cost+=5;
                    }
                    if (i >= 3) {
                        cost += 30;
                    }
                    if (i >=5){
                        cost+=30;
                    }
                    costs[i] = cost+ rng.nextInt(10); //some randomness
                }
                
            }
            
            String s = "";
            for (int i= 0; i < dirs.length;i++){
                s+=String.valueOf(costs[i])+ " ";
            }
            s+=String.valueOf(rc.canMove(toDest));
            rc.setIndicatorString(s);
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

    public int[] flip(int[] dir){//directional vector input
        assert(dir.length == 2);
        MapLocation loc = rc.getLocation();
        int[] ret = new int[2];
        ret[0] = dir[0];
        ret[1] = dir[1];
        if (loc.x == 0 && ret[0] <0){
            ret[0] = -1*ret[0];
        } 
        if (loc.y == 0 && ret[1] <0){
            ret[1] = -1*ret[1];
        }
        if (loc.x >= rc.getMapWidth()-1 && ret[0]>0){
            ret[0] = -1*ret[0];
        }
        if (loc.y >= rc.getMapHeight()-1 && ret[1]>0){
            ret[1] = -1*ret[1];
        }
        return ret;
    }

    int[][] exploreLocs;
    public void calcExploreDirs(int N){ //number of directions
        int w = rc.getMapWidth(); int h = rc.getMapHeight();
        double regionArea = ((double) mapArea / (double) N);
        int[] numRegions = {(int) Math.ceil((0.5 * (double) w * (double) homeArchon.y) / regionArea), //bottom
                            (int) Math.ceil((0.5 * (double) w * (double) (h - homeArchon.y)) / regionArea), //top
                            (int) Math.ceil((0.5 * (double) h * (double) homeArchon.x) / regionArea), //left edge
                            (int) Math.ceil((0.5 * (double) h * (double) (w - homeArchon.x)) / regionArea)}; //right
        int[] regionSizes = {w/numRegions[0], w/numRegions[1], h/numRegions[2], h/numRegions[3]};
        int tot = 0;
        for (int i = 0; i < 4; i++){
          //  System.out.println("yohoo " + numRegions[i] + " " + regionSizes[i]);
            tot+=numRegions[i];
        }
        exploreLocs = new int[tot][2];
        int cur = 0;
        for (int i= 1; i <=numRegions[0]; i++){
            exploreLocs[cur] = new int[]{regionSizes[0]*i - regionSizes[0]/2, 0};
            cur++;
            
        }
        for (int i= 1; i <= numRegions[1]; i++){
            exploreLocs[cur] = new int[]{regionSizes[1]*i - regionSizes[1]/2, h-1};
            cur++;
        }
        for (int i= 1; i <= numRegions[2]; i++){
            exploreLocs[cur] = new int[]{0, regionSizes[2]*i - regionSizes[2]/2};
            cur++;
        }
        for (int i= 1; i <= numRegions[3]; i++){
            exploreLocs[cur] = new int[]{w-1, regionSizes[3]*i - regionSizes[3]/2};
            cur++;
        }
        /*
        for (int i= 0; i < tot; i++){
            System.out.println(exploreDirs[i][0] + " " + exploreDirs[i][1]);
        }*/
    }

    public int[] locationToDir(int x, int y){
        double desiredLength = 12; //length of vector
        MapLocation my = rc.getLocation();
        double curLength = Math.sqrt(Math.pow(x-my.x, 2)+Math.pow(y-my.y, 2));
        return new int[]{(int) (((double)x-(double)my.x)*(desiredLength/curLength)),
                        (int) (((double)y-(double)my.y)*(desiredLength/curLength))};
    }

    public int[] getExploratoryDir() {
        return getExploratoryDir(5);
    }

    public int[] getExploratoryDir(int span) {
        // presumes span is odd.
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
        int[][] dirs = new int[span][2];
        int counter = 0;

        int increment;
        int init_val;
        if (dir[0] < 0) {
            increment = -4;
            init_val = -8 + ((((span + 1 ) / 2) - 1) * -increment);
        } else {
            increment = 4;
            init_val = 8 + ((((span + 1 ) / 2) - 1) * -increment);
        }

        for (int i = init_val; i != dir[0]; i+= increment) {
            dirs[counter] = new int[]{i, dir[1]};
            counter += 1;
        }

        if (dir[1] < 0) {
            increment = -4;
            init_val = -8 + ((((span + 1 ) / 2) - 1) * -increment);;
        } else {
            increment = 4;
            init_val = 8 + ((((span + 1 ) / 2) - 1) * -increment);;
        }


        for (int i = init_val; i != dir[1]; i+= increment) {
            dirs[counter] = new int[]{dir[0], i};
            counter += 1;
        }

        dirs[dirs.length - 1] = dir;
        // print directions
        // rc.setIndicatorString("dirs: " + dirs[0][0] + " " + dirs[0][1] + " " + dirs[1][0] + " " + dirs[1][1] + " " + dirs[2][0] + " " + dirs[2][1] + " " + dirs[3][0] + " " + dirs[3][1] + " " + dirs[4][0] + " " + dirs[4][1] + " | " + (center.x - cur.x) + " " + (center.y - cur.y) + " | " + center.x + " " + center.y + " | " + cur.x + " " + cur.y);

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
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(1, rc.getTeam());
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

    public int locationToInt(MapLocation loc) {
        return 64 * loc.x + loc.y;
    }

    public RANK[] initializeRankMap() {
        RANK[] map = new RANK[64];
        RANK[] ranks = RANK.values();
        for (int i = 0; i < ranks.length; i++) {
            map[ranks[i].getValue()] = ranks[i];
        }
        return map;
    }

    public RANK getRank(int index) {
        return rank_map[index];
    }
}
