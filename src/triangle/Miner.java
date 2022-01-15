package triangle;

import battlecode.common.*;
import java.util.*;

public class Miner extends Unit {
    enum MODE {
        EXPLORING,
        MINE_DISCOVERED,
        MINING,
        FLEEING;
    }

    int[] exploratoryDir;
    int round_num;
    MODE mode;
    private MapLocation target;
    private int[] fleeDirection;
    RANK rank;
    private int stopFleeingRound = 10000;
    
	public Miner(RobotController rc) throws GameActionException {
        super(rc);
        exploratoryDir = getExploratoryDir(7);
        rank = findRankMiner();
    }
    @Override
    public void run() throws GameActionException {
        round_num = rc.getRoundNum();
        updateCount();
        int amountMined = mine();
        mode = getMode(amountMined);
        switch (rank) {
            case DEFAULT:
                switch (mode) {
                    case EXPLORING:
                        moveInDirection(exploratoryDir);
                        break;
                    case MINE_DISCOVERED:
                        fuzzyMove(target);
                        break;
                    case FLEEING:
                        moveInDirection(fleeDirection);
                        break;
                }
                senseArchon();
                if (adjacentToEdge()) {
                    exploratoryDir = getExploratoryDir(7);
                }
                break;
            default:
                break;
        }
        amountMined+=mine();
        senseMiningArea();
        rc.setIndicatorString(" " + mode + " " + amountMined + " " + target);
    }

    public RANK findRankMiner() throws GameActionException{
        return RANK.DEFAULT;
    }

    public MODE getMode(int amountMined) throws GameActionException {
        int[] potFleeDirection = enemiesDetected();
        // if you just escaped an enemy, explore in a new direction
        if (potFleeDirection == null && stopFleeingRound == round_num) exploratoryDir = getExploratoryDir(7);
        
        // if there are enemies nearby or there were recently, flee
        if (potFleeDirection != null || stopFleeingRound <= round_num) {
            // fleeDirection is official.
            if (potFleeDirection != null) fleeDirection = potFleeDirection;
            // keep fleeing for two moves (2 rounds per move)
            if (stopFleeingRound <= round_num) {
                stopFleeingRound = round_num + 4;
            }
            return MODE.FLEEING;
        }
        //if you're getting lead here, stay put
        if (amountMined >=4){
            return MODE.MINING;
        }

        if (target!=null) {
            if (rc.canSenseLocation(target) && getValue(target) <= 1){
                target =null;
            } 
            else if (!rc.canSenseLocation(target) && !isMiningArea(target)){
                target=null;
            }
            else return MODE.MINE_DISCOVERED; // continue going to it
        }
        //found another, go there
        MapLocation loc = findMiningArea();
        if (loc!=null){
            target = loc;
            return MODE.MINE_DISCOVERED; 
        }
        //
        return MODE.EXPLORING;
    }
    /*
    public boolean mining_detour() throws GameActionException {
        MapLocation cur = rc.getLocation();
        if (minersAdjacentToLocation(cur)) {
            return false;
        } 
        if (minedAmount < 4){
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
        */
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

    public int getValue(MapLocation loc) throws GameActionException{
        return rc.senseGold(loc) * goldToLeadConversionRate + rc.senseLead(loc);
    }

    public boolean isMiningArea(MapLocation loc) throws GameActionException{
        for (int i = 0; i < 5; i++){
            int data = rc.readSharedArray(CHANNEL.MINING1.getValue()+i);
            if (data != 0) {
                int x = data / 64;
                int y = data % 64;
                MapLocation dest = new MapLocation(x, y);
                if (dest.equals(loc)) return true; //within range
            }
        }
        return false;
    }
    /**
     * findMiningArea()
     * Returns the location of the most lucrative mining area outside of mining radius
     **/
    public MapLocation findMiningArea() throws GameActionException{
        //look at the channels
        for (int i = 0; i < 5; i++){
            int data = rc.readSharedArray(CHANNEL.MINING1.getValue()+i);
            if (data != 0) {
                int x = data / 64;
                int y = data % 64;
                MapLocation dest = new MapLocation(x, y);
                if (rc.getLocation().distanceSquaredTo(dest) < 200) return dest; //within range
            }
        }
        // if square only has 1 lead don't go for it.
        int maxRes = 1;
        MapLocation[] goldLocs = rc.senseNearbyLocationsWithGold();
        MapLocation[] leadLocs = rc.senseNearbyLocationsWithLead();
        MapLocation bestLocation = null;
        
        for (MapLocation loc: goldLocs) {
            int res = getValue(loc);
            if (res > maxRes) {
                maxRes = res;
                bestLocation = loc;
            }
        }

        for (MapLocation loc: leadLocs) {
            int res = getValue(loc);
            if (res > maxRes) {
                maxRes = res;
                bestLocation = loc;
            }
        }

        // if our best location is outside of the mining range, return it
        if (maxRes >=1 && bestLocation != null) {
            return bestLocation;
        }

        
        return null;
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
