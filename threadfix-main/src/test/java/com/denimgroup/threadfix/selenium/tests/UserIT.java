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
import com.denimgroup.threadfix.selenium.pages.DashboardPage;
import com.denimgroup.threadfix.selenium.pages.UserChangePasswordPage;
import com.denimgroup.threadfix.selenium.pages.UserIndexPage;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.By;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(CommunityTests.class)
public class UserIT extends BaseIT {

    private UserIndexPage userIndexPage;

    @Before
    public void initialize() {
        userIndexPage = loginPage.defaultLogin()
                .clickManageUsersLink();
    }

	@Test
	public void testCreateUser() {
		String userName = getName();
        String password = "testCreateUser";

		userIndexPage.clickAddUserLink()
                    .setName(userName)
                    .setPassword(password)
                    .setConfirmPassword(password)
                    .clickAddNewUserBtn();

        assertTrue("User name was not present in the table.", userIndexPage.isUserNamePresent(userName));
		assertTrue("Success message was not displayed.", userIndexPage.isSuccessDisplayed(userName));
	}

    @Test
    public void testCreateTwoUsers() {
        String userName = getName();
        String password = "testCreateUser";

        String secondUserName = getName();

        userIndexPage.clickAddUserLink()
                .setName(userName)
                .setPassword(password)
                .setConfirmPassword(password)
                .clickAddNewUserBtn();

        assertTrue("User name was not present in the table.", userIndexPage.isUserNamePresent(userName));
        assertTrue("Success message was not displayed.", userIndexPage.isSuccessDisplayed(userName));

        userIndexPage.clickAddUserLink()
                .setName(secondUserName)
                .setPassword(password)
                .setConfirmPassword(password)
                .clickAddNewUserBtn();

        assertTrue("User name was not present in the table.", userIndexPage.isUserNamePresent(secondUserName));
        assertTrue("Success message was not displayed.", userIndexPage.isSuccessDisplayed(secondUserName));
        assertTrue("User name was not present in the table.", userIndexPage.isUserNamePresent(userName));
    }

    @Test
    public void testUserFieldValidation() {
        userIndexPage.clickAddUserLink()
                .setName("        ")
                .setPassword("  ")
                .setConfirmPassword("  ")
                .clickAddNewUserBtnInvalid();

        sleep(5000);

        assertTrue("Name is required error was not present.",
                userIndexPage.getRequiredNameError().equals("Name is required."));
        assertTrue("Password is required error was not present.",
                userIndexPage.getPasswordRequiredError().equals("Password is required."));
        assertTrue("Confirm Password is required error was not present.",
                userIndexPage.getConfirmPasswordRequiredError().equals("Confirm Password is required."));

        // Test length
        userIndexPage.setName("Test User");
        userIndexPage.setPassword("test");
        userIndexPage.setConfirmPassword("test");

        userIndexPage = userIndexPage.clickAddNewUserBtnInvalid();

        assertTrue("Password length error not present", userIndexPage.getPasswordLengthError().equals("8 characters needed"));

        // Test non-matching passwords
        userIndexPage.setName("new name");
        userIndexPage.setPassword("lengthy password 1");
        userIndexPage.setConfirmPassword("lengthy password 2");
        userIndexPage = userIndexPage.clickAddNewUserBtnInvalid();
        assertTrue("Password matching error is not correct.", userIndexPage.getPasswordMatchError().equals("Passwords do not match."));
    }

    @Test
    public void testCreateDuplicateUser(){
        String userName = getName();
        String password = getRandomString(15);
        // Create a user
        userIndexPage.clickAddUserLink()
                .setName(userName)
                .setPassword(password)
                .setConfirmPassword(password)
                .clickAddNewUserBtn();

        assertTrue("User name was not present in the table.", userIndexPage.isUserNamePresent(userName));
        assertTrue("Success message was not displayed.", userIndexPage.isSuccessDisplayed(userName));

        DashboardPage dashboardPage = userIndexPage.logout()
                .login(userName, password);

        assertTrue("user: "+userName+" was not logged in.",dashboardPage.isLoggedInUser(userName));

        userIndexPage = dashboardPage.logout()
                .defaultLogin()
                .clickManageUsersLink()
                .clickAddUserLink();
        // Test name uniqueness check

        userIndexPage.setName(userName);
        userIndexPage.setPassword("dummy password");
        userIndexPage.setConfirmPassword("dummy password");

        userIndexPage = userIndexPage.clickAddNewUserBtnInvalid();
        sleep(5000);
        assertTrue("Name uniqueness error is not correct.", userIndexPage.getNameError().equals("That name is already taken."));
    }

