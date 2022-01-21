package schlumbergera;

import battlecode.common.*;
import java.util.*;

public class Sage extends Unit {
    int counter = 0;
    int archon_index = -1;
    double s_attraction = 0.5;
    double m_attraction = 10.0;
    double s_repulsion = 1;
    double m_repulsion = 1/10;

    MapLocation target;
    int[] exploratoryDir = getExploratoryDir();

	public Sage(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        /*
        if (isLowHealth()) {
            fuzzyMove(homeArchon);
        }
        else if (isExploring()){
            moveInDirection(friendlyDir());
            if (rc.getLocation().isAdjacentTo(homeArchon)) {
                // rc.setIndicatorString("moving away");
                moveInDirection(rc.getLocation().directionTo(homeArchon).opposite());
            }
            else {
                moveInDirection(friendlyDir());
            } 
        }
        else if (archon_found) {
            huntArchon();
        }
        senseArchon();
        attemptAttack();
        detectArchon();
        counter += 1;*/
    }
/*
    public boolean isExploring() throws GameActionException{
        if (archon_found) {
            return false;
        }
        else {
            return true;
        }
    }

    public void detectArchon() throws GameActionException {
        // if archon still alive, don't do anything
        int data = 0;
        if (archon_found) {
            // rc.setIndicatorString("archon already found");
            data = rc.readSharedArray(archon_index);
            // rc.setIndicatorString("archon read: " + data);
            if (data != 0) {
                return;
            }
            else {
                archon_found = false;
            }
        }

        // rc.setIndicatorString("finding new archon");
        // if archon dead, find new archon
        for (int i = 0; i < 4; i++) {
            data = rc.readSharedArray(i);
            if (data != 0) {
                // rc.setIndicatorString("archon found UWU");
                archon_index = i;
                break;
            }
        }
        data = rc.readSharedArray(archon_index);
        if (data != 0) {
            // rc.setIndicatorString("new archon found");
            archon_found = true;
            int x = data / 1000;
            int y = data % 1000;
            target = new MapLocation(x, y);
        }
        else {
            archon_found = false;
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

    public void huntArchon() throws GameActionException {
        // if robot should be able to see archon but can't, inform everyone that archon is dead
        if (rc.canSenseLocation(target)) {
            if (!rc.canSenseRobotAtLocation(target)){
                int data = rc.readSharedArray(archon_index);
                if (data != 0) {
                    rc.writeSharedArray(archon_index, 0);
                }
                archon_found = false;
                return;
            }
        }
        // if robot can't see archon, or sees archon, move towards it
        fuzzyMove(target);
    }

    public Direction friendlyDir() throws GameActionException {

        Direction d = usefulDir(); // placeholder
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
        int far_miners = 0;
        int num_soldiers = 0;

        for (RobotInfo robot: friendlyRobos) {
            if (robot.type == RobotType.SOLDIER) {
                incrementx = robot.location.x;
                incrementy = robot.location.y;

                // increment repulsion
                if ((Math.abs(robot.location.x - loc.x) + Math.abs(robot.location.y - loc.y)) <= 4) {
                    dx2 -= (loc.x - robot.location.x) * s_repulsion;
                    dy2 -= (loc.y - robot.location.y) * s_repulsion;
                }
                cxs += incrementx;
                cys += incrementy;
                num_soldiers += 1;
            }
            else if (robot.type == RobotType.MINER) {
                if ((Math.abs(robot.location.x - loc.x) + Math.abs(robot.location.y - loc.y)) > 3) {
                    far_miners += 1;
                }
                incrementx = robot.location.x;
                incrementy = robot.location.y;
                // increment repulsion
                if ((Math.abs(robot.location.x - loc.x) + Math.abs(robot.location.y - loc.y)) <= 2) {
                    dx2 -= (loc.x - robot.location.x) * m_repulsion;
                    dy2 -= (loc.y - robot.location.y) * m_repulsion;
                }
                cxm += incrementx;
                cym += incrementy;
                num_miners += 1;
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
        rc.setIndicatorString("dir: " + d + "| attraction: " + Math.round(dx1) + ", " + Math.round(dy1) + " | repulsion: " + Math.round(dx2) + ", " + Math.round(dy2));
        // rc.setIndicatorString("vs: " + Math.round(cxs) + ", " + Math.round(cys) + " | vm: " + Math.round(cxm) + ", " + Math.round(cym));
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
        rc.setIndicatorString(d.dx + " " + d.dy);
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
    }*/
}
