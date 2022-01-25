package opuntia_sage;

import battlecode.common.*;
import java.util.*;

public class Archon extends Unit {

    enum MODE {
        INITIAL,
        DEFAULT,
        SOLDIER_HUB,
        THREATENED,
        OTHER_THREATENED,
        MOVING,
        MAKE_LAB;
    }

    int round_num;
    int archonNumber = -1;

    Direction[] dirs;

    int built_units = 0;
    int counter = 0;

    int num_soldiers = 0;
    int num_miners = 0;
    int num_builders = 0;
    int num_sages = 0;
    int num_archons_init;
    int num_archons_alive;

    int num_soldiers_hub = 0;

    int[] defaultBuildOrder;
    int threatChannel = -1;

    private int[] troopCounter = { 0, 0, 0, 0, 0 }; // miner, soldier, builder, sage, watchtower
    private int desiredNumMiners = 1000;
    private boolean initial = true;

    public Archon(RobotController rc) throws GameActionException {
        super(rc);
        num_archons_alive = rc.getArchonCount();
        num_archons_init = num_archons_alive;
        dirs = sortedDirections();
        defaultBuildOrder = chooseBuildOrder();
        archonNumber = radio.getArchonNum();
        radio.postArchonLocation(archonNumber);
        radio.clearLabLoc();
        addLeadEstimate();
    }

    @Override
    public void run() throws GameActionException {
        super.run();
        /*
        if (round_num == 100){
            rc.resign();
        }
        */
        round_num = rc.getRoundNum();
        radio.update();
        radio.clearThreat();
        radio.clearMiningAreas();
        radio.clearTargetAreas();
        updateAmountMined();
        radio.clearArchonMovementLocation();
        radio.clearArchonMoving();

        archonNumber = radio.getArchonNum();
        radio.postArchonLocation(archonNumber);

        troopCounter = new int[] {
                radio.readCounter(RobotType.MINER),
                radio.readCounter(RobotType.SOLDIER),
                radio.readCounter(RobotType.BUILDER),
                0,
                radio.readCounter(RobotType.WATCHTOWER)
        };

        if (round_num == 2) {
            int leadEstimate = radio.getLeadEstimate();
            desiredNumMiners = determineMinerNum(leadEstimate);
        }

        //System.out.println("Archon number: " + archonNumber + " Mode num: " + radio.getMode() + " " + " round: " + round_num);
        MODE mode = determineMode();
        double useful_miners = (double) radio.readCounter(BiCHANNEL.USEFUL_MINERS);

        if (mode != MODE.MOVING){
            if (rc.getMode()== RobotMode.PORTABLE && rc.canTransform()) rc.transform();
        }

        switch (mode) {
            case THREATENED:
                threatChannel = radio.sendThreatAlert();
                int tot = radio.totalUnderThreat();

                if (tot != 0 && round_num % tot != threatChannel) { // alternate between those under threat
                    break;
                }
                int leadReq = radio.readLeadRequest();
                if(leadReq > Math.max(rc.getTeamLeadAmount(rc.getTeam())-RobotType.SOLDIER.buildCostLead, 0)) {
                    System.out.println("holding for lr: " + leadReq);
                    break;
                }

                if (rc.getTeamGoldAmount(rc.getTeam()) >= 20) {
                    boolean builtSage = build(RobotType.SAGE);
                    if (builtSage) {
                        return;
                    }
                }
                if (checkForResources(RobotType.SOLDIER.buildCostLead)) {
                    Direction[] enemyDirs = getEnemyDirs();
                    for (Direction dir : enemyDirs) {
                        buildSoldier(dir);
                    }
                } else
                    attemptHeal();
                break;
            case INITIAL:
                if (round_num >= 60) {
                    initial = false;
                    break;
                }
                if (round_num % num_archons_alive != archonNumber) break;
                if (num_miners > (double) troopCounter[0] / (double) num_archons_init) break;
                if (troopCounter[0] < desiredNumMiners) {
                    build(RobotType.MINER);
                }
                else if ((useful_miners / (double) troopCounter[0]) >= 0.80) {
                    build(RobotType.MINER);
                }
                else if ((useful_miners / (double) troopCounter[0]) <= 0.75) {
                    initial = false;
                }
//                System.out.println("Desired miners: " + desiredNumMiners + " Useful miners: " + useful_miners + " Ratio: " + (useful_miners / (double) troopCounter[0]));
                break;
            case MAKE_LAB: //acc making a builder
                // System.out.println(radio.readLabLoc());
                builderBuilt = build(RobotType.BUILDER);
                break;
            case SOLDIER_HUB:
                leadReq = radio.readLeadRequest();
                if(leadReq > Math.max(rc.getTeamLeadAmount(rc.getTeam())-RobotType.SOLDIER.buildCostLead, 0)) {
                    System.out.println("holding for lr: " + leadReq);
                    break;
                }

                if (rc.getTeamGoldAmount(rc.getTeam()) >= 20) {
                    boolean builtSage = build(RobotType.SAGE);
                    if (builtSage) {
                        return;
                    }
                }

                if (checkForResources(RobotType.SOLDIER.buildCostLead)) {
                    boolean soldier_built = build(RobotType.SOLDIER);
                    if (soldier_built) num_soldiers_hub++;
                }
                else {
                    attemptHeal();
                    // rc.setIndicatorString("ATTEMPTING HEALING");
                }
                if (num_soldiers_hub > 20) {
                    radio.broadcastMode((archonNumber + 1) % num_archons_alive);
                    num_soldiers_hub = 0;
                }
                break;
            case MOVING:
                moveToLocation(move);
                radio.sendMovingAlert();
                break;
            case OTHER_THREATENED:
                if (rc.getTeamLeadAmount(rc.getTeam()) < 600) {
                    attemptHeal();
                    break; // save for attacked archons
                }
            case DEFAULT:
                attemptHeal();
                if (round_num % num_archons_alive != archonNumber || round_num % 5 != 0) break;

                leadReq = radio.readLeadRequest();
                if(leadReq > Math.max(rc.getTeamLeadAmount(rc.getTeam())-RobotType.MINER.buildCostLead, 0)) {
                    System.out.println("holding for lr: " + leadReq);
                    break;
                }

                if ((useful_miners / (double) troopCounter[0]) >= 0.25) {
                    build(RobotType.MINER);
                }

                // if ((useful_miners / (double) troopCounter[0]) >= 0.60) build(new int[] {1, 0, 0});
                break;
        }
        num_archons_alive = rc.getArchonCount();
        rc.setIndicatorString("mode: " + mode.toString() + " " + getArchonMovementLocation() + " " + archonNumber);
    }

