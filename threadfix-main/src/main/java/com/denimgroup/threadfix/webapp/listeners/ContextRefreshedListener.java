////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2015 Denim Group, Ltd.
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

package com.denimgroup.threadfix.webapp.listeners;

import com.denimgroup.threadfix.annotations.ReportPlugin;
import com.denimgroup.threadfix.data.entities.DashboardWidget;
import com.denimgroup.threadfix.data.entities.DefaultConfiguration;
import com.denimgroup.threadfix.importer.loader.AnnotationLoader;
import com.denimgroup.threadfix.logging.SanitizedLogger;
import com.denimgroup.threadfix.service.DashboardWidgetService;
import com.denimgroup.threadfix.service.DefaultConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.denimgroup.threadfix.CollectionUtils.newMap;

/**
 * @author zabdisubhan
 */

@Component
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

    private final SanitizedLogger log = new SanitizedLogger(ContextRefreshedListener.class);

    @Autowired
    private DashboardWidgetService dashboardWidgetService;

    @Autowired
    private DefaultConfigService defaultConfigService;

    private DashboardWidget createAndSaveDashboardWidget(Boolean nativeReport, String widgetName, String displayName,
                                              String jspFilePath) {
        return createAndSaveDashboardWidget(nativeReport, widgetName, displayName, jspFilePath, null);
    }

    private DashboardWidget createAndSaveDashboardWidget(Boolean nativeReport, String widgetName, String displayName,
                                              String jspFilePath, String jsFilePath) {
        DashboardWidget dashboardWidget = new DashboardWidget();

        dashboardWidget.setAvailable(true);
        dashboardWidget.setNativeReport(nativeReport);
        dashboardWidget.setWidgetName(widgetName);
        dashboardWidget.setDisplayName(displayName);
        dashboardWidget.setJspFilePath(jspFilePath);

        if(jsFilePath != null && !jsFilePath.isEmpty()) {
            dashboardWidget.setJsFilePath(jsFilePath);
        }

        log.info("Storing new Dashboard Widget [" + dashboardWidget.getDisplayName() + "].");
        dashboardWidgetService.store(dashboardWidget);

        return dashboardWidget;
    }

    private void addNativeDashboardWidgets() {

        DefaultConfiguration config = defaultConfigService.loadCurrentConfiguration();

        config.setDashboardTopLeftId(createAndSaveDashboardWidget(
                true,
                "vulnerabilityTrending",
                "Vulnerability Trending",
                "/WEB-INF/views/applications/widgets/vulnerabilityTrending.jsp",
                "/scripts/left-report-controller.js").getId());

        config.setDashboardTopRightId(createAndSaveDashboardWidget(
                true,
                "mostVulnerableApps",
                "Most Vulnerable Applications",
                "/WEB-INF/views/applications/widgets/mostVulnerableApps.jsp",
                "/scripts/right-report-controller.js").getId());

        config.setDashboardBottomLeftId(createAndSaveDashboardWidget(
                true,
                "recentUploads",
                "Recent Uploads",
                "/WEB-INF/views/applications/widgets/recentUploads.jsp").getId());

        config.setDashboardBottomRightId(createAndSaveDashboardWidget(
                true,
                "recentComments",
                "Recent Comments",
                "/WEB-INF/views/applications/widgets/recentComments.jsp").getId());

        defaultConfigService.saveConfiguration(config);
        log.info("Setting native Dashboard Widgets positions in Default Configuration.");
    }

    @SuppressWarnings("unchecked")
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if(!dashboardWidgetService.isInitialized()) {

            dashboardWidgetService.setInitialized(true);

            if (dashboardWidgetService.loadAllNativeReports().size() == 0) {
                addNativeDashboardWidgets();
            }

            List<DashboardWidget> dashboardWidgets = dashboardWidgetService.loadAllNonNativeReports();
            Map<DashboardWidget, Boolean> availableReportPlugins = newMap();

            for (DashboardWidget dashboardWidget : dashboardWidgets) {
                if (dashboardWidget.getAvailable()) {
                    availableReportPlugins.put(dashboardWidget, false);
                }
            }

            Map<Class<?>, ReportPlugin> typeMap =
                    AnnotationLoader.getMap(
                            ReportPlugin.class,
                            "com.denimgroup.threadfix");

            log.info("ReportPlugin map has " + typeMap.entrySet().size() + " entries.");

            for (Map.Entry<Class<?>, ReportPlugin> entry : typeMap.entrySet()) {
                ReportPlugin annotation = entry.getValue();

                if (annotation.displayName().isEmpty() || annotation.jspRelFilePath().isEmpty()
                        || annotation.reportName().isEmpty()) {

                    log.warn("Required attrs for ReportPlugin were empty. Skip to next ReportPlugin.");
                    continue;
                }

                DashboardWidget widget = null;

                for (DashboardWidget dashboardWidget : dashboardWidgets) {
                    if (dashboardWidget.getWidgetName().equals(annotation.reportName())) {

                        // note that report plugin is available to Threadfix
                        widget = dashboardWidget;
                        availableReportPlugins.put(dashboardWidget, true);

                        // set previously unavailable report plugin to true
                        // now that plugin has be re-added to Threadfix
                        if (!dashboardWidget.getAvailable()) {
                            log.info("Plugin for existing Dashboard Widget [" + dashboardWidget.getDisplayName() +
                                    "] was found. Setting availability to true.");
                            dashboardWidget.setAvailable(true);
                            dashboardWidgetService.store(dashboardWidget);
                        }
                    }
                }

                if (widget != null && availableReportPlugins.get(widget)) {
                    log.info("ReportPlugin already exists as a Dashboard Widget. Skip to next ReportPlugin.");
                    continue;
                }

                createAndSaveDashboardWidget(false, annotation.reportName(), annotation.displayName(),
                        annotation.jspRelFilePath(), annotation.jsRelFilePath());
            }

            // set dashboard reports" availability that were not found in annotations to false
            for (DashboardWidget dashboardWidget : availableReportPlugins.keySet()) {
                if (!availableReportPlugins.get(dashboardWidget)) {
                    if (dashboardWidget.getAvailable()) {
                        log.info("Plugin for existing Dashboard Widget [" + dashboardWidget.getDisplayName() +
                                "] not found. Setting availability to false.");
                        dashboardWidget.setAvailable(false);
                        dashboardWidgetService.store(dashboardWidget);
                    }
                }
            }
        }
    }
}
