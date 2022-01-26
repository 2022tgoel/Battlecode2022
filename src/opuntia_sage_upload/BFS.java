package opuntia_sage_upload;

import battlecode.common.*;
public abstract class BFS {
	abstract Direction getBestDir(MapLocation target) throws GameActionException;
}