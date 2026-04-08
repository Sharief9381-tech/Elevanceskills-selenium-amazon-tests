package testcases;

import java.util.Set;

import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.ama.qa.base.TestBase;
import com.ama.qa.pages.AmazonHomePage;
import com.ama.qa.pages.AmazonSearchProductsPage;


/* 
* Author Information:
* Author: Sonal Garg 
* LinkedIn: https://www.linkedin.com/in/sonalgarg32/
* 
* @version 1.0
* @since 2024-09-22
*/
public class AmazonSearchProductsTest extends TestBase{

	AmazonSearchProductsPage amazonSearchProductsPage;
	AmazonHomePage amazonHomePage;
	/*
	 * public AmazonSearchProductsTest() { super();
	 * 
	 * 
	 * }
	 */

	@BeforeTest
	public void setup() throws InterruptedException {
		initialization();
		amazonSearchProductsPage = new AmazonSearchProductsPage();
		amazonHomePage =new AmazonHomePage();
	}


	@Test(priority=0)
	public void serachedProductPageOpened() throws InterruptedException
	{
		amazonHomePage.selectCategory("Electronics");
		Assert.assertEquals(amazonHomePage.getSelectedCategory(), "Electronics");
		amazonHomePage.searchForProduct("female smartwatch");
		amazonHomePage.clickOnSearchButton(); 
		Thread.sleep(5000);
		Assert.assertEquals(amazonSearchProductsPage.getPageLabelForSearchedProduct().contains("watch"),true);
		Assert.assertEquals(amazonSearchProductsPage.getPageLabelForSearchedProduct().contains("female"),true);
	
	}
	
	@Test(priority=1)
	public void clickOnRequiredProduct() throws InterruptedException
	{
       
		amazonSearchProductsPage.clickOnProduct("Noise Twist Go Bluetooth Calling Smart Watch for Women with Sleek Metal Dial, Glossy Finish, 1.39\" Display, 100+ Watch Faces, IP68, Sleep Tracking, Voice Assistance, Upto 7 Days Battery (Gold Link)");
	
		
	}
	
	
	
}
