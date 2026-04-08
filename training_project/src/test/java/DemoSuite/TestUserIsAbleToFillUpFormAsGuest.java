package DemoSuite;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import static org.testng.Assert.assertEquals;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/* 
* Author Information:
* Author: Sonal Garg 
* LinkedIn: https://www.linkedin.com/in/sonalgarg32/
* 
* @version 1.0
* @since 2024-09-22
*/
public class TestUserIsAbleToFillUpFormAsGuest {
	String url = "https://appointment.questdiagnostics.com/schedule-appointment/as-personal-information";
	WebDriver driver;
	@BeforeClass
	public void setup() {
		url = "https://appointment.questdiagnostics.com/schedule-appointment/as-personal-information";
		System.setProperty("webdriver.chrome.driver", "E:\\libs\\chromedriver-win64\\chromedriver.exe");
		//test = report.createTest("Quest_BookAppointment");
		// Initialize WebDriver
		driver = new ChromeDriver();

		// Open URL
		driver.get(url);

	}

	@Test
	public void fillUpForm() {
		driver.findElement(By.xpath("//button[@id='onetrust-accept-btn-handler'][1]")).click();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//button[@aria-label=\"Continue as a guest\"]")));
		// Find search box and enter text
		WebElement searchBox = driver.findElement(By.xpath("//button[@aria-label=\"Continue as a guest\"]"));
		searchBox.click();
		
		String AppointFormPageTitle=driver.getTitle();
		
		assertEquals(AppointFormPageTitle, "Schedule Appointment - Personal information", "Appointment Form Page Displayed");
		
		driver.findElement(By.id("firstName")).sendKeys("Sonal");
		driver.findElement(By.id("lastName")).sendKeys("Garg");
		driver.findElement(By.id("dateOfBirth")).sendKeys("01011998");
		
		//selecting gender
		driver.findElement(By.xpath("//div[@class='ds-input__radio'][1]")).click();
		
		WebElement gendropdown=driver.findElement(By.id("mat-select-0"));
		gendropdown.click();
		driver.findElement(By.id("mat-option-1")).click();
		
		
		//Select genSelect=new Select(driver.findElement(By.id("mat-select-0")));
		//genSelect.selectByValue("Female");
		
		
	}
	@AfterTest
	public void tearDown() {
		//driver.close();
	}

}
