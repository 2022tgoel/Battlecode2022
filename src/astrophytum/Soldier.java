package astrophytum;

import battlecode.common.*;

import java.util.*;

public class Soldier extends Unit {

    enum MODE {
        EXPLORATORY,
        HUNTING,
        ARCHON_RUSH,
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

	public Soldier(RobotController rc) throws GameActionException {
        super(rc);
        rank = findRankSoldier();
    }
    @Override
    public void run() throws GameActionException {
        round_num = rc.getRoundNum();
        attacked = attemptAttack(false);
        mode = determineMode();
        switch (mode) {
            case EXPLORATORY:
                moveInDirection(exploratoryDir);
                break;
            case ARCHON_RUSH:
                approachArchon();
                break;
            case HUNTING:
                huntTarget();
                target = null;
                break;
            case DEFENSIVE_RUSH:
                defensiveMove();
            default:
                break;
        }
        if (!attacked) attemptAttack(true);
        if (adjacentToEdge()) {
            exploratoryDir = getExploratoryDir(5);
        }
        rc.setIndicatorString("MODE: " + mode.toString());
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
        if (rc.getLocation().distanceSquaredTo(target) <= 9) {
            int[] dir = new int[]{target.x - cur.x, target.y - cur.y};
            exploratoryDir = dir;
            return;
        }
        else {
            int[] dir = new int[]{target.x - cur.x, target.y - cur.y};
            moveInDirection(dir);
            exploratoryDir = dir;
        }
    }

    public MODE determineMode() throws GameActionException {
        boolean archonDetected = detectArchon() || senseArchon();
        threatenedArchons = findThreatenedArchons();
        if (threatenedArchons != null) {
            for (MapLocation archon: threatenedArchons) {
                if (rc.getLocation().distanceSquaredTo(archon) <= 400) {
                    return MODE.DEFENSIVE_RUSH;
                }
            }
        }
        if (archonDetected) {
            if (rc.getLocation().distanceSquaredTo(archon_target) <= 900)
                return MODE.ARCHON_RUSH;
        }
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
        boolean enemy_soldiers = false;
        boolean enemy_miners = false;
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(RobotType.SOLDIER.actionRadiusSquared, rc.getTeam().opponent());
        // if there are any nearby enemy robots, attack the one with the least health
        if (nearbyBots.length > 0) {
            RobotInfo weakestBot = nearbyBots[0];
            for (RobotInfo bot : nearbyBots) {
                if (bot.type == RobotType.SOLDIER)
                    if (bot.health < weakestBot.health) {
                        weakestBot = bot;
                    }
                    enemy_soldiers = true;
            }
            if (enemy_soldiers) {
                if (rc.canAttack(weakestBot.location)) rc.attack(weakestBot.location);
            }
            else {
                if (attackMiners) {
                    for (RobotInfo bot : nearbyBots) {
                        if (bot.type == RobotType.MINER)
                            if (bot.health < weakestBot.health) {
                                weakestBot = bot;
                            }
                            enemy_miners = true;

                    }
                    if (rc.canAttack(weakestBot.location)) rc.attack(weakestBot.location);
                }
            }
            target = weakestBot.location;
        }
        if ((!enemy_miners || !attackMiners)  && !enemy_soldiers) {
            return false;
        }
        else {
            return true;
        }
    }
}
