package pathfindingplayer;

import battlecode.common.*;
import java.util.*;

public class Miner extends Unit {
    boolean archon_found = false;
    int archon_index = -1;
    MapLocation target;

    int[] exploratoryDir = getExploratoryDir();
	public Miner(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        //Direction d = fuzzyMove(new MapLocation(27, 15));
       // rc.setIndicatorString(d.toString());
        
        rc.setIndicatorString("exploratoryDir: " + exploratoryDir[0] + " " + exploratoryDir[1]);
        if (isExploring()){
            moveInDirection(exploratoryDir);
            if (adjacentToEdge()) {
                exploratoryDir = getExploratoryDir();
            }
        }
        else if (!archon_found) {
            mining_detour();
        }
        else {
            huntArchon();
        }

        if (!archon_found) {
            senseArchon();
        }
    }

    public void huntArchon() throws GameActionException {
        int data = rc.readSharedArray(archon_index);
        if (data != 0) {
            int x = data / 1000;
            int y = data % 1000;
            target = new MapLocation(x, y);
            fuzzyMove(target);
        }
        else {
            archon_found = false;
        }
    }

    public boolean isExploring() throws GameActionException {
        if (archon_found) {
            return false;
        }
        else {
            MapLocation newLocation = findMiningArea();
            if (newLocation != null) {
                target = newLocation;
                return false;
            }
            else {
                return true;
            }
        }
    }

    public void mining_detour() throws GameActionException {
        MapLocation cur = rc.getLocation();
        if (cur.equals(target)){
            int amountMined = mine();
            if (amountMined < 3){
                //sense if there is a lucrative nearby area and move there instead
                MapLocation newLocation = findMiningArea();
                if (newLocation==null) {
                    target = null;
                }
                else {
                    target = newLocation;
                }
            }
        }
        else {
            moveToLocation(target);
        }
    }

    /**
     * findMiningArea()
     * Returns the location of the most lucrative mining area outside of mining radius
     **/
    public MapLocation findMiningArea() throws GameActionException{
        MapLocation cur = rc.getLocation();
        // if square only has 1 lead don't go for it.
        int maxRes = 1;
        MapLocation bestLocation = null;
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                int x_coord = cur.x + dx;
                int y_coord = cur.y + dy;
                MapLocation loc;
                if (validCoords(x_coord, y_coord)) {
                    loc = new MapLocation(x_coord, y_coord);
                }
                else {
                    continue;
                }
                if (rc.canSenseLocation(loc)){
                    int res = rc.senseGold(loc) * goldToLeadConversionRate + rc.senseLead(loc);
                    if (res > maxRes) {
                        maxRes = res;
                        bestLocation = loc;
                    }
                }
            }
        }

        // if our best location is outside of the mining range, return it
        if (maxRes > 1 && bestLocation != null) {
            return bestLocation;
        }
        else return null;
    }
    /**
     * mine() mines the surrounding area
     * @return int representing total value of what was mined
     **/
    public int mine() throws GameActionException{
        MapLocation me = rc.getLocation();
        //prioritize gold
        int amountMined = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation loc = new MapLocation(me.x + dx, me.y + dy);
                // Notice that the Miner's action cooldown is very low.
                // You can mine multiple times per turn!
                while (rc.canMineGold(loc)) {
                    rc.mineGold(loc);
                    amountMined += goldToLeadConversionRate;
                }
            }
        }
        //then go to lead
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation loc = new MapLocation(me.x + dx, me.y + dy);
                // Notice that the Miner's action cooldown is very low.
                // You can mine multiple times per turn!
                while (rc.canMineLead(loc) && rc.senseLead(loc) > 1) {
                    rc.mineLead(loc);
                    amountMined+=1;
                }
            }
        }
        return amountMined;
    }
}
