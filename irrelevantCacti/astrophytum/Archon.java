package astrophytum;

import battlecode.common.*;
import java.util.*;

public class Archon extends Unit {

    enum MODE {
        DEFAULT,
        THREATENED,
        OTHER_THREATENED,
        ;
    }

    int round_num;
    int archonNumber = -1;
    int archons_dead = 0;

    int[] build_order;
    Direction[] dirs;

    int built_units = 0;
    int counter = 0;
    int num_soldiers = 0;
    int num_miners = 0;
    int num_builders = 0;
    int num_archons_init;
    int num_archons_alive;
    int mapArea = getMapArea();

    public Archon(RobotController rc) throws GameActionException {
        super(rc);
        System.out.println("here");
        archonNumber = getArchonNumber();
        num_archons_alive = rc.getArchonCount();
        num_archons_init = num_archons_alive;
        build_order = chooseBuildOrder();
        dirs = sortedDirections();
    }
    
    @Override
    public void run() throws GameActionException {
        round_num = rc.getRoundNum();
        
        if (round_num == 250){
          //  rc.resign();
        }
        
        // handles all the logistics for when an archon dies
        checkDead();

        if (round_num % num_archons_alive != archonNumber) {
            rc.setIndicatorString("ARCHON NUM: " + archonNumber);
            return;
        }

        clearDRush();
        
        MODE mode = determineMode();

        switch (mode) {
            case THREATENED:
                sendThreatAlert();
                for (Direction dir: dirs) {
                    buildSoldier(dir);
                }
                break;
            case OTHER_THREATENED:
                /* TODO
                if (rc.getTeamLeadAmount(rc.getTeam()) < 5000){
                    break;
                }*/
            default:
                for (Direction dir: dirs) {
                    int sizeBracket = (int) Math.ceil((double) mapArea / 1000);

                    if (num_miners < (sizeBracket*6)/num_archons_init){
                        if (rc.canBuildRobot(RobotType.MINER, dir)) {
                            rc.buildRobot(RobotType.MINER, dir);
                            num_miners++;
                            break;
                        }
                        
                    }

                    switch (counter % 3) {
                        case 0:
                            rc.setIndicatorString("Trying to build a miner" + " built_units: " + built_units + " " + build_order[counter % 3]);
                            buildMiner(dir);
                            break;
                        case 1:
                            rc.setIndicatorString("Trying to build a soldier" + " built_units: " + built_units + " " + build_order[counter % 3]);
                            buildSoldier(dir);
                            break;
                        case 2:
                            rc.setIndicatorString("Trying to build a builder" + " built_units: " + built_units + " " + build_order[counter % 3]);
                            buildBuilder(dir);
                            break;
                    }
                    if (built_units >= build_order[counter % 3]){
                        counter++;
                        built_units = 0;
                    }   
                }
                break;
        }

        
        attemptHeal();
        
    }

    public MODE determineMode() throws GameActionException {
        if (underThreat()) {
            return MODE.THREATENED;
        }
        if (otherUnderThreat()){
            return MODE.OTHER_THREATENED;
        }
        return MODE.DEFAULT;
    }

