package triangle;

import battlecode.common.*;

import java.util.*;

public class Soldier extends Unit {

    enum MODE {
        EXPLORATORY,
        HUNTING,
        ARCHON_RUSH,
        FLEE,
        LOW_HEALTH,
        DEFENSIVE_RUSH,
        WAITING,
        CONVOY
        ;
    }

    boolean attacked = false;
    boolean convoy_found = false;
    int counter = 0;
    int exploratoryDirUpdateRound = 0;
    int threatDetectedRound = 10000;
    int round_num = 0;
    int dRushChannel = -1;

    int convoyDeployRound = 10000;

    RANK rank;
    MODE mode = MODE.EXPLORATORY;

    MapLocation[] threatenedArchons;
    MapLocation target;

    int[] exploratoryDir = getExploratoryDir(5);
    private int[] fleeDirection = {Integer.MAX_VALUE, Integer.MAX_VALUE};
    private int stopFleeingRound = 10000;
    private int DRUSH_RSQR = 400;
    private int ARUSH_RSQR = 900;

	public Soldier(RobotController rc) throws GameActionException {
        super(rc);
        rank = findRankSoldier();
        initialize();
    }
    @Override
    public void run() throws GameActionException {
        round_num = rc.getRoundNum();
        updateCount();
        attacked = attemptAttack(false);
        mode = determineMode();
        switch (mode) {
            case EXPLORATORY:
                moveInDirection(exploratoryDir);
                break;
            case ARCHON_RUSH:
                approachArchon();
                target = null;
                break;
            case HUNTING:
                huntTarget();
                target = null;
                break;
            case DEFENSIVE_RUSH:
                defensiveMove();
            case FLEE:
                moveInDirection(fleeDirection);
                break;
            default:
                break;
        }
        if (!attacked) attemptAttack(true);
        if (adjacentToEdge()) {
            exploratoryDir = getExploratoryDir(5);
        }
        senseMiningArea();
        rc.setIndicatorString("MODE: " + mode.toString());
    }

    public int[] fleeDirection() throws GameActionException{
        MapLocation cur = rc.getLocation();
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(-1);
        double cxsf = 0;
        double cysf = 0;
        double cxse = 0;
        double cyse = 0;
        int numEnemies = 0;
        int maxHealthEnemy = 0;
        int numFriends = 0;
        for (RobotInfo bot: nearbyBots) {
            if (bot.team == rc.getTeam()) {
                if (bot.type == RobotType.SOLDIER) {
                    cxsf += bot.location.x;
                    cysf += bot.location.y;
                    numFriends++;
                }
            }
            else if (bot.team == rc.getTeam().opponent())
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
        // for example, if soldier has 7,8, or 9 health this expression spits out 3, as the soldier can only survive 3 hits.
        int soldierHitsLeft = (rc.getHealth() + 2) / 3;
        int enemyHitsLeft = (maxHealthEnemy + 2) / 3;
        boolean winsInteraction = false;
        // soldier strikes second, so must have more hits remaining
        if (attacked) winsInteraction = (soldierHitsLeft > enemyHitsLeft);
        else winsInteraction = (soldierHitsLeft >= enemyHitsLeft);
        // count yourself
        if (numEnemies > (numFriends + 1) || (numEnemies == 1 && !winsInteraction)) {
            double dx = -(cxse - cur.x);
            double dy = -(cyse - cur.y);
            // more attracted
            // dx = 0.7 * dx + 0.3 * (cxsf - cur.x);
            // dy = 0.7 * dx + 0.3 * (cysf - cur.y);
            return new int[]{(int) dx, (int) dy};
        }
        return new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE}; 
    }

