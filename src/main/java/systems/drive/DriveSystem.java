package systems.drive;

import java.util.HashMap;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import robot.Controller;
import robot.Devices;
import robot.Controller.Button;
import robot.Controller.Axis;
import robot.Controller.ButtonEvent;
import systems.drive.controllers.NormalDriveController;
import systems.RobotSystem;
import systems.drive.controllers.IDriveController;
import systems.drive.controllers.SwerveController;
import systems.drive.controllers.SwerveController.CvtMode;
import systems.drive.controllers.SwerveController.SwerveMode;

public class DriveSystem extends RobotSystem {

	public static enum DriveController { NORMAL, OPERATOR, AUTON_CARGO_TO_FEEDER, AUTON_ROCKET_TO_FEEDER, LIMELIGHT_ALIGN, LIGHT_ALIGN, TEST; }

	private SwerveController swerveController;
	private Controller driverController;
	private Controller opController;
	private HashMap<DriveController,IDriveController> driveControllerMap;

	private IDriveController activeDriveController;
	private DriveController currentDriveController;
	private DriveController nextDriveController;

	public DriveSystem () {
		super("Drive System");
		swerveController = Devices.getSwerveController();
	}

	@Override
	public void init () {
		driverController = Devices.getDriverController();

		Runnable returnToNormalController = () -> {
			callback("State machine complete, returning control to Normal.");
			nextDriveController = DriveController.TEST;
		};

		driveControllerMap = new HashMap<>();
		driveControllerMap.put(DriveController.NORMAL, new NormalDriveController(swerveController));
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}

		currentDriveController = null;

		/*
		 * DRIVER CONTROLS
		 */
		driverController.registerButtonListener(ButtonEvent.PRESS, Button.START, () -> {
			callback("reset gyro");
			Devices.getGyro().resetGyro(); 
		});
		
		driverController.registerButtonListener(ButtonEvent.PRESS, Button.Y, () -> {
			callback("switch to light align mode...");
			if (nextDriveController == DriveController.NORMAL) {
				nextDriveController = DriveController.LIMELIGHT_ALIGN;
			}
			else {
				nextDriveController = DriveController.NORMAL;
			}
		});

		driverController.registerButtonListener(ButtonEvent.PRESS, Button.X, () -> {
			if (currentDriveController == DriveController.OPERATOR) {
				callback("switch to normal drive mode...");
				nextDriveController = DriveController.NORMAL;
			}
		});
	}

	@Override
	public void disable () {
		swerveController.disable();	
	}

	@Override
	public void enable () {
		swerveController.enable();
	}

	@Override public void preStateUpdate () { }

	@Override public void postStateUpdate () { }

	@Override public void disabledInit () {
		driverController.vibrate(RumbleType.kLeftRumble, 0.0);
		driverController.vibrate(RumbleType.kRightRumble, 0.0);
		if (currentDriveController != null) {
			driveControllerMap.get(currentDriveController).deactivate();
			currentDriveController = null;
		}
	}

	@Override public void disabledUpdate () { }

	@Override public void autonInit () {
		swerveController.setSwerveMode(SwerveMode.ROBOT_CENTRIC);
		swerveController.setCVTMode(CvtMode.SANDSTORM);
		currentDriveController = null;
		nextDriveController = DriveController.NORMAL;
		Devices.getGyro().resetGyro();
	}

	@Override public void autonUpdate () { }

	@Override public void teleopInit () {
		if (currentDriveController != null) { driveControllerMap.get(currentDriveController).deactivate(); }
		swerveController.setSwerveMode(SwerveMode.FIELD_CENTRIC);
		swerveController.setCVTMode(CvtMode.SHIFTING);
		currentDriveController = null;
		nextDriveController = DriveController.NORMAL;
	}

	@Override
	public void teleopUpdate () {
		CvtMode cvtMode = swerveController.getCVTMode();
		SwerveMode swerveMode = swerveController.getSwerveMode();

		/* *********** RUMBLE *********** */

		boolean rumbleDriver = (swerveMode == SwerveMode.ROBOT_CENTRIC) && (currentDriveController != DriveController.OPERATOR);
		boolean rumbleOperator = currentDriveController == DriveController.OPERATOR;

		driverController.vibrate(RumbleType.kLeftRumble, (rumbleDriver) ? 0.1 : 0.0);
		driverController.vibrate(RumbleType.kRightRumble, (rumbleDriver) ? 0.1 : 0.0);

		/* *********** CONTROLLERS *********** */

		if (nextDriveController != currentDriveController) {
			if (currentDriveController != null) {
				activeDriveController.deactivate();
			}
			currentDriveController = nextDriveController;
			activeDriveController = driveControllerMap.get(currentDriveController);
			activeDriveController.activate();
		}

		activeDriveController.update();
	}

	@Override public void testInit () { }

	@Override public void testUpdate () { }

}
