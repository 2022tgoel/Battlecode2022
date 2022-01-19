package rebutia_pathfinding;
import java.util.Map;

import battlecode.common.*;

public class BFS {
    RobotController rc;
    public BFS(RobotController robotController) {
        rc = robotController;
    }   

    public Direction getBestDirBFS(MapLocation target) throws GameActionException{
        MapLocation cur = rc.getLocation();
        int rsqr = 20;
        MapLocation[] sortedPos = sortedPos(rsqr);
        int num_locs = sortedPos.length;
        int num_edges = 24;

        MapLocation[] locs = getLocs(cur, sortedPos);
        double[] vs = new double[num_locs];
        Direction[] dirs = new Direction[num_locs];

        Direction[] starting_dirs = new Direction[]{Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST, Direction.SOUTHWEST, Direction.SOUTHEAST, Direction.NORTHWEST, Direction.NORTHEAST}; //{"S", "W", "N", "E", "SW", "SE", "NW", "NE"};

        for (int i = 0; i < num_locs; i++) {
            vs[i] = 1000000;
            dirs[i] = null;
        }

        for (int i = 0; i < num_locs; i++) {
            if (rc.onTheMap(locs[i])) {
                double r = 1 + rc.senseRubble(locs[i]) / 10;
                double v = vs[getHash(locs[i])];
                double v_n;
                MapLocation[] neighbors = getCloserNeighbors(locs[i], cur);
                for (int j = 0; j < neighbors.length; j++) {
                    v_n = vs[getHash(neighbors[j])];
                    if (v > v_n + r) {
                        v = v_n + r;
                        locs[i] = neighbors[j];
                        if (j < 8) dirs[i] = starting_dirs[j];
                        else dirs[i] = dirs[getHash(neighbors[j])];
                    }
                }
            }
        }

        if (cur.distanceSquaredTo(target) <= rsqr) {
            return dirs[getHash(new MapLocation(target.x - cur.x + 5, target.y - cur.y + 5))];
        }

        Direction ans = null;
        MapLocation[] edges = getLastN(locs, num_edges);
        double bestEstimation = 0;
        double initialDist = Math.sqrt(cur.distanceSquaredTo(target));

        for (int i = 0; i < edges.length; i++) {
            double dist = (initialDist - Math.sqrt(edges[i].distanceSquaredTo(target))) / vs[getHash(edges[i])];
            if (dist > bestEstimation) {
                bestEstimation = dist;
                ans = dirs[getHash(edges[i])];
            }
        }

        return ans;
    }


    public static int getHash(MapLocation pos) {
        return pos.x * 11 + pos.y;
    }

    public static double getR(int[] loc) {
        return 0;
    }

    public MapLocation[] getCloserNeighbors(MapLocation pos, MapLocation center) {
        MapLocation[] neighbors = new MapLocation[8];
        int counter = 0;
        // finds neighbors that are closer to center
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }
                MapLocation neighbor = new MapLocation(pos.x + x, pos.y + y);
                if (neighbor.distanceSquaredTo(center) <= pos.distanceSquaredTo(center)) {
                    neighbors[counter] = new MapLocation(pos.x + x, pos.y + y);
                    counter++;
                }
            }
        }

        MapLocation[] justNeighbors = new MapLocation[counter];
        for (int i = 0; i < counter; i++) {
            justNeighbors[i] = new MapLocation(neighbors[i].x, neighbors[i].y);
        }

        return justNeighbors;
    }

    // last N elements from array
    public MapLocation[] getLastN(MapLocation[] pos, int n) {
        MapLocation[] lastN = new MapLocation[n];
        for (int i = 0; i < n; i++) {
            lastN[i] = pos[pos.length - 1 - i];
        }
        return lastN;
    }

    public static MapLocation[] getLocs(MapLocation center, MapLocation[] sortedPos) {
        MapLocation[] locs = new MapLocation[sortedPos.length];
        for (int i = 0; i < locs.length; i++) {
            locs[i] = new MapLocation(sortedPos[i].x + center.x - 5, sortedPos[i].y + center.y - 5);
        }
        return locs;
    }

    public static MapLocation[] sortedPos(int rsqr) {
        int n = (int) Math.ceil(Math.sqrt(rsqr));
        MapLocation center = new MapLocation(n, n);
        MapLocation[] pos = new MapLocation[(2 * n + 1) * (2 * n + 1)];
        double[] dist = new double[(2 * n + 1) * (2 * n + 1)];
        double d = 0;
        int counter = 0;
        for (int i = 0; i < 2 * n + 1; i++) {
            for (int j = 0; j < 2 * n + 1; j++) {
                // if point in circle
                d = center.distanceSquaredTo(new MapLocation(i, j));
                if (d <= rsqr) {
                    pos[counter] = new MapLocation(i,j);
                    dist[counter] = (double) d;
                    counter++;
                }
            }
        }

        // sort pos by distance
        for (int i = 0; i < counter; i++) {
            for (int j = i + 1; j < counter; j++) {
                if (dist[i] > dist[j]) {
                    double temp = dist[i];
                    dist[i] = dist[j];
                    dist[j] = temp;
                    MapLocation temp2 = pos[i];
                    pos[i] = pos[j];
                    pos[j] = temp2;
                }
            }
        }

        MapLocation[] pos_new = new MapLocation[counter - 1];

        for (int i = 1; i < counter; i++) {
            pos_new[i - 1] = pos[i];
        }

        return pos_new;
    }

    public static void printVector(int[] vector) {
        System.out.print("[ ");
        for (int i = 0; i < vector.length; i++) {
            System.out.print(vector[i] + " ");
        }
        System.out.print("]");
    } 
}
