/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code	  */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package robot;

import edu.wpi.first.wpilibj.TimedRobot;
import systems.drive.DriveSystem;
import utilities.LogUtil;
import utilities.TimingUtil2;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

	public static Robot self = null;

	public static enum RobotState { DISABLED, TELEOP, AUTONOMOUS, TEST };

	private static boolean prevHatchLastAction = true;
	private static boolean hatchLastAction = true;

	private long count = 0;
	private long count2 = 0;
	private int count3 = 0;

	public static RobotState getState () {
		if (self == null || self.isDisabled()) { return RobotState.DISABLED; }
		else if (self.isOperatorControl()) { return RobotState.TELEOP; }
		else if (self.isAutonomous()) { return RobotState.AUTONOMOUS; }
		else if (self.isTest()) { return RobotState.TEST; }
		return RobotState.DISABLED;
	}

	public static void setHatchAction () {
		if (hatchLastAction != prevHatchLastAction) { LogUtil.log("Cargo/Hatch", "Thinks it has a hatch."); }
		prevHatchLastAction = hatchLastAction;
		hatchLastAction = true;
	}

	public static void setCargoAction () {
		if (hatchLastAction != prevHatchLastAction) { LogUtil.log("Cargo/Hatch", "Thinks it has a cargo."); }
		prevHatchLastAction = hatchLastAction;
		hatchLastAction = false;
	}

	public static boolean wasHatchLastAction () {
		return hatchLastAction;
	}

	public static boolean wasCargoLastAction () {
		return !wasHatchLastAction();
	}

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit () {
		self = this;

		TimingUtil2.registerOneTimeCallback(500, () -> { LogUtil.log("TimingUtil2Verification", "TimingUtil2 one time callback worked!"); });
		LogUtil.log("INIT", "Startin g LogUtil");
		Devices.init();

		new DriveSystem();
	}

	@Override public void disabledInit () { LogUtil.log("ROBOT_STATE", "DISABLED");  }

	@Override public void autonomousInit () { LogUtil.log("ROBOT_STATE", "AUTON ENABLED"); }

	@Override public void teleopInit () { LogUtil.log("ROBOT_STATE", "TELEOP ENABLED");  }

	@Override public void testInit () { LogUtil.log("ROBOT_STATE", "TEST ENABLED"); }
	
	@Override public void robotPeriodic() { }

	@Override public void disabledPeriodic () { }
	
	@Override public void autonomousPeriodic () { }

	@Override public void teleopPeriodic () {
		
	}

	@Override public void testPeriodic () { }

}
