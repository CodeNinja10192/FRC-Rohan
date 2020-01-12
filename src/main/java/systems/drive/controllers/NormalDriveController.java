package systems.drive.controllers;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import robot.Controller;
import robot.Devices;
import robot.Gyro;
import robot.Controller.Axis;
import robot.Controller.Button;
import robot.Controller.ButtonEvent;
import robot.Robot;
import robot.Robot.RobotState;
import systems.drive.controllers.SwerveController.CvtMode;
import systems.drive.pivot.CVTPivot;
import systems.drive.pivot.Pivot;
import utilities.LogUtil;

public class NormalDriveController implements IDriveController {

	private SwerveController swerveController;
	private Controller driverController;
	private List<Integer> driverCallbackIds;
	private PIDController x2PID;
	private double x2;
	private double targetHeading;
	private Gyro gyro;
	double prevServoAngle;
	boolean servoSlow = false;

	public NormalDriveController (SwerveController swerveController) {
		driverController = Devices.getDriverController();
		this.swerveController = swerveController;
		driverCallbackIds = new ArrayList<>();
		gyro = Devices.getGyro();

		PIDSource x2Src = new PIDSource() {
			@Override
			public void setPIDSourceType(PIDSourceType pidSource) {
			}

			@Override
			public double pidGet() {
				double dAngle = (targetHeading - gyro.getYaw());
				return Math.sin(dAngle * Math.PI / 180.0);
			}

			@Override
			public PIDSourceType getPIDSourceType() {
				return PIDSourceType.kDisplacement;
			}
		};

		PIDOutput x2Out = new PIDOutput() {

			@Override
			public void pidWrite(double output) {
				x2 = output;
			}
		};
		x2PID = new PIDController(0.9, 0.0, 0, x2Src, x2Out);
	}

	@Override
	public void activate() {
		LogUtil.log(getClass(), "Activating");

		driverController.registerButtonListener(ButtonEvent.PRESS, Button.A, () -> {
			setServoSlow(true);
		});
		driverController.registerButtonListener(ButtonEvent.PRESS, Button.X, () -> {
			setServoSlow(false);
		});
		driverController.registerButtonListener(ButtonEvent.PRESS, Button.SELECT, () -> {
			swerveController.toggleFieldCentric();
		});

		x2PID.enable();

	}

	public void setServoSlow(boolean servoSlow) {
		this.servoSlow = servoSlow;
	}

	public boolean getServoSlow() {
		return servoSlow;
	}

	@Override
	public void deactivate() {
		LogUtil.log(getClass(), "Deactivating");
		for (int id : driverCallbackIds) { driverController.unregisterButtonListener(id); }
		x2PID.disable();
	}

	@Override
	public void update() {

		CvtMode cvtMode = swerveController.getCVTMode();
	
			double x1 = driverController.getAxis(Axis.LX);
			double y1 = driverController.getAxis(Axis.LY);
			double x2Normal = driverController.getAxis(Axis.RX);

			System.out.println("Target:" + targetHeading);
			System.out.println("RX:" + x2Normal);

			double servos = 0;
			System.out.println("Gyro: " + gyro.getYaw());
			System.out.println(x2);
			
			if (swerveController == null) {
				LogUtil.error(getClass(), "Swerve Null");
			} else if (cvtMode == null) {
				LogUtil.error(getClass(), "CvtMode Null");
			} else {
				if (Math.abs(x2Normal) < 0.15) {
					if (!x2PID.isEnabled()) {
						x2PID.enable();
					}
					double multiplier = cvtMode.getSpeedMultiplier();
					double x2Multiplier = multiplier * (cvtMode == CvtMode.SHIFTING ? 1.0 : 1.0);
					swerveController.drive(x1 * multiplier, y1 * multiplier, x2, getServoSlow());
				} else {
					if (x2PID.isEnabled()) {
						x2PID.disable();
					}
					double multiplier = cvtMode.getSpeedMultiplier();
					double x2Multiplier = multiplier * (cvtMode == CvtMode.SHIFTING ? 1.0 : 1.0);
					targetHeading = gyro.getYaw();
					swerveController.drive(x1 * multiplier, y1 * multiplier, x2Normal * x2Multiplier, getServoSlow());
				}
			}

	}

}