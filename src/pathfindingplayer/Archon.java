package pathfindingplayer;

import battlecode.common.*;
import java.util.*;

public class Archon extends Unit {
    int counter = 0;
    int[] build_order = chooseBuildOrder();
    int built_units = 0;
    int num_builders = 0;
    boolean fortified = true;
	public Archon(RobotController rc) throws GameActionException {
        super(rc);
    }

 	@Override
    public void run() throws GameActionException {
        // Pick a direction to build in.
        Direction[] dirs = sortedDirections();
        for (Direction dir: dirs) {
            if (rc.getTeamGoldAmount(rc.getTeam()) >= 50) {
                if (rc.canBuildRobot(RobotType.SAGE, dir)) {
                    rc.buildRobot(RobotType.SAGE, dir);
                    break;
                }
            }
            else if (built_units < build_order[counter % 3]) {
                switch (counter % 3) {
                    case 0:
                        rc.setIndicatorString("Trying to build a miner" + " built_units: " + built_units + " " + counter);
                        if (rc.canBuildRobot(RobotType.MINER, dir)) {
                            rc.buildRobot(RobotType.MINER, dir);
                            built_units++;
                        }
                        break;
                    case 1:
                        rc.setIndicatorString("Trying to build a soldier");
                        if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                            rc.buildRobot(RobotType.SOLDIER, dir);
                            built_units++;
                        }
                        break;
                    case 2:
                        rc.setIndicatorString("Trying to build a builder");
                        if (rc.canBuildRobot(RobotType.BUILDER, dir)) {
                            rc.buildRobot(RobotType.BUILDER, dir);
                            built_units++;
                        }
                        break;
                }
            }
            else {
                counter++;
                built_units = 0;
            }
        }
        attemptHeal();
        turn_update();
    }
    /**
     * enemySoldiersInRange() checks if a soldier that might want to attack in nearby
     **/
    public boolean enemySoldiersInRange() throws GameActionException{
        for (RobotInfo r :rc.senseNearbyRobots(RobotType.ARCHON.actionRadiusSquared, rc.getTeam().opponent()) ){
            if (r.type == RobotType.SOLDIER){
                return true;
            }
        }
        return false;
    }
    /**
     * fortify() builds the builders so that they can build the watchtowers (in a designated formation for now)
     **/
    public void fortify() throws GameActionException{
        Direction[] dirs = {Direction.NORTHEAST,Direction.SOUTHEAST, Direction.SOUTHWEST,Direction.NORTHWEST,}; //directions to build builders in
        MapLocation my = rc.getLocation();
        for (int i =0 ;i < dirs.length; i++){
            MapLocation builderLocation  = my.add(dirs[i]);
            if (rc.canBuildRobot(RobotType.BUILDER, dirs[i])){
                rc.buildRobot(RobotType.BUILDER, dirs[i]);
                num_builders++;
            }
        }
    }

    public Direction[] sortedDirections() throws GameActionException {
        Direction[] dirs = {Direction.NORTH, Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST, Direction.SOUTH, Direction.SOUTHWEST, Direction.WEST, Direction.NORTHWEST};
        Arrays.sort(dirs, (a,b) -> getRubble(a) - getRubble(b));
        return dirs;
    }

    public int getRubble(Direction d) {
        try {
            MapLocation loc = rc.getLocation();
            return rc.senseRubble(loc.add(d));
        } catch (GameActionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    public int[] chooseBuildOrder() {
        int mapArea = rc.getMapHeight() * rc.getMapHeight();
        if (mapArea < 1400) {
            return new int[]{2, 3, 0}; // miners, soldiers, builders
        }
        else if (mapArea < 2200) {
            return new int[]{2, 2, 0}; // miners, soldiers, builders
        }
        else {
            return new int[]{3, 2, 0}; // miners, soldiers, builders
        }
    }

    public void attemptHeal() throws GameActionException {
        boolean soldiers_home = false;
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(RobotType.ARCHON.actionRadiusSquared, rc.getTeam());
        // if there are any nearby enemy robots, attack the one with the least health
        if (nearbyBots.length > 0) {
            RobotInfo weakestBot = nearbyBots[0];
            for (RobotInfo bot : nearbyBots) {
                if (bot.type == RobotType.SOLDIER)
                    if (bot.health < weakestBot.health) {
                        weakestBot = bot;
                    }
                    soldiers_home = true;
            }
            if (soldiers_home) {
                if (rc.canRepair(weakestBot.location)) {
                    rc.setIndicatorString("Succesful Heal!");
                    rc.repair(weakestBot.location);
                }
            }
            else {
                for (RobotInfo bot : nearbyBots) {
                    if (bot.type == RobotType.MINER)
                        if (bot.health < weakestBot.health) {
                            weakestBot = bot;
                        }
                }
                if (rc.canRepair(weakestBot.location)) {
                    rc.repair(weakestBot.location);
                }
            }
        }
    }
}


