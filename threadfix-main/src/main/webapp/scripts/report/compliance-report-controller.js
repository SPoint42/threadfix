var module = angular.module('threadfix');

module.controller('ComplianceReportController', function($scope, $rootScope, $window, $http, tfEncoder, reportUtilities, trendingUtilities, reportConstants) {

    $scope.parameters = {};
    $scope.filterScans = [];
    $scope.noData = false;
    $scope.margin = [70, 70, 100, 70];
    $scope.title = {
        svgId: reportConstants.reportTypes.compliance.name
    };

    $scope.resetFilters = function() {
        $scope.parameters = {
            tags: [],
            severities: {
                critical: true,
                high: true,
                medium: true,
                low: true,
                info: true
            },
            daysOldModifier: 'LastYear',
            endDate: undefined,
            startDate: undefined
        };
    };

    $scope.$on('loadComplianceReport', function() {
        $scope.noData = false;
        $scope.savedFilters = $scope.$parent.savedFilters.filter(function(filter){
            var parameters = JSON.parse(filter.json);
            return (parameters.filterType && parameters.filterType.isTrendingFilter);
        });

        // Data for trending chart and table
        if (!$scope.allScans) {
            if ($scope.gotDataFromTrending) {
                $scope.noData = true;
            } else {
                $scope.loading = true;
                $http.post(tfEncoder.encode("/reports/trendingScans"), $scope.getReportParameters()).
                    success(function(data) {
                        $scope.loading = false;
                        $scope.resetFilters();
                        $scope.allScans = data.object.scanList;

                        if (!$scope.allScans) {
                            $scope.allScans = [];
                        }
                        $scope.allScans.sort(function (a, b) {
                            return a.importTime - b.importTime;
                        });
                        trendingUtilities.filterByTag($scope);
                        trendingUtilities.refreshScans($scope);
                    }).
                    error(function() {
                        $scope.loading = false;
                    });
            }
        } else {
            trendingUtilities.filterByTag($scope);
            trendingUtilities.refreshScans($scope);
        }
        renderTable();

    });

    $scope.$on('resetParameters', function(event, parameters) {
        if (!$scope.$parent.complianceActive)
            return;
        $scope.parameters = angular.copy(parameters);
        trendingUtilities.filterByTag($scope);
        trendingUtilities.refreshScans($scope);
        renderTable();
        $scope.$broadcast("updateParameters");
    });

    $scope.$on('updateDisplayData', function(event, parameters) {
        if (!$scope.$parent.complianceActive)
            return;
        $scope.parameters = angular.copy(parameters);
        trendingUtilities.updateDisplayData($scope);
        renderTable();
        $scope.$broadcast("updateParameters");
    });

    var renderTable = function() {
        var startingInfo, endingInfo;
            $scope.tableInfo = [];
        if ($scope.trendingScansData.length> 0) {
            startingInfo = (trendingUtilities.getFirstHashInList()) ? trendingUtilities.getFirstHashInList() : $scope.trendingScansData[0];
            endingInfo = (trendingUtilities.getLastHashInList()) ? trendingUtilities.getLastHashInList() : $scope.trendingScansData[$scope.trendingScansData.length-1];
            var keys = Object.keys(startingInfo);

            keys.forEach(function(key){
                if (key !== 'importTime') {
                    var map = {};
                    map['Severity'] = key;
                    map['Starting Count'] = startingInfo[key];
                    map['Ending Count'] = endingInfo[key];
                    $scope.tableInfo.push(map);
                }
            })
        }
    };

    $scope.$on('allTrendingScans', function(event, trendingScans) {
        $scope.allScans = trendingScans;
        $scope.gotDataFromTrending = true;
        $scope.resetFilters();
    });

    $scope.exportPNG = function(){

        if (!$scope.exportInfo) {
            $scope.exportInfo = {
                id: reportConstants.reportTypes.compliance.id
            }
        } else {
            if ($scope.exportInfo.id  === reportConstants.reportTypes.compliance.id)
                $scope.exportInfo.id  = "" +  reportConstants.reportTypes.compliance.id;
            else
                $scope.exportInfo.id  = reportConstants.reportTypes.compliance.id;
        }
        $scope.exportInfo.svgId = reportConstants.reportTypes.compliance.name;
        $scope.exportInfo.tags = $scope.title.tags;
        $scope.exportInfo.teams = undefined;
        $scope.exportInfo.apps = undefined;
    };

});