    public RANK findRankSoldier() throws GameActionException{
        RANK new_rank = findRank();
        if (new_rank != RANK.DEFENDER && new_rank != RANK.DEFAULT) {
            return RANK.DEFAULT;
        }
        else {
            return new_rank;
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
            for (RobotInfo robot: friendlySoldiers) {
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
        if (dir[0] != 0 || dir[1] != 0) exploratoryDir = dir;
        else  {
            dir = new int[]{dx, dy};
            exploratoryDir = dir;
        }
        if (rc.getLocation().distanceSquaredTo(target) <= 9) return;
        else moveInDirection(dir);
    }

    public MODE determineMode() throws GameActionException {

        // Priority 2 - Defend.
        threatenedArchons = findThreatenedArchons();
        if (threatenedArchons != null) {
            for (MapLocation archon: threatenedArchons) {
                if (rc.getLocation().distanceSquaredTo(archon) <= DRUSH_RSQR) {
                    return MODE.DEFENSIVE_RUSH;
                }
            }
        }

        // Priority 1 - Don't die.
        int[] potFleeDir = fleeDirection();
        boolean validFlee = (potFleeDir[0] != Integer.MAX_VALUE && potFleeDir[1] != Integer.MAX_VALUE);
        if (!validFlee && stopFleeingRound == round_num) {
            exploratoryDir = getExploratoryDir(5);
        }
        if (validFlee || stopFleeingRound <= round_num) {
            if (validFlee) fleeDirection = potFleeDir;
            // keep fleeing for two moves (2 rounds per move)
            if (stopFleeingRound <= round_num) {
                stopFleeingRound = round_num + 4;
            }
            return MODE.FLEE;
        }
        // Priority 3 - Kill Archons.
        boolean archonDetected = detectArchon() || senseArchon();
        if (archonDetected) {
            if (rc.getLocation().distanceSquaredTo(archon_target) <= ARUSH_RSQR)
                return MODE.ARCHON_RUSH;
        }
        // Priority 4 - Hunt enemies.
        else if (target != null) {
            return MODE.HUNTING;
        }

        return MODE.EXPLORATORY;
    }

    public void defensiveMove() throws GameActionException{
        MapLocation closest = threatenedArchons[0];
        int min_dist = Integer.MAX_VALUE;
        // only find closest archon if there is more then one
        if (threatenedArchons.length > 1) {
            for (MapLocation loc: threatenedArchons) {
                if (loc.distanceSquaredTo(rc.getLocation()) < min_dist) {
                    min_dist = loc.distanceSquaredTo(rc.getLocation());
                    closest = loc;
                }
            }
        }
        fuzzyMove(closest);
    }

    public boolean archonDied() throws GameActionException{
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
    public void waitAtDist(MapLocation loc, int idealDistSquared, boolean shouldRepel) throws GameActionException{
        //stays at around an ideal dist
        MapLocation myLocation = rc.getLocation();
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getType().visionRadiusSquared, rc.getTeam());
        int buffer = 4;
        // rc.setIndicatorString("" + Math.abs(myLocation.distanceSquaredTo(homeArchon)-idealDistSquared));
        if (Math.abs(myLocation.distanceSquaredTo(loc)-idealDistSquared) <= buffer){
            return; //you're already in range
        }
        int[] costs = new int[8];
        for (int i = 0; i < 8; i++){
            MapLocation newLocation = myLocation.add(directions[i]);
            int cost = 0;
            if (!rc.onTheMap(newLocation)) {
                cost = 999999;
            }
            else {
                cost = 10*Math.abs(newLocation.distanceSquaredTo(loc)-idealDistSquared);
                if (shouldRepel){ //don't go near fellow ally soldiers
                    for (RobotInfo robot : nearbyAllies) {
                        if (robot.type == rc.getType()) {
                            cost -= newLocation.distanceSquaredTo(robot.location); //trying to maximize distance
                        }
                    }
                }
                //cost += rc.senseRubble(newLocation);
            }
            
            costs[i] = cost;

        }
        /*
        String s = " ";
        for (int i =0; i < 8; i++) s += directions[i] + ": " + costs[i] + " ";
        rc.setIndicatorString(s);
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

    // now has a boolean flag to tell it to go to the archon
    public MapLocation[] findThreatenedArchons() throws GameActionException {
        int data;
        MapLocation[] archons = new MapLocation[4];
        int numThreatenedArchons = 0;
        for (int i = 0; i < 4; i++) {
            // rc.writeSharedArray(, value);
            data = rc.readSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + i);
            int w = data / 4096;
            // go through channels until you find an empty one to communicate with.
            if (w != 0) {
                int x = (data - w * 4096) / 64;
                int y = data % 64;
                if (validCoords(x, y)) {
                    archons[numThreatenedArchons] = new MapLocation(x, y);
                    numThreatenedArchons++;
                }
            }
        }

        if (numThreatenedArchons == 0) {
            return null;
        }
        else {
            // only return threatened archons.
            MapLocation[] threatenedArchons = new MapLocation[numThreatenedArchons];
            for (int i = 0; i < numThreatenedArchons; i++) {
                threatenedArchons[i] = archons[i];
            }
            return threatenedArchons;
        }
    }

    public void moveToEnemySoldiers() throws GameActionException{
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
        if (num_enemies ==0) fuzzyMove(homeArchon);
        else fuzzyMove(new MapLocation((int) (cx / num_enemies), (int) (cy / num_enemies)));
    }

    public boolean isLowHealth() throws GameActionException {
        if (rc.getHealth() < 20) {
            return true;
        }
        else {
            return false;
        }
    }

    public Direction usefulDir() throws GameActionException {
        MapLocation cur = rc.getLocation();
        Direction d = rc.getLocation().directionTo(homeArchon); // placeholder
        MapLocation center = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2);
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
        Direction[] dirs = {d, d.rotateLeft(), d.rotateRight()};
        return dirs[rng.nextInt(dirs.length)];
    }

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
            }
            else if (weakestMiner != null && attackMiners) {
                if (rc.canAttack(weakestMiner.location)) {
                    rc.attack(weakestMiner.location);
                    return true;
                }
            }
            else if (archon != null) {
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