    public boolean build(RobotType type) throws GameActionException {
        boolean unit_built = false;
        for (Direction dir : dirs) {
            switch (type) {
                case MINER:
                    unit_built = buildMiner(dir);                    
                    break;
                case SOLDIER:
                    unit_built = buildSoldier(dir);
                    break;
                case BUILDER:
                    unit_built = buildBuilder(dir);
                    break;
                case SAGE:
                    unit_built = buildSage(dir);
            }
            if (unit_built)
                return true;
        }
        return false;
    }

    //for lab building
    static boolean builderBuilt = false; 
    static MapLocation labLoc = null;
    boolean shouldBuildLab;
    //
    public MODE determineMode() throws GameActionException {
        //
        if (round_num == 2){
            shouldBuildLab = isEdgeArchon();
        }
        if (shouldBuildLab && !builderBuilt){
          //  System.out.println();
            if (labLoc == null) labLoc = findLabLocation();
            if(labLoc != null) return MODE.MAKE_LAB;
        }
        

        //
        if (underThreat())
            return MODE.THREATENED;
        else if (radio.totalUnderThreat() > 0)
            return MODE.OTHER_THREATENED;
        else if (initial)
            return MODE.INITIAL;

        move = getArchonMovementLocation();
        // burn the surplus.
        if (rc.getTeamLeadAmount(rc.getTeam()) >= 750) return MODE.SOLDIER_HUB;
        if (move == null || num_archons_alive == 1){
            if (radio.getMode() == archonNumber) return MODE.SOLDIER_HUB;
            else return MODE.DEFAULT;
        }
        else {
            if (isClosestArchon(move)) { 

                return robotModeSwitch();
            }
            else {
                if (archonMoving()){ //soldier hub delegation passed to second closest while it is moving
                    if (isSecondClosestArchon(move)) return MODE.SOLDIER_HUB;
                }
            }
        }
        return MODE.DEFAULT;
    }

    public int determineMinerNum(int leadEstimate) throws GameActionException {
        int sizeBracket = (int) Math.ceil((double) mapArea / 1000.0);
        int numMinersMap = sizeBracket * 2;
        if (leadEstimate > 2000) {
            return 12 + numMinersMap;
        }
        else if (leadEstimate > 1000) {
            return 8 + numMinersMap;
        }
        else if (leadEstimate > 500) {
            return 4 + numMinersMap;
        }
        else if (leadEstimate > 100) {
            return 2 + numMinersMap;
        }
        else {
            return 2 + numMinersMap;
        }
    }

