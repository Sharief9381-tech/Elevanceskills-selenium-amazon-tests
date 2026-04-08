package testcases;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.ama.qa.pages.AmazonProductCheckoutPage;

/* 
* Author Information:
* Author: Sonal Garg 
* LinkedIn: https://www.linkedin.com/in/sonalgarg32/
* 
* @version 1.0
* @since 2024-09-22
*/
public class AmazonProductCheckoutTest {
	
	AmazonProductCheckoutPage amazonProductCheckoutPage;
	@BeforeSuite
	public void setup()
	{
		//initialization();
		amazonProductCheckoutPage=new AmazonProductCheckoutPage();
		
	}
	
	@Test(priority = 5)
	public void useAddress()
	{
		amazonProductCheckoutPage.clickShipToAddressButton();
	}
}
