package opuntia_sage_new_and_less_old;

import battlecode.common.*;
public abstract class BFS {
	abstract Direction getBestDir(MapLocation target) throws GameActionException;
}