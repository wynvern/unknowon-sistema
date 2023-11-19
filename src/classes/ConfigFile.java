/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wynvern
 */
public class ConfigFile {
    private static void createDefaultINIFile(String filePath) throws IOException {
        Properties defaultProperties = new Properties();

        // Set default values for the variables
        defaultProperties.setProperty("database", "unknown");
        defaultProperties.setProperty("hostname", "localhost");
        defaultProperties.setProperty("password", "x");
        defaultProperties.setProperty("port", "3306");
        defaultProperties.setProperty("theme", "white");
        defaultProperties.setProperty("username", "x");

        // Write the default properties to the file
        try (OutputStream output = new FileOutputStream(filePath)) {
            defaultProperties.store(output, null);
        }
    }
    
    public static Properties getVariables() {
        String filePath = "config.ini";

        // Create a File object with the specified path
        File file = new File(filePath);

        // Check if the file exists
        if (!file.exists()) {
            try {
                createDefaultINIFile("config.ini");
            } catch (IOException ex) {
                Logger.getLogger(ConfigFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("config.ini")) {
            // Load the properties from the file
            properties.load(input);
        } catch (IOException io) {
            io.printStackTrace();
        }

        return properties;
    }
    
    public static String getVariable(String key) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("config.ini")) {
            // Load the properties from the file
            properties.load(input);
        } catch (IOException io) {
            io.printStackTrace();
        }

        return properties.getProperty(key);
    }
    
    
    public static void setVariable(String key, String value) {
        Properties properties = getVariables(); // Load existing properties

        // Set the new property
        properties.setProperty(key, value);

        try (OutputStream output = new FileOutputStream("config.ini")) {
            // Save the updated properties to the file
            properties.store(output, "Updated properties");
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
