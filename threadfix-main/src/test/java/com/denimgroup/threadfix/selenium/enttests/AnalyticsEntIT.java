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
package com.denimgroup.threadfix.selenium.enttests;

import com.denimgroup.threadfix.EnterpriseTests;
import com.denimgroup.threadfix.selenium.pages.ApplicationDetailPage;
import com.denimgroup.threadfix.selenium.pages.TagDetailPage;
import com.denimgroup.threadfix.selenium.tests.BaseDataTest;
import com.denimgroup.threadfix.selenium.utils.DatabaseUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.By;
import static org.junit.Assert.assertTrue;

@Category(EnterpriseTests.class)
public class AnalyticsEntIT extends BaseDataTest {

    @Test
    public void testUtilsAttachPCITag() {
        initializeTeamAndAppWithIBMScan();
        DatabaseUtils.attachAppToTag("PCI",appName,teamName);

        loginPage.defaultLogin()
                .clickAnalyticsLink()
                .clickEnterpriseTab(true)
                .selectComplianceType("PCI");
    }

    @Test
    public void testUtilsAttachHIPAATag() {
        initializeTeamAndAppWithIBMScan();
        DatabaseUtils.attachAppToTag("HIPAA",appName,teamName);

        loginPage.defaultLogin()
                .clickAnalyticsLink()
                .clickEnterpriseTab(true)
                .selectComplianceType("HIPAA");
    }

    @Test
    public void testManuallyAttachPCITagtoApp() {
        initializeTeamAndApp();

        ApplicationDetailPage applicationDetailPage = loginPage.defaultLogin()
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName,teamName);

        applicationDetailPage.clickEditDeleteBtn()
                .attachTag("PCI")
                .clickModalSubmit();

        TagDetailPage tagDetailPage = applicationDetailPage.clickTagsLink()
                .clickTagName("PCI");

        assertTrue("PCI tag was not attached to application", tagDetailPage.isTagAttachedtoApp(appName));
    }

    @Test
    public void testManuallyAttachHIPAATagtoApp() {
        initializeTeamAndApp();

        ApplicationDetailPage applicationDetailPage = loginPage.defaultLogin()
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName,teamName);

        applicationDetailPage.clickEditDeleteBtn()
                .attachTag("HIPAA")
                .clickModalSubmit();

        TagDetailPage tagDetailPage = applicationDetailPage.clickTagsLink()
                .clickTagName("HIPAA");

        assertTrue("HIPAA tag was not attached to application", tagDetailPage.isTagAttachedtoApp(appName));
    }

    @Test
    public void testPCITagPresence() {
        loginPage.defaultLogin()
                .clickTagsLink();

        assertTrue("PCI Tag not on page", driver.findElement(By.linkText("PCI")).isDisplayed());
    }

    @Test
    public void testHIPAATagPresence() {
        loginPage.defaultLogin()
                .clickTagsLink();

        assertTrue("HIPAA Tag not on page", driver.findElement(By.linkText("HIPAA")).isDisplayed());
    }
}