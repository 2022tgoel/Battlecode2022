d = ["Direction.EAST","Direction.WEST","Direction.SOUTH" ,"Direction.NORTH",
	"Direction.NORTHEAST","Direction.NORTHWEST","Direction.SOUTHEAST" ,"Direction.SOUTHWEST"]


with open("File.java", "w") as f:
	f.write("""

	import battlecode.common.*;
	public class MinerNav {
		static RobotController rc;
		MinerNav(RobotController rc) {
			this.rc = rc;
		}\n""")

	numTabs = 2
	tabs = "\t"*numTabs
	for i in range(len(d)):
		f.write(f"{tabs}static MapLocation loc{i};\n")
	f.write(f"{tabs}static MapLocation bestSpot;\n")
	f.write(f"{tabs}int rubble;\n")

	fn_name = "findBestSquare"

	f.write(f"{tabs}MapLocation " + fn_name + "(MapLocation leadLoc, int minRubble) throws GameActionException{\n")

	numTabs += 1
	tabs = "\t"*numTabs

	for i in range(len(d)):
		f.write(f"{tabs}loc{i} = leadLoc.add({d[i]});\n")

	for i in range(len(d)):
		'''
		if (rc.canSenseLocation(loc)) {
			rubble = 1 + rc.senseRubble(loc) / 10;
			if (rubble < minRubble) {
				minRubble = rubble;
				bestSpot = loc;
			}
		}
        '''
		f.write(f"{tabs}if (rc.canSenseLocation(loc{i})) {'{'}\n")
		f.write(f"{tabs}\trubble = 1 + rc.senseRubble(loc{i}) / 10;\n")
		f.write(f"{tabs}\tif (rubble < minRubble) {'{'}\n")
		f.write(f"{tabs}\t\tminRubble = rubble;\n")
		f.write(f"{tabs}\t\tbestSpot = loc{i};\n")
		f.write(tabs + "\t}\n")
		f.write(tabs + "}\n")
	
	f.write(f"{tabs}return bestSpot;\n")
	numTabs -= 1
	tabs = "\t"*numTabs
	f.write(tabs + "}\n")
	numTabs -= 1
	tabs = "\t"*numTabs
	f.write(tabs + "}\n")