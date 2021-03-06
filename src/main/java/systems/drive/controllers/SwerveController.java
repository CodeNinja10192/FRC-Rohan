package systems.drive.controllers;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ArrayList;

import robot.Devices;
import robot.Gyro;
import systems.drive.pivot.CVTPivot;
import systems.drive.pivot.Pivot;
import utilities.LogUtil;
import utilities.Vector2;

public class SwerveController {

	public static final double W = 21.0;
	public static final double L = 27.25;

	public static enum SwerveMode {
		ROBOT_CENTRIC,
		FIELD_CENTRIC;
	}

	public static enum CvtMode {
		PRECISION (0.0, 0.3),
		TORQUE(0.0, 1.0),
		SHIFTING(0.0, 1.0),
		SANDSTORM(0.0, 0.6);

		double servoAngle;
		double speedMultiplier;

		CvtMode(double servoAngle, double speedMultiplier) {
			this.servoAngle = servoAngle;
			this.speedMultiplier = speedMultiplier;
		}

		public double getSpeedMultiplier () { return speedMultiplier; }
		public double getServoAngle () { return servoAngle; }
	}

	private SwerveMode swerveMode;
	private CvtMode cvtMode = CvtMode.SHIFTING;
	private boolean habDrivePivots;

	HashMap<Pivot,Vector2> pivotMap;

	public SwerveController (HashMap<Pivot,Vector2> pivotMap) {
		this.pivotMap = pivotMap;
		swerveMode = SwerveMode.ROBOT_CENTRIC;
		LogUtil.log("SwerveMode", "Switching to: " + swerveMode.toString());
	}

	/*
	 *	Swerve Mode Stuff
	 */
	public void setSwerveMode (SwerveMode sm) {
		if (sm != swerveMode) { LogUtil.log("SwerveMode", "Switching to: " + sm.toString()); }
		synchronized (swerveMode) { swerveMode = sm; }
	}

	public SwerveMode getSwerveMode() {
		return swerveMode;
	}

	public void toggleFieldCentric () {
		synchronized (swerveMode) {
			if (swerveMode == SwerveMode.FIELD_CENTRIC) { swerveMode = SwerveMode.ROBOT_CENTRIC; }
			else { swerveMode = SwerveMode.FIELD_CENTRIC; }
		}
	}

	/*
	 * CVT Mode Stuff
	 */
	public void setCVTMode (CvtMode mode) {
		if (mode != cvtMode) { LogUtil.log("CvtMode", "Switching to: " + mode.toString()); }
		cvtMode = mode;
	}

	public CvtMode getCVTMode () {
		return cvtMode;
	}

	/*
	 *	Hab Drive Stuff
	 */
	public void setHabDrivePivots (boolean value) { habDrivePivots = value; }

	public boolean getHabDrivePivots () { return habDrivePivots; }

	/*
	 *	Drive Code
	 */

	public void drivePolar (double mag, double angD, double x2) {
		double angR = Math.toRadians(angD);
	}

	public void drive (double x1, double y1, double x2, boolean isSlow) {

		// Clamp x1, y1, and x2 to be between -1 and 1
		// Really only matters for x2, since that can go over for gyro-correction
		x1 = Math.max(-1.0, Math.min(x1, 1.0));
		y1 = Math.max(-1.0, Math.min(y1, 1.0));
		x2 = Math.max(-1.0, Math.min(x2, 1.0));

		double yaw = Math.toRadians(Devices.getGyro().getYaw());
		double sin = Math.sin(yaw);
		double cos = Math.cos(yaw);

		boolean isFieldCentric;
		synchronized (swerveMode) { isFieldCentric = (swerveMode == SwerveMode.FIELD_CENTRIC); }

		if (isFieldCentric) {
			double temp = x1 * cos + y1 * sin;
			y1 =  -x1 * sin + y1 * cos;
			x1 = temp;
		}

		Vector2 tVec = new Vector2(x1, y1);
		if (tVec.magnitude() < 0.03) { tVec.reset(); }

		ArrayList<Double> arrayList = new ArrayList<Double>();

		double max = 0.0;
		for (Pivot piv : pivotMap.keySet()) {
			Vector2 vt = piv.getPosition().copy().rotateD(-90.0).unit().multiply(x2).add(tVec);
			max = Math.max(max, vt.magnitude());
			pivotMap.get(piv).set(vt);
		}
		
		double s = Math.max(Math.sqrt(x1*x1 + y1*y1), Math.abs(x2));

		for (Pivot piv : pivotMap.keySet()) {
			if (max > 1e-14) {
				pivotMap.get(piv).multiply(s/max);
			}
			else {
				pivotMap.get(piv).reset();
			}
		}

		/* *********** PIVOT CONTROL UPDATES *********** */

		for (Entry<Pivot,Vector2> entry : pivotMap.entrySet()) {
			Pivot piv = entry.getKey();
			Vector2 v = entry.getValue();
			
			double mag = v.magnitude();

			System.out.println(piv.getName() + " " + piv.getMinVoltage() + " " + piv.getMaxVoltage());

			if(!habDrivePivots) {
				((CVTPivot)piv).setSpeed(mag, isSlow);

				if (v.magnitude() != 0) { piv.setTargetAngleR(v.angleR()); }

				try {
				} catch (Exception e) {
					LogUtil.warn(getClass(), String.format("%s is not a CVTPivot!", piv.getName()));
				}
			}

		}
	}

	public void testDrive (double x1, double y1) {
		for (Pivot piv : pivotMap.keySet()) {
			piv.setMotorDirect(x1);
			piv.setRotationSpeed(y1);
			System.out.println(((CVTPivot)piv).getPivotAngleD());
		}
	}

	public void enable () {
		for (Pivot piv : pivotMap.keySet()) {
			piv.enable();
		}
	}

	public void disable () {
		for (Pivot piv : pivotMap.keySet()) {
			piv.disable();
		}
	}
}