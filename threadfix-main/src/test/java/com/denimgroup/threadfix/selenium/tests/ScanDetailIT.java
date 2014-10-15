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
package com.denimgroup.threadfix.selenium.tests;

import com.denimgroup.threadfix.CommunityTests;
import com.denimgroup.threadfix.selenium.pages.*;
import com.denimgroup.threadfix.selenium.utils.DatabaseUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(CommunityTests.class)
public class ScanDetailIT extends BaseIT {
    private String teamName;
    private String appName;

    @Before
    public void initialize() {
        teamName = createTeam();
        appName = createApplication(teamName);

        DatabaseUtils.uploadScan(teamName, appName, ScanContents.SCAN_FILE_MAP.get("IBM Rational AppScan"));
    }

    @Test
    public void checkViewScan() {
        ApplicationDetailPage applicationDetailPage = loginPage.login("user", "password")
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName, teamName)
                .clickScansTab();
        ScanDetailPage scanDetailPage = applicationDetailPage.clickViewScan();

        assertTrue("Scans Finding is not available"
                ,scanDetailPage.isViewFindingPresent());
    }

    @Test
    public void testShowHideStatisticsDetail() {
        ApplicationDetailPage applicationDetailPage = loginPage.login("user", "password")
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName, teamName)
                .clickScansTab();

        ScanDetailPage scanDetailPage = applicationDetailPage.clickViewScan()
                .toggleStatistics();

        assertTrue("Statistics were not show.", scanDetailPage.areStatisticsDisplayed());

        scanDetailPage.toggleStatistics();

        assertFalse("Statistics were show", scanDetailPage.areStatisticsDisplayed());
    }

    @Test
    public void testShowHideStatisticsToggle() {
        ApplicationDetailPage applicationDetailPage = loginPage.login("user", "password")
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName, teamName)
                .clickScansTab();

        ScanDetailPage scanDetailPage = applicationDetailPage.clickViewScan();

        assertTrue("Show Statistics Button wasn't displayed", scanDetailPage.isHideStatisticsButtonDisplay("Show Statistics"));

        scanDetailPage.toggleStatistics();

        assertTrue("Hide Statistics Button wasn't displayed", scanDetailPage.isHideStatisticsButtonDisplay("Hide Statistics"));
    }

    @Test
    public void findingNavigationTest() {
        ApplicationDetailPage applicationDetailPage = loginPage.login("user", "password")
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName, teamName)
                .clickScansTab();
        ScanDetailPage scanDetailPage = applicationDetailPage.clickViewScan();

        FindingDetailPage findingDetailPage = scanDetailPage.clickViewFinding();

        assertTrue("Finding Vulnerabilities Detail is not available"
                ,findingDetailPage.isViewVulnetabilityButtonDisplayed());
    }

    @Test
    public void showStatisticResultsCorrect() {
        ApplicationDetailPage applicationDetailPage = loginPage.login("user", "password")
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName, teamName)
                .clickScansTab();

        ScanDetailPage scanDetailPage = applicationDetailPage.clickViewScan()
                .toggleStatistics();

        assertTrue("Imported Result is incorrect", scanDetailPage.isImportedResultsCorrect("45"));
        assertTrue("Duplicate Results is incorrect", scanDetailPage.isDuplicatedResultsCorrect("0"));
        assertTrue("Total finding result is incorrect", scanDetailPage.isTotalFindingCorrect("45"));
        assertTrue("Finding Without Vulnerabilities result is incorrect", scanDetailPage.isFindingsWithoutVulnerabilitiesCorrect("0"));
        assertTrue("Finding With Vulnerabilities result is incorrect", scanDetailPage.isFindingsWithVulnerabilitiesCorrect("45"));
        assertTrue("Duplicate Finding result is incorrect",scanDetailPage.isDuplicateFindingCorrect("0"));
        assertTrue("Hidden Vulnerabilities result is incorrect",scanDetailPage.isHiddenVulnerabilitiesCorrect("0"));
        assertTrue("Total Vulnerabilities is incorrect", scanDetailPage.isTotalVulnerabilitiesCorrect("45"));
        assertTrue("New Vulnerabilities is incorrect", scanDetailPage.isNewVulnerabilitiesCorrect("45"));
        assertTrue("Old Vulnerabilities result is incorrect", scanDetailPage.isOldVulnerabilitiesCorrect("0"));
        assertTrue("Resurfaced Vulnerabilities result is incorrect", scanDetailPage.isResurfacedVulnerabilitiesCorrect("0"));
        assertTrue("Closed Vulnerabilities result is incorrect", scanDetailPage.isClosedVulnerabilitiesCorrect("0"));
    }
}