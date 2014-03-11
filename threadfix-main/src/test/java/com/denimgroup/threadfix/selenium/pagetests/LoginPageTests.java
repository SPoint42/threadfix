////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2013 Denim Group, Ltd.
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
package com.denimgroup.threadfix.selenium.pagetests;

import com.denimgroup.threadfix.selenium.pages.LoginPage;
import com.denimgroup.threadfix.selenium.tests.BaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoginPageTests extends BaseTest {
	
	@Test
	public void usernameFieldPresentTest(){
		assertTrue("Username field was not present on the page",loginPage.isUserNameFieldPresent());
	}
	
	@Test
	public void usernameFieldInputTest(){
		String username = "user"+ getRandomString(4);
		
		loginPage = loginPage.setUsername(username);
		
		assertTrue("Username does not accept text properly",loginPage.getUserNameInput().equals(username));
	}
	
	@Test
	public void passwordFieldPresentTest(){
		assertTrue("Password field was not present on the page",loginPage.isPasswordFieldPresent());
	}
	
	@Test
	public void passwordFieldInputTest(){
		String password = "password"+ getRandomString(4);
		
		loginPage = loginPage.setPassword(password);
		
		assertTrue("password does not accept text properly",loginPage.getLoginInput().equals(password));
	}
	
	@Test
	public void loginButtonPresentTest(){
		assertTrue("Login button was not present on the page",loginPage.isLoginButtonPresent());
	}
	
	@Test
	public void loginButtonClickableTest(){
		assertTrue("Login button is not clickable",loginPage.isLoginButtonClickable());
	}
	
	@Test
	public void rememberCheckBoxPresentTest(){
		assertTrue("Remember me checkbox was not present on the page",loginPage.isRememberMeCheckBoxPresent());
	}
	
	@Test
	public void rememberCheckBoxSelectableTest(){
		loginPage = loginPage.checkRememberCheckbox();
		
		assertTrue("Remember me checkbox did not select properly",loginPage.isRememeberMeCheckBoxSelected());
	}
	
	@Test
	public void rememberCheckBoxUnSelectableTest(){
		loginPage = loginPage.checkRememberCheckbox()
							.checkRememberCheckbox();
		
		assertFalse("Remember me checkbox did not unselect properly",loginPage.isRememeberMeCheckBoxSelected());
	}
	
	
}