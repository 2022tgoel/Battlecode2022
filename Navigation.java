import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
public class Navigation {
	static RobotController rc;	Navigation(RobotController rc) {
		this.rc= rc;
	}

	static MapLocation l31;
	static double v31;
	static Direction d31;
	static double p31;

	static MapLocation l32;
	static double v32;
	static Direction d32;
	static double p32;

	static MapLocation l33;
	static double v33;
	static Direction d33;
	static double p33;

	static MapLocation l42;
	static double v42;
	static Direction d42;
	static double p42;

	static MapLocation l43;
	static double v43;
	static Direction d43;
	static double p43;

	static MapLocation l44;
	static double v44;
	static Direction d44;
	static double p44;

	static MapLocation l45;
	static double v45;
	static Direction d45;
	static double p45;

	static MapLocation l46;
	static double v46;
	static Direction d46;
	static double p46;

	static MapLocation l47;
	static double v47;
	static Direction d47;
	static double p47;

	static MapLocation l48;
	static double v48;
	static Direction d48;
	static double p48;

	static MapLocation l55;
	static double v55;
	static Direction d55;
	static double p55;

	static MapLocation l56;
	static double v56;
	static Direction d56;
	static double p56;

	static MapLocation l57;
	static double v57;
	static Direction d57;
	static double p57;

	static MapLocation l58;
	static double v58;
	static Direction d58;
	static double p58;

	static MapLocation l59;
	static double v59;
	static Direction d59;
	static double p59;

	static MapLocation l60;
	static double v60;
	static Direction d60;
	static double p60;

	static MapLocation l61;
	static double v61;
	static Direction d61;
	static double p61;

	static MapLocation l67;
	static double v67;
	static Direction d67;
	static double p67;

	static MapLocation l68;
	static double v68;
	static Direction d68;
	static double p68;

	static MapLocation l69;
	static double v69;
	static Direction d69;
	static double p69;

	static MapLocation l70;
	static double v70;
	static Direction d70;
	static double p70;

	static MapLocation l71;
	static double v71;
	static Direction d71;
	static double p71;

	static MapLocation l72;
	static double v72;
	static Direction d72;
	static double p72;

	static MapLocation l73;
	static double v73;
	static Direction d73;
	static double p73;

	static MapLocation l74;
	static double v74;
	static Direction d74;
	static double p74;

	static MapLocation l75;
	static double v75;
	static Direction d75;
	static double p75;

	static MapLocation l80;
	static double v80;
	static Direction d80;
	static double p80;

	static MapLocation l81;
	static double v81;
	static Direction d81;
	static double p81;

	static MapLocation l82;
	static double v82;
	static Direction d82;
	static double p82;

	static MapLocation l83;
	static double v83;
	static Direction d83;
	static double p83;

	static MapLocation l84;
	static double v84;
	static Direction d84;
	static double p84;

	static MapLocation l85;
	static double v85;
	static Direction d85;
	static double p85;

	static MapLocation l86;
	static double v86;
	static Direction d86;
	static double p86;

	static MapLocation l87;
	static double v87;
	static Direction d87;
	static double p87;

	static MapLocation l88;
	static double v88;
	static Direction d88;
	static double p88;

	static MapLocation l93;
	static double v93;
	static Direction d93;
	static double p93;

	static MapLocation l94;
	static double v94;
	static Direction d94;
	static double p94;

	static MapLocation l95;
	static double v95;
	static Direction d95;
	static double p95;

	static MapLocation l96;
	static double v96;
	static Direction d96;
	static double p96;

	static MapLocation l97;
	static double v97;
	static Direction d97;
	static double p97;

	static MapLocation l98;
	static double v98;
	static Direction d98;
	static double p98;

	static MapLocation l99;
	static double v99;
	static Direction d99;
	static double p99;

	static MapLocation l100;
	static double v100;
	static Direction d100;
	static double p100;

	static MapLocation l101;
	static double v101;
	static Direction d101;
	static double p101;

	static MapLocation l107;
	static double v107;
	static Direction d107;
	static double p107;

	static MapLocation l108;
	static double v108;
	static Direction d108;
	static double p108;

	static MapLocation l109;
	static double v109;
	static Direction d109;
	static double p109;

	static MapLocation l110;
	static double v110;
	static Direction d110;
	static double p110;

	static MapLocation l111;
	static double v111;
	static Direction d111;
	static double p111;

	static MapLocation l112;
	static double v112;
	static Direction d112;
	static double p112;

	static MapLocation l113;
	static double v113;
	static Direction d113;
	static double p113;

	static MapLocation l120;
	static double v120;
	static Direction d120;
	static double p120;

	static MapLocation l121;
	static double v121;
	static Direction d121;
	static double p121;

	static MapLocation l122;
	static double v122;
	static Direction d122;
	static double p122;

