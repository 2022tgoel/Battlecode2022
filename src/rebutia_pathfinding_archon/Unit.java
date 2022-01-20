package rebutia_pathfinding_archon;

import battlecode.common.*;
import java.util.*;
// shared code across the units
public class Unit{

    RobotController rc;
    int archon_index = -1;
    RANK[] rank_map = initializeRankMap();
    final Random rng = new Random();
    static final int goldToLeadConversionRate = 200;
    static final int minerToLeadRate = 250;
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

    static Navigation mover;

    public Unit(RobotController robotController) throws GameActionException {
        rc = robotController;
        rng.setSeed((long) rc.getID() + seed_increment);
        homeArchon = findHomeArchon();
        initializeRankMap();
        mapArea = getMapArea();
        mover = new Navigation(rc);
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
            moveToLocation(target);
            return true;
        }
        else {
            archon_index = -1; 
            return false; //no longer there
        }
    }

    public int numFriendlyMiners(){
        RobotInfo[] allies =  rc.senseNearbyRobots(-1, rc.getTeam());
        int c = 0;
        for (RobotInfo r : allies){
            if (r.type == RobotType.MINER) c++;
        }
        return c;
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
        if (value >=25){
            MapLocation dest = new MapLocation(cx/value, cy/value);
            // demand disabled for now
            int demand = value/minerToLeadRate - numFriendlyMiners();
            if (demand > 0) {
                broadcastMiningArea(dest, demand); 
                return true;
            }
        }
        return false;
    }
    
    public void broadcastMiningArea(MapLocation loc, int demand) throws GameActionException{
        int indToPut = 0; // where to put the archon (if all spots are filled, it will be put at 0)
        //fuzzy location
        int x_loc= Math.min( (int)Math.round((double)loc.x/4.0) , 15);
        int y_loc= Math.min( (int)Math.round((double)loc.y/4.0) , 15);
        for (int i= 0; i < 5; i++){
            int data = rc.readSharedArray(CHANNEL.MINING1.getValue() + i);
            int x = (data >> 4) & 15;
            int y = data & 15;
            if (x_loc == x && y_loc == y) {
                return;
            }
            if (data == 0){
                indToPut = i;
            }
        }
        int value = (demand << 8) + (x_loc << 4) + y_loc; 
        rc.setIndicatorDot(new MapLocation(x_loc*4, y_loc*4), 255, 0, 0);
        System.out.println("Broadcasting miner request " + x_loc*4 + " " + y_loc*4 + " " + demand + " " + rc.getRoundNum());
        rc.writeSharedArray(CHANNEL.MINING1.getValue() +indToPut, value);
    }

    public MapLocation findNearestArchon() throws GameActionException {
        int min_dist = Integer.MAX_VALUE;
        MapLocation closest = null;
        for (int i = 0; i < 4; i++) {
            int data = rc.readSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + i);
            if (data != 0) {
                int w = data / 4096;
                int x = (data - w * 4096) / 64;
                int y = data % 64;
                MapLocation loc = new MapLocation(x, y);
                int dist = loc.distanceSquaredTo(rc.getLocation());
                if (dist < min_dist) {
                    min_dist = dist;
                    closest = loc;
                }
            }
        }
        return closest;
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
        Direction d= mover.getBestDir(loc);
        System.out.println(d);
        if (d!=null && rc.canMove(d)){
            rc.move(d);
        }
       // fuzzyMove(loc); // best pathfinding strat 
    }

    public void moveInDirection(int[] toDest) throws GameActionException{
        MapLocation loc = rc.getLocation();
        MapLocation dest = new MapLocation(loc.x + toDest[0], loc.y + toDest[1]);
        Direction d= mover.getBestDir(dest);
        System.out.println(d);
        if (d!=null && rc.canMove(d)){
            rc.move(d);
        }
      //  fuzzyMove(dest);
        // rc.setIndicatorString("I JUST MOVED TO " + toDest[0] + " " + toDest[1]);
    }

    public MapLocation scaleToEdge(int[] toDest){
        MapLocation loc = rc.getLocation();
        //scaling this directional vector so that it reaches the edge 
        double scaleFactor = 1000;
        if (toDest[0] > 0){
            scaleFactor = Math.min(scaleFactor, ((double) (rc.getMapWidth() - 1 - loc.x))/((double) toDest[0]));
        }
        else if (toDest[0] < 0){
            scaleFactor = Math.min(scaleFactor, ((double) (loc.x))/((double) -1*toDest[0]));
        }
        if (toDest[1] > 0){
            scaleFactor = Math.min(scaleFactor, ((double) (rc.getMapHeight() - 1 - loc.y))/((double) toDest[1]));
        }
        else if (toDest[1] < 0) {
            scaleFactor = Math.min(scaleFactor, ((double) (loc.y))/((double) -1*toDest[1]));
        }
        assert(scaleFactor >=0);
        int nx = loc.x + (int) (scaleFactor * (double) toDest[0]);
        nx = Math.min(nx, rc.getMapWidth() - 1);
        nx = Math.max(nx, 0);
        int ny = loc.y + (int) (scaleFactor * (double) toDest[1]);
        ny = Math.min(ny, rc.getMapHeight() - 1);
        ny = Math.max(ny, 0);
        return new MapLocation(nx, ny);
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

    public int[] flip(int[] dir){//directional vector input
        assert(dir.length == 2);
        MapLocation loc = rc.getLocation();
        int[] ret = new int[2];
        ret[0] = dir[0];
        ret[1] = dir[1];
        if (loc.x < 5 && ret[0] <0){
            ret[0] = -1*ret[0];
        } 
        if (loc.y < 5 && ret[1] <0){
            ret[1] = -1*ret[1];
        }
        if (rc.getMapWidth()- loc.x < 5 && ret[0]>0){
            ret[0] = -1*ret[0];
        }
        if (rc.getMapHeight() - loc.y < 5 && ret[1]>0){
            ret[1] = -1*ret[1];
        }
        return ret;
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

    public void broadcastTarget(MapLocation enemy) throws GameActionException {
        int indToPut = 0; // where to put the archon (if all spots are filled, it will be put at 0)
        //fuzzy location
        int x_loc= Math.min( (int)Math.round((double)enemy.x/4.0) , 15);
        int y_loc= Math.min( (int)Math.round((double)enemy.y/4.0) , 15);
        for (int i= 0; i < CHANNEL.NUM_TARGETS; i++){
            int data = rc.readSharedArray(CHANNEL.TARGET.getValue() + i);
            int x = (data >> 4) & 15;
            int y = data & 15;
            if (x_loc == x && y_loc == y) {
                return;
            }
            if (data == 0){
                indToPut = i;
            }
        }
        int value = (x_loc << 4) + y_loc; 
        rc.setIndicatorDot(new MapLocation(x_loc*4, y_loc*4), 0, 100, 0);
        rc.writeSharedArray(CHANNEL.TARGET.getValue() + indToPut, value);
        System.out.println("I broadcasted an enemy at " + enemy.toString());
    }
}
