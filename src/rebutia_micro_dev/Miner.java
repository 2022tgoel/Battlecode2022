package rebutia_micro_dev;

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
    private boolean isBroadcast; //whether the target you are pursuing was taken off broadcast
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
        super.run();
        round_num = rc.getRoundNum();
        radio.updateCounter();
        int amountMined = mine();
        mode = getMode(amountMined);
        switch (rank) {
            case DEFAULT:
                switch (mode) {
                    case EXPLORING:
                        moveInDirection(exploratoryDir);
                        break;
                    case MINE_DISCOVERED:
                        rc.setIndicatorLine(rc.getLocation(), target, 0, 0, 255);
                        moveToLocation(target);
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
        int deposit_value = senseMiningArea();
        int num_friends = numFriendlyMiners(2);
        if (amountMined > 0) {
            if (((double) deposit_value / (double) (num_friends + 1)) >= 15) {
                radio.updateCounter(BiCHANNEL.USEFUL_MINERS);
            }
        }
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
        //check that you should still pursue
        if (target!=null) {
            if ((!isBroadcast && rc.canSenseLocation(target)) || (isBroadcast && rc.getLocation().distanceSquaredTo(target)<=10)) {//stricter distance requirements for a broadcast
                if (getValue(target) <= 1 || occupiedWithMinerAlly(target)){
                    rc.setIndicatorDot(target, 0, 255, 0);
                    target = null; 
                }
            }
        }
        //choose location to pursue
        if (target!=null){
            return MODE.MINE_DISCOVERED;
        }
        else {
            MapLocation loc = findMiningAreaWithSensing();
            if (loc!=null){
                target = loc;
                isBroadcast = false;
                return MODE.MINE_DISCOVERED; 
            }
            loc = findMiningAreaWithBroadcast();
            if (loc!=null){
                target = loc;
                isBroadcast = true;
                return MODE.MINE_DISCOVERED;            
            }
        }
        //
        return MODE.EXPLORING;
    }
    public int[] enemiesDetected() throws GameActionException {
        RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        double cxs = 0;
        double cys = 0;
        double cxm = 0;
        double cym = 0;
        double numSoldiers = 0;
        double numMiners = 0;
        for (RobotInfo enemy: enemies) {
            if (enemy.type == RobotType.SOLDIER) {
                cxs += enemy.location.x;
                cys += enemy.location.y;
                numSoldiers++;
            }
            if (enemy.type == RobotType.MINER) {
                cxm += enemy.location.x;
                cym += enemy.location.y;
                numMiners++;
            }
        }
        if (numMiners > 0) {
            cxm /= numMiners;
            cym /= numMiners;
            MapLocation enemy_center = new MapLocation((int)cxs, (int)cys);
            broadcastTarget(enemy_center);
        }
        if (numSoldiers > 0) {
            cxs /= numSoldiers;
            cys /= numSoldiers;
            MapLocation enemy_center = new MapLocation((int)cxs, (int)cys);
            broadcastTarget(enemy_center);
            Direction d = rc.getLocation().directionTo(enemy_center).opposite();
            return new int[] {d.getDeltaX() * 5, d.getDeltaY() * 5};
        }
        return null; 
    }

    public int getValue(MapLocation loc) throws GameActionException{
        return rc.senseGold(loc) * goldToLeadConversionRate + rc.senseLead(loc);
    }

    public boolean occupiedWithMinerAlly(MapLocation loc) throws GameActionException{
        if (rc.getLocation().equals(loc)) return false;
        RobotInfo r = rc.senseRobotAtLocation(loc);
        if (r!= null && r.team == rc.getTeam() && r.type == RobotType.MINER){
            return true;
        }
        else return false;
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
    
    public MapLocation findMiningAreaWithSensing() throws GameActionException{
        int maxRes = 1;
        MapLocation[] goldLocs = rc.senseNearbyLocationsWithGold();
        MapLocation[] leadLocs = rc.senseNearbyLocationsWithLead(rc.getType().visionRadiusSquared, 2);
        MapLocation bestLocation = null;
        
        for (MapLocation loc: goldLocs) {
            if (!occupiedWithMinerAlly(loc)){
                int res = getValue(loc) + rng.nextInt(5);
                if (res > maxRes) {
                    maxRes = res;
                    bestLocation = loc;
                }
            }
        }

        for (MapLocation loc: leadLocs) {
            if (!occupiedWithMinerAlly(loc)){
                int res = getValue(loc)+ rng.nextInt(5);
                if (res > maxRes) {
                    maxRes = res;
                    bestLocation = loc;
                }
            }
        }

        // if our best location is outside of the mining range, return it
        if (maxRes >=1 && bestLocation != null) {
            return bestLocation;
        }

        return null;
    }



    static final int[] dx = {0, 0, 0, 1, 1, 1, -1,-1, -1};
    static final int[] dy = {0, 1, -1, 0, 1, -1, 0, 1, -1};
    public MapLocation findMiningAreaWithSensingIntensive() throws GameActionException{ //currently too bytecode intensive
        int maxRes = 1;
        MapLocation bestLocation = null;
        
        MapLocation my = rc.getLocation();
        //look at surrounding area
        MapLocation[] goldLocs = rc.senseNearbyLocationsWithGold();
        MapLocation[] leadLocs = rc.senseNearbyLocationsWithLead(rc.getType().visionRadiusSquared, 2);
        
        int[][] value = new int[9][9];
        for (MapLocation loc: goldLocs) {
            int x = (loc.x-my.x)+4;
            int y = (loc.y-my.y)+4;
            assert(x >= 0 && x < 9 && y>=0 && y < 9);
            int upd = rc.senseGold(loc)*goldToLeadConversionRate;
            for (int i= 0; i < dx.length; i++){
                int nx = x + dx[i];
                int ny = y + dy[i];
                
                if (nx >=0 && nx < 9 && ny >=0 && ny < 9) value[nx][ny] += upd;
            }
            
        }

        for (MapLocation loc: leadLocs) {
            int x = (loc.x-my.x)+4;
            int y = (loc.y-my.y)+4;
            assert(x >= 0 && x < 9 && y>=0 && y < 9);
            int upd = rc.senseLead(loc) - 1;
            for (int i= 0; i < dx.length; i++){
                int nx = x + dx[i];
                int ny = y + dy[i];
                
                if (nx >=0 && nx < 9 && ny >=0 && ny < 9) value[nx][ny] += upd;
            }
        }

        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                MapLocation loc = new MapLocation(my.x + (i -4), my.y +(j-4));
                if (value[i][j] > maxRes) {
                    maxRes = value[i][j];
                    bestLocation = loc;
                }
            }
        }

        if (maxRes >=1 && bestLocation != null) {
            return bestLocation;
        }

        return null;
    }

    public MapLocation findMiningAreaWithBroadcast() throws GameActionException{
        int maxRes = 1;
        MapLocation bestLocation = null;
        int channel = -1;
        
        MapLocation my = rc.getLocation();

        //look at the channels
        for (int i = 0; i < 5; i++){
            int data = rc.readSharedArray(CHANNEL.MINING1.getValue()+i);
            if (data != 0) {
                int demand = (data >> 8) & 255;
                int x = (data >> 4) & 15;
                int y = data & 15;
                MapLocation dest = new MapLocation(Math.min(x*4, rc.getMapWidth() - 1), Math.min(y*4, rc.getMapHeight() - 1));
             //   System.out.println("Recieved miner request: " + x*4 + " " + y*4 + " " + demand);
                int res = minerToLeadRate*demand;
                if (my.distanceSquaredTo(dest) < 300 && res > maxRes && demand > 0) { //within range //TODO: add not fulfilled
                    maxRes = res;
                    bestLocation = dest; 
                    channel = i;
                }
            }
        }

        if (maxRes >=1 && bestLocation != null) {
            //reduce demand
            int data = rc.readSharedArray(CHANNEL.MINING1.getValue()+channel);
            int demand = ((data >> 8) & 255) - 1;
            int x = (data >> 4) & 15;
            int y = data & 15;
          //  System.out.println("Taking miner request: " + x*4 + " " + y*4 + " " + demand);
            rc.writeSharedArray(CHANNEL.MINING1.getValue()+channel,(demand << 8) + (x << 4) + y);
            return bestLocation;
        }

        return null;
    }
    /**
     * mine() mines the surrounding area
     * @return int representing total value of what was mined
     **/
    public int mine() throws GameActionException{
        //prioritize gold
        int amountMined = 0;
        for (MapLocation loc : rc.senseNearbyLocationsWithGold(1)) {
            // Notice that the Miner's action cooldown is very low.
            // You can mine multiple times per turn!
            while (rc.canMineGold(loc)) {
                rc.mineGold(loc);
                amountMined += goldToLeadConversionRate;
            }
        }
        //then go to lead
        for (MapLocation loc : rc.senseNearbyLocationsWithLead(1)) {
            // Notice that the Miner's action cooldown is very low.
            // You can mine multiple times per turn;
            while (rc.canMineLead(loc) && rc.senseLead(loc) > 1) {
                rc.mineLead(loc);
                amountMined+=1;
            }
        }
        return amountMined;
    }
}