	static MapLocation l123;
	static double v123;
	static Direction d123;
	static double p123;

	static MapLocation l124;
	static double v124;
	static Direction d124;
	static double p124;

	static MapLocation l125;
	static double v125;
	static Direction d125;
	static double p125;

	static MapLocation l126;
	static double v126;
	static Direction d126;
	static double p126;

	static MapLocation l135;
	static double v135;
	static Direction d135;
	static double p135;

	static MapLocation l136;
	static double v136;
	static Direction d136;
	static double p136;

	static MapLocation l137;
	static double v137;
	static Direction d137;
	static double p137;


	Direction getBestDir(MapLocation target) throws GameActionException{
		l31 = ;
		v31 = 1000000;
		d31 = null;
		l32 = ;
		v32 = 1000000;
		d32 = null;
		l33 = ;
		v33 = 1000000;
		d33 = null;
		l42 = ;
		v42 = 1000000;
		d42 = null;
		l43 = ;
		v43 = 1000000;
		d43 = null;
		l44 = ;
		v44 = 1000000;
		d44 = null;
		l45 = ;
		v45 = 1000000;
		d45 = null;
		l46 = ;
		v46 = 1000000;
		d46 = null;
		l47 = ;
		v47 = 1000000;
		d47 = null;
		l48 = ;
		v48 = 1000000;
		d48 = null;
		l55 = ;
		v55 = 1000000;
		d55 = null;
		l56 = ;
		v56 = 1000000;
		d56 = null;
		l57 = ;
		v57 = 1000000;
		d57 = null;
		l58 = ;
		v58 = 1000000;
		d58 = null;
		l59 = ;
		v59 = 1000000;
		d59 = null;
		l60 = ;
		v60 = 1000000;
		d60 = null;
		l61 = ;
		v61 = 1000000;
		d61 = null;
		l67 = ;
		v67 = 1000000;
		d67 = null;
		l68 = ;
		v68 = 1000000;
		d68 = null;
		l69 = ;
		v69 = 1000000;
		d69 = null;
		l70 = ;
		v70 = 1000000;
		d70 = null;
		l71 = ;
		v71 = 1000000;
		d71 = null;
		l72 = ;
		v72 = 1000000;
		d72 = null;
		l73 = ;
		v73 = 1000000;
		d73 = null;
		l74 = ;
		v74 = 1000000;
		d74 = null;
		l75 = ;
		v75 = 1000000;
		d75 = null;
		l80 = ;
		v80 = 1000000;
		d80 = null;
		l81 = ;
		v81 = 1000000;
		d81 = null;
		l82 = ;
		v82 = 1000000;
		d82 = null;
		l83 = ;
		v83 = 1000000;
		d83 = null;
		l84 = ;
		v84 = 0;
		d84 = null;
		l85 = ;
		v85 = 1000000;
		d85 = null;
		l86 = ;
		v86 = 1000000;
		d86 = null;
		l87 = ;
		v87 = 1000000;
		d87 = null;
		l88 = ;
		v88 = 1000000;
		d88 = null;
		l93 = ;
		v93 = 1000000;
		d93 = null;
		l94 = ;
		v94 = 1000000;
		d94 = null;
		l95 = ;
		v95 = 1000000;
		d95 = null;
		l96 = ;
		v96 = 1000000;
		d96 = null;
		l97 = ;
		v97 = 1000000;
		d97 = null;
		l98 = ;
		v98 = 1000000;
		d98 = null;
		l99 = ;
		v99 = 1000000;
		d99 = null;
		l100 = ;
		v100 = 1000000;
		d100 = null;
		l101 = ;
		v101 = 1000000;
		d101 = null;
		l107 = ;
		v107 = 1000000;
		d107 = null;
		l108 = ;
		v108 = 1000000;
		d108 = null;
		l109 = ;
		v109 = 1000000;
		d109 = null;
		l110 = ;
		v110 = 1000000;
		d110 = null;
		l111 = ;
		v111 = 1000000;
		d111 = null;
		l112 = ;
		v112 = 1000000;
		d112 = null;
		l113 = ;
		v113 = 1000000;
		d113 = null;
		l120 = ;
		v120 = 1000000;
		d120 = null;
		l121 = ;
		v121 = 1000000;
		d121 = null;
		l122 = ;
		v122 = 1000000;
		d122 = null;
		l123 = ;
		v123 = 1000000;
		d123 = null;
		l124 = ;
		v124 = 1000000;
		d124 = null;
		l125 = ;
		v125 = 1000000;
		d125 = null;
		l126 = ;
		v126 = 1000000;
		d126 = null;
		l135 = ;
		v135 = 1000000;
		d135 = null;
		l136 = ;
		v136 = 1000000;
		d136 = null;
		l137 = ;
		v137 = 1000000;
		d137 = null;
	}

}