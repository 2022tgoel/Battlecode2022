package pathfindingplayer;

import battlecode.common.*;
import java.util.*;

public class Miner extends Unit {
    int[] exploratoryDir = getExploratoryDir();
    double soldier_repulsion = 2.0;
    
	public Miner(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        //Direction d = fuzzyMove(new MapLocation(27, 15));
       // rc.setIndicatorString(d.toString());
        if (!mining_detour()) {
            moveInDirection(safeDir(exploratoryDir));
        }
        if (adjacentToEdge()) {
            exploratoryDir = getExploratoryDir();
        }
        rc.setIndicatorString("exploratoryDir: " + exploratoryDir[0] + " " + exploratoryDir[1]);
        senseArchon();
    }

    public boolean mining_detour() throws GameActionException {
        MapLocation cur = rc.getLocation();
        int amountMined = mine();
        if (minersAdjacentToLocation(cur)) {
            return false;
        }
        if (amountMined < 4){
            MapLocation target = findMiningArea();
            if (target != null) {
                if (!cur.equals(target)){
                    //sense if there is a lucrative nearby area and move there instead
                    moveInDirection(safeDir(target));
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public Direction safeDir(MapLocation dir) throws GameActionException {
        Direction d = rc.getLocation().directionTo(dir);
        double[] doubleDir = directionToVector(d);
        double[] soldier_repulsion = fleeFromSoldiers();
        double[] archon_repulsion = archonRepulsion();
        double[] newDir = addVectors(doubleDir, soldier_repulsion);
        newDir = addVectors(newDir, archon_repulsion);
        return doubleToDirection(newDir[0], newDir[1]);
    }

    public Direction safeDir(int[] dir) throws GameActionException {
        Direction d = doubleToDirection((double) dir[0], (double) dir[1]);
        double[] doubleDir = directionToVector(d);
        double[] soldier_repulsion = fleeFromSoldiers();
        double[] archon_repulsion = archonRepulsion();
        double[] newDir = addVectors(doubleDir, soldier_repulsion);
        newDir = addVectors(newDir, archon_repulsion);
        return doubleToDirection(newDir[0], newDir[1]);
    }

    public double[] archonRepulsion() throws GameActionException {
        MapLocation cur = rc.getLocation();
        Direction d = rc.getLocation().directionTo(homeArchon).opposite();
        if (cur.distanceSquaredTo(homeArchon) <= 5) {
            return directionToVector(d);
        }
        else return new double[]{0,0};
    }

    public double[] fleeFromSoldiers() {
        double[] result = {0.0,0.0};
        MapLocation my = rc.getLocation();
        int num_enemies = 0;
        RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());

        // if no bots from opposite team return
        if (enemies.length == 0) {
            return result;
        }
        for (RobotInfo ri : enemies) {
            if (ri.type == RobotType.SOLDIER || ri.type == RobotType.WATCHTOWER || ri.type == RobotType.SAGE) {
                result[0] += ri.location.x;
                result[1] += ri.location.y;
                num_enemies += 1;
            }
        }

        // if no damaging enemies from enemy team, return
        if (num_enemies == 0) {
            return result;
        }

        result[0] /= num_enemies;
        result[1] /= num_enemies;
        result[0] = -1 * (result[0] - my.x) * soldier_repulsion;
        result[1] = -1 * (result[1] - my.y) * soldier_repulsion;
        return result;
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
                if (rc.canSenseLocation(loc)) {
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

    public boolean minersAdjacentToLocation(MapLocation loc) throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots(loc, 2, rc.getTeam());
        for (RobotInfo robot : robots) {
            if (robot.type == RobotType.MINER) {
                return true;
            }
        }
        return false;
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
