package com.ama.qa.pages;

import java.time.Duration;

import org.checkerframework.checker.units.qual.s;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ama.qa.base.TestBase;

/* 
* Author Information:
* Author: Sonal Garg 
* LinkedIn: https://www.linkedin.com/in/sonalgarg32/
* 
* @version 1.0
* @since 2024-09-22
*/
public class AmazonProductDetailsPage extends TestBase {
	
	@FindBy(xpath = "//div[@id='titleSection']")WebElement serachedProductTitle;
	@FindBy(xpath = "//div[@id='corePriceDisplay_desktop_feature_div']//span[@class='a-price-whole']")WebElement serachedProductPrice;
	@FindBy(xpath = "(//div[@id='rightCol']//input[@id='add-to-cart-button'])[2]")WebElement addToCartButton;
	@FindBy(xpath="//a[@id='attach-close_sideSheet-link']") WebElement proceedToCheckoutButton;
	
	WebElement serachProductPanel;
	// Create WebDriverWait instance
    WebDriverWait wait = new WebDriverWait(getdriver(), Duration.ofSeconds(20));

	public AmazonProductDetailsPage(){
			PageFactory.initElements(getdriver(), this);
		}
	 
	public String getSearchedProductTitle() {
		return serachedProductTitle.getText();
	} 
	public String getSearchedProductPrice() {
		return serachedProductPrice.getText();
	} 
	
	public void clickOnAddToCart()
	{
        addToCartButton.click();
	}
	
	public void clickProceedToCheckOut()
	{
        proceedToCheckoutButton.click();
	}
	
}
