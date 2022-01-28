package opuntia_sage_upload;

import battlecode.common.*;
import java.util.*;

public class Builder extends Unit {
    public enum MODE {
        HEALING,
        BUILD_TOWER,
        BUILD_LAB,
        REPAIRING
    }

    int travel_counter = 0;
    int[] exploratoryDir = getExploratoryDir();
    int counter = 0;
    Direction[] dirs;
    RANK rank;
    MODE mode;

    private int num_watchtowers = 0;
    private int num_labs = 0;
    private int desiredLabs = 0;
    private int[] troopCounter = { 0, 0, 0, 0, 0 }; // miner, soldier, builder, sage, watchtower
    MapLocation target = null;
    MapLocation buildLabLoc = null;
    private int built_units = 0;

    public Builder(RobotController rc) throws GameActionException {
        super(rc);
        rank = getBuilderRank();
        dirs = sortedDirections();
    }

    @Override
    public void run() throws GameActionException {
        super.run();
        radio.updateCounter();

        troopCounter = new int[] { radio.readCounter(RobotType.MINER), radio.readCounter(RobotType.SOLDIER),
                radio.readCounter(RobotType.BUILDER), 0, radio.readCounter(RobotType.WATCHTOWER), radio.readCounter(RobotType.LABORATORY) };
        System.out.println(troopCounter[0] + " " + troopCounter[1] + " " + troopCounter[2] + " " + troopCounter[3]);
        switch (rank) {
            case MARTYR:
                forTheGreaterGood();
                break;
            case DEFAULT:
                mode = getMode();
                switch (mode) {
                    case HEALING:
                        heal();
                        break;
                    case BUILD_TOWER:
                        build(new int[]{0, 1});
                        break;
                    case BUILD_LAB:
                        MapLocation my = rc.getLocation();
                        int dist= rc.getLocation().distanceSquaredTo(buildLabLoc);
                        if (dist == 0){
                            moveOff();
                        }
                        else if (dist >=1 && dist <=2){
                            int curLead = rc.getTeamLeadAmount(rc.getTeam());
                            if(curLead < RobotType.LABORATORY.buildCostLead && unitsOnMap()) {
                                System.out.println("sucess: " + troopCounter[1] + " " + troopCounter[3]);
                                boolean suc = radio.requestLead(RobotType.LABORATORY.buildCostLead);
                                break;
                            }
                            boolean suc = buildLaboratory(rc.getLocation().directionTo(buildLabLoc));
                            if(suc) {
                                radio.removeLeadRequest();
                                radio.clearLabLoc();
                            }
                        }
                        else{
                            moveToLocation(buildLabLoc);
                        }
                        break;
                    case REPAIRING:
                        if (rc.canRepair(target))
                            rc.repair(target);
                        else
                            moveToLocation(target);
                        target = null;
                        break;
                }
                break;
            default:
                break;
        }

        rc.setIndicatorString("RANK: " + rank + " MODE: " + mode + " " + buildLabLoc + " " + troopCounter[0] + " " + troopCounter[1] + " " + troopCounter[3] + " " + radio.readLeadRequest());
    }

    public MODE getMode() throws GameActionException {
        if (findUnrepaired()) {
            return MODE.REPAIRING;
        }

        buildLabLoc = radio.readLabLoc();

        if (buildLabLoc!=null){
            return MODE.BUILD_LAB;
        }

        return MODE.HEALING;
    }

    public boolean findUnrepaired() throws GameActionException {
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(-1, rc.getTeam());
        for (RobotInfo bot : nearbyBots) {
            if ((bot.type == RobotType.WATCHTOWER || bot.type == RobotType.LABORATORY) && bot.mode == RobotMode.PROTOTYPE) {
                target = bot.location;
                return true;
            }
        }
        return false;
    }

    public int distToWall(Direction d) {
        MapLocation my = rc.getLocation();
        MapLocation n = my.add(d);
        int min = 61;
        min = Math.min(min, n.x);
        min = Math.min(min, n.y);
        min = Math.min(min, rc.getMapWidth() - n.x);
        min = Math.min(min, rc.getMapHeight() - n.y);
        return min;
    }

    public Direction[] sortedDirections() {
        Direction[] dirs = { Direction.NORTH, Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST, Direction.SOUTH,
                Direction.SOUTHWEST, Direction.WEST, Direction.NORTHWEST };
        Arrays.sort(dirs, (a, b) -> distToWall(b) - distToWall(a));
        return dirs;
    }

