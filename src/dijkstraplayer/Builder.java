package dijkstraplayer;

import battlecode.common.*;
import java.util.*;

public class Builder extends Unit {
    int travel_counter = 0;
    MapLocation target;
    Direction exploratoryDir = getExploratoryDir();
	public Builder(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        if (isExploring()){
            moveInDirection(exploratoryDir);
        }
    }

    public boolean isExploring() throws GameActionException {
        return true;
    }

    public Direction getExploratoryDir() {
        MapLocation cur = rc.getLocation();
        MapLocation center = new MapLocation(rc.getMapHeight()/2, rc.getMapWidth()/2);
        if (center.x - cur.x > 0) {
            if (center.y - cur.y > 0) {
                exploratoryDir = Direction.NORTHEAST;
            } else {
                exploratoryDir = Direction.SOUTHEAST;
            }
        } else {
            if (center.y - cur.y > 0) {
                exploratoryDir = Direction.NORTHWEST;
            } else {
                exploratoryDir = Direction.SOUTHWEST;
            }
        }
        rc.setIndicatorString(exploratoryDir.dx + " " + exploratoryDir.dy);
        Direction[] dirs = {exploratoryDir, exploratoryDir.rotateLeft(), exploratoryDir.rotateRight()};
        return dirs[rng.nextInt(dirs.length)];
    }
}
