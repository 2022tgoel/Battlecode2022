package kiwi;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Comms {
    RobotController rc;
    private int round_num;

    public Comms(RobotController robotController) throws GameActionException {
        rc = robotController;
    }

    public void update() {
        round_num = rc.getRoundNum();
    }

    public int getMiners() throws GameActionException {
        return rc.readSharedArray(CHANNEL.MINERS_ALIVE.getValue());
    }

    public void setMiners(int value) throws GameActionException {
        rc.writeSharedArray(CHANNEL.MINERS_ALIVE.getValue(), value);
    }

    public int getSoldiers() throws GameActionException {
        return rc.readSharedArray(CHANNEL.SOLDIERS_ALIVE.getValue());
    }

    public void setSoldiers(int value) throws GameActionException {
        rc.writeSharedArray(CHANNEL.SOLDIERS_ALIVE.getValue(), value);
    }

    public int getBuilders() throws GameActionException {
        return rc.readSharedArray(CHANNEL.BUILDERS_ALIVE.getValue());
    }

    public void setBuilders(int value) throws GameActionException {
        rc.writeSharedArray(CHANNEL.BUILDERS_ALIVE.getValue(), value);
    }

    public void clearCounts() throws GameActionException {
        rc.writeSharedArray(CHANNEL.MINERS_ALIVE.getValue(), 0);
        rc.writeSharedArray(CHANNEL.SOLDIERS_ALIVE.getValue(), 0);
        rc.writeSharedArray(CHANNEL.BUILDERS_ALIVE.getValue(), 0);
    }

    public void postBuild(BOT b) throws GameActionException {
        switch (b) {
            case MINER:
                rc.writeSharedArray(CHANNEL.UNIT_BUILT.getValue(), BOT.MINER.getValue());
                break;
            case SOLDIER:
                rc.writeSharedArray(CHANNEL.UNIT_BUILT.getValue(), BOT.SOLDIER.getValue());
                break;
            case BUILDER:
                rc.writeSharedArray(CHANNEL.UNIT_BUILT.getValue(), BOT.BUILDER.getValue());
                break;
            case SAGE:
                rc.writeSharedArray(CHANNEL.UNIT_BUILT.getValue(), BOT.SAGE.getValue());
                break;
            case NONE:
                rc.writeSharedArray(CHANNEL.UNIT_BUILT.getValue(), BOT.NONE.getValue());
            default:
                break;
        }
    }

    public BOT getPreviousBuild() throws GameActionException {
        int data = rc.readSharedArray(CHANNEL.UNIT_BUILT.getValue());
        switch (data) {
            case 0:
                return BOT.NONE;
            case 1:
                return BOT.MINER;
            case 2:
                return BOT.SOLDIER;
            case 3:
                return BOT.BUILDER;
            case 4:
                return BOT.SAGE;
            default:
                return BOT.NONE;
        }
    }

    public void clearThreat() throws GameActionException {
        if (round_num % 15 == 0) {
            for (int i = 0; i < 4; i++) {
                rc.writeSharedArray(CHANNEL.FRIENDLY_ARCHON_STATUS.getValue() + i, 0);
            }
        }
    }

    public void clearTargetAreas() throws GameActionException {
        if (round_num % 3 == 0) {
            for (int i = 0; i < 5; i++) {
                rc.writeSharedArray(CHANNEL.TARGET.getValue() + i, 0);
            }
        }
    }

    public void clearMiningAreas() throws GameActionException {
        if (round_num % 3 == 0) {
            for (int i = 0; i < 5; i++) {
                rc.writeSharedArray(CHANNEL.MINING1.getValue() + i, 0);
            }
        }
    }

    public int sendThreatAlert() throws GameActionException {
        MapLocation my = rc.getLocation();
        int threatChannel = -1;
        for (int i = 0; i < 4; i++) {
            // rc.writeSharedArray(, value);
            int data = rc.readSharedArray(CHANNEL.FRIENDLY_ARCHON_STATUS.getValue() + i);
            // go through channels until you find an empty one to communicate with.
            int x = data / 64;
            int y = data % 64;
            // already alerted.
            if (x == my.x && y == my.y) {
                threatChannel = i;
                return threatChannel;
            }
            if (data == 0 && threatChannel == -1) {
                threatChannel = i;
            }
        }
        if (threatChannel == -1)
            threatChannel = 4; // override
        rc.writeSharedArray(CHANNEL.FRIENDLY_ARCHON_STATUS.getValue() + threatChannel, locationToInt(my));
        return threatChannel;
    }

    public int totalUnderThreat() throws GameActionException {
        int numThreatenedArchons = 0;
        for (int i = 0; i < 4; i++) {
            // rc.writeSharedArray(, value);
            int data = rc.readSharedArray(CHANNEL.FRIENDLY_ARCHON_STATUS.getValue() + i);
            if (data != 0)
                numThreatenedArchons++;
        }
        return numThreatenedArchons;
    }

    public void clearArchonNumbers() throws GameActionException {
        // if you don't read all 0s for the first four numbers, set them to zero.
        for (int i = 0; i < 4; i++) {
            if ((rc.readSharedArray(CHANNEL.ENEMY_ARCHON_LOCATION.getValue() + i)) != 0) {
                rc.writeSharedArray(i, 0);
            }
        }
    }

    public static MapLocation getFriendlyArchonLocation(RobotController rc, int i) throws GameActionException {
        if (i > rc.getArchonCount()) {
            return null;
        }

        int data = rc.readSharedArray(CHANNEL.FRIENDLY_ARCHON_LOCATION.getValue() + i);
        return intToLocation(data);
    }

    public int assignArchonNumber() throws GameActionException {
        int baseChannelID = CHANNEL.FRIENDLY_ARCHON_LOCATION.getValue();
        for (int i = 0; i < 4; i++) {
            if (rc.readSharedArray(baseChannelID + i) == 0) {
                rc.writeSharedArray(baseChannelID + i, locationToInt(rc.getLocation()));
                return i;
            }
        }
        return 0;
    }

    public void postRank(RANK rank) throws GameActionException {
        MapLocation loc = rc.getLocation();
        int loc_int;
        if (rank == RANK.DEFAULT) {
            return;
        } else {
            // all locations are within 60, so can be compressed to 6 bits.
            loc_int = rank.getValue() * 4096 + locationToInt(loc);
            if (rc.getRoundNum() % 2 == 0) {
                rc.writeSharedArray(CHANNEL.SEND_RANKS1.getValue(), loc_int);
            } else {
                rc.writeSharedArray(CHANNEL.SEND_RANKS2.getValue(), loc_int);
            }
        }
    }

    public void clearRanks() throws GameActionException {
        if (rc.getRoundNum() % 2 == 0) {
            rc.writeSharedArray(CHANNEL.SEND_RANKS1.getValue(), 0);
        } else {
            rc.writeSharedArray(CHANNEL.SEND_RANKS2.getValue(), 0);
        }
    }

    public MapLocation getEnemyArchonLocation(int i) throws GameActionException {
        return intToLocation(rc.readSharedArray(CHANNEL.ENEMY_ARCHON_LOCATION.getValue() + i));
    }

    public static int locationToInt(MapLocation loc) {
        return 64 * loc.x + loc.y;
    }

    public static MapLocation getTarget(RobotController rc, int i) throws GameActionException {
        int data = rc.readSharedArray(CHANNEL.TARGET.getValue() + i);
        return intToLocation(data);
    }

    public static MapLocation intToLocation(int loc_int) {
        if (loc_int == 0) {
            return null;
        }
        int x = loc_int / 64;
        int y = loc_int % 64;
        return new MapLocation(x, y);
    }
}