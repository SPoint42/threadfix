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
package com.denimgroup.threadfix.importer.impl.remoteprovider;

import com.denimgroup.threadfix.data.entities.*;
import com.denimgroup.threadfix.importer.util.DateUtils;
import org.apache.commons.codec.binary.Base64;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class VeracodeRemoteProvider extends RemoteProvider {

	private static final String GET_APP_BUILDS_URI = "https://analysiscenter.veracode.com/api/2.0/getappbuilds.do";
	private static final String GET_DETAILED_REPORT_URI = "https://analysiscenter.veracode.com/api/detailedreport.do";
	
	private static final String
		DATE_FORMAT_WITH_T = "yyyy-MM-dd'T'HH:mm:ss",
		DATE_FORMAT_WITHOUT_T = "yyyy-MM-dd kk:mm:ss";

	private String password = null;
	private String username = null;

	public VeracodeRemoteProvider() {
		super(ScannerType.VERACODE);
	}

	@Override
	public List<Scan> getScans(RemoteProviderApplication remoteProviderApplication) {
		if (remoteProviderApplication == null ||
				remoteProviderApplication.getApplicationChannel() == null) {
			LOG.error("Veracode getScan() called with invalid parameters. Returning null");
			return null;
		}
		
		username = remoteProviderApplication.getRemoteProviderType().getUsername();
		password = remoteProviderApplication.getRemoteProviderType().getPassword();
		
		// This block tries to get the latest build for the app and dies if it fails.
		InputStream appBuildsInputStream = getUrl(GET_APP_BUILDS_URI,username,password);
		String appName = remoteProviderApplication.getNativeId();
		VeracodeApplicationIdMapParser parser = new VeracodeApplicationIdMapParser();
		
		List<String> buildIds = null;
		
		if (appBuildsInputStream != null) {
			parse(appBuildsInputStream, parser);
			buildIds = parser.map.get(appName);
		}
		
		if (buildIds == null || buildIds.size() == 0) {
			LOG.warn("No build IDs were parsed.");
			return null; // we failed.
		} else {
			LOG.warn("Retrieved build IDs " + buildIds + " for application " + appName);
		}
		
		List<Scan> scans = new ArrayList<>();
		
		for (String buildId : buildIds) {
			if (buildId == null || buildId.trim().equals("")) {
				LOG.warn("Build ID was null or empty. This should never happen.");
				continue; // we failed.
            } else if (parser.dateMap.get(buildId) != null && parser.dateMap.get(buildId)
                    .before(remoteProviderApplication.getLastImportTime())) {
                log.info("Build ID " + buildId + " was scanned before the most recent scan in ThreadFix.");
                continue;
            } else if (parser.dateMap.get(buildId)== null) {
                log.info("Build ID " + buildId + " was null.");
                continue;
            }
				
			LOG.warn("Importing scan for build ID " + buildId + " and application " + appName);
	
			// This block tries to parse the scan corresponding to the build.
			inputStream = getUrl(GET_DETAILED_REPORT_URI + "?build_id=" + buildId, username, password);

			if (inputStream == null) {
				LOG.warn("Received a bad response from Veracode servers, returning null.");
				continue;
			}
			
			VeracodeSAXParser scanParser = new VeracodeSAXParser();
			Scan resultScan = parseSAXInput(scanParser);
			
			if (resultScan == null) {
				LOG.error("No scan was parsed, something is broken.");
				continue;
			}
			
			resultScan.setImportTime(parser.dateMap.get(buildId));
			resultScan.setApplicationChannel(remoteProviderApplication.getApplicationChannel());
			
			LOG.info("Veracode scan (Build ID " + buildId + ") was successfully parsed.");
			
			scans.add(resultScan);
		}
		
		return scans;
	}

	@Override
	public List<RemoteProviderApplication> fetchApplications() {
		if (remoteProviderType == null || remoteProviderType.getUsername() == null ||
				remoteProviderType.getPassword() == null) {
			LOG.warn("Insufficient credentials.");
			return null;
		}
		
		LOG.info("Fetching Veracode applications.");
		
		password = remoteProviderType.getPassword();
		username = remoteProviderType.getUsername();
		
		InputStream stream = null;
		
		stream = getUrl(GET_APP_BUILDS_URI,username,password);
		
		if (stream == null) {
			LOG.warn("Got a bad response from Veracode. Check your username and password.");
			return null;
		}
		
		VeracodeApplicationBuildsParser parser = new VeracodeApplicationBuildsParser();
		
		parse(stream, parser);
		
		if (parser.list != null && parser.list.size() > 0) {
			LOG.info("Number of Veracode applications found: " + parser.list.size());
		} else {
			LOG.warn("No Veracode applications were found. Check your configuration.");
		}
		
		return parser.list;
	}
	
	public InputStream getUrl(String urlString, String username, String password) {
		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
            LOG.error("Encountered MalformedURLException in getUrl in VeracodeRemoteProvider.", e);
			return null;
		}

		try {
            HttpsURLConnection m_connect;
            if (proxyService == null) {
                m_connect = (HttpsURLConnection) url.openConnection();
            } else {
                m_connect = proxyService.getSSLConnectionWithProxyConfig(url);
            }

			setupAuthorization(m_connect, username, password);

			return m_connect.getInputStream();
		} catch (IOException e) {
            LOG.error("Encountered IOException in getUrl in VeracodeRemoteProvider.", e);
		}
		return null;
	}

	public void setupAuthorization(HttpsURLConnection connection,
			String username, String password) {
		String login = username + ":" + password;
		String encodedLogin = new String(Base64.encodeBase64(login.getBytes()));
		//String encodedLogin = Base64.encodeBase64String(login.getBytes());
		connection.setRequestProperty("Authorization", "Basic " + encodedLogin);
	}

	public class VeracodeApplicationBuildsParser extends DefaultHandler {
		
		public List<RemoteProviderApplication> list = new ArrayList<>();

	    @Override
		public void startElement (String uri, String name, String qName, Attributes atts) throws SAXException {
	    	if (qName.equals("application")) {
	    		RemoteProviderApplication remoteProviderApplication = new RemoteProviderApplication();
	    		remoteProviderApplication.setNativeId(atts.getValue("app_name"));
	    		remoteProviderApplication.setRemoteProviderType(remoteProviderType);
	    		list.add(remoteProviderApplication);
	    	}
	    }
	}
	
	public class VeracodeApplicationIdMapParser extends DefaultHandler {
		
		public Map<String, List<String>> map = new HashMap<>();
		public Map<String, Calendar> dateMap = new HashMap<>();
		
		private String currentAppName = null;
		private String currentBuildId = null;
		
	    @Override
		public void startElement (String uri, String name, String qName, Attributes atts) throws SAXException {
	    	if (qName.equals("application")) {
	    		currentAppName = atts.getValue("app_name");
	    		map.put(currentAppName, new ArrayList<String>());
	    	} else if (currentAppName != null && qName.equals("build")) {
	    		currentBuildId = atts.getValue("build_id");
	    		map.get(currentAppName).add(currentBuildId);
	    	} else if (currentAppName != null && currentBuildId != null
	    			&& qName.equals("analysis_unit")) {
	    		
	    		String dateString = atts.getValue("published_date");
	    		if (dateString != null && dateString.length() > 5) {
					dateString = dateString.substring(0,dateString.length() - 5);
				}
	    		
	    		Calendar calendar = DateUtils.getCalendarFromString(DATE_FORMAT_WITH_T, dateString);
	    		dateMap.put(currentBuildId, calendar);
	    	}
	    }
	}
	
	public class VeracodeSAXParser extends DefaultHandler {
		
		private boolean inStaticFlaws = true;
		
		private Finding lastFinding = null;
		private boolean mitigationProposed = false;

	    ////////////////////////////////////////////////////////////////////
	    // Event handlers.
	    ////////////////////////////////////////////////////////////////////

	    @Override
		public void startElement (String uri, String name, String qName, Attributes atts) {
	    	if ("detailedreport".equals(qName)) {
	    		date = DateUtils.getCalendarFromString(DATE_FORMAT_WITHOUT_T, atts.getValue("last_update_time"));
	    		if (date == null) {
					date = DateUtils.getCalendarFromString(DATE_FORMAT_WITHOUT_T, atts.getValue("generation_date"));
				}
	    	}
	    	
	    	if ("dynamicflaws".equals(qName)) {
	    		inStaticFlaws = false;
	    	}
	    	
	    	// TODO look through more Veracode scans and see if the inputvector component is the parameter.
	    	if ("flaw".equals(qName)) {
	    		if ("Fixed".equals(atts.getValue("remediation_status"))) {
					return;
				}
	    		
	    		String url = null;
	    		if (atts.getValue("url") != null) {
					url = atts.getValue("url");
				} else if (atts.getValue("location") != null) {
					url = atts.getValue("location");
				}

	    		Finding finding = constructFinding(url,
	    										   atts.getValue("vuln_parameter"),
	    										   atts.getValue("cweid"),
	    										   atts.getValue("severity"));
	    		if (finding != null) {
	    			finding.setNativeId(atts.getValue("issueid"));
	    			
	    			// TODO revise this method of deciding whether the finding is static.
    				finding.setIsStatic(inStaticFlaws);
    				if (atts.getValue("sourcefile") != null && atts.getValue("sourcefilepath") != null) {
    					String sourceFileLocation = atts.getValue("sourcefilepath") + atts.getValue("sourcefile");
    					finding.setSourceFileLocation(sourceFileLocation);
    					finding.getSurfaceLocation().setPath(sourceFileLocation);
    					if (atts.getValue("line") != null) {
    						DataFlowElement dataFlowElement = new DataFlowElement();
    						dataFlowElement.setFinding(finding);
    						try {
    							dataFlowElement.setLineNumber(Integer.valueOf(atts.getValue("line")));
    						} catch (NumberFormatException e) {
    							LOG.error("Non-numeric value '" + atts.getValue("line") + "' found in Veracode results when trying to parse line number.", e);
    						}
    						dataFlowElement.setSourceFileName(sourceFileLocation);
    						finding.setDataFlowElements(new ArrayList<DataFlowElement>());
    						finding.getDataFlowElements().add(dataFlowElement);
    					}
    				}
    				lastFinding = finding;
    				mitigationProposed = false;
	        		saxFindingList.add(finding);
	    		}
	    	}
	    	
	    	if (mitigationProposed && "mitigation".equals(qName) &&
	    			atts.getValue("action") != null &&
	    			atts.getValue("action").equals("Mitigation Accepted")) {
	    		mitigationProposed = false;
	    		lastFinding.setMarkedFalsePositive(true);
	    		LOG.info("The false positive mitigation was accepted.");
	    	}
	    	
	    	if ("mitigation".equals(qName) && atts.getValue("action") != null
	    			&& atts.getValue("action").equals("Mitigated as Potential False Positive")) {
	    		mitigationProposed = true;
	    		LOG.info("Found a Finding with false positive mitigation proposed.");
	    	}
	    }
	    
	    @Override
	    public void endElement (String uri, String localName, String qName) throws SAXException {
	    	if (qName.equals("dynamicflaws")) {
	    		if ("dynamicflaws".equals(qName)) {
		    		inStaticFlaws = true;
		    	}
	    	}
	    }
	}
}
