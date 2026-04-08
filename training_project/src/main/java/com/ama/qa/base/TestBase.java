package com.ama.qa.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class TestBase {

    public static WebDriver driver = null;
    public static Properties prop;

    public TestBase() {
        try {
            prop = new Properties();
            // Use classpath-relative path
            InputStream ip = TestBase.class.getClassLoader()
                .getResourceAsStream("com/ama/qa/config/config.properties");
            if (ip == null) {
                // Fallback: relative file path
                ip = new FileInputStream(
                    "src/main/java/com/ama/qa/config/config.properties");
            }
            prop.load(ip);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initialization() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get(prop.getProperty("URL").trim());
    }

    public static WebDriver getdriver() {
        if (driver == null) {
            driver = new ChromeDriver();
        }
        return driver;
    }
}
