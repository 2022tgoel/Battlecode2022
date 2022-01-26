package opuntia_sage;

import battlecode.common.*;
import java.util.*;

// shared code across the units
public class Unit {
    Comms radio;
    RobotController rc;
    int archon_index = -1;
    RANK[] rank_map = initializeRankMap();
    final Random rng = new Random();
    static final int goldToLeadConversionRate = 200;
    static final int minerToLeadRate = 250;
    int seed_increment = 4;
    MapLocation homeArchon;
    public MapLocation archon_target;
    public int mapArea;
    public MapLocation attackTarget;
    /** Array containing all the possible movement directions. */
    static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
    };

    static Navigation mover;

    public Unit(RobotController robotController) throws GameActionException {
        rc = robotController;
        rng.setSeed((long) rc.getID() + seed_increment);
        homeArchon = findHomeArchon();
        initializeRankMap();
        mapArea = getMapArea();
        radio = new Comms(rc);
        mover = new Navigation(rc);
    }

    /**
     * run() is a placeholder implemented in the specific files
     **/
    public void run() throws GameActionException {
        radio.init();
    }

    // when you sense or detect, you get an archon_index
    /**
     * detectArchon() looks through the archon positions for a new one, then stores
     * it in archon_index
     * 
     * @return true if it found an archon
     **/
    public boolean detectArchon() throws GameActionException {
        if (archon_index != -1) {
            int data = rc.readSharedArray(CHANNEL.ARCHON_LOC_1.getValue() + archon_index);
            if (data != 0) {
                rc.setIndicatorString("archon found UWU1 " + archon_index);
                assert (archon_index != -1);
                int x = data / 64;
                int y = data % 64;
                archon_target = new MapLocation(x, y);
                return true;
            }
        }
        for (int i = 0; i < 4; i++) {
            int data = rc.readSharedArray(CHANNEL.ARCHON_LOC_1.getValue() + i);
            if (data != 0) {
                rc.setIndicatorString("archon found UWU2 " + archon_index);
                archon_index = i;
                assert (archon_index != -1);
                int x = data / 64;
                int y = data % 64;
                archon_target = new MapLocation(x, y);
                return true;
            }
        }
        return false;
    }

    /**
     * checks if any of the nearby robots are enemy archons, if so broadcasts it
     * 
     * @return true if archon was found, false otherwise
     **/
    public boolean senseArchon() throws GameActionException {
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        // if there are any nearby enemy robots, attack the one with the least health
        boolean found = false;
        if (nearbyBots.length > 0) {
            for (RobotInfo bot : nearbyBots) {
                if (bot.type == RobotType.ARCHON) {
                    int ind = broadcastArchon(bot.location);
                    archon_target = bot.location;
                    // store index of last archon sensed
                    archon_index = ind;
                    found = true;
                }
            }
        }
        return found;
    }

    public int broadcastArchon(MapLocation loc) throws GameActionException {
        // check that the loc is not already broadcasted
        int indToPut = 0; // where to put the archon (if all spots are filled, it will be put at 0)
        for (int i = 0; i < 4; i++) {
            int data = rc.readSharedArray(CHANNEL.ARCHON_LOC_1.getValue() + i);
            int x = data / 64;
            int y = data % 64;
            if (loc.x == x && loc.y == y) {
                return i; // already broadcasted, return index where it is stored
            }
            if (data == 0) {
                indToPut = i;
            }
        }
        int loc_int = locationToInt(loc);
        rc.writeSharedArray(CHANNEL.ARCHON_LOC_1.getValue() + indToPut, loc_int);
        // rc.setIndicatorString("broadcasting succesful, archon_index " +
        // available_index);
        return indToPut;
    }

    /**
     * approachArchon() moves towards the archon specified by archon_index
     * 
     * @return false is the archon is not longer there, true otherwise
     **/
    public boolean approachArchon() throws GameActionException {
        int data = rc.readSharedArray(archon_index);
        if (data != 0) {
            int x = data / 64;
            int y = data % 64;
            MapLocation target = new MapLocation(x, y);
            if (rc.canSenseLocation(target)) {
                RobotInfo r = rc.senseRobotAtLocation(target);
                if (r == null || r.type != RobotType.ARCHON) {
                    rc.writeSharedArray(archon_index, 0);
                    archon_index = -1;
                    return false;
                }

            }
            moveToLocation(target);
            return true;
        } else {
            archon_index = -1;
            return false; // no longer there
        }
    }

    public boolean transformInto(RobotMode mode) throws GameActionException {
        if (rc.getMode() == mode) {
            return true;
        }

        if (rc.canTransform()) {
            rc.transform();
            return true;
        }

        return false;
    }

    public boolean turret() throws GameActionException {
        return transformInto(RobotMode.TURRET);
    }

    public boolean attack() throws GameActionException {
        return transformInto(RobotMode.PORTABLE);
    }

    public Direction getLeastRubbleBuildableDirection(RobotType robotType) throws GameActionException {
        int bestDirectionIdx = -1;
        int bestDirectionRubble = 100000;
        MapLocation me = rc.getLocation();
        for (int i = 0; i < directions.length; i++) {
            int rubbleHere = rc.senseRubble(me.add(directions[i]));
            if (rubbleHere < bestDirectionRubble && rc.canBuildRobot(robotType, directions[i])) {
                bestDirectionRubble = rubbleHere;
                bestDirectionIdx = i;
            }
        }

        if (bestDirectionIdx == -1) {
            return null;
        } else {
            return directions[bestDirectionIdx];
        }
    }
    public int numFriendlyMiners() {
        return numFriendlyMiners(-1);
    }

    public int numFriendlyMiners(int rsqr) {
        RobotInfo[] allies = rc.senseNearbyRobots(rsqr, rc.getTeam());
        int c = 0;
        for (RobotInfo r : allies) {
            if (r.type == RobotType.MINER)
                c++;
        }
        return c;
    }

    public int senseMiningArea() throws GameActionException {
        int value = 0;
        int cx = 0;
        int cy = 0;
        for (MapLocation loc : rc.senseNearbyLocationsWithGold()) {
            int margin = rc.senseGold(loc) * goldToLeadConversionRate;
            value += margin;
            cx += margin * loc.x;
            cy += margin * loc.y;
        }
        for (MapLocation loc : rc.senseNearbyLocationsWithLead()) {
            int margin = rc.senseLead(loc) - 1;
            value += margin;
            cx += margin * loc.x;
            cy += margin * loc.y;
        }

        if (value >= 25) {
            MapLocation dest = new MapLocation(cx / value, cy / value);
            // demand disabled for now
            int demand = value / minerToLeadRate - numFriendlyMiners();
            if (demand > 0) {
                broadcastMiningArea(dest, demand);
            }
        }
        return value;
    }

    public void broadcastMiningArea(MapLocation loc, int demand) throws GameActionException {
        int indToPut = 0; // where to put the archon (if all spots are filled, it will be put at 0)
        // fuzzy location
        int x_loc = Math.min((int) Math.round((double) loc.x / 4.0), 15);
        int y_loc = Math.min((int) Math.round((double) loc.y / 4.0), 15);
        for (int i = 0; i < 5; i++) {
            int data = rc.readSharedArray(CHANNEL.MINING1.getValue() + i);
            int x = (data >> 4) & 15;
            int y = data & 15;
            if (x_loc == x && y_loc == y) {
                return;
            }
            if (data == 0) {
                indToPut = i;
            }
        }
        int value = (demand << 8) + (x_loc << 4) + y_loc;
        rc.setIndicatorDot(new MapLocation(x_loc * 4, y_loc * 4), 255, 0, 0);
        // System.out.println("Broadcasting miner request " + x_loc*4 + " " + y_loc*4 +
        // " " + demand + " " + rc.getRoundNum());
        rc.writeSharedArray(CHANNEL.MINING1.getValue() + indToPut, value);
    }

    public void senseFriendlySoldiersArea() throws GameActionException {
        //archons will move to support friendly soldiers
        RobotInfo[] friends = rc.senseNearbyRobots(-1, rc.getTeam());
        int numFriendlySoldiers = 0;
        for (RobotInfo r : friends){
            if (r.type == RobotType.SOLDIER) numFriendlySoldiers++;
        }
        if (numFriendlySoldiers >= 6){
            numFriendlySoldiers /= 6; //capping the amount at ~12, which would fit in 4 bits
            int data = rc.readSharedArray(CHANNEL.ARCHON_MOVE.getValue());
            int curFriends = (data >> 12) & 15; // how many friends are in this hub
            if (numFriendlySoldiers > curFriends){ //this is better
                MapLocation my = rc.getLocation();
                int value = (numFriendlySoldiers << 12) + (my.x << 6) + my.y;
                rc.writeSharedArray(CHANNEL.ARCHON_MOVE.getValue(), value);
            }
        }
    }

    /**
     * validCoords() check if the x and y are on the map
     * 
     * @return bool, true if on the map
     **/
    public boolean validCoords(int x_coord, int y_coord) {
        return x_coord >= 0 && x_coord < rc.getMapWidth() && y_coord >= 0 && y_coord < rc.getMapHeight();
    }

    // pathfinding strategies
    /**
     * moveToLocation() is the moving function that will actually be used by the
     * bots (made a separate function for
     * easy replacement)
     * 
     * @param loc is where to go
     **/
    public void moveToLocation(MapLocation loc) throws GameActionException {
        Direction d = mover.getBestDir(loc);
        if (d != null && rc.canMove(d)) {
            rc.move(d);
        }
    }

    public void moveInDirection(int[] toDest) throws GameActionException {
        MapLocation loc = rc.getLocation();
        MapLocation dest = new MapLocation(loc.x + toDest[0], loc.y + toDest[1]);
        if (!validCoords(dest.x, dest.y)){
            dest = scaleToEdge(toDest);
        }
        assert(validCoords(dest.x, dest.y));
     //   System.out.println(dest);
        Direction d = mover.getBestDir(dest);
        // System.out.println(d);
        if (d != null) {
            if (rc.canMove(d)) rc.move(d);
        }
        // fuzzyMove(dest);
        // rc.setIndicatorString("I JUST MOVED TO " + toDest[0] + " " + toDest[1]);
    }

    public MapLocation scaleToEdge(int[] toDest) {
        MapLocation loc = rc.getLocation();
        // scaling this directional vector so that it reaches the edge
        double scaleFactor = 1000;
        if (toDest[0] > 0) {
            scaleFactor = Math.min(scaleFactor, ((double) (rc.getMapWidth() - 1 - loc.x)) / ((double) toDest[0]));
        } else if (toDest[0] < 0) {
            scaleFactor = Math.min(scaleFactor, ((double) (loc.x)) / ((double) -1 * toDest[0]));
        }
        if (toDest[1] > 0) {
            scaleFactor = Math.min(scaleFactor, ((double) (rc.getMapHeight() - 1 - loc.y)) / ((double) toDest[1]));
        } else if (toDest[1] < 0) {
            scaleFactor = Math.min(scaleFactor, ((double) (loc.y)) / ((double) -1 * toDest[1]));
        }
        assert (scaleFactor >= 0);
        int nx = loc.x + (int) (scaleFactor * (double) toDest[0]);
        nx = Math.min(nx, rc.getMapWidth() - 1);
        nx = Math.max(nx, 0);
        int ny = loc.y + (int) (scaleFactor * (double) toDest[1]);
        ny = Math.min(ny, rc.getMapHeight() - 1);
        ny = Math.max(ny, 0);
        return new MapLocation(nx, ny);
    }

    public int[] scaleToSize(int[] toDest) {
        return scaleToSize(toDest, 12.0);
    }

    public int[] scaleToSize(int[] toDest, double desiredLength) {
        double len = Math.sqrt(Math.pow(toDest[0], 2) + Math.pow(toDest[1], 2));
        double scaleFactor = desiredLength / len;
        int[] newDir = new int[] { (int) (toDest[0] * scaleFactor), (int) (toDest[1] * scaleFactor) };
        return newDir;
    }

    public int cooldown(MapLocation loc) throws GameActionException {
        // returns cooldown of movement
        return (int) Math.floor((1 + rc.senseRubble(loc) / 10.0) * rc.getType().movementCooldown);
    }

    public double cooldownMultiplier(MapLocation loc) throws GameActionException{
        return (1.0+(double)rc.senseRubble(loc)/10.0);
    }

    public int[] getExploratoryDir() {
        return getExploratoryDir(5, new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2));
    }

    public int[] getExploratoryDir(int span) {
        return getExploratoryDir(span, new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2));
    }

    public int[] getExploratoryDir(int span, MapLocation loc) {
        // presumes span is odd.
        int[] dir;
        MapLocation cur = rc.getLocation();
        if (loc.x - cur.x > 0) {
            if (loc.y - cur.y > 0) {
                dir = new int[] { 8, 8 };
            } else {
                dir = new int[] { 8, -8 };
            }
        } else {
            if (loc.y - cur.y > 0) {
                dir = new int[] { -8, 8 };
            } else {
                dir = new int[] { -8, -8 };
            }
        }
        int[][] dirs = new int[span][2];
        int counter = 0;

        int increment;
        int init_val;
        if (dir[0] < 0) {
            increment = -4;
            init_val = -8 + ((((span + 1) / 2) - 1) * -increment);
        } else {
            increment = 4;
            init_val = 8 + ((((span + 1) / 2) - 1) * -increment);
        }

        for (int i = init_val; i != dir[0]; i += increment) {
            dirs[counter] = new int[] { i, dir[1] };
            counter += 1;
        }

        if (dir[1] < 0) {
            increment = -4;
            init_val = -8 + ((((span + 1) / 2) - 1) * -increment);
            ;
        } else {
            increment = 4;
            init_val = 8 + ((((span + 1) / 2) - 1) * -increment);
            ;
        }

        for (int i = init_val; i != dir[1]; i += increment) {
            dirs[counter] = new int[] { dir[0], i };
            counter += 1;
        }

        dirs[dirs.length - 1] = dir;
        // print directions
        // rc.setIndicatorString("dirs: " + dirs[0][0] + " " + dirs[0][1] + " " +
        // dirs[1][0] + " " + dirs[1][1] + " " + dirs[2][0] + " " + dirs[2][1] + " " +
        // dirs[3][0] + " " + dirs[3][1] + " " + dirs[4][0] + " " + dirs[4][1] + " | " +
        // (center.x - cur.x) + " " + (center.y - cur.y) + " | " + center.x + " " +
        // center.y + " | " + cur.x + " " + cur.y);

        return dirs[rng.nextInt(dirs.length)];
    }

    public boolean adjacentToEdge() throws GameActionException {
        MapLocation cur = rc.getLocation();
        int mapheight = rc.getMapHeight();
        int mapwidth = rc.getMapWidth();
        if (mapheight - cur.y < 3 || cur.y < 3 || cur.x < 3 || mapwidth - cur.x < 3) {
            return true;
        }
        return false;
    }

    public int[] flip(int[] dir) {// directional vector input
        assert (dir.length == 2);
        MapLocation loc = rc.getLocation();
        int[] ret = new int[2];
        ret[0] = dir[0];
        ret[1] = dir[1];
        if (loc.x < 5 && ret[0] < 0) {
            ret[0] = -1 * ret[0];
        }
        if (loc.y < 5 && ret[1] < 0) {
            ret[1] = -1 * ret[1];
        }
        if (rc.getMapWidth() - loc.x < 5 && ret[0] > 0) {
            ret[0] = -1 * ret[0];
        }
        if (rc.getMapHeight() - loc.y < 5 && ret[1] > 0) {
            ret[1] = -1 * ret[1];
        }
        return ret;
    }

    public MapLocation findHomeArchon() throws GameActionException {
        if (rc.getType() == RobotType.ARCHON) {
            return rc.getLocation();
        }
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(1, rc.getTeam());
        // if there are any nearby enemy robots, attack the one with the least health
        if (nearbyBots.length > 0) {
            for (RobotInfo bot : nearbyBots) {
                if (bot.type == RobotType.ARCHON) {
                    return bot.location;
                }
            }
        }
        return rc.getLocation();
    }

    public int locationToInt(MapLocation loc) {
        return 64 * loc.x + loc.y;
    }

    public RANK[] initializeRankMap() {
        RANK[] map = new RANK[64];
        RANK[] ranks = RANK.values();
        for (int i = 0; i < ranks.length; i++) {
            map[ranks[i].getValue()] = ranks[i];
        }
        return map;
    }

    public RANK getRank(int index) {
        return rank_map[index];
    }

    public RANK findRank() throws GameActionException {
        rc.setIndicatorString("READY TO READ");
        int data;
        // check all channels to see if you've received a rank
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                data = rc.readSharedArray(CHANNEL.SEND_RANKS1.getValue());
            } else {
                data = rc.readSharedArray(CHANNEL.SEND_RANKS2.getValue());
            }

            int status = data / 4096;
            int x = (data - (4096 * status)) / 64;
            int y = (data - (4096 * status) - (x * 64));

            // only set rank if instructed to.
            if (homeArchon.equals(new MapLocation(x, y))) {
                return getRank(status);
            }
        }
        return RANK.DEFAULT;
    }

    public int getMapArea() {
        return rc.getMapHeight() * rc.getMapWidth();
    }

    public void broadcastTarget(MapLocation enemy, boolean isMiner) throws GameActionException {
        int indToPut = 0; // where to put the archon (if all spots are filled, it will be put at 0)
        // fuzzy location
        int x_loc = enemy.x;
        int y_loc = enemy.y;
        RobotInfo r = rc.senseRobotAtLocation(enemy);
        //boolean isMiner = (r.type == RobotType.MINER);
        for (int i = 0; i < CHANNEL.NUM_TARGETS; i++) {
            int data = rc.readSharedArray(CHANNEL.TARGET.getValue() + i);
            // int w = (data >> 12) ;
            int x = (data >> 6) & 63;
            int y = data & 63;
            if (x_loc == x && y_loc == y) {
                return;
            }
            MapLocation loc = new MapLocation(x, y);
            // don't store closeby targets
            if (loc.distanceSquaredTo(enemy) < 6) return;
            if (data == 0) {
                indToPut = i;
            }
        }
        int value = ((isMiner ? 1 : 0)<< 12) + (x_loc << 6) + y_loc;
        rc.setIndicatorDot(new MapLocation(x_loc, y_loc), 0, 100, 0);
        rc.writeSharedArray(CHANNEL.TARGET.getValue() + indToPut, value);
        // System.out.println("I broadcasted an enemy at " + enemy.toString());
    }

    public ATTACK determineSageAttack() throws GameActionException {
        if (rc.getActionCooldownTurns() > 0) return ATTACK.NONE;
        if (rc.senseNearbyRobots(-1, rc.getTeam().opponent()) == null) return ATTACK.NONE;
        
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(RobotType.SAGE.actionRadiusSquared);

        int numfSoldiers = 0;
        int numfSages = 0;
        int numeSoldiers = 0;
        int numeSages = 0;

        int enemyHealth = 0;
        int numFriends = 0;
        int friendHealth = rc.getHealth();

        boolean canOneShot = false;

        int highestSub45Health = -1;
        RobotInfo highestSub45 = null;
        int maxHealth = -1;
        RobotInfo maxHealthBot = null;

        int[] soldierHealths  = new int[nearbyBots.length];
        int[] sageHealths  = new int[nearbyBots.length];
        for (RobotInfo bot: nearbyBots) {
            if (bot.team == rc.getTeam()) {
                if (bot.type == RobotType.SOLDIER) {
                    friendHealth += bot.health;
                    numFriends++;
                }
            }
            else if (bot.team == rc.getTeam().opponent()) {
                if (bot.type == RobotType.SOLDIER) {
                    enemyHealth += bot.health;
                    soldierHealths[numeSoldiers] = bot.health;
                    numeSoldiers++;

                    if (bot.health <= 45) {
                        if (highestSub45Health < bot.health) {
                            highestSub45Health = bot.health;
                            highestSub45 = bot;
                        }
                        canOneShot = true;
                    }
                    else {
                        if (maxHealth < bot.health) {
                            maxHealth = bot.health;
                            maxHealthBot = bot;
                        }
                    }
                }
                else if (bot.type == RobotType.SAGE) {
                    enemyHealth += bot.health;
                    sageHealths[numeSages] = bot.health;
                    numeSages++;

                    if (bot.health <= 45) {
                        if (highestSub45Health < bot.health) {
                            highestSub45Health = bot.health;
                            highestSub45 = bot;
                        }
                        canOneShot = true;
                    }
                    else {
                        if (maxHealth < bot.health) {
                            maxHealth = bot.health;
                            maxHealthBot = bot;
                        }
                    }
                }
            }
        }

        if (numeSoldiers == 0 && numeSages == 0) return ATTACK.NONE;

        // remove zero values from enemyHealth array
        soldierHealths = cleanup(soldierHealths, numeSoldiers);
        sageHealths = cleanup(sageHealths, numeSages);

        ATTACK bestAttack = ATTACK.NONE;
        int maxAdvantage = -100000;
        int advantage;
        int unit_difference = numFriends + 1 - numeSages - numeSoldiers;
        double a = 3.0; // = 6 * ratio;
        // double ratio;
        int unit_advantage;


        ATTACK[] attacks = {ATTACK.DEFAULT, ATTACK.CHARGE};
        for (ATTACK attack: attacks) {
            switch (attack) {
                case DEFAULT:
                    if (canOneShot) {
                        unit_advantage = (int) (a * Math.pow(unit_difference + 1, 2) * Math.signum(unit_difference + 1));
                        advantage = friendHealth - enemyHealth + unit_advantage + highestSub45Health;
                    }
                    else {
                        unit_advantage = (int) (a * Math.pow(unit_difference, 2) * Math.signum(unit_difference));
                        advantage = friendHealth - enemyHealth + unit_advantage + 45;
                    }
                    /* System.out.println("ATTACK: " + attack + " friendHealth: " + friendHealth + " enemyHealth: " + enemyHealth + " unit_difference: " + unit_difference + " unit_advantage: " + unit_advantage + " advantage: " + advantage); */
                    break;
                case CHARGE:
                    int numSoldiersKilled = 0;
                    int numSagesKilled = 0;
                    int health_reduced = 0;
                    if (soldierHealths != null) {
                        for (int i = 0; i < soldierHealths.length; i++) {
                            if (soldierHealths[i] <= 11) {
                                numSoldiersKilled++;
                                health_reduced += soldierHealths[i];
                            }
                            else health_reduced += 11;
                        }
                    }
                    if (sageHealths != null) {
                        for (int i = 0; i < sageHealths.length; i++) {
                            if (sageHealths[i] <= 22) {
                                numSagesKilled++;
                                health_reduced += sageHealths[i];
                            }
                            else health_reduced += 22;
                        }
                    }
                    unit_difference = unit_difference + numSoldiersKilled + numSagesKilled;
                    unit_advantage = (int) (a * Math.pow(unit_difference, 2) * Math.signum(unit_difference));
                    advantage = friendHealth - enemyHealth + unit_advantage + health_reduced;
                    /* System.out.println("ATTACK: " + attack + " friendHealth: " + friendHealth + " enemyHealth: " + enemyHealth + " unit_difference: " + unit_difference + " unit_advantage: " + unit_advantage + " health_reduced " + health_reduced + " num_soldiers killed " + numSoldiersKilled + " num_sages killed " + numSagesKilled +  " advantage: " + advantage); */
                    break;
                default:
                    advantage = 0;
            }
            if (advantage > maxAdvantage) {
                bestAttack = attack;
                maxAdvantage = advantage;
            }
        }

        if (bestAttack == ATTACK.DEFAULT) {
            if (highestSub45 != null) {
                attackTarget = highestSub45.location;
            }
            else {
                attackTarget = maxHealthBot.location;
            }
        }
        // System.out.println("");
        return bestAttack;
    }

    public int[] cleanup(int[] a, int length) {
        int[] b = new int[length];
        int j = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != 0) {
                b[j] = a[i];
                j++;
            }
        }
        Arrays.sort(b);
        return b;
    }

    public void broadcastSageTarget(MapLocation loc) throws GameActionException {
        int indToPut = 0; // where to put the archon (if all spots are filled, it will be put at 0)
        // fuzzy location
        int x_loc = loc.x;
        int y_loc = loc.y;
        for (int i = 0; i < 5; i++) {
            int data = rc.readSharedArray(CHANNEL.SAGE_TARGET.getValue() + i);
            // int w = (data >> 12) ;
            int x = (data >> 6) & 63;
            int y = data & 63;
            if (x_loc == x && y_loc == y) {
                return;
            }
            MapLocation new_loc = new MapLocation(x, y);
            // don't store closeby targets
            if (new_loc.distanceSquaredTo(loc) < 6) return;
            if (data == 0) {
                indToPut = i;
            }
        }
        int value = x_loc * 64 + y_loc;
        // rc.setIndicatorDot(new MapLocation(x_loc, y_loc), 100, 100, 100);
        System.out.println("I BROADCASTED A SAGE TARGET");
        rc.writeSharedArray(CHANNEL.SAGE_TARGET.getValue() + indToPut, value);
        // System.out.println("I broadcasted an enemy at " + enemy.toString());
    }

    public MapLocation getAvgEnemyPos() throws GameActionException {
        int x = 0;
        int y = 0;
        int numEnemies = 0;
        RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        for (RobotInfo enemy: enemies) {
            x += enemy.location.x;
            y += enemy.location.y;
            numEnemies++;
        }
        if (numEnemies == 0) return null;
        return new MapLocation(x / numEnemies, y / numEnemies);
    }
}
