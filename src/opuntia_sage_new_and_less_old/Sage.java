package opuntia_sage_new_and_less_old;

import battlecode.common.*;

import java.util.*;

public class Sage extends Unit {

    enum MODE {
        EXPLORATORY, //at the start of the game, before anyone has found anything
        HUNTING,
        SEARCHING_ENEMIES, //when u have already found enemies
        FLEE,
        DEFENSIVE_RUSH,
        HIGH_COOLDOWN,
        ;
    }

    enum ATTACK {
        NONE,
        DEFAULT, 
        CHARGE,
        FURY
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
    private MapLocation attackTarget;

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
        attacked = attemptAttack();
        findTargets();
        senseMiningArea();
        senseFriendlySoldiersArea();
        mode = determineMode();
        visualize();
        rc.setIndicatorString("got here");
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
            case HIGH_COOLDOWN:
                fleeFromAttackers();
                /*
                if (target != null) {
                    if (rc.getLocation().distanceSquaredTo(target) <= RobotType.SAGE.visionRadiusSquared + 4) {
                        moveLowRubble(new int[]{-target.x + rc.getLocation().x, -target.y + rc.getLocation().y});
                    }
                    else if ( rc.getLocation().distanceSquaredTo(target) > RobotType.SAGE.visionRadiusSquared + 16) {
                        moveToLocation(target);
                    }
                }*/
                break;
            case HUNTING:
                rc.setIndicatorString("got here1");
                huntTarget();
                rc.setIndicatorString("got here2");
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
        rc.setIndicatorString("mode: " + mode);
        if (!attacked) attemptAttack();
    }