    ///
    public boolean isEdgeArchon() throws GameActionException{
        
        MapLocation closest = null;
        for (int i = 0; i < num_archons_alive; i++){
            MapLocation cur = radio.readArchonLocation(i);
            if (closest == null || 
                (closest!=null && distToWall(cur) < distToWall(closest))){
                closest = cur;
            }
        }
        if (closest.equals(rc.getLocation())) return true;
        return false;
        
    }

    public MapLocation findLabLocation() throws GameActionException {
        if(radio.readLabLoc() != null) return null; //wait to broadcast

        MapLocation[] nearbyLocs = rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), rc.getType().visionRadiusSquared);
        MapLocation bestLocation = null;
        int value = 100000;
        for (MapLocation loc : nearbyLocs){
            int v = distToWall(loc)*4 + rc.senseRubble(loc);
            if (v < value){
                bestLocation = loc;
                value = v;
            }
        }

        radio.broadcastLab(bestLocation);
        System.out.println(bestLocation);
        return bestLocation;
    }

    ///

    public MapLocation getArchonMovementLocation() throws GameActionException{
        // read channel to see where soldiers suggest an archon moves
        int data = rc.readSharedArray(CHANNEL.ARCHON_MOVE.getValue());
        if (data!=0) {
            int x = (data >> 6) & 63;
            int y= data & 63;
            return new MapLocation(x, y);
        }
        else return null;
    }

    public boolean isClosestArchon(MapLocation loc) throws GameActionException{ //gets the position you should move to
        if (loc != null){
            // return only if you are the closest archon
            MapLocation closest = null;
            for (int i = 0; i < num_archons_alive; i++){
                MapLocation cur = radio.readArchonLocation(i);
                if (closest == null || 
                    (closest!=null && loc.distanceSquaredTo(cur) < loc.distanceSquaredTo(closest))){
                    closest = cur;
                }
            }
            if (closest.equals(rc.getLocation())) return true;
        }
        return false;
    }

    public boolean isSecondClosestArchon(MapLocation loc) throws GameActionException {
        if (loc != null){
            // return only if you are the closest archon
            MapLocation closest = null;
            MapLocation secondClosest = null;
            for (int i = 0; i < num_archons_alive; i++){
                MapLocation cur = radio.readArchonLocation(i);
                if (closest == null || 
                    (closest!=null && loc.distanceSquaredTo(cur) < loc.distanceSquaredTo(closest))){
                    secondClosest = closest;
                    closest = cur;
                }
                else if (secondClosest==null ||
                        (secondClosest!=null && loc.distanceSquaredTo(cur) < loc.distanceSquaredTo(secondClosest))){
                    secondClosest = cur;
                }
            }
            if (secondClosest.equals(rc.getLocation())) return true;
        }
        return false;
    }

    public boolean archonMoving() throws GameActionException {
        return (rc.readSharedArray(CHANNEL.ARCHON_MOVING.getValue()) > 0);
    }

    public boolean underThreat() throws GameActionException {
        RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        if (enemies.length > 0) {
            broadcastTarget(enemies[0].location);
            return true;
        }
        return false;
    }
    MapLocation move = null;
    int turnsMoving = 0;
    final int maxTurnsMoving = 200;
    int turnsWaiting = 150;
    final int minTurnsWaiting = 150;
    public MODE robotModeSwitch() throws GameActionException{
        if (rc.getMode() == RobotMode.PORTABLE){
            if (rc.getLocation().distanceSquaredTo(move) < 20 || turnsMoving >= maxTurnsMoving){ 
                //TODO: factor in rubble, don't settle on low rubble
                if (rc.getMode() == RobotMode.PORTABLE){
                    if (rc.canTransform()) rc.transform();
                    turnsMoving = 0; // reset
                }
            }
            else {
                turnsMoving++;
            }
        }
        else { //turret mode
            if (rc.getLocation().distanceSquaredTo(move) > 20 && turnsWaiting >= minTurnsWaiting ){
                if (rc.canTransform()) {
                    rc.transform();
                    turnsWaiting = 0;
                }
            }
            else turnsWaiting++;
        }
        if (rc.getMode() == RobotMode.PORTABLE) return MODE.MOVING;
        else return MODE.SOLDIER_HUB;
    }
    /////////////////////////////////////////////////////////////////////
    public boolean buildMiner(Direction dir) throws GameActionException {
        if (rc.canBuildRobot(RobotType.MINER, dir)) {
            rc.buildRobot(RobotType.MINER, dir);
            radio.updateCounter(RobotType.MINER);
            built_units++;
            num_miners++;
            troopCounter[0]++;
            return true;
        }
        return false;
    }

    public boolean buildSoldier(Direction dir) throws GameActionException {
        if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
            rc.buildRobot(RobotType.SOLDIER, dir);
            radio.updateCounter(RobotType.SOLDIER);
            built_units++;
            num_soldiers++;
            troopCounter[1]++;
            return true;
        }
        return false;
    }

    public boolean buildBuilder(Direction dir) throws GameActionException {
        if (rc.canBuildRobot(RobotType.BUILDER, dir)) {
            rc.buildRobot(RobotType.BUILDER, dir);
            radio.updateCounter(RobotType.BUILDER);
            built_units++;
            num_builders++;
            troopCounter[2]++;
            return true;
        }
        return false;
    }

    public boolean buildSage(Direction dir) throws GameActionException {
        if (rc.canBuildRobot(RobotType.SAGE, dir)) {
            rc.buildRobot(RobotType.SAGE, dir);
            radio.updateCounter(RobotType.SAGE);
            built_units++;
            num_sages++;
            troopCounter[3]++;
            return true;
        }
        return false;
    }

    public boolean builderInRange() throws GameActionException {
        RobotInfo[] allies = rc.senseNearbyRobots(RobotType.BUILDER.actionRadiusSquared, rc.getTeam());
        for (RobotInfo r : allies) {
            if (r.type == RobotType.BUILDER) {
                return true;
            }
        }
        return false;
    }

    //////////////////////////////////////////////////////////////////////
    static int curLead = 200;
    static int leadLastCall = 200;
    static int[] amountMined = new int[10]; // 10 turn avg

    public void updateAmountMined() {
        curLead = rc.getTeamLeadAmount(rc.getTeam());
        if (curLead >= leadLastCall) { // otherwise, something was spent
            int minedLastCall = curLead - leadLastCall;
            amountMined[round_num % amountMined.length] = minedLastCall;
        }
        leadLastCall = curLead;
    }

    public double getAvgMined() {
        int valid_entries = 0;
        double avg = 0;
        for (int i = 0; i < amountMined.length; i++)
            if (amountMined[i] != 0) {
                avg += amountMined[i];
                valid_entries++;
            }
        avg = avg / ((double) valid_entries);
        return avg;
    }

    public boolean checkForResources(int buildCost) throws GameActionException { // CHANGE TO INCORPORATE GOLD ONCE WE
                                                                                 // USE SAGES
        if (curLead >= buildCost || round_num < 20) {
            return true;
        } else {
            int numTurnsToResources = (int) (((double) buildCost - (double) curLead) / Math.max(getAvgMined(), 2.0));
            int numTurnsToAct = rc.getActionCooldownTurns()
                    + (int) ((cooldownMultiplier(rc.getLocation()) * rc.getType().actionCooldown) / 10);
            if (numTurnsToResources > numTurnsToAct) {
                return false;
            }
            if (numTurnsToResources > numTurnsToAct) {
                return false;
            } else
                return true;
        }
    }

    public MapLocation getAvgEnemyLocation() throws GameActionException {
        RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        int num_enemies = 0;
        double cx = 0;
        double cy = 0;
        for (RobotInfo enemy : enemies) {
            if (enemy.type == RobotType.SOLDIER || enemy.type == RobotType.SAGE) {
                cx += (double) enemy.location.x;
                cy += (double) enemy.location.y;
                num_enemies += 1;
            }
        }
        if (num_enemies > 0)
            return new MapLocation((int) (cx / num_enemies), (int) (cy / num_enemies));
        else
            return rc.getLocation().add(dirs[0]);
    }

    public Direction[] getEnemyDirs() throws GameActionException {
        MapLocation loc = getAvgEnemyLocation();
        Direction toDest = rc.getLocation().directionTo(loc);
        Direction[] d_arr = { toDest, toDest.rotateLeft(), toDest.rotateRight(), toDest.rotateLeft().rotateLeft(),
                toDest.rotateRight().rotateRight(), toDest.opposite().rotateLeft(), toDest.opposite().rotateRight(),
                toDest.opposite() };

        return d_arr;
    }

    public int distToWall(MapLocation n) {
        int min = Math.min(rc.getMapWidth() - 1 - n.x, n.x) + Math.min(n.y, rc.getMapHeight() - 1 - n.y);
        assert (min <= 60);
        return min;
    }

    public Direction[] sortedDirections() {
        Direction[] dirs = { Direction.NORTH, Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST, Direction.SOUTH,
                Direction.SOUTHWEST, Direction.WEST, Direction.NORTHWEST };
        Arrays.sort(dirs, (a, b) -> distToWall(rc.getLocation().add(b)) - distToWall(rc.getLocation().add(a)));
        return dirs;
    }

    public int[] chooseBuildOrder() {
        if (mapArea < 1400) {
            return new int[] { 1, 6, 0 }; // miners, soldiers, builders
        } else if (mapArea < 2200) {
            return new int[] { 1, 6, 0 }; // miners, soldiers, builders
        } else {
            return new int[] { 1, 6, 0 }; // miners, soldiers, builders
        }
    }

    public int[] chooseInitialBuildOrder() throws GameActionException {
        return new int[] { 1, 0, 0 };
    }

    public void attemptHeal() throws GameActionException {
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(rc.getType().actionRadiusSquared, rc.getTeam());
        // if there are any nearby enemy robots, attack the one with the least health
        if (nearbyBots.length > 0) {
            RobotInfo weakestBot = null;
            for (RobotInfo bot : nearbyBots) {
                if (bot.type == RobotType.SOLDIER)
                    if ((weakestBot == null && bot.health < RobotType.SOLDIER.health) ||
                            (weakestBot != null && bot.health < weakestBot.health)) {
                        weakestBot = bot;
                    }
            }
            if (weakestBot != null) {
                if (rc.canRepair(weakestBot.location)) {
                    // rc.setIndicatorString("Succesful Heal!");
                    rc.repair(weakestBot.location);
                }
            } else {
                for (RobotInfo bot : nearbyBots) {
                    if (bot.type == RobotType.MINER)
                        if ((weakestBot == null && bot.health < RobotType.MINER.health) ||
                                (weakestBot != null && bot.health < weakestBot.health)) {
                            weakestBot = bot;
                        }
                }
                if (weakestBot != null) {
                    if (rc.canRepair(weakestBot.location)) {
                        rc.repair(weakestBot.location);
                    }
                }
            }
        }
    }

    public void addLeadEstimate() throws GameActionException {
        MapLocation[] leadLocs = rc.senseNearbyLocationsWithLead();
        MapLocation[] friendlyArchonLocs = radio.getPreviousArchons(archonNumber);
        int total_value = 0;
        if (archonNumber != 0) {
            MapLocation[] prevArchons = firstN(friendlyArchonLocs, archonNumber - 1);
            MapLocation[] leadLocsUncounted = leadLocsNotCounted(leadLocs, prevArchons);
            for (MapLocation loc : leadLocsUncounted) {
                total_value += rc.senseLead(loc);
            }
        } else {
            for (MapLocation loc : leadLocs) {
                total_value += rc.senseLead(loc);
            }
        }
        if (total_value > 0) {
            radio.incrementLeadEsimate(total_value);
        }
    }

    public MapLocation[] leadLocsNotCounted(MapLocation[] leadLocs, MapLocation[] archons) throws GameActionException {
        MapLocation[] locs = new MapLocation[leadLocs.length];
        int uncounted = 0;
        for (int i = 0; i < leadLocs.length; i++) {
            boolean counted = false;
            // check if lead is visible by any of the archons
            for (int j = 0; j < archons.length; j++) {
                if (leadLocs[i].distanceSquaredTo(archons[j]) <= RobotType.ARCHON.visionRadiusSquared) {
                    counted = true;
                }
            }
            // if not visible, add to list
            if (!counted) {
                locs[uncounted] = leadLocs[i];
                uncounted++;
            }
        }
        MapLocation[] leadLocsUncounted = new MapLocation[uncounted];
        for (int i = 0; i < uncounted; i++) {
            leadLocsUncounted[i] = locs[i];
        }
        return leadLocsUncounted;
    }

    public MapLocation[] firstN(MapLocation[] locs, int n) throws GameActionException {
        MapLocation[] newLocs = new MapLocation[n];
        for (int i = 0; i < n; i++) {
            newLocs[i] = locs[i];
        }
        return newLocs;
    }
}