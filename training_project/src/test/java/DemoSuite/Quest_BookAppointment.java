package DemoSuite;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/* 
* Author Information:
* Author: Sonal Garg 
* LinkedIn: https://www.linkedin.com/in/sonalgarg32/
* 
* @version 1.0
* @since 2024-09-22
*/
public class Quest_BookAppointment {
	WebDriver driver;
	
	
	String btn_AddToCartListingPage="\\";

	//By btn_AddToCart=driver.findElement(By.xpath(null));
	
	
	String url = "https://appointment.questdiagnostics.com/schedule-appointment/as-personal-information";
	@BeforeClass
	public void setup() {
		url = "https://appointment.questdiagnostics.com/schedule-appointment/as-personal-information";
		System.setProperty("webdriver.chrome.driver", "E:\\libs\\chromedriver-win64\\chromedriver.exe");
		// Initialize WebDriver
		driver = new ChromeDriver();
		// Open URL
		driver.get(url);

	}

	@Test
	public void ContinueAsGuestAndBookAppointment() {
		driver.findElement(By.xpath("//button[@id='onetrust-accept-btn-handler'][1]")).click();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//button[@aria-label=\"Continue as a guest\"]")));
		// Find search box and enter text
		WebElement searchBox = driver.findElement(By.xpath(btn_AddToCartListingPage));
		searchBox.click();
		
		String AppointFormPageTitle=driver.getTitle();
		
		assertEquals(AppointFormPageTitle, "Schedule Appointment - Personal information", "Appointment Form Page Displayed");
		
	}
	@AfterTest
	public void tearDown() {
		driver.close();
	}

}
