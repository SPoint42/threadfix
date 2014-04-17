<%@ include file="/common/taglibs.jsp"%>

<%@ include file="/WEB-INF/views/successMessage.jspf" %>


<div style="padding-bottom:10px">
	<security:authorize ifAnyGranted="ROLE_CAN_MANAGE_TEAMS">
		<a id="addTeamModalButton" href="#myTeamModal" role="button" class="btn" data-toggle="modal" 
			data-default-show="<c:out value="${ showTeamModal }"/>">Add Team</a>
	</security:authorize>
	<c:if test="${ not empty organizationList }">
		<a class="btn" id="expandAllButton">Expand All</a>
		<a class="btn" id="collapseAllButton">Collapse All</a>
	</c:if>
</div>

<c:if test="${ empty organizationList }">
	<security:authorize ifNotGranted="ROLE_CAN_MANAGE_TEAMS">
		<div class="alert alert-error">
			You don't have permission to access any ThreadFix applications or to create one for yourself. 
			Contact your administrator to get help.
		</div>
	</security:authorize>
</c:if>

<c:if test="${ not empty organizationList }">
<table class="table table-hover white-inner-table">
	<thead>
		<tr>
			<th style="width:8px"></th>
			<th style="width:98px;">Name</th>
			<th class="centered fixed-team-header">Total</th>
			<th class="centered fixed-team-header">Critical</th>
			<th class="centered fixed-team-header">High</th>
			<th class="centered fixed-team-header">Medium</th>
			<th class="centered fixed-team-header">Low</th>
			<th class="centered fixed-team-header">Info</th>
			<th></th>
			<th style="width:130px;"></th>
			<th style="width:70px;"></th>
		</tr>
	</thead>
	<c:forEach var="organization" items="${ organizationList }" varStatus="status">
		<tr id="teamRow<c:out value="${ organization.name }"/>" class="pointer" data-target-div="teamInfoDiv${ organization.id}"
				data-caret-div="caret${ organization.id }" data-report-div="reportDiv${organization.id}">
			<td id="teamCaret<c:out value="${ organization.name }"/>" class="expandableTrigger">
				<span id="caret<c:out value="${ organization.name }"/>" class="caret-right"></span>
			</td>
			<td class="expandableTrigger" id="teamName<c:out value="${ organization.name }"/>">
				<div style="word-wrap: break-word;width:300px;text-align:left;"><c:out value="${ organization.name }"/></div>
			</td>
			<td class="centered expandableTrigger" id="numTotalVulns<c:out value="${ organization.name }"/>"><c:out value="${ organization.totalVulnCount }"/></td>
			<td class="centered expandableTrigger" id="numCriticalVulns<c:out value="${ organization.name }"/>"><c:out value="${ organization.criticalVulnCount }"/></td>
			<td class="centered expandableTrigger" id="numHighVulns<c:out value="${ organization.name }"/>"><c:out value="${ organization.highVulnCount }"/></td>
			<td class="centered expandableTrigger" id="numMediumVulns<c:out value="${ organization.name }"/>"><c:out value="${ organization.mediumVulnCount }"/></td>
			<td class="centered expandableTrigger" id="numLowVulns<c:out value="${ organization.name }"/>"><c:out value="${ organization.lowVulnCount }"/></td>
			<td class="centered expandableTrigger" id="numInfoVulns<c:out value="${ organization.name }"/>"><c:out value="${ organization.infoVulnCount }"/></td>
			<td class="expandableTrigger"></td>
			<td>
                <c:if test="${ licenseService }">
                    <a id="addApplicationModalButton<c:out value="${ organization.name }"/>" href="#myAppModal${ organization.id }"
                            role="button" class="btn" data-toggle="modal">
                        Add Application
                    </a>
                </c:if>
		        <c:if test="${ not canAddApps }">
                    <a id="addApplicationModalButton" href="javascript:alert('You have reached the application limit of <c:out value="${ appLimit }"/> for your current license. To upgrade your license, please contact Denim Group.');" class="btn">Add Application</a>

		        </c:if>
            <td>
				<spring:url value="/organizations/{orgId}" var="organizationUrl">
					<spring:param name="orgId" value="${ organization.id }"/>
				</spring:url>
				<a style="text-decoration:none" id="organizationLink<c:out value="${ organization.name }"/>" href="<c:out value="${ organizationUrl }"/>">View Team</a>
			</td>
		</tr>
		<tr class="grey-background">
			<td colspan="11">
				<div id="teamInfoDiv${organization.id}" class="collapse">
					<c:if test="${ organization.totalVulnCount != 0 }">
						<spring:url value="/organizations/{orgId}/getReport" var="reportUrl">
							<spring:param name="orgId" value="${ organization.id }"/>
						</spring:url>
						<div style="float:right;margin-right:-50px;margin-top:-40px;" id="reportDiv${organization.id}" data-url="<c:out value="${ reportUrl }"/>"></div>
					</c:if>
				
					<div id="teamAppTableDiv${ status.count }">
					<c:if test="${ empty organization.activeApplications }">
						No applications found.
					</c:if>
					<c:if test="${ not empty organization.activeApplications }">
						<table id="teamAppTable${ status.count }">
							<thead>
								<tr>
									<th style="width:70px;"></th>
									<th class="centered fixed-team-header">Total</th>
									<th class="centered fixed-team-header">Critical</th>
									<th class="centered fixed-team-header">High</th>
									<th class="centered fixed-team-header">Medium</th>
									<th class="centered fixed-team-header">Low</th>
									<th class="centered fixed-team-header">Info</th>
									<th style="width:110px;"></th>
								</tr>
							</thead>
						<c:forEach var="application" items="${ organization.applications }" varStatus="innerStatus">
							<c:if test="${ application.active }">
								<spring:url value="/organizations/{orgId}/applications/{appId}" var="appUrl">
									<spring:param name="orgId" value="${ organization.id }"/>
									<spring:param name="appId" value="${ application.id }"/>
								</spring:url>
								<spring:url value="/organizations/{orgId}/applications/{appId}/scans/upload" var="uploadUrl">
									<spring:param name="orgId" value="${ organization.id }"/>
									<spring:param name="appId" value="${ application.id }"/>
								</spring:url>
								<tr class="app-row">
									<td style="padding:5px;word-wrap: break-word;">
										<div style="word-wrap: break-word;width:120px;text-align:left;">
											<a id="applicationLink<c:out value="${ organization.name }"/>-<c:out value="${ application.name }"/>" href="${ fn:escapeXml(appUrl) }">
												<c:out value="${ application.name }"/>
											</a>
										</div>
									</td>
									<td class="centered" id="numTotalVulns<c:out value="${ organization.name }"/>-<c:out value="${ application.name }"/>"><c:out value="${ application.totalVulnCount }"/></td>
									<td class="centered" id="numCriticalVulns<c:out value="${ organization.name }"/>-<c:out value="${ application.name }"/>"><c:out value="${ application.criticalVulnCount }"/></td>
									<td class="centered" id="numHighVulns<c:out value="${ organization.name }"/>-<c:out value="${ application.name }"/>"><c:out value="${ application.highVulnCount }"/></td>
									<td class="centered" id="numMediumVulns<c:out value="${ organization.name }"/>-<c:out value="${ application.name }"/>"><c:out value="${ application.mediumVulnCount }"/></td>
									<td class="centered" id="numLowVulns<c:out value="${ organization.name }"/>-<c:out value="${ application.name }"/>"><c:out value="${ application.lowVulnCount }"/></td>
									<td class="centered" id="numInfoVulns<c:out value="${ organization.name }"/>-<c:out value="${ application.name }"/>"><c:out value="${ application.infoVulnCount }"/></td>
									<td class="centered" style="padding:5px;">
										<a id="uploadScanModalLink<c:out value="${ organization.name }"/>-<c:out value="${ application.name }"/>" href="#uploadScan${ application.id }" role="button" class="btn" data-toggle="modal">Upload Scan</a>
										<%@ include file="/WEB-INF/views/applications/modals/uploadScanModal.jsp" %>
									</td>
								</tr>
							</c:if>
						</c:forEach>
						</table>
					</c:if>
					
					</div>
					<security:authorize ifAnyGranted="ROLE_CAN_MANAGE_APPLICATIONS">
						<div id="myAppModal${ organization.id }" class="modal hide fade" tabindex="-1"
							role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
							<div id="formDiv${ organization.id }">
								<%@ include file="/WEB-INF/views/applications/forms/newApplicationForm.jsp" %>
							</div>
						</div>
					</security:authorize>
				</div>
			</td>
		</tr>
	</c:forEach>
</table>
</c:if>
