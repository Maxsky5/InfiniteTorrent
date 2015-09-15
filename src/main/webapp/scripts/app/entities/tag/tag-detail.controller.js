'use strict';

angular.module('infinitetorrentApp')
    .controller('TagDetailController', function ($scope, $rootScope, $stateParams, entity, Tag) {
        $scope.tag = entity;
        $scope.load = function (id) {
            Tag.get({id: id}, function(result) {
                $scope.tag = result;
            });
        };
        $rootScope.$on('infinitetorrentApp:tagUpdate', function(event, result) {
            $scope.tag = result;
        });
    });
