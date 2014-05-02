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

package com.denimgroup.threadfix.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Calendar;

@Entity
@Table(name="DefaultConfiguration")
public class DefaultConfiguration extends BaseEntity {
	
	private static final long serialVersionUID = 2584623185996706729L;
	
	private Boolean globalGroupEnabled = null;

    @Column
    public Boolean getHasCachedData() {
        return hasCachedData != null && hasCachedData;
    }

    public void setHasCachedData(Boolean hasCachedData) {
        this.hasCachedData = hasCachedData;
    }

    private Boolean hasCachedData = null;
	private Integer defaultRoleId = null;
	
	private String activeDirectoryBase, activeDirectoryURL, activeDirectoryUsername, activeDirectoryCredentials;

    private String proxyHost = null, proxyUsername = null, proxyPassword = null, proxyUsernameEncrypted = null, proxyPasswordEncrypted = null;
    private Integer proxyPort = null;
	
	private Calendar lastScannerMappingsUpdate;

    public static DefaultConfiguration getInitialConfig() {
        DefaultConfiguration config = new DefaultConfiguration();
        config.setDefaultRoleId(1);
        config.setGlobalGroupEnabled(true);
        return config;
    }
	
	@Column
	public Integer getDefaultRoleId() {
		return defaultRoleId;
	}
	
	public void setDefaultRoleId(Integer defaultRoleId) {
		this.defaultRoleId = defaultRoleId;
	}
	
	@Column
	public Boolean getGlobalGroupEnabled() {
		return globalGroupEnabled != null && globalGroupEnabled;
	}
	public void setGlobalGroupEnabled(Boolean globalGroupEnabled) {
		this.globalGroupEnabled = globalGroupEnabled;
	}
	
	@Column(length=256)
	public void setActiveDirectoryBase(String activeDirectoryBase) {
		this.activeDirectoryBase = activeDirectoryBase;
	}
	
	public String getActiveDirectoryURL() {
		if (activeDirectoryURL == null) {
			return "";
		} else {
			return activeDirectoryURL;
		}
	}
	
	@Column(length=256)
	public void setActiveDirectoryURL(String activeDirectoryURL) {
		this.activeDirectoryURL = activeDirectoryURL;
	}
	public String getActiveDirectoryUsername() {
		if (activeDirectoryUsername == null) {
			return "";
		} else {
			return activeDirectoryUsername;
		}
	}
	
	@Column(length=256)
	public void setActiveDirectoryUsername(String activeDirectoryUsername) {
		this.activeDirectoryUsername = activeDirectoryUsername;
	}
	
	public String getActiveDirectoryCredentials() {
		if (activeDirectoryCredentials == null) {
			return "";
		} else {
			return activeDirectoryCredentials;
		}
	}
	
	@Column(length=256)
	public void setActiveDirectoryCredentials(String activeDirectoryCredentials) {
		this.activeDirectoryCredentials = activeDirectoryCredentials;
	}
	
	public String getActiveDirectoryBase() {
		if (activeDirectoryCredentials == null) {
			return "";
		} else {
			return activeDirectoryBase;
		}
	}

	@Column
	public Calendar getLastScannerMappingsUpdate() {
		return lastScannerMappingsUpdate;
	}

	public void setLastScannerMappingsUpdate(Calendar lastScannerMappingsUpdate) {
		this.lastScannerMappingsUpdate = lastScannerMappingsUpdate;
	}

    @Transient
    public String getProxyUsername() {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    @Transient
    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    @Column
    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    @Column(length = 1024)
    public String getProxyUsernameEncrypted() {
        return proxyUsernameEncrypted;
    }

    public void setProxyUsernameEncrypted(String proxyUsernameEncrypted) {
        this.proxyUsernameEncrypted = proxyUsernameEncrypted;
    }

    @Column(length = 1024)
    public String getProxyPasswordEncrypted() {
        return proxyPasswordEncrypted;
    }

    public void setProxyPasswordEncrypted(String proxyPasswordEncrypted) {
        this.proxyPasswordEncrypted = proxyPasswordEncrypted;
    }

    @Column(length = 1024)
    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    @Transient
    public boolean hasConfiguredHostAndPort() {
        return proxyHost != null && !proxyHost.equals("") &&
                proxyPort != null && proxyPort > 0;
    }

    @Transient
    public boolean hasConfiguredCredentials() {
        return proxyUsername != null && !proxyUsername.equals("") &&
                proxyPassword != null && !proxyPassword.equals("");
    }


}
