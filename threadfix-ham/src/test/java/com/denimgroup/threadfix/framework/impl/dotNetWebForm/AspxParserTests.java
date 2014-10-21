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
package com.denimgroup.threadfix.framework.impl.dotNetWebForm;

import com.denimgroup.threadfix.framework.ResourceManager;
import org.junit.Test;

/**
 * Created by mac on 9/4/14.
 */
public class AspxParserTests {

    @Test
    public void testBasicNameParsing() {
        AspxParser parser = AspxParser.parse(ResourceManager.getDotNetWebFormsFile("WebForm1.aspx"));

        assert parser.aspName.equals("WebForm1.aspx") :
                "Got " + parser.aspName + " instead of WebForm1.aspx.";
    }

    @Test
    public void testBasicIdParsing() {
        AspxParser parser = AspxParser.parse(ResourceManager.getDotNetWebFormsFile("WebForm1.aspx"));

        assert parser.ids.contains("ddl") :
                "Parser didn't find ddl: " + parser;
        assert parser.ids.contains("newitem") :
                "Parser didn't find newitem: " + parser;
        assert parser.ids.contains("test") :
                "Parser didn't find test: " + parser;
    }


}
