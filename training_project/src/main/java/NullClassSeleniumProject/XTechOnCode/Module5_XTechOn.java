package NullClassSeleniumProject.XTechOnCode;

import org.testng.annotations.Test;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Module5_XTechOn {
	@Test
    public  void test1() {
		
		String  url= "https://appointment.questdiagnostics.com/schedule-appointment/as-personal-information";
        // Set up ChromeDriver path
        System.setProperty("webdriver.chrome.driver", "E:\\libs\\chromedriver-win64\\chromedriver.exe");

        
        // Initialize WebDriver
        WebDriver driver = new ChromeDriver();

        // Open Google
        driver.get(url);
        WebDriverWait wait= new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@aria-label=\"Continue as a guest\"]")));
        // Find search box and enter text
        WebElement searchBox = driver.findElement(By.xpath("//button[@aria-label=\"Continue as a guest\"]"));
        searchBox.click();

        // Close the browser
        driver.quit();
    }
	
	@Test
public  void test2() {
		
		String  url= "https://appointment.questdiagnostics.com/schedule-appointment/as-personal-information";
        // Set up ChromeDriver path
        System.setProperty("webdriver.chrome.driver", "E:\\libs\\chromedriver-win64\\chromedriver.exe");

        
        // Initialize WebDriver
        WebDriver driver = new ChromeDriver();

        // Open Google
        driver.get(url);
        WebDriverWait wait= new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@aria-label=\"Continue as a guest\"]")));
        // Find search box and enter text
        WebElement searchBox = driver.findElement(By.xpath("//button[@aria-label=\"Continue as a guest\"]"));
        searchBox.click();        
        // Close the browser
        driver.quit();
    }
}
