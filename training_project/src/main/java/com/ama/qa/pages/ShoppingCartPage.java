package com.ama.qa.pages;

import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import com.ama.qa.base.TestBase;

/* 
* Author Information:
* Author: Sonal Garg 
* LinkedIn: https://www.linkedin.com/in/sonalgarg32/
* 
* @version 1.0
* @since 2024-09-22
*/
public class ShoppingCartPage extends TestBase {

	@FindBy(xpath = "//input[@name='proceedToRetailCheckout']")WebElement proceedToBuy;
	
	public ShoppingCartPage(){
			PageFactory.initElements(getdriver(), this);
		}
	 
	@Test(priority = 3)
	public void clickProceedToBuy() {
		 proceedToBuy.click();
	} 
	
	
}
