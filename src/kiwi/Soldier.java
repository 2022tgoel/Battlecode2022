package kiwi;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class Soldier extends Unit {
    enum SoldierMode {
        EXPLORATORY,
        HUNTING,
        SUPPORT,
        ARCHON_RUSH,
        FLEE,
        LOW_HEALTH,
        DEFENSIVE_RUSH,
        WAITING,
        CONVOY;
    }

    private MapLocation target;

    private int DRUSH_RSQR = 400;
    private int ARUSH_RSQR = 900;

    public Soldier(RobotController rc) throws GameActionException {
        super(rc);
        // Seed the rng!
        rng.setSeed(0);
        initialize();
    }

    public static int manhattanDistance(MapLocation a, MapLocation b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    public double getSquareQValue(MapLocation location, RobotInfo[] allNearbyRobots, MapLocation[] threatenedArchons,
            MapLocation nearestEnemyArchon)
            throws GameActionException {
        double Q = 0;

        int enemySoldiers = 0;
        int friendlySoldiers = 0;
        MapLocation nearbyFriendlyArchon = null;
        Team team = rc.getTeam();

        if (false) {
            for (RobotInfo robot : allNearbyRobots) {
                int r2 = location.distanceSquaredTo(robot.location);
                int manhattan = Math.abs(location.x - robot.location.x) + Math.abs(location.y - robot.location.y);
                double levelDistance = Math.pow(r2, 1 / 4);

                switch (robot.type) {
                    case SOLDIER:
                        int hitsUntilDeath = (robot.health + 2) / 3;
                        // 1.0 casts to double
                        if (robot.team == rc.getTeam()) {
                            friendlySoldiers++;
                            // Q += 0.5 * hitsUntilDeath / levelDistance;
                        } else {
                            enemySoldiers++;
                            // Q -= 0.5 * hitsUntilDeath / levelDistance;
                        }
                        break;
                    case ARCHON:
                        if (robot.team == team) {
                            nearbyFriendlyArchon = robot.location;
                        } else {
                            System.out.println("Detected nearby Archon at a distance of " + r2);
                            // There's an archon nearby, we BETTER get it.
                            Q -= manhattan * 1000;
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        // MapLocation nearestEnemyArchon = getNearestBroadcastedEnemyArchon();
        if (nearestEnemyArchon != null) {
            Q -= location.distanceSquaredTo(nearestEnemyArchon);
        }

        // Fall towards threatened Archons
        for (MapLocation threatenedArchonLocation : threatenedArchons) {
            Q -= location.distanceSquaredTo(threatenedArchonLocation);
        }

        // Fall away from nearby Archon
        // if (nearbyFriendlyArchon != null) {
        // Q += Math.pow(location.distanceSquaredTo(nearbyFriendlyArchon), 1 / 4);
        // }

        // Discourage moving into rubble when there are enemies.
        // int delayTurns = rc.senseRubble(location) / 10;
        // Q -= delayTurns * enemySoldiers;

        // // Introduce some noise :)
        // // Why? Because we can.
        // Q += rng.nextDouble() * 0.01;

        return Q;
    }

    @Override
    public void run() throws GameActionException {
        super.run();

        /**
         * Soldier strategy:
         * Choose the nearby location with the highest value according to the formula:
         * value = number of enemies * (allied hits - enemy hits) + number of allies.
         */

        RobotInfo[] allNearbyRobots = rc.senseNearbyRobots(-1);
        MapLocation[] threatenedArchons = findThreatenedArchons();

        Direction[] allDirections = Direction.allDirections();
        double[] q = new double[allDirections.length];
        boolean moved = false;

        if (rc.isMovementReady()) {
            double qMax = -Double.MAX_VALUE;
            Direction qMaxDirection = null;

            // Choose the best location to move to.
            for (int i = 0; i < allDirections.length; i++) {
                Direction direction = allDirections[i];
                if (rc.canMove(direction)) {
                    MapLocation location = rc.getLocation().add(direction);
                    double Q = getSquareQValue(location, allNearbyRobots, threatenedArchons, nearestEnemyArchon);
                    q[i] = Q;
                    if (Q > qMax) {
                        qMaxDirection = direction;
                    }
                }
            }

            if (qMaxDirection != null && qMaxDirection != Direction.CENTER) {
                rc.move(qMaxDirection);
                moved = true;
            }
        }

        // Repeatedly attack the weakest troop.
        RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        RobotInfo weakestEnemy = null;
        for (RobotInfo enemy : enemies) {
            if (enemy.type == RobotType.SOLDIER) {
                if ((weakestEnemy == null || enemy.health < weakestEnemy.health) && rc.canAttack(enemy.location)) {
                    weakestEnemy = enemy;
                }
            } else if (enemy.type == RobotType.ARCHON) {
                weakestEnemy = enemy;
                break;
            }
        }
        if (weakestEnemy != null) {
            // rc.setIndicatorString("Attacking " + weakestEnemy.location.toString());
            rc.setIndicatorLine(rc.getLocation(), weakestEnemy.getLocation(), 100, 0, 0);
            while (rc.canAttack(weakestEnemy.location)) {
                rc.attack(weakestEnemy.location);
            }
        }

        String qString = "";
        if (moved) {
            for (int i = 0; i < q.length; i++) {
                qString += q[i] + " ";
            }
        } else {
            qString = "No move";
        }

        rc.setIndicatorString("Q: " + qString);
    }

    public double[] getHealthGradient() throws GameActionException {
        // Returns the gradient from positive health (aka healthy friendly troops) to
        // negative health (aka healthy unfriendly troops)
        // By default, troops will stick together. However, when they detect enemies,
        // Troops at the fringe will move towards them.
        // AKA The value of being at a given location is given by
        // enemyHealth * (friendHealth - enemyHealth - 1) + numFriends.
        // If there are no enemies, then the value comes from being near friends.

        double dx = 0;
        double dy = 0;

        MapLocation cur = rc.getLocation();
        RobotInfo[] nearbyBots = rc.senseNearbyRobots(-1);

        for (RobotInfo bot : nearbyBots) {
            if (bot.type == RobotType.SOLDIER) {
                double benefit = bot.health;
                if (bot.team == rc.getTeam().opponent()) {
                    // If there is one robot on the other side, and two on this side,
                    // then the gradient is positive.
                    benefit = -1.5 * benefit;
                }

                double botX = bot.location.x - cur.x;
                double botY = bot.location.y - cur.y;

                if (botX != 0) {
                    // Going in the positive X direction will increase health by...
                    dx += benefit / botX;
                }
                if (botY != 0) {
                    // Going in the positive Y direction will increase health by...
                    dy += benefit / botY;
                }
            }
        }

        return new double[] { dx, dy };
    }

    public void broadcastTarget(MapLocation enemy) throws GameActionException {
        int data;
        int loc = Comms.locationToInt(enemy);
        for (int i = 0; i < CHANNEL.NUM_TARGETS; i++) {
            data = rc.readSharedArray(CHANNEL.TARGET.getValue() + i);
            if (data == 0) {
                rc.writeSharedArray(CHANNEL.TARGET.getValue() + i, loc);
                break;
            }
        }
    }

    public void forgetDistantTargets() {
        // If too far away from the target, forget about it
        if (target != null && rc.getLocation().distanceSquaredTo(target) > 100) {
            target = null;
            return;
        }
    }

    public SoldierMode determineMode() throws GameActionException {
        // Priority 1 - Defend.
        MapLocation[] threatenedArchons = findThreatenedArchons();
        if (threatenedArchons != null) {
            for (MapLocation archon : threatenedArchons) {
                if (rc.getLocation().distanceSquaredTo(archon) <= DRUSH_RSQR) {
                    return SoldierMode.DEFENSIVE_RUSH;
                }
            }
        }

        // Priority 3 - Kill Archons.
        if (archonIndex != -1) {
            MapLocation archon = Comms.intToLocation(rc.readSharedArray(archonIndex));
            if (rc.getLocation().distanceSquaredTo(archon) <= ARUSH_RSQR) {
                return SoldierMode.ARCHON_RUSH;
            }
        }

        // See if there's a good direction to go.
        // Follow the direction of the health gradient.
        double[] healthGradient = getHealthGradient();
        // Translate the health gradient to a direction
        // double thres = 1;
        double attackThreshold = 0.5;
        int[] direction = {
                healthGradient[0] > -attackThreshold ? 1 : healthGradient[0] < attackThreshold ? -1 : 0,
                healthGradient[1] > -attackThreshold ? 1 : healthGradient[1] < attackThreshold ? -1 : 0
        };
        moveInDirection(direction);

        // If there's a nearby target, and we have enough ppl nearby, swarm it.
        if (target == null) {
            RobotInfo[] nearby = rc.senseNearbyRobots(-1);
            int friendlySoldierCount = 0;
            int enemySoldierCount = 0;
            if (nearby != null) {
                for (RobotInfo robot : nearby) {
                    if (robot.type == RobotType.SOLDIER) {
                        if (robot.team == rc.getTeam()) {
                            friendlySoldierCount++;
                        } else {
                            enemySoldierCount++;
                        }
                    }
                }
                if (friendlySoldierCount > enemySoldierCount && enemySoldierCount > 0) {
                    // MapLocation tgt = Comms.getTarget(rc, i);
                    // if (tgt == null)
                    // continue;
                    // if (rc.getLocation().distanceSquaredTo(tgt) < 100) {
                    // target = tgt;
                    return SoldierMode.HUNTING;
                    // }
                }
            }
        }

        // Priority 4 - Hunt enemies.
        if (target != null) {
            return SoldierMode.HUNTING;
        }

        return SoldierMode.EXPLORATORY;
    }

    public void moveTowardsClosest(MapLocation[] locations) throws GameActionException {
        MapLocation closest = locations[0];
        MapLocation location = rc.getLocation();
        int closestDistance = Integer.MAX_VALUE;
        // only find closest archon if there is more then one
        if (locations.length > 1) {
            for (MapLocation loc : locations) {
                if (loc.distanceSquaredTo(rc.getLocation()) < closestDistance) {
                    closestDistance = loc.distanceSquaredTo(location);
                    closest = loc;
                }
            }
        }
        fuzzyMove(closest);
    }

    public boolean archonDied() throws GameActionException {
        RobotInfo home;
        if (rc.canSenseLocation(homeArchon)) {
            home = rc.senseRobotAtLocation(homeArchon);
            return (home == null || home.type != RobotType.ARCHON);
        }
        return false;
    }

    /**
     * waitAtDist() stays near the home archon
     **/
    public void waitAtDist(MapLocation loc, int idealDistSquared, boolean shouldRepel) throws GameActionException {
        // stays at around an ideal dist
        MapLocation myLocation = rc.getLocation();
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getType().visionRadiusSquared, rc.getTeam());
        int buffer = 4;
        // rc.setIndicatorString("" +
        // Math.abs(myLocation.distanceSquaredTo(homeArchon)-idealDistSquared));
        if (Math.abs(myLocation.distanceSquaredTo(loc) - idealDistSquared) <= buffer) {
            return; // you're already in range
        }
        int[] costs = new int[8];
        for (int i = 0; i < 8; i++) {
            MapLocation newLocation = myLocation.add(directions[i]);
            int cost = 0;
            if (!rc.onTheMap(newLocation)) {
                cost = 999999;
            } else {
                cost = 10 * Math.abs(newLocation.distanceSquaredTo(loc) - idealDistSquared);
                if (shouldRepel) { // don't go near fellow ally soldiers
                    for (RobotInfo robot : nearbyAllies) {
                        if (robot.type == rc.getType()) {
                            cost -= newLocation.distanceSquaredTo(robot.location); // trying to maximize distance
                        }
                    }
                }
                // cost += rc.senseRubble(newLocation);
            }

            costs[i] = cost;

        }
        /*
         * String s = " ";
         * for (int i =0; i < 8; i++) s += directions[i] + ": " + costs[i] + " ";
         * rc.setIndicatorString(s);
         */
        int cost = 99999;
        Direction optimalDir = null;
        for (int i = 0; i < directions.length; i++) {
            Direction dir = directions[i];
            if (rc.canMove(dir)) {
                if (costs[i] < cost) {
                    cost = costs[i];
                    optimalDir = dir;
                }
            }
        }
        if (optimalDir != null) {
            if (rc.canMove(optimalDir)) {
                // rc.setIndicatorString("here2");
                rc.move(optimalDir);
            }
        }

    }

    public MapLocation[] findThreatenedArchons() throws GameActionException {
        MapLocation[] archons = new MapLocation[4];
        int numThreatenedArchons = 0;
        for (int i = 0; i < 4; i++) {
            // rc.writeSharedArray(, value);
            int data = rc.readSharedArray(CHANNEL.FRIENDLY_ARCHON_STATUS.getValue() + i);
            // go through channels until you find an empty one to communicate with.
            MapLocation location = Comms.intToLocation(data);
            if (location != null) {
                archons[numThreatenedArchons++] = location;
            }
        }

        // only return threatened archons.
        MapLocation[] threatenedArchons = new MapLocation[numThreatenedArchons];
        for (int i = 0; i < numThreatenedArchons; i++) {
            threatenedArchons[i] = archons[i];
        }
        return threatenedArchons;
    }

    public void moveToEnemySoldiers() throws GameActionException {
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
        if (num_enemies == 0)
            fuzzyMove(homeArchon);
        else
            fuzzyMove(new MapLocation((int) (cx / num_enemies), (int) (cy / num_enemies)));
    }

    public boolean isLowHealth() throws GameActionException {
        if (rc.getHealth() < 20) {
            return true;
        } else {
            return false;
        }
    }

    public Direction usefulDir() throws GameActionException {
        MapLocation cur = rc.getLocation();
        Direction d = rc.getLocation().directionTo(homeArchon); // placeholder
        MapLocation center = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);
        if (center.x - cur.x > 0) {
            if (center.y - cur.y > 0) {
                d = Direction.NORTHEAST;
            } else {
                d = Direction.SOUTHEAST;
            }
        } else {
            if (center.y - cur.y > 0) {
                d = Direction.NORTHWEST;
            } else {
                d = Direction.SOUTHWEST;
            }
        }
        // rc.setIndicatorString(d.dx + " " + d.dy);
        Direction[] dirs = { d, d.rotateLeft(), d.rotateRight() };
        return dirs[rng.nextInt(dirs.length)];
    }

    public RobotInfo findWeakestRobot(RobotInfo[] nearby, RobotType type) {
        RobotInfo weakest = null;
        for (RobotInfo ri : nearby) {
            if (ri.type == type || type == null) {
                if (weakest == null || weakest.health > ri.health) {
                    weakest = ri;
                }
            }
        }
        return weakest;
    }

    public void initialize() {
        DRUSH_RSQR = (int) ((double) mapArea / 9.0);
        ARUSH_RSQR = (int) ((double) mapArea / 4.0);
    }
}
