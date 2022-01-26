package opuntia_sage;

import battlecode.common.*;

import java.util.*;

public class Sage extends Unit {

    enum MODE {
        EXPLORATORY, //at the start of the game, before anyone has found anything
        HUNTING,
        SEARCHING_ENEMIES, //when u have already found enemies
        FLEE,
        DEFENSIVE_RUSH,
        DYING,
        CHARGING,
        ;
    }

    boolean attacked = false;
    int round_num = 0;

    RANK rank;

    MapLocation[] threatenedArchons;

    private int[] fleeDirection = {Integer.MAX_VALUE, Integer.MAX_VALUE};
    private int stopFleeingRound = -1;
    private int DRUSH_RSQR = 400;
    private int ARUSH_RSQR = 900;

    MODE mode;

    //for explore mode only
    private MapLocation exploreLoc;
    private int[] exploratoryDir;

    //for attacking and searching enemies modes
    private MapLocation target = null;
    private int[] lastAttackDir = null;
    private MapLocation chargeTarget;

	public Sage(RobotController rc) throws GameActionException {
        super(rc);
        initialize();
        exploreLoc = getInitialExploratoryLocation();
    }
    @Override
    public void run() throws GameActionException {
        super.run();
        round_num = rc.getRoundNum();
        radio.updateCounter();
        findTargets();
        findSageTargets();
        attacked = attemptAttack();
        senseMiningArea();
        senseFriendlySoldiersArea();
        mode = determineMode();
        visualize();
        switch (mode) {
            case EXPLORATORY:
                if (soldierBehindMe()) {
                    if (adjacentToEdge()){
                        exploratoryDir = flip(exploratoryDir);
                        exploreLoc = scaleToEdge(exploratoryDir);
                    }
                    moveToLocation(exploreLoc);
                }
                break;
            case CHARGING:
                huntChargeTarget();
                if (!attacked) attemptAttack();
                chargeTarget = null;
                break;
            case HUNTING:
                huntTarget();
                target = null;
                break;
            case SEARCHING_ENEMIES:
                if (adjacentToEdge()) { //TODO: bots occasionally get stuck somehow
                    lastAttackDir = flip(lastAttackDir);
                }
                moveInDirection(lastAttackDir);
                break;
            case DEFENSIVE_RUSH:
                defensiveMove();
                break;
            case FLEE:
                moveLowRubble(fleeDirection);
                break;
            default:
                break;
        }

        if (!attacked) attemptAttack();
        visualize();
    }

    public boolean attemptAttack() throws GameActionException {
        if (target == null && chargeTarget == null) return false;
        /* if (rc.getLocation().distanceSquaredTo(target) <= 16 || rc.getHealth() < 30) */
        if (chargeTarget != null) {
            if (rc.getLocation().distanceSquaredTo(chargeTarget) <= 12 || rc.getHealth() < 30) {}
            else return false;
        }
        ATTACK attack = determineSageAttack();
        executeAttack(attack);
        if (attack == ATTACK.CHARGE) {
            MapLocation avgEnemyLoc = getAvgEnemyPos();
            if (avgEnemyLoc != null) {
                broadcastSageTarget(avgEnemyLoc);
            }
        }
        if (attack != ATTACK.NONE) {
            return true;
        }
        return false;
    }

    public void executeAttack(ATTACK attack) throws GameActionException {
        switch (attack) {
            case DEFAULT:
                if (attackTarget != null) {
                    if (rc.canAttack(attackTarget)) rc.attack(attackTarget);
                }
                break;
            case CHARGE:
                if (rc.canEnvision(AnomalyType.CHARGE)) rc.envision(AnomalyType.CHARGE);
                break;
            case FURY:
                if (rc.canEnvision(AnomalyType.FURY)) rc.envision(AnomalyType.FURY);
                break;
            default:
                break;
        }
    }

