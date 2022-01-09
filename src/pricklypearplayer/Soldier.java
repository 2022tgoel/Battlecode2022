package pricklypearplayer;

import battlecode.common.*;

import java.util.*;

public class Soldier extends Unit {

    enum MODE {
        EXPLORATORY,
        HUNTING,
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
    int archon_index = -1;
    int dRushChannel = -1;
    double s_attraction = 1.0;
    double m_attraction = 3.0;
    double m_e_attraction = 8.0;
    double s_repulsion = 1.0;
    double m_repulsion = 0.1;
    double exploration_weight = 2.0;

    int convoyDeployRound = 10000;

    RANK rank;
    MODE mode = MODE.EXPLORATORY;

    MapLocation target;
    MapLocation[] threatenedArchons;
    RobotInfo[] nearbyAllies;
    int[] exploratoryDir = getExploratoryDir();

    public boolean SEARCH_FOR_CONVOY = false;
    private MapLocation convoyTarget;

	public Soldier(RobotController rc) throws GameActionException {
        super(rc);
        rank = findRank(true);
        momentumDir = directions[rng.nextInt(8)];
    }
    @Override
    public void run() throws GameActionException {
        round_num = rc.getRoundNum();
        attacked = attemptAttack(false);
        switch (rank) {
            case DEFENDER:
                if (archonDied()){
                    rank = RANK.DEFAULT; //stop being a defender bot
                }
                else {
                    mode = determineDefenderMode();
                    rc.setIndicatorString("MODE: " + mode.toString());
                    switch (mode) {
                        case WAITING:
                            waitAtDist(homeArchon, 20, true);
                            detectArchonThreat();
                            break;
                        case DEFENSIVE_RUSH:
                            if (rc.getLocation().distanceSquaredTo(homeArchon) < 36){
                                moveToEnemySoldiers();
                            }
                            else {
                                fuzzyMove(homeArchon);
                            }
                            break;
                        default:
                            break;
                    }
                }
                break;
            case DEFAULT:
                mode = determineMode();
                switch (mode) {
                    case DEFENSIVE_RUSH:
                        // find closest maplocation to robot
                        defensiveMove();
                        break;
                    case LOW_HEALTH:
                        fuzzyMove(homeArchon);
                        break;
                    case EXPLORATORY:
                        int[] d = friendlyDir();
                        moveInDirection(d);
                        // rc.setIndicatorString("d: " + d[0] + " " + d[1] + "exp: " + exploratoryDir[0] + " " + exploratoryDir[1]);
                        break;
                    case HUNTING:
                        approachArchon();
                        break;
                    case CONVOY:
                        if (convoyDeployRound >= round_num){
                            waitAtDist(convoyTarget, 9, true);
                            // rc.setIndicatorString("convoyDEPLOYROUND: " + convoyDeployRound);
                        }
                        else {
                            if (archon_index != -1){
                                approachArchon();
                            }
                            else {
                                convoyDeployRound += 30;
                            }
                        }
                    default:
                        break;
                }
                break;
            default:
                break;
            }

        if (adjacentToEdge()) {
            if (round_num - exploratoryDirUpdateRound > 2)
                exploratoryDir = getExploratoryDir();
                exploratoryDirUpdateRound = round_num;
        }

        if (!attacked) {
            // attack miners if they're the only things nearby
            attemptAttack(true);
        }

        counter += 1;
        // rc.setIndicatorString("MODE: " + mode.toString());
    }

    public boolean canHunt(){
        if (round_num < 300) return true;
        else return false;
    }

    public MapLocation findConvoy() throws GameActionException {
        int data = rc.readSharedArray(CHANNEL.CONVOY.getValue());
        if (data != 0) {
            int x = data / 64;
            int y = data % 64;
            MapLocation loc = new MapLocation(x, y);
            MapLocation cur = rc.getLocation();
            int dist = cur.distanceSquaredTo(loc);
            if (dist < 225) {
                return loc;
            }
        }
        return null;
    }
    public MODE determineDefenderMode() throws GameActionException {
        threatenedArchons = findThreatenedArchons();

        boolean homeArchonThreatened = false;
        if (threatenedArchons!=null){
            for (MapLocation archon : threatenedArchons){
                if (archon.equals(homeArchon)) homeArchonThreatened = true;
            }
        }
        
        if (homeArchonThreatened) {
            return MODE.DEFENSIVE_RUSH;
        }
        else return MODE.WAITING; 
    }

