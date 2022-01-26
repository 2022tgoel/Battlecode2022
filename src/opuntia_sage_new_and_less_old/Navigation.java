package schlumbergera;
import battlecode.common.*;
public class Navigation{
	static RobotController rc;
	static BFS bfs;
	Navigation(RobotController rc) {
		this.rc = rc;
		if (rc.getType().visionRadiusSquared >=34){
			bfs = new BFSRad34(rc);
		}
		else {
			bfs = new BFSRad20(rc);
		}
	}

	public Direction getBestDir(MapLocation loc) throws GameActionException{
		return bfs.getBestDir(loc);
	}


}