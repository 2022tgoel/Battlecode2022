package schlumbergera;

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
        ;
    }

    int round_num;
    int archonNumber = -1;

    Direction[] dirs;

    int built_units = 0;
    int counter = 0;

    int num_soldiers = 0;
    int num_miners = 0;
    int num_builders = 0;
    int num_archons_init;
    int num_archons_alive;

    int num_soldiers_hub = 0;

    int[] defaultBuildOrder;
    int threatChannel = -1;

    Comms radio;

	public Archon(RobotController rc) throws GameActionException {
        super(rc);
        System.out.println("here");
        num_archons_alive = rc.getArchonCount();
        num_archons_init = num_archons_alive;
        dirs = sortedDirections();
        defaultBuildOrder = chooseBuildOrder();
        radio = new Comms(rc);
        archonNumber = radio.getArchonNum();
    }

    @Override
    public void run() throws GameActionException {
        round_num = rc.getRoundNum();
        radio.update();
        radio.clearThreat();
        radio.clearMiningAreas();
        radio.clearTargetAreas();
        radio.clearArchonMovementLocation();


        archonNumber = radio.getArchonNum();
        radio.postArchonLocation(archonNumber);
        updateAmountMined();

        //System.out.println("Archon number: " + archonNumber + " Mode num: " + radio.getMode() + " " + " round: " + round_num);
        MODE mode = determineMode();
            
        if (mode != MODE.MOVING){
            if (rc.getMode()== RobotMode.PORTABLE && rc.canTransform()) rc.transform();
        }

        switch (mode) {
            case THREATENED:
                threatChannel = radio.sendThreatAlert();
                int tot = radio.totalUnderThreat();
                if (round_num % tot !=threatChannel){ //alternate between those under threat
                    break;
                }
                if (checkForResources(RobotType.SOLDIER.buildCostLead)) {
                    Direction[] enemyDirs = getEnemyDirs();
                    for (Direction dir: enemyDirs) {
                        buildSoldier(dir);
                    }
                }
                else attemptHeal();
                break;
            case INITIAL:
                if (round_num % num_archons_alive != archonNumber) break;
                build(chooseInitialBuildOrder());
                break;
            case SOLDIER_HUB:
                if (checkForResources(RobotType.SOLDIER.buildCostLead)) {
                    boolean soldier_built = build(new int[]{0, 1, 0});
                    if (soldier_built) num_soldiers_hub++;
                }
                else {
                    attemptHeal();
                 //   rc.setIndicatorString("ATTEMPTING HEALING");
                }
                break;
            case MOVING:
                moveToLocation(move);
                radio.sendMovingAlert();
                break;
            case OTHER_THREATENED: 
                if (rc.getTeamLeadAmount(rc.getTeam()) < 600) {
                    attemptHeal();
                    break; //save for attacked archons
                }
            case DEFAULT:
                attemptHeal();
                if (round_num % num_archons_alive != archonNumber || round_num % 4 != 0) break;
                //if (b){
                build(new int[]{4, 1, 0}); //defaultBuildOrder);
                //}
                break;
        }
        num_archons_alive = rc.getArchonCount();
        rc.setIndicatorString("mode: " + mode.toString() + " " + getArchonMovementLocation());
    }

    public boolean build(int[] build_order) throws GameActionException{
        boolean unit_built = false;
        for (Direction dir: dirs) {
            switch (counter % 3) {
                case 0:
                    if (built_units < build_order[counter % 3]) {
                       // rc.setIndicatorString("Trying to build a miner" + " built_units: " + built_units + " " + build_order[counter % 3]);
                        unit_built = buildMiner(dir);
                        // System.out.println("MINER BUILT: " + unit_built + " Roundnum: " + rc.getRoundNum());
                    }
                    break;
                case 1:
                    if (built_units < build_order[counter % 3]) {
                   // rc.setIndicatorString("Trying to build a soldier" + " built_units: " + built_units + " " + build_order[counter % 3]);
                    unit_built = buildSoldier(dir);
                    // System.out.println("SOLDIER BUILT: " + unit_built);
                    }
                    break;
                case 2:
                    if (built_units < build_order[counter % 3]) {
                      //  rc.setIndicatorString("Trying to build a builder" + " built_units: " + built_units + " " + build_order[counter % 3]);
                        unit_built = buildBuilder(dir);
                        // System.out.println("BUILDER BUILT: " + unit_built);
                    }
                    break;
            }
            if (built_units >= build_order[counter % 3]){
                counter++;
                built_units = 0;
            }
            if (unit_built) return true;
        }
        return false;
    }

    
    public MODE determineMode() throws GameActionException {
        int sizeBracket = (int) Math.ceil((double) mapArea / 1000);
        int numMinersInitial = Math.max((sizeBracket*3)/num_archons_init, 1);
        if (underThreat()) return MODE.THREATENED;
        else if (radio.totalUnderThreat() > 0) return MODE.OTHER_THREATENED;
        else if (num_miners < numMinersInitial) return MODE.INITIAL; 
        else {
            move = getArchonMovementLocation();
            if (move == null){
                //no soldier action anywhere
                if (archonNumber == 0) return MODE.SOLDIER_HUB;
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
                    return MODE.DEFAULT;
                } 
            }
        }
    }

    public boolean underThreat() throws GameActionException{
        RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        if (enemies.length > 0) {
            broadcastTarget(enemies[0].location);
            return true;
        }
        return false;
    }

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

    MapLocation move = null;
    int turnsMoving = 0;
    final int maxTurnsMoving = 60;
    int turnsWaiting = 60;
    final int minTurnsWaiting = 60;
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
                if (rc.canTransform()) rc.transform();
                turnsWaiting = 0;
            }
            else turnsWaiting++;
        }
        if (rc.getMode() == RobotMode.PORTABLE) return MODE.MOVING;
        else return MODE.SOLDIER_HUB;
    }
    
    /////////////////////////////////////////////////////////////////////
    public boolean buildMiner(Direction dir) throws GameActionException{
        if (rc.canBuildRobot(RobotType.MINER, dir)) {
            rc.buildRobot(RobotType.MINER, dir);
            built_units++;
            num_miners++;
            return true;
        }
        return false;
    }

    public boolean buildSoldier(Direction dir) throws GameActionException{ 
        if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
            rc.buildRobot(RobotType.SOLDIER, dir);
            built_units++;
            num_soldiers++;
            return true;
        }
        return false;
    }

    public boolean buildBuilder(Direction dir) throws GameActionException {
        if (rc.canBuildRobot(RobotType.BUILDER, dir)) {
            rc.buildRobot(RobotType.BUILDER, dir);
            built_units++;
            num_builders++;
            return true;
        }
        return false;
    }

    public boolean builderInRange() throws GameActionException{
        RobotInfo[] allies = rc.senseNearbyRobots(RobotType.BUILDER.actionRadiusSquared, rc.getTeam());
        for (RobotInfo r : allies) {
            if (r.type == RobotType.BUILDER){
                return true;
            }
        }
        return false;
    }
    //////////////////////////////////////////////////////////////////////
    static int curLead = 200;
    static int leadLastCall = 200;
    static int[] amountMined = new int[10]; //10 turn avg
    public void updateAmountMined(){
        curLead = rc.getTeamLeadAmount(rc.getTeam());
        if (curLead >= leadLastCall){ //otherwise, something was spent
            int minedLastCall = curLead - leadLastCall;
            amountMined[round_num % amountMined.length] = minedLastCall;
        }
        leadLastCall = curLead;
    }

    public double getAvgMined(){
        double avg = 0;
        for (int i = 0; i < amountMined.length; i++) avg += (double)amountMined[i];
        avg = avg / (double) amountMined.length;
        return avg;
        
    }

    public boolean checkForResources(int buildCost) throws GameActionException { //CHANGE TO INCORPORATE GOLD ONCE WE USE SAGES
        if (curLead >= buildCost || round_num < 20){
            return true;
        }
        else {
            int numTurnsToResources = (int)(((double)buildCost - (double)curLead)/ Math.max(getAvgMined(), 2.0));
            int numTurnsToAct = rc.getActionCooldownTurns() + (int) ((cooldownMultiplier(rc.getLocation()) * rc.getType().actionCooldown)/10);
            if (numTurnsToResources > numTurnsToAct) {
                return false;
            }
            else return true;
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
        if (num_enemies > 0) return new MapLocation((int) (cx / num_enemies), (int) (cy / num_enemies));
        else return rc.getLocation().add(dirs[0]);
    }

    public Direction[] getEnemyDirs() throws GameActionException{
        MapLocation loc = getAvgEnemyLocation();
        Direction toDest = rc.getLocation().directionTo(loc);
        Direction[] d_arr = {toDest, toDest.rotateLeft(), toDest.rotateRight(), toDest.rotateLeft().rotateLeft(),
                toDest.rotateRight().rotateRight(), toDest.opposite().rotateLeft(), toDest.opposite().rotateRight(), toDest.opposite()};
        
        return d_arr;
    }

    public int distToWall(Direction d) {
        MapLocation my = rc.getLocation();
        MapLocation n = my.add(d);
        int min = Math.min(rc.getMapWidth() - 1 - n.x, n.x) + Math.min(n.y, rc.getMapHeight() - 1 - n.y);
        assert(min<=60);
        return min;
    }

    public Direction[] sortedDirections() {
        Direction[] dirs = {Direction.NORTH, Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST, Direction.SOUTH, Direction.SOUTHWEST, Direction.WEST, Direction.NORTHWEST};
        Arrays.sort(dirs, (a,b) -> distToWall(b) - distToWall(a));
        return dirs;
    }

    public int[] chooseBuildOrder() {
        if (mapArea < 1400) {
            return new int[]{1, 6, 0}; // miners, soldiers, builders
        }
        else if (mapArea < 2200) {
            return new int[]{1, 6, 0}; // miners, soldiers, builders
        }
        else {
            return new int[]{1, 6, 0}; // miners, soldiers, builders
        }
    }

    public int[] chooseInitialBuildOrder() throws GameActionException{
        return new int[]{1, 0, 0};
    }
    
    final static RobotType[] healingOrder = new RobotType[]{RobotType.SOLDIER, RobotType.MINER, RobotType.SAGE, RobotType.WATCHTOWER};
    public void attemptHeal() throws GameActionException {
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(rc.getType().actionRadiusSquared, rc.getTeam());
        // if there are any nearby enemy robots, attack the one with the least health
        RobotInfo weakestBot = null;
        for (RobotType typeToHeal : healingOrder){
            for (RobotInfo bot : nearbyBots) {
                if (bot.type == typeToHeal)
                    if ((weakestBot == null && bot.health < typeToHeal.health) || 
                        (weakestBot != null && bot.health < weakestBot.health)) {
                        weakestBot = bot;
                    }
            }
            if (weakestBot!=null) {
                if (rc.canRepair(weakestBot.location)) {
                  //  rc.setIndicatorString("Succesful Heal!");
                    rc.repair(weakestBot.location);
                }
                return;
            }
        }
    }
    
    
}