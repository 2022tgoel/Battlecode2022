package pathfindingplayer;

import battlecode.common.*;
import java.util.*;

public class Soldier extends Unit {
    int counter = 0;
    int archon_index = -1;
    double s_attraction = 1/2;
    double m_attraction = 10;
    double s_repulsion = 2;
    double m_repulsion = 1/10;

    MapLocation target;
    int[] exploratoryDir = getExploratoryDir();
	public Soldier(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        rc.setIndicatorString("archon_found: " + archon_found);
        if (isExploring()){
            Direction dir = friendlyDir();
            rc.setIndicatorString("dir: " + dir);
            moveInDirection(friendlyDir());
        }
        else if (archon_found) {
            huntArchon();
        }
        else {
        }

        attemptAttack();
        detectArchon();
        counter += 1;
    }

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
        Direction d = Direction.NORTH; // placeholder
        RobotInfo[] friendlyRobos = rc.senseNearbyRobots(9, rc.getTeam());
        MapLocation loc = rc.getLocation();
        int num = friendlyRobos.length;
        double cx = 0;
        double cy = 0;
        double dx2 = 0;
        double dy2 = 0;

        for (RobotInfo robot: friendlyRobos) {
            if (robot.type == RobotType.SOLDIER) {
                cx += robot.location.x * s_attraction;
                cy += robot.location.y * s_attraction;
                // absolute value
                if ((Math.abs(robot.location.x - loc.x) + Math.abs(robot.location.y - loc.y)) < 2) {
                    dx2 -= Math.abs(robot.location.x - loc.x) * s_repulsion;
                    dy2 -= Math.abs(robot.location.y - loc.y) * s_repulsion;
                }
            }
            else if (robot.type == RobotType.MINER) {
                cx += robot.location.x * m_attraction;
                cy += robot.location.y * m_attraction;
                // absolute value
                if ((Math.abs(robot.location.x - loc.x) + Math.abs(robot.location.y - loc.y)) < 2) {
                    dx2 -= Math.abs(robot.location.x - loc.x) * m_repulsion;
                    dy2 -= Math.abs(robot.location.y - loc.y) * m_repulsion;
                }
            }
        }
        cx /= num;
        cy /= num;
        double dx1 = (cx - (double) loc.x);
        double dy1 = cy - (double) loc.y;
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
        return d;
    }

    public void attemptAttack() throws GameActionException {
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(RobotType.SOLDIER.actionRadiusSquared, rc.getTeam().opponent());
        // if there are any nearby enemy robots, attack the one with the least health
        if (nearbyBots.length > 0) {
            RobotInfo weakestBot = nearbyBots[0];
            for (RobotInfo bot : nearbyBots) {
                if (bot.health < weakestBot.health) {
                    weakestBot = bot;
                }
            }
            rc.attack(weakestBot.location);
        }
    }
}
