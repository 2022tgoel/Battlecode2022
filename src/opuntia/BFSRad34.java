package opuntia;

import battlecode.common.*;
public class BFSRad34 extends BFS {
	static RobotController rc;
	static int cooldown;
	BFSRad34(RobotController rc) {
		this.rc = rc;
		this.cooldown = rc.getType().movementCooldown;
	}

	static MapLocation l112;
	static double v112;
	static Direction d112;
	static double p112;

	static MapLocation l97;
	static double v97;
	static Direction d97;
	static double p97;

	static MapLocation l111;
	static double v111;
	static Direction d111;
	static double p111;

	static MapLocation l113;
	static double v113;
	static Direction d113;
	static double p113;

	static MapLocation l127;
	static double v127;
	static Direction d127;
	static double p127;

	static MapLocation l96;
	static double v96;
	static Direction d96;
	static double p96;

	static MapLocation l98;
	static double v98;
	static Direction d98;
	static double p98;

	static MapLocation l126;
	static double v126;
	static Direction d126;
	static double p126;

	static MapLocation l128;
	static double v128;
	static Direction d128;
	static double p128;

	static MapLocation l82;
	static double v82;
	static Direction d82;
	static double p82;

	static MapLocation l110;
	static double v110;
	static Direction d110;
	static double p110;

	static MapLocation l114;
	static double v114;
	static Direction d114;
	static double p114;

	static MapLocation l142;
	static double v142;
	static Direction d142;
	static double p142;

	static MapLocation l81;
	static double v81;
	static Direction d81;
	static double p81;

	static MapLocation l83;
	static double v83;
	static Direction d83;
	static double p83;

	static MapLocation l95;
	static double v95;
	static Direction d95;
	static double p95;

	static MapLocation l99;
	static double v99;
	static Direction d99;
	static double p99;

	static MapLocation l125;
	static double v125;
	static Direction d125;
	static double p125;

	static MapLocation l129;
	static double v129;
	static Direction d129;
	static double p129;

	static MapLocation l141;
	static double v141;
	static Direction d141;
	static double p141;

	static MapLocation l143;
	static double v143;
	static Direction d143;
	static double p143;

	static MapLocation l80;
	static double v80;
	static Direction d80;
	static double p80;

	static MapLocation l84;
	static double v84;
	static Direction d84;
	static double p84;

	static MapLocation l140;
	static double v140;
	static Direction d140;
	static double p140;

	static MapLocation l144;
	static double v144;
	static Direction d144;
	static double p144;

	static MapLocation l67;
	static double v67;
	static Direction d67;
	static double p67;

	static MapLocation l109;
	static double v109;
	static Direction d109;
	static double p109;

	static MapLocation l115;
	static double v115;
	static Direction d115;
	static double p115;

	static MapLocation l157;
	static double v157;
	static Direction d157;
	static double p157;

	static MapLocation l66;
	static double v66;
	static Direction d66;
	static double p66;

	static MapLocation l68;
	static double v68;
	static Direction d68;
	static double p68;

	static MapLocation l94;
	static double v94;
	static Direction d94;
	static double p94;

	static MapLocation l100;
	static double v100;
	static Direction d100;
	static double p100;

	static MapLocation l124;
	static double v124;
	static Direction d124;
	static double p124;

	static MapLocation l130;
	static double v130;
	static Direction d130;
	static double p130;

	static MapLocation l156;
	static double v156;
	static Direction d156;
	static double p156;

	static MapLocation l158;
	static double v158;
	static Direction d158;
	static double p158;

	static MapLocation l65;
	static double v65;
	static Direction d65;
	static double p65;

	static MapLocation l69;
	static double v69;
	static Direction d69;
	static double p69;

	static MapLocation l79;
	static double v79;
	static Direction d79;
	static double p79;

	static MapLocation l85;
	static double v85;
	static Direction d85;
	static double p85;

	static MapLocation l139;
	static double v139;
	static Direction d139;
	static double p139;

	static MapLocation l145;
	static double v145;
	static Direction d145;
	static double p145;

	static MapLocation l155;
	static double v155;
	static Direction d155;
	static double p155;

	static MapLocation l159;
	static double v159;
	static Direction d159;
	static double p159;

	static MapLocation l52;
	static double v52;
	static Direction d52;
	static double p52;

	static MapLocation l108;
	static double v108;
	static Direction d108;
	static double p108;

	static MapLocation l116;
	static double v116;
	static Direction d116;
	static double p116;

	static MapLocation l172;
	static double v172;
	static Direction d172;
	static double p172;

	static MapLocation l51;
	static double v51;
	static Direction d51;
	static double p51;

	static MapLocation l53;
	static double v53;
	static Direction d53;
	static double p53;

	static MapLocation l93;
	static double v93;
	static Direction d93;
	static double p93;

	static MapLocation l101;
	static double v101;
	static Direction d101;
	static double p101;

	static MapLocation l123;
	static double v123;
	static Direction d123;
	static double p123;

	static MapLocation l131;
	static double v131;
	static Direction d131;
	static double p131;

	static MapLocation l171;
	static double v171;
	static Direction d171;
	static double p171;

	static MapLocation l173;
	static double v173;
	static Direction d173;
	static double p173;

	static MapLocation l64;
	static double v64;
	static Direction d64;
	static double p64;

	static MapLocation l70;
	static double v70;
	static Direction d70;
	static double p70;

	static MapLocation l154;
	static double v154;
	static Direction d154;
	static double p154;

	static MapLocation l160;
	static double v160;
	static Direction d160;
	static double p160;

	static MapLocation l50;
	static double v50;
	static Direction d50;
	static double p50;

	static MapLocation l54;
	static double v54;
	static Direction d54;
	static double p54;

	static MapLocation l78;
	static double v78;
	static Direction d78;
	static double p78;

	static MapLocation l86;
	static double v86;
	static Direction d86;
	static double p86;

	static MapLocation l138;
	static double v138;
	static Direction d138;
	static double p138;

	static MapLocation l146;
	static double v146;
	static Direction d146;
	static double p146;

	static MapLocation l170;
	static double v170;
	static Direction d170;
	static double p170;

	static MapLocation l174;
	static double v174;
	static Direction d174;
	static double p174;

	static MapLocation l37;
	static double v37;
	static Direction d37;
	static double p37;

	static MapLocation l49;
	static double v49;
	static Direction d49;
	static double p49;

	static MapLocation l55;
	static double v55;
	static Direction d55;
	static double p55;

	static MapLocation l63;
	static double v63;
	static Direction d63;
	static double p63;

	static MapLocation l71;
	static double v71;
	static Direction d71;
	static double p71;

	static MapLocation l107;
	static double v107;
	static Direction d107;
	static double p107;

	static MapLocation l117;
	static double v117;
	static Direction d117;
	static double p117;

	static MapLocation l153;
	static double v153;
	static Direction d153;
	static double p153;

	static MapLocation l161;
	static double v161;
	static Direction d161;
	static double p161;

	static MapLocation l169;
	static double v169;
	static Direction d169;
	static double p169;

	static MapLocation l175;
	static double v175;
	static Direction d175;
	static double p175;

	static MapLocation l187;
	static double v187;
	static Direction d187;
	static double p187;

	static MapLocation l36;
	static double v36;
	static Direction d36;
	static double p36;

	static MapLocation l38;
	static double v38;
	static Direction d38;
	static double p38;

	static MapLocation l92;
	static double v92;
	static Direction d92;
	static double p92;

	static MapLocation l102;
	static double v102;
	static Direction d102;
	static double p102;

	static MapLocation l122;
	static double v122;
	static Direction d122;
	static double p122;

	static MapLocation l132;
	static double v132;
	static Direction d132;
	static double p132;

	static MapLocation l186;
	static double v186;
	static Direction d186;
	static double p186;

	static MapLocation l188;
	static double v188;
	static Direction d188;
	static double p188;

	static MapLocation l35;
	static double v35;
	static Direction d35;
	static double p35;

	static MapLocation l39;
	static double v39;
	static Direction d39;
	static double p39;

	static MapLocation l77;
	static double v77;
	static Direction d77;
	static double p77;

	static MapLocation l87;
	static double v87;
	static Direction d87;
	static double p87;

	static MapLocation l137;
	static double v137;
	static Direction d137;
	static double p137;

	static MapLocation l147;
	static double v147;
	static Direction d147;
	static double p147;

	static MapLocation l185;
	static double v185;
	static Direction d185;
	static double p185;

	static MapLocation l189;
	static double v189;
	static Direction d189;
	static double p189;

	static MapLocation l48;
	static double v48;
	static Direction d48;
	static double p48;

	static MapLocation l56;
	static double v56;
	static Direction d56;
	static double p56;

	static MapLocation l168;
	static double v168;
	static Direction d168;
	static double p168;

	static MapLocation l176;
	static double v176;
	static Direction d176;
	static double p176;

	static MapLocation l34;
	static double v34;
	static Direction d34;
	static double p34;

	static MapLocation l40;
	static double v40;
	static Direction d40;
	static double p40;

	static MapLocation l62;
	static double v62;
	static Direction d62;
	static double p62;

	static MapLocation l72;
	static double v72;
	static Direction d72;
	static double p72;

	static MapLocation l152;
	static double v152;
	static Direction d152;
	static double p152;

	static MapLocation l162;
	static double v162;
	static Direction d162;
	static double p162;

	static MapLocation l184;
	static double v184;
	static Direction d184;
	static double p184;

	static MapLocation l190;
	static double v190;
	static Direction d190;
	static double p190;


