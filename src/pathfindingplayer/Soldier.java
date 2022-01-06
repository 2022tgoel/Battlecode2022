package pathfindingplayer;

import battlecode.common.*;
import java.util.*;

public class Soldier extends Unit {
    int counter = 0;
    int archon_index = -1;
    float attraction = 1 / 100;

    MapLocation target;
    int[] exploratoryDir = getExploratoryDir();
	public Soldier(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        rc.setIndicatorString("archon_found: " + archon_found);
        if (isExploring()){
            moveInDirection(exploratoryDir);
            if (counter % 2 == 0) {
                senseArchon();
            }
            if (adjacentToEdge()) {
                exploratoryDir = getExploratoryDir();
            }
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

    public void stayNearFriendly() {
        RobotInfo[] friendlyRobos = rc.senseNearbyRobots(9, rc.getTeam());
        MapLocation loc = rc.getLocation();
        int num = friendlyRobos.length;
        float cx = 0;
        float cy = 0;
        float dx2 = 0;
        float dy2 = 0;

        for (RobotInfo robot: friendlyRobos) {
            cx += robot.location.x;
            cy += robot.location.y;
            // absolute value
            if ((Math.abs(robot.location.x - loc.x) + Math.abs(robot.location.y - loc.y)) < 2) {
                dx2 += robot.location.x;
                dy2 += robot.location.y;
            }
        }
        cx /= num;
        cy /= num;
        float dx1 = (cx - (float) loc.x) * attraction;
        float dy1 = cy - (float) loc.y * attraction;
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
