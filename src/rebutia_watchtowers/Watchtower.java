package rebutia_watchtowers;

import battlecode.common.*;
import java.util.*;

public class Watchtower extends Unit {
	public Watchtower(RobotController rc) throws GameActionException {
        super(rc);
    }

    public void run() throws GameActionException {
    	attemptAttack();
    }


    public void attemptAttack() throws GameActionException {
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(RobotType.WATCHTOWER.actionRadiusSquared, rc.getTeam().opponent());
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