package exploratoryplayer;

import battlecode.common.*;
import java.util.*;

public class Miner extends Unit {
    int mode = 0; 
    /*
    modes explanation

    0 is waiting 
    1 in exploring, trying to find archons
    2 in mining
    3 in hunting
    */

    int archon_index = -1;

    int[] exploratoryDir = getExploratoryDir();
	public Miner(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        //Direction d = fuzzyMove(new MapLocation(27, 15));
       // rc.setIndicatorString(d.toString());
        switch (mode){
            case 0:
                wait();
            case 1:
                moveInDirection(getExploratoryDir());
                boolean b = senseArchon();
                if (b) mode = 3; // switch to hunting mode
            case 2:
                mining_detour();
            case 3:
                boolean b = approachArchon();
                if (!b) mode =1; // switch to exploration again
        }
        if (archon_index==-1){ //if you are not in hunting mode
            boolean b= detectArchon(); 
            if (b) mode = 3;
        }
    }

    public void wait(){
        //TODO 
    }
    
    MapLocation miningTarget = null;
    public void mining_detour() throws GameActionException {
        if (miningTarget == null){
            miningTarget = findMiningArea();
        }
        if (miningTarget == null){ //still nothing
            return;
        }
        MapLocation cur = rc.getLocation();
        if (cur.equals(miningTarget)){
            int amountMined = mine();
            if (amountMined < 3){
                //sense if there is a lucrative nearby area and move there instead
                MapLocation newLocation = findMiningArea();
                miningTarget = newLocation;
            }
        }
        else {
            moveToLocation(miningTarget);
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