	Direction getBestDir(MapLocation target) throws GameActionException{
		l112 = rc.getLocation();
		v112 = 0;
		l97 = l112.add(Direction.SOUTH);
		v97 = 1000000;
		d97 = null;
		l111 = l112.add(Direction.WEST);
		v111 = 1000000;
		d111 = null;
		l113 = l112.add(Direction.EAST);
		v113 = 1000000;
		d113 = null;
		l127 = l112.add(Direction.NORTH);
		v127 = 1000000;
		d127 = null;
		l96 = l112.add(Direction.SOUTHWEST);
		v96 = 1000000;
		d96 = null;
		l98 = l112.add(Direction.SOUTHEAST);
		v98 = 1000000;
		d98 = null;
		l126 = l112.add(Direction.NORTHWEST);
		v126 = 1000000;
		d126 = null;
		l128 = l112.add(Direction.NORTHEAST);
		v128 = 1000000;
		d128 = null;
		l82 = l97.add(Direction.SOUTH);
		v82 = 1000000;
		d82 = null;
		l110 = l111.add(Direction.WEST);
		v110 = 1000000;
		d110 = null;
		l114 = l113.add(Direction.EAST);
		v114 = 1000000;
		d114 = null;
		l142 = l127.add(Direction.NORTH);
		v142 = 1000000;
		d142 = null;
		l81 = l97.add(Direction.SOUTHWEST);
		v81 = 1000000;
		d81 = null;
		l83 = l97.add(Direction.SOUTHEAST);
		v83 = 1000000;
		d83 = null;
		l95 = l111.add(Direction.SOUTHWEST);
		v95 = 1000000;
		d95 = null;
		l99 = l113.add(Direction.SOUTHEAST);
		v99 = 1000000;
		d99 = null;
		l125 = l111.add(Direction.NORTHWEST);
		v125 = 1000000;
		d125 = null;
		l129 = l113.add(Direction.NORTHEAST);
		v129 = 1000000;
		d129 = null;
		l141 = l127.add(Direction.NORTHWEST);
		v141 = 1000000;
		d141 = null;
		l143 = l127.add(Direction.NORTHEAST);
		v143 = 1000000;
		d143 = null;
		l80 = l96.add(Direction.SOUTHWEST);
		v80 = 1000000;
		d80 = null;
		l84 = l98.add(Direction.SOUTHEAST);
		v84 = 1000000;
		d84 = null;
		l140 = l126.add(Direction.NORTHWEST);
		v140 = 1000000;
		d140 = null;
		l144 = l128.add(Direction.NORTHEAST);
		v144 = 1000000;
		d144 = null;
		l67 = l82.add(Direction.SOUTH);
		v67 = 1000000;
		d67 = null;
		l109 = l110.add(Direction.WEST);
		v109 = 1000000;
		d109 = null;
		l115 = l114.add(Direction.EAST);
		v115 = 1000000;
		d115 = null;
		l157 = l142.add(Direction.NORTH);
		v157 = 1000000;
		d157 = null;
		l66 = l82.add(Direction.SOUTHWEST);
		v66 = 1000000;
		d66 = null;
		l68 = l82.add(Direction.SOUTHEAST);
		v68 = 1000000;
		d68 = null;
		l94 = l110.add(Direction.SOUTHWEST);
		v94 = 1000000;
		d94 = null;
		l100 = l114.add(Direction.SOUTHEAST);
		v100 = 1000000;
		d100 = null;
		l124 = l110.add(Direction.NORTHWEST);
		v124 = 1000000;
		d124 = null;
		l130 = l114.add(Direction.NORTHEAST);
		v130 = 1000000;
		d130 = null;
		l156 = l142.add(Direction.NORTHWEST);
		v156 = 1000000;
		d156 = null;
		l158 = l142.add(Direction.NORTHEAST);
		v158 = 1000000;
		d158 = null;
		l65 = l81.add(Direction.SOUTHWEST);
		v65 = 1000000;
		d65 = null;
		l69 = l83.add(Direction.SOUTHEAST);
		v69 = 1000000;
		d69 = null;
		l79 = l95.add(Direction.SOUTHWEST);
		v79 = 1000000;
		d79 = null;
		l85 = l99.add(Direction.SOUTHEAST);
		v85 = 1000000;
		d85 = null;
		l139 = l125.add(Direction.NORTHWEST);
		v139 = 1000000;
		d139 = null;
		l145 = l129.add(Direction.NORTHEAST);
		v145 = 1000000;
		d145 = null;
		l155 = l141.add(Direction.NORTHWEST);
		v155 = 1000000;
		d155 = null;
		l159 = l143.add(Direction.NORTHEAST);
		v159 = 1000000;
		d159 = null;
		l52 = l67.add(Direction.SOUTH);
		v52 = 1000000;
		d52 = null;
		l108 = l109.add(Direction.WEST);
		v108 = 1000000;
		d108 = null;
		l116 = l115.add(Direction.EAST);
		v116 = 1000000;
		d116 = null;
		l172 = l157.add(Direction.NORTH);
		v172 = 1000000;
		d172 = null;
		l51 = l67.add(Direction.SOUTHWEST);
		v51 = 1000000;
		d51 = null;
		l53 = l67.add(Direction.SOUTHEAST);
		v53 = 1000000;
		d53 = null;
		l93 = l109.add(Direction.SOUTHWEST);
		v93 = 1000000;
		d93 = null;
		l101 = l115.add(Direction.SOUTHEAST);
		v101 = 1000000;
		d101 = null;
		l123 = l109.add(Direction.NORTHWEST);
		v123 = 1000000;
		d123 = null;
		l131 = l115.add(Direction.NORTHEAST);
		v131 = 1000000;
		d131 = null;
		l171 = l157.add(Direction.NORTHWEST);
		v171 = 1000000;
		d171 = null;
		l173 = l157.add(Direction.NORTHEAST);
		v173 = 1000000;
		d173 = null;
		l64 = l80.add(Direction.SOUTHWEST);
		v64 = 1000000;
		d64 = null;
		l70 = l84.add(Direction.SOUTHEAST);
		v70 = 1000000;
		d70 = null;
		l154 = l140.add(Direction.NORTHWEST);
		v154 = 1000000;
		d154 = null;
		l160 = l144.add(Direction.NORTHEAST);
		v160 = 1000000;
		d160 = null;
		l50 = l66.add(Direction.SOUTHWEST);
		v50 = 1000000;
		d50 = null;
		l54 = l68.add(Direction.SOUTHEAST);
		v54 = 1000000;
		d54 = null;
		l78 = l94.add(Direction.SOUTHWEST);
		v78 = 1000000;
		d78 = null;
		l86 = l100.add(Direction.SOUTHEAST);
		v86 = 1000000;
		d86 = null;
		l138 = l124.add(Direction.NORTHWEST);
		v138 = 1000000;
		d138 = null;
		l146 = l130.add(Direction.NORTHEAST);
		v146 = 1000000;
		d146 = null;
		l170 = l156.add(Direction.NORTHWEST);
		v170 = 1000000;
		d170 = null;
		l174 = l158.add(Direction.NORTHEAST);
		v174 = 1000000;
		d174 = null;
		l37 = l52.add(Direction.SOUTH);
		v37 = 1000000;
		d37 = null;
		l49 = l65.add(Direction.SOUTHWEST);
		v49 = 1000000;
		d49 = null;
		l55 = l69.add(Direction.SOUTHEAST);
		v55 = 1000000;
		d55 = null;
		l63 = l79.add(Direction.SOUTHWEST);
		v63 = 1000000;
		d63 = null;
		l71 = l85.add(Direction.SOUTHEAST);
		v71 = 1000000;
		d71 = null;
		l107 = l108.add(Direction.WEST);
		v107 = 1000000;
		d107 = null;
		l117 = l116.add(Direction.EAST);
		v117 = 1000000;
		d117 = null;
		l153 = l139.add(Direction.NORTHWEST);
		v153 = 1000000;
		d153 = null;
		l161 = l145.add(Direction.NORTHEAST);
		v161 = 1000000;
		d161 = null;
		l169 = l155.add(Direction.NORTHWEST);
		v169 = 1000000;
		d169 = null;
		l175 = l159.add(Direction.NORTHEAST);
		v175 = 1000000;
		d175 = null;
		l187 = l172.add(Direction.NORTH);
		v187 = 1000000;
		d187 = null;
		l36 = l52.add(Direction.SOUTHWEST);
		v36 = 1000000;
		d36 = null;
		l38 = l52.add(Direction.SOUTHEAST);
		v38 = 1000000;
		d38 = null;
		l92 = l108.add(Direction.SOUTHWEST);
		v92 = 1000000;
		d92 = null;
		l102 = l116.add(Direction.SOUTHEAST);
		v102 = 1000000;
		d102 = null;
		l122 = l108.add(Direction.NORTHWEST);
		v122 = 1000000;
		d122 = null;
		l132 = l116.add(Direction.NORTHEAST);
		v132 = 1000000;
		d132 = null;
		l186 = l172.add(Direction.NORTHWEST);
		v186 = 1000000;
		d186 = null;
		l188 = l172.add(Direction.NORTHEAST);
		v188 = 1000000;
		d188 = null;
		l35 = l51.add(Direction.SOUTHWEST);
		v35 = 1000000;
		d35 = null;
		l39 = l53.add(Direction.SOUTHEAST);
		v39 = 1000000;
		d39 = null;
		l77 = l93.add(Direction.SOUTHWEST);
		v77 = 1000000;
		d77 = null;
		l87 = l101.add(Direction.SOUTHEAST);
		v87 = 1000000;
		d87 = null;
		l137 = l123.add(Direction.NORTHWEST);
		v137 = 1000000;
		d137 = null;
		l147 = l131.add(Direction.NORTHEAST);
		v147 = 1000000;
		d147 = null;
		l185 = l171.add(Direction.NORTHWEST);
		v185 = 1000000;
		d185 = null;
		l189 = l173.add(Direction.NORTHEAST);
		v189 = 1000000;
		d189 = null;
		l48 = l64.add(Direction.SOUTHWEST);
		v48 = 1000000;
		d48 = null;
		l56 = l70.add(Direction.SOUTHEAST);
		v56 = 1000000;
		d56 = null;
		l168 = l154.add(Direction.NORTHWEST);
		v168 = 1000000;
		d168 = null;
		l176 = l160.add(Direction.NORTHEAST);
		v176 = 1000000;
		d176 = null;
		l34 = l50.add(Direction.SOUTHWEST);
		v34 = 1000000;
		d34 = null;
		l40 = l54.add(Direction.SOUTHEAST);
		v40 = 1000000;
		d40 = null;
		l62 = l78.add(Direction.SOUTHWEST);
		v62 = 1000000;
		d62 = null;
		l72 = l86.add(Direction.SOUTHEAST);
		v72 = 1000000;
		d72 = null;
		l152 = l138.add(Direction.NORTHWEST);
		v152 = 1000000;
		d152 = null;
		l162 = l146.add(Direction.NORTHEAST);
		v162 = 1000000;
		d162 = null;
		l184 = l170.add(Direction.NORTHWEST);
		v184 = 1000000;
		d184 = null;
		l190 = l174.add(Direction.NORTHEAST);
		v190 = 1000000;
		d190 = null;
		if (rc.onTheMap(l97)) {
			if (!rc.isLocationOccupied(l97)) {
				p97 = Math.floor((1.0 + (double)rc.senseRubble(l97)/10.0)*cooldown);
				if (v97 > v112 + p97) {
					v97 = v112 + p97;
					d97 = Direction.SOUTH;
				}
			}
		}
		if (rc.onTheMap(l111)) {
			if (!rc.isLocationOccupied(l111)) {
				p111 = Math.floor((1.0 + (double)rc.senseRubble(l111)/10.0)*cooldown);
				if (v111 > v112 + p111) {
					v111 = v112 + p111;
					d111 = Direction.WEST;
				}
				if (v111 > v97 + p111) {
					v111 = v97 + p111;
					d111 = d97;
				}
			}
		}
		if (rc.onTheMap(l113)) {
			if (!rc.isLocationOccupied(l113)) {
				p113 = Math.floor((1.0 + (double)rc.senseRubble(l113)/10.0)*cooldown);
				if (v113 > v112 + p113) {
					v113 = v112 + p113;
					d113 = Direction.EAST;
				}
				if (v113 > v97 + p113) {
					v113 = v97 + p113;
					d113 = d97;
				}
			}
		}
		if (rc.onTheMap(l127)) {
			if (!rc.isLocationOccupied(l127)) {
				p127 = Math.floor((1.0 + (double)rc.senseRubble(l127)/10.0)*cooldown);
				if (v127 > v112 + p127) {
					v127 = v112 + p127;
					d127 = Direction.NORTH;
				}
				if (v127 > v111 + p127) {
					v127 = v111 + p127;
					d127 = d111;
				}
				if (v127 > v113 + p127) {
					v127 = v113 + p127;
					d127 = d113;
				}
			}
		}
		if (rc.onTheMap(l96)) {
			if (!rc.isLocationOccupied(l96)) {
				p96 = Math.floor((1.0 + (double)rc.senseRubble(l96)/10.0)*cooldown);
				if (v96 > v112 + p96) {
					v96 = v112 + p96;
					d96 = Direction.SOUTHWEST;
				}
				if (v96 > v97 + p96) {
					v96 = v97 + p96;
					d96 = d97;
				}
				if (v96 > v111 + p96) {
					v96 = v111 + p96;
					d96 = d111;
				}
			}
		}
		if (rc.onTheMap(l98)) {
			if (!rc.isLocationOccupied(l98)) {
				p98 = Math.floor((1.0 + (double)rc.senseRubble(l98)/10.0)*cooldown);
				if (v98 > v112 + p98) {
					v98 = v112 + p98;
					d98 = Direction.SOUTHEAST;
				}
				if (v98 > v97 + p98) {
					v98 = v97 + p98;
					d98 = d97;
				}
				if (v98 > v113 + p98) {
					v98 = v113 + p98;
					d98 = d113;
				}
			}
		}
		if (rc.onTheMap(l126)) {
			if (!rc.isLocationOccupied(l126)) {
				p126 = Math.floor((1.0 + (double)rc.senseRubble(l126)/10.0)*cooldown);
				if (v126 > v112 + p126) {
					v126 = v112 + p126;
					d126 = Direction.NORTHWEST;
				}
				if (v126 > v111 + p126) {
					v126 = v111 + p126;
					d126 = d111;
				}
				if (v126 > v127 + p126) {
					v126 = v127 + p126;
					d126 = d127;
				}
			}
		}
		if (rc.onTheMap(l128)) {
			if (!rc.isLocationOccupied(l128)) {
				p128 = Math.floor((1.0 + (double)rc.senseRubble(l128)/10.0)*cooldown);
				if (v128 > v112 + p128) {
					v128 = v112 + p128;
					d128 = Direction.NORTHEAST;
				}
				if (v128 > v113 + p128) {
					v128 = v113 + p128;
					d128 = d113;
				}
				if (v128 > v127 + p128) {
					v128 = v127 + p128;
					d128 = d127;
				}
			}
		}
		if (rc.onTheMap(l82)) {
				p82 = Math.floor((1.0 + (double)rc.senseRubble(l82)/10.0)*cooldown);
				if (v82 > v97 + p82) {
					v82 = v97 + p82;
					d82 = d97;
				}
				if (v82 > v96 + p82) {
					v82 = v96 + p82;
					d82 = d96;
				}
				if (v82 > v98 + p82) {
					v82 = v98 + p82;
					d82 = d98;
				}
		}
		if (rc.onTheMap(l110)) {
				p110 = Math.floor((1.0 + (double)rc.senseRubble(l110)/10.0)*cooldown);
				if (v110 > v111 + p110) {
					v110 = v111 + p110;
					d110 = d111;
				}
				if (v110 > v96 + p110) {
					v110 = v96 + p110;
					d110 = d96;
				}
				if (v110 > v126 + p110) {
					v110 = v126 + p110;
					d110 = d126;
				}
		}
		if (rc.onTheMap(l114)) {
				p114 = Math.floor((1.0 + (double)rc.senseRubble(l114)/10.0)*cooldown);
				if (v114 > v113 + p114) {
					v114 = v113 + p114;
					d114 = d113;
				}
				if (v114 > v98 + p114) {
					v114 = v98 + p114;
					d114 = d98;
				}
				if (v114 > v128 + p114) {
					v114 = v128 + p114;
					d114 = d128;
				}
		}
		if (rc.onTheMap(l142)) {
				p142 = Math.floor((1.0 + (double)rc.senseRubble(l142)/10.0)*cooldown);
				if (v142 > v127 + p142) {
					v142 = v127 + p142;
					d142 = d127;
				}
				if (v142 > v126 + p142) {
					v142 = v126 + p142;
					d142 = d126;
				}
				if (v142 > v128 + p142) {
					v142 = v128 + p142;
					d142 = d128;
				}
		}
		if (rc.onTheMap(l81)) {
				p81 = Math.floor((1.0 + (double)rc.senseRubble(l81)/10.0)*cooldown);
				if (v81 > v97 + p81) {
					v81 = v97 + p81;
					d81 = d97;
				}
				if (v81 > v96 + p81) {
					v81 = v96 + p81;
					d81 = d96;
				}
				if (v81 > v82 + p81) {
					v81 = v82 + p81;
					d81 = d82;
				}
		}
		if (rc.onTheMap(l83)) {
				p83 = Math.floor((1.0 + (double)rc.senseRubble(l83)/10.0)*cooldown);
				if (v83 > v97 + p83) {
					v83 = v97 + p83;
					d83 = d97;
				}
				if (v83 > v98 + p83) {
					v83 = v98 + p83;
					d83 = d98;
				}
				if (v83 > v82 + p83) {
					v83 = v82 + p83;
					d83 = d82;
				}
		}
		if (rc.onTheMap(l95)) {
				p95 = Math.floor((1.0 + (double)rc.senseRubble(l95)/10.0)*cooldown);
				if (v95 > v111 + p95) {
					v95 = v111 + p95;
					d95 = d111;
				}
				if (v95 > v96 + p95) {
					v95 = v96 + p95;
					d95 = d96;
				}
				if (v95 > v110 + p95) {
					v95 = v110 + p95;
					d95 = d110;
				}
				if (v95 > v81 + p95) {
					v95 = v81 + p95;
					d95 = d81;
				}
		}
		if (rc.onTheMap(l99)) {
				p99 = Math.floor((1.0 + (double)rc.senseRubble(l99)/10.0)*cooldown);
				if (v99 > v113 + p99) {
					v99 = v113 + p99;
					d99 = d113;
				}
				if (v99 > v98 + p99) {
					v99 = v98 + p99;
					d99 = d98;
				}
				if (v99 > v114 + p99) {
					v99 = v114 + p99;
					d99 = d114;
				}
				if (v99 > v83 + p99) {
					v99 = v83 + p99;
					d99 = d83;
				}
		}
		if (rc.onTheMap(l125)) {
				p125 = Math.floor((1.0 + (double)rc.senseRubble(l125)/10.0)*cooldown);
				if (v125 > v111 + p125) {
					v125 = v111 + p125;
					d125 = d111;
				}
				if (v125 > v126 + p125) {
					v125 = v126 + p125;
					d125 = d126;
				}
				if (v125 > v110 + p125) {
					v125 = v110 + p125;
					d125 = d110;
				}
		}
		if (rc.onTheMap(l129)) {
				p129 = Math.floor((1.0 + (double)rc.senseRubble(l129)/10.0)*cooldown);
				if (v129 > v113 + p129) {
					v129 = v113 + p129;
					d129 = d113;
				}
				if (v129 > v128 + p129) {
					v129 = v128 + p129;
					d129 = d128;
				}
				if (v129 > v114 + p129) {
					v129 = v114 + p129;
					d129 = d114;
				}
		}
		if (rc.onTheMap(l141)) {
				p141 = Math.floor((1.0 + (double)rc.senseRubble(l141)/10.0)*cooldown);
				if (v141 > v127 + p141) {
					v141 = v127 + p141;
					d141 = d127;
				}
				if (v141 > v126 + p141) {
					v141 = v126 + p141;
					d141 = d126;
				}
				if (v141 > v142 + p141) {
					v141 = v142 + p141;
					d141 = d142;
				}
				if (v141 > v125 + p141) {
					v141 = v125 + p141;
					d141 = d125;
				}
		}
		if (rc.onTheMap(l143)) {
				p143 = Math.floor((1.0 + (double)rc.senseRubble(l143)/10.0)*cooldown);
				if (v143 > v127 + p143) {
					v143 = v127 + p143;
					d143 = d127;
				}
				if (v143 > v128 + p143) {
					v143 = v128 + p143;
					d143 = d128;
				}
				if (v143 > v142 + p143) {
					v143 = v142 + p143;
					d143 = d142;
				}
				if (v143 > v129 + p143) {
					v143 = v129 + p143;
					d143 = d129;
				}
		}
		if (rc.onTheMap(l80)) {
				p80 = Math.floor((1.0 + (double)rc.senseRubble(l80)/10.0)*cooldown);
				if (v80 > v96 + p80) {
					v80 = v96 + p80;
					d80 = d96;
				}
				if (v80 > v81 + p80) {
					v80 = v81 + p80;
					d80 = d81;
				}
				if (v80 > v95 + p80) {
					v80 = v95 + p80;
					d80 = d95;
				}
		}
		if (rc.onTheMap(l84)) {
				p84 = Math.floor((1.0 + (double)rc.senseRubble(l84)/10.0)*cooldown);
				if (v84 > v98 + p84) {
					v84 = v98 + p84;
					d84 = d98;
				}
				if (v84 > v83 + p84) {
					v84 = v83 + p84;
					d84 = d83;
				}
				if (v84 > v99 + p84) {
					v84 = v99 + p84;
					d84 = d99;
				}
		}
		if (rc.onTheMap(l140)) {
				p140 = Math.floor((1.0 + (double)rc.senseRubble(l140)/10.0)*cooldown);
				if (v140 > v126 + p140) {
					v140 = v126 + p140;
					d140 = d126;
				}
				if (v140 > v125 + p140) {
					v140 = v125 + p140;
					d140 = d125;
				}
				if (v140 > v141 + p140) {
					v140 = v141 + p140;
					d140 = d141;
				}
		}
		if (rc.onTheMap(l144)) {
				p144 = Math.floor((1.0 + (double)rc.senseRubble(l144)/10.0)*cooldown);
				if (v144 > v128 + p144) {
					v144 = v128 + p144;
					d144 = d128;
				}
				if (v144 > v129 + p144) {
					v144 = v129 + p144;
					d144 = d129;
				}
				if (v144 > v143 + p144) {
					v144 = v143 + p144;
					d144 = d143;
				}
		}
		if (rc.onTheMap(l67)) {
				p67 = Math.floor((1.0 + (double)rc.senseRubble(l67)/10.0)*cooldown);
				if (v67 > v82 + p67) {
					v67 = v82 + p67;
					d67 = d82;
				}
				if (v67 > v81 + p67) {
					v67 = v81 + p67;
					d67 = d81;
				}
				if (v67 > v83 + p67) {
					v67 = v83 + p67;
					d67 = d83;
				}
		}
		if (rc.onTheMap(l109)) {
				p109 = Math.floor((1.0 + (double)rc.senseRubble(l109)/10.0)*cooldown);
				if (v109 > v110 + p109) {
					v109 = v110 + p109;
					d109 = d110;
				}
				if (v109 > v95 + p109) {
					v109 = v95 + p109;
					d109 = d95;
				}
				if (v109 > v125 + p109) {
					v109 = v125 + p109;
					d109 = d125;
				}
		}
		if (rc.onTheMap(l115)) {
				p115 = Math.floor((1.0 + (double)rc.senseRubble(l115)/10.0)*cooldown);
				if (v115 > v114 + p115) {
					v115 = v114 + p115;
					d115 = d114;
				}
				if (v115 > v99 + p115) {
					v115 = v99 + p115;
					d115 = d99;
				}
				if (v115 > v129 + p115) {
					v115 = v129 + p115;
					d115 = d129;
				}
		}
		if (rc.onTheMap(l157)) {
				p157 = Math.floor((1.0 + (double)rc.senseRubble(l157)/10.0)*cooldown);
				if (v157 > v142 + p157) {
					v157 = v142 + p157;
					d157 = d142;
				}
				if (v157 > v141 + p157) {
					v157 = v141 + p157;
					d157 = d141;
				}
				if (v157 > v143 + p157) {
					v157 = v143 + p157;
					d157 = d143;
				}
		}
		if (rc.onTheMap(l66)) {
				p66 = Math.floor((1.0 + (double)rc.senseRubble(l66)/10.0)*cooldown);
				if (v66 > v82 + p66) {
					v66 = v82 + p66;
					d66 = d82;
				}
				if (v66 > v81 + p66) {
					v66 = v81 + p66;
					d66 = d81;
				}
				if (v66 > v80 + p66) {
					v66 = v80 + p66;
					d66 = d80;
				}
				if (v66 > v67 + p66) {
					v66 = v67 + p66;
					d66 = d67;
				}
		}
		if (rc.onTheMap(l68)) {
				p68 = Math.floor((1.0 + (double)rc.senseRubble(l68)/10.0)*cooldown);
				if (v68 > v82 + p68) {
					v68 = v82 + p68;
					d68 = d82;
				}
				if (v68 > v83 + p68) {
					v68 = v83 + p68;
					d68 = d83;
				}
				if (v68 > v84 + p68) {
					v68 = v84 + p68;
					d68 = d84;
				}
				if (v68 > v67 + p68) {
					v68 = v67 + p68;
					d68 = d67;
				}
		}
		if (rc.onTheMap(l94)) {
				p94 = Math.floor((1.0 + (double)rc.senseRubble(l94)/10.0)*cooldown);
				if (v94 > v110 + p94) {
					v94 = v110 + p94;
					d94 = d110;
				}
				if (v94 > v95 + p94) {
					v94 = v95 + p94;
					d94 = d95;
				}
				if (v94 > v80 + p94) {
					v94 = v80 + p94;
					d94 = d80;
				}
				if (v94 > v109 + p94) {
					v94 = v109 + p94;
					d94 = d109;
				}
		}
		if (rc.onTheMap(l100)) {
				p100 = Math.floor((1.0 + (double)rc.senseRubble(l100)/10.0)*cooldown);
				if (v100 > v114 + p100) {
					v100 = v114 + p100;
					d100 = d114;
				}
				if (v100 > v99 + p100) {
					v100 = v99 + p100;
					d100 = d99;
				}
				if (v100 > v84 + p100) {
					v100 = v84 + p100;
					d100 = d84;
				}
				if (v100 > v115 + p100) {
					v100 = v115 + p100;
					d100 = d115;
				}
		}
		if (rc.onTheMap(l124)) {
				p124 = Math.floor((1.0 + (double)rc.senseRubble(l124)/10.0)*cooldown);
				if (v124 > v110 + p124) {
					v124 = v110 + p124;
					d124 = d110;
				}
				if (v124 > v125 + p124) {
					v124 = v125 + p124;
					d124 = d125;
				}
				if (v124 > v140 + p124) {
					v124 = v140 + p124;
					d124 = d140;
				}
				if (v124 > v109 + p124) {
					v124 = v109 + p124;
					d124 = d109;
				}
		}
		if (rc.onTheMap(l130)) {
				p130 = Math.floor((1.0 + (double)rc.senseRubble(l130)/10.0)*cooldown);
				if (v130 > v114 + p130) {
					v130 = v114 + p130;
					d130 = d114;
				}
				if (v130 > v129 + p130) {
					v130 = v129 + p130;
					d130 = d129;
				}
				if (v130 > v144 + p130) {
					v130 = v144 + p130;
					d130 = d144;
				}
				if (v130 > v115 + p130) {
					v130 = v115 + p130;
					d130 = d115;
				}
		}
		if (rc.onTheMap(l156)) {
				p156 = Math.floor((1.0 + (double)rc.senseRubble(l156)/10.0)*cooldown);
				if (v156 > v142 + p156) {
					v156 = v142 + p156;
					d156 = d142;
				}
				if (v156 > v141 + p156) {
					v156 = v141 + p156;
					d156 = d141;
				}
				if (v156 > v140 + p156) {
					v156 = v140 + p156;
					d156 = d140;
				}
				if (v156 > v157 + p156) {
					v156 = v157 + p156;
					d156 = d157;
				}
		}
		if (rc.onTheMap(l158)) {
				p158 = Math.floor((1.0 + (double)rc.senseRubble(l158)/10.0)*cooldown);
				if (v158 > v142 + p158) {
					v158 = v142 + p158;
					d158 = d142;
				}
				if (v158 > v143 + p158) {
					v158 = v143 + p158;
					d158 = d143;
				}
				if (v158 > v144 + p158) {
					v158 = v144 + p158;
					d158 = d144;
				}
				if (v158 > v157 + p158) {
					v158 = v157 + p158;
					d158 = d157;
				}
		}
		if (rc.onTheMap(l65)) {
				p65 = Math.floor((1.0 + (double)rc.senseRubble(l65)/10.0)*cooldown);
				if (v65 > v81 + p65) {
					v65 = v81 + p65;
					d65 = d81;
				}
				if (v65 > v80 + p65) {
					v65 = v80 + p65;
					d65 = d80;
				}
				if (v65 > v66 + p65) {
					v65 = v66 + p65;
					d65 = d66;
				}
		}
		if (rc.onTheMap(l69)) {
				p69 = Math.floor((1.0 + (double)rc.senseRubble(l69)/10.0)*cooldown);
				if (v69 > v83 + p69) {
					v69 = v83 + p69;
					d69 = d83;
				}
				if (v69 > v84 + p69) {
					v69 = v84 + p69;
					d69 = d84;
				}
				if (v69 > v68 + p69) {
					v69 = v68 + p69;
					d69 = d68;
				}
		}
		if (rc.onTheMap(l79)) {
				p79 = Math.floor((1.0 + (double)rc.senseRubble(l79)/10.0)*cooldown);
				if (v79 > v95 + p79) {
					v79 = v95 + p79;
					d79 = d95;
				}
				if (v79 > v80 + p79) {
					v79 = v80 + p79;
					d79 = d80;
				}
				if (v79 > v94 + p79) {
					v79 = v94 + p79;
					d79 = d94;
				}
				if (v79 > v65 + p79) {
					v79 = v65 + p79;
					d79 = d65;
				}
		}
		if (rc.onTheMap(l85)) {
				p85 = Math.floor((1.0 + (double)rc.senseRubble(l85)/10.0)*cooldown);
				if (v85 > v99 + p85) {
					v85 = v99 + p85;
					d85 = d99;
				}
				if (v85 > v84 + p85) {
					v85 = v84 + p85;
					d85 = d84;
				}
				if (v85 > v100 + p85) {
					v85 = v100 + p85;
					d85 = d100;
				}
				if (v85 > v69 + p85) {
					v85 = v69 + p85;
					d85 = d69;
				}
		}
		if (rc.onTheMap(l139)) {
				p139 = Math.floor((1.0 + (double)rc.senseRubble(l139)/10.0)*cooldown);
				if (v139 > v125 + p139) {
					v139 = v125 + p139;
					d139 = d125;
				}
				if (v139 > v140 + p139) {
					v139 = v140 + p139;
					d139 = d140;
				}
				if (v139 > v124 + p139) {
					v139 = v124 + p139;
					d139 = d124;
				}
		}
		if (rc.onTheMap(l145)) {
				p145 = Math.floor((1.0 + (double)rc.senseRubble(l145)/10.0)*cooldown);
				if (v145 > v129 + p145) {
					v145 = v129 + p145;
					d145 = d129;
				}
				if (v145 > v144 + p145) {
					v145 = v144 + p145;
					d145 = d144;
				}
				if (v145 > v130 + p145) {
					v145 = v130 + p145;
					d145 = d130;
				}
		}
		if (rc.onTheMap(l155)) {
				p155 = Math.floor((1.0 + (double)rc.senseRubble(l155)/10.0)*cooldown);
				if (v155 > v141 + p155) {
					v155 = v141 + p155;
					d155 = d141;
				}
				if (v155 > v140 + p155) {
					v155 = v140 + p155;
					d155 = d140;
				}
				if (v155 > v156 + p155) {
					v155 = v156 + p155;
					d155 = d156;
				}
				if (v155 > v139 + p155) {
					v155 = v139 + p155;
					d155 = d139;
				}
		}
		if (rc.onTheMap(l159)) {
				p159 = Math.floor((1.0 + (double)rc.senseRubble(l159)/10.0)*cooldown);
				if (v159 > v143 + p159) {
					v159 = v143 + p159;
					d159 = d143;
				}
				if (v159 > v144 + p159) {
					v159 = v144 + p159;
					d159 = d144;
				}
				if (v159 > v158 + p159) {
					v159 = v158 + p159;
					d159 = d158;
				}
				if (v159 > v145 + p159) {
					v159 = v145 + p159;
					d159 = d145;
				}
		}
		if (rc.onTheMap(l52)) {
				p52 = Math.floor((1.0 + (double)rc.senseRubble(l52)/10.0)*cooldown);
				if (v52 > v67 + p52) {
					v52 = v67 + p52;
					d52 = d67;
				}
				if (v52 > v66 + p52) {
					v52 = v66 + p52;
					d52 = d66;
				}
				if (v52 > v68 + p52) {
					v52 = v68 + p52;
					d52 = d68;
				}
		}
		if (rc.onTheMap(l108)) {
				p108 = Math.floor((1.0 + (double)rc.senseRubble(l108)/10.0)*cooldown);
				if (v108 > v109 + p108) {
					v108 = v109 + p108;
					d108 = d109;
				}
				if (v108 > v94 + p108) {
					v108 = v94 + p108;
					d108 = d94;
				}
				if (v108 > v124 + p108) {
					v108 = v124 + p108;
					d108 = d124;
				}
		}
		if (rc.onTheMap(l116)) {
				p116 = Math.floor((1.0 + (double)rc.senseRubble(l116)/10.0)*cooldown);
				if (v116 > v115 + p116) {
					v116 = v115 + p116;
					d116 = d115;
				}
				if (v116 > v100 + p116) {
					v116 = v100 + p116;
					d116 = d100;
				}
				if (v116 > v130 + p116) {
					v116 = v130 + p116;
					d116 = d130;
				}
		}
		if (rc.onTheMap(l172)) {
				p172 = Math.floor((1.0 + (double)rc.senseRubble(l172)/10.0)*cooldown);
				if (v172 > v157 + p172) {
					v172 = v157 + p172;
					d172 = d157;
				}
				if (v172 > v156 + p172) {
					v172 = v156 + p172;
					d172 = d156;
				}
				if (v172 > v158 + p172) {
					v172 = v158 + p172;
					d172 = d158;
				}
		}
		if (rc.onTheMap(l51)) {
				p51 = Math.floor((1.0 + (double)rc.senseRubble(l51)/10.0)*cooldown);
				if (v51 > v67 + p51) {
					v51 = v67 + p51;
					d51 = d67;
				}
				if (v51 > v66 + p51) {
					v51 = v66 + p51;
					d51 = d66;
				}
				if (v51 > v65 + p51) {
					v51 = v65 + p51;
					d51 = d65;
				}
				if (v51 > v52 + p51) {
					v51 = v52 + p51;
					d51 = d52;
				}
		}
		if (rc.onTheMap(l53)) {
				p53 = Math.floor((1.0 + (double)rc.senseRubble(l53)/10.0)*cooldown);
				if (v53 > v67 + p53) {
					v53 = v67 + p53;
					d53 = d67;
				}
				if (v53 > v68 + p53) {
					v53 = v68 + p53;
					d53 = d68;
				}
				if (v53 > v69 + p53) {
					v53 = v69 + p53;
					d53 = d69;
				}
				if (v53 > v52 + p53) {
					v53 = v52 + p53;
					d53 = d52;
				}
		}
		if (rc.onTheMap(l93)) {
				p93 = Math.floor((1.0 + (double)rc.senseRubble(l93)/10.0)*cooldown);
				if (v93 > v109 + p93) {
					v93 = v109 + p93;
					d93 = d109;
				}
				if (v93 > v94 + p93) {
					v93 = v94 + p93;
					d93 = d94;
				}
				if (v93 > v79 + p93) {
					v93 = v79 + p93;
					d93 = d79;
				}
				if (v93 > v108 + p93) {
					v93 = v108 + p93;
					d93 = d108;
				}
		}
		if (rc.onTheMap(l101)) {
				p101 = Math.floor((1.0 + (double)rc.senseRubble(l101)/10.0)*cooldown);
				if (v101 > v115 + p101) {
					v101 = v115 + p101;
					d101 = d115;
				}
				if (v101 > v100 + p101) {
					v101 = v100 + p101;
					d101 = d100;
				}
				if (v101 > v85 + p101) {
					v101 = v85 + p101;
					d101 = d85;
				}
				if (v101 > v116 + p101) {
					v101 = v116 + p101;
					d101 = d116;
				}
		}
		if (rc.onTheMap(l123)) {
				p123 = Math.floor((1.0 + (double)rc.senseRubble(l123)/10.0)*cooldown);
				if (v123 > v109 + p123) {
					v123 = v109 + p123;
					d123 = d109;
				}
				if (v123 > v124 + p123) {
					v123 = v124 + p123;
					d123 = d124;
				}
				if (v123 > v139 + p123) {
					v123 = v139 + p123;
					d123 = d139;
				}
				if (v123 > v108 + p123) {
					v123 = v108 + p123;
					d123 = d108;
				}
		}
		if (rc.onTheMap(l131)) {
				p131 = Math.floor((1.0 + (double)rc.senseRubble(l131)/10.0)*cooldown);
				if (v131 > v115 + p131) {
					v131 = v115 + p131;
					d131 = d115;
				}
				if (v131 > v130 + p131) {
					v131 = v130 + p131;
					d131 = d130;
				}
				if (v131 > v145 + p131) {
					v131 = v145 + p131;
					d131 = d145;
				}
				if (v131 > v116 + p131) {
					v131 = v116 + p131;
					d131 = d116;
				}
		}
		if (rc.onTheMap(l171)) {
				p171 = Math.floor((1.0 + (double)rc.senseRubble(l171)/10.0)*cooldown);
				if (v171 > v157 + p171) {
					v171 = v157 + p171;
					d171 = d157;
				}
				if (v171 > v156 + p171) {
					v171 = v156 + p171;
					d171 = d156;
				}
				if (v171 > v155 + p171) {
					v171 = v155 + p171;
					d171 = d155;
				}
				if (v171 > v172 + p171) {
					v171 = v172 + p171;
					d171 = d172;
				}
		}
		if (rc.onTheMap(l173)) {
				p173 = Math.floor((1.0 + (double)rc.senseRubble(l173)/10.0)*cooldown);
				if (v173 > v157 + p173) {
					v173 = v157 + p173;
					d173 = d157;
				}
				if (v173 > v158 + p173) {
					v173 = v158 + p173;
					d173 = d158;
				}
				if (v173 > v159 + p173) {
					v173 = v159 + p173;
					d173 = d159;
				}
				if (v173 > v172 + p173) {
					v173 = v172 + p173;
					d173 = d172;
				}
		}
		if (rc.onTheMap(l64)) {
				p64 = Math.floor((1.0 + (double)rc.senseRubble(l64)/10.0)*cooldown);
				if (v64 > v80 + p64) {
					v64 = v80 + p64;
					d64 = d80;
				}
				if (v64 > v65 + p64) {
					v64 = v65 + p64;
					d64 = d65;
				}
				if (v64 > v79 + p64) {
					v64 = v79 + p64;
					d64 = d79;
				}
		}
		if (rc.onTheMap(l70)) {
				p70 = Math.floor((1.0 + (double)rc.senseRubble(l70)/10.0)*cooldown);
				if (v70 > v84 + p70) {
					v70 = v84 + p70;
					d70 = d84;
				}
				if (v70 > v69 + p70) {
					v70 = v69 + p70;
					d70 = d69;
				}
				if (v70 > v85 + p70) {
					v70 = v85 + p70;
					d70 = d85;
				}
		}
		if (rc.onTheMap(l154)) {
				p154 = Math.floor((1.0 + (double)rc.senseRubble(l154)/10.0)*cooldown);
				if (v154 > v140 + p154) {
					v154 = v140 + p154;
					d154 = d140;
				}
				if (v154 > v139 + p154) {
					v154 = v139 + p154;
					d154 = d139;
				}
				if (v154 > v155 + p154) {
					v154 = v155 + p154;
					d154 = d155;
				}
		}
		if (rc.onTheMap(l160)) {
				p160 = Math.floor((1.0 + (double)rc.senseRubble(l160)/10.0)*cooldown);
				if (v160 > v144 + p160) {
					v160 = v144 + p160;
					d160 = d144;
				}
				if (v160 > v145 + p160) {
					v160 = v145 + p160;
					d160 = d145;
				}
				if (v160 > v159 + p160) {
					v160 = v159 + p160;
					d160 = d159;
				}
		}
		if (rc.onTheMap(l50)) {
				p50 = Math.floor((1.0 + (double)rc.senseRubble(l50)/10.0)*cooldown);
				if (v50 > v66 + p50) {
					v50 = v66 + p50;
					d50 = d66;
				}
				if (v50 > v65 + p50) {
					v50 = v65 + p50;
					d50 = d65;
				}
				if (v50 > v51 + p50) {
					v50 = v51 + p50;
					d50 = d51;
				}
				if (v50 > v64 + p50) {
					v50 = v64 + p50;
					d50 = d64;
				}
		}
		if (rc.onTheMap(l54)) {
				p54 = Math.floor((1.0 + (double)rc.senseRubble(l54)/10.0)*cooldown);
				if (v54 > v68 + p54) {
					v54 = v68 + p54;
					d54 = d68;
				}
				if (v54 > v69 + p54) {
					v54 = v69 + p54;
					d54 = d69;
				}
				if (v54 > v53 + p54) {
					v54 = v53 + p54;
					d54 = d53;
				}
				if (v54 > v70 + p54) {
					v54 = v70 + p54;
					d54 = d70;
				}
		}
		if (rc.onTheMap(l78)) {
				p78 = Math.floor((1.0 + (double)rc.senseRubble(l78)/10.0)*cooldown);
				if (v78 > v94 + p78) {
					v78 = v94 + p78;
					d78 = d94;
				}
				if (v78 > v79 + p78) {
					v78 = v79 + p78;
					d78 = d79;
				}
				if (v78 > v93 + p78) {
					v78 = v93 + p78;
					d78 = d93;
				}
				if (v78 > v64 + p78) {
					v78 = v64 + p78;
					d78 = d64;
				}
		}
		if (rc.onTheMap(l86)) {
				p86 = Math.floor((1.0 + (double)rc.senseRubble(l86)/10.0)*cooldown);
				if (v86 > v100 + p86) {
					v86 = v100 + p86;
					d86 = d100;
				}
				if (v86 > v85 + p86) {
					v86 = v85 + p86;
					d86 = d85;
				}
				if (v86 > v101 + p86) {
					v86 = v101 + p86;
					d86 = d101;
				}
				if (v86 > v70 + p86) {
					v86 = v70 + p86;
					d86 = d70;
				}
		}
		if (rc.onTheMap(l138)) {
				p138 = Math.floor((1.0 + (double)rc.senseRubble(l138)/10.0)*cooldown);
				if (v138 > v124 + p138) {
					v138 = v124 + p138;
					d138 = d124;
				}
				if (v138 > v139 + p138) {
					v138 = v139 + p138;
					d138 = d139;
				}
				if (v138 > v123 + p138) {
					v138 = v123 + p138;
					d138 = d123;
				}
				if (v138 > v154 + p138) {
					v138 = v154 + p138;
					d138 = d154;
				}
		}
		if (rc.onTheMap(l146)) {
				p146 = Math.floor((1.0 + (double)rc.senseRubble(l146)/10.0)*cooldown);
				if (v146 > v130 + p146) {
					v146 = v130 + p146;
					d146 = d130;
				}
				if (v146 > v145 + p146) {
					v146 = v145 + p146;
					d146 = d145;
				}
				if (v146 > v131 + p146) {
					v146 = v131 + p146;
					d146 = d131;
				}
				if (v146 > v160 + p146) {
					v146 = v160 + p146;
					d146 = d160;
				}
		}
		if (rc.onTheMap(l170)) {
				p170 = Math.floor((1.0 + (double)rc.senseRubble(l170)/10.0)*cooldown);
				if (v170 > v156 + p170) {
					v170 = v156 + p170;
					d170 = d156;
				}
				if (v170 > v155 + p170) {
					v170 = v155 + p170;
					d170 = d155;
				}
				if (v170 > v171 + p170) {
					v170 = v171 + p170;
					d170 = d171;
				}
				if (v170 > v154 + p170) {
					v170 = v154 + p170;
					d170 = d154;
				}
		}
		if (rc.onTheMap(l174)) {
				p174 = Math.floor((1.0 + (double)rc.senseRubble(l174)/10.0)*cooldown);
				if (v174 > v158 + p174) {
					v174 = v158 + p174;
					d174 = d158;
				}
				if (v174 > v159 + p174) {
					v174 = v159 + p174;
					d174 = d159;
				}
				if (v174 > v173 + p174) {
					v174 = v173 + p174;
					d174 = d173;
				}
				if (v174 > v160 + p174) {
					v174 = v160 + p174;
					d174 = d160;
				}
		}
		if (rc.onTheMap(l37)) {
				p37 = Math.floor((1.0 + (double)rc.senseRubble(l37)/10.0)*cooldown);
				if (v37 > v52 + p37) {
					v37 = v52 + p37;
					d37 = d52;
				}
				if (v37 > v51 + p37) {
					v37 = v51 + p37;
					d37 = d51;
				}
				if (v37 > v53 + p37) {
					v37 = v53 + p37;
					d37 = d53;
				}
		}
		if (rc.onTheMap(l49)) {
				p49 = Math.floor((1.0 + (double)rc.senseRubble(l49)/10.0)*cooldown);
				if (v49 > v65 + p49) {
					v49 = v65 + p49;
					d49 = d65;
				}
				if (v49 > v64 + p49) {
					v49 = v64 + p49;
					d49 = d64;
				}
				if (v49 > v50 + p49) {
					v49 = v50 + p49;
					d49 = d50;
				}
		}
		if (rc.onTheMap(l55)) {
				p55 = Math.floor((1.0 + (double)rc.senseRubble(l55)/10.0)*cooldown);
				if (v55 > v69 + p55) {
					v55 = v69 + p55;
					d55 = d69;
				}
				if (v55 > v70 + p55) {
					v55 = v70 + p55;
					d55 = d70;
				}
				if (v55 > v54 + p55) {
					v55 = v54 + p55;
					d55 = d54;
				}
		}
		if (rc.onTheMap(l63)) {
				p63 = Math.floor((1.0 + (double)rc.senseRubble(l63)/10.0)*cooldown);
				if (v63 > v79 + p63) {
					v63 = v79 + p63;
					d63 = d79;
				}
				if (v63 > v64 + p63) {
					v63 = v64 + p63;
					d63 = d64;
				}
				if (v63 > v78 + p63) {
					v63 = v78 + p63;
					d63 = d78;
				}
				if (v63 > v49 + p63) {
					v63 = v49 + p63;
					d63 = d49;
				}
		}
		if (rc.onTheMap(l71)) {
				p71 = Math.floor((1.0 + (double)rc.senseRubble(l71)/10.0)*cooldown);
				if (v71 > v85 + p71) {
					v71 = v85 + p71;
					d71 = d85;
				}
				if (v71 > v70 + p71) {
					v71 = v70 + p71;
					d71 = d70;
				}
				if (v71 > v86 + p71) {
					v71 = v86 + p71;
					d71 = d86;
				}
				if (v71 > v55 + p71) {
					v71 = v55 + p71;
					d71 = d55;
				}
		}
		if (rc.onTheMap(l107)) {
				p107 = Math.floor((1.0 + (double)rc.senseRubble(l107)/10.0)*cooldown);
				if (v107 > v108 + p107) {
					v107 = v108 + p107;
					d107 = d108;
				}
				if (v107 > v93 + p107) {
					v107 = v93 + p107;
					d107 = d93;
				}
				if (v107 > v123 + p107) {
					v107 = v123 + p107;
					d107 = d123;
				}
		}
		if (rc.onTheMap(l117)) {
				p117 = Math.floor((1.0 + (double)rc.senseRubble(l117)/10.0)*cooldown);
				if (v117 > v116 + p117) {
					v117 = v116 + p117;
					d117 = d116;
				}
				if (v117 > v101 + p117) {
					v117 = v101 + p117;
					d117 = d101;
				}
				if (v117 > v131 + p117) {
					v117 = v131 + p117;
					d117 = d131;
				}
		}
		if (rc.onTheMap(l153)) {
				p153 = Math.floor((1.0 + (double)rc.senseRubble(l153)/10.0)*cooldown);
				if (v153 > v139 + p153) {
					v153 = v139 + p153;
					d153 = d139;
				}
				if (v153 > v154 + p153) {
					v153 = v154 + p153;
					d153 = d154;
				}
				if (v153 > v138 + p153) {
					v153 = v138 + p153;
					d153 = d138;
				}
		}
		if (rc.onTheMap(l161)) {
				p161 = Math.floor((1.0 + (double)rc.senseRubble(l161)/10.0)*cooldown);
				if (v161 > v145 + p161) {
					v161 = v145 + p161;
					d161 = d145;
				}
				if (v161 > v160 + p161) {
					v161 = v160 + p161;
					d161 = d160;
				}
				if (v161 > v146 + p161) {
					v161 = v146 + p161;
					d161 = d146;
				}
		}
		if (rc.onTheMap(l169)) {
				p169 = Math.floor((1.0 + (double)rc.senseRubble(l169)/10.0)*cooldown);
				if (v169 > v155 + p169) {
					v169 = v155 + p169;
					d169 = d155;
				}
				if (v169 > v154 + p169) {
					v169 = v154 + p169;
					d169 = d154;
				}
				if (v169 > v170 + p169) {
					v169 = v170 + p169;
					d169 = d170;
				}
				if (v169 > v153 + p169) {
					v169 = v153 + p169;
					d169 = d153;
				}
		}
		if (rc.onTheMap(l175)) {
				p175 = Math.floor((1.0 + (double)rc.senseRubble(l175)/10.0)*cooldown);
				if (v175 > v159 + p175) {
					v175 = v159 + p175;
					d175 = d159;
				}
				if (v175 > v160 + p175) {
					v175 = v160 + p175;
					d175 = d160;
				}
				if (v175 > v174 + p175) {
					v175 = v174 + p175;
					d175 = d174;
				}
				if (v175 > v161 + p175) {
					v175 = v161 + p175;
					d175 = d161;
				}
		}
		if (rc.onTheMap(l187)) {
				p187 = Math.floor((1.0 + (double)rc.senseRubble(l187)/10.0)*cooldown);
				if (v187 > v172 + p187) {
					v187 = v172 + p187;
					d187 = d172;
				}
				if (v187 > v171 + p187) {
					v187 = v171 + p187;
					d187 = d171;
				}
				if (v187 > v173 + p187) {
					v187 = v173 + p187;
					d187 = d173;
				}
		}
		if (rc.onTheMap(l36)) {
				p36 = Math.floor((1.0 + (double)rc.senseRubble(l36)/10.0)*cooldown);
				if (v36 > v52 + p36) {
					v36 = v52 + p36;
					d36 = d52;
				}
				if (v36 > v51 + p36) {
					v36 = v51 + p36;
					d36 = d51;
				}
				if (v36 > v50 + p36) {
					v36 = v50 + p36;
					d36 = d50;
				}
				if (v36 > v37 + p36) {
					v36 = v37 + p36;
					d36 = d37;
				}
		}
		if (rc.onTheMap(l38)) {
				p38 = Math.floor((1.0 + (double)rc.senseRubble(l38)/10.0)*cooldown);
				if (v38 > v52 + p38) {
					v38 = v52 + p38;
					d38 = d52;
				}
				if (v38 > v53 + p38) {
					v38 = v53 + p38;
					d38 = d53;
				}
				if (v38 > v54 + p38) {
					v38 = v54 + p38;
					d38 = d54;
				}
				if (v38 > v37 + p38) {
					v38 = v37 + p38;
					d38 = d37;
				}
		}
		if (rc.onTheMap(l92)) {
				p92 = Math.floor((1.0 + (double)rc.senseRubble(l92)/10.0)*cooldown);
				if (v92 > v108 + p92) {
					v92 = v108 + p92;
					d92 = d108;
				}
				if (v92 > v93 + p92) {
					v92 = v93 + p92;
					d92 = d93;
				}
				if (v92 > v78 + p92) {
					v92 = v78 + p92;
					d92 = d78;
				}
				if (v92 > v107 + p92) {
					v92 = v107 + p92;
					d92 = d107;
				}
		}
		if (rc.onTheMap(l102)) {
				p102 = Math.floor((1.0 + (double)rc.senseRubble(l102)/10.0)*cooldown);
				if (v102 > v116 + p102) {
					v102 = v116 + p102;
					d102 = d116;
				}
				if (v102 > v101 + p102) {
					v102 = v101 + p102;
					d102 = d101;
				}
				if (v102 > v86 + p102) {
					v102 = v86 + p102;
					d102 = d86;
				}
				if (v102 > v117 + p102) {
					v102 = v117 + p102;
					d102 = d117;
				}
		}
		if (rc.onTheMap(l122)) {
				p122 = Math.floor((1.0 + (double)rc.senseRubble(l122)/10.0)*cooldown);
				if (v122 > v108 + p122) {
					v122 = v108 + p122;
					d122 = d108;
				}
				if (v122 > v123 + p122) {
					v122 = v123 + p122;
					d122 = d123;
				}
				if (v122 > v138 + p122) {
					v122 = v138 + p122;
					d122 = d138;
				}
				if (v122 > v107 + p122) {
					v122 = v107 + p122;
					d122 = d107;
				}
		}
		if (rc.onTheMap(l132)) {
				p132 = Math.floor((1.0 + (double)rc.senseRubble(l132)/10.0)*cooldown);
				if (v132 > v116 + p132) {
					v132 = v116 + p132;
					d132 = d116;
				}
				if (v132 > v131 + p132) {
					v132 = v131 + p132;
					d132 = d131;
				}
				if (v132 > v146 + p132) {
					v132 = v146 + p132;
					d132 = d146;
				}
				if (v132 > v117 + p132) {
					v132 = v117 + p132;
					d132 = d117;
				}
		}
		if (rc.onTheMap(l186)) {
				p186 = Math.floor((1.0 + (double)rc.senseRubble(l186)/10.0)*cooldown);
				if (v186 > v172 + p186) {
					v186 = v172 + p186;
					d186 = d172;
				}
				if (v186 > v171 + p186) {
					v186 = v171 + p186;
					d186 = d171;
				}
				if (v186 > v170 + p186) {
					v186 = v170 + p186;
					d186 = d170;
				}
				if (v186 > v187 + p186) {
					v186 = v187 + p186;
					d186 = d187;
				}
		}
		if (rc.onTheMap(l188)) {
				p188 = Math.floor((1.0 + (double)rc.senseRubble(l188)/10.0)*cooldown);
				if (v188 > v172 + p188) {
					v188 = v172 + p188;
					d188 = d172;
				}
				if (v188 > v173 + p188) {
					v188 = v173 + p188;
					d188 = d173;
				}
				if (v188 > v174 + p188) {
					v188 = v174 + p188;
					d188 = d174;
				}
				if (v188 > v187 + p188) {
					v188 = v187 + p188;
					d188 = d187;
				}
		}
		if (rc.onTheMap(l35)) {
				p35 = Math.floor((1.0 + (double)rc.senseRubble(l35)/10.0)*cooldown);
				if (v35 > v51 + p35) {
					v35 = v51 + p35;
					d35 = d51;
				}
				if (v35 > v50 + p35) {
					v35 = v50 + p35;
					d35 = d50;
				}
				if (v35 > v49 + p35) {
					v35 = v49 + p35;
					d35 = d49;
				}
				if (v35 > v36 + p35) {
					v35 = v36 + p35;
					d35 = d36;
				}
		}
		if (rc.onTheMap(l39)) {
				p39 = Math.floor((1.0 + (double)rc.senseRubble(l39)/10.0)*cooldown);
				if (v39 > v53 + p39) {
					v39 = v53 + p39;
					d39 = d53;
				}
				if (v39 > v54 + p39) {
					v39 = v54 + p39;
					d39 = d54;
				}
				if (v39 > v55 + p39) {
					v39 = v55 + p39;
					d39 = d55;
				}
				if (v39 > v38 + p39) {
					v39 = v38 + p39;
					d39 = d38;
				}
		}
		if (rc.onTheMap(l77)) {
				p77 = Math.floor((1.0 + (double)rc.senseRubble(l77)/10.0)*cooldown);
				if (v77 > v93 + p77) {
					v77 = v93 + p77;
					d77 = d93;
				}
				if (v77 > v78 + p77) {
					v77 = v78 + p77;
					d77 = d78;
				}
				if (v77 > v63 + p77) {
					v77 = v63 + p77;
					d77 = d63;
				}
				if (v77 > v92 + p77) {
					v77 = v92 + p77;
					d77 = d92;
				}
		}
		if (rc.onTheMap(l87)) {
				p87 = Math.floor((1.0 + (double)rc.senseRubble(l87)/10.0)*cooldown);
				if (v87 > v101 + p87) {
					v87 = v101 + p87;
					d87 = d101;
				}
				if (v87 > v86 + p87) {
					v87 = v86 + p87;
					d87 = d86;
				}
				if (v87 > v71 + p87) {
					v87 = v71 + p87;
					d87 = d71;
				}
				if (v87 > v102 + p87) {
					v87 = v102 + p87;
					d87 = d102;
				}
		}
		if (rc.onTheMap(l137)) {
				p137 = Math.floor((1.0 + (double)rc.senseRubble(l137)/10.0)*cooldown);
				if (v137 > v123 + p137) {
					v137 = v123 + p137;
					d137 = d123;
				}
				if (v137 > v138 + p137) {
					v137 = v138 + p137;
					d137 = d138;
				}
				if (v137 > v153 + p137) {
					v137 = v153 + p137;
					d137 = d153;
				}
				if (v137 > v122 + p137) {
					v137 = v122 + p137;
					d137 = d122;
				}
		}
		if (rc.onTheMap(l147)) {
				p147 = Math.floor((1.0 + (double)rc.senseRubble(l147)/10.0)*cooldown);
				if (v147 > v131 + p147) {
					v147 = v131 + p147;
					d147 = d131;
				}
				if (v147 > v146 + p147) {
					v147 = v146 + p147;
					d147 = d146;
				}
				if (v147 > v161 + p147) {
					v147 = v161 + p147;
					d147 = d161;
				}
				if (v147 > v132 + p147) {
					v147 = v132 + p147;
					d147 = d132;
				}
		}
		if (rc.onTheMap(l185)) {
				p185 = Math.floor((1.0 + (double)rc.senseRubble(l185)/10.0)*cooldown);
				if (v185 > v171 + p185) {
					v185 = v171 + p185;
					d185 = d171;
				}
				if (v185 > v170 + p185) {
					v185 = v170 + p185;
					d185 = d170;
				}
				if (v185 > v169 + p185) {
					v185 = v169 + p185;
					d185 = d169;
				}
				if (v185 > v186 + p185) {
					v185 = v186 + p185;
					d185 = d186;
				}
		}
		if (rc.onTheMap(l189)) {
				p189 = Math.floor((1.0 + (double)rc.senseRubble(l189)/10.0)*cooldown);
				if (v189 > v173 + p189) {
					v189 = v173 + p189;
					d189 = d173;
				}
				if (v189 > v174 + p189) {
					v189 = v174 + p189;
					d189 = d174;
				}
				if (v189 > v175 + p189) {
					v189 = v175 + p189;
					d189 = d175;
				}
				if (v189 > v188 + p189) {
					v189 = v188 + p189;
					d189 = d188;
				}
		}
		if (rc.onTheMap(l48)) {
				p48 = Math.floor((1.0 + (double)rc.senseRubble(l48)/10.0)*cooldown);
				if (v48 > v64 + p48) {
					v48 = v64 + p48;
					d48 = d64;
				}
				if (v48 > v49 + p48) {
					v48 = v49 + p48;
					d48 = d49;
				}
				if (v48 > v63 + p48) {
					v48 = v63 + p48;
					d48 = d63;
				}
		}
		if (rc.onTheMap(l56)) {
				p56 = Math.floor((1.0 + (double)rc.senseRubble(l56)/10.0)*cooldown);
				if (v56 > v70 + p56) {
					v56 = v70 + p56;
					d56 = d70;
				}
				if (v56 > v55 + p56) {
					v56 = v55 + p56;
					d56 = d55;
				}
				if (v56 > v71 + p56) {
					v56 = v71 + p56;
					d56 = d71;
				}
		}
		if (rc.onTheMap(l168)) {
				p168 = Math.floor((1.0 + (double)rc.senseRubble(l168)/10.0)*cooldown);
				if (v168 > v154 + p168) {
					v168 = v154 + p168;
					d168 = d154;
				}
				if (v168 > v153 + p168) {
					v168 = v153 + p168;
					d168 = d153;
				}
				if (v168 > v169 + p168) {
					v168 = v169 + p168;
					d168 = d169;
				}
		}
		if (rc.onTheMap(l176)) {
				p176 = Math.floor((1.0 + (double)rc.senseRubble(l176)/10.0)*cooldown);
				if (v176 > v160 + p176) {
					v176 = v160 + p176;
					d176 = d160;
				}
				if (v176 > v161 + p176) {
					v176 = v161 + p176;
					d176 = d161;
				}
				if (v176 > v175 + p176) {
					v176 = v175 + p176;
					d176 = d175;
				}
		}
		if (rc.onTheMap(l34)) {
				p34 = Math.floor((1.0 + (double)rc.senseRubble(l34)/10.0)*cooldown);
				if (v34 > v50 + p34) {
					v34 = v50 + p34;
					d34 = d50;
				}
				if (v34 > v49 + p34) {
					v34 = v49 + p34;
					d34 = d49;
				}
				if (v34 > v35 + p34) {
					v34 = v35 + p34;
					d34 = d35;
				}
				if (v34 > v48 + p34) {
					v34 = v48 + p34;
					d34 = d48;
				}
		}
		if (rc.onTheMap(l40)) {
				p40 = Math.floor((1.0 + (double)rc.senseRubble(l40)/10.0)*cooldown);
				if (v40 > v54 + p40) {
					v40 = v54 + p40;
					d40 = d54;
				}
				if (v40 > v55 + p40) {
					v40 = v55 + p40;
					d40 = d55;
				}
				if (v40 > v39 + p40) {
					v40 = v39 + p40;
					d40 = d39;
				}
				if (v40 > v56 + p40) {
					v40 = v56 + p40;
					d40 = d56;
				}
		}
		if (rc.onTheMap(l62)) {
				p62 = Math.floor((1.0 + (double)rc.senseRubble(l62)/10.0)*cooldown);
				if (v62 > v78 + p62) {
					v62 = v78 + p62;
					d62 = d78;
				}
				if (v62 > v63 + p62) {
					v62 = v63 + p62;
					d62 = d63;
				}
				if (v62 > v77 + p62) {
					v62 = v77 + p62;
					d62 = d77;
				}
				if (v62 > v48 + p62) {
					v62 = v48 + p62;
					d62 = d48;
				}
		}
		if (rc.onTheMap(l72)) {
				p72 = Math.floor((1.0 + (double)rc.senseRubble(l72)/10.0)*cooldown);
				if (v72 > v86 + p72) {
					v72 = v86 + p72;
					d72 = d86;
				}
				if (v72 > v71 + p72) {
					v72 = v71 + p72;
					d72 = d71;
				}
				if (v72 > v87 + p72) {
					v72 = v87 + p72;
					d72 = d87;
				}
				if (v72 > v56 + p72) {
					v72 = v56 + p72;
					d72 = d56;
				}
		}
		if (rc.onTheMap(l152)) {
				p152 = Math.floor((1.0 + (double)rc.senseRubble(l152)/10.0)*cooldown);
				if (v152 > v138 + p152) {
					v152 = v138 + p152;
					d152 = d138;
				}
				if (v152 > v153 + p152) {
					v152 = v153 + p152;
					d152 = d153;
				}
				if (v152 > v137 + p152) {
					v152 = v137 + p152;
					d152 = d137;
				}
				if (v152 > v168 + p152) {
					v152 = v168 + p152;
					d152 = d168;
				}
		}
		if (rc.onTheMap(l162)) {
				p162 = Math.floor((1.0 + (double)rc.senseRubble(l162)/10.0)*cooldown);
				if (v162 > v146 + p162) {
					v162 = v146 + p162;
					d162 = d146;
				}
				if (v162 > v161 + p162) {
					v162 = v161 + p162;
					d162 = d161;
				}
				if (v162 > v147 + p162) {
					v162 = v147 + p162;
					d162 = d147;
				}
				if (v162 > v176 + p162) {
					v162 = v176 + p162;
					d162 = d176;
				}
		}
		if (rc.onTheMap(l184)) {
				p184 = Math.floor((1.0 + (double)rc.senseRubble(l184)/10.0)*cooldown);
				if (v184 > v170 + p184) {
					v184 = v170 + p184;
					d184 = d170;
				}
				if (v184 > v169 + p184) {
					v184 = v169 + p184;
					d184 = d169;
				}
				if (v184 > v185 + p184) {
					v184 = v185 + p184;
					d184 = d185;
				}
				if (v184 > v168 + p184) {
					v184 = v168 + p184;
					d184 = d168;
				}
		}
		if (rc.onTheMap(l190)) {
				p190 = Math.floor((1.0 + (double)rc.senseRubble(l190)/10.0)*cooldown);
				if (v190 > v174 + p190) {
					v190 = v174 + p190;
					d190 = d174;
				}
				if (v190 > v175 + p190) {
					v190 = v175 + p190;
					d190 = d175;
				}
				if (v190 > v189 + p190) {
					v190 = v189 + p190;
					d190 = d189;
				}
				if (v190 > v176 + p190) {
					v190 = v176 + p190;
					d190 = d176;
				}
		}
		int dx = target.x - l84.x;
		int dy = target.y - l84.y;
		switch (dx) {
			case -5:
				switch (dy) {
					case -3:
						return d62;
					case -2:
						return d77;
					case -1:
						return d92;
					case 0:
						return d107;
					case 1:
						return d122;
					case 2:
						return d137;
					case 3:
						return d152;
				}
				break;
			case -4:
				switch (dy) {
					case -4:
						return d48;
					case -3:
						return d63;
					case -2:
						return d78;
					case -1:
						return d93;
					case 0:
						return d108;
					case 1:
						return d123;
					case 2:
						return d138;
					case 3:
						return d153;
					case 4:
						return d168;
				}
				break;
			case -3:
				switch (dy) {
					case -5:
						return d34;
					case -4:
						return d49;
					case -3:
						return d64;
					case -2:
						return d79;
					case -1:
						return d94;
					case 0:
						return d109;
					case 1:
						return d124;
					case 2:
						return d139;
					case 3:
						return d154;
					case 4:
						return d169;
					case 5:
						return d184;
				}
				break;
			case -2:
				switch (dy) {
					case -5:
						return d35;
					case -4:
						return d50;
					case -3:
						return d65;
					case -2:
						return d80;
					case -1:
						return d95;
					case 0:
						return d110;
					case 1:
						return d125;
					case 2:
						return d140;
					case 3:
						return d155;
					case 4:
						return d170;
					case 5:
						return d185;
				}
				break;
			case -1:
				switch (dy) {
					case -5:
						return d36;
					case -4:
						return d51;
					case -3:
						return d66;
					case -2:
						return d81;
					case -1:
						return d96;
					case 0:
						return d111;
					case 1:
						return d126;
					case 2:
						return d141;
					case 3:
						return d156;
					case 4:
						return d171;
					case 5:
						return d186;
				}
				break;
			case 0:
				switch (dy) {
					case -5:
						return d37;
					case -4:
						return d52;
					case -3:
						return d67;
					case -2:
						return d82;
					case -1:
						return d97;
					case 0:
						return d112;
					case 1:
						return d127;
					case 2:
						return d142;
					case 3:
						return d157;
					case 4:
						return d172;
					case 5:
						return d187;
				}
				break;
			case 1:
				switch (dy) {
					case -5:
						return d38;
					case -4:
						return d53;
					case -3:
						return d68;
					case -2:
						return d83;
					case -1:
						return d98;
					case 0:
						return d113;
					case 1:
						return d128;
					case 2:
						return d143;
					case 3:
						return d158;
					case 4:
						return d173;
					case 5:
						return d188;
				}
				break;
			case 2:
				switch (dy) {
					case -5:
						return d39;
					case -4:
						return d54;
					case -3:
						return d69;
					case -2:
						return d84;
					case -1:
						return d99;
					case 0:
						return d114;
					case 1:
						return d129;
					case 2:
						return d144;
					case 3:
						return d159;
					case 4:
						return d174;
					case 5:
						return d189;
				}
				break;
			case 3:
				switch (dy) {
					case -5:
						return d40;
					case -4:
						return d55;
					case -3:
						return d70;
					case -2:
						return d85;
					case -1:
						return d100;
					case 0:
						return d115;
					case 1:
						return d130;
					case 2:
						return d145;
					case 3:
						return d160;
					case 4:
						return d175;
					case 5:
						return d190;
				}
				break;
			case 4:
				switch (dy) {
					case -4:
						return d56;
					case -3:
						return d71;
					case -2:
						return d86;
					case -1:
						return d101;
					case 0:
						return d116;
					case 1:
						return d131;
					case 2:
						return d146;
					case 3:
						return d161;
					case 4:
						return d176;
				}
				break;
			case 5:
				switch (dy) {
					case -3:
						return d72;
					case -2:
						return d87;
					case -1:
						return d102;
					case 0:
						return d117;
					case 1:
						return d132;
					case 2:
						return d147;
					case 3:
						return d162;
				}
				break;
		}
		Direction ans = null;
		double bestEstimation = 0;
		double initialDist = Math.sqrt(l84.distanceSquaredTo(target));
		double dist37 = (initialDist - Math.sqrt(l37.distanceSquaredTo(target))) / v37;
		if (dist37 > bestEstimation) {
			bestEstimation = dist37;
			ans = d37;
		}
		double dist107 = (initialDist - Math.sqrt(l107.distanceSquaredTo(target))) / v107;
		if (dist107 > bestEstimation) {
			bestEstimation = dist107;
			ans = d107;
		}
		double dist117 = (initialDist - Math.sqrt(l117.distanceSquaredTo(target))) / v117;
		if (dist117 > bestEstimation) {
			bestEstimation = dist117;
			ans = d117;
		}
		double dist187 = (initialDist - Math.sqrt(l187.distanceSquaredTo(target))) / v187;
		if (dist187 > bestEstimation) {
			bestEstimation = dist187;
			ans = d187;
		}
		double dist36 = (initialDist - Math.sqrt(l36.distanceSquaredTo(target))) / v36;
		if (dist36 > bestEstimation) {
			bestEstimation = dist36;
			ans = d36;
		}
		double dist38 = (initialDist - Math.sqrt(l38.distanceSquaredTo(target))) / v38;
		if (dist38 > bestEstimation) {
			bestEstimation = dist38;
			ans = d38;
		}
		double dist92 = (initialDist - Math.sqrt(l92.distanceSquaredTo(target))) / v92;
		if (dist92 > bestEstimation) {
			bestEstimation = dist92;
			ans = d92;
		}
		double dist102 = (initialDist - Math.sqrt(l102.distanceSquaredTo(target))) / v102;
		if (dist102 > bestEstimation) {
			bestEstimation = dist102;
			ans = d102;
		}
		double dist122 = (initialDist - Math.sqrt(l122.distanceSquaredTo(target))) / v122;
		if (dist122 > bestEstimation) {
			bestEstimation = dist122;
			ans = d122;
		}
		double dist132 = (initialDist - Math.sqrt(l132.distanceSquaredTo(target))) / v132;
		if (dist132 > bestEstimation) {
			bestEstimation = dist132;
			ans = d132;
		}
		double dist186 = (initialDist - Math.sqrt(l186.distanceSquaredTo(target))) / v186;
		if (dist186 > bestEstimation) {
			bestEstimation = dist186;
			ans = d186;
		}
		double dist188 = (initialDist - Math.sqrt(l188.distanceSquaredTo(target))) / v188;
		if (dist188 > bestEstimation) {
			bestEstimation = dist188;
			ans = d188;
		}
		double dist35 = (initialDist - Math.sqrt(l35.distanceSquaredTo(target))) / v35;
		if (dist35 > bestEstimation) {
			bestEstimation = dist35;
			ans = d35;
		}
		double dist39 = (initialDist - Math.sqrt(l39.distanceSquaredTo(target))) / v39;
		if (dist39 > bestEstimation) {
			bestEstimation = dist39;
			ans = d39;
		}
		double dist77 = (initialDist - Math.sqrt(l77.distanceSquaredTo(target))) / v77;
		if (dist77 > bestEstimation) {
			bestEstimation = dist77;
			ans = d77;
		}
		double dist87 = (initialDist - Math.sqrt(l87.distanceSquaredTo(target))) / v87;
		if (dist87 > bestEstimation) {
			bestEstimation = dist87;
			ans = d87;
		}
		double dist137 = (initialDist - Math.sqrt(l137.distanceSquaredTo(target))) / v137;
		if (dist137 > bestEstimation) {
			bestEstimation = dist137;
			ans = d137;
		}
		double dist147 = (initialDist - Math.sqrt(l147.distanceSquaredTo(target))) / v147;
		if (dist147 > bestEstimation) {
			bestEstimation = dist147;
			ans = d147;
		}
		double dist185 = (initialDist - Math.sqrt(l185.distanceSquaredTo(target))) / v185;
		if (dist185 > bestEstimation) {
			bestEstimation = dist185;
			ans = d185;
		}
		double dist189 = (initialDist - Math.sqrt(l189.distanceSquaredTo(target))) / v189;
		if (dist189 > bestEstimation) {
			bestEstimation = dist189;
			ans = d189;
		}
		double dist48 = (initialDist - Math.sqrt(l48.distanceSquaredTo(target))) / v48;
		if (dist48 > bestEstimation) {
			bestEstimation = dist48;
			ans = d48;
		}
		double dist56 = (initialDist - Math.sqrt(l56.distanceSquaredTo(target))) / v56;
		if (dist56 > bestEstimation) {
			bestEstimation = dist56;
			ans = d56;
		}
		double dist168 = (initialDist - Math.sqrt(l168.distanceSquaredTo(target))) / v168;
		if (dist168 > bestEstimation) {
			bestEstimation = dist168;
			ans = d168;
		}
		double dist176 = (initialDist - Math.sqrt(l176.distanceSquaredTo(target))) / v176;
		if (dist176 > bestEstimation) {
			bestEstimation = dist176;
			ans = d176;
		}
		double dist34 = (initialDist - Math.sqrt(l34.distanceSquaredTo(target))) / v34;
		if (dist34 > bestEstimation) {
			bestEstimation = dist34;
			ans = d34;
		}
		double dist40 = (initialDist - Math.sqrt(l40.distanceSquaredTo(target))) / v40;
		if (dist40 > bestEstimation) {
			bestEstimation = dist40;
			ans = d40;
		}
		double dist62 = (initialDist - Math.sqrt(l62.distanceSquaredTo(target))) / v62;
		if (dist62 > bestEstimation) {
			bestEstimation = dist62;
			ans = d62;
		}
		double dist72 = (initialDist - Math.sqrt(l72.distanceSquaredTo(target))) / v72;
		if (dist72 > bestEstimation) {
			bestEstimation = dist72;
			ans = d72;
		}
		double dist152 = (initialDist - Math.sqrt(l152.distanceSquaredTo(target))) / v152;
		if (dist152 > bestEstimation) {
			bestEstimation = dist152;
			ans = d152;
		}
		double dist162 = (initialDist - Math.sqrt(l162.distanceSquaredTo(target))) / v162;
		if (dist162 > bestEstimation) {
			bestEstimation = dist162;
			ans = d162;
		}
		double dist184 = (initialDist - Math.sqrt(l184.distanceSquaredTo(target))) / v184;
		if (dist184 > bestEstimation) {
			bestEstimation = dist184;
			ans = d184;
		}
		double dist190 = (initialDist - Math.sqrt(l190.distanceSquaredTo(target))) / v190;
		if (dist190 > bestEstimation) {
			bestEstimation = dist190;
			ans = d190;
		}
		return ans;
	}

