<%@ include file="/common/taglibs.jsp"%>

<head>
	<title>Manage Filters</title>
	<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/vulnerability-filters-controller.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/modal-controller-with-config.js"></script>
</head>

<body ng-controller="VulnerabilityFiltersController">

    <%@ include file="/WEB-INF/views/angular-init.jspf"%>

    <div ng-hide="initialized" class="spinner-div"><span class="spinner dark"></span>Loading</div><br>

    <div ng-show="initialized">
        <%@ include file="/WEB-INF/views/filters/form.jsp"%>

        <ul ng-show="type !== 'Global'" class="breadcrumb">
            <li><a href="<spring:url value="/"/>">Applications Index</a> <span class="divider">/</span></li>

            <li ng-show="type === 'Application'"><a ng-click="goToTeam(organization)">Team: {{ application.team.name }}</a> <span class="divider">/</span></li>
            <li ng-show="type === 'Application'"><a ng-click="goToTeam(organization)">Application: {{ application.name }}</a><span class="divider">/</span></li>

            <li ng-show="type === 'Organization'"><a ng-click="goToTeam(organization)">Team: {{ organization.name }}</a> <span class="divider">/</span></li>

            <li class="active">Vulnerability Filters</li>
        </ul>

        <h2 ng-show="type === 'Application'">Application {{ application.name }} Filters</h2>
        <h2 ng-show="type === 'Organization'">Team {{ organization.name }} Filters</h2>
        <h2 ng-show="type === 'Global'">Global Filters</h2>

        <div id="helpText">
            ThreadFix Vulnerability Filters are used to sort data.<br/>
        </div>

        <tabset ng-hide="type === 'Global'">
            <tab ng-click="setTab('Applications')" ng-show="type === 'Application'" heading="Application Filters"></tab>
            <tab ng-click="setTab('Organization')" heading="Team Filters"></tab>
            <tab ng-click="setTab('Global')" heading="Global Filters"></tab>
        </tabset>

        <div id="tabsDiv">
            <h3>{{ vulnFiltersTitle }}</h3>

            <%@ include file="/WEB-INF/views/successMessage.jspf" %>

            <a id="createNewKeyModalButton" ng-click="showNewFilterModal()" class="btn">Create New Filter</a>

            <div id="tableDiv">
                <%@ include file="/WEB-INF/views/filters/table.jsp" %>
            </div>

            <h3>{{ severityFiltersTitle }}</h3>

            <%@ include file="/WEB-INF/views/filters/severityFilterForm.jsp" %>
        </div>
    </div>
</body>
