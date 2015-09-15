'use strict';

angular.module('infinitetorrentApp')
    .controller('SynchronizeController', function ($scope, SynchronizeService) {
        $scope.synchronizing = false;
        $scope.trackers = {};

        $scope.synchronize = function () {
            $scope.synchronizing = true;
            $scope.trackers = {};
            SynchronizeService.synchronize().then(function (data) {
                $scope.updatingsynchronizing = false;
                $scope.trackers = data;
            });
        };

    });
