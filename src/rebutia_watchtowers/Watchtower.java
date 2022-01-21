package rebutia_watchtowers;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotMode;
import battlecode.common.RobotType;

public class Watchtower extends Unit {

    enum MODE {
        BRANCH_OUT,
        SEEKING,
        ATTACKING,
        SEARCHING_ENEMIES, // when u have already found enemies
        FLEE,
        DEFENSIVE_RUSH,
        NONE;
    }

    boolean attacked = false;
    boolean anchored = false;

    int round_num = 0;

    MapLocation[] threatenedArchons;
    MapLocation baseArchonLocation;
    MapLocation anchorLocation;

    private int[] fleeDirection = { Integer.MAX_VALUE, Integer.MAX_VALUE };
    private int stopFleeingRound = -1;
    private int DRUSH_RSQR = 400;

    // up to 10 manhattan distance yk
    public static final int ANCHOR_DISTANCE = 5;

    MODE mode;

    // for attacking and searching enemies modes
    private MapLocation target = null;
    private int[] lastAttackDir = null;

    public Watchtower(RobotController rc) throws GameActionException {
        super(rc);
        initialize();
    }

    public void run() throws GameActionException {
        super.run();
        round_num = rc.getRoundNum();
        radio.updateCounter();
        attacked = attemptAttack(false);
        senseMiningArea();

        mode = determineMode();
        visualize();
        switch (mode) {
            case SEEKING:
                if (rc.getMode() == RobotMode.TURRET && rc.canTransform())
                    rc.transform();
                if (rc.getMode() == RobotMode.PORTABLE) {
                    if (target != null)
                        huntTarget();
                    else
                        moveInDirection(lastAttackDir);
                }
                target = null;
                break;
            case ATTACKING:
                attemptAttack(true);
                break;
            case SEARCHING_ENEMIES:
                if (adjacentToEdge()) { // TODO: bots occasionally get stuck somehow
                    lastAttackDir = flip(lastAttackDir);
                }
                moveInDirection(lastAttackDir);
                break;
            case DEFENSIVE_RUSH:
                defensiveMove();
                break;
            case FLEE:
                moveInDirection(fleeDirection);
                break;
            case BRANCH_OUT:
                // Choose an anchor location
                if (anchorLocation == null) {
                    // this may return null
                    anchorLocation = chooseAnchorLocation();
                }
                if (!rc.canMove(rc.getLocation().directionTo(anchorLocation))) {
                    // it's okay to anchor if the space is occupied and we're within two squares of
                    // it
                    if (rc.getLocation().isWithinDistanceSquared(anchorLocation, 2)) {
                        anchorLocation = null;
                        anchored = true;
                        break;
                    }
                }
                if (anchorLocation != null) {
                    moveToLocation(anchorLocation);
                }
                break;
            case NONE:
                break;
        }
        // System.out.println("Watchtower mode: " + rc.getMode().toString());
    }

    public boolean isMapLocationValid(MapLocation loc) {
        return loc.x >= 0 && loc.x < rc.getMapWidth() && loc.y >= 0 && loc.y < rc.getMapHeight();
    }

    public MapLocation chooseAnchorLocation() {
        if (baseArchonLocation == null) {
            return null;
        }
        MapLocation me = rc.getLocation();
        MapLocation anchorLocation;
        int minDistanceSquared = ANCHOR_DISTANCE * ANCHOR_DISTANCE;
        for (Direction d : Direction.values()) {
            // branch out in all 8 directions, look for the best place to anchor
            anchorLocation = new MapLocation(me.x + d.dx * 10, me.y + d.dy * 10);
            if (isMapLocationValid(anchorLocation)
                    && anchorLocation.distanceSquaredTo(baseArchonLocation) >= minDistanceSquared) {
                return anchorLocation;
            }
        }
        return null;
    }

