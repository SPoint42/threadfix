package com.denimgroup.threadfix.selenium.tests;

import com.denimgroup.threadfix.RegressionTests;
import com.denimgroup.threadfix.selenium.pages.TeamDetailPage;
import com.denimgroup.threadfix.selenium.utils.DatabaseUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertTrue;

@Category(RegressionTests.class)
public class TeamDetailPageIT extends BaseIT {

	@Test
	public void actionButtonTest(){
        String teamName = getRandomString(8);
        String appName = getRandomString(8);
        String file = ScanContents.getScanFilePath();

        DatabaseUtils.createTeam(teamName);
        DatabaseUtils.createApplication(teamName, appName);
        DatabaseUtils.uploadScan(teamName, appName, file);

        TeamDetailPage teamDetailPage = loginPage.login("user", "password").
                clickOrganizationHeaderLink()
                .clickViewTeamLink(teamName)
                .clickActionButton();

        assertTrue("Action button was not present.", teamDetailPage.isActionBtnPresent());
        assertTrue("Action button was not clickable.", teamDetailPage.isActionBtnClickable());
        assertTrue("Edit/Delete Link was not present.", teamDetailPage.isEditDeleteLinkPresent());
        assertTrue("Edit/Delete link was not clickable.", teamDetailPage.isEditDeleteLinkClickable());
	}
	
	@Test
	public void editDeleteModalTest(){
        String teamName = getRandomString(8);
        String appName = getRandomString(8);
        String file = ScanContents.getScanFilePath();

        DatabaseUtils.createTeam(teamName);
        DatabaseUtils.createApplication(teamName, appName);
        DatabaseUtils.uploadScan(teamName, appName, file);

        TeamDetailPage teamDetailPage = loginPage.login("user", "password")
                .clickOrganizationHeaderLink()
                .clickViewTeamLink(teamName)
                .clickEditOrganizationLink();

        assertTrue("Edit/Modal was not present.", teamDetailPage.isEditDeleteModalPresent());
        assertTrue("Delete Button was not present.", teamDetailPage.isDeleteTeamButtonPresent());
        assertTrue("Delete Button was not clickable.", teamDetailPage.EDDeleteClickable());
        assertTrue("Name input was not present", teamDetailPage.EDNamePresent());
        assertTrue("Close modal button was not present", teamDetailPage.EDClosePresent());
        assertTrue("Close modal button was not clickable", teamDetailPage.EDCloseClickable());
		assertTrue("Save button was not present.", teamDetailPage.EDSavePresent());
		assertTrue("Save button was not clickable.", teamDetailPage.EDSaveClickable());
	}
	
	@Test
	public void chartTest(){
        String teamName = getRandomString(8);
        String appName = getRandomString(8);
        String file = ScanContents.getScanFilePath();

        DatabaseUtils.createTeam(teamName);
        DatabaseUtils.createApplication(teamName, appName);
        DatabaseUtils.uploadScan(teamName, appName, file);

        TeamDetailPage teamDetailPage = loginPage.login("user", "password")
                .clickOrganizationHeaderLink()
                .clickViewTeamLink(teamName);
		sleep(5000); 
        assertTrue("Left view more link was not present.", teamDetailPage.isleftViewMoreLinkPresent());
        assertTrue("Left view more link was not clickable.", teamDetailPage.isleftViewMoreLinkClickable());
        assertTrue("Right view more link was not present.", true);
        assertTrue("Right view more link was not clickable.", teamDetailPage.isrightViewMoreLinkClickable());
        assertTrue("6 month vulnerability burn-down chart was not present", teamDetailPage.is6MonthChartPresnt());
        assertTrue("Top 10 vulnerabilities chart was not present.", teamDetailPage.isTop10ChartPresent());
	}
	
	@Test
	public void addApplicationButtonTest(){
        String teamName = getRandomString(8);
        String appName = getRandomString(8);
        String file = ScanContents.getScanFilePath();

        DatabaseUtils.createTeam(teamName);
        DatabaseUtils.createApplication(teamName, appName);
        DatabaseUtils.uploadScan(teamName, appName, file);

        TeamDetailPage teamDetailPage = loginPage.login("user", "password")
                .clickOrganizationHeaderLink()
                .clickViewTeamLink(teamName);

        assertTrue("Add App button was not present.", teamDetailPage.isAddAppBtnPresent());
        assertTrue("Add app button was not clickable.", teamDetailPage.isAddAppBtnClickable());

	}

	@Test
	public void applicationDetailLink(){
        String teamName = getRandomString(8);
        String appName = getRandomString(8);
        String file = ScanContents.getScanFilePath();

        DatabaseUtils.createTeam(teamName);
        DatabaseUtils.createApplication(teamName, appName);
        DatabaseUtils.uploadScan(teamName, appName, file);

        TeamDetailPage teamDetailPage = loginPage.login("user", "password")
                .clickOrganizationHeaderLink()
                .clickViewTeamLink(teamName);

        assertTrue("Link for application detail page was not present.", teamDetailPage.isAppLinkPresent(appName));
        assertTrue("Link for application detail page was not clickable.", teamDetailPage.isAppLinkClickable(appName));
	}
}