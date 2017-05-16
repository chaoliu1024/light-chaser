/*
 * copy from jstorm 2.1.1
 * in com.alibaba.jstorm.utils package LoadConf class
 */

package me.chaoliu.lightchaser.core.config;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.*;
import java.net.URL;
import java.util.*;

@Slf4j
public class LoadConfig {

    public static Map readLightChaserConfig() {
        return LoadConfig.findAndReadConfigFile("light-chaser.yaml");
    }

    public static Map readConfig(String fileName) {
        return LoadConfig.findAndReadConfigFile(fileName);
    }

    public static List<URL> findResources(String name) {
        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(name);
            List<URL> ret = new ArrayList<>();
            while (resources.hasMoreElements()) {
                ret.add(resources.nextElement());
            }
            return ret;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param name
     * @param mustExist   -- if this is true, the file must exist, otherwise throw exception
     * @param canMultiple -- if this is false and there is multiple conf, it will throw exception
     * @return
     */
    public static Map findAndReadYaml(String name, boolean mustExist, boolean canMultiple) {
        InputStream in = null;
        boolean confFileEmpty = false;
        try {
            in = getConfigFileInputStream(name, canMultiple);
            if (null != in) {
                Yaml yaml = new Yaml(new SafeConstructor());
                Map ret = (Map) yaml.load(new InputStreamReader(in));
                if (null != ret) {
                    return new HashMap(ret);
                } else {
                    confFileEmpty = true;
                }
            }

            if (mustExist) {
                if (confFileEmpty)
                    throw new RuntimeException("Config file " + name + " doesn't have any valid storm configs.");
                else
                    throw new RuntimeException("Could not find config file on classpath " + name);
            } else {
                return new HashMap();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static Map findAndReadConfigFile(String name) {
        return findAndReadYaml(name, true, false);
    }

    public static InputStream getConfigFileInputStream(String configFilePath, boolean canMultiple) throws IOException {
        if (null == configFilePath) {
            throw new IOException("Could not find config file, name not specified.");
        }

        HashSet<URL> resources = new HashSet<>(findResources(configFilePath));
        if (resources.isEmpty()) {
            File configFile = new File(configFilePath);
            if (configFile.exists()) {
                return new FileInputStream(configFile);
            }
        } else if (resources.size() > 1 && canMultiple == false) {
            throw new IOException("Found multiple " + configFilePath + " resources. You're probably bundling the light-chaser jars with your own jar." + resources);
        } else {
            log.info("Using " + configFilePath + " from resources.");
            URL resource = resources.iterator().next();
            return resource.openStream();
        }
        return null;
    }

    public static InputStream getConfigFileInputStream(String configFilePath) throws IOException {
        return getConfigFileInputStream(configFilePath, true);
    }

    public static Map LoadYaml(String confPath) {
        return findAndReadYaml(confPath, true, true);
    }

    public static Map LoadProperty(String prop) {

        InputStream in = null;
        Properties properties = new Properties();

        try {
            in = getConfigFileInputStream(prop);
            properties.load(in);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No such file " + prop);
        } catch (Exception e1) {
            throw new RuntimeException("Failed to read config file.");
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        Map ret = new HashMap();
        ret.putAll(properties);
        return ret;
    }
}