    public void visualize() throws GameActionException {
        rc.setIndicatorString("MODE: " + mode.toString());
        if (mode == MODE.SEEKING) {
            if (target != null) {
                rc.setIndicatorLine(rc.getLocation(), target, 255, 0, 0);
            }
        }
        if (mode == MODE.ATTACKING) {
            rc.setIndicatorLine(rc.getLocation(), target, 100, 0, 0);
        } else if (mode == MODE.SEARCHING_ENEMIES) {
            rc.setIndicatorString("MODE: " + mode.toString() + " DIR: " + lastAttackDir[0] + " " + lastAttackDir[1]);
        } else if (target != null) {
            if (mode != MODE.FLEE)
                rc.setIndicatorString("TARGET: " + target.toString() + " MODE: " + mode.toString());
            else
                rc.setIndicatorString(
                        "TARGET: " + target.toString() + " MODE: FLEE " + "FLEEROUND: " + stopFleeingRound);
            rc.setIndicatorLine(rc.getLocation(), target, 0, 0, 100);
        } else {
            if (mode != MODE.FLEE)
                rc.setIndicatorString("TARGET: null MODE: " + mode.toString());
            else
                rc.setIndicatorString("TARGET: null MODE: FLEE " + "FLEEROUND: " + stopFleeingRound);
        }
    }

    public MODE determineMode() throws GameActionException {
        // Before doing anything, branch out from the Archon
        // Make sure always 10 units^2 away from the Archon
        // Branch out
        if (baseArchonLocation == null) {
            RobotInfo[] nearby = rc.senseNearbyRobots(10, rc.getTeam());
            for (RobotInfo ri : nearby) {
                if (ri.type == RobotType.ARCHON) {
                    baseArchonLocation = ri.location;
                    return MODE.BRANCH_OUT;
                }
            }
        } else if (baseArchonLocation.isWithinDistanceSquared(rc.getLocation(), 10)) {
            return MODE.BRANCH_OUT;
        }

        // Priority 1 - Defend.
        threatenedArchons = findThreatenedArchons();
        if (threatenedArchons != null) {
            for (MapLocation archon : threatenedArchons) {
                if (rc.getLocation().distanceSquaredTo(archon) <= DRUSH_RSQR) {
                    return MODE.DEFENSIVE_RUSH;
                }
            }
        }

        /*
         * // Priority 2 - Don't die.
         * int[] potFleeDir = fleeDirection();
         * boolean validFlee = (potFleeDir[0] != Integer.MAX_VALUE && potFleeDir[1] !=
         * Integer.MAX_VALUE);
         * if (!validFlee && stopFleeingRound == round_num) lastAttackDir = null;
         * if (validFlee || stopFleeingRound > round_num) {
         * if (validFlee) fleeDirection = potFleeDir;
         * // keep fleeing for two moves (2 rounds per move)
         * if (stopFleeingRound <= round_num) {
         * stopFleeingRound = round_num + 6;
         * }
         * return MODE.FLEE;
         * }
         */

        // Priority 3 - Hunt enemies.
        findTargets();
        if (target != null) {
            if (rc.getLocation().distanceSquaredTo(target) > 40) {
                return MODE.SEEKING;
            } else {
                Direction lowRubble = findLowRubble();
                if (lowRubble != null)
                    rc.move(lowRubble);
                if (rc.getMode() == RobotMode.PORTABLE && rc.canTransform())
                    rc.transform();
                return MODE.ATTACKING;
            }
        } else if (lastAttackDir != null) {
            return MODE.SEEKING;
        } else
            return MODE.NONE;
    }

    public int[] fleeDirection() throws GameActionException {
        MapLocation cur = rc.getLocation();
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(-1);
        double cxse = 0;
        double cyse = 0;
        int numEnemies = 0;
        int numEnemyHits = 0;
        int numFriendHits = (rc.getHealth() + 2) / 3;
        int numFriends = 0;
        for (RobotInfo bot : nearbyBots) {
            if (bot.team == rc.getTeam()) {
                if (bot.type == RobotType.SOLDIER) {
                    numFriendHits += ((bot.health + 2) / 3);
                    numFriends++;
                }
            } else if (bot.team == rc.getTeam().opponent())
                if (bot.type == RobotType.SOLDIER) {
                    cxse += bot.location.x;
                    cyse += bot.location.y;
                    numEnemyHits += ((bot.health + 2) / 3);
                    numEnemies++;
                }
        }
        if (numEnemies == 0)
            return new int[] { Integer.MAX_VALUE, Integer.MAX_VALUE };

        if (numEnemies > 0) {
            cxse /= numEnemies;
            cyse /= numEnemies;
        }

        double unit_difference = (double) (numFriends + 1 - numEnemies);
        double ratio;

        if (((numFriends + 1) / numEnemies) > 1) {
            ratio = ((numFriends + 1) / numEnemies);
        } else {
            ratio = numEnemies / (numFriends + 1);
        }

        double a = 6 * ratio;
        int unit_advantage = (int) (a * Math.pow(unit_difference, 2) * Math.signum(unit_difference));

        // System.out.println("Unit advantage: " + unit_advantage + " Ratio: " + ratio +
        // " numFriendHits " + numFriendHits + " numEnemyHits " + numEnemyHits +
        // "round_num " + round_num + " id " + rc.getID());

        if (numFriendHits + unit_advantage < numEnemyHits) {
            double dx = -(cxse - cur.x) * 3;
            double dy = -(cyse - cur.y) * 3;
            // more attracted
            // dx = 0.7 * dx + 0.3 * (cxsf - cur.x);
            // dy = 0.7 * dx + 0.3 * (cysf - cur.y);
            return new int[] { (int) dx, (int) dy };
        }
        return new int[] { Integer.MAX_VALUE, Integer.MAX_VALUE };
    }

