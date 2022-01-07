package pathfindingplayer;

import battlecode.common.*;
import java.util.*;

public class Archon extends Unit {
    int counter = 0;
    int round_num;
    int archonNumber = -1;

    int[] build_order = chooseBuildOrder();
    int built_units = 0;
    int num_soldiers = 0;
    int num_miners = 0;
    int num_builders = 0;
    int num_archons;
    boolean PASS_ON = false;
    boolean CONSTRUCT_SPECIAL_UNIT = false;
    boolean CONSTRUCTED_SPECIAL_UNIT = false;

    // if another archon is commanding you to do something
    RANK ORDER;

    RANK unitRank;
	public Archon(RobotController rc) throws GameActionException {
        super(rc);
        archonNumber = getArchonNumber();
        num_archons = rc.getArchonCount();
    }

    @Override
    public void run() throws GameActionException {
        round_num = rc.getRoundNum();
        if (round_num % num_archons != archonNumber) {
            return;
        }
        // refresh the posted rank with each bot
        clearRanks();
        ORDER = getOrder();

        // If you recieved an order, then set the rank of the current unit to the order
        if (ORDER != RANK.DEFAULT) {
            unitRank = ORDER;
        }
        else {
            unitRank = specialUnit();
        }

        // if you have an order or a special unit you want to build then do it
        Direction[] dirs = sortedDirections();
        if (unitRank != RANK.DEFAULT) {
            // Pick a direction to build in.
            for (Direction dir: dirs) {
                if (unitRank == RANK.CONVOY_LEADER) {
                    // rc.setIndicatorString("Trying to build a CONVOY LEADER");
                    if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                        rc.setIndicatorString("I BUILT A CONVOY LEADER");
                        rc.buildRobot(RobotType.SOLDIER, dir);
                        postRank(RANK.CONVOY_LEADER);
                        num_soldiers += 1;
                        CONSTRUCTED_SPECIAL_UNIT = true;
                        break;
                    }
                }
            }
        }

        // if you received an order and you couldn't build it, or if you couldn't build a special unit and wanted to, send another order
        if ((unitRank != RANK.DEFAULT && CONSTRUCT_SPECIAL_UNIT == true && PASS_ON) || (ORDER != RANK.DEFAULT && !CONSTRUCTED_SPECIAL_UNIT)) {
            if (ORDER != RANK.DEFAULT) {
                rc.setIndicatorString("ORDERING A " + ORDER.toString());
                postOrder(ORDER);
            }
            else {
                rc.setIndicatorString("ORDERING A " + unitRank.toString());
                postOrder(unitRank);
            }
        }

