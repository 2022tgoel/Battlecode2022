package barbary_gleb;

import battlecode.common.*;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.Collections;

public class Archon extends Unit {

    enum MODE {
        INITIAL,
        DEFAULT,
        THREATENED,
        OTHER_THREATENED,
        BEST_RUBBLE
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

    int[] defaultBuildOrder;
    int threatChannel = -1;
    private int total_sage_count = 0;
    private int total_builder_count = 0;
    private int total_soldier_count= 0;
    private int total_miner_count = 0;

    Comms radio;

	public Archon(RobotController rc) throws GameActionException {
        super(rc);
        System.out.println("here");
        num_archons_alive = rc.getArchonCount();
        num_archons_init = num_archons_alive;
        dirs = sortedDirections();
        defaultBuildOrder = chooseBuildOrder();
        radio = new Comms(rc);
        archonNumber = radio.getArchonNumInit();
    }

    @Override
    public void run() throws GameActionException {
        round_num = rc.getRoundNum();
        radio.update();
        radio.clearThreat();
        radio.clearMiningAreas();

        archonNumber = radio.getArchonNum(num_archons_init, num_archons_alive, archonNumber);
        num_archons_alive = rc.getArchonCount();
        total_miner_count = radio.getMiners();
        total_builder_count = radio.getBuilders();
        total_soldier_count = radio.getSoldiers();
        if (round_num % num_archons_alive == archonNumber) {
            radio.clearCounts();
        }
        MODE mode = determineMode();
        switch (mode) {
            case THREATENED:
                threatChannel = radio.sendThreatAlert();
                int tot = radio.totalUnderThreat();
                
                if (round_num % tot !=threatChannel){ //alternate between those under threat
                    return;
                }
                Direction[] enemyDirs = getEnemyDirs();
                for (Direction dir: enemyDirs) {
                    buildSoldier(dir);
                }
                return;
            case INITIAL:
                moveToOptimalCooldown();

                if (round_num % num_archons_alive != archonNumber) {
                    return;
                }
                /*
                if (rc.getHealth() < RobotType.ARCHON.health && !builderInRange()){
                    for (int i = dirs.length -1; i>=0; i--){ //reverse
                        buildBuilder(dirs[i]);
                    }
                }*/
                build(chooseInitialBuildOrder());
                break;
            case OTHER_THREATENED: 
                if (rc.getTeamLeadAmount(rc.getTeam()) < 600){
                    break; //save for attacked archon
                }
            case DEFAULT:
                // could be optimize to stop running after already optimal
                moveToOptimalCooldown();


                if (round_num % num_archons_alive != archonNumber) {
                    return;
                }
                /*
                if (rc.getHealth() < RobotType.ARCHON.health && !builderInRange()){
                    for (int i = dirs.length -1; i>=0; i--){ //reverse
                        buildBuilder(dirs[i]);
                    }
                }*/
                build(defaultBuildOrder);
                break;
        }
        rc.setIndicatorString("Number of miners: " + total_miner_count);
        rc.setIndicatorString("mode: " + mode.toString() + " " +radio.totalUnderThreat());
    }

    public void build(int[] build_order) throws GameActionException{
        boolean unit_built = false;
        for (Direction dir: dirs) {
            switch (counter % 3) {
                case 0:
                    if (built_units < build_order[counter % 3]) {
                        rc.setIndicatorString("Trying to build a miner" + " built_units: " + built_units + " " + build_order[counter % 3]);
                        unit_built = buildMiner(dir);
                        // System.out.println("MINER BUILT: " + unit_built + " Roundnum: " + rc.getRoundNum());
                        if (unit_built) radio.setMiners(1);
                    }
                    break;
                case 1:
                    if (built_units < build_order[counter % 3]) {
                    rc.setIndicatorString("Trying to build a soldier" + " built_units: " + built_units + " " + build_order[counter % 3]);
                    unit_built = buildSoldier(dir);
                    // System.out.println("SOLDIER BUILT: " + unit_built);
                    if (unit_built) radio.setSoldiers(1);
                    }
                    break;
                case 2:
                    if (built_units < build_order[counter % 3]) {
                        rc.setIndicatorString("Trying to build a builder" + " built_units: " + built_units + " " + build_order[counter % 3]);
                        unit_built = buildBuilder(dir);
                        // System.out.println("BUILDER BUILT: " + unit_built);
                        if (unit_built) radio.setBuilders(1);
                    }
                    break;
            }
            if (built_units >= build_order[counter % 3]){
                counter++;
                built_units = 0;
            }
            if (unit_built) return;
        }
        radio.postBuild(BOT.NONE);
    }

