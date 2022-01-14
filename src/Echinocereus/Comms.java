package echinocereus;

import battlecode.common.*;

public class Comms {
    RobotController rc;
    public Comms(RobotController robotController) throws GameActionException{
        rc = robotController;
    }

    public int getMiners() throws GameActionException{
        return rc.readSharedArray(CHANNEL.MINERS_ALIVE.getValue());
    }

    public void setMiners() throws GameActionException{
        rc.writeSharedArray(CHANNEL.MINERS_ALIVE.getValue(), 1);
    }

    public int getSoldiers() throws GameActionException{
        return rc.readSharedArray(CHANNEL.SOLDIERS_ALIVE.getValue());
    }

    public void setSoldiers() throws GameActionException{
        rc.writeSharedArray(CHANNEL.SOLDIERS_ALIVE.getValue(), 1);
    }

    public int getBuilders() throws GameActionException{
        return rc.readSharedArray(CHANNEL.BUILDERS_ALIVE.getValue());
    }

    public void setBuilders() throws GameActionException{
        rc.writeSharedArray(CHANNEL.BUILDERS_ALIVE.getValue(), 1);
    }

    public void clearCounts(int archonNumber) throws GameActionException {
        // clear only if it's your turn
        if (rc.getRoundNum() % rc.getArchonCount() == archonNumber){
            rc.writeSharedArray(CHANNEL.MINERS_ALIVE.getValue(), 0);
            rc.writeSharedArray(CHANNEL.SOLDIERS_ALIVE.getValue(), 0);
            rc.writeSharedArray(CHANNEL.BUILDERS_ALIVE.getValue(), 0);
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
        System.out.println("Roundnum: " + round_num + " data: " + data);
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

    public void incrementTotals() throws GameActionException {
        // don't read increment you just posted
        if (round_num % num_archons_alive == ((archonNumber + 1) % num_archons_alive)) {
            return;
        }
        BOT b = getPreviousBuild();
        switch (b) {
            case MINER:
                total_miner_count += 1;
            case SOLDIER:
                total_soldier_count += 1;
                break;
            case BUILDER:
                total_builder_count += 1;
                break;
            case SAGE:
                total_sage_count += 1;
                break;
            case NONE:
                break;
            default:
                break;
        }
    }

    public void clearTotals() throws GameActionException {
        // don't read increment you just posted
        if (round_num % num_archons_alive == ((archonNumber + 1) % num_archons_alive)) {
            return;
        }
        BOT b = getPreviousBuild();
        switch (b) {
            case MINER:
                total_miner_count += 1;
            case SOLDIER:
                total_soldier_count += 1;
                break;
            case BUILDER:
                total_builder_count += 1;
                break;
            case SAGE:
                total_sage_count += 1;
                break;
            case NONE:
                break;
            default:
                break;
        }
    }

    public void getClearRound() throws GameActionException {
        if (round_num % num_archons_alive == archonNumber) {
            clear_round = round_num + 1;
        }
    }

    public void clearThreat() throws GameActionException{
        if (round_num % 15 == 0){
            for (int i = 0; i < 4; i++) {
                rc.writeSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + i, 0);
            }
        }
        
    }

    public void sendThreatAlert() throws GameActionException {
        MapLocation my = rc.getLocation();
        threatChannel = -1;
        for (int i = 0; i < 4; i++) {
            // rc.writeSharedArray(, value);
            int data = rc.readSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + i);
            // go through channels until you find an empty one to communicate with.
            int x = data / 64;
            int y = data % 64;
            // already alerted.
            if (x == my.x && y == my.y) {
                threatChannel = i;
                return;
            }
            if (data == 0 && threatChannel==-1) {
                threatChannel = i;
                
            }
        }
        rc.writeSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + threatChannel, locationToInt(my));
    }

    public int totalUnderThreat() throws GameActionException{
        int numThreatenedArchons = 0;
        for (int i = 0; i < 4; i++) {
            // rc.writeSharedArray(, value);
            int data = rc.readSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + i);
            if (data != 0) {
                int x = data / 64;
                int y = data % 64;
                if (validCoords(x, y)) {
                    numThreatenedArchons++;
                }
            }
        }
        return numThreatenedArchons;
    }

    public void checkDead() throws GameActionException {
        // none dead
        int archonCount = rc.getArchonCount();
        if (archonCount >= num_archons_alive) {
            return;
        }
        else {
            num_archons_alive = rc.getArchonCount();
            archons_dead += 1;
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
            archonNumber = binary_sum;
        }
    }

    public void clearArchonNumbers() throws GameActionException {
        // if you don't read all 0s for the first four numbers, set them to zero.
        for (int i = 0; i < 4; i++) {
            if ((rc.readSharedArray(CHANNEL.ARCHON_LOC_1.getValue() + i)) != 0) {
                rc.writeSharedArray(i, 0);
            }
        }
    }

    public int getArchonNumber() throws GameActionException {
        int data;
        for (int i = 0; i < 4; i++) {
            data = rc.readSharedArray(CHANNEL.ARCHON_LOC_1.getValue() + i);
            if (data == 0){
                rc.writeSharedArray(i, 1);
                if (i == rc.getArchonCount() - 1) {
                    clearArchonNumbers();
                }
                return i;
            }
        }
        return -1;
    }

    public void postOrder(RANK order) throws GameActionException {
        rc.setIndicatorString("ORDERING A " + order.toString() + " ON CHANNEL " + CHANNEL.ORDERS.getValue());
        rc.writeSharedArray(CHANNEL.ORDERS.getValue(), order.getValue());
    }

    public RANK getOrder() throws GameActionException {
        // rc.setIndicatorString("READING ORDERS FROM CHANNEL " + CHANNEL.ORDERS.getValue());
        int data = rc.readSharedArray(CHANNEL.ORDERS.getValue());
        // rc.setIndicatorString("DATA RECIEVED ");
        return RANK.DEFAULT;
    }

    public void clearOrder() throws GameActionException {
        rc.writeSharedArray(CHANNEL.ORDERS.getValue(), 0);
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
            if (round_num % 2 == 0) {
                rc.writeSharedArray(CHANNEL.SEND_RANKS1.getValue(), loc_int);
            }
            else {
                rc.writeSharedArray(CHANNEL.SEND_RANKS2.getValue(), loc_int);
            }
        }
    }

    public void clearRanks() throws GameActionException {
        if (round_num % 2 == 0) {
            rc.writeSharedArray(CHANNEL.SEND_RANKS1.getValue(), 0);
        }
        else {
            rc.writeSharedArray(CHANNEL.SEND_RANKS2.getValue(), 0);
        }
    }
}