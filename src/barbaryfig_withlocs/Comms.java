package barbaryfig_withlocs;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Comms {
    RobotController rc;
    private int roundNum;

    public static boolean isTallyingRound(int roundNumber) {
        return (roundNumber - 1) % 10 == 0;
    }

    public Comms(RobotController robotController) throws GameActionException {
        rc = robotController;
    }

    public void updateRoundNumber() {
        roundNum = rc.getRoundNum();
    }

    /**
     * 
     * @param i 0 to 3
     * @throws GameActionException
     */
    public static int getTopActiveGridSquare(RobotController rc, int i) throws GameActionException {
        int chan = CHANNEL.TOP4_ACTIVE_GRID_SQUARES.getValue();
        int value = (rc.readSharedArray(chan) >> (i * 8)) & 0b0000_1111;
        return value;
    }

    public void setTopFourActiveGridSquares(int a, int b, int c, int d) throws GameActionException {
        int chan = CHANNEL.TOP4_ACTIVE_GRID_SQUARES.getValue();
        int encoded = a << 24 | b << 16 | c << 8 | d;

        rc.writeSharedArray(chan, encoded);
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

    public void clearBuilds() throws GameActionException {
    }

    public void getClearRound(int archonNumber) throws GameActionException {
    }

    public void clearThreat() throws GameActionException {
        if (roundNum % 15 == 0) {
            for (int i = 0; i < 4; i++) {
                rc.writeSharedArray(CHANNEL.fARCHON_STATUS1.getValue() + i, 0);
            }
        }
    }

    public void clearMiningAreas() throws GameActionException {
        if (roundNum % 3 == 0) {
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

    public int getArchonNum(int num_archons_init, int num_archons_alive, int archonNumber) throws GameActionException {
        // none dead
        int archonCount = rc.getArchonCount();
        if (archonCount >= num_archons_alive) {
            return archonNumber;
        } else {
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
            } else {
                int data = (int) (Math.pow(2.0, ((double) archonNumber)));
                rc.writeSharedArray(CHANNEL.ARCHON_ALIVE.getValue(), cur_data + data);
            }
            // if 4 archons alive, binary sum is 3, if 3 archons alive, binary sum is 2...
            return binary_sum;
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

    public static int getGridSquareValue(RobotController rc, int baseChannelID, int gridSquare,
            int gridSquaresPerChannel) throws GameActionException {
        int channelID = baseChannelID + gridSquare / gridSquaresPerChannel;
        int channelValue = rc.readSharedArray(channelID);

        // Isolate the bits that correspond to the grid square
        int bitsPerGridSquare = 16 / gridSquaresPerChannel;
        int bitOffset = (gridSquare % gridSquaresPerChannel) * bitsPerGridSquare;
        int bitMask = (1 << bitsPerGridSquare) - 1;

        return (channelValue >>> bitOffset) & bitMask;
    }

    public static void setGridSquareValue(RobotController rc, int baseChannelID, int gridSquare,
            int gridSquaresPerChannel,
            int value, int currentChannelValue) throws GameActionException {
        int channelID = baseChannelID + gridSquare / gridSquaresPerChannel;

        // Isolate the bits that correspond to the grid square
        int bitsPerGridSquare = 16 / gridSquaresPerChannel;
        int bitOffset = (gridSquare % gridSquaresPerChannel) * bitsPerGridSquare;
        int bitMask = (1 << bitsPerGridSquare) - 1;
        // Write the new value back to the channel.
        // For example, if bitOffset = 2, and gridSquaresPerChannel = 4,
        // then bitMask = 0b1111, and bitMask << bitOffset = 0b11110000.
        // "~" is the bitwise NOT operator, so ~(bitMask << bitOffset) = 0b00001111.
        // "&" clears the bits that correspond to the grid square.
        // "|" sets the bits that correspond to the grid square.
        currentChannelValue = (currentChannelValue & ~(bitMask << bitOffset)) | ((value & bitMask) << bitOffset);
        rc.writeSharedArray(channelID, currentChannelValue);
    }

    public static MapLocation getGridSquareCenter(int gridSquare, int width, int height) {
        int gridX = gridSquare % CHANNEL.GRID_SIZE;
        int gridY = gridSquare / CHANNEL.GRID_SIZE;
        int gridSquareWidth = width / CHANNEL.GRID_SIZE;
        int gridSquareHeight = height / CHANNEL.GRID_SIZE;
        int x = (gridX * CHANNEL.GRID_SIZE) * gridSquareWidth + gridSquareWidth / 2;
        int y = (gridY * CHANNEL.GRID_SIZE) * gridSquareHeight + gridSquareHeight / 2;
        return new MapLocation(x, y);
    }

    /**
     * Used to determine which channel to use for a given location.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    public static int locationToGridSquare(int x, int y, int width, int height) {
        int gridX = (x * CHANNEL.GRID_SIZE) / width;
        int gridY = (y * CHANNEL.GRID_SIZE) / height;

        return gridY * CHANNEL.GRID_SIZE + gridX;
    }

    public void clearGridSquares() throws GameActionException {
        int gridSquareCount = CHANNEL.GRID_SIZE * CHANNEL.GRID_SIZE;
        int gridSquareChannelCount = gridSquareCount / CHANNEL.GRID_SQUARES_PER_CHANNEL;
        for (int offset = gridSquareChannelCount; --offset >= 0;) {
            rc.writeSharedArray(CHANNEL.GRID_BASE.getValue() + offset, 0);
        }
    }
}