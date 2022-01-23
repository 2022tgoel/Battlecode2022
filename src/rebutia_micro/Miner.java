package rebutia_micro;

import battlecode.common.*;
import java.util.*;

public class Miner extends Unit {
    enum MODE {
        EXPLORING,
        MINE_DISCOVERED,
        MINING,
        FLEEING;
    }

    
    int round_num;
    MODE mode;

    int[] exploratoryDir;
    private MapLocation exploratoryTarget;

    private MapLocation miningSpot;
    private MapLocation target;
    static MinerNav mNav;
    private boolean isBroadcast; //whether the target you are pursuing was taken off broadcast
    private int[] fleeDirection;
    RANK rank;
    private int stopFleeingRound = 10000;
    private MapLocation deposit;
    
	public Miner(RobotController rc) throws GameActionException {
        super(rc);
        exploratoryTarget = calcExploreDirs(7);
        rank = findRankMiner();
        mNav = new MinerNav(rc);
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
                        // moveInDirection(exploratoryDir);
                        if (adjacentToEdge()) {
                            // rc.setIndicatorString("moving away");
                            exploratoryTarget = calcExploreDirs(7);
                        }
                        else {
                            moveToLocation(exploratoryTarget);
                        }
                        break;
                    case MINE_DISCOVERED:
                        if (miningSpot != null) {
                            rc.setIndicatorLine(rc.getLocation(), miningSpot, 255, 0, 255);
                            moveToLocation(miningSpot);
                        }
                        else {
                            rc.setIndicatorLine(rc.getLocation(), target, 0, 0, 255);
                            moveToLocation(target);
                        }
                        break;
                    case FLEEING:
                        moveInDirection(fleeDirection);
                        exploratoryTarget = getExploratoryTarget();
                        break;
                }
                senseArchon();
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

    public MapLocation findMiningSpot(MapLocation target) throws GameActionException {
        int minRubble = 10000;
        int rubble;
        MapLocation[] leadLocs = rc.senseNearbyLocationsWithLead(target, 2, 2);
        MapLocation bestSpot = null;
        for (MapLocation leadLoc : leadLocs) {
            MapLocation spot = mNav.findBestSquare(leadLoc, minRubble);
            if (spot != null) {
                rubble = rc.senseRubble(spot);
                if (rubble < minRubble) {
                    minRubble = rubble;
                    bestSpot = spot;
                    deposit = leadLoc;
                }
            }
        }
        return bestSpot;
    }

    private MapLocation getExploratoryTarget() {
        int randx = rng.nextInt(rc.getMapWidth());
        int randy = rng.nextInt(rc.getMapHeight());
        return new MapLocation(randx, randy);
    }

    public RANK findRankMiner() throws GameActionException{
        return RANK.DEFAULT;
    }

    public MODE getMode(int amountMined) throws GameActionException {
        int[] potFleeDirection = enemiesDetected();
        // if you just escaped an enemy, explore in a new direction
        if (potFleeDirection == null && stopFleeingRound == round_num) exploratoryTarget = calcExploreDirs(7);
        
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
            if (miningSpot == null && rc.getLocation().distanceSquaredTo(target) <= 9) {
                miningSpot = findMiningSpot(target);
                return MODE.MINE_DISCOVERED;
            }
            else {
                if (deposit != null) {
                    if (rc.canSenseLocation(deposit)) {
                        if (getValue(deposit) <= 1) {
                            deposit = null;
                            miningSpot = findMiningSpot(target);
                        }
                    }
                }
            }
            return MODE.MINE_DISCOVERED;
        }
        else {
            miningSpot = null;
            deposit = null;
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
            MapLocation enemy_center = new MapLocation((int)cxm, (int)cym);
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
                if (my.distanceSquaredTo(dest) < 300 && res > maxRes && demand > 0) {
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
        for (MapLocation loc : rc.senseNearbyLocationsWithGold(RobotType.MINER.actionRadiusSquared)) {
            // Notice that the Miner's action cooldown is very low.
            // You can mine multiple times per turn!
            while (rc.canMineGold(loc)) {
                rc.mineGold(loc);
                amountMined += goldToLeadConversionRate;
            }
        }
        //then go to lead
        for (MapLocation loc : rc.senseNearbyLocationsWithLead(RobotType.MINER.actionRadiusSquared)) {
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
