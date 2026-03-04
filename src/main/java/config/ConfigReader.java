package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    
    private static Properties properties;
    private static final String CONFIG_FILE_PATH = System.getProperty("user.dir") 
            + "/src/test/resources/config.properties";
    
    static {
        try {
            FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH);
            properties = new Properties();
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            System.out.println("⚠️ Config file not found, using default values");
            properties = new Properties();
            setDefaultValues();
        }
    }
    
    private static void setDefaultValues() {
        properties.setProperty("browser", "chrome");
        properties.setProperty("headless", "false");
        properties.setProperty("environment", "qa");
        properties.setProperty("base.url", "https://www.saucedemo.com/");
        properties.setProperty("implicit.wait", "10");
        properties.setProperty("explicit.wait", "20");
        properties.setProperty("page.load.timeout", "30");
        properties.setProperty("extent.report.folder", "test-output/reports");
        properties.setProperty("extent.report.name", "TestReport");
        properties.setProperty("screenshot.folder", "screenshots");
    }
    
    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value != null) {
            return value.trim();
        }
        return null;
    }
    
    public static String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            return value.trim();
        }
        return defaultValue;
    }
    
    public static String getBrowser() {
        return getProperty("browser", "chrome");
    }
    
    public static boolean isHeadless() {
        String headless = getProperty("headless", "false");
        return Boolean.parseBoolean(headless);
    }
    
    public static String getBaseUrl() {
        return getProperty("base.url", "https://www.saucedemo.com/");
    }
    
    public static String getEnvironment() {
        return getProperty("environment", "qa");
    }
    
    public static int getImplicitWait() {
        String wait = getProperty("implicit.wait", "10");
        return Integer.parseInt(wait);
    }
    
    public static int getExplicitWait() {
        String wait = getProperty("explicit.wait", "20");
        return Integer.parseInt(wait);
    }
    
    public static int getPageLoadTimeout() {
        String timeout = getProperty("page.load.timeout", "30");
        return Integer.parseInt(timeout);
    }
    
    public static String getScreenshotFolder() {
        return getProperty("screenshot.folder", "screenshots");
    }
    
    public static String getExtentReportFolder() {
        return getProperty("extent.report.folder", "test-output/reports");
    }
    
    public static String getExtentReportName() {
        return getProperty("extent.report.name", "TestReport");
    }
}