package pathfindingplayer;

import battlecode.common.*;
import java.util.*;

public class Miner extends Unit {
    int travel_counter = 0;
    MapLocation target;
    Direction exploratoryDir = getExploratoryDir();
	public Miner(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        if (isExploring()){
            rc.move(exploratoryDir);
        }
        else {
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
                rc.move(cur.directionTo(target));
            }
        }
    }

    public boolean isExploring() throws GameActionException {
        if (travel_counter % 4 != 0) {
            return true;
        }
        else {
            rc.setIndicatorString("OOGA BOOGA");
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
        rc.setIndicatorString("BOOGA BOOGA");
        MapLocation bestLocation = null;
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                int x_coord = cur.x + dx;
                int y_coord = cur.y + dy;
                rc.setIndicatorString("BOOGA WOOGA");
                MapLocation loc;
                if (x_coord >= 0 && x_coord < rc.getMapWidth() && y_coord >= 0 && y_coord < rc.getMapHeight()) {
                    loc = new MapLocation(x_coord, y_coord);
                }
                else {
                    continue;
                }
                if (cur.isWithinDistanceSquared(loc, 20)){
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
        rc.setIndicatorString(exploratoryDir.dx + " " + exploratoryDir.dy);
        Direction[] dirs = {exploratoryDir, exploratoryDir.rotateLeft(), exploratoryDir.rotateRight()};
        return dirs[rng.nextInt(dirs.length)];
    }


}
