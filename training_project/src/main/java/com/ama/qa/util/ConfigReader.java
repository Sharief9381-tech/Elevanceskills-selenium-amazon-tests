package com.ama.qa.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private String username = "";
    private String password = "";
    private String browser  = "chrome";

    public ConfigReader() {
        try {
            Properties p = new Properties();
            InputStream fis = ConfigReader.class.getClassLoader()
                .getResourceAsStream("com/ama/qa/config/config.properties");
            if (fis == null) {
                fis = new FileInputStream(
                    "src/main/java/com/ama/qa/config/config.properties");
            }
            p.load(fis);
            username = p.getProperty("USERNAME", "").trim();
            password = p.getProperty("PASSWORD", "").trim();
            browser  = p.getProperty("BROWSER",  "chrome").trim();
        } catch (IOException e) {
            System.out.println("Cannot read config.properties: " + e.getMessage());
        }
    }

    public String getUsername() { return username; }
    public String getPassword()  { return password;  }
    public String getBrowser()   { return browser;   }
}
