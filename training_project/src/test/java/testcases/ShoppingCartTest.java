package testcases;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.ama.qa.base.TestBase;
import com.ama.qa.pages.ShoppingCartPage;

/* 
* Author Information:
* Author: Sonal Garg 
* LinkedIn: https://www.linkedin.com/in/sonalgarg32/
* 
* @version 1.0
* @since 2024-09-22
*/
public class ShoppingCartTest extends TestBase {

	ShoppingCartPage shoppingCartPage;

	@BeforeTest
	public void setup() throws InterruptedException {
		//initialization();
		shoppingCartPage = new ShoppingCartPage();

	}

	@Test(priority=3)
	public void SearchForAProduct() throws InterruptedException {
		shoppingCartPage.clickProceedToBuy();
		
		
	}}
