package systems;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;

public class ColorSystem extends RobotSystem {

    private final I2C.Port i2cPort = I2C.Port.kOnboard;
	private final ColorSensorV3 colorSensor = new ColorSensorV3(i2cPort);
	private final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
  	private final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
  	private final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
	private final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);
	private final ColorMatch m_colorMatcher = new ColorMatch();

    public ColorSystem () {
        super("Color System");
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
    }

    @Override
    public void preStateUpdate() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void postStateUpdate() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void disabledInit() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void disabledUpdate() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void autonInit() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void autonUpdate() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void teleopInit() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void teleopUpdate() throws Exception {
        // TODO Auto-generated method stub
            Color detectedColor = colorSensor.getColor();
			String colorString;
            ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);
            
            m_colorMatcher.addColorMatch(kBlueTarget);
            m_colorMatcher.addColorMatch(kGreenTarget);
            m_colorMatcher.addColorMatch(kRedTarget);
            m_colorMatcher.addColorMatch(kYellowTarget);

			if (match.color == kBlueTarget) {
			colorString = "Blue";
			System.out.println("blue");
			} else if (match.color == kRedTarget) {
			colorString = "Red";
			System.out.println("red");
			} else if (match.color == kGreenTarget) {
			colorString = "Green";
			System.out.println("green");
			} else if (match.color == kYellowTarget) {
			colorString = "Yellow";
			System.out.println("yellow");
			} else {
			colorString = "Unknown";
			System.out.println("unknown");
			}

			System.out.println(match.confidence);
    }

    @Override
    public void testInit() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void testUpdate() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void enable() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void disable() throws Exception {
        // TODO Auto-generated method stub

    }



}