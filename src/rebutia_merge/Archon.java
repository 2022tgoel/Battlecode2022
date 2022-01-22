package rebutia_merge;

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

    private int[] troopCounter = { 0, 0, 0, 0, 0 }; // miner, soldier, builder, sage, watchtower
    private int desiredNumMiners = 1000;
    private boolean initial = true;

    public Archon(RobotController rc) throws GameActionException {
        super(rc);
        System.out.println("here");
        num_archons_alive = rc.getArchonCount();
        num_archons_init = num_archons_alive;
        dirs = sortedDirections();
        defaultBuildOrder = chooseBuildOrder();
        archonNumber = radio.getArchonNum();
        radio.initalizeArchonLoc(archonNumber, rc.getLocation());
        addLeadEstimate();
    }

    @Override
    public void run() throws GameActionException {
        round_num = rc.getRoundNum();
        radio.update();
        radio.clearThreat();
        radio.clearMiningAreas();
        radio.clearTargetAreas();
        updateAmountMined();

        archonNumber = radio.getArchonNum();

        troopCounter = new int[] {
                radio.readCounter(RobotType.MINER),
                radio.readCounter(RobotType.SOLDIER),
                radio.readCounter(RobotType.BUILDER),
                0,
                radio.readCounter(RobotType.WATCHTOWER)
        };

        if (round_num == 2) {
            int leadEstimate = radio.getLeadEstimate();
            desiredNumMiners = determineMinerNum(leadEstimate);
        }

        //System.out.println("Archon number: " + archonNumber + " Mode num: " + radio.getMode() + " " + " round: " + round_num);
        MODE mode = determineMode();
        double useful_miners = (double) radio.readCounter(BiCHANNEL.USEFUL_MINERS);

        switch (mode) {
            case THREATENED:
                threatChannel = radio.sendThreatAlert();
                int tot = radio.totalUnderThreat();

                if (tot != 0 && round_num % tot != threatChannel) { // alternate between those under threat
                    break;
                }
                if (checkForResources(RobotType.SOLDIER.buildCostLead)) {
                    Direction[] enemyDirs = getEnemyDirs();
                    for (Direction dir : enemyDirs) {
                        buildSoldier(dir);
                    }
                } else
                    attemptHeal();
                break;
            case INITIAL:
                if (round_num >= 60) {
                    initial = false;
                    break;
                }
                if (round_num % num_archons_alive != archonNumber) break;
                if (num_miners > (double) troopCounter[0] / (double) num_archons_init) break;
                if (troopCounter[0] < desiredNumMiners) {
                    build(new int[] {1, 0, 0});
                }
                else if ((useful_miners / (double) troopCounter[0]) >= 0.80) {
                    build(new int[] {1, 0, 0});
                }
                else if ((useful_miners / (double) troopCounter[0]) <= 0.75) {
                    initial = false;
                }
                System.out.println("Desired miners: " + desiredNumMiners + " Useful miners: " + useful_miners + " Ratio: " + (useful_miners / (double) troopCounter[0]));
                break;
            case SOLDIER_HUB:
                if (checkForResources(RobotType.SOLDIER.buildCostLead)) {
                    boolean soldier_built = build(new int[] { 0, 1, 0 });
                    if (soldier_built)
                        num_soldiers_hub++;
                } else {
                    attemptHeal();
                    // rc.setIndicatorString("ATTEMPTING HEALING");
                }
                if (num_soldiers_hub > 20) {
                    radio.broadcastMode((archonNumber + 1) % num_archons_alive);
                    num_soldiers_hub = 0;
                }
                break;
            case OTHER_THREATENED:
                if (rc.getTeamLeadAmount(rc.getTeam()) < 600) {
                    attemptHeal();
                    break; // save for attacked archons
                }
            case DEFAULT:
                attemptHeal();
                if (round_num % num_archons_alive != archonNumber || round_num % 5 != 0) break;
                else {
                    if ((useful_miners / (double) troopCounter[0]) >= 0.25) {
                        build(new int[] {1, 0, 0});
                    }
                }
                // if ((useful_miners / (double) troopCounter[0]) >= 0.60) build(new int[] {1, 0, 0});
                break;
        }
        num_archons_alive = rc.getArchonCount();
        rc.setIndicatorString("mode: " + mode.toString() + " " + leadLastCall + " " + getAvgMined());
    }

    public boolean build(int[] build_order) throws GameActionException {
        boolean unit_built = false;
        for (Direction dir : dirs) {
            switch (counter % 3) {
                case 0:
                    if (built_units < build_order[counter % 3]) {
                        // rc.setIndicatorString("Trying to build a miner" + " built_units: " +
                        // built_units + " " + build_order[counter % 3]);
                        unit_built = buildMiner(dir);
                        // System.out.println("MINER BUILT: " + unit_built + " Roundnum: " +
                        // rc.getRoundNum());
                    }
                    break;
                case 1:
                    if (built_units < build_order[counter % 3]) {
                        // rc.setIndicatorString("Trying to build a soldier" + " built_units: " +
                        // built_units + " " + build_order[counter % 3]);
                        unit_built = buildSoldier(dir);
                        // System.out.println("SOLDIER BUILT: " + unit_built);
                    }
                    break;
                case 2:
                    if (built_units < build_order[counter % 3]) {
                        // rc.setIndicatorString("Trying to build a builder" + " built_units: " +
                        // built_units + " " + build_order[counter % 3]);
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
        if (underThreat())
            return MODE.THREATENED;
        else if (radio.totalUnderThreat() > 0)
            return MODE.OTHER_THREATENED;
        else if (initial)
            return MODE.INITIAL;

        if (radio.getMode() == archonNumber)
            return MODE.SOLDIER_HUB;
        else
            return MODE.DEFAULT;
    }

    public int determineMinerNum(int leadEstimate) throws GameActionException {
        int sizeBracket = (int) Math.ceil((double) mapArea / 1000.0);
        int numMinersMap = sizeBracket * 2;
        if (leadEstimate > 2000) {
            return 12 + numMinersMap;
        }
        else if (leadEstimate > 1000) {
            return 8 + numMinersMap;
        }
        else if (leadEstimate > 500) {
            return 4 + numMinersMap;
        }
        else if (leadEstimate > 100) {
            return 2 + numMinersMap;
        }
        else {
            return 2 + numMinersMap;
        }
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
    static int curLead = 200;
    static int leadLastCall = 200;
    static int[] amountMined = new int[10]; // 10 turn avg

    public void updateAmountMined() {
        curLead = rc.getTeamLeadAmount(rc.getTeam());
        if (curLead >= leadLastCall) { // otherwise, something was spent
            int minedLastCall = curLead - leadLastCall;
            amountMined[round_num % amountMined.length] = minedLastCall;
        }
        leadLastCall = curLead;
    }

    public double getAvgMined() {
        int valid_entries = 0;
        double avg = 0;
        for (int i = 0; i < amountMined.length; i++)
            if (amountMined[i] != 0) {
                avg += amountMined[i];
                valid_entries++;
            }
        avg = avg / ((double) valid_entries);
        return avg;
    }

    public boolean checkForResources(int buildCost) throws GameActionException { // CHANGE TO INCORPORATE GOLD ONCE WE
                                                                                 // USE SAGES
        if (curLead >= buildCost || round_num < 20) {
            return true;
        } else {
            int numTurnsToResources = (int) (((double) buildCost - (double) curLead) / Math.max(getAvgMined(), 2.0));
            int numTurnsToAct = rc.getActionCooldownTurns()
                    + (int) ((cooldownMultiplier(rc.getLocation()) * rc.getType().actionCooldown) / 10);
            if (numTurnsToResources > numTurnsToAct) {
                return false;
            }
            if (numTurnsToResources > numTurnsToAct) {
                return false;
            } else
                return true;
        }
    }

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
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(rc.getType().actionRadiusSquared, rc.getTeam());
        // if there are any nearby enemy robots, attack the one with the least health
        if (nearbyBots.length > 0) {
            RobotInfo weakestBot = null;
            for (RobotInfo bot : nearbyBots) {
                if (bot.type == RobotType.SOLDIER)
                    if ((weakestBot == null && bot.health < RobotType.SOLDIER.health) ||
                            (weakestBot != null && bot.health < weakestBot.health)) {
                        weakestBot = bot;
                    }
            }
            if (weakestBot != null) {
                if (rc.canRepair(weakestBot.location)) {
                    // rc.setIndicatorString("Succesful Heal!");
                    rc.repair(weakestBot.location);
                }
            } else {
                for (RobotInfo bot : nearbyBots) {
                    if (bot.type == RobotType.MINER)
                        if ((weakestBot == null && bot.health < RobotType.MINER.health) ||
                                (weakestBot != null && bot.health < weakestBot.health)) {
                            weakestBot = bot;
                        }
                }
                if (weakestBot != null) {
                    if (rc.canRepair(weakestBot.location)) {
                        rc.repair(weakestBot.location);
                    }
                }
            }
        }
    }

    public void addLeadEstimate() throws GameActionException {
        MapLocation[] leadLocs = rc.senseNearbyLocationsWithLead();
        MapLocation[] friendlyArchonLocs = radio.getFriendlyArchons(num_archons_alive);
        int total_value = 0;
        if (archonNumber != 0) {
            MapLocation[] prevArchons = firstN(friendlyArchonLocs, archonNumber - 1);
            MapLocation[] leadLocsUncounted = leadLocsNotCounted(leadLocs, prevArchons);
            for (MapLocation loc : leadLocsUncounted) {
                total_value += rc.senseLead(loc);
            }
        } else {
            for (MapLocation loc : leadLocs) {
                total_value += rc.senseLead(loc);
            }
        }
        if (total_value > 0) {
            radio.incrementLeadEsimate(total_value);
        }
    }

    public MapLocation[] leadLocsNotCounted(MapLocation[] leadLocs, MapLocation[] archons) throws GameActionException {
        MapLocation[] locs = new MapLocation[leadLocs.length];
        int uncounted = 0;
        for (int i = 0; i < leadLocs.length; i++) {
            boolean counted = false;
            // check if lead is visible by any of the archons
            for (int j = 0; j < archons.length; j++) {
                if (leadLocs[i].distanceSquaredTo(archons[j]) <= RobotType.ARCHON.visionRadiusSquared) {
                    counted = true;
                }
            }
            // if not visible, add to list
            if (!counted) {
                locs[uncounted] = leadLocs[i];
                uncounted++;
            }
        }
        MapLocation[] leadLocsUncounted = new MapLocation[uncounted];
        for (int i = 0; i < uncounted; i++) {
            leadLocsUncounted[i] = locs[i];
        }
        return leadLocsUncounted;
    }

    public MapLocation[] firstN(MapLocation[] locs, int n) throws GameActionException {
        MapLocation[] newLocs = new MapLocation[n];
        for (int i = 0; i < n; i++) {
            newLocs[i] = locs[i];
        }
        return newLocs;
    }
}