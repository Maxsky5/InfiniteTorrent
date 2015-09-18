'use strict';

angular.module('infinitetorrentApp')
    .controller('SynchronizeController', function ($scope, SynchronizeService) {
        $scope.synchronizing = false;
        $scope.trackersDto = [];

        $scope.synchronize = function () {
            $scope.synchronizing = true;
            $scope.trackersDto = [];
            SynchronizeService.synchronize().then(function (data) {
                $scope.synchronizing = false;
                $scope.trackersDto = data;
            });
        };

    });