    public MODE determineMode() throws GameActionException {
        // this is global
        threatenedArchons = findThreatenedArchons();
        MapLocation cur = rc.getLocation();
        if (threatenedArchons != null) {
            for (MapLocation archon : threatenedArchons){
                if (cur.distanceSquaredTo(archon) < 145) 
                    return MODE.DEFENSIVE_RUSH;
            }
        }

        boolean canHunt = canHunt();

        boolean archonDetected = detectArchon() || senseArchon();

        //rc.setIndicatorString("archonDetected: " + detectArchon() + " " + archon_index);

        if (archonDetected && canHunt) {
            return MODE.HUNTING;
        }

        MapLocation loc = findConvoy();
        if (loc != null){
            if (!convoy_found) {
                convoyDeployRound = 30 + (30 * (round_num / 30));
                convoyTarget = loc;
                convoy_found = true;
            }
        }

        if (convoy_found) return MODE.CONVOY;

        return MODE.EXPLORATORY;
    }

    public RANK findRank(boolean assign) throws GameActionException {
        rc.setIndicatorString("READY TO READ");
        int data;
        // check all channels to see if you've received a rank
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                data = rc.readSharedArray(CHANNEL.SEND_RANKS1.getValue());
            }
            else {
                data = rc.readSharedArray(CHANNEL.SEND_RANKS2.getValue());
            }

            int status = data / 4096;
            int x = (data - (4096 * status)) / 64;
            int y = (data - (4096 * status) - (x * 64));

            // only set rank if instructed to.
            if (homeArchon.equals(new MapLocation(x, y)) && assign) {
                return getRank(status);
            }
        }
        return RANK.DEFAULT;
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

