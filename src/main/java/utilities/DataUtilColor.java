package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.ArrayDeque;

import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.I2C;

import java.io.PrintWriter;

public class DataUtilColor {

    private PrintWriter p;
    private final I2C.Port i2cPort = I2C.Port.kOnboard;
	private final ColorSensorV3 colorSensor = new ColorSensorV3(i2cPort);

    public DataUtilColor(File file) throws FileNotFoundException {

        PrintWriter p = new PrintWriter(file);
        p.println("Red, Green, Blue");

        new Thread(() -> {
            while (true) {
                p.format("%d, %d, %d \n", colorSensor.getRed(), colorSensor.getGreen(), colorSensor.getBlue());
                p.flush();
            }

        }).start();

    }

}