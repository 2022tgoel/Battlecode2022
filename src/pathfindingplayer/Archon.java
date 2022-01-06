package pathfindingplayer;

import battlecode.common.*;
import java.util.*;

public class Archon extends Unit {
    int counter = 0;
    int[] build_order = chooseBuildOrder();
    int built_units = 0;
	public Archon(RobotController rc) throws GameActionException {
        super(rc);
    }

 	@Override
    public void run() throws GameActionException {
        // Pick a direction to build in.
        Direction[] dirs = sortedDirections();
        for (Direction dir: dirs) {
            if (built_units < build_order[counter % 3]) {
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
    }

    public Direction[] sortedDirections() throws GameActionException {
        Direction[] dirs = {Direction.NORTH, Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST, Direction.SOUTH, Direction.SOUTHWEST, Direction.WEST, Direction.NORTHWEST};

        Arrays.sort(dirs, (a,b) -> {
            try {
                return getRubble(a) - getRubble(b);
            } catch (GameActionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return 0;
        });
        return dirs;
    }

    public int getRubble(Direction d) throws GameActionException {
        MapLocation loc = rc.getLocation();
        return rc.senseRubble(loc.add(d));
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
}


