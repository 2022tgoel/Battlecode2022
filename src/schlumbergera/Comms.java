package schlumbergera;

import battlecode.common.*;

public class Comms {
    RobotController rc;
    private int round_num;
    public Comms(RobotController robotController) throws GameActionException{
        rc = robotController;
    }

    public void update() {
        round_num = rc.getRoundNum();
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

    public void clearThreat() throws GameActionException{
        if (round_num % 15 == 0){
            for (int i = 0; i < 4; i++) {
                rc.writeSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + i, 0);
            }
        }
    }

    public void clearTargetAreas() throws GameActionException {
        if (round_num % 3 == 0){
            for (int i = 0; i < CHANNEL.NUM_TARGETS; i++) {
                rc.writeSharedArray(CHANNEL.TARGET.getValue() + i, 0);
            }
        }
    }

    public void clearMiningAreas() throws GameActionException {
        if (round_num % 3 == 0){
            for (int i = 0; i < 5; i++) {
                rc.writeSharedArray(CHANNEL.MINING1.getValue() + i, 0);
            }
        }
    }

    public void clearArchonMovementLocation() throws GameActionException {
        if (round_num % 75 == 0){
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
        if (threatChannel == -1) threatChannel = 4; //override
        rc.writeSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + threatChannel, locationToInt(my));
        return threatChannel;
    }

    public int totalUnderThreat() throws GameActionException{
        int numThreatenedArchons = 0;
        for (int i = 0; i < 4; i++) {
            // rc.writeSharedArray(, value);
            int data = rc.readSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + i);
            if (data != 0) numThreatenedArchons++;
        }
        return numThreatenedArchons;
    }

    /* public int getArchonNum(int num_archons_init, int num_archons_alive, int archonNumber) throws GameActionException {
        // none dead
        int archonCount = rc.getArchonCount();
        if (archonCount >= num_archons_alive) {
            return archonNumber;
        }
        else {
            int cur_data = rc.readSharedArray(CHANNEL.ARCHON_ALIVE.getValue());
            int binary_sum = 0;
            int tempData = cur_data;
            for (int i = num_archons_init - 1; i >= 0; i--) {
                if (tempData / (int) Math.pow(2.0, (double) i) == 1) {
                    binary_sum += 1;
                    tempData -= (int) Math.pow(2.0, (double) i);
                }
            }

            // if you're the last one, clear the data
            if (binary_sum == num_archons_alive - 1) {
                rc.writeSharedArray(CHANNEL.ARCHON_ALIVE.getValue(), 0);
            }
            else {
                int data = (int) (Math.pow(2.0, ((double) archonNumber)));
                rc.writeSharedArray(CHANNEL.ARCHON_ALIVE.getValue(), cur_data + data);
            }
            // if 4 archons alive, binary sum is 3, if 3 archons alive, binary sum is 2...
            return binary_sum;
        }
    } */

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
        }
        else if (data == 1) {
            rc.writeSharedArray(CHANNEL.ARCHON_NUMBER.getValue(), 3);
            archonNumber = 1;
        }
        else if (data == 3) {
            rc.writeSharedArray(CHANNEL.ARCHON_NUMBER.getValue(), 7);
            archonNumber = 2;
        }
        else archonNumber = 3;
        if (archonNumber == rc.getArchonCount() - 1) clearArchonNumbers();
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

    public void postRank(RANK rank) throws GameActionException {
        MapLocation loc = rc.getLocation();
        int loc_int;
        if (rank == RANK.DEFAULT) {
            return;
        }
        else {
            // all locations are within 60, so can be compressed to 6 bits.
            loc_int = rank.getValue() * 4096 + locationToInt(loc);
            if (rc.getRoundNum() % 2 == 0) {
                rc.writeSharedArray(CHANNEL.SEND_RANKS1.getValue(), loc_int);
            }
            else {
                rc.writeSharedArray(CHANNEL.SEND_RANKS2.getValue(), loc_int);
            }
        }
    }

    public void clearRanks() throws GameActionException {
        if (rc.getRoundNum() % 2 == 0) {
            rc.writeSharedArray(CHANNEL.SEND_RANKS1.getValue(), 0);
        }
        else {
            rc.writeSharedArray(CHANNEL.SEND_RANKS2.getValue(), 0);
        }
    }

    public int locationToInt(MapLocation loc) {
        return 64 * loc.x + loc.y;
    }
}