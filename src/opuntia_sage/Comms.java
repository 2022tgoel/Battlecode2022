package opuntia_sage;

import battlecode.common.*;

public class Comms {
    RobotController rc;
    private int round_num;
    boolean wasFirstConnection;

    public Comms(RobotController robotController) throws GameActionException {
        rc = robotController;
    }

    public boolean init() throws GameActionException {
   //     System.out.println("i am here");
        wasFirstConnection = false;
        // TODO: distribute init comms clearing?
        round_num = rc.getRoundNum();
        if (rc.readSharedArray(CHANNEL.ROUND_NUM.getValue()) != round_num) {
            rc.writeSharedArray(CHANNEL.ROUND_NUM.getValue(), round_num);

            // clear robot counter update channels
            for (BiCHANNEL bich : BiCHANNEL.values()) {
                CHANNEL ch = getCounterChannel(bich, false);
                if (ch != null)
                    rc.writeSharedArray(ch.getValue(), 0);
            }

            if(round_num == 1){
                clearLabLoc();
            }

            wasFirstConnection = true;
        }
        return wasFirstConnection;
    }

    public void update() {
        round_num = rc.getRoundNum();
    }

    public boolean requestLead(int val) throws GameActionException {
        int req = readChannel(CHANNEL.REQUEST_LEAD);
        if(req == 0){
            writeChannel(CHANNEL.REQUEST_LEAD, val);
            return true;
        }
        return false;
    }

    public int readLeadRequest() throws GameActionException {
        return readChannel(CHANNEL.REQUEST_LEAD);
    }

    public void removeLeadRequest() throws GameActionException {
        writeChannel(CHANNEL.REQUEST_LEAD, 0);
    }

    public int readChannel(CHANNEL ch) throws GameActionException {
        return rc.readSharedArray(ch.getValue());
    }

    public void writeChannel(CHANNEL ch, int val) throws GameActionException {
        rc.writeSharedArray(ch.getValue(), val);
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
            case LABORATORY:
                return BiCHANNEL.LABS_ALIVE;
            case SAGE:
                return BiCHANNEL.SAGES_ALIVE;
            default:
                return null;
        }
    }

    public CHANNEL getCounterChannel(BiCHANNEL bich, boolean isReadMode) {
        boolean mod = rc.getRoundNum() % 2 == 0;
        if (isReadMode)
            mod = !mod;
        return mod ? bich.ch1 : bich.ch2;
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
        if (round_num % CONSTANTS.SOLDIER_REFRESH_RATE == 0) {
            for (int i = 0; i < CHANNEL.NUM_TARGETS; i++) {
                rc.writeSharedArray(CHANNEL.TARGET.getValue() + i, 0);
            }
        }
    }

    public void clearMiningAreas() throws GameActionException {
        if (round_num % CONSTANTS.MINER_REFRESH_RATE == 0) {
            for (int i = 0; i < 5; i++) {
                rc.writeSharedArray(CHANNEL.MINING1.getValue() + i, 0);
            }
        }
    }

    public int getLeadEstimate() throws GameActionException {
        return rc.readSharedArray(CHANNEL.LEAD_ESTIMATE.getValue());
    }

    public void incrementLeadEsimate(int increment) throws GameActionException {
        rc.writeSharedArray(CHANNEL.LEAD_ESTIMATE.getValue(),
                rc.readSharedArray(CHANNEL.LEAD_ESTIMATE.getValue()) + increment);
    }

    public void clearArchonMovementLocation() throws GameActionException {
        if (round_num % 2001 == 0){ //currently doesn't clear
            rc.writeSharedArray(CHANNEL.ARCHON_MOVE.getValue(), 0);
        }
    }

    public void clearArchonMoving() throws GameActionException {
        if (round_num % 15 == 0){
            rc.writeSharedArray(CHANNEL.ARCHON_MOVING.getValue(), 0);
        }
    }

    public int getMode() throws GameActionException {
        return rc.readSharedArray(CHANNEL.ARCHON_MODE.getValue());
    }

    public void broadcastMode(int num) throws GameActionException {
        rc.writeSharedArray(CHANNEL.ARCHON_MODE.getValue(), num);
    }

    public void sendMovingAlert() throws GameActionException {
        rc.writeSharedArray(CHANNEL.ARCHON_MOVING.getValue(), 1);
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
            if (data == 0 && threatChannel==-1) {
                threatChannel = i;
                
            }
        }
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

    public void clearArchonNumbers() throws GameActionException {
        // if you don't read all 0s for the first four numbers, set them to zero.
        if (rc.readSharedArray(CHANNEL.ARCHON_NUMBER.getValue()) != 0) {
            rc.writeSharedArray(CHANNEL.ARCHON_NUMBER.getValue(), 0);
        }
    }

    public int getArchonNum() throws GameActionException {
        int data = rc.readSharedArray(CHANNEL.ARCHON_NUMBER.getValue());
        int archonNumber = -1;
        if (data == 0) {
            rc.writeSharedArray(CHANNEL.ARCHON_NUMBER.getValue(), 1);
            archonNumber = 0;
        } else if (data == 1) {
            rc.writeSharedArray(CHANNEL.ARCHON_NUMBER.getValue(), 2);
            archonNumber = 1;
        } else if (data == 2) {
            rc.writeSharedArray(CHANNEL.ARCHON_NUMBER.getValue(), 3);
            archonNumber = 2;
        } else
            archonNumber = 3;
        if (archonNumber == rc.getArchonCount() - 1)
            clearArchonNumbers();
        return archonNumber;
    }

    public void postArchonLocation(int archonNumber)throws GameActionException{
        MapLocation my = rc.getLocation();
        int data = my.x*64 + my.y;
        rc.writeSharedArray(CHANNEL.ARCHON_POSITION.getValue() + archonNumber, data);
    }

    public MapLocation readArchonLocation(int archonNumber) throws GameActionException{
        int data = rc.readSharedArray(CHANNEL.ARCHON_POSITION.getValue() + archonNumber);
        if (data == 0){
            System.out.println("this archon position has not been updated yet");
        }
        return new MapLocation(data/64, data%64);
    }

    public MapLocation[] getPreviousArchons(int archonNumber) throws GameActionException {
        MapLocation[] farchons = new MapLocation[archonNumber];
        for (int i = 0; i < archonNumber; i++){
            farchons[i] = readArchonLocation(i);
        }
        return farchons;
    }

    public void broadcastLab(MapLocation loc) throws GameActionException {
        int data = locationToInt(loc);
        rc.writeSharedArray(CHANNEL.LAB_LOC.getValue(), data);
    }

    public MapLocation readLabLoc() throws GameActionException {
        int data = rc.readSharedArray(CHANNEL.LAB_LOC.getValue());
        if(data == 65535) return null;
        return new MapLocation(data/64, data%64);
    }

    public void clearLabLoc() throws GameActionException {
        writeChannel(CHANNEL.LAB_LOC, 65535);
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