package com.ama.qa.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.ama.qa.base.TestBase;

/* 
* Author Information:
* Author: Sonal Garg 
* LinkedIn: https://www.linkedin.com/in/sonalgarg32/
* 
* @version 1.0
* @since 2024-09-22
*/
public class AmazonLoginPage extends TestBase{
	@FindBy(xpath = "//span[text()='Log In']")WebElement logInButton;
	@FindBy(name = "email")WebElement email;
	@FindBy(id = "continue")WebElement continueButton;
	@FindBy(id = "ap_password")@CacheLookup WebElement password;
	@FindBy(id = "signInSubmit")WebElement signInSubmit;
	
	
	public AmazonLoginPage(){
		PageFactory.initElements(getdriver(), this);
	}
	
	public String gettitle(){
		return getdriver().getTitle();
	}
	
	public void enterEmailOrPhone(String emailOrPhone){
		email.sendKeys(emailOrPhone);
	}
	
	public void clickContinue(){
		continueButton.click();
	}
	public void enterPassword(String passwordValue){
		password.sendKeys(passwordValue);
	}
	
	
	public void clickSignIn(){
		signInSubmit.click();
	}
}
