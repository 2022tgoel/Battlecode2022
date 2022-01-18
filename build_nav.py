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

assert((GRID//2 + 1)**2 > VISION) # grid is large enough to incorporate entire vision radius

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

def getNodes():
	for i in range(GRID**2):
		if numWithinRange(i):
			nodes.append(i)
	'''
	## now try all edges
	d = [1, -1, -1*GRID, GRID, 1+GRID, -1+GRID, 1-GRID, -1-GRID]
	for node in nodes:
		for delta in d:
			if (distFromDroid(node+delta) < distFromDroid(node)):
				edges[node].append(node+delta); # backwards dp
	'''

getNodes()

with open("Navigation.java", "w") as f:
	def writeInstantiations(f, indent=1):
		s = "\t"*indent
		for n in nodes:
			f.write(f"{s}static MapLocation l{n};\n")
			f.write(f"{s}static double v{n};\n")
			f.write(f"{s}static Direction d{n};\n")
			f.write(f"{s}static double p{n};\n")
			f.write("\n")

	def writeValueSetting(f, indent=2):
		s = "\t"*indent
		center = GRID*(GRID//2) + (GRID//2)
		for n in nodes:
			value = 0 if n == center else 1000000
			f.write(f"{s}l{n} = ;\n")
			f.write(f"{s}v{n} = {value};\n")
			f.write(f"{s}d{n} = null;\n")

	def writeFunction(f):
		f.write("\tDirection getBestDir(MapLocation target) throws GameActionException{\n")
		writeValueSetting(f)
		f.write("\t}\n")


	lines = ["import battlecode.common.Direction;",
			"import battlecode.common.MapLocation;",
			"import battlecode.common.RobotController;",
			"public class Navigation {", 
			"\tstatic RobotController rc;"
			"\tNavigation(RobotController rc) {",
			"\t\tthis.rc= rc;",
			"\t}"]
	for line in lines:
		f.write(line+"\n")
	f.write("\n")

	writeInstantiations(f)
	f.write("\n")

	writeFunction(f)
	f.write("\n")

	f.write("}")
