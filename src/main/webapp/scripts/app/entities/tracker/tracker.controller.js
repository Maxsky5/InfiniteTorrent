'use strict';

angular.module('infinitetorrentApp')
    .controller('TrackerController', function ($scope, Tracker, ParseLinks) {
        $scope.trackers = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Tracker.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.trackers.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.trackers = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Tracker.get({id: id}, function(result) {
                $scope.tracker = result;
                $('#deleteTrackerConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Tracker.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteTrackerConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.tracker = {name: null, id: null};
        };
    });