    public void moveToOptimalCooldown() throws GameActionException {
        // find least rubble location within radius
        MapLocation[] nearbyLocs = rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), RobotType.ARCHON.visionRadiusSquared);
        MapLocation minRubbleLoc = rc.getLocation(); int minRubble = rc.senseRubble(minRubbleLoc);
        int curRubble = minRubble;

        for(MapLocation loc : nearbyLocs){
            int r = rc.senseRubble(loc);
            if(r < minRubble || (r == minRubble && loc.distanceSquaredTo(rc.getLocation()) < minRubbleLoc.distanceSquaredTo(rc.getLocation()))){
                minRubble = r;
                minRubbleLoc = loc;
            }
        }

        // move only if it gives u better cooldown multiplier
        if(curRubble/10 > minRubble/10){
            if(rc.getMode() == RobotMode.TURRET && rc.isTransformReady()) {
                System.out.println("Archon transformed to PORTABLE");
                rc.transform();
            }
            if(rc.isMovementReady()) {
                System.out.println("Archon moving to " + minRubbleLoc + " with rubble: " + curRubble + " -> " + minRubble);
                fuzzyMove(minRubbleLoc);
            }
        } else {
            if(rc.getMode() == RobotMode.PORTABLE && rc.isTransformReady()) {
                System.out.println("Archon transformed to TURRET");
                rc.transform();
            }
        }
    }

    public MODE determineMode() throws GameActionException {
        int sizeBracket = (int) Math.ceil((double) mapArea / 1000);
        if (underThreat()) return MODE.THREATENED;
        else if (radio.totalUnderThreat() > 0) return MODE.OTHER_THREATENED;
        else if (num_miners < (sizeBracket*3)/num_archons_init) return MODE.INITIAL;
        else return MODE.DEFAULT;
    }

    public boolean underThreat(){
        RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        return (enemies.length >=1);
    }
    
    /////////////////////////////////////////////////////////////////////
    public boolean buildMiner(Direction dir) throws GameActionException{
        if (rc.canBuildRobot(RobotType.MINER, dir)) {
            rc.buildRobot(RobotType.MINER, dir);
            built_units++;
            num_miners++;
            total_miner_count++;
            return true;
        }
        return false;
    }

    public boolean buildSoldier(Direction dir) throws GameActionException{ 
        if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
            rc.buildRobot(RobotType.SOLDIER, dir);
            built_units++;
            num_soldiers++;
            total_soldier_count++;
            return true;
        }
        return false;
    }

    public boolean buildBuilder(Direction dir) throws GameActionException {
        rc.setIndicatorString("here " + rc.canBuildRobot(RobotType.BUILDER, dir));
        if (rc.canBuildRobot(RobotType.BUILDER, dir)) {
            rc.buildRobot(RobotType.BUILDER, dir);
            built_units++;
            num_builders++;
            total_builder_count++;
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

    public void attemptHeal() throws GameActionException {
        boolean soldiers_home = false;
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(rc.getType().actionRadiusSquared, rc.getTeam());
        // if there are any nearby enemy robots, attack the one with the least health
        if (nearbyBots.length > 0) {
            RobotInfo weakestBot = nearbyBots[0];
            for (RobotInfo bot : nearbyBots) {
                if (bot.type == RobotType.SOLDIER)
                    if (bot.health < weakestBot.health) {
                        weakestBot = bot;
                    }
                    soldiers_home = true;
            }
            if (soldiers_home) {
                if (rc.canRepair(weakestBot.location)) {
                  //  rc.setIndicatorString("Succesful Heal!");
                    rc.repair(weakestBot.location);
                }
            }
            else {
                for (RobotInfo bot : nearbyBots) {
                    if (bot.type == RobotType.MINER)
                        if (bot.health < weakestBot.health) {
                            weakestBot = bot;
                        }
                }
                if (rc.canRepair(weakestBot.location)) {
                    rc.repair(weakestBot.location);
                }
            }
        }
    }
}