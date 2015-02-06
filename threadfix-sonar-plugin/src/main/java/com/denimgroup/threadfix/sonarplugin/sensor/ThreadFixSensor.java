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
package com.denimgroup.threadfix.sonarplugin.sensor;

import com.denimgroup.threadfix.data.entities.Application;
import com.denimgroup.threadfix.data.entities.VulnerabilityMarker;
import com.denimgroup.threadfix.data.interfaces.Endpoint;
import com.denimgroup.threadfix.framework.engine.full.EndpointDatabase;
import com.denimgroup.threadfix.framework.engine.full.EndpointDatabaseFactory;
import com.denimgroup.threadfix.importer.util.SpringConfiguration;
import com.denimgroup.threadfix.remote.PluginClient;
import com.denimgroup.threadfix.service.merge.Merger;
import com.denimgroup.threadfix.sonarplugin.ThreadFixMetrics;
import com.denimgroup.threadfix.sonarplugin.configuration.Mode;
import com.denimgroup.threadfix.sonarplugin.configuration.ThreadFixInfo;
import com.denimgroup.threadfix.sonarplugin.util.SonarTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.SonarIndex;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.issue.Issuable;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by mcollins on 1/28/15.
 */
public class ThreadFixSensor implements Sensor {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadFixSensor.class);
    private final FileSystem moduleFileSystem;

    private ThreadFixInfo info = null;
    private final ResourcePerspectives resourcePerspectives;
    private SonarIndex sonarIndex;

    public ThreadFixSensor(ResourcePerspectives resourcePerspectives,
                           Settings settings,
                           FileSystem moduleFileSystem,
                           SonarIndex sonarIndex) {
        this.sonarIndex = sonarIndex;
        checkProperties(settings);
        this.moduleFileSystem = moduleFileSystem;
        runHAM(this.moduleFileSystem);

        if (resourcePerspectives == null) {
            LOG.error("Got null resources perspective from autowiring. Will probably die.");
        }
        this.resourcePerspectives = resourcePerspectives;
    }

    private void runHAM(FileSystem moduleFileSystem) {

        EndpointDatabase database = EndpointDatabaseFactory.getDatabase(moduleFileSystem.baseDir());

        if (database != null) {
            LOG.info("Got an EndpointDatabase successfully:");
            for (Endpoint endpoint : database) {
                LOG.info(endpoint.toString());
            }
        } else {
            LOG.info("Failed to get an EndpointDatabase.");
        }
    }

    private void checkProperties(Settings settings) {
        Map<String, String> properties = settings.getProperties();

        ThreadFixInfo info = new ThreadFixInfo(properties);

        if (!info.valid()) {
            LOG.info("Invalid ThreadFix configuration.");
            for (String error : info.getErrors()) {
                LOG.info(error);
            }
            this.info = null;
        } else if (info.getMode() == Mode.LOCAL) {
            LOG.info("Using ThreadFix filesystem properties.");
            this.info = info;
        } else if (testConnection(info)) {
            LOG.info("ThreadFix connection was valid.");
            this.info = info;
        } else {
            LOG.info("ThreadFix properties were present but the connection failed.");
            this.info = null;
        }
    }

    private boolean testConnection(ThreadFixInfo info) {
        return info.getApplicationId() != null;
    }

    @Override
    public void analyse(Project project, SensorContext sensorContext) {

        if (info != null) {

            VulnerabilityMarker[] endpoints = getEndpoints();

            for (VulnerabilityMarker vulnerabilityMarker : endpoints) {
                LOG.debug("Got endpoint " + vulnerabilityMarker);

                processMarker(vulnerabilityMarker, sensorContext);
            }

            LOG.info("Setting total vulns to " + endpoints.length);

            String data = String.valueOf(endpoints.length);
            Double aDouble = Double.valueOf(data);

            Measure measure1 = new Measure(ThreadFixMetrics.TOTAL_VULNS, aDouble);
            measure1.setValue(aDouble);
            measure1.setPersistenceMode(PersistenceMode.FULL);
            sensorContext.saveMeasure(measure1);
        }
    }

    private VulnerabilityMarker[] getEndpoints() {
        if (info.getMode() == Mode.SERVER) {
            PluginClient client = getConfiguredClient();

            return client.getVulnerabilityMarkers(info.getApplicationId());
        } else {
            Application application = getApplication(moduleFileSystem.baseDir().getAbsolutePath(), info.getFiles());

            List<VulnerabilityMarker> markers = application.getMarkers();
            return markers.toArray(new VulnerabilityMarker[markers.size()]);
        }
    }

    public static Application getApplication(String sourceRoot, Collection<String> filePaths) {

        SpringConfiguration.initializeWithClassLoader(ThreadFixSensor.class.getClassLoader());

        return SpringConfiguration.getSpringBean(Merger.class).mergeFromDifferentScannersInternal(sourceRoot, filePaths);
    }

    private void processMarker(VulnerabilityMarker vulnerabilityMarker, SensorContext sensorContext) {

        Resource resource = SonarTools.resourceOf(sonarIndex,
                sensorContext,
                vulnerabilityMarker.getFilePath());

        if (resource != null) {

            Issuable issuable = resourcePerspectives.as(Issuable.class, resource);

            if (issuable != null) {
                SonarTools.addIssue(issuable, resource, vulnerabilityMarker);
            } else {
                LOG.error("Failed to get issuable for resource " + resource);
            }
        } else {
            LOG.debug("Got null resource for path " + vulnerabilityMarker.getFilePath());
        }

    }



    public PluginClient getConfiguredClient() {
        return new PluginClient(info.getUrl(), info.getApiKey());
    }

    @Override
    public boolean shouldExecuteOnProject(Project project) {
        return true;
    }
}