    public void findTargets() throws GameActionException {
        int data;
        int closestDist = 100000;
        MapLocation cur = rc.getLocation();
        MapLocation closestTarget = null;
        for (int i = 0; i < CHANNEL.NUM_TARGETS; i++) {
            data = rc.readSharedArray(CHANNEL.TARGET.getValue() + i);
            if (data != 0) {
                int x = (data >> 4) & 15;
                int y = data & 15;
                // System.out.println("I received an enemy at " + x*4 + " " + y*4 + " on round "
                // + round_num);
                MapLocation potentialTarget = new MapLocation(x * 4, y * 4);
                if (cur.distanceSquaredTo(potentialTarget) < closestDist) {
                    closestDist = cur.distanceSquaredTo(potentialTarget);
                    closestTarget = potentialTarget;
                }
            }
        }

        // finds closest target, and advances towards it.
        if (closestTarget != null) {
            target = closestTarget;
            lastAttackDir = new int[] { closestTarget.x - cur.x, closestTarget.y - cur.y };
            lastAttackDir = scaleToSize(lastAttackDir);
            // wanders in direction of target
        }
    }

    public void huntTarget() throws GameActionException {
        // if target is within 3 tiles, do not move closer, otherwise move closer
        // check if it's in turret mode
        if (rc.getMode() == RobotMode.TURRET)
            return;
        moveToLocation(target);
        lastAttackDir = new int[] { target.x - rc.getLocation().x, target.y - rc.getLocation().y };
        if (rc.getLocation().distanceSquaredTo(target) <= 20) {
            // check for low rubble squares to move to
            Direction lowRubble = findLowRubble();
            if (lowRubble != null)
                rc.move(lowRubble);
        } else
            moveInDirection(lastAttackDir);
    }

    public Direction findLowRubble() throws GameActionException {
        MapLocation cur = rc.getLocation();
        int lowest_rubble = 1 + rc.senseRubble(cur) / 10;
        int rubble;
        Direction bestDir = null;
        for (int i = 0; i < 8; i++) {
            if (!rc.canMove(directions[i]))
                continue;
            rubble = 1 + rc.senseRubble(cur.add(directions[i])) / 10;
            if (rubble < lowest_rubble && rc.canMove(directions[i])) {
                lowest_rubble = rubble;
                bestDir = directions[i];
            }
        }
        return bestDir;
    }

    public void defensiveMove() throws GameActionException {
        MapLocation closest = threatenedArchons[0];
        int min_dist = Integer.MAX_VALUE;
        // only find closest archon if there is more then one
        if (threatenedArchons.length > 1) {
            for (MapLocation loc : threatenedArchons) {
                if (loc.distanceSquaredTo(rc.getLocation()) < min_dist) {
                    min_dist = loc.distanceSquaredTo(rc.getLocation());
                    closest = loc;
                }
            }
        }
        // if you don't see the enemy, and you're not close to the archon, move towards
        // it
        if (rc.getLocation().distanceSquaredTo(closest) > 36 || target == null) {
            moveToLocation(closest);
        } else {
            huntTarget();
        }
    }

    public boolean archonDied() throws GameActionException {
        RobotInfo home;
        if (rc.canSenseLocation(homeArchon)) {
            home = rc.senseRobotAtLocation(homeArchon);
            return (home == null || home.type != RobotType.ARCHON);
        }
        return false;
    }

