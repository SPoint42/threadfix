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
package com.denimgroup.threadfix.service;

import com.denimgroup.threadfix.data.dao.DashboardWidgetDao;
import com.denimgroup.threadfix.data.entities.DashboardWidget;
import com.denimgroup.threadfix.logging.SanitizedLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly=true)
public class DashboardWidgetServiceImpl implements DashboardWidgetService {

    private final SanitizedLogger log = new SanitizedLogger(DashboardWidgetServiceImpl.class);

    @Autowired
    private DashboardWidgetDao dashboardWidgetDao;

    private boolean initialized = false;

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public List<DashboardWidget> loadAll() {
        return dashboardWidgetDao.retrieveAll();
    }

    @Override
    public List<DashboardWidget> loadAllAvailable() {
        return dashboardWidgetDao.retrieveAllAvailable();
    }

    @Override
    public List<DashboardWidget> loadByIds(List<Integer> dashboardWidgetIds) {
        return dashboardWidgetDao.retrieveByIds(dashboardWidgetIds);
    }

    @Override
    public List<DashboardWidget> loadAllNativeReports() {
        return dashboardWidgetDao.retrieveAllNativeReports();
    }

    @Override
    public List<DashboardWidget> loadAllNonNativeReports() {
        return dashboardWidgetDao.retrieveAllNonNativeReports();
    }

    @Override
    public DashboardWidget load(int dashboardWidgetId) {
        return dashboardWidgetDao.retrieveById(dashboardWidgetId);
    }

    @Override
    public DashboardWidget load(String name) {
        return dashboardWidgetDao.retrieveByName(name);
    }

    @Override
    @Transactional(readOnly = false)
    public void store(DashboardWidget dashboardWidget) {
        dashboardWidgetDao.saveOrUpdate(dashboardWidget);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteById(int dashboardWidgetId) {
        log.info("Deleting Dashboard Widget with ID " + dashboardWidgetId);
        dashboardWidgetDao.delete(dashboardWidgetId);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(DashboardWidget dashboardWidget) {
        log.info("Deleting Dashboard Widget '" + dashboardWidget.getWidgetName() +"'.");
        dashboardWidgetDao.delete(dashboardWidget);
    }

}
