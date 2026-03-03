package utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import base.DriverFactory;
import config.ConfigReader;

public class ScreenshotUtil {
    private static final Logger logger = LogManager.getLogger(ScreenshotUtil.class);
    private static final String SCREENSHOT_FOLDER = ConfigReader.getProperty("screenshot.folder");

    public static String captureScreenshot(String testName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = testName + "_" + timestamp + ".png";
        String filePath = SCREENSHOT_FOLDER + File.separator + fileName;

        try {
            File directory = new File(SCREENSHOT_FOLDER);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            TakesScreenshot ts = (TakesScreenshot) DriverFactory.getDriver();
            File source = ts.getScreenshotAs(OutputType.FILE);
            File destination = new File(filePath);

            FileUtils.copyFile(source, destination);
            logger.info("Screenshot captured: " + filePath);

            return filePath;

        } catch (IOException e) {
            logger.error("Error capturing screenshot: " + e.getMessage());
            return null;
        }
    }

    public static String getBase64Screenshot() {
        try {
            TakesScreenshot ts = (TakesScreenshot) DriverFactory.getDriver();
            return ts.getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            logger.error("Error capturing base64 screenshot: " + e.getMessage());
            return null;
        }
    }
}