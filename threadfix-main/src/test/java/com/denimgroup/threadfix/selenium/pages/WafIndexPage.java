////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2014 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////
package com.denimgroup.threadfix.selenium.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public class WafIndexPage extends BasePage {

	private List<WebElement> names = new ArrayList<>();
	
	public WafIndexPage(WebDriver webdriver) {
		super(webdriver);
		//for (int i = 1; i <= getNumRows(); i++) {
		//	names.add(driver.findElementById("wafName" + i));
		//}
	}
	
	public int getNumRows() {
		List<WebElement> bodyRows = driver.findElementsByClassName("bodyRow");
		
		if (bodyRows != null && bodyRows.size() == 1 && bodyRows.get(0).getText().trim().contains("No WAFs found.")) {
			return 0;
		}		
		
		return driver.findElementsByClassName("bodyRow").size();
	}
	
	public int getIndex(String roleName) {
		int i = -1;
		for (int j = 1; j <= getNumRows(); j++) {
			names.add(driver.findElementById("wafName" + j));
		}
		for (WebElement name : names) {
			i++;
			String text = name.getText().trim();
			if (text.equals(roleName.trim())) {
				return i;
			}
		}
		return -1;
	}
	
	public WafRulesPage clickRules(String wafName){
		driver.findElementsByLinkText("Rules").get(getIndex(wafName)).click();
		return new WafRulesPage(driver);
	}

	public WafIndexPage clickDeleteWaf(String wafName){
		clickEditWaf(wafName);
		driver.findElementById("deleteWaf"+ (getIndex(wafName) + 1)).click();
		handleAlert();
		sleep(1000);
		return new WafIndexPage(driver);
	}

	public WafIndexPage clickAddWafLink() {
        waitForElement(driver.findElementById("createWafModalButton"));
        sleep(5000);
		driver.findElementById("createWafModalButton").click();
		waitForElement(driver.findElementById("submit"));
		return new WafIndexPage(driver);
	}
	
	public WafIndexPage createNewWaf(String name,String Type){
		setNewNameInput(name);
		setType(null,Type);
		return this;
	}
	
	public WafIndexPage clickCreateWaf(){
        //sleep(4000);
		driver.findElementById("submitWafModal").click();
        if (!(driver.findElementByClassName("alert-success").isDisplayed())) {
            sleep(4000);
        }
		return new WafIndexPage(driver);
	}
	
	public WafIndexPage clickCreateWafInvalid(){
		driver.findElementById("submitWafModal").click();
		return new WafIndexPage(driver);
	}
	
	

	public boolean isTextPresentInWafTableBody(String text) {
		return driver.findElementById("wafTableBody").getText().contains(text);
	}
	
	public boolean isNamePresent(String wafName){
        //sleep(3000);
		for (int j = 1; j <= getNumRows(); j++) {
			if(driver.findElementById("wafName" + j).getText().trim().equals(wafName.trim())){
				return true;
			}
		}

		return false;
	}
	
	public boolean isSuccessPresent(String wafName){
		return driver.findElementByClassName("alert-success").getText().contains(wafName);
	}
	
	public WafIndexPage clickEditWaf(String wafName){
        int temp = getIndex(wafName) + 1;
		driver.findElementById("editWafModalButton"+temp).click();
		waitForElement(driver.findElementById("deleteWaf"+ (getIndex(wafName) + 1)));
		return new WafIndexPage(driver);
	}
	
	public WafIndexPage editWaf(String wafName, String newName, String type){
		sleep(5000);
		driver.findElementsById("nameInput").get(getIndex(wafName)).clear();
		driver.findElementsById("nameInput").get(getIndex(wafName)).sendKeys(newName);
		new Select(driver.findElementsById("typeSelect").get(getIndex(wafName))).selectByVisibleText(type);
		return new WafIndexPage(driver);
	}
	
	public WafIndexPage clickUpdateWaf(String oldWafName){
		driver.findElementByLinkText("Update WAF").click();
		try{
			waitForInvisibleElement(driver.findElementById("deleteWaf"+(getIndex(oldWafName)+1)));
		}catch(NoSuchElementException | StaleElementReferenceException e){
			System.out.println("Waf name was updated.");
		}
		sleep(1000);
		return new WafIndexPage(driver);
	}
	
	public WafIndexPage clickUpdateWafInvalid(){
		driver.findElementByLinkText("Update WAF").click();
		sleep(2000);
		return new WafIndexPage(driver);
	}
	
	public String getWafName(int row){
		return driver.findElementById("wafName" + row).getText();
	}

	public WafIndexPage setNewNameInput(String name){
		driver.findElementById("wafCreateNameInput").clear();
		driver.findElementById("wafCreateNameInput").sendKeys(name);
		return new WafIndexPage(driver);
	}
	
	public WafIndexPage setNameInput(String oldName,String newName){
		driver.findElementsById("nameInput").get(getIndex(oldName)).clear();
		driver.findElementsById("nameInput").get(getIndex(oldName)).sendKeys(newName);
		return new WafIndexPage(driver);
	}
	
	
	public WafIndexPage setType(String oldName,String type){
		if (oldName == null) {
			new Select(driver.findElementsById("typeSelect").get(getNumRows())).selectByVisibleText(type);
		} else {
			new Select(driver.findElementsById("typeSelect").get(getIndex(oldName))).selectByVisibleText(type);
		}
		return new WafIndexPage(driver);
	}
	
	public String getNameErrorsText(){
		return driver.findElementById("name.errors").getText();
	}

	public String getNameText(int row){
		return  driver.findElementById("wafName" + row).getText();
	}
	
	public WafIndexPage clickCloseCreateWafModal(){
		driver.findElementById("createWaf").findElement(By.className("modal-footer")).findElements(By.className("btn")).get(0).click();
		sleep(1000);
		return new WafIndexPage(driver);
	}
	public WafIndexPage clickCloseWafModal(String wafName){
		driver.findElementsByClassName("modal-footer").get(getIndex(wafName)).findElements(By.className("btn")).get(0).click();
		sleep(1000);
		return new WafIndexPage(driver);
	}

	public int getWafEditHeaderWidth(String wafName) {
		return driver.findElementsByClassName("ellipsis").get(getIndex(wafName)).getSize().width;
	}
}
