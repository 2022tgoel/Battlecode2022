package dijkstraplayer;


import battlecode.common.*;
import java.util.*;

public class Soldier extends Unit {
    int counter = 0;
    boolean archon_found = false;
    int archon_id = -1;
    MapLocation target;
    Direction exploratoryDir = getExploratoryDir();
	public Soldier(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        if (isExploring()){
            rc.setIndicatorString("exploring");
            moveInDirection(exploratoryDir);
        }
        else if (archon_found) {
            huntArchon();
            if (counter % 3 == 0) {

            }
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
        int data = rc.readSharedArray(0);
        if (data != 0) {
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
        if (rc.canSenseRobotAtLocation(target))
            fuzzyMove(target);
        else {
            archon_found = false;
            rc.writeSharedArray(0, 0);
        }
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
        rc.setIndicatorString(exploratoryDir.dx + " " + exploratoryDir.dy);
        Direction[] dirs = {exploratoryDir, exploratoryDir.rotateLeft(), exploratoryDir.rotateRight()};
        return dirs[rng.nextInt(dirs.length)];
    }
}
