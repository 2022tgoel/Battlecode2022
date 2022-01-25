package opuntia_sage_old;

import battlecode.common.*;
public abstract class BFS {
	abstract Direction getBestDir(MapLocation target) throws GameActionException;
}