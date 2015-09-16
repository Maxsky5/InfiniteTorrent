'use strict';

angular.module('infinitetorrentApp')
    .controller('TrackerDetailController', function ($scope, $rootScope, $stateParams, entity, Tracker) {
        $scope.tracker = entity;
        $scope.load = function (id) {
            Tracker.get({id: id}, function(result) {
                $scope.tracker = result;
            });
        };
        $rootScope.$on('infinitetorrentApp:trackerUpdate', function(event, result) {
            $scope.tracker = result;
        });
    });
