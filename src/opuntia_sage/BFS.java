package opuntia_sage;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
public abstract class BFS {
	abstract Direction getBestDir(MapLocation target) throws GameActionException;
}