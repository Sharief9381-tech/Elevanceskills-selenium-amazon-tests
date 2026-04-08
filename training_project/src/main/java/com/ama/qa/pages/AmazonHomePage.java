package com.ama.qa.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import com.ama.qa.base.TestBase;

/* 
* Author Information:
* Author: Sonal Garg 
* LinkedIn: https://www.linkedin.com/in/sonalgarg32/
* 
* @version 1.0
* @since 2024-09-22
*/
public class AmazonHomePage extends TestBase{
	
	
	Select category;

	 @FindBy(xpath = "//input[@id='twotabsearchtextbox']") WebElement searchBoxInput;
	 @FindBy(xpath = "//select[@id='searchDropdownBox']")WebElement categoryDropDown;
	 @FindBy(xpath = "//input[@id='nav-search-submit-button']")@CacheLookup WebElement searchButton;
	 @FindBy(xpath = "//div[@class='s-suggestion-container']//div[1]/span") WebElement searchSuggestion;
	 @FindBy(xpath = "//div[@id='nav-cart-count-container']") WebElement cartButton;
	 public AmazonHomePage(){
			PageFactory.initElements(getdriver(), this);
		}

	
	public void selectCategory(String categoryValue){
		category=new Select(categoryDropDown);
		category.selectByVisibleText(categoryValue);
		
	}
	
	public String getSelectedCategory(){
		String selectedCategory=category.getFirstSelectedOption().getText();
		return selectedCategory;
	}
	
	public void searchForProduct(String product){
		searchBoxInput.sendKeys(product);	
		//searchSuggestion.click();
	}
	
	public void clickOnSearchButton()
	{
		searchButton.click();
	}
	public void clickOnCart()
	{
		cartButton.click();
	}
	
	
}
