package pathfindingplayer;

import battlecode.common.*;
import java.util.*;

public class Soldier extends Unit {
    int counter = 0;
    int archon_index = -1;
    boolean archon_found = false;

    MapLocation target;
    Direction exploratoryDir = getExploratoryDir();
	public Soldier(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        if (isExploring()){
            moveInDirection(exploratoryDir);
        }
        else if (archon_found) {
            huntArchon();
        }
        else {
            senseArchon();
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
            rc.setIndicatorString("archon already found");
            data = rc.readSharedArray(archon_index);
            rc.setIndicatorString("archon read: " + data);
            if (data != 0) {
                return;
            }
            else {
                archon_found = false;
            }
        }

        rc.setIndicatorString("finding new archon");
        // if archon dead, find new archon
        for (int i = 0; i < 4; i++) {
            data = rc.readSharedArray(i);
            if (data != 0) {
                archon_index = i;
                break;
            }
        }
        data = rc.readSharedArray(archon_index);
        if (data != 0) {
            rc.setIndicatorString("new archon found");
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

    public Direction getExploratoryDir() {
        MapLocation cur = rc.getLocation();
        MapLocation center = new MapLocation(rc.getMapHeight()/2, rc.getMapWidth()/2);
        if (center.x - cur.x > 0) {
            if (center.y - cur.y > 0) {
                exploratoryDir = Direction.NORTHEAST;
            } else {
                exploratoryDir = Direction.SOUTHEAST;
            }
        } else {
            if (center.y - cur.y > 0) {
                exploratoryDir = Direction.NORTHWEST;
            } else {
                exploratoryDir = Direction.SOUTHWEST;
            }
        }
        Direction[] dirs = {exploratoryDir, exploratoryDir.rotateLeft(), exploratoryDir.rotateRight()};
        return dirs[rng.nextInt(dirs.length)];
    }
}
