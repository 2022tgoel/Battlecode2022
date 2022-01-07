package pathfindingplayer;

import battlecode.common.*;
import pathfindingplayer.RANK;

import java.util.*;

public class Soldier extends Unit {

    enum MODE {
        EXPLORATORY,
        HUNTING,
        ;
    }

    int counter = 0;
    int threatDetectedRound = 10000;
    int round_num = 0;
    int archon_index = -1;
    double s_attraction = 0.0;
    double m_attraction = 10.0;
    double s_repulsion = 10;
    double m_repulsion = 1/10;
    double a_repulsion = 100;

    RANK rank;
    MODE mode = MODE.EXPLORATORY;

    MapLocation target;
    MapLocation[] threatenedArchons;
    RobotInfo[] nearbyAllies;
    Direction exploratoryDir = usefulDir();
    int[] exploratoryDir2 = getExploratoryDir();
	public Soldier(RobotController rc) throws GameActionException {
        super(rc);
        rank = findRank();
        momentumDir = directions[rng.nextInt(8)];
    }
    @Override
    public void run() throws GameActionException {
        round_num = rc.getRoundNum();
        attemptAttack();
        if (rank == RANK.DEFENDER){
            waitAtDist(20, true);
            detectArchonThreat();
        }
        else {
            boolean b;
            threatenedArchons = findThreatenedArchons();
            if (threatenedArchons != null) {
                // find closest maplocation to robot
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
            else if (isLowHealth()) {
                fuzzyMove(homeArchon);
            }
            else if (mode == MODE.EXPLORATORY) {
               // exploreMove();
                
                moveInDirection(exploratoryDir2);
                if (adjacentToEdge()) {
                    exploratoryDir2 = getExploratoryDir();
                }
            }
            else if (mode == MODE.HUNTING){
                b = approachArchon();
                if (!b) mode = MODE.EXPLORATORY;
            }
            
            if (mode == MODE.EXPLORATORY){ 
                b = senseArchon();
                if (b) mode = MODE.HUNTING;
                else {
                    b= detectArchon(); 
                    if (b) mode = MODE.HUNTING; //switch to hunting
                }
            }
        }
        counter += 1;
      //  rc.setIndicatorString("RANK: " + rank.toString());
    }

    public RANK findRank() throws GameActionException {
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
            rc.setIndicatorString("STATUS: " + status + " X: " + x + " Y: " + y);
            if (homeArchon.equals(new MapLocation(x, y))) {
                return getRank(status);
            }
        }
        return RANK.DEFAULT;
    }
    /**
     * waitAtDist() stays near the home archon
     **/
    public void waitAtDist(int idealDistSquared, boolean shouldRepel) throws GameActionException{
        //stays at around an ideal dist
        MapLocation myLocation = rc.getLocation();
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getType().visionRadiusSquared, rc.getTeam());
        int buffer = 4;
        // rc.setIndicatorString("" + Math.abs(myLocation.distanceSquaredTo(homeArchon)-idealDistSquared));
        if (Math.abs(myLocation.distanceSquaredTo(homeArchon)-idealDistSquared) <= buffer){
            // rc.setIndicatorString("here");
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
                cost = 10*Math.abs(newLocation.distanceSquaredTo(homeArchon)-idealDistSquared);
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

    Direction momentumDir = null;
    public void exploreMove() throws GameActionException{
        MapLocation myLocation = rc.getLocation();
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getType().visionRadiusSquared, rc.getTeam());
        Direction[] dirs = {momentumDir, momentumDir.rotateLeft(), momentumDir.rotateRight(), 
                momentumDir.rotateLeft().rotateLeft(), momentumDir.rotateRight().rotateRight(), 
                momentumDir.opposite().rotateLeft(), momentumDir.opposite().rotateRight(), momentumDir.opposite()};
        int[] costs = new int[8];
        for (int i = 0; i < 8; i++){
            MapLocation newLocation = myLocation.add(dirs[i]);
            int cost = 0;
            if (!rc.onTheMap(newLocation)) {
                cost = 999999;
            }
            else {
                //momentum component
                if (i >=1){
                    cost+=10;
                }
                if (i >= 3) {
                    cost += 30;
                }
                if (i >=5 ){
                    cost+=30;
                }
                

                //repelling component
                for (RobotInfo robot : nearbyAllies) {
                    if (robot.type == rc.getType()) {
                        cost -= newLocation.distanceSquaredTo(robot.location); //trying to maximize distance
                    }
                }
                //rubble component
                cost += rc.senseRubble(newLocation);
            }
            costs[i] = cost;

        }
        String s = " ";
        for (int i =0; i < 8; i++) s += costs[i] + " ";
        rc.setIndicatorString(s);
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
<<<<<<< HEAD
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
                archons[numThreatenedArchons] = new MapLocation(x, y);
                numThreatenedArchons += 1;
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
            return archons;
        }
=======
>>>>>>> d2db73b2638e1bc1956ad68bf192de9701838f85
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
        if (threatLevel >= 4) {
            for (int i = 0; i < 4; i++) {
                // rc.writeSharedArray(, value);
                data = rc.readSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + i);
                // go through channels until you find an empty one to communicate with.
                if (data == 0) {
                    rc.writeSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + i, locationToInt(homeArchon));
                    break;
                }
            }
            threatDetectedRound = round_num;
        }
        else {
            for (int i = 0; i < 4; i++) {
                // rc.writeSharedArray(, value);
                data = rc.readSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + i);
                // go through channels until you find an empty one to communicate with.
                if (data != 0) {
                    int x = data / 64;
                    int y = data % 64;
                    // clear channel if threat is no longer active
                    if (homeArchon.x == x && homeArchon.y == y && (round_num - threatDetectedRound) > 10) {
                        rc.writeSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + i, 0);
                        break;
                    }
                }
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
    public Direction friendlyDir() throws GameActionException {

        Direction d = exploratoryDir;
        RobotInfo[] friendlyRobos = rc.senseNearbyRobots(-1, rc.getTeam());
        MapLocation loc = rc.getLocation();

        // average position of soldiers and miners
        double cxs = 0;
        double cys = 0;
        double cxm = 0;
        double cym = 0;

        // repulsion from friendly robots
        double dx2 = 0;
        double dy2 = 0;

        double incrementx = 0;
        double incrementy = 0;

        int num_miners = 0;
        int num_soldiers = 0;

        for (RobotInfo robot: friendlyRobos) {
            if (robot.type == RobotType.SOLDIER) {
                incrementx = robot.location.x;
                incrementy = robot.location.y;

                // increment repulsion
                if ((Math.abs(robot.location.x - loc.x) + Math.abs(robot.location.y - loc.y)) <= 4) {
                    dx2 += (loc.x - robot.location.x) * s_repulsion;
                    dy2 += (loc.y - robot.location.y) * s_repulsion;
                }
                cxs += incrementx;
                cys += incrementy;
                num_soldiers += 1;
            }
            else if (robot.type == RobotType.MINER) {
                incrementx = robot.location.x;
                incrementy = robot.location.y;
                // increment repulsion
                if ((Math.abs(robot.location.x - loc.x) + Math.abs(robot.location.y - loc.y)) <= 2) {
                    dx2 += (loc.x - robot.location.x) * m_repulsion;
                    dy2 += (loc.y - robot.location.y) * m_repulsion;
                }
                cxm += incrementx;
                cym += incrementy;
                num_miners += 1;
            }
            else if (robot.type == RobotType.ARCHON) {
                // increment repulsion
                if ((Math.abs(robot.location.x - loc.x) + Math.abs(robot.location.y - loc.y)) <= 13) {
                    dx2 += (loc.x - robot.location.x) * a_repulsion;
                    dy2 += (loc.y - robot.location.y) * a_repulsion;
                }
            }
        }
        // if there are no miners, explore.
        if (num_miners == 0) {
            return d;
        }

        double dx1 = ((cxm / num_miners) - (double) loc.x) * m_attraction;
        dx1 += ((cxs / num_soldiers) - (double) loc.x) * s_attraction;
        double dy1 = ((cym / num_miners) - (double) loc.y) * m_attraction;
        dy1 += ((cys / num_soldiers) - (double) loc.y) * s_attraction;

        double dx = dx1 + dx2;
        double dy = dy1 + dy2;
        // convert dx and dy to direction
        // values are derived from tangent of 22.5 and 67.5
        if (dy > 0) {
            if (dy > 2.4 * Math.abs(dx)) {
                d = Direction.NORTH;
            }
            else if (dy > 0.4 * Math.abs(dx)) {
                if (dx > 0) {
                    d = Direction.NORTHEAST;
                }
                else {
                    d = Direction.NORTHWEST;
                }
            }
            else {
                if (dx > 0) {
                    d = Direction.EAST;
                }
                else {
                    d = Direction.WEST;
                }
            }
        }
        else {
            if (dy < -2.4 * Math.abs(dx)) {
                d = Direction.SOUTH;
            }
            else if (dy < -0.4 * Math.abs(dx)) {
                if (dx > 0) {
                    d = Direction.SOUTHEAST;
                }
                else {
                    d = Direction.SOUTHWEST;
                }
            }
        }
        // rc.setIndicatorString("dir: " + d + "| attraction: " + Math.round(dx1) + ", " + Math.round(dy1) + " | repulsion: " + Math.round(dx2) + ", " + Math.round(dy2));
        // rc.setIndicatorString("vs: " + Math.round(cxs / num_soldiers) + ", " + Math.round(cys / num_soldiers) + " | vm: " + Math.round(cxm / num_miners) + ", " + Math.round(cym / num_miners));
        return d;
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

    public void attemptAttack() throws GameActionException {
        boolean enemy_soldiers = false;
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
                rc.attack(weakestBot.location);
            }
            else {
                for (RobotInfo bot : nearbyBots) {
                    if (bot.type == RobotType.MINER)
                        if (bot.health < weakestBot.health) {
                            weakestBot = bot;
                        }
                }
                rc.attack(weakestBot.location);
            }
        }
    }
}
