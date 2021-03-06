package triangle;

import battlecode.common.*;
import java.util.*;
// shared code across the units
public class Unit{

    RobotController rc;
    int archon_index = -1;
    RANK[] rank_map = initializeRankMap();
    final Random rng = new Random();
    static final int goldToLeadConversionRate = 200;
    int seed_increment = 4;
    MapLocation homeArchon;
    public MapLocation archon_target;
    public int mapArea;
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
        rng.setSeed((long) rc.getID() + seed_increment);
        homeArchon = findHomeArchon();
        initializeRankMap();
        mapArea = getMapArea();
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
                int x = data / 64;
                int y = data % 64;
                archon_target = new MapLocation(x, y);
                return true;
            }
        }
        for (int i = 0; i < 4; i++) {
            int data = rc.readSharedArray(CHANNEL.ARCHON_LOC_1.getValue() + i);
            if (data != 0) {
                rc.setIndicatorString("archon found UWU2 " + archon_index);
                archon_index = i;
                assert(archon_index != -1);
                int x = data / 64;
                int y = data % 64;
                archon_target = new MapLocation(x, y);
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
                    archon_target = bot.location;
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


    public boolean senseMiningArea() throws GameActionException {
        int value = 0;
        int cx = 0;
        int cy = 0;
        for (MapLocation loc : rc.senseNearbyLocationsWithGold()) {
            int margin = rc.senseGold(loc)*goldToLeadConversionRate;
            value+=margin;
            cx+=margin*loc.x;
            cy+=margin*loc.y;
        }
        for (MapLocation loc : rc.senseNearbyLocationsWithLead()) {
            int margin = rc.senseLead(loc)-1;
            value+=margin;
            cx+=margin*loc.x;
            cy+=margin*loc.y;
        }
        if (value >=75){ 
            MapLocation dest = new MapLocation(cx/value, cy/value);
         //   System.out.println(dest);
            broadcastMiningArea(dest);
            return true;
        }
        return false;
    }
    
    public void broadcastMiningArea(MapLocation loc) throws GameActionException{
        //check that the loc is not already broadcasted
        int indToPut = 0; // where to put the archon (if all spots are filled, it will be put at 0)
        for (int i= 0; i < 5; i++){
            int data = rc.readSharedArray(CHANNEL.MINING1.getValue() + i);
            int x = data / 64;
            int y = data % 64;
            if (loc.x == x && loc.y == y) {
                return;
            }
            if (data == 0){
                indToPut = i;
            }
        }
        MapLocation dest = new MapLocation(Math.min((int)Math.round((double)loc.x/7.0)*7, rc.getMapWidth()-1),
                                          Math.min((int)Math.round((double)loc.y/7.0)*7, rc.getMapHeight()-1));//rounding each value to multiples of seven - it's a fuzzy location!
        int loc_int = locationToInt(dest); 
        rc.writeSharedArray(CHANNEL.MINING1.getValue() +indToPut, loc_int);
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

    public int[] getExploratoryDir() {
        return getExploratoryDir(5, new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2));
    }

    public int[] getExploratoryDir(int span) {
        return getExploratoryDir(span, new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2));
    }

    public int[] getExploratoryDir(int span, MapLocation loc) {
        // presumes span is odd.
        int[] dir;
        MapLocation cur = rc.getLocation();
        if (loc.x - cur.x > 0) {
            if (loc.y - cur.y > 0) {
                dir = new int[]{8,8};
            } else {
                dir = new int[]{8,-8};
            }
        } else {
            if (loc.y - cur.y > 0) {
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

    public RANK findRank() throws GameActionException {
        rc.setIndicatorString("READY TO READ");
        int data;
        // check all channels to see if you've received a rank
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                data = rc.readSharedArray(CHANNEL.SEND_RANKS1.getValue());
            }
            else {
                data = rc.readSharedArray(CHANNEL.SEND_RANKS2.getValue());
            }

            int status = data / 4096;
            int x = (data - (4096 * status)) / 64;
            int y = (data - (4096 * status) - (x * 64));

            // only set rank if instructed to.
            if (homeArchon.equals(new MapLocation(x, y))) {
                return getRank(status);
            }
        }
        return RANK.DEFAULT;
    }

    public int getMapArea() {
        return rc.getMapHeight() * rc.getMapWidth();
    }

    // should add channels for each unit...
    public void updateCount() throws GameActionException {
        RobotType r = rc.getType();
        switch (r) {
            case MINER:
                int num = rc.readSharedArray(CHANNEL.MINERS_ALIVE.getValue());
                rc.writeSharedArray(CHANNEL.MINERS_ALIVE.getValue(), num + 1);
            case SOLDIER:
                num = rc.readSharedArray(CHANNEL.SOLDIERS_ALIVE.getValue());
                rc.writeSharedArray(CHANNEL.SOLDIERS_ALIVE.getValue(), num + 1);
            case BUILDER:
                num = rc.readSharedArray(CHANNEL.BUILDERS_ALIVE.getValue());
                rc.writeSharedArray(CHANNEL.BUILDERS_ALIVE.getValue(), num + 1);
            default:
                break;
            
        }
    }
}
