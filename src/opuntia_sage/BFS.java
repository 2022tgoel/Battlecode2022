package opuntia_sage;

import battlecode.common.*;
public abstract class BFS {
	abstract Direction getBestDir(MapLocation target) throws GameActionException;
}