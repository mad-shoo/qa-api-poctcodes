package www.postcodes.io.config;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TestConfig {

    static Properties config;
    private static String URI;
    private static String TestEnv;
    final static Logger LOGGER = LoggerFactory.getLogger(TestConfig.class);

    public static void loadConfig() {
        config = loadProperties("config.properties");
        TestEnv = config.getProperty("TestEnv");
        URI = config.getProperty(TestEnv.toUpperCase() + ".environment");
        LOGGER.info("Configuration Loaded Successfully");

    }

    public static Properties loadProperties(String fileName) {
        String configLocation = System.getProperty("user.dir") + File.separator + "config/";
        Properties pro = new Properties();
        try {
            pro.load(new FileInputStream(configLocation + fileName));
        } catch (IOException e) {
            LOGGER.error(fileName + " file not found");
            Assert.fail();
        }
        return pro;
    }

    public static String getURI() {
        try {
            return URI;
        } catch (Exception e) {
            LOGGER.error("URI value not found in Config file" + e.getMessage());
            return null;
        }
    }

    public static String getConfigElement(String string) {
        try {
            if (config.getProperty(string).equals(null))
                LOGGER.error(string + " Config element missing");
            else
                return config.getProperty(string);
        } catch (Exception e) {
            LOGGER.error("config element reading error" + string);
            return null;
        }
        return null;
    }

}
