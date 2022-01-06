package exploratoryplayer;

import battlecode.common.*;
import java.util.*;

public class Builder extends Unit {
    int travel_counter = 0;
    MapLocation target;
    int[] exploratoryDir = getExploratoryDir();
	public Builder(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        /* builds watchtowers in surrounding regoin */
        Direction[] dirs = {Direction.NORTHEAST,Direction.SOUTHEAST, Direction.SOUTHWEST,Direction.NORTHWEST,}; 
        MapLocation my = rc.getLocation();
        for (int i =0 ;i < dirs.length; i++){
            MapLocation watchtowerLocation  = my.add(dirs[i]);
            if (rc.canBuildRobot(RobotType.WATCHTOWER, dirs[i])){
                rc.buildRobot(RobotType.WATCHTOWER, dirs[i]);
            }
        }
    }
}