    public boolean attemptAttack() throws GameActionException {
        ATTACK attack = determineAttack();
        executeAttack(attack);
        if (attack != ATTACK.NONE) {
            // System.out.println("ATTEMPTED " + attack.toString() + " " + rc.getRoundNum());
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

    public ATTACK determineAttack() throws GameActionException {
        if (rc.getActionCooldownTurns() > 0) return ATTACK.NONE;
        if (rc.senseNearbyRobots(RobotType.SAGE.actionRadiusSquared, rc.getTeam().opponent()) == null) return ATTACK.NONE;
        
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(RobotType.SAGE.actionRadiusSquared, rc.getTeam().opponent());

        int numeSoldiers = 0;
        int numeSages = 0;

        int enemyHealth = 0;
        int numFriends = 0;
        int friendHealth = rc.getHealth();

        boolean canOneShot = false;

        int highestSub45Health = -1;
        RobotInfo highestSub45 = null;
        int maxHealth = -1;
        RobotInfo maxHealthBot = null;

        int[] soldierHealths  = new int[nearbyBots.length];
        int[] sageHealths  = new int[nearbyBots.length];
        for (RobotInfo bot: nearbyBots) {
            if (bot.type == RobotType.SOLDIER) {
                enemyHealth += bot.health;
                soldierHealths[numeSoldiers] = bot.health;
                numeSoldiers++;

                if (bot.health <= 45) {
                    if (highestSub45Health < bot.health) {
                        highestSub45Health = bot.health;
                        highestSub45 = bot;
                    }
                    canOneShot = true;
                }
                else {
                    if (maxHealth < bot.health) {
                        maxHealth = bot.health;
                        maxHealthBot = bot;
                    }
                }
            }
            else if (bot.type == RobotType.SAGE) {
                enemyHealth += bot.health;
                sageHealths[numeSages] = bot.health;
                numeSages++;

                if (bot.health <= 45) {
                    if (highestSub45Health < bot.health) {
                        highestSub45Health = bot.health;
                        highestSub45 = bot;
                    }
                    canOneShot = true;
                }
                else {
                    if (maxHealth < bot.health) {
                        maxHealth = bot.health;
                        maxHealthBot = bot;
                    }
                }
            }
        }

        if (numeSoldiers == 0 && numeSages == 0) return ATTACK.NONE;

        System.out.println("units: " + numeSoldiers + " " + numeSages);
        // remove zero values from enemyHealth array
      //  soldierHealths = cleanup(soldierHealths, numeSoldiers);
      //  sageHealths = cleanup(sageHealths, numeSages);

        /* for (int i = 0; i < numeSoldiers; i++) {
            System.out.println("Soldier " + i + " health: " + soldierHealths[i]);
        } */

        /* if (((numFriends + 1) / numEnemies) > 1) {
            ratio = ((numFriends + 1) / numEnemies);
        }
        else {
            ratio = numEnemies / (numFriends + 1);
        } */

        ATTACK bestAttack = ATTACK.NONE;
        int maxAdvantage = -100000;
        int advantage;
        int unit_difference = numFriends + 1 - numeSages - numeSoldiers;
        double a = 6.0; // = 6 * ratio;
        // double ratio;
        int unit_advantage;


        ATTACK[] attacks = {ATTACK.DEFAULT, ATTACK.CHARGE};
        for (ATTACK attack: attacks) {
            switch (attack) {
                case DEFAULT:
                    if (canOneShot) {
                        unit_advantage = (int) (a * Math.pow(unit_difference + 1, 2) * Math.signum(unit_difference + 1));
                        advantage = friendHealth - enemyHealth + unit_advantage + highestSub45Health;
                    }
                    else {
                        unit_advantage = (int) (a * Math.pow(unit_difference, 2) * Math.signum(unit_difference));
                        advantage = friendHealth - enemyHealth + unit_advantage + 45;
                    }
                    /* System.out.println("ATTACK: " + attack + " friendHealth: " + friendHealth + " enemyHealth: " + enemyHealth + " unit_difference: " + unit_difference + " unit_advantage: " + unit_advantage + " advantage: " + advantage); */
                    break;
                case CHARGE:
                    int numSoldiersKilled = 0;
                    int numSagesKilled = 0;
                    int health_reduced = 0;
                    if (soldierHealths != null) {
                        for (int i = 0; i < soldierHealths.length; i++) {
                            if (soldierHealths[i] > 0){
                                assert(soldierHealths[i] != 0);
                                if (soldierHealths[i] <= 11) {
                                    numSoldiersKilled++;
                                    health_reduced += soldierHealths[i];
                                }
                                else health_reduced += 11;                                
                            }
                            else break;

                        }
                    }
                    if (sageHealths != null) {
                        for (int i = 0; i < sageHealths.length; i++) {
                            if (sageHealths[i] > 0){
                                assert(sageHealths[i] != 0);
                                if (sageHealths[i] <= 22) {
                                    numSagesKilled++;
                                    health_reduced += sageHealths[i];
                                }
                                else health_reduced += 22;                               
                            }
                            else break;
                        }
                    }
                    unit_difference = unit_difference + numSoldiersKilled + numSagesKilled;
                    unit_advantage = (int) (a * Math.pow(unit_difference, 2) * Math.signum(unit_difference));
                    advantage = friendHealth - enemyHealth + unit_advantage + health_reduced;
                    //System.out.println("ATTACK: " + attack + " friendHealth: " + friendHealth + " enemyHealth: " + enemyHealth + " unit_difference: " + unit_difference + " unit_advantage: " + unit_advantage + " health_reduced " + health_reduced + " num_soldiers killed " + numSoldiersKilled + " num_sages killed " + numSagesKilled +  " advantage: " + advantage); 
                    
                 //   advantage = 0;
                    break;
                default:
                    advantage = 0;
            }
            if (advantage > maxAdvantage) {
                bestAttack = attack;
                maxAdvantage = advantage;
            }
        }

        if (bestAttack == ATTACK.DEFAULT) {
            if (highestSub45 != null) {
                attackTarget = highestSub45.location;
            }
            else {
                attackTarget = maxHealthBot.location;
            }
        }
        System.out.println("");
        return bestAttack;
    }

    public int[] cleanup(int[] a, int length) {
        int[] b = new int[length];
        int j = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != 0) {
                b[j] = a[i];
                j++;
            }
        }
        Arrays.sort(b);
        return b;
    }