    @Test
    public void testEditUserName() {
        String userName = getName();
        String editedUserName = getName();
        String password = getRandomString(15);
        String editedPassword = getRandomString(15);

        userIndexPage.clickAddUserLink()
                .setName(userName)
                .setPassword(password)
                .setConfirmPassword(password)
                .clickAddNewUserBtn();

        DashboardPage dashboardPage= userIndexPage.logout()
                .login(userName, password);

        assertTrue("New user was not able to login.", dashboardPage.isLoggedin());

        userIndexPage = dashboardPage.logout()
                .defaultLogin()
                .clickManageUsersLink()
                .clickEditLink(userName)
                .setName(editedUserName)
                .setPassword(editedPassword)
                .setConfirmPassword(editedPassword)
                .clickModalSubmit();

        dashboardPage = userIndexPage.logout()
                .login(editedUserName, editedPassword);

        assertTrue("Edited user was not able to login.", dashboardPage.isLoggedin());
    }

	@Test
	public void testEditPassword() {
		String userName = getName();
        String password = getRandomString(15);
        String editedPassword = getRandomString(15);

		assertFalse("User was already in the table.", userIndexPage.isUserNamePresent(userName));

        UserChangePasswordPage userChangePasswordPage = userIndexPage.clickAddUserLink()
				.setName(userName)
				.setPassword(password)
				.setConfirmPassword(password)
				.clickAddNewUserBtn()
				.logout()
				.login(userName, password)
                .clickChangePasswordLink()
                .setCurrentPassword(password)
                .setNewPassword(editedPassword)
                .setConfirmPassword(editedPassword)
                .clickUpdate();


		DashboardPage dashboardPage = userChangePasswordPage.logout()
                .login(userName, editedPassword);

        assertTrue("Edited user could not login.", dashboardPage.isLoggedin());
	}

    @Test
    public void testEditPasswordValidation() {
        UserChangePasswordPage changePasswordPage = userIndexPage.clickChangePasswordLink()
                .setCurrentPassword(" ")
                .setNewPassword("password1234")
                .setConfirmPassword("password1234")
                .clickUpdateInvalid();

        assertTrue("Password is required error was not present.",
                changePasswordPage.getPasswordRequiredError().equals("Password is required."));

        changePasswordPage = changePasswordPage.setCurrentPassword("password")
                .setNewPassword("                     ")
                .setConfirmPassword("password1234")
                .clickUpdateInvalid();

        assertTrue("Password match error not present",
                changePasswordPage.getErrorText("passwordMatchError").contains("Passwords do not match."));

        changePasswordPage = changePasswordPage.setCurrentPassword("password")
                .setConfirmPassword("                  ")
                .setNewPassword("password1234")
                .clickUpdateInvalid();

        assertTrue("Password match error not present",
                changePasswordPage.getErrorText("passwordMatchError").contains("Passwords do not match."));

        changePasswordPage = changePasswordPage.setCurrentPassword("password")
                .setConfirmPassword("      ")
                .setNewPassword("password124")
                .clickUpdateInvalid();

        assertTrue("Field required error missing",
                changePasswordPage.getErrorText("confirmRequiredError").contains("This field is required."));

        changePasswordPage.logout();
    }

	@Test
	public void testEditUserFieldValidation() {
		String baseUserName = getName();
		String userNameDuplicateTest = getName();

		// Set up the two User objects for the test

		userIndexPage.clickAddUserLink()
                .setName(baseUserName)
                .setPassword("lengthy password 2")
                .setConfirmPassword("lengthy password 2")
                .clickAddNewUserBtn();

        userIndexPage = userIndexPage.clickOrganizationHeaderLink()
                .clickManageUsersLink()
                .clickAddUserLink()
                .setName(userNameDuplicateTest)
                .setPassword("lengthy password 2")
                .setConfirmPassword("lengthy password 2")
                .clickAddNewUserBtn();

		// Test submission with no changes
		userIndexPage = userIndexPage.clickManageUsersLink()
                .clickEditLink(baseUserName)
                .clickUpdateUserBtn(baseUserName);
		assertTrue("User name was not present in the table.",userIndexPage.isUserNamePresent(baseUserName));

        userIndexPage = userIndexPage.clickManageUsersLink();

		// Test Empty
		userIndexPage = userIndexPage.clickEditLink(baseUserName)
                .setName("")
                .setPassword("")
                .setConfirmPassword("")
                .clickUpdateUserBtnInvalid(baseUserName);

		assertTrue("Name error not present", userIndexPage.isSaveChangesButtonClickable(baseUserName));
    }