    /* public void findSageTargets() throws GameActionException {
        int data;
        int closestDist = 100000;
        MapLocation cur = rc.getLocation();
        MapLocation closestTarget = null;
        for (int i = 0; i < CHANNEL.NUM_TARGETS; i++) {
            data = rc.readSharedArray(CHANNEL.SAGE_TARGET.getValue() + i);
            if (data != 0) {
                int x = data/64;
                int y = data%64;
                // System.out.println("I received an enemy at " + x*4 + " " + y*4 + " on round " + round_num);
                MapLocation potentialTarget = new MapLocation(x, y);
                if (cur.distanceSquaredTo(potentialTarget) < closestDist) {
                    closestDist = cur.distanceSquaredTo(potentialTarget);
                    closestTarget = potentialTarget;
                }
            }
        }

        // finds closest target, and advances towards it.
        if (closestTarget != null) {
            if (cur.distanceSquaredTo(closestTarget) <= mapArea / 8) {
                target = closestTarget;
            }
        }
    } */

    public void visualize() throws GameActionException {
        if (mode == MODE.EXPLORATORY){
            rc.setIndicatorDot(exploreLoc, 100, 100, 0);
        }
        else if (chargeTarget != null) {
            rc.setIndicatorString("CHARGE TARGET: " + chargeTarget.toString());
            rc.setIndicatorLine(rc.getLocation(), chargeTarget, 255, 255, 255);
        }
        else if (target != null) {
            if (mode != MODE.FLEE) rc.setIndicatorString("TARGET: " + target.toString() + " MODE: " + mode.toString());
            else rc.setIndicatorString("TARGET: " + target.toString() + " MODE: FLEE " + "FLEEROUND: " + stopFleeingRound);
            rc.setIndicatorLine(rc.getLocation(), target, 0, 0, 100);
        }
        else {
            if (mode != MODE.FLEE) rc.setIndicatorString("TARGET: null MODE: " + mode.toString());
            else rc.setIndicatorString("TARGET: null MODE: FLEE " + "FLEEROUND: " + stopFleeingRound);
        }
        rc.setIndicatorString("Cooldown Turns: " + rc.getActionCooldownTurns());
    }

    public MODE determineMode() throws GameActionException {
        
        // Priority 1 - Defend.
        threatenedArchons = findThreatenedArchons();
        if (threatenedArchons != null) {
            for (MapLocation archon: threatenedArchons) {
                if (rc.getLocation().distanceSquaredTo(archon) <= DRUSH_RSQR) {
                    return MODE.DEFENSIVE_RUSH;
                }
            }
        }

        // Priority 2 - Don't die.
        int[] potFleeDir = fleeDirection();
        boolean validFlee = (potFleeDir[0] != Integer.MAX_VALUE && potFleeDir[1] != Integer.MAX_VALUE);
        if (!validFlee && stopFleeingRound == round_num) {
            exploreLoc = getInitialExploratoryLocation(); 
            lastAttackDir = null;
        }
        if (validFlee || stopFleeingRound > round_num) {
            if (validFlee) fleeDirection = potFleeDir;
            // keep fleeing for two moves (2 rounds per move)
            if (stopFleeingRound <= round_num) {
                stopFleeingRound = round_num + 6;
            }
            return MODE.FLEE;
        }

        if (chargeTarget != null) {
            return MODE.CHARGING;
        }
        
        // Priority 3 - Hunt enemies.
        if (target != null) {
            return MODE.HUNTING;
        }
        else if (lastAttackDir != null){
            return MODE.SEARCHING_ENEMIES;
        }
        else return MODE.EXPLORATORY;
    }

