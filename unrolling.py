
d = ["Direction.EAST","Direction.WEST","Direction.SOUTH" ,"Direction.NORTH",
	"Direction.NORTHEAST","Direction.NORTHWEST","Direction.SOUTHEAST" ,"Direction.SOUTHWEST"]


with open("File.java", "w") as f:
	for i in range(len(d)):
		f.write(f"loc{i} = leadLoc.add({d[i]});\n")

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
		f.write(f"if (rc.canSenseLocation(loc{i})) {'{'}\n")
		f.write(f"\trubble = 1 + rc.senseRubble(loc{i}) / 10;\n")
		f.write(f"\tif (rubble < minRubble) {'{'}\n")
		f.write(f"\t\tminRubble = rubble;\n")
		f.write(f"\t\tbestSpot = loc{i};\n")
		f.write("\t}\n")
		f.write("}\n")