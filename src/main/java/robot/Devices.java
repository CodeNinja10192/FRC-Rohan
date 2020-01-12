package robot;

import java.util.HashMap;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import systems.drive.controllers.SwerveController;
import systems.drive.pivot.CVTPivot;
import systems.drive.pivot.Pivot;
import systems.drive.pivot.PivotConfig;
import utilities.Vector2;

public class Devices {

	public static Devices instance = null;

	private Gyro gyro;
	private HashMap<Pivot,Vector2> pivotMap;
	private Controller driverController;
	private Compressor compressor;
	private SwerveController swerveController;

	private Devices () {
		gyro = new Gyro();

		PivotConfig.loadConfigs("/home/lvuser/deploy/config/pivotcfg.json");
		pivotMap = new HashMap<>();

		driverController = new Controller(0);

		pivotMap.put(new CVTPivot("1"), new Vector2()); // FL
		pivotMap.put(new CVTPivot("2"), new Vector2()); // FR
		pivotMap.put(new CVTPivot("3"), new Vector2()); // BL
		pivotMap.put(new CVTPivot("4"), new Vector2()); // BR

		swerveController = new SwerveController(pivotMap);
	}

	public static void init () {
		if (instance == null) {
			instance = new Devices();
		}
	}

	public static HashMap<Pivot,Vector2> getPivotMap () {
		return instance.pivotMap;
	}

	public static SwerveController getSwerveController () {
		return instance.swerveController;
	}

	public static Controller getDriverController () {
		return instance.driverController;
	}

	public static Gyro getGyro () {
		return instance.gyro;
	}

	public static Compressor getCompressor () {
		return instance.compressor;
	}

}