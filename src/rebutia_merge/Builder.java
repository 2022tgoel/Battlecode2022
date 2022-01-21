package rebutia_merge;

import battlecode.common.*;
import java.util.*;

public class Builder extends Unit {
    public enum MODE {
        HEALING,
        BUILDING,
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
    private int[] troopCounter = { 0, 0, 0, 0, 0 }; // miner, soldier, builder, sage, watchtower
    MapLocation target = null;

    private int built_units = 0;
    private int[] build_order;

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
                radio.readCounter(RobotType.BUILDER), 0, radio.readCounter(RobotType.WATCHTOWER) };
        build_order = getBuildOrder();
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
                    case BUILDING:
                        build(build_order);
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
    }

    private int[] getBuildOrder() {
        return new int[] { 0, 1 }; // laboratories, watchtowers
    }

    public MODE getMode() throws GameActionException {
        if (findUnrepaired()) {
            return MODE.REPAIRING;
        } else if (troopCounter[4] <= (CONSTANTS.SOLDIERS_TO_TOWERS * (double) troopCounter[2])) {
            return MODE.BUILDING;
        } else
            return MODE.HEALING;
    }

    public boolean findUnrepaired() throws GameActionException {
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(-1, rc.getTeam());
        for (RobotInfo bot : nearbyBots) {
            if (bot.type == RobotType.WATCHTOWER && bot.mode == RobotMode.PROTOTYPE) {
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

    public void buildLaboratory(Direction dir) throws GameActionException {
        if (rc.canBuildRobot(RobotType.LABORATORY, dir)) {
            rc.buildRobot(RobotType.LABORATORY, dir);
            built_units++;
            num_labs++;
        }
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

    public RANK getBuilderRank() throws GameActionException {
        RANK new_rank = findRank();
        if (new_rank == RANK.DEFAULT || new_rank == RANK.MARTYR) {
            return new_rank;
        }
        return RANK.DEFAULT;
    }
}