    public void build(int[] build_order) throws GameActionException {
        for (Direction dir : dirs) {
            switch (counter % 2) {
                case 0:
                    rc.setIndicatorString("Trying to build a laboratory" + " built_units: " + built_units + " "
                            + build_order[counter % 2]);
                    buildLaboratory(dir);
                    break;
                case 1:
                    rc.setIndicatorString("Trying to build a watchtower" + " built_units: " + built_units + " "
                            + build_order[counter % 2]);
                    buildWatchtower(dir);
                    break;
            }
            if (built_units >= build_order[counter % 2]) {
                counter++;
                built_units = 0;
            }
        }
    }

    public boolean buildLaboratory(Direction dir) throws GameActionException {
        if (rc.canBuildRobot(RobotType.LABORATORY, dir)) {
            rc.buildRobot(RobotType.LABORATORY, dir);
            built_units++;
            num_labs++;
            return true;
        }
        return false;
    }

    public void moveOff() throws GameActionException{
        MapLocation my = rc.getLocation();
        Direction bestDirection = null;
        int minRubble = 100000;
        for (Direction d : dirs){
            if (rc.canMove(d)){
                MapLocation n = my.add(d);
                if (rc.senseRubble(n) < minRubble){
                    bestDirection = d;
                    minRubble = rc.senseRubble(n);
                }
            }
        }

        if (rc.canMove(bestDirection))
            rc.move(bestDirection);
    }

    public void buildWatchtower(Direction dir) throws GameActionException {
        if (rc.canBuildRobot(RobotType.WATCHTOWER, dir)) {
            rc.buildRobot(RobotType.WATCHTOWER, dir);
            built_units++;
            num_watchtowers++;
        }
    }

    public void heal() throws GameActionException {
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(-1, rc.getTeam());
        for (RobotInfo bot : nearbyBots) {
            if (bot.type == RobotType.WATCHTOWER || bot.type == RobotType.LABORATORY) {
                if (bot.health < bot.type.health) {
                    if (rc.canRepair(bot.location))
                        rc.repair(bot.location);
                    else
                        moveToLocation(bot.location);
                }
            }
        }
    }

    public void forTheGreaterGood() throws GameActionException {
        MapLocation cur = rc.getLocation();
        MapLocation target = null;
        if (rc.senseLead(cur) == 0) {
            rc.disintegrate();
        }
        // robot finds closest spot to archon without lead on it, then destroys itself.
        int distSquared;
        int minDistSquared = 10000;
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                if (dx == 0 && dy == 0)
                    continue;
                if (validCoords(cur.x + dx, cur.y + dy)) {
                    MapLocation loc = new MapLocation(cur.x + dx, cur.y + dy);
                    if (loc.equals(homeArchon))
                        continue;
                    if (rc.canSenseLocation(loc)) {
                        int numLead = rc.senseLead(loc);
                        if (numLead == 0) {
                            distSquared = cur.distanceSquaredTo(loc);
                            if (distSquared < minDistSquared) {
                                minDistSquared = distSquared;
                                target = loc;
                            }
                        }

                    }
                }
            }
        }
        if (target != null) {
            moveToLocation(target);
            rc.setIndicatorString("target: " + target.x + " " + target.y);
        } else
            moveInDirection(exploratoryDir);
    }

    public boolean unitsOnMap() throws GameActionException{
        //checks if the counters for the various units are sufficient
        //varies based on which lab you're building (1st, 2nd, 3rd)
        troopCounter = new int[] { radio.readCounter(RobotType.MINER), radio.readCounter(RobotType.SOLDIER),
                radio.readCounter(RobotType.BUILDER), radio.readCounter(RobotType.SAGE), 
                radio.readCounter(RobotType.WATCHTOWER), radio.readCounter(RobotType.LABORATORY) };
        System.out.println("hi there: " + radio.readCounter(RobotType.LABORATORY) + " " + troopCounter[5]);
        switch(troopCounter[5]){
            case 0:
                return true;
             //   return (troopCounter[3] + troopCounter[1] > 5);
            case 1:
                return (troopCounter[3] + troopCounter[1] > 30);
            case 2:
                return (troopCounter[3] + troopCounter[1] > 70);
            case 3:
                return (troopCounter[3] + troopCounter[1] > 100);
            case 4:
                return (troopCounter[3] + troopCounter[1]> 150);
            default:
                return false;

        }
    }

    public RANK getBuilderRank() throws GameActionException {
        RANK new_rank = findRank();
        if (new_rank == RANK.DEFAULT || new_rank == RANK.MARTYR) {
            return new_rank;
        }
        return RANK.DEFAULT;
    }
}