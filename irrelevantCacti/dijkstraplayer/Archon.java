package dijkstraplayer;

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
        if (built_units < build_order[counter % 3]) {
            if (counter <1) {
                rc.setIndicatorString("Trying to build a miner" + " built_units: " + built_units + " " + counter);
                if (rc.canBuildRobot(RobotType.MINER, dir)) {
                    rc.buildRobot(RobotType.MINER, dir);
                    built_units++;
                }
            }
        }
        else {
            counter++;
            built_units = 0;
        }
    }
}


