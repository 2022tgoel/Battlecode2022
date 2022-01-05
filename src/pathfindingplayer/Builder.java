package pathfindingplayer;

import battlecode.common.*;
import java.util.*;

public class Builder extends Unit {
    int travel_counter = 0;
    MapLocation target;
    int[] exploratoryDir = getExploratoryDir();
	public Builder(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        if (isExploring()){
            moveInDirection(exploratoryDir);
        }
        if (adjacentToEdge()) {
            exploratoryDir = getExploratoryDir();
        }
        attemptAttack();
    }

    public void attemptAttack() throws GameActionException {
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(RobotType.BUILDER.actionRadiusSquared, rc.getTeam());
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

    public boolean isExploring() throws GameActionException {
        return true;
    }
}
