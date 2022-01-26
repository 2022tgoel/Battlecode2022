package opuntia_sage_new_and_less_old;

import battlecode.common.*;

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
                if (rc.getTeamGoldAmount(rc.getTeam()) > 40) return;
                if (radio.readCounter(RobotType.MINER) <= 1){
                    return;
                }
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