	Direction getBestDir_withOccupied(MapLocation target) throws GameActionException{
		l112 = rc.getLocation();
		v112 = 0;
		l97 = l112.add(Direction.SOUTH);
		v97 = 1000000;
		d97 = null;
		l111 = l112.add(Direction.WEST);
		v111 = 1000000;
		d111 = null;
		l113 = l112.add(Direction.EAST);
		v113 = 1000000;
		d113 = null;
		l127 = l112.add(Direction.NORTH);
		v127 = 1000000;
		d127 = null;
		l96 = l112.add(Direction.SOUTHWEST);
		v96 = 1000000;
		d96 = null;
		l98 = l112.add(Direction.SOUTHEAST);
		v98 = 1000000;
		d98 = null;
		l126 = l112.add(Direction.NORTHWEST);
		v126 = 1000000;
		d126 = null;
		l128 = l112.add(Direction.NORTHEAST);
		v128 = 1000000;
		d128 = null;
		l82 = l97.add(Direction.SOUTH);
		v82 = 1000000;
		d82 = null;
		l110 = l111.add(Direction.WEST);
		v110 = 1000000;
		d110 = null;
		l114 = l113.add(Direction.EAST);
		v114 = 1000000;
		d114 = null;
		l142 = l127.add(Direction.NORTH);
		v142 = 1000000;
		d142 = null;
		l81 = l97.add(Direction.SOUTHWEST);
		v81 = 1000000;
		d81 = null;
		l83 = l97.add(Direction.SOUTHEAST);
		v83 = 1000000;
		d83 = null;
		l95 = l111.add(Direction.SOUTHWEST);
		v95 = 1000000;
		d95 = null;
		l99 = l113.add(Direction.SOUTHEAST);
		v99 = 1000000;
		d99 = null;
		l125 = l111.add(Direction.NORTHWEST);
		v125 = 1000000;
		d125 = null;
		l129 = l113.add(Direction.NORTHEAST);
		v129 = 1000000;
		d129 = null;
		l141 = l127.add(Direction.NORTHWEST);
		v141 = 1000000;
		d141 = null;
		l143 = l127.add(Direction.NORTHEAST);
		v143 = 1000000;
		d143 = null;
		l80 = l96.add(Direction.SOUTHWEST);
		v80 = 1000000;
		d80 = null;
		l84 = l98.add(Direction.SOUTHEAST);
		v84 = 1000000;
		d84 = null;
		l140 = l126.add(Direction.NORTHWEST);
		v140 = 1000000;
		d140 = null;
		l144 = l128.add(Direction.NORTHEAST);
		v144 = 1000000;
		d144 = null;
		l67 = l82.add(Direction.SOUTH);
		v67 = 1000000;
		d67 = null;
		l109 = l110.add(Direction.WEST);
		v109 = 1000000;
		d109 = null;
		l115 = l114.add(Direction.EAST);
		v115 = 1000000;
		d115 = null;
		l157 = l142.add(Direction.NORTH);
		v157 = 1000000;
		d157 = null;
		l66 = l82.add(Direction.SOUTHWEST);
		v66 = 1000000;
		d66 = null;
		l68 = l82.add(Direction.SOUTHEAST);
		v68 = 1000000;
		d68 = null;
		l94 = l110.add(Direction.SOUTHWEST);
		v94 = 1000000;
		d94 = null;
		l100 = l114.add(Direction.SOUTHEAST);
		v100 = 1000000;
		d100 = null;
		l124 = l110.add(Direction.NORTHWEST);
		v124 = 1000000;
		d124 = null;
		l130 = l114.add(Direction.NORTHEAST);
		v130 = 1000000;
		d130 = null;
		l156 = l142.add(Direction.NORTHWEST);
		v156 = 1000000;
		d156 = null;
		l158 = l142.add(Direction.NORTHEAST);
		v158 = 1000000;
		d158 = null;
		l65 = l81.add(Direction.SOUTHWEST);
		v65 = 1000000;
		d65 = null;
		l69 = l83.add(Direction.SOUTHEAST);
		v69 = 1000000;
		d69 = null;
		l79 = l95.add(Direction.SOUTHWEST);
		v79 = 1000000;
		d79 = null;
		l85 = l99.add(Direction.SOUTHEAST);
		v85 = 1000000;
		d85 = null;
		l139 = l125.add(Direction.NORTHWEST);
		v139 = 1000000;
		d139 = null;
		l145 = l129.add(Direction.NORTHEAST);
		v145 = 1000000;
		d145 = null;
		l155 = l141.add(Direction.NORTHWEST);
		v155 = 1000000;
		d155 = null;
		l159 = l143.add(Direction.NORTHEAST);
		v159 = 1000000;
		d159 = null;
		l52 = l67.add(Direction.SOUTH);
		v52 = 1000000;
		d52 = null;
		l108 = l109.add(Direction.WEST);
		v108 = 1000000;
		d108 = null;
		l116 = l115.add(Direction.EAST);
		v116 = 1000000;
		d116 = null;
		l172 = l157.add(Direction.NORTH);
		v172 = 1000000;
		d172 = null;
		l51 = l67.add(Direction.SOUTHWEST);
		v51 = 1000000;
		d51 = null;
		l53 = l67.add(Direction.SOUTHEAST);
		v53 = 1000000;
		d53 = null;
		l93 = l109.add(Direction.SOUTHWEST);
		v93 = 1000000;
		d93 = null;
		l101 = l115.add(Direction.SOUTHEAST);
		v101 = 1000000;
		d101 = null;
		l123 = l109.add(Direction.NORTHWEST);
		v123 = 1000000;
		d123 = null;
		l131 = l115.add(Direction.NORTHEAST);
		v131 = 1000000;
		d131 = null;
		l171 = l157.add(Direction.NORTHWEST);
		v171 = 1000000;
		d171 = null;
		l173 = l157.add(Direction.NORTHEAST);
		v173 = 1000000;
		d173 = null;
		l64 = l80.add(Direction.SOUTHWEST);
		v64 = 1000000;
		d64 = null;
		l70 = l84.add(Direction.SOUTHEAST);
		v70 = 1000000;
		d70 = null;
		l154 = l140.add(Direction.NORTHWEST);
		v154 = 1000000;
		d154 = null;
		l160 = l144.add(Direction.NORTHEAST);
		v160 = 1000000;
		d160 = null;
		l50 = l66.add(Direction.SOUTHWEST);
		v50 = 1000000;
		d50 = null;
		l54 = l68.add(Direction.SOUTHEAST);
		v54 = 1000000;
		d54 = null;
		l78 = l94.add(Direction.SOUTHWEST);
		v78 = 1000000;
		d78 = null;
		l86 = l100.add(Direction.SOUTHEAST);
		v86 = 1000000;
		d86 = null;
		l138 = l124.add(Direction.NORTHWEST);
		v138 = 1000000;
		d138 = null;
		l146 = l130.add(Direction.NORTHEAST);
		v146 = 1000000;
		d146 = null;
		l170 = l156.add(Direction.NORTHWEST);
		v170 = 1000000;
		d170 = null;
		l174 = l158.add(Direction.NORTHEAST);
		v174 = 1000000;
		d174 = null;
		l37 = l52.add(Direction.SOUTH);
		v37 = 1000000;
		d37 = null;
		l49 = l65.add(Direction.SOUTHWEST);
		v49 = 1000000;
		d49 = null;
		l55 = l69.add(Direction.SOUTHEAST);
		v55 = 1000000;
		d55 = null;
		l63 = l79.add(Direction.SOUTHWEST);
		v63 = 1000000;
		d63 = null;
		l71 = l85.add(Direction.SOUTHEAST);
		v71 = 1000000;
		d71 = null;
		l107 = l108.add(Direction.WEST);
		v107 = 1000000;
		d107 = null;
		l117 = l116.add(Direction.EAST);
		v117 = 1000000;
		d117 = null;
		l153 = l139.add(Direction.NORTHWEST);
		v153 = 1000000;
		d153 = null;
		l161 = l145.add(Direction.NORTHEAST);
		v161 = 1000000;
		d161 = null;
		l169 = l155.add(Direction.NORTHWEST);
		v169 = 1000000;
		d169 = null;
		l175 = l159.add(Direction.NORTHEAST);
		v175 = 1000000;
		d175 = null;
		l187 = l172.add(Direction.NORTH);
		v187 = 1000000;
		d187 = null;
		l36 = l52.add(Direction.SOUTHWEST);
		v36 = 1000000;
		d36 = null;
		l38 = l52.add(Direction.SOUTHEAST);
		v38 = 1000000;
		d38 = null;
		l92 = l108.add(Direction.SOUTHWEST);
		v92 = 1000000;
		d92 = null;
		l102 = l116.add(Direction.SOUTHEAST);
		v102 = 1000000;
		d102 = null;
		l122 = l108.add(Direction.NORTHWEST);
		v122 = 1000000;
		d122 = null;
		l132 = l116.add(Direction.NORTHEAST);
		v132 = 1000000;
		d132 = null;
		l186 = l172.add(Direction.NORTHWEST);
		v186 = 1000000;
		d186 = null;
		l188 = l172.add(Direction.NORTHEAST);
		v188 = 1000000;
		d188 = null;
		l35 = l51.add(Direction.SOUTHWEST);
		v35 = 1000000;
		d35 = null;
		l39 = l53.add(Direction.SOUTHEAST);
		v39 = 1000000;
		d39 = null;
		l77 = l93.add(Direction.SOUTHWEST);
		v77 = 1000000;
		d77 = null;
		l87 = l101.add(Direction.SOUTHEAST);
		v87 = 1000000;
		d87 = null;
		l137 = l123.add(Direction.NORTHWEST);
		v137 = 1000000;
		d137 = null;
		l147 = l131.add(Direction.NORTHEAST);
		v147 = 1000000;
		d147 = null;
		l185 = l171.add(Direction.NORTHWEST);
		v185 = 1000000;
		d185 = null;
		l189 = l173.add(Direction.NORTHEAST);
		v189 = 1000000;
		d189 = null;
		l48 = l64.add(Direction.SOUTHWEST);
		v48 = 1000000;
		d48 = null;
		l56 = l70.add(Direction.SOUTHEAST);
		v56 = 1000000;
		d56 = null;
		l168 = l154.add(Direction.NORTHWEST);
		v168 = 1000000;
		d168 = null;
		l176 = l160.add(Direction.NORTHEAST);
		v176 = 1000000;
		d176 = null;
		l34 = l50.add(Direction.SOUTHWEST);
		v34 = 1000000;
		d34 = null;
		l40 = l54.add(Direction.SOUTHEAST);
		v40 = 1000000;
		d40 = null;
		l62 = l78.add(Direction.SOUTHWEST);
		v62 = 1000000;
		d62 = null;
		l72 = l86.add(Direction.SOUTHEAST);
		v72 = 1000000;
		d72 = null;
		l152 = l138.add(Direction.NORTHWEST);
		v152 = 1000000;
		d152 = null;
		l162 = l146.add(Direction.NORTHEAST);
		v162 = 1000000;
		d162 = null;
		l184 = l170.add(Direction.NORTHWEST);
		v184 = 1000000;
		d184 = null;
		l190 = l174.add(Direction.NORTHEAST);
		v190 = 1000000;
		d190 = null;
		if (rc.onTheMap(l97)) {
				p97 = Math.floor((1.0 + (double)rc.senseRubble(l97)/10.0)*cooldown);
				if (v97 > v112 + p97) {
					v97 = v112 + p97;
					d97 = Direction.SOUTH;
				}
		}
		if (rc.onTheMap(l111)) {
				p111 = Math.floor((1.0 + (double)rc.senseRubble(l111)/10.0)*cooldown);
				if (v111 > v112 + p111) {
					v111 = v112 + p111;
					d111 = Direction.WEST;
				}
				if (v111 > v97 + p111) {
					v111 = v97 + p111;
					d111 = d97;
				}
		}
		if (rc.onTheMap(l113)) {
				p113 = Math.floor((1.0 + (double)rc.senseRubble(l113)/10.0)*cooldown);
				if (v113 > v112 + p113) {
					v113 = v112 + p113;
					d113 = Direction.EAST;
				}
				if (v113 > v97 + p113) {
					v113 = v97 + p113;
					d113 = d97;
				}
		}
		if (rc.onTheMap(l127)) {
				p127 = Math.floor((1.0 + (double)rc.senseRubble(l127)/10.0)*cooldown);
				if (v127 > v112 + p127) {
					v127 = v112 + p127;
					d127 = Direction.NORTH;
				}
				if (v127 > v111 + p127) {
					v127 = v111 + p127;
					d127 = d111;
				}
				if (v127 > v113 + p127) {
					v127 = v113 + p127;
					d127 = d113;
				}
		}
		if (rc.onTheMap(l96)) {
				p96 = Math.floor((1.0 + (double)rc.senseRubble(l96)/10.0)*cooldown);
				if (v96 > v112 + p96) {
					v96 = v112 + p96;
					d96 = Direction.SOUTHWEST;
				}
				if (v96 > v97 + p96) {
					v96 = v97 + p96;
					d96 = d97;
				}
				if (v96 > v111 + p96) {
					v96 = v111 + p96;
					d96 = d111;
				}
		}
		if (rc.onTheMap(l98)) {
				p98 = Math.floor((1.0 + (double)rc.senseRubble(l98)/10.0)*cooldown);
				if (v98 > v112 + p98) {
					v98 = v112 + p98;
					d98 = Direction.SOUTHEAST;
				}
				if (v98 > v97 + p98) {
					v98 = v97 + p98;
					d98 = d97;
				}
				if (v98 > v113 + p98) {
					v98 = v113 + p98;
					d98 = d113;
				}
		}
		if (rc.onTheMap(l126)) {
				p126 = Math.floor((1.0 + (double)rc.senseRubble(l126)/10.0)*cooldown);
				if (v126 > v112 + p126) {
					v126 = v112 + p126;
					d126 = Direction.NORTHWEST;
				}
				if (v126 > v111 + p126) {
					v126 = v111 + p126;
					d126 = d111;
				}
				if (v126 > v127 + p126) {
					v126 = v127 + p126;
					d126 = d127;
				}
		}
		if (rc.onTheMap(l128)) {
				p128 = Math.floor((1.0 + (double)rc.senseRubble(l128)/10.0)*cooldown);
				if (v128 > v112 + p128) {
					v128 = v112 + p128;
					d128 = Direction.NORTHEAST;
				}
				if (v128 > v113 + p128) {
					v128 = v113 + p128;
					d128 = d113;
				}
				if (v128 > v127 + p128) {
					v128 = v127 + p128;
					d128 = d127;
				}
		}
		if (rc.onTheMap(l82)) {
				p82 = Math.floor((1.0 + (double)rc.senseRubble(l82)/10.0)*cooldown);
				if (v82 > v97 + p82) {
					v82 = v97 + p82;
					d82 = d97;
				}
				if (v82 > v96 + p82) {
					v82 = v96 + p82;
					d82 = d96;
				}
				if (v82 > v98 + p82) {
					v82 = v98 + p82;
					d82 = d98;
				}
		}
		if (rc.onTheMap(l110)) {
				p110 = Math.floor((1.0 + (double)rc.senseRubble(l110)/10.0)*cooldown);
				if (v110 > v111 + p110) {
					v110 = v111 + p110;
					d110 = d111;
				}
				if (v110 > v96 + p110) {
					v110 = v96 + p110;
					d110 = d96;
				}
				if (v110 > v126 + p110) {
					v110 = v126 + p110;
					d110 = d126;
				}
		}
		if (rc.onTheMap(l114)) {
				p114 = Math.floor((1.0 + (double)rc.senseRubble(l114)/10.0)*cooldown);
				if (v114 > v113 + p114) {
					v114 = v113 + p114;
					d114 = d113;
				}
				if (v114 > v98 + p114) {
					v114 = v98 + p114;
					d114 = d98;
				}
				if (v114 > v128 + p114) {
					v114 = v128 + p114;
					d114 = d128;
				}
		}
		if (rc.onTheMap(l142)) {
				p142 = Math.floor((1.0 + (double)rc.senseRubble(l142)/10.0)*cooldown);
				if (v142 > v127 + p142) {
					v142 = v127 + p142;
					d142 = d127;
				}
				if (v142 > v126 + p142) {
					v142 = v126 + p142;
					d142 = d126;
				}
				if (v142 > v128 + p142) {
					v142 = v128 + p142;
					d142 = d128;
				}
		}
		if (rc.onTheMap(l81)) {
				p81 = Math.floor((1.0 + (double)rc.senseRubble(l81)/10.0)*cooldown);
				if (v81 > v97 + p81) {
					v81 = v97 + p81;
					d81 = d97;
				}
				if (v81 > v96 + p81) {
					v81 = v96 + p81;
					d81 = d96;
				}
				if (v81 > v82 + p81) {
					v81 = v82 + p81;
					d81 = d82;
				}
		}
		if (rc.onTheMap(l83)) {
				p83 = Math.floor((1.0 + (double)rc.senseRubble(l83)/10.0)*cooldown);
				if (v83 > v97 + p83) {
					v83 = v97 + p83;
					d83 = d97;
				}
				if (v83 > v98 + p83) {
					v83 = v98 + p83;
					d83 = d98;
				}
				if (v83 > v82 + p83) {
					v83 = v82 + p83;
					d83 = d82;
				}
		}
		if (rc.onTheMap(l95)) {
				p95 = Math.floor((1.0 + (double)rc.senseRubble(l95)/10.0)*cooldown);
				if (v95 > v111 + p95) {
					v95 = v111 + p95;
					d95 = d111;
				}
				if (v95 > v96 + p95) {
					v95 = v96 + p95;
					d95 = d96;
				}
				if (v95 > v110 + p95) {
					v95 = v110 + p95;
					d95 = d110;
				}
				if (v95 > v81 + p95) {
					v95 = v81 + p95;
					d95 = d81;
				}
		}
		if (rc.onTheMap(l99)) {
				p99 = Math.floor((1.0 + (double)rc.senseRubble(l99)/10.0)*cooldown);
				if (v99 > v113 + p99) {
					v99 = v113 + p99;
					d99 = d113;
				}
				if (v99 > v98 + p99) {
					v99 = v98 + p99;
					d99 = d98;
				}
				if (v99 > v114 + p99) {
					v99 = v114 + p99;
					d99 = d114;
				}
				if (v99 > v83 + p99) {
					v99 = v83 + p99;
					d99 = d83;
				}
		}
		if (rc.onTheMap(l125)) {
				p125 = Math.floor((1.0 + (double)rc.senseRubble(l125)/10.0)*cooldown);
				if (v125 > v111 + p125) {
					v125 = v111 + p125;
					d125 = d111;
				}
				if (v125 > v126 + p125) {
					v125 = v126 + p125;
					d125 = d126;
				}
				if (v125 > v110 + p125) {
					v125 = v110 + p125;
					d125 = d110;
				}
		}
		if (rc.onTheMap(l129)) {
				p129 = Math.floor((1.0 + (double)rc.senseRubble(l129)/10.0)*cooldown);
				if (v129 > v113 + p129) {
					v129 = v113 + p129;
					d129 = d113;
				}
				if (v129 > v128 + p129) {
					v129 = v128 + p129;
					d129 = d128;
				}
				if (v129 > v114 + p129) {
					v129 = v114 + p129;
					d129 = d114;
				}
		}
		if (rc.onTheMap(l141)) {
				p141 = Math.floor((1.0 + (double)rc.senseRubble(l141)/10.0)*cooldown);
				if (v141 > v127 + p141) {
					v141 = v127 + p141;
					d141 = d127;
				}
				if (v141 > v126 + p141) {
					v141 = v126 + p141;
					d141 = d126;
				}
				if (v141 > v142 + p141) {
					v141 = v142 + p141;
					d141 = d142;
				}
				if (v141 > v125 + p141) {
					v141 = v125 + p141;
					d141 = d125;
				}
		}
		if (rc.onTheMap(l143)) {
				p143 = Math.floor((1.0 + (double)rc.senseRubble(l143)/10.0)*cooldown);
				if (v143 > v127 + p143) {
					v143 = v127 + p143;
					d143 = d127;
				}
				if (v143 > v128 + p143) {
					v143 = v128 + p143;
					d143 = d128;
				}
				if (v143 > v142 + p143) {
					v143 = v142 + p143;
					d143 = d142;
				}
				if (v143 > v129 + p143) {
					v143 = v129 + p143;
					d143 = d129;
				}
		}
		if (rc.onTheMap(l80)) {
				p80 = Math.floor((1.0 + (double)rc.senseRubble(l80)/10.0)*cooldown);
				if (v80 > v96 + p80) {
					v80 = v96 + p80;
					d80 = d96;
				}
				if (v80 > v81 + p80) {
					v80 = v81 + p80;
					d80 = d81;
				}
				if (v80 > v95 + p80) {
					v80 = v95 + p80;
					d80 = d95;
				}
		}
		if (rc.onTheMap(l84)) {
				p84 = Math.floor((1.0 + (double)rc.senseRubble(l84)/10.0)*cooldown);
				if (v84 > v98 + p84) {
					v84 = v98 + p84;
					d84 = d98;
				}
				if (v84 > v83 + p84) {
					v84 = v83 + p84;
					d84 = d83;
				}
				if (v84 > v99 + p84) {
					v84 = v99 + p84;
					d84 = d99;
				}
		}
		if (rc.onTheMap(l140)) {
				p140 = Math.floor((1.0 + (double)rc.senseRubble(l140)/10.0)*cooldown);
				if (v140 > v126 + p140) {
					v140 = v126 + p140;
					d140 = d126;
				}
				if (v140 > v125 + p140) {
					v140 = v125 + p140;
					d140 = d125;
				}
				if (v140 > v141 + p140) {
					v140 = v141 + p140;
					d140 = d141;
				}
		}
		if (rc.onTheMap(l144)) {
				p144 = Math.floor((1.0 + (double)rc.senseRubble(l144)/10.0)*cooldown);
				if (v144 > v128 + p144) {
					v144 = v128 + p144;
					d144 = d128;
				}
				if (v144 > v129 + p144) {
					v144 = v129 + p144;
					d144 = d129;
				}
				if (v144 > v143 + p144) {
					v144 = v143 + p144;
					d144 = d143;
				}
		}
		if (rc.onTheMap(l67)) {
				p67 = Math.floor((1.0 + (double)rc.senseRubble(l67)/10.0)*cooldown);
				if (v67 > v82 + p67) {
					v67 = v82 + p67;
					d67 = d82;
				}
				if (v67 > v81 + p67) {
					v67 = v81 + p67;
					d67 = d81;
				}
				if (v67 > v83 + p67) {
					v67 = v83 + p67;
					d67 = d83;
				}
		}
		if (rc.onTheMap(l109)) {
				p109 = Math.floor((1.0 + (double)rc.senseRubble(l109)/10.0)*cooldown);
				if (v109 > v110 + p109) {
					v109 = v110 + p109;
					d109 = d110;
				}
				if (v109 > v95 + p109) {
					v109 = v95 + p109;
					d109 = d95;
				}
				if (v109 > v125 + p109) {
					v109 = v125 + p109;
					d109 = d125;
				}
		}
		if (rc.onTheMap(l115)) {
				p115 = Math.floor((1.0 + (double)rc.senseRubble(l115)/10.0)*cooldown);
				if (v115 > v114 + p115) {
					v115 = v114 + p115;
					d115 = d114;
				}
				if (v115 > v99 + p115) {
					v115 = v99 + p115;
					d115 = d99;
				}
				if (v115 > v129 + p115) {
					v115 = v129 + p115;
					d115 = d129;
				}
		}
		if (rc.onTheMap(l157)) {
				p157 = Math.floor((1.0 + (double)rc.senseRubble(l157)/10.0)*cooldown);
				if (v157 > v142 + p157) {
					v157 = v142 + p157;
					d157 = d142;
				}
				if (v157 > v141 + p157) {
					v157 = v141 + p157;
					d157 = d141;
				}
				if (v157 > v143 + p157) {
					v157 = v143 + p157;
					d157 = d143;
				}
		}
		if (rc.onTheMap(l66)) {
				p66 = Math.floor((1.0 + (double)rc.senseRubble(l66)/10.0)*cooldown);
				if (v66 > v82 + p66) {
					v66 = v82 + p66;
					d66 = d82;
				}
				if (v66 > v81 + p66) {
					v66 = v81 + p66;
					d66 = d81;
				}
				if (v66 > v80 + p66) {
					v66 = v80 + p66;
					d66 = d80;
				}
				if (v66 > v67 + p66) {
					v66 = v67 + p66;
					d66 = d67;
				}
		}
		if (rc.onTheMap(l68)) {
				p68 = Math.floor((1.0 + (double)rc.senseRubble(l68)/10.0)*cooldown);
				if (v68 > v82 + p68) {
					v68 = v82 + p68;
					d68 = d82;
				}
				if (v68 > v83 + p68) {
					v68 = v83 + p68;
					d68 = d83;
				}
				if (v68 > v84 + p68) {
					v68 = v84 + p68;
					d68 = d84;
				}
				if (v68 > v67 + p68) {
					v68 = v67 + p68;
					d68 = d67;
				}
		}
		if (rc.onTheMap(l94)) {
				p94 = Math.floor((1.0 + (double)rc.senseRubble(l94)/10.0)*cooldown);
				if (v94 > v110 + p94) {
					v94 = v110 + p94;
					d94 = d110;
				}
				if (v94 > v95 + p94) {
					v94 = v95 + p94;
					d94 = d95;
				}
				if (v94 > v80 + p94) {
					v94 = v80 + p94;
					d94 = d80;
				}
				if (v94 > v109 + p94) {
					v94 = v109 + p94;
					d94 = d109;
				}
		}
		if (rc.onTheMap(l100)) {
				p100 = Math.floor((1.0 + (double)rc.senseRubble(l100)/10.0)*cooldown);
				if (v100 > v114 + p100) {
					v100 = v114 + p100;
					d100 = d114;
				}
				if (v100 > v99 + p100) {
					v100 = v99 + p100;
					d100 = d99;
				}
				if (v100 > v84 + p100) {
					v100 = v84 + p100;
					d100 = d84;
				}
				if (v100 > v115 + p100) {
					v100 = v115 + p100;
					d100 = d115;
				}
		}
		if (rc.onTheMap(l124)) {
				p124 = Math.floor((1.0 + (double)rc.senseRubble(l124)/10.0)*cooldown);
				if (v124 > v110 + p124) {
					v124 = v110 + p124;
					d124 = d110;
				}
				if (v124 > v125 + p124) {
					v124 = v125 + p124;
					d124 = d125;
				}
				if (v124 > v140 + p124) {
					v124 = v140 + p124;
					d124 = d140;
				}
				if (v124 > v109 + p124) {
					v124 = v109 + p124;
					d124 = d109;
				}
		}
		if (rc.onTheMap(l130)) {
				p130 = Math.floor((1.0 + (double)rc.senseRubble(l130)/10.0)*cooldown);
				if (v130 > v114 + p130) {
					v130 = v114 + p130;
					d130 = d114;
				}
				if (v130 > v129 + p130) {
					v130 = v129 + p130;
					d130 = d129;
				}
				if (v130 > v144 + p130) {
					v130 = v144 + p130;
					d130 = d144;
				}
				if (v130 > v115 + p130) {
					v130 = v115 + p130;
					d130 = d115;
				}
		}
		if (rc.onTheMap(l156)) {
				p156 = Math.floor((1.0 + (double)rc.senseRubble(l156)/10.0)*cooldown);
				if (v156 > v142 + p156) {
					v156 = v142 + p156;
					d156 = d142;
				}
				if (v156 > v141 + p156) {
					v156 = v141 + p156;
					d156 = d141;
				}
				if (v156 > v140 + p156) {
					v156 = v140 + p156;
					d156 = d140;
				}
				if (v156 > v157 + p156) {
					v156 = v157 + p156;
					d156 = d157;
				}
		}
		if (rc.onTheMap(l158)) {
				p158 = Math.floor((1.0 + (double)rc.senseRubble(l158)/10.0)*cooldown);
				if (v158 > v142 + p158) {
					v158 = v142 + p158;
					d158 = d142;
				}
				if (v158 > v143 + p158) {
					v158 = v143 + p158;
					d158 = d143;
				}
				if (v158 > v144 + p158) {
					v158 = v144 + p158;
					d158 = d144;
				}
				if (v158 > v157 + p158) {
					v158 = v157 + p158;
					d158 = d157;
				}
		}
		if (rc.onTheMap(l65)) {
				p65 = Math.floor((1.0 + (double)rc.senseRubble(l65)/10.0)*cooldown);
				if (v65 > v81 + p65) {
					v65 = v81 + p65;
					d65 = d81;
				}
				if (v65 > v80 + p65) {
					v65 = v80 + p65;
					d65 = d80;
				}
				if (v65 > v66 + p65) {
					v65 = v66 + p65;
					d65 = d66;
				}
		}
		if (rc.onTheMap(l69)) {
				p69 = Math.floor((1.0 + (double)rc.senseRubble(l69)/10.0)*cooldown);
				if (v69 > v83 + p69) {
					v69 = v83 + p69;
					d69 = d83;
				}
				if (v69 > v84 + p69) {
					v69 = v84 + p69;
					d69 = d84;
				}
				if (v69 > v68 + p69) {
					v69 = v68 + p69;
					d69 = d68;
				}
		}
		if (rc.onTheMap(l79)) {
				p79 = Math.floor((1.0 + (double)rc.senseRubble(l79)/10.0)*cooldown);
				if (v79 > v95 + p79) {
					v79 = v95 + p79;
					d79 = d95;
				}
				if (v79 > v80 + p79) {
					v79 = v80 + p79;
					d79 = d80;
				}
				if (v79 > v94 + p79) {
					v79 = v94 + p79;
					d79 = d94;
				}
				if (v79 > v65 + p79) {
					v79 = v65 + p79;
					d79 = d65;
				}
		}
		if (rc.onTheMap(l85)) {
				p85 = Math.floor((1.0 + (double)rc.senseRubble(l85)/10.0)*cooldown);
				if (v85 > v99 + p85) {
					v85 = v99 + p85;
					d85 = d99;
				}
				if (v85 > v84 + p85) {
					v85 = v84 + p85;
					d85 = d84;
				}
				if (v85 > v100 + p85) {
					v85 = v100 + p85;
					d85 = d100;
				}
				if (v85 > v69 + p85) {
					v85 = v69 + p85;
					d85 = d69;
				}
		}
		if (rc.onTheMap(l139)) {
				p139 = Math.floor((1.0 + (double)rc.senseRubble(l139)/10.0)*cooldown);
				if (v139 > v125 + p139) {
					v139 = v125 + p139;
					d139 = d125;
				}
				if (v139 > v140 + p139) {
					v139 = v140 + p139;
					d139 = d140;
				}
				if (v139 > v124 + p139) {
					v139 = v124 + p139;
					d139 = d124;
				}
		}
		if (rc.onTheMap(l145)) {
				p145 = Math.floor((1.0 + (double)rc.senseRubble(l145)/10.0)*cooldown);
				if (v145 > v129 + p145) {
					v145 = v129 + p145;
					d145 = d129;
				}
				if (v145 > v144 + p145) {
					v145 = v144 + p145;
					d145 = d144;
				}
				if (v145 > v130 + p145) {
					v145 = v130 + p145;
					d145 = d130;
				}
		}
		if (rc.onTheMap(l155)) {
				p155 = Math.floor((1.0 + (double)rc.senseRubble(l155)/10.0)*cooldown);
				if (v155 > v141 + p155) {
					v155 = v141 + p155;
					d155 = d141;
				}
				if (v155 > v140 + p155) {
					v155 = v140 + p155;
					d155 = d140;
				}
				if (v155 > v156 + p155) {
					v155 = v156 + p155;
					d155 = d156;
				}
				if (v155 > v139 + p155) {
					v155 = v139 + p155;
					d155 = d139;
				}
		}
		if (rc.onTheMap(l159)) {
				p159 = Math.floor((1.0 + (double)rc.senseRubble(l159)/10.0)*cooldown);
				if (v159 > v143 + p159) {
					v159 = v143 + p159;
					d159 = d143;
				}
				if (v159 > v144 + p159) {
					v159 = v144 + p159;
					d159 = d144;
				}
				if (v159 > v158 + p159) {
					v159 = v158 + p159;
					d159 = d158;
				}
				if (v159 > v145 + p159) {
					v159 = v145 + p159;
					d159 = d145;
				}
		}
		if (rc.onTheMap(l52)) {
				p52 = Math.floor((1.0 + (double)rc.senseRubble(l52)/10.0)*cooldown);
				if (v52 > v67 + p52) {
					v52 = v67 + p52;
					d52 = d67;
				}
				if (v52 > v66 + p52) {
					v52 = v66 + p52;
					d52 = d66;
				}
				if (v52 > v68 + p52) {
					v52 = v68 + p52;
					d52 = d68;
				}
		}
		if (rc.onTheMap(l108)) {
				p108 = Math.floor((1.0 + (double)rc.senseRubble(l108)/10.0)*cooldown);
				if (v108 > v109 + p108) {
					v108 = v109 + p108;
					d108 = d109;
				}
				if (v108 > v94 + p108) {
					v108 = v94 + p108;
					d108 = d94;
				}
				if (v108 > v124 + p108) {
					v108 = v124 + p108;
					d108 = d124;
				}
		}
		if (rc.onTheMap(l116)) {
				p116 = Math.floor((1.0 + (double)rc.senseRubble(l116)/10.0)*cooldown);
				if (v116 > v115 + p116) {
					v116 = v115 + p116;
					d116 = d115;
				}
				if (v116 > v100 + p116) {
					v116 = v100 + p116;
					d116 = d100;
				}
				if (v116 > v130 + p116) {
					v116 = v130 + p116;
					d116 = d130;
				}
		}
		if (rc.onTheMap(l172)) {
				p172 = Math.floor((1.0 + (double)rc.senseRubble(l172)/10.0)*cooldown);
				if (v172 > v157 + p172) {
					v172 = v157 + p172;
					d172 = d157;
				}
				if (v172 > v156 + p172) {
					v172 = v156 + p172;
					d172 = d156;
				}
				if (v172 > v158 + p172) {
					v172 = v158 + p172;
					d172 = d158;
				}
		}
		if (rc.onTheMap(l51)) {
				p51 = Math.floor((1.0 + (double)rc.senseRubble(l51)/10.0)*cooldown);
				if (v51 > v67 + p51) {
					v51 = v67 + p51;
					d51 = d67;
				}
				if (v51 > v66 + p51) {
					v51 = v66 + p51;
					d51 = d66;
				}
				if (v51 > v65 + p51) {
					v51 = v65 + p51;
					d51 = d65;
				}
				if (v51 > v52 + p51) {
					v51 = v52 + p51;
					d51 = d52;
				}
		}
		if (rc.onTheMap(l53)) {
				p53 = Math.floor((1.0 + (double)rc.senseRubble(l53)/10.0)*cooldown);
				if (v53 > v67 + p53) {
					v53 = v67 + p53;
					d53 = d67;
				}
				if (v53 > v68 + p53) {
					v53 = v68 + p53;
					d53 = d68;
				}
				if (v53 > v69 + p53) {
					v53 = v69 + p53;
					d53 = d69;
				}
				if (v53 > v52 + p53) {
					v53 = v52 + p53;
					d53 = d52;
				}
		}
		if (rc.onTheMap(l93)) {
				p93 = Math.floor((1.0 + (double)rc.senseRubble(l93)/10.0)*cooldown);
				if (v93 > v109 + p93) {
					v93 = v109 + p93;
					d93 = d109;
				}
				if (v93 > v94 + p93) {
					v93 = v94 + p93;
					d93 = d94;
				}
				if (v93 > v79 + p93) {
					v93 = v79 + p93;
					d93 = d79;
				}
				if (v93 > v108 + p93) {
					v93 = v108 + p93;
					d93 = d108;
				}
		}
		if (rc.onTheMap(l101)) {
				p101 = Math.floor((1.0 + (double)rc.senseRubble(l101)/10.0)*cooldown);
				if (v101 > v115 + p101) {
					v101 = v115 + p101;
					d101 = d115;
				}
				if (v101 > v100 + p101) {
					v101 = v100 + p101;
					d101 = d100;
				}
				if (v101 > v85 + p101) {
					v101 = v85 + p101;
					d101 = d85;
				}
				if (v101 > v116 + p101) {
					v101 = v116 + p101;
					d101 = d116;
				}
		}
		if (rc.onTheMap(l123)) {
				p123 = Math.floor((1.0 + (double)rc.senseRubble(l123)/10.0)*cooldown);
				if (v123 > v109 + p123) {
					v123 = v109 + p123;
					d123 = d109;
				}
				if (v123 > v124 + p123) {
					v123 = v124 + p123;
					d123 = d124;
				}
				if (v123 > v139 + p123) {
					v123 = v139 + p123;
					d123 = d139;
				}
				if (v123 > v108 + p123) {
					v123 = v108 + p123;
					d123 = d108;
				}
		}
		if (rc.onTheMap(l131)) {
				p131 = Math.floor((1.0 + (double)rc.senseRubble(l131)/10.0)*cooldown);
				if (v131 > v115 + p131) {
					v131 = v115 + p131;
					d131 = d115;
				}
				if (v131 > v130 + p131) {
					v131 = v130 + p131;
					d131 = d130;
				}
				if (v131 > v145 + p131) {
					v131 = v145 + p131;
					d131 = d145;
				}
				if (v131 > v116 + p131) {
					v131 = v116 + p131;
					d131 = d116;
				}
		}
		if (rc.onTheMap(l171)) {
				p171 = Math.floor((1.0 + (double)rc.senseRubble(l171)/10.0)*cooldown);
				if (v171 > v157 + p171) {
					v171 = v157 + p171;
					d171 = d157;
				}
				if (v171 > v156 + p171) {
					v171 = v156 + p171;
					d171 = d156;
				}
				if (v171 > v155 + p171) {
					v171 = v155 + p171;
					d171 = d155;
				}
				if (v171 > v172 + p171) {
					v171 = v172 + p171;
					d171 = d172;
				}
		}
		if (rc.onTheMap(l173)) {
				p173 = Math.floor((1.0 + (double)rc.senseRubble(l173)/10.0)*cooldown);
				if (v173 > v157 + p173) {
					v173 = v157 + p173;
					d173 = d157;
				}
				if (v173 > v158 + p173) {
					v173 = v158 + p173;
					d173 = d158;
				}
				if (v173 > v159 + p173) {
					v173 = v159 + p173;
					d173 = d159;
				}
				if (v173 > v172 + p173) {
					v173 = v172 + p173;
					d173 = d172;
				}
		}
		if (rc.onTheMap(l64)) {
				p64 = Math.floor((1.0 + (double)rc.senseRubble(l64)/10.0)*cooldown);
				if (v64 > v80 + p64) {
					v64 = v80 + p64;
					d64 = d80;
				}
				if (v64 > v65 + p64) {
					v64 = v65 + p64;
					d64 = d65;
				}
				if (v64 > v79 + p64) {
					v64 = v79 + p64;
					d64 = d79;
				}
		}
		if (rc.onTheMap(l70)) {
				p70 = Math.floor((1.0 + (double)rc.senseRubble(l70)/10.0)*cooldown);
				if (v70 > v84 + p70) {
					v70 = v84 + p70;
					d70 = d84;
				}
				if (v70 > v69 + p70) {
					v70 = v69 + p70;
					d70 = d69;
				}
				if (v70 > v85 + p70) {
					v70 = v85 + p70;
					d70 = d85;
				}
		}
		if (rc.onTheMap(l154)) {
				p154 = Math.floor((1.0 + (double)rc.senseRubble(l154)/10.0)*cooldown);
				if (v154 > v140 + p154) {
					v154 = v140 + p154;
					d154 = d140;
				}
				if (v154 > v139 + p154) {
					v154 = v139 + p154;
					d154 = d139;
				}
				if (v154 > v155 + p154) {
					v154 = v155 + p154;
					d154 = d155;
				}
		}
		if (rc.onTheMap(l160)) {
				p160 = Math.floor((1.0 + (double)rc.senseRubble(l160)/10.0)*cooldown);
				if (v160 > v144 + p160) {
					v160 = v144 + p160;
					d160 = d144;
				}
				if (v160 > v145 + p160) {
					v160 = v145 + p160;
					d160 = d145;
				}
				if (v160 > v159 + p160) {
					v160 = v159 + p160;
					d160 = d159;
				}
		}
		if (rc.onTheMap(l50)) {
				p50 = Math.floor((1.0 + (double)rc.senseRubble(l50)/10.0)*cooldown);
				if (v50 > v66 + p50) {
					v50 = v66 + p50;
					d50 = d66;
				}
				if (v50 > v65 + p50) {
					v50 = v65 + p50;
					d50 = d65;
				}
				if (v50 > v51 + p50) {
					v50 = v51 + p50;
					d50 = d51;
				}
				if (v50 > v64 + p50) {
					v50 = v64 + p50;
					d50 = d64;
				}
		}
		if (rc.onTheMap(l54)) {
				p54 = Math.floor((1.0 + (double)rc.senseRubble(l54)/10.0)*cooldown);
				if (v54 > v68 + p54) {
					v54 = v68 + p54;
					d54 = d68;
				}
				if (v54 > v69 + p54) {
					v54 = v69 + p54;
					d54 = d69;
				}
				if (v54 > v53 + p54) {
					v54 = v53 + p54;
					d54 = d53;
				}
				if (v54 > v70 + p54) {
					v54 = v70 + p54;
					d54 = d70;
				}
		}
		if (rc.onTheMap(l78)) {
				p78 = Math.floor((1.0 + (double)rc.senseRubble(l78)/10.0)*cooldown);
				if (v78 > v94 + p78) {
					v78 = v94 + p78;
					d78 = d94;
				}
				if (v78 > v79 + p78) {
					v78 = v79 + p78;
					d78 = d79;
				}
				if (v78 > v93 + p78) {
					v78 = v93 + p78;
					d78 = d93;
				}
				if (v78 > v64 + p78) {
					v78 = v64 + p78;
					d78 = d64;
				}
		}
		if (rc.onTheMap(l86)) {
				p86 = Math.floor((1.0 + (double)rc.senseRubble(l86)/10.0)*cooldown);
				if (v86 > v100 + p86) {
					v86 = v100 + p86;
					d86 = d100;
				}
				if (v86 > v85 + p86) {
					v86 = v85 + p86;
					d86 = d85;
				}
				if (v86 > v101 + p86) {
					v86 = v101 + p86;
					d86 = d101;
				}
				if (v86 > v70 + p86) {
					v86 = v70 + p86;
					d86 = d70;
				}
		}
		if (rc.onTheMap(l138)) {
				p138 = Math.floor((1.0 + (double)rc.senseRubble(l138)/10.0)*cooldown);
				if (v138 > v124 + p138) {
					v138 = v124 + p138;
					d138 = d124;
				}
				if (v138 > v139 + p138) {
					v138 = v139 + p138;
					d138 = d139;
				}
				if (v138 > v123 + p138) {
					v138 = v123 + p138;
					d138 = d123;
				}
				if (v138 > v154 + p138) {
					v138 = v154 + p138;
					d138 = d154;
				}
		}
		if (rc.onTheMap(l146)) {
				p146 = Math.floor((1.0 + (double)rc.senseRubble(l146)/10.0)*cooldown);
				if (v146 > v130 + p146) {
					v146 = v130 + p146;
					d146 = d130;
				}
				if (v146 > v145 + p146) {
					v146 = v145 + p146;
					d146 = d145;
				}
				if (v146 > v131 + p146) {
					v146 = v131 + p146;
					d146 = d131;
				}
				if (v146 > v160 + p146) {
					v146 = v160 + p146;
					d146 = d160;
				}
		}
		if (rc.onTheMap(l170)) {
				p170 = Math.floor((1.0 + (double)rc.senseRubble(l170)/10.0)*cooldown);
				if (v170 > v156 + p170) {
					v170 = v156 + p170;
					d170 = d156;
				}
				if (v170 > v155 + p170) {
					v170 = v155 + p170;
					d170 = d155;
				}
				if (v170 > v171 + p170) {
					v170 = v171 + p170;
					d170 = d171;
				}
				if (v170 > v154 + p170) {
					v170 = v154 + p170;
					d170 = d154;
				}
		}
		if (rc.onTheMap(l174)) {
				p174 = Math.floor((1.0 + (double)rc.senseRubble(l174)/10.0)*cooldown);
				if (v174 > v158 + p174) {
					v174 = v158 + p174;
					d174 = d158;
				}
				if (v174 > v159 + p174) {
					v174 = v159 + p174;
					d174 = d159;
				}
				if (v174 > v173 + p174) {
					v174 = v173 + p174;
					d174 = d173;
				}
				if (v174 > v160 + p174) {
					v174 = v160 + p174;
					d174 = d160;
				}
		}
		if (rc.onTheMap(l37)) {
				p37 = Math.floor((1.0 + (double)rc.senseRubble(l37)/10.0)*cooldown);
				if (v37 > v52 + p37) {
					v37 = v52 + p37;
					d37 = d52;
				}
				if (v37 > v51 + p37) {
					v37 = v51 + p37;
					d37 = d51;
				}
				if (v37 > v53 + p37) {
					v37 = v53 + p37;
					d37 = d53;
				}
		}
		if (rc.onTheMap(l49)) {
				p49 = Math.floor((1.0 + (double)rc.senseRubble(l49)/10.0)*cooldown);
				if (v49 > v65 + p49) {
					v49 = v65 + p49;
					d49 = d65;
				}
				if (v49 > v64 + p49) {
					v49 = v64 + p49;
					d49 = d64;
				}
				if (v49 > v50 + p49) {
					v49 = v50 + p49;
					d49 = d50;
				}
		}
		if (rc.onTheMap(l55)) {
				p55 = Math.floor((1.0 + (double)rc.senseRubble(l55)/10.0)*cooldown);
				if (v55 > v69 + p55) {
					v55 = v69 + p55;
					d55 = d69;
				}
				if (v55 > v70 + p55) {
					v55 = v70 + p55;
					d55 = d70;
				}
				if (v55 > v54 + p55) {
					v55 = v54 + p55;
					d55 = d54;
				}
		}
		if (rc.onTheMap(l63)) {
				p63 = Math.floor((1.0 + (double)rc.senseRubble(l63)/10.0)*cooldown);
				if (v63 > v79 + p63) {
					v63 = v79 + p63;
					d63 = d79;
				}
				if (v63 > v64 + p63) {
					v63 = v64 + p63;
					d63 = d64;
				}
				if (v63 > v78 + p63) {
					v63 = v78 + p63;
					d63 = d78;
				}
				if (v63 > v49 + p63) {
					v63 = v49 + p63;
					d63 = d49;
				}
		}
		if (rc.onTheMap(l71)) {
				p71 = Math.floor((1.0 + (double)rc.senseRubble(l71)/10.0)*cooldown);
				if (v71 > v85 + p71) {
					v71 = v85 + p71;
					d71 = d85;
				}
				if (v71 > v70 + p71) {
					v71 = v70 + p71;
					d71 = d70;
				}
				if (v71 > v86 + p71) {
					v71 = v86 + p71;
					d71 = d86;
				}
				if (v71 > v55 + p71) {
					v71 = v55 + p71;
					d71 = d55;
				}
		}
		if (rc.onTheMap(l107)) {
				p107 = Math.floor((1.0 + (double)rc.senseRubble(l107)/10.0)*cooldown);
				if (v107 > v108 + p107) {
					v107 = v108 + p107;
					d107 = d108;
				}
				if (v107 > v93 + p107) {
					v107 = v93 + p107;
					d107 = d93;
				}
				if (v107 > v123 + p107) {
					v107 = v123 + p107;
					d107 = d123;
				}
		}
		if (rc.onTheMap(l117)) {
				p117 = Math.floor((1.0 + (double)rc.senseRubble(l117)/10.0)*cooldown);
				if (v117 > v116 + p117) {
					v117 = v116 + p117;
					d117 = d116;
				}
				if (v117 > v101 + p117) {
					v117 = v101 + p117;
					d117 = d101;
				}
				if (v117 > v131 + p117) {
					v117 = v131 + p117;
					d117 = d131;
				}
		}
		if (rc.onTheMap(l153)) {
				p153 = Math.floor((1.0 + (double)rc.senseRubble(l153)/10.0)*cooldown);
				if (v153 > v139 + p153) {
					v153 = v139 + p153;
					d153 = d139;
				}
				if (v153 > v154 + p153) {
					v153 = v154 + p153;
					d153 = d154;
				}
				if (v153 > v138 + p153) {
					v153 = v138 + p153;
					d153 = d138;
				}
		}
		if (rc.onTheMap(l161)) {
				p161 = Math.floor((1.0 + (double)rc.senseRubble(l161)/10.0)*cooldown);
				if (v161 > v145 + p161) {
					v161 = v145 + p161;
					d161 = d145;
				}
				if (v161 > v160 + p161) {
					v161 = v160 + p161;
					d161 = d160;
				}
				if (v161 > v146 + p161) {
					v161 = v146 + p161;
					d161 = d146;
				}
		}
		if (rc.onTheMap(l169)) {
				p169 = Math.floor((1.0 + (double)rc.senseRubble(l169)/10.0)*cooldown);
				if (v169 > v155 + p169) {
					v169 = v155 + p169;
					d169 = d155;
				}
				if (v169 > v154 + p169) {
					v169 = v154 + p169;
					d169 = d154;
				}
				if (v169 > v170 + p169) {
					v169 = v170 + p169;
					d169 = d170;
				}
				if (v169 > v153 + p169) {
					v169 = v153 + p169;
					d169 = d153;
				}
		}
		if (rc.onTheMap(l175)) {
				p175 = Math.floor((1.0 + (double)rc.senseRubble(l175)/10.0)*cooldown);
				if (v175 > v159 + p175) {
					v175 = v159 + p175;
					d175 = d159;
				}
				if (v175 > v160 + p175) {
					v175 = v160 + p175;
					d175 = d160;
				}
				if (v175 > v174 + p175) {
					v175 = v174 + p175;
					d175 = d174;
				}
				if (v175 > v161 + p175) {
					v175 = v161 + p175;
					d175 = d161;
				}
		}
		if (rc.onTheMap(l187)) {
				p187 = Math.floor((1.0 + (double)rc.senseRubble(l187)/10.0)*cooldown);
				if (v187 > v172 + p187) {
					v187 = v172 + p187;
					d187 = d172;
				}
				if (v187 > v171 + p187) {
					v187 = v171 + p187;
					d187 = d171;
				}
				if (v187 > v173 + p187) {
					v187 = v173 + p187;
					d187 = d173;
				}
		}
		if (rc.onTheMap(l36)) {
				p36 = Math.floor((1.0 + (double)rc.senseRubble(l36)/10.0)*cooldown);
				if (v36 > v52 + p36) {
					v36 = v52 + p36;
					d36 = d52;
				}
				if (v36 > v51 + p36) {
					v36 = v51 + p36;
					d36 = d51;
				}
				if (v36 > v50 + p36) {
					v36 = v50 + p36;
					d36 = d50;
				}
				if (v36 > v37 + p36) {
					v36 = v37 + p36;
					d36 = d37;
				}
		}
		if (rc.onTheMap(l38)) {
				p38 = Math.floor((1.0 + (double)rc.senseRubble(l38)/10.0)*cooldown);
				if (v38 > v52 + p38) {
					v38 = v52 + p38;
					d38 = d52;
				}
				if (v38 > v53 + p38) {
					v38 = v53 + p38;
					d38 = d53;
				}
				if (v38 > v54 + p38) {
					v38 = v54 + p38;
					d38 = d54;
				}
				if (v38 > v37 + p38) {
					v38 = v37 + p38;
					d38 = d37;
				}
		}
		if (rc.onTheMap(l92)) {
				p92 = Math.floor((1.0 + (double)rc.senseRubble(l92)/10.0)*cooldown);
				if (v92 > v108 + p92) {
					v92 = v108 + p92;
					d92 = d108;
				}
				if (v92 > v93 + p92) {
					v92 = v93 + p92;
					d92 = d93;
				}
				if (v92 > v78 + p92) {
					v92 = v78 + p92;
					d92 = d78;
				}
				if (v92 > v107 + p92) {
					v92 = v107 + p92;
					d92 = d107;
				}
		}
		if (rc.onTheMap(l102)) {
				p102 = Math.floor((1.0 + (double)rc.senseRubble(l102)/10.0)*cooldown);
				if (v102 > v116 + p102) {
					v102 = v116 + p102;
					d102 = d116;
				}
				if (v102 > v101 + p102) {
					v102 = v101 + p102;
					d102 = d101;
				}
				if (v102 > v86 + p102) {
					v102 = v86 + p102;
					d102 = d86;
				}
				if (v102 > v117 + p102) {
					v102 = v117 + p102;
					d102 = d117;
				}
		}
		if (rc.onTheMap(l122)) {
				p122 = Math.floor((1.0 + (double)rc.senseRubble(l122)/10.0)*cooldown);
				if (v122 > v108 + p122) {
					v122 = v108 + p122;
					d122 = d108;
				}
				if (v122 > v123 + p122) {
					v122 = v123 + p122;
					d122 = d123;
				}
				if (v122 > v138 + p122) {
					v122 = v138 + p122;
					d122 = d138;
				}
				if (v122 > v107 + p122) {
					v122 = v107 + p122;
					d122 = d107;
				}
		}
		if (rc.onTheMap(l132)) {
				p132 = Math.floor((1.0 + (double)rc.senseRubble(l132)/10.0)*cooldown);
				if (v132 > v116 + p132) {
					v132 = v116 + p132;
					d132 = d116;
				}
				if (v132 > v131 + p132) {
					v132 = v131 + p132;
					d132 = d131;
				}
				if (v132 > v146 + p132) {
					v132 = v146 + p132;
					d132 = d146;
				}
				if (v132 > v117 + p132) {
					v132 = v117 + p132;
					d132 = d117;
				}
		}
		if (rc.onTheMap(l186)) {
				p186 = Math.floor((1.0 + (double)rc.senseRubble(l186)/10.0)*cooldown);
				if (v186 > v172 + p186) {
					v186 = v172 + p186;
					d186 = d172;
				}
				if (v186 > v171 + p186) {
					v186 = v171 + p186;
					d186 = d171;
				}
				if (v186 > v170 + p186) {
					v186 = v170 + p186;
					d186 = d170;
				}
				if (v186 > v187 + p186) {
					v186 = v187 + p186;
					d186 = d187;
				}
		}
		if (rc.onTheMap(l188)) {
				p188 = Math.floor((1.0 + (double)rc.senseRubble(l188)/10.0)*cooldown);
				if (v188 > v172 + p188) {
					v188 = v172 + p188;
					d188 = d172;
				}
				if (v188 > v173 + p188) {
					v188 = v173 + p188;
					d188 = d173;
				}
				if (v188 > v174 + p188) {
					v188 = v174 + p188;
					d188 = d174;
				}
				if (v188 > v187 + p188) {
					v188 = v187 + p188;
					d188 = d187;
				}
		}
		if (rc.onTheMap(l35)) {
				p35 = Math.floor((1.0 + (double)rc.senseRubble(l35)/10.0)*cooldown);
				if (v35 > v51 + p35) {
					v35 = v51 + p35;
					d35 = d51;
				}
				if (v35 > v50 + p35) {
					v35 = v50 + p35;
					d35 = d50;
				}
				if (v35 > v49 + p35) {
					v35 = v49 + p35;
					d35 = d49;
				}
				if (v35 > v36 + p35) {
					v35 = v36 + p35;
					d35 = d36;
				}
		}
		if (rc.onTheMap(l39)) {
				p39 = Math.floor((1.0 + (double)rc.senseRubble(l39)/10.0)*cooldown);
				if (v39 > v53 + p39) {
					v39 = v53 + p39;
					d39 = d53;
				}
				if (v39 > v54 + p39) {
					v39 = v54 + p39;
					d39 = d54;
				}
				if (v39 > v55 + p39) {
					v39 = v55 + p39;
					d39 = d55;
				}
				if (v39 > v38 + p39) {
					v39 = v38 + p39;
					d39 = d38;
				}
		}
		if (rc.onTheMap(l77)) {
				p77 = Math.floor((1.0 + (double)rc.senseRubble(l77)/10.0)*cooldown);
				if (v77 > v93 + p77) {
					v77 = v93 + p77;
					d77 = d93;
				}
				if (v77 > v78 + p77) {
					v77 = v78 + p77;
					d77 = d78;
				}
				if (v77 > v63 + p77) {
					v77 = v63 + p77;
					d77 = d63;
				}
				if (v77 > v92 + p77) {
					v77 = v92 + p77;
					d77 = d92;
				}
		}
		if (rc.onTheMap(l87)) {
				p87 = Math.floor((1.0 + (double)rc.senseRubble(l87)/10.0)*cooldown);
				if (v87 > v101 + p87) {
					v87 = v101 + p87;
					d87 = d101;
				}
				if (v87 > v86 + p87) {
					v87 = v86 + p87;
					d87 = d86;
				}
				if (v87 > v71 + p87) {
					v87 = v71 + p87;
					d87 = d71;
				}
				if (v87 > v102 + p87) {
					v87 = v102 + p87;
					d87 = d102;
				}
		}
		if (rc.onTheMap(l137)) {
				p137 = Math.floor((1.0 + (double)rc.senseRubble(l137)/10.0)*cooldown);
				if (v137 > v123 + p137) {
					v137 = v123 + p137;
					d137 = d123;
				}
				if (v137 > v138 + p137) {
					v137 = v138 + p137;
					d137 = d138;
				}
				if (v137 > v153 + p137) {
					v137 = v153 + p137;
					d137 = d153;
				}
				if (v137 > v122 + p137) {
					v137 = v122 + p137;
					d137 = d122;
				}
		}
		if (rc.onTheMap(l147)) {
				p147 = Math.floor((1.0 + (double)rc.senseRubble(l147)/10.0)*cooldown);
				if (v147 > v131 + p147) {
					v147 = v131 + p147;
					d147 = d131;
				}
				if (v147 > v146 + p147) {
					v147 = v146 + p147;
					d147 = d146;
				}
				if (v147 > v161 + p147) {
					v147 = v161 + p147;
					d147 = d161;
				}
				if (v147 > v132 + p147) {
					v147 = v132 + p147;
					d147 = d132;
				}
		}
		if (rc.onTheMap(l185)) {
				p185 = Math.floor((1.0 + (double)rc.senseRubble(l185)/10.0)*cooldown);
				if (v185 > v171 + p185) {
					v185 = v171 + p185;
					d185 = d171;
				}
				if (v185 > v170 + p185) {
					v185 = v170 + p185;
					d185 = d170;
				}
				if (v185 > v169 + p185) {
					v185 = v169 + p185;
					d185 = d169;
				}
				if (v185 > v186 + p185) {
					v185 = v186 + p185;
					d185 = d186;
				}
		}
		if (rc.onTheMap(l189)) {
				p189 = Math.floor((1.0 + (double)rc.senseRubble(l189)/10.0)*cooldown);
				if (v189 > v173 + p189) {
					v189 = v173 + p189;
					d189 = d173;
				}
				if (v189 > v174 + p189) {
					v189 = v174 + p189;
					d189 = d174;
				}
				if (v189 > v175 + p189) {
					v189 = v175 + p189;
					d189 = d175;
				}
				if (v189 > v188 + p189) {
					v189 = v188 + p189;
					d189 = d188;
				}
		}
		if (rc.onTheMap(l48)) {
				p48 = Math.floor((1.0 + (double)rc.senseRubble(l48)/10.0)*cooldown);
				if (v48 > v64 + p48) {
					v48 = v64 + p48;
					d48 = d64;
				}
				if (v48 > v49 + p48) {
					v48 = v49 + p48;
					d48 = d49;
				}
				if (v48 > v63 + p48) {
					v48 = v63 + p48;
					d48 = d63;
				}
		}
		if (rc.onTheMap(l56)) {
				p56 = Math.floor((1.0 + (double)rc.senseRubble(l56)/10.0)*cooldown);
				if (v56 > v70 + p56) {
					v56 = v70 + p56;
					d56 = d70;
				}
				if (v56 > v55 + p56) {
					v56 = v55 + p56;
					d56 = d55;
				}
				if (v56 > v71 + p56) {
					v56 = v71 + p56;
					d56 = d71;
				}
		}
		if (rc.onTheMap(l168)) {
				p168 = Math.floor((1.0 + (double)rc.senseRubble(l168)/10.0)*cooldown);
				if (v168 > v154 + p168) {
					v168 = v154 + p168;
					d168 = d154;
				}
				if (v168 > v153 + p168) {
					v168 = v153 + p168;
					d168 = d153;
				}
				if (v168 > v169 + p168) {
					v168 = v169 + p168;
					d168 = d169;
				}
		}
		if (rc.onTheMap(l176)) {
				p176 = Math.floor((1.0 + (double)rc.senseRubble(l176)/10.0)*cooldown);
				if (v176 > v160 + p176) {
					v176 = v160 + p176;
					d176 = d160;
				}
				if (v176 > v161 + p176) {
					v176 = v161 + p176;
					d176 = d161;
				}
				if (v176 > v175 + p176) {
					v176 = v175 + p176;
					d176 = d175;
				}
		}
		if (rc.onTheMap(l34)) {
				p34 = Math.floor((1.0 + (double)rc.senseRubble(l34)/10.0)*cooldown);
				if (v34 > v50 + p34) {
					v34 = v50 + p34;
					d34 = d50;
				}
				if (v34 > v49 + p34) {
					v34 = v49 + p34;
					d34 = d49;
				}
				if (v34 > v35 + p34) {
					v34 = v35 + p34;
					d34 = d35;
				}
				if (v34 > v48 + p34) {
					v34 = v48 + p34;
					d34 = d48;
				}
		}
		if (rc.onTheMap(l40)) {
				p40 = Math.floor((1.0 + (double)rc.senseRubble(l40)/10.0)*cooldown);
				if (v40 > v54 + p40) {
					v40 = v54 + p40;
					d40 = d54;
				}
				if (v40 > v55 + p40) {
					v40 = v55 + p40;
					d40 = d55;
				}
				if (v40 > v39 + p40) {
					v40 = v39 + p40;
					d40 = d39;
				}
				if (v40 > v56 + p40) {
					v40 = v56 + p40;
					d40 = d56;
				}
		}
		if (rc.onTheMap(l62)) {
				p62 = Math.floor((1.0 + (double)rc.senseRubble(l62)/10.0)*cooldown);
				if (v62 > v78 + p62) {
					v62 = v78 + p62;
					d62 = d78;
				}
				if (v62 > v63 + p62) {
					v62 = v63 + p62;
					d62 = d63;
				}
				if (v62 > v77 + p62) {
					v62 = v77 + p62;
					d62 = d77;
				}
				if (v62 > v48 + p62) {
					v62 = v48 + p62;
					d62 = d48;
				}
		}
		if (rc.onTheMap(l72)) {
				p72 = Math.floor((1.0 + (double)rc.senseRubble(l72)/10.0)*cooldown);
				if (v72 > v86 + p72) {
					v72 = v86 + p72;
					d72 = d86;
				}
				if (v72 > v71 + p72) {
					v72 = v71 + p72;
					d72 = d71;
				}
				if (v72 > v87 + p72) {
					v72 = v87 + p72;
					d72 = d87;
				}
				if (v72 > v56 + p72) {
					v72 = v56 + p72;
					d72 = d56;
				}
		}
		if (rc.onTheMap(l152)) {
				p152 = Math.floor((1.0 + (double)rc.senseRubble(l152)/10.0)*cooldown);
				if (v152 > v138 + p152) {
					v152 = v138 + p152;
					d152 = d138;
				}
				if (v152 > v153 + p152) {
					v152 = v153 + p152;
					d152 = d153;
				}
				if (v152 > v137 + p152) {
					v152 = v137 + p152;
					d152 = d137;
				}
				if (v152 > v168 + p152) {
					v152 = v168 + p152;
					d152 = d168;
				}
		}
		if (rc.onTheMap(l162)) {
				p162 = Math.floor((1.0 + (double)rc.senseRubble(l162)/10.0)*cooldown);
				if (v162 > v146 + p162) {
					v162 = v146 + p162;
					d162 = d146;
				}
				if (v162 > v161 + p162) {
					v162 = v161 + p162;
					d162 = d161;
				}
				if (v162 > v147 + p162) {
					v162 = v147 + p162;
					d162 = d147;
				}
				if (v162 > v176 + p162) {
					v162 = v176 + p162;
					d162 = d176;
				}
		}
		if (rc.onTheMap(l184)) {
				p184 = Math.floor((1.0 + (double)rc.senseRubble(l184)/10.0)*cooldown);
				if (v184 > v170 + p184) {
					v184 = v170 + p184;
					d184 = d170;
				}
				if (v184 > v169 + p184) {
					v184 = v169 + p184;
					d184 = d169;
				}
				if (v184 > v185 + p184) {
					v184 = v185 + p184;
					d184 = d185;
				}
				if (v184 > v168 + p184) {
					v184 = v168 + p184;
					d184 = d168;
				}
		}
		if (rc.onTheMap(l190)) {
				p190 = Math.floor((1.0 + (double)rc.senseRubble(l190)/10.0)*cooldown);
				if (v190 > v174 + p190) {
					v190 = v174 + p190;
					d190 = d174;
				}
				if (v190 > v175 + p190) {
					v190 = v175 + p190;
					d190 = d175;
				}
				if (v190 > v189 + p190) {
					v190 = v189 + p190;
					d190 = d189;
				}
				if (v190 > v176 + p190) {
					v190 = v176 + p190;
					d190 = d176;
				}
		}
		int dx = target.x - l84.x;
		int dy = target.y - l84.y;
		switch (dx) {
			case -5:
				switch (dy) {
					case -3:
						return d62;
					case -2:
						return d77;
					case -1:
						return d92;
					case 0:
						return d107;
					case 1:
						return d122;
					case 2:
						return d137;
					case 3:
						return d152;
				}
				break;
			case -4:
				switch (dy) {
					case -4:
						return d48;
					case -3:
						return d63;
					case -2:
						return d78;
					case -1:
						return d93;
					case 0:
						return d108;
					case 1:
						return d123;
					case 2:
						return d138;
					case 3:
						return d153;
					case 4:
						return d168;
				}
				break;
			case -3:
				switch (dy) {
					case -5:
						return d34;
					case -4:
						return d49;
					case -3:
						return d64;
					case -2:
						return d79;
					case -1:
						return d94;
					case 0:
						return d109;
					case 1:
						return d124;
					case 2:
						return d139;
					case 3:
						return d154;
					case 4:
						return d169;
					case 5:
						return d184;
				}
				break;
			case -2:
				switch (dy) {
					case -5:
						return d35;
					case -4:
						return d50;
					case -3:
						return d65;
					case -2:
						return d80;
					case -1:
						return d95;
					case 0:
						return d110;
					case 1:
						return d125;
					case 2:
						return d140;
					case 3:
						return d155;
					case 4:
						return d170;
					case 5:
						return d185;
				}
				break;
			case -1:
				switch (dy) {
					case -5:
						return d36;
					case -4:
						return d51;
					case -3:
						return d66;
					case -2:
						return d81;
					case -1:
						return d96;
					case 0:
						return d111;
					case 1:
						return d126;
					case 2:
						return d141;
					case 3:
						return d156;
					case 4:
						return d171;
					case 5:
						return d186;
				}
				break;
			case 0:
				switch (dy) {
					case -5:
						return d37;
					case -4:
						return d52;
					case -3:
						return d67;
					case -2:
						return d82;
					case -1:
						return d97;
					case 0:
						return d112;
					case 1:
						return d127;
					case 2:
						return d142;
					case 3:
						return d157;
					case 4:
						return d172;
					case 5:
						return d187;
				}
				break;
			case 1:
				switch (dy) {
					case -5:
						return d38;
					case -4:
						return d53;
					case -3:
						return d68;
					case -2:
						return d83;
					case -1:
						return d98;
					case 0:
						return d113;
					case 1:
						return d128;
					case 2:
						return d143;
					case 3:
						return d158;
					case 4:
						return d173;
					case 5:
						return d188;
				}
				break;
			case 2:
				switch (dy) {
					case -5:
						return d39;
					case -4:
						return d54;
					case -3:
						return d69;
					case -2:
						return d84;
					case -1:
						return d99;
					case 0:
						return d114;
					case 1:
						return d129;
					case 2:
						return d144;
					case 3:
						return d159;
					case 4:
						return d174;
					case 5:
						return d189;
				}
				break;
			case 3:
				switch (dy) {
					case -5:
						return d40;
					case -4:
						return d55;
					case -3:
						return d70;
					case -2:
						return d85;
					case -1:
						return d100;
					case 0:
						return d115;
					case 1:
						return d130;
					case 2:
						return d145;
					case 3:
						return d160;
					case 4:
						return d175;
					case 5:
						return d190;
				}
				break;
			case 4:
				switch (dy) {
					case -4:
						return d56;
					case -3:
						return d71;
					case -2:
						return d86;
					case -1:
						return d101;
					case 0:
						return d116;
					case 1:
						return d131;
					case 2:
						return d146;
					case 3:
						return d161;
					case 4:
						return d176;
				}
				break;
			case 5:
				switch (dy) {
					case -3:
						return d72;
					case -2:
						return d87;
					case -1:
						return d102;
					case 0:
						return d117;
					case 1:
						return d132;
					case 2:
						return d147;
					case 3:
						return d162;
				}
				break;
		}
		Direction ans = null;
		double bestEstimation = 0;
		double initialDist = Math.sqrt(l84.distanceSquaredTo(target));
		double dist37 = (initialDist - Math.sqrt(l37.distanceSquaredTo(target))) / v37;
		if (dist37 > bestEstimation) {
			bestEstimation = dist37;
			ans = d37;
		}
		double dist107 = (initialDist - Math.sqrt(l107.distanceSquaredTo(target))) / v107;
		if (dist107 > bestEstimation) {
			bestEstimation = dist107;
			ans = d107;
		}
		double dist117 = (initialDist - Math.sqrt(l117.distanceSquaredTo(target))) / v117;
		if (dist117 > bestEstimation) {
			bestEstimation = dist117;
			ans = d117;
		}
		double dist187 = (initialDist - Math.sqrt(l187.distanceSquaredTo(target))) / v187;
		if (dist187 > bestEstimation) {
			bestEstimation = dist187;
			ans = d187;
		}
		double dist36 = (initialDist - Math.sqrt(l36.distanceSquaredTo(target))) / v36;
		if (dist36 > bestEstimation) {
			bestEstimation = dist36;
			ans = d36;
		}
		double dist38 = (initialDist - Math.sqrt(l38.distanceSquaredTo(target))) / v38;
		if (dist38 > bestEstimation) {
			bestEstimation = dist38;
			ans = d38;
		}
		double dist92 = (initialDist - Math.sqrt(l92.distanceSquaredTo(target))) / v92;
		if (dist92 > bestEstimation) {
			bestEstimation = dist92;
			ans = d92;
		}
		double dist102 = (initialDist - Math.sqrt(l102.distanceSquaredTo(target))) / v102;
		if (dist102 > bestEstimation) {
			bestEstimation = dist102;
			ans = d102;
		}
		double dist122 = (initialDist - Math.sqrt(l122.distanceSquaredTo(target))) / v122;
		if (dist122 > bestEstimation) {
			bestEstimation = dist122;
			ans = d122;
		}
		double dist132 = (initialDist - Math.sqrt(l132.distanceSquaredTo(target))) / v132;
		if (dist132 > bestEstimation) {
			bestEstimation = dist132;
			ans = d132;
		}
		double dist186 = (initialDist - Math.sqrt(l186.distanceSquaredTo(target))) / v186;
		if (dist186 > bestEstimation) {
			bestEstimation = dist186;
			ans = d186;
		}
		double dist188 = (initialDist - Math.sqrt(l188.distanceSquaredTo(target))) / v188;
		if (dist188 > bestEstimation) {
			bestEstimation = dist188;
			ans = d188;
		}
		double dist35 = (initialDist - Math.sqrt(l35.distanceSquaredTo(target))) / v35;
		if (dist35 > bestEstimation) {
			bestEstimation = dist35;
			ans = d35;
		}
		double dist39 = (initialDist - Math.sqrt(l39.distanceSquaredTo(target))) / v39;
		if (dist39 > bestEstimation) {
			bestEstimation = dist39;
			ans = d39;
		}
		double dist77 = (initialDist - Math.sqrt(l77.distanceSquaredTo(target))) / v77;
		if (dist77 > bestEstimation) {
			bestEstimation = dist77;
			ans = d77;
		}
		double dist87 = (initialDist - Math.sqrt(l87.distanceSquaredTo(target))) / v87;
		if (dist87 > bestEstimation) {
			bestEstimation = dist87;
			ans = d87;
		}
		double dist137 = (initialDist - Math.sqrt(l137.distanceSquaredTo(target))) / v137;
		if (dist137 > bestEstimation) {
			bestEstimation = dist137;
			ans = d137;
		}
		double dist147 = (initialDist - Math.sqrt(l147.distanceSquaredTo(target))) / v147;
		if (dist147 > bestEstimation) {
			bestEstimation = dist147;
			ans = d147;
		}
		double dist185 = (initialDist - Math.sqrt(l185.distanceSquaredTo(target))) / v185;
		if (dist185 > bestEstimation) {
			bestEstimation = dist185;
			ans = d185;
		}
		double dist189 = (initialDist - Math.sqrt(l189.distanceSquaredTo(target))) / v189;
		if (dist189 > bestEstimation) {
			bestEstimation = dist189;
			ans = d189;
		}
		double dist48 = (initialDist - Math.sqrt(l48.distanceSquaredTo(target))) / v48;
		if (dist48 > bestEstimation) {
			bestEstimation = dist48;
			ans = d48;
		}
		double dist56 = (initialDist - Math.sqrt(l56.distanceSquaredTo(target))) / v56;
		if (dist56 > bestEstimation) {
			bestEstimation = dist56;
			ans = d56;
		}
		double dist168 = (initialDist - Math.sqrt(l168.distanceSquaredTo(target))) / v168;
		if (dist168 > bestEstimation) {
			bestEstimation = dist168;
			ans = d168;
		}
		double dist176 = (initialDist - Math.sqrt(l176.distanceSquaredTo(target))) / v176;
		if (dist176 > bestEstimation) {
			bestEstimation = dist176;
			ans = d176;
		}
		double dist34 = (initialDist - Math.sqrt(l34.distanceSquaredTo(target))) / v34;
		if (dist34 > bestEstimation) {
			bestEstimation = dist34;
			ans = d34;
		}
		double dist40 = (initialDist - Math.sqrt(l40.distanceSquaredTo(target))) / v40;
		if (dist40 > bestEstimation) {
			bestEstimation = dist40;
			ans = d40;
		}
		double dist62 = (initialDist - Math.sqrt(l62.distanceSquaredTo(target))) / v62;
		if (dist62 > bestEstimation) {
			bestEstimation = dist62;
			ans = d62;
		}
		double dist72 = (initialDist - Math.sqrt(l72.distanceSquaredTo(target))) / v72;
		if (dist72 > bestEstimation) {
			bestEstimation = dist72;
			ans = d72;
		}
		double dist152 = (initialDist - Math.sqrt(l152.distanceSquaredTo(target))) / v152;
		if (dist152 > bestEstimation) {
			bestEstimation = dist152;
			ans = d152;
		}
		double dist162 = (initialDist - Math.sqrt(l162.distanceSquaredTo(target))) / v162;
		if (dist162 > bestEstimation) {
			bestEstimation = dist162;
			ans = d162;
		}
		double dist184 = (initialDist - Math.sqrt(l184.distanceSquaredTo(target))) / v184;
		if (dist184 > bestEstimation) {
			bestEstimation = dist184;
			ans = d184;
		}
		double dist190 = (initialDist - Math.sqrt(l190.distanceSquaredTo(target))) / v190;
		if (dist190 > bestEstimation) {
			bestEstimation = dist190;
			ans = d190;
		}
		return ans;
	}

}