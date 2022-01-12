package cereusplayer;

import battlecode.common.*;
import java.util.*;

public class Builder extends Unit {
    int travel_counter = 0;
    int[] exploratoryDir = getExploratoryDir();
	public Builder(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        RobotInfo h = rc.senseRobotAtLocation(homeArchon);
        if (h.type == RobotType.ARCHON && h.health < RobotType.ARCHON.health) { //healing mode
            if (rc.canRepair(homeArchon)) rc.repair(homeArchon);
        
        }
        else {
            forTheGreaterGood();
        }
    }



    public void forTheGreaterGood() throws GameActionException {
        MapLocation cur = rc.getLocation();
        MapLocation target = null;
        if (rc.senseLead(cur) == 0) {
            rc.disintegrate();
        }
        // robot finds closest spot to archon without lead on it, then destroys itself.
        int distSquared;
        int minDistSquared = 10000;
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                if (dx == 0 && dy == 0) continue;
                if (validCoords(cur.x + dx, cur.y + dy)) {
                    MapLocation loc = new MapLocation(cur.x + dx, cur.y + dy);
                    if (loc.equals(homeArchon)) continue;
                    if (rc.canSenseLocation(loc)) {
                        int numLead = rc.senseLead(loc);
                        if (numLead == 0) {
                            distSquared = cur.distanceSquaredTo(loc);
                            if (distSquared < minDistSquared) {
                                minDistSquared = distSquared;
                                target = loc;
                            }
                        }

                    }
                }
            }
        }
        if (target != null) {
            fuzzyMove(target);
            rc.setIndicatorString("target: " + target.x + " " + target.y);
        }
        else moveInDirection(exploratoryDir);
    }
}
