package com.ama.qa.pages;

import java.time.Duration;
import java.util.Set;

import org.checkerframework.checker.units.qual.s;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
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
public class AmazonSearchProductsPage extends TestBase {

	@FindBy(xpath = "//div[@class='a-section a-spacing-small a-spacing-top-small']//span[@class='a-color-state a-text-bold']")
	WebElement searchedProductsPageLabel;
	WebElement serachProductPanel;

	// Create WebDriverWait instance
	WebDriverWait wait = new WebDriverWait(getdriver(), Duration.ofSeconds(20));
	Set<String> newWindows;
	String oldWindow;

	public AmazonSearchProductsPage() {
		PageFactory.initElements(getdriver(), this);
	}

	public String getPageLabelForSearchedProduct() {
		return searchedProductsPageLabel.getText();
	}

	public void clickOnProduct(String productName) {
		serachProductPanel = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//span[contains(text(), '" + productName + "')]")));
		oldWindow = getdriver().getWindowHandle();
		serachProductPanel.click();

		newWindows = getdriver().getWindowHandles();

		for (String window : newWindows) {
			if (!oldWindow.equalsIgnoreCase(window)) {
				getdriver().switchTo().window(window);

				break;
			}
		}
		
	}

}
