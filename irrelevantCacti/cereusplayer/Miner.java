package cereusplayer;

import battlecode.common.*;
import java.util.*;

public class Miner extends Unit {

    enum MODE {
        EXPLORING,
        MINE_DISCOVERED,
        FLEEING;
    }

    int[] exploratoryDir;
    MapLocation exploratoryLoc;
    double soldier_repulsion = 2.0;
    double archon_repulsion = 2.0;
    double miner_repulsion = 1.5;
    MODE mode;
    private MapLocation target;
    private int[] fleeDirection;
    
    public Miner(RobotController rc) throws GameActionException {
        super(rc);
        exploratoryLoc = chooseLoc();
        rc.setIndicatorString(" " + exploratoryLoc);
    }

    @Override
    public void run() throws GameActionException {
        if (rc.getRoundNum() % 50 == 0) {
            exploratoryDir = getExploratoryDir(7);
        }
        mode = getMode();
        switch (mode) {
            case EXPLORING:
                fuzzyMove(exploratoryLoc);
                break;
            case MINE_DISCOVERED:
                break;
        }
        if (adjacentToEdge()) {
            exploratoryDir = getExploratoryDir(7);
        }
        // rc.setIndicatorString("exploratoryDir: " + exploratoryDir[0] + " " + exploratoryDir[1]);
        senseArchon();
    }

    public MODE getMode() throws GameActionException {
        if (mining_detour()) {
            return MODE.MINE_DISCOVERED;
        }
        else {
            return MODE.EXPLORING;
        }
    }

    public MapLocation chooseLoc() throws GameActionException{
        int data = rc.readSharedArray(CHANNEL.MINER_DIR.getValue());
        assert(exploreLocs.length <=16); 
        for (int i = 0; i < exploreLocs.length; i++){
            if ((data&(1<<i)) == 0){
                data = data|(1<<i);
                rc.writeSharedArray(CHANNEL.MINER_DIR.getValue(), data);
                return new MapLocation(exploreLocs[i][0], exploreLocs[i][1]);
            }
        }
        //if you reach here, need to clear
        rc.writeSharedArray(CHANNEL.MINER_DIR.getValue(), 1);
        return new MapLocation(exploreLocs[0][0], exploreLocs[0][1]);
    }

    // TODO: make this work
    public void avoid_archons() throws GameActionException {
        boolean archonDetected = detectArchon();
        if (archonDetected) {
            int data = rc.readSharedArray(archon_index);
            int x = data / 64;
            int y = data % 64;
            MapLocation enemy_archon = new MapLocation(x, y);
        }
        else {
            rc.setIndicatorString("archon not detected");
        }
    }

    public boolean mining_detour() throws GameActionException {
        MapLocation cur = rc.getLocation();
        int amountMined = mine();
        if (minersAdjacentToLocation(cur)) {
            return false;
        }
        if (amountMined < 4){
            target = findMiningArea();
            if (target != null) {
                if (!cur.equals(target)){
                    fuzzyMove(target);
                    //sense if there is a lucrative nearby area and move there instead
                    // moveToLocation(target);
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public int[] enemiesDetected() throws GameActionException {
        RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        double cxs = 0;
        double cys = 0;
        double numSoldiers = 0;
        for (RobotInfo enemy: enemies) {
            if (enemy.type == RobotType.SOLDIER) {
                cxs += enemy.location.x;
                cys += enemy.location.y;
                numSoldiers++;
            }
        }
        if (numSoldiers > 0) {
            cxs /= numSoldiers;
            cys /= numSoldiers;
            MapLocation enemy_center = new MapLocation((int)cxs, (int)cys);
            Direction d = rc.getLocation().directionTo(enemy_center).opposite();
            return new int[] {d.getDeltaX() * 5, d.getDeltaY() * 5};
        }
        return null; 
    }

    /**
     * findMiningArea()
     * Returns the location of the most lucrative mining area outside of mining radius
     **/
    public MapLocation findMiningArea() throws GameActionException{
        MapLocation cur = rc.getLocation();
        // if square only has 1 lead don't go for it.
        int maxRes = 1;
        MapLocation bestLocation = null;
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                int x_coord = cur.x + dx;
                int y_coord = cur.y + dy;
                MapLocation loc;
                if (validCoords(x_coord, y_coord)) {
                    loc = new MapLocation(x_coord, y_coord);
                }
                else {
                    continue;
                }
                if (rc.canSenseLocation(loc)) {
                    int res = rc.senseGold(loc) * goldToLeadConversionRate + rc.senseLead(loc);
                    if (res > maxRes) {
                        maxRes = res;
                        bestLocation = loc;
                    }
                }
            }
        }

        // if our best location is outside of the mining range, return it
        if (maxRes > 1 && bestLocation != null) {
            return bestLocation;
        }
        else return null;
    }

    public boolean minersAdjacentToLocation(MapLocation loc) throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots(loc, 2, rc.getTeam());
        for (RobotInfo robot : robots) {
            if (robot.type == RobotType.MINER) {
                return true;
            }
        }
        return false;
    }
    /**
     * mine() mines the surrounding area
     * @return int representing total value of what was mined
     **/
    public int mine() throws GameActionException{
        MapLocation me = rc.getLocation();
        //prioritize gold
        int amountMined = 0;
        for (MapLocation loc : rc.senseNearbyLocationsWithGold()) {
            // Notice that the Miner's action cooldown is very low.
            // You can mine multiple times per turn!
            while (rc.canMineGold(loc)) {
                rc.mineGold(loc);
                amountMined += goldToLeadConversionRate;
            }
        }
        //then go to lead
        for (MapLocation loc : rc.senseNearbyLocationsWithLead()) {
            // Notice that the Miner's action cooldown is very low.
            // You can mine multiple times per turn!
            while (rc.canMineLead(loc) && rc.senseLead(loc) > 1) {
                rc.mineLead(loc);
                amountMined+=1;
            }
        }
        return amountMined;
    }
}