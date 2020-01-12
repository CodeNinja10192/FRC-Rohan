package auton.commands;

import edu.wpi.first.wpilibj.Timer;
import robot.Devices;
import systems.drive.controllers.SwerveController;

public class Drive extends Command {

	private double x1;
	private double y1;
	private double x2;
	private double duration;

	private SwerveController swerveController;

	private double startTime;

	public Drive(double x1, double y1, double x2, double duration) {
		super("Drive");
		
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.duration = duration;

		this.swerveController = Devices.getSwerveController();

	}

	@Override
	public void init() {
		swerveController.drive(0.0, 0.0, 0.0, false);
		swerveController.driveStraight(true);

		startTime = Timer.getFPGATimestamp();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		swerveController.drive(x1, y1, x2, false);
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		if (Timer.getFPGATimestamp() >= startTime + duration) {
			swerveController.drive(0.0, 0.0, 0.0, false);
			return true;
		}
		return false;
	}

}