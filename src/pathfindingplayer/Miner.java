package pathfindingplayer;

import battlecode.common.*;
import java.util.*;

public class Miner extends Unit {
    boolean archon_found = false;
    MapLocation target;

    Direction exploratoryDir = getExploratoryDir();
	public Miner(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        if (isExploring()){
            moveInDirection(exploratoryDir);
        }
        else if (!archon_found) {
            mining_detour();
        }
        else {
            huntArchon();
        }

        if (!archon_found) {
            rc.setIndicatorString("before detect");
            detectArchon();
        }
    }

    public void huntArchon() throws GameActionException {
        rc.setIndicatorString("right before read");
        int data = rc.readSharedArray(0);
        if (data != 0) {
            rc.setIndicatorString("succesful read");
            int x = data / 1000;
            int y = data % 1000;
            target = new MapLocation(x, y);
            fuzzyMove(target);
        }
    }

    public void detectArchon() throws GameActionException {
        rc.setIndicatorString("before sense");
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        // if there are any nearby enemy robots, attack the one with the least health
        if (nearbyBots.length > 0) {
            for (RobotInfo bot : nearbyBots) {
                if (bot.type == RobotType.ARCHON) {
                    archon_found = true;
                    rc.setIndicatorString("before broadcast");
                    broadcastArchon(bot.location);
                    // writeSharedArray(int index, int value)
                }
            }
        }
    }

    public void broadcastArchon(MapLocation loc) throws GameActionException{
        // convert two ints to one int
        int x = loc.x;
        int y = loc.y;
        int loc_int = x * 1000 + y;
        rc.setIndicatorString("right before broadcast");
        if (rc.readSharedArray(0) == 0) {
            rc.writeSharedArray(0, loc_int);
        }
        else {
            rc.writeSharedArray(1, loc_int);
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

    //
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
     * runMiningMission()
     * 
     **/

    /**
     * findMiningArea()
     * Returns the location of the most lucrative mining area outside of mining radius
     **/
    public MapLocation findMiningArea() throws GameActionException{
        MapLocation cur = rc.getLocation();
        int maxRes = 0;
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
                while (rc.canMineLead(loc)) {
                    rc.mineLead(loc);
                    amountMined+=1;
                }
            }
        }
        return amountMined;
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
