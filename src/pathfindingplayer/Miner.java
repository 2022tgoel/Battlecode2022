package pathfindingplayer;

import battlecode.common.*;
import java.util.*;

public class Miner extends Unit {

    public boolean onMiningMission = false;
    public MapLocation mineLocation = null;

	public Miner(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        if (onMiningMission){
            runMiningMission();
        }
        // can include separate tracks here
        
    }

    /**
     * sendOnMiningMission() sets the minet on a mutlimove course to reach a location which contains resources
     * and mine the resources from that place
     * 
     * @param loc place which contains lead (rough measure of the region to mine)
     **/
    public void sendOnMiningMission(MapLocation loc) throws GameActionException{
        onMiningMission = true;
        mineLocation = loc;
    }

    /**
     * runMiningMission()
     * 
     **/
    public void runMiningMission() throws GameActionException{
        MapLocation cur = rc.getLocation();
        if (!cur.equals(mineLocation)){ //this is too strict, placeholder for now
            move(mineLocation);
        }
        else{
            int amountMined = mine();  
            if (amountMined < 3){
                //sense if there is a lucrative nearby area and move there instead
                MapLocation newLocation = findMiningArea();
                if (newLocation==null){
                    //abort
                    onMiningMission = false;
                    mineLocation = null;
                }
                else{
                    mineLocation = newLocation;
                }
            }
       }
        
    } 

    public MapLocation findMiningArea() throws GameActionException{
        MapLocation cur = rc.getLocation();
        int maxRes = 0;
        MapLocation bestLocation = null;
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                MapLocation loc = new MapLocation(cur.x + dx, cur.y + dy);
                if (cur.isWithinDistanceSquared(loc, 20)){
                    int res = rc.senseGold(loc)*goldToLeadConversionRate + rc.senseLead(loc);
                    if (res >  maxRes){
                        maxRes = res;
                        bestLocation = loc;
                    }
                }
            }
        }
        if (maxRes > 1){
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


}
