package baseline;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public enum CHANNEL {
    // channels 0 - 3
    ENEMY_ARCHON_LOCATION(0),
    // channels 4 - 7
    FRIENDLY_ARCHON_STATUS(4),
    ARCHON_ALIVE(8),
    UNIT_BUILT(9),
    MINERS_ALIVE(10),
    SOLDIERS_ALIVE(11),
    BUILDERS_ALIVE(12),
    // channels 15 - 19
    MINING1(15),
    // channels 20 - 24
    TARGET(20),
    // channels 25 - 28
    FRIENDLY_ARCHON_LOCATION(25),
    // channels 29 - 33
    DISTRESS(29),
    DISTRESS1(30),
    DISTRESS2(31),
    DISTRESS3(32),
    DISTRESS4(33),
    ORDERS(61),
    SEND_RANKS1(62),
    SEND_RANKS2(63),
    ;

    public static final int NUM_TARGETS = 5;

    private final int id;

    public static final CHANNEL[] byID = new CHANNEL[64];

    CHANNEL(int id) {
        this.id = id;
    }

    static {
        for (CHANNEL c : CHANNEL.values()) {
            byID[c.id] = c;
        }
    }

    public int readInt(RobotController rc) throws GameActionException {
        return rc.readSharedArray(id);
    }

    public void writeInt(RobotController rc, int value) throws GameActionException {
        rc.writeSharedArray(id, value);
    }

    public MapLocation readLocation(RobotController rc) throws GameActionException {
        return Comms.intToLocation(rc.readSharedArray(this.id));
    }

    public void writeLocation(RobotController rc, MapLocation location) throws GameActionException {
        rc.writeSharedArray(this.id, Comms.locationToInt(location));
    }

    public int getValue() {
        return id;
    }
}
