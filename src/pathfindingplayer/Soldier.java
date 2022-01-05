package pathfindingplayer;

import battlecode.common.*;
import java.util.*;

public class Soldier extends Unit {
    int travel_counter = 0;
    MapLocation target;
    Direction exploratoryDir = getExploratoryDir();
	public Soldier(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        if (isExploring()){
            if (rc.canMove(exploratoryDir)) {
                moveInDirection(exploratoryDir);
            }
            attemptAttack();
        }
        else {
            huntArchon();
        }
    }

    public boolean isExploring() throws GameActionException {
        return true;
    }

    public void huntArchon() throws GameActionException {
        int data = rc.readSharedArray(0);
        if (data != 0) {
            int x = data / 1000;
            int y = data % 1000;
            target = new MapLocation(x, y);
            fuzzyMove(target);
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