    public MapLocation[] findThreatenedArchons() throws GameActionException {
        int data;
        MapLocation[] archons = new MapLocation[4];
        int numThreatenedArchons = 0;
        for (int i = 0; i < 4; i++) {
            // rc.writeSharedArray(, value);
            data = rc.readSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + i);
            // go through channels until you find an empty one to communicate with.
            if (data != 0) {
                int x = data / 64;
                int y = data % 64;
                if (validCoords(x, y)) {
                    archons[numThreatenedArchons] = new MapLocation(x, y);
                    numThreatenedArchons++;
                }
            }
        }

        if (numThreatenedArchons == 0) {
            return null;
        } else {
            // only return threatened archons.
            MapLocation[] threatenedArchons = new MapLocation[numThreatenedArchons];
            for (int i = 0; i < numThreatenedArchons; i++) {
                threatenedArchons[i] = archons[i];
            }
            return threatenedArchons;
        }
    }

    public boolean attemptAttack(boolean attackMiners) throws GameActionException {
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(RobotType.SOLDIER.actionRadiusSquared, rc.getTeam().opponent());
        int weakestSoldierHealth = 100000;
        int weakestMinerHealth = 100000;
        int weakestSageHealth = 100000;
        int weakestTowerHealth = 100000;
        int weakestBuilerHealth = 100000;
        RobotInfo weakestSoldier = null;
        RobotInfo weakestTower = null;
        RobotInfo weakestSage = null;
        RobotInfo weakestMiner = null;
        RobotInfo weakestBuilder = null;
        RobotInfo archon = null;
        // if there are any nearby enemy robots, attack the one with the least health
        if (nearbyBots.length > 0) {
            for (RobotInfo bot : nearbyBots) {
                if (bot.type == RobotType.SOLDIER) {
                    if (bot.health < weakestSoldierHealth) {
                        weakestSoldier = bot;
                        weakestSoldierHealth = bot.health;
                    }
                }
                if (bot.type == RobotType.MINER) {
                    if (bot.health < weakestMinerHealth) {
                        weakestMiner = bot;
                        weakestMinerHealth = bot.health;
                    }
                }
                if (bot.type == RobotType.WATCHTOWER) {
                    if (bot.health < weakestTowerHealth) {
                        weakestTower = bot;
                        weakestTowerHealth = bot.health;
                    }
                }
                if (bot.type == RobotType.SAGE) {
                    if (bot.health < weakestSageHealth) {
                        weakestSage = bot;
                        weakestSageHealth = bot.health;
                    }
                }
                if (bot.type == RobotType.BUILDER) {
                    if (bot.health < weakestBuilerHealth) {
                        weakestBuilder = bot;
                        weakestBuilerHealth = bot.health;
                    }
                }
                if (bot.type == RobotType.ARCHON) {
                    archon = bot;
                }
            }
            // make more conditional, like damaging which one would give the biggest
            // advantage
            if (weakestSage != null) {
                if (rc.canAttack(weakestSage.location)) {
                    rc.attack(weakestSage.location);
                    target = weakestSage.location;
                    broadcastTarget(weakestSage.location);
                    return true;
                }
            } else if (weakestSoldier != null) {
                if (rc.canAttack(weakestSoldier.location)) {
                    rc.attack(weakestSoldier.location);
                    target = weakestSoldier.location;
                    broadcastTarget(weakestSoldier.location);
                    return true;
                }
            } else if (weakestTower != null) {
                if (rc.canAttack(weakestTower.location)) {
                    rc.attack(weakestTower.location);
                    target = weakestTower.location;
                    broadcastTarget(weakestTower.location);
                    return true;
                }
            } else if (weakestMiner != null && attackMiners) {
                if (rc.canAttack(weakestMiner.location)) {
                    rc.attack(weakestMiner.location);
                    target = weakestMiner.location;
                    broadcastTarget(weakestMiner.location);
                    return true;
                }
            } else if (weakestBuilder != null && attackMiners) {
                if (rc.canAttack(weakestBuilder.location)) {
                    rc.attack(weakestBuilder.location);
                    target = weakestBuilder.location;
                    broadcastTarget(weakestBuilder.location);
                    return true;
                }
            } else if (archon != null) {
                if (rc.canAttack(archon.location)) {
                    rc.attack(archon.location);
                    broadcastTarget(archon.location);
                    return true;
                }
            }
        }
        return false;
    }

    public void initialize() {
        DRUSH_RSQR = (int) ((double) mapArea / 9.0);
    }
}