    public void visualize() throws GameActionException {
        if (mode == MODE.EXPLORATORY){
            rc.setIndicatorDot(exploreLoc, 100, 100, 0);
        }
        else if (mode == MODE.HUNTING){
            rc.setIndicatorLine(rc.getLocation(), target, 0, 100, 0);
        }
        else if (mode == MODE.SEARCHING_ENEMIES){
            rc.setIndicatorString("MODE: " + mode.toString() + " DIR: " + lastAttackDir[0] + " " + lastAttackDir[1]);
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
        
        // Priority 1 - Defend.
        threatenedArchons = findThreatenedArchons();
        if (threatenedArchons != null) {
            for (MapLocation archon: threatenedArchons) {
                if (rc.getLocation().distanceSquaredTo(archon) <= DRUSH_RSQR) {
                    return MODE.DEFENSIVE_RUSH;
                }
            }
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
            if (r.type == RobotType.SOLDIER  || r.type == RobotType.SAGE){
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
        int numeSoldiers = 0;
        int numeSages = 0;

        int enemyHealth = 0;
        int numFriends = 0;
        int friendHealth = rc.getHealth();

        for (RobotInfo bot: nearbyBots) {
            if (bot.team == rc.getTeam()) {
                if (bot.type == RobotType.SOLDIER) {
                    friendHealth += bot.health;
                    numFriends++;
                }
                else if (bot.type == RobotType.SAGE) {
                    friendHealth += bot.health;
                    numFriends++;
                }
            }
            else if (bot.team == rc.getTeam().opponent()) {
                if (bot.type == RobotType.SOLDIER) {
                    enemyHealth += bot.health;
                    numeSoldiers++;
                    cxse += bot.location.x;
                    cyse += bot.location.y;
                }
                else if (bot.type == RobotType.SAGE) {
                    enemyHealth += bot.health;
                    numeSages++;
                    cxse += bot.location.x;
                    cyse += bot.location.y;
                }
            }
        }
        if (numeSages + numeSoldiers == 0) return new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE};

        if (numeSages + numeSoldiers > 0) {
            cxse /= (numeSages + numeSoldiers);
            cyse /= (numeSages + numeSoldiers);
        }

        double unit_difference = (double) (numFriends + 1 - numeSages - numeSoldiers);

        double a = 3;
        int unit_advantage = (int) (a * Math.pow(unit_difference,2) * Math.signum(unit_difference));

        // System.out.println("Unit advantage: " + unit_advantage + " Ratio: " + ratio + " numFriendHits " + numFriendHits + " numEnemyHits " + numEnemyHits + "round_num " + round_num);

        if (friendHealth + unit_advantage < enemyHealth) {
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


    public void fleeFromAttackers() throws GameActionException {
        MapLocation my = rc.getLocation();
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        MapLocation[] locs = new MapLocation[9];
        for (int i= 0; i < 8; i++) locs[i] = my.add(directions[i]);
        locs[8] = my;
        int[] costs = new int[9];
        int numBots = 0;
        for (RobotInfo r : nearbyBots){
            if (r.type == RobotType.SOLDIER || r.type == RobotType.SAGE){
                if (numBots > 5) return; //restrict for bytecode 
                for (int i = 0; i < 9; i++){
                    if (r.location.distanceSquaredTo(locs[i]) <= 30){
                        costs[i] +=40;
                    }
                }
                numBots++; 
            }
           
        }
        if (numBots == 0){
            return; //nothing to flee from
        }
        Direction bestDirection = null;
        int minCost = 9999999;
        for (int i= 0; i < 8; i++){
            if (rc.canMove(directions[i])){
                costs[i] += rc.senseRubble(locs[i]);
                if (costs[i] < minCost){
                    bestDirection = directions[i];
                    minCost = costs[i];
                }
            }
        }
        if (costs[8] < minCost){
            //should just stay put
            return;
        }
        rc.move(bestDirection);
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
