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

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class WafRulesPage extends BasePage {

    private WebElement nameText;
    private WebElement wafTypeText;
    private WebElement editLink;
    private WebElement lastItemFoundInApplicationsTableBodyLink;
    private WebElement deleteButton;


    /*------------------------------------ Action Methods ------------------------------------*/

    public WafRulesPage(WebDriver webdriver) {
        super(webdriver);

        nameText = driver.findElementById("nameText");
        wafTypeText = driver.findElementById("wafTypeText");
    }

    //SET FUNCTIONS
    public WafRulesPage setWafDirectiveSelect(String code) {
        new Select(driver.findElementById("wafDirectiveSelect")).selectByVisibleText(code);
        return new WafRulesPage(driver);
    }

    public WafRulesPage setWafApplicationSelect(String teamName, String appName) {
        new Select(driver.findElementById("wafApplicationSelect")).selectByVisibleText(teamName + "/" + appName);
        return new WafRulesPage(driver);
    }

    public WafRulesPage setLogFile(String file) {
        waitForElement(driver.findElementById("fileInput"));
        driver.findElementById("fileInput").sendKeys(file);
        return new WafRulesPage(driver);
    }

    //CLICK FUNCTIONS
    public WafRulesPage clickGenerateWafRulesButton() {
        sleep(2000);
        driver.findElementById("generateWafRulesButton").click();
        waitForElement(driver.findElementByLinkText("Download Waf Rules"));
        return new WafRulesPage(driver);
    }

    public WafIndexPage clickCancelButton() {
        driver.findElementByLinkText("Cancel").click();
        return new WafIndexPage(driver);
    }

    public WafLogPage clickUploadLogFile() {
        driver.findElementById("submitButton").click();
        waitForElement(driver.findElementByLinkText("Continue"));
        return new WafLogPage(driver);
    }

    public WafRulesPage clickViewDetails() {
        driver.findElementByLinkText("View Details").click();
        return new WafRulesPage(driver);
    }

    public WafRulesDetailPage clickLogLink() {
        driver.findElementByLinkText("100000 - fired 52 times").click();
        return new WafRulesDetailPage(driver);
    }

    public TeamIndexPage clickDownloadWafRulesButton() {
        driver.findElementById("downloadWafRulesButton").click();
        return new TeamIndexPage(driver);
    }

    public ApplicationDetailPage clickAppName(String appName) {
        driver.findElementByLinkText(appName).click();
        return new ApplicationDetailPage(driver);
    }

    public WafIndexPage clickDeleteButton() {
        deleteButton.click();

        Alert alert = driver.switchTo().alert();
        alert.accept();

        return new WafIndexPage(driver);
    }

    /*------------------------------------ Get Methods ------------------------------------*/

    public String getNameText() {
        return nameText.getText();
    }

    public String getWafTypeText() {
        return wafTypeText.getText();
    }

    public String getWafDirectiveSelect() {
        return new Select(driver.findElementById("wafDirectiveSelect")).getFirstSelectedOption().getText();
    }

    /*------------------------------------ Boolean Methods ------------------------------------*/

    public boolean isDownloadWafRulesDisplay() {
        return driver.findElementByLinkText("Download Waf Rules").isDisplayed();
    }

    public boolean isLogsNumberPresent() {
        return driver.findElementByLinkText("100000 - fired 52 times").isDisplayed();
    }

    public boolean checkFiredWafNav(String wafCode) {
        driver.findElementByPartialLinkText(wafCode).click();
        return driver.findElementByCssSelector("h3").getAttribute("innerHTML").contains(wafCode);
    }

    public boolean clickDownloadWafRulesEnabled() {
        return driver.findElementByLinkText("Download Waf Rules").isDisplayed();
    }

    public boolean isTextPresentInApplicationsTableBody(String text) {
        if (isElementPresent("applicationsTableBody")) {
            for (WebElement element : driver.findElementById("applicationsTableBody").findElements(By.xpath(".//tr/td/a"))) {
                if (element.getText().contains(text)) {
                    lastItemFoundInApplicationsTableBodyLink = element;
                    return true;
                }
            }
        }

        return false;
    }
}
