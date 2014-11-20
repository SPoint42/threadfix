    <div class="vuln-tree">

        <div ng-if="remediationEnterpriseActive" ng-init="newFilteredTagName = enterpriseTags[0].name; addNewTag(newFilteredTagName)">
            <c:if test="${isEnterprise}">
                <select ng-change="addNewTag(newFilteredTagName)" style="margin-bottom: 0" class="complianceTypeSelect" id="complianceTypeSelect" ng-model="newFilteredTagName">
                    <option ng-selected="newFilteredTagName === option.name" ng-repeat="option in enterpriseTags" value="{{ option.name }}">
                        {{ option.name }}
                    </option>
                </select>
            </c:if>
        </div>
        <span class="spinner-div">
            <span id="loadingSpinner" ng-if="loading" class="spinner dark"></span>
        </span>

        <div ng-show="complianceActive || remediationEnterpriseActive">
            <d3-trending data="trendingScansData" label="title" width="670" height="400" margin="margin"
                         table-info="tableInfo" start-date="trendingStartDate" end-date="trendingEndDate" export-info="exportInfo" svg-id="graphName"></d3-trending>
        </div>

        <div id="complianceTable">
            <table>
                <thead></thead>
                <tbody></tbody>
            </table>
        </div>

        <div ng-controller="VulnerabilityCommentsTableController" ng-init="vulnType='openVulns'; init()">
            <h4 style="padding-top:10px">Open Vulnerabilities</h4>
            <%@ include file="/WEB-INF/views/tags/commentTable.jsp" %>
        </div>

        <div ng-controller="VulnerabilityCommentsTableController" ng-init="vulnType='closedVulns'; init()">
            <h4 style="padding-top:10px">Closed Vulnerabilities</h4>
            <%@ include file="/WEB-INF/views/tags/commentTable.jsp" %>
        </div>

    </div>

    <div id="complianceFilterDiv" class="filter-controls">
        <h3>Filters</h3>

        <tabset ng-init="showFilterSections = true">
            <tab heading="Filters" ng-click="$parent.showFilterSections = true; $parent.showSavedFilters = false">
            </tab>
            <tab heading="Load Filters" ng-click="$parent.showFilterSections = false; $parent.showSavedFilters = true">
            </tab>
        </tabset>

        <%@ include file="filter.jsp" %>
    </div>




