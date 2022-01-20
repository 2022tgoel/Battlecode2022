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

    public double getSquareQValue(MapLocation location, RobotInfo[] allNearbyRobots, MapLocation[] threatenedArchons)
            throws GameActionException {
        double Q = 0;

        double enemySoldiers = 0;
        double friendlySoldiers = 0;
        MapLocation nearbyFriendlyArchon = null;
        Team team = rc.getTeam();

        if (true) {
            for (RobotInfo robot : allNearbyRobots) {
                int r2 = location.distanceSquaredTo(robot.location);
                int manhattan = Math.abs(location.x - robot.location.x) + Math.abs(location.y - robot.location.y);
                double levelDistance = Math.pow(r2, 1 / 4);

                switch (robot.type) {
                    case MINER:
                        if (robot.team != team) {
                            // Get attracted to miners
                            Q += -Math.pow(r2, 1 / 4);
                        }
                    case SOLDIER:
                        int hitsUntilDeath = (robot.health + 2) / 3;
                        // 1.0 casts to double
                        if (robot.team == rc.getTeam()) {
                            friendlySoldiers += 1 / (levelDistance + 2);
                            // Q += 0.5 * hitsUntilDeath / levelDistance;
                        } else {
                            enemySoldiers += 1 / (levelDistance + 2);
                            // Q -= 0.5 * hitsUntilDeath / levelDistance;
                        }
                        break;
                    case ARCHON:
                        if (robot.team == team) {
                            nearbyFriendlyArchon = robot.location;
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        if (threatenedArchons.length > 0) {
            // Defensive.
            // Fall towards closest threatened Archon.
            // Don't be indecisive!!
            int closestThreatenedArchonDistance = Integer.MAX_VALUE;
            for (MapLocation threatenedArchonLocation : threatenedArchons) {
                int distance = location.distanceSquaredTo(threatenedArchonLocation);
                if (distance < closestThreatenedArchonDistance) {
                    closestThreatenedArchonDistance = distance;
                }
            }

            Q -= closestThreatenedArchonDistance;
        } else {
            Q += friendlySoldiers - enemySoldiers;

            // Offensive.
            // Encourage exploration.
            // Fall away from nearby Archon, but only to an extent.
            if (nearbyFriendlyArchon != null) {
                Q -= -Math.min(2, Math.pow(location.distanceSquaredTo(nearbyFriendlyArchon), 1 / 4));
            }

            // Fall towards enemy Archon
            if (nearestEnemyArchon != null) {
                Q += -Math.pow(location.distanceSquaredTo(nearestEnemyArchon), 1 / 2);
            }
        }

        // Discourage moving into rubble when there are enemies.
        int delayTurns = rc.senseRubble(location) / 10;
        Q -= delayTurns;

        // Introduce a very slight tug towards the center.
        MapLocation mapCenter = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);
        Q -= Math.pow(location.distanceSquaredTo(mapCenter), 1 / 4);
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
            MapLocation me = rc.getLocation();

            // Choose the best location to move to.
            for (int i = 0; i < allDirections.length; i++) {
                Direction direction = allDirections[i];
                if (rc.canMove(direction)) {
                    MapLocation location = me.add(direction);
                    double Q = getSquareQValue(location, allNearbyRobots, threatenedArchons);
                    // System.out.println(direction.toString() + " has Q value " + Q + "; qMax=" +
                    // qMax);
                    q[i] = Q;
                    if (Q > qMax) {
                        qMax = Q; // how tf am I such a dummy
                        qMaxDirection = direction;
                    }
                }
            }

            if (qMaxDirection != null && qMaxDirection != Direction.CENTER && rc.canMove(qMaxDirection)) {
                // rc.setIndicatorLine(me, me.add(qMaxDirection), 0, 100, 0);
                rc.move(qMaxDirection);
                moved = true;
            }
        }

        // Repeatedly attack the weakest troop.
        RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        RobotInfo weakestEnemy = null;
        for (RobotInfo enemy : enemies) {
            if (!rc.canAttack(enemy.location)) {
                continue;
            }
            if (enemy.type == RobotType.ARCHON) {
                weakestEnemy = enemy;
                break;
            }
            if (weakestEnemy == null) {
                weakestEnemy = enemy;
            }
            if (weakestEnemy.type == RobotType.MINER && enemy.type == RobotType.SOLDIER) {
                weakestEnemy = enemy;
            }
            if (weakestEnemy.type == enemy.type && weakestEnemy.health < enemy.health) {
                weakestEnemy = enemy;
            }
        }
        if (weakestEnemy != null) {
            // rc.setIndicatorString("Attacking " + weakestEnemy.location.toString());
            // rc.setIndicatorLine(rc.getLocation(), weakestEnemy.getLocation(), 100, 0, 0);
            while (rc.canAttack(weakestEnemy.location)) {
                rc.attack(weakestEnemy.location);
            }
        }

        // String qString = "" + nearestEnemyArchon;
        // if (moved) {
        // for (int i = 0; i < q.length; i++) {
        // qString += " " + q[i];
        // }
        // } else {
        // qString += " No move";
        // }
        String qString = "t.len: " + threatenedArchons.length + "; enemy: " + nearestEnemyArchon;

        rc.setIndicatorString(qString);
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

    public MapLocation[] findThreatenedArchons() throws GameActionException {
        MapLocation[] archons = new MapLocation[4];
        int n = 0;
        for (int i = 0; i < 4; i++) {
            int data = rc.readSharedArray(CHANNEL.FRIENDLY_ARCHON_STATUS.getValue() + i);
            // go through channels until you find an empty one to communicate with.
            MapLocation location = Comms.intToLocation(data);
            if (location != null) {
                archons[n++] = location;
            }
        }

        // only return threatened archons.
        MapLocation[] threatenedArchons = new MapLocation[n];
        for (int i = 0; i < n; i++) {
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
