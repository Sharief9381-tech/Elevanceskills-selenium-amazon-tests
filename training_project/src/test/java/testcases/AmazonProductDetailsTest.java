package testcases;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.ama.qa.base.TestBase;
import com.ama.qa.pages.AmazonHomePage;
import com.ama.qa.pages.AmazonLoginPage;
import com.ama.qa.pages.AmazonProductDetailsPage;
import com.ama.qa.pages.ShoppingCartPage;


/* 
* Author Information:
* Author: Sonal Garg 
* LinkedIn: https://www.linkedin.com/in/sonalgarg32/
* 
* @version 1.0
* @since 2024-09-22
*/
public class AmazonProductDetailsTest extends TestBase{

	
	AmazonProductDetailsPage amazonProductDetailsPage;
	AmazonHomePage amazonHomePage;
	ShoppingCartPage shoppingCartPage;
	AmazonLoginPage amazonLoginPage;
	@BeforeSuite
	public void setup()
	{
		//initialization();
		amazonProductDetailsPage=new AmazonProductDetailsPage();
		amazonHomePage =new AmazonHomePage();
		shoppingCartPage=new ShoppingCartPage();
		amazonLoginPage=new AmazonLoginPage();
	}
	
	@Test(priority = 2)
	public void VerifyProductDetailsAndAddToCart() throws InterruptedException {
		String productTitle="Noise Twist Go Bluetooth Calling Smart Watch for Women with Sleek Metal Dial, Glossy Finish, 1.39\" Display, 100+ Watch Faces, IP68, Sleep Tracking, Voice Assistance, Upto 7 Days Battery (Gold Link)";
		String productPrice="1,599";
		Assert.assertEquals(amazonProductDetailsPage.getSearchedProductTitle(),productTitle);
		Assert.assertEquals(amazonProductDetailsPage.getSearchedProductPrice(),productPrice);
		amazonProductDetailsPage.clickOnAddToCart(); 
		amazonProductDetailsPage.clickProceedToCheckOut();
		amazonHomePage.clickOnCart();
		//shoppingCartPage.clickProceedToBuy();
		/*
		 * amazonLoginPage.enterEmailOrPhone("6370171564");
		 * amazonLoginPage.clickContinue(); amazonLoginPage.enterPassword("Test@123");
		 * amazonLoginPage.clickSignIn();
		 */
	}}