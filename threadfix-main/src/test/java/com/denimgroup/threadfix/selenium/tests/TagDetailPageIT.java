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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(CommunityTests.class)
public class TagDetailPageIT extends BaseDataTest {

    @Test
    public void testAttachTagToApp() {
        initializeTeamAndApp();
        String tagName = getName();

        ApplicationDetailPage applicationDetailPage = loginPage.defaultLogin()
                .clickTagsLink()
                .createNewTag(tagName)
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName,teamName);

        applicationDetailPage.clickEditDeleteBtn()
                .attachTag(tagName)
                .clickModalSubmit();

        TagDetailPage tagDetailPage = applicationDetailPage.clickTagsLink()
                .clickTagName(tagName);

        assertTrue("Tag was not attached to application", tagDetailPage.isTagAttachedtoApp(appName));
    }

    @Test
    public void testCorrectNumberofApps() {
        initializeTeamAndApp();
        String appName2 = createApplication(teamName);
        String tagName = getName();

        ApplicationDetailPage applicationDetailPage = loginPage.defaultLogin()
                .clickTagsLink()
                .createNewTag(tagName)
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName,teamName);

        applicationDetailPage.clickEditDeleteBtn()
                .attachTag(tagName)
                .clickModalSubmit();

        ApplicationDetailPage applicationDetailPage1 = applicationDetailPage.clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName2,teamName);

        applicationDetailPage1.clickEditDeleteBtn()
                .attachTag(tagName)
                .clickModalSubmit();

        TagDetailPage tagDetailPage = applicationDetailPage1.clickTagsLink()
                .clickTagName(tagName);

        assertTrue("The number of apps attached is incorrect", tagDetailPage.getNumberofAttachedApps().equals("2"));
    }

    @Test
    public void testAttachTagToComment() {
        initializeTeamAndAppWithIBMScan();
        String tagName = getName();

        ApplicationDetailPage applicationDetailPage = loginPage.defaultLogin()
                .clickTagsLink()
                .createNewTag(tagName)
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName,teamName);

        applicationDetailPage.expandVulnerabilityByType("Critical79")
                .expandCommentSection("Critical790")
                .addComment("Critical790")
                .attachTag(tagName)
                .setComment(teamName + appName)
                .clickModalSubmit();

        assertTrue("Tag was not attached to application", applicationDetailPage.isClickable("cmtTag0"));
    }

    @Test
    public void testCorrectNumberofComments() {
        initializeTeamAndAppWithIBMScan();
        String tagName = getName();

        ApplicationDetailPage applicationDetailPage = loginPage.defaultLogin()
                .clickTagsLink()
                .createNewTag(tagName)
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName,teamName);

        applicationDetailPage.expandVulnerabilityByType("Critical79")
                .expandCommentSection("Critical790")
                .addComment("Critical790")
                .attachTag(tagName)
                .setComment(teamName + appName)
                .clickModalSubmit();

        applicationDetailPage.expandCommentSection("Critical791")
                .addComment("Critical791")
                .setComment(teamName + appName)
                .clickModalSubmit();

        TagDetailPage tagDetailPage = applicationDetailPage.clickTagsLink()
                .clickTagName(tagName);

        assertTrue("Number of attached comments is incorrect", tagDetailPage.getNumberofAttachedComments().equals("2"));
    }

    @Test
    public void testAppLinkNavigation() {
        initializeTeamAndApp();
        String tagName = getName();

        ApplicationDetailPage applicationDetailPage = loginPage.defaultLogin()
                .clickTagsLink()
                .createNewTag(tagName)
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName,teamName);

        applicationDetailPage.clickEditDeleteBtn()
                .attachTag(tagName)
                .clickModalSubmit();

        ApplicationDetailPage applicationDetailPage1 = applicationDetailPage.clickTagsLink()
                .clickTagName(tagName)
                .clickAppName(appName);

        assertTrue("Application navigation failed.", applicationDetailPage1.isApplicationNameCorrect(appName));
    }

    @Test
    public void testTeamLinkNavigation() {
        initializeTeamAndApp();
        String tagName = getName();

        ApplicationDetailPage applicationDetailPage = loginPage.defaultLogin()
                .clickTagsLink()
                .createNewTag(tagName)
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName,teamName);

        applicationDetailPage.clickEditDeleteBtn()
                .attachTag(tagName)
                .clickModalSubmit();

        TeamDetailPage teamDetailPage = applicationDetailPage.clickTagsLink()
                .clickTagName(tagName)
                .clickTeamName(teamName);

        assertTrue("Team page navigation failed.", teamDetailPage.isTeamNameDisplayedCorrectly(teamName));
    }

    @Test
    public void testCommentTagLinkNavigation() {
        initializeTeamAndAppWithIBMScan();
        String tagName = getName();

        ApplicationDetailPage applicationDetailPage = loginPage.defaultLogin()
                .clickTagsLink()
                .createNewTag(tagName)
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName,teamName);

        applicationDetailPage.expandVulnerabilityByType("Critical79")
                .expandCommentSection("Critical790")
                .addComment("Critical790")
                .attachTag(tagName)
                .setComment(teamName + appName)
                .clickModalSubmit();

        TagDetailPage tagDetailPage = applicationDetailPage.clickTagName(tagName);

        assertTrue("Tag was not attached to application", tagDetailPage.isLinkPresent(appName));
    }
}
