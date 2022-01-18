# Source: https://github.com/IvanGeffner/battlecode2021/blob/master/thirtyone/BFSMuckraker.java
# also in Nav.java

# changes
# 1. cooldown calculation: instead of 1/passability
# Math.floor((1 + (double)rc.senseRubble(loc)/10.0)*rc.getType().movementCooldown)

# 2. location used (vision radius not neccessarily 20)
# encoding scheme -> 15 x 15 box , with droid's location at (7, 7)

# 3. edge locations are diff as a consequence of having diff locs in general

from collections import defaultdict

VISION = 20 # radius squared 
GRID = 13

assert((GRID/2)**2 >= VISION)

def numToLoc(num):
 return num%GRID, num//GRID

def distFromDroid(num):
	center = (GRID//2)
	x2, y2 = numToLoc(num)
	return (center-x2)**2 + (center-y2)**2

def numWithinRange(num):
	return distFromDroid(num) < VISION;

nodes = []
edges = defaultdict(lambda:[])

def constructGraph():
	for i in range(GRID**2):
		if numWithinRange(i):
			nodes.append(i)

	## now try all edges
	d = [1, -1, -1*GRID, GRID, 1+GRID, -1+GRID, 1-GRID, -1-GRID]
	for node in nodes:
		for delta in d:
			if (distFromDroid(node+delta) < distFromDroid(node)):
				edges[node].append(node+delta); # backwards dp

constructGraph()
print(nodes)
print(edges) 