    Direction momentumDir = directions[rng.nextInt(directions.length)];
    public void exploreMove() throws GameActionException{
        MapLocation myLocation = rc.getLocation();
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getType().visionRadiusSquared, rc.getTeam());
        Direction[] dirs = {momentumDir, momentumDir.rotateLeft(), momentumDir.rotateRight(), 
                momentumDir.rotateLeft().rotateLeft(), momentumDir.rotateRight().rotateRight(), 
                momentumDir.opposite().rotateLeft(), momentumDir.opposite().rotateRight(), momentumDir.opposite()};
        int[] costs = new int[8];
        int s = 0;
        for (RobotInfo robot : nearbyAllies) {
            if (robot.type == rc.getType()) {
                s++; 
            }
        }
        for (int i = 0; i < 8; i++){
            MapLocation newLocation = myLocation.add(dirs[i]);
            int cost = 0;
            if (!rc.onTheMap(newLocation)) {
                cost = 999999;
            }
            else {
                //momentum component
                if (s ==0 ){
                    if (i >=1){
                        cost+=25;
                    }
                    if (i >= 3) {
                        cost += 25;
                    }
                    if (i >=5 ){
                        cost+=25;
                    }
                }
                else {
                    //repelling component
                    for (RobotInfo robot : nearbyAllies) {
                        if (robot.type == rc.getType()) {
                            cost -= newLocation.distanceSquaredTo(robot.location); //trying to maximize distance
                        }
                    }
                }
                
                //rubble component
                cost += rc.senseRubble(newLocation);
            }
            costs[i] = cost;

        }
        int cost = 99999;
        Direction optimalDir = null;
        for (int i = 0; i < dirs.length; i++) {
            Direction dir = dirs[i];
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
                momentumDir = optimalDir;
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

    public void detectArchonThreat() throws GameActionException {
        RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        int threatLevel = 0;
        int data;
        for (RobotInfo enemy : enemies) {
            if (enemy.type == RobotType.SOLDIER) {
                threatLevel += 1;
            }
            else if (enemy.type == RobotType.SAGE) {
                threatLevel += 4;
            }
        }
        if (threatLevel >= 2) {
            for (int i = 0; i < 4; i++) {
                // rc.writeSharedArray(, value);
                data = rc.readSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + i);
                // go through channels until you find an empty one to communicate with.
                int x = data / 64;
                int y = data % 64;
                // channel already written too.
                if (x == homeArchon.x && y == homeArchon.y) {
                    dRushChannel = i;
                    return;
                }
                if (data == 0) {
                    dRushChannel = i;
                    
                }
            }
            rc.writeSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + dRushChannel, locationToInt(homeArchon));
            threatDetectedRound = round_num;
        }
        else {
            // clear channel if threat is no longer active
            if (dRushChannel != -1) {
                data = rc.readSharedArray(dRushChannel);
            }
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
    public int[] friendlyDir() throws GameActionException { 
        MapLocation loc = rc.getLocation();
        // average position of soldiers and miners
        double cxs = 0;
        double cys = 0;
        double cxm = 0;
        double cym = 0;
        double cxme = 0;
        double cyme = 0;
        // repulsion from friendly robots
        double dx2 = 0;
        double dy2 = 0;

        double num_miners = 0;
        double num_soldiers = 0;
        double num_miners_enemy = 0;

        RobotInfo[] friendlyRobos = rc.senseNearbyRobots(-1, rc.getTeam());
        for (RobotInfo robot: friendlyRobos) {
            if (robot.type == RobotType.SOLDIER) {
                cxs += (double) robot.location.x;
                cys += (double) robot.location.y;
                // increment repulsion
                if ((Math.abs(robot.location.x - loc.x) + Math.abs(robot.location.y - loc.y)) <= 2) {
                    dx2 += ((double) (loc.x - robot.location.x)) * s_repulsion;
                    dy2 += ((double) (loc.y - robot.location.y)) * s_repulsion;
                }
                num_soldiers += 1;
            }
            else if (robot.type == RobotType.MINER) {
                cxm += (double) robot.location.x;
                cym += (double) robot.location.y;
                // increment repulsion
                if ((Math.abs(robot.location.x - loc.x) + Math.abs(robot.location.y - loc.y)) <= 2) {
                    dx2 += ((double) (loc.x - robot.location.x)) * m_repulsion;
                    dy2 += ((double) (loc.y - robot.location.y)) * m_repulsion;
                }
                num_miners += 1;
            }
        }
        RobotInfo[] enemyRobos = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        for (RobotInfo robot: enemyRobos) {
            if (robot.type == RobotType.MINER) {
                cxme += (double) robot.location.x;
                cyme += (double) robot.location.y;
                num_miners_enemy += 1;
            }
        }
        double dx1 = 0;
        double dy1 = 0;
        if (num_miners > 0) dx1 += ((cxm / num_miners) - ((double) loc.x)) * m_attraction;
        if (num_soldiers > 0) dx1 += ((cxs / num_soldiers) - ((double) loc.x)) * s_attraction;
        if (num_miners_enemy > 0) dx1 += ((cxme / num_miners_enemy) - ((double) loc.x)) * m_e_attraction;
        dx1 += ((double) exploratoryDir[0]) * exploration_weight;

        if (num_miners > 0) dy1 = ((cym / num_miners) - ((double) loc.y)) * m_attraction;
        if (num_soldiers > 0) dy1 += ((cys / num_soldiers) - ((double) loc.y)) * s_attraction;
        if (num_miners_enemy > 0) dy1 += ((cyme / num_miners_enemy) - ((double) loc.y)) * m_e_attraction;
        dy1 += ((double) exploratoryDir[1]) * exploration_weight;

        double dx = dx1 + dx2;
        double dy = dy1 + dy2;
        rc.setIndicatorString("d: " + dx + " " + dy + "exp: " + exploratoryDir[0] + " " + exploratoryDir[1]);
        // convert dx and dy to direction
        // normalize double vector
        double dir_strength = 4.0;
        if (Math.abs(dx) >= Math.abs(dy)) {
            if (dx > 0) return new int[]{(int) dir_strength, (int) (dir_strength * dy / dx)};
            else if (dx < 0) return new int[]{(int) -dir_strength, (int) (-dir_strength * dy / dx)};
            else return new int[]{0, (int) (dir_strength * Math.signum(dy))};
        }
        else {
            if (dy > 0) return new int[]{(int) (dir_strength * dx / dy), (int) dir_strength};
            else if (dy < 0) return new int[]{(int) (-dir_strength * dx / dy), (int) -dir_strength};
            else return new int[]{(int) (dir_strength * Math.signum(dx)), 0};
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
        }
        if ((!enemy_miners || !attackMiners)  && !enemy_soldiers) {
            return false;
        }
        else {
            return true;
        }
    }
}
