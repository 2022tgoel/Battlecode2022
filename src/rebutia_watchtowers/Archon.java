package rebutia_watchtowers;

import battlecode.common.*;
import java.util.*;

public class Archon extends Unit {

    enum MODE {
        INITIAL,
        DEFAULT,
        SOLDIER_HUB,
        MAKE_BUILDER,
        THREATENED,
        OTHER_THREATENED,
        REINFORCE_WATCHTOWER;
    }

    int round_num;
    int archonNumber = -1;

    int built_units = 0;
    int counter = 0;

    int num_soldiers = 0;
    int num_miners = 0;
    int num_builders = 0;
    int num_watchtowers = 0;
    int num_archons_init;
    int num_archons_alive;

    int num_soldiers_hub = 0;

    int[] defaultBuildOrder;
    int threatChannel = -1;

    private int[] troopCounter = { 0, 0, 0, 0, 0 }; // miner, soldier, builder, sage, watchtower

    public Archon(RobotController rc) throws GameActionException {
        super(rc);
        System.out.println("here");
        num_archons_alive = rc.getArchonCount();
        num_archons_init = num_archons_alive;
        defaultBuildOrder = chooseBuildOrder();
        archonNumber = radio.getArchonNumber();
    }

    @Override
    public void run() throws GameActionException {
        round_num = rc.getRoundNum();
        radio.update();
        radio.clearThreat();
        radio.clearMiningAreas();
        radio.clearTargetAreas();

        int nextArchonNumber = radio.getArchonNumber();
        if (nextArchonNumber > archonNumber) {
            // If this happens, then the last archon was unable to clear the counter
            archonNumber = 0;
            rc.writeSharedArray(CHANNEL.ARCHON_NUMBER.getValue(), 1);
        }
        if ((archonNumber + 1) == rc.getArchonCount()) {
            // The last archon should clear the counter
            rc.writeSharedArray(CHANNEL.ARCHON_NUMBER.getValue(), 0);
        }
        troopCounter = new int[] {
                radio.readCounter(RobotType.MINER),
                radio.readCounter(RobotType.SOLDIER),
                radio.readCounter(RobotType.BUILDER),
                0,
                radio.readCounter(RobotType.WATCHTOWER)
        };

        MODE mode = determineMode();
        switch (mode) {
            case THREATENED:
                threatChannel = radio.sendThreatAlert();
                if (round_num % radio.getThreatenedArchonCount() == threatChannel) {
                    for (Direction direction : getDirectionsTowardEnemy()) {
                        buildSoldier(direction);
                    }
                }
                break;
            case INITIAL:
                if (round_num % num_archons_alive != archonNumber)
                    break;
                build(chooseInitialBuildOrder());
                break;
            case MAKE_BUILDER:
                build(new int[] { 0, 0, 1 });
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
            case REINFORCE_WATCHTOWER:
                Direction dir = getLeastRubbleBuildableDirection(RobotType.WATCHTOWER);
                if (dir != null) {
                    buildWatchtower(dir);
                    break;
                } else {
                    System.out.println("Could not find a place eligible to build a watchtower");
                }
                // If it doesn't work, intentionally fall through to default
            case DEFAULT:
                if (round_num % num_archons_alive != archonNumber || round_num % 4 != 0)
                    break;
                build(new int[] { 4, 1, 0 }); // defaultBuildOrder);
                break;
        }
        num_archons_alive = rc.getArchonCount();
        rc.setIndicatorString("mode: " + mode.toString() + " " + num_soldiers_hub);
    }

    public boolean build(int[] build_order) throws GameActionException {
        boolean unit_built = false;
        for (Direction dir : directions) {
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
        int sizeBracket = (int) Math.ceil(mapArea / 1000.0);
        int numMinersInitial = Math.max((sizeBracket * 3) / num_archons_init, 1);
        if (underThreat()) {
            return MODE.THREATENED;
        }

        if (radio.getThreatenedArchonCount() > 0) {
            return MODE.OTHER_THREATENED;
        }

        if (num_miners < numMinersInitial) {
            return MODE.INITIAL;
        }

        // if (radio.getMode() == archonNumber
        // && troopCounter[4] > (CONSTANTS.SOLDIERS_TO_TOWERS * (double)
        // troopCounter[2])) {
        // return MODE.SOLDIER_HUB;
        // }

        // if (troopCounter[2] < 4 && num_builders < 1) {
        // return MODE.MAKE_BUILDER;
        // }

        if (troopCounter[4] < 2 && rc.getRoundNum() > 200) {
            return MODE.REINFORCE_WATCHTOWER;
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
            radio.updateCounter(RobotType.MINER);
            built_units++;
            num_miners++;
            troopCounter[0]++;
            return true;
        }
        return false;
    }

    public boolean buildWatchtower(Direction dir) throws GameActionException {
        if (rc.canBuildRobot(RobotType.WATCHTOWER, dir)) {
            rc.buildRobot(RobotType.WATCHTOWER, dir);
            built_units++;
            num_watchtowers++;
            troopCounter[4]++;
            return true;
        }
        return false;
    }

    public boolean buildSoldier(Direction dir) throws GameActionException {
        if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
            rc.buildRobot(RobotType.SOLDIER, dir);
            radio.updateCounter(RobotType.SOLDIER);
            built_units++;
            num_soldiers++;
            troopCounter[1]++;
            return true;
        }
        return false;
    }

    public boolean buildBuilder(Direction dir) throws GameActionException {
        rc.setIndicatorString("here " + rc.canBuildRobot(RobotType.BUILDER, dir));
        if (rc.canBuildRobot(RobotType.BUILDER, dir)) {
            rc.buildRobot(RobotType.BUILDER, dir);
            radio.updateCounter(RobotType.BUILDER);
            built_units++;
            num_builders++;
            troopCounter[2]++;
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
    public MapLocation getAverageEnemyLocation() throws GameActionException {
        RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        int n = 0;
        double cx = 0;
        double cy = 0;
        for (RobotInfo enemy : enemies) {
            if (enemy.type == RobotType.SOLDIER || enemy.type == RobotType.SAGE) {
                cx += enemy.location.x;
                cy += enemy.location.y;
                n++;
            }
        }
        if (n > 0) {
            return new MapLocation((int) (cx / n), (int) (cy / n));
        }

        return null;
    }

    public Direction[] getDirectionsTowardEnemy() throws GameActionException {
        MapLocation loc = getAverageEnemyLocation();
        MapLocation me = rc.getLocation();
        if (loc == null) {
            loc = me.add(Direction.NORTH);
        }
        Direction to = me.directionTo(loc);
        return new Direction[] {
                to,
                to.rotateLeft(),
                to.rotateRight(),
                to.rotateLeft().rotateLeft(),
                to.rotateRight().rotateRight(),
                to.opposite().rotateLeft(),
                to.opposite().rotateRight(),
                to.opposite()
        };
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
        // miners, soldiers, builders
        return new int[] { 1, 2, 0 };
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