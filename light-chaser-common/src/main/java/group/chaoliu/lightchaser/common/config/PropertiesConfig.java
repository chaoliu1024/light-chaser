package group.chaoliu.lightchaser.common.config;

import java.io.IOException;
import java.util.Properties;

public class PropertiesConfig {

    private static final String PROPERTIES_PATH = "/light-chaser.properties";

    private static Properties properties;

    private PropertiesConfig() {
    }

    private static synchronized void loadProperties() {
        properties = new Properties();
        try {
            properties.load(PropertiesConfig.class.getResourceAsStream(PROPERTIES_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getString(String key) {
        if (null == properties) {
            loadProperties();
        }
        if (properties.containsKey(key)) {
            return properties.getProperty(key, "");
        } else {
            return "";
        }
    }

    public static int getInt(String key) {
        if (null == properties) {
            loadProperties();
        }
        if (properties.containsKey(key)) {
            try {
                return Integer.parseInt(properties.getProperty(key, ""));
            } catch (NumberFormatException e) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public static Boolean getBoolean(String key) {
        if (null == properties) {
            loadProperties();
        }
        if (properties.containsKey(key)) {
            return Boolean.parseBoolean(properties.getProperty(key, ""));
        } else {
            return false;
        }
    }
}
