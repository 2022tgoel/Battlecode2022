package baseline;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

/**
 * RobotPlayer is the class that describes your main robot strategy.
 * The run() method inside this class is like your main function: this is what
 * we'll call once your robot
 * is created!
 */
public strictfp class RobotPlayer {

	/**
	 * We will use this variable to count the number of turns this robot has been
	 * alive.
	 * You can use static variables like this to save any information you want. Keep
	 * in mind that even though
	 * these variables are static, in Battlecode they aren't actually shared between
	 * your robots.
	 */
	static int turnCount = 0;

	/**
	 * run() is the method that is called when a robot is instantiated in the
	 * Battlecode world.
	 * It is like the main function for your robot. If this method returns, the
	 * robot dies!
	 *
	 * @param rc The RobotController object. You use it to perform actions from this
	 *           robot, and to get
	 *           information on its current status. Essentially your portal to
	 *           interacting with the world.
	 **/
	@SuppressWarnings("unused")
	public static void run(RobotController rc) throws GameActionException {
		while (true) {
			Clock.yield();
		}
	}
}