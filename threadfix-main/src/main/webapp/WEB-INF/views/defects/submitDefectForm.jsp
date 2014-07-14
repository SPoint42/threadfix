<script type="text/ng-template" id="submitDefectForm.html">
    <div class="modal-header">
        <h4 id="myModalLabel">
            Submit Defect
        </h4>
    </div>
    <div ng-form="form" class="modal-body">
        <div ng-hide="initialized" class="modal-spinner-div"><span class="spinner dark"></span>Loading</div><br>

        <div ng-if="config.typeName === 'HP Quality Center' || config.typeName === 'Jira'">
            <span ng-if="stdFormTemplate" class="errors">* required field</span>
            <dynamic-form ng-if="stdFormTemplate" template="stdFormTemplate"
                          ng-model="fieldsMap">
            </dynamic-form>

        </div>

        <div ng-if="config.typeName !== 'HP Quality Center' && config.typeName !== 'Jira'">

            <table ng-show="initialized" class="dataTable" style="text-align: left">
                <tbody>
                <tr ng-show="config.typeName === 'Version One' || config.typeName === 'Bugzilla' || config.typeName === 'Jira'">
                    <td ng-show="config.typeName === 'Version One'">Sprint</td>
                    <td ng-show="config.typeName === 'Bugzilla' || config.typeName === 'Jira'">Component</td>
                    <td class="inputValue">
                        <select ng-model="object.selectedComponent" name="selectedComponent" ng-options="component for component in config.components"></select>
                    </td>
                </tr>
                <tr>
                    <td>Priority</td>
                    <td class="inputValue">
                        <select ng-model="object.priority" name="priority" ng-options="priority for priority in config.priorities"></select>
                    </td>
                </tr>
                <tr>
                    <td>Status</td>
                    <td class="inputValue">
                        <select ng-model="object.status" name="status" ng-options="status for status in config.statuses"></select>
                    </td>
                </tr>
                <tr ng-show="config.typeName === 'Bugzilla' || config.typeName === 'HP Quality Center'">
                    <td>Version</td>
                    <td class="inputValue">
                        <select ng-model="object.version" name="version" ng-options="version for version in config.versions"></select>
                    </td>
                </tr>
                <tr ng-show="config.typeName === 'Bugzilla' || config.typeName === 'HP Quality Center'">
                    <td>Severity</td>
                    <td class="inputValue">
                        <select ng-model="object.severity" name="severity" ng-options="severity for severity in config.severities"></select>
                    </td>
                </tr>
                <tr>
                    <td>Title</td>
                    <td colspan="5" class="inputValue">
                        <input focus-on="focusInput" required style="width:549px;" type="text" ng-model="object.summary" name="summary"/>
                    </td>
                </tr>
                <tr style="margin-top:5px;">
                    <td>Description</td>
                    <td colspan="5" class="inputValue">
                        <textarea name="preamble" ng-model="object.preamble" style="width:549px; height:100px;"></textarea>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <%@ include file="littleVulnTable.jspf" %>
    </div>
    <div class="modal-footer">
        <span class="errors" style="float:left">{{ errorMessage }}</span>

        <a class="btn" ng-click="cancel()">Close</a>
        <button id="loadingButton"
                disabled="disabled"
                class="btn btn-primary"
                ng-show="loading">
            <span class="spinner"></span>
            Submitting
        </button>
        <button id="submit"
                ng-class="{ disabled : form.$invalid }"
                class="btn btn-primary"
                ng-mouseenter="form.summary.$dirty = true"
                ng-hide="loading"
                ng-click="ok(form.$valid)">Submit Defect</button>
    </div>
</script>