    @Test
    public void testEditUserValidationWhiteSpace (){
        String userName = getName();
        String passWord = getName();

        userIndexPage.clickAddUserLink()
                .setName(userName)
                .setPassword(passWord)
                .setConfirmPassword(passWord)
                .clickAddNewUserBtn();

		// Test White Space
		userIndexPage = userIndexPage.clickManageUsersLink()
                .clickAddUserLink()
                .setName("        ")
                .setPassword("             ")
                .setConfirmPassword("             ")
                .clickAddNewUserBtn();

        sleep(5000);
		assertTrue("Name error not present", userIndexPage.getRequiredNameError().equals("Name is required."));
    }

    @Test
    public void testEditUserValidationPasswordMatching(){
        String userName = getName();
		// Test non-matching passwords
		userIndexPage.clickAddUserLink()
                .setName(userName)
                .setPassword("lengthy password 1")
                .setConfirmPassword("lengthy password 2")
                .clickAddNewUserBtn();

        sleep(5000);
		assertTrue("Password matching error is not correct.", userIndexPage.getPasswordMatchError().equals("Passwords do not match."));

    }

    @Test
    public void testEditUserValidationLength(){
		// Test length
		userIndexPage = userIndexPage.clickAddUserLink()
                .setName("Test User")
                .setPassword("test")
                .setConfirmPassword("test")
                .clickAddNewUserBtn();

        sleep(5000);
		assertTrue("Password length error not present", userIndexPage.getPasswordLengthError().equals("8 characters needed"));

    }

    @Test
    public void testEditUserValidationUnique(){
        String userName = getName();
        String passWord = getName();

        userIndexPage.clickAddUserLink()
                .setName(userName)
                .setPassword(passWord)
                .setConfirmPassword(passWord)
                .clickAddNewUserBtn();
	
		// Test name uniqueness check
		userIndexPage = userIndexPage
				.clickManageUsersLink()
				.clickAddUserLink()
				.setName(userName)
				.setPassword("lengthy password 2")
				.setConfirmPassword("lengthy password 2")
				.clickAddNewUserBtn();

        sleep(5000);
		assertTrue("Name uniqueness error is not correct.", userIndexPage.getNameError().equals("That name is already taken."));
		
	}

	@Test
	public void testNavigation() {
        assertTrue("Could not navigate to User Index Page.",driver.findElements(By.id("newUserModalLink")).size() != 0);
		}

    @Test
    public void testDeleteUser(){
        String userName = getName();
        String password = "testDeleteUser";

        userIndexPage.clickAddUserLink()
                .setName(userName)
                .setPassword(password)
                .setConfirmPassword(password)
                .clickAddNewUserBtn()
                .clickEditLink(userName)
                .clickDelete(userName);

        assertTrue("Deletion Message not displayed.", userIndexPage.isSuccessDisplayed(userName));
        assertFalse("User still present in user table.", userIndexPage.isUserNamePresent(userName));
    }

    @Test
    public void testEditMultipleUsers() {
        String userName1 = getName();
        String password1 = "testEditMultipleUsers";
        String userName2 = getName();
        String password2 = "testEditMultipleUsers2";
        String changedPassword = "changedPasswordTestMultipleUsers";
        String changedName = getName();
        String displayCssId = "displayName" + changedName;

        userIndexPage = userIndexPage.clickAddUserLink()
                .setName(userName1)
                .setPassword(password1)
                .setConfirmPassword(password1)
                .clickAddNewUserBtn()
                .clickAddUserLink()
                .setName(userName2)
                .setPassword(password2)
                .setConfirmPassword(password2)
                .clickAddNewUserBtn();

        userIndexPage.clickEditLink(userName1)
                .setDisplayName(changedName)
                .clickUpdateUserBtn(userName1)
                .clickEditLink(userName2)
                .setPassword(changedPassword)
                .setConfirmPassword(changedPassword)
                .clickUpdateUserBtn(userName2);

        assertTrue("Second user's display name was changed to the first user's name when attempting to change only password.",
                driver.findElements(By.id(displayCssId)).size() < 2);
    }
}
