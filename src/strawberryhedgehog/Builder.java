package strawberryhedgehog;

import battlecode.common.*;
import java.util.*;

public class Builder extends Unit {
    int travel_counter = 0;
    int[] exploratoryDir = getExploratoryDir();
    public Builder(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        RobotInfo h = rc.senseRobotAtLocation(homeArchon);
        if (h.type == RobotType.ARCHON && h.health < RobotType.ARCHON.health) { //healing mode
            if (rc.canRepair(homeArchon)) rc.repair(homeArchon);
        
        }
        else {
        }
    }


}