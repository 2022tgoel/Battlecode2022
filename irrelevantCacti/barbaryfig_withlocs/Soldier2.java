package barbaryfig_withlocs;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class Soldier2 extends Unit {
    enum MODE {
        EXPLORATORY,
        HUNTING,
        ARCHON_RUSH,
        FLEE,
        LOW_HEALTH,
        DEFENSIVE_RUSH,
        WAITING,
        CONVOY;
    }

    final int ACTIVE_SQUARE_MINIMUM = 1;

    boolean attemptedAttack = false;
    boolean convoy_found = false;
    int counter = 0;
    int exploratoryDirUpdateRound = 0;
    int threatDetectedRound = 10000;
    int roundNum = 0;
    int dRushChannel = -1;

    int convoyDeployRound = 10000;

    RANK rank;
    MODE mode = MODE.EXPLORATORY;

    MapLocation[] threatenedArchons;
    MapLocation target;

    int[] exploratoryDir = getExploratoryDir(5);
    private int[] fleeDirection = { Integer.MAX_VALUE, Integer.MAX_VALUE };
    private int stopFleeingRound = 10000;
    private int DRUSH_RSQR = 400;
    private int ARUSH_RSQR = 900;
    private int closestActiveSquare;

    public Soldier2(RobotController rc) throws GameActionException {
        super(rc);
        rank = findRankSoldier();
        initialize();
    }

    public void calculateAndStoreClosestActiveSquare() throws GameActionException {
        closestActiveSquare = -1;
        int closestActiveSquareDistanceSquared = Integer.MAX_VALUE;
        // Determine which grid square to go to
        for (int i = 0; i < 4; i++) {
            int square = Comms.getTopActiveGridSquare(rc, i);
            int squareValue = Comms.getGridSquareValue(rc, CHANNEL.GRID_BASE.getValue(), square,
                    CHANNEL.GRID_SQUARES_PER_CHANNEL);
            MapLocation squareCenter = Comms.getGridSquareCenter(square, rc.getMapWidth(), rc.getMapHeight());

            if (squareValue > ACTIVE_SQUARE_MINIMUM) {
                int distanceSquared = rc.getLocation().distanceSquaredTo(squareCenter);
                if (distanceSquared < closestActiveSquareDistanceSquared) {
                    closestActiveSquare = square;
                    closestActiveSquareDistanceSquared = distanceSquared;
                }
            }
        }
    }

    @Override
    public void run() throws GameActionException {
        roundNum = rc.getRoundNum();
        // MapLocation myLocation = rc.getLocation();
        // int width = rc.getMapWidth();
        // int height = rc.getMapHeight();

        if (Comms.isTallyingRound(roundNum)) {
            tallyEnemies(rc.senseNearbyRobots());
        } else if (Comms.isTallyingRound(roundNum - 1)) {
            calculateAndStoreClosestActiveSquare();
        }

        // rc.setIndicatorString(indicator);
        tallyUnitTotals();

        attemptedAttack = attemptAttack(false);

        if (shouldDefendArchon()) {
            fuzzyMove(closestThreatenedArchon());
            rc.setIndicatorString("Defending archon");
        } else if (shouldFlee()) {
            moveInDirection(fleeDirection);
            rc.setIndicatorString("Fleeing");
        } else if (shouldAttackArchon()) {
            approachArchon();
            rc.setIndicatorString("Attacking archon");
        } else {
            // // Move towards the center of the closest active square.
            // if (closestActiveSquare != -1) {
            // int myGridSquare = Comms.locationToGridSquare(myLocation.x, myLocation.y,
            // width, height);
            // if (myGridSquare == closestActiveSquare) {
            // closestActiveSquare = -1;
            // } else {
            // MapLocation center = Comms.getGridSquareCenter(closestActiveSquare, width,
            // height);
            // fuzzyMove(center);
            // rc.setIndicatorString("Moving to active square: " + center);
            // rc.setIndicatorLine(myLocation, center, 0, 255, 0);
            // }
            // } else
            if (target != null) {
                // Find a target.
                huntTarget();
                target = null;
            } else {
                moveInDirection(exploratoryDir);
                rc.setIndicatorString("Exploring (" + exploratoryDir[0] + ", " + exploratoryDir[1] + ")");
            }
        }

        if (!attemptedAttack) {
            attemptAttack(true);
        }

        if (adjacentToEdge()) {
            exploratoryDir = getExploratoryDir(5);
        }

        // rc.setIndicatorLine(new MapLocation(0, 0), myLocation, 0, 255, 0);

        senseMiningArea();
        // rc.setIndicatorString("MODE: " + mode.toString());
    }

    public int[] getPotentialFleeDirection() throws GameActionException {
        MapLocation cur = rc.getLocation();
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(-1);
        double cxsf = 0;
        double cysf = 0;
        double cxse = 0;
        double cyse = 0;
        int numEnemies = 0;
        int maxHealthEnemy = 0;
        int numFriends = 0;
        for (RobotInfo bot : nearbyBots) {
            if (bot.team == rc.getTeam()) {
                if (bot.type == RobotType.SOLDIER) {
                    cxsf += bot.location.x;
                    cysf += bot.location.y;
                    numFriends++;
                }
            } else if (bot.team == rc.getTeam().opponent())
                if (bot.type == RobotType.SOLDIER) {
                    cxse += bot.location.x;
                    cyse += bot.location.y;
                    maxHealthEnemy = Math.max(maxHealthEnemy, bot.health);
                    numEnemies++;
                }
        }
        if (numEnemies > 0) {
            cxse /= numEnemies;
            cyse /= numEnemies;
        }
        if (numFriends > 0) {
            cxsf /= numFriends;
            cysf /= numFriends;
        }
        // for example, if soldier has 7,8, or 9 health this expression spits out 3, as
        // the soldier can only survive 3 hits.
        int soldierHitsLeft = (rc.getHealth() + 2) / 3;
        int enemyHitsLeft = (maxHealthEnemy + 2) / 3;
        boolean winsInteraction = false;
        // soldier strikes second, so must have more hits remaining
        if (attemptedAttack)
            winsInteraction = (soldierHitsLeft > enemyHitsLeft);
        else
            winsInteraction = (soldierHitsLeft >= enemyHitsLeft);
        // count yourself
        if (numEnemies > (numFriends + 1) || (numEnemies == 1 && !winsInteraction)) {
            double dx = -(cxse - cur.x);
            double dy = -(cyse - cur.y);
            // more attracted
            // dx = 0.7 * dx + 0.3 * (cxsf - cur.x);
            // dy = 0.7 * dx + 0.3 * (cysf - cur.y);
            return new int[] { (int) dx, (int) dy };
        }
        return null;
    }

    public RANK findRankSoldier() throws GameActionException {
        RANK rank = findRank();
        if (rank != RANK.DEFENDER && rank != RANK.DEFAULT) {
            return RANK.DEFAULT;
        } else {
            return rank;
        }
    }

    public void huntTarget() throws GameActionException {
        // if target is within 3 tiles, do not move closer, otherwise move closer
        MapLocation cur = rc.getLocation();
        RobotInfo[] friendlySoldiers = rc.senseNearbyRobots(-1, rc.getTeam());
        int[] dir = new int[2];
        int dx = 0;
        int dy = 0;
        int num_soldiers = 0;
        if (friendlySoldiers != null) {
            for (RobotInfo robot : friendlySoldiers) {
                if (robot.type == RobotType.SOLDIER) {
                    dir[0] += (target.x - robot.location.x);
                    dir[1] += (target.y - robot.location.y);
                    num_soldiers++;
                }
            }
        }
        dx += (target.x - cur.x);
        dy += (target.y - cur.y);
        dir[0] = (dir[0] + dx) / (num_soldiers + 1);
        dir[1] = (dir[1] + dy) / (num_soldiers + 1);
        if (dir[0] != 0 || dir[1] != 0)
            exploratoryDir = dir;
        else {
            dir = new int[] { dx, dy };
            exploratoryDir = dir;
        }
        if (rc.getLocation().distanceSquaredTo(target) <= 9)
            return;
        else
            moveInDirection(dir);
    }

    public boolean shouldDefendArchon() throws GameActionException {
        // Priority 2 - Defend.
        threatenedArchons = findThreatenedArchons();
        if (threatenedArchons != null) {
            for (MapLocation archon : threatenedArchons) {
                if (rc.getLocation().distanceSquaredTo(archon) <= DRUSH_RSQR) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean shouldFlee() throws GameActionException {
        // Priority 1 - Don't die.
        int[] potFleeDir = getPotentialFleeDirection();
        boolean validFlee = potFleeDir != null;
        if (!validFlee && stopFleeingRound == roundNum) {
            exploratoryDir = getExploratoryDir(5);
        }
        if (validFlee || stopFleeingRound <= roundNum) {
            if (validFlee)
                fleeDirection = potFleeDir;
            // keep fleeing for two moves (2 rounds per move)
            if (stopFleeingRound <= roundNum) {
                stopFleeingRound = roundNum + 4;
            }
            return true;
        }
        return false;
    }

    public boolean shouldAttackArchon() throws GameActionException {
        // Priority 3 - Kill Archons.
        return (detectArchon() || senseArchon()) && (rc.getLocation().distanceSquaredTo(archon_target) <= ARUSH_RSQR);
    }

    public MapLocation closestThreatenedArchon() throws GameActionException {
        MapLocation closest = threatenedArchons[0];
        MapLocation me = rc.getLocation();
        int d = me.distanceSquaredTo(closest);
        // only find closest archon if there is more then one
        if (threatenedArchons.length > 1) {
            for (int i = threatenedArchons.length - 1; --i > 0;) {
                int distance = me.distanceSquaredTo(threatenedArchons[i]);
                if (distance < d) {
                    d = distance;
                    closest = threatenedArchons[i];
                }
            }
        }
        return closest;
    }

    public boolean archonDied() throws GameActionException {
        RobotInfo home;
        if (rc.canSenseLocation(homeArchon)) {
            home = rc.senseRobotAtLocation(homeArchon);
            return (home == null || home.type != RobotType.ARCHON);
        }
        return false;
    }

    /**
     * waitAtDist() stays near the home archon
     **/
    public void waitAtDist(MapLocation loc, int idealDistSquared, boolean shouldRepel) throws GameActionException {
        // stays at around an ideal dist
        MapLocation myLocation = rc.getLocation();
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getType().visionRadiusSquared, rc.getTeam());
        int buffer = 4;
        // rc.setIndicatorString("" +
        // Math.abs(myLocation.distanceSquaredTo(homeArchon)-idealDistSquared));
        if (Math.abs(myLocation.distanceSquaredTo(loc) - idealDistSquared) <= buffer) {
            return; // you're already in range
        }
        int[] costs = new int[8];
        for (int i = 0; i < 8; i++) {
            MapLocation newLocation = myLocation.add(directions[i]);
            int cost = 0;
            if (!rc.onTheMap(newLocation)) {
                cost = 999999;
            } else {
                cost = 10 * Math.abs(newLocation.distanceSquaredTo(loc) - idealDistSquared);
                if (shouldRepel) { // don't go near fellow ally soldiers
                    for (RobotInfo robot : nearbyAllies) {
                        if (robot.type == rc.getType()) {
                            cost -= newLocation.distanceSquaredTo(robot.location); // trying to maximize distance
                        }
                    }
                }
                // cost += rc.senseRubble(newLocation);
            }

            costs[i] = cost;

        }
        /*
         * String s = " ";
         * for (int i =0; i < 8; i++) s += directions[i] + ": " + costs[i] + " ";
         * rc.setIndicatorString(s);
         */
        int cost = 99999;
        Direction optimalDir = null;
        for (int i = 0; i < directions.length; i++) {
            Direction dir = directions[i];
            if (rc.canMove(dir)) {
                if (costs[i] < cost) {
                    cost = costs[i];
                    optimalDir = dir;
                }
            }
        }
        if (optimalDir != null) {
            if (rc.canMove(optimalDir)) {
                // rc.setIndicatorString("here2");
                rc.move(optimalDir);
            }
        }

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

    public void moveToEnemySoldiers() throws GameActionException {
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
        if (num_enemies == 0)
            fuzzyMove(homeArchon);
        else
            fuzzyMove(new MapLocation((int) (cx / num_enemies), (int) (cy / num_enemies)));
    }

    public boolean isLowHealth() throws GameActionException {
        if (rc.getHealth() < 20) {
            return true;
        } else {
            return false;
        }
    }

    public Direction usefulDir() throws GameActionException {
        MapLocation cur = rc.getLocation();
        Direction d = rc.getLocation().directionTo(homeArchon); // placeholder
        MapLocation center = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);
        if (center.x - cur.x > 0) {
            if (center.y - cur.y > 0) {
                d = Direction.NORTHEAST;
            } else {
                d = Direction.SOUTHEAST;
            }
        } else {
            if (center.y - cur.y > 0) {
                d = Direction.NORTHWEST;
            } else {
                d = Direction.SOUTHWEST;
            }
        }
        // rc.setIndicatorString(d.dx + " " + d.dy);
        Direction[] dirs = { d, d.rotateLeft(), d.rotateRight() };
        return dirs[rng.nextInt(dirs.length)];
    }

    /**
     * @param attackMiners
     * @return Whether or not an attack was attempted
     * @throws GameActionException
     */
    public boolean attemptAttack(boolean attackMiners) throws GameActionException {
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(RobotType.SOLDIER.actionRadiusSquared, rc.getTeam().opponent());
        int weakestSoldierHealth = 100000;
        int weakestMinerHealth = 100000;
        RobotInfo weakestSoldier = null;
        RobotInfo weakestMiner = null;
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
                if (bot.type == RobotType.ARCHON) {
                    archon = bot;
                }
            }
            if (weakestSoldier != null) {
                if (rc.canAttack(weakestSoldier.location)) {
                    rc.attack(weakestSoldier.location);
                    return true;
                }
            } else if (weakestMiner != null && attackMiners) {
                if (rc.canAttack(weakestMiner.location)) {
                    rc.attack(weakestMiner.location);
                    return true;
                }
            } else if (archon != null) {
                if (rc.canAttack(archon.location)) {
                    rc.attack(archon.location);
                    return true;
                }
            }
        }
        return false;
    }

    public void initialize() {
        DRUSH_RSQR = (int) ((double) mapArea / 9.0);
        ARUSH_RSQR = (int) ((double) mapArea / 4.0);
    }
}
