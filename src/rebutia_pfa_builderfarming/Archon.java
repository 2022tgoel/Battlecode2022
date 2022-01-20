package rebutia_pfa_builderfarming;

import battlecode.common.*;
import java.util.*;

public class Archon extends Unit {

    enum MODE {
        INITIAL,
        DEFAULT,
        SOLDIER_HUB,
        THREATENED,
        OTHER_THREATENED,
        SEND_MARTYRS
    }

    int round_num;
    int archonNumber = -1;

    Direction[] dirs;

    int built_units = 0;
    int counter = 0;

    int num_soldiers = 0;
    int num_miners = 0;
    int num_builders = 0;
    int num_archons_init;
    int num_archons_alive;

    int num_soldiers_hub = 0;

    int[] defaultBuildOrder;
    int threatChannel = -1;

    Comms radio;

    public Archon(RobotController rc) throws GameActionException {
        super(rc);
        System.out.println("here");
        num_archons_alive = rc.getArchonCount();
        num_archons_init = num_archons_alive;
        dirs = sortedDirections();
        defaultBuildOrder = chooseBuildOrder();
        radio = new Comms(rc);
        archonNumber = radio.getArchonNum();
    }

    @Override
    public void run() throws GameActionException {
        round_num = rc.getRoundNum();
        radio.update();
        radio.clearThreat();
        radio.clearMiningAreas();
        radio.clearTargetAreas();
        radio.clearRanks();

        archonNumber = radio.getArchonNum();
        // System.out.println("Archon number: " + archonNumber + " Mode num: " +
        // radio.getMode() + " " + " round: " + round_num);
        MODE mode = determineMode();
        switch (mode) {
            case THREATENED:
                threatChannel = radio.sendThreatAlert();
                int tot = radio.totalUnderThreat();

                if (round_num % tot != threatChannel) { // alternate between those under threat
                    break;
                }
                Direction[] enemyDirs = getEnemyDirs();
                for (Direction dir : enemyDirs) {
                    buildSoldier(dir);
                }
                break;
            case INITIAL:
                if (round_num % num_archons_alive != archonNumber)
                    break;
                build(chooseInitialBuildOrder());
                break;
            case SOLDIER_HUB:
                boolean soldier_built = build(new int[] { 0, 1, 0 });
                if (soldier_built)
                    num_soldiers_hub++;
                if (num_soldiers_hub > 20) {
                    radio.broadcastMode((archonNumber + 1) % num_archons_alive);
                    num_soldiers_hub = 0;
                }
                break;
            case OTHER_THREATENED:
                if (rc.getTeamLeadAmount(rc.getTeam()) < 600)
                    break; // save for attacked archons
            case DEFAULT:
                if (round_num % num_archons_alive != archonNumber || round_num % 4 != 0)
                    break;
                build(new int[] { 4, 1, 0 }); // defaultBuildOrder);
                break;
            case SEND_MARTYRS:
                if (round_num % num_archons_alive != archonNumber)
                    break;
                if (rc.getTeamLeadAmount(rc.getTeam()) < 600)
                    break;
                radio.postRank(RANK.MARTYR);
                int bestDirectionIdx = -1;
                int bestDirectionRubble = 100000;
                for (int i = 0; i < dirs.length; i++) {
                    int rubbleHere = rc.senseRubble(rc.getLocation().add(dirs[i]));
                    if (rubbleHere < bestDirectionRubble && rc.canBuildRobot(RobotType.BUILDER, dirs[i])) {
                        bestDirectionRubble = rubbleHere;
                        bestDirectionIdx = i;
                    }
                }
                if (bestDirectionIdx != -1) {
                    buildBuilder(dirs[bestDirectionIdx]);
                }
                break;
        }
        num_archons_alive = rc.getArchonCount();
        rc.setIndicatorString("mode: " + mode.toString() + " " + num_soldiers_hub);
    }

    public boolean build(int[] build_order) throws GameActionException {
        boolean unit_built = false;
        for (Direction dir : dirs) {
            switch (counter % 3) {
                case 0:
                    if (built_units < build_order[counter % 3]) {
                        rc.setIndicatorString("Trying to build a miner" + " built_units: " + built_units + " "
                                + build_order[counter % 3]);
                        unit_built = buildMiner(dir);
                        // System.out.println("MINER BUILT: " + unit_built + " Roundnum: " +
                        // rc.getRoundNum());
                    }
                    break;
                case 1:
                    if (built_units < build_order[counter % 3]) {
                        rc.setIndicatorString("Trying to build a soldier" + " built_units: " + built_units + " "
                                + build_order[counter % 3]);
                        unit_built = buildSoldier(dir);
                        // System.out.println("SOLDIER BUILT: " + unit_built);
                    }
                    break;
                case 2:
                    if (built_units < build_order[counter % 3]) {
                        rc.setIndicatorString("Trying to build a builder" + " built_units: " + built_units + " "
                                + build_order[counter % 3]);
                        unit_built = buildBuilder(dir);
                        // System.out.println("BUILDER BUILT: " + unit_built);
                    }
                    break;
            }
            if (built_units >= build_order[counter % 3]) {
                counter++;
                built_units = 0;
            }
            if (unit_built)
                return true;
        }
        return false;
    }

