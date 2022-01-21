package rebutia;

import battlecode.common.*;

public class Comms {
    RobotController rc;
    private int round_num;
    private boolean wasFirstConnection;

    public Comms(RobotController robotController) throws GameActionException {
        rc = robotController;
    }

    public boolean update() throws GameActionException {
        round_num = rc.getRoundNum();

        wasFirstConnection = false;
        // TODO: distribute init comms clearing?
        if (rc.readSharedArray(CHANNEL.ROUND_NUM.getValue()) != rc.getRoundNum()) {
            rc.writeSharedArray(CHANNEL.ROUND_NUM.getValue(), rc.getRoundNum());

            // clear robot counter update channels
            for (BiCHANNEL bich : BiCHANNEL.values()) {
                CHANNEL ch = getCounterChannel(bich, false);
                if (ch != null)
                    rc.writeSharedArray(ch.getValue(), 0);
            }

            wasFirstConnection = true;
        }
        return wasFirstConnection;
    }

    public CHANNEL getCounterChannel(BiCHANNEL bich, boolean isReadMode) {
        boolean mod = rc.getRoundNum() % 2 == 0;
        if (isReadMode)
            mod = !mod;
        return mod ? bich.ch1 : bich.ch2;
    }

    public void updateCounter() throws GameActionException {
        updateCounter(rc.getType());
    }

    public void updateCounter(RobotType rt) throws GameActionException {
        updateCounter(getRobotCounterBiChannel(rt));
    }

    public void updateCounter(BiCHANNEL bich) throws GameActionException {
        CHANNEL channel = getCounterChannel(bich, false);
        int num = wasFirstConnection ? 0 : rc.readSharedArray(channel.getValue());
        rc.writeSharedArray(channel.getValue(), num + 1);
    }

    public int readCounter(RobotType rt) throws GameActionException {
        return readCounter(getRobotCounterBiChannel(rt));
    }

    public int readCounter(BiCHANNEL bich) throws GameActionException {
        CHANNEL channel = getCounterChannel(bich, true);
        return rc.readSharedArray(channel.getValue());
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

    public BiCHANNEL getRobotCounterBiChannel(RobotType rt) {
        switch (rt) {
            case MINER:
                return BiCHANNEL.MINERS_ALIVE;
            case SOLDIER:
                return BiCHANNEL.SOLDIERS_ALIVE;
            case BUILDER:
                return BiCHANNEL.BUILDERS_ALIVE;
            case WATCHTOWER:
                return BiCHANNEL.TOWERS_ALIVE;
            default:
                return null;
        }
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
                rc.writeSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + i, 0);
            }
        }
    }

    public void clearTargetAreas() throws GameActionException {
        if (round_num % 3 == 0) {
            for (int i = 0; i < CHANNEL.NUM_TARGETS; i++) {
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
            int data = rc.readSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + i);
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
        rc.writeSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + threatChannel, locationToInt(my));
        return threatChannel;
    }

    public int totalUnderThreat() throws GameActionException {
        int numThreatenedArchons = 0;
        for (int i = 0; i < 4; i++) {
            // rc.writeSharedArray(, value);
            int data = rc.readSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + i);
            if (data != 0)
                numThreatenedArchons++;
        }
        return numThreatenedArchons;
    }

    public int getArchonNumber() throws GameActionException {
        int chan = CHANNEL.ARCHON_NUMBER.getValue();
        int archonNumber = rc.readSharedArray(chan);
        rc.writeSharedArray(chan, archonNumber + 1);
        return archonNumber;
    }

    public void clearArchonNumbers() throws GameActionException {
        // if you don't read all 0s for the first four numbers, set them to zero.
        for (int i = 0; i < 4; i++) {
            if ((rc.readSharedArray(CHANNEL.ARCHON_LOC_1.getValue() + i)) != 0) {
                rc.writeSharedArray(i, 0);
            }
        }
    }

    public int getArchonNumInit() throws GameActionException {
        int data;
        for (int i = 0; i < 4; i++) {
            data = rc.readSharedArray(CHANNEL.ARCHON_LOC_1.getValue() + i);
            if (data == 0) {
                rc.writeSharedArray(i, 1);
                if (i == rc.getArchonCount() - 1) {
                    clearArchonNumbers();
                }
                return i;
            }
        }
        return -1;
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

    public int locationToInt(MapLocation loc) {
        return 64 * loc.x + loc.y;
    }
}