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
        Direction dir = directions[rng.nextInt(directions.length)];
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


