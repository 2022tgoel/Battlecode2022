package pathfindingplayer;

import battlecode.common.*;
import java.util.*;

public class Archon extends Unit {
	public Archon(RobotController rc) throws GameActionException {
        super(rc);
    }

 	@Override
    public void run() throws GameActionException {
        // Pick a direction to build in.
        Direction dir = directions[rng.nextInt(directions.length)];
        rc.setIndicatorString("Trying to build a miner");
        if (rc.canBuildRobot(RobotType.MINER, dir)) {
            rc.buildRobot(RobotType.MINER, dir);
        }
    }
}
