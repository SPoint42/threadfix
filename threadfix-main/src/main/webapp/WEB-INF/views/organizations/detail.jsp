<%@ include file="/common/taglibs.jsp"%>

<head>
	<title><c:out value="${ organization.name }"/></title>
	<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/team-detail-page-controller.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/reports-controller.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/modal-controller-with-config.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/vuln-search-controller.js"></script>
</head>

<body ng-controller="TeamDetailPageController"
      id="apps">

    <%@ include file="/WEB-INF/views/angular-init.jspf"%>

    <%@ include file="/WEB-INF/views/applications/forms/newApplicationForm.jsp" %>
    <%@ include file="/WEB-INF/views/organizations/editTeamForm.jsp" %>
    <%@ include file="/WEB-INF/views/config/users/permissibleUsers.jsp" %>

    <ul class="breadcrumb">
        <li><a href="<spring:url value="/organizations"/>">Applications Index</a> <span class="divider">/</span></li>
        <li class="active">Team: {{ team.name }}</li>
    </ul>
    <h2 id="name" style="padding-top:5px;">
        {{ team.name }}
        <c:if test="${ canManageTeams || canManageUsers }">
            <div id="btnDiv1" class="btn-group">
                <button id="actionButton" class="btn dropdown-toggle" data-toggle="dropdown" type="button">Action <span class="caret"></span></button>
                <ul class="dropdown-menu">
                    <c:if test="${ canManageTeams }">
                        <li>
                            <a id="teamModalButton" ng-click="openEditModal()">Edit / Delete</a>
                        </li>
                    </c:if>
                    <c:if test="${ canModifyVulnerabilities }">
                        <li>
                            <spring:url value="{orgId}/filters" var="filterUrl">
                                <spring:param name="orgId" value="${ organization.id }"/>
                            </spring:url>
                            <a id="editfiltersButton1" href="<c:out value='${ filterUrl }'/>" data-toggle="modal">
                                Edit Filters
                            </a>
                        </li>
                    </c:if>
                    <c:if test="${ canManageUsers && isEnterprise }">
                        <li><a id="userListModelButton" ng-click="showUsers()">View Permissible Users</a></li>
                    </c:if>
                </ul>
            </div>
        </c:if>
    </h2>

    <%@ include file="/WEB-INF/views/successMessage.jspf" %>

    <div class="container-fluid">
        <%@ include file="/WEB-INF/views/applications/reports.jspf" %>
    </div>

    <tabset style="margin-top:10px;">
        <tab heading="{{ applications.length }} Applications">
            <%@ include file="applicationsTable.jsp" %>
        </tab>
        <tab heading="{{ vulnerabilityCount }} Vulnerabilities" ng-click="clickVulnTab()">
            <%@ include file="../vulnerabilities/vulnSearchControls.jsp" %>
        </tab>
    </tabset>

</body>