    public MODE determineMode() throws GameActionException {
        int sizeBracket = (int) Math.ceil((double) mapArea / 1000);
        int numMinersInitial = Math.max((sizeBracket * 3) / num_archons_init, 1);
        if (underThreat()) {
            return MODE.THREATENED;
        }

        if (radio.totalUnderThreat() > 0) {
            return MODE.OTHER_THREATENED;
        }

        if (num_miners < numMinersInitial) {
            return MODE.INITIAL;
        }

        if (radio.getMode() == archonNumber) {
            return MODE.SOLDIER_HUB;
        }

        if (this.built_units % 13 == 0) {
            return MODE.SEND_MARTYRS;
        }

        return MODE.DEFAULT;
    }

    public boolean underThreat() throws GameActionException {
        RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        if (enemies.length > 0) {
            broadcastTarget(enemies[0].location);
            return true;
        }
        return false;
    }

    /////////////////////////////////////////////////////////////////////
    public boolean buildMiner(Direction dir) throws GameActionException {
        if (rc.canBuildRobot(RobotType.MINER, dir)) {
            rc.buildRobot(RobotType.MINER, dir);
            built_units++;
            num_miners++;
            return true;
        }
        return false;
    }

    public boolean buildSoldier(Direction dir) throws GameActionException {
        if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
            rc.buildRobot(RobotType.SOLDIER, dir);
            built_units++;
            num_soldiers++;
            return true;
        }
        return false;
    }

    public boolean buildBuilder(Direction dir) throws GameActionException {
        rc.setIndicatorString("here " + rc.canBuildRobot(RobotType.BUILDER, dir));
        if (rc.canBuildRobot(RobotType.BUILDER, dir)) {
            rc.buildRobot(RobotType.BUILDER, dir);
            built_units++;
            num_builders++;
            return true;
        }
        return false;
    }

    public boolean builderInRange() throws GameActionException {
        RobotInfo[] allies = rc.senseNearbyRobots(RobotType.BUILDER.actionRadiusSquared, rc.getTeam());
        for (RobotInfo r : allies) {
            if (r.type == RobotType.BUILDER) {
                return true;
            }
        }
        return false;
    }

    //////////////////////////////////////////////////////////////////////
    public MapLocation getAvgEnemyLocation() throws GameActionException {
        RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        int num_enemies = 0;
        double cx = 0;
        double cy = 0;
        for (RobotInfo enemy : enemies) {
            if (enemy.type == RobotType.SOLDIER || enemy.type == RobotType.SAGE) {
                cx += (double) enemy.location.x;
                cy += (double) enemy.location.y;
                num_enemies += 1;
            }
        }
        if (num_enemies > 0)
            return new MapLocation((int) (cx / num_enemies), (int) (cy / num_enemies));
        else
            return rc.getLocation().add(dirs[0]);
    }

    public Direction[] getEnemyDirs() throws GameActionException {
        MapLocation loc = getAvgEnemyLocation();
        Direction toDest = rc.getLocation().directionTo(loc);
        Direction[] d_arr = { toDest, toDest.rotateLeft(), toDest.rotateRight(), toDest.rotateLeft().rotateLeft(),
                toDest.rotateRight().rotateRight(), toDest.opposite().rotateLeft(), toDest.opposite().rotateRight(),
                toDest.opposite() };

        return d_arr;
    }

    public int distToWall(Direction d) {
        MapLocation my = rc.getLocation();
        MapLocation n = my.add(d);
        int min = Math.min(rc.getMapWidth() - 1 - n.x, n.x) + Math.min(n.y, rc.getMapHeight() - 1 - n.y);
        assert (min <= 60);
        return min;
    }

    public Direction[] sortedDirections() {
        Direction[] dirs = { Direction.NORTH, Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST, Direction.SOUTH,
                Direction.SOUTHWEST, Direction.WEST, Direction.NORTHWEST };
        Arrays.sort(dirs, (a, b) -> distToWall(b) - distToWall(a));
        return dirs;
    }

    public int[] chooseBuildOrder() {
        if (mapArea < 1400) {
            return new int[] { 1, 6, 0 }; // miners, soldiers, builders
        } else if (mapArea < 2200) {
            return new int[] { 1, 6, 0 }; // miners, soldiers, builders
        } else {
            return new int[] { 1, 6, 0 }; // miners, soldiers, builders
        }
    }

    public int[] chooseInitialBuildOrder() throws GameActionException {
        return new int[] { 1, 0, 0 };
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
                    // rc.setIndicatorString("Succesful Heal!");
                    rc.repair(weakestBot.location);
                }
            } else {
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