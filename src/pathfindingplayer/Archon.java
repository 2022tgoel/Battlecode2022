package pathfindingplayer;

import battlecode.common.*;
import java.util.*;

public class Archon extends Unit {
    int counter = 0;
    int[] build_order = {3, 2, 1}; // miners, soldiers, builders
    int built_units = 0;
	public Archon(RobotController rc) throws GameActionException {
        super(rc);
    }

 	@Override
    public void run() throws GameActionException {
        // Pick a direction to build in.
        Direction dir = directions[rng.nextInt(directions.length)];
        rc.setIndicatorString("Trying to build a miner");
        if (built_units < build_order[counter]) {
            switch (counter) {
                case 0:
                    rc.setIndicatorString("Trying to build a miner");
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


