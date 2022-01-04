package pathfindingplayer;

import battlecode.common.*;
import java.util.*;

public class Archon extends Unit {
	boolean builtMiner = false;
	public Archon(RobotController rc) throws GameActionException {
        super(rc);
    }

 	@Override
    public void run() throws GameActionException {
        // Pick a direction to build in.
        Direction dir = directions[rng.nextInt(directions.length)];
        if (rng.nextBoolean()) {
            // Let's try to build a miner.
            rc.setIndicatorString("Trying to build a miner");
            if (!builtMiner && rc.canBuildRobot(RobotType.MINER, dir)) {
                rc.buildRobot(RobotType.MINER, dir);
                builtMiner = true;
            }
            rc.setIndicatorString("builtMiner = " + builtMiner);
        }
    }
}
