package com.gamanne.ri3d.server;

import java.util.Properties;
import java.util.logging.Logger;

/**
 * Configuration class, it opens the configuration file and has a public method to
 * retrieve the property needed
 *
 * @author Joaquin Fernandez
 *
 */
public class Config {
	
	private static Logger LOGGER = Logger.getLogger("InfoLogging");

    /** Property object, that holds the config file */
    Properties configFile;

    /**
     * Constructor that opens the configuration file so it can be used later
     */
    public Config() {

        configFile = new java.util.Properties();
        try {
            configFile.load(this.getClass().getClassLoader().
                    getResourceAsStream("config.cfg"));

        } catch(Exception eta){
            eta.printStackTrace();
        }
    }

    /**
     * Getter method that retrieves the property bounded to the key
     * @param key of the property wanted
     * @return the property wanted
     */
    public String getProperty(String key) {
        String value = this.configFile.getProperty(key);
		LOGGER.info("Retrieved property: " + key + " = " + value);
        return value;
    }
}