    public void clearDRush() throws GameActionException{
        if (round_num % 50 == 0){
            for (int i = 0; i < 4; i++) {
                rc.writeSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + i, 0);
            }
        }
        
    }

    public boolean underThreat(){
        RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        int threatLevel = 0;
        int data;
        for (RobotInfo enemy : enemies) {
            if (enemy.type == RobotType.SOLDIER) {
                threatLevel += 1;
            }
            else if (enemy.type == RobotType.SAGE) {
                threatLevel += 4;
            }
        }
        return (threatLevel >=3);
    }

    public void sendThreatAlert() throws GameActionException {
        MapLocation my = rc.getLocation();
        int dRushChannel = 0;
        for (int i = 0; i < 4; i++) {
            // rc.writeSharedArray(, value);
            int data = rc.readSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + i);
            // go through channels until you find an empty one to communicate with.
            int x = data / 64;
            int y = data % 64;
            // already alerted.
            if (x == my.x && y == my.y) {
                return;
            }
            if (data == 0) {
                dRushChannel = i;
                
            }
        }
        rc.writeSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + dRushChannel, locationToInt(my));
    }

    public boolean otherUnderThreat() throws GameActionException{
        int numThreatenedArchons = 0;
        for (int i = 0; i < 4; i++) {
            // rc.writeSharedArray(, value);
            int data = rc.readSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + i);
            // go through channels until you find an empty one to communicate with.
            if (data != 0) {
                int x = data / 64;
                int y = data % 64;
                if (validCoords(x, y)) {
                    numThreatenedArchons++;
                }
            }
        }
        return (numThreatenedArchons > 0);
    }

    public void checkDead() throws GameActionException {
        // none dead
        int archonCount = rc.getArchonCount();
        if (archonCount >= num_archons_alive) {
            return;
        }
        else {
            num_archons_alive = rc.getArchonCount();
            archons_dead += 1;
            int cur_data = rc.readSharedArray(CHANNEL.ALIVE.getValue());
            int binary_sum = 0;
            int tempData = cur_data;
            for (int i = num_archons_init - 1; i >= 0; i--) {
                if (tempData / (int) Math.pow(2.0, (double) i) == 1) {
                    binary_sum += 1;
                    tempData -= (int) Math.pow(2.0, (double) i);
                }
            }

            // if you're the last one, clear the data
            if (binary_sum == num_archons_alive - 1) {
                rc.writeSharedArray(CHANNEL.ALIVE.getValue(), 0);
            }
            else {
                int data = (int) (Math.pow(2.0, ((double) archonNumber)));
                rc.writeSharedArray(CHANNEL.ALIVE.getValue(), cur_data + data);
            }
            // if 4 archons alive, binary sum is 3, if 3 archons alive, binary sum is 2...
            archonNumber = binary_sum;
        }
    }

    public void clearArchonNumbers() throws GameActionException {
        // if you don't read all 0s for the first four numbers, set them to zero.
        for (int i = 0; i < 4; i++) {
            if ((rc.readSharedArray(CHANNEL.ARCHON_LOC_1.getValue() + i)) != 0) {
                rc.writeSharedArray(i, 0);
            }
        }
    }

    public int getArchonNumber() throws GameActionException {
        int data;
        for (int i = 0; i < 4; i++) {
            data = rc.readSharedArray(CHANNEL.ARCHON_LOC_1.getValue() + i);
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

    


    // COMMS SECTION ////////////////////////////////////////////////
    public void postOrder(RANK order) throws GameActionException {
        rc.setIndicatorString("ORDERING A " + order.toString() + " ON CHANNEL " + CHANNEL.ORDERS.getValue());
        rc.writeSharedArray(CHANNEL.ORDERS.getValue(), order.getValue());
    }

    public RANK getOrder() throws GameActionException {
        // rc.setIndicatorString("READING ORDERS FROM CHANNEL " + CHANNEL.ORDERS.getValue());
        int data = rc.readSharedArray(CHANNEL.ORDERS.getValue());
        // rc.setIndicatorString("DATA RECIEVED ");
        return RANK.DEFAULT;
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
            // all locations are within 60, so can be compressed to 6 bits.
            loc_int = rank.getValue() * 4096 + locationToInt(loc);
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
    ////////////////////////////////////////////////
    public void buildMiner(Direction dir) throws GameActionException{
        if (rc.canBuildRobot(RobotType.MINER, dir)) {
            rc.buildRobot(RobotType.MINER, dir);
            built_units++;
            num_miners++;
        }
    }

    public void buildSoldier(Direction dir) throws GameActionException{ 
        if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
            rc.buildRobot(RobotType.SOLDIER, dir);
            built_units++;
            num_soldiers++;
        }
    }

    public void buildBuilder(Direction dir) throws GameActionException {
        if (rc.canBuildRobot(RobotType.BUILDER, dir)) {
            rc.buildRobot(RobotType.BUILDER, dir);
            built_units++;
            num_builders++;
        }
    }
    /**
     * enemySoldiersInRange() checks if a soldier that might want to attack in nearby
     **/
    public boolean enemySoldiersInRange() throws GameActionException{
        for (RobotInfo r :rc.senseNearbyRobots(rc.getType().visionRadiusSquared, rc.getTeam().opponent()) ){
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

    public int distToWall(Direction d){
        MapLocation my = rc.getLocation();
        MapLocation n = my.add(d);
        int min = 61;
        min = Math.min(min, n.x);
        min = Math.min(min, n.y);
        min = Math.min(min, rc.getMapWidth() - n.x);
        min = Math.min(min, rc.getMapHeight() - n.y);
        return min;
    }

    public Direction[] sortedDirections() throws GameActionException {
        
        Direction[] dirs = {Direction.NORTH, Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST, Direction.SOUTH, Direction.SOUTHWEST, Direction.WEST, Direction.NORTHWEST};
        Arrays.sort(dirs, (a,b) -> distToWall(b) - distToWall(a));
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

    public int getMapArea(){
        return rc.getMapHeight() * rc.getMapWidth();
    }
    public int[] chooseBuildOrder() {
        if (mapArea < 1400) {
            return new int[]{1, 4, 0}; // miners, soldiers, builders
        }
        else if (mapArea < 2200) {
            return new int[]{1, 4, 0}; // miners, soldiers, builders
        }
        else {
            return new int[]{1, 4, 0}; // miners, soldiers, builders
        }
    }

    public void attemptHeal() throws GameActionException {
        boolean soldiers_home = false;
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(rc.getType().actionRadiusSquared, rc.getTeam());
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


