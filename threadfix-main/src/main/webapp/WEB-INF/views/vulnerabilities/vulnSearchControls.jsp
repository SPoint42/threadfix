<div class="vuln-search-filter-control" style="width:900px;" ng-controller="VulnSearchController">

    <%--<%@ include file="../successMessage.jspf" %>--%>

    <!-- This is the Action button -->
    <c:if test="${ canModifyVulnerabilities || canSubmitDefects }">
        <div ng-if="treeApplication">
            <div ng-show="treeApplication && vulnTree" id="btnDiv" class="btn-group" ng-controller="BulkOperationsController">
                {{ $parent | json }}
                <button ng-hide="submitting" id="actionItems" class="btn dropdown-toggle" data-toggle="dropdown" type="button">
                    Action <span class="caret"></span>
                </button>
                <ul class="dropdown-menu">

                    <c:if test="${ canSubmitDefects }">
                        <li ng-show="$parent.treeApplication.defectTracker"><a class="pointer" id="submitDefectButton" ng-click="showSubmitDefectModal()">Submit Defect</a></li>
                        <li ng-show="$parent.treeApplication.defectTracker"><a class="pointer" id="mergeDefectButton" ng-click="showMergeDefectModal()">Merge Defect</a></li>
                    </c:if>
                    <c:if test="${ canModifyVulnerabilities }">
                        <li ng-show="parameters.showOpen"><a class="pointer" id="closeVulnsButton" ng-click="closeVulnerabilities()">Close Vulnerabilities</a></li>
                        <li ng-show="parameters.showClosed"><a class="pointer" id="openVulnsButton" ng-click="openVulnerabilities()">Open Vulnerabilities</a></li>
                        <li><a class="pointer" id="markFalsePositivesButton" ng-click="markFalsePositives()">Mark as False Positive</a></li>
                        <li ng-show="parameters.showFalsePositive"><a class="pointer" id="unmarkFalsePositivesButton" ng-click="unmarkFalsePositives()">Unmark as False Positive</a></li>
                    </c:if>
                </ul>
                <button id="submittingButton" ng-disabled class="btn" ng-show="submitting">
                    <span class="spinner dark"></span>
                    Submitting
                </button>
            </div>
        </div>
    </c:if>

    <div class="filter-controls">
        <h3>Filters</h3>

        <!-- Clear / select all -->
        <div class="accordion-group">
            <div class="accordion-heading" style="text-align:center">
                <a id="toggleAllButton" class="btn" ng-click="toggleAllFilters()">
                    {{ (showSaveAndLoadControls || showTeamAndApplicationControls || showDetailsControls || showDateControls || showDateRange || showTypeAndMergedControls) ? 'Collapse' : 'Expand' }} All
                </a>
                <a id="clearFiltersButton" class="btn" ng-click="resetFilters()">Clear</a>
            </div>
        </div>


        <!-- Saved Filters section -->
        <div class="accordion-group">
            <div class="accordion-heading" ng-click="showSaveAndLoadControls = !showSaveAndLoadControls">
                <span id="savedFiltersExpand" class="icon" ng-class="{ 'icon-minus': showSaveAndLoadControls, 'icon-plus': !showSaveAndLoadControls }"></span> Saved Filters
            </div>
            <div ng-show="showSaveAndLoadControls" class="filter-group-body">
                <div class="accordion-inner">
                    <div id="saveFilterSuccessMessage" ng-show="saveFilterSuccessMessage" class="alert alert-success">
                        <button id="closeSaveFilterSuccessMessage" class="close" ng-click="saveFilterSuccessMessage = undefined" type="button">&times;</button>
                        {{ saveFilterSuccessMessage }}
                    </div>
                    <div id="saveFilterErrorMessage" ng-show="saveFilterErrorMessage" class="alert alert-success">
                        <button id="closeSaveFilterErrorMessage" class="close" ng-click="saveFilterErrorMessage = undefined" type="button">&times;</button>
                        {{ saveFilterErrorMessage }}
                    </div>
                    <input id="filterNameInput" style="width: 180px;" placeholder="Enter a name for the filter" ng-model="currentFilterNameInput" type="text"/>
                    <a id="saveFilterButton" class="btn btn-primary" ng-hide="savingFilter" ng-disabled="!currentFilterNameInput" ng-click="saveCurrentFilters()">Create</a>
                    <button id="savingFilterButton"
                            ng-show="savingFilter"
                            disabled="disabled"
                            class="btn btn-primary">
                        <span class="spinner"></span>
                        Saving
                    </button>
                </div>
                <div ng-show="savedFilters" class="accordion-inner">
                    <div id="deleteFilterSuccessMessage" ng-show="deleteFilterSuccessMessage" class="alert alert-success">
                        <button id="closeDeleteFilterSuccessMessage" class="close" ng-click="deleteFilterSuccessMessage = undefined" type="button">&times;</button>
                        {{ deleteFilterSuccessMessage }}
                    </div>
                    <select id="filterSelect" style="width: 194px;" ng-model="selectedFilter" ng-options="filter.name for filter in savedFilters">
                        <option>Select a Filter</option>
                    </select>
                    <a id="loadFilterButton" class="btn" ng-disabled="!selectedFilter || lastLoadedFilterName === selectedFilter.name" ng-click="loadFilter()">Load</a>
                    <a id="deleteFilterButton" class="btn btn-danger" ng-disabled="!selectedFilter" ng-click="deleteFilter()"><span class="icon icon-white icon-trash"></span></a>
                </div>
            </div>
        </div>

        <!-- Teams and Applications section (should only show on Reports page -->
        <div class="accordion-group" ng-if="!treeApplication && !treeTeam">
            <div class="accordion-heading" ng-click="showTeamAndApplicationControls = !showTeamAndApplicationControls">
                <span id="expandTeamAndApplicationFilters" class="icon" ng-class="{ 'icon-minus': showTeamAndApplicationControls, 'icon-plus': !showTeamAndApplicationControls }"></span> Teams And Applications
            </div>
            <div ng-show="showTeamAndApplicationControls" class="filter-group-body">
                <div class="accordion-inner">
                    Teams
                    <a ng-hide="showTeamInput" ng-click="showTeamInput = !showTeamInput">
                        <span id="showTeamInput" class="icon" ng-class="{ 'icon-minus': showTeamInput, 'icon-plus': !showTeamInput }"></span>
                    </a>
                    <br>
                    <input id="teamNameTypeahead" focus-on="showTeamInput"
                           ng-show="showTeamInput"
                           typeahead="team.name for team in teams | filter:$viewValue | limitTo:8"
                           type="text"
                           ng-model="newFilteredTeam.name"
                           typeahead-on-select="addNew(parameters.teams, newFilteredTeam.name); newFilteredTeam = {}; showTeamInput = false"/>
                    <div ng-repeat="filteredTeam in parameters.teams">
                        <span id="removeTeam{{ filteredTeam.name }}" class="pointer icon icon-minus-sign" ng-click="remove(parameters.teams, $index)"></span>
                        {{ filteredTeam.name }}
                    </div>
                </div>

                <div class="accordion-inner">
                    Applications
                    <a ng-hide="showApplicationInput" ng-click="showApplicationInput = !showApplicationInput">
                        <span id="showApplicationInput" class="icon" ng-class="{ 'icon-minus': showApplicationInput, 'icon-plus': !showApplicationInput }"></span>
                    </a>
                    <br>
                    <input id="applicationNameTypeahead"
                           focus-on="showApplicationInput"
                           ng-show="showApplicationInput"
                           typeahead="(application.team.name + ' / ' + application.name) for application in searchApplications | filter:$viewValue | limitTo:8"
                           type="text"
                           ng-model="newFilteredApplication.name"
                           typeahead-on-select="addNew(parameters.applications, newFilteredApplication.name); newFilteredApplication = {}; showApplicationInput = false"/>
                    <div ng-repeat="filteredApplication in parameters.applications">
                        <span id="removeApplication{{ filteredApplication.name }}" class="pointer icon icon-minus-sign" ng-click="remove(parameters.applications, $index)"></span>
                        {{ filteredApplication.name }}
                    </div>
                </div>
            </div>
        </div>

        <!-- This is the same as the above control but only shows up on the team page. -->
        <div class="accordion-group" ng-if="treeTeam">
            <div class="accordion-heading" ng-click="showTeamAndApplicationControls = !showTeamAndApplicationControls">
                <span id="expandTeamAndApplicationFilters" class="icon" ng-class="{ 'icon-minus': showTeamAndApplicationControls, 'icon-plus': !showTeamAndApplicationControls }"></span> Teams And Applications
            </div>
            <div ng-show="showTeamAndApplicationControls" class="filter-group-body">

                <div class="accordion-inner">
                    Applications
                    <a ng-hide="showApplicationInput" ng-click="showApplicationInput = !showApplicationInput">
                        <span id="showApplicationInput" class="icon" ng-class="{ 'icon-minus': showApplicationInput, 'icon-plus': !showApplicationInput }"></span>
                    </a>
                    <br>
                    <input id="applicationNameTypeahead"
                           focus-on="showApplicationInput"
                           ng-show="showApplicationInput"
                           typeahead="(treeTeam.name + ' / ' + application.name) for application in treeTeam.applications | filter:$viewValue | limitTo:8"
                           type="text"
                           ng-model="newFilteredApplication.name"
                           typeahead-on-select="addNew(parameters.applications, newFilteredApplication.name); newFilteredApplication = {}; showApplicationInput = false"/>
                    <div ng-repeat="filteredApplication in parameters.applications">
                        <span class="pointer icon icon-minus-sign" ng-click="remove(parameters.applications, $index)"></span>
                        {{ filteredApplication.name }}
                    </div>
                </div>
            </div>
        </div>

        <!-- Scanner and # Merged controls -->
        <div class="accordion-group">
            <div class="accordion-heading" ng-click="showTypeAndMergedControls = !showTypeAndMergedControls">
                <span id="expandScannerFilters" class="icon" ng-class="{ 'icon-minus': showTypeAndMergedControls, 'icon-plus': !showTypeAndMergedControls }"></span> Scanner and # Merged
            </div>
            <div class="filter-group-body" ng-show="showTypeAndMergedControls">

                <div class="accordion-inner">
                    Number Merged Findings
                    <ul class="nav nav-pills">
                        <li id="set2MergedFindings" ng-class="{ active: parameters.numberMerged === 2 }"><a ng-click="setNumberMerged(2)">2+</a></li>
                        <li id="set3MergedFindings" ng-class="{ active: parameters.numberMerged === 3 }"><a ng-click="setNumberMerged(3)">3+</a></li>
                        <li id="set4MergedFindings" ng-class="{ active: parameters.numberMerged === 4 }"><a ng-click="setNumberMerged(4)">4+</a></li>
                        <li id="set5MergedFindings" ng-class="{ active: parameters.numberMerged === 5 }"><a ng-click="setNumberMerged(5)">5+</a></li>
                    </ul>
                </div>

                <div class="accordion-inner">
                    Scanners
                    <a ng-hide="showScannerInput" ng-click="showScannerInput = !showScannerInput">
                        <span id="showScannerInput" class="icon" ng-class="{ 'icon-minus': showScannerInput, 'icon-plus': !showScannerInput }"></span>
                    </a>
                    <br>
                    <input id="scannerTypeahead"
                           ng-show="showScannerInput"
                           focus-on="showScannerInput"
                           typeahead="scanner.name for scanner in scanners | filter:$viewValue | limitTo:8"
                           type="text"
                           ng-model="newFilteredScanner.name"
                           typeahead-on-select="addNew(parameters.scanners, newFilteredScanner.name); newFilteredScanner = {}; showScannerInput = false"/>
                    <div ng-repeat="filteredScanner in parameters.scanners">
                        <span id="removeScanner{{ filteredScanner.name }}" class="pointer icon icon-minus-sign" ng-click="remove(parameters.scanners, $index)"></span>
                        {{ filteredScanner.name }}
                    </div>
                </div>
            </div>
        </div>

        <!-- Field Controls: Type, path, parameter, etc. -->
        <div class="accordion-group">
            <div class="accordion-heading" ng-click="showDetailsControls = !showDetailsControls">
                <span id="showFieldControls" class="icon" ng-class="{ 'icon-minus': showDetailsControls, 'icon-plus': !showDetailsControls }"></span> Field Controls
            </div>
            <div class="filter-group-body" ng-show="showDetailsControls">

                <div class="accordion-inner">
                    Vulnerability Type
                    <a ng-hide="showTypeInput" ng-click="showTypeInput = !showTypeInput">
                        <span id="showTypeInput" class="icon" ng-class="{ 'icon-minus': showTypeInput, 'icon-plus': !showTypeInput }"></span>
                    </a>
                    <br>
                    <input id="vulnerabilityTypeTypeahead"
                           ng-show="showTypeInput"
                           focus-on="showTypeInput"
                           type="text"
                           class="form-control"
                           ng-model="newFilteredType.text"
                           typeahead="(vulnerability.name + ' (CWE ' + vulnerability.displayId + ')') for vulnerability in genericVulnerabilities | filter:$viewValue | limitTo:10"
                           typeahead-on-select="addNew(parameters.genericVulnerabilities, newFilteredType.text); newFilteredType = {}; showTypeInput = false"/>
                    <div ng-repeat="filteredType in parameters.genericVulnerabilities">
                        <span id="removeType{{ filteredType.displayId }}" class="pointer icon icon-minus-sign" ng-click="remove(parameters.genericVulnerabilities, $index)"></span>
                        {{ filteredType.name | shortCweNames }}
                    </div>
                </div>

                <div class="accordion-inner">
                    Path
                    <br>
                    <input id="pathInput" style="width: 180px;" type="text" placeholder="Example: /login.jsp"
                           ng-model="parameters.path" ng-blur="refresh()" ng-enter="refresh()"/>
                </div>

                <div class="accordion-inner">
                    Parameter
                    <br>
                    <input id="parameterFilterInput" style="width: 180px;" type="text" placeholder="Example: username"
                           ng-model="parameters.parameter" ng-blur="refresh()" ng-enter="refresh()"/>
                </div>

                <div class="accordion-inner">
                    Severity
                    <br>
                    <div>
                        <input id="showInfo" type="checkbox" class="btn" ng-change="refresh()" ng-model="parameters.severities.info"/>Info<br>
                        <input id="showLow" type="checkbox" class="btn" ng-change="refresh()" ng-model="parameters.severities.low"/>Low<br>
                        <input id="showMedium" type="checkbox" class="btn" ng-change="refresh()" ng-model="parameters.severities.medium"/>Medium<br>
                        <input id="showHigh" type="checkbox" class="btn" ng-change="refresh()" ng-model="parameters.severities.high"/>High<br>
                        <input id="showCritical" type="checkbox" class="btn" ng-change="refresh()" ng-model="parameters.severities.critical"/>Critical
                    </div>
                </div>

                <div class="accordion-inner">
                    Status
                    <br>
                    <div>
                        <input id="showOpen" type="checkbox" class="btn" ng-change="refresh()" ng-model="parameters.showOpen"/>Open<br>
                        <input id="showClosed" type="checkbox" class="btn" ng-change="refresh()" ng-model="parameters.showClosed"/>Closed<br>
                        <input id="showFalsePositive" type="checkbox" class="btn" ng-change="refresh()" ng-model="parameters.showFalsePositive"/>False Positive<br>
                        <input id="showHidden" type="checkbox" class="btn" ng-change="refresh()" ng-model="parameters.showHidden"/>Hidden
                    </div>
                </div>
            </div>
        </div>

        <!-- Aging -->
        <div class="accordion-group">
            <div class="accordion-heading" ng-click="showDateControls = !showDateControls">
                <span id="showDateControls" class="icon" ng-class="{ 'icon-minus': showDateControls, 'icon-plus': !showDateControls }"></span> Aging
            </div>
            <div class="filter-group-body" ng-show="showDateControls">
                <div class="accordion-inner">
                    Days Old
                    <ul class="nav nav-pills">
                        <li id="lessThan" ng-class="{ active: parameters.daysOldModifier === 'Less' }"><a ng-click="setDaysOldModifier('Less')">Less Than</a></li>
                        <li id="moreThan" ng-class="{ active: parameters.daysOldModifier === 'More' }"><a ng-click="setDaysOldModifier('More')">More Than</a></li>
                    </ul>
                    <ul class="nav nav-pills">
                        <li id="oneWeek" ng-class="{ active: parameters.daysOld === 7 }"><a ng-click="setDaysOld(7)">1 Week</a></li>
                        <li id="30days" ng-class="{ active: parameters.daysOld === 30 }"><a ng-click="setDaysOld(30)">30 days</a></li>
                        <li id="60days" ng-class="{ active: parameters.daysOld === 60 }"><a ng-click="setDaysOld(60)">60 days</a></li>
                        <li id="90days" ng-class="{ active: parameters.daysOld === 90 }"><a ng-click="setDaysOld(90)">90 days</a></li>
                    </ul>
                </div>
            </div>
        </div>

        <!-- Date Range -->
        <div class="accordion-group">
            <div class="accordion-heading" ng-click="showDateRange = !showDateRange">
                <span id="showDateRange" class="icon" ng-class="{ 'icon-minus': showDateRange, 'icon-plus': !showDateRange }"></span> Date Range
            </div>
            <div class="filter-group-body" ng-show="showDateRange">
                <div class="accordion-inner">
                    <h4>Start Date</h4>
                    <div class="col-md-6">
                        <p class="input-group">
                            <input id="startDateInput" type="text" class="form-control" ng-model="startDate" style="width:135px;margin-bottom:0" datepicker-popup="dd-MMMM-yyyy" ng-model="startDate"
                                   is-open="startDateOpened" min-date="minDate" max-date="maxDate" date-disabled="disabled(date, mode)" close-text="Close"
                                   ng-change="refresh()"
                                    />
                          <span class="input-group-btn">
                            <button type="button" class="btn btn-default" ng-click="openStartDate($event)"><i class="icon icon-calendar"></i></button>
                          </span>
                        </p>
                    </div>
                </div>

                <div class="accordion-inner">
                    <h4>End Date</h4>
                    <div class="col-md-6">
                        <p class="input-group">
                            <input id="endDateInput" type="text" class="form-control" ng-model="endDate" style="width:135px;margin-bottom:0" datepicker-popup="dd-MMMM-yyyy" ng-model="endDate"
                                   is-open="endDateOpened" min-date="startDate" max-date="maxDate" date-disabled="disabled(date, mode)" close-text="Close"
                                   ng-change="refresh()"
                                    />
                            <span class="input-group-btn">
                                <button type="button" class="btn btn-default" ng-click="openEndDate($event)"><i class="icon icon-calendar"></i></button>
                            </span>
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Export buttons -->
        <div class="accordion-group">
            <div class="accordion-heading" style="text-align:center">
                <a id="exportCSVButton" ng-click="exportCSV()" class="btn">Export CSV</a>
            </div>
        </div>
    </div>

    <%@ include file="vulnSearchTree.jsp" %>
    <%--<%@ include file="vulnerabilityTable.jsp" %>--%>

</div>
