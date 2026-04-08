package testcases;



import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ama.qa.base.TestBase;
import com.ama.qa.pages.AmazonHomePage;

/* 
* Author Information:
* Author: Sonal Garg 
* LinkedIn: https://www.linkedin.com/in/sonalgarg32/
* 
* @version 1.0
* @since 2024-09-22
*/
public class AmazonHomeTest extends TestBase {

	AmazonHomePage amazonHomePage;

	/*
	 * public AmazonHomeTest() { super(); }
	 */

	@BeforeMethod
	public void setup() throws InterruptedException {
		//initialization();
		amazonHomePage = new AmazonHomePage();

	}
	/*
	 * @Test public void SearchForAProduct() throws InterruptedException {
	 * amazonHomePage.selectCategory("Electronics");
	 * Assert.assertEquals(amazonHomePage.getSelectedCategory(), "Electronics");
	 * amazonHomePage.searchForProduct("female smartwatch");
	 * amazonHomePage.clickOnSearchButton();
	 * 
	 * }
	 */}
