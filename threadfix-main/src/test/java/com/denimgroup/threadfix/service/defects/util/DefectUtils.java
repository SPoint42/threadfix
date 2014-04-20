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
package com.denimgroup.threadfix.service.defects.util;

import com.denimgroup.threadfix.data.entities.Defect;
import com.denimgroup.threadfix.data.entities.GenericSeverity;
import com.denimgroup.threadfix.data.entities.GenericVulnerability;
import com.denimgroup.threadfix.data.entities.Vulnerability;
import com.denimgroup.threadfix.service.defects.DefectMetadata;
import com.denimgroup.threadfix.service.defects.ProjectMetadata;
import org.apache.commons.exec.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefectUtils {

    private DefectUtils(){}

    public static List<Defect> getDefectList(String... nativeIds) {

        List<Defect> defects = new ArrayList<>();

        for (String nativeId : nativeIds) {
            Defect defect = new Defect();
            defect.setNativeId(nativeId);
            defects.add(defect);
        }

        return defects;
    }

    public static List<Vulnerability> getSampleVulnerabilities() {
        Vulnerability vulnerability = new Vulnerability();

        vulnerability.setGenericSeverity(new GenericSeverity());
        vulnerability.getGenericSeverity().setName("Critical");

        vulnerability.setGenericVulnerability(new GenericVulnerability());
        vulnerability.getGenericVulnerability().setName("XSS");

        return Arrays.asList(vulnerability);
    }

    public static DefectMetadata getBasicMetadata(ProjectMetadata projectMetadata) {
        return new DefectMetadata("Dummy Description", "simple preamble",
                getFirstOrEmptyString(projectMetadata.getComponents()),
                getFirstOrEmptyString(projectMetadata.getVersions()),
                getFirstOrEmptyString(projectMetadata.getSeverities()),
                getFirstOrEmptyString(projectMetadata.getPriorities()),
                getFirstOrEmptyString(projectMetadata.getStatuses()));
    }

    public static String getFirstOrEmptyString(List<String> metadataList) {
        return metadataList.isEmpty() ? "" : metadataList.get(0);
    }

    public static List<String> getProductsFromString(String projects) {
        return Arrays.asList(StringUtils.split(projects, ","));
    }

}
