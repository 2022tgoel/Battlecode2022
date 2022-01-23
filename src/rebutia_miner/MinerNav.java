package rebutia_miner;

import battlecode.common.*;
public class MinerNav {
    static RobotController rc;
    MinerNav(RobotController rc) {
        this.rc = rc;
    }
    static MapLocation loc0;
    static MapLocation loc1;
    static MapLocation loc2;
    static MapLocation loc3;
    static MapLocation loc4;
    static MapLocation loc5;
    static MapLocation loc6;
    static MapLocation loc7;
    static MapLocation bestSpot;
    int rubble;
    MapLocation findBestSquare(MapLocation leadLoc, int minRubble) throws GameActionException{
        loc0 = leadLoc.add(Direction.EAST);
        loc1 = leadLoc.add(Direction.WEST);
        loc2 = leadLoc.add(Direction.SOUTH);
        loc3 = leadLoc.add(Direction.NORTH);
        loc4 = leadLoc.add(Direction.NORTHEAST);
        loc5 = leadLoc.add(Direction.NORTHWEST);
        loc6 = leadLoc.add(Direction.SOUTHEAST);
        loc7 = leadLoc.add(Direction.SOUTHWEST);
        bestSpot = null;
        if (rc.canSenseLocation(loc0)) {
            rubble = 1 + rc.senseRubble(loc0) / 10;
            if (rubble < minRubble) {
                minRubble = rubble;
                bestSpot = loc0;
            }
        }
        if (rc.canSenseLocation(loc1)) {
            rubble = 1 + rc.senseRubble(loc1) / 10;
            if (rubble < minRubble) {
                minRubble = rubble;
                bestSpot = loc1;
            }
        }
        if (rc.canSenseLocation(loc2)) {
            rubble = 1 + rc.senseRubble(loc2) / 10;
            if (rubble < minRubble) {
                minRubble = rubble;
                bestSpot = loc2;
            }
        }
        if (rc.canSenseLocation(loc3)) {
            rubble = 1 + rc.senseRubble(loc3) / 10;
            if (rubble < minRubble) {
                minRubble = rubble;
                bestSpot = loc3;
            }
        }
        if (rc.canSenseLocation(loc4)) {
            rubble = 1 + rc.senseRubble(loc4) / 10;
            if (rubble < minRubble) {
                minRubble = rubble;
                bestSpot = loc4;
            }
        }
        if (rc.canSenseLocation(loc5)) {
            rubble = 1 + rc.senseRubble(loc5) / 10;
            if (rubble < minRubble) {
                minRubble = rubble;
                bestSpot = loc5;
            }
        }
        if (rc.canSenseLocation(loc6)) {
            rubble = 1 + rc.senseRubble(loc6) / 10;
            if (rubble < minRubble) {
                minRubble = rubble;
                bestSpot = loc6;
            }
        }
        if (rc.canSenseLocation(loc7)) {
            rubble = 1 + rc.senseRubble(loc7) / 10;
            if (rubble < minRubble) {
                minRubble = rubble;
                bestSpot = loc7;
            }
        }
        return bestSpot;
    }

    public int getValue(MapLocation leadLoc) throws GameActionException{
        loc0 = leadLoc.add(Direction.EAST);
        loc1 = leadLoc.add(Direction.WEST);
        loc2 = leadLoc.add(Direction.SOUTH);
        loc3 = leadLoc.add(Direction.NORTH);
        loc4 = leadLoc.add(Direction.NORTHEAST);
        loc5 = leadLoc.add(Direction.NORTHWEST);
        loc6 = leadLoc.add(Direction.SOUTHEAST);
        loc7 = leadLoc.add(Direction.SOUTHWEST);
        int value = 0;
        if (rc.canSenseLocation(loc0)) {
            value+=CONSTANTS.GOLD_TO_LEAD * rc.senseGold(loc0) + Math.max(rc.senseLead(loc0) - 1, 0);
        }
        if (rc.canSenseLocation(loc1)) {
            value+=CONSTANTS.GOLD_TO_LEAD * rc.senseGold(loc1) + Math.max(rc.senseLead(loc1) - 1, 0);
        }
        if (rc.canSenseLocation(loc2)) {
            value+=CONSTANTS.GOLD_TO_LEAD * rc.senseGold(loc2) + Math.max(rc.senseLead(loc2) - 1, 0);
        }
        if (rc.canSenseLocation(loc3)) {
            value+=CONSTANTS.GOLD_TO_LEAD * rc.senseGold(loc3) + Math.max(rc.senseLead(loc3) - 1, 0);
        }
        if (rc.canSenseLocation(loc4)) {
            value+=CONSTANTS.GOLD_TO_LEAD * rc.senseGold(loc4) + Math.max(rc.senseLead(loc4) - 1, 0);
        }
        if (rc.canSenseLocation(loc5)) {
            value+=CONSTANTS.GOLD_TO_LEAD * rc.senseGold(loc5) + Math.max(rc.senseLead(loc5) - 1, 0);
        }
        if (rc.canSenseLocation(loc6)) {
            value+=CONSTANTS.GOLD_TO_LEAD * rc.senseGold(loc6) + Math.max(rc.senseLead(loc6) - 1, 0);
        }
        if (rc.canSenseLocation(loc7)) {
            value+=CONSTANTS.GOLD_TO_LEAD * rc.senseGold(loc7) + Math.max(rc.senseLead(loc7) - 1, 0);
        }

        value+=CONSTANTS.GOLD_TO_LEAD * rc.senseGold(leadLoc) + Math.max(rc.senseLead(leadLoc) - 1, 0);
        return value;
    }
}