    /**
     * getInitialExploratoryLocation() gets the location when you extend the vector from your location 
     * to the center to the edge
     **/
    public MapLocation getInitialExploratoryLocation(){
        MapLocation my = rc.getLocation();
        MapLocation center = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2);
        exploratoryDir = new int[]{center.x - my.x, center.y - my.y};
        if (!my.equals(center)){
            return scaleToEdge(exploratoryDir);
        }
        else {
            System.out.println("YOU REACHED A CASE THAT SHOULD BE IMPLEMENTED");
            return null; //TODO
        }
    }

    public boolean isBehind(MapLocation loc){
        MapLocation my = rc.getLocation();
        int[] v = new int[]{loc.x - my.x, loc.y - my.y};
        int dotProduct = v[0]*exploratoryDir[0] + v[1]*exploratoryDir[1];
        return (dotProduct < 0); 
    }

    public boolean soldierBehindMe(){
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(15, rc.getTeam());
        for (RobotInfo r : nearbyBots){
            if (r.type == RobotType.SOLDIER || r.type == RobotType.SAGE){
                if (isBehind(r.location)) return true;
            }
        }
        return false;
    }

    public int[] fleeDirection() throws GameActionException{
        MapLocation cur = rc.getLocation();
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(-1);
        double cxse = 0;
        double cyse = 0;
        int numEnemies = 0;
        int numEnemyHits = 0;
        int numFriendHits = (rc.getHealth() + 2) / 3;
        int numFriends = 0;
        for (RobotInfo bot: nearbyBots) {
            if (bot.team == rc.getTeam()) {
                if (bot.type == RobotType.SOLDIER) {
                    numFriendHits += ((bot.health + 2) / 3);
                    numFriends++;
                }
            }
            else if (bot.team == rc.getTeam().opponent())
                if (bot.type == RobotType.SOLDIER) {
                    cxse += bot.location.x;
                    cyse += bot.location.y;
                    numEnemyHits += ((bot.health + 2) / 3);
                    numEnemies++;
                }
        }
        if (numEnemies == 0) return new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE};

        if (numEnemies > 0) {
            cxse /= numEnemies;
            cyse /= numEnemies;
        }

        double unit_difference = (double) (numFriends + 1 - numEnemies);
        double ratio;

        if (((numFriends + 1) / numEnemies) > 1) {
            ratio = ((numFriends + 1) / numEnemies);
        }
        else {
            ratio = numEnemies / (numFriends + 1);
        }

        double a = 6 * ratio;
        int unit_advantage = (int) (a * Math.pow(unit_difference,2) * Math.signum(unit_difference));

        // System.out.println("Unit advantage: " + unit_advantage + " Ratio: " + ratio + " numFriendHits " + numFriendHits + " numEnemyHits " + numEnemyHits + "round_num " + round_num);

        if (numFriendHits + unit_advantage < numEnemyHits) {
            double dx = -(cxse - cur.x) * 3;
            double dy = -(cyse - cur.y) * 3;
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

    public void findTargets() throws GameActionException {
        int data;
        int closestDist = 100000;
        MapLocation cur = rc.getLocation();
        MapLocation closestTarget = null;
        for (int i = 0; i < CHANNEL.NUM_TARGETS; i++) {
            data = rc.readSharedArray(CHANNEL.TARGET.getValue() + i);
            if (data != 0) {
                boolean isMiner = ((data >> 12) > 0);
                int x = (data >> 6) & 63;
                int y = data & 63;
                // System.out.println("I received an enemy at " + x*4 + " " + y*4 + " on round " + round_num);
                MapLocation potentialTarget = new MapLocation(x, y);
             //   System.out.println("sage read location " + isMiner + " " + x + " " + y);
                if (cur.distanceSquaredTo(potentialTarget) < closestDist && !isMiner) {
                    closestDist = cur.distanceSquaredTo(potentialTarget);
                    closestTarget = potentialTarget;
                }
            }
        }

        // finds closest target, and advances towards it.
        if (closestTarget != null) {
            if (cur.distanceSquaredTo(closestTarget) <= mapArea / 8) {
                target = closestTarget;
            }
            lastAttackDir = new int[]{closestTarget.x - cur.x, closestTarget.y - cur.y};
            lastAttackDir = scaleToSize(lastAttackDir);
            // wanders in direction of target
        }
    }

    public void findSageTargets() throws GameActionException {
        if (rc.getActionCooldownTurns() > 50) return;
        int data;
        int closestDist = 100000;
        MapLocation cur = rc.getLocation();
        MapLocation closestTarget = null;
        for (int i = 0; i < 5; i++) {
            data = rc.readSharedArray(CHANNEL.SAGE_TARGET.getValue() + i);
            if (data != 0) {
                int x = data/64;
                int y = data%64;
                // System.out.println("I received an enemy at " + x*4 + " " + y*4 + " on round " + round_num);
                MapLocation potentialTarget = new MapLocation(x, y);
                if (cur.distanceSquaredTo(potentialTarget) < closestDist) {
                    closestDist = cur.distanceSquaredTo(potentialTarget);
                    closestTarget = potentialTarget;
                }
            }
        }

        // finds closest target, and advances towards it.
        if (closestTarget != null) {
            if (cur.distanceSquaredTo(closestTarget) <= mapArea / 8) {
                chargeTarget = closestTarget;
            }
            lastAttackDir = new int[]{closestTarget.x - cur.x, closestTarget.y - cur.y};
            lastAttackDir = scaleToSize(lastAttackDir);
            // wanders in direction of target
        }
    }

    public void huntTarget() throws GameActionException {
        MapLocation cur = rc.getLocation();
        if (rc.getLocation().distanceSquaredTo(target) > RobotType.SAGE.actionRadiusSquared) {
            moveToLocation(target);
        }
        else if (rc.getActionCooldownTurns() <= 30) {
            moveLowRubble(new int[] {target.x - cur.x, target.y - cur.y}, 15);
        }
        else {
            moveLowRubble(new int[] {-target.x + cur.x, -target.y + cur.y}, 15);
        }
    }

    public void huntChargeTarget() throws GameActionException {
        MapLocation cur = rc.getLocation();
        if (rc.getLocation().distanceSquaredTo(chargeTarget) > RobotType.SAGE.actionRadiusSquared) {
            moveToLocation(chargeTarget);
        }
        else if (rc.getActionCooldownTurns() <= 30) {
            moveLowRubble(new int[] {chargeTarget.x - cur.x, chargeTarget.y - cur.y}, 15);
        }
    }

    public Direction findLowRubble() throws GameActionException {
        MapLocation cur = rc.getLocation();
        int lowest_rubble = 1 + rc.senseRubble(cur) / 10;
        int rubble;
        Direction bestDir = null;
        for (int i = 0; i < 8; i++) {
            if (!rc.canMove(directions[i])) continue;
            rubble = 1 + rc.senseRubble(cur.add(directions[i])) / 10;
            if (rubble < lowest_rubble && rc.canMove(directions[i])) {
                lowest_rubble = rubble;
                bestDir = directions[i];
            }
        }
        return bestDir;
    }

    public void moveLowRubble(int[] dir) throws GameActionException {
        moveLowRubble(dir, 20);
    }

    public void moveLowRubble(int[] dir, int threshold) throws GameActionException {
        MapLocation cur = rc.getLocation();
        Direction d = cur.directionTo(new MapLocation(cur.x + dir[0], cur.y + dir[1]));
        Direction[] sorted_dirs = {d, d.rotateLeft(), d.rotateRight(), d.rotateLeft().rotateLeft(), d.rotateRight().rotateRight(), d.opposite().rotateRight(), d.opposite().rotateLeft(), d.opposite()};
        int a = 6;
        int lowestCost = a * (1 + (rc.senseRubble(rc.getLocation()) / 10)) + threshold;
        Direction bestDir = null;
        for (int i = 0; i < 8; i++) {
            if (!rc.canMove(sorted_dirs[i])) continue;
            MapLocation loc = cur.add(sorted_dirs[i]);

            int cost = 0;
            cost += (int) a * (1 + (rc.senseRubble(loc) / 10)) ;
            // Preference tier for moving towards target
            if (i >=1){
                cost+=5;
            }
            if (i >= 3) {
                cost += 15;
            }
            if (i >=5){
                cost+=30;
            }
            if (cost < lowestCost) {
                lowestCost = cost;
                bestDir = sorted_dirs[i];
            }
        }
        if (bestDir != null) rc.move(bestDir);
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
        // if you don't see the enemy, and you're not close to the archon, move towards it
        if (rc.getLocation().distanceSquaredTo(closest) > 36 || target == null) {
            moveToLocation(closest);
        }
        else {
            if (target != null) huntTarget();
        }
    }

    public boolean archonDied() throws GameActionException{
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

    public boolean isLowHealth() throws GameActionException {
        if (rc.getHealth() < 20) {
            return true;
        }
        else {
            return false;
        }
    }

    public void initialize() {
        DRUSH_RSQR = (int) ((double) mapArea / 9.0);
        ARUSH_RSQR = (int) ((double) mapArea / 4.0);
    }
}
