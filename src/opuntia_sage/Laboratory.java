package opuntia_sage;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Laboratory extends Unit {
    public enum MODE {
        DEFAULT,
        MOVING
    }

    MODE mode;

    public Laboratory(RobotController rc) throws GameActionException {
        super(rc);
    }

    public void run() throws GameActionException {
        radio.updateCounter();

        mode = getMode();
        switch (mode){
            case DEFAULT:
                if(rc.canTransmute()) {
                    // System.out.println("transmuted 1");
                    rc.transmute();
                }
                break;
            default:
                break;
        }
    }

    public MODE getMode(){
        return MODE.DEFAULT;
    }
}