        // if you received an order and you couldn't build it, DON't WASTE RESOURCES
        if (ORDER == RANK.DEFAULT) {
            for (Direction dir: dirs) {
                if (built_units < build_order[counter % 3]) {
                    switch (counter % 3) {
                        case 0:
                            // rc.setIndicatorString("Trying to build a miner" + " built_units: " + built_units + " " + counter);
                            if (rc.canBuildRobot(RobotType.MINER, dir)) {
                                rc.buildRobot(RobotType.MINER, dir);
                                built_units++;
                                num_miners++;
                            }
                            break;
                        case 1:
                            //  rc.setIndicatorString("Trying to build a soldier");
                            buildSoldier(dir);
                            break;
                        case 2:
                            // rc.setIndicatorString("Trying to build a builder");
                            if (rc.canBuildRobot(RobotType.BUILDER, dir)) {
                                rc.buildRobot(RobotType.BUILDER, dir);
                                built_units++;
                                num_builders++;
                            }
                            break;
                    }
                }
                else {
                    counter++;
                    built_units = 0;
                }
            }
        }
        attemptHeal();
        turn_update();
        CONSTRUCT_SPECIAL_UNIT = false;
        CONSTRUCTED_SPECIAL_UNIT = false;
        // rc.setIndicatorString("BUILDING RANK: " + unitRank.toString());
        // rc.setIndicatorString("archonNumber: " + archonNumber);
    }

    public RANK specialUnit() {
        if (rc.getRoundNum() % 50 == 0 ) {
            CONSTRUCT_SPECIAL_UNIT = true;
            PASS_ON = true;
            return RANK.CONVOY_LEADER;
        }
        else {
            return RANK.DEFAULT;
        }
    }

    public void postOrder(RANK order) throws GameActionException {
        rc.setIndicatorString("ORDERING A " + ORDER.toString() + " ON CHANNEL " + CHANNEL.ORDERS.getValue());
        rc.writeSharedArray(CHANNEL.ORDERS.getValue(), order.getValue());
    }

    public RANK getOrder() throws GameActionException {
        // rc.setIndicatorString("READING ORDERS FROM CHANNEL " + CHANNEL.ORDERS.getValue());
        int data = rc.readSharedArray(CHANNEL.ORDERS.getValue());
        // rc.setIndicatorString("DATA RECIEVED ");
        if (data == 0) {
            return RANK.DEFAULT;
        }

        clearOrder();

        if (data == 1) {
            return RANK.CONVOY_LEADER;
        }
        else {
            return RANK.DEFAULT;
        }
    }

    public void clearOrder() throws GameActionException {
        rc.writeSharedArray(CHANNEL.ORDERS.getValue(), 0);
    }

    public void postRank(RANK rank) throws GameActionException {
        MapLocation loc = rc.getLocation();
        int loc_int;
        if (rank == RANK.DEFAULT) {
            return;
        }
        else {
            loc_int = rank.getValue() * 10000 + loc.x * 100 + loc.y;
            if (round_num % 2 == 0) {
                rc.writeSharedArray(CHANNEL.SEND_RANKS1.getValue(), loc_int);
            }
            else {
                rc.writeSharedArray(CHANNEL.SEND_RANKS2.getValue(), loc_int);
            }
        }
    }

    public void clearRanks() throws GameActionException {
        if (round_num % 2 == 0) {
            rc.writeSharedArray(CHANNEL.SEND_RANKS1.getValue(), 0);
        }
        else {
            rc.writeSharedArray(CHANNEL.SEND_RANKS2.getValue(), 0);
        }
    }
    public void buildSoldier(Direction dir) throws GameActionException{ 
        if (!rc.canBuildRobot(RobotType.SOLDIER, dir)) return;
        //1. find num soliders nearby
        int s =0;
        for (RobotInfo r :rc.senseNearbyRobots(RobotType.ARCHON.visionRadiusSquared, rc.getTeam()) ){
            if (r.type == RobotType.SOLDIER){
                s++;
            }
        }
        
        // 2. choose defnesive or offensive
        if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
            rc.buildRobot(RobotType.SOLDIER, dir);
            built_units++;
            if (s <= 40) {
                postRank(RANK.DEFENDER);
                rc.setIndicatorString("BUILDING A DEFENSIVE SOLDIER");
            } 
        }
    }

    public void clearArchonNumbers() throws GameActionException {
        // if you don't read all 0s for the first four numbers, set them to zero.
        for (int i = 0; i < 4; i++) {
            if (rc.readSharedArray(i) != 0) {
                rc.writeSharedArray(i, 0);
            }
        }
    }

    public int getArchonNumber() throws GameActionException {
        int data;
        for (int i = 0; i < 4; i++) {
            data = rc.readSharedArray(i);
            if (data == 0){
                rc.writeSharedArray(i, 1);
                if (i == rc.getArchonCount() - 1) {
                    clearArchonNumbers();
                }
                return i;
            }
        }
        return -1;
    }
    /**
     * enemySoldiersInRange() checks if a soldier that might want to attack in nearby
     **/
    public boolean enemySoldiersInRange() throws GameActionException{
        for (RobotInfo r :rc.senseNearbyRobots(RobotType.ARCHON.visionRadiusSquared, rc.getTeam().opponent()) ){
            if (r.type == RobotType.SOLDIER){
                return true;
            }
        }
        return false;
    }
    /**
     * fortify() builds the builders so that they can build the watchtowers (in a designated formation for now)
     **/
    public void fortify() throws GameActionException{
        Direction[] dirs = {Direction.NORTHEAST,Direction.SOUTHEAST, Direction.SOUTHWEST,Direction.NORTHWEST,}; //directions to build builders in
        MapLocation my = rc.getLocation();
        for (int i =0 ;i < dirs.length; i++){
            MapLocation builderLocation  = my.add(dirs[i]);
            if (rc.canBuildRobot(RobotType.BUILDER, dirs[i])){
                rc.buildRobot(RobotType.BUILDER, dirs[i]);
                num_builders++;
            }
        }
    }

    public Direction[] sortedDirections() throws GameActionException {
        Direction[] dirs = {Direction.NORTH, Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST, Direction.SOUTH, Direction.SOUTHWEST, Direction.WEST, Direction.NORTHWEST};
        Arrays.sort(dirs, (a,b) -> getRubble(a) - getRubble(b));
        return dirs;
    }

    public int getRubble(Direction d) {
        try {
            MapLocation loc = rc.getLocation();
            return rc.senseRubble(loc.add(d));
        } catch (GameActionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    public int[] chooseBuildOrder() {
        int mapArea = rc.getMapHeight() * rc.getMapHeight();
        if (mapArea < 1400) {
            return new int[]{2, 3, 0}; // miners, soldiers, builders
        }
        else if (mapArea < 2200) {
            return new int[]{2, 2, 0}; // miners, soldiers, builders
        }
        else {
            return new int[]{3, 4, 0}; // miners, soldiers, builders
        }
    }

    public void attemptHeal() throws GameActionException {
        boolean soldiers_home = false;
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(RobotType.ARCHON.actionRadiusSquared, rc.getTeam());
        // if there are any nearby enemy robots, attack the one with the least health
        if (nearbyBots.length > 0) {
            RobotInfo weakestBot = nearbyBots[0];
            for (RobotInfo bot : nearbyBots) {
                if (bot.type == RobotType.SOLDIER)
                    if (bot.health < weakestBot.health) {
                        weakestBot = bot;
                    }
                    soldiers_home = true;
            }
            if (soldiers_home) {
                if (rc.canRepair(weakestBot.location)) {
                  //  rc.setIndicatorString("Succesful Heal!");
                    rc.repair(weakestBot.location);
                }
            }
            else {
                for (RobotInfo bot : nearbyBots) {
                    if (bot.type == RobotType.MINER)
                        if (bot.health < weakestBot.health) {
                            weakestBot = bot;
                        }
                }
                if (rc.canRepair(weakestBot.location)) {
                    rc.repair(weakestBot.location);
                }
            }
        }